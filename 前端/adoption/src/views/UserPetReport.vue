<script setup>
import {
  RefreshLeft,
  Plus,
  Upload
} from '@element-plus/icons-vue'
import { ref, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'

// 导入流浪宠物上报接口
import { reportAddService } from '@/api/petsReport.js'

// 状态管理
const submitting = ref(false)
const uploadLoading = ref(false)

// 表单数据
const reportForm = reactive({
  petName: '',      // 宠物名字
  petType: '',      // 宠物种类
  petGender: '',    // 宠物性别
  petAge: '',       // 宠物年龄
  findLocation: '', // 发现地点
  contactInfo: '',  // 联系方式
  reportDesc: '',   // 上报描述
  reportImage: '',  // 上报图片URL
  createDate: formatDateTime(new Date()) // 改为包含时分秒的格式
})

// 表单验证规则
const rules = reactive({
  petName: [
    { required: true, message: '请输入宠物名字', trigger: 'blur' },
    { min: 1, max: 20, message: '名字长度在1到20个字符', trigger: 'blur' }
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
    { required: true, message: '请输入发现地点', trigger: 'blur' },
    { min: 5, message: '请提供更详细的地点信息', trigger: 'blur' }
  ],
  contactInfo: [
    { required: true, message: '请输入联系方式', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  reportImage: [
    { required: true, message: '请上传宠物图片', trigger: 'change' }
  ]
})

// 表单引用
const reportFormRef = ref(null)

// 日期时间格式化方法（匹配后端yyyy-MM-dd HH:mm:ss格式）
function formatDateTime(date) {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

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
  uploadLoading.value = true
  return true
}

// 上传成功回调
const handleImageUploadSuccess = (result) => {
  uploadLoading.value = false
  if (result.code === 0) {
    reportForm.reportImage = result.data
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error('图片上传失败：' + result.msg)
  }
}

// 上传失败回调
const handleUploadError = () => {
  uploadLoading.value = false
  ElMessage.error('图片上传失败，请重试')
}

// 提交上报信息
const submitReport = async () => {
  if (!reportFormRef.value) return
  try {
    await reportFormRef.value.validate()
  } catch (error) {
    return ElMessage.warning('请完善表单信息后再提交')
  }

  // 提交前更新为当前时间（确保时分秒准确）
  reportForm.createDate = formatDateTime(new Date())
  
  submitting.value = true
  try {
    const result = await reportAddService({
      ...reportForm,
      userId: localStorage.getItem('userId') || 'anonymous'
    })
    
    if (result.code === 0) {
      ElMessage.success('流浪宠物信息上报成功，感谢您的爱心！')
      resetForm()
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
  if (reportFormRef.value) {
    reportFormRef.value.resetFields()
  }
  reportForm.reportImage = ''
  reportForm.createDate = formatDateTime(new Date()) // 重置时更新为当前时间
}

// 初始化
onMounted(() => {
  // 初始化时设置为当前时间
  reportForm.createDate = formatDateTime(new Date())
})
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span class="page-title">流浪宠物上报</span>
        <div class="operation-group">
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

    <!-- 流浪宠物上报表单 -->
    <el-form 
      :model="reportForm" 
      :rules="rules" 
      label-width="120px"
      ref="reportFormRef"
      class="adoption-form"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="宠物名字" prop="petName">
            <el-input v-model="reportForm.petName" placeholder="请输入宠物的名字" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="宠物种类" prop="petType">
            <el-select v-model="reportForm.petType" placeholder="请选择宠物种类">
              <el-option label="犬类" value="犬类" />
              <el-option label="猫类" value="猫类" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="宠物性别" prop="petGender">
            <el-select v-model="reportForm.petGender" placeholder="请选择宠物性别">
              <el-option label="公" value="公" />
              <el-option label="母" value="母" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="宠物年龄" prop="petAge">
            <el-input v-model="reportForm.petAge" placeholder="请输入宠物年龄（例如：2岁）" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="发现地点" prop="findLocation">
            <el-input v-model="reportForm.findLocation" placeholder="请输入发现宠物的详细地点" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系方式" prop="contactInfo">
            <el-input v-model="reportForm.contactInfo" placeholder="请输入您的手机号码" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="宠物图片" prop="reportImage">
            <el-upload 
              class="avatar-uploader"
              :auto-upload="true"
              :show-file-list="false"
              action="http://localhost:8080/springboot_adoption/upload" 
              :on-success="handleImageUploadSuccess"
              :on-error="handleUploadError"
              :before-upload="beforeUpload"
              :loading="uploadLoading"
              name="file"
            >
              <img v-if="reportForm.reportImage" :src="reportForm.reportImage" class="avatar" />
              <div v-else class="upload-placeholder">
                <el-icon class="upload-icon">
                  <Plus />
                </el-icon>
                <div class="upload-text">点击上传宠物图片</div>
              </div>
            </el-upload>
            <div class="input-hint">请上传清晰的宠物照片，有助于更快救助</div>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="上报描述" prop="reportDesc">
            <el-input 
              v-model="reportForm.reportDesc" 
              type="textarea" 
              rows="4" 
              placeholder="请描述宠物的状态、特征、发现时的情况等信息" 
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="上报时间" prop="createDate">
            <el-date-picker
              v-model="reportForm.createDate"
              type="datetime" 
              placeholder="上报时间（自动生成）"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
              disabled
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item class="form-actions">
        <el-button 
          class="pink-add-btn" 
          type="primary" 
          @click="submitReport"
          :loading="submitting"
        >
          <Plus /> 提交上报信息
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
  </el-card>
</template>

<style lang="scss" scoped>
.page-container {
  min-height: 100vh;
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

  .adoption-form {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
    margin-bottom: 30px;

    .input-hint {
      color: #666;
      font-size: 12px;
      margin-top: 5px;
    }

    .form-actions {
      display: flex;
      justify-content: center;
      margin-top: 20px;
    }

    .upload-placeholder {
      width: 200px;
      height: 200px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      border: 2px dashed #ffb6c1;
      border-radius: 8px;
      color: #ff6b81;
      transition: all 0.3s ease;

      &:hover {
        border-color: #ff758c;
        background-color: rgba(255, 236, 241, 0.2);
      }
    }

    .upload-icon {
      font-size: 32px;
      margin-bottom: 8px;
    }

    .upload-text {
      font-size: 14px;
    }

    .avatar {
      width: 200px;
      height: 200px;
      object-fit: cover;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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

  :deep(.el-upload) {
    margin-top: 5px;
  }
}
</style>