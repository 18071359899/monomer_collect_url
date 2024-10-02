<!-- 回收站页面 -->
<template>
  <el-tree
    class="treeView"
    :data="treeData"
    @node-click="handleNodeClick"
    node-key="id"
    :load="loadNode"
    :props="props"
    lazy
    ref="treeRef"
    empty-text="No Data"
  >
    <template #default="{ node }">
      <div class="custom-tree-node">
        <!-- 第一级固定一个图标 -->
        <FolderIcon class="folderIcon" />
        <span @click="getNode(node)" class="text">{{ node.label }}</span>
      </div>
    </template>
  </el-tree>
</template>
<!-- :class="[node.childNodes.length ? 'bold' : '', node.isCurrent ? 'orange' : '']" -->
<script setup>
import MyAxios from "@/utils/MyAxios";
import FolderIcon from "@/components/icons/FolderIcon.vue";
import { ref, onMounted, defineEmits } from "vue";
const emit = defineEmits(["handleCutDirectory"]);
const treeData = ref([]);
const props = {
  label: "label",
  isLeaf: "leaf",
  children: "children",
};
const treeRef = ref(null);
// 点击树形结构
const handleNodeClick = (data) => {
  emit("clickOnNode",data);
};

// 这个打印出来的是node
const getNode = (node) => {
  console.log(node);
};
// 获取某一节点
const getData = (pid, callback) => {
  MyAxios.get("/category/node/", { params: { pid } }).then((res) => {
    if (res.code === undefined) {
      callback(res);
    }
  });
};
onMounted(() => {});
// 模拟异步加载节点数据
async function loadNode(node, resolve) {
  if (node.level === 0) {
    // getData(0, (data) => {
    //   let rootData = [
    //     {
    //       id: 0,
    //       label: "全部文件",
    //       leaf: false,
    //     },
    //   ];
    //   rootData.push(data);
    //   resolve(rootData);
    // });

    resolve([
      {
        id: 0,
        label: "全部文件",
        leaf: false,
      },
    ]);
  } else {
    // 子节点，延迟加载
    getData(node.data.id, (data) => {
      resolve(data);
    });
  }
}
</script>

<style scoped>
:deep(.el-tree-node__content) {
  height: 30px;
}
.folderIcon {
  width: 30px;
  height: 30px;
}
.treeView {
  height: 400px;
  overflow-x: hidden;
  overflow-y: auto;
  border: 1px solid #ebeef5;
}
.text {
  margin-left: 4px;
  font-size: 13px;
}
</style>
