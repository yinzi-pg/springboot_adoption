<script setup>
import { ref, reactive, unref } from 'vue';
import { Edit, Delete, Search, RefreshLeft, Document, User, Clock, InfoFilled,Plus } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox, ElForm } from 'element-plus';

// 导入公告信息管理相关方法
import { getPublicList, publicAddService, publicDeleteService, publicUpdateService, searchPublicService } from '@/api/publicInformation';

// 图片上传前校验
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

// 上传成功回调
const handleImageUploadSuccess = (result) => {
  if (result.code === 0) {
    publicModel.publicImage = result.data;
    ElMessage.success('图片上传成功');
  } else {
    ElMessage.error('图片上传失败：' + result.msg);
  }
};

// 图片路径处理
const BACKEND_BASE = 'http://localhost:8080/springboot_adoption';

const getImageUrl = (path) => {
  if (path.startsWith('/images/')) {
    return BACKEND_BASE + path;
  }
}

// 分页相关
const pageNum = ref(1);
const pageSize = ref(5);
const total = ref(0);

const loadData = () => {
  if (searchKeyword.value.trim()) {
    handleSearch();
  } else {
    getAllPublics();
  }
};

const onSizeChange = (size) => {
  pageSize.value = size;
  loadData();
};

const onCurrentChange = (num) => {
  pageNum.value = num;
  loadData();
};

// 公告列表数据
const publics = ref([]);

// 控制弹窗显示
const dialogVisible =  ref(false);
const detailVisible = ref(false);

// 弹窗标题
const title = ref('');

// 公告数据模型
const publicModel = reactive({
  publicId: null,
  title: '',
  content: '',
  author: '',
  status: '',
  publishTime: '',
  publicImage:''   
});

// 详情模型
const detailModel = reactive({
  publicId: '',
  title: '',
  content: '',
  author: '',
  status: '',
  publishTime: ''
});

// 搜索关键词
const searchKeyword = ref('');

// 表单校验规则
const rules = reactive({
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { min: 1, max: 50, message: '标题长度1-50位', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入公告内容', trigger: 'blur' },
    { min: 1, max: 1000, message: '内容长度1-1000位', trigger: 'blur' }
  ],
  author: [
    { required: true, message: '请输入发布人', trigger: 'blur' },
    { min: 1, max: 20, message: '发布人名称1-20位', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择公告状态', trigger: 'change' }
  ]
});

// 表单引用
const publicFormRef = ref(null);

// 获取所有公告信息
const getAllPublics = async () => {
  try {
    let params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    };
    const res = await getPublicList(params);
    if (res.code === 0) {
      publics.value = res.data.items;
      total.value = res.data.total;
    } else {
      publics.value = [];
      ElMessage.warning('未获取到公告数据');
    }
  } catch (error) {
    ElMessage.error('获取公告列表失败，请重试');
    console.error('公告列表接口异常:', error);
  }
};

// 初始化加载公告数据
getAllPublics();

// 搜索公告
const handleSearch = async () => {
  const keyword = searchKeyword.value.trim();
  if (!keyword) {
    getAllPublics();
    return;
  }
  try {
    const result = await searchPublicService(keyword,pageNum.value,pageSize.value);
    if (result.code === 0) {
      publics.value = result.data.items;
      total.value = result.data.total;
      if (publics.value.length === 0) {
        ElMessage.info(`未找到包含"${keyword}"的公告`);
      }
    } else {
      ElMessage.error('搜索公告失败，请重试');
    }
  } catch (error) {
    ElMessage.error('搜索接口异常');
    console.error('公告搜索异常:', error);
  }
};

// 重置搜索
const resetSearch = () => {
  searchKeyword.value = '';
  getAllPublics();
  ElMessage.success('已重置为全部公告');
};

// 删除公告
const deletePublic = (row) => {
  ElMessageBox.confirm(
    `确认要删除公告【${row.title}】吗？删除后不可恢复！`,
    '删除确认',
    {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'danger',
      center: true
    }
  ).then(async () => {
    try {
      const res = await publicDeleteService(row.publicId);
      if (res.code === 0) {
        ElMessage.success('公告删除成功');
        getAllPublics();
      } else {
        ElMessage.error(`删除失败：${res.message || '未知错误'}`);
      }
    } catch (error) {
      ElMessage.error('删除接口异常，请重试');
      console.error('公告删除异常:', error);
    }
  }).catch(() => {
    ElMessage.info('已取消删除公告');
  });
};

// 添加公告
const addPublic = async () => {
  try {
    if (publicModel.status === '已发布') {
      publicModel.publishTime = formatDateTime(new Date());
    }
    
    const res = await publicAddService(publicModel);
    if (res.code === 0) {
      ElMessage.success('公告添加成功');
      dialogVisible.value = false;
      getAllPublics();
      resetPublicModel();
    } else {
      ElMessage.error(`添加失败：${res.message || '未知错误'}`);
    }
  } catch (error) {
    ElMessage.error('添加公告接口异常，请重试');
    console.error('公告添加异常:', error);
  }
};

// 编辑公告回显
const updatePublicEcho = (row) => {
  title.value = '修改公告信息';
  dialogVisible.value = true;
  const publicData = JSON.parse(JSON.stringify(row));
  Object.assign(publicModel, publicData);
};

// 修改公告
const updatePublic = async () => {
  try {
    if (publicModel.status === '已发布' && !publicModel.publishTime) {
      publicModel.publishTime = formatDateTime(new Date());
    }
    
    const res = await publicUpdateService(publicModel);
    if (res.code === 0) {
      ElMessage.success('公告修改成功');
      dialogVisible.value = false;
      getAllPublics();
      resetPublicModel();
    } else {
      ElMessage.error(`修改失败：${res.message || '未知错误'}`);
    }
  } catch (error) {
    ElMessage.error('修改公告接口异常，请重试');
    console.error('公告修改异常:', error);
  }
};

// 查看公告详情
const handleViewDetail = (row) => {
  const publicData = JSON.parse(JSON.stringify(row));
  Object.assign(detailModel, publicData);
  detailVisible.value = true;
};

// 重置公告数据模型
const resetPublicModel = () => {
  Object.assign(publicModel, {
    publicId: null,
    title: '',
    content: '',
    author: '',
    status: '',
    publishTime: ''
  });

  const formRef = unref(publicFormRef);
  if (formRef && typeof formRef.clearValidate === 'function') {
    formRef.clearValidate();
  }
};

// 表单提交
const handleFormSubmit = async () => {
  const formRef = unref(publicFormRef);
  if (!formRef) return;

  try {
    await formRef.validate();
    
    if (title.value === '添加公告信息') {
      delete publicModel.publicId;
      await addPublic();
    } else {
      if (!publicModel.publicId) {
        ElMessage.error('编辑公告缺少公告ID，请刷新页面重试');
        return;
      }
      await updatePublic();
    }
  } catch (error) {
    console.error('表单校验失败:', error);
  }
};

// 格式化状态标签类型
const getStatusType = (status) => {
  switch (status) {
    case '已发布':
      return 'success';
    case '草稿':
      return 'info';
    default:
      return 'default';
  }
};

// 日期格式化函数
const formatDateTime = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span class="page-title">公告信息管理</span>
        <div class="operation-group">
          <div class="search-group">
            <el-input 
              v-model="searchKeyword" 
              placeholder="搜索公告标题/发布人..." 
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
            @click="dialogVisible = true; title = '添加公告信息'; resetPublicModel()"
          >
            添加公告信息
          </el-button>
        </div>
      </div>
    </template>

    <!-- 公告列表表格 -->
    <el-table :data="publics" style="width: 100%" border stripe>
      <el-table-column label="序号" width="100" type="index" align="center" />
      <el-table-column label="公告ID" prop="publicId" align="center" />
      <el-table-column label="公告标题" prop="title" show-overflow-tooltip align="center" />
      <!-- 公告图片列 -->
      <el-table-column label="公告图片" align="center" width="140">
        <template #default="{ row }">
          <div v-if="row.publicImage" class="image-container">
            <img 
              :src="getImageUrl(row.publicImage)"  
              class="table-image" 
              alt="公告图片"
            >
          </div>
          <div v-else class="image-placeholder">
            <InfoFilled class="placeholder-icon" />
          </div>
        </template>
      </el-table-column>
      <el-table-column label="发布人" prop="author" align="center" />
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
      <el-table-column label="发布时间" prop="publishTime" width="180" align="center" />
      <el-table-column label="公告内容" prop="content" width="300" show-overflow-tooltip align="center" />
      <el-table-column label="操作" width="220" align="center">
        <template #default="{ row }">
          <el-button 
            :icon="Edit" 
            circle 
            plain 
            type="primary" 
            @click="updatePublicEcho(row)"
            size="small" 
            title="编辑"
          ></el-button>
          <el-button 
            :icon="Delete" 
            circle 
            plain 
            type="danger" 
            @click="deletePublic(row)"
            size="small" 
            title="删除"
          ></el-button>
          <el-button 
            :icon="InfoFilled" 
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
        <el-empty description="暂无公告信息" />
      </template>
    </el-table>
    
    <!-- 分页条 -->
    <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[1, 3, 5]"
      layout="jumper, total, sizes, prev, pager, next" background :total="total" @size-change="onSizeChange"
      @current-change="onCurrentChange" style="margin-top: 20px; justify-content: flex-end" />
    
    <!-- 添加/编辑公告弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="title" 
      width="600px"
      destroy-on-close
    >
      <el-form 
        :model="publicModel" 
        :rules="rules" 
        label-width="100px"
        ref="publicFormRef"
        class="public-form"
      >
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="publicModel.title" placeholder="请输入公告标题" />
        </el-form-item>
        
        <el-form-item label="发布人" prop="author">
          <el-input v-model="publicModel.author" placeholder="请输入发布人名称" />
        </el-form-item>
        
        <!-- 公告图片上传 -->
        <el-form-item label="公告图片" prop="publicImage">
          <el-upload 
            class="avatar-uploader"
            :auto-upload="true"
            :show-file-list="false"
            action="http://localhost:8080/springboot_adoption/upload" 
            :on-success="handleImageUploadSuccess"
            :before-upload="beforeUpload"
            name="file"
          >
            <img v-if="publicModel.publicImage" :src="getImageUrl(publicModel.publicImage)" class="avatar" />
            <div v-else class="upload-placeholder">
              <el-icon class="upload-icon">
                <Plus />
              </el-icon>
            </div>
          </el-upload>
        </el-form-item>

        <el-form-item label="公告状态" prop="status">
          <el-select v-model="publicModel.status" placeholder="请选择公告状态">
            <el-option label="已发布" value="已发布" />
            <el-option label="草稿" value="草稿" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="公告内容" prop="content">
          <el-input 
            v-model="publicModel.content" 
            type="textarea" 
            :rows="6" 
            placeholder="请输入公告详细内容"
          />
        </el-form-item>
        
        <el-form-item label="发布时间" v-if="publicModel.publishTime">
          <el-input 
            v-model="publicModel.publishTime" 
            disabled 
            placeholder="发布时自动生成"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span>
          <el-button @click="dialogVisible = false; resetPublicModel()">取消</el-button>
          <el-button 
            class="pink-add-btn" 
            type="primary" 
            @click="handleFormSubmit"
          >
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 公告详情弹窗 -->
    <el-dialog 
      v-model="detailVisible" 
      title="公告详情" 
      width="50%" 
      destroy-on-close
      :close-on-click-modal="false"
      class="detail-dialog"
    >
      <!-- 详情头部 -->
      <div class="detail-header">
        <div class="detail-header__title">
          <Document class="icon-document" />
          <span>公告ID：{{ detailModel.publicId }}</span>
        </div>
        <div class="detail-header__line"></div>
      </div>

      <!-- 详情内容 -->
      <div class="detail-content">
        <!-- 基本信息卡片 -->
        <div class="detail-card">
          <div class="detail-card__title">
            <InfoFilled class="icon-title" />
            <span>基本信息</span>
          </div>
          <div class="detail-card__content">
            <!-- 显示详情中的图片 -->
            <div v-if="detailModel.publicImage" class="detail-image-container">
              <span class="detail-label">公告图片：</span>
              <img 
                :src="getImageUrl(detailModel.publicImage)"  
                class="detail-image" 
                alt="公告图片"
              >
            </div>
            
            <div class="detail-item">
              <span class="detail-label">公告标题：</span>
              <span class="detail-value">{{ detailModel.title }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">发布人：</span>
              <span class="detail-value">
                <User class="icon-small" />
                {{ detailModel.author }}
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">公告状态：</span>
              <span class="detail-value">
                <el-tag :type="getStatusType(detailModel.status)" effect="light" class="status-tag">
                  {{ detailModel.status }}
                </el-tag>
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">发布时间：</span>
              <span class="detail-value">
                <Clock class="icon-small" />
                {{ detailModel.publishTime || '未发布' }}
              </span>
            </div>
          </div>
        </div>

        <!-- 公告内容卡片 -->
        <div class="detail-card">
          <div class="detail-card__title">
            <Document class="icon-title" />
            <span>公告内容</span>
          </div>
          <div class="detail-card__content detail-content-text">
            {{ detailModel.content || '无公告内容' }}
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
  min-height:95vh;
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

  .status-tag {
    padding: 2px 8px;
    border-radius: 4px;
    font-size: 12px;
  }

  /* 表格中的图片样式 */
  .image-container {
    width: 80px;
    height: 80px;
    margin: 0 auto;
    border-radius: 6px;
    overflow: hidden;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  .table-image {
    width: 100%;
    height: 100%;
    object-fit: cover; /* 保持图片比例，填充容器 */
    transition: transform 0.3s ease;
  }
  
  .image-container:hover .table-image {
    transform: scale(1.05); /* 悬停时轻微放大 */
  }
  
  .image-placeholder {
    width: 80px;
    height: 80px;
    margin: 0 auto;
    background-color: #f5f5f5;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #ccc;
    border-radius: 6px;
  }
  
  .placeholder-icon {
    font-size: 24px;
  }

  /* 弹窗中图片上传样式 */
  .avatar-uploader {
    width: 150px; /* 适当增大上传区域 */
    height: 150px;
    border-radius: 8px; /* 改为圆角矩形更适合公告图片 */
    border: 2px dashed #ffb6c1;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
  }

  .avatar-uploader:hover {
    border-color: #ff758c;
    transform: translateY(-2px);
    background-color: rgba(255, 236, 241, 0.2);
  }

  .avatar {
    width: 100%;
    height: 100%;
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
    color: #ffb6c1;
  }

  /* 详情弹窗中的图片样式 */
  .detail-image-container {
    display: flex;
    margin-bottom: 16px;
    align-items: center;
    
    .detail-label {
      width: 100px;
      color: #888;
      text-align: right;
      padding-right: 16px;
      flex-shrink: 0;
    }
  }
  
  .detail-image {
    width: 200px;
    height: 140px;
    object-fit: cover;
    border-radius: 6px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }

  /* 详情弹窗其他样式 */
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
      
      .icon-document {
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

  .detail-content-text {
    line-height: 24px;
    padding: 12px;
    background-color: #fff8f9;
    border-radius: 6px;
    color: #666;
    min-height: 120px;
    white-space: pre-wrap;
    text-align: justify;
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
}
</style>
