<template>
    <div class="box">
        <div class="container">
            <el-card class="card-box">
                <el-row align="middle">
                    <el-col :span="21">
                        <div>消息通知</div>
                    </el-col>
                    <el-col :span="3" style="text-align: right;">
                        <el-button type="primary" plain @click="HandleAllRead">全部已读</el-button>
                    </el-col>
                </el-row>
                <el-divider border-style="double" style="margin-top: 15px;" />
                <div class="content" v-for="MessageNotify in MessageNotifyList" :key="MessageNotify.id">
                    <el-row>
                        <div class="imgDom">
                            <router-link class="linkCss" :to="{
                        name: 'userInfo', params:
                            { userInfoId: MessageNotify.sendUserId }
                    }">
                                <img :src="MessageNotify.sendPhoto" alt="">
                            </router-link>
                        </div>
                        <el-col :span="18">
                            <div style="margin-left:20px;">
                                <router-link class="linkCss" :to="{
                        name: 'userInfo', params:
                            { userInfoId: MessageNotify.sendUserId }
                    }">
                                    <div class="username interactionUsernameToJump">{{ MessageNotify.sendUserUsername }}
                                    </div>
                                </router-link>
                                <div v-if="MessageNotify.type === 0">
                                    <router-link class="linkCss" :to="{
                        name: 'userInfo', params:
                            { userInfoId: MessageNotify.sendUserId }
                    }">
                                        <span class="interactionUsernameToJump">{{ MessageNotify.sendUserUsername + '\t'
                                            }}</span>
                                    </router-link>
                                    <span>关注了我</span>
                                </div>
                                <div v-else-if="MessageNotify.type === 1">
                                    <span>赞了</span>
                                    <router-link class="linkCss" :to="{
                        name: 'showShare', params:
                            { shareId: MessageNotify.businessId }
                    }">
                                        <span class="interactionUsernameToJump">{{
                        '\t' + MessageNotify.businessData }}</span>
                                    </router-link>
                                </div>
                                <div v-else-if="MessageNotify.type === 2">
                                    <span>赞了</span>
                                    <router-link class="linkCss" :to="{
                        name: 'messageShowShare', params:
                            { shareId: MessageNotify.shareId, commentId: MessageNotify.businessId }
                    }">
                                        <span class="interactionUsernameToJump">{{
                        '\t' + MessageNotify.businessData + '\t' }}</span>
                                    </router-link>

                                    <span>的评论</span>
                                </div>
                                <div v-else-if="MessageNotify.type === 3">
                                    <span>回复了</span>
                                    <router-link class="linkCss" :to="{
                        name: 'messageShowShare', params:
                            { shareId: MessageNotify.shareId, commentId: MessageNotify.businessId }
                    }">
                                        <span class="interactionUsernameToJump">{{ '\t' + MessageNotify.businessData
                                            }}</span>
                                    </router-link>
                                </div>
                                <div v-else-if="MessageNotify.type === 4">
                                    <span>收藏了</span>
                                    <router-link class="linkCss" :to="{
                        name: 'showShare', params:
                            { shareId: MessageNotify.businessId }
                    }">
                                        <span class="interactionUsernameToJump">{{ '\t' + MessageNotify.businessData
                                            }}</span>
                                    </router-link>
                                </div>
                                <div v-else-if="MessageNotify.type === 5">
                                    <span>评论了</span>
                                    <router-link class="linkCss" :to="{
                        name: 'messageShowShare', params:
                            { shareId: MessageNotify.shareId, commentId: MessageNotify.businessId }
                    }">
                                        <span class="interactionUsernameToJump">{{ '\t' + MessageNotify.businessData
                                            }}</span>
                                    </router-link>
                                </div>
                            </div>
                        </el-col>
                        <el-col :span="4">
                            <el-row align="bottom" class="time">
                                <el-col :span="24">
                                    <span>{{ MessageNotify.createTime }}</span>
                                </el-col>
                            </el-row>
                        </el-col>

                    </el-row>
                    <div class="contentDivide">
                        <el-divider border-style="double" style="margin-top: 10px;" />
                    </div>
                </div>
                <el-empty :image-size="200" v-if="MessageNotifyList.length === 0" />
                <PageComponent @getData="getData" ref="pageCommponent" style="margin-top: 20px"
                    v-show="MessageNotifyList.length > 0" />
            </el-card>
        </div>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { listMessageNotify, allReadMessageNotify, readMessageNotify } from '@/api/message_notify/MessageNotifyRequest.js'
import { useCounterStore } from '@/store/UserStore.js'
import PageComponent from "@/components/PageComponent.vue";
import { ElMessage, ElMessageBox } from 'element-plus'
const userStore = useCounterStore()
const MessageNotifyList = ref([])
const pageCommponent = ref(null)
const getData = () => {
    listMessageNotify(userStore.loginUserObject.id, pageCommponent.value.currentPage, pageCommponent.value.pageSize).then(res => {
        if (res.code == undefined) {
            MessageNotifyList.value = res.records;
            pageCommponent.value.setAllPageAttributes(res);
        }
    })
}
const HandleAllRead = () => {
    allReadMessageNotify(userStore.loginUserObject.id).then(res => {
        if (res == "ok") {
            ElMessage({ type: 'success', message: '操作成功' })
        }
    })
}
onMounted(() => {
    getData()
})

</script>

<style scoped>
.container {
    width: 1000px;
}

.card-box {
    padding: 20px;
}

.box {
    margin-top: 30px;
    caret-color: rgba(0, 0, 0, 0);
}

.content {}

.imgDom img {
    cursor: pointer;
    border-radius: 50%;
    height: 60px;
    width: 60px;
}

.username {
    width: fit-content;
    font-size: 16px;
    margin-bottom: 8px;
}

:deep .contentDivide>.el-divider--horizontal {
    border-top: 1px solid rgba(0, 0, 0, .06);
}

.time {
    margin-top: 8px;
    text-align: right;
    height: 100%;
}
</style>