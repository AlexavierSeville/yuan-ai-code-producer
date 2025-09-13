<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { addApp, listMyAppVoByPage, listGoodAppVoByPage } from '@/api/appController'
import AppCard from '@/components/AppCard.vue'
import { DEPLOY_DOMAIN } from '@/config/env'

const router = useRouter()
const loginUserStore = useLoginUserStore()

// 用户提示词
const userPrompt = ref('')
const creating = ref(false)

// 我的应用数据
const myApps = ref<API.AppVO[]>([])
const myAppsPage = reactive({
  current: 1,
  pageSize: 6,
  total: 0,
})

// 精选应用数据
const featuredApps = ref<API.AppVO[]>([])
const featuredAppsPage = reactive({
  current: 1,
  pageSize: 6,
  total: 0,
})

// 设置提示词
const setPrompt = (prompt: string) => {
  userPrompt.value = prompt
}


// 提示词模板数据
const promptTemplates = ref([
  {
    id: 1,
    icon: '📝',
    title: '个人博客',
    desc: '现代化博客网站',
    type: '简易实现',
    prompt: '简易实现：创建一个简单的个人博客网站，包含首页、文章列表、文章详情三个页面。采用简洁的设计风格，支持响应式布局，文章支持分类和搜索功能，适合个人记录和分享。'
  },
  {
    id: 2,
    icon: '📝',
    title: '个人博客',
    desc: '现代化博客网站',
    type: '原生多文件',
    prompt: '原生多文件实现：创建一个完整的个人博客系统，包含前端展示页面和管理后台。支持用户注册登录、文章发布编辑、评论系统、内容管理等功能。采用现代化的界面设计，支持文章分类、标签管理和数据统计。'
  },
  {
    id: 3,
    icon: '📝',
    title: '个人博客',
    desc: '现代化博客网站',
    type: 'Vue实现',
    prompt: 'Vue实现：创建一个现代化的个人博客网站，包含文章列表、详情页、分类标签、搜索功能、评论系统和个人简介页面。采用简洁的设计风格，支持响应式布局，文章支持Markdown格式，首页展示最新文章和热门推荐。'
  },
  {
    id: 4,
    icon: '🏢',
    title: '企业官网',
    desc: '专业商务网站',
    type: '简易实现',
    prompt: '简易实现：创建一个简单的企业官网，包含首页、关于我们、产品服务、联系我们四个页面。采用商务风格的设计，包含轮播图、产品展示卡片，适合小型企业展示品牌形象。'
  },
  {
    id: 5,
    icon: '🏢',
    title: '企业官网',
    desc: '专业商务网站',
    type: '原生多文件',
    prompt: '原生多文件实现：创建一个完整的企业官网系统，包含前端展示页面和管理后台。支持内容管理、新闻发布、产品管理、客户留言等功能。采用专业的商务设计风格，支持多语言切换和在线客服功能。'
  },
  {
    id: 6,
    icon: '🏢',
    title: '企业官网',
    desc: '专业商务网站',
    type: 'Vue实现',
    prompt: 'Vue实现：设计一个专业的企业官网，包含公司介绍、产品服务展示、新闻资讯、联系我们等页面。采用商务风格的设计，包含轮播图、产品展示卡片、团队介绍、客户案例展示，支持多语言切换和在线客服功能。'
  },
  {
    id: 7,
    icon: '🛒',
    title: '在线商城',
    desc: '完整电商平台',
    type: '简易实现',
    prompt: '简易实现：创建一个简单的在线商城，包含商品展示、购物车、用户注册登录功能。采用现代化的商品卡片布局，支持商品搜索和筛选，适合小型电商业务。'
  },
  {
    id: 8,
    icon: '🛒',
    title: '在线商城',
    desc: '完整电商平台',
    type: '原生多文件',
    prompt: '原生多文件实现：创建一个完整的电商系统，包含前端商城和管理后台。支持商品管理、订单处理、支付集成、用户管理、库存管理等功能。采用现代化的界面设计，支持多种支付方式和物流跟踪。'
  },
  {
    id: 9,
    icon: '🛒',
    title: '在线商城',
    desc: '完整电商平台',
    type: 'Vue实现',
    prompt: 'Vue实现：构建一个功能完整的在线商城，包含商品展示、购物车、用户注册登录、订单管理、支付结算等功能。设计现代化的商品卡片布局，支持商品搜索筛选、用户评价、优惠券系统和会员积分功能。'
  }
])

// 技术栈数据
const techStack = ref([
  { name: 'Vue.js', icon: '💚' },
  { name: 'TypeScript', icon: '🔷' },
  { name: 'Node.js', icon: '🟢' },
  { name: 'AI/ML', icon: '🤖' }
])

// 侧边栏交互方法
const scrollToInput = () => {
  const inputSection = document.querySelector('.input-section')
  if (inputSection) {
    inputSection.scrollIntoView({ behavior: 'smooth' })
  }
}

const showExamples = () => {
  const examplesSection = document.querySelector('.section')
  if (examplesSection) {
    examplesSection.scrollIntoView({ behavior: 'smooth' })
  }
}

const clearInput = () => {
  userPrompt.value = ''
  message.success('输入已清空')
}


// 创建应用
const createApp = async () => {
  if (!userPrompt.value.trim()) {
    message.warning('请输入应用描述')
    return
  }

  if (!loginUserStore.loginUser.id) {
    message.warning('请先登录')
    await router.push('/user/login')
    return
  }

  creating.value = true
  try {
    const res = await addApp({
      initPrompt: userPrompt.value.trim(),
    })

    if (res.data.code === 0 && res.data.data) {
      message.success('应用创建成功')
      const appId = String(res.data.data)
      await router.push(`/app/chat/${appId}`)
    } else {
      message.error('创建失败：' + res.data.message)
    }
  } catch (error) {
    console.error('创建应用失败：', error)
    message.error('创建失败，请重试')
  } finally {
    creating.value = false
  }
}

// 加载我的应用
const loadMyApps = async () => {
  if (!loginUserStore.loginUser.id) {
    return
  }
  try {
    const res = await listMyAppVoByPage({
      pageNum: myAppsPage.current,
      pageSize: myAppsPage.pageSize,
      sortField: 'createTime',
      sortOrder: 'desc',
    })
    if (res.data.code === 0 && res.data.data) {
      myApps.value = res.data.data.records || []
      myAppsPage.total = Number(res.data.data.totalRow) || 0
    }
  } catch (error) {
    console.error('加载我的应用失败：', error)
  }
}

// 加载精选应用
const loadFeaturedApps = async () => {
  try {
    const res = await listGoodAppVoByPage({
      pageNum: featuredAppsPage.current,
      pageSize: featuredAppsPage.pageSize,
      sortField: 'createTime',
      sortOrder: 'desc',
    })
    if (res.data.code === 0 && res.data.data) {
      featuredApps.value = res.data.data.records || []
      featuredAppsPage.total = Number(res.data.data.totalRow) || 0
    }
  } catch (error) {
    console.error('加载精选应用失败：', error)
  }
}

// 查看对话
const viewChat = (appId: string | number | undefined) => {
  if (appId !== undefined && appId !== null) {
    router.push(`/app/chat/${appId}?view=1`)
  }
}

// 查看作品（通过 nginx 访问部署后的网页）
const viewWork = (app: API.AppVO) => {
  const key = app.deployKey
  if (!key) {
    message.warning('该应用尚未部署，无法预览')
    return
  }
  
  // 如果 deployKey 已经是完整URL，直接使用
  if (key.startsWith('http://') || key.startsWith('https://')) {
    window.open(key, '_blank')
    return
  }
  
  // 通过 nginx 访问部署后的网页
  // 使用配置的部署域名
  const nginxUrl = `${DEPLOY_DOMAIN}/${key}/`
  window.open(nginxUrl, '_blank')
}

// 鼠标跟随光效
const handleMouseMove = (e: MouseEvent) => {
  const { clientX, clientY } = e
  const { innerWidth, innerHeight } = window
  const x = (clientX / innerWidth) * 100
  const y = (clientY / innerHeight) * 100
  document.documentElement.style.setProperty('--mouse-x', `${x}%`)
  document.documentElement.style.setProperty('--mouse-y', `${y}%`)
}

onMounted(() => {
  loadMyApps()
  loadFeaturedApps()
  document.addEventListener('mousemove', handleMouseMove)
})

onUnmounted(() => {
  document.removeEventListener('mousemove', handleMouseMove)
})
</script>

<template>
  <div id="homePage">
    <!-- 左侧边栏 -->
    <div class="sidebar left-sidebar">
      <div class="sidebar-content">
        <!-- 提示词模板 -->
        <div class="template-panel">
          <h3>💡 提示词模板</h3>
          <div class="template-list">
            <div 
              v-for="template in promptTemplates" 
              :key="template.id"
              class="template-item"
              @click="setPrompt(template.prompt)"
            >
              <div class="template-icon">{{ template.icon }}</div>
              <div class="template-content">
                <div class="template-title">{{ template.title }}</div>
                <div class="template-type">{{ template.type }}</div>
                <div class="template-desc">{{ template.desc }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 功能特色 -->
        <div class="features-panel">
          <h3>✨ 平台特色</h3>
          <div class="feature-list">
            <div class="feature-item">
              <span class="feature-icon">🚀</span>
              <span>一键生成</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">🎨</span>
              <span>多种风格</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">📱</span>
              <span>响应式设计</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">⚡</span>
              <span>快速部署</span>
            </div>
          </div>
        </div>

        <!-- 使用技巧 -->
        <div class="tips-panel">
          <h3>💡 使用技巧</h3>
          <div class="tips-list">
            <div class="tip-item">
              <div class="tip-number">1</div>
              <div class="tip-text">描述越详细，生成效果越好</div>
            </div>
            <div class="tip-item">
              <div class="tip-number">2</div>
              <div class="tip-text">可以指定颜色、布局等要求</div>
            </div>
            <div class="tip-item">
              <div class="tip-number">3</div>
              <div class="tip-text">支持多种技术栈选择</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧边栏 -->
    <div class="sidebar right-sidebar">
      <div class="sidebar-content">
        <!-- 快速操作 -->
        <div class="actions-panel">
          <h3>⚡ 快速操作</h3>
          <div class="action-buttons">
            <a-button 
              type="primary" 
              block 
              class="action-btn"
              @click="scrollToInput"
            >
              <template #icon>✏️</template>
              开始创作
            </a-button>
            <a-button 
              type="default" 
              block 
              class="action-btn"
              @click="showExamples"
            >
              <template #icon>👀</template>
              查看案例
            </a-button>
            <a-button 
              type="default" 
              block 
              class="action-btn"
              @click="clearInput"
            >
              <template #icon>🗑️</template>
              清空输入
            </a-button>
          </div>
        </div>

        <!-- 技术栈展示 -->
        <div class="tech-panel">
          <h3>🛠️ 支持技术</h3>
          <div class="tech-grid">
            <div class="tech-item" v-for="tech in techStack" :key="tech.name">
              <div class="tech-icon">{{ tech.icon }}</div>
              <div class="tech-name">{{ tech.name }}</div>
            </div>
          </div>
        </div>

        <!-- 帮助信息 -->
        <div class="help-panel">
          <h3>❓ 需要帮助？</h3>
          <div class="help-content">
            <div class="help-item" @click="router.push('/funny/tutorial')">
              <div class="help-icon">📖</div>
              <div class="help-text">查看使用教程</div>
            </div>
            <div class="help-item" @click="router.push('/funny/support')">
              <div class="help-icon">💬</div>
              <div class="help-text">联系客服支持</div>
            </div>
            <div class="help-item" @click="router.push('/funny/feedback')">
              <div class="help-icon">📧</div>
              <div class="help-text">发送邮件反馈</div>
            </div>
          </div>
        </div>

      </div>
    </div>

    <div class="container">
      <!-- 英雄区域 -->
      <div class="hero-section">
        <h1 class="hero-title">元仔 AI 应用生成平台</h1>
        <p class="hero-description">代码写不了一点，用一句话帮您生成</p>
      </div>

      <!-- 用户提示词输入框 -->
      <div class="input-section">
        <a-textarea
          v-model:value="userPrompt"
          placeholder="帮我创建个人博客网站"
          :rows="4"
          :maxlength="1000"
          class="prompt-input"
        />
        <div class="input-actions">
          <a-button type="primary" size="large" @click="createApp" :loading="creating">
            <template #icon>
              <span>↑</span>
            </template>
          </a-button>
        </div>
      </div>

      <!-- 快捷按钮 -->
      <div class="quick-actions">
        <a-button
          type="default"
          @click="setPrompt('Vue实现：创建一个现代化的个人博客网站，包含文章列表、详情页、分类标签、搜索功能、评论系统和个人简介页面。采用简洁的设计风格，支持响应式布局，文章支持Markdown格式，首页展示最新文章和热门推荐。')"
        >个人博客网站</a-button>
        <a-button
          type="default"
          @click="setPrompt('Vue实现：设计一个专业的企业官网，包含公司介绍、产品服务展示、新闻资讯、联系我们等页面。采用商务风格的设计，包含轮播图、产品展示卡片、团队介绍、客户案例展示，支持多语言切换和在线客服功能。')"
        >企业官网</a-button>
        <a-button
          type="default"
          @click="setPrompt('Vue实现：构建一个功能完整的在线商城，包含商品展示、购物车、用户注册登录、订单管理、支付结算等功能。设计现代化的商品卡片布局，支持商品搜索筛选、用户评价、优惠券系统和会员积分功能。')"
        >在线商城</a-button>
        <a-button
          type="default"
          @click="setPrompt('Vue实现：制作一个精美的作品展示网站，适合设计师、摄影师、艺术家等创作者。包含作品画廊、项目详情页、个人简历、联系方式等模块。采用瀑布流或网格布局展示作品，支持图片放大预览和作品分类筛选。')"
        >作品展示网站</a-button>
      </div>

      <!-- 我的作品 -->
      <div class="section">
        <h2 class="section-title">我的作品</h2>
        <div class="app-grid">
          <AppCard
            v-for="app in myApps"
            :key="app.id"
            :app="app"
            @view-chat="viewChat"
            @view-work="viewWork"
          />
        </div>
        <div class="pagination-wrapper">
          <a-pagination
            v-model:current="myAppsPage.current"
            v-model:page-size="myAppsPage.pageSize"
            :total="myAppsPage.total"
            :show-size-changer="false"
            :show-total="(total: number) => `共 ${total} 个应用`"
            @change="loadMyApps"
          />
        </div>
      </div>

      <!-- 精选案例 -->
      <div class="section">
        <h2 class="section-title">精选案例</h2>
        <div class="featured-grid">
          <AppCard
            v-for="app in featuredApps"
            :key="app.id"
            :app="app"
            :featured="true"
            @view-chat="viewChat"
            @view-work="viewWork"
          />
        </div>
        <div class="pagination-wrapper">
          <a-pagination
            v-model:current="featuredAppsPage.current"
            v-model:page-size="featuredAppsPage.pageSize"
            :total="featuredAppsPage.total"
            :show-size-changer="false"
            :show-total="(total: number) => `共 ${total} 个案例`"
            @change="loadFeaturedApps"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
#homePage {
  width: 100%;
  margin: 0;
  padding: 0;
  min-height: 100vh;
  --mouse-x: 50%;
  --mouse-y: 50%;
  background: transparent;
  position: relative;
  overflow: hidden;
}

/* 科技感网格背景 */
#homePage::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    linear-gradient(rgba(59, 130, 246, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(59, 130, 246, 0.05) 1px, transparent 1px),
    linear-gradient(rgba(139, 92, 246, 0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(139, 92, 246, 0.04) 1px, transparent 1px);
  background-size:
    100px 100px,
    100px 100px,
    20px 20px,
    20px 20px;
  pointer-events: none;
  animation: gridFloat 20s ease-in-out infinite;
}

/* 动态光效 */
#homePage::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(
      600px circle at var(--mouse-x, 50%) var(--mouse-y, 50%),
      rgba(59, 130, 246, 0.08) 0%,
      rgba(139, 92, 246, 0.06) 40%,
      transparent 80%
    ),
    linear-gradient(45deg, transparent 30%, rgba(59, 130, 246, 0.04) 50%, transparent 70%),
    linear-gradient(-45deg, transparent 30%, rgba(139, 92, 246, 0.04) 50%, transparent 70%);
  pointer-events: none;
  animation: lightPulse 8s ease-in-out infinite alternate;
}

@keyframes gridFloat {
  0%,
  100% {
    transform: translate(0, 0);
  }
  50% {
    transform: translate(5px, 5px);
  }
}

@keyframes lightPulse {
  0% {
    opacity: 0.3;
  }
  100% {
    opacity: 0.7;
  }
}

/* 侧边栏样式 */
.sidebar {
  position: fixed;
  top: 80px;
  width: 280px;
  height: calc(100vh - 100px);
  overflow: visible;
  z-index: 10;
  padding: 20px;
  box-sizing: border-box;
}

.left-sidebar {
  left: 20px;
}

.right-sidebar {
  right: 20px;
}

.sidebar-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.sidebar h3 {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 16px;
  color: #1e293b;
  padding-bottom: 8px;
  border-bottom: 2px solid rgba(59, 130, 246, 0.2);
}

/* 提示词模板面板 */
.template-panel {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  padding: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.template-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.template-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid rgba(59, 130, 246, 0.1);
  flex-shrink: 0;
}

.template-item:hover {
  background: rgba(59, 130, 246, 0.05);
  border-color: rgba(59, 130, 246, 0.3);
  transform: translateX(4px);
}

.template-icon {
  font-size: 20px;
  flex-shrink: 0;
}

.template-content {
  flex: 1;
}

.template-title {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 4px;
}

.template-type {
  font-size: 11px;
  color: #3b82f6;
  background: rgba(59, 130, 246, 0.1);
  padding: 2px 6px;
  border-radius: 4px;
  display: inline-block;
  margin-bottom: 4px;
  font-weight: 500;
}

.template-desc {
  font-size: 12px;
  color: #64748b;
}

/* 功能特色面板 */
.features-panel {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  padding: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.feature-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: rgba(59, 130, 246, 0.05);
  border-radius: 8px;
  font-size: 12px;
  color: #475569;
  transition: all 0.3s;
}

.feature-item:hover {
  background: rgba(59, 130, 246, 0.1);
  transform: translateY(-2px);
}

.feature-icon {
  font-size: 16px;
}

/* 使用技巧面板 */
.tips-panel {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  padding: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.tips-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.tip-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  background: rgba(16, 185, 129, 0.05);
  border-radius: 8px;
  transition: all 0.3s;
}

.tip-item:hover {
  background: rgba(16, 185, 129, 0.1);
  transform: translateX(4px);
}

.tip-number {
  width: 20px;
  height: 20px;
  background: #10b981;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
}

.tip-text {
  font-size: 12px;
  color: #475569;
  line-height: 1.4;
}

/* 快速操作面板 */
.actions-panel {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  padding: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.action-btn {
  border-radius: 12px;
  height: 40px;
  font-weight: 500;
  transition: all 0.3s;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 技术栈面板 */
.tech-panel {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  padding: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.tech-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.tech-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1), rgba(139, 92, 246, 0.1));
  border-radius: 12px;
  transition: all 0.3s;
  border: 1px solid rgba(59, 130, 246, 0.2);
}

.tech-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
}

.tech-icon {
  font-size: 20px;
}

.tech-name {
  font-size: 12px;
  font-weight: 500;
  color: #475569;
}

/* 帮助面板 */
.help-panel {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  padding: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.help-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.help-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  transition: all 0.3s;
  cursor: pointer;
}

.help-item:hover {
  background: rgba(59, 130, 246, 0.05);
  transform: translateX(4px);
}

.help-icon {
  font-size: 16px;
  flex-shrink: 0;
}

.help-text {
  font-size: 12px;
  color: #475569;
}


.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  position: relative;
  z-index: 2;
  width: 100%;
  box-sizing: border-box;
}

/* 英雄区域 */
.hero-section {
  text-align: center;
  padding: 80px 0 60px;
  margin-bottom: 28px;
  color: #1e293b;
  position: relative;
  overflow: hidden;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(ellipse 800px 400px at center, rgba(59, 130, 246, 0.12) 0%, transparent 70%),
    linear-gradient(45deg, transparent 30%, rgba(139, 92, 246, 0.05) 50%, transparent 70%),
    linear-gradient(-45deg, transparent 30%, rgba(16, 185, 129, 0.04) 50%, transparent 70%);
  animation: heroGlow 10s ease-in-out infinite alternate;
}

@keyframes heroGlow {
  0% {
    opacity: 0.6;
    transform: scale(1);
  }
  100% {
    opacity: 1;
    transform: scale(1.02);
  }
}

.hero-title {
  font-size: 64px;
  font-weight: 700;
  margin: 0 0 20px;
  line-height: 1.2;
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 50%, #10b981 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -1px;
  position: relative;
  z-index: 2;
  animation: titleShimmer 3s ease-in-out infinite;
}

@keyframes titleShimmer {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

.hero-description {
  font-size: 20px;
  margin: 0;
  opacity: 0.8;
  color: #64748b;
  position: relative;
  z-index: 2;
}

/* 输入区域 */
.input-section {
  position: relative;
  margin: 0 auto 24px;
  max-width: 800px;
}

.prompt-input {
  border-radius: 16px;
  border: none;
  font-size: 16px;
  padding: 20px 60px 20px 20px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.prompt-input:focus {
  background: rgba(255, 255, 255, 1);
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.3);
  transform: translateY(-2px);
}

.input-actions {
  position: absolute;
  bottom: 12px;
  right: 12px;
  display: flex;
  gap: 8px;
  align-items: center;
}

/* 快捷按钮 */
.quick-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-bottom: 60px;
  flex-wrap: wrap;
}

.quick-actions .ant-btn {
  border-radius: 25px;
  padding: 8px 20px;
  height: auto;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(59, 130, 246, 0.2);
  color: #475569;
  backdrop-filter: blur(15px);
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.quick-actions .ant-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(59, 130, 246, 0.1), transparent);
  transition: left 0.5s;
}

.quick-actions .ant-btn:hover::before {
  left: 100%;
}

.quick-actions .ant-btn:hover {
  background: rgba(255, 255, 255, 0.9);
  border-color: rgba(59, 130, 246, 0.4);
  color: #3b82f6;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(59, 130, 246, 0.2);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-title {
    font-size: 40px;
  }

  .hero-description {
    font-size: 16px;
  }
}

/* 区域与网格样式 */
.section {
  margin-bottom: 60px;
}

.section-title {
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 32px;
  color: #1e293b;
}

.app-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.featured-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

@media (max-width: 768px) {
  .app-grid,
  .featured-grid {
    grid-template-columns: 1fr;
  }
}
</style>
