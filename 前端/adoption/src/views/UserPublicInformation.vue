<script setup>
import {
  Search,
  RefreshLeft,
  Pointer,
  Star,
} from '@element-plus/icons-vue'
import { ref, onMounted } from 'vue'
import { ElMessage, ElDialog } from 'element-plus'
import { getPublicList, searchPublicService } from '@/api/publicInformation'

// 分页相关
const pageNum = ref(1)
const pageSize = ref(6)
const total = ref(0)

// 公告列表数据
const publics = ref([])
const searchKeyword = ref('')
const detailVisible = ref(false)
const currentPublic = ref(null)

// 图片路径处理
const getImageUrl = (path) => {
  if (path?.startsWith('/assets/')) {
    try {
      return require('@/assets' + path)
    } catch (e) {
      console.error('无法加载图片:', path)
      return '/assets/default-announcement.png'
    }
  }
  return path || '/assets/default-announcement.png'
}

// 文本截断函数
const ellipsis = (text, maxLength) => {
  if (!text) return ''
  return text.length > maxLength ? text.slice(0, maxLength) + '...' : text
}

// 加载公告列表时初始化随机点赞数
const getAllPublics = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    const result = await getPublicList(params)
    if (result.code === 0) {
      publics.value = result.data.items.map(item => ({
        ...item,
        likeCount: item.likeCount || Math.floor(Math.random() * 41) + 10,
        liked: item.liked || false
      }))
      total.value = result.data.total
    } else {
      ElMessage.error('获取公告信息失败')
    }
  } catch (error) {
    ElMessage.error('网络请求异常')
    console.error(error)
  }
}

// 初始化加载
onMounted(() => {
  getAllPublics()
})

// 搜索功能
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    getAllPublics()
    return
  }
  try {
    const result = await searchPublicService(searchKeyword.value.trim())
    if (result.code === 0) {
      publics.value = result.data.map(item => ({
        ...item,
        likeCount: item.likeCount || 0,
        liked: item.liked || false
      }))
      total.value = result.data.length
      if (result.data.length === 0) {
        ElMessage.info('未找到匹配的公告信息')
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
  getAllPublics()
  ElMessage.success('已重置为全部公告')
}

// 分页事件
const onSizeChange = (size) => {
  pageSize.value = size
  getAllPublics()
}

const onCurrentChange = (num) => {
  pageNum.value = num
  getAllPublics()
}

// 查看详情
const handleViewDetail = (item) => {
  currentPublic.value = { ...item }
  detailVisible.value = true
}

// 点赞功能
const handleLike = (item) => {
  if (item.liked) return;
  
  const increment = Math.floor(Math.random() * 1) + 1;
  item.likeCount += increment;
  item.liked = true;
  ElMessage.success(`点赞成功，增加了${increment}个赞`);
};
</script>

<template>
  <div class="public-info-page">
    <div class="page-header">
      <h2 class="page-title">公告信息</h2>
      <!-- 搜索区域调整为右侧布局 -->
      <div class="search-group">
        <el-input 
          v-model="searchKeyword" 
          placeholder="搜索公告标题/内容..." 
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

    <!-- 公告展示布局 -->
    <div class="public-cards-container">
      <div class="public-card" v-for="item in publics" :key="item.publicId" @click="handleViewDetail(item)">
        <div class="public-card__image">
          <img 
            :src="getImageUrl(item.publicImage)" 
            :alt="item.title" 
            class="card-image"
          >
        </div>
        
        <div class="public-card__body">
          <div class="public-card__header">
            <h3 class="public-card__title">{{ item.title }}</h3>
            <span class="public-card__date">{{ item.createDate }}</span>
          </div>
          
          <div class="public-card__content">
            <p class="public-card__brief">{{ ellipsis(item.content, 200) }}</p>
          </div>
          
          <div class="public-card__footer">
            <div class="like-container" @click.stop="handleLike(item)">
              <el-icon :color="item.liked ? '#ff6b81' : ''" class="like-icon">
                <Star v-if="item.liked" />
                <Pointer v-else />
              </el-icon>
              <span class="like-count">{{ item.likeCount || 0 }}</span>
            </div>
            <span class="read-more">查看详情 →</span>
          </div>
        </div>
      </div>
      
      <div v-if="publics.length === 0" class="empty-state">
        <el-empty description="暂无公告信息" />
      </div>
    </div>

    <!-- 分页条 -->
    <el-pagination 
      v-model:current-page="pageNum" 
      v-model:page-size="pageSize" 
      :page-sizes="[6, 8, 12]"  
      layout="jumper, total, sizes, prev, pager, next" 
      background 
      :total="total" 
      @size-change="onSizeChange"
      @current-change="onCurrentChange" 
      class="pagination"
    />

    <!-- 详情弹窗 -->
    <el-dialog 
      v-model="detailVisible" 
      :title="currentPublic?.title || '公告详情'" 
      width="60%"
      :close-on-click-modal="false"
    >
      <div class="public-detail">
        <div v-if="currentPublic?.publicImage" class="detail-image-container">
          <img 
            :src="getImageUrl(currentPublic.publicImage)" 
            :alt="currentPublic.title" 
            class="detail-image"
          >
        </div>
        
        <div class="detail-meta">
          <span class="detail-date">发布时间: {{ currentPublic?.createDate }}</span>
        </div>
        <div class="detail-content">
          <p v-html="currentPublic?.content"></p>
        </div>
      </div>
      <template #footer>
        <el-button class="pink-btn" @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.public-info-page {
  background-color: #fff5f7;
  min-height: 100vh;
  padding: 20px;
  box-sizing: border-box;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between; /* 关键：使标题和搜索区分开两端对齐 */
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ffccd5;

  .page-title {
    font-size: 22px;
    font-weight: 700;
    color: #333;
  }

  .search-group {
    display: flex;
    align-items: center;
    gap: 10px;
    /* 搜索框组默认靠右，通过父容器的justify-content: space-between实现 */
  }

  .search-input {
    width: 300px;
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

// 公告卡片布局
.public-cards-container {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
  margin-bottom: 30px;

  @media (min-width: 768px) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (min-width: 1200px) {
    grid-template-columns: repeat(2, 1fr);
  }
}

.public-card {
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid #f8f9fa;
  display: flex;
  flex-direction: column;
  height: 100%;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 24px rgba(255, 107, 129, 0.15);
    border-color: #ffccd5;
  }

  &__image {
    width: 100%;
    height: 220px;
    overflow: hidden;
    background-color: #f9f9f9;
    flex-shrink: 0;
    
    .card-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s ease;
      
      &:hover {
        transform: scale(1.05);
      }
    }
  }

  &__body {
    padding: 20px;
    flex-grow: 1;
    display: flex;
    flex-direction: column;
  }

  &__header {
    margin-bottom: 16px;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;

    &__title {
      font-size: 18px;
      font-weight: 600;
      color: #333;
      margin: 0;
      display: -webkit-box;
      -webkit-line-clamp: 1;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    &__date {
      font-size: 12px;
      color: #999;
      white-space: nowrap;
    }
  }

  &__content {
    flex-grow: 1;

    &__brief {
      margin: 0;
      font-size: 14px;
      color: #666;
      line-height: 1.6;
      display: -webkit-box;
      -webkit-line-clamp: 4;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
  }

  &__footer {
    padding-top: 16px;
    margin-top: auto;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-top: 1px solid #f5f5f5;

    .like-container {
      display: flex;
      align-items: center;
      gap: 6px;
      cursor: pointer;
      color: #666;
      transition: color 0.2s;

      &:hover {
        color: #ff6b81;
      }

      .like-icon {
        font-size: 16px;
      }

      .like-count {
        font-size: 14px;
      }
    }

    .read-more {
      font-size: 14px;
      color: #ff6b81;
      font-weight: 500;
    }
  }
}

.empty-state {
  grid-column: 1 / -1;
  padding: 60px 0;
  text-align: center;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.public-detail {
  padding: 10px 0;

  .detail-image-container {
    margin-bottom: 20px;
    border-radius: 8px;
    overflow: hidden;
    max-height: 400px;
    
    .detail-image {
      width: 100%;
      height: 100%;
      object-fit: contain;
    }
  }

  .detail-meta {
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px dashed #eee;

    .detail-date {
      font-size: 14px;
      color: #999;
    }
  }

  .detail-content {
    font-size: 16px;
    line-height: 1.8;
    color: #333;

    p {
      margin: 0 0 16px 0;
    }
  }
}

.pink-btn {
  background-color: #ff6b81 !important;
  border-color: #ff6b81 !important;
  color: #ffffff !important;
  border-radius: 8px !important;

  &:hover {
    background-color: #ff4d6d !important;
    border-color: #ff4d6d !important;
  }
}
</style>