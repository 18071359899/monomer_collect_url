<template>
    <div class="uploader-panel">
        <div class="uploader-title">
            <span>上传任务</span>
            <span class="tips">（仅展示本次上传任务）</span>
        </div>
        <div class="file-list">
            <div v-for="(item) in fileList" class="file-item" :key="item.uid">
                <div class="uploader-panel">
                    <div class="file-name">
                        {{ item.fileName }}
                    </div>
                    <div>
                        <el-progress :percentage="item.uploadProgress" v-if="item.status == STATUS.uploading.value">
                        </el-progress>
                    </div>
                    <div class="uploader-status">
                        <!-- 图标 -->
                        <svg-icon :icon-class="item.status" style="margin-right: 5px;" />
                        <!-- 状态描述   -->
                        <span class="status" :style="{ color: STATUS[item.status].color }">
                            {{
                item.status == 'fail' ? item.errorMsg : STATUS[item.status].desc
            }}
                        </span>
                        <!-- 上传中 -->
                        <span class="upload-info" v-if="item.status == STATUS.uploading.value">
                            {{ formatBytes(item.uploadSize) }} / {{ formatBytes(item.totalSize) }}
                        </span>
                    </div>
                </div>
                <div class="op">
                    <!-- md5 -->
                    <el-progress type="circle" :width="50" :percentage="item.md5Progress"
                        v-if="item.status == STATUS.init.value">
                    </el-progress>
                </div>
                <div class="icons">
                    <svg-icon :icon-class="item.pause === false ? 'continue' : 'stop'"
                        style="margin-right: 10px;  cursor: pointer;" v-if="item.status == STATUS.uploading.value"
                        @click="handleStopUploadFile(item)" />
                    <svg-icon icon-class="delete" style="margin-left:10px;  cursor: pointer;" v-if="item.status == STATUS.uploading.value ||
                item.status == STATUS.upload_seconds.value ||
                item.status == STATUS.upload_finish.value" @click="deleteUploadFile(item.file.uid)" />
                </div>
            </div>
            <!-- <div v-if="fileList.length <= 0 ">        -->
            <el-empty v-if="fileList.length <= 0" :image-size="200" />
            <!-- </div> -->
        </div>
    </div>
</template>

<script setup>
import { defineExpose, ref, onMounted,defineEmits } from 'vue'
import { formatBytes } from '@/utils/ByteToString';
import { uploaderFileApi, queryUploadFileApi, unionUploadFileApi } from '@/api/uploader/UploaderFile';
import { selectCurrUserIsSpaceRequest } from '@/api/upload_file/upload_file/UploadFileApi';
import { ElMessage } from 'element-plus'
onMounted(() => {
})
const  emits = defineEmits(['unionFileSuccess'])
const STATUS = {
    emptyfile: { value: 'emptyfile', desc: '文件为空', color: '#F75000', icon: 'close' },
    fail: { value: 'fail', desc: '上传失败', color: '#F75000', icon: 'close' },
    init: { value: 'init', desc: '解析中', color: '#e6a23c', icon: 'clock' },
    uploading: { value: 'uploading', desc: '上传中', color: '#409eff', icon: 'upload' },
    fileTranscode: { value: 'fileTranscode', desc: '文件转码中', color: '#409eff', icon: 'upload' },
    upload_finish: { value: 'upload_finish', desc: '上传完成', color: '#409eff', icon: 'ok' },
    upload_seconds: { value: 'upload_seconds', desc: '秒传', color: '#67c23a', icon: 'ok' },
}
const fileList = ref([])
const deleteUploadFile = (fileUid) => {  //根据uid删除正在上传的文件
    fileList.value.forEach((item, index) => {
        if (item.file.uid === fileUid) {
            fileList.value.splice(index, 1)
            return;
        }
    })
}
const addFile = async (file, filePid) => {
    const fileItem = {
        file: file,  //文件源对象，包含文件大小，文件流，文件名等数据
        uid: file.uid,  //文件uid
        md5Progress: 0,  //解析MD5进度
        md5: null, //MD5值，最开始未解析为null
        fileName: file.name, //文件名称
        status: STATUS.init.value, //上传状态
        uploadSize: 0,  //已经上传大小
        totalSize: file.size,  //文件总大小
        uploadProgress: 0,//文件上传进度
        pause: false, //是否暂停
        chunkIndex: 0, //当前分片
        filePid: filePid, //文件父级id
        errorMsg: null, //错误信息
    }
    try{
        const isSuccess = await selectCurrUserIsSpaceRequest(file.size)
        if(isSuccess != null && isSuccess.code != undefined){
            return;
        }
    }catch(error){
        return;
    }
    fileList.value.unshift(fileItem)
    if (fileItem.totalSize == 0) {
        fileItem.status = STATUS.emptyfile.value
        return;
    }
    computeMd5(fileItem, function (md5FileUid) {
        if (md5FileUid == null) {  //计算出错
            return;
        }
        uploadFile(md5FileUid)
    })

}
const chunkSize = 1024 * 1024 * 10  //每个分片10M
let recordAllStopChunkIndex = []  //记录所有暂停的文件上传关键数据：文件的uid、当前上传到哪个分片了
const computeChunks = (totalSize) => {
    return Math.ceil(totalSize / chunkSize)
}
const getFileItemByUid = (uid) => {  //受闭包影响，通过全局变量拿到对应上传文件对象，从而改变才会修改成功
    let file = fileList.value.find(item => {
        return item.file.uid === uid
    })
    return file
}
const setUploadFileSuccess = (fileUid,errorMsg) =>{
   const result =  getFileItemByUid(fileUid)
   if(result == null) {
    result.status = STATUS['fail'].value
    result.errorMsg = errorMsg
    return;
   }
   result.status = STATUS['upload_finish'].value
} 

//计算文件的md5值
const computeMd5 = (fileItem, callBack) => {
    let worker = new Worker(new URL('./hash.js', import.meta.url))
    worker.postMessage({ fileItem: fileItem })
    fileItem = getFileItemByUid(fileItem.file.uid)
    worker.onmessage = function (event) {
        const { md5, percentage } = event.data
        fileItem.md5Progress = percentage
        if (md5 != null) {
            fileItem.status = STATUS.uploading.value
            fileItem.md5 = md5
            callBack(fileItem.file.uid)
        }
    }
    worker.onerror = function (event) {
        console.log(event);
        worker.terminate()  //出错后关闭子线程
    }
}
const handleStopUploadFile = (item) => {
    item.pause = !item.pause
    const fileUid = item.file.uid
    let stopChunkIndex = recordAllStopChunkIndex.find(item=>{
        return item.uid === fileUid
    })
    if(stopChunkIndex == null) return;
    uploadFile(fileUid,stopChunkIndex.chunkIndex)
}
//执行分片上传逻辑
const uploadFile = async (fileUid, fromChunkIndex) => {
    let fileItem = getFileItemByUid(fileUid)
    if (fileItem == undefined) return
    if (fromChunkIndex == null) {  //如果不是点击暂停继续上传
        const secondResult = await queryUploadFileApi(fileItem.md5,fileItem.filePid,fileItem.file.size,fileItem.file.name)
        if (secondResult != null && secondResult.fileStatus == STATUS.upload_seconds.value) {  //秒传
            fileItem.status = STATUS[secondResult.fileStatus].value
            fileItem.uploadProgress = 100
            return;
        }
    }
    let chunkIndex = fromChunkIndex || 0
    let file = fileItem.file
    let fileSize = file.size
    //分片上传
    let chunks = computeChunks(fileSize)
    const taskPool = []
    const maxTask = 2 //最大异步处理数量
    for (let i = chunkIndex; i < chunks; i++) {
        fileItem = getFileItemByUid(fileUid)
        if (fileItem == null || fileItem.pause) { //处理删除或暂停逻辑
            await Promise.all(taskPool)
            if(fileItem != null)
            recordAllStopChunkIndex.push({uid: file.uid,chunkIndex: i})  //记录暂停的具体信息
            return;
        }
        let start = i * chunkSize
        let end = start + chunkSize >= fileSize ? fileSize : start + chunkSize
        let chunkFile = file.slice(start, end)
        const task = uploaderFileApi({
            file: chunkFile, chunkIndex: i, chunks: chunks, fileMd5: fileItem.md5
        })
        task.then(res => {
            if (res.code == undefined && res.fileStatus === 'uploading' && fileItem != null) {  //计算上传速度
                fileItem.uploadSize = fileItem.uploadSize + chunkFile.size
                fileItem.uploadProgress = Math.floor((fileItem.uploadSize / fileSize) * 100)
            }
            taskPool.splice(taskPool.findIndex((item) => item === task),1)  //清除已经完成的分片请求
        }).catch(error => { console.log(error); taskPool.splice(taskPool.findIndex((item) => item === task),1) })
        taskPool.push(task)
        if (taskPool.length >= maxTask) {
            await Promise.race(taskPool)  //race方法会在这些请求中第一个完成后结束这个阻塞代码，限制最大请求数量
        }
    }
    await Promise.all(taskPool)   //将剩下的分片发送并等待所有分片完成
    //所有分片上传完成，执行合并操作
    unionUploadFileApi({ fileName: file.name, fileMd5: fileItem.md5,pid: fileItem.filePid }).then(res => {
        if (res != null && res.code == undefined) {
            fileItem.uploadProgress = 100
            fileItem.status = STATUS["fileTranscode"].value
            emits('unionFileSuccess',{ monthDay: res.monthDay,realFileName: res.realFileName,md5: fileItem.md5,
                fileName: file.name,pid: fileItem.filePid,fileSize: fileSize},file.uid)
        } else {
            fileItem.status = STATUS["fail"].value
            fileItem.errorMsg = res.message
        }
    }).catch(error => { console.log(error); fileItem.status = STATUS["fail"].value; fileItem.errorMsg = '合并失败' })
}
defineExpose({
    addFile,setUploadFileSuccess
})
</script>
<style scoped>
.uploader-panel {
    .uploader-title {
        border-bottom: 1px solid #ddd;
        line-height: 40px;
        padding: 0 10px;
        font-size: 15px;

        .tips {
            font-size: 13px;
            color: rgb(169, 169, 169);
        }
    }

    .file-list {
        overflow: auto;
        padding: 10px 0px;
        min-height: calc(100vh / 2);
        max-height: calc(100vh -120px);

        .file-item {
            position: relative;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 3px 10px;
            background-color: #FFF;
            border-bottom: 1px solid #ddd;
        }

        .file-item:nth-child(even) {
            background-color: #fcf8f4;
        }

        .uploader-panel {
            flex: 1;
            .file-name {
                color: rgb(64, 62, 62);
            }

            .uploader-status {
                display: flex;
                align-items: center;
                margin-top: 5px;

                .iconfont {
                    margin-top: 3px;
                }

                .status {
                    color: red;
                    font-size: 13px;
                }

                .upload-info {
                    margin-left: 5px;
                    font-size: 12px;
                    color: rgb(112, 111, 111);
                }
            }

        }

        .icons {
            margin-left: 20px;
        }

        .op {
            width: 100px;
            display: flex;
            align-items: center;
            justify-content: flex-end;

            .op-btn {
                .btn-item {
                    cursor: pointer;
                }

                .del,
                .clean {
                    margin-left: 5px;
                }
            }
        }
    }
}
</style>


// const uploadResult = await uploaderFileApi({file: chunkFile,fileCode,fileMd5: fileItem.md5,
// chunkIndex: i,chunks: chunks,fileName: file.name},function (progressEvent){
// let loaded = progressEvent.loaded
// if(loaded > fileSize){
// loaded = fileSize
// }
// fileItem.uploadSize = i * chunkSize + loaded
// fileItem.uploadProgress = Math.floor((fileItem.uploadSize / fileSize) * 100)
// })
// if(uploadResult == null) break;
// const resultStatus = uploadResult.fileStatus
// fileItem.status = STATUS[resultStatus].value
// if(resultStatus == STATUS.upload_finish.value){
// fileItem.uploadProgress = 100
// break;
// }