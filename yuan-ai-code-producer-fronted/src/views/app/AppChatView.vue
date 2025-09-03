<template>
  <div id="appChatPage">
    <!-- 顶部栏 -->
    <div class="header-bar">
      <div class="header-left">
        <h1 class="app-name">{{ appInfo?.appName || '网站生成器' }}</h1>
        <a-tag v-if="appInfo?.codeGenType" color="blue" class="code-gen-type-tag">
          {{ formatCodeGenType(appInfo.codeGenType) }}
        </a-tag>
      </div>
      <div class="header-right">
        <a-button type="default" @click="showAppDetail">
          <template #icon>
            <InfoCircleOutlined />
          </template>
          应用详情
        </a-button>
        <a-button
          type="primary"
          ghost
          @click="downloadCode"
          :loading="downloading"
          :disabled="!isOwner"
        >
          <template #icon>
            <DownloadOutlined />
          </template>
          下载代码
        </a-button>
        <a-button type="primary" @click="deployApp" :loading="deploying">
          <template #icon>
            <CloudUploadOutlined />
          </template>
          部署~
        </a-button>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧对话区域 -->
      <div class="chat-section">
        <!-- 消息区域 -->
        <div class="messages-container" ref="messagesContainer">
          <!-- 加载更多按钮 -->
          <div v-if="hasMoreHistory" class="load-more-container">
            <a-button type="link" @click="loadMoreHistory" :loading="loadingHistory" size="small">
              加载更多历史消息
            </a-button>
          </div>
          
          <!-- 滚动到顶部按钮 -->
          <div v-if="showScrollToTop" class="scroll-to-top-container">
            <a-button 
              type="primary" 
              shape="circle" 
              size="small"
              @click="scrollToTop"
              class="scroll-to-top-btn"
            >
              <template #icon>
                <span>↑</span>
              </template>
            </a-button>
          </div>
          
          <div v-for="(message, index) in messages" :key="index" class="message-item">
            <div v-if="message.type === 'user'" class="user-message">
              <div class="message-content">{{ message.content }}</div>
              <div class="message-avatar">
                <a-avatar :src="loginUserStore.loginUser.userAvatar" :size="48" />
              </div>
            </div>
            <div v-else class="ai-message">
              <div class="message-avatar">
                <a-avatar :src="aiAvatar" :size="48" />
              </div>
              <div class="message-content">
                <MarkdownRenderer v-if="message.content" :content="message.content" />
                <div v-if="message.loading" class="loading-indicator">
                  <a-spin size="small" />
                  <span>AI 正在思考...</span>
                  <div class="loading-dots">
                    <span class="dot"></span>
                    <span class="dot"></span>
                    <span class="dot"></span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 选中元素信息展示 -->
        <a-alert
          v-if="selectedElementInfo"
          class="selected-element-alert"
          type="info"
          closable
          @close="clearSelectedElement"
        >
          <template #message>
            <div class="selected-element-info">
              <div class="element-header">
                <span class="element-tag">
                  选中元素：{{ selectedElementInfo.tagName.toLowerCase() }}
                </span>
                <span v-if="selectedElementInfo.id" class="element-id">
                  #{{ selectedElementInfo.id }}
                </span>
                <span v-if="selectedElementInfo.className" class="element-class">
                  .{{ selectedElementInfo.className.split(' ').join('.') }}
                </span>
              </div>
              <div class="element-details">
                <div v-if="selectedElementInfo.textContent" class="element-item">
                  内容: {{ selectedElementInfo.textContent.substring(0, 50) }}
                  {{ selectedElementInfo.textContent.length > 50 ? '...' : '' }}
                </div>
                <div v-if="selectedElementInfo.pagePath" class="element-item">
                  页面路径: {{ selectedElementInfo.pagePath }}
                </div>
                <div class="element-item">
                  选择器:
                  <code class="element-selector-code">{{ selectedElementInfo.selector }}</code>
                </div>
              </div>
            </div>
          </template>
        </a-alert>

        <!-- 用户消息输入框 -->
        <div class="input-container">
          <div class="input-wrapper">
            <a-tooltip v-if="!isOwner" title="无法在别人的作品下对话哦~" placement="top">
              <a-textarea
                v-model:value="userInput"
                :placeholder="getInputPlaceholder()"
                :rows="4"
                :maxlength="1000"
                @keydown.enter.prevent="sendMessage"
                :disabled="isGenerating || !isOwner"
              />
            </a-tooltip>
            <a-textarea
              v-else
              v-model:value="userInput"
              :placeholder="getInputPlaceholder()"
              :rows="4"
              :maxlength="1000"
              @keydown.enter.prevent="sendMessage"
              :disabled="isGenerating"
            />
            <div class="input-actions">
              <a-button
                type="primary"
                @click="sendMessage"
                :loading="isGenerating"
                :disabled="!isOwner"
              >
                <template #icon>
                  <SendOutlined />
                </template>
              </a-button>
            </div>
          </div>
        </div>
      </div>
      <!-- 右侧网页展示区域 -->
      <div class="preview-section">
        <div class="preview-header">
          <h3>生成后的网页展示</h3>
          <div class="preview-actions">
            <a-button
              v-if="isOwner && previewUrl"
              type="link"
              :danger="isEditMode"
              @click="toggleEditMode"
              :class="{ 'edit-mode-active': isEditMode }"
              style="padding: 0; height: auto; margin-right: 12px"
            >
              <template #icon>
                <EditOutlined />
              </template>
              {{ isEditMode ? '退出编辑' : '编辑模式' }}
            </a-button>
            <a-button v-if="previewUrl" type="link" @click="refreshPreview">
              <template #icon>
                <ReloadOutlined />
              </template>
              刷新预览
            </a-button>
            <a-button v-if="previewUrl" type="link" @click="openInNewTab">
              <template #icon>
                <ExportOutlined />
              </template>
              新窗口打开
            </a-button>
          </div>
        </div>
        <div class="preview-content">
          <div v-if="!previewUrl && !isGenerating" class="preview-placeholder">
            <div class="placeholder-icon">🌐</div>
            <p>网站文件生成完成后将在这里展示</p>
          </div>
          <div v-else-if="isGenerating" class="preview-loading">
            <a-spin size="large" />
            <p>正在生成网站...</p>
          </div>
          <div v-else-if="previewUrl && !previewReady" class="preview-loading">
            <a-spin size="large" />
            <p>正在加载预览...</p>
            <div v-if="previewCheckCount > 0" class="preview-check-info">
              <small>检查中... ({{ previewCheckCount }}/{{ maxPreviewChecks }})</small>
            </div>
          </div>
          <iframe
            v-else
            :key="previewRefreshKey"
            :src="previewUrl"
            class="preview-iframe"
            frameborder="0"
            @load="onIframeLoad"
          ></iframe>
        </div>
      </div>
    </div>

    <!-- 应用详情弹窗 -->
    <AppDetailModal
      v-model:open="appDetailVisible"
      :app="appInfo"
      :show-actions="isOwner || isAdmin"
      @edit="editApp"
      @delete="deleteApp"
    />

    <!-- 部署成功弹窗 -->
    <DeploySuccessModal
      v-model:open="deployModalVisible"
      :deploy-url="deployUrl"
      @open-site="openDeployedSite"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, onUnmounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import {
  getAppVoById,
  deployApp as deployAppApi,
  deleteApp as deleteAppApi,
} from '@/api/appController'
import { listAppChatHistory } from '@/api/chatHistoryController'
import { CodeGenTypeEnum, formatCodeGenType } from '@/utils/codeGenTypes'
import request from '@/request'

import MarkdownRenderer from '@/components/MarkdownRenderer.vue'
import AppDetailModal from '@/components/AppDetailModal.vue'
import DeploySuccessModal from '@/components/DeploySuccessModal.vue'
import aiAvatar from '@/assets/aiAvatar2.png'
import { API_BASE_URL, getStaticPreviewUrl } from '@/config/env'
import { VisualEditor, type ElementInfo } from '@/utils/visualEditor'

import {
  CloudUploadOutlined,
  SendOutlined,
  ExportOutlined,
  InfoCircleOutlined,
  DownloadOutlined,
  EditOutlined,
  ReloadOutlined,
} from '@ant-design/icons-vue'

const route = useRoute()
const router = useRouter()
const loginUserStore = useLoginUserStore()

// 应用信息
const appInfo = ref<API.AppVO>()
const appId = ref<any>()

// 对话相关
interface Message {
  type: 'user' | 'ai'
  content: string
  loading?: boolean
  createTime?: string
}

const messages = ref<Message[]>([])
const userInput = ref('')
const isGenerating = ref(false)
const messagesContainer = ref<HTMLElement>()

// 对话历史相关
const loadingHistory = ref(false)
const hasMoreHistory = ref(false)
const lastCreateTime = ref<string>()
const historyLoaded = ref(false)

// 预览相关
const previewUrl = ref('')
const previewReady = ref(false)
const previewRefreshKey = ref(0) // 用于强制刷新iframe
const previewCheckInterval = ref<number | null>(null) // 预览检查定时器
const previewCheckCount = ref(0) // 预览检查次数
const maxPreviewChecks = 60 // 最大检查次数（60秒）
const lastPreviewHash = ref<string>('') // 记录上次预览内容的哈希值
const previewContentCache = ref<Map<string, string>>(new Map()) // 预览内容缓存

// 部署相关
const deploying = ref(false)
const deployModalVisible = ref(false)
const deployUrl = ref('')

// 下载相关
const downloading = ref(false)

// 可视化编辑相关
const isEditMode = ref(false)
const selectedElementInfo = ref<ElementInfo | null>(null)
const visualEditor = new VisualEditor({
  onElementSelected: (elementInfo: ElementInfo) => {
    selectedElementInfo.value = elementInfo
  },
})

// 权限相关
const isOwner = computed(() => {
  return appInfo.value?.userId === loginUserStore.loginUser.id
})

const isAdmin = computed(() => {
  return loginUserStore.loginUser.userRole === 'admin'
})

// 应用详情相关
const appDetailVisible = ref(false)

// 显示应用详情
const showAppDetail = () => {
  appDetailVisible.value = true
}

// 加载对话历史
const loadChatHistory = async (isLoadMore = false) => {
  if (!appId.value || loadingHistory.value) return
  loadingHistory.value = true
  try {
    const params: API.listAppChatHistoryParams = {
      appId: appId.value,
      pageSize: 10,
    }
    // 如果是加载更多，传递最后一条消息的创建时间作为游标
    if (isLoadMore && lastCreateTime.value) {
      params.lastCreateTime = lastCreateTime.value
    }
    const res = await listAppChatHistory(params)
    if (res.data.code === 0 && res.data.data) {
      const chatHistories = res.data.data.records || []
      if (chatHistories.length > 0) {
        // 将对话历史转换为消息格式，并按时间正序排列（老消息在前）
        const historyMessages: Message[] = chatHistories
          .map((chat) => ({
            type: (chat.messageType === 'user' ? 'user' : 'ai') as 'user' | 'ai',
            content: chat.message || '',
            createTime: chat.createTime,
          }))
          .reverse() // 反转数组，让老消息在前
        if (isLoadMore) {
          // 加载更多时，将历史消息添加到开头
          messages.value.unshift(...historyMessages)
        } else {
          // 初始加载，直接设置消息列表
          messages.value = historyMessages
        }
        // 更新游标
        lastCreateTime.value = chatHistories[chatHistories.length - 1]?.createTime
        // 检查是否还有更多历史
        hasMoreHistory.value = chatHistories.length === 10
        
        // 如果不是加载更多，则滚动到底部
        if (!isLoadMore) {
          await nextTick()
          setTimeout(() => {
            scrollToBottom()
          }, 100)
        }
      } else {
        hasMoreHistory.value = false
      }
      historyLoaded.value = true
    }
  } catch (error) {
    console.error('加载对话历史失败：', error)
    message.error('加载对话历史失败')
  } finally {
    loadingHistory.value = false
  }
}

// 加载更多历史消息
const loadMoreHistory = async () => {
  await loadChatHistory(true)
}

// 获取应用信息
const fetchAppInfo = async () => {
  const id = route.params.id as string
  if (!id) {
    message.error('应用ID不存在')
    router.push('/')
    return
  }

  appId.value = id

  try {
    const res = await getAppVoById({ id: id as unknown as number })
    if (res.data.code === 0 && res.data.data) {
      appInfo.value = res.data.data

      // 先加载对话历史
      await loadChatHistory()
      
      // 等待DOM更新完成
      await nextTick()
      
      // 如果有至少2条对话记录，展示对应的网站
      if (messages.value.length >= 2) {
        updatePreview()
      }
      // 检查是否需要自动发送初始提示词
      // 只有在是自己的应用且没有对话历史时才自动发送
      if (
        appInfo.value.initPrompt &&
        isOwner.value &&
        messages.value.length === 0 &&
        historyLoaded.value
      ) {
        await sendInitialMessage(appInfo.value.initPrompt)
      }
    } else {
      message.error('获取应用信息失败')
      router.push('/')
    }
  } catch (error) {
    console.error('获取应用信息失败：', error)
    message.error('获取应用信息失败')
    router.push('/')
  }
}

// 发送初始消息
const sendInitialMessage = async (prompt: string) => {
  // 添加用户消息
  messages.value.push({
    type: 'user',
    content: prompt,
  })

  // 添加AI消息占位符
  const aiMessageIndex = messages.value.length
  messages.value.push({
    type: 'ai',
    content: '',
    loading: true,
  })

  await nextTick()
  scrollToBottom()

  // 开始生成
  isGenerating.value = true
  await generateCode(prompt, aiMessageIndex)
}

// 发送消息
const sendMessage = async () => {
  if (!userInput.value.trim() || isGenerating.value) {
    return
  }

  let message = userInput.value.trim()
  // 如果有选中的元素，将元素信息添加到提示词中
  if (selectedElementInfo.value) {
    let elementContext = `\n\n选中元素信息：`
    if (selectedElementInfo.value.pagePath) {
      elementContext += `\n- 页面路径: ${selectedElementInfo.value.pagePath}`
    }
    elementContext += `\n- 标签: ${selectedElementInfo.value.tagName.toLowerCase()}\n- 选择器: ${selectedElementInfo.value.selector}`
    if (selectedElementInfo.value.textContent) {
      elementContext += `\n- 当前内容: ${selectedElementInfo.value.textContent.substring(0, 100)}`
    }
    message += elementContext
  }
  userInput.value = ''
  // 添加用户消息（包含元素信息）
  messages.value.push({
    type: 'user',
    content: message,
  })

  // 发送消息后，清除选中元素并退出编辑模式
  if (selectedElementInfo.value) {
    clearSelectedElement()
    if (isEditMode.value) {
      toggleEditMode()
    }
  }

  // 添加AI消息占位符
  const aiMessageIndex = messages.value.length
  messages.value.push({
    type: 'ai',
    content: '',
    loading: true,
  })

  await nextTick()
  scrollToBottom()

  // 开始生成
  isGenerating.value = true
  await generateCode(message, aiMessageIndex)
}

// 生成代码 - 使用 EventSource 处理流式响应
const generateCode = async (userMessage: string, aiMessageIndex: number) => {
  let eventSource: EventSource | null = null
  let streamCompleted = false

  try {
    // 获取 axios 配置的 baseURL
    const baseURL = request.defaults.baseURL || API_BASE_URL

    // 构建URL参数
    const params = new URLSearchParams({
      appId: appId.value || '',
      message: userMessage,
    })

    const url = `${baseURL}/app/chat/gen/code?${params}`

    // 创建 EventSource 连接
    eventSource = new EventSource(url, {
      withCredentials: true,
    })

    let fullContent = ''

    // 处理接收到的消息
    eventSource.onmessage = function (event) {
      if (streamCompleted) return

      try {
        // 解析JSON包装的数据
        const parsed = JSON.parse(event.data)
        const content = parsed.d

        // 拼接内容
        if (content !== undefined && content !== null) {
          fullContent += content
          messages.value[aiMessageIndex].content = fullContent
          messages.value[aiMessageIndex].loading = false
          scrollToBottom()
        }
      } catch (error) {
        console.error('解析消息失败:', error)
        handleError(error, aiMessageIndex)
      }
    }

    // 处理done事件
    eventSource.addEventListener('done', function () {
      if (streamCompleted) return

      streamCompleted = true
      isGenerating.value = false
      eventSource?.close()

      // 立即开始检查预览，然后延迟更新应用信息
      updatePreview()
      
      // 延迟更新应用信息，确保后端已完成处理
      setTimeout(async () => {
        await fetchAppInfo()
        // 再次检查预览，确保获取到最新的信息
        updatePreview()
      }, 2000) // 减少延迟时间，提高响应速度
    })

    // 处理错误
    eventSource.onerror = function () {
      if (streamCompleted || !isGenerating.value) return
      // 检查是否是正常的连接关闭
      if (eventSource?.readyState === EventSource.CONNECTING) {
        streamCompleted = true
        isGenerating.value = false
        eventSource?.close()

        // 立即开始检查预览
        updatePreview()
        
        setTimeout(async () => {
          await fetchAppInfo()
          updatePreview()
        }, 2000) // 减少延迟时间，提高响应速度
      } else {
        handleError(new Error('SSE连接错误'), aiMessageIndex)
      }
    }
  } catch (error) {
    console.error('创建 EventSource 失败：', error)
    handleError(error, aiMessageIndex)
  }
}

// 错误处理函数
const handleError = (error: unknown, aiMessageIndex: number) => {
  console.error('生成代码失败：', error)
  messages.value[aiMessageIndex].content = '抱歉，生成过程中出现了错误，请重试。'
  messages.value[aiMessageIndex].loading = false
  message.error('生成失败，请重试')
  isGenerating.value = false
}

// 更新预览
const updatePreview = () => {
  if (appId.value) {
    const codeGenType = appInfo.value?.codeGenType || CodeGenTypeEnum.HTML
    const newPreviewUrl = getStaticPreviewUrl(codeGenType, appId.value)
    
    // 检查URL是否发生变化
    if (previewUrl.value !== newPreviewUrl) {
      previewUrl.value = newPreviewUrl
      // 重置预览状态
      previewReady.value = false
      lastPreviewHash.value = ''
      
      // 强制刷新iframe
      previewRefreshKey.value++
      
      console.log('开始检查新的预览URL:', newPreviewUrl)
      // 开始检查预览是否可用
      startPreviewCheck()
    } else {
      // URL没有变化，但需要检查内容是否更新
      console.log('URL未变化，检查内容更新...')
      previewReady.value = false
      startPreviewCheck()
    }
  }
}

// 开始预览检查
const startPreviewCheck = () => {
  // 清除之前的定时器
  if (previewCheckInterval.value) {
    clearInterval(previewCheckInterval.value)
  }
  
  previewCheckCount.value = 0
  console.log('开始预览检查...')
  
  // 每500毫秒检查一次预览是否可用，提高响应速度
  previewCheckInterval.value = setInterval(async () => {
    previewCheckCount.value++
    console.log(`预览检查第${previewCheckCount.value}次...`)
    
    try {
      // 检查预览文件是否存在和内容是否变化
      const isPreviewAvailable = await checkPreviewAvailabilitySmart()
      
      if (isPreviewAvailable) {
        // 预览可用，停止检查
        stopPreviewCheck()
        console.log('预览文件已就绪')
        message.success('预览已就绪！')
        // 设置预览就绪状态
        previewReady.value = true
        return
      }
      
      // 超过最大检查次数，停止检查
      if (previewCheckCount.value >= maxPreviewChecks) {
        stopPreviewCheck()
        console.log('预览检查超时')
        message.warning('预览检查超时，请手动刷新')
        return
      }
    } catch (error) {
      console.error('预览检查失败:', error)
    }
  }, 500) // 减少检查间隔，提高响应速度
}

// 停止预览检查
const stopPreviewCheck = () => {
  if (previewCheckInterval.value) {
    clearInterval(previewCheckInterval.value)
    previewCheckInterval.value = null
    console.log('预览检查已停止')
  }
}

// 检查预览是否可用
const checkPreviewAvailability = async (): Promise<boolean> => {
  if (!previewUrl.value) return false
  
  try {
    // 尝试通过fetch检查文件是否存在
    const response = await fetch(previewUrl.value, {
      method: 'HEAD',
      mode: 'no-cors', // 避免CORS问题
    })
    
    // 如果能获取到响应，说明文件存在
    return true
  } catch (error) {
    // 如果fetch失败，尝试通过iframe加载检查
    try {
      return await checkIframeAvailability()
    } catch (iframeError) {
      console.error('iframe检查失败:', iframeError)
      return false
    }
  }
}

// 改进的预览检查逻辑 - 检查文件是否存在和内容是否变化
const checkPreviewAvailabilityImproved = async (): Promise<boolean> => {
  if (!previewUrl.value) return false
  
  try {
    // 尝试通过fetch获取文件内容
    const response = await fetch(previewUrl.value, {
      method: 'GET',
      mode: 'no-cors', // 避免CORS问题
    })
    
    if (response.ok) {
      // 获取文件内容
      const content = await response.text()
      
      // 计算内容的哈希值（简单的字符串哈希）
      const contentHash = simpleHash(content)
      
      // 检查内容是否发生变化
      if (contentHash !== lastPreviewHash.value) {
        console.log('检测到预览内容变化，哈希值:', contentHash)
        lastPreviewHash.value = contentHash
        return true
      }
      
      // 内容没有变化，继续等待
      return false
    }
    
    return false
  } catch (error) {
    // 如果fetch失败，尝试通过iframe加载检查
    try {
      return await checkIframeAvailability()
    } catch (iframeError) {
      console.error('iframe检查失败:', iframeError)
      return false
    }
  }
}

// 增强的预览检查 - 通过多种方式检测变化
const checkPreviewAvailabilityEnhanced = async (): Promise<boolean> => {
  if (!previewUrl.value) return false
  
  try {
    // 方法1: 通过fetch获取文件内容并检查哈希
    const response = await fetch(previewUrl.value, {
      method: 'GET',
      mode: 'no-cors',
    })
    
    if (response.ok) {
      const content = await response.text()
      const contentHash = simpleHash(content)
      
      // 检查内容是否发生变化
      if (contentHash !== lastPreviewHash.value) {
        console.log('检测到预览内容变化，哈希值:', contentHash)
        lastPreviewHash.value = contentHash
        return true
      }
    }
    
    // 方法2: 检查文件大小变化（如果支持）
    try {
      const headResponse = await fetch(previewUrl.value, {
        method: 'HEAD',
        mode: 'no-cors',
      })
      
      if (headResponse.ok) {
        const contentLength = headResponse.headers.get('content-length')
        if (contentLength) {
          const currentSize = parseInt(contentLength)
          const cachedSize = previewContentCache.value.get('size')
          
          if (cachedSize && currentSize !== parseInt(cachedSize)) {
            console.log('检测到文件大小变化:', cachedSize, '->', currentSize)
            previewContentCache.value.set('size', currentSize.toString())
            return true
          }
          
          if (!cachedSize) {
            previewContentCache.value.set('size', currentSize.toString())
          }
        }
      }
    } catch (sizeError) {
      // 忽略大小检查错误
    }
    
    // 方法3: 检查Last-Modified头（如果支持）
    try {
      const lastModified = response.headers.get('last-modified')
      if (lastModified) {
        const currentModified = new Date(lastModified).getTime()
        const cachedModified = previewContentCache.value.get('lastModified')
        
        if (cachedModified && currentModified !== parseInt(cachedModified)) {
          console.log('检测到文件修改时间变化:', cachedModified, '->', currentModified)
          previewContentCache.value.set('lastModified', currentModified.toString())
          return true
        }
        
        if (!cachedModified) {
          previewContentCache.value.set('lastModified', currentModified.toString())
        }
      }
    } catch (modifiedError) {
      // 忽略修改时间检查错误
    }
    
    // 内容没有变化，继续等待
    return false
  } catch (error) {
    // 如果fetch失败，尝试通过iframe加载检查
    try {
      return await checkIframeAvailability()
    } catch (iframeError) {
      console.error('iframe检查失败:', iframeError)
      return false
    }
  }
}

// 智能预览检查 - 结合多种检测方式
const checkPreviewAvailabilitySmart = async (): Promise<boolean> => {
  if (!previewUrl.value) return false
  
  try {
    // 首先尝试通过HEAD请求检查文件状态
    const headResponse = await fetch(previewUrl.value, {
      method: 'HEAD',
      mode: 'no-cors',
    })
    
    if (headResponse.ok) {
      // 检查ETag（如果支持）
      const etag = headResponse.headers.get('etag')
      if (etag) {
        const cachedEtag = previewContentCache.value.get('etag')
        if (cachedEtag && cachedEtag !== etag) {
          console.log('检测到ETag变化:', cachedEtag, '->', etag)
          previewContentCache.value.set('etag', etag)
          return true
        }
        if (!cachedEtag) {
          previewContentCache.value.set('etag', etag)
        }
      }
      
      // 检查Last-Modified（如果支持）
      const lastModified = headResponse.headers.get('last-modified')
      if (lastModified) {
        const currentModified = new Date(lastModified).getTime()
        const cachedModified = previewContentCache.value.get('lastModified')
        
        if (cachedModified && currentModified !== parseInt(cachedModified)) {
          console.log('检测到文件修改时间变化:', cachedModified, '->', currentModified)
          previewContentCache.value.set('lastModified', currentModified.toString())
          return true
        }
        
        if (!cachedModified) {
          previewContentCache.value.set('lastModified', currentModified.toString())
        }
      }
    }
    
    // 然后通过GET请求检查内容变化
    const response = await fetch(previewUrl.value, {
      method: 'GET',
      mode: 'no-cors',
    })
    
    if (response.ok) {
      const content = await response.text()
      const contentHash = simpleHash(content)
      
      // 检查内容是否发生变化
      if (contentHash !== lastPreviewHash.value) {
        console.log('检测到预览内容变化，哈希值:', contentHash)
        lastPreviewHash.value = contentHash
        return true
      }
    }
    
    // 内容没有变化，继续等待
    return false
  } catch (error) {
    // 如果fetch失败，尝试通过iframe加载检查
    try {
      return await checkIframeAvailability()
    } catch (iframeError) {
      console.error('iframe检查失败:', iframeError)
      return false
    }
  }
}

// 简单的字符串哈希函数
const simpleHash = (str: string): string => {
  let hash = 0
  if (str.length === 0) return hash.toString()
  
  for (let i = 0; i < str.length; i++) {
    const char = str.charCodeAt(i)
    hash = ((hash << 5) - hash) + char
    hash = hash & hash // 转换为32位整数
  }
  
  return hash.toString()
}

// 通过iframe检查预览是否可用
const checkIframeAvailability = (): Promise<boolean> => {
  return new Promise((resolve) => {
    const testIframe = document.createElement('iframe')
    testIframe.style.display = 'none'
    testIframe.src = previewUrl.value
    
    const timeout = setTimeout(() => {
      document.body.removeChild(testIframe)
      resolve(false)
    }, 3000) // 3秒超时
    
    testIframe.onload = () => {
      clearTimeout(timeout)
      document.body.removeChild(testIframe)
      resolve(true)
    }
    
    testIframe.onerror = () => {
      clearTimeout(timeout)
      document.body.removeChild(testIframe)
      resolve(false)
    }
    
    document.body.appendChild(testIframe)
  })
}

// 滚动到底部
const scrollToBottom = () => {
  if (messagesContainer.value) {
    // 使用nextTick确保DOM更新完成
    nextTick(() => {
      if (messagesContainer.value) {
        // 平滑滚动到底部
        messagesContainer.value.scrollTo({
          top: messagesContainer.value.scrollHeight,
          behavior: 'smooth'
        })
      }
    })
  }
}

// 滚动到顶部
const scrollToTop = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = 0
  }
}

// 显示滚动到顶部按钮
const showScrollToTop = computed(() => {
  return messagesContainer.value && messagesContainer.value.scrollTop > 100
})

// 部署应用
const deployApp = async () => {
  if (!appId.value) {
    message.error('应用ID不存在')
    return
  }

  deploying.value = true
  try {
    const res = await deployAppApi({
      appId: appId.value as unknown as number,
    })

    if (res.data.code === 0 && res.data.data) {
      deployUrl.value = res.data.data
      deployModalVisible.value = true
      message.success('部署成功')
    } else {
      message.error('部署失败：' + res.data.message)
    }
  } catch (error) {
    console.error('部署失败：', error)
    message.error('部署失败，请重试')
  } finally {
    deploying.value = false
  }
}

// 刷新预览
const refreshPreview = () => {
  if (previewUrl.value) {
    // 强制刷新iframe
    previewRefreshKey.value++
    previewReady.value = false
    
    // 重新开始预览检查
    startPreviewCheck()
    
    message.success('正在刷新预览...')
  }
}

// 在新窗口打开预览
const openInNewTab = () => {
  if (previewUrl.value) {
    window.open(previewUrl.value, '_blank')
  }
}

// 打开部署的网站
const openDeployedSite = () => {
  if (deployUrl.value) {
    window.open(deployUrl.value, '_blank')
  }
}

// iframe加载完成
const onIframeLoad = () => {
  previewReady.value = true
  console.log('预览iframe加载完成')
  
  // 停止预览检查
  stopPreviewCheck()
  
  const iframe = document.querySelector('.preview-iframe') as HTMLIFrameElement
  if (iframe) {
    visualEditor.init(iframe)
    visualEditor.onIframeLoad()
  }
}

// 编辑应用
const editApp = () => {
  if (appInfo.value?.id) {
    router.push(`/app/edit/${appInfo.value.id}`)
  }
}

// 删除应用
const deleteApp = async () => {
  if (!appInfo.value?.id) return

  try {
    const res = await deleteAppApi({ id: appInfo.value.id })
    if (res.data.code === 0) {
      message.success('删除成功')
      appDetailVisible.value = false
      router.push('/')
    } else {
      message.error('删除失败：' + res.data.message)
    }
  } catch (error) {
    console.error('删除失败：', error)
    message.error('删除失败')
  }
}

// 下载代码
const downloadCode = async () => {
  if (!appId.value) {
    message.error('应用ID不存在')
    return
  }
  downloading.value = true
  try {
    const API_BASE_URL = request.defaults.baseURL || ''
    const url = `${API_BASE_URL}/app/download/${appId.value}`
    const response = await fetch(url, {
      method: 'GET',
      credentials: 'include',
    })
    if (!response.ok) {
      throw new Error(`下载失败: ${response.status}`)
    }
    // 获取文件名
    const contentDisposition = response.headers.get('Content-Disposition')
    const fileName = contentDisposition?.match(/filename="(.+)"/)?.[1] || `app-${appId.value}.zip`
    // 下载文件
    const blob = await response.blob()
    const downloadUrl = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = downloadUrl
    link.download = fileName
    link.click()
    // 清理
    URL.revokeObjectURL(downloadUrl)
    message.success('代码下载成功')
  } catch (error) {
    console.error('下载失败：', error)
    message.error('下载失败，请重试')
  } finally {
    downloading.value = false
  }
}

// 可视化编辑相关函数
const toggleEditMode = () => {
  // 检查 iframe 是否已经加载
  const iframe = document.querySelector('.preview-iframe') as HTMLIFrameElement
  if (!iframe) {
    message.warning('请等待页面加载完成')
    return
  }
  // 确保 visualEditor 已初始化
  if (!previewReady.value) {
    message.warning('请等待页面加载完成')
    return
  }
  const newEditMode = visualEditor.toggleEditMode()
  isEditMode.value = newEditMode
}

const clearSelectedElement = () => {
  selectedElementInfo.value = null
  visualEditor.clearSelection()
}

const getInputPlaceholder = () => {
  if (selectedElementInfo.value) {
    return `正在编辑 ${selectedElementInfo.value.tagName.toLowerCase()} 元素，描述您想要的修改...`
  }
  return '请描述你想生成的网站，越详细效果越好哦'
}

// 页面加载时获取应用信息
onMounted(async () => {
  await fetchAppInfo()
  
  // 等待DOM更新完成后滚动到底部
  await nextTick()
  
  // 延迟滚动，确保消息容器和对话历史已渲染
  setTimeout(() => {
    scrollToBottom()
  }, 300)

  // 监听 iframe 消息
  window.addEventListener('message', (event) => {
    visualEditor.handleIframeMessage(event)
  })
})

// 监听对话历史加载状态，确保滚动到底部
watch(historyLoaded, (newValue) => {
  if (newValue && messages.value.length > 0) {
    // 对话历史加载完成后，延迟滚动到底部
    nextTick(() => {
      setTimeout(() => {
        scrollToBottom()
      }, 100)
    })
  }
})

// 清理资源
onUnmounted(() => {
  // EventSource 会在组件卸载时自动清理
  
  // 清理预览检查定时器
  if (previewCheckInterval.value) {
    clearInterval(previewCheckInterval.value)
    previewCheckInterval.value = null
  }
})
</script>

<style scoped>
#appChatPage {
  height: 87vh;
  display: flex;
  flex-direction: column;
  padding: 16px;
  background: #fdfdfd;
}

/* 顶部栏 */
.header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
}

.code-gen-type-tag {
  font-size: 12px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.app-name {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
}

.header-right {
  display: flex;
  gap: 12px;
}

/* 主要内容区域 */
.main-content {
  flex: 1;
  display: flex;
  gap: 16px;
  padding: 8px;
  overflow: hidden;
}

/* 左侧对话区域 */
.chat-section {
  flex: 2;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.messages-container {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  scroll-behavior: smooth;
}

.message-item {
  margin-bottom: 12px;
}

.user-message {
  display: flex;
  justify-content: flex-end;
  align-items: flex-start;
  gap: 12px;
}

.ai-message {
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  gap: 12px;
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.5;
  word-wrap: break-word;
}

.user-message .message-content {
  background: #1890ff;
  color: white;
}

.ai-message .message-content {
  background: #f5f5f5;
  color: #1a1a1a;
  padding: 8px 12px;
}

.message-avatar {
  flex-shrink: 0;
}

.loading-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
}

.loading-dots {
  display: flex;
  gap: 4px;
}

.dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: #1890ff;
  animation: loading-dots 1.4s infinite ease-in-out;
}

.dot:nth-child(1) {
  animation-delay: -0.32s;
}

.dot:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes loading-dots {
  0%, 80%, 100% {
    transform: scale(0);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

/* 加载更多按钮 */
.load-more-container {
  text-align: center;
  padding: 8px 0;
  margin-bottom: 16px;
}

/* 滚动到顶部按钮 */
.scroll-to-top-container {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 10;
}

.scroll-to-top-btn {
  background-color: #1890ff;
  border-color: #1890ff;
  color: white;
}

.scroll-to-top-btn:hover {
  background-color: #40a9ff;
  border-color: #40a9ff;
}

/* 输入区域 */
.input-container {
  padding: 16px;
  background: white;
}

.input-wrapper {
  position: relative;
}

.input-wrapper .ant-input {
  padding-right: 50px;
}

.input-actions {
  position: absolute;
  bottom: 8px;
  right: 8px;
}

/* 右侧预览区域 */
.preview-section {
  flex: 3;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e8e8e8;
}

.preview-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.preview-actions {
  display: flex;
  gap: 8px;
}

.preview-content {
  flex: 1;
  position: relative;
  overflow: hidden;
}

.preview-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #666;
}

.placeholder-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.preview-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #666;
}

.preview-loading p {
  margin-top: 16px;
}

.preview-iframe {
  width: 100%;
  height: 100%;
  border: none;
}

/* 预览检查信息样式 */
.preview-check-info {
  margin-top: 12px;
  text-align: center;
  color: #8c8c8c;
}

.preview-check-info small {
  font-size: 12px;
  background: rgba(24, 144, 255, 0.1);
  padding: 4px 8px;
  border-radius: 12px;
  border: 1px solid rgba(24, 144, 255, 0.2);
}

/* 选中元素信息样式 */
.selected-element-alert {
  margin: 0 16px;
}

.selected-element-info {
  line-height: 1.4;
}

.element-header {
  margin-bottom: 8px;
}

.element-details {
  margin-top: 8px;
}

.element-item {
  margin-bottom: 4px;
  font-size: 13px;
}

.element-item:last-child {
  margin-bottom: 0;
}

.element-tag {
  font-family: 'Monaco', 'Menlo', monospace;
  font-size: 14px;
  font-weight: 600;
  color: #007bff;
}

.element-id {
  color: #28a745;
  margin-left: 4px;
}

.element-class {
  color: #ffc107;
  margin-left: 4px;
}

.element-selector-code {
  font-family: 'Monaco', 'Menlo', monospace;
  background: #f6f8fa;
  padding: 2px 4px;
  border-radius: 3px;
  font-size: 12px;
  color: #d73a49;
  border: 1px solid #e1e4e8;
}

/* 编辑模式按钮样式 */
.edit-mode-active {
  background-color: #52c41a !important;
  border-color: #52c41a !important;
  color: white !important;
}

.edit-mode-active:hover {
  background-color: #73d13d !important;
  border-color: #73d13d !important;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .main-content {
    flex-direction: column;
  }

  .chat-section,
  .preview-section {
    flex: none;
    height: 50vh;
  }
  
  #appChatPage {
    height: 95vh;
  }
}

@media (max-width: 768px) {
  .header-bar {
    padding: 12px 16px;
  }

  .app-name {
    font-size: 16px;
  }

  .main-content {
    padding: 8px;
    gap: 8px;
  }

  .message-content {
    max-width: 85%;
  }
  
  #appChatPage {
    height: 98vh;
    padding: 12px;
  }
}
</style>
