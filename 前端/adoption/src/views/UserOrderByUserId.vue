<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span class="page-title">我的订单管理</span>
        <div class="operation-group">
          <div class="search-group">
            <el-input 
              v-model="searchKeyword" 
              placeholder="搜索订单号/订单状态..." 
              :prefix-icon="Search"
              class="search-input"
              clearable
              @keyup.enter="handleSearch"
            />
            <el-button 
              type="primary" 
              :icon="Search"
              @click="handleSearch"
              class="search-btn"
            >
              搜索
            </el-button>
            <el-button 
              type="default" 
              :icon="RefreshLeft"
              @click="resetSearch"
              class="reset-btn"
            >
              重置
            </el-button>
          </div>
        </div>
      </div>
    </template>

    <!-- 表格展示订单数据 -->
    <el-table 
      :data="filteredOrders" 
      style="width: 100%" 
      border 
      stripe
      @row-click="handleRowClick"
      :row-class-name="tableRowClassName"
    >
      <el-table-column label="序号" width="100" type="index" align="center" />
      <el-table-column label="订单ID" prop="orderId" align="center" />
      <el-table-column label="用户ID" prop="userId" align="center" />
      <el-table-column label="用户名" prop="userName" align="center" />
      <el-table-column label="联系电话" prop="userPhone" align="center" />
      <el-table-column label="订单金额(元)" prop="totalAmount" align="center">
        <template #default="{ row }">
          {{ row.totalAmount.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column label="订单状态" width="120" align="center">
        <template #default="{ row }">
          <el-tag 
            :type="getStatusType(row.status)" 
            effect="light"
            class="status-tag"
          >
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" align="center" width="180" />
      <el-table-column label="订单备注" prop="remark" align="center" show-overflow-tooltip />
      <template #empty>
        <el-empty description="暂无订单信息" />
      </template>
    </el-table>
    
    <!-- 选中订单的详情展示区域 - 仅保留订单信息 -->
    <div v-if="selectedOrder" class="order-details-section">
      <div class="section-title">
        <Box class="icon-title" />
        <span>订单 {{ selectedOrder.orderId }} 详情</span>
      </div>
      
      <div class="detail-content">
        <!-- 订单基本信息卡片 -->
        <div class="detail-card">
          <div class="detail-card__title">
            <CollectionTag class="icon-title" />
            <span>订单信息</span>
          </div>
          <div class="detail-card__content">
            <div class="detail-item">
              <span class="detail-label">订单金额：</span>
              <span class="detail-value">
                <Money  class="icon-small" />
                {{ selectedOrder.totalAmount.toFixed(2) }} 元
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">订单状态：</span>
              <span class="detail-value">
                <el-tag :type="getStatusType(selectedOrder.status)" effect="light" class="status-tag">
                  {{ selectedOrder.status }}
                </el-tag>
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">创建时间：</span>
              <span class="detail-value">
                <Clock class="icon-small" />
                {{ selectedOrder.createTime }}
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">备注信息：</span>
              <span class="detail-value">{{ selectedOrder.remark || '无备注信息' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">用户ID：</span>
              <span class="detail-value">{{ selectedOrder.userId }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">用户名：</span>
              <span class="detail-value">{{ selectedOrder.userName }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">联系电话：</span>
              <span class="detail-value">{{ selectedOrder.userPhone }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="no-selection-hint">
      请点击订单行查看详情
    </div>

    <!-- 分页条 -->
    <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[1, 3, 5]"
      layout="jumper, total, sizes, prev, pager, next" background :total="total" @size-change="onSizeChange"
      @current-change="onCurrentChange" style="margin-top: 20px; justify-content: flex-end" />
  </el-card>
</template>

<script setup>
import {
  Search,
  RefreshLeft,
  Money,
  Clock,
  CollectionTag,
  Box
} from '@element-plus/icons-vue'
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

// 导入订单管理相关接口
import { 
  selectOrderById,
  searchOrderService 
} from '@/api/orderManage'

// 导入Pinia store
import { useUserInfoStore } from '@/stores/user'
import { useRouter } from 'vue-router'

// 路由实例
const router = useRouter()

// 用户信息store
const userStore = useUserInfoStore()

// 分页条数据模型
const pageNum = ref(1) // 当前页码
const pageSize = ref(5) // 每页条数
const total = ref(0) // 总条数

// 订单列表数据存储
const orders = ref([])
// 搜索关键词
const searchKeyword = ref('')

// 选中的订单
const selectedOrder = ref(null)

// 过滤后的订单列表（用于搜索功能）
const filteredOrders = computed(() => {
  const keyword = searchKeyword.value.trim().toLowerCase()
  if (!keyword) {
    return orders.value
  }
  
  return orders.value.filter(order => {
    return (
      order.orderId?.toString().includes(keyword) ||
      order.userName?.toLowerCase().includes(keyword) ||
      order.userPhone?.includes(keyword) ||
      order.status?.toLowerCase().includes(keyword)
    )
  })
})

// 每页条数改变
const onSizeChange = (size) => {
  pageSize.value = size
  getOrdersByUserId()
}

// 当前页码改变
const onCurrentChange = (num) => {
  pageNum.value = num
  getOrdersByUserId()
}

/**
 * 根据当前登录用户ID获取订单列表
 */
const getOrdersByUserId = async () => {
  // 检查用户是否登录
  if (!userStore.info?.userId) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    const result = await selectOrderById(userStore.info.userId)
    if (result.code === 0) {
      orders.value = result.data || []
      total.value = orders.value.length
    } else {
      orders.value = []
      total.value = 0
      ElMessage.warning('未获取到订单数据')
    }
  } catch (error) {
    ElMessage.error('获取订单列表失败，请重试')
    console.error('订单列表接口异常:', error)
  }
}

/**
 * 页面初始化时加载订单列表
 */
onMounted(() => {
  getOrdersByUserId()
})

/**
 * 搜索订单
 */
const handleSearch = () => {
  // 重置选中状态
  selectedOrder.value = null
  ElMessage.success('搜索完成')
}

/**
 * 重置搜索
 */
const resetSearch = () => {
  searchKeyword.value = ''
  // 重置选中状态
  selectedOrder.value = null
  ElMessage.success('已重置搜索条件')
}

/**
 * 处理订单行点击事件，展示订单详情
 */
const handleRowClick = async (row) => {
  try {
    // 复制订单基本信息
    const orderData = JSON.parse(JSON.stringify(row))
    
    // 更新选中的订单数据
    selectedOrder.value = orderData
  } catch (error) {
    console.error('处理订单点击事件出错:', error)
    ElMessage.error('加载订单详情失败')
  }
}

/**
 * 表格行样式 - 用于标记选中状态
 */
const tableRowClassName = ({ row }) => {
  if (selectedOrder.value && selectedOrder.value.orderId === row.orderId) {
    return 'selected-row'
  }
  return ''
}

/**
 * 订单状态样式映射
 */
const getStatusType = (status) => {
  switch (status) {
    case '已支付':
    case '已完成':
      return 'success'
    case '已取消':
      return 'danger'
    case '待支付':
      return 'warning'
    default:
      return 'info'
  }
}
</script>

<style lang="scss" scoped>
.page-container {
  min-height: 100%;
  box-sizing: border-box;
  padding: 20px;

  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;

    .page-title {
      font-size: 18px;
      font-weight: 600;
      color: #333;
      line-height: 1;
    }

    .operation-group {
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .search-group {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .search-input {
      width: 260px;
      border-radius: 8px;
      border: 1px solid #ffccd5;
      
      .el-icon-search {
        color: #ff6b81;
      }
      
      &:focus-within {
        border-color: #ff6b81;
        box-shadow: 0 0 0 2px rgba(255, 107, 129, 0.2);
      }
    }

    .search-btn {
      background-color: #ff6b81 !important;
      border-color: #ff6b81 !important;
      color: #fff !important;
      border-radius: 8px;
      
      &:hover {
        background-color: #ff4d6d !important;
        border-color: #ff4d6d !important;
      }
    }

    .reset-btn {
      border-color: #ffb3c1 !important;
      color: #ff6b81 !important;
      border-radius: 8px;
      
      &:hover {
        background-color: #fff5f7 !important;
      }
    }
  }

  /* 表格样式 */
  .el-table {
    margin-bottom: 20px;
  }
  
  .selected-row {
    background-color: #fff5f7 !important;
  }
  
  .el-table__row:hover > td {
    background-color: #fff0f3 !important;
  }

  /* 订单详情区域样式 */
  .order-details-section {
    margin: 20px 0;
    padding: 15px;
    border-radius: 10px;
    background-color: #fff;
    border: 1px solid #f5f5f5;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }
  
  .section-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 600;
    color: #ff6b81;
    margin-bottom: 15px;
    padding-bottom: 10px;
    border-bottom: 2px solid #ffe6ea;
    
    .icon-title {
      width: 20px;
      height: 20px;
    }
  }
  
  .no-selection-hint {
    text-align: center;
    padding: 40px;
    color: #999;
    background-color: #f9f9f9;
    border-radius: 10px;
    margin: 20px 0;
  }

  .detail-content {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .detail-card {
    background-color: #fff;
    border-radius: 10px;
    border: 1px solid #f5f5f5;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    transition: all 0.3s ease;
    
    &:hover {
      box-shadow: 0 4px 12px rgba(255, 107, 129, 0.1);
      border-color: #ffe6ea;
    }
    
    &__title {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px 16px;
      border-bottom: 1px solid #f5f5f5;
      font-weight: 500;
      color: #555;
      
      .icon-title {
        width: 18px;
        height: 18px;
        color: #ff6b81;
      }
    }
    
    &__content {
      padding: 16px;
    }
  }

  .detail-item {
    display: flex;
    margin-bottom: 12px;
    line-height: 28px;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .detail-label {
      width: 100px;
      color: #888;
      text-align: right;
      padding-right: 16px;
      flex-shrink: 0;
    }
    
    .detail-value {
      flex: 1;
      color: #333;
      display: flex;
      align-items: center;
      gap: 6px;
      
      .icon-small {
        width: 16px;
        height: 16px;
        color: #ff8da1;
      }
    }
  }

  .status-tag {
    padding: 2px 8px;
    border-radius: 4px;
  }
}
</style>