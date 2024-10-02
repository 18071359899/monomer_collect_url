<template>
  <el-row :class="findCommentId != null && comment.id === parseInt(findCommentId) ? 'toCommentData' : ''">
    <router-link class="router-link" :class="comment.rootId == 0 ? 'photo_one' : 'photo_two'"
      :to="{ name: 'userInfo', params: { userInfoId: comment.userId } }">
      <img :src="comment.userPhoto"
        :id="findCommentId != null && comment.id === parseInt(findCommentId) ? 'toCommentData' : ''" />
    </router-link>
    <el-col :span="22" class="right">
      <router-link class="router-link username" :to="{ name: 'userInfo', params: { userInfoId: comment.userId } }">
        {{ comment.userUsername }}
      </router-link>
      <span v-if="comment.toUserUsername !== null" class="replyBox">
        回复了<router-link class="router-link usernameReply"
          :to="{ name: 'userInfo', params: { userInfoId: comment.toUserId } }">{{ comment.toUserUsername
          }}</router-link></span>
      <div class="text" id="content" ref="content">{{ comment.content }}</div>
      <span class="createTime">{{ comment.createTime }}</span>
      <LikeIcon v-if="!like" class="icon" @click="updateLike" />
      <LikeActiveIcon v-else class="icon" @click="updateLike" />
      <span class="agree" id="asdqwewe">{{ countAgree }}</span>
      <button type="button" class="btn reply" @click="open_reply">
        {{ state }}
      </button>
      <el-icon v-if="parseInt(userStore.loginUserObject.id) === parseInt(comment.userId)" class="deleteBtn"
        @click="isDelete">
        <Delete />
      </el-icon>

      <div v-show="isShowReply" style="margin-top:10px;">
        <el-input v-model="inputValue" type="textarea" placeholder="请填写评论，支持markdown语法" rows="3" />
        <div style="text-align: right">
          <button type="button" class="btn" style="margin-top: 10px" @click="submitComment">
            提交评论
          </button>
        </div>
      </div>
    </el-col>
  </el-row>

</template>
<script setup>
import { ref, defineProps, defineEmits, onMounted } from "vue";
import { useCounterStore } from '@/store/UserStore.js'
import LikeIcon from "@/components/icons/LikeIcon.vue";
import LikeActiveIcon from "@/components/icons/LikeActiveIcon.vue";
import { ElMessage, ElMessageBox } from 'element-plus'
import MyAxios from '@/utils/MyAxios.js'
import { renderMarkDown } from "@/js/vditor/RenderMarkdown";
const userStore = useCounterStore()
const props = defineProps(['comment', 'findCommentId'])
const emits = defineEmits(['submitComment', 'submitDeleteComment'])
let like = ref(false);
let countAgree = ref(props.comment.agree);
let state = ref("回复");
const inputValue = ref("")
const isShowReply = ref(false)
const content = ref(null)
const updateCommentLikeRequest = (userBehavior) => {
  return MyAxios.post('/share/behavior/comment/', userBehavior)
}
onMounted(() => {
  like.value = props.comment.isLike == 1 ? true : false
  renderMarkDown(content.value, props.comment.content)
  const result = document.getElementById('toCommentData')
  if (result) {
    result.scrollIntoView({  //消息通知点开的评论详情
      behavior: "instant",
      block: "center",
    });
  }
})
const updateLike = () => {
  like.value = !like.value;
  if (like.value) {
    updateCommentLikeRequest({ id: props.comment.id, type: 2, isAdd: 1 }).then(res => {
      countAgree.value = res
    })
  } else {
    updateCommentLikeRequest({ id: props.comment.id, type: 2, isAdd: 0 }).then(res => {
      countAgree.value = res
    })
  }
};

//提交多级评论
const submitComment = () => {
  let toUserUsername = props.comment.userUsername;
  let rootId = props.comment.rootId;
  //二级评论
  if (props.comment.rootId === 0) {
    rootId = props.comment.id;
    toUserUsername = null;
  }
  let newComment = {
    shareId: props.comment.shareId,
    content: inputValue.value,
    userId: userStore.loginUserObject.id,
    toUserId: props.comment.userId,
    rootId: rootId,
    fatherId: props.comment.id,
    userPhoto: userStore.loginUserObject.photo,
    userUsername: userStore.loginUserObject.username,
    toUserUsername: toUserUsername,
    children: null,
    agree: 0,
  };

  //触发父元素中的函数
  emits("submitComment", props.comment.id, newComment);
  open_reply(props.comment.id);
};
const deleteComment = () => {
  let rootId = props.comment.rootId;
  if (rootId === 0) {
    rootId = props.comment.id;
  }
  emits("submitDeleteComment", props.comment);
}
const isDelete = () => {
  ElMessageBox.confirm(
    '你确定删除吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      deleteComment()
    })
    .catch(() => {
    })
}
const open_reply = () => {
  isShowReply.value = !isShowReply.value
  if (state.value == "收起") {
    state.value = "回复";
  } else {
    state.value = "收起";
  }
};
</script>
<style scoped>
.deleteBtn {
  cursor: pointer;
}

.toCommentData {
  animation-duration: 3s;
  animation-name: flash;
}

/* 闪烁 */
@keyframes flash {

  0%,
  50%,
  100% {
    opacity: 1;
  }

  25%,
  75% {
    opacity: 0;
  }
}

.btn:hover {
  color: rgb(13, 110, 253);
}

.username {
  font-weight: 500;
  color: #61666d;
}

.username:hover {
  color: rgb(13, 110, 253);
}

.photo_one img {
  border-radius: 50%;
  width: 45px;
  height: 45px;
}

.photo_two img {
  border-radius: 50%;
  width: 30px;
  height: 30px;
}

.a {
  color: rgb(51, 122, 199);
  text-decoration: none;
  font-size: 16px;
}

.a:hover {
  color: rgb(35, 82, 124);
  text-decoration: underline;
}

.usernameReply {
  margin-left: 5px;
  color: #008AC5;
}

.usernameReply:hover {
  color: #40C5F1;
}

.replyBox {
  margin-left: 10px;
}

.icon {
  margin-left: 20px;
}

.agree {
  margin-left: 5px;
}

.agree,
.createTime,
.reply {
  text-align: left;
  color: #9499A0;
  font-weight: 400;
  font-size: 14px;
}

.reply {
  border: none;
  margin-left: 20px;
}

.text {
  font-weight: 400;
  color: #18191C;
  margin-top: 8px;
  margin-bottom: 8px;
  font-size: 18px;
}

textarea {
  padding: 10px;
}

.right {
  padding-left: 15px;
}
</style>