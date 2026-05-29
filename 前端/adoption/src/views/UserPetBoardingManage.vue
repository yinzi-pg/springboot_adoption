<script setup>
import { ref, onMounted, computed, getCurrentInstance } from 'vue';
import { 
  Edit, 
  Delete, 
  Search, 
  RefreshLeft,
  ChatLineSquare,
  User,
  Clock,
  Document,
  PictureFilled,
  Plus
} from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox, ElImage } from 'element-plus';
import { 
  getUserPetBoardingList, 
  addUserPetBoardingService, 
  deleteUserPetBoardingService, 
  updateUserPetBoardingService,
  searchUserPetBoardingService,
  getUserPetBoardingByUserPetId  // 导入新接口
} from '@/api/userPetBoarding';
import { useUserInfoStore } from '@/stores/user.js';

// 获取当前组件实例
const instance = getCurrentInstance();
// 获取表单引用
const formRef = ref(null);
const BACKEND_BASE = 'http://localhost:8080/springboot_adoption';
// 获取用户信息Store
const userInfoStore = useUserInfoStore();

// 分页数据
const pageNum = ref(1);
const pageSize = ref(5);
const total = ref(0);

// 分页事件
const onSizeChange = (size) => {
  pageSize.value = size;
  loadUserPetList();
};
const onCurrentChange = (num) => {
  pageNum.value = num;
  loadUserPetList();
};

// 宠物信息数据
const userPets = ref([]);
const loading = ref(false);

// 宠物体型相关
const petSizes = ref([]); // 存储从后端获取的体型列表
const loadingSizes = ref(false); // 体型数据加载状态

// 弹窗控制
const dialogVisible = ref(false);
const detailVisible = ref(false);
const title = ref('');
const imagePreviewVisible = ref(false);
const imagePreviewUrl = ref('');

// 判断是否为添加模式
const isAddMode = computed(() => !formData.value.userPetId);

// 表单数据
const formData = ref({
  userPetId: null,
  userId: null,
  petName: '',
  petType: '',
  petSize: '',
  petAge: null,
  petGender: '',
  healthStatus: '',
  vaccineStatus: '',
  specialHabit: '',
  petImage: ''
});

// 详情数据模型
const detailModel = ref({
  userPetId: '',
  userId: '',
  petName: '',
  petType: '',
  petSize: '',
  petAge: null,
  petGender: '',
  healthStatus: '',
  vaccineStatus: '',
  specialHabit: '',
  petImage: '',
  createTime: ''
});

// 搜索相关
const searchKeyword = ref('');

// 表单校验规则
const rules = ref({
  userId: [
    { required: true, message: '请输入用户ID', trigger: 'blur' },
    { type: 'number', message: '用户ID必须为数字', trigger: 'blur' }
  ],
  petName: [
    { required: true, message: '请输入宠物姓名', trigger: 'blur' },
    { min: 1, max: 20, message: '宠物姓名长度在1-20字符', trigger: 'blur' }
  ],
  petType: [
    { required: true, message: '请选择宠物类型', trigger: 'change' }
  ],
  petSize: [
    { required: true, message: '请选择体型大小', trigger: 'change' },
  ],
  petAge: [
    { required: true, message: '请输入宠物年龄', trigger: 'blur' },
    { type: 'number', min: 0, message: '年龄不能为负数', trigger: 'blur' }
  ],
  petGender: [
    { required: true, message: '请选择宠物性别', trigger: 'change' }
  ],
  healthStatus: [
    { required: true, message: '请输入健康状况', trigger: 'blur' }
  ],
  vaccineStatus: [
    { required: true, message: '请选择疫苗状态', trigger: 'change' }
  ]
});

// 新增：获取宠物体型列表
const loadPetSizes = async () => {
  try {
    loadingSizes.value = true;
    // 注意：这里使用特定的 userPetId 来获取对应宠物的体型信息
    // 实际使用时需传入有效的 userPetId（例如从表单或选中的宠物中获取）
    const userPetId = formData.value.userPetId; // 从表单数据中获取当前操作的宠物ID
    if (!userPetId) {
      // 如果没有有效的 userPetId，使用默认体型列表
      petSizes.value = [
        { label: '小型', value: '小型' },
        { label: '中型', value: '中型' },
        { label: '大型', value: '大型' }
      ];
      return;
    }
    
    // 调用接口获取该宠物的体型信息
    const res = await getUserPetBoardingByUserPetId(userPetId);
    if (res.code === 0) {
      // 假设接口返回格式为 { code: 0, data: { petSize: [...] } }
      // 根据实际返回结构调整解析逻辑
      let sizesData = res.data?.petSize || [];
      
      // 确保数据格式为 {label, value} 结构的数组
      if (Array.isArray(sizesData)) {
        petSizes.value = sizesData.map(size => {
          if (typeof size === 'string') {
            return { label: size, value: size };
          } else if (typeof size === 'object') {
            return {
              label: size.label || size.value || String(size),
              value: size.value || size.label || String(size)
            };
          } else {
            return { label: String(size), value: String(size) };
          }
        });
      }
      
      // 如果接口返回空数据，使用默认值
      if (petSizes.value.length === 0) {
        petSizes.value = [
          { label: '小型', value: '小型' },
          { label: '中型', value: '中型' },
          { label: '大型', value: '大型' }
        ];
      }
    } else {
      ElMessage.error('获取宠物体型数据失败：' + res.msg);
      // 失败时使用默认值
      petSizes.value = [
        { label: '小型', value: '小型' },
        { label: '中型', value: '中型' },
        { label: '大型', value: '大型' }
      ];
    }
  } catch (error) {
    ElMessage.error('获取宠物体型接口异常');
    console.error('获取体型数据错误:', error);
    // 异常时使用默认值
    petSizes.value = [
      { label: '小型', value: '小型' },
      { label: '中型', value: '中型' },
      { label: '大型', value: '大型' }
    ];
  } finally {
    loadingSizes.value = false;
  }
};

// 处理宠物类型变化 - 修改这里：允许其他类型选择体型
const handlePetTypeChange = (value) => {
  // 无论选择什么类型，都重新加载对应体型数据
  loadPetSizes();
};

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
    formData.value.petImage = result.data;
    console.log('上传成功，图片路径：', result.data);
    ElMessage.success('图片上传成功');
  } else {
    ElMessage.error('图片上传失败：' + result.msg);
  }
};

// 上传失败处理
const handleUploadError = (err) => {
  if (err.message.includes('exceeded size limit')) {
    ElMessage.error('文件大小超过限制，请上传2MB以内的图片');
  } else {
    ElMessage.error('上传失败，请稍后重试');
  }
};

// 获取图片URL
const getImageUrl = (path) => {
  if (!path) {
    return new URL('@/assets/b.png', import.meta.url).href;
  }
  
  if (path.startsWith('/assets/')) {
    try {
      return new URL(`../assets${path}`, import.meta.url).href;
    } catch (e) {
      console.error('无法加载图片:', path);
      return new URL('@/assets/b.png', import.meta.url).href;
    }
  }
  return path;
};

// 查看图片
const handleViewImage = (url) => {
  if (url) {
    imagePreviewUrl.value = url;
    imagePreviewVisible.value = true;
  }
};

// 加载宠物列表
const loadUserPetList = async () => {
  try {
    loading.value = true;
    const res = await getUserPetBoardingList({
      pageNum: pageNum.value,
      pageSize: pageSize.value
    });
    if (res.code === 0) {
      // 过滤掉null值，确保数据安全
      userPets.value = res.data.items?.filter(item => item !== null) || [];
      total.value = res.data.total || 0;
    } else {
      ElMessage.error('获取宠物信息失败：' + res.msg);
      userPets.value = [];
      total.value = 0;
    }
  } catch (error) {
    ElMessage.error('网络请求异常');
    userPets.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 模糊查询
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    loadUserPetList();
    return;
  }
  try {
    const result = await searchUserPetBoardingService(searchKeyword.value.trim());
    if (result.code === 0) {
      // 过滤null值
      userPets.value = result.data?.filter(item => item !== null) || [];
      if (userPets.value.length === 0) {
        ElMessage.info('未找到匹配的宠物信息');
      }
    } else {
      ElMessage.error('搜索失败，请重试');
    }
  } catch (error) {
    ElMessage.error('搜索接口异常');
    console.error(error);
  }
};

// 重置搜索
const resetSearch = () => {
  searchKeyword.value = '';
  loadUserPetList();
  ElMessage.success('已重置为全部宠物信息');
};

// 显示添加弹窗
const showAddDialog = () => {
  title.value = '添加宠物信息';
  // 从Pinia获取当前登录用户ID并自动填充
  const currentUserId = userInfoStore.info?.userId;
  
  formData.value = {
    userPetId: null,
    userId: currentUserId || null,
    petName: '',
    petType: '',
    petSize: '',
    petAge: null,
    petGender: '',
    healthStatus: '',
    vaccineStatus: '',
    specialHabit: '',
    petImage: ''
  };
  
  dialogVisible.value = true;
  // 打开弹窗时加载体型数据
  loadPetSizes();
};

// 显示编辑弹窗
const showEditDialog = (row) => {
  if (!row) return; // 防止row为null时打开弹窗
  title.value = '编辑宠物信息';
  formData.value = { 
    userPetId: row.userPetId,
    userId: row.userId,
    petName: row.petName,
    petType: row.petType,
    petSize: row.petSize,
    petAge: row.petAge,
    petGender: row.petGender,
    healthStatus: row.healthStatus,
    vaccineStatus: row.vaccineStatus,
    specialHabit: row.specialHabit,
    petImage: row.petImage
  };
  
  dialogVisible.value = true;
  // 打开弹窗时加载体型数据
  loadPetSizes();
};

// 查看详情
const handleViewDetail = (row) => {
  if (!row) return; // 防止row为null时打开弹窗
  detailModel.value = JSON.parse(JSON.stringify(row));
  detailVisible.value = true;
};

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return;
  
  try {
    // 表单校验
    await formRef.value.validate();
    
    let res;
    if (formData.value.userPetId) {
      res = await updateUserPetBoardingService(formData.value);
    } else {
      res = await addUserPetBoardingService(formData.value);
    }
    
    if (res.code === 0) {
      ElMessage.success(formData.value.userPetId ? '更新成功' : '添加成功');
      dialogVisible.value = false;
      loadUserPetList();
    } else {
      ElMessage.error('操作失败：' + res.msg);
    }
  } catch (error) {
    // 校验失败时不处理
    if (error.name === 'ValidationError') return;
    ElMessage.error('操作异常');
  }
};

// 删除宠物信息
const handleDelete = (userPetId) => {
  if (!userPetId) {
    ElMessage.warning('无效的记录ID');
    return;
  }
  
  ElMessageBox.confirm('确认要删除这条宠物信息吗？删除后无法恢复！', '警告', {
    confirmButtonText: '确认删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteUserPetBoardingService(userPetId);
      if (res.code === 0) {
        ElMessage.success('删除成功');
        loadUserPetList();
      } else {
        ElMessage.error('删除失败：' + res.msg);
      }
    } catch (error) {
      ElMessage.error('操作异常');
      console.error(error);
    }
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
};

// 初始化
onMounted(() => {
  loadUserPetList();
  loadPetSizes(); // 初始加载宠物体型数据
});
</script>
<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span class="page-title">用户宠物寄养信息管理</span>
        <div class="operation-group">
          <div class="search-group">
            <el-input 
              v-model="searchKeyword" 
              placeholder="搜索宠物信息..." 
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
            class="add-btn" 
            type="primary" 
            @click="showAddDialog"
          >
            添加宠物信息
          </el-button>
        </div>
      </div>
    </template>

    <!-- 宠物信息表格 -->
    <el-table 
      :data="userPets" 
      style="width: 100%" 
      v-loading="loading"
      border
    >
      <el-table-column label="序号" width="80" type="index" align="center" />
      <el-table-column label="宠物ID" prop="userPetId" width="100" align="center" />
      <el-table-column label="宠物图片" width="100" align="center">
        <template #default="{ row }">
          <div class="pet-image-container" @click="handleViewImage(row?.petImage)">
            <el-image
              :src="BACKEND_BASE+row?.petImage"
              :alt="row?.petName || '宠物图片'"
              class="pet-image"
              fit="cover"
            >
              <template #error>
                <div class="image-placeholder">
                  <PictureFilled class="icon" />
                </div>
              </template>
            </el-image>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="宠物姓名" prop="petName" width="120" />
      <el-table-column label="宠物类型" width="180">
        <template #default="{ row }">
          {{ row.petType }} {{ row.petSize || '' }}
        </template>
      </el-table-column>
      <el-table-column label="宠物年龄" prop="petAge" width="100" align="center" />
      <el-table-column label="宠物性别" prop="petGender" width="100" align="center" />
      <el-table-column label="健康状况" prop="healthStatus" width="120" />
      <el-table-column label="疫苗状态" prop="vaccineStatus" width="120" />
      <el-table-column label="用户ID" prop="userId" width="100" align="center" />
      <el-table-column label="创建时间" prop="createTime" width="160" />
      <el-table-column label="操作" width="180" align="center">
        <template #default="{ row }">
          <el-button 
            :icon="Edit" 
            circle 
            plain 
            type="primary" 
            @click="showEditDialog(row)"
            size="small"
            title="编辑"
            :disabled="!row"
          ></el-button>
          <el-button 
            :icon="ChatLineSquare" 
            circle 
            plain 
            type="info" 
            @click="handleViewDetail(row)"
            size="small"
            title="查看详情"
            :disabled="!row"
          ></el-button>
          <el-button 
            :icon="Delete" 
            circle 
            plain 
            type="danger" 
            @click="handleDelete(row?.userPetId)"
            size="small"
            title="删除"
            :disabled="!row?.userPetId"
          ></el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="暂无宠物信息" />
      </template>
    </el-table>

    <!-- 分页条 -->
    <el-pagination 
      v-model:current-page="pageNum" 
      v-model:page-size="pageSize" 
      :page-sizes="[1, 3, 5, 10]"
      layout="jumper, total, sizes, prev, pager, next" 
      background 
      :total="total" 
      @size-change="onSizeChange"
      @current-change="onCurrentChange" 
      style="margin-top: 20px; justify-content: flex-end" 
    />

    <!-- 添加/编辑弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="title" 
      width="500px"
    >
      <el-form 
        :model="formData" 
        :rules="rules" 
        ref="formRef" 
        label-width="120px"
      >
        <el-form-item label="用户ID" prop="userId">
          <el-input 
            v-model="formData.userId" 
            placeholder="请输入用户ID"
            type="number"
            :disabled="isAddMode"
          />
          <el-tooltip 
            v-if="isAddMode" 
            content="当前登录用户ID已自动填充" 
            placement="right"
          >
        
          </el-tooltip>
        </el-form-item>
        
        <el-form-item label="宠物姓名" prop="petName">
          <el-input 
            v-model="formData.petName" 
            placeholder="请输入宠物姓名"
          />
        </el-form-item>
        
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="宠物类型" prop="petType">
              <el-select 
                v-model="formData.petType" 
                placeholder="请选择宠物类型"
                @change="handlePetTypeChange"
              >
                <el-option label="猫类" value="猫类" />
                <el-option label="犬类" value="犬类" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体型大小" prop="petSize">
              <el-select 
                v-model="formData.petSize" 
                placeholder="请选择体型大小"
                :disabled="!formData.petType || loadingSizes"
              >
                <template v-if="!loadingSizes">
                  <el-option 
                    v-for="size in petSizes" 
                    :key="size.value" 
                    :label="size.label" 
                    :value="size.value" 
                  />
                </template>
                <template v-else>
                  <el-option disabled value="">加载中...</el-option>
                </template>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="宠物年龄" prop="petAge">
          <el-input-number 
            v-model="formData.petAge" 
            :min="0" 
            placeholder="请输入宠物年龄"
          />
        </el-form-item>
        
        <el-form-item label="宠物性别" prop="petGender">
          <el-select v-model="formData.petGender" placeholder="请选择宠物性别">
            <el-option label="公" value="公" />
            <el-option label="母" value="母" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="健康状况" prop="healthStatus">
          <el-input 
            v-model="formData.healthStatus" 
            placeholder="请输入健康状况"
          />
        </el-form-item>
        
        <el-form-item label="疫苗状态" prop="vaccineStatus">
          <el-select v-model="formData.vaccineStatus" placeholder="请选择疫苗状态">
            <el-option label="已接种" value="已接种" />
            <el-option label="未接种" value="未接种" />
            <el-option label="部分接种" value="部分接种" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="特殊习性" prop="specialHabit">
          <el-input
            v-model="formData.specialHabit"
            type="textarea"
            :rows="3"
            placeholder="请输入宠物特殊习性"
          />
        </el-form-item>
        
        <el-form-item label="宠物图片">
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
            <img 
              v-if="formData.petImage" 
              :src="BACKEND_BASE+formData.petImage"
              class="avatar" 
            />
            <div v-else class="upload-placeholder">
              <el-icon class="upload-icon">
                <Plus />
              </el-icon>
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button class="add-btn" type="primary" @click="handleSubmit">确认</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog 
      v-model="detailVisible" 
      title="宠物信息详情" 
      width="50%" 
      destroy-on-close
      :close-on-click-modal="false"
      class="detail-dialog"
    >
      <div class="detail-header">
        <div class="detail-header__title">
          <User class="icon-user" />
          <span>宠物信息编号：{{ detailModel.userPetId }}</span>
        </div>
        <div class="detail-header__line"></div>
      </div>

      <div class="detail-content">
        <div class="detail-card">
          <div class="detail-card__content">
            <div class="detail-item">
              <span class="detail-label">宠物图片：</span>
              <span class="detail-value">
                <div class="detail-pet-image" @click="handleViewImage(detailModel.petImage)">
                  <el-image
                    :src="BACKEND_BASE+detailModel.petImage"
                    :alt="detailModel.petName || '宠物图片'"
                    fit="cover"
                  >
                    <template #error>
                      <div class="image-placeholder">
                        <PictureFilled class="icon" />
                      </div>
                    </template>
                  </el-image>
                </div>
              </span>
            </div>
          </div>
        </div>

        <div class="detail-card">
          <div class="detail-card__title">
            <User class="icon-title" />
            <span>基本信息</span>
          </div>
          <div class="detail-card__content">
            <div class="detail-item">
              <span class="detail-label">宠物ID：</span>
              <span class="detail-value">{{ detailModel.userPetId || '未知' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">宠物姓名：</span>
              <span class="detail-value">{{ detailModel.petName || '未知' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">宠物类型：</span>
              <span class="detail-value">{{ detailModel.petType }} {{ detailModel.petSize || '' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">宠物年龄：</span>
              <span class="detail-value">{{ detailModel.petAge || '未知' }} 岁</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">宠物性别：</span>
              <span class="detail-value">{{ detailModel.petGender || '未知' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">所属用户ID：</span>
              <span class="detail-value">{{ detailModel.userId || '未知' }}</span>
            </div>
          </div>
        </div>

        <div class="detail-card">
          <div class="detail-card__title">
            <Document class="icon-title" />
            <span>健康信息</span>
          </div>
          <div class="detail-card__content">
            <div class="detail-item">
              <span class="detail-label">健康状况：</span>
              <span class="detail-value">{{ detailModel.healthStatus || '未知' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">疫苗状态：</span>
              <span class="detail-value">
                <el-tag 
                  :type="detailModel.vaccineStatus === '已接种' ? 'success' : 'warning'" 
                  effect="light" 
                  class="status-tag"
                >
                  {{ detailModel.vaccineStatus || '未知' }}
                </el-tag>
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">创建时间：</span>
              <span class="detail-value">
                <Clock class="icon-small" />
                {{ detailModel.createTime || '未知' }}
              </span>
            </div>
          </div>
        </div>

        <div class="detail-card">
          <div class="detail-card__title">
            <Document class="icon-title" />
            <span>特殊习性</span>
          </div>
          <div class="detail-card__content detail-remark">
            {{ detailModel.specialHabit || '无特殊习性记录' }}
          </div>
        </div>
      </div>

      <template #footer>
        <el-button 
          class="close-btn" 
          @click="detailVisible = false"
        >
          关闭
        </el-button>
      </template>
    </el-dialog>

    <!-- 图片预览弹窗 -->
    <el-dialog 
      v-model="imagePreviewVisible" 
      title="宠物图片预览" 
      width="80%"
      :close-on-click-modal="false"
    >
      <div class="image-preview-container">
        <el-image
          :src="BACKEND_BASE+imagePreviewUrl"
          :alt="detailModel.petName || '宠物图片'"
          fit="contain"
          class="preview-image"
        >
          <template #error>
            <div class="image-placeholder">
              <PictureFilled class="icon" />
              <p>图片加载失败</p>
            </div>
          </template>
        </el-image>
      </div>
    </el-dialog>
  </el-card>
</template>



<style lang="scss" scoped>
.page-container {
  min-height: 120vh;
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
      border: 1px solid #ddd;
      
      .el-icon-search {
        color: #666;
      }
      
      &:focus-within {
        border-color: #409eff;
        box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
      }
    }

    .search-btn {
      background-color: #409eff !important;
      border-color: #409eff !important;
      color: #fff !important;
      border-radius: 8px;
      
      &:hover {
        background-color: #66b1ff !important;
        border-color: #66b1ff !important;
      }
    }

    .reset-btn {
      border-color: #ddd !important;
      color: #666 !important;
      border-radius: 8px;
      
      &:hover {
        background-color: #f5f5f5 !important;
      }
    }

    .add-btn {
      background-color: #409eff !important;
      border-color: #409eff !important;
      color: #ffffff !important;
      box-shadow: 0 2px 4px rgba(64, 158, 255, 0.3);

      &:hover {
        background-color: #66b1ff !important;
        border-color: #66b1ff !important;
        transform: translateY(-1px);
        box-shadow: 0 4px 8px rgba(64, 158, 255, 0.5);
      }

      &:active {
        background-color: #3a8ee6 !important;
        border-color: #3a8ee6 !important;
      }

      border-radius: 8px !important;
      padding: 12px 24px !important;
    }
  }

  .el-table {
    margin-top: 20px;
    border-radius: 8px;
    overflow: hidden;

    :deep(.cell) {
      padding: 12px 10px;
    }
  }

  .pet-image-container {
    width: 60px;
    height: 60px;
    margin: 0 auto;
    cursor: pointer;
    border-radius: 4px;
    overflow: hidden;
    transition: transform 0.2s;
    
    &:hover {
      transform: scale(1.05);
    }
  }
  
  .pet-image {
    width: 100%;
    height: 100%;
  }
  
  .form-pet-image {
    width: 120px;
    height: 120px;
    border-radius: 4px;
    overflow: hidden;
    cursor: pointer;
    
    .el-image {
      width: 100%;
      height: 100%;
    }
  }
  
  .no-image {
    width: 120px;
    height: 120px;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px dashed #ddd;
    border-radius: 4px;
    color: #999;
    font-size: 14px;
  }
  
  .detail-pet-image {
    width: 100px;
    height: 100px;
    border-radius: 4px;
    overflow: hidden;
    cursor: pointer;
    
    .el-image {
      width: 100%;
      height: 100%;
    }
  }
  
  .image-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background-color: #f5f5f5;
    color: #999;
    
    .icon {
      width: 24px;
      height: 24px;
      margin-bottom: 8px;
    }
  }
  
  .image-preview-container {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20px 0;
    
    .preview-image {
      max-width: 100%;
      max-height: 70vh;
    }
  }

  .el-date-editor, .el-select, .el-input-number {
    width: 100%;
  }

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
      color: #409eff;
      margin-bottom: 10px;
      
      .icon-user {
        width: 20px;
        height: 20px;
      }
    }
    
    &__line {
      height: 2px;
      background: linear-gradient(90deg, #e6f7ff, transparent);
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
      box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
      border-color: #e6f7ff;
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
        color: #409eff;
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
        color: #66b1ff;
      }
    }
  }

  .detail-remark {
    line-height: 24px;
    padding: 8px 12px;
    background-color: #f0f7ff;
    border-radius: 6px;
    color: #666;
    min-height: 60px;
    white-space: pre-wrap;
  }

  .close-btn {
    background-color: #fff !important;
    border-color: #e6f7ff !important;
    color: #409eff !important;
    padding: 8px 24px;
    border-radius: 6px;
    
    &:hover {
      background-color: #f0f7ff !important;
    }
  }

  .status-tag {
    padding: 2px 8px;
    border-radius: 4px;
  }

  // 宠物图片上传样式
  .avatar-uploader {
    width: 120px;
    height: 120px;
    border-radius: 50%;
    border: 2px dashed #c6e2ff;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;

    &:hover {
      border-color: #99d1ff;
      transform: scale(1.05);
      background-color: rgba(222, 235, 255, 0.2);
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
      color: #99d1ff;
    }
  }

  .info-icon {
    color: #409eff;
    margin-left: 8px;
    cursor: help;
  }
}
</style>
