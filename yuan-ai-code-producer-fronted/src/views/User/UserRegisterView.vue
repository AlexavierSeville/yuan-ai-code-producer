<script setup lang="ts">
import { reactive } from 'vue'
import { register } from '@/api/userController.ts'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'

const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})

const router = useRouter();

/**
 * 提交表单
 */
const handleSubmit = async (values: any) => {
  const res = await register(values);
  // 注册成功
  if (res.data.code === 0 && res.data.data){
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
</script>

<template>
  <div id="userRegisterPage">
    <h2 class="title">元仔 AI 应用生成 - 用户注册</h2>
    <div class="desc">代码写不了一点，帮我生成完整应用</div>
    <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSubmit">
      <a-form-item name="userAccount" :rules="[{ required: true, message: '请输入账号' }]">
        <a-input v-model:value="formState.userAccount" placeholder="请输入账号" />
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
          { validator: validateCheckPassword, trigger: 'blur' }
        ]"
      >
        <a-input-password v-model:value="formState.checkPassword" placeholder="请确认密码" />
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
#userRegisterPage{
  max-width: 500px;
  margin: 0 auto;
}
.title{
  text-align: center;
  font-weight: bold;
  font-size: 25px;
  margin-bottom: 17px;
}
.desc{
  text-align: center;
  color: #46b3aa;
  margin-bottom: 17px;
}
.tips{
  text-align: right;
  font-size: 15px;
  margin-bottom: 17px;
}
</style>
