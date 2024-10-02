<template>
  <div v-for="(comment, index) in commentList" :key="comment.id">
    <!-- 一级评论 -->
    <div class="firstComment">
      <ShowComment @submitComment="(id, newComment) => $emit('submitComment', id, newComment)"
        @submitDeleteComment="(rootId, id) => $emit('submitDeleteComment', rootId, id, index)" :comment="comment" 
        :findCommentId="findCommentId"/>
    </div>

    <!-- 多级评论 -->
    <div v-if="comment.children !== null">
      <div class="subComment" v-for="childrenComment in comment.children" :key="childrenComment.id">
        <ShowComment @submitComment="(id, newComment) => $emit('submitComment', id, newComment)"
          @submitDeleteComment="(comment) => $emit('submitDeleteComment',comment, index)"
          :comment="childrenComment" :findCommentId="findCommentId"/>
      </div>
    </div>

    <div style="margin-top: 10px;">
      <el-divider  border-style="double" />
    </div>

  </div>
</template>

<script setup>
import { defineProps } from "vue";
import ShowComment from "@/views/share/comment/ShowComment.vue";
const props = defineProps(['commentList','findCommentId'])

</script>

<style scoped>
.firstComment {
  margin-top: 20px;
}

.subComment {
  padding-left: 60px;
  margin-top: 15px;
}
:deep .el-divider--horizontal{
  border-top: 1px solid rgba(0,0,0,.06);
}
* {
  padding: 0;
  margin: 0;
}
</style>