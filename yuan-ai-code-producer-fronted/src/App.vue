<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'
import { useLoginUserStore } from '@/stores/loginUser.ts'

const loginUserStore = useLoginUserStore();
loginUserStore.fetchLoginUser();

</script>

<template>
  <BasicLayout />

</template>

<style>
/* 全局样式优化 */
* {
  box-sizing: border-box;
}

body {
  margin: 0;
  padding: 0;
  overflow-x: hidden;
  background-image: linear-gradient(60deg, #3d3393 0%, #2b76b9 37%, #2cacd1 65%, #35eb93 100%);
}

html {
  overflow-x: hidden;
  scroll-behavior: smooth;
}

/* 选择文本样式 */
::selection {
  background-color: rgba(24, 144, 255, 0.2);
  color: #1a202c;
}

::-moz-selection {
  background-color: rgba(24, 144, 255, 0.2);
  color: #1a202c;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: rgba(24, 144, 255, 0.3);
  border-radius: 4px;
  transition: background 0.2s ease;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(24, 144, 255, 0.5);
}

/* 响应式字体大小 */
@media (max-width: 768px) {
  html {
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  html {
    font-size: 13px;
  }
}
</style>
