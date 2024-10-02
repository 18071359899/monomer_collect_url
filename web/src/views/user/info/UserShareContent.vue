<!-- 展示用户的所有动态信息 -->
<template>
    <div class="contentBox">
        <div class="content" v-for="share in Data" :key="share.id">
            <div class="topUser">
                <div class="avatar"><img :src="share.photo" alt=""></div>
                <div class="username"><span>{{ share.username }}
                </span></div>
              </div>
            <div class="title">
              <router-link class="router-link" :to="{ name: 'showShare',params:{ shareId: share.id} }">{{share.title}}</router-link>
            </div>
            <div class="mainBody" :id="'content_' + share.id" :style="controlHeightByButton(share)"></div>
            <div class="expandAll" @click="handleClickExpandAllOrClose(share)">{{ share.expandAllOrClose }}</div>
            <div class="contentFooter">
                 <div class="buttonStates">
                  <span class="updateTime">{{share.createTime}}</span>
                  <el-divider direction="vertical"   class="dividerVertical"/>
                  <span class="buttonState">
                    <LikeIcon v-if="share.isLike == 0" class="icon" @click="handleClickLike(share,1)"/>
                    <LikeActiveIcon v-else class="icon" @click="handleClickLike(share,0)"/>
                    <span class="rightCount">{{ share.like }}</span>
                  </span>
                  <el-divider direction="vertical"   class="dividerVertical"/>
                  <span class="buttonState">
                    <CollectIcon v-if="share.isCollect == 0" class="icon" @click="handleClickCollect(share,1)"/>
                     <CollectActiveIcon v-else class="icon" @click="handleClickCollect(share,0)"/>
                    <span class="rightCount">{{ share.collect }}</span>
                  </span>
                  <el-divider direction="vertical" class="dividerVertical"/>
                  <span class="buttonState">
                    <CommontIcon class="icon" @click="share.isShowComment = !share.isShowComment" />
                    <span class="rightCount">{{ share.comment }}</span>
                  </span>
                </div>
            </div>
            <CommentOnShareBottom v-if="share.isShowComment" :shareId="share.id" :commentCount="share.comment" />
            <el-divider border-style="double" />  
        </div>
        <PageComponent @getData="getData" ref="pageCommponent" style="margin-top: 20px" v-show="Data.length > 0"/>
        <el-empty :image-size="200"  v-if="Data.length == 0"/>
    </div>
</template>
<script setup>
import { onMounted,ref,defineProps,nextTick } from 'vue';
import MyAxios from '@/utils/MyAxios.js'
import LikeIcon from "@/components/icons/LikeIcon.vue";
import CollectIcon from "@/components/icons/CollectIcon.vue";
import CommontIcon from "@/components/icons/CommontIcon.vue";
import LikeActiveIcon from "@/components/icons/LikeActiveIcon.vue";
import CollectActiveIcon from "@/components/icons/CollectActiveIcon.vue";
import PageComponent from "@/components/PageComponent.vue";
import { handleClickExpandAllOrClose,controlHeightByButton,renderingMarkDown,handleClickLike,handleClickCollect } from '@/js/share/ShareInfo'
import CommentOnShareBottom from '@/views/share/comment/CommentOnShareBottom.vue'
const props = defineProps(['userId','attributes'])  //attributes  0:查询该用户的帖子  1：查询该用户收藏的帖子列表
const Data = ref([])
const pageCommponent = ref(null)

const getShareInfoByUserId = (userId)=>{
    return MyAxios.get('/share/get/info/',{params:{userId: userId,
      page: pageCommponent.value.currentPage,
      size: pageCommponent.value.pageSize,}})
}
const getShareCollectInfoByUserId = (userId)=>{
    return MyAxios.get('/share/behavior/collect/list/',{params:{userId: userId,
      page: pageCommponent.value.currentPage,
      size: pageCommponent.value.pageSize,}})
}
const updateData = (res) =>{
  Data.value = res.records
  Data.value.forEach(value => {
        value.isShowComment = false
      })
  pageCommponent.value.setAllPageAttributes(res);
      nextTick(()=>{
        renderingMarkDown(Data.value)
      })
}
const getData = () =>{
  if(props.attributes == 0){
        getShareInfoByUserId(props.userId).then(res=>{
    if(res.code == undefined){
      updateData(res)
    }
  })
    }else{
        getShareCollectInfoByUserId(props.userId).then(res=>{
    if(res.code == undefined){
      updateData(res)
    }
  })
    }
}
onMounted(() => {
  getData()
})
</script>
<style src="@/css/share/ShareInfo.css" scoped>
</style>
<style scoped>
.contentBox{
  padding: 20px;
}
:deep .el-divider--horizontal{
  border-top: 1px solid rgba(0,0,0,.06);
}
</style>