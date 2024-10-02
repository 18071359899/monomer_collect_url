<template>
  <NarBar />
  <!-- <input type="file" @change="getFile" />
  <el-button @click="sumbit"></el-button> -->
  <router-view />
  <!-- <Tree :data="data" @open="open" /> -->
</template>
<script setup>
import { onMounted } from 'vue';
import MyAxios from '@/utils/MyAxios.js'
import NarBar from "@/components/NarBar";
onMounted(()=>{
  // console.log(process.env.VUE_APP_BASE_URL);
  // MyAxios.get("/search/get/",{params:{type: 'share',search:"",page: 1, size: 10}})
})
// import { onMounted } from "vue";
// import myAxios from "@/utils/MyAxios.js";
// let file = null;
// var formData = new FormData();
// const getFile = (e) => {
//   file = e.target.files[0];
//   formData.append("file", file);
//   formData.append(
//     "zxc",
//     JSON.stringify({
//       key: "key",
//       value: "value",
//     })
//   );
// };
// const sumbit = () => {
//   myAxios.post("/file/uploadFile", formData).then((res) => {
//     console.log(res);
//   });
// };
// onMounted(() => {});

// 解决ERROR ResizeObserver loop completed with undelivered notifications.
//问题的
const debounce = (fn, delay) => {
  let timer = null;
  return function () {
    let context = this;
    let args = arguments;
    clearTimeout(timer);
    timer = setTimeout(function () {
      fn.apply(context, args);
    }, delay);
  };
};
// 解决ERROR ResizeObserver loop completed with undelivered notifications.
const _ResizeObserver = window.ResizeObserver;
window.ResizeObserver = class ResizeObserver extends _ResizeObserver {
  constructor(callback) {
    callback = debounce(callback, 20);
    super(callback);
  }
};
</script>

<style>
.router-link{
  text-decoration: none;
  color: black;
}
* {
  padding: 0px;
  margin: 0px;
  border: 0px;
  outline: 0px;
  vertical-align: middle;
  box-sizing: border-box;  
}
.interactionUsernameToJump{
  cursor: pointer;
  color: #337AB7;
}
.interactionUsernameToJump:hover{
  text-decoration: underline;
  color: #23527C;
}
.linkCss{
  text-decoration: none;
}
.noSelect {
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
}
.topMenu {
  display: flex;
  padding: 20px;
  padding-bottom: 5px;
  align-items: center;
}
.el-card__body{
  padding: 0 !important;
  margin: 0 !important;
}
.el-tabs__item{
  margin-left: 20px;
}
body{
  background-color: rgb(240,242,245);
}
.icon{
  cursor: pointer;
}

</style>

// import Tree from "@/views/TreeView.vue"; // const open = (data) => { //
console.log(data); // }; // const data = [ // { // label: "Level one 1", // children: [ //
{ // label: "Level two 1-1", // children: [ // { // label: "Level three 1-1-1", // }, //
], // }, // ], // }, // { // label: "Level one 2", // children: [ // { // label: "Level
two 2-1", // children: [ // { // label: "Level three 2-1-1", // }, // ], // }, // { //
label: "Level two 2-2", // children: [ // { // label: "Level three 2-2-1", // }, // ], //
}, // ], // }, // { // label: "Level one 3", // children: [ // { // label: "Level two
3-1", // children: [ // { // label: "Level three 3-1-1", // }, // ], // }, // { // label:
"Level two 3-2", // children: [ // { // label: "Level three 3-2-1", // }, // ], // }, //
], // }, // ];
