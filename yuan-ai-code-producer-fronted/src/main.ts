import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';

// 引入字体配置
import '@/assets/fonts.css'

// 引入动画配置
import '@/assets/animations.css'

import '@/access.ts'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(Antd);

app.mount('#app')
