<script setup>
import {
  Search,
  RefreshLeft
} from '@element-plus/icons-vue'
import { ref, onMounted } from 'vue'
import { ElMessage, ElDialog } from 'element-plus'

// 导入接口
import { getReportList, searchReportService } from '@/api/petsReport'

// 分页相关
const pageNum = ref(1)
const pageSize = ref(6)
const total = ref(0)

// 上报列表数据
const reports = ref([])
const searchKeyword = ref('')

// 弹窗相关
const dialogVisible = ref(false)
const currentReport = ref(null) // 当前选中的上报信息

// 初始化加载
onMounted(() => {
  getAllReports()
})

// 加载上报列表
const getAllReports = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    const result = await getReportList(params)
    if (result.code === 0) {
      reports.value = result.data.items
      total.value = result.data.total
    } else {
      ElMessage.error('获取上报信息失败')
    }
  } catch (error) {
    ElMessage.error('网络请求异常')
    console.error(error)
  }
}

// 搜索功能
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    getAllReports()
    return
  }
  try {
    const result = await searchReportService(searchKeyword.value.trim())
    if (result.code === 0) {
      reports.value = result.data
      total.value = result.data.length
      if (result.data.length === 0) {
        ElMessage.info('未找到匹配的上报信息')
      }
    } else {
      ElMessage.error('搜索失败，请重试')
    }
  } catch (error) {
    ElMessage.error('搜索接口异常')
    console.error(error)
  }
}

// 重置搜索
const resetSearch = () => {
  searchKeyword.value = ''
  getAllReports()
  ElMessage.success('已重置为全部上报信息')
}

// 分页事件
const onSizeChange = (size) => {
  pageSize.value = size
  getAllReports()
}

const onCurrentChange = (num) => {
  pageNum.value = num
  getAllReports()
}

// 图片路径处理
const getImageUrl = (path) => {
  if (path?.startsWith('/assets/')) {
    try {
      return new URL(`../assets${path}`, import.meta.url).href
    } catch (e) {
      console.error('无法加载图片:', path)
      return new URL('@/assets/b.png', import.meta.url).href
    }
  }
  return path || new URL('@/assets/b.png', import.meta.url).href
}

// 打开上报详情弹窗
const openReportDetail = (report) => {
  currentReport.value = { ...report }
  dialogVisible.value = true
}

// 关闭弹窗的方法
const handleDialogClose = () => {
  dialogVisible.value = false
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>
<template>
  <div class="user-animal-report-page">
    <div class="page-header">
      <h2 class="page-title">流浪宠物上报信息</h2>
      <div class="search-group">
        <el-input 
          v-model="searchKeyword" 
          placeholder="搜索宠物名称/发现地点..." 
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

    <!-- 卡片式展示流浪宠物上报信息 -->
    <div class="report-cards-container">
      <div 
        class="report-card" 
        v-for="report in reports" 
        :key="report.reportId"
        @click="openReportDetail(report)"
        style="cursor: pointer"
      >
        <div class="report-card__header">
          <div class="report-img-wrapper">
            <img 
              :src="getImageUrl(report.reportImage)" 
              alt="宠物照片" 
              class="report-card__img"
            >
          </div>
          <el-tag 
            type="info" 
            effect="light"
            class="report-tag"
          >
            上报于 {{ formatDate(report.createDate) }}
          </el-tag>
        </div>
        
        <div class="report-card__body">
          <h3 class="report-card__name">{{ report.petName }}</h3>
          <div class="report-card__info">
            <div class="info-item">
              <span class="info-label">种类：</span>
              <span class="info-value">{{ report.petType }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">性别：</span>
              <span class="info-value">{{ report.petGender }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">年龄：</span>
              <span class="info-value">{{ report.petAge }}岁</span>
            </div>
            <div class="info-item">
              <span class="info-label">发现地点：</span>
              <span class="info-value">{{ report.findLocation }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-if="reports.length === 0" class="empty-state">
        <el-empty description="暂无流浪宠物上报信息" />
      </div>
    </div>

    <!-- 分页条 -->
    <el-pagination 
      v-model:current-page="pageNum" 
      v-model:page-size="pageSize" 
      :page-sizes="[3, 6, 9]"
      layout="jumper, total, sizes, prev, pager, next" 
      background 
      :total="total" 
      @size-change="onSizeChange"
      @current-change="onCurrentChange" 
      class="pagination"
    />

    <!-- 上报详情弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="流浪宠物上报详情"
      :width="600"
      @close="handleDialogClose"
    >
      <div class="report-detail">
        <div class="detail-image">
          <img 
            :src="getImageUrl(currentReport?.reportImage)" 
            alt="宠物照片" 
            class="detail-img"
          >
        </div>
        <div class="detail-info">
          <div class="info-row">
            <span class="label">宠物名称：</span>
            <span class="value">{{ currentReport?.petName || '未知' }}</span>
          </div>
          <div class="info-row">
            <span class="label">宠物种类：</span>
            <span class="value">{{ currentReport?.petType || '未知' }}</span>
          </div>
          <div class="info-row">
            <span class="label">性别：</span>
            <span class="value">{{ currentReport?.petGender || '未知' }}</span>
          </div>
          <div class="info-row">
            <span class="label">年龄：</span>
            <span class="value">{{ currentReport?.petAge || '未知' }}岁</span>
          </div>
          <div class="info-row">
            <span class="label">发现地点：</span>
            <span class="value">{{ currentReport?.findLocation || '未知' }}</span>
          </div>
          <div class="info-row">
            <span class="label">联系方式：</span>
            <span class="value">{{ currentReport?.contactInfo || '未知' }}</span>
          </div>
          <div class="info-row">
            <span class="label">上报时间：</span>
            <span class="value">{{ formatDate(currentReport?.createDate) || '未知' }}</span>
          </div>
          <div class="info-row full-width">
            <span class="label">详细描述：</span>
            <div class="value full-content">{{ currentReport?.reportDesc || '暂无详细描述' }}</div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>



<style lang="scss" scoped>
.user-animal-report-page {
  background-color: #fff5f7;
  min-height: 100vh;
  padding: 20px;
  box-sizing: border-box;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ffccd5;

  .page-title {
    font-size: 20px;
    font-weight: 700;
    color: #333;
  }

  .search-group {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .search-input {
    width: 280px;
    border-radius: 8px;
    border: 1px solid #ffccd5;
    background-color: #fff;
    
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
    padding: 8px 16px;
    
    &:hover {
      background-color: #ff4d6d !important;
      border-color: #ff4d6d !important;
    }
  }

  .reset-btn {
    border-color: #ffb3c1 !important;
    color: #ff6b81 !important;
    border-radius: 8px;
    padding: 8px 16px;
    
    &:hover {
      background-color: #fff5f7 !important;
    }
  }
}

.report-cards-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.report-card {
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 24px rgba(255, 107, 129, 0.15);
  }
  
  &__header {
    position: relative;
    height: 180px;
    overflow: hidden;
    
    .report-img-wrapper {
      width: 100%;
      height: 100%;
      overflow: hidden;
    }
    
    .report-card__img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.5s ease;
      
      &:hover {
        transform: scale(1.05);
      }
    }
    
    .report-tag {
      position: absolute;
      top: 10px;
      right: 10px;
      background-color: rgba(255, 255, 255, 0.9);
    }
  }
  
  &__body {
    padding: 16px;
  }
  
  &__name {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 12px;
    color: #333;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  
  &__info {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 8px;
  }
  
  .info-item {
    font-size: 14px;
    
    .info-label {
      color: #888;
      margin-right: 4px;
    }
    
    .info-value {
      color: #555;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
}

.empty-state {
  margin: 60px auto;
  text-align: center;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.report-detail {
  display: flex;
  gap: 20px;
  margin-top: 10px;
  
  @media (max-width: 768px) {
    flex-direction: column;
  }
}

.detail-image {
  width: 200px;
  height: 200px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  
  .detail-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.detail-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-row {
  display: flex;
  align-items: flex-start;
  
  .label {
    width: 100px;
    color: #888;
    text-align: right;
    padding-right: 16px;
    flex-shrink: 0;
  }
  
  .value {
    flex: 1;
    color: #333;
    word-break: break-word;
  }
  
  &.full-width {
    margin-top: 10px;
    
    .full-content {
      line-height: 1.6;
      padding: 10px;
      background-color: #fff5f7;
      border-radius: 6px;
      min-height: 80px;
    }
  }
}
</style>