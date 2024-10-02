<!-- 展示用户的所有关注信息 -->
<template>
    <div class="contentBox">
        <div class="content" v-for="data in Data" :key="data.id">
            <el-row >
                <div class="imgDom">
                    <router-link class="linkCss" :to="{ name: 'userInfo',params:
                    { userInfoId: props.attributes == 1 ? data.userId : data.followUserId } }"
                    >
                    <img :src="data.photo" alt="" >
                    </router-link>
                </div>
                <el-col :span="20">
                    <div style="margin-left:20px;">
                        <router-link class="linkCss" :to="{ name: 'userInfo',params:
                        { userInfoId: props.attributes == 1 ? data.userId : data.followUserId } }"
                        >
                        <div class="username interactionUsernameToJump">{{ data.username}}</div>
                    </router-link>
                        <div class="introduction">{{ data.introduction}}</div>
                    </div>
                </el-col>
                <div>
                    <el-button type="primary" :icon="Plus" v-if="data.linkStatus == 0" @click="followUser(data)">关注</el-button>
                    <el-button type="primary" plain v-if="data.linkStatus == 1"  @click="followCancelUser(data)">已关注</el-button>
                    <el-button type="primary" plain v-if="data.linkStatus == 2"  @click="followCancelUser(data)">已互粉</el-button>
                    <div v-else></div>
                </div>
            </el-row>
            <div style="margin-top: 20px;"></div>
        </div>
        <el-empty :image-size="200"  v-if="Data.length == 0"/>
        <PageComponent @getData="getData" ref="pageCommponent" style="margin-top: 20px"  v-show="Data.length > 0"/>
    </div>
    
</template>
<!-- v-clickToUserInfoFocus="props.attributes == 1 ? data.userId : data.followUserId" -->
<script setup>
import { onMounted,ref,defineProps } from 'vue';
import { Plus} from '@element-plus/icons-vue'
import MyAxios from '@/utils/MyAxios.js'
import PageComponent from "@/components/PageComponent.vue";
import { followCancelUserRequest,followUserRequest } from '@/api/manage_url/user/UserFollowApi';
import { useCounterStore } from '@/store/UserStore.js'
import { vClickToUserInfoFocus } from '@/js/directives/ClickToUserInfo';

const UserStore = useCounterStore()
const props = defineProps(['userId','attributes'])  //attributes 0:查询关注  1:查询粉丝

const followCancelUser = (data) =>{
    let followCancelUserId = data.followUserId
    if(props.attributes === 1){
        followCancelUserId = data.userId
    }
    followCancelUserRequest(UserStore.loginUserObject.id,followCancelUserId).then(res=>{
        if(res.code == undefined){
            data.linkStatus = 0
        }
    })
}
const followUser = (data) =>{
    let followUserId = data.followUserId
    if(props.attributes === 1){
        followUserId = data.userId
    }
    followUserRequest(UserStore.loginUserObject.id,followUserId).then(res=>{
        if(res.code == undefined){
            data.linkStatus = 1
        }
    })
}
const pageCommponent = ref(null)
const Data = ref([])
const getFollowInfoByUserId = (userId)=>{
    return MyAxios.get('/user/follow/list/',{params:{userId: userId,page: pageCommponent.value.currentPage,
      size: pageCommponent.value.pageSize,}})
}
const getFansInfoByUserId = (userId)=>{
    return MyAxios.get('/user/follow/fans/list/',{params:{userId: userId,page: pageCommponent.value.currentPage,
      size: pageCommponent.value.pageSize,}})
}
const getData = () => {
    if(props.attributes == 0){
        getFollowInfoByUserId(props.userId).then(res=>{
    if(res.code == undefined){
        updateData(res)
    }
  })
    }else{
        getFansInfoByUserId(props.userId).then(res=>{
    if(res.code == undefined){
        updateData(res)
    }
  })
    }
}
const updateData = (res) => {
    Data.value = res.records
    pageCommponent.value.setAllPageAttributes(res);
}
onMounted(() => {
    getData()
})
</script>
<style scoped>
.contentBox{
    padding: 20px;
}
.imgDom img{
    cursor: pointer;
    border-radius: 50%;
    height: 48px;
    width: 48px;
}
.username{
    font-size: 18px;
    margin-bottom: 8px;
    width: fit-content;
}
.introduction{
    font-size: 14px;
    color: gray;
}
</style>