import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import UserRegisterView from '@/views/User/UserRegisterView.vue'
import UserLoginView from '@/views/User/UserLoginView.vue'
import UserManageView from '@/views/admin/UserManageView.vue'
import AppManageView from '@/views/admin/AppManageView.vue'
import AppChatView from '@/views/app/AppChatView.vue'
import AppEditView from '@/views/app/AppEditView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/user/register',
      name: '用户注册',
      component: UserRegisterView,
    },
    {
      path: '/user/login',
      name: '用户登录',
      component: UserLoginView,
    },
    {
      path: '/admin/userManage',
      name: '用户管理',
      component: UserManageView,
    },
    {
      path: '/admin/appManage',
      name: '应用管理',
      component: AppManageView,
    },
    {
      path: '/app/chat/:id',
      name: '应用对话',
      component: AppChatView,
    },
    {
      path: '/app/edit/:id',
      name: '编辑应用',
      component: AppEditView,
    },
  ],
})

export default router
