<script setup>
import { ref, onMounted } from 'vue';
import { 
  Edit, 
  Delete, 
  Search, 
  RefreshLeft,
  ChatLineSquare,
  User,
  Phone,
  Calendar,
  Money,
  Clock,
  Document,
  PictureFilled,
} from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox, ElImage } from 'element-plus';
import { 
  getBoardingList, 
  boardingAddService, 
  boardingDeleteService, 
  boardingUpdateService,
  searchBoardingService
} from '@/api/petBoarding';
import { getUserPetBoardingList } from '@/api/userPetBoarding'; // 修改为用户宠物接口

// 分页数据
const pageNum = ref(1);
const pageSize = ref(5);
const total = ref(0);
const BACKEND_BASE = 'http://localhost:8080/springboot_adoption';
// 分页事件
const onSizeChange = (size) => {
  pageSize.value = size;
  loadBoardingList();
};
const onCurrentChange = (num) => {
  pageNum.value = num;
  loadBoardingList();
};

// 寄养记录数据
const boardings = ref([]);
const loading = ref(false);

// 弹窗控制
const dialogVisible = ref(false);
const detailVisible = ref(false);
const title = ref('');
const imagePreviewVisible = ref(false);
const imagePreviewUrl = ref('');

// 表单数据（与后端PetBoarding实体对应）
const formData = ref({
  boardingId: null,
  userPetId: null, // 关联用户宠物ID
  userId: null,    // 关联用户ID
  petName: '',     // 从userpet_boarding表获取
  petImage: '',    // 宠物图片
  userName: '',    // 从user表获取
  userPhone: '',   // 从user表获取
  boardingStart: '',
  boardingEnd: '',
  boardingStatus: '寄养中',
  specialInstructions: '',
  fee: 0,
  paymentStatus: '未支付',
  createTime: ''
});

// 详情数据模型
const detailModel = ref({
  boardingId: '',
  userPetId: '',
  userId: '',
  petName: '',
  petImage: '',    // 宠物图片
  userName: '',
  userPhone: '',
  boardingStart: '',
  boardingEnd: '',
  boardingStatus: '',
  specialInstructions: '',
  fee: 0,
  paymentStatus: '',
  createTime: ''
});

// 用户宠物列表（替代原宠物列表）
const userPetList = ref([]);

// 搜索相关
const searchKeyword = ref('');

// 表单校验规则
const rules = ref({
  userPetId: [
    { required: true, message: '请选择关联宠物', trigger: 'blur' }
  ],
  userName: [
    { required: true, message: '请输入寄养人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在2-20字符', trigger: 'blur' }
  ],
  userPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  boardingStart: [
    { required: true, message: '请选择寄养开始日期', trigger: 'change' }
  ],
  boardingEnd: [
    { required: true, message: '请选择寄养结束日期', trigger: 'change' },
    { 
      validator: (rule, value, callback) => {
        if (value && formData.value.boardingStart && value < formData.value.boardingStart) {
          callback(new Error('结束日期不能早于开始日期'));
        } else {
          callback();
        }
      }, 
      trigger: 'change' 
    }
  ],
  fee: [
    { required: true, message: '请输入寄养费用', trigger: 'blur' },
    { type: 'number', min: 0, message: '费用不能为负数', trigger: 'blur' }
  ],
  boardingStatus: [
    { required: true, message: '请选择寄养状态', trigger: 'change' }
  ],
  paymentStatus: [
    { required: true, message: '请选择支付状态', trigger: 'change' }
  ]
});

// 状态标签样式
const getStatusTagType = (status) => {
  if (!status) return 'warning';
  switch(status) {
    case '寄养中': return 'primary';
    case '已结束': return 'info';
    case '已取消': return 'danger';
    default: return 'warning';
  }
};

// 查看图片
const handleViewImage = (url) => {
  if (url) {
    imagePreviewUrl.value = url;
    imagePreviewVisible.value = true;
  }
};

// 加载寄养记录列表
const loadBoardingList = async () => {
  try {
    loading.value = true;
    const res = await getBoardingList({
      pageNum: pageNum.value,
      pageSize: pageSize.value
    });
    if (res.code === 0) {
      // 过滤掉null值，确保数据安全
      boardings.value = res.data.items?.filter(item => item !== null) || [];
      total.value = res.data.total || 0;
    } else {
      ElMessage.error('获取寄养记录失败：' + res.msg);
      boardings.value = [];
      total.value = 0;
    }
  } catch (error) {
    ElMessage.error('网络请求异常');
    boardings.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 获取用户宠物列表（用于选择关联宠物）
const loadUserPetList = async () => {
  try {
    const res = await getUserPetBoardingList({
      pageNum: 1,
      pageSize: 100 // 加载足够多的选项
    });
    if (res.code === 0) {
      userPetList.value = res.data.items?.filter(item => item !== null) || [];
    } else {
      ElMessage.error('获取宠物列表失败');
      userPetList.value = [];
    }
  } catch (error) {
    console.error('获取宠物列表异常:', error);
    userPetList.value = [];
  }
};

// 模糊查询
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    loadBoardingList();
    return;
  }
  try {
    const result = await searchBoardingService(searchKeyword.value.trim());
    if (result.code === 0) {
      // 过滤null值
      boardings.value = result.data?.filter(item => item !== null) || [];
      if (boardings.value.length === 0) {
        ElMessage.info('未找到匹配的寄养记录');
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
  loadBoardingList();
  ElMessage.success('已重置为全部寄养记录');
};

// 显示添加弹窗
const showAddDialog = () => {
  title.value = '添加寄养记录';
  formData.value = {
    boardingId: null,
    userPetId: null,
    userId: null,
    petName: '',
    petImage: '',    // 初始化宠物图片
    userName: '',
    userPhone: '',
    boardingStart: '',
    boardingEnd: '',
    boardingStatus: '寄养中',
    specialInstructions: '',
    fee: 0,
    paymentStatus: '未支付'
  };
  dialogVisible.value = true;
};

// 显示编辑弹窗
const showEditDialog = (row) => {
  if (!row) return; // 防止row为null时打开弹窗
  title.value = '编辑寄养记录';
  formData.value = { ...row };
  dialogVisible.value = true;
};

// 查看详情
const handleViewDetail = (row) => {
  if (!row) return; // 防止row为null时打开弹窗
  detailModel.value = JSON.parse(JSON.stringify(row));
  detailVisible.value = true;
};

// 提交表单
const handleSubmit = async () => {
  try {
    // 从用户宠物列表匹配宠物信息
    const selectedPet = userPetList.value.find(pet => pet.userPetId === formData.value.userPetId);
    if (selectedPet) {
      formData.value.petName = selectedPet.petName;
      formData.value.petImage = selectedPet.petImage; // 获取宠物图片
      formData.value.userId = selectedPet.userId; // 关联用户ID
    } else {
      ElMessage.warning('请选择有效的宠物');
      return;
    }

    let res;
    if (formData.value.boardingId) {
      res = await boardingUpdateService(formData.value);
    } else {
      res = await boardingAddService(formData.value);
    }
    
    if (res.code === 0) {
      ElMessage.success(formData.value.boardingId ? '更新成功' : '添加成功');
      dialogVisible.value = false;
      loadBoardingList();
    } else {
      ElMessage.error('操作失败：' + res.msg);
    }
  } catch (error) {
    ElMessage.error('操作异常');
    console.error(error);
  }
};

// 删除寄养记录
const handleDelete = (boardingId) => {
  if (!boardingId) {
    ElMessage.warning('无效的记录ID');
    return;
  }
  
  ElMessageBox.confirm('确认要删除这条寄养记录吗？删除后无法恢复！', '警告', {
    confirmButtonText: '确认删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await boardingDeleteService(boardingId);
      if (res.code === 0) {
        ElMessage.success('删除成功');
        loadBoardingList();
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
  loadBoardingList();
  loadUserPetList();
});
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span class="page-title">宠物寄养记录管理</span>
        <div class="operation-group">
          <div class="search-group">
            <el-input 
              v-model="searchKeyword" 
              placeholder="搜索寄养信息..." 
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
            @click="showAddDialog"
          >
            添加寄养记录
          </el-button>
        </div>
      </div>
    </template>

    <!-- 寄养记录表格 -->
    <el-table 
      :data="boardings" 
      style="width: 100%" 
      v-loading="loading"
      border
    >
      <el-table-column label="序号" width="80" type="index" align="center" />
      <el-table-column label="寄养ID" prop="boardingId" width="100" align="center" />
      <el-table-column label="宠物图片" width="100" align="center">
        <template #default="{ row }">
          <div class="pet-image-container" @click="handleViewImage(row?.petImage)">
            <el-image
              :src="BACKEND_BASE+row?.petImage || require('@/assets/default-pet.png')"
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
      <el-table-column label="寄养人" prop="userName" width="120" />
      <el-table-column label="联系电话" prop="userPhone" width="130" />
      <el-table-column label="寄养时间" width="220">
        <template #default="{ row }">
          {{ row?.boardingStart ?? '' }} 至 {{ row?.boardingEnd ?? '' }}
        </template>
      </el-table-column>
      <el-table-column label="寄养状态" width="120" align="center">
        <template #default="{ row }">
          <el-tag :type="getStatusTagType(row?.boardingStatus)" effect="light">
            {{ row?.boardingStatus ?? '未知' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="支付状态" width="120" align="center">
        <template #default="{ row }">
          <el-tag 
            :type="row?.paymentStatus === '已支付' ? 'success' : 'warning'" 
            effect="light"
          >
            {{ row?.paymentStatus ?? '未知' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="费用（元）" prop="fee" width="100" align="center" />
      <el-table-column label="创建时间" prop="createTime" width="160" />
      <el-table-column label="操作" width="220" align="center">
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
            @click="handleDelete(row?.boardingId)"
            size="small"
            title="删除"
            :disabled="!row?.boardingId"
          ></el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="暂无寄养记录" />
      </template>
    </el-table>

    <!-- 分页条 -->
    <el-pagination 
      v-model:current-page="pageNum" 
      v-model:page-size="pageSize" 
      :page-sizes="[1, 3, 5]"
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
        <el-form-item label="关联宠物" prop="userPetId">
          <el-select 
            v-model="formData.userPetId" 
            placeholder="请选择宠物"
            filterable  
            clearable   
            @change="() => {
              const pet = userPetList.find(p => p.userPetId === formData.userPetId);
              if (pet) {
                formData.petImage = pet.petImage;
              }
            }"
          >
            <el-option 
              v-for="pet in userPetList" 
              :key="pet.userPetId" 
              :label="`${pet.petName} (ID:${pet.userPetId})`"  
              :value="pet.userPetId"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="宠物图片">
          <div class="form-pet-image" v-if="formData.petImage">
            <el-image
              :src="BACKEND_BASE+formData.petImage"
              :alt="formData.petName || '宠物图片'"
              fit="cover"
              @click="handleViewImage(formData.petImage)"
            />
          </div>
          <div class="no-image" v-else>
            选择宠物后显示图片
          </div>
        </el-form-item>
        
        <el-form-item label="寄养人姓名" prop="userName">
          <el-input 
            v-model="formData.userName" 
            placeholder="请输入寄养人姓名"
          />
        </el-form-item>
        
        <el-form-item label="联系电话" prop="userPhone">
          <el-input 
            v-model="formData.userPhone" 
            placeholder="请输入联系电话"
          />
        </el-form-item>
        
        <el-form-item label="寄养开始日期" prop="boardingStart">
          <el-date-picker
            v-model="formData.boardingStart"
            type="date"
            placeholder="选择开始日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        
        <el-form-item label="寄养结束日期" prop="boardingEnd">
          <el-date-picker
            v-model="formData.boardingEnd"
            type="date"
            placeholder="选择结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        
        <el-form-item label="寄养状态" prop="boardingStatus">
          <el-select v-model="formData.boardingStatus" placeholder="请选择寄养状态">
            <el-option label="寄养中" value="寄养中" />
            <el-option label="已结束" value="已结束" />
            <el-option label="已取消" value="已取消" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="特殊要求" prop="specialInstructions">
          <el-input
            v-model="formData.specialInstructions"
            type="textarea"
            :rows="3"
            placeholder="请输入特殊要求说明"
          />
        </el-form-item>
        
        <el-form-item label="寄养费用" prop="fee">
          <el-input-number 
            v-model="formData.fee" 
            :min="0" 
            :precision="2"
            placeholder="请输入费用金额"
          />
        </el-form-item>
        
        <el-form-item label="支付状态" prop="paymentStatus">
          <el-select v-model="formData.paymentStatus" placeholder="请选择支付状态">
            <el-option label="已支付" value="已支付" />
            <el-option label="未支付" value="未支付" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button class="pink-add-btn" type="primary" @click="handleSubmit">确认</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog 
      v-model="detailVisible" 
      title="寄养记录详情" 
      width="50%" 
      destroy-on-close
      :close-on-click-modal="false"
      class="detail-dialog"
    >
      <div class="detail-header">
        <div class="detail-header__title">
          <Calendar class="icon-calendar" />
          <span>寄养记录编号：{{ detailModel.boardingId }}</span>
        </div>
        <div class="detail-header__line"></div>
      </div>

      <div class="detail-content">
        <div class="detail-card">
          <div class="detail-card__title">
            <User class="icon-title" />
            <span>宠物信息</span>
          </div>
          <div class="detail-card__content">
            <div class="detail-item">
              <span class="detail-label">宠物图片：</span>
              <span class="detail-value">
                <div class="detail-pet-image" @click="handleViewImage(detailModel.petImage)">
                  <el-image
                    :src="BACKEND_BASE+detailModel.petImage || require('@/assets/default-pet.png')"
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
            <div class="detail-item">
              <span class="detail-label">宠物ID：</span>
              <span class="detail-value">{{ detailModel.userPetId || '未知' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">宠物姓名：</span>
              <span class="detail-value">{{ detailModel.petName || '未知' }}</span>
            </div>
          </div>
        </div>

        <div class="detail-card">
          <div class="detail-card__title">
            <User class="icon-title" />
            <span>寄养人信息</span>
          </div>
          <div class="detail-card__content">
            <div class="detail-item">
              <span class="detail-label">寄养人姓名：</span>
              <span class="detail-value">{{ detailModel.userName || '未知' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">联系电话：</span>
              <span class="detail-value">
                <Phone class="icon-small" />
                {{ detailModel.userPhone || '未知' }}
              </span>
            </div>
          </div>
        </div>

        <div class="detail-card">
          <div class="detail-card__title">
            <Calendar class="icon-title" />
            <span>寄养详情</span>
          </div>
          <div class="detail-card__content">
            <div class="detail-item">
              <span class="detail-label">寄养时间：</span>
              <span class="detail-value">
                <Calendar class="icon-small" />
                {{ detailModel.boardingStart || '未知' }} 至 {{ detailModel.boardingEnd || '未知' }}
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">寄养状态：</span>
              <span class="detail-value">
                <el-tag :type="getStatusTagType(detailModel.boardingStatus)" effect="light" class="status-tag">
                  {{ detailModel.boardingStatus || '未知' }}
                </el-tag>
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">支付状态：</span>
              <span class="detail-value">
                <el-tag 
                  :type="detailModel.paymentStatus === '已支付' ? 'success' : 'warning'" 
                  effect="light" 
                  class="status-tag"
                >
                  {{ detailModel.paymentStatus || '未知' }}
                </el-tag>
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">寄养费用：</span>
              <span class="detail-value">
                <Money class="icon-small" />
                {{ detailModel.fee ? detailModel.fee.toFixed(2) : '0.00' }} 元
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
            <span>特殊要求</span>
          </div>
          <div class="detail-card__content detail-remark">
            {{ detailModel.specialInstructions || '无特殊要求' }}
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
      color: #ff6b81;
      margin-bottom: 10px;
      
      .icon-calendar {
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
    padding: 8px 12px;
    background-color: #fff8f9;
    border-radius: 6px;
    color: #666;
    min-height: 60px;
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

  .status-tag {
    padding: 2px 8px;
    border-radius: 4px;
  }
}
</style>
    