import m from './messages'

const MyComponent = {};

MyComponent.install = function (Vue) {
  Vue.component("vTree", () => import('./tree/Tree'));
  Vue.component("vCascader", () => import('./cascader/Cascader'));
  Vue.component("vUpload", () => import('./form/Upload'));
  Vue.component("vEditor", () => import('./form/Editor'));
  Vue.prototype.$message = m;
  Vue.prototype.$format = function (val) {
    if(typeof val === 'string'){
      if(isNaN(val)){
        return null;
      }
      // 价格转为整数
      const index = val.lastIndexOf(".");
      let p = "";
      if(index < 0){
        // 无小数
        p = val + "00";
      }else if(index === p.length - 2){
        // 1位小数
        p = val.replace("\.","") + "0";
      }else{
        // 2位小数
        p = val.replace("\.","")
      }
      return parseInt(p);
    }else if(typeof val === 'number'){
      if(val == null){
        return null;
      }
      const s = val + '';
      if(s.length === 0){
        return 0;
      }
      if(s.length === 1){
        return "0.0" + val;
      }
      if(s.length === 2){
        return "0." + val;
      }
      const i = s.indexOf(".");
      if(i < 0){
        return s.substring(0, s.length - 2) + "." + s.substring(s.length-2)
      }
      const num = s.substring(0,i) + s.substring(i+1);
      if(i === 1){
        // 1位整数
        return "0.0" + num;
      }
      if(i === 2){
        return "0." + num;
      }
      if( i > 2){
        return num.substring(0,i-2) + "." + num.substring(i-2)
      }
    }
  }
}


Object.deepCopy = function (src) {
  // for(let key in src){
  //   if(!src[key]){
  //     continue;
  //   }
  //   if(src[key].constructor === Array){
  //     dest[key] = [];
  //     Object.deepCopy(src[key],dest[key])
  //   }else if(typeof src[key] === 'object'){
  //     dest[key] = {};
  //     Object.deepCopy(src[key],dest[key])
  //   }
  //   dest[key] = src[key];
  // }
  return JSON.parse(JSON.stringify(src));
}

export default MyComponent;
