<template>
  <div class="funny-page">
    <div class="container">
      <div class="content">
        <div class="emoji">😏</div>
        <h1 class="title">啊？都有AI了还需要我帮你？</h1>
        <p class="subtitle">元仔 AI 比我聪明多了，你找它不香吗？</p>
        <div class="actions">
          <a-button type="primary" size="large" @click="goBack"> 你说得对，我找AI去 </a-button>
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
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
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
  animation: shake 2s infinite;
}

@keyframes shake {
  0%,
  100% {
    transform: translateX(0);
  }
  25% {
    transform: translateX(-5px);
  }
  75% {
    transform: translateX(5px);
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
