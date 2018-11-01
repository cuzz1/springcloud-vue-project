<template>
  <v-app :dark="dark">
    <!-- 左侧导航条 -->
    <v-navigation-drawer
      :dark="dark"
      persistent
      :mini-variant="miniVariant"
      v-model="drawer"
      enable-resize-watcher
      fixed
      app
    >
      <v-toolbar flat class="transparent">
        <v-list class="pa-0">
          <v-list-tile avatar>
            <v-list-tile-avatar>
              <img src="../assets/2.jpeg">
            </v-list-tile-avatar>
            <v-list-tile-content>
              <v-list-tile-title>虎哥</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
        </v-list>
      </v-toolbar>
      <v-divider/>
      <!-- 左侧菜单 -->
      <v-list class="pt-0" dense>
        <v-list-group
          v-model="item.active"
          v-for="item in items"
          :key="item.title"
          :prepend-icon="item.action"
          no-action
        >
          <!--一级菜单 -->
          <v-list-tile slot="activator">
            <v-list-tile-content>
              <v-list-tile-title>{{ item.title }}</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
          <!-- 二级菜单 -->
          <v-list-tile v-for="subItem in item.items" :key="subItem.title" :to="item.path + subItem.path">
            <v-list-tile-content>
              <v-list-tile-title>{{ subItem.title }}</v-list-tile-title>
            </v-list-tile-content>
            <v-list-tile-action>
              <v-icon>{{ subItem.action }}</v-icon>
            </v-list-tile-action>
          </v-list-tile>
        </v-list-group>
      </v-list>
    </v-navigation-drawer>
    <!-- 顶部工具条 -->
    <v-toolbar
      app
      dark
      :color="dark ? 'secondary' : 'primary'"
    >
      <!-- 隐藏左侧菜单的按钮-->
      <v-toolbar-side-icon @click.stop="drawer = !drawer"/>
      <!-- 收起左侧菜单的按钮-->
      <v-btn icon @click.stop="miniVariant = !miniVariant">
        <v-icon v-html="miniVariant ? 'chevron_right' : 'chevron_left'"/>
      </v-btn>
      <!-- 切换黑暗主题 -->
      <v-flex xs1>
        <v-switch
          :label="dark ? '暗黑' : '明亮'"
          v-model="dark"
          color="dark"
          hide-details
        />
      </v-flex>
      <!-- 顶部导航标题 -->
      <v-flex xs3></v-flex>
      <v-toolbar-title v-text="title"/>
      <v-spacer/>

      <!-- 调色板 -->
      <v-btn icon @click.stop="dark = !dark">
        <v-icon>invert_colors</v-icon>
      </v-btn>
      <!-- 顶部导航用户菜单 -->
      <v-btn icon @click.stop="dark = !dark">
        <v-icon>account_box</v-icon>
      </v-btn>
    </v-toolbar>
    <!--中间内容主体-->
    <v-content>
      <v-breadcrumbs>
        <v-icon slot="divider">chevron_right</v-icon>
        <v-breadcrumbs-item>{{item1}}</v-breadcrumbs-item>
        <v-breadcrumbs-item>{{item2}}</v-breadcrumbs-item>
      </v-breadcrumbs>
      <div>
        <!--定义一个路由锚点，Layout的子组件内容将在这里展示-->
        <router-view/>
      </div>
    </v-content>
  </v-app>
</template>

<script>
  import menus from "../menu";

  export default {
    data() {
      return {
        dark: false,// 是否暗黑主题
        drawer: true,// 左侧导航是否隐藏
        miniVariant: false,// 左侧导航是否收起
        title: '乐优商城后台管理',// 顶部导航条名称,
        menuMap: {}
      }
    },
    computed: {
      items() {
        return menus;
      },
      item1() {
        const arr = this.$route.path.split("/");
        return this.menuMap[arr[1]].name;
      },
      item2() {
        const arr = this.$route.path.split("/");
        return this.menuMap[arr[1]][arr[2]];
      }
    },
    name: 'App',
    watch: {},
    created() {
      menus.forEach(m => {
        const p1 = m.path.slice(1);
        this.menuMap[p1] = {name: m.title};
        m.items.forEach(i => {
          this.menuMap[p1][i.path.slice(1)] = i.title;
        })
      })
    }
  }
</script>

<style scoped>
  .box {
    width: 90%;
  }
</style>
