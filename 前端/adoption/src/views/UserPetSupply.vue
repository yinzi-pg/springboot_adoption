<script setup>
import {
  Search,
  RefreshLeft
} from '@element-plus/icons-vue'
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { orderAddService } from '@/api/orderManage.js'

// 导入Pinia
import { useUserInfoStore } from '@/stores/user.js'

// 导入接口
import { getSupplyList, searchSuppliesService } from '@/api/petsupplies.js'
import { addOrderItemService } from '@/api/orderItem.js'

// 路由
const router = useRouter()

// 状态管理
const userStore = useUserInfoStore()

// 加载状态
const loading = ref(false)

// 分页相关
const pageNum = ref(1)
const pageSize = ref(8)
const total = ref(0)

// 用品列表数据
const supplies = ref([])
const searchKeyword = ref('')
const activeCategory = ref(0) // 0表示全部
const quickViewId = ref(null)
const quickViewVisible = ref(false)  // 弹窗显示控制
const currentSupply = ref(null)      // 当前查看的商品
const buyQuantity = ref(1)

// 分类数据
const categories = ref([
  { id: 0, name: '全部' },
  { id: 1, name: '食品零食' },
  { id: 2, name: '洗护用品' },
  { id: 3, name: '玩具互动' },
  { id: 4, name: '窝垫家具' },
  { id: 5, name: '出行装备' }
])

// 1. 修复后的“过滤后用品列表”计算属性
const filteredSupplies = computed(() => {
  // 先找到当前激活的分类对象（根据activeCategory的id匹配）
  const activeCategoryObj = categories.value.find(cat => cat.id === activeCategory.value);
  // 防止异常情况（比如分类数据加载失败），默认返回所有商品
  if (!activeCategoryObj) return supplies.value;

  // 情况1：如果是“全部”分类，直接返回所有商品
  if (activeCategoryObj.id === 0) {
    return supplies.value;
  }

  // 情况2：非“全部”分类，按supplyType筛选（和分类name完全匹配）
  return supplies.value.filter(supply => {
    // 处理两种异常：1.商品的supplyType为空 2.大小写不一致（比如“洗护用品”和“洗护用品 ”）
    if (!supply.supplyType) return false;
    // 转小写后匹配，避免空格/大小写导致的筛选失效
    return supply.supplyType.toLowerCase() === activeCategoryObj.name.toLowerCase();
  });
});

// 加载用品列表
const getAllSupplies = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    const result = await getSupplyList(params)
    if (result.code === 0) {
      supplies.value = result.data.items
      total.value = result.data.total
    } else {
      ElMessage.error('获取宠物用品信息失败')
    }
  } catch (error) {
    ElMessage.error('网络请求异常')
    console.error(error)
  }
}

// 初始化加载
onMounted(() => {
  getAllSupplies()
})

// 搜索功能
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    getAllSupplies()
    return
  }
  try {
    const result = await searchSuppliesService(searchKeyword.value.trim())
    if (result.code === 0) {
      supplies.value = result.data
      total.value = result.data.length
      if (result.data.length === 0) {
        ElMessage.info('未找到匹配的宠物用品信息')
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
  activeCategory.value = 0
  getAllSupplies()
  ElMessage.success('已重置为全部宠物用品')
}

// 2. 优化分类切换方法（切换时重置分页，避免无数据）
const filterByCategory = (categoryId) => {
  activeCategory.value = categoryId;
  pageNum.value = 1; // 切换分类时，默认回到第1页
  getAllSupplies(); // 重新加载当前分类的第1页数据
};

// 分页事件
const onSizeChange = (size) => {
  pageSize.value = size
  getAllSupplies()
}

const onCurrentChange = (num) => {
  pageNum.value = num
  getAllSupplies()
}

// 显示/隐藏快速查看按钮
const showQuickView = (id) => {
  quickViewId.value = id
}

const hideQuickView = () => {
  quickViewId.value = null
}

// 处理卡片点击，显示详情弹窗
const handleCardClick = (supply) => {
  currentSupply.value = supply
  buyQuantity.value = 1
  quickViewVisible.value = true  // 显示弹窗
}

// 关闭详情弹窗
const handleCloseQuickView = () => {
  quickViewVisible.value = false
  currentSupply.value = null
}

const addToCart = async (supply, quantity = 1) => {
  // 检查登录状态
  if (!userStore.info?.userId) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  // 检查商品状态
  if (supply.status !== '上架') {
    ElMessage.warning('该商品暂未上架')
    return
  }
  
  if (supply.stock <= 0) {
    ElMessage.warning('该商品已售罄')
    return
  }
  
  if (quantity > supply.stock) {
    ElMessage.warning('超过最大库存')
    return
  }

  try {
    loading.value = true
    // 直接调用添加订单项目接口（购物车项）
    const result = await addOrderItemService({
      userId: userStore.info.userId,
      productId: supply.supplyId, // 商品ID
      productName: supply.supplyName,
      price: supply.price,
      quantity: quantity,
      orderId:null,
      status: 0, // 0表示购物车状态
      imgUrl: supply.supplyImage // 补充图片地址，便于购物车展示
    })
    
    if (result.code === 0) {
      ElMessage.success(`已添加 ${supply.supplyName} 到购物车`)
      
      // 关闭快速查看窗口
      if (quickViewVisible.value) {
        quickViewVisible.value = false
      }
    } else {
      ElMessage.error(result.msg || '添加到购物车失败')
    }
  } catch (error) {
    console.error('添加到购物车失败:', error)
    ElMessage.error('添加到购物车失败，请重试')
  } finally {
    loading.value = false
  }
}

// 格式化价格
const formatPrice = (price) => {
  return typeof price === 'number' ? price.toFixed(2) : '0.00'
}

// 图片路径处理
const getImageUrl = (path) => {
  if (path?.startsWith('/assets/')) {
    try {
      return new URL(`../${path}`, import.meta.url).href
    } catch (e) {
      console.error('无法加载图片:', path)
      return '/assets/default-supply.png'
    }
  }
  return path || '/assets/default-supply.png'
}
</script>

<template>
  <div class="supply-info-page">
    <!-- 顶部横幅 -->
    <div class="page-banner">
      <div class="banner-content">
        <h1>宠物用品专区</h1>
        <p>为您的爱宠提供优质生活用品</p>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-container">
      <el-input 
        v-model="searchKeyword" 
        placeholder="搜索用品名称/品牌..." 
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

    <!-- 分类筛选 -->
    <div class="category-filter">
      <el-tag 
        v-for="category in categories" 
        :key="category.id"
        :class="{ 'active': activeCategory === category.id }"
        @click="filterByCategory(category.id)"
      >
        {{ category.name }}
      </el-tag>
    </div>

    <!-- 卡片式用品信息展示 -->
    <div class="supply-cards-container">
      <!-- 点击卡片触发handleCardClick显示详情弹窗 -->
      <div 
        class="supply-card" 
        v-for="supply in filteredSupplies" 
        :key="supply.supplyId"
        @click="handleCardClick(supply)"
      >
        <!-- 商品标签 -->
        <div v-if="supply.isNew" class="supply-tag new-tag">新品</div>
        <div v-if="supply.isHot" class="supply-tag hot-tag">热销</div>
        
        <!-- 商品图片 -->
        <div class="supply-img-container">
          <img 
            :src="getImageUrl(supply.supplyImage)" 
            alt="用品照片" 
            class="supply-img"
          >
        </div>
        
        <!-- 商品信息 -->
        <div class="supply-info">
          <div class="supply-category">{{ supply.supplyType }}</div>
          <h3 class="supply-name">{{ supply.supplyName }}</h3>
          <div class="supply-rating">
            <div class="stars">
              <i class="el-icon-star" v-for="n in 5" :key="n" :class="{ 'filled': n <= supply.rating }"></i>
            </div>
            <span class="review-count">({{ supply.reviewCount }}条评价)</span>
          </div>
          <div class="supply-price">
            <span class="current-price">¥{{ formatPrice(supply.price) }}</span>
            <span class="original-price" v-if="supply.originalPrice">¥{{ formatPrice(supply.originalPrice) }}</span>
          </div>
        </div>
        
        <!-- 操作按钮 -->
        <div class="supply-actions">
          <el-button 
            type="primary" 
            class="add-to-cart-btn"
            @click.stop="addToCart(supply)"  
            :disabled="supply.status !== '上架' || supply.stock <= 0 || loading"
            :loading="loading"
          >
            {{ supply.stock <= 0 ? '已售罄' : '加入购物车' }}
          </el-button>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-if="filteredSupplies.length === 0" class="empty-state">
        <div class="empty-icon">🔍</div>
        <p class="empty-text">未找到匹配的宠物用品</p>
        <el-button 
          type="default" 
          @click="resetSearch"
        >
          查看全部
        </el-button>
      </div>
    </div>

    <!-- 分页条 -->
    <el-pagination 
      v-model:current-page="pageNum" 
      v-model:page-size="pageSize" 
      :page-sizes="[4, 8, 12]"
      layout="jumper, total, sizes, prev, pager, next" 
      background 
      :total="total" 
      @size-change="onSizeChange"
      @current-change="onCurrentChange" 
      class="pagination"
    />

    <!-- 商品详情弹窗 -->
    <el-dialog 
      v-model="quickViewVisible" 
      title="商品详情" 
      width="700px"
      :before-close="handleCloseQuickView"
    >
      <div v-if="currentSupply" class="quick-view-content">
        <div class="quick-view-img">
          <img 
            :src="getImageUrl(currentSupply.supplyImage)" 
            alt="商品图片"
          >
        </div>
        <div class="quick-view-details">
          <div class="detail-category">{{ currentSupply.supplyType }}</div>
          <h2 class="detail-name">{{ currentSupply.supplyName }}</h2>
          <div class="detail-rating">
            <div class="stars">
              <i class="el-icon-star" v-for="n in 5" :key="n" :class="{ 'filled': n <= currentSupply.rating }"></i>
            </div>
            <span class="review-count">({{ currentSupply.reviewCount }}条评价)</span>
          </div>
          <div class="detail-price">
            <span class="current-price">¥{{ formatPrice(currentSupply.price) }}</span>
            <span class="original-price" v-if="currentSupply.originalPrice">¥{{ formatPrice(currentSupply.originalPrice) }}</span>
          </div>
          <div class="detail-stock">
            <span>库存：{{ currentSupply.stock }}件</span>
          </div>
          <div class="detail-desc">
            <h4>商品介绍：</h4>
            <p>{{ currentSupply.description || '暂无详细介绍' }}</p>
          </div>
          <div class="detail-actions">
            <el-input-number 
              v-model="buyQuantity" 
              :min="1" 
              :max="currentSupply.stock || 1"
              class="quantity-input"
            ></el-input-number>
            <el-button 
              type="primary" 
              class="add-to-cart-btn"
              @click="addToCart(currentSupply, buyQuantity)"
              :disabled="currentSupply.status !== '上架' || currentSupply.stock <= 0 || loading"
              :loading="loading"
            >
              {{ currentSupply.stock <= 0 ? '已售罄' : '加入购物车' }}
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>


<style lang="scss" scoped>
.supply-info-page {
  background-color: #f8f9fa;
  min-height: 100vh;
}

// 顶部横幅
.page-banner {
  background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 99%, #fad0c4 100%);
  padding: 60px 0;
  text-align: center;
  color: white;
  margin-bottom: 30px;

  .banner-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;

    h1 {
      font-size: 32px;
      margin-bottom: 15px;
      text-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }

    p {
      font-size: 18px;
      opacity: 0.9;
    }
  }
}

// 搜索容器 - 调整为右侧显示
.search-container {
  max-width: 1200px;
  margin: 0 auto 30px;
  padding: 0 20px;
  display: flex;
  gap: 10px;
  align-items: center;
  justify-content: flex-end; /* 靠右对齐 */

  .search-input {
    flex: 0 1 500px; /* 限制最大宽度 */
    height: 42px;
    border-radius: 21px;
    padding-left: 20px;
  }

  .search-btn {
    background-color: #ff6b81 !important;
    border-color: #ff6b81 !important;
    border-radius: 21px;
    padding: 0 20px;
    height: 42px;
  }

  .reset-btn {
    border-radius: 21px;
    padding: 0 20px;
    height: 42px;
  }
}

// 分类筛选
.category-filter {
  max-width: 1200px;
  margin: 0 auto 25px;
  padding: 0 20px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;

  .el-tag {
    padding: 6px 16px;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s;
    background-color: #fff;
    color: #666;
    border-color: #e5e5e5;

    &:hover {
      background-color: #fff5f7;
      color: #ff6b81;
      border-color: #ffccd5;
    }

    &.active {
      background-color: #ff6b81;
      color: white;
      border-color: #ff6b81;
    }
  }
}

// 卡片容器
.supply-cards-container {
  max-width: 1200px;
  margin: 0 auto 40px;
  padding: 0 20px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 25px;
}

// 商品卡片 - 添加 cursor: pointer 提示可点击
.supply-card {
  background-color: #fff;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  transition: all 0.3s ease;
  position: relative;
  cursor: pointer;  /* 显示手型光标，提示可点击 */

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 20px rgba(0,0,0,0.1);
  }

  .supply-tag {
    position: absolute;
    top: 10px;
    left: 10px;
    padding: 3px 8px;
    font-size: 12px;
    color: white;
    border-radius: 4px;
    z-index: 1;
  }

  .new-tag {
    background-color: #409eff;
  }

  .hot-tag {
    background-color: #ff6b81;
  }
}

// 商品图片容器
.supply-img-container {
  position: relative;
  height: 200px;
  overflow: hidden;

  .supply-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s ease;
  }

  .supply-card:hover .supply-img {
    transform: scale(1.08);
  }
}

// 商品信息
.supply-info {
  padding: 15px;

  .supply-category {
    font-size: 12px;
    color: #999;
    margin-bottom: 5px;
  }

  .supply-name {
    font-size: 16px;
    color: #333;
    margin: 0 0 10px;
    height: 40px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .supply-rating {
    display: flex;
    align-items: center;
    margin-bottom: 8px;

    .stars {
      color: #e6a23c;
      margin-right: 5px;

      .el-icon-star {
        font-size: 14px;
        margin-right: 1px;

        &.filled {
          color: #e6a23c;
        }
      }
    }

    .review-count {
      font-size: 12px;
      color: #999;
    }
  }

  .supply-price {
    display: flex;
    align-items: center;
    gap: 8px;

    .current-price {
      font-size: 18px;
      color: #ff6b81;
      font-weight: 600;
    }

    .original-price {
      font-size: 14px;
      color: #999;
      text-decoration: line-through;
    }
  }
}

// 操作按钮
.supply-actions {
  padding: 0 15px 15px;

  .add-to-cart-btn {
    width: 100%;
    background-color: #ff6b81 !important;
    border-color: #ff6b81 !important;
    color: white !important;
    border-radius: 20px;
    height: 36px;
    font-size: 14px;

    &:hover {
      background-color: #ff4d6d !important;
      border-color: #ff4d6d !important;
    }

    &:disabled {
      background-color: #f5f5f5 !important;
      border-color: #e5e5e5 !important;
      color: #999 !important;
      cursor: not-allowed;
    }
  }
}

// 空状态
.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 80px 0;

  .empty-icon {
    font-size: 60px;
    color: #ddd;
    margin-bottom: 20px;
  }

  .empty-text {
    font-size: 16px;
    color: #999;
    margin-bottom: 25px;
  }
}

// 分页样式
.pagination {
  max-width: 1200px;
  margin: 0 auto 40px;
  padding: 0 20px;
  display: flex;
  justify-content: flex-end;
}

// 快速查看弹窗
.quick-view-content {
  display: flex;
  gap: 20px;
  padding: 10px 0;

  .quick-view-img {
    width: 300px;
    flex-shrink: 0;

    img {
      width: 100%;
      height: 300px;
      object-fit: cover;
      border-radius: 8px;
    }
  }

  .quick-view-details {
    flex: 1;

    .detail-category {
      font-size: 13px;
      color: #999;
      margin-bottom: 8px;
    }

    .detail-name {
      font-size: 20px;
      color: #333;
      margin: 0 0 15px;
    }

    .detail-rating {
      margin-bottom: 15px;

      .stars {
        color: #e6a23c;
        margin-right: 8px;

        .el-icon-star {
          font-size: 16px;
          margin-right: 2px;

          &.filled {
            color: #e6a23c;
          }
        }
      }

      .review-count {
        font-size: 14px;
        color: #999;
      }
    }

    .detail-price {
      margin-bottom: 15px;
      display: flex;
      align-items: center;
      gap: 10px;

      .current-price {
        font-size: 24px;
        color: #ff6b81;
        font-weight: 600;
      }

      .original-price {
        font-size: 16px;
        color: #999;
        text-decoration: line-through;
      }
    }

    .detail-stock {
      color: #666;
      margin-bottom: 20px;
      padding-bottom: 15px;
      border-bottom: 1px solid #eee;
    }

    .detail-desc {
      margin-bottom: 25px;

      h4 {
        font-size: 16px;
        margin: 0 0 10px;
        color: #333;
      }

      p {
        margin: 0;
        color: #666;
        line-height: 1.6;
      }
    }

    .detail-actions {
      display: flex;
      align-items: center;
      gap: 15px;

      .quantity-input {
        width: 100px;
      }

      .add-to-cart-btn {
        padding: 10px 30px;
        background-color: #ff6b81 !important;
        border-color: #ff6b81 !important;
        color: white !important;
        border-radius: 4px;
        font-size: 16px;

        &:hover {
          background-color: #ff4d6d !important;
          border-color: #ff4d6d !important;
        }

        &:disabled {
          background-color: #f5f5f5 !important;
          border-color: #e5e5e5 !important;
          color: #999 !important;
          cursor: not-allowed;
        }
      }
    }
  }
}

// 全局样式调整
::v-deep .el-dialog__header {
  padding: 20px 20px 15px;
  border-bottom: 1px solid #eee;

  .el-dialog__title {
    font-size: 18px;
    color: #333;
  }
}

::v-deep .el-dialog__footer {
  padding: 15px 20px 20px;
  border-top: 1px solid #eee;
}
</style>