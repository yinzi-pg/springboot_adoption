<template>
  <el-card class="page-container">
    <!-- 志愿者服务协议弹窗（优化居中与样式） -->
    <el-dialog
      v-model="agreementDialogVisible"
      title="志愿者服务协议"
      width="70%"
      :before-close="() => false"
      :close-on-click-modal="false"
      :show-close="false"
      :custom-class="['agreement-dialog', 'center-dialog']"
      :modal-class="['agreement-modal']"
    >
      <!-- 协议标题栏（新增，增强视觉层次） -->
      <div class="agreement-header">
        <div class="agreement-badge">必读条款</div>
        <h3 class="agreement-main-title">志愿者服务协议</h3>
        <p class="agreement-subtitle">请仔细阅读以下条款，确认理解并同意后方可继续申请</p>
      </div>

      <!-- 协议内容区（优化滚动、间距与字体） -->
      <div class="agreement-content">
        <h4 class="agreement-section-title">一、志愿者基本义务</h4>
        <p class="agreement-paragraph">1. 年满18周岁，具备完全民事行为能力，能够独立承担志愿服务过程中的相关责任。</p>
        <p class="agreement-paragraph">2. 认同宠物救助与领养工作的公益理念，自愿无偿参与志愿服务，不谋取商业利益。</p>
        <p class="agreement-paragraph">3. 严格遵守志愿服务安排，按时完成承诺的服务时长，如需请假需提前24小时告知负责人。</p>
        <p class="agreement-paragraph">4. 服务过程中爱护宠物，严禁虐待、遗弃或擅自处置救助宠物，遵守宠物照料的专业规范。</p>
        <p class="agreement-paragraph">5. 保护服务对象隐私，不泄露领养人、救助人及宠物的个人信息和敏感数据。</p>
        
        <h4 class="agreement-section-title">二、志愿服务内容与要求</h4>
        <p class="agreement-paragraph">1. 宠物照料：负责宠物的日常喂食、清洁、健康观察，协助完成疫苗接种、驱虫等基础医疗配合工作。</p>
        <p class="agreement-paragraph">2. 领养审核：协助工作人员联系领养申请人，核实领养条件，参与家访或线上沟通，提交审核意见。</p>
        <p class="agreement-paragraph">3. 活动协助：参与公益宣传、领养活动的筹备与执行，包括场地布置、流程引导、信息登记等工作。</p>
        <p class="agreement-paragraph">4. 医疗辅助：在专业人员指导下，协助照顾病弱宠物，记录医疗过程数据，整理医疗物资。</p>
        <p class="agreement-paragraph">5. 流浪宠物救助：在安全前提下参与流浪宠物的发现、诱捕与临时安置，及时反馈救助需求。</p>
        
        <h4 class="agreement-section-title">三、权利与责任约定</h4>
        <p class="agreement-paragraph">1. 志愿者有权获得志愿服务所需的基础培训、物资支持，以及服务过程中的安全保障指导。</p>
        <p class="agreement-paragraph">2. 服务过程中如因个人故意或重大过失导致宠物受伤、财产损失，需承担相应赔偿责任。</p>
        <p class="agreement-paragraph">3. 若无法继续参与志愿服务，需提前7天提交退出申请，配合完成工作交接，不得擅自离岗。</p>
        <p class="agreement-paragraph">4. 严禁利用志愿者身份从事与公益无关的活动，如发现违规行为，将取消志愿者资格并保留追责权利。</p>
        <p class="agreement-paragraph">5. 协议自志愿者勾选同意并提交申请之日起生效，有效期1年，期满可申请续期。</p>
      </div>
      
      <!-- 协议底部操作区（优化布局与按钮样式） -->
      <div class="agreement-footer">
        <div class="agreement-check-wrapper">
          <el-checkbox v-model="agreeChecked" class="agreement-checkbox">
            我已仔细阅读并同意上述志愿者服务协议
          </el-checkbox>
        </div>
        <div class="agreement-action-group">
          <div class="count-down-hint" :class="{ 'count-down-end': isCountDownEnd }">
            请继续浏览协议：{{ countDown }} 秒
          </div>
          <el-button
            type="primary"
            @click="closeAgreementDialog"
            :disabled="!agreeChecked || !isCountDownEnd"
            class="agreement-confirm-btn"
          >
            确认同意并进入申请
          </el-button>
        </div>
      </div>
    </el-dialog>

    <template #header>
      <div class="header">
        <span class="page-title">志愿者申请</span>
        <div class="operation-group">
          <el-button 
            type="default" 
            :icon="RefreshLeft"
            @click="resetVolunteerForm" 
            class="reset-btn"
            :disabled="agreementDialogVisible"
          >
            重置表单
          </el-button>
        </div>
      </div>
    </template>

    <!-- 表单部分 -->
    <el-form 
      :model="volunteerModel" 
      :rules="rules" 
      label-width="140px"
      ref="volunteerModelRef" 
      class="volunteer-form"
      :disabled="agreementDialogVisible"
    >
      <el-row :gutter="24">
        <!-- 第一列：用户基础信息（自动获取） -->
        <el-col :span="12">
          <el-form-item label="关联用户ID" prop="userId">
            <el-input 
              v-model.number="volunteerModel.userId" 
              type="number" 
              placeholder="当前登录用户ID"
              readonly
              :disabled="true"
              :class="{ 'empty-field': !volunteerModel.userId }"
            />
            <div class="input-hint">已自动获取当前登录用户ID，无需手动输入</div>
          </el-form-item>

          <el-form-item label="申请人姓名" prop="userName">
            <el-input 
              v-model="volunteerModel.userName" 
              placeholder="自动匹配姓名" 
              readonly
              :disabled="true"
              :class="{ 'empty-field': !volunteerModel.userName }"
            />
          </el-form-item>

          <el-form-item label="联系电话" prop="userPhone">
            <el-input 
              v-model="volunteerModel.userPhone" 
              placeholder="自动匹配电话" 
              readonly
              :disabled="true"
              :class="{ 'empty-field': !volunteerModel.userPhone }"
            />
          </el-form-item>

          <el-form-item label="申请日期" prop="createTime">
            <el-date-picker
              v-model="volunteerModel.createTime"
              type="date"
              placeholder="申请日期（自动生成）"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              disabled
            />
          </el-form-item>

          <el-form-item label="志愿者图片">
            <el-upload 
              class="avatar-uploader"
              :auto-upload="true"
              :show-file-list="false"
              action="http://localhost:8080/springboot_adoption/upload" 
              :on-success="handleImageUploadSuccess"
              :before-upload="beforeUpload"
              name="file"
              :disabled="uploadLoading || agreementDialogVisible"
            >
              <img 
                v-if="volunteerModel.volunteerImage" 
                :src="getImageUrl(volunteerModel.volunteerImage)" 
                class="avatar" 
                alt="志愿者照片"
              />
              <div v-else class="upload-placeholder">
                <el-icon class="upload-icon" :loading="uploadLoading">
                  <Plus />
                </el-icon>
              </div>
            </el-upload>
            <div class="input-hint">上传个人照片（支持JPG/PNG格式，不超过2MB）</div>
          </el-form-item>

        </el-col>

        <!-- 第二列：志愿者服务信息 -->
        <el-col :span="12">
          <el-form-item label="服务类型" prop="volunteerType">
            <el-select 
              v-model="volunteerModel.volunteerType" 
              placeholder="请选择您能提供的服务"
              multiple
              style="width: 100%"
            >
              <el-option label="宠物照料" value="宠物照料" />
              <el-option label="领养审核" value="领养审核" />
              <el-option label="活动协助" value="活动协助" />
              <el-option label="医疗辅助" value="医疗辅助" />
              <el-option label="流浪宠物救助" value="流浪宠物救助" />
            </el-select>
          </el-form-item>

          <el-form-item label="服务时长(小时)" prop="serviceHours">
            <el-input 
              v-model.number="volunteerModel.serviceHours" 
              type="number" 
              step="0.01" 
              placeholder="例如：每周可服务10小时"
              min="0" 
            />
            <div class="input-hint">按每周可服务时长填写，支持0.01小时递增</div>
          </el-form-item>

          <el-form-item label="申请状态" prop="volunteerStatus">
            <el-select 
              v-model="volunteerModel.volunteerStatus" 
              placeholder="请选择申请状态"
              disabled
            >
              <el-option label="待审核" value="待审核" />
              <el-option label="已通过" value="已通过" />
              <el-option label="已拒绝" value="已拒绝" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 申请理由 -->
      <el-row :gutter="24">
        <el-col :span="24">
          <el-form-item label="申请理由" prop="applyReason">
            <el-input 
              v-model="volunteerModel.applyReason" 
              type="textarea" 
              rows="5" 
              placeholder="请详细说明您申请成为志愿者的理由、相关经验等信息" 
            />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 备注信息 -->
      <el-row :gutter="24">
        <el-col :span="24">
          <el-form-item label="备注信息" prop="remark">
            <el-input 
              v-model="volunteerModel.remark" 
              type="textarea" 
              rows="3" 
              placeholder="其他需要说明的信息" 
            />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 提交按钮 -->
      <el-form-item class="form-actions">
        <el-button 
          class="pink-add-btn" 
          type="primary" 
          @click="submitVolunteerApply"
          :loading="submitting"
        >
          提交志愿者申请
        </el-button>
        <el-button 
          type="default" 
          @click="resetVolunteerForm" 
          style="margin-left: 16px"
        >
          清空重填
        </el-button>
      </el-form-item>
    </el-form>

    <!-- 我的申请记录 -->
    <div class="record-section">
      <div class="search-group">
        <el-input 
          v-model="searchKeyword" 
          placeholder="搜索服务类型/状态/备注..." 
          :prefix-icon="Search"
          class="search-input"
          clearable
          @keyup.enter="handleSearch"
          :disabled="agreementDialogVisible"
        />
        <el-button 
          type="primary" 
          :icon="Search"
          @click="handleSearch"
          class="search-btn"
          :disabled="agreementDialogVisible"
        >
          搜索
        </el-button>
        <el-button 
          type="default" 
          :icon="RefreshLeft"
          @click="resetSearch"
          class="reset-btn"
          :disabled="agreementDialogVisible"
        >
          重置
        </el-button>
      </div>
      
      <h3 class="record-title">
         我的志愿者申请记录
      </h3>
      <el-table 
        :data="myApplyRecords" 
        style="width: 100%" 
        v-loading="loading"
        element-loading-text="加载申请记录中..."
        border
        stripe
      >
        <el-table-column label="序号" width="80" type="index" />
        <el-table-column label="志愿者ID" prop="volunteerId" width="120" />
        <el-table-column label="关联用户ID" prop="userId" width="120" />
        <el-table-column label="申请人信息" width="220">
          <template #default="{ row }">
            <div class="user-info-cell">
              <div class="user-text-info">
                <div><User class="icon" /> 姓名：{{ row.userInfo?.userName || '未知' }}</div>
                <div><Phone class="icon" /> 电话：{{ row.userInfo?.userPhone || '未知' }}</div>
                <div><Tickets class="icon" /> 用户ID：{{ row.userId }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="服务类型" width="200">
          <template #default="{ row }">
            <div class="type-tags">
              <el-tag 
                v-for="type in (row.volunteerType || '').split(',')" 
                :key="type" 
                effect="light"
                size="small"
              >
                {{ type }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="服务时长(小时)" prop="serviceHours" width="120" />
        <el-table-column label="注册时间" prop="createTime" width="180" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag 
              :type="getStatusType(row.volunteerStatus)" 
              effect="light"
              class="status-tag"
            >
              {{ row.volunteerStatus }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页控件 -->
      <el-pagination 
        v-model:current-page="pageNum" 
        v-model:page-size="pageSize" 
        :page-sizes="[3, 5, 10]"
        layout="jumper, total, sizes, prev, pager, next" 
        background 
        :total="total" 
        @size-change="onSizeChange"
        @current-change="onCurrentChange" 
        style="margin-top: 20px; justify-content: flex-end" 
        :disabled="agreementDialogVisible"
      />
    </div>
  </el-card>
</template>

<script setup>
import {
  RefreshLeft,
  Search,
  User,
  Phone,
  Tickets,
  Plus
} from '@element-plus/icons-vue'
import { ref, onMounted, reactive, unref, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
// 导入Pinia用户Store
import { useUserInfoStore } from '@/stores/user.js'
import { storeToRefs } from 'pinia'

// 导入志愿者相关接口
import { AddVolunteerService, getVolunteerList, searchVolunteerService } from '@/api/volunteer'
// 导入用户信息查询接口（用于申请记录补充信息）
import { getUserById } from '@/api/userInformation'

// 初始化Pinia用户Store并获取userInfo
const userStore = useUserInfoStore()
const { info: userInfo } = storeToRefs(userStore)  // 修复：正确解构获取userInfo

// 协议弹窗相关状态
const agreementDialogVisible = ref(true)
const agreeChecked = ref(false)
const countDown = ref(10) // 志愿者协议阅读时间设为10秒
const isCountDownEnd = ref(false)
const countDownTimer = ref(null)

// 图片路径处理
const getImageUrl = (path) => {
  if (!path) return '/assets/default-volunteer.png'
  if (path.startsWith('/assets/')) {
    try {
      return require('@/assets' + path)
    } catch (e) {
      console.error('无法加载图片:', path)
      return '/assets/default-volunteer.png'
    }
  }
  return path
}

// 图片上传处理
const handleImageUploadSuccess = (result) => {
  uploadLoading.value = false
  if (result.code === 0) {
    volunteerModel.volunteerImage = result.data
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error('图片上传失败：' + result.msg)
  }
}

const beforeUpload = (file) => {
  uploadLoading.value = true
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    uploadLoading.value = false
    ElMessage.error('只能上传JPG/PNG格式的图片')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 10
  if (!isLt2M) {
    uploadLoading.value = false
    ElMessage.error('图片大小不能超过2MB')
    return false
  }
  return true
}

// 分页相关
const pageNum = ref(1)
const pageSize = ref(5)
const total = ref(0)

// 状态管理
const loading = ref(false)
const submitting = ref(false)
const uploadLoading = ref(false)

// 搜索相关变量
const searchKeyword = ref('')

// 表单数据
const volunteerModel = reactive({
  volunteerId: null,
  userId: null,
  volunteerType: [],
  serviceHours: 0.00,
  volunteerStatus: '待审核',
  remark: '',
  createTime: formatDate(new Date()),
  reviewDate: null,
  userName: '',
  userPhone: '',
  volunteerImage:'',
  applyReason: ''
})

// 表单验证规则
const rules = reactive({
  userId: [
    { required: true, message: '未检测到登录用户ID，请先登录', trigger: 'mounted' },
    { type: 'number', message: '用户ID必须为数字', trigger: 'mounted' }
  ],
  volunteerType: [
    { required: true, message: '请选择服务类型', trigger: 'change' },
    { type: 'array', min: 1, message: '至少选择1种服务类型', trigger: 'change' }
  ],
  serviceHours: [
    { required: true, message: '请输入服务时长', trigger: 'blur' },
    { type: 'number', min: 0, message: '服务时长不能为负数', trigger: 'blur' }
  ],
  volunteerStatus: [
    { required: true, message: '请选择申请状态', trigger: 'change' }
  ],
  userName: [
    { required: true, message: '未获取到用户姓名，请检查登录状态', trigger: 'mounted' }
  ],
  userPhone: [
    { required: true, message: '未获取到联系电话，请检查登录状态', trigger: 'mounted' },
    { pattern: /^1[3-9]\d{9}$/, message: '登录用户的手机号格式不正确', trigger: 'mounted' }
  ],
  applyReason: [
    { required: true, message: '请说明申请成为志愿者的理由', trigger: 'blur' },
    { min: 20, message: '申请理由需至少20个字符', trigger: 'blur' }
  ]
})

// 我的申请记录
const myApplyRecords = ref([])

// 表单引用
const volunteerModelRef = ref(null)

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
    return ElMessage.warning('请先勾选同意志愿者服务协议')
  }
  if (!isCountDownEnd.value) {
    return ElMessage.warning('请完整浏览协议后再关闭')
  }
  
  agreementDialogVisible.value = false
}

// 从Pinia自动获取当前登录用户信息
const fillUserInfoFromPinia = () => {
  // 现在可以正确访问userInfo.value了
  if (userInfo.value && userInfo.value.userId) {
    volunteerModel.userId = Number(userInfo.value.userId)
    volunteerModel.userName = userInfo.value.userName || ''
    volunteerModel.userPhone = userInfo.value.userPhone || ''
    ElMessage.success(`已自动获取用户信息：${userInfo.value.userName}`)
  } else {
    ElMessage.warning('未检测到登录用户信息，请先登录')
    volunteerModel.userId = null
    volunteerModel.userName = ''
    volunteerModel.userPhone = ''
  }
}

// 分页事件
const onSizeChange = (size) => {
  pageSize.value = size
  searchKeyword.value ? handleSearch() : getMyApplyRecords()
}

const onCurrentChange = (num) => {
  pageNum.value = num
  searchKeyword.value ? handleSearch() : getMyApplyRecords()
}

// 为申请记录补充用户信息
const fillUserInfo = async (volunteer) => {
  try {
    const userResult = await getUserById(volunteer.userId)
    if (userResult.code === 0) {
      volunteer.userInfo = {
        userName: userResult.data.userName,
        userPhone: userResult.data.userPhone,
        userGender: userResult.data.userGender
      }
    } else {
      volunteer.userInfo = {
        userName: '未知用户',
        userPhone: '未知',
        userGender: '未知'
      }
    }
  } catch (error) {
    volunteer.userInfo = {
      userName: '获取失败',
      userPhone: '获取失败',
      userGender: '获取失败'
    }
    console.error(`获取用户ID为${volunteer.userId}的信息失败`, error)
  }
}

// 获取我的志愿者申请记录
const getMyApplyRecords = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      // 使用Pinia中的用户ID
      userId: userInfo.value?.userId || volunteerModel.userId
    }
    const result = await getVolunteerList(params)
    if (result.code === 0) {
      const volunteerList = result.data.items || []
      for (const volunteer of volunteerList) {
        await fillUserInfo(volunteer)
        volunteer.serviceHours = Number(volunteer.serviceHours)
      }
      myApplyRecords.value = volunteerList
      total.value = result.data.total || 0
    } else {
      myApplyRecords.value = []
      total.value = 0
      ElMessage.error('获取申请记录失败')
    }
  } catch (error) {
    myApplyRecords.value = []
    total.value = 0
    ElMessage.error('获取申请记录接口异常')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 模糊查询申请记录
const handleSearch = async () => {
  const keyword = searchKeyword.value.trim()
  loading.value = true
  try {
    const result = await searchVolunteerService(keyword)
    if (result.code === 0) {
      let volunteerList = result.data || []
      
      // 补充用户信息
      for (const volunteer of volunteerList) {
        await fillUserInfo(volunteer)
        volunteer.serviceHours = Number(volunteer.serviceHours)
      }
      
      myApplyRecords.value = volunteerList
      total.value = volunteerList.length
      
      if (myApplyRecords.value.length === 0) {
        ElMessage.info('未找到匹配的申请记录')
      }
    } else {
      ElMessage.error('搜索失败，请重试')
    }
  } catch (error) {
    ElMessage.error('搜索接口异常')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchKeyword.value = ''
  pageNum.value = 1
  getMyApplyRecords()
  ElMessage.success('已重置搜索')
}

// 提交志愿者申请
const submitVolunteerApply = async () => {
  if (!volunteerModelRef.value) return
  try {
    await volunteerModelRef.value.validate()
  } catch (error) {
    return ElMessage.warning('请完善表单信息后再提交')
  }

  const submitData = {
    ...volunteerModel,
    volunteerType: volunteerModel.volunteerType.join(','),
    createTime: formatDateTime(new Date()),
    // 使用Pinia中的用户ID作为登录用户ID
    loginUserId: userInfo.value?.userId || volunteerModel.userId
  }
  delete submitData.volunteerId
  delete submitData.reviewDate
  delete submitData.userName
  delete submitData.userPhone

  submitting.value = true
  try {
    const result = await AddVolunteerService(submitData)
    if (result.code === 0) {
      ElMessage.success('志愿者申请已提交，请等待管理员审核！')
      resetVolunteerForm()
      getMyApplyRecords()
    } else {
      ElMessage.error(`提交失败：${result.msg || '服务器错误'}`)
    }
  } catch (error) {
    ElMessage.error('提交接口异常，请稍后重试')
    console.error('提交参数：', submitData)
    console.error(error)
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetVolunteerForm = () => {
  const formRef = unref(volunteerModelRef)
  if (formRef) {
    formRef.clearValidate()
  }
  Object.assign(volunteerModel, {
    volunteerId: null,
    userId: null,
    volunteerType: [],
    serviceHours: 0.00,
    volunteerStatus: '待审核',
    remark: '',
    createTime: formatDate(new Date()),
    reviewDate: null,
    userName: '',
    userPhone: '',
    volunteerImage: '',
    applyReason: ''
  })
  // 重置后重新从Pinia获取用户信息
  setTimeout(() => {
    fillUserInfoFromPinia()
  }, 100)
}

// 状态样式映射
const getStatusType = (status) => {
  switch(status) {
    case '已激活': return 'success';
    case '待认证': return 'warning';
    case '已冻结': return 'danger';
    case '已离职': return 'info';
    case '待审核': return 'warning';
    default: return 'info';
  }
}

// 初始化加载
onMounted(() => {
  startCountDown()
  // 从Pinia自动填充用户信息
  fillUserInfoFromPinia()
  // 加载当前用户的申请记录
  getMyApplyRecords()
})

// 组件卸载时清除定时器
onUnmounted(() => {
  if (countDownTimer.value) {
    clearInterval(countDownTimer.value)
  }
})
</script>

<style lang="scss" scoped>
.page-container {
  min-height: 180vh;
  box-sizing: border-box;
  padding: 24px;

  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 32px;

    .page-title {
      font-size: 20px;
      font-weight: 600;
      color: #333;
      line-height: 1;
    }

    .operation-group {
      display: flex;
      align-items: center;
      gap: 16px;
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

  // 协议弹窗样式优化
  .agreement-dialog {
    border-radius: 16px !important;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15) !important;
    overflow: hidden;
    max-width: 900px;
  }

  .center-dialog {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }

  .agreement-modal {
    background-color: rgba(0, 0, 0, 0.5) !important;
  }

  .agreement-header {
    text-align: center;
    padding: 20px 0 16px;
    border-bottom: 1px solid #f5f5f5;
    margin-bottom: 20px;

    .agreement-badge {
      display: inline-block;
      background-color: #fff0f0;
      color: #ff4d4f;
      font-size: 12px;
      padding: 2px 10px;
      border-radius: 12px;
      margin-bottom: 10px;
      font-weight: 500;
    }

    .agreement-main-title {
      font-size: 22px;
      font-weight: 600;
      color: #2c3e50;
      margin: 0 0 8px;
    }

    .agreement-subtitle {
      color: #666;
      font-size: 14px;
      margin: 0;
    }
  }

  .agreement-content {
    max-height: 500px;
    overflow-y: auto;
    padding: 0 10px;
    line-height: 1.8;
    font-size: 15px;
    color: #333;
    margin-bottom: 16px;

    &::-webkit-scrollbar {
      width: 8px;
    }

    &::-webkit-scrollbar-track {
      background: #f5f5f5;
      border-radius: 4px;
    }

    &::-webkit-scrollbar-thumb {
      background: #ddd;
      border-radius: 4px;
      
      &:hover {
        background: #ccc;
      }
    }

    .agreement-section-title {
      color: #2c3e50;
      font-weight: 600;
      margin: 24px 0 12px;
      font-size: 16px;
    }

    .agreement-paragraph {
      margin: 0 0 12px 16px;
      padding-left: 8px;
      position: relative;
      
      &:before {
        content: '';
        position: absolute;
        left: 0;
        top: 10px;
        width: 4px;
        height: 4px;
        border-radius: 50%;
        background-color: #ff6b81;
      }
    }
  }

  .agreement-footer {
    display: flex;
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
    padding: 15px 0 5px;
    border-top: 1px solid #f5f5f5;
    margin-top: 10px;

    .agreement-check-wrapper {
      padding: 8px 0;
    }

    .agreement-checkbox {
      font-size: 14px;
      color: #333;
      
      & .el-checkbox__label {
        padding-left: 8px;
      }
      
      & .el-checkbox__input.is-checked .el-checkbox__inner {
        background-color: #ff6b81;
        border-color: #ff6b81;
      }
    }

    .agreement-action-group {
      display: flex;
      align-items: center;
      justify-content: flex-end;
      gap: 16px;
    }

    .count-down-hint {
      color: #666;
      font-size: 13px;
      white-space: nowrap;
      transition: color 0.3s ease;
    }

    .count-down-end {
      color: #52c41a;
    }

    .agreement-confirm-btn {
      background-color: #ff6b81 !important;
      border-color: #ff6b81 !important;
      color: #fff !important;
      border-radius: 8px;
      padding: 8px 24px;
      font-size: 14px;
      transition: all 0.3s ease;
      
      &:hover {
        background-color: #ff4d6d !important;
        border-color: #ff4d6d !important;
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(255, 107, 129, 0.3);
      }
      
      &:disabled {
        background-color: #ffccd5 !important;
        border-color: #ffccd5 !important;
        color: #fff !important;
        cursor: not-allowed;
        transform: none;
        box-shadow: none;
      }
    }
  }

  .volunteer-form {
    background-color: #fff;
    padding: 24px;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
    margin-bottom: 36px;

    &:disabled {
      opacity: 0.7;
      pointer-events: none;
    }

    .input-hint {
      color: #666;
      font-size: 12px;
      margin-top: 6px;
      line-height: 1.4;
    }

    .empty-field {
      border-color: #ff4d4f !important;
    }

    .form-actions {
      display: flex;
      justify-content: center;
      margin-top: 24px;
      padding-top: 16px;
      border-top: 1px solid #f5f5f5;
    }
  }

  .record-section {
    margin-top: 40px;

    .search-group {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 16px;
      justify-content: flex-end;
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

    .record-title {
      font-size: 18px;
      font-weight: 600;
      color: #333;
      margin-bottom: 16px;
      padding-bottom: 12px;
      border-bottom: 1px solid #eee;
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .user-info-cell {
      display: flex;
      align-items: flex-start;
      gap: 12px;
      padding: 6px 0;
    }

    .user-text-info {
      display: flex;
      flex-direction: column;
      gap: 4px;
      font-size: 14px;

      .icon {
        width: 14px;
        height: 14px;
        margin-right: 4px;
        color: #ff6b81;
      }
    }

    .type-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 4px;
      justify-content: flex-start;
    }
  }

  .status-tag {
    font-weight: 500;
    border-radius: 16px;
    padding: 2px 10px;
    font-size: 13px;
  }

  .pink-add-btn {
    background-color: #ff6b81 !important;
    border-color: #ff6b81 !important;
    color: #ffffff !important;
    box-shadow: 0 2px 8px rgba(255, 107, 129, 0.3);
    border-radius: 8px !important;
    padding: 12px 28px !important;
    font-size: 14px;

    &:hover {
      background-color: #ff4d6d !important;
      border-color: #ff4d6d !important;
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(255, 107, 129, 0.5);
    }

    &:active {
      background-color: #ff3355 !important;
      border-color: #ff3355 !important;
    }
  }

  .el-table {
    margin-top: 12px;
    border-radius: 8px;
    overflow: hidden;
    
    :deep(.cell) {
      padding: 12px 16px;
      font-size: 14px;
    }

    :deep(.el-table__header th) {
      background-color: #fff5f7;
      color: #333;
      font-weight: 600;
    }
  }

  :deep(.el-input.is-disabled .el-input__inner) {
    background-color: #fafafa;
    color: #666;
    opacity: 0.9;
  }

  :deep(.el-date-editor.is-disabled) {
    opacity: 0.9;
    cursor: not-allowed;
  }
}

.avatar-uploader {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 2px dashed #ffb6c1;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;

  &:hover {
    border-color: #ff758c;
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
    color: #ffb6c1;
  }
}
</style>
    