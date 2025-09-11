<template>
  <div id="userManagePage">
    <!-- 搜索表单 -->
    <div class="search-section">
      <h2 class="page-title">用户管理</h2>
      <a-form layout="inline" :model="searchParams" @finish="doSearch" class="search-form">
        <a-form-item label="账号">
          <a-input 
            v-model:value="searchParams.userAccount" 
            placeholder="输入账号" 
            size="large"
            class="search-input"
          />
        </a-form-item>
        <a-form-item label="用户名">
          <a-input 
            v-model:value="searchParams.userName" 
            placeholder="输入用户名" 
            size="large"
            class="search-input"
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" size="large" class="search-btn">
            搜索
          </a-button>
        </a-form-item>
      </a-form>
    </div>
    <!-- 表格 -->
    <div class="table-section">
      <a-table
        :columns="columns"
        :data-source="data"
        :pagination="pagination"
        @change="doTableChange"
        class="user-table"
        size="large"
      >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'userAvatar'">
          <a-image :src="record.userAvatar" :width="120" />
        </template>
        <template v-else-if="column.dataIndex === 'userRole'">
          <div v-if="record.userRole === 'admin'">
            <a-tag color="green">管理员</a-tag>
          </div>
          <div v-else>
            <a-tag color="blue">普通用户</a-tag>
          </div>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-popconfirm title="确定要删除这个用户吗？" @confirm="doDelete(record.id)">
            <a-button danger size="middle" class="action-btn delete-btn">
              删除
            </a-button>
          </a-popconfirm>
        </template>
      </template>
      </a-table>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { deleteUser, listUserVoByPage } from '@/api/userController.ts'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 80,
    fixed: 'left',
    align: 'center',
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
    width: 150,
    ellipsis: true,
  },
  {
    title: '用户名',
    dataIndex: 'userName',
    width: 150,
    ellipsis: true,
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
    width: 120,
    align: 'center',
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
    width: 200,
    ellipsis: true,
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
    width: 120,
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
    width: 120,
    fixed: 'right',
    align: 'center',
  },
]

const data = ref<API.UserVO[]>([])
const total = ref(0)
const searchParams = reactive<API.UserQueryRequest>({
  pageNum: 1,
  pageSize: 20,
})

// 获取数据
const fetchData = async () => {
  const res = await listUserVoByPage({
    ...searchParams,
  })
  if (res.data.data) {
    data.value = res.data.data.records ?? []
    total.value = res.data.data.totalRow ?? 0
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
}

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
const doTableChange = (page: any) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

//搜索
const doSearch = () => {
  // 重置页码
  searchParams.pageNum = 1
  fetchData()
}

// 删除数据
const doDelete = async (id: string) => {
  if (!id) {
    return
  }
  const res = await deleteUser({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    // 刷新数据
    fetchData()
  } else {
    message.error('删除失败')
  }
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
#userManagePage {
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
  content: '👥';
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

.user-table {
  font-size: 15px;
}

.user-table :deep(.ant-table-thead > tr > th) {
  background: #fafafa;
  font-size: 16px;
  font-weight: 700;
  color: #333;
  padding: 18px 16px;
  border-bottom: 2px solid #e8e8e8;
}

.user-table :deep(.ant-table-tbody > tr > td) {
  padding: 18px 16px;
  vertical-align: middle;
  font-size: 15px;
  font-weight: 500;
  border-bottom: 1px solid #f0f0f0;
}

.user-table :deep(.ant-table-tbody > tr:hover > td) {
  background: #f8f9ff;
}

.user-table :deep(.ant-table-tbody > tr:nth-child(even)) {
  background: #fafafa;
}

.user-table :deep(.ant-table-tbody > tr:nth-child(even):hover) {
  background: #f0f2ff;
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

/* 分页样式 */
.user-table :deep(.ant-pagination) {
  margin-top: 24px;
  text-align: center;
}

.user-table :deep(.ant-pagination .ant-pagination-item) {
  font-size: 15px;
  height: 36px;
  min-width: 36px;
}

.user-table :deep(.ant-pagination .ant-pagination-item a) {
  font-size: 15px;
  font-weight: 600;
  line-height: 36px;
}

.user-table :deep(.ant-pagination .ant-pagination-prev),
.user-table :deep(.ant-pagination .ant-pagination-next) {
  font-size: 15px;
  height: 36px;
  min-width: 36px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .search-input {
    width: 160px;
  }
}

@media (max-width: 768px) {
  #userManagePage {
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
  
  .search-input {
    width: 100%;
    margin-bottom: 12px;
  }
}
</style>
