<template>
  <div class="funny-page">
    <div class="container">
      <div class="content">
        <div class="emoji">🤔</div>
        <h1 class="title">啊？都这么简单了还需要教程？</h1>
        <p class="subtitle">元仔 AI 已经够智能了，你还要我手把手教你？</p>
        <div class="actions">
          <a-button type="primary" size="large" @click="goBack"> 好吧，我回去试试 </a-button>
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%,
  20%,
  50%,
  80%,
  100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-20px);
  }
  60% {
    transform: translateY(-10px);
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
