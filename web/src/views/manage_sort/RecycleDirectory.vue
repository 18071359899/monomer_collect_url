<!-- 回收站页面 -->
<template>
  <div class="topMenu">
    <el-button
      type="success"
      @click="reductionData"
      :disabled="multipleSelection.length <= 0"
    >
      还原<el-icon class="el-icon--right"><Refresh /></el-icon>
    </el-button>
    <el-button
      type="danger"
      @click="removeCompletely"
      :disabled="multipleSelection.length <= 0"
    >
      批量删除<el-icon class="el-icon--right"><Delete /></el-icon>
    </el-button>
  </div>
  <div class="content">
    <el-table
      :data="tableData"
      class="myTable"
      @selection-change="handleSelectionChange"
      :row-class-name="rowClassName"
    >
      <el-table-column type="selection" />
      <el-table-column prop="date" label="名称">
        <template #default="scope">
          <div v-if="scope.row.type === 0" class="nameBox">
            <Folder class="folderIcon" />
            <div class="text">{{ scope.row.name }}</div>
          </div>
          <div v-else-if="scope.row.type === 1" class="nameBox">
            <div class="imgBox">
              <img :src="scope.row.icon" alt="" />
            </div>
            <div class="text">{{ scope.row.title }}</div>
          </div>
          <div v-else-if="scope.row.type === 2" class="nameBox">
            <div class="imgBox">
              <img :src=" VUE_APP_BASE_URL + '/images/get/' + scope.row.icon" alt="" />
            </div>
            <div class="text">{{ scope.row.title }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="deleteTime" label="删除时间" :formatter="dateFormatter" />
    </el-table>
    <PageComponent @getData="getData" ref="pageCommponent" style="margin-top: 20px"  v-show="tableData.length > 0"/>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import MyAxios from "@/utils/MyAxios";
import PageComponent from "@/components/PageComponent.vue";
import Folder from "@/components/icons/FolderIcon.vue";
const tableData = ref([]);
const pageCommponent = ref(null);
import { ElMessage } from "element-plus";
import { dayjs } from "element-plus";
const VUE_APP_BASE_URL = process.env.VUE_APP_BASE_URL  //获取请求地址

// 存储表格的选中信息
const multipleSelection = ref([]);

const handleSelectionChange = (val) => {
  multipleSelection.value = val;
};
const rowClassName = ({ row, rowIndex }) => {
  //把每一行的索引放进row
  row.index = rowIndex;
};
const dateFormatter = (row, column) => {
  return dayjs(row.deleteTime).format("YYYY/MM/DD HH:mm");
};
const reductionData = () => {
  MyAxios.post("/recycle/reduction/", multipleSelection.value).then((res) => {
    if (res === "ok") {
      ElMessage({ type: "success", message: "还原成功" });
      getData();
    } else {
      ElMessage({ type: "warning", message: "还原失败" });
    }
  });
};
const removeCompletely = () => {
  MyAxios.post("/recycle/remove/", multipleSelection.value).then((res) => {
    if (res === "ok") {
      ElMessage({ type: "success", message: "删除成功" });
      getData();
    } else {
      ElMessage({ type: "warning", message: "删除失败" });
    }
  });
};
onMounted(() => {
  getData();
});
const getData = () => {
  // 调用接口获取数据
  MyAxios.get("/recycle/list/", {
    params: {
      page: pageCommponent.value.currentPage,
      size: pageCommponent.value.pageSize,
    },
  })
    .then(function (response) {
      if (response.code === undefined) {
        tableData.value = response.records;
        pageCommponent.value.setAllPageAttributes(response);
      }
    })
    .catch(function () {});
};
</script>

<style scoped>
.content {
  padding: 20px;
  padding-top: 10px;
}
.folderIcon,
.imgBox {
  width: 40px;
  height: 40px;
  margin-right: 8px;
}
.nameBox {
  display: flex;
  align-items: center;
}
.text {
  font-size: 14px;
}
.imgBox {
  text-align: center;
}
.imgBox img {
  width: 30px;
  height: 36px;
}
</style>
