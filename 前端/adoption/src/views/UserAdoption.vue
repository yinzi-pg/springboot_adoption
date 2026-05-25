<script setup>
import {
  RefreshLeft,
  Search,
  Download
} from '@element-plus/icons-vue'
import { ref, onMounted, reactive, watch, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox, ElDialog, ElCheckbox } from 'element-plus'
import * as XLSX from 'xlsx'

// 核心：Excel导出功能（仅导出当前正在编辑的申请表单数据）
const exportToExcel = () => {
  // 仅处理当前表单数据
  const exportData = []
  
  // 校验表单是否有有效数据
  if (!adoptionForm.applicantName && !adoptionForm.petId) {
    return ElMessage.warning('当前表单无有效数据可导出，请先填写申请表单关键信息（姓名/宠物ID）')
  }
  
  // 整理当前表单数据
  exportData.push({
    '申请ID': adoptionForm.adoptionId || '未提交（当前表单）',
    '申请人姓名': adoptionForm.applicantName || '未填写',
    '联系电话': adoptionForm.applicantPhone || '未填写',
    '宠物ID': adoptionForm.petId || '未填写',
    '宠物名称': adoptionForm.petName || '未匹配',
    '领养原因': adoptionForm.reason || '未填写',
    '备注信息': adoptionForm.remark || '无',
    '申请日期': adoptionForm.applyDate || formatDate(new Date()),
    '审核状态': adoptionForm.status || '待提交',
    '审核时间': adoptionForm.reviewDate || '未审核'
  })
  
  // 生成Excel文件
  const workbook = XLSX.utils.book_new()
  const worksheet = XLSX.utils.json_to_sheet(exportData, {
    header: ['申请ID', '申请人姓名', '联系电话', '宠物ID', '宠物名称', '领养原因', '备注信息', '申请日期', '审核状态', '审核时间']
  })
  XLSX.utils.book_append_sheet(workbook, worksheet, '当前领养申请表单')
  
  // 下载Excel文件
  const fileName = `当前宠物领养申请表单_${formatDate(new Date())}.xlsx`
  XLSX.writeFile(workbook, fileName)
  
  // 导出成功提示
  ElMessage.success(`当前表单Excel已导出：${fileName}`)
}

// 导入宠物列表和领养相关接口
import { getPetList } from '@/api/petInformation'
import { 
  adoptionApplyService,
  getAdoptionList
} from '@/api/adoption'

// 分页相关
const pageNum = ref(1)
const pageSize = ref(5)
const total = ref(0)

// 状态管理
const loading = ref(false)
const submitting = ref(false)
const petLoading = ref(false)
const agreementDialogVisible = ref(true)
const agreeChecked = ref(false)
const countDown = ref(10)
const isCountDownEnd = ref(false)
const countDownTimer = ref(null)

// 宠物列表数据存储
const pets = ref([])
const petsMap = ref(new Map()) // 宠物名称到宠物对象的映射表

// 表单数据（当前正在编辑的申请表单）
const adoptionForm = reactive({
  adoptionId: null,
  applicantName: '',
  applicantPhone: '',
  petId: '',
  petName: '',
  petImage: '',
  reason: '',
  remark: '',
  applyDate: formatDate(new Date()),
  status: '待审核',
  reviewDate: null
})

// 宠物下拉框相关：绑定宠物名称
const selectedPetName = ref(''); 

// 表单验证规则
const rules = reactive({
  applicantName: [
    { required: true, message: '请输入申请人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在2到20个字符', trigger: 'blur' }
  ],
  applicantPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  petName: [
    { required: true, message: '请选择宠物', trigger: 'change' }
  ],
  reason: [
    { required: true, message: '请说明领养原因', trigger: 'blur' },
    { min: 10, message: '请详细描述您的领养意愿和饲养条件（至少10个字符）', trigger: 'blur' }
  ]
})

// 我的申请记录
const myApplications = ref([])

// 表单引用
const adoptionFormRef = ref(null)

// 日期格式化方法
function formatDate(date) {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

function formatDateTime(date) {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 加载宠物列表
const loadPetList = async () => {
  petLoading.value = true
  try {
    const result = await getPetList({ pageNum: 1, pageSize: 1000 })
    if (result.code === 0) {
      pets.value = result.data.items
      // 构建宠物名称到宠物对象的映射表
      const map = new Map()
      result.data.items.forEach(pet => {
        map.set(pet.petName, pet)
      })
      petsMap.value = map
      ElMessage.success(`已加载 ${result.data.items.length} 条宠物信息`)
    } else {
      ElMessage.error('获取宠物列表失败')
    }
  } catch (error) {
    ElMessage.error('获取宠物列表接口异常')
    console.error(error)
  } finally {
    petLoading.value = false
  }
}

// 选择宠物后更新表单
const handlePetChange = (petName) => {
  selectedPetName.value = petName;
  const pet = petsMap.value.get(petName)
  if (pet) {
    adoptionForm.petId = pet.petId;
    adoptionForm.petName = pet.petName;
    adoptionForm.petImage = pet.petImage;
  } else {
    adoptionForm.petId = '';
    adoptionForm.petName = '';
    adoptionForm.petImage = '';
  }
};

// 分页事件
const onSizeChange = (size) => {
  pageSize.value = size
  getMyApplications()
}

const onCurrentChange = (num) => {
  pageNum.value = num
  getMyApplications()
}

// 获取申请记录
const getMyApplications = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    const result = await getAdoptionList(params)
    if (result.code === 0) {
      myApplications.value = result.data.items
      total.value = result.data.total
    } else {
      ElMessage.error('获取申请记录失败')
    }
  } catch (error) {
    ElMessage.error('获取申请记录接口异常')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 提交领养申请
const submitAdoption = async () => {
  if (!adoptionFormRef.value) return
  try {
    await adoptionFormRef.value.validate()
  } catch (error) {
    return ElMessage.warning('请完善表单信息后再提交')
  }

  adoptionForm.applyDate = formatDateTime(new Date())

  submitting.value = true
  try {
    const result = await adoptionApplyService({
      ...adoptionForm,
      userId: localStorage.getItem('userId') || 'anonymous'
    })
    
    if (result.code === 0) {
     ElMessage.success('您的领养申请已提交，请等待管理员审核');
     resetForm();
     getMyApplications();
    } else {
      ElMessage.error(`提交失败: ${result.msg || '服务器错误'}`)
    }
  } catch (error) {
    ElMessage.error('提交接口异常，请稍后重试')
    console.error(error)
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  if (adoptionFormRef.value) {
    adoptionFormRef.value.resetFields()
  }
  adoptionForm.adoptionId = null
  adoptionForm.applyDate = formatDate(new Date())
  adoptionForm.status = '待审核'
  adoptionForm.reviewDate = null
  adoptionForm.petImage = ''
  selectedPetName.value = '';
}

// 状态样式映射
const getStatusType = (status) => {
  switch (status) {
    case '待审核': return 'warning'
    case '已通过': return 'success'
    case '已拒绝': return 'danger'
    default: return 'info'
  }
}

// 根据宠物ID获取宠物信息
const getPetInfo = (petId) => {
  if (!petId) return { name: '', image: '' }
  const targetId = Number(petId)
  const pet = pets.value.find(item => item.petId === targetId)
  return pet ? { 
    name: pet.petName, 
    image: pet.petImage || 'https://picsum.photos/200/200?grayscale'
  } : { name: '', image: '' }
}

// 协议弹窗倒计时逻辑
const startCountDown = () => {
  if (countDownTimer.value) clearInterval(countDownTimer.value)
  
  countDown.value = 5
  isCountDownEnd.value = false
  
  countDownTimer.value = setInterval(() => {
    if (countDown.value > 0) {
      countDown.value--
    } else {
      isCountDownEnd.value = true
      clearInterval(countDownTimer.value)
    }
  }, 1000)
}

// 关闭协议弹窗
const closeAgreementDialog = () => {
  if (!agreeChecked.value) {
    return ElMessage.warning('请先勾选同意领养协议')
  }
  if (!isCountDownEnd.value) {
    return ElMessage.warning('请完整浏览协议后再关闭')
  }
  
  agreementDialogVisible.value = false
}

// 初始化加载
onMounted(() => {
  startCountDown()
  loadPetList()
  getMyApplications()
})

// 组件卸载时清除定时器
onUnmounted(() => {
  if (countDownTimer.value) {
    clearInterval(countDownTimer.value)
  }
})
</script>
<template>
  <el-card class="page-container">
    <!-- 领养协议弹窗 -->
    <el-dialog
      v-model="agreementDialogVisible"
      title="宠物领养协议"
      width="70%"
      :before-close="() => false"
      :close-on-click-modal="false"
      :show-close="false"
    >
      <div class="agreement-content">
        <h4 class="agreement-title">一、领养人基本义务</h4>
        <p>1. 领养人需年满18周岁，具备完全民事行为能力，有稳定的工作和住所，能够为宠物提供长期、稳定的生活环境。</p>
        <p>2. 领养人需承诺不虐待、不遗弃宠物，不将宠物用于商业交易、实验或其他非法用途。</p>
        <p>3. 领养人需定期为宠物接种疫苗、进行体检，保障宠物的健康，如宠物生病需及时就医。</p>
        <p>4. 领养人需为宠物提供适宜的饮食和居住条件，禁止长期笼养，保证宠物有足够的活动空间和陪伴时间。</p>
        
        <h4 class="agreement-title" style="margin-top: 16px;">二、领养流程说明</h4>
        <p>1. 领养人需如实填写领养申请表，提供真实的个人信息和饲养条件说明。</p>
        <p>2. 工作人员将在3个工作日内审核申请，审核通过后将联系领养人进行家访或线上沟通。</p>
        <p>3. 确认符合领养条件后，领养人需签署纸质领养协议，方可领取宠物。</p>
        <p>4. 领养后1个月内，领养人需配合工作人员进行2-3次回访（线上或线下），反馈宠物适应情况。</p>
        
        <h4 class="agreement-title" style="margin-top: 16px;">三、特殊情况约定</h4>
        <p>1. 若领养人因特殊原因（如搬家、疾病等）无法继续饲养宠物，需第一时间联系领养中心，由中心协助寻找新的领养人，禁止私自转让或遗弃。</p>
        <p>2. 如发现领养人存在虐待宠物、遗弃宠物等违约行为，领养中心有权收回宠物，并追究相关责任。</p>
        <p>3. 本协议自领养人勾选同意并提交申请表之日起生效，领养人与领养中心共同遵守。</p>
      </div>
      
      <div class="agreement-footer">
        <div class="agreement-check">
          <el-checkbox v-model="agreeChecked" style="margin-right: 8px;">
            我已仔细阅读并同意上述领养协议
          </el-checkbox>
        </div>
        <div class="count-down提示">
          请继续浏览协议：{{ countDown }} 秒
        </div>
        <el-button
          type="primary"
          @click="closeAgreementDialog"
          :disabled="!agreeChecked || !isCountDownEnd"
          style="margin-left: auto;"
        >
          确认同意并进入表单
        </el-button>
      </div>
    </el-dialog>

    <template #header>
      <div class="header">
        <span class="page-title">宠物领养申请</span>
        <div class="operation-group">
          <el-button 
            type="success" 
            :icon="Download"
            @click="exportToExcel"
            class="export-btn"
            :disabled="agreementDialogVisible" 
          >
            导出当前表单Excel
          </el-button>
          <el-button 
            type="default" 
            :icon="RefreshLeft"
            @click="resetForm"
            class="reset-btn"
          >
            重置表单
          </el-button>
        </div>
      </div>
    </template>

    <!-- 领养申请表单 -->
    <el-form 
      :model="adoptionForm" 
      :rules="rules" 
      label-width="120px"
      ref="adoptionFormRef"
      class="adoption-form"
      :disabled="agreementDialogVisible"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="申请人姓名" prop="applicantName">
            <el-input v-model="adoptionForm.applicantName" placeholder="请输入您的姓名" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系电话" prop="applicantPhone">
            <el-input v-model="adoptionForm.applicantPhone" placeholder="请输入您的手机号码" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="宠物名称" prop="petName">
            <el-select
              v-model="selectedPetName"
              placeholder="请选择宠物"
              clearable
              @change="handlePetChange"
              style="width: 100%"
              :loading="petLoading"
            >
              <el-option
                v-for="pet in pets"
                :key="pet.petId"
                :label="`${pet.petName}（品种：${pet.petType || '未知'}）`"
                :value="pet.petName" 
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="宠物ID" prop="petId">
            <el-input 
              v-model="adoptionForm.petId" 
              placeholder="选择宠物后自动生成" 
              readonly
              :disabled="true"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 宠物图片显示区域 -->
      <el-row :gutter="20" v-if="adoptionForm.petImage">
        <el-col :span="24">
          <el-form-item label="宠物图片">
            <div class="pet-image-container">
              <img 
                :src="adoptionForm.petImage" 
                :alt="adoptionForm.petName || '宠物图片'" 
                class="pet-image"
                @error="adoptionForm.petImage = 'https://picsum.photos/200/200?grayscale'"
              >
            </div>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="申请日期" prop="applyDate">
            <el-date-picker
              v-model="adoptionForm.applyDate"
              type="date"
              placeholder="申请日期（自动生成）"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              disabled
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="领养原因" prop="reason">
        <el-input 
          v-model="adoptionForm.reason" 
          type="textarea" 
          rows="4" 
          placeholder="请详细说明您的领养原因、饲养条件等信息" 
        />
      </el-form-item>

      <el-form-item label="备注信息" prop="remark">
        <el-input 
          v-model="adoptionForm.remark" 
          type="textarea" 
          rows="2" 
          placeholder="其他需要说明的信息" 
        />
      </el-form-item>

      <el-form-item class="form-actions">
        <el-button 
          class="pink-add-btn" 
          type="primary" 
          @click="submitAdoption"
          :loading="submitting"
        >
          提交领养申请
        </el-button>
        <el-button 
          type="default" 
          @click="resetForm"
          style="margin-left: 10px"
        >
          清空重填
        </el-button>
      </el-form-item>
    </el-form>

    <!-- 申请记录表格 - 已添加申请人姓名和联系电话列 -->
    <div class="record-section">
      <h3 class="record-title">领养申请记录</h3>
      <el-table 
        :data="myApplications" 
        style="width: 100%" 
        v-loading="loading"
        element-loading-text="加载中..."
      >
        <el-table-column label="序号" width="80" type="index" />
        <el-table-column label="申请ID" prop="adoptionId" width="120" />
        <el-table-column label="申请人姓名" prop="applicantName" width="140" />
        <el-table-column label="联系电话" prop="applicantPhone" width="140" />
        <el-table-column label="宠物信息" width="250">
          <template #default="{ row }">
            <div class="pet-info-cell">
              <img 
                :src="getPetInfo(row.petId).image || 'https://picsum.photos/200/200?grayscale'" 
                :alt="getPetInfo(row.petId).name || '宠物图片'" 
                class="pet-small-image"
                @error="($event.target.src = 'https://picsum.photos/200/200?grayscale')"
              >
              <div class="pet-text-info">
                <div>ID: {{ row.petId }}</div>
                <div>名称: {{ getPetInfo(row.petId).name || '未知宠物' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" prop="applyDate" />
        <el-table-column label="状态" width="120">
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
      </el-table>

      <!-- 分页 -->
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
    </div>
  </el-card>
</template>

<style lang="scss" scoped>
.export-btn {
  background-color: #42b983 !important;
  border-color: #42b983 !important;
  color: #ffffff !important;
  box-shadow: 0 2px 4px rgba(66, 185, 131, 0.3);
  border-radius: 8px !important;
  padding: 12px 24px !important;
  
  &:hover {
    background-color: #359469 !important;
    border-color: #359469 !important;
    transform: translateY(-1px);
    box-shadow: 0 4px 8px rgba(66, 185, 131, 0.5);
  }
  
  &:active {
    background-color: #2a7d56 !important;
    border-color: #2a7d56 !important;
  }
}
.page-container {
  min-height: 175vh;
  box-sizing: border-box;
  padding: 20px;

  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 30px;

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

    .reset-btn {
      border-color: #ffb3c1 !important;
      color: #ff6b81 !important;
      border-radius: 8px;
      
      &:hover {
        background-color: #fff5f7 !important;
      }
    }
  }

  .agreement-content {
    max-height: 500px;
    overflow-y: auto;
    padding-right: 10px;
    line-height: 1.8;
    font-size: 14px;
    color: #333;

    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-track {
      background: #f5f5f5;
      border-radius: 3px;
    }

    &::-webkit-scrollbar-thumb {
      background: #ddd;
      border-radius: 3px;
    }

    .agreement-title {
      color: #ff6b81;
      font-weight: 600;
      margin-bottom: 8px;
    }
  }

  .agreement-footer {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 15px 0 5px;
    flex-wrap: wrap;

    .count-down提示 {
      color: #666;
      font-size: 13px;
      white-space: nowrap;
    }

    .agreement-check {
      font-size: 14px;
      display: flex;
      align-items: center;
    }
  }

  .adoption-form {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
    margin-bottom: 30px;

    &:disabled {
      opacity: 0.7;
      pointer-events: none;
    }

    .input-hint {
      color: #666;
      font-size: 12px;
      margin-top: 5px;
    }

    .empty-name {
      border-color: #ff4d4f !important;
    }

    .form-actions {
      display: flex;
      justify-content: center;
      margin-top: 20px;
    }

    .pet-image-container {
      margin-top: 5px;
    }

    .pet-image {
      width: 200px;
      height: 200px;
      object-fit: cover;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      transition: transform 0.3s ease;

      &:hover {
        transform: scale(1.02);
      }
    }
  }

  .record-section {
    margin-top: 30px;

    .record-title {
      font-size: 16px;
      font-weight: 600;
      color: #333;
      margin-bottom: 15px;
      padding-bottom: 10px;
      border-bottom: 1px solid #eee;
    }

    .pet-info-cell {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 5px 0;
    }

    .pet-small-image {
      width: 60px;
      height: 60px;
      object-fit: cover;
      border-radius: 4px;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    }

    .pet-text-info {
      display: flex;
      flex-direction: column;
      gap: 4px;
    }
  }

  .status-tag {
    font-weight: 500;
    border-radius: 12px;
    padding: 0 10px;
  }

  .el-table {
    margin-top: 10px;
    
    :deep(.cell) {
      padding: 12px 10px;
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

  :deep(.el-date-editor.is-disabled) {
    opacity: 0.9;
    cursor: not-allowed;
  }
}
</style>
    