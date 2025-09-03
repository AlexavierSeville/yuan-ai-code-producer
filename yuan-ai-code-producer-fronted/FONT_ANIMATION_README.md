# 字体和动画优化说明

## 字体优化

### 字体系统
项目使用了现代化的字体系统，包括：

- **主要字体**: Inter (英文数字)
- **中文字体**: Noto Sans SC (思源黑体)
- **代码字体**: JetBrains Mono

### 字体变量
使用CSS变量统一管理字体：

```css
:root {
  --font-primary: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  --font-chinese: 'Noto Sans SC', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  --font-mono: 'JetBrains Mono', 'Fira Code', 'Consolas', 'Monaco', 'Courier New', monospace;
}
```

### 字体大小
响应式字体大小系统：

```css
:root {
  --font-size-xs: 0.75rem;    /* 12px */
  --font-size-sm: 0.875rem;   /* 14px */
  --font-size-base: 1rem;     /* 16px */
  --font-size-lg: 1.125rem;   /* 18px */
  --font-size-xl: 1.25rem;    /* 20px */
  --font-size-2xl: 1.5rem;    /* 24px */
  --font-size-3xl: 1.875rem;  /* 30px */
  --font-size-4xl: 2.25rem;   /* 36px */
  --font-size-5xl: 3rem;      /* 48px */
}
```

### 使用示例
```css
.title {
  font-family: var(--font-primary);
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-semibold);
}

.content {
  font-family: var(--font-chinese);
  font-size: var(--font-size-base);
  line-height: var(--line-height-relaxed);
}
```

## 动画系统

### 动画变量
```css
:root {
  --animation-duration-fast: 0.2s;
  --animation-duration-normal: 0.3s;
  --animation-duration-slow: 0.5s;
  --animation-duration-slower: 0.8s;
  
  --animation-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  --animation-timing-function-bounce: cubic-bezier(0.68, -0.55, 0.265, 1.55);
  --animation-timing-function-elastic: cubic-bezier(0.175, 0.885, 0.32, 1.275);
}
```

### 基础动画类

#### 淡入动画
```css
.fade-in          /* 淡入 */
.fade-in-up       /* 淡入向上 */
.fade-in-down     /* 淡入向下 */
.fade-in-left     /* 淡入向左 */
.fade-in-right    /* 淡入向右 */
```

#### 缩放动画
```css
.scale-in         /* 缩放进入 */
.bounce-in        /* 弹跳进入 */
```

#### 滑入动画
```css
.slide-in-up      /* 向上滑入 */
.slide-in-down    /* 向下滑入 */
.slide-in-left    /* 向左滑入 */
.slide-in-right   /* 向右滑入 */
```

#### 特殊动画
```css
.rotate-in        /* 旋转进入 */
.flip-in-x        /* X轴翻转 */
.glow             /* 光晕效果 */
.breath           /* 呼吸效果 */
.pulse            /* 脉冲效果 */
.swing            /* 摇摆效果 */
.shake            /* 抖动效果 */
.wave             /* 波浪效果 */
.float            /* 浮动效果 */
```

### 悬停效果类
```css
.hover-lift       /* 悬停时上移 */
.hover-scale      /* 悬停时缩放 */
.hover-rotate     /* 悬停时旋转 */
```

### 动画控制类

#### 延迟
```css
.delay-100        /* 延迟0.1s */
.delay-200        /* 延迟0.2s */
.delay-300        /* 延迟0.3s */
.delay-400        /* 延迟0.4s */
.delay-500        /* 延迟0.5s */
```

#### 持续时间
```css
.duration-fast    /* 快速动画 */
.duration-normal  /* 正常动画 */
.duration-slow    /* 慢速动画 */
.duration-slower  /* 更慢动画 */
```

#### 迭代次数
```css
.animate-infinite /* 无限循环 */
.animate-once     /* 执行一次 */
.animate-twice    /* 执行两次 */
```

#### 方向
```css
.animate-reverse  /* 反向 */
.animate-alternate /* 交替 */
.animate-alternate-reverse /* 交替反向 */
```

### 使用示例

#### 基础使用
```html
<div class="fade-in-up">
  这个元素会从下方淡入
</div>

<button class="hover-lift">
  悬停时会向上移动
</button>
```

#### 组合使用
```html
<div class="fade-in-up delay-200 duration-slow">
  延迟0.2秒，慢速淡入向上
</div>

<div class="bounce-in animate-infinite">
  无限弹跳效果
</div>
```

#### 自定义动画
```css
.custom-animation {
  animation: customKeyframe 1s ease-in-out infinite;
}

@keyframes customKeyframe {
  0% { transform: scale(1); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}
```

## Header动态效果

### 主要特性
- **Logo动画**: 呼吸效果 + 悬停缩放旋转
- **标题渐变**: 线性渐变文字 + 下划线动画
- **菜单动画**: 悬停时上移 + 下划线展开
- **用户区域**: 悬停时背景变化 + 头像缩放
- **整体效果**: 悬停时阴影增强 + 轻微上移

### 动画细节
```css
/* Logo呼吸动画 */
.logo {
  animation: logoBreath 3s ease-in-out infinite;
}

/* 悬停时Logo效果 */
.header-left:hover .logo {
  transform: scale(1.05) rotate(2deg);
  filter: drop-shadow(0 4px 16px rgba(24, 144, 255, 0.4));
}

/* 标题下划线动画 */
.title::after {
  width: 0;
  transition: width 0.3s ease;
}

.header-left:hover .title::after {
  width: 100%;
}
```

## 响应式设计

### 字体响应式
```css
@media (max-width: 768px) {
  :root {
    --font-size-5xl: 2.5rem;   /* 40px */
    --font-size-4xl: 2rem;     /* 32px */
    --font-size-3xl: 1.75rem;  /* 28px */
  }
}
```

### 动画响应式
```css
@media (prefers-reduced-motion: reduce) {
  * {
    animation-duration: 0.01ms !important;
    transition-duration: 0.01ms !important;
  }
}
```

## 性能优化

### 字体优化
- 使用 `font-display: swap` 优化字体加载
- 字体子集化减少文件大小
- 本地字体回退策略

### 动画优化
- 使用 `transform` 和 `opacity` 进行动画
- 避免重排重绘的属性
- 硬件加速支持
- 动画暂停/恢复控制

## 浏览器兼容性

### 支持的浏览器
- Chrome 60+
- Firefox 55+
- Safari 12+
- Edge 79+

### 降级策略
- 不支持CSS变量的浏览器使用默认值
- 不支持backdrop-filter的浏览器使用半透明背景
- 不支持CSS动画的浏览器显示静态内容

## 自定义配置

### 修改字体
在 `src/assets/fonts.css` 中修改字体变量：

```css
:root {
  --font-primary: 'Your Font', sans-serif;
  --font-chinese: 'Your Chinese Font', sans-serif;
}
```

### 修改动画
在 `src/assets/animations.css` 中修改动画变量：

```css
:root {
  --animation-duration-normal: 0.4s;
  --animation-timing-function: ease-out;
}
```

### 添加新动画
```css
@keyframes yourAnimation {
  0% { /* 起始状态 */ }
  100% { /* 结束状态 */ }
}

.your-animation {
  animation: yourAnimation 1s ease-in-out;
}
```

## 最佳实践

1. **适度使用**: 动画应该增强用户体验，而不是干扰
2. **性能优先**: 优先使用CSS动画，避免JavaScript动画
3. **可访问性**: 尊重用户的动画偏好设置
4. **一致性**: 保持动画风格的一致性
5. **测试**: 在不同设备和浏览器上测试动画效果

## 故障排除

### 常见问题
1. **字体不显示**: 检查网络连接和字体文件路径
2. **动画卡顿**: 检查是否使用了性能较差的CSS属性
3. **兼容性问题**: 检查浏览器版本和CSS支持情况

### 调试技巧
1. 使用浏览器开发者工具检查CSS
2. 临时禁用动画进行对比
3. 检查控制台错误信息
4. 使用性能分析工具分析动画性能
