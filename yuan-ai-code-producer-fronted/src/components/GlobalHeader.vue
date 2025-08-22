<script setup lang="ts">
import { computed, watch, ref, h } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { LogoutOutlined, HomeOutlined } from '@ant-design/icons-vue'
import { userLogout } from '@/api/userController.ts'
import { type MenuProps, message } from 'ant-design-vue'

// 获取登录用户状态
const loginUserStore = useLoginUserStore()

const props = withDefaults(
  defineProps<{
    title?: string
    menuItems?: MenuProps['items']
    logoSrc?: string
  }>(),
  {
    title: '元仔出品',
    menuItems: () => []
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

// 退出登录
const doLogout = async () => {
  const res = await userLogout()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('注销成功')
    await router.push('/user/login')
  } else {
    message.error('注销失败' + res.data.message)
  }
}

// 菜单配置项
const originItems = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/admin/userManage',
    label: '用户管理',
    title: '用户管理',
  },
  {
    key: 'others',
    label: h('a', { href: 'https://alexavieryuan.us.kg/', target: '_blank' }, '元仔代码站'),
    title: '元仔代码站',
  },
]

// 过滤菜单项
const filterMenus = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    const menuKey = menu?.key as string
    if (menuKey?.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== 'admin') {
        return false
      }
    }
    return true
  })
}

// 展示在菜单的路由数组
const filteredMenuItems = computed<MenuProps['items']>(() => filterMenus(originItems))
</script>

<template>
  <a-layout-header class="global-header">
    <div class="header-left" @click="router.push('/')">
      <img class="logo" :src="resolvedLogo" alt="logo" />
      <span class="title">{{ props.title }}</span>
    </div>
    <div class="header-middle">
      <a-menu
        theme="light"
        mode="horizontal"
        :selectedKeys="selectedKeys"
        :items="filteredMenuItems"
        @click="handleMenuClick"
      />
    </div>
    <div class="header-right">
      <div v-if="loginUserStore.loginUser.id">
        <a-dropdown>
          <a-space>
            <a-avatar :src="loginUserStore.loginUser.userAvatar" />
            <span class="user-name">
              {{ loginUserStore.loginUser.userName ?? '竟然没有名字' }}
            </span>
          </a-space>
          <template #overlay>
            <a-menu>
              <a-menu-item @click="doLogout">
                <LogoutOutlined />
                退出登录
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </div>
      <div v-else>
        <a-button type="primary" href="/user/login">登录</a-button>
      </div>
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
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  height: 64px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.logo {
  width: 96px;
}

.title {
  color: #1f1f1f;
  font-weight: 700;
  font-size: 20px;
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

.user-name {
  color: #1f1f1f;
}

/* 放大并加粗菜单文字 */
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item),
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-submenu) {
  font-size: 16px;
  font-weight: 600;
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
