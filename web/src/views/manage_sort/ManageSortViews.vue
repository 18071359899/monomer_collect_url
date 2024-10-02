<template>
  <div class="box">
    <div class="leftChoose">
      <div class="leftMenuItem" :class="currActive === '1' ? 'menuItemActive' : ''" @click="clickManage">
        <div class="iconfont icon-guanli manageIcon"></div>
        <div>管理</div>
      </div>
      <div class="leftMenuItem" :class="currActive === '2' ? 'menuItemActive' : ''" @click="clickConfigDelete">
        <div class="iconfont icon-huishouzhan manageIcon"></div>
        <div>回收站</div>
      </div>
    </div>
    <div class="leftCurrUseChoose" v-if="currActive === '1'">
      <div class="commonTypeData" style="margin-top:20px;"
        :class="userChooseType == userChooseTypeEnums.myFile.value ? 'commonTypeDataActive' : ''" 
        @click="handleUserChooseType(userChooseTypeEnums.myFile.value)">
        <svg-icon icon-class="AllFile" className="leftCurrUseIcon" />
        <span class="leftCurrUseText">{{ userChooseTypeEnums.myFile.desc }}
        </span>
      </div>
      <div class="commonTypeData" :class="userChooseType == userChooseTypeEnums.video.value ? 'commonTypeDataActive' : ''"
        @click="handleUserChooseType(userChooseTypeEnums.video.value)">
        <svg-icon icon-class="VideoType" className="leftCurrUseIcon" />
        <span class="leftCurrUseText">{{ userChooseTypeEnums.video.desc }}</span>
      </div>
      <div class="commonTypeData" :class="userChooseType == userChooseTypeEnums.image.value ? 'commonTypeDataActive' : ''"
        @click="handleUserChooseType(userChooseTypeEnums.image.value)">
        <svg-icon icon-class="ImageType" className="leftCurrUseIcon" />
        <span class="leftCurrUseText">{{userChooseTypeEnums.image.desc}}</span>
      </div>
      <div class="commonTypeData" :class="userChooseType == userChooseTypeEnums.document.value ? 'commonTypeDataActive' : ''"
        @click="handleUserChooseType(userChooseTypeEnums.document.value)">
        <svg-icon icon-class="DocumentType" className="leftCurrUseIcon" />
        <span class="leftCurrUseText">{{userChooseTypeEnums.document.desc}}</span>
      </div>
      <div class="commonTypeData" :class="userChooseType == userChooseTypeEnums.audio.value ? 'commonTypeDataActive' : ''"
        @click="handleUserChooseType(userChooseTypeEnums.audio.value)">
        <svg-icon icon-class="AudioType" className="leftCurrUseIcon" />
        <span class="leftCurrUseText">{{userChooseTypeEnums.audio.desc}}</span>
      </div>
      <div class="commonTypeData" :class="userChooseType == userChooseTypeEnums.other.value ? 'commonTypeDataActive' : ''"
        @click="handleUserChooseType(userChooseTypeEnums.other.value)">
        <svg-icon icon-class="OthrerType" className="leftCurrUseIcon" />
        <span class="leftCurrUseText">{{userChooseTypeEnums.other.desc}}</span>
      </div>
      <div class="commonTypeData" :class="userChooseType == userChooseTypeEnums.website.value ? 'commonTypeDataActive' : ''"
        @click="handleUserChooseType(userChooseTypeEnums.website.value)">
        <svg-icon icon-class="WebsiteType" className="leftCurrUseIcon" />
        <span class="leftCurrUseText">{{userChooseTypeEnums.website.desc}}</span>
      </div>
      <div class="showUserSpace">
        <el-progress :percentage="userSpaceData.percentage" />
        <span class="userSpace-text">{{ userSpaceData.userFileUseSpace + '/' + userSpaceData.userFileTotalSpace
          }}</span>
      </div>
    </div>
    <div class="showContent" @contextmenu.stop="handleContextmenu($event)">
      <ManageHome v-if="currActive === '1'" ref="ManageHomeRef" :userChooseType="userChooseType"
      @handleUserChooseType="handleUserChooseType">
      </ManageHome>
      <RecycleDirectory v-else-if="currActive === '2'"></RecycleDirectory>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import ManageHome from "@/views/manage_sort/manage_home/ManageHome.vue";
import RecycleDirectory from "./RecycleDirectory.vue";
import { getUserSpaceApi } from "@/api/manage_url/DirectoryApi";

const currActive = ref("1"); //1 管理页面 2 分享页面
const ManageHomeRef = ref(null)
const userSpaceData = ref({})
const userChooseType = ref('myFile')
const userChooseTypeEnums = ref({
  video: { type: 0, desc: '视频',value: 'video' },
  image: { type: 1, desc: '图片',value: 'image' },
  document: { type: 2, desc: '文档',value: 'document' },
  audio: { type: 3, desc: '音频',value: 'audio' },
  other: { type: 4, desc: '其他',value: 'other' },
  website: { type: 5, desc: '网址',value: 'website' },
  myFile: { type: 6, desc: '我的文件',value: 'myFile' },
})
const clickConfigDelete = () => {
  currActive.value = "2";
};
const clickManage = () => {
  currActive.value = "1";
};
//根据类型值调取不同的接口：0-4 上传的文件类型 5 网址类型  6 对目录的分类数据管理
const handleUserChooseType = (typeValue) => {
  if (typeValue === userChooseType.value) return;
  const typeEnums = userChooseTypeEnums.value[typeValue]
  if(typeEnums == null) return;
  userChooseType.value = typeValue
  const type = typeEnums.type
  let navigationRecord = [  //有id属性表示调取我的文件对应接口，有type根据type调取对应接口，0-4 调取文件类型接口 5调取网址列表接口
    {
      id: 0, label: '全部文件'//是否刷新父组件数据
    },
  ]
  if (type >= 0 && type <= 5) {
    navigationRecord.push({ type, label: typeEnums.desc })
  }
  ManageHomeRef.value.initNavigationRecord(navigationRecord)
}
//处理右键菜单，调用子组件函数
const handleContextmenu = (e) => {
  if (currActive.value === "1") {
    e.preventDefault();
    ManageHomeRef.value.setPosition(e, 1);
  }
}
onMounted(async function(){
  document.querySelector('body').setAttribute('style', 'background-color: #ffff')
  getUserSpaceApi().then(res => {
    if (res != null && res.code == undefined) {
      userSpaceData.value = res
      userSpaceData.value.percentage = Math.floor(
        userSpaceData.value.userFileUseSpaceValue / userSpaceData.value.userFileTotalSpaceValue * 100)
    }
  })

});
onUnmounted(async function() {
  document.querySelector('body').removeAttribute('style')

})
</script>

<style scoped>
.box {
  display: flex;
  caret-color: rgba(0, 0, 0, 0);
}

.leftChoose,
.leftCurrUseChoose {
  box-shadow: 0 3px 10px #0000000f;
  border-right: 1px solid #f1f2f4;
  height: calc(100vh - 55px);
}

.leftChoose {
  width: 80px;
}

.leftCurrUseChoose {
  width: 200px;
  display: flex;
  flex-direction: column;
}

.showUserSpace {
  padding: 10px;
  margin-top: auto;
}

.manageIcon {
  font-size: 30px;
  font-weight: 400;
}

.leftMenuItem {
  text-align: center;
  padding: 20px 0px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
}

.leftMenuItem:hover {
  background-color: #f3f3f3;
}

.menuItemActive {
  color: #06a7ff;
}

.showContent {
  flex: 1;
}

.userSpace-text {
  color: gray;
  font-size: 14px;
}

.commonTypeData {
  display: flex;
  align-items: center;
  padding: 10px;
  padding-left: 40px;
  cursor: pointer;
}
.commonTypeData:hover {
  background-color: #EDF6FB;
}

.commonTypeDataActive {
  background-color: #E0F2FB;
  border-left: 5px #06A8FF solid;
  color: #06A8FF;
  padding-left: 35px;
}

.leftCurrUseIcon {
  width: 20px;
  height: 20px;
  cursor: pointer;
}

.leftCurrUseText {
  margin-left: 10px;
}
</style>
