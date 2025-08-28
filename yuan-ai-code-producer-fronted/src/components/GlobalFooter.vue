<script setup lang="ts">
</script>

<template>
  <a-layout-footer class="global-footer">
    <span>
      元仔项目 by
      <a href="https://alexavieryuan.us.kg/" target="_blank" rel="noopener noreferrer">程序猿 元仔</a>
    </span>
  </a-layout-footer>

</template>

<style scoped>
.global-footer {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  text-align: center;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(102, 126, 234, 0.1);
  padding: 20px; /* 更舒展的内边距 */
  z-index: 10;
}

.global-footer a {
  color: #1677ff;
}
</style>



