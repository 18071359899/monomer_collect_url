<!-- 展示用户的所有关注信息 -->
<template>
    <div class="contentBox">
        <div class="content" v-for="data in Data" :key="data.id">
            <el-row >
                <div class="imgDom" style="display:inline-block;">
                    <router-link class="dropdown-item" :to="{ name: 'userInfo',params:
                    { userInfoId:  data.id } }"
                    >
                    <img :src="data.photo" alt="" >
                    </router-link>
                </div>
                <el-col :span="20">
                    <div style="margin-left:20px;">
                        <div class="username">{{ data.username}}</div>
                        <div class="introduction">{{ data.introduction}}</div>
                    </div>
                </el-col>
                <div>
                    <el-button type="primary" :icon="Plus" v-if="data.isFollow == 0" @click="followUser(data)">关注</el-button>
                    <el-button type="primary" plain v-if="data.isFollow == 1"  @click="followCancelUser(data)">已关注</el-button>
                    <div v-else></div>
                </div>
            </el-row>
            <div style="margin-top: 20px;"></div>
        </div>
        <PageComponent @getData="getData" ref="pageCommponent" style="margin-top: 20px"  v-show="Data.length > 0"/>
        <el-empty :image-size="200"  v-if="Data.length == 0"/>
    </div>
    
</template>
<!-- v-clickToUserInfoFocus="props.attributes == 1 ? data.userId : data.followUserId" -->
<script setup>
import { onMounted,ref,defineProps,defineExpose,watch } from 'vue';
import { Plus} from '@element-plus/icons-vue'
import PageComponent from "@/components/PageComponent.vue";
import { useCounterStore } from '@/store/UserStore.js'
import { searchRequest } from '@/api/search/SearchInfo.js'
import { followCancelUserRequest,followUserRequest } from '@/api/manage_url/user/UserFollowApi';
import debounce from 'lodash/debounce'
const UserStore = useCounterStore()
const followUser = (data) =>{
    followUserRequest(UserStore.loginUserObject.id,data.id).then(res=>{
        if(res.code == undefined){
            data.isFollow = 1
        }
    })
}
const followCancelUser = (data) =>{
    followCancelUserRequest(UserStore.loginUserObject.id,data.id).then(res=>{
        if(res.code == undefined){
            data.isFollow = 0
        }
    })
}

const props = defineProps(['search'])
const Data = ref([])
const pageCommponent = ref(null)
const getData = () =>{
    searchRequest("user",props.search).then(res=>{
        Data.value = res.records
        pageCommponent.value.setAllPageAttributes(res)
    })
}
const debounceSearchData = debounce(getData, 1000)
onMounted(() => {
    getData()
})
watch(()=>props.search,()=>{
    debounceSearchData()
})
const setPageCommentData  = (records) => {
    pageCommponent.value.setAllPageAttributes(records)
}
defineExpose({
    setPageCommentData
})
</script>
<style scoped>
.contentBox{
    padding: 20px;
    caret-color: rgba(0, 0, 0, 0);

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
}
.introduction{
    font-size: 14px;
    color: gray;
}
</style>