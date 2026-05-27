<template>
  <div class="cart-page">
    <div class="page-header">
      <h2 class="page-title">我的购物车</h2>
      <el-button 
        type="default" 
        :icon="RefreshLeft"
        @click="refreshCart"
        class="refresh-btn"
        :loading="loading"
      >
        刷新
      </el-button>
    </div>
    <!-- 结算 -->
    <div v-if="cartItems.length > 0" class="cart-footer">
      <div class="cart-summary">
        <div class="summary-info">
          <span>已选 {{ checkedItemsCount }} 件商品</span>
          <span class="total-price">
            合计：<span class="price">¥{{ formatPrice(checkedItemsTotal) }}</span>
          </span>
        </div>
        <div class="summary-actions">
          <el-button 
            type="default" 
            @click="clearCartItems"
            class="clear-btn"
            :disabled="isProcessingPayment"
          >
            清空购物车
          </el-button>
          <el-button 
            type="primary" 
            class="checkout-btn"
            :disabled="checkedItemsCount === 0 || isProcessingPayment"
            @click="handleCheckout"
          >
            结算
          </el-button>
        </div>
      </div>
    </div>

    <el-card class="cart-container">
      <div v-if="cartItems.length === 0" class="empty-cart">
        <div class="empty-icon"><ShoppingCart /></div>
        <p class="empty-text">购物车还是空的哦~</p>
        <el-button 
          type="primary" 
          @click="router.push('/userPetSupply')"
        >
          去逛逛
        </el-button>
      </div>

      <el-table 
        v-if="cartItems.length > 0"
        :data="cartItems" 
        border 
        stripe
        :loading="loading"
      >
        <el-table-column width="55">
          <template #header>
            <el-checkbox 
              v-model="allChecked" 
              @change="handleAllChecked"
              :disabled="isProcessingPayment"
            ></el-checkbox>
          </template>
          <template #default="{ row }">
            <el-checkbox 
              v-model="row.checked" 
              @change="handleItemChecked"
              :disabled="isProcessingPayment"
            ></el-checkbox>
          </template>
        </el-table-column>
        
        <el-table-column label="商品信息" width="400">
          <template #default="{ row }">
            <div class="product-info">
              <img 
                :src="getImageUrl(row.supplyImage)" 
                :alt="row.supplyName || row.productName || '商品图片'" 
                class="product-img"
                @error="handleImageError($event)"
              >
              <div class="product-details">
                <div class="product-name">{{ row.productName || row.supplyName }}</div>
                <div class="product-price">¥{{ formatPrice(row.price) }}</div>
                <div class="product-stock" v-if="row.stock <= 0">
                  <span class="stock-warning">已售罄</span>
                </div>
                </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="单价" align="center">
          <template #default="{ row }">
            <span>¥{{ formatPrice(row.price) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="数量" align="center">
          <template #default="{ row }">
            <el-input-number 
              v-model="row.quantity" 
              :min="1" 
              :max="row.stock"
              @change="handleQuantityChange(row, $event)"
              size="small"
              :disabled="row.stock <= 0 || isProcessingPayment"
            ></el-input-number>
          </template>
        </el-table-column>
        
        <el-table-column label="小计" align="center">
          <template #default="{ row }">
            <span class="subtotal">¥{{ formatPrice(row.price * row.quantity) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" align="center" width="120">
          <template #default="{ row }">
            <el-button 
              type="text" 
              :icon="Delete" 
              @click="removeFromCart(row.orderItemId)"
              class="delete-btn"
              :disabled="isProcessingPayment"
            >
              移除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 分页组件 -->
    <el-pagination 
      v-if="total > 0"
      v-model:current-page="pageNum" 
      v-model:page-size="pageSize" 
      :page-sizes="[1, 3, 5]"
      layout="jumper, total, sizes, prev, pager, next" 
      background 
      :total="total" 
      @size-change="onSizeChange"
      @current-change="onCurrentChange" 
      style="margin-top: 20px; justify-content: flex-end" 
      :disabled="isProcessingPayment"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import {
  Delete,
  RefreshLeft,
  ShoppingCart
} from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
// 引入Pinia store
import { useUserInfoStore } from '@/stores/user'
import { getSupplyList } from '@/api/petsupplies'

// 导入接口
import { 
  getOrderItemList, 
  getOrderByUserId,
  updateOrderItemService, 
  deleteOrderItemService
} from '@/api/orderItem.js'

// 导入用户相关接口
import { 
  userDeleteMoney,
  getUserById,
  userAddMoney  // 用于退款场景
} from '@/api/userInformation'

// 导入订单管理相关接口
import { orderAddService } from '@/api/orderManage'

// 路由
const router = useRouter()

// 获取用户store
const userStore = useUserInfoStore()

// 购物车列表数据
const cartItems = ref([])
const loading = ref(false)
const allChecked = ref(false)
const isProcessingPayment = ref(false) // 支付处理状态

// 分页相关变量
const pageNum = ref(1)          // 当前页码
const pageSize = ref(5)         // 每页条数
const total = ref(0)            // 总条数

// 缓存商品列表（用Map提高查询效率）
const supplyMap = ref(new Map())

// 加载商品列表并缓存
const loadSuppliesList = async () => {
  try {
    const result = await getSupplyList({ pageNum: 1, pageSize: 1000 })
    if (result.code === 0) {
      // 用商品ID作为key，存储完整信息
      result.data.items.forEach(item => {
        supplyMap.value.set(item.supplyId, item)
      })
      console.log('商品列表缓存完成，共', result.data.items.length, '条')
    }
  } catch (error) {
    console.error('加载商品列表失败', error)
  }
}

// 加载购物车数据 - 只加载状态为购物车中的商品
const loadCartItems = async () => {
  if (!userStore.info?.userId) {
    ElMessage.warning('请先登录')
    return
  }
  
  try {
    loading.value = true
    // 调用接口，查询当前用户的购物车商品，添加分页参数
    const result = await getOrderByUserId({
      userId: userStore.info.userId,
      status: 0, // 0表示购物车中
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    
    if (result.code === 0) {
      // 处理订单数据，补充商品图片信息
      const itemsWithImages = result.data.items.map(item => {
        // 从缓存中查找对应的商品信息
        const supplyInfo = supplyMap.value.get(item.supplyId || item.productId)
        
        return {
          ...item,
          // 补充商品图片（优先用商品列表中的图片，没有则用默认图）
          supplyImage: supplyInfo?.supplyImage || item.supplyImage || '',
          // 补充商品名称
          supplyName: supplyInfo?.supplyName || item.productName || '未知商品'
        }
      })
      
      // 假设接口返回格式为 { data: { items: [], total: 0 } }
      cartItems.value = itemsWithImages.map(item => ({
        ...item,
        checked: false // 初始化为未选中状态
      }))
      total.value = result.data.total  // 设置总条数
      updateAllCheckedStatus()
      // 同步到本地存储
      syncCartToLocalStorage()
    } else {
      ElMessage.error('获取购物车失败: ' + (result.msg || '未知错误'))
      // 降级使用本地存储
      cartItems.value = JSON.parse(localStorage.getItem('petCart') || '[]')
      total.value = cartItems.value.length
    }
  } catch (error) {
    console.error('加载购物车失败', error)
    // 出错时从本地存储获取
    cartItems.value = JSON.parse(localStorage.getItem('petCart') || '[]')
    total.value = cartItems.value.length
    ElMessage.error('加载购物车失败，使用本地数据')
  } finally {
    loading.value = false
  }
}

// 分页事件处理
const onSizeChange = (size) => {
  pageSize.value = size
  loadCartItems()
}

const onCurrentChange = (num) => {
  pageNum.value = num
  loadCartItems()
}

// 初始化加载
onMounted(() => {
  // 先加载商品列表，再加载购物车
  loadSuppliesList().then(() => {
    loadCartItems()
  })
  
  // 监听用户ID变化，重新加载购物车
  watch(() => userStore.info?.userId, () => {
    pageNum.value = 1  // 重置页码
    loadSuppliesList().then(() => {
      loadCartItems()
    })
  })
  
  // 监听本地存储变化，实现跨页面同步
  window.addEventListener('storage', (e) => {
    if (e.key === 'petCart' && !isProcessingPayment.value) {
      cartItems.value = JSON.parse(e.newValue || '[]')
      total.value = cartItems.value.length
      updateAllCheckedStatus()
    }
  })
})

// 更新全选状态
const updateAllCheckedStatus = () => {
  if (cartItems.value.length === 0) {
    allChecked.value = false
    return
  }
  allChecked.value = cartItems.value.every(item => item.checked)
}

// 全选/取消全选
const handleAllChecked = (checked) => {
  cartItems.value.forEach(item => {
    item.checked = checked
  })
}

// 单项选择变化
const handleItemChecked = () => {
  updateAllCheckedStatus()
}

// 更改数量
const handleQuantityChange = async (item, newQuantity) => {
  if (newQuantity < 1) newQuantity = 1
  if (newQuantity > item.stock) {
    newQuantity = item.stock
    ElMessage.warning('已超过最大库存')
  }
  
  // 临时更新UI
  const oldQuantity = item.quantity
  item.quantity = newQuantity
  
  try {
    // 调用修改接口
    await updateOrderItemService({
      orderItemId: item.orderItemId,
      productId: item.productId,
      productName: item.productName,
      price: item.price,
      userId: item.userId,
      status: item.status,
      quantity: newQuantity
    })
    syncCartToLocalStorage()
    ElMessage.success('数量已更新')
  } catch (error) {
    console.error('更新购物车项失败', error)
    // 接口失败时回滚
    item.quantity = oldQuantity
    syncCartToLocalStorage()
    ElMessage.error('更新失败，请重试')
  }
}

// 从购物车移除
const removeFromCart = async (orderItemId) => {
  ElMessageBox.confirm(
    '确定要从购物车中移除该商品吗？',
    '确认移除',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // 调用删除接口
      const result = await deleteOrderItemService(orderItemId)
      if (result.code === 0) {
        // 本地数据同步
        cartItems.value = cartItems.value.filter(item => item.orderItemId !== orderItemId)
        total.value--  // 减少总条数
        syncCartToLocalStorage()
        updateAllCheckedStatus()
        ElMessage.success('已从购物车移除')
      } else {
        ElMessage.error('删除失败: ' + (result.msg || '未知错误'))
      }
    } catch (error) {
      console.error('删除购物车项失败', error)
      // 接口失败时仅本地删除
      cartItems.value = cartItems.value.filter(item => item.orderItemId !== orderItemId)
      total.value--  // 减少总条数
      syncCartToLocalStorage()
      updateAllCheckedStatus()
      ElMessage.error('删除失败，已本地移除')
    }
  }).catch(() => {
    ElMessage.info('已取消移除')
  })
}

// 清空购物车
const clearCartItems = async () => {
  if (cartItems.value.length === 0) {
    ElMessage.info('购物车已为空')
    return
  }
  
  ElMessageBox.confirm(
    '确定要清空购物车吗？',
    '确认清空',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // 批量删除购物车商品
      for (const item of cartItems.value) {
        await deleteOrderItemService(item.orderItemId)
      }
      cartItems.value = []
      total.value = 0  // 重置总条数
      syncCartToLocalStorage()
      updateAllCheckedStatus()
      ElMessage.success('购物车已清空')
    } catch (error) {
      console.error('清空购物车失败', error)
      cartItems.value = []
      total.value = 0  // 重置总条数
      syncCartToLocalStorage()
      updateAllCheckedStatus()
      ElMessage.error('清空失败，已本地清空')
    }
  }).catch(() => {
    ElMessage.info('已取消清空')
  })
}

// 计算选中商品总数
const checkedItemsCount = computed(() => {
  return cartItems.value.reduce((total, item) => {
    return item.checked ? total + item.quantity : total
  }, 0)
})

// 计算选中商品总价
const checkedItemsTotal = computed(() => {
  return cartItems.value.reduce((total, item) => {
    return item.checked ? total + (item.price * item.quantity) : total
  }, 0)
})

// 同步到本地存储
const syncCartToLocalStorage = () => {
  localStorage.setItem('petCart', JSON.stringify(cartItems.value))
}

// 格式化价格
const formatPrice = (price) => {
  return typeof price === 'number' ? price.toFixed(2) : '0.00'
}

// 图片路径处理函数
const getImageUrl = (imagePath) => {
  // 如果是完整URL直接返回
  if (imagePath && (imagePath.startsWith('http://') || imagePath.startsWith('https://'))) {
    return imagePath;
  }
  
  // 如果是相对路径则处理
  try {
    // 处理相对路径，根据实际项目结构调整
    const basePath = imagePath?.startsWith('/') ? imagePath.substring(1) : imagePath;
    return new URL(`../${basePath || 'assets/a.png'}`, import.meta.url).href;
  } catch (error) {
    console.error('图片路径处理错误:', imagePath, error);
    // 返回默认商品图片
    return new URL('../assets/a.png', import.meta.url).href;
  }
};

// 图片加载错误处理
const handleImageError = (event) => {
  // 加载失败时显示默认图
  event.target.src = new URL('../assets/a.png', import.meta.url).href;
};

// 刷新购物车数据
const refreshCart = () => {
  loadSuppliesList().then(() => {
    loadCartItems()
    ElMessage.success('购物车已刷新')
  })
}

// 结算功能 - 包含订单创建逻辑
const handleCheckout = async () => {
  // 防止重复调用
  if (isProcessingPayment.value) return;
  
  const checkedItems = cartItems.value.filter(item => item.checked)
  if (checkedItems.length === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  
  // 获取总金额
  const totalAmount = checkedItemsTotal.value
  
  // 声明loading实例变量
  let loadingInstance = null
  
  try {
    // 显示结算确认弹窗
    await ElMessageBox.confirm(
      `您选择了 ${checkedItemsCount.value} 件商品，总金额为 ¥${formatPrice(totalAmount)}，确认支付吗？`,
      '确认结算',
      {
        confirmButtonText: '确认支付',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    // 检查用户是否登录
    if (!userStore.info?.userId) {
      ElMessage.warning('请先登录')
      return
    }
    
    // 设置支付状态
    isProcessingPayment.value = true
    
    // 创建加载实例
    loadingInstance = ElLoading.service({
      lock: true,
      text: '处理支付中...',
      background: 'rgba(255, 255, 255, 0.7)'
    })
    
    // 1. 获取用户当前信息，检查余额
    const userInfo = await getUserById(userStore.info.userId)
    if (userInfo.code !== 0 || !userInfo.data) {
      throw new Error('获取用户信息失败')
    }
    
    // 提取用户电话（尝试多种可能的字段名）
    const userPhone = userInfo.data.userPhone || 
                     userInfo.data.phone || 
                     userInfo.data.mobile || 
                     userInfo.data.telephone || 
                     '';
                     
    // 调试：输出获取到的用户电话
    console.log('获取到的用户电话:', userPhone)
    
    const userBalance = Number(userInfo.data.money)
    if (userBalance < totalAmount) {
      throw new Error(`余额不足，当前余额: ¥${userBalance.toFixed(2)}`)
    }
    
    // 2. 调用扣款接口
    const deductResult = await userDeleteMoney({
      userId: userStore.info.userId,
      amount: totalAmount
    })
    
    if (deductResult.code !== 0) {
      // 扣款失败，回滚操作
      throw new Error(deductResult.message || '扣款失败')
    }
    
    // 3. 创建订单记录 - 包含商品详情
    const orderItemsDetail = checkedItems.map(item => ({
      productId: item.productId || item.supplyId,
      productName: item.productName || item.supplyName,
      quantity: item.quantity,
      price: item.price,
      subtotal: item.price * item.quantity
    }));

    // 格式化商品详情为文本，例如："牵引绳 x1, 狗粮 x2"
    const productsText = orderItemsDetail
      .map(item => `${item.productName} x${item.quantity}`)
      .join(', ');

    const orderResult = await orderAddService({
      userId: userStore.info.userId,
      userName: userInfo.data.userName || '未知用户',
      userPhone: userPhone,
      totalAmount: totalAmount,
      status: '已支付',
      createTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
      remark: `包含${checkedItemsCount.value}件商品: ${productsText}`,
      // 添加商品详情数组，供订单详情页使用
      items: orderItemsDetail
    });
    
    if (orderResult.code !== 0) {
      // 订单创建失败，需要退款
      await userAddMoney({
        userId: userStore.info.userId,
        amount: totalAmount
      })
      throw new Error(`订单创建失败: ${orderResult.message || '未知错误'}`)
    }
    
    // 4. 移除已结算的商品（从购物车中删除）
    const orderItemIds = checkedItems.map(item => item.orderItemId)
    const deleteErrors = []
    
    for (const id of orderItemIds) {
      try {
        await deleteOrderItemService(id)
      } catch (error) {
        deleteErrors.push(id)
        console.error(`删除订单项 ${id} 失败`, error)
      }
    }
    
    // 5. 更新本地购物车数据
    const remainingItems = cartItems.value.filter(item => !item.checked)
    cartItems.value = remainingItems
    total.value = remainingItems.length
    syncCartToLocalStorage()
    updateAllCheckedStatus()
    
    // 6. 更新用户信息缓存
    const updatedUser = await getUserById(userStore.info.userId)
    if (updatedUser.code === 0 && updatedUser.data) {
      userStore.setInfo(updatedUser.data)
    }
    
    // 7. 显示成功消息
    if (deleteErrors.length > 0) {
      ElMessage.success(`支付成功，订单编号: ${orderResult.data.orderId}，但部分商品未从购物车移除，请刷新购物车`)
    } else {
      ElMessage.success(`支付成功`)
    }
    
  } catch (error) {
    // 仅处理真实错误，忽略取消操作的错误
    if (error.name !== 'CanceledError') {
      console.error('结算失败:', error)
      ElMessage.error(`结算失败: ${error.message}`)
    } else {
      // 用户取消操作
      ElMessage.info('已取消结算')
    }
  } finally {
    // 确保加载状态关闭
    if (loadingInstance) {
      loadingInstance.close()
    }
    // 重置支付状态
    isProcessingPayment.value = false
  }
}
</script>

<style lang="scss" scoped>
.cart-page {
  background-color: #f8f9fa;
  min-height: 100vh;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto 20px;

  .page-title {
    font-size: 24px;
    color: #333;
    margin: 0;
  }

  .refresh-btn {
    border-radius: 4px;
  }
}

.cart-container {
  max-width: 1200px;
  margin: 0 auto;
  background-color: #fff;

  .empty-cart {
    text-align: center;
    padding: 60px 0;

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

  .el-table {
    margin-top: 10px;

    .product-info {
      display: flex;
      align-items: center;
      gap: 15px;

      .product-img {
        width: 80px;
        height: 80px;
        object-fit: cover;
        border-radius: 4px;
        display: block;
      }

      .product-details {
        .product-name {
          font-size: 14px;
          color: #333;
          margin-bottom: 8px;
          max-width: 250px;
          display: -webkit-box;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .product-price {
          font-size: 14px;
          color: #ff6b81;
          margin-bottom: 4px;
        }
        
        .product-stock {
          .stock-warning {
            font-size: 12px;
            color: #f56c6c;
            background-color: #fef0f0;
            padding: 2px 6px;
            border-radius: 3px;
          }
        }
      }
    }

    .subtotal {
      color: #ff6b81;
      font-weight: 500;
    }

    .delete-btn {
      color: #ff4d6d;

      &:hover {
        color: #ff6b81;
      }
    }
  }
}

.cart-footer {
  max-width: 1200px;
  margin: 20px auto 0;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);

  .cart-summary {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 20px;

    .summary-info {
      display: flex;
      align-items: center;
      gap: 20px;

      .total-price {
        font-size: 16px;
        color: #333;

        .price {
          font-size: 20px;
          color: #ff6b81;
          font-weight: 600;
        }
      }
    }

    .summary-actions {
      display: flex;
      gap: 10px;

      .clear-btn {
        color: #666;
      }

      .checkout-btn {
        background-color: #ff6b81;
        border-color: #ff6b81;
        padding: 10px 30px;
        font-size: 16px;

        &:hover {
          background-color: #ff4d6d;
          border-color: #ff4d6d;
        }

        &:disabled {
          background-color: #f5f5f5;
          border-color: #e5e5e5;
          color: #999;
        }
      }
    }
  }
}
</style>