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
      <div class="content-wrapper">
        <router-view v-slot="{ Component }">
          <transition name="page" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </a-layout-content>
    <GlobalFooter />
  </a-layout>
</template>

<style scoped>
.basic-layout {
  min-height: 100vh;
  background: transparent;
  position: relative;
}

.basic-layout::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 80%, rgba(120, 119, 198, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(255, 119, 198, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 40% 40%, rgba(120, 219, 255, 0.2) 0%, transparent 50%);
  pointer-events: none;
  z-index: -1;
  animation: backgroundFloat 20s ease-in-out infinite;
}

@keyframes backgroundFloat {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  33% {
    transform: translateY(-20px) rotate(1deg);
  }
  66% {
    transform: translateY(10px) rotate(-1deg);
  }
}

.layout-content {
  margin: 16px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 16px;
  padding-bottom: 60px;
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.layout-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.1),
    transparent
  );
  transition: left 0.5s ease;
}

.layout-content:hover::before {
  left: 100%;
}

.layout-content:hover {
  background: rgba(255, 255, 255, 0.3);
  box-shadow: 
    0 12px 40px rgba(0, 0, 0, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

.content-wrapper {
  position: relative;
  z-index: 1;
}

/* 页面切换动画 */
.page-enter-active,
.page-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.page-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.page-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

.page-enter-to,
.page-leave-from {
  opacity: 1;
  transform: translateX(0);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .layout-content {
    margin: 12px;
    padding: 12px;
    border-radius: 12px;
  }
}

@media (max-width: 600px) {
  .layout-content {
    margin: 8px;
    padding: 12px;
    border-radius: 8px;
  }
  
  .basic-layout::before {
    animation: backgroundFloat 15s ease-in-out infinite;
  }

}

/* 添加进入动画 */
.basic-layout {
  animation: fadeInUp 0.8s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 优化滚动条 */
.layout-content::-webkit-scrollbar {
  width: 6px;
}

.layout-content::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
}

.layout-content::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
  transition: background 0.2s ease;
}

.layout-content::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}
</style>



