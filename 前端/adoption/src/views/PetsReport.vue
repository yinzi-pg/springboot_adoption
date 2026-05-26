<script setup>
import { ref, reactive, unref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { 
  Edit, 
  Delete,
  Plus,
  Search,
  RefreshLeft,
  PictureFilled,
  MapLocation,
  Calendar,
  InfoFilled,
  Phone,
} from '@element-plus/icons-vue';


// 图片上传前校验（与注册页完全一致）
const beforeUpload = (file) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
  if (!isJpgOrPng) {
    ElMessage.error('只能上传JPG/PNG格式的图片');
    return false;
  }
  const isLt2M = file.size / 1024 / 1024 < 5;
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB');
    return false;
  }
  return true;
};

// 上传成功回调（后端返回格式与注册页一致）
const handleImageUploadSuccess = (result) => {
  // 后端返回格式：{ code: 0, data: "http://localhost:8080/..." }
  if (result.code === 0) {
    reportModel.reportImage = result.data; // 绑定图片URL到宠物模型
    ElMessage.success('图片上传成功');
  } else {
    ElMessage.error('图片上传失败：' + result.msg);
  }
};

//分页条数据模型
const pageNum = ref(1);//当前页码
const pageSize = ref(5);//每页条数
const total = ref(0);//总条数
//每页条数改变
const onSizeChange = (size) => {
    pageSize.value = size;
    getAllReports();
}
//当前页码改变
const onCurrentChange = (num) => {
    pageNum.value = num;
    getAllReports();
}

// 导入流浪宠物上报相关的接口方法
import { 
  getReportList, 
  reportAddService, 
  reportDeleteService, 
  reportUpdateService,
  searchReportService 
} from '@/api/petsReport';

// 流浪宠物上报列表数据
const reports = ref([]);
// 弹窗控制
const dialogVisible = ref(false);
const detailVisible = ref(false);
// 弹窗标题
const title = ref('');
// 表单引用（用于校验）
const reportFormRef = ref(null);
// 搜索关键词
const searchKeyword = ref('');

// 添加/编辑表单数据模型
const reportModel = reactive({
  reportId: null,
  petName: '',      // 宠物名字
  petType: '',      // 宠物种类（犬类/猫类等）
  petGender: '',    // 宠物性别（公/母）
  petAge: '',       // 宠物年龄（岁）
  findLocation: '', // 发现地点
  contactInfo: '',  // 联系方式
  reportDesc: '',   // 上报描述
  reportImage: '',  // 上报图片（URL）
  createDate: ''    // 创建时间
});

// 详情模型
const detailModel = reactive({
  reportId: '',
  petName: '',
  petType: '',
  petGender: '',
  petAge: '',
  findLocation: '',
  contactInfo: '',
  reportDesc: '',
  reportImage: '',
  createDate: ''
});

// 表单校验规则
const rules = reactive({
  petName: [
    { required: true, message: '请输入宠物名字', trigger: 'blur' },
    { min: 1, max: 20, message: '长度1-20位', trigger: 'blur' }
  ],
  petType: [
    { required: true, message: '请选择宠物种类', trigger: 'change' }
  ],
  petGender: [
    { required: true, message: '请选择宠物性别', trigger: 'change' }
  ],
  petAge: [
    { required: true, message: '请输入宠物年龄', trigger: 'blur' }
  ],
  findLocation: [
    { required: true, message: '请输入发现地点', trigger: 'blur' }
  ],
  contactInfo: [
    { required: true, message: '请输入联系方式', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的11位手机号码', trigger: 'blur' }
  ]
});

// 获取所有流浪宠物上报数据
const getAllReports = async () => {
  let params = {
        pageNum: pageNum.value,
        pageSize: pageSize.value
    }
  try {
    const res = await getReportList(params);
    if (res.code === 0) {
      reports.value = res.data.items;
      total.value = res.data.total;
    } else {
      ElMessage.error('获取流浪宠物上报信息失败：' + res.msg);
    }
  } catch (error) {
    ElMessage.error('网络请求异常');
    console.error('获取流浪宠物列表异常:', error);
  }
};

// 初始化加载数据
getAllReports();

/**
 * 搜索流浪宠物上报信息
 */
const handleSearch = async () => {
  const keyword = searchKeyword.value.trim();
  if (!keyword) {
    getAllReports();
    return;
  }

  try {
    const result = await searchReportService(keyword);
    if (result.code === 0) {
      reports.value = result.data;
      if (reports.value.length === 0) {
        ElMessage.info(`未找到包含"${keyword}"的上报信息`);
      }
    } else {
      ElMessage.error('搜索失败，请重试');
    }
  } catch (error) {
    ElMessage.error('搜索接口异常');
    console.error('搜索流浪宠物异常:', error);
  }
};

/**
 * 重置搜索
 */
const resetSearch = () => {
  searchKeyword.value = '';
  getAllReports();
  ElMessage.success('已重置为全部上报信息');
};

// 删除流浪宠物上报记录
const deleteReport = (row) => {
  ElMessageBox.confirm(
    `确认要删除【${row.petName}】的上报记录吗？删除后不可恢复！`,
    '删除确认',
    {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'danger',
      center: true
    }
  ).then(async () => {
    try {
      const res = await reportDeleteService(row.reportId);
      if (res.code === 0) {
        ElMessage.success('删除成功');
        getAllReports(); // 刷新列表
      } else {
        ElMessage.error('删除失败：' + res.msg);
      }
    } catch (error) {
      ElMessage.error('操作异常');
      console.error('删除流浪宠物上报异常:', error);
    }
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
};

// 打开添加弹窗
const handleAddClick = () => {
  resetReportForm();
  title.value = '添加流浪宠物上报';
  dialogVisible.value = true;
};

// 添加流浪宠物上报
const addReport = async () => {
  try {
    // 表单校验
    await unref(reportFormRef).validate();
    
    const res = await reportAddService(reportModel);
    if (res.code === 0) {
      ElMessage.success('添加成功');
      dialogVisible.value = false;
      getAllReports(); // 刷新列表
    } else {
      ElMessage.error('添加失败：' + res.msg);
    }
  } catch (error) {
    // 校验失败时不处理
    if (error.name === 'ValidationError') return;
    ElMessage.error('操作异常');
    console.error('添加流浪宠物上报异常:', error);
  }
};

// 编辑回显
const handleUpdateEcho = (row) => {
  resetReportForm();
  title.value = '修改流浪宠物上报信息';
  // 深拷贝避免修改原始数据
  Object.assign(reportModel, JSON.parse(JSON.stringify(row)));
  dialogVisible.value = true;
};

// 修改流浪宠物上报
const updateReport = async () => {
  try {
    // 表单校验
    await unref(reportFormRef).validate();
    
    const res = await reportUpdateService(reportModel);
    if (res.code === 0) {
      ElMessage.success('修改成功');
      dialogVisible.value = false;
      getAllReports(); // 刷新列表
    } else {
      ElMessage.error('修改失败：' + res.msg);
    }
  } catch (error) {
    // 校验失败时不处理
    if (error.name === 'ValidationError') return;
    ElMessage.error('操作异常');
    console.error('修改流浪宠物上报异常:', error);
  }
};

/**
 * 查看详情
 */
const handleViewDetail = (row) => {
  Object.assign(detailModel, JSON.parse(JSON.stringify(row)));
  detailVisible.value = true;
};

/**
 * 重置表单
 */
const resetReportForm = () => {
  Object.assign(reportModel, {
    reportId: null,
    petName: '',
    petType: '',
    petGender: '',
    petAge: '',
    findLocation: '',
    contactInfo: '',
    reportDesc: '',
    reportImage: '',
    createDate: ''
  });

  const formRef = unref(reportFormRef);
  if (formRef && typeof formRef.clearValidate === 'function') {
    formRef.clearValidate();
  }
};

// 图片路径处理
const getImageUrl = (path) => {
  if (!path) return '/assets/default-pet.png';
  
  if (path.startsWith('/assets/')) {
    try {
      return require(`@/assets${path}`);
    } catch (e) {
      console.error('无法加载图片:', path);
      return '/assets/default-pet.png'; // 默认宠物图片
    }
  }
  return path; // 完整URL直接返回
};
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span class="page-title">流浪宠物上报管理</span>
        <div class="operation-group">
          <div class="search-group">
            <el-input 
              v-model="searchKeyword" 
              placeholder="搜索宠物名/地点/联系方式..." 
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
            <Plus /> 添加流浪宠物上报
          </el-button>
        </div>
      </div>
    </template>

    <!-- 流浪宠物上报表格 -->
    <el-table 
      :data="reports" 
      style="width: 100%" 
      border 
      stripe
      :empty-text="reports.length === 0 ? '暂无流浪宠物上报信息' : ''"
    >
      <el-table-column label="序号" width="100" type="index" align="center" />
      <el-table-column label="上报ID" prop="reportId" align="center" />
      <el-table-column label="宠物名字" prop="petName" align="center" />
      <!-- 图片列 -->
      <el-table-column label="宠物图片" width="120" align="center">
        <template #default="{ row }">
          <img 
            v-if="row.reportImage" 
            :src="getImageUrl(row.reportImage)" 
            alt="宠物照片" 
            class="pet-img"
          >
          <span v-else class="no-img">无图片</span>
        </template>
      </el-table-column>
      <el-table-column label="宠物种类" prop="petType" align="center" />
      <el-table-column label="宠物性别" prop="petGender" align="center" />
      <el-table-column label="宠物年龄" prop="petAge" align="center" />
      <el-table-column label="发现地点" prop="findLocation" align="center" show-overflow-tooltip />
      <el-table-column label="联系方式" prop="contactInfo" align="center" />
      
      <el-table-column label="创建时间" prop="createDate" align="center" width="180" />
      <el-table-column label="操作" width="200" align="center">
        <template #default="{ row }">
          <el-button 
            :icon="Edit" 
            circle 
            plain 
            type="primary" 
            @click="handleUpdateEcho(row)"
            size="small"
            title="编辑"
          ></el-button>
          <el-button 
            :icon="Delete" 
            circle 
            plain 
            type="danger" 
            @click="deleteReport(row)"
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
        <el-empty description="暂无流浪宠物上报信息" />
      </template>
    </el-table>
    <!-- 分页条 -->
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[1, 3, 5]"
            layout="jumper, total, sizes, prev, pager, next" background :total="total" @size-change="onSizeChange"
            @current-change="onCurrentChange" style="margin-top: 20px; justify-content: flex-end" />

    <!-- 添加/编辑流浪宠物上报弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="title" 
      width="500px"
      destroy-on-close
    >
      <el-form 
        :model="reportModel" 
        :rules="rules" 
        label-width="100px"
        ref="reportFormRef"
        class="report-form"
      >
        <el-form-item label="宠物名字" prop="petName">
          <el-input v-model="reportModel.petName" placeholder="请输入宠物名字" />
        </el-form-item>
        
        <el-form-item label="宠物种类" prop="petType">
          <el-select v-model="reportModel.petType" placeholder="请选择宠物种类">
            <el-option label="犬类" value="犬类" />
            <el-option label="猫类" value="猫类" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="宠物性别" prop="petGender">
          <el-select v-model="reportModel.petGender" placeholder="请选择宠物性别">
            <el-option label="公" value="公" />
            <el-option label="母" value="母" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="宠物年龄" prop="petAge">
          <el-input v-model="reportModel.petAge" placeholder="请输入宠物年龄" />
        </el-form-item>
        
        <el-form-item label="发现地点" prop="findLocation">
          <el-input v-model="reportModel.findLocation" placeholder="请输入发现地点" />
        </el-form-item>
        
        <el-form-item label="联系方式" prop="contactInfo">
          <el-input v-model="reportModel.contactInfo" placeholder="请输入联系方式" />
        </el-form-item>
        
        <el-form-item label="上报描述" prop="reportDesc">
          <el-input 
            v-model="reportModel.reportDesc" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入上报描述"
          />
        </el-form-item>

        <el-form-item label="流浪动物图片" prop="reportImage">
                <el-upload 
                    class="avatar-uploader"
                    :auto-upload="true"
                    :show-file-list="false"
                    action="http://localhost:8080/springboot_adoption/upload" 
                    :on-success="handleImageUploadSuccess"
                    :before-upload="beforeUpload"
                    name="file"
                >
                    <img v-if="reportModel.reportImage" :src="getImageUrl(reportModel.reportImage)" class="avatar" />
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
            @click="title === '添加流浪宠物上报' ? addReport() : updateReport()"
          >
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog 
      v-model="detailVisible" 
      title="流浪宠物上报详情" 
      width="80%" 
      destroy-on-close
      :close-on-click-modal="false"
      class="detail-dialog"
    >
      <!-- 详情头部 -->
      <div class="detail-header">
        <div class="detail-header__title">
          <InfoFilled class="icon-info" />
          <span>上报编号：{{ detailModel.reportId }}</span>
        </div>
        <div class="detail-header__line"></div>
      </div>

      <!-- 详情内容 -->
      <div class="detail-content">
        <!-- 宠物信息卡片 -->
        <div class="detail-card">
          <div class="detail-card__title">
            <PictureFilled class="icon-title" />
            <span>宠物信息</span>
          </div>
          <div class="detail-card__content">
            <div class="detail-photo">
              <img 
                :src="getImageUrl(detailModel.reportImage)" 
                alt="宠物照片" 
                class="detail-img"
              >
            </div>
            <div class="detail-info">
              <div class="detail-item">
                <span class="detail-label">宠物名字：</span>
                <span class="detail-value">{{ detailModel.petName }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">宠物种类：</span>
                <span class="detail-value">{{ detailModel.petType }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">宠物性别：</span>
                <span class="detail-value">{{ detailModel.petGender }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">宠物年龄：</span>
                <span class="detail-value">{{ detailModel.petAge }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 上报信息卡片 - 美化部分 -->
        <div class="detail-card report-card">
          <div class="detail-card__title report-card__title">
            <MapLocation class="icon-title" />
            <span>上报信息</span>
          </div>
          <div class="detail-card__content report-info-container">
            <!-- 三列布局，确保数据在一行显示 -->
            <div class="report-info-item">
              <div class="report-info-icon">
                <MapLocation class="icon-small" />
              </div>
              <div class="report-info-content">
                <div class="report-info-label">发现地点</div>
                <div class="report-info-value">{{ detailModel.findLocation }}</div>
              </div>
            </div>
            
            <div class="report-info-item">
              <div class="report-info-icon">
                <Phone class="icon-small" />
              </div>
              <div class="report-info-content">
                <div class="report-info-label">联系方式</div>
                <div class="report-info-value">{{ detailModel.contactInfo }}</div>
              </div>
            </div>
            
            <div class="report-info-item">
              <div class="report-info-icon">
                <Calendar class="icon-small" />
              </div>
              <div class="report-info-content">
                <div class="report-info-label">上报时间</div>
                <div class="report-info-value">{{ detailModel.createDate }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 描述信息卡片 -->
        <div class="detail-card">
          <div class="detail-card__title">
            <InfoFilled class="icon-title" />
            <span>详细描述</span>
          </div>
          <div class="detail-card__content detail-remark">
            {{ detailModel.reportDesc || '无详细描述信息' }}
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

  .pet-img {
    max-width: 60px;
    max-height: 60px;
    border-radius: 4px;
    object-fit: cover;
  }

  .no-img {
    color: #999;
    font-size: 12px;
  }

  .report-form {
    margin-top: 10px;
  }

  /* 详情弹窗样式 */
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
      
      .icon-info {
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
      display: flex;
      gap: 20px;
      
      @media (max-width: 768px) {
        flex-direction: column;
      }
    }
  }

  .detail-photo {
    flex-shrink: 0;
    width: 160px;
    height: 160px;
    border-radius: 8px;
    overflow: hidden;
    border: 1px solid #f0f0f0;
    
    .detail-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .detail-info {
    flex: 1;
    min-width: 0;
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
      word-break: break-all;
      
      .icon-small {
        width: 16px;
        height: 16px;
        color: #ff8da1;
      }
    }
  }

  // 上报信息卡片美化样式
  .report-card {
    border-color: #ffe6ea;
    background-color: #fff9fa;
    
    &__title {
      background-color: #fff0f2;
      border-bottom-color: #ffccd5;
      color: #d92d42;
    }
  }
  
  .report-info-container {
    display: flex;
    width: 100%;
    gap: 16px;
    padding: 20px 16px;
    flex-wrap: wrap;
  }
  
  .report-info-item {
    flex: 1;
    min-width: 220px;
    display: flex;
    align-items: flex-start;
    gap: 12px;
    padding: 12px 16px;
    background-color: #ffffff;
    border-radius: 8px;
    border: 1px solid #fff0f2;
    transition: all 0.2s ease;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(255, 107, 129, 0.08);
      border-color: #ffccd5;
    }
  }
  
  .report-info-icon {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background-color: #fff0f2;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    
    .icon-small {
      color: #ff6b81;
      width: 18px;
      height: 18px;
    }
  }
  
  .report-info-content {
    flex: 1;
    min-width: 0;
  }
  
  .report-info-label {
    font-size: 13px;
    color: #ff6b81;
    margin-bottom: 4px;
    font-weight: 500;
  }
  
  .report-info-value {
    color: #333;
    word-break: break-all;
    line-height: 1.5;
  }

  .detail-remark {
    line-height: 24px;
    padding: 12px 16px;
    background-color: #fff8f9;
    border-radius: 6px;
    color: #666;
    min-height: 80px;
    white-space: pre-wrap;
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

  .page-container {
    min-height: 100%;
    box-sizing: border-box;

    .header {
        display: flex;
        align-items: center;
        justify-content: space-between;
    }
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
    