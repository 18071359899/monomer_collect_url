<template>
  <div>
    <div class="common-layout">
      <el-container>
        <el-header class="common-header flex-float">
          <div class="flex">
            <img class="logo" src="../assets/logo.png" alt="" />
            <h1 class="title">商铺后台管理系统</h1>
          </div>

          <el-button type="danger">退出</el-button>
        </el-header>
        <el-container>
          <!-- 侧边栏 -->
          <el-aside class="common-aside" width="200px">
            <!-- 菜单 -->
            <el-menu background-color="none" text-color="#fff" :router="true">
              <testItem
                v-for="menu in menuList"
                :menu="menu"
                :key="menu.id"
                :index="menu.id + ''"
              ></testItem>
            </el-menu>
          </el-aside>
          <el-main><router-view></router-view></el-main>
        </el-container>
      </el-container>
    </div>
  </div>
</template>
<script setup>
import testItem from "./TestItem.vue";
import { reactive } from "vue";
let menuList = reactive([
  {
    authname: "随便管理",
    id: 1,
    children: [],
  },
  {
    authname: "用户管理",
    id: 2,
    children: [
      {
        authname: "二级用户1",
        id: 12,
        children: [],
      },
      {
        authname: "二级用户2",
        id: 13,
        children: [
          {
            id: 14,
            authname: "三级用户1",
          },
        ],
      },
    ],
  },
  {
    authname: "订单管理",
    id: 3,
    children: [
      {
        authname: "二级订单1",
        id: 15,
        children: [],
      },
      {
        authname: "二级订单2",
        id: 16,
        children: [
          {
            id: 17,
            authname: "三级订单1",
            children: [
              {
                id: 18,
                authname: "四级",
                children: [
                  {
                    id: 20,
                    authname: "五级",
                    children: [],
                  },
                ],
              },
            ],
          },
        ],
      },
    ],
  },
]);
</script>

<style scoped>
.el-container {
  height: 100vh;
  overflow: hidden;
}

.common-header {
  background: rgb(63 67 72);
}

.common-aside {
  background: rgb(48, 55, 65);
}

.logo {
  width: 60px;
}

.title {
  color: #fff;
}
</style>
