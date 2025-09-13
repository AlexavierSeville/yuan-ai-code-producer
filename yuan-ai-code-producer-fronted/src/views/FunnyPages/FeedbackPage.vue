<template>
  <div class="funny-page">
    <div class="container">
      <div class="content">
        <div class="emoji">😴</div>
        <h1 class="title">啊？别了吧？我很忙的。</h1>
        <p class="subtitle">元仔 AI 在努力工作，我也要休息一下了...</p>
        <div class="actions">
          <a-button type="primary" size="large" @click="goBack"> 好吧，不打扰你了 </a-button>
          <a-button type="default" size="large" @click="goHome"> 回到首页 </a-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goBack = () => {
  router.back()
}

const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.funny-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.container {
  max-width: 600px;
  width: 100%;
}

.content {
  text-align: center;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 60px 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(20px);
}

.emoji {
  font-size: 80px;
  margin-bottom: 30px;
  animation: sleep 3s infinite;
}

@keyframes sleep {
  0%,
  100% {
    transform: rotate(0deg);
  }
  25% {
    transform: rotate(-5deg);
  }
  75% {
    transform: rotate(5deg);
  }
}

.title {
  font-size: 36px;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 20px;
  line-height: 1.3;
}

.subtitle {
  font-size: 18px;
  color: #718096;
  margin-bottom: 40px;
  line-height: 1.6;
}

.actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
}

.actions .ant-btn {
  border-radius: 25px;
  padding: 12px 30px;
  height: auto;
  font-weight: 500;
}

@media (max-width: 768px) {
  .content {
    padding: 40px 20px;
  }

  .title {
    font-size: 28px;
  }

  .emoji {
    font-size: 60px;
  }

  .actions {
    flex-direction: column;
    align-items: center;
  }

  .actions .ant-btn {
    width: 200px;
  }
}
</style>
