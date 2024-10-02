<template>
  <ManageHomeTopContent v-model:searchInput="searchInput" :documentList="documentList" :userChooseType="userChooseType"
    :navigationRecord="navigationRecord" :getSelectedDocumentId="getSelectedDocumentId().length"
    @getCurrPid="transferPidToChild" @handleAddWebsite="handleAddWebsite" @handleAddCategory="handleAddCategory"
    @deleteDocumentByIds="deleteDocumentByIds" @HandleMoveToDirectory="HandleMoveToDirectory" />
  <ManageHomePathContent :navigationRecord="navigationRecord" :eachStepRecord="eachStepRecord"
    @selectNoSameDataByType="selectNoSameDataByType" @getContentByPid="getContentByPid"
    @toDirectoryData="toDirectoryData" @previousDirectory="previousDirectory" @nextDirectory="nextDirectory"
    @toRecordContentDirectory="toRecordContentDirectory" />
  <ManageHomeContent :documentList="documentList" @handleElementRightClick="handleElementRightClick"
    @handleToDirectory="handleToDirectory" @toRecordContentDirectory="toRecordContentDirectory"
    @setSelectedCount="setSelectedCount" @handleOpenCheckFileDetailsDialog="handleOpenCheckFileDetailsDialog" />
  <ManageWebSite v-model:dialogState="dialogState" ref="dialogRef" :title="dialogTitle"
    @updateWebsiteSuccess="getContentByPid(navigationRecord[navigationRecord.length - 1].id)" />
  <MouseClickMenu v-show="isShowContextMenu" :style="fixedBoxStyleObject" :selectState="selectState"
    @handleDownloadFile="handleDownloadFile(rightClickDocument)" @deleteDocumentByIds="deleteDocumentByIds"
    @copySaveDirectory="copySaveDirectory" @cutSaveDirectory="cutSaveDirectory"
    @handleToDirectory="handleToDirectory(rightClickDocument)" @handleAddWebsite="handleAddWebsite"
    @handleAddCategory="handleAddCategory" @handlePasteToDirectory="handlePasteToDirectory"
    @getContentByPid="getContentByPid(navigationRecord[navigationRecord.length - 1].id)"
    @handleUpdateCategoryAndWebSite="handleUpdateCategoryAndWebSite" @HandleMoveToDirectory="HandleMoveToDirectory"
    @handleDetailsInfo="handleDetailsInfo" />
  <MoveToDirectoryDialog title="选择移动到的目录" v-model:dialogState="MoveToDirectoryDialogState"
    @handleCutDirectory="handleCutDirectory" />
  <OpenDetailsDialog v-model:dialogState="openDetailsDialogState" ref="openDetailsDialogRef" />
  <CheckFileDetailsDialog ref="checkFileDetailsDialogRef" />
</template>

<script setup>
import { ref, onMounted, nextTick, reactive, defineExpose, watch, defineEmits, defineProps } from "vue";
import ManageWebSite from "./ManageWebSite.vue";
import OpenDetailsDialog from "./OpenDetailsDialog.vue";
import { ElMessage } from "element-plus";
import {
  copyDirectory,
  cutDirectory,
  deleteDirectory,
  getDirectory, searchDirectory, getPositionDirectory
} from "@/api/manage_url/DirectoryApi";
import MouseClickMenu from "@/components/MouseClickMenu.vue";
import MoveToDirectoryDialog from "./MoveToDirectoryDialog.vue";
import CheckFileDetailsDialog from "../preview/CheckFileDetailsDialog.vue";
import ManageHomeTopContent from './ManageHomeTopContent.vue'
import ManageHomePathContent from './ManageHomePathContent'
import ManageHomeContent from './ManageHomeContent.vue'
import debounce from 'lodash/debounce'
import { getFileTypeDataRequest } from "@/api/upload_file/upload_file/UploadFileApi";
import { listWebsiteRequest } from "@/api/manage_url/DirectoryApi";
import { ElLoading } from 'element-plus'
import { downloadFileRequest,getDownloadCode } from "@/api/upload_file/upload_file/UploadFileApi";

const emits = defineEmits(['handleUserChooseType'])
const props = defineProps(['userChooseType'])

const saveClickMoVEBeforeData = ref([]); //存储点击移动前的数据
const HandleMoveToDirectory = () => {
  saveClickMoVEBeforeData.value = getSelectedDocumentId();
  MoveToDirectoryDialogState.value = true;
};
const dialogState = ref(false);
const dialogRef = ref(null);
const dialogTitle = ref("新增网址");
//shirt键和ctrl键状态
const ctrlKeyState = ref(false); //true按下。false收起
const shiftKeyState = ref(false);
const MoveToDirectoryDialogState = ref(false);
//存储在shift键下第一个选择的元素
const firstSelectedDocument = ref(null);
//右键菜单是否显示
const isShowContextMenu = ref(false);
const selectState = ref(1);
const fixedBoxStyleObject = reactive({ left: "", top: "" });
let rightClickDocument = null;
const openDetailsDialogState = ref(false)
const openDetailsDialogRef = ref(null);
const documentList = ref([]);
const checkFileDetailsDialogRef = ref(null)
//存储用户进入目录或搜索的所有操作
const navigationRecord = ref([  //几种情况：id有值，查询我的文件下的目录   type有值，查询上传文件类型或上传网址列表 id和type都为空，表示搜索操作
]);
//记录用户的每一步，用户回退和前进
const eachStepRecord = ref()
const initEachStepRecord = () => {
  eachStepRecord.value = {
    records: [
      { name: '全部文件', id: 0 }
    ],
    currStep: 0,  //当前在哪一步，以records中的id为标准
  }
}
//下载文件逻辑
const handleDownloadFile = async (currDocument) => {
  if (currDocument.type !== 2) {
    ElMessage({ type: 'warning', message: '非上传文件' })
    return;
  }
  const code  = await getDownloadCode()
  let path = currDocument.filePath
    if (currDocument.fileType === 0) {  //视频地址
    path = currDocument.videoFilePath
  }
  window.location.href = process.env.VUE_APP_BASE_URL + "/file/download/" + code + "/" + path
}
initEachStepRecord()
const searchInput = ref("");
const handleOpenCheckFileDetailsDialog = (document) => {
  checkFileDetailsDialogRef.value.showDialog();
  checkFileDetailsDialogRef.value.setFileType(document.fileType);
  checkFileDetailsDialogRef.value.setFilePath(document.filePath);
  checkFileDetailsDialogRef.value.setFileName(document.name)
}
const previousDirectory = () => {
  getContentByPid(eachStepRecord.value.records[++(eachStepRecord.value.currStep)].id)
};
const nextDirectory = () => {
  getContentByPid(eachStepRecord.value.records[--(eachStepRecord.value.currStep)].id)
};
const transferPidToChild = (callback) => {
  callback(getCurrPid())
}
const getCurrPid = () => {
  const currNavigationRecord = navigationRecord.value[navigationRecord.value.length - 1]
  if (currNavigationRecord.id == null) {  //当前不在某个目录下，那就存在根目录
    return '0';
  }
  return currNavigationRecord.id
}
const handleAddWebsite = () => {
  dialogState.value = true;
  dialogTitle.value = "新增网址";
  dialogRef.value.setDataFormPid(
    getCurrPid()
  );
};
const handleAddCategory = async () => {
  let document = {
    name: "新文件夹",
    type: 0,
    pid: getCurrPid()
  };
  document.documentRef = ref(null);
  document.isEdit = true;
  documentList.value.unshift(document);
  nextTick(() => {
    document.documentRef.value.focus();
  });
};
//搜索请求
const searchData = () => {
  // if(searchInput.value === '') return;
  let searchName = eachStepRecord.value.records[eachStepRecord.value.currStep].search
  let newSearchContent = '搜索 "' + searchInput.value + '"'
  if (searchName === undefined) {  //当前是用户主动点击搜索，而否退到上几步的搜索
    eachStepRecord.value.records.unshift({ name: newSearchContent, search: searchInput.value })
    eachStepRecord.value.currStep = 0
  } else {   //退到上几步的搜索或当前还在搜索中
    eachStepRecord.value.records[eachStepRecord.value.currStep].search = searchInput.value
    eachStepRecord.value.records[eachStepRecord.value.currStep].name = newSearchContent
  }
  navigationRecord.value = [
    { id: 0, label: '全部文件' },
    { label: '"' + searchInput.value + '"的搜索结果' }
  ]
  searchDirectory(searchInput.value).then(res => {
    if (res.code === undefined) {
      documentList.value = res
      handleGetContentByPid()
    }
  })
}
const debounceSearchData = debounce(searchData, 1000)
watch(searchInput, (newVal) => {
  debounceSearchData()
}
)
//点击记录刷新目录和内容
const toRecordContentDirectory = (record, index) => {
  if (record == undefined || record == null) return
  let searchName = record.search
  eachStepRecord.value.currStep = index;
  if (searchName === undefined) {
    searchInput.value = ""   //清空搜索内容记录
    if (record.id === 0) {
      emits('handleUserChooseType', 'myFile')  //同步更新按钮选中状态
    }
    getContentByPid(record.id);
  } else {
    searchInput.value = searchName
  }
}
//右键元素后的回调函数
const handleElementRightClick = (e, document) => {
  e.preventDefault();
  rightClickDocument = document;
  let len = getSelectedDocumentId().length;
  if (document.mouseState === "active") {
    if (len > 1) setPosition(e, 3);
    else setPosition(e, 2);
  } else {
    initDocumentListMouseState(0, documentList.value.length);
    document.mouseState = "active";
    setPosition(e, 2);
  }
};
//打开详细信息对话框
const handleDetailsInfo = () => {
  openDetailsDialogState.value = true
  openDetailsDialogRef.value.getDataForm(rightClickDocument.type, rightClickDocument.id)
}
//设置右键菜单的位置,并显示
const setPosition = (event, newSelectState) => {
  selectState.value = newSelectState;
  isShowContextMenu.value = true;
  fixedBoxStyleObject.left = event.clientX + "px";
  fixedBoxStyleObject.top = event.clientY + "px";
};
//更新除了参数之外的所有元素，鼠标状态为none
const updateDocumentListMouseState = (document) => {
  documentList.value.forEach((value) => {
    if (value !== document) value.mouseState = "none";
  });
};
//获取选中信息的id
const getSelectedDocumentId = () => {
  let selectedIds = [];
  documentList.value.forEach((value) => {
    if (value.mouseState === "active") selectedIds.push(value);
  });
  return selectedIds;
};
const deleteDocumentByIds = () => {
  let selectedIds = getSelectedDocumentId();
  if (selectedIds.length === 0) return;
  deleteDirectory(selectedIds).then((res) => {
    if (res === "ok") {
      ElMessage({ type: "success", message: "删除成功" });
      selectNoSameDataByType(navigationRecord.value[navigationRecord.value.length - 1]);
    }
  });
};
const initDocumentListMouseState = (left, right) => {
  for (let i = left; i < right; i++) documentList.value[i].mouseState = "none";
};
//根据shift键选择元素，连续的
const setShiftDocumentMouseState = (document) => {
  if (firstSelectedDocument.value === null) firstSelectedDocument.value = document;
  let currIndex,
    firstIndex = 0;
  documentList.value.forEach((value, index) => {
    if (value === document) currIndex = index;
    if (value === firstSelectedDocument.value) firstIndex = index;
  });
  for (
    let i = Math.min(firstIndex, currIndex);
    i <= Math.max(firstIndex, currIndex);
    i++
  ) {
    documentList.value[i].mouseState = "active";
  }
  //区分左右区域，更新另一个区域里的元素鼠标状态
  if (firstIndex < currIndex) {
    initDocumentListMouseState(0, firstIndex);
  } else if (firstIndex > currIndex) {
    initDocumentListMouseState(firstIndex + 1, documentList.value.length);
  } else {
    initDocumentListMouseState(0, documentList.value.length);
    documentList.value[firstIndex].mouseState = "active";
  }
};
//设置选中状态
const setSelectedCount = (document) => {
  if (ctrlKeyState.value) {
    document.mouseState = "active";
  } else if (shiftKeyState.value) {
    setShiftDocumentMouseState(document);
  } else {
    updateDocumentListMouseState(document);
    document.mouseState = "active";
    firstSelectedDocument.value = document;
  }
};

const handleUpdateCategoryAndWebSite = async () => {
  if (rightClickDocument.isEdit === undefined) {
    dialogTitle.value = "修改网址";
    dialogState.value = true;
    dialogRef.value.getDataForm(rightClickDocument.id);
    return;
  }
  rightClickDocument.isEdit = true;
  nextTick(() => {
    rightClickDocument.documentRef.focus();
  });
};
const getIsSelfByDocumentList = (documentListBox, e) => {
  for (let i = 0; i < documentListBox.length; i++) {
    if (documentListBox[i].contains(e.target)) {
      return true;
    }
  }
  return false;
};
onMounted(() => {
  getContentByPid(0);
  document.addEventListener("keyup", handleKeyUp);
  document.addEventListener("keydown", handleKeyDown);
  document.addEventListener("click", (e) => {
    let documentListBox = document.querySelectorAll(".documentList");
    if (documentListBox) {
      //点击其他地方时，取消选中状态
      if (getIsSelfByDocumentList(documentListBox, e) === false) {
        firstSelectedDocument.value = null;
        initDocumentListMouseState(0, documentList.value.length);
        isShowContextMenu.value = false;
      }
    }
  });
});
const handleKeyUp = (event) => {
  switch (event.key) {
    case "Control":
      ctrlKeyState.value = false;
      break;
    case "Shift":
      shiftKeyState.value = false;
      break;
  }
};
let currState = ""; //当前是复制还是剪贴
let selectedDataToUpdate = ref([]);
const clearPasteData = () => {
  currState = "";
  selectedDataToUpdate.value.map((value) => (value.isCut = false));
  selectedDataToUpdate.value = [];
};
const handleCutDirectory = (cotByTreeOrView, pid) => {
  let directoryVos = [];
  if (cotByTreeOrView === "tree") {
    //判断当前是从树形结构还是视图结构进行剪切操作
    directoryVos = saveClickMoVEBeforeData.value;
  } else if (cotByTreeOrView === "view") {
    directoryVos = selectedDataToUpdate.value;
  }
  //剪切到目录
  cutDirectory({ directoryVos: directoryVos, pid: pid }).then((res) => {
    if (res === "ok") {
      ElMessage({ message: "文件移动成功", type: "success" });
      selectNoSameDataByType(navigationRecord.value[navigationRecord.value.length - 1]);
    }
    clearPasteData();
    MoveToDirectoryDialogState.value = false;
  });
};
const handleCopyDirectory = (pid) => {
  copyDirectory({
    directoryVos: selectedDataToUpdate.value,
    pid: pid,
  }).then((res) => {
    if (res === "ok") {
      ElMessage({ message: "文件复制成功", type: "success" });
      clearPasteData();
      if (pid === navigationRecord.value[navigationRecord.value.length - 1].id) {
        getContentByPid(pid);
      }
    }
  });
};
const handlePasteToDirectory = () => {
  //粘贴到目录
  if (selectedDataToUpdate.value.length === 0) return;
  let pid = navigationRecord.value[navigationRecord.value.length - 1].id;
  switch (currState) {
    case "c":
      handleCopyDirectory(pid);
      break;
    case "x":
      handleCutDirectory("view", pid);
      break;
  }
};
const copySaveDirectory = () => {
  currState = "c";
  selectedDataToUpdate.value = getSelectedDocumentId();
  selectedDataToUpdate.value.map((value) => (value.isCut = false));
};
const cutSaveDirectory = () => {
  currState = "x";
  selectedDataToUpdate.value = getSelectedDocumentId();
  selectedDataToUpdate.value.map((value) => (value.isCut = true));
};
const handleKeyDown = (event) => {
  if (event.ctrlKey && event.key === "c") {
    copySaveDirectory();
  }
  if (event.ctrlKey && event.key === "v") {
    handlePasteToDirectory();
  }
  if (event.ctrlKey && event.key === "x") {
    cutSaveDirectory();
  }
  switch (event.key) {
    case "Control":
      ctrlKeyState.value = true;
      break;
    case "Shift":
      shiftKeyState.value = true;
      break;
    case "Delete":
      deleteDocumentByIds();
      break;
  }
};
//获取某个目录下的数据成功逻辑
const handleGetContentByPid = () => {
  documentList.value.forEach((document) => {
    //文件夹重命名需要的属性
    if (document.type === 0 || document.type === 2) {
      document.isEdit = false;
      document.documentRef = ref(null);
    }
    document.isCut = false; //是否被剪切
    document.mouseState = "none"; //鼠标对应的状态
  });
}
//根据pid，获取当前目录下的所有内容（文件夹和网址）
const getContentByPid = (pid) => {
  const loading = ElLoading.service({ fullscreen: true })
  getDirectory({ params: { pid } }).then((res) => {
    if (res.code === undefined) {
      documentList.value = res;
      handleGetContentByPid()
      getPositionDirectory({ params: { pid } }).then(res => {
        if (res.code === undefined) {
          navigationRecord.value = res
        }
      })
    }
    loading.close()
  }).catch(error => { loading.close() });
};
const handleToDirectory = (document) => {
  //当用户在输入时，不进行跳转
  if (document.isEdit === true) {
    return;
  }
  //进入目录时移除掉用户之前进行的操作
  for (let i = eachStepRecord.value.currStep - 1; i >= 0; i--) {
    eachStepRecord.value.records.splice(i, 1)
  }
  eachStepRecord.value.records.unshift({ name: document.name, id: document.id })
  eachStepRecord.value.currStep = 0
  getContentByPid(document.id);
};
const handleGetFileTypeDataRequest = (type) => {
  const loading = ElLoading.service({ fullscreen: true })
  getFileTypeDataRequest(type).then(res => {
    if (res.code == undefined) {
      documentList.value = res
      handleGetContentByPid()
    }
    loading.close()
  }).catch(error => { loading.close() })
}
const handleListWebsiteRequest = () => {
  const loading = ElLoading.service({ fullscreen: true })
  listWebsiteRequest().then(res => {
    if (res.code == undefined) {
      documentList.value = res
      handleGetContentByPid()
    }
    loading.close()
  }).catch(error => { loading.close() })
}
const selectNoSameDataByType = (currNavigationRecord) => {  //根据类型调取不同的接口数据
  const type = currNavigationRecord.type
  if (currNavigationRecord.id != null) {  //我的文件
    getContentByPid(currNavigationRecord.id);
    if (currNavigationRecord.id === 0)
      emits('handleUserChooseType', 'myFile')  //同步更新按钮选中状态
  } else if (type != null && type >= 0 && type <= 4) { //上传的文件分类
    handleGetFileTypeDataRequest(type)
  } else if (type != null && type === 5) {  //网址
    handleListWebsiteRequest()
  } else {
    searchData()
  }
}
const toDirectoryData = (record) => {  //加上调取不同的接口逻辑
  eachStepRecord.value.records.forEach((value, index) => {
    if (value.id === record.id) {
      eachStepRecord.value.currStep = index
    }
  })
  selectNoSameDataByType(record)
};
const initNavigationRecord = (newNavigationRecord) => {
  initEachStepRecord()
  navigationRecord.value = newNavigationRecord
  selectNoSameDataByType(navigationRecord.value[navigationRecord.value.length - 1])
}
defineExpose({
  setPosition, initNavigationRecord
})
</script>

<style scoped></style>
