<template>
    <el-card class="card-box-comment">
        <div class="comment-text">{{ commentCount + '个评论' }}</div>
        <el-row class="publishComment" align="top">
            <div class="userPhoto"><img :src="userStore.loginUserObject.photo" alt=""></div>
            <el-col :span="22" style="margin-left:20px;">
                <div class="inputBox">
                    <el-input v-model="inputValue" type="textarea" placeholder="请填写评论，支持markdown语法" rows="3" />
                </div>
            </el-col>
        </el-row>
        <div style="text-align: right;">
            <el-button type="primary" style="margin-top: 20px;margin-right:20px;"
                @click="submitComment(0)">发评论</el-button>
        </div>
        <div class="contentBox" v-infinite-scroll="load" infinite-scroll-distance="1" infinite-scroll-immediate="false">
            <CommentListSubView @submitComment="submitComment" @submitDeleteComment="submitDeleteComment"
                :commentList="commentList" :findCommentId="findCommentId" />
        </div>
        <div style="margin-top: 10px;">
            <div v-if="isHave == true" style="text-align:center;color:gray;">正在加载中……</div>
            <div v-if="isHave == false" style="text-align:center;color:gray;">没有更多评论</div>
        </div>

    </el-card>
</template>


<script setup>
import { ref, defineProps, onMounted, reactive } from "vue";
import CommentListSubView from "./CommentListSubView.vue";
import { useCounterStore } from '@/store/UserStore.js'
import { addComment } from '@/js/share/comment/ManageComment.js'
import { formatDate } from "@/js/ConvertFormatData";
import { addCommentRequest, getCommentRequest, deleteCommentRequest, getMessageCommentRequest, messageLastListCommentRequest } from "@/js/share/comment/CommentRequest";
const inputValue = ref("")
const pageObject = reactive({ page: 1, size: 10 })
const userStore = useCounterStore()
const props = defineProps(['shareId', 'commentCount', 'findCommentId'])
let commentList = ref([]);
const isHave = ref(true);
onMounted(() => {
    getComments()
})
const load = () => {
    if (isHave.value == true) {
        isHave.value = false
        if (!props.findCommentId) {
            getComments();
        } else {
            getLastComments();
        }
    }
};
const getLastComments = () => {
    messageLastListCommentRequest(pageObject,props).then(res => {
        if (res.code == undefined) {
            commentList.value.push(...res.data);
            isHave.value = res.have;
            pageObject.page++;
        }
    })
}
let getComments = () => {
    if (!props.findCommentId) {
        getCommentRequest(pageObject, props).then(res => {
            if (res.code == undefined) {
                commentList.value.push(...res.data);
                isHave.value = res.have;
                pageObject.page++;
            }
        })
    } else {
        getMessageCommentRequest(props).then(res => {
            if (res.code == undefined) {
                commentList.value.push(...res.data);
                isHave.value = res.have;
            }
        })
    }

};
//提交评论
const submitComment = (id, comment) => {
    let newComment = comment
    if (!newComment) {
        newComment = {
            shareId: props.shareId,
            content: inputValue.value,
            userId: userStore.loginUserObject.id,
            toUserId: 0,
            rootId: 0,
            fatherId: 0,
            userPhoto: userStore.loginUserObject.photo,
            userUsername: userStore.loginUserObject.username,
            children: null,
            toUserUsername: null,
            agree: 0,
        };
    }
    addCommentRequest(newComment).then(resp => {
        if (resp.code == undefined) {
            inputValue.value = ""
            newComment.id = resp;
            newComment.createTime = formatDate(new Date());
            addComment(commentList.value, id, newComment);
        }
    })
};
const submitDeleteComment = (comment, index) => {
    deleteCommentRequest(comment).then(res => {
        if (!res || res.code == undefined) {
            deleteComment(index, res);
        }
    })
}
const deleteComment = (index, comment) => {
    //删除级别：一级
    if (comment === null) {
        commentList.value.splice(index, 1);
        return;
    }
    //删除级别：多级
    commentList.value.splice(index, 1, comment);
}
</script>
<style src="@/css/share/comment/CommentInfo.css" scoped></style>

<style scoped>
.card-box-comment {
    padding: 30px;
    margin-top: 20px;
    margin-bottom: 30px;
}

.comment-text {
    font-size: 20px;
}
</style>