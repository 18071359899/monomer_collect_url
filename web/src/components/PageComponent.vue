<!-- 封装分页组件 -->
<template>
    <el-pagination
    v-model:current-page="currentPage"
    v-model:page-size="pageSize"
    :page-sizes="[10, 20, 30, 40]"
    :small="false"
    :disabled="false"
    :background="false"
    layout="->,total, sizes, prev, pager, next, jumper"
    :total="total"
    @size-change="handleSizeChange"
    @current-change="handleCurrentChange"
   />
</template>

<script setup>
import { defineEmits,ref,defineExpose } from "vue"

const emit = defineEmits(['getData'])

const pageSize = ref(10)
const currentPage = ref(1)
const total = ref(0)

const handleSizeChange = () => {
    emit("getData");    
}

// 只在点击上一页 下一页 跳转页数时触发
const handleCurrentChange = () => {
    emit("getData");    
}

const setAllPageAttributes = (response) =>{
    currentPage.value = response.current;
    pageSize.value = response.size;
    total.value = response.total;
}
defineExpose({
    pageSize,
    currentPage,
    total,
    setAllPageAttributes
})

</script>