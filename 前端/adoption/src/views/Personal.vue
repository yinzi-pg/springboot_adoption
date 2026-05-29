<script setup>
import { ref, reactive, unref, computed, onMounted } from 'vue'
import { Edit, Plus } from '@element-plus/icons-vue'
import { userUpdateService, userAddMoney, getUserById } from '@/api/userInformation'
import { useUserInfoStore } from '@/stores/user.js'
import { ElMessage, ElLoading } from 'element-plus'
import request from '@/utils/request'

// 状态管理
const userInfoStore = useUserInfoStore()
const userInfo = ref(userInfoStore.info)
const BACKEND_BASE = 'http://localhost:8080/springboot_adoption';
// 编辑对话框状态
const dialogVisible = ref(false)
const title = ref('')
const formRef = ref(null)

// 充值对话框状态
const rechargeDialogVisible = ref(false)
const rechargeFormRef = ref(null)
const isRecharging = ref(false)

// 表单数据模型
const userModel = reactive({
  userId: '',
  userName: '',
  userPassword: '',
  userGender: '',
  userAge: '',
  userPhone: '',
  userEmail: '',
  avatar: '',
  userRole: '',
  money: 0  // 余额字段
})

// 充值表单数据
const rechargeForm = reactive({
  amount: null  // 只需要金额，userId从用户信息中获取
})

// 表单验证规则
const rules = reactive({
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
  ],
  userPhone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  userEmail: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
})

// 充值表单验证规则（修复版）
const rechargeRules = reactive({
  amount: [
    { required: true, message: '请输入充值金额', trigger: 'blur' },
    { 
      type: 'number', 
      message: '请输入有效的数字', 
      trigger: 'blur' 
    },
    // 修复验证规则：确保是数字且大于0
    { 
      validator: (rule, value, callback) => {
        // 显式转换为数字并验证
        const num = Number(value)
        if (isNaN(num) || num <= 0) {
          callback(new Error('充值金额必须大于0元'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

// 格式化余额显示
const formattedMoney = computed(() => {
  if (!userInfo.value || userInfo.value.money === undefined) {
    return '0.00 元'
  }
  const money = Number(userInfo.value.money)
  return money.toFixed(2) + ' 元'
})

// 页面加载时获取用户信息
onMounted(() => {
  fetchUserInfo()
})

// 从数据库获取当前用户信息
const fetchUserInfo = async () => {
  let loading = null
  try {
    loading = ElLoading.service({
      lock: true,
      text: '加载用户信息中...',
      background: 'rgba(255, 255, 255, 0.7)'
    })

    // 获取原始userId并打印调试信息
    const rawUserId = userInfoStore.info?.userId
    console.log('原始userId值:', rawUserId)
    console.log('原始userId类型:', typeof rawUserId)

    // 强制转换为整数并严格验证
    const currentUserId = parseInt(rawUserId, 10)
    console.log('转换后userId:', currentUserId)

    // 严格验证条件
    if (
      isNaN(currentUserId) ||          // 不是数字
      currentUserId <= 0 ||            // 不是正数
      !Number.isInteger(currentUserId) // 不是整数
    ) {
      ElMessage.error(`用户ID格式错误: 必须是正整数，当前值为${rawUserId}`)
      return
    }

    // 调用接口时传递验证后的整数ID
    const result = await getUserById(currentUserId)

    // 处理后端响应
    if ([0, 200].includes(result.code) && result.data) {
      const currentUser = result.data
      userInfo.value = currentUser
      userInfoStore.setInfo(currentUser)  // 更新缓存
      ElMessage.success('用户信息加载成功')
    } else {
      ElMessage.error(`获取用户信息失败：${result.message || '用户不存在'}`)
    }
  } catch (error) {
    console.error('获取用户信息错误详情：', error)
    console.error('请求参数：', { userId: parseInt(userInfoStore.info?.userId, 10) })
    console.error('响应状态码：', error.response?.status)
    console.error('后端错误信息：', error.response?.data)
    
    // 错误类型提示
    if (error.response?.status === 400) {
      ElMessage.error('参数错误：用户ID格式不正确（必须是正整数）')
    } else if (error.response?.status === 404) {
      ElMessage.error('接口地址错误，请检查后端路径')
    } else if (error.response?.status === 401) {
      ElMessage.error('未授权访问，请重新登录')
    } else {
      ElMessage.error('网络异常，无法获取用户信息')
    }
  } finally {
    if (loading) {
      loading.close()
    }
  }
}

// 打开编辑对话框
const openEditDialog = () => {
  title.value = '编辑个人资料'
  if (userInfo.value && Object.keys(userInfo.value).length > 0) {
    // 确保userId是整数类型
    userModel.userId = parseInt(userInfo.value.userId, 10) || ''
    userModel.userName = userInfo.value.userName || ''
    userModel.userPassword = ''  // 密码默认空
    userModel.userGender = userInfo.value.userGender || ''
    userModel.userAge = userInfo.value.userAge || ''
    userModel.userPhone = userInfo.value.userPhone || ''
    userModel.userEmail = userInfo.value.userEmail || ''
    userModel.avatar = userInfo.value.avatar || ''
    userModel.userRole = userInfo.value.userRole || ''
    userModel.money = userInfo.value.money || 0
    dialogVisible.value = true
  } else {
    ElMessage.warning('未获取到用户信息，无法编辑')
  }
}

// 打开充值对话框
const openRechargeDialog = () => {
  if (!userInfo.value || !userInfo.value.userId) {
    ElMessage.warning('未获取到用户信息，无法充值')
    return
  }
  
  // 重置充值表单
  rechargeForm.amount = null
  rechargeDialogVisible.value = true
}

// 保存用户信息
const saveUserInfo = async () => {
  const form = unref(formRef)
  if (!form) return

  let loading = null
  try {
    await form.validate()
    
    // 验证userId格式
    const userId = parseInt(userModel.userId, 10)
    if (isNaN(userId) || userId <= 0 || !Number.isInteger(userId)) {
      ElMessage.error(`用户ID格式错误: 必须是正整数`)
      return
    }

    loading = ElLoading.service({
      lock: true,
      text: '保存中...',
      background: 'rgba(255, 255, 255, 0.7)'
    })

    // 处理提交数据（移除空密码）
    const submitData = { ...userModel }
    // 确保提交的userId是整数类型
    submitData.userId = userId
    if (!submitData.userPassword) {
      delete submitData.userPassword
    }

    const result = await userUpdateService(submitData)
    
    if ([0, 200].includes(result.code)) {
      userInfoStore.setInfo({ ...userInfo.value, ...submitData })
      userInfo.value = userInfoStore.info  // 刷新显示
      ElMessage.success('个人信息修改成功')
      dialogVisible.value = false
    } else {
      ElMessage.error(`修改失败：${result.message || '未知错误'}`)
    }
  } catch (error) {
    if (error.name !== 'ValidationError') {
      console.error('修改个人信息失败：', error)
      ElMessage.error('网络异常，修改失败')
    }
  } finally {
    if (loading) {
      loading.close()
    }
  }
}

// 处理充值（修复版）
const handleRecharge = async () => {
  const form = unref(rechargeFormRef)
  if (!form) return

  try {
    // 先进行表单验证
    await form.validate()
    
    const rawUserId = userInfo.value?.userId
    const userId = parseInt(rawUserId, 10)
    // 确保amount是数字类型
    const amount = Number(rechargeForm.amount)
    
    // 额外的参数验证（双重保险）
    if (!Number.isInteger(userId) || userId <= 0) {
      ElMessage.error(`用户ID必须是正整数，当前值: ${rawUserId}`)
      return
    }
    
    // 修正金额验证逻辑
    if (typeof amount !== 'number' || amount <= 0 || isNaN(amount)) {
      ElMessage.error(`充值金额必须是正数，当前值: ${rechargeForm.amount}`)
      return
    }

    isRecharging.value = true
    
    // 打印参数信息用于调试
    console.log('充值参数验证通过:', {
      userId: { value: userId, type: typeof userId, isInteger: Number.isInteger(userId) },
      amount: { value: amount, type: typeof amount, isNumber: !isNaN(amount) }
    })
    
    // 调用充值接口
    const result = await userAddMoney({ userId, amount })
    
    if ([0, 200].includes(result.code)) {
      ElMessage.success('充值成功')
      rechargeDialogVisible.value = false
      fetchUserInfo()
    } else {
      ElMessage.error(`充值失败：${result.message || '服务器返回错误'}`)
    }
    
  } catch (error) {
    console.error('充值错误详情:', error)
    
    // 区分是验证错误还是其他错误
    if (error.name === 'ValidationError' || (error.amount && error.amount.length)) {
      // 表单验证错误，提取第一个错误信息
      const firstError = error.amount?.[0] || error
      ElMessage.error(firstError.message || '表单验证失败')
    } else if (error.name === 'AbortError') {
      ElMessage.error('请求超时，请稍后重试')
    } else if (error.message.includes('Network Error')) {
      ElMessage.error('网络连接错误，请检查后端服务是否启动')
    } else if (error.response) {
      ElMessage.error(`服务器错误 [${error.response.status}]: ${error.response.data?.message || '未知错误'}`)
    } else {
      ElMessage.error(`充值失败: ${error.message || '未知错误'}`)
    }
  } finally {
    isRecharging.value = false
  }
}

// 头像上传处理
const uploadAvatar = async (options) => {
  try {
    const formData = new FormData()
    formData.append('file', options.file)
    const result = await request.post('/upload', formData)
    
    if (result.code === 0) {
      userModel.avatar = result.data
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error(`头像上传失败：${result.message || '未知错误'}`)
    }
  } catch (error) {
    console.error('头像上传异常：', error)
    ElMessage.error('头像上传失败，请重试')
  }
}

// 头像上传校验
const beforeAvatarUpload = (file) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 10

  if (!isJpgOrPng) {
    ElMessage.error('上传头像图片只能是 JPG/PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
  }
  return isJpgOrPng && isLt2M
}
</script>
<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>个人资料</span>
        <div class="extra">
          <el-button type="primary" @click="openEditDialog">
            <el-icon><Edit /></el-icon> 编辑资料
          </el-button>
        </div>
      </div>
    </template>
    
    <div class="user-profile">
      <div class="avatar-container" v-if="userInfo">
        <el-avatar :size="100" :src="BACKEND_BASE+userInfo.avatar || '/src/assets/default.png'" />
        <div class="username">{{ userInfo.userName || '未设置' }}</div>
        
        <!-- 余额显示和充值按钮 -->
        <div class="balance-info">
          <span class="balance-label">账户余额：</span>
          <span class="balance-value">{{ formattedMoney }}</span>
          <el-button 
            type="success" 
            size="small" 
            class="recharge-btn"
            @click="openRechargeDialog"
          >
            <el-icon><Plus /></el-icon> 充值
          </el-button>
        </div>
      </div>
      
      <el-empty v-else description="暂无用户信息" />
      
      <el-descriptions v-if="userInfo" :column="1" border>
        <el-descriptions-item label="用户名">{{ userInfo.userName || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ userInfo.userGender || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ userInfo.userAge || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ userInfo.userPhone || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ userInfo.userEmail || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ userInfo.userRole || '普通用户' }}</el-descriptions-item>
        <el-descriptions-item label="账户余额">{{ formattedMoney }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <!-- 编辑资料对话框 -->
    <el-dialog v-model="dialogVisible" :title="title" width="500px">
      <el-form ref="formRef" :model="userModel" :rules="rules" label-width="80px">
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :http-request="uploadAvatar"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="userModel.avatar" :src="BACKEND_BASE+userModel.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Edit /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="userModel.userName" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="性别" prop="userGender">
          <el-select v-model="userModel.userGender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄" prop="userAge">
          <el-input v-model.number="userModel.userAge" type="number" placeholder="请输入年龄" />
        </el-form-item>
        <el-form-item label="手机号" prop="userPhone">
          <el-input v-model="userModel.userPhone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="userEmail">
          <el-input v-model="userModel.userEmail" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="密码（选填）">
          <el-input v-model="userModel.userPassword" type="password" placeholder="不修改请留空" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveUserInfo">保存</el-button>
      </template>
    </el-dialog>

    <!-- 充值对话框 -->
    <el-dialog v-model="rechargeDialogVisible" title="余额充值" width="400px">
      <el-form ref="rechargeFormRef" :model="rechargeForm" :rules="rechargeRules" label-width="80px">
        <el-form-item label="充值金额" prop="amount">
          <el-input 
            v-model.number="rechargeForm.amount" 
            type="number" 
            placeholder="请输入充值金额" 
            min="1"
          >
            <template #append>元</template>
          </el-input>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="rechargeDialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          @click="handleRecharge"
          :loading="isRecharging"
        >
          确认充值
        </el-button>
      </template>
    </el-dialog>
  </el-card>
</template>



<style lang="scss" scoped>
.page-container {
  min-height: 85vh;
  box-sizing: border-box;
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  
  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  
  .user-profile {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 0px;
    width: 100%;
    
    .avatar-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      margin-bottom: 30px;
      width: 100%;
      
      .username {
        margin-top: 10px;
        font-size: 18px;
        font-weight: bold;
        text-align: center;
      }
      
      .balance-info {
        margin-top: 15px;
        display: flex;
        align-items: center;
        gap: 10px;
        
        .balance-label {
          font-size: 16px;
          color: #666;
        }
        
        .balance-value {
          font-size: 18px;
          font-weight: bold;
          color: #2c3e50;
        }
        
        .recharge-btn {
          margin-left: 10px;
        }
      }
    }
  }
  
  .el-descriptions {
    width: 100%;
    margin-top: 20px;
    
    ::v-deep .el-descriptions__label {
      font-weight: 500;
      background-color: #f5f7fa;
    }
  }

  .avatar-uploader {
    text-align: center;
    
    .avatar {
      width: 178px;
      height: 178px;
      display: block;
      border-radius: 50%;
      object-fit: cover;
      margin: 0 auto;
    }
    
    .avatar-uploader-icon {
      width: 178px;
      height: 178px;
      line-height: 178px;
      border-radius: 50%;
      font-size: 28px;
      color: #8c939d;
      background-color: #f5f7fa;
      margin: 0 auto;
    }
  }
}
</style>
    