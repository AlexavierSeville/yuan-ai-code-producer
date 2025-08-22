<script setup lang="ts">
import { reactive } from 'vue'
import {userLogin} from '@/api/userController.ts'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { message } from 'ant-design-vue'

const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})

const router = useRouter();
const loginUserStore = useLoginUserStore();

/**
 * 提交表单
 */
const handleSubmit = async (values: any) => {
  const res = await userLogin(values);
  // 登录成功
  if (res.data.code === 0 && res.data.data){
    await loginUserStore.fetchLoginUser();
    message.success('登录成功')
    router.push({
      path: '/',
      replace: true,
    })
  } else {
    message.error('登录失败' + res.data.message)
  }
}
</script>

<template>
  <div id="userLoginPage">
    <h2 class="title">元仔 AI 应用生成 - 用户登录</h2>
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
      <div class="tips">
        没有账号？
        <RouterLink to="/user/register">去注册</RouterLink>
      </div>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">登录</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<style scoped>
#userLoginPage{
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
