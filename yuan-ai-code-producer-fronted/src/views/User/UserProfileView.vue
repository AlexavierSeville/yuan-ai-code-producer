<template>
  <div class="user-profile-container">
    <a-layout class="profile-layout">
      <a-layout-content class="profile-content">
        <div class="profile-header">
          <h1 class="profile-title">个人信息</h1>
          <p class="profile-subtitle">管理您的个人信息和设置</p>
        </div>

        <div class="profile-body">
          <!-- 用户信息卡片 -->
          <a-card class="profile-card" :bordered="false">
            <template #title>
              <div class="card-title">
                <UserOutlined class="title-icon" />
                <span>基本信息</span>
              </div>
            </template>

            <div class="profile-info">
              <div class="avatar-section">
                <div class="avatar-container">
                  <a-avatar :size="120" :src="userProfile.userAvatar" class="user-avatar">
                    <template #icon>
                      <UserOutlined />
                    </template>
                  </a-avatar>
                  <div class="avatar-overlay" @click="showUploadModal = true">
                    <CameraOutlined class="camera-icon" />
                    <span class="upload-text">更换头像</span>
                  </div>
                </div>
              </div>

              <div class="info-section">
                <div class="info-item">
                  <label class="info-label">用户账号</label>
                  <div class="info-value">{{ userProfile.userAccount }}</div>
                </div>
                <div class="info-item">
                  <label class="info-label">用户昵称</label>
                  <div class="info-value">{{ userProfile.userName || '未设置' }}</div>
                </div>
                <div class="info-item">
                  <label class="info-label">用户角色</label>
                  <a-tag :color="userProfile.userRole === 'admin' ? 'red' : 'blue'">
                    {{ userProfile.userRole === 'admin' ? '管理员' : '普通用户' }}
                  </a-tag>
                </div>
                <div class="info-item">
                  <label class="info-label">注册时间</label>
                  <div class="info-value">{{ formatTime(userProfile.createTime) }}</div>
                </div>
                <div class="info-item">
                  <label class="info-label">最后更新</label>
                  <div class="info-value">{{ formatTime(userProfile.updateTime) }}</div>
                </div>
              </div>
            </div>

            <div class="profile-description">
              <label class="info-label">个人简介</label>
              <div class="description-content">
                {{ userProfile.userProfile || '这个人很懒，什么都没有留下...' }}
              </div>
            </div>

            <div class="action-buttons">
              <a-button type="primary" @click="openEditModal" class="edit-btn">
                <EditOutlined />
                编辑个人信息
              </a-button>
            </div>
          </a-card>

          <!-- 我的应用卡片 -->
          <a-card class="profile-card" :bordered="false">
            <template #title>
              <div class="card-title">
                <AppstoreOutlined class="title-icon" />
                <span>我的应用</span>
              </div>
            </template>

            <div class="apps-section">
              <div v-if="userApps.length === 0" class="empty-apps">
                <a-empty description="暂无应用">
                  <template #image>
                    <AppstoreOutlined style="font-size: 48px; color: #d9d9d9" />
                  </template>
                  <a-button type="primary" @click="goToHome"> 创建应用 </a-button>
                </a-empty>
              </div>
              <div v-else class="apps-grid">
                <div
                  v-for="app in userApps"
                  :key="app.id"
                  class="app-item"
                  @click="goToApp(app.id)"
                >
                  <div class="app-header">
                    <div class="app-icon" :class="getAppIconClass(app)">
                      <component :is="getAppIcon(app)" />
                    </div>
                    <div class="app-status">
                      <div class="status-dot"></div>
                    </div>
                  </div>

                  <div class="app-content">
                    <div class="app-name">{{ app.appName }}</div>
                    <div class="app-desc">{{ app.appDesc || '暂无描述' }}</div>

                    <div class="app-footer">
                      <div class="app-time">
                        <ClockCircleOutlined />
                        {{ formatTime(app.createTime) }}
                      </div>
                      <div class="app-action">
                        <RightOutlined />
                      </div>
                    </div>
                  </div>

                  <div class="app-background">
                    <div class="bg-pattern"></div>
                  </div>
                </div>
              </div>
            </div>
          </a-card>
        </div>
      </a-layout-content>
    </a-layout>

    <!-- 编辑个人信息模态框 -->
    <a-modal
      v-model:open="showEditModal"
      title="编辑个人信息"
      :width="600"
      @ok="handleUpdateProfile"
      @cancel="resetEditForm"
      :confirm-loading="updateLoading"
    >
      <a-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        layout="vertical"
        class="edit-form"
      >
        <a-form-item label="用户昵称" name="userName">
          <a-input v-model:value="editForm.userName" placeholder="请输入用户昵称" />
        </a-form-item>

        <a-form-item label="个人简介" name="userProfile">
          <a-textarea
            v-model:value="editForm.userProfile"
            placeholder="请输入个人简介"
            :rows="4"
            :maxlength="200"
            show-count
          />
        </a-form-item>

        <div class="password-section">
          <a-button type="link" @click="showChangePasswordModal" class="change-password-btn">
            <LockOutlined /> 修改密码
          </a-button>
        </div>
      </a-form>
    </a-modal>

    <!-- 头像上传模态框 -->
    <a-modal
      v-model:open="showUploadModal"
      title="上传头像"
      :width="400"
      @ok="handleUploadAvatar"
      @cancel="resetUploadForm"
      :confirm-loading="uploadLoading"
    >
      <div class="upload-section">
        <a-upload
          v-model:file-list="fileList"
          :before-upload="beforeUpload"
          :show-upload-list="false"
          accept="image/*"
        >
          <div class="upload-area">
            <div v-if="!previewImage" class="upload-placeholder">
              <PlusOutlined class="upload-icon" />
              <div class="upload-text">点击上传头像</div>
              <div class="upload-hint">支持 JPG、PNG、GIF 格式，大小不超过 5MB</div>
            </div>
            <div v-else class="preview-container">
              <img :src="previewImage" alt="预览" class="preview-image" />
            </div>
          </div>
        </a-upload>
      </div>
    </a-modal>

    <!-- 修改密码模态框 -->
    <a-modal
      v-model:open="showPasswordModal"
      title="修改密码"
      :width="400"
      @ok="handleChangePassword"
      @cancel="resetPasswordForm"
      :confirm-loading="passwordLoading"
    >
      <a-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" layout="vertical">
        <a-form-item label="新密码" name="newPassword">
          <a-input-password v-model:value="passwordForm.newPassword" placeholder="请输入新密码" />
        </a-form-item>

        <a-form-item label="确认密码" name="confirmPassword">
          <a-input-password
            v-model:value="passwordForm.confirmPassword"
            placeholder="请再次输入新密码"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  UserOutlined,
  EditOutlined,
  CameraOutlined,
  AppstoreOutlined,
  PlusOutlined,
  LockOutlined,
  RobotOutlined,
  CodeOutlined,
  BulbOutlined,
  ToolOutlined,
  ThunderboltOutlined,
  RocketOutlined,
  ClockCircleOutlined,
  RightOutlined,
} from '@ant-design/icons-vue'
import { getUserProfile, updateUserProfile, uploadUserAvatar } from '@/api/userController'
import { getCurrentUserApps } from '@/api/appController'
import { formatTime } from '@/utils/time'

const router = useRouter()

// 统一错误处理
const handleError = (error: any, defaultMessage: string) => {
  if (error.errorFields) {
    message.error('请检查输入信息')
  } else {
    message.error(defaultMessage)
    console.error(error)
  }
}

// 用户信息
const userProfile = ref({
  id: 0,
  userAccount: '',
  userName: '',
  userAvatar: '',
  userProfile: '',
  userRole: '',
  createTime: '',
  updateTime: '',
})

// 用户应用
const userApps = ref([])

// 编辑模态框
const showEditModal = ref(false)
const updateLoading = ref(false)
const editFormRef = ref()

const editForm = reactive({
  userName: '',
  userProfile: '',
})

const editRules = {
  userName: [{ min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }],
}

// 修改密码模态框
const showPasswordModal = ref(false)
const passwordLoading = ref(false)
const passwordFormRef = ref()

const passwordForm = reactive({
  newPassword: '',
  confirmPassword: '',
})

const passwordRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, message: '密码长度不能少于8位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string) => {
        if (value !== passwordForm.newPassword) {
          return Promise.reject('两次输入的密码不一致')
        }
        return Promise.resolve()
      },
      trigger: 'blur',
    },
  ],
}

// 上传模态框
const showUploadModal = ref(false)
const uploadLoading = ref(false)
const fileList = ref([])
const previewImage = ref('')
const selectedFile = ref<File | null>(null)

// 获取用户信息
const loadUserProfile = async () => {
  try {
    const res = await getUserProfile()
    if (res.data.code === 0) {
      userProfile.value = res.data.data
    } else {
      message.error('获取用户信息失败：' + res.data.message)
    }
  } catch (error) {
    message.error('获取用户信息失败')
    console.error(error)
  }
}

// 获取用户应用
const loadUserApps = async () => {
  try {
    const res = await getCurrentUserApps()
    if (res.data.code === 0) {
      userApps.value = res.data.data || []
    }
  } catch (error) {
    console.error('获取用户应用失败:', error)
  }
}

// 更新个人信息
const handleUpdateProfile = async () => {
  try {
    await editFormRef.value.validate()
    updateLoading.value = true

    const updateData: any = {}

    // 只添加有值的字段
    if (editForm.userName && editForm.userName.trim()) {
      updateData.userName = editForm.userName.trim()
    }
    if (editForm.userProfile && editForm.userProfile.trim()) {
      updateData.userProfile = editForm.userProfile.trim()
    }

    // 检查是否有任何字段需要更新
    if (Object.keys(updateData).length === 0) {
      message.warning('请至少修改一个字段')
      return
    }

    const res = await updateUserProfile(updateData)
    if (res.data.code === 0) {
      message.success('更新成功')
      showEditModal.value = false
      await loadUserProfile()
    } else {
      message.error('更新失败：' + res.data.message)
    }
  } catch (error) {
    handleError(error, '更新失败')
  } finally {
    updateLoading.value = false
  }
}

// 重置编辑表单
const resetEditForm = () => {
  editForm.userName = userProfile.value.userName || ''
  editForm.userProfile = userProfile.value.userProfile || ''
  // 清除表单验证状态
  editFormRef.value?.clearValidate()
}

// 上传前检查
const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('只能上传图片文件!')
    return false
  }
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) {
    message.error('图片大小不能超过 5MB!')
    return false
  }

  // 预览图片
  const reader = new FileReader()
  reader.onload = (e) => {
    previewImage.value = e.target?.result as string
  }
  reader.readAsDataURL(file)

  // 保存原始文件对象
  selectedFile.value = file
  return false // 阻止自动上传
}

// 上传头像
const handleUploadAvatar = async () => {
  if (!selectedFile.value) {
    message.error('请选择要上传的图片')
    return
  }

  try {
    uploadLoading.value = true
    const formData = new FormData()
    formData.append('file', selectedFile.value)

    const res = await uploadUserAvatar(formData)
    if (res.data.code === 0) {
      message.success('头像上传成功')
      showUploadModal.value = false

      // 更新用户头像URL到数据库
      const updateData = {
        userAvatar: res.data.data,
      }
      const updateRes = await updateUserProfile(updateData)
      if (updateRes.data.code === 0) {
        // 更新本地状态
        userProfile.value.userAvatar = res.data.data
        await loadUserProfile()
      } else {
        message.error('头像更新失败：' + updateRes.data.message)
      }
    } else {
      message.error('头像上传失败：' + res.data.message)
    }
  } catch (error: any) {
    if (error.response?.data?.message) {
      message.error('头像上传失败：' + error.response.data.message)
    } else {
      handleError(error, '头像上传失败，请检查网络连接')
    }
  } finally {
    uploadLoading.value = false
  }
}

// 重置上传表单
const resetUploadForm = () => {
  fileList.value = []
  previewImage.value = ''
  selectedFile.value = null
}

// 跳转到应用对话页面
const goToApp = (appId: number) => {
  router.push(`/app/chat/${appId}`)
}

// 获取应用图标
const getAppIcon = (app: any) => {
  const icons = [
    RobotOutlined,
    CodeOutlined,
    BulbOutlined,
    ToolOutlined,
    ThunderboltOutlined,
    RocketOutlined,
    AppstoreOutlined,
  ]
  const index = app.id % icons.length
  return icons[index]
}

// 获取应用图标样式类
const getAppIconClass = (app: any) => {
  const classes = [
    'icon-robot',
    'icon-code',
    'icon-bulb',
    'icon-tool',
    'icon-thunder',
    'icon-rocket',
    'icon-app',
  ]
  const index = app.id % classes.length
  return classes[index]
}

// 跳转到首页
const goToHome = () => {
  router.push('/')
}

// 打开编辑个人信息模态框
const openEditModal = () => {
  resetEditForm() // 预填充表单数据
  showEditModal.value = true
}

// 显示修改密码模态框
const showChangePasswordModal = () => {
  showEditModal.value = false // 关闭编辑个人信息模态框
  showPasswordModal.value = true
}

// 修改密码
const handleChangePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    passwordLoading.value = true

    const updateData = {
      userPassword: passwordForm.newPassword.trim(),
      checkPassword: passwordForm.confirmPassword.trim(),
    }

    const res = await updateUserProfile(updateData)
    if (res.data.code === 0) {
      message.success('密码修改成功')
      showPasswordModal.value = false
      resetPasswordForm()
    } else {
      message.error('密码修改失败：' + res.data.message)
    }
  } catch (error) {
    handleError(error, '密码修改失败')
  } finally {
    passwordLoading.value = false
  }
}

// 重置密码表单
const resetPasswordForm = () => {
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.clearValidate()
}

// 组件挂载时加载数据
onMounted(() => {
  loadUserProfile()
  loadUserApps()
})
</script>

<style scoped>
:root {
  --primary-color: #1890ff;
  --primary-hover: #40a9ff;
  --primary-light: #e3f2fd;
  --primary-shadow: rgba(24, 144, 255, 0.15);
  --primary-shadow-light: rgba(24, 144, 255, 0.2);
}

.user-profile-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.profile-layout {
  background: transparent;
}

.profile-content {
  max-width: 1200px;
  margin: 0 auto;
}

.profile-header {
  text-align: center;
  margin-bottom: 40px;
  color: white;
}

.profile-title {
  font-size: 48px;
  font-weight: 700;
  margin: 0 0 16px 0;
  background: linear-gradient(135deg, #ffffff 0%, #e6e6fa 50%, #dda0dd 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 0 20px rgba(255, 255, 255, 0.3);
}

.profile-subtitle {
  font-size: 18px;
  margin: 0;
  opacity: 0.9;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.3);
}

.profile-body {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.profile-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.profile-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: var(--primary-color);
}

.title-icon {
  font-size: 20px;
}

.profile-info {
  display: flex;
  gap: 40px;
  align-items: flex-start;
  margin-bottom: 24px;
}

.avatar-section {
  flex-shrink: 0;
}

.avatar-container {
  position: relative;
  display: inline-block;
}

.user-avatar {
  border: 4px solid #fff;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.3s ease;
  cursor: pointer;
  color: white;
}

.avatar-container:hover .avatar-overlay {
  opacity: 1;
}

.camera-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.upload-text {
  font-size: 12px;
  font-weight: 500;
}

.info-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 16px;
}

.info-label {
  min-width: 80px;
  font-weight: 500;
  color: #666;
}

.info-value {
  color: #333;
  font-weight: 500;
}

.profile-description {
  margin-bottom: 24px;
}

.description-content {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  border-left: 4px solid var(--primary-color);
  color: #666;
  line-height: 1.6;
  min-height: 60px;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.edit-btn {
  height: 40px;
  padding: 0 24px;
  border-radius: 8px;
  font-weight: 500;
  box-shadow: 0 2px 8px var(--primary-shadow-light);
  transition: all 0.3s ease;
}

.edit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(24, 144, 255, 0.3);
}

.apps-section {
  min-height: 200px;
}

.empty-apps {
  text-align: center;
  padding: 40px 0;
}

.apps-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  padding: 8px;
}

.app-item {
  position: relative;
  display: flex;
  flex-direction: column;
  padding: 24px 20px;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border-radius: 16px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  min-height: 180px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.app-item:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
  border-color: var(--primary-color);
}

.app-item:hover .app-background {
  opacity: 1;
}

.app-item:hover .app-action {
  transform: translateX(4px);
  opacity: 1;
}

.app-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.app-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.app-item:hover .app-icon {
  transform: scale(1.1) rotate(5deg);
}

.app-status {
  position: relative;
}

.status-dot {
  width: 8px;
  height: 8px;
  background: #52c41a;
  border-radius: 50%;
  box-shadow: 0 0 0 2px rgba(82, 196, 26, 0.2);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(82, 196, 26, 0.4);
  }
  70% {
    box-shadow: 0 0 0 6px rgba(82, 196, 26, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(82, 196, 26, 0);
  }
}

.app-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.app-name {
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 8px;
  font-size: 18px;
  line-height: 1.3;
  letter-spacing: -0.02em;
}

.app-desc {
  color: #666;
  font-size: 14px;
  margin-bottom: 16px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  min-height: 42px;
}

.app-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.app-time {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #999;
  font-size: 12px;
  font-weight: 500;
}

.app-action {
  opacity: 0;
  transform: translateX(-4px);
  transition: all 0.3s ease;
  color: var(--primary-color);
  font-size: 14px;
}

.app-background {
  position: absolute;
  top: 0;
  right: 0;
  width: 100px;
  height: 100px;
  opacity: 0.1;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.bg-pattern {
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at 30% 30%, var(--primary-color) 0%, transparent 50%);
  border-radius: 50%;
  transform: translate(20px, -20px);
}

/* 不同图标的样式 */
.icon-robot {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.icon-code {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.icon-bulb {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.icon-tool {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.icon-thunder {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.icon-rocket {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
}

.icon-app {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
}

.edit-form {
  padding: 16px 0;
}

.upload-section {
  text-align: center;
}

.upload-area {
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  padding: 40px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.upload-area:hover {
  border-color: var(--primary-color);
  background: var(--primary-light);
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.upload-icon {
  font-size: 32px;
  color: #d9d9d9;
}

.upload-text {
  font-size: 16px;
  color: #666;
  font-weight: 500;
}

.upload-hint {
  font-size: 12px;
  color: #999;
}

/* 密码相关样式 */
.password-section {
  margin-top: 16px;
  text-align: center;
}

.change-password-btn {
  color: var(--primary-color);
  font-size: 14px;
  padding: 4px 8px;
  height: auto;
  border: none;
  background: transparent;
}

.change-password-btn:hover {
  color: var(--primary-hover);
  background: var(--primary-light);
}

.preview-container {
  display: flex;
  justify-content: center;
}

.preview-image {
  max-width: 200px;
  max-height: 200px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-profile-container {
    padding: 16px;
  }

  .profile-title {
    font-size: 36px;
  }

  .profile-info {
    flex-direction: column;
    gap: 24px;
    align-items: center;
  }

  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .info-label {
    min-width: auto;
  }

  .apps-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .app-item {
    min-height: 160px;
    padding: 20px 16px;
  }

  .app-icon {
    width: 40px;
    height: 40px;
    font-size: 18px;
  }

  .app-name {
    font-size: 16px;
  }

  .app-desc {
    font-size: 13px;
    min-height: 36px;
  }
}

/* 中等屏幕 - 2列 */
@media (min-width: 769px) and (max-width: 1200px) {
  .apps-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* 大屏幕 - 3列 */
@media (min-width: 1201px) {
  .apps-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>
