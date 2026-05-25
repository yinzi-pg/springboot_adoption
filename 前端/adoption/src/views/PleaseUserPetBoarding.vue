<script setup>
import {
  RefreshLeft,
  Plus,
  Wallet
} from '@element-plus/icons-vue'
import { ref, onMounted, reactive, watch, computed, onUnmounted } from 'vue'
import { ElMessage, ElSteps, ElStep, ElDialog, ElButton, ElCheckbox } from 'element-plus'
import { useUserInfoStore } from '@/stores/user.js'

// 导入相关接口
import { 
  addUserPetBoardingService,
  getUserPetBoardingByUserPetId,
  getImageByUserPetId
} from '@/api/userPetBoarding'
import { boardingAddService } from '@/api/petBoarding'

// 导入用户相关接口
import { 
  getUserById,
  userDeleteMoney 
} from '@/api/userInformation'

// 获取用户信息Store
const userInfoStore = useUserInfoStore()

// 表单引用
const formRef = ref(null)

// 状态管理
const submitting = ref(false)
const loadingSizes = ref(false)
const loadingImage = ref(false)
const loadingPayment = ref(false)
const activeStep = ref(0)
const userPetId = ref(null)
const showPayDialog = ref(false)

// 寄养协议弹窗相关状态
const agreementDialogVisible = ref(true) // 协议弹窗默认显示
const agreeChecked = ref(false) // 是否同意协议
const countDown = ref(10) // 倒计时10秒
const isCountDownEnd = ref(false) // 倒计时是否结束
const countDownTimer = ref(null) // 倒计时定时器

// 计算属性 - 从用户信息存储中获取余额，格式化保留两位小数
const userBalance = computed(() => {
  const balance = userInfoStore.info?.money || 0
  return Number(balance).toFixed(2)
})

// 每日费用标准
const dailyRates = {
  '猫类': {
    '小型': 50,
    '中型': 60,
    '大型': 70
  },
  '犬类': {
    '小型': 70,
    '中型': 90,
    '大型': 120
  },
  '其他': {
    '小型': 60,
    '中型': 80,
    '大型': 100
  }
}

// 宠物体型列表
const petSizes = ref([
  { label: '小型', value: '小型' },
  { label: '中型', value: '中型' },
  { label: '大型', value: '大型' }
])

// 表单数据
const formData = reactive({
  // 宠物基本信息
  petName: '',
  petType: '',
  petSize: '',
  petAge: null,
  petGender: '',
  healthStatus: '',
  vaccineStatus: '',
  specialHabit: '',
  petImage: '',
  userPetId: 0,
  
  // 寄养人信息
  userName: '',
  userPhone: '',
  
  // 寄养信息
  boardingStart: '',
  boardingEnd: '',
  fee: 0,
  paymentStatus: '未支付',
  specialInstructions: '',
  
  // 系统字段
  userId: ''
})

// 计算寄养天数
const boardingDays = computed(() => {
  if (!formData.boardingStart || !formData.boardingEnd) return 0
  
  const start = new Date(formData.boardingStart)
  const end = new Date(formData.boardingEnd)
  
  if (end <= start) return 0
  
  const diffTime = Math.abs(end - start)
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))// 向上取整
  
  return diffDays
})

// 计算每日费率
const dailyRate = computed(() => {
  return dailyRates[formData.petType]?.[formData.petSize] || 0
})

// 自动计算费用
const calculateFee = () => {
  if (!formData.petType || !formData.petSize || boardingDays.value === 0) {
    formData.fee = 0
    return
  }
  
  const rate = dailyRates[formData.petType]?.[formData.petSize] || 0
  formData.fee = rate * boardingDays.value
}

// 监听相关字段变化，自动计算费用
watch([
  () => formData.petType,
  () => formData.petSize,
  () => formData.boardingStart,
  () => formData.boardingEnd,
  boardingDays// 监听天数变化（间接监听日期）
], calculateFee)// 变化后重新计算费用

// 表单验证规则
const rules = reactive({
  // 宠物基本信息验证
  petName: [
    { required: true, message: '请输入宠物姓名', trigger: 'blur' },
    { min: 1, max: 20, message: '宠物姓名长度在1-20字符', trigger: 'blur' }
  ],
  petType: [
    { required: true, message: '请选择宠物类型', trigger: 'change' }
  ],
  petSize: [
    { required: true, message: '请选择体型大小', trigger: 'change' }
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
  ],
  petImage: [
    { required: true, message: '请上传宠物图片', trigger: 'change' }
  ],
  
  // 寄养人信息验证
  userName: [
    { required: true, message: '请输入寄养人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在2-20字符', trigger: 'blur' }
  ],
  userPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  
  // 寄养信息验证
  boardingStart: [
    { required: true, message: '请选择寄养开始日期', trigger: 'change' }
  ],
  boardingEnd: [
    { required: true, message: '请选择寄养结束日期', trigger: 'change' },
    { 
      validator: (rule, value, callback) => {
        if (value && formData.boardingStart && value < formData.boardingStart) {
          callback(new Error('结束日期不能早于开始日期'))
        } else {
          callback()
        }
      }, 
      trigger: 'change' 
    }
  ],
  fee: [
    { required: true, message: '请确认寄养费用', trigger: 'blur' },
    { type: 'number', min: 1, message: '费用不能为0或负数', trigger: 'blur' }
  ],
  paymentStatus: [
    { required: true, message: '请选择支付状态', trigger: 'change' }
  ]
})

// 图片上传前校验
const beforeUpload = (file) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    ElMessage.error('只能上传JPG/PNG格式的图片')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 10
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB')
    return false
  }
  return true
}

// 上传成功回调
const handleImageUploadSuccess = (result) => {
  if (result.code === 0) {
    formData.petImage = result.data
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error('图片上传失败：' + result.msg)
  }
}

// 上传失败处理
const handleUploadError = (err) => {
  if (err.message.includes('exceeded size limit')) {
    ElMessage.error('文件大小超过限制，请上传2MB以内的图片')
  } else {
    ElMessage.error('上传失败，请稍后重试')
  }
}

// 获取图片URL
const getImageUrl = (path) => {
  if (!path) {
    return ''
  }
  
  if (path.startsWith('/assets/')) {
    try {
      return new URL(`../assets${path}`, import.meta.url).href
    } catch (e) {
      console.error('无法加载图片:', path)
      return ''
    }
  }
  return path
}

// 处理宠物类型变化
const handlePetTypeChange = async (value) => {
  try {
    loadingSizes.value = true;
    if (value === '猫类') {
      petSizes.value = [
        { label: '小型', value: '小型' },
        { label: '中型', value: '中型' }
      ];
    } else if (value === '犬类') {
      petSizes.value = [
        { label: '小型', value: '小型' },
        { label: '中型', value: '中型' },
        { label: '大型', value: '大型' }
      ];
    } else {
      petSizes.value = [
        { label: '小型', value: '小型' },
        { label: '中型', value: '中型' },
        { label: '大型', value: '大型' }
      ];
    }
  } catch (error) {
    ElMessage.error('获取体型数据失败');
    console.error(error);
  } finally {
    loadingSizes.value = false;
  }
};

// 获取宠物图片的方法
const fetchPetImage = async (petId) => {
  if (!petId) return null;
  
  try {
    loadingImage.value = true;
    const response = await getImageByUserPetId(petId);
    
    if (response.code === 0 && response.data) {
      return response.data;
    } else {
      ElMessage.warning('获取宠物图片失败，使用上传的图片');
      return null;
    }
  } catch (error) {
    console.error('获取宠物图片异常:', error);
    ElMessage.warning('获取宠物图片异常，使用上传的图片');
    return null;
  } finally {
    loadingImage.value = false;
  }
};

// 提交宠物信息
const submitPetInfo = async () => {
  if (!formRef.value) return
  
  try {
    const petFields = ['userPetId','petName', 'petType', 'petSize', 'petAge', 'petGender', 'healthStatus', 'vaccineStatus', 'petImage', 'userName', 'userPhone']
    await formRef.value.validateField(petFields)
    
    const userId = userInfoStore.info?.userId || localStorage.getItem('userId')
    if (!userId) {
      ElMessage.error('请先登录')
      return
    }
    formData.userId = userId
    
    submitting.value = true
    
    const userPetRes = await addUserPetBoardingService({
      userId: formData.userId,
      petName: formData.petName,
      petType: formData.petType,
      petSize: formData.petSize,
      petAge: formData.petAge,
      petGender: formData.petGender,
      healthStatus: formData.healthStatus,
      vaccineStatus: formData.vaccineStatus,
      specialHabit: formData.specialHabit,
      petImage: formData.petImage
    })
    
    if (userPetRes.code !== 0) {
      ElMessage.error(`宠物信息提交失败: ${userPetRes.msg || '服务器错误'}`)
      return
    }
    
    if (!userPetRes.data) {
      ElMessage.error('服务器返回数据格式异常')
      return
    }
    
    userPetId.value = userPetRes.data; 
    
    if (!userPetId.value) {
      ElMessage.error('未能获取宠物信息ID，请重试')
      console.error('宠物ID获取失败，返回数据:', userPetRes.data)
      return
    }
    
    const petImageUrl = await fetchPetImage(userPetId.value);
    if (petImageUrl) {
      formData.petImage = petImageUrl;
    }
    
    ElMessage.success('宠物信息提交成功，请填写寄养信息')
    activeStep.value = 1
    
  } catch (error) {
    if (error.name !== 'ValidationError') {
      ElMessage.error('提交接口异常，请稍后重试')
      console.error(error)
    }
  } finally {
    submitting.value = false
  }
}

// 处理支付
const handlePayment = async () => {
  if (!formData.userId || formData.fee <= 0) {
    ElMessage.error('支付信息不完整')
    return
  }
  
  // 使用计算属性获取最新余额，转为数字进行比较
  if (Number(userBalance.value) < formData.fee) {
    ElMessage.error('余额不足，请先充值')
    showPayDialog.value = false
    return
  }
  
  try {
    loadingPayment.value = true
    
    // 按照userInformation.js要求的参数格式调用扣款接口
    const response = await userDeleteMoney({
      userId: Number(formData.userId),  // 确保是数字类型
      amount: Number(formData.fee)     // 转换为数字，匹配后端BigDecimal类型
    })
    
    if (response.code === 0) {
      ElMessage.success('支付成功！')
      // 支付成功后更新用户信息存储中的余额
      userInfoStore.setInfo({
        ...userInfoStore.info,
        money: Number(userInfoStore.info.money) - formData.fee
      })
      formData.paymentStatus = '已支付'
      showPayDialog.value = false
      
      await submitBoardingInfo(true)
    } else {
      ElMessage.error(`支付失败: ${response.msg || '未知错误'}`)
    }
  } catch (error) {
    console.error('支付异常:', error)
    ElMessage.error('支付过程中发生错误，请稍后重试')
  } finally {
    loadingPayment.value = false
  }
}

// 提交寄养信息
const submitBoardingInfo = async (isPaid = false) => {
  if (!formRef.value || !userPetId.value) {
    ElMessage.error('宠物信息不完整，请返回上一步重新提交')
    return
  }
  
  try {
    const boardingFields = ['boardingStart', 'boardingEnd', 'fee', 'paymentStatus']
    await formRef.value.validateField(boardingFields)
    
    if (new Date(formData.boardingStart) >= new Date(formData.boardingEnd)) {
      ElMessage.error('寄养结束日期必须晚于开始日期')
      return
    }
    
    submitting.value = true
    
    let finalPetImage = formData.petImage;
    if (!finalPetImage) {
      const petImageUrl = await fetchPetImage(userPetId.value);
      if (petImageUrl) {
        finalPetImage = petImageUrl;
      }
    }
    
    const boardingRes = await boardingAddService({
      userPetId: userPetId.value,
      userId: formData.userId,
      boardingStart: formData.boardingStart,
      boardingEnd: formData.boardingEnd,
      boardingStatus: isPaid ? '已付款' : '待付款',
      fee: formData.fee,
      paymentStatus: formData.paymentStatus,
      specialInstructions: formData.specialInstructions,
      petImage: finalPetImage
    })
    
    if (boardingRes.code === 0) {
      ElMessage.success('寄养申请已提交成功，请等待处理')
      resetForm()
      activeStep.value = 0
    } else {
      ElMessage.error(`寄养申请提交失败: ${boardingRes.msg || '服务器错误'}`)
    }
    
  } catch (error) {
    if (error.name !== 'ValidationError') {
      ElMessage.error('提交接口异常，请稍后重试')
      console.error(error)
    }
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  formData.petImage = ''
  userPetId.value = null
  activeStep.value = 0
}

// 协议弹窗倒计时逻辑
const startCountDown = () => {
  // 清除已有定时器，避免重复触发
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

// 关闭协议弹窗（需满足条件）
const closeAgreementDialog = () => {
  if (!agreeChecked.value) {
    return ElMessage.warning('请先勾选同意寄养协议')
  }
  if (!isCountDownEnd.value) {
    return ElMessage.warning('请完整浏览协议后再关闭')
  }
  
  agreementDialogVisible.value = false
}

// 初始化
onMounted(() => {
  // 进入页面启动协议倒计时
  startCountDown()
  
  const userInfo = userInfoStore.info
  const userId = userInfo?.userId || localStorage.getItem('userId')
  
  if (userId) {
    formData.userId = userId
    if (userInfo) {
      formData.userName = userInfo.userName || ''
      formData.userPhone = userInfo.userPhone || ''
    }
  } else {
    ElMessage.warning('未检测到登录状态，部分功能可能受限')
  }
})

// 组件卸载时清除定时器
onUnmounted(() => {
  if (countDownTimer.value) {
    clearInterval(countDownTimer.value)
  }
})

// 监听用户信息变化，自动更新
watch(
  () => userInfoStore.info,
  (newUserInfo) => {
    if (newUserInfo) {
      formData.userId = newUserInfo.userId
      formData.userName = newUserInfo.userName || formData.userName
      formData.userPhone = newUserInfo.userPhone || formData.userPhone
    }
  }
)
</script>
<template>
  <el-card class="page-container">
    <!-- 寄养协议弹窗 -->
    <el-dialog
      v-model="agreementDialogVisible"
      title="宠物寄养服务协议"
      width="70%"
      :before-close="() => false"
      :close-on-click-modal="false"
      :show-close="false"
    >
      <div class="agreement-content">
        <h4 class="agreement-title">一、寄养人责任与义务</h4>
        <p>1. 寄养人需提供真实、准确的宠物信息，包括健康状况、疫苗接种情况、特殊习性等。</p>
        <p>2. 寄养前应确保宠物健康状况良好，无传染病、寄生虫或其他可能影响其他宠物健康的疾病。</p>
        <p>3. 寄养人需提前告知宠物是否有攻击性、特殊饮食习惯或需要特殊照顾的情况。</p>
        <p>4. 寄养期间，如宠物因自身健康原因发生意外，寄养中心不承担责任，但会及时通知寄养人。</p>
        
        <h4 class="agreement-title" style="margin-top: 16px;">二、寄养中心责任与义务</h4>
        <p>1. 寄养中心为宠物提供适宜的生活环境，包括干净的住所、充足的饮水和食物。</p>
        <p>2. 每日为宠物提供必要的照顾，包括喂食、换水、清洁卫生和适当活动。</p>
        <p>3. 如发现宠物异常情况，会及时联系寄养人并根据情况采取必要措施。</p>
        <p>4. 寄养期间妥善保管宠物，但对宠物自然死亡、突发疾病或因宠物自身原因导致的伤害不承担责任。</p>
        
        <h4 class="agreement-title" style="margin-top: 16px;">三、费用与支付</h4>
        <p>1. 寄养费用按日计算，根据宠物类型和体型收取，具体标准以系统显示为准。</p>
        <p>2. 寄养人需在确认寄养信息后支付全部费用，费用支付后不予退还。</p>
        <p>3. 如寄养人需延长寄养时间，需提前通知寄养中心并补足相应费用。</p>
        
        <h4 class="agreement-title" style="margin-top: 16px;">四、其他约定</h4>
        <p>1. 寄养人需在约定时间内接送宠物，逾期未接将按日收取额外寄养费用。</p>
        <p>2. 寄养中心有权拒绝接收具有严重攻击性或传染性疾病的宠物。</p>
        <p>3. 寄养期间，寄养中心有权根据实际情况对宠物进行必要的管理和照顾调整。</p>
        <p>4. 本协议自寄养人确认同意并支付费用后生效，双方共同遵守。</p>
      </div>
      
      <div class="agreement-footer">
        <div class="agreement-check">
          <el-checkbox v-model="agreeChecked" style="margin-right: 8px;">
            我已仔细阅读并同意上述寄养协议
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
        <span class="page-title">宠物寄养综合申请</span>
        <div class="operation-group">
          <el-button 
            type="default" 
            :icon="RefreshLeft"
            @click="resetForm"
            class="reset-btn"
            :loading="submitting"
          >
            重置表单
          </el-button>
        </div>
      </div>
    </template>

    <!-- 步骤指示器 -->
    <el-steps :active="activeStep" finish-status="success" class="steps-bar">
      <el-step title="填写宠物信息" />
      <el-step title="填写寄养信息" />
    </el-steps>

    <!-- 综合申请表单 -->
    <el-form 
      :model="formData" 
      :rules="rules" 
      label-width="140px"
      ref="formRef"
      class="application-form"
      :disabled="agreementDialogVisible"
    >
      <!-- 第一步：宠物基本信息 -->
      <template v-if="activeStep === 0">
        <h4 class="form-section-title">一、宠物基本信息</h4>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="宠物姓名" prop="petName">
              <el-input v-model="formData.petName" placeholder="请输入宠物姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="宠物类型" prop="petType">
              <el-select v-model="formData.petType" placeholder="请选择宠物类型" @change="handlePetTypeChange">
                <el-option label="猫类" value="猫类" />
                <el-option label="犬类" value="犬类" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="体型大小" prop="petSize">
              <el-select 
                v-model="formData.petSize" 
                placeholder="请选择体型大小"
                :disabled="loadingSizes"
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
          <el-col :span="12">
            <el-form-item label="宠物年龄" prop="petAge">
              <el-input-number 
                v-model="formData.petAge" 
                :min="0" 
                placeholder="请输入宠物年龄"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="宠物性别" prop="petGender">
              <el-select v-model="formData.petGender" placeholder="请选择宠物性别">
                <el-option label="公" value="公" />
                <el-option label="母" value="母" />
               </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="健康状况" prop="healthStatus">
              <el-input v-model="formData.healthStatus" placeholder="请输入健康状况" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="疫苗状态" prop="vaccineStatus">
              <el-select v-model="formData.vaccineStatus" placeholder="请选择疫苗状态">
                <el-option label="已接种" value="已接种" />
                <el-option label="未接种" value="未接种" />
                <el-option label="部分接种" value="部分接种" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="特殊习性" prop="specialHabit">
              <el-input v-model="formData.specialHabit" placeholder="请输入特殊习性" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="宠物图片" prop="petImage">
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
                  :src="getImageUrl(formData.petImage)" 
                  class="avatar" 
                />
                <div v-else class="upload-placeholder">
                  <el-icon class="upload-icon">
                    <Plus />
                  </el-icon>
                </div>
              </el-upload>
              <div class="input-hint">请上传宠物照片（JPG/PNG格式，不超过2MB）</div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="寄养人姓名" prop="userName">
              <el-input v-model="formData.userName" placeholder="请输入您的姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="userPhone">
              <el-input v-model="formData.userPhone" placeholder="请输入您的手机号码" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item class="form-actions">
          <el-button 
            class="pink-add-btn" 
            type="primary" 
            @click="submitPetInfo"
            :loading="submitting"
          >
            提交宠物信息
          </el-button>
        </el-form-item>
      </template>

      <!-- 第二步：寄养信息 -->
      <template v-if="activeStep === 1">
        <h4 class="form-section-title">二、寄养信息</h4>
        
        <!-- 显示当前宠物ID和图片 -->
        <el-row :gutter="20" v-if="userPetId">
          <el-col :span="24">
            <el-form-item label="当前宠物ID">
              <el-input 
                v-model="userPetId" 
                readonly 
                disabled 
                placeholder="自动生成的宠物ID"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <!-- 显示宠物图片 -->
        <el-row :gutter="20" v-if="formData.petImage && activeStep === 1">
          <el-col :span="24">
            <el-form-item label="宠物图片">
              <img 
                :src="getImageUrl(formData.petImage)" 
                class="avatar" 
                style="max-width: 200px;"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="寄养开始日期" prop="boardingStart">
              <el-date-picker
                v-model="formData.boardingStart"
                type="date"
                placeholder="选择开始日期"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="寄养结束日期" prop="boardingEnd">
              <el-date-picker
                v-model="formData.boardingEnd"
                type="date"
                placeholder="选择结束日期"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="寄养天数" prop="boardingDays">
              <el-input 
                v-model="boardingDays" 
                readonly 
                disabled 
                placeholder="自动计算的寄养天数"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="寄养费用(元)" prop="fee">
              <el-input 
                v-model="formData.fee" 
                readonly 
                disabled 
                placeholder="自动计算的寄养费用"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 费用明细预览 -->
        <el-row :gutter="20" v-if="formData.petType && formData.petSize && boardingDays > 0">
          <el-col :span="24">
            <el-form-item label="费用明细">
              <div class="fee-details">
                <div class="fee-item">
                  {{ formData.petType }} {{ formData.petSize }}: 
                  <span class="highlight">¥{{ dailyRate }}/天</span>
                </div>
                <div class="fee-item">
                  寄养天数: 
                  <span class="highlight">{{ boardingDays }} 天</span>
                </div>
                <div class="fee-calculation">
                  计算: ¥{{ dailyRate }} × {{ boardingDays }} 天 = ¥{{ formData.fee }}
                </div>
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="特殊说明" prop="specialInstructions">
              <el-input 
                v-model="formData.specialInstructions" 
                type="textarea" 
                rows="3"
                placeholder="请输入特殊照顾要求或其他说明"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 显示用户余额信息 -->
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="当前账户余额">
              <el-input 
                v-model="userBalance" 
                readonly 
                disabled 
                :prefix-icon="Wallet"
                placeholder="0.00"
              />
              <div class="input-hint">余额不足时请先充值</div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item class="form-actions">
          <el-button 
            type="default" 
            @click="activeStep = 0"
            style="margin-right: 10px"
            :loading="submitting || loadingImage"
          >
            返回上一步
          </el-button>
          <el-button 
            class="pink-add-btn" 
            type="primary" 
            @click="showPayDialog = true"
            :loading="submitting || loadingImage"
            :disabled="!formData.boardingStart || !formData.boardingEnd || formData.fee <= 0"
          >
            确认并支付
          </el-button>
        </el-form-item>
      </template>
    </el-form>

    <!-- 支付确认对话框 -->
    <el-dialog 
      title="确认支付" 
      v-model="showPayDialog" 
      width="400px"
    >
      <div class="payment-info">
        <p>您确认要支付以下寄养费用吗？</p>
        
        <!-- 支付明细卡片 -->
        <div class="payment-card">
          <div class="payment-header">
            <span class="payment-title">支付明细</span>
          </div>
          
          <div class="payment-details">
            <p><span class="label">寄养宠物：</span>{{ formData.petName }}</p>
            <p><span class="label">宠物类型：</span>{{ formData.petType }} {{ formData.petSize }}</p>
            <p><span class="label">寄养时间：</span>{{ formData.boardingStart }} 至 {{ formData.boardingEnd }}</p>
            <p><span class="label">寄养天数：</span>{{ boardingDays }} 天</p>
            
            <!-- 费用计算明细 -->
            <div class="fee-breakdown">
              <span class="label">费用计算：</span>
              <div class="calculation">
                ¥{{ dailyRate }} <span class="operator">×</span> {{ boardingDays }} 天 
                <span class="operator">=</span> 
                <span class="amount">¥{{ formData.fee.toFixed(2) }}</span>
              </div>
            </div>
            
            <p class="divider"></p>
            
            <p class="total-amount">
              <span class="label">应付总额：</span>
              <span class="total-value">¥{{ formData.fee.toFixed(2) }}</span>
            </p>
            
            <p><span class="label">当前余额：</span>¥{{ userBalance }}</p>
          </div>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="showPayDialog = false">取消</el-button>
        <el-button 
          type="primary" 
          @click="handlePayment"
          :loading="loadingPayment"
        >
          确认支付
        </el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<style lang="scss" scoped>
.page-container {
  min-height: 150vh;
  box-sizing: border-box;
  padding: 20px;

  // 协议弹窗样式
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

  .steps-bar {
    margin-bottom: 30px;
    padding: 20px 0;
  }

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

  .application-form {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
    margin-bottom: 30px;

    &:disabled {
      opacity: 0.7;
      pointer-events: none;
    }

    .form-section-title {
      margin: 20px 0 15px;
      padding-left: 5px;
      border-left: 3px solid #ff6b81;
      color: #333;
      font-size: 16px;
    }

    .input-hint {
      color: #666;
      font-size: 12px;
      margin-top: 5px;
    }

    .form-actions {
      display: flex;
      justify-content: center;
      margin-top: 30px;
    }

    // 费用明细样式
    .fee-details {
      padding: 15px;
      background-color: #f9f9f9;
      border-radius: 6px;
      border-left: 3px solid #42b983;
      
      .fee-item {
        margin-bottom: 8px;
        color: #666;
        
        .highlight {
          color: #333;
          font-weight: 500;
        }
      }
      
      .fee-calculation {
        margin-top: 10px;
        padding-top: 10px;
        border-top: 1px dashed #e0e0e0;
        color: #333;
        font-weight: 500;
      }
    }
  }

  // 宠物图片上传样式
  .avatar-uploader {
    width: 150px;
    height: 150px;
    border-radius: 8px;
    border: 2px dashed #c6e2ff;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;

    &:hover {
      border-color: #99d1ff;
      background-color: rgba(222, 235, 255, 0.2);
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
      flex-direction: column;
      align-items: center;
      justify-content: center;
      color: #99d1ff;
    }

    .upload-icon {
      font-size: 32px;
      margin-bottom: 8px;
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

  // 支付对话框样式
  .payment-info {
    margin: 10px 0;
  }

  .payment-card {
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    overflow: hidden;
    margin-top: 10px;
    
    .payment-header {
      background-color: #f8fafc;
      padding: 12px 15px;
      border-bottom: 1px solid #e5e7eb;
      
      .payment-title {
        font-weight: 600;
        color: #333;
      }
    }
  }

  .payment-details {
    margin: 0;
    padding: 15px;
    background-color: #fff;
    
    p {
      margin: 0 0 12px 0;
      line-height: 1.5;
    }
    
    .label {
      display: inline-block;
      width: 100px;
      color: #666;
      font-size: 14px;
    }
    
    // 费用计算明细样式
    .fee-breakdown {
      margin: 12px 0;
      padding: 10px;
      background-color: #f8fafc;
      border-radius: 4px;
      
      .calculation {
        display: inline;
        color: #333;
        
        .operator {
          margin: 0 5px;
          color: #999;
        }
        
        .amount {
          color: #f56c6c;
          font-weight: 500;
        }
      }
    }
    
    .divider {
      border-top: 1px dashed #e0e0e0;
      margin: 10px 0;
      padding-top: 5px;
    }
    
    .total-amount {
      margin-top: 15px;
      padding-top: 10px;
      border-top: 1px solid #eee;
      
      .total-value {
        color: #f56c6c;
        font-size: 16px;
        font-weight: 600;
      }
    }
  }
}
</style>
