<template>
    <div style="margin-top: 70px;">
        <div class="container">
            <div style="text-align: center;">
                <el-input v-model="search" placeholder="请输入要搜索的内容" size="large" class="input" :prefix-icon="Search" />
            </div>
            <el-card class="bottom">
                <el-tabs v-model="activeName" class="demo-tabs">
                    <el-tab-pane label="分享" name="share">
                        <SearchOnShare ref="viewRef" :search="search"  v-if="activeName == 'share'" />
                    </el-tab-pane>
                    <el-tab-pane label="用户" name="user">
                        <SearchOnUserInfo ref="viewRef" :search="search" v-if="activeName == 'user'" />
                    </el-tab-pane>
                </el-tabs>
            </el-card>
        </div>

    </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import SearchOnShare from '@/views/search/SearchOnShare.vue'
import SearchOnUserInfo from '@/views/search/SearchOnUserInfo.vue'
import { Search } from '@element-plus/icons-vue'
const search = ref("")
const activeName = ref("share")
onMounted(() => {
})
</script>


<style scoped>
.input {
    width: 70%;
}

.bottom {
    margin-top: 30px;
}
</style>