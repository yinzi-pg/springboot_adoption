<script setup>
import { ref, reactive, unref } from 'vue';
import { Edit, Delete, Search, RefreshLeft, Key, InfoFilled, User, Lock } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox, ElForm } from 'element-plus';

// 导入角色管理相关方法
import { getRoleList, roleAddService, roleDeleteService, roleUpdateService, searchRoleService } from '@/api/role';

//分页条数据模型
const pageNum = ref(1);//当前页码
const pageSize = ref(5);//每页条数
const total = ref(0);//总条数
//每页条数改变
const onSizeChange = (size) => {
    pageSize.value = size;
    getAllRoles();
}
//当前页码改变
const onCurrentChange = (num) => {
    pageNum.value = num;
    getAllRoles();
}

// 角色列表数据
const roles = ref([]);

// 弹窗控制
const dialogVisible = ref(false);
const detailVisible = ref(false);

// 弹窗标题
const title = ref('');

// 角色数据模型（与表单字段匹配）
const roleModel = reactive({
  roleId: null,
  roleName: '',      // 角色名称
  roleCode: '',      // 角色编码（如 admin/user）
  roleDesc: '',      // 角色描述
  permissions: ''    // 角色权限（逗号分隔的字符串）
});

// 详情模型
const detailModel = reactive({
  roleId: '',
  roleName: '',
  roleCode: '',
  roleDesc: '',
  permissions: '',   // 角色权限（逗号分隔的字符串）
  createDate: ''
});

// 用于权限选择的临时数组
const selectedPermissions = ref([]);

// 搜索关键词
const searchKeyword = ref('');

// 表单校验规则
const rules = reactive({
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 1, max: 20, message: '长度1-20位', trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '仅支持字母、数字和下划线', trigger: 'blur' }
  ]
});

// 权限列表（模拟数据，实际应从接口获取）
const permissionOptions = ref([
  { label: '用户管理', value: '用户管理' },
  { label: '宠物管理', value: '宠物管理' },
  { label: '角色管理', value: '角色管理' },
  { label: '领养审核', value: '领养审核' },
  { label: '订单管理', value: '订单管理' },
  { label: '内容管理', value: '内容管理' }
]);

// 表单引用
const roleFormRef = ref(null);

// 获取所有角色信息
const getAllRoles = async () => {
  try {
      let params = {
    pageNum: pageNum.value,
    pageSize: pageSize.value
  }
    const res = await getRoleList(params);
    if (res.code === 0) {
      // 处理权限数据，确保是字符串格式并过滤空值
      roles.value = res.data.items.map(role => ({
        ...role,
        permissions: role.permissions ? 
          (Array.isArray(role.permissions) ? role.permissions.join(',') : role.permissions) : 
          ''
      }));
      total.value = res.data.total;
    } else {
      roles.value = [];
      ElMessage.warning('未获取到角色数据');
    }
  } catch (error) {
    ElMessage.error('获取角色列表失败，请重试');
    console.error('角色列表接口异常:', error);
  }
};

// 初始化加载角色数据
getAllRoles();

// 搜索角色
const handleSearch = async () => {
  const keyword = searchKeyword.value.trim();
  if (!keyword) {
    getAllRoles();
    return;
  }

  try {
    const result = await searchRoleService(keyword);
    if (result.code === 0) {
      roles.value = result.data.map(role => ({
        ...role,
        permissions: role.permissions ? 
          (Array.isArray(role.permissions) ? role.permissions.join(',') : role.permissions) : 
          ''
      }));
      if (roles.value.length === 0) {
        ElMessage.info(`未找到包含"${keyword}"的角色`);
      }
    } else {
      ElMessage.error('搜索角色失败，请重试');
    }
  } catch (error) {
    ElMessage.error('搜索接口异常');
    console.error('角色搜索异常:', error);
  }
};

// 重置搜索
const resetSearch = () => {
  searchKeyword.value = '';
  getAllRoles();
  ElMessage.success('已重置为全部角色');
};

// 删除角色
const deleteRole = (row) => {
  ElMessageBox.confirm(
    `确认要删除角色【${row.roleName}】吗？删除后关联用户将失去对应权限！`,
    '删除确认',
    {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'danger',
      center: true
    }
  ).then(async () => {
    try {
      const res = await roleDeleteService(row.roleId);
      if (res.code === 0) {
        ElMessage.success('角色删除成功');
        getAllRoles(); // 刷新列表
      } else {
        ElMessage.error(`删除失败：${res.message || '未知错误'}`);
      }
    } catch (error) {
      ElMessage.error('删除接口异常，请重试');
      console.error('角色删除异常:', error);
    }
  }).catch(() => {
    ElMessage.info('已取消删除角色');
  });
};

// 处理权限选择变化
const handlePermissionChange = (value) => {
  // 将选中的权限数组转为逗号分隔的字符串
  roleModel.permissions = value.join(',');
  selectedPermissions.value = [...value];
};

// 添加角色
const addRole = async () => {
  try {
    const res = await roleAddService(roleModel);
    if (res.code === 0) {
      ElMessage.success('角色添加成功');
      dialogVisible.value = false;
      getAllRoles();
      // 重置表单
      resetRoleModel();
    } else {
      ElMessage.error(`添加失败：${res.message || '未知错误'}`);
    }
  } catch (error) {
    ElMessage.error('添加角色接口异常，请重试');
    console.error('角色添加异常:', error);
  }
};

// 编辑角色回显
const updateRoleEcho = (row) => {
  title.value = '修改角色信息';
  dialogVisible.value = true;
  // 深拷贝避免直接修改原数据
  const roleData = JSON.parse(JSON.stringify(row));
  
  // 处理权限数据，将字符串转为数组用于回显，过滤空值
  const permissionsArray = roleData.permissions 
    ? roleData.permissions.split(',').filter(p => p.trim()) 
    : [];
  selectedPermissions.value = permissionsArray;
  
  Object.assign(roleModel, {
    ...roleData,
    // 保持permissions为字符串格式
    permissions: roleData.permissions || ''
  });
};

// 修改角色
const updateRole = async () => {
  try {
    const res = await roleUpdateService(roleModel);
    if (res.code === 0) {
      ElMessage.success('角色修改成功');
      dialogVisible.value = false;
      getAllRoles();
      // 重置表单
      resetRoleModel();
    } else {
      ElMessage.error(`修改失败：${res.message || '未知错误'}`);
    }
  } catch (error) {
    ElMessage.error('修改角色接口异常，请重试');
    console.error('角色修改异常:', error);
  }
};

// 查看角色详情
const handleViewDetail = (row) => {
  const roleData = JSON.parse(JSON.stringify(row));
  Object.assign(detailModel, roleData);
  detailVisible.value = true;
};

// 重置角色数据模型
const resetRoleModel = () => {
  Object.assign(roleModel, {
    roleId: null,
    roleName: '',
    roleCode: '',
    roleDesc: '',
    permissions: ''
  });
  
  selectedPermissions.value = [];

  const formRef = unref(roleFormRef);
  if (formRef && typeof formRef.clearValidate === 'function') {
    formRef.clearValidate(); // 确保方法存在再调用
  }
};

// 表单提交
const handleFormSubmit = async () => {
  const formRef = unref(roleFormRef);
  if (!formRef) return;

  try {
    await formRef.validate();
    
    if (title.value === '添加角色') {
      delete roleModel.roleId;
      await addRole();
    } else {
      if (!roleModel.roleId) {
        ElMessage.error('编辑角色缺少角色ID，请刷新页面重试');
        return;
      }
      await updateRole();
    }
  } catch (error) {
    console.error('表单校验失败:', error);
  }
};
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span class="page-title">角色管理</span>
        <div class="operation-group">
          <div class="search-group">
            <el-input 
              v-model="searchKeyword" 
              placeholder="搜索角色名称/编码..." 
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
            @click="dialogVisible = true; title = '添加角色'; resetRoleModel()"
          >
            添加角色
          </el-button>
        </div>
      </div>
    </template>

    <!-- 角色列表表格 -->
    <el-table :data="roles" style="width: 100%" border stripe>
      <el-table-column label="序号" width="100" type="index" align="center" />
      <el-table-column label="角色ID" prop="roleId" align="center" />
      <el-table-column label="角色名称" prop="roleName" align="center" />
      <el-table-column label="角色编码" prop="roleCode" align="center" />
      <el-table-column label="权限列表" width="300" align="center">
        <template #default="{ row }">
          <!-- 修复权限列表显示问题：过滤空值和空格 -->
          <el-tag 
            v-for="(perm, index) in (row.permissions || '').split(',').filter(p => p.trim())" 
            :key="index" 
            effect="light" 
            size="small"
            class="permission-tag"
          >
            {{ perm.trim() }}
          </el-tag>
          <!-- 无权限时显示提示 -->
          <div v-if="!(row.permissions || '').split(',').filter(p => p.trim()).length" class="no-permission">
            无权限
          </div>
        </template>
      </el-table-column>
      <el-table-column label="角色描述" prop="roleDesc" align="center" show-overflow-tooltip />
      <el-table-column label="创建时间" prop="createDate" align="center" width="180" />
      <el-table-column label="操作" width="220" align="center">
        <template #default="{ row }">
          <el-button 
            :icon="Edit" 
            circle 
            plain 
            type="primary" 
            @click="updateRoleEcho(row)"
            size="small" 
            title="编辑"
          ></el-button>
          <el-button 
            :icon="Delete" 
            circle 
            plain 
            type="danger" 
            @click="deleteRole(row)"
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
        <el-empty description="暂无角色信息" />
      </template>
    </el-table>
     <!-- 分页条 -->
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[1, 3, 5]"
            layout="jumper, total, sizes, prev, pager, next" background :total="total" @size-change="onSizeChange"
            @current-change="onCurrentChange" style="margin-top: 20px; justify-content: flex-end" />

    <!-- 添加/编辑角色弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="title" 
      width="500px"
      destroy-on-close
    >
      <el-form 
        :model="roleModel" 
        :rules="rules" 
        label-width="100px"
        ref="roleFormRef"
        class="role-form"
      >
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleModel.roleName" placeholder="请输入角色名称（如管理员）" />
        </el-form-item>
        
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="roleModel.roleCode" placeholder="请输入角色编码（如 admin）" />
        </el-form-item>
        
        <el-form-item label="角色权限">
          <el-select 
            v-model="selectedPermissions" 
            placeholder="请选择角色拥有的权限" 
            multiple
            collapse-tags
            @change="handlePermissionChange"
          >
            <el-option 
              v-for="perm in permissionOptions" 
              :key="perm.value" 
              :label="perm.label" 
              :value="perm.value"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="角色描述">
          <el-input 
            v-model="roleModel.roleDesc" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入角色描述信息"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span>
          <el-button @click="dialogVisible = false; resetRoleModel()">取消</el-button>
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

    <!-- 角色详情弹窗 -->
    <el-dialog 
      v-model="detailVisible" 
      title="角色详情" 
      width="50%" 
      destroy-on-close
      :close-on-click-modal="false"
      class="detail-dialog"
    >
      <!-- 详情头部：角色ID+装饰线 -->
      <div class="detail-header">
        <div class="detail-header__title">
          <Key class="icon-key" />
          <span>角色ID：{{ detailModel.roleId }}</span>
        </div>
        <div class="detail-header__line"></div>
      </div>

      <!-- 详情内容：卡片式布局 -->
      <div class="detail-content">
        <!-- 第一行：基本信息卡片 -->
        <div class="detail-card">
          <div class="detail-card__title">
            <User class="icon-title" />
            <span>基本信息</span>
          </div>
          <div class="detail-card__content">
            <div class="detail-item">
              <span class="detail-label">角色名称：</span>
              <span class="detail-value">{{ detailModel.roleName }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">角色编码：</span>
              <span class="detail-value">{{ detailModel.roleCode }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">创建时间：</span>
              <span class="detail-value">{{ detailModel.createDate || '未知' }}</span>
            </div>
          </div>
        </div>

        <!-- 第二行：权限信息卡片 -->
        <div class="detail-card">
          <div class="detail-card__title">
            <Lock class="icon-title" />
            <span>权限信息</span>
          </div>
          <div class="detail-card__content">
            <div class="detail-item">
              <span class="detail-label">拥有权限：</span>
              <div class="detail-value permissions-list">
                <!-- 修复详情弹窗权限显示问题 -->
                <el-tag 
                  v-for="(perm, index) in (detailModel.permissions || '').split(',').filter(p => p.trim())" 
                  :key="index" 
                  effect="light" 
                  size="small"
                  class="permission-tag"
                >
                  {{ perm.trim() }}
                </el-tag>
                <div v-if="!(detailModel.permissions || '').split(',').filter(p => p.trim()).length" class="no-permission">
                  该角色暂无任何权限
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 第三行：描述信息卡片 -->
        <div class="detail-card">
          <div class="detail-card__title">
            <InfoFilled class="icon-title" />
            <span>角色描述</span>
          </div>
          <div class="detail-card__content detail-description">
            {{ detailModel.roleDesc || '无描述信息' }}
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

  .permission-tag {
    margin: 0 4px 4px 0;
  }

  // 新增无权限提示样式
  .no-permission {
    color: #999;
    font-size: 12px;
    font-style: italic;
    padding: 4px 0;
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
      
      .icon-key {
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
      
      .permissions-list {
        display: flex;
        flex-wrap: wrap;
      }
    }
  }

  .detail-description {
    line-height: 24px;
    padding: 12px;
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
