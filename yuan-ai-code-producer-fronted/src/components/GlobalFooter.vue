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
  border-top: 1px solid rgba(138, 43, 226, 0.3);
  padding: 8px 20px;
  z-index: 10;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 -2px 12px rgba(138, 43, 226, 0.2);
}

.global-footer:hover {
  background: linear-gradient(135deg, 
    rgba(30, 60, 114, 0.98) 0%, 
    rgba(75, 0, 130, 0.98) 25%, 
    rgba(138, 43, 226, 0.98) 50%, 
    rgba(30, 60, 114, 0.98) 75%, 
    rgba(75, 0, 130, 0.98) 100%
  );
  background-size: 400% 400%;
  animation: gradientShift 6s ease-in-out infinite;
  box-shadow: 0 -4px 20px rgba(138, 43, 226, 0.3);
  transform: translateY(-1px);
}

.footer-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.footer-text {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  color: #ffffff;
  transition: all 0.3s ease;
  font-weight: 500;
  text-shadow: 0 0 5px rgba(255, 255, 255, 0.3);
}

.footer-brand {
  font-weight: 700;
  color: #ffffff;
  background: linear-gradient(135deg, #ffffff 0%, #e6e6fa 50%, #dda0dd 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  transition: all 0.3s ease;
  font-size: 17px;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
}

.footer-brand:hover {
  transform: scale(1.05);
}

.footer-separator {
  color: #e6e6fa;
  font-weight: 500;
  font-size: 15px;
  text-shadow: 0 0 3px rgba(230, 230, 250, 0.5);
}

.footer-link {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  color: #ffffff;
  text-decoration: none;
  padding: 3px 6px;
  border-radius: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  font-size: 16px;
  text-shadow: 0 0 5px rgba(255, 255, 255, 0.3);
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
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 255, 255, 0.2);
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.6);
}

.link-text {
  font-weight: 600;
  transition: color 0.3s ease;
}

.footer-link:hover .link-text {
  color: #e6e6fa;
  text-shadow: 0 0 8px rgba(230, 230, 250, 0.8);
}

.link-icon {
  font-size: 14px;
  transition: all 0.3s ease;
}

.footer-link:hover .link-icon {
  transform: scale(1.2) rotate(10deg);
}

.footer-decoration {
  display: flex;
  gap: 4px;
  align-items: center;
}

.decoration-dot {
  width: 3px;
  height: 3px;
  background: linear-gradient(135deg, #ffffff 0%, #e6e6fa 50%, #dda0dd 100%);
  border-radius: 50%;
  animation: dotPulse 2s ease-in-out infinite;
  box-shadow: 0 0 5px rgba(255, 255, 255, 0.5);
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
    padding: 6px 16px;
  }
  
  .footer-text {
    font-size: 14px;
    gap: 6px;
  }
  
  .footer-brand {
    font-size: 15px;
  }
  
  .footer-link {
    font-size: 14px;
  }
  
  .footer-decoration {
    gap: 3px;
  }
  
  .decoration-dot {
    width: 2px;
    height: 2px;
  }
}

/* 添加进入动画 */
.global-footer {
  animation: slideInUp 0.8s ease-out 0.2s both;
}

/* 渐变动画 */
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



