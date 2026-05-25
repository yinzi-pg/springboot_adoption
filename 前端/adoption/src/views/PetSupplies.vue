<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { 
  Edit, 
  Delete,
  Search,
  RefreshLeft,
  ChatLineSquare,
  CollectionTag,
  PictureFilled,
  InfoFilled,
  Calendar,
  Plus,
  Money
} from '@element-plus/icons-vue';
import { 
  getSupplyList, 
  supplyAddService, 
  supplyDeleteService, 
  supplyUpdateService,
  searchSuppliesService
} from '@/api/petsupplies';

// 图片上传前校验（与注册页完全一致）
const beforeUpload = (file) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
  if (!isJpgOrPng) {
    ElMessage.error('只能上传JPG/PNG格式的图片');
    return false;
  }
  const isLt2M = file.size / 1024 / 1024 < 10;
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB');
    return false;
  }
  return true;
};

// 上传成功回调（后端返回格式与注册页一致）
const handleImageUploadSuccess = (result) => {
  if (result.code === 0) {
    supplyModel.value.supplyImage = result.data;
    console.log('上传成功，图片路径：', result.data); // 添加日志
    ElMessage.success('图片上传成功');
  } else {
    ElMessage.error('图片上传失败：' + result.msg);
  }
};

const handleUploadError = (err) => {
  if (err.message.includes('exceeded size limit')) {
    ElMessage.error('文件大小超过限制，请上传1MB以内的图片');
  } else {
    ElMessage.error('上传失败，请稍后重试');
  }
};

//分页条数据模型
const pageNum = ref(1);//当前页码
const pageSize = ref(3);//每页条数
const total = ref(0);//总条数
//每页条数改变
const onSizeChange = (size) => {
    pageSize.value = size;
    loadSupplies();
}
//当前页码改变
const onCurrentChange = (num) => {
    pageNum.value = num;
    loadSupplies();
}

// 宠物用品列表数据
const supplies = ref([]);
// 弹窗控制
const dialogVisible = ref(false);
const detailVisible = ref(false);
// 弹窗标题
const title = ref('');
// 表单引用（用于校验）
const supplyFormRef = ref(null);
// 添加/编辑表单数据模型
const supplyModel = ref({
  supplyId: null,
  supplyName: '',
  supplyType: '',
  brand: '',
  price: 0,
  stock: 0,
  status: '上架',
  description: '',
  supplyImage: '',
  createDate: ''
});

// 详情数据模型
const detailModel = ref({
  supplyId: '',
  supplyName: '',
  supplyType: '',
  brand: '',
  price: 0,
  stock: 0,
  status: '',
  description: '',
  supplyImage: '',
  createDate: ''
});

// 搜索关键词
const searchKeyword = ref('');

// 表单校验规则
const rules = ref({
  supplyName: [
    { required: true, message: '请输入用品名称', trigger: 'blur' },
    { min: 1, max: 50, message: '长度1-50位', trigger: 'blur' }
  ],
  supplyType: [
    { required: true, message: '请选择用品类型', trigger: 'change' }
  ],
  brand: [
    { required: true, message: '请输入品牌', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' },
    { type: 'number', min: 0, message: '价格不能为负数', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入库存数量', trigger: 'blur' },
    { type: 'number', min: 0, message: '库存不能为负数', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
});

// 获取所有宠物用品数据
const loadSupplies = async () => {
  try {
    let params = {
        pageNum: pageNum.value,
        pageSize: pageSize.value
    }
    const res = await getSupplyList(params);
    if (res.code === 0) {
      supplies.value = res.data.items;
      total.value = res.data.total;
    } else {
      ElMessage.error('获取宠物用品信息失败：' + res.msg);
    }
  } catch (error) {
    ElMessage.error('网络请求异常');
  }
};

// 初始化加载数据
onMounted(() => {
  loadSupplies();
});

// 搜索宠物用品
const handleSearch = async () => {
  const keyword = searchKeyword.value.trim();
  if (!keyword) {
    loadSupplies();
    return;
  }

  try {
    const result = await searchSuppliesService(keyword);
    if (result.code === 0) {
      supplies.value = result.data;
      if (supplies.value.length === 0) {
        ElMessage.info(`未找到包含"${keyword}"的宠物用品`);
      }
    } else {
      ElMessage.error('搜索宠物用品失败，请重试');
    }
  } catch (error) {
    ElMessage.error('搜索接口异常');
    console.error('宠物用品搜索异常:', error);
  }
};

// 重置搜索
const resetSearch = () => {
  searchKeyword.value = '';
  loadSupplies();
  ElMessage.success('已重置为全部宠物用品');
};

// 删除宠物用品
const deleteSupply = (row) => {
  ElMessageBox.confirm(
    `确认要删除宠物用品【${row.supplyName}】吗？删除后不可恢复！`,
    '删除确认',
    {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'danger',
      center: true
    }
  ).then(async () => {
    try {
      const res = await supplyDeleteService(row.supplyId);
      if (res.code === 0) {
        ElMessage.success('删除成功');
        loadSupplies(); // 刷新列表
      } else {
        ElMessage.error(`删除失败：${res.msg || '未知错误'}`);
      }
    } catch (error) {
      ElMessage.error('操作异常');
    }
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
};

// 打开添加弹窗
const handleAddClick = () => {
  resetSupplyForm();
  title.value = '添加宠物用品';
  dialogVisible.value = true;
};

// 添加宠物用品
const addSupply = async () => {
  try {
    // 表单校验
    await supplyFormRef.value.validate();
    
    const res = await supplyAddService(supplyModel.value);
    if (res.code === 0) {
      ElMessage.success('添加成功');
      dialogVisible.value = false;
      resetSupplyForm();
      loadSupplies(); // 刷新列表
    } else {
      ElMessage.error(`添加失败：${res.msg || '未知错误'}`);
    }
  } catch (error) {
    // 校验失败时不处理
    if (error.name === 'ValidationError') return;
    ElMessage.error('操作异常');
  }
};

// 编辑回显
const updateSupplyInformationEcho = (row) => {
  title.value = '修改宠物用品信息';
  dialogVisible.value = true;
  // 深拷贝避免修改原始数据
  supplyModel.value = { ...row };
};

// 修改宠物用品
const updateSupply = async () => {
  try {
    // 表单校验
    await supplyFormRef.value.validate();
    
    const res = await supplyUpdateService(supplyModel.value);
    if (res.code === 0) {
      ElMessage.success('修改成功');
      dialogVisible.value = false;
      resetSupplyForm();
      loadSupplies(); // 刷新列表
    } else {
      ElMessage.error(`修改失败：${res.msg || '未知错误'}`);
    }
  } catch (error) {
    // 校验失败时不处理
    if (error.name === 'ValidationError') return;
    ElMessage.error('操作异常');
  }
};

// 查看详情
const handleViewDetail = (row) => {
  detailModel.value = { ...row };
  detailVisible.value = true;
};

// 重置表单
const resetSupplyForm = () => {
  supplyModel.value = {
    supplyId: null,
    supplyName: '',
    supplyType: '',
    brand: '',
    price: 0,
    stock: 0,
    status: '上架',
    description: '',
    supplyImage: '',
    createDate: ''
  };

  if (supplyFormRef.value && typeof supplyFormRef.value.clearValidate === 'function') {
    supplyFormRef.value.clearValidate();
  }
};

// 状态标签类型映射
const getStatusType = (status) => {
  switch(status) {
    case '上架': return 'success';
    case '下架': return 'danger';
    default: return 'info';
  }
};

const getImageUrl = (path) => {
  if (path?.startsWith('/assets/')) {
    try {
      // Vite 中通过 new URL 加载图片
      return new URL(`../assets${path}`, import.meta.url).href
    } catch (e) {
      console.error('无法加载图片:', path)
      return new URL('../assets/a.png', import.meta.url).href
    }
  }
  return path || new URL('../assets/a.png', import.meta.url).href
}

</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span class="page-title">宠物用品管理</span>
        <div class="operation-group">
          <div class="search-group">
            <el-input 
              v-model="searchKeyword" 
              placeholder="搜索用品名称/品牌/类型..." 
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
          <el-button 
            class="pink-add-btn" 
            type="primary" 
            @click="handleAddClick"
          >
            添加宠物用品
          </el-button>
        </div>
      </div>
    </template>

    <!-- 宠物用品表格 -->
    <el-table :data="supplies" style="width: 100%" border stripe>
      <el-table-column label="序号" width="100" type="index" align="center" />
      <el-table-column label="用品ID" prop="supplyId" width="100" align="center" />
      <el-table-column label="用品名称" prop="supplyName" width="150" />
      <!-- 图片列 -->
      <el-table-column label="用品图片" width="120" align="center">
        <template #default="{ row }">
          <img 
            v-if="row.supplyImage" 
            :src="getImageUrl(row.supplyImage)" 
            alt="商品照片" 
            class="pet-img"
          >
          
        </template>
      </el-table-column>
      <el-table-column label="用品类型" prop="supplyType" width="120" />
      <el-table-column label="品牌" prop="brand" width="120" />
      <el-table-column label="价格（元）" prop="price" width="120" align="center">
        <template #default="{ row }">
          ¥{{ row.price.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column label="库存" prop="stock" width="100" align="center" />
      <el-table-column label="状态" width="120" align="center">
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
      <el-table-column label="创建时间" prop="createDate" width="180" align="center" />
      <el-table-column label="操作" width="220" align="center">
        <template #default="{ row }">
          <el-button 
            :icon="Edit" 
            circle 
            plain 
            type="primary" 
            @click="updateSupplyInformationEcho(row)"
            size="small" 
            title="编辑"
          ></el-button>
          <el-button 
            :icon="Delete" 
            circle 
            plain 
            type="danger" 
            @click="deleteSupply(row)"
            size="small" 
            title="删除"
          ></el-button>
          <el-button 
            :icon="ChatLineSquare" 
            circle 
            plain 
            type="info" 
            @click="handleViewDetail(row)"
            size="small" 
            title="查看详情"
          ></el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="暂无宠物用品信息" />
      </template>
    </el-table>
     <!-- 分页条 -->
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[1, 3]"
            layout="jumper, total, sizes, prev, pager, next" background :total="total" @size-change="onSizeChange"
            @current-change="onCurrentChange" style="margin-top: 20px; justify-content: flex-end" />
    <!-- 添加/编辑宠物用品弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="title" 
      width="40%"
      destroy-on-close
    >
      <el-form 
        :model="supplyModel" 
        :rules="rules" 
        label-width="120px"
        ref="supplyFormRef"
        class="order-form"
      >
        <el-form-item label="用品名称" prop="supplyName">
          <el-input v-model="supplyModel.supplyName" placeholder="请输入用品名称" />
        </el-form-item>
        
        <el-form-item label="用品类型" prop="supplyType">
          <el-select v-model="supplyModel.supplyType" placeholder="请选择用品类型">
            <el-option label="食品零食" value="食品零食" />
            <el-option label="洗护用品" value="洗护用品" />
            <el-option label="玩具互动" value="玩具互动" />
            <el-option label="窝垫家具" value="窝垫家具" />
            <el-option label="出行装备" value="出行装备" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="品牌" prop="brand">
          <el-input v-model="supplyModel.brand" placeholder="请输入品牌" />
        </el-form-item>
        
        <el-form-item label="价格（元）" prop="price">
          <el-input-number 
            v-model="supplyModel.price" 
            :min="0" 
            :precision="2" 
            placeholder="请输入价格"
          />
        </el-form-item>
        
        <el-form-item label="库存数量" prop="stock">
          <el-input-number 
            v-model="supplyModel.stock" 
            :min="0" 
            placeholder="请输入库存"
          />
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-select v-model="supplyModel.status" placeholder="请选择状态">
            <el-option label="上架" value="上架" />
            <el-option label="下架" value="下架" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="描述" prop="description">
          <el-input 
            v-model="supplyModel.description" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入用品描述"
          />
        </el-form-item>
      
        <el-form-item label="宠物用品图片" prop="supplyImage">
                <el-upload 
                    class="avatar-uploader"
                    :auto-upload="true"
                    :show-file-list="false"
                    action="http://localhost:8080/springboot_adoption/upload" 
                    :on-success="handleImageUploadSuccess"
                    :before-upload="beforeUpload"
                    name="file"
                    :on-error="handleUploadError" 
                >
                    <img v-if="supplyModel.supplyImage" :src="getImageUrl(supplyModel.supplyImage)" class="avatar" />
                    <div v-else class="upload-placeholder">
                        <el-icon class="upload-icon">
                            <Plus />
                        </el-icon>
                    </div>
                </el-upload>
                </el-form-item>
      </el-form>
      
      <template #footer>
        <span>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button 
            class="pink-add-btn" 
            type="primary" 
            @click="title === '添加宠物用品' ? addSupply() : updateSupply()"
          >
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 宠物用品详情弹窗 -->
    <el-dialog 
      v-model="detailVisible" 
      title="宠物用品详情" 
      width="50%" 
      destroy-on-close
      :close-on-click-modal="false"
      class="detail-dialog"
    >
      <!-- 详情头部：用品编号+装饰线 -->
      <div class="detail-header">
        <div class="detail-header__title">
          <CollectionTag class="icon-cart" />
          <span>用品编号：{{ detailModel.supplyId }}</span>
        </div>
        <div class="detail-header__line"></div>
      </div>

      <!-- 详情内容：卡片式布局 -->
      <div class="detail-content">
        <!-- 第一行：基本信息卡片 -->
        <div class="detail-card">
          <div class="detail-card__title">
            <InfoFilled class="icon-title" />
            <span>基本信息</span>
          </div>
          <div class="detail-card__content">
            <div class="detail-item">
              <span class="detail-label">用品名称：</span>
              <span class="detail-value">{{ detailModel.supplyName }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">用品类型：</span>
              <span class="detail-value">{{ detailModel.supplyType }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">品牌：</span>
              <span class="detail-value">{{ detailModel.brand }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">状态：</span>
              <span class="detail-value">
                <el-tag :type="getStatusType(detailModel.status)" effect="light" class="status-tag">
                  {{ detailModel.status }}
                </el-tag>
              </span>
            </div>
          </div>
        </div>

        <!-- 第二行：库存价格卡片 -->
        <div class="detail-card">
          <div class="detail-card__title">
            <Money class="icon-title" />
            <span>库存与价格</span>
          </div>
          <div class="detail-card__content">
            <div class="detail-item">
              <span class="detail-label">价格：</span>
              <span class="detail-value">
                <Money class="icon-small" />
                ¥{{ detailModel.price.toFixed(2) }}
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">库存数量：</span>
              <span class="detail-value">{{ detailModel.stock }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">创建时间：</span>
              <span class="detail-value">
                <Calendar class="icon-small" />
                {{ detailModel.createDate }}
              </span>
            </div>
          </div>
        </div>

        <!-- 第三行：图片与描述卡片 -->
        <div class="detail-card">
          <div class="detail-card__title">
            <PictureFilled class="icon-title" />
            <span>图片与描述</span>
          </div>
          <div class="detail-card__content">
            <div class="detail-item">
              <span class="detail-label">用品图片：</span>
              <span class="detail-value">
                <img 
                  :src="getImageUrl(detailModel.supplyImage)" 
                  alt="用品照片" 
                  class="supply-img"
                >
              </span>
            </div>
            <div class="detail-item" style="flex-direction: column; align-items: flex-start;">
              <span class="detail-label">用品描述：</span>
              <div class="detail-remark">
                {{ detailModel.description || '无描述信息' }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 详情页底部按钮 -->
      <template #footer>
        <el-button 
          class="close-btn" 
          @click="detailVisible = false"
        >
          关闭
        </el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

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

    .pink-add-btn {
      background-color: #ff6b81 !important;
      border-color: #ff6b81 !important;
      color: #ffffff !important;
      box-shadow: 0 2px 4px rgba(255, 107, 129, 0.3);

      &:hover {
        background-color: #ff4d6d !important;
        border-color: #ff4d6d !important;
        transform: translateY(-1px);
        box-shadow: 0 4px 8px rgba(255, 107, 129, 0.5);
      }

      &:active {
        background-color: #ff3355 !important;
        border-color: #ff3355 !important;
      }

      border-radius: 8px !important;
      padding: 12px 24px !important;
    }
  }

  .supply-img {
    max-width: 120px;
    max-height: 120px;
    border-radius: 8px;
    margin-top: 8px;
  }

  .no-img {
    color: #999;
    font-size: 12px;
  }

  .el-table {
    margin-top: 20px;

    :deep(.cell) {
      padding: 12px 10px;
    }
  }

  .el-input-number, .el-select, .el-textarea {
    width: 100%;
  }

  /* 详情弹窗美化样式 */
  .detail-dialog {
    .el-dialog__body {
      padding: 20px 30px;
      max-height: 60vh;
      overflow-y: auto;
    }
  }

  .detail-header {
    margin-bottom: 20px;
    
    &__title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 16px;
      font-weight: 600;
      color: #ff6b81;
      margin-bottom: 10px;
      
      .icon-cart {
        width: 20px;
        height: 20px;
      }
    }
    
    &__line {
      height: 2px;
      background: linear-gradient(90deg, #ffccd5, transparent);
      border-radius: 1px;
    }
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

  .detail-remark {
    line-height: 24px;
    padding: 12px;
    background-color: #fff8f9;
    border-radius: 6px;
    color: #666;
    min-height: 80px;
    width: 100%;
    white-space: pre-wrap;
    margin-top: 8px;
  }

  .close-btn {
    background-color: #fff !important;
    border-color: #ffccd5 !important;
    color: #ff6b81 !important;
    padding: 8px 24px;
    border-radius: 6px;
    
    &:hover {
      background-color: #fff5f7 !important;
    }
  }

  .status-tag {
    padding: 2px 8px;
    border-radius: 4px;
  }

  .pet-img {
  max-width: 60px;
  max-height: 60px; /* 添加最大高度限制 */
  display: inline-block; /* 确保图片正确显示 */
}

  /* 宠物图片上传样式（复用现有粉色主题） */
.avatar-uploader {
  width: 120px;
  height: 120px;
  border-radius: 50%; /* 圆形上传框，与注册页一致 */
  border: 2px dashed #ffb6c1; /* 粉色虚线边框 */
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;

  &:hover {
    border-color: #ff758c; /*  hover时变色，与其他按钮一致 */
    transform: scale(1.05);
    background-color: rgba(255, 236, 241, 0.2);
  }

  .avatar {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    object-fit: cover;
    display: block;
  }

  .upload-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .upload-icon {
    font-size: 32px;
    color: #ffb6c1; /* 粉色图标，与主题统一 */
  }
}
}
</style>