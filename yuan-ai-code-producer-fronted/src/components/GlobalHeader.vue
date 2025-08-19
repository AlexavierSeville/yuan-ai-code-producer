<script setup lang="ts">
import { computed, watch, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

interface MenuItemConfig {
  key: string
  label: string
}

const props = withDefaults(
  defineProps<{
    title?: string
    menuItems?: MenuItemConfig[]
    logoSrc?: string
  }>(),
  {
    title: '元仔出品',
    menuItems: () => [
      { key: '/', label: '首页' },
      { key: '/about', label: '关于' },
    ],
  },
)

const router = useRouter()
const route = useRoute()

const selectedKeys = ref<string[]>([route.path])
// 监听路由编号，更新当前选中菜单
// router.afterEach((to) => {
//   selectedKeys.value = [to.path]
// })
watch(
  () => route.path,
  (newPath) => {
    selectedKeys.value = [newPath]
  },
)

const resolvedLogo = computed(
  () => props.logoSrc || new URL('@/assets/yuan-pro-notext.png', import.meta.url).toString(),
)

function handleMenuClick(info: { key: string }) {
  if (info?.key) {
    router.push({ path: info.key })
  }
}
</script>

<template>
  <a-layout-header class="global-header">
    <div class="header-left" @click="router.push('/')">
      <img class="logo" :src="resolvedLogo" alt="logo" />
      <span class="title">{{ props.title }}</span>
    </div>
    <div class="header-middle">
      <a-menu
        theme="dark"
        mode="horizontal"
        :selectedKeys="selectedKeys"
        :items="props.menuItems.map((item) => ({ key: item.key, label: item.label }))"
        @click="handleMenuClick"
      />
    </div>
    <div class="header-right">
      <a-button type="primary">登录</a-button>
    </div>
  </a-layout-header>
</template>

<style scoped>
.global-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 0 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.logo {
  width: 77px;
}

.title {
  color: #fff;
  font-weight: 600;
  font-size: 16px;
  white-space: nowrap;
}

.header-middle {
  flex: 1;
  min-width: 0;
}

.header-right {
  display: flex;
  align-items: center;
}

/* 响应式：窄屏隐藏标题，菜单允许横向滚动 */
@media (max-width: 600px) {
  .title {
    display: none;
  }
  :deep(.ant-menu) {
    overflow-x: auto;
  }
}
</style>
