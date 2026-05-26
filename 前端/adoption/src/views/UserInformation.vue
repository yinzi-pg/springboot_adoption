<script setup>
import { ref, reactive, unref } from 'vue';
import { Edit, Delete, Search, RefreshLeft, User,ChatLineSquare, Phone,MessageBox,Notebook, Document,DocumentCopy,SetUp } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox, ElForm } from 'element-plus';

// 导入用户信息管理相关方法
import { getUserList, userAddService, userDeleteService, userUpdateService, searchUserService } from '@/api/userInformation';

//分页条数据模型
const pageNum = ref(1);//当前页码
const pageSize = ref(5);//每页条数
const total = ref(0);//总条数
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

// 用户信息列表数据
const users = ref([]);
// 弹窗控制
const dialogVisible = ref(false);
const detailVisible = ref(false);
// 弹窗标题
const dialogTitle = ref('');
// 表单引用（用于校验）
const userFormRef = ref(null);
// 添加/编辑表单数据模型
const userModel = reactive({
  userId: null,
  userName: '',      // 用户名
  userGender: '',    // 性别
  userAge: '',       // 年龄
  userPhone: '',     // 手机号
  userEmail: '',     // 邮箱
  userRole: ''       // 角色
});

// 详情模型
const detailModel = reactive({
  userId: '',
  userName: '',
  userGender: '',
  userAge: '',
  userPhone: '',
  userEmail: '',
  userRole: '',
  createDate: ''
});

// 搜索关键词
const searchKeyword = ref('');

// 表单校验规则
const rules = reactive({
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 1, max: 20, message: '长度1-20位', trigger: 'blur' }
  ],
  userGender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  userAge: [
    { required: true, message: '请输入年龄', trigger: 'blur' },
    { type: 'number', min: 0, message: '年龄不能为负数', trigger: 'blur' }
  ],
  userPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  userEmail: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  userRole: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
});

// 获取所有用户信息数据
const getAllUsers = async () => {
  try {
    let params = {
        pageNum: pageNum.value,
        pageSize: pageSize.value
    }
    const res = await getUserList(params);
    if (res.code === 0) {
      users.value = res.data.items;
      total.value = res.data.total;
    } else {
      users.value = [];
      ElMessage.warning('未获取到用户数据');
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败，请重试');
    console.error('用户列表接口异常:', error);
  }
};

// 初始化加载数据
getAllUsers();

// 搜索用户
const handleSearch = async () => {
  const keyword = searchKeyword.value.trim();
  if (!keyword) {
    getAllUsers();
    return;
  }

  try {
    const result = await searchUserService(keyword,pageNum.value,pageSize.value);
    if (result.code === 0) {
      users.value = result.data.items;
      total.value = result.data.total;
      if (users.value.length === 0) {
        ElMessage.info(`未找到包含"${keyword}"的用户`);
      }
    } else {
      ElMessage.error('搜索用户失败，请重试');
    }
  } catch (error) {
    ElMessage.error('搜索接口异常');
    console.error('用户搜索异常:', error);
  }
};

// 重置搜索
const resetSearch = () => {
  searchKeyword.value = '';
  getAllUsers();
  ElMessage.success('已重置为全部用户');
};

// 删除用户
const deleteUser = (row) => {
  ElMessageBox.confirm(
    `确认要删除用户【${row.userName}】吗？删除后不可恢复！`,
    '删除确认',
    {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'danger',
      center: true
    }
  ).then(async () => {
    try {
      const res = await userDeleteService(row.userId);
      if (res.code === 0) {
        ElMessage.success('用户删除成功');
        getAllUsers(); // 刷新列表
      } else {
        ElMessage.error(`删除失败：${res.message || '未知错误'}`);
      }
    } catch (error) {
      ElMessage.error('删除接口异常，请重试');
      console.error('用户删除异常:', error);
    }
  }).catch(() => {
    ElMessage.info('已取消删除用户');
  });
};

// 添加用户
const handleAddClick = () => {
  resetUserForm();
  dialogTitle.value = '添加用户信息';
  dialogVisible.value = true;
};

// 处理表单提交
const handleFormSubmit = async () => {
  const formRef = unref(userFormRef);
  if (!formRef) return;

  try {
    await formRef.validate();
    
    if (dialogTitle.value === '添加用户信息') {
      delete userModel.userId;
      await addUser();
    } else {
      if (!userModel.userId) {
        ElMessage.error('编辑用户缺少用户ID，请刷新页面重试');
        return;
      }
      await updateUser();
    }
  } catch (error) {
    console.error('表单校验失败:', error);
  }
};

// 添加用户
const addUser = async () => {
  try {
    const res = await userAddService(userModel);
    if (res.code === 0) {
      ElMessage.success('用户添加成功');
      dialogVisible.value = false;
      resetUserForm();
      getAllUsers(); // 刷新列表
    } else {
      ElMessage.error(`添加失败：${res.message || '未知错误'}`);
    }
  } catch (error) {
    ElMessage.error('添加用户接口异常，请重试');
    console.error('用户添加异常:', error);
  }
};

// 编辑回显
const updateUserInformationEcho = (row) => {
  Object.assign(userModel, JSON.parse(JSON.stringify(row)));
  dialogTitle.value = '修改用户信息';
  dialogVisible.value = true;
};

// 修改用户信息
const updateUser = async () => {
  try {
    const res = await userUpdateService(userModel);
    if (res.code === 0) {
      ElMessage.success('用户修改成功');
      dialogVisible.value = false;
      resetUserForm();
      getAllUsers(); // 刷新列表
    } else {
      ElMessage.error(`修改失败：${res.message || '未知错误'}`);
    }
  } catch (error) {
    ElMessage.error('修改用户接口异常，请重试');
    console.error('用户修改异常:', error);
  }
};

// 重置用户表单
const resetUserForm = () => {
  Object.assign(userModel, {
    userId: null,
    userName: '',
    userGender: '',
    userAge: '',
    userPhone: '',
    userEmail: '',
    userRole: ''
  });

  const formRef = unref(userFormRef);
  if (formRef && typeof formRef.clearValidate === 'function') {
    formRef.clearValidate(); // 确保方法存在再调用
  }
};

// 查看用户详情
const handleViewDetail = (row) => {
  Object.assign(detailModel, JSON.parse(JSON.stringify(row)));
  detailVisible.value = true;
};

// 获取角色样式
const getRoleType = (role) => {
  switch (role) {
    case 'admin':
      return 'primary';
    case 'user':
      return 'success';
    case 'volunteer':
      return 'info';
    default:
      return 'default';
  }
};
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span class="page-title">用户信息管理</span>
        <div class="operation-group">
          <div class="search-group">
            <el-input 
              v-model="searchKeyword" 
              placeholder="搜索用户名/手机号/邮箱..." 
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
        </div>
      </div>
    </template>

    <!-- 用户信息表格 -->
    <el-table :data="users" style="width: 100%" border stripe>
      <el-table-column label="序号" width="100" type="index" align="center" />
      <el-table-column label="用户ID" prop="userId" align="center" />
      <el-table-column label="用户名" prop="userName" align="center" />
      <el-table-column label="性别" prop="userGender" align="center" />
      <el-table-column label="年龄" prop="userAge" align="center" />
      <el-table-column label="手机号" prop="userPhone" align="center" />
      <el-table-column label="邮箱" prop="userEmail" align="center" />
      <el-table-column label="角色" align="center">
        <template #default="{ row }">
          <el-tag 
            :type="getRoleType(row.userRole)" 
            effect="light"
            class="role-tag"
          >
            {{ row.userRole }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createDate" align="center" width="180" />
      <el-table-column label="操作" width="220" align="center">
        <template #default="{ row }">
          <el-button 
            :icon="Edit" 
            circle 
            plain 
            type="primary" 
            @click="updateUserInformationEcho(row)"
            size="small" 
            title="编辑"
          ></el-button>
          <el-button 
            :icon="Delete" 
            circle 
            plain 
            type="danger" 
            @click="deleteUser(row)"
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
        <el-empty description="暂无用户信息" />
      </template>
    </el-table>
    <!-- 分页条 -->
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[1, 3, 5]"
            layout="jumper, total, sizes, prev, pager, next" background :total="total" @size-change="onSizeChange"
            @current-change="onCurrentChange" style="margin-top: 20px; justify-content: flex-end" />
    <!-- 添加/编辑用户信息弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="40%"
      destroy-on-close
    >
      <el-form 
        :model="userModel" 
        :rules="rules" 
        label-width="120px"
        ref="userFormRef"
        class="user-form"
      >
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="userModel.userName" placeholder="请输入用户名" />
        </el-form-item>
        
        <el-form-item label="性别" prop="userGender">
          <el-select v-model="userModel.userGender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="年龄" prop="userAge">
          <el-input-number 
            v-model="userModel.userAge" 
            :min="0" 
            placeholder="请输入年龄"
          />
        </el-form-item>
        
        <el-form-item label="手机号" prop="userPhone">
          <el-input v-model="userModel.userPhone" placeholder="请输入手机号" />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="userEmail">
          <el-input v-model="userModel.userEmail" placeholder="请输入邮箱" />
        </el-form-item>
        
        <el-form-item label="角色" prop="userRole">
          <el-select v-model="userModel.userRole" placeholder="请选择角色">
            <el-option label="管理员" value="admin" />
            <el-option label="普通用户" value="user" />
            <el-option label="志愿者" value="volunteer" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span>
          <el-button @click="dialogVisible = false">取消</el-button>
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

    <!-- 用户详情弹窗 -->
    <el-dialog 
      v-model="detailVisible" 
      title="用户详情" 
      width="50%" 
      destroy-on-close
      :close-on-click-modal="false"
      class="detail-dialog"
    >
      <!-- 详情头部：用户ID+装饰线 -->
      <div class="detail-header">
        <div class="detail-header__title">
          <User class="icon-user" />
          <span>用户ID：{{ detailModel.userId }}</span>
        </div>
        <div class="detail-header__line"></div>
      </div>

      <!-- 详情内容：卡片式布局 -->
      <div class="detail-content">
        <!-- 第一行：基本信息卡片 -->
        <div class="detail-card">
          <div class="detail-card__title">
            <Notebook class="icon-title" />
            <span>基本信息</span>
          </div>
          <div class="detail-card__content">
            <div class="detail-item">
              <span class="detail-label">用户名：</span>
              <span class="detail-value">{{ detailModel.userName }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">性别：</span>
              <span class="detail-value">
                <SetUp class="icon-small" />
                {{ detailModel.userGender }}
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">年龄：</span>
              <span class="detail-value">{{ detailModel.userAge }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">角色：</span>
              <span class="detail-value">
                <el-tag :type="getRoleType(detailModel.userRole)" effect="light" class="role-tag">
                  {{ detailModel.userRole }}
                </el-tag>
              </span>
            </div>
          </div>
        </div>

        <!-- 第二行：联系信息卡片 -->
        <div class="detail-card">
          <div class="detail-card__title">
            <Phone class="icon-title" />
            <span>联系信息</span>
          </div>
          <div class="detail-card__content">
            <div class="detail-item">
              <span class="detail-label">手机号：</span>
              <span class="detail-value">
                <Phone class="icon-small" />
                {{ detailModel.userPhone }}
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">邮箱：</span>
              <span class="detail-value">
                <DocumentCopy class="icon-small" />
                {{ detailModel.userEmail }}
              </span>
            </div>
          </div>
        </div>

        <!-- 第三行：系统信息卡片 -->
        <div class="detail-card">
          <div class="detail-card__title">
            <MessageBox class="icon-title" />
            <span>系统信息</span>
          </div>
          <div class="detail-card__content">
            <div class="detail-item">
              <span class="detail-label">创建时间：</span>
              <span class="detail-value">
                <MessageBox class="icon-small" />
                {{ detailModel.createDate || '未知' }}
              </span>
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
      
      .icon-user {
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

  .role-tag {
    padding: 2px 8px;
    border-radius: 4px;
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
}
</style>
