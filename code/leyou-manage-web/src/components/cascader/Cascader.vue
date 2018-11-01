<template>
  <cas :data="options" @on-change="handleChange" :loadData="loadOption" transfer>
    <v-select :label="label" chips clearable v-model="selected" tags :required="required"
      :rules="defaultRules">
      <template slot="selection" slot-scope="data">
        <v-chip @click.stop="" v-if="multiple" close @input="remove(data.item)" small outline color="green">
          {{ data.item.label}}&nbsp;
        </v-chip>
        <v-chip @click.stop="" v-else small>{{ data.item}}</v-chip>
      </template>
    </v-select>
  </cas>
</template>

<script>
  import {Cascader} from 'iview'
  import 'iview/dist/styles/iview.css';

  export default {
    name: "vCascader",
    components: {
      cas: Cascader
    },
    props: {
      value: {},
      label: {
        type: String
      },
      url: {
        type: String
      },
      itemText: {
        type: String,
        default: 'name'
      },
      itemValue: {
        type: String,
        default: 'id'
      },
      children: {
        type: String,
        default: 'children'
      },
      multiple: {
        type: Boolean,
        default: false
      },
      showAllLevels: {
        type: Boolean,
        default: false
      },
      required:{
        type: Boolean,
        default: false
      },
      rules:{
        type:Array,
      }
    },
    data() {
      return {
        options: [],
        selected: [],
        defaultRules:[]
      }
    },
    methods: {
      handleChange(value, selectedData) {
        // 获取最后一级
        const option = selectedData[selectedData.length - 1];
        // 如果是多选，则默认只保存最后一级选项
        if (this.multiple) {
          // 将最后一级保存到selected中
          if (this.selected.findIndex(o => o.value === option.value) < 0) {
            this.selected.push(option);
          }
          // 返回已选中的值
          this.$emit("input", this.transfer(this.selected));
        } else {
          // 单选，则需要判断是否需要显示所有级别
          if (this.showAllLevels) {
            // 显示所有级别,将各级别label进行拼接
            this.selected = [option.__label];
            // 返回id数组
            this.$emit("input", this.transfer(selectedData));
          } else {
            // 只显示最后一级
            this.selected = [option.label];
            // 返回
            this.$emit("input", this.transfer([option])[0]);
          }
        }
      },
      loadOption(item, callback) {// 延迟加载次级选项
        item.loading = true;
        this.loadData(item.value).then(data => {
          item.children = data;
          item.loading = false;
          callback();
        }).catch(() => {
          item.loading = false;
          callback();
        })
      },
      loadData(pid) {// 从指定的url地址加载数据，并格式化
        return new Promise((resolve) => {
          this.$http.get(this.url, {
            params: {
              pid: pid
            }
          }).then(resp => {
            const data = [];
            for (let d of resp.data) {
              const node = {
                value: d[this.itemValue],
                label: d[this.itemText]
              }
              if (d.isParent) {
                node['children'] = [];
                node['loading'] = false;
              }
              data.push(node)
            }
            resolve(data);
          })
        })
      },
      remove(item) {
        this.selected = this.selected.filter(o => o.value !== item.value)
        this.$emit("input", this.transfer(this.selected))
      },
      transfer(arr) {
        return arr.map(({label, value}) => {
          const obj = {};
          obj[this.itemText] = label;
          obj[this.itemValue] = value;
          return obj;
        })
      },
      validate(){
        if(this.required){
          this.$refs.form.validate();
        }
      }
    },
    created() {
      this.loadData(0).then(data => {
        this.options = data;
      })
      if(this.required){
        this.defaultRules.push(v => v.length > 0 || this.label + "不能为空");
      }
      if(this.rules){
        this.rules.forEach(r => this.defaultRules.push(r));
      }
    },
    watch: {
      value: {
        deep: true,
        handler(val) {
          if(!val){
            this.selected = [];
            return;
          }
          if(val && this.showAllLevels && !this.multiple){
            this.selected = [val.map(o => o[this.itemText]).join("/")]
          } else if (this.multiple && val) {
            this.selected = val.map(o => {
              return {
                label: o[this.itemText],
                value: o[this.itemValue]
              }
            })
          } else{
            this.selected = [val[this.itemText]]
          }
        }
      }
    }
  }
</script>

<style scoped>
  .ivu-cascader-menu-item {
    font-size: 14px;
  }

</style>
