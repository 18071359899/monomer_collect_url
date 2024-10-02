<template>
  <div class="box">
    <div class="container">
      <el-card class="chooseCard">
        <div class="flex gap-2">
          <el-tag type="primary" size="large" class="tag" :style="currTags === 'new' ? 'color: #6492FF;' : ''"
            @click="changeTags('new')">最新</el-tag>
          <el-tag type="success" size="large" class="tag" @click="changeTags('follow')" style="margin-left: 5px;"
            :style="currTags === 'follow' ? 'color: #6492FF;' : ''">关注</el-tag>
        </div>
      </el-card>
      <div class="contentBox" v-infinite-scroll="load" infinite-scroll-distance="1" infinite-scroll-immediate="false">
        <div class="content-item" v-for="share in shareList" :key="share.id">
          <el-card class="content-card">
            <div class="topUser">
              <div class="avatar"><img :src="share.photo" alt="" v-clickToUserInfoFocus="share.userId"></div>
              <div class="username"><span>{{ share.username }}
                </span></div>
            </div>
            <div class="title">
              <router-link class="router-link" :to="{ name: 'showShare', params: { shareId: share.id } }">{{ share.title
                }}</router-link>
            </div>
            <div class="mainBody" :id="'content_' + share.id" :style="controlHeightByButton(share)"></div>
            <div class="expandAll" @click="handleClickExpandAllOrClose(share)">{{ share.expandAllOrClose }}</div>
            <div class="contentFooter">
              <div class="buttonStates">
                <span class="updateTime">{{ share.createTime }}</span>
                <el-divider direction="vertical" class="dividerVertical" />
                <span class="buttonState">
                  <LikeIcon v-if="share.isLike == 0" class="icon" @click="handleClickLike(share, 1)" />
                  <LikeActiveIcon v-else class="icon" @click="handleClickLike(share, 0)" />
                  <span class="rightCount">{{ share.like }}</span>
                </span>
                <el-divider direction="vertical" class="dividerVertical" />
                <span class="buttonState">
                  <CollectIcon v-if="share.isCollect == 0" class="icon" @click="handleClickCollect(share, 1)" />
                  <CollectActiveIcon v-else class="icon" @click="handleClickCollect(share, 0)" />
                  <span class="rightCount">{{ share.collect }}</span>
                </span>
                <el-divider direction="vertical" class="dividerVertical" />
                <span class="buttonState">
                  <CommontIcon class="icon" @click="share.isShowComment = !share.isShowComment" />
                  <span class="rightCount">{{ share.comment }}</span>
                </span>
                <span v-if="share.userId == userStore.loginUserObject.id">
                  <el-divider direction="vertical" class="dividerVertical" />
                  <span class="buttonState">
                    <el-popover trigger="click" :show-arrow="false">
                      <template #reference>
                        <el-icon class="icon">
                          <More />
                        </el-icon>
                      </template>
                      <div class="editShare"><router-link :to="{ name: 'shareUpdateId', params: { shareId: share.id } }"
                          class="router-link">编辑</router-link></div>
                      <div class="deleteShare" @click="isDelete(share.id)">删除</div>
                    </el-popover>
                  </span>
                </span>
              </div>
            </div>
            <CommentOnShareBottom v-if="share.isShowComment" :shareId="share.id" :commentCount="share.comment" />
          </el-card>
        </div>
        <el-card>
          <el-empty :image-size="200" v-if="shareList.length == 0" />
        </el-card>
      </div>
    </div>
    <div style="margin-bottom:30px;"></div>
  </div>
</template>

<script setup>
import { onMounted, ref, reactive, nextTick, onUnmounted } from "vue";
import LikeIcon from "@/components/icons/LikeIcon.vue";
import LikeActiveIcon from "@/components/icons/LikeActiveIcon.vue";
import CollectIcon from "@/components/icons/CollectIcon.vue";
import CommontIcon from "@/components/icons/CommontIcon.vue";
import CollectActiveIcon from "@/components/icons/CollectActiveIcon.vue";
import MyAxios from '@/utils/MyAxios.js'
import { vClickToUserInfoFocus } from '@/js/directives/ClickToUserInfo';
import { useCounterStore } from '@/store/UserStore.js'
import { vClickOutside } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { handleClickExpandAllOrClose, controlHeightByButton, renderingMarkDown, handleClickLike, handleClickCollect } from '@/js/share/ShareInfo'
import CommentOnShareBottom from '@/views/share/comment/CommentOnShareBottom.vue'
const userStore = useCounterStore()
const currTags = ref('new')

const pageObject = reactive({ page: 1, size: 10 })
const shareList = ref([])
const isHave = ref(false)
let cursor = null
let offset = 0;
let lastId = 0;
const deleteShareRequest = (id) => {
  return MyAxios.post(`/share/delete/?id=${id}`)
}
const followShareList = () => {
  return MyAxios.get(`/share/follow/list/`, { params: { lastId, offset } })
}
const clearPageData = () => {
  pageObject.page = 1;
  shareList.value.splice(0, shareList.value.length);
}
const isDelete = (id) => {
  ElMessageBox.confirm(
    '是否确定删除？不可找回',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      deleteShareRequest(id).then(res => {
        if (res.code == undefined) {
          ElMessage({ type: 'success', message: '删除成功' })
          clearPageData()
          cursor = null;
          getShareList();
        }
      })
    })
    .catch(() => {
    })
}
const handleFollowShareList = () => {
  followShareList(lastId, offset).then(res => {
    if (res != undefined || res != null) {
      shareList.value.push(...res.data)
      lastId = res.lastTime
      offset = res.offset
      shareList.value.forEach(value => {
        value.isShowComment = false
      })
      nextTick(() => {
        renderingMarkDown(shareList.value)
      })
    }
  })
}
const initCursorParams = () => {
  offset = 0;
  lastId = (new Date()).valueOf()
}
const changeTags = (newTag) => {
  currTags.value = newTag;
  clearPageData()
  initCursorParams()
  if (currTags.value === 'new') {
    getShareList()
  } else if (currTags.value === 'follow') {
    handleFollowShareList(lastId, offset)
  }
}
const getShareList = () => {
  MyAxios.get("/share/list/", { params: { cursor } }).then(res => {
    if (res.code == undefined) {
      shareList.value.push(...res.list)
      isHave.value = res.isLast
      cursor = res.cursor
      shareList.value.forEach(value => {
        value.isShowComment = false
      })
      nextTick(() => {
        renderingMarkDown(shareList.value)
      })
    }
  })
}
const load = () => {
  if (isHave.value == false) {
    isHave.value = true
    if (currTags.value === 'new') {
      getShareList();
    } else {
      handleFollowShareList()
    }
  }
};

onMounted(() => {
  getShareList();
  document.querySelector('body').setAttribute('style', 'background-color: #fffff')
});
onUnmounted(() => {
  document.querySelector('body').removeAttribute('style')
})
</script>

<style src="@/css/share/ShareInfo.css" scoped></style>
<style scoped>
.followTag {
  margin-left: 5px;
}
</style>
