<template>
    <div class="container">
        <el-card class="top">
            <el-row>
                <div class="imgDom">
                    <img :src="userInfoData.photo" alt="">
                </div>
                <el-col :span="18" class="centerInfo">
                    <div class="username">{{ userInfoData.username }}</div>
                    <div class="introduction">{{ userInfoData.introduction }}</div>
                </el-col>
                <el-button type="primary" :icon="Plus" v-if="userInfoData.isFollow === 0"
                    @click="followUser">关注</el-button>
                <el-button type="primary" v-else-if="userInfoData.isFollow === 1" @click="followCancelUser"
                    plain>取消关注</el-button>
                <el-button type="primary" v-else>编辑个人资料</el-button>
            </el-row>
            <el-divider border-style="double" />
            <div>
                <span>关注：{{ userInfoData.follow }}</span>
                <el-divider style="margin-left: 30px;margin-right: 30px;" direction="vertical" border-style="double" />
                <span style="">粉丝：{{ userInfoData.fans }}</span>
            </div>
        </el-card>
        <el-card class="bottom">
            <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick">
                <el-tab-pane label="动态" name="first">
                    <UserShareContent :userId="currUserId" :attributes="0" v-if="activeName == 'first'" />
                </el-tab-pane>
                <el-tab-pane label="收藏" name="second">
                    <UserShareContent :userId="currUserId" :attributes="1" v-if="activeName == 'second'" />
                </el-tab-pane>
                <el-tab-pane label="关注" name="third">
                    <UserFollowContent :userId="currUserId" :attributes="0" v-if="activeName == 'third'" />
                </el-tab-pane>
                <el-tab-pane label="粉丝" name="fourth">
                    <UserFollowContent :userId="currUserId" :attributes="1" v-if="activeName == 'fourth'" />
                </el-tab-pane>
            </el-tabs>
        </el-card>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { Plus } from '@element-plus/icons-vue'
import UserFollowContent from './UserFollowContent.vue';
import UserShareContent from './UserShareContent.vue';
import MyAxios from '@/utils/MyAxios.js'
import { followCancelUserRequest, followUserRequest } from '@/api/manage_url/user/UserFollowApi';
import { useCounterStore } from '@/store/UserStore.js'
import { onBeforeRouteUpdate } from 'vue-router'
const UserStore = useCounterStore()

const route = useRouter()
const userInfoData = ref({})
const activeName = ref('first')
const currUserId = ref(route.currentRoute.value.params.userInfoId)
const getInfoByUserId = (userId) => {
    return MyAxios.get('/user/account/get/homepage/', { params: { id: userId } })
}
onMounted(() => {
    getInfoByUserId(currUserId.value).then(res => {
        if (res.code == undefined) {
            userInfoData.value = res
        }
    })
})

const followUser = () => {
    followUserRequest(UserStore.loginUserObject.id, currUserId.value).then(res => {
        if (res.code == undefined) {
            userInfoData.value.fans = userInfoData.value.fans + 1
            userInfoData.value.isFollow = 1
        }
    })
}
const followCancelUser = () => {
    followCancelUserRequest(UserStore.loginUserObject.id, currUserId.value).then(res => {
        if (res.code == undefined) {
            userInfoData.value.fans = userInfoData.value.fans - 1
            userInfoData.value.isFollow = 0
        }
    })
}

onBeforeRouteUpdate((to, from, next) => {
    activeName.value = 'first'
    currUserId.value = to.params.userInfoId
    getInfoByUserId(currUserId.value).then(res => {
        if (res.code == undefined) {
            userInfoData.value = res
        }
    })
    next()
})
</script>

<style scoped>
body {
    background-color: aqua;
}

.container {
    width: 1000px;
    margin-top: 30px;
}

.top,
.bottom {
    margin-top: 20px;
    padding: 20px;
}

.bottom {
    padding: 0px;
}

.right {
    text-align: right;
}

.imgDom img {
    border-radius: 50%;
    height: 96px;
    width: 96px;
}

.username {
    font-size: 18px;
    margin-bottom: 8px;
}

.introduction {
    font-size: 14px;
    color: gray;
}

.centerInfo {
    margin-left: 20px;
}
</style>