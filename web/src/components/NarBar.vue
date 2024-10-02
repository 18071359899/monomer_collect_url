<template>
  <div class="box-narbar">
    <nav class="navbar navbar-expand-lg fixed-top bg-body-tertiary" data-bs-theme="dark" style="margin-bottom: 100px">
      <div class="container">
        <a class="navbar-brand" href="#">
          <img src="@/assets/cloud.png" alt="" width="30" height="24" class="d-inline-block align-center">
          <span style="margin-left: 15px;">网聚盘</span></a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNavDropdown"
          aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNavDropdown">
          <ul class="navbar-nav">
            <li class="nav-item">
              <router-link class="nav-link" :class="route.name == 'share' || route.name == 'home' ? 'active' : ''"
                :to="{ name: 'share' }">分享</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" :to="{ name: 'manageSort' }"
                :class="route.name == 'manageSort' ? 'active' : ''">管理</router-link>
            </li>
            <!-- <li class="nav-item">
              <router-link class="nav-link" :to="{ name: 'manageSort' }"
                :class="route.name == 'navbar' ? 'active' : ''">站长导航</router-link>
            </li> -->
            <li class="nav-item">
              <router-link class="nav-link" :to="{ name: 'search' }"
                :class="route.name == 'search' ? 'active' : ''">搜索</router-link>
            </li>
          </ul>
        </div>
        <ul class="navbar-nav navbar-right" v-if="UserStore.loginUserObject.id !== 0">
          <li class="nav-item">
            <router-link class="nav-link" :to="{ name: 'shareUpdate' }"
              :class="route.name == 'shareUpdate' ? 'active' : ''">发布</router-link>
          </li>
          <li class="nav-item">
            <router-link class="navbar-link" :to="{ name: 'messageNotify' }">
              <el-badge :value="totalMessage"  :hidden="totalMessage === 0 ? true : false" :max="99" class="badgeItem">
                <MessageNotifyIcon class="messageNotifyIcon" />
              </el-badge>
            </router-link>
          </li>
          <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle navbar-brand" :to="{ name: 'messageNotify' }" role="button"
              data-bs-toggle="dropdown" aria-expanded="false">
              <img :src="UserStore.loginUserObject.photo" alt="" class="imgDom">
            </a>
            <ul class="dropdown-menu">
              <li>
                <router-link class="dropdown-item" :to="{ name: 'manageSort' }">我的信息</router-link>
              </li>
              <li>
                <router-link class="dropdown-item"
                  :to="{ name: 'userInfo', params: { userInfoId: UserStore.loginUserObject.id } }">个人主页</router-link>
              </li>
              <hr class="dropdown-divider" />
              <li>
                <router-link class="dropdown-item" :to="{ name: 'login' }" @click="handleExitLogin">退出登录</router-link>
              </li>
            </ul>
          </li>
        </ul>
      </div>
    </nav>
  </div>
</template>
<script setup>
import { useCounterStore } from '@/store/UserStore.js'
import { onMounted, ref } from 'vue';
import router from "@/router";
import { useRoute } from 'vue-router';
import MessageNotifyIcon from './icons/MessageNotifyIcon.vue';
import { totalMessageNotify } from '@/api/message_notify/MessageNotifyRequest.js'
let route = useRoute()
const UserStore = useCounterStore()
const totalMessage = ref(0)
const handleToHomePage = () => {
  if (!UserStore.loginUserObject) {
    router.push({ name: 'login' })
  } else {
    router.push({ name: 'userInfo', params: { userInfoId: UserStore.loginUserObject.id } })
  }
}
const handleExitLogin = () => {
  UserStore.loginOut()
  localStorage.removeItem('COLLECT_URL_TOKEN');
}
onMounted(() => {
  if(UserStore.loginUserObject.id !== 0)
  totalMessageNotify().then(res => {
        if (res !== null && res !== undefined) {
          totalMessage.value = res
        }
      })
})
</script>
<style scoped>
.box-narbar {
  height: 55px;
}

.imgDom {
  width: 35px;
  height: 35px;
  border-radius: 50%;
  margin-right: 6px;
}

.messageNotifyIcon {
  margin-left: 20px;
  margin-right: 20px;
}
.totalRecord-box {
  display: inline-block;
  width: 0%;
  position: absolute;
  right: 2vh;
  bottom: 4vh;
}
.totalRecord {
  display: inline-block;
  color: white;
  background-color: #ff3b30;
  border-radius: 50%;
  padding-left: 0.3vh;
  padding-right: 0.3vh;
  font-size: 10px;
}
.message{
  width: 20px;
  height: 35px;
}
.badgeItem{
  width: 65%;
}
</style>
