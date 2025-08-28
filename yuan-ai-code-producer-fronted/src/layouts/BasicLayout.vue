<script setup lang="ts">
import GlobalHeader from '@/components/GlobalHeader.vue'
import GlobalFooter from '@/components/GlobalFooter.vue'

const menuItems = [
  { key: '/', label: '首页' },
  { key: '/about', label: '关于' }
]
</script>

<template>
  <a-layout class="basic-layout">
    <GlobalHeader :menu-items="menuItems" />
    <a-layout-content class="layout-content">
      <router-view />
    </a-layout-content>
    <GlobalFooter />
  </a-layout>
</template>

<style scoped>
.basic-layout {
  min-height: 100vh;
  background: transparent;
}

.layout-content {
  margin: 16px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 8px;
  /* 预留底部空间防止被固定 Footer 遮挡 */
  padding-bottom: 72px;
  backdrop-filter: blur(10px);
}

@media (max-width: 600px) {
  .layout-content {
    margin: 8px;
    padding: 12px;
  }
}
</style>



