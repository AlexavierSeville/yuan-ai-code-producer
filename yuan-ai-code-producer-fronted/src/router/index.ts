import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import UserRegisterView from '@/views/User/UserRegisterView.vue'
import UserLoginView from '@/views/User/UserLoginView.vue'
import UserProfileView from '@/views/User/UserProfileView.vue'
import UserManageView from '@/views/admin/UserManageView.vue'
import AppManageView from '@/views/admin/AppManageView.vue'
import AppChatView from '@/views/app/AppChatView.vue'
import AppEditView from '@/views/app/AppEditView.vue'
import ChatManageView from '@/views/admin/ChatManageView.vue'
import TutorialPage from '@/views/FunnyPages/TutorialPage.vue'
import SupportPage from '@/views/FunnyPages/SupportPage.vue'
import FeedbackPage from '@/views/FunnyPages/FeedbackPage.vue'

const router = createRouter({
  history: createWebHistory('/'),
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
      path: '/user/profile',
      name: '个人信息',
      component: UserProfileView,
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
      path: '/admin/chatManage',
      name: '对话管理',
      component: ChatManageView,
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
    {
      path: '/funny/tutorial',
      name: '使用教程',
      component: TutorialPage,
    },
    {
      path: '/funny/support',
      name: '客服支持',
      component: SupportPage,
    },
    {
      path: '/funny/feedback',
      name: '邮件反馈',
      component: FeedbackPage,
    },
  ],
})

export default router
