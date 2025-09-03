<script setup lang="ts">
</script>

<template>
  <a-layout-footer class="global-footer">
    <div class="footer-content">
      <div class="footer-text">
        <span class="footer-brand">元仔项目</span>
        <span class="footer-separator">by</span>
        <a 
          href="https://alexavieryuan.us.kg/" 
          target="_blank" 
          rel="noopener noreferrer"
          class="footer-link"
        >
          <span class="link-text">程序猿 元仔</span>
          <span class="link-icon">🚀</span>
        </a>
      </div>
      <div class="footer-decoration">
        <div class="decoration-dot"></div>
        <div class="decoration-dot"></div>
        <div class="decoration-dot"></div>
      </div>
    </div>
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
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-top: 1px solid rgba(102, 126, 234, 0.15);
  padding: 16px 20px;
  z-index: 10;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.08);
}

.global-footer:hover {
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 -6px 30px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.footer-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.footer-text {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
  transition: all 0.3s ease;
}

.footer-brand {
  font-weight: 600;
  color: #1890ff;
  background: linear-gradient(135deg, #1890ff 0%, #722ed1 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  transition: all 0.3s ease;
}

.footer-brand:hover {
  transform: scale(1.05);
}

.footer-separator {
  color: #999;
  font-weight: 400;
}

.footer-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: #1677ff;
  text-decoration: none;
  padding: 4px 8px;
  border-radius: 6px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.footer-link::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(24, 144, 255, 0.1),
    transparent
  );
  transition: left 0.5s ease;
}

.footer-link:hover::before {
  left: 100%;
}

.footer-link:hover {
  background: rgba(24, 144, 255, 0.05);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.2);
}

.link-text {
  font-weight: 500;
  transition: color 0.3s ease;
}

.footer-link:hover .link-text {
  color: #40a9ff;
}

.link-icon {
  font-size: 12px;
  transition: all 0.3s ease;
}

.footer-link:hover .link-icon {
  transform: scale(1.2) rotate(10deg);
}

.footer-decoration {
  display: flex;
  gap: 6px;
  align-items: center;
}

.decoration-dot {
  width: 4px;
  height: 4px;
  background: linear-gradient(135deg, #1890ff 0%, #722ed1 100%);
  border-radius: 50%;
  animation: dotPulse 2s ease-in-out infinite;
}

.decoration-dot:nth-child(2) {
  animation-delay: 0.3s;
}

.decoration-dot:nth-child(3) {
  animation-delay: 0.6s;
}

@keyframes dotPulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.6;
  }
  50% {
    transform: scale(1.3);
    opacity: 1;
  }
}

/* 响应式设计 */
@media (max-width: 600px) {
  .global-footer {
    padding: 12px 16px;
  }
  
  .footer-text {
    font-size: 13px;
    gap: 6px;
  }
  
  .footer-decoration {
    gap: 4px;
  }
  
  .decoration-dot {
    width: 3px;
    height: 3px;
  }
}

/* 添加进入动画 */
.global-footer {
  animation: slideInUp 0.8s ease-out 0.2s both;
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 优化滚动条 */
.global-footer::-webkit-scrollbar {
  display: none;
}
</style>



