<template>
    <div class="directoryNavigation">
        <el-icon class="directoryNavigationIcon"
          :style="eachStepRecord.records.length > 1 && eachStepRecord.records.length - 1 !== eachStepRecord.currStep ? '' : 'color:gray;'"
          @click="previousDirectory">
          <ArrowLeftBold />
        </el-icon>
        <el-icon class="directoryNavigationIcon"
          :style="eachStepRecord.currStep !== 0 && eachStepRecord.records.length > 1 ? '' : 'color:gray;'"
          @click="nextDirectory">
          <ArrowRightBold />
        </el-icon>
    
        <el-popover trigger="click" :show-arrow="false" direction="vertical" popper-style="width:fit-content;
        width:-webkit-fit-content;
        width:-moz-fit-content;padding:0px;background-color: #F2F2F2;box-shadow: 2px 2px 2px 2px #A2A2A2;
        ">
          <template #reference>
            <el-icon class="directoryNavigationIcon">
              <ArrowDownBold />
            </el-icon>
          </template>
          <div class="eachStepRecords">
            <div class="eachStepRecord" v-for="(record, index) in eachStepRecord.records" :key="record.id"
              @click="handleToRecordContentDirectory(record, index)">
              <RightIcon v-if="eachStepRecord.currStep === index" />
              <BackIcon v-else />
              <span class="eachStepRecordText" :style="eachStepRecord.currStep === index ? 'font-weight: bold   ;' : ''">{{
          record.name }}</span>
            </div>
          </div>
        </el-popover>
    
        <el-icon class="directoryNavigationIcon" @click="RefreshRightClick">
          <RefreshRight />
        </el-icon>
        <el-divider direction="vertical" />
        <div v-for="record in navigationRecord" :key="record.id" class="navigationText" @click="clickRecordToData(record)">
          {{ record.label + ">" }}
        </div>
      </div>
</template>

<script setup>
import { ref,defineExpose,nextTick,defineProps,defineEmits,watch,computed } from 'vue'
import BackIcon from "@/components/icons/BackIcon.vue";
import RightIcon from "@/components/icons/RightIcon.vue";
const props = defineProps(['navigationRecord','eachStepRecord'])
const emits = defineEmits(['getContentByPid','toDirectoryData','previousDirectory','nextDirectory','toRecordContentDirectory','selectNoSameDataByType'])
const RefreshRightClick = () =>{
  emits('selectNoSameDataByType',props.navigationRecord[props.navigationRecord.length - 1])
}
const handleToRecordContentDirectory = (record, index) =>{
  emits('toRecordContentDirectory',record, index)
}
const clickRecordToData = (record) =>{
  emits('toDirectoryData',record)
}
//返回上一级目录
const previousDirectory = () => { //有问题：解决
  if (props.eachStepRecord.records.length <= 1 || 
  props.eachStepRecord.records.length - 1 === props.eachStepRecord.currStep) {
    return;
  }
  emits('previousDirectory',)
};
//进入当前目录的第一个目录
const nextDirectory = () => {  //也存在问题
  if (props.eachStepRecord.currStep === 0 || props.eachStepRecord.records.length <= 1) {
    return;
  }
  emits('nextDirectory')
};
</script>

<style scope>
.directoryNavigation {
  display: flex;
  align-items: center;
  width: 100%;
  height: 40px;
  border: 1px solid #f0f0f0;
  border-left: none;
  border-right: none;
}

.navigationText {
  font-size: 14px;
  cursor: pointer;
  margin-left: 8px;
  caret-color: rgba(0, 0, 0, 0);
}

.navigationText:hover {
  color: #06a7ff;
}

.directoryNavigationIcon {
  margin-left: 12px;
  cursor: pointer;
}
.eachStepRecord:hover {
  background-color: #91C9F7;
  cursor: pointer;
}

.eachStepRecordText {
  margin-left: 5px;
}
.eachStepRecords {
  font-size: 14px;
  caret-color: rgba(0, 0, 0, 0);
}
</style>