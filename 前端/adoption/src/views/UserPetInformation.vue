<script setup>
import {
  Search,
  RefreshLeft,
  Close
} from '@element-plus/icons-vue'
import { ref } from 'vue'
import { ElMessage, ElDialog } from 'element-plus'

// 导入接口
import { getPetList, searchPetService } from '@/api/petInformation'

// 分页相关
const pageNum = ref(1)
const pageSize = ref(6)
const total = ref(0)

// 宠物列表数据
const pets = ref([])
const searchKeyword = ref('')

// 弹窗相关
const dialogVisible = ref(false)
const currentPet = ref(null) // 当前选中的宠物信息

// 关闭弹窗的方法
const handleDialogClose = () => {
  dialogVisible.value = false
}

// 加载宠物列表
const getAllPets = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    const result = await getPetList(params)
    if (result.code === 0) {
      pets.value = result.data.items
      total.value = result.data.total
    } else {
      ElMessage.error('获取宠物信息失败')
    }
  } catch (error) {
    ElMessage.error('网络请求异常')
    console.error(error)
  }
}

// 初始化加载
getAllPets()

// 搜索功能
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    getAllPets()
    return
  }
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
      const result = await searchPetService(searchKeyword.value.trim(),params)
    if (result.code === 0) {
      pets.value = result.data.items
      total.value = result.data.total
      if (result.data.length === 0) {
        ElMessage.info('未找到匹配的宠物信息')
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
  getAllPets()
  ElMessage.success('已重置为全部宠物')
}

// 分页事件
const onSizeChange = (size) => {
  pageSize.value = size
  if (searchKeyword.value.trim()) {
    handleSearch()
  } else {
    getAllPets()
  }
}

const onCurrentChange = (num) => {
  pageNum.value = num
  if (searchKeyword.value.trim()) {
    handleSearch()
  } else {
    getAllPets()
  }
}

// 状态样式映射
const getStatusType = (status) => {
  switch (status) {
    case '可领养': return 'success'
    case '已领养': return 'danger'
    case '寻主中': return 'warning'
    default: return 'info'
  }
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

// 打开宠物详情弹窗
const openPetDetail = (pet) => {
  currentPet.value = { ...pet }
  dialogVisible.value = true
}
</script>

<template>
  <div class="pet-info-page">
    <div class="page-header">
      <h2 class="page-title">宠物信息展示</h2>
      <div class="search-group">
        <el-input 
          v-model="searchKeyword" 
          placeholder="搜索宠物名称/种类..." 
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

    <!-- 卡片式宠物信息展示 -->
    <div class="pet-cards-container">
      <div 
        class="pet-card" 
        v-for="pet in pets" 
        :key="pet.petId"
        @click="openPetDetail(pet)"
        style="cursor: pointer"
      >
        <div class="pet-card__header">
          <div class="pet-img-wrapper">
            <img 
              :src="getImageUrl(pet.petImage)" 
              alt="宠物照片" 
              class="pet-card__img"
            >
          </div>
          <el-tag 
            :type="getStatusType(pet.petStatus)" 
            effect="light"
            class="status-tag"
          >
            {{ pet.petStatus }}
          </el-tag>
        </div>
        
        <div class="pet-card__body">
          <h3 class="pet-card__name">{{ pet.petName }}</h3>
          <div class="pet-card__info">
            <div class="info-item">
              <span class="info-label">种类：</span>
              <span class="info-value">{{ pet.petType }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">性别：</span>
              <span class="info-value">{{ pet.petGender }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">年龄：</span>
              <span class="info-value">{{ pet.petAge }}岁</span>
            </div>
          </div>
          
          <div class="pet-card__desc">
            <h4>宠物介绍：</h4>
            <p class="desc-text">{{ pet.petDesc || '暂无介绍' }}</p>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-if="pets.length === 0" class="empty-state">
        <el-empty description="暂无宠物信息" />
      </div>
    </div>

    <!-- 分页条 -->
    <el-pagination 
      v-model:current-page="pageNum" 
      v-model:page-size="pageSize" 
      :page-sizes="[3,6,9]"
      layout="jumper, total, sizes, prev, pager, next" 
      background 
      :total="total" 
      @size-change="onSizeChange"
      @current-change="onCurrentChange" 
      class="pagination"
    />

    <!-- 宠物详情弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="宠物详细信息"
      :width="600"
      @close="handleDialogClose"
    >
      <div class="pet-detail">
        <div class="detail-image">
          <img 
            :src="getImageUrl(currentPet?.petImage)" 
            alt="宠物照片" 
            class="detail-img"
          >
        </div>
        <div class="detail-info">
          <div class="info-row">
            <span class="label">宠物名称：</span>
            <span class="value">{{ currentPet?.petName || '未知' }}</span>
          </div>
          <div class="info-row">
            <span class="label">宠物种类：</span>
            <span class="value">{{ currentPet?.petType || '未知' }}</span>
          </div>
          <div class="info-row">
            <span class="label">性别：</span>
            <span class="value">{{ currentPet?.petGender || '未知' }}</span>
          </div>
          <div class="info-row">
            <span class="label">年龄：</span>
            <span class="value">{{ currentPet?.petAge || '未知' }}岁</span>
          </div>
          <div class="info-row">
            <span class="label">状态：</span>
            <span class="value">
              <el-tag 
                :type="getStatusType(currentPet?.petStatus)" 
                effect="light"
              >
                {{ currentPet?.petStatus || '未知' }}
              </el-tag>
            </span>
          </div>
          <div class="info-row full-width">
            <span class="label">详细介绍：</span>
            <div class="value full-content">{{ currentPet?.petDesc || '暂无详细介绍' }}</div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.pet-info-page {
  background-color: #fff5f7;
  min-height: 128vh;
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

// 卡片容器样式
.pet-cards-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

// 宠物卡片样式
.pet-card {
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  }

  &__header {
    position: relative;
    height: 180px;
    overflow: hidden;

    .pet-img-wrapper {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: #f9f9f9;

      .pet-card__img {
        max-width: 100%;
        max-height: 100%;
        object-fit: contain;
        transition: transform 0.5s ease;
      }

      &:hover .pet-card__img {
        transform: scale(1.05);
      }
    }

    .status-tag {
      position: absolute;
      top: 10px;
      right: 10px;
      padding: 4px 8px;
      font-size: 12px;
      border-radius: 4px;
    }
  }

  &__body {
    padding: 16px;

    &__name {
      margin: 0 0 12px 0;
      font-size: 18px;
      color: #333;
      font-weight: 600;
    }

    &__info {
      display: flex;
      flex-direction: column;
      gap: 4px;
      margin-bottom: 16px;
      color: #666;
      font-size: 14px;

      .info-item {
        display: flex;
        align-items: center;

        .info-label {
          color: #999;
          margin-right: 4px;
          width: 40px;
          display: inline-block;
        }
      }
    }

    &__desc {
      color: #666;
      font-size: 14px;
      line-height: 1.6;

      h4 {
        margin: 0 0 8px 0;
        font-size: 15px;
        color: #333;
      }

      .desc-text {
        margin: 0;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 3;
        overflow: hidden;
      }
    }
  }
}

// 空状态样式
.empty-state {
  padding: 60px 0;
  text-align: center;
}

// 分页样式
.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}

// 详情弹窗样式
.pet-detail {
  display: flex;
  gap: 20px;
  padding: 10px 0;

  .detail-image {
    width: 200px;
    height: 200px;
    flex-shrink: 0;
    border-radius: 8px;
    overflow: hidden;
    background-color: #f9f9f9;

    .detail-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .detail-info {
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    gap: 12px;

    .info-row {
      display: flex;
      align-items: flex-start;

      &.full-width {
        width: 100%;
      }

      .label {
        width: 100px;
        color: #666;
        font-weight: 500;
      }

      .value {
        flex-grow: 1;
        color: #333;

        &.full-content {
          line-height: 1.6;
          white-space: pre-line;
        }
      }
    }
  }
}

</style>