<script setup lang="ts">
import { reactive, ref } from 'vue'
import { userRegister, sendVerificationCode } from '@/api/userController.ts'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'

const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
  verificationCode: '',
})

const router = useRouter()

// 验证码相关状态
const countdown = ref(0)
const isSendingCode = ref(false)
let countdownTimer: NodeJS.Timeout | null = null

/**
 * 提交表单
 */
const handleSubmit = async (values: any) => {
  const res = await userRegister(values)
  // 注册成功
  if (res.data.code === 0 && res.data.data) {
    message.success('注册成功，请登录')
    router.push({
      path: '/user/login',
      replace: true,
    })
  } else {
    message.error('注册失败：' + res.data.message)
  }
}

/**
 * 自定义密码确认验证
 */
const validateCheckPassword = async (_rule: any, value: string) => {
  if (value && value !== formState.userPassword) {
    throw new Error('两次输入的密码不一致')
  }
}

/**
 * 验证邮箱格式
 */
const validateEmail = (email: string) => {
  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
  return emailRegex.test(email)
}

/**
 * 发送验证码
 */
const handleSendVerificationCode = async () => {
  if (!formState.userAccount) {
    message.error('请先输入邮箱地址')
    return
  }

  if (!validateEmail(formState.userAccount)) {
    message.error('请输入有效的邮箱地址')
    return
  }

  if (countdown.value > 0) {
    message.warning(`请等待 ${countdown.value} 秒后再发送`)
    return
  }

  try {
    isSendingCode.value = true
    const res = await sendVerificationCode({
      email: formState.userAccount,
      codeType: 'REGISTER',
    })

    if (res.data.code === 0) {
      message.success('验证码已发送，请查收邮件')
      startCountdown()
    } else {
      message.error('发送失败：' + res.data.message)
    }
  } catch (error: any) {
    message.error('发送失败：' + (error.response?.data?.message || error.message))
  } finally {
    isSendingCode.value = false
  }
}

/**
 * 开始倒计时
 */
const startCountdown = () => {
  countdown.value = 180 // 3分钟 = 180秒
  countdownTimer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(countdownTimer!)
      countdownTimer = null
    }
  }, 1000)
}

/**
 * 清理定时器
 */
const cleanup = () => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
}

// 组件卸载时清理定时器
import { onUnmounted } from 'vue'
onUnmounted(() => {
  cleanup()
})
</script>

<template>
  <div id="userRegisterPage">
    <h2 class="title">元仔 AI 应用生成 - 用户注册</h2>
    <div class="desc">代码写不了一点，帮我生成完整应用</div>
    <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSubmit">
      <a-form-item
        name="userAccount"
        :rules="[
          { required: true, message: '请输入邮箱地址' },
          {
            pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
            message: '请输入有效的邮箱地址',
          },
        ]"
      >
        <a-input v-model:value="formState.userAccount" placeholder="请输入邮箱地址" />
      </a-form-item>
      <a-form-item
        name="userPassword"
        :rules="[
          { required: true, message: '请输入密码' },
          { min: 8, message: '密码不能小于 8 位' },
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" placeholder="请输入密码" />
      </a-form-item>
      <a-form-item
        name="checkPassword"
        :rules="[
          { required: true, message: '请确认密码' },
          { min: 8, message: '密码不能小于 8 位' },
          { validator: validateCheckPassword, trigger: 'blur' },
        ]"
      >
        <a-input-password v-model:value="formState.checkPassword" placeholder="请确认密码" />
      </a-form-item>
      <a-form-item
        name="verificationCode"
        :rules="[
          { required: true, message: '请输入验证码' },
          { len: 6, message: '验证码为6位数字' },
        ]"
      >
        <a-input-group compact>
          <a-input
            v-model:value="formState.verificationCode"
            placeholder="请输入验证码"
            style="width: calc(100% - 120px)"
            maxlength="6"
          />
          <a-button
            type="primary"
            :disabled="countdown > 0 || isSendingCode"
            :loading="isSendingCode"
            @click="handleSendVerificationCode"
            style="width: 120px"
          >
            {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
          </a-button>
        </a-input-group>
      </a-form-item>
      <div class="tips">
        已有账号？
        <RouterLink to="/user/login">去登录</RouterLink>
      </div>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">注册</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<style scoped>
#userRegisterPage {
  max-width: 500px;
  margin: 0 auto;
}
.title {
  text-align: center;
  font-weight: bold;
  font-size: 25px;
  margin-bottom: 17px;
}
.desc {
  text-align: center;
  color: #46b3aa;
  margin-bottom: 17px;
}
.tips {
  text-align: right;
  font-size: 15px;
  margin-bottom: 17px;
}
</style>
