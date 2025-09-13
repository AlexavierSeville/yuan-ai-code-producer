<template>
  <div id="appManagePage">
    <!-- 搜索表单 -->
    <div class="search-section">
      <h2 class="page-title">应用管理</h2>
      <a-form layout="inline" :model="searchParams" @finish="doSearch" class="search-form">
        <a-form-item label="应用名称">
          <a-input
            v-model:value="searchParams.appName"
            placeholder="输入应用名称"
            size="large"
            class="search-input"
          />
        </a-form-item>
        <a-form-item label="创建者">
          <a-input
            v-model:value="searchParams.userId"
            placeholder="输入用户ID"
            size="large"
            class="search-input"
          />
        </a-form-item>
        <a-form-item label="生成类型">
          <a-select
            v-model:value="searchParams.codeGenType"
            placeholder="选择生成类型"
            size="large"
            class="search-select"
          >
            <a-select-option value="">全部</a-select-option>
            <a-select-option
              v-for="option in CODE_GEN_TYPE_OPTIONS"
              :key="option.value"
              :value="option.value"
            >
              {{ option.label }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" size="large" class="search-btn">
            搜索
          </a-button>
        </a-form-item>
      </a-form>
    </div>
    <a-divider />

    <!-- 表格 -->
    <div class="table-section">
      <a-table
        :columns="columns"
        :data-source="data"
        :pagination="pagination"
        @change="doTableChange"
        :scroll="{ x: 1200 }"
        class="app-table"
        size="large"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'cover'">
            <a-image v-if="record.cover" :src="record.cover" :width="80" :height="60" />
            <div v-else class="no-cover">无封面</div>
          </template>
          <template v-else-if="column.dataIndex === 'initPrompt'">
            <a-tooltip :title="record.initPrompt">
              <div class="prompt-text">{{ record.initPrompt }}</div>
            </a-tooltip>
          </template>
          <template v-else-if="column.dataIndex === 'codeGenType'">
            {{ formatCodeGenType(record.codeGenType) }}
          </template>
          <template v-else-if="column.dataIndex === 'priority'">
            <a-tag v-if="record.priority === 99" color="gold">精选</a-tag>
            <span v-else>{{ record.priority || 0 }}</span>
          </template>
          <template v-else-if="column.dataIndex === 'deployedTime'">
            <span v-if="record.deployedTime">
              {{ formatTime(record.deployedTime) }}
            </span>
            <span v-else class="text-gray">未部署</span>
          </template>
          <template v-else-if="column.dataIndex === 'createTime'">
            {{ formatTime(record.createTime) }}
          </template>
          <template v-else-if="column.dataIndex === 'user'">
            <UserInfo :user="record.user" size="small" />
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space size="middle">
              <a-button
                type="primary"
                size="middle"
                @click="editApp(record)"
                class="action-btn edit-btn"
              >
                编辑
              </a-button>
              <a-button
                type="default"
                size="middle"
                @click="toggleFeatured(record)"
                :class="{ 'featured-btn': record.priority === 99 }"
                class="action-btn"
              >
                {{ record.priority === 99 ? '取消精选' : '精选' }}
              </a-button>
              <a-popconfirm title="确定要删除这个应用吗？" @confirm="deleteApp(record.id)">
                <a-button danger size="middle" class="action-btn delete-btn"> 删除 </a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { listAppVoByPageByAdmin, deleteAppByAdmin, updateAppByAdmin } from '@/api/appController'
import { CODE_GEN_TYPE_OPTIONS, formatCodeGenType } from '@/utils/codeGenTypes'
import { formatTime } from '@/utils/time'
import UserInfo from '@/components/UserInfo.vue'

const router = useRouter()

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 90,
    fixed: 'left',
    align: 'center',
  },
  {
    title: '应用名称',
    dataIndex: 'appName',
    width: 180,
    ellipsis: true,
  },
  {
    title: '封面',
    dataIndex: 'cover',
    width: 120,
    align: 'center',
  },
  {
    title: '初始提示词',
    dataIndex: 'initPrompt',
    width: 250,
    ellipsis: true,
  },
  {
    title: '生成类型',
    dataIndex: 'codeGenType',
    width: 120,
    align: 'center',
  },
  {
    title: '优先级',
    dataIndex: 'priority',
    width: 100,
    align: 'center',
  },
  {
    title: '部署时间',
    dataIndex: 'deployedTime',
    width: 180,
    align: 'center',
  },
  {
    title: '创建者',
    dataIndex: 'user',
    width: 150,
    align: 'center',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 180,
    align: 'center',
  },
  {
    title: '操作',
    key: 'action',
    width: 280,
    fixed: 'right',
    align: 'center',
  },
]

// 数据
const data = ref<API.AppVO[]>([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.AppQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 获取数据
const fetchData = async () => {
  try {
    const res = await listAppVoByPageByAdmin({
      ...searchParams,
    })
    if (res.data.data) {
      data.value = res.data.data.records ?? []
      total.value = res.data.data.totalRow ?? 0
    } else {
      message.error('获取数据失败，' + res.data.message)
    }
  } catch (error) {
    console.error('获取数据失败：', error)
    message.error('获取数据失败')
  }
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})

// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.pageNum ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total: number) => `共 ${total} 条`,
  }
})

// 表格变化处理
const doTableChange = (page: { current: number; pageSize: number }) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 搜索
const doSearch = () => {
  // 重置页码
  searchParams.pageNum = 1
  fetchData()
}

// 编辑应用
const editApp = (app: API.AppVO) => {
  router.push(`/app/edit/${app.id}`)
}

// 切换精选状态
const toggleFeatured = async (app: API.AppVO) => {
  if (!app.id) return

  const newPriority = app.priority === 99 ? 0 : 99

  try {
    const res = await updateAppByAdmin({
      id: app.id,
      priority: newPriority,
    })

    if (res.data.code === 0) {
      message.success(newPriority === 99 ? '已设为精选' : '已取消精选')
      // 刷新数据
      fetchData()
    } else {
      message.error('操作失败：' + res.data.message)
    }
  } catch (error) {
    console.error('操作失败：', error)
    message.error('操作失败')
  }
}

// 删除应用
const deleteApp = async (id: number | undefined) => {
  if (!id) return

  try {
    const res = await deleteAppByAdmin({ id })
    if (res.data.code === 0) {
      message.success('删除成功')
      // 刷新数据
      fetchData()
    } else {
      message.error('删除失败：' + res.data.message)
    }
  } catch (error) {
    console.error('删除失败：', error)
    message.error('删除失败')
  }
}
</script>

<style scoped>
#appManagePage {
  padding: 32px;
  background: #f8f9fa;
  min-height: 100vh;
}

/* 搜索区域样式 */
.search-section {
  background: white;
  padding: 32px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px;
}

.page-title {
  font-size: 26px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 28px 0;
  display: flex;
  align-items: center;
}

.page-title::before {
  content: '📱';
  margin-right: 14px;
  font-size: 22px;
}

.search-form {
  margin-top: 16px;
}

.search-form :deep(.ant-form-item-label) {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.search-input {
  width: 200px;
  font-size: 16px;
  height: 40px;
  font-weight: 500;
}

.search-select {
  width: 180px;
  font-size: 16px;
  height: 40px;
  font-weight: 500;
}

.search-btn {
  font-size: 16px;
  font-weight: 600;
  height: 40px;
  padding: 0 24px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(24, 144, 255, 0.2);
}

.search-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(24, 144, 255, 0.3);
}

/* 表格区域样式 */
.table-section {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.app-table {
  font-size: 15px;
}

.app-table :deep(.ant-table-thead > tr > th) {
  background: #fafafa;
  font-size: 16px;
  font-weight: 700;
  color: #333;
  padding: 18px 16px;
  border-bottom: 2px solid #e8e8e8;
}

.app-table :deep(.ant-table-tbody > tr > td) {
  padding: 18px 16px;
  vertical-align: middle;
  font-size: 15px;
  font-weight: 500;
  border-bottom: 1px solid #f0f0f0;
}

.app-table :deep(.ant-table-tbody > tr:hover > td) {
  background: #f8f9ff;
}

.app-table :deep(.ant-table-tbody > tr:nth-child(even)) {
  background: #fafafa;
}

.app-table :deep(.ant-table-tbody > tr:nth-child(even):hover) {
  background: #f0f2ff;
}

/* 封面样式 */
.no-cover {
  width: 100px;
  height: 75px;
  background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 14px;
  border-radius: 8px;
  border: 2px dashed #d9d9d9;
  transition: all 0.3s ease;
}

.no-cover:hover {
  border-color: #1890ff;
  color: #1890ff;
}

/* 提示词文本样式 */
.prompt-text {
  max-width: 250px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 15px;
  line-height: 1.5;
  color: #333;
  font-weight: 500;
}

.text-gray {
  color: #999;
  font-style: italic;
  font-size: 15px;
  font-weight: 500;
}

/* 操作按钮样式 */
.action-btn {
  font-size: 15px;
  font-weight: 600;
  height: 36px;
  padding: 0 18px;
  border-radius: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  border: none;
  color: white !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
  min-width: 70px;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  color: white !important;
}

.action-btn:focus {
  color: white !important;
}

.action-btn:active {
  color: white !important;
}

.edit-btn {
  background: #1890ff !important;
  border-color: #1890ff !important;
  color: white !important;
}

.edit-btn:hover {
  background: #40a9ff !important;
  border-color: #40a9ff !important;
  color: white !important;
}

.delete-btn {
  background: #ff4d4f !important;
  border-color: #ff4d4f !important;
  color: white !important;
}

.delete-btn:hover {
  background: #ff7875 !important;
  border-color: #ff7875 !important;
  color: white !important;
}

.featured-btn {
  background: #faad14 !important;
  border-color: #faad14 !important;
  color: white !important;
  font-weight: 600;
  font-size: 15px;
  height: 36px;
  padding: 0 18px;
  border-radius: 8px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
  min-width: 80px;
}

.featured-btn:hover {
  background: #d48806 !important;
  border-color: #d48806 !important;
  color: white !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(250, 173, 20, 0.3);
}

/* 分页样式 */
.app-table :deep(.ant-pagination) {
  margin-top: 24px;
  text-align: center;
}

.app-table :deep(.ant-pagination .ant-pagination-item) {
  font-size: 15px;
  height: 36px;
  min-width: 36px;
}

.app-table :deep(.ant-pagination .ant-pagination-item a) {
  font-size: 15px;
  font-weight: 600;
  line-height: 36px;
}

.app-table :deep(.ant-pagination .ant-pagination-prev),
.app-table :deep(.ant-pagination .ant-pagination-next) {
  font-size: 15px;
  height: 36px;
  min-width: 36px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .search-input {
    width: 160px;
  }

  .search-select {
    width: 140px;
  }
}

@media (max-width: 768px) {
  #appManagePage {
    padding: 16px;
  }

  .search-section {
    padding: 20px;
  }

  .page-title {
    font-size: 24px;
  }

  .search-form {
    flex-direction: column;
  }

  .search-input,
  .search-select {
    width: 100%;
    margin-bottom: 12px;
  }
}
</style>
