<template>
  <a-layout-header class="global-header">
    <div class="header-left" @click="router.push('/')">
      <div class="logo-container">
        <img class="logo" :src="resolvedLogo" alt="logo" />
        <div class="logo-glow"></div>
      </div>
      <span class="title">{{ props.title }}</span>
    </div>
    <div class="header-middle">
      <a-menu
        theme="light"
        mode="horizontal"
        :selectedKeys="selectedKeys"
        :items="filteredMenuItems"
        @click="handleMenuClick"
        class="animated-menu"
      />
    </div>
    <div class="header-right">
      <div v-if="loginUserStore.loginUser.id" class="user-section">
        <a-dropdown>
          <a-space class="user-info" @click="goToProfile">
            <a-avatar :src="loginUserStore.loginUser.userAvatar" class="user-avatar" />
            <span class="user-name">
              {{ loginUserStore.loginUser.userName ?? '竟然没有名字' }}
            </span>
          </a-space>
          <template #overlay>
            <a-menu>
              <a-menu-item @click="goToProfile">
                <UserOutlined />
                个人信息
              </a-menu-item>
              <a-menu-item @click="doLogout">
                <LogoutOutlined />
                退出登录
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </div>
      <div v-else class="login-section">
        <a-button type="primary" href="/user/login" class="login-btn">登录</a-button>
      </div>
    </div>
  </a-layout-header>
</template>

<script setup lang="ts">
import { computed, watch, ref, h } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { LogoutOutlined, UserOutlined } from '@ant-design/icons-vue'
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
    // Find the menu item to check if it has an onClick handler
    const menuItem = originItems.find(item => item.key === info.key)
    if (menuItem && menuItem.onClick) {
      // If the menu item has an onClick handler, don't route
      return
    }
    router.push({ path: info.key })
  }
}

// 跳转到个人信息页面
const goToProfile = () => {
  router.push('/user/profile')
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
    icon: () => h('span', { class: 'menu-icon emoji-icon' }, '🏠'),
    label: '主页',
    title: '主页',
  },
  {
    key: '/admin/userManage',
    icon: () => h('span', { class: 'menu-icon emoji-icon' }, '👥'),
    label: '用户管理',
    title: '用户管理',
  },
  {
    key: '/admin/appManage',
    icon: () => h('span', { class: 'menu-icon emoji-icon' }, '📱'),
    label: '应用管理',
    title: '应用管理',
  },
  {
    key: 'others',
    icon: () => h('span', { class: 'menu-icon emoji-icon' }, '💻'),
    label: '元仔代码站',
    title: '元仔代码站',
    onClick: () => {
      window.open('https://alexavieryuan.us.kg/', '_blank')
    }
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

<style scoped>
.global-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 0 16px;
  background: linear-gradient(135deg,
    rgba(30, 60, 114, 0.95) 0%,
    rgba(75, 0, 130, 0.95) 25%,
    rgba(138, 43, 226, 0.95) 50%,
    rgba(30, 60, 114, 0.95) 75%,
    rgba(75, 0, 130, 0.95) 100%
  );
  background-size: 400% 400%;
  animation: gradientShift 8s ease-in-out infinite;
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(138, 43, 226, 0.3);
  height: 64px;
  position: relative;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 20px rgba(138, 43, 226, 0.2);
}

.global-header:hover {
  background: linear-gradient(135deg,
    rgba(30, 60, 114, 0.98) 0%,
    rgba(75, 0, 130, 0.98) 25%,
    rgba(138, 43, 226, 0.98) 50%,
    rgba(30, 60, 114, 0.98) 75%,
    rgba(75, 0, 130, 0.98) 100%
  );
  background-size: 400% 400%;
  animation: gradientShift 6s ease-in-out infinite;
  box-shadow: 0 8px 30px rgba(138, 43, 226, 0.3);
  transform: translateY(-1px);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.header-left:hover {
  transform: scale(1.02);
}

.logo-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo {
  width: 110px;
  height: auto;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  filter: drop-shadow(0 2px 8px rgba(24, 144, 255, 0.2));
}

.logo-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle, rgba(24, 144, 255, 0.1) 0%, transparent 70%);
  border-radius: 50%;
  transform: translate(-50%, -50%) scale(0);
  transition: all 0.3s ease;
  pointer-events: none;
}

.header-left:hover .logo-glow {
  transform: translate(-50%, -50%) scale(1.2);
  opacity: 1;
}

.header-left:hover .logo {
  transform: scale(1.05) rotate(2deg);
  filter: drop-shadow(0 4px 16px rgba(24, 144, 255, 0.4));
}

.title {
  color: #ffffff;
  font-weight: 700;
  font-size: 24px;
  white-space: nowrap;
  margin: 0;
  background: linear-gradient(135deg, #ffffff 0%, #e6e6fa 50%, #dda0dd 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  transition: all 0.3s ease;
  position: relative;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.3);
}

.title::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(135deg, #ffffff 0%, #e6e6fa 50%, #dda0dd 100%);
  transition: width 0.3s ease;
  box-shadow: 0 0 5px rgba(255, 255, 255, 0.5);
}

.header-left:hover .title::after {
  width: 100%;
}

.header-middle {
  flex: 1;
  min-width: 0;
  display: flex;
  justify-content: center; /* 菜单居中 */
  align-items: center;
}

/* 菜单容器样式 */
:deep(.ant-menu) {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  max-width: 800px; /* 增加最大宽度，容纳更多菜单项 */
}

/* 菜单动画效果 */
:deep(.ant-menu-horizontal) {
  border-bottom: none !important;
  background: transparent;
}

:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item),
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-submenu) {
  font-size: 20px; /* 从18px增加到20px */
  font-weight: 600;
  color: #ffffff !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  padding: 0 16px; /* 减少左右内边距，节省空间 */
  height: 64px; /* 确保高度与header一致 */
  line-height: 64px; /* 垂直居中 */
  min-width: 100px; /* 减少最小宽度 */
  text-align: center; /* 文字居中 */
}

:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item::before),
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-submenu::before) {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 3px;
  background: linear-gradient(135deg, #ffffff 0%, #e6e6fa 50%, #dda0dd 100%);
  transition: all 0.3s ease;
  transform: translateX(-50%);
  box-shadow: 0 0 5px rgba(255, 255, 255, 0.5);
}

:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item:hover::before),
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-submenu:hover::before) {
  width: 100%;
}

:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item:hover),
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-submenu:hover) {
  color: #ffffff !important;
  transform: translateY(-2px);
  text-shadow: 0 2px 8px rgba(255, 255, 255, 0.5);
}

:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item-selected) {
  color: #ffffff !important;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 6px;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
}

:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item-selected::before) {
  width: 100%;
}

/* 子菜单样式优化 */
:deep(.ant-menu-submenu-title) {
  font-size: 20px !important; /* 确保子菜单标题也是20px */
  padding: 0 16px !important;
  height: 64px !important;
  line-height: 64px !important;
  min-width: 100px !important;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .global-header {
    padding: 0 16px;
  }

  :deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item),
  :deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-submenu) {
    padding: 0 14px;
    min-width: 90px;
  }

  :deep(.ant-menu-submenu-title) {
    padding: 0 14px !important;
    min-width: 90px !important;
  }
}

@media (max-width: 768px) {
  .title {
    display: none;
  }

  .global-header {
    padding: 0 12px;
  }

  :deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item),
  :deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-submenu) {
    font-size: 18px;
    padding: 0 12px;
    min-width: 80px;
  }

  :deep(.ant-menu-submenu-title) {
    font-size: 18px !important;
    padding: 0 12px !important;
    min-width: 80px !important;
  }

  :deep(.ant-menu) {
    max-width: 100%;
    overflow-x: auto;
    justify-content: flex-start; /* 移动端左对齐 */
  }
}

@media (max-width: 600px) {
  .global-header {
    padding: 0 8px;
  }

  :deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item),
  :deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-submenu) {
    font-size: 16px;
    padding: 0 8px;
    min-width: 70px;
  }

  :deep(.ant-menu-submenu-title) {
    font-size: 16px !important;
    padding: 0 8px !important;
    min-width: 70px !important;
  }

  .header-left {
    gap: 12px;
  }

  .logo {
    width: 90px; /* 移动端logo稍微小一点 */
  }
}

.header-right {
  display: flex;
  align-items: center;
}

.user-section {
  transition: all 0.3s ease;
}

.user-info {
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.user-info:hover {
  background: rgba(24, 144, 255, 0.05);
  transform: translateY(-1px);
}

.user-avatar {
  width: 36px !important;
  height: 36px !important;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.user-info:hover .user-avatar {
  transform: scale(1.1);
  border-color: rgba(24, 144, 255, 0.3);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.2);
}

.user-name {
  color: #ffffff;
  font-weight: 500;
  font-size: 16px;
  transition: color 0.3s ease;
  text-shadow: 0 0 5px rgba(255, 255, 255, 0.3);
}

.user-info:hover .user-name {
  color: #e6e6fa;
  text-shadow: 0 0 10px rgba(230, 230, 250, 0.8);
}

.login-section {
  transition: all 0.3s ease;
}

.login-btn {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 8px;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.2);
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(24, 144, 255, 0.4);
}

/* 响应式设计 */
@media (max-width: 600px) {
  .title {
    display: none;
  }

  .global-header {
    padding: 0 16px;
  }

  :deep(.ant-menu) {
    overflow-x: auto;
  }
}

/* 添加进入动画 */
@keyframes slideInDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.global-header {
  animation: slideInDown 0.6s ease-out;
}

/* 添加logo呼吸效果 */
@keyframes logoBreath {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.02);
  }
}

.logo {
  animation: logoBreath 3s ease-in-out infinite;
}

.header-left:hover .logo {
  animation: none;
}

/* Emoji图标样式和动态效果 */
.menu-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin-right: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.emoji-icon {
  filter: grayscale(0.2);
  transform: scale(1);
}

/* 菜单项悬停效果 */
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item:hover .emoji-icon),
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-submenu:hover .emoji-icon) {
  transform: scale(1.15) rotate(3deg);
  filter: grayscale(0) drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
  transition: all 0.3s ease;
}


/* 菜单项悬停时的图标效果 */
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item:hover .emoji-icon),
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-submenu:hover .emoji-icon) {
  filter: drop-shadow(0 0 4px rgba(24, 144, 255, 0.2));
  text-shadow: 0 0 4px rgba(24, 144, 255, 0.3);
  animation: emojiBounce 2s ease-in-out infinite;
}

@keyframes emojiBounce {
  0%, 20%, 53%, 80%, 100% {
    transform: scale(1.15) rotate(3deg) translateY(0);
  }
  40%, 43% {
    transform: scale(1.15) rotate(3deg) translateY(-1.5px);
  }
  70% {
    transform: scale(1.15) rotate(3deg) translateY(-0.8px);
  }
  90% {
    transform: scale(1.15) rotate(3deg) translateY(-0.3px);
  }
}

/* 选中状态的emoji效果 */
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item-selected .emoji-icon),
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-submenu-selected .emoji-icon) {
  transform: scale(1.1);
  filter: grayscale(0) drop-shadow(0 2px 6px rgba(24, 144, 255, 0.3));
  animation: emojiBounce 0.6s ease-out;
}

/* 菜单项悬停时的整体效果增强 */
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item:hover),
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-submenu:hover) {
  background: linear-gradient(135deg, rgba(24, 144, 255, 0.05), rgba(24, 144, 255, 0.1));
  transform: translateY(-1px) scale(1.01);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.1);
  border-radius: 6px 6px 0 0;
  transition: all 0.3s ease;
}


@keyframes gradientShift {
  0%, 100% {
    background-position: 0% 50%;
  }
  25% {
    background-position: 100% 50%;
  }
  50% {
    background-position: 50% 100%;
  }
  75% {
    background-position: 50% 0%;
  }
}


/* 菜单项悬停时的光波效果 */
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item:hover::after),
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item:hover::before),
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-submenu:hover::after) {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(24, 144, 255, 0.03), transparent);
  animation: lightWave 4s ease-in-out infinite;
}

@keyframes lightWave {
  0% {
    left: -100%;
  }
  100% {
    left: 100%;
  }
}

/* 菜单项悬停时的文字颜色变化 */
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item:hover .ant-menu-title-content),
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-submenu:hover .ant-menu-title-content) {
  color: #1890ff !important;
  font-weight: 600;
  transition: all 0.3s ease;
  transform: scale(1.02);
}



/* 选中状态的菜单项效果 */
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item-selected),
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-submenu-selected) {
  background: linear-gradient(135deg, rgba(24, 144, 255, 0.1), rgba(24, 144, 255, 0.15));
  border-bottom: 2px solid #1890ff;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.2);
  position: relative;
}

/* 选中状态的渐变边框效果 */
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item-selected::before),
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-submenu-selected::before) {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, #1890ff, #40a9ff, #1890ff);
  background-size: 200% 100%;
  animation: gradientMove 2s ease-in-out infinite;
}

@keyframes gradientMove {
  0%, 100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

/* 菜单项点击时的波纹效果 */
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item:active),
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-submenu:active) {
  transform: scale(0.98);
  transition: transform 0.1s ease;
}

/* 菜单项悬停时的波纹效果 */
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item:hover::before),
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-submenu:hover::before) {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: rgba(24, 144, 255, 0.03);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  animation: ripple 1.5s ease-out;
}

@keyframes ripple {
  to {
    width: 100px;
    height: 100px;
    opacity: 0;
  }
}


/* 菜单项悬停时的光晕效果 */
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item:hover::after),
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-submenu:hover::after) {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at center, rgba(24, 144, 255, 0.03) 0%, transparent 70%);
  border-radius: 6px 6px 0 0;
  opacity: 0;
  animation: glowPulse 4s ease-in-out infinite;
}

/* 菜单项悬停时的边框效果 */
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item:hover::before),
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-submenu:hover::before) {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border: 1px solid rgba(24, 144, 255, 0.1);
  border-radius: 6px 6px 0 0;
  opacity: 0;
  animation: borderFadeIn 0.5s ease-out forwards;
}

/* 菜单项悬停时的颜色变化效果 */
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item:hover),
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-submenu:hover) {
  color: #1890ff;
  transition: color 0.3s ease;
}

/* 菜单项悬停时的图标颜色变化效果 */
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item:hover .emoji-icon),
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-submenu:hover .emoji-icon) {
  filter: saturate(1.1) brightness(1.05);
  transition: all 0.3s ease;
  transform: scale(1.15) rotate(3deg);
}


@keyframes borderFadeIn {
  to {
    opacity: 1;
  }
}

@keyframes glowPulse {
  0%, 100% {
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
}

/* 链接菜单项的特殊样式 */
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item a) {
  display: flex;
  align-items: center;
  color: inherit;
  text-decoration: none;
}

:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item a:hover) {
  color: #1890ff;
}

/* 响应式emoji图标 */
@media (max-width: 768px) {
  .menu-icon {
    font-size: 18px;
    margin-right: 6px;
  }
}

/* 菜单项进入动画 */
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item),
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-submenu) {
  animation: menuItemSlideIn 0.6s ease-out;
  animation-fill-mode: both;
  position: relative;
  overflow: hidden;
}

/* 菜单项悬停时的阴影效果 */
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item:hover),
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-submenu:hover) {
  filter: drop-shadow(0 2px 4px rgba(24, 144, 255, 0.1));
  box-shadow:
    0 2px 8px rgba(24, 144, 255, 0.1),
    0 0 10px rgba(24, 144, 255, 0.05);
}

:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item:nth-child(1)) { animation-delay: 0.1s; }
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item:nth-child(2)) { animation-delay: 0.2s; }
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item:nth-child(3)) { animation-delay: 0.3s; }
:deep(.ant-menu-light.ant-menu-horizontal > .ant-menu-item:nth-child(4)) { animation-delay: 0.4s; }

@keyframes menuItemSlideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 600px) {
  .menu-icon {
    font-size: 16px;
    margin-right: 4px;
  }
}
</style>
