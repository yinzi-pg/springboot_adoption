<script setup>
import {
    Edit,
    Delete,
    Search,
    RefreshLeft,
    Plus,
    User,
    Phone,
    Tickets
} from '@element-plus/icons-vue'
import { ref, reactive, unref } from 'vue'

// 导入志愿者信息管理相关方法
import { 
    getVolunteerList, 
    AddVolunteerService, 
    DeleteVolunteerService,
    UpdateVolunteerService, 
    searchVolunteerService
} from '@/api/volunteer'
// 导入用户信息查询和更新方法
import { getUserById, userUpdateByUserIdService } from '@/api/userInformation'
import { useUserInfoStore } from '@/stores/user.js'

import { ElMessage, ElMessageBox, ElForm } from 'element-plus'
import Volunteer from "@/views/Volunteer.vue";

// 状态管理
const UserStore = useUserInfoStore()
const uploadLoading = ref(false)

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
  uploadLoading.value = false;
  if (result.code === 0) {
    volunteerModel.volunteerImage = result.data; // 后端返回的图片路径
    console.log('图片上传后赋值：', volunteerModel.volunteerImage);
    ElMessage.success('图片上传成功');
  } else {
    ElMessage.error('图片上传失败：' + result.msg);
  }
};

// 分页条数据模型
const pageNum = ref(1);
const pageSize = ref(5);
const total = ref(0);

// 每页条数改变
const onSizeChange = (size) => {
    pageSize.value = size;
    getAllVolunteers();
}

// 当前页码改变
const onCurrentChange = (num) => {
    pageNum.value = num;
    getAllVolunteers();
}

// 志愿者信息列表数据
const volunteers = ref([]);
const dialogVisible = ref(false);
const title = ref('');
const volunteerFormRef = ref(null);

// 志愿者表单数据模型
const volunteerModel = reactive({
    volunteerId: null,
    userId: null,               // 关联用户ID
    volunteerType: '',
    serviceHours: 0.00,
    volunteerStatus: '',
    createTime: null,
    remark: '',
    volunteerImage: ''
});

// 用于选择用户时展示的用户信息
const userInfo = ref({
    userName: '',
    userPhone: '',
    userGender: '',
    userRole: '' // 新增：存储用户角色
});

// 表单校验规则
const rules = reactive({
    userId: [
        { required: true, message: '请输入关联用户ID', trigger: 'blur' },
        { type: 'number', message: '请输入有效的用户ID（数字）', trigger: 'blur' }
    ],
    volunteerType: [
        { required: true, message: '请选择服务类型', trigger: 'change' }
    ],
    serviceHours: [
        { required: true, message: '请输入服务时长', trigger: 'blur' },
        { type: 'number', min: 0, message: '服务时长不能为负数', trigger: 'blur' }
    ],
    volunteerStatus: [
        { required: true, message: '请选择志愿者状态', trigger: 'change' }
    ]
});

// 搜索相关变量
const searchKeyword = ref('');

// 获取所有志愿者信息
const getAllVolunteers = async () => {
    try {
        const params = {
            pageNum: pageNum.value,
            pageSize: pageSize.value
        };
        const result = await getVolunteerList(params);
        if (result.code === 0) {
            const volunteerList = result.data.items;
            for (const volunteer of volunteerList) {
                await fillUserInfo(volunteer);
                volunteer.serviceHours = Number(volunteer.serviceHours);
            }
            volunteers.value = volunteerList;
            total.value = result.data.total;
        } else {
            volunteers.value = [];
            ElMessage.warning('未获取到志愿者数据');
        }
    } catch (error) {
        ElMessage.error('获取志愿者列表失败');
        console.error(error);
    }
};

// 为志愿者补充用户信息（包含角色）
const fillUserInfo = async (volunteer) => {
    try {
        const userResult = await getUserById(volunteer.userId);
        if (userResult.code === 0) {
            volunteer.userInfo = {
                userName: userResult.data.userName,
                userPhone: userResult.data.userPhone,
                userGender: userResult.data.userGender,
                userRole: userResult.data.userRole || 'user' // 新增：获取用户角色
            };
        } else {
            volunteer.userInfo = {
                userName: '未知用户',
                userPhone: '未知',
                userGender: '未知',
                userRole: 'user'
            };
        }
    } catch (error) {
        volunteer.userInfo = {
            userName: '获取失败',
            userPhone: '获取失败',
            userGender: '获取失败',
            userRole: 'user'
        };
        console.error(`获取用户ID为${volunteer.userId}的信息失败`, error);
    }
};

// 初始化加载
getAllVolunteers();
// 模糊查询志愿者
// const handleSearch = async () => {
//     const keyword = searchKeyword.value.trim();
//     if (!keyword) {
//         getAllVolunteers();
//         return;
//     }
//     try {
//         const result = await searchVolunteerService(keyword,pageNum.value,pageSize.value);
//         volunteers.value = result.data.items;
//         total.value = result.data.total;
//         if (result.code === 0) {
//           const volunteerList = result.data.items || [];
//             for (const volunteer of volunteerList) {
//                 await fillUserInfo(volunteer);
//                 volunteer.serviceHours = Number(volunteer.serviceHours);
//             }
//             volunteers.value = volunteerList;
//             if (volunteers.value.length === 0) {
//                 ElMessage.info('未找到匹配的志愿者信息');
//             }
//         } else {
//             ElMessage.error('搜索失败，请重试');
//         }
//     } catch (error) {
//         ElMessage.error('搜索接口异常');
//         console.error(error);
//     }
// };
//模糊查询
const handleSearch = async () => {
  const keyword = searchKeyword.value.trim();
  if (!keyword) {
    getAllVolunteers();
    return;
  }
  try {
    const result = await searchVolunteerService(keyword, pageNum.value, pageSize.value);
    if (result.code === 0) {
      // 根据实际后端返回结构调整：可能是 result.data.items 或 result.data.records
      const volunteerList = result.data.items || result.data || [];
      if (!Array.isArray(volunteerList)) {
        console.error('搜索结果不是数组', volunteerList);
        volunteers.value = [];
        total.value = 0;
        return;
      }
      for (const volunteer of volunteerList) {
        await fillUserInfo(volunteer);
        volunteer.serviceHours = Number(volunteer.serviceHours);
      }
      volunteers.value = volunteerList;
      total.value = result.data.total || volunteerList.length;
      if (volunteers.value.length === 0) {
        ElMessage.info('未找到匹配的志愿者信息');
      }
    } else {
      ElMessage.error('搜索失败：' + result.message);
      volunteers.value = [];
      total.value = 0;
    }
  } catch (error) {
    ElMessage.error('搜索接口异常');
    console.error(error);
  }
};

// 重置搜索
const resetSearch = () => {
    searchKeyword.value = '';
    getAllVolunteers();
    ElMessage.success('已重置为全部志愿者');
};

// 验证用户ID并获取用户信息（包含角色）
const verifyUserId = async () => {
    if (volunteerModel.userId === null || volunteerModel.userId === undefined) return;
    try {
        const result = await getUserById(volunteerModel.userId);
        if (result.code === 0) {
            userInfo.value = {
                userName: result.data.userName,
                userPhone: result.data.userPhone,
                userGender: result.data.userGender,
                userRole: result.data.userRole || 'user' // 新增：显示用户当前角色
            };
            ElMessage.success(`已找到用户：${result.data.userName}，当前角色：${userInfo.value.userRole}`);
        } else {
            userInfo.value = {
                userName: '未找到用户',
                userPhone: '未知',
                userGender: '未知',
                userRole: 'user'
            };
            ElMessage.warning('未找到该用户信息');
        }
    } catch (error) {
        ElMessage.error('验证用户ID失败');
        console.error(error);
    }
};

// 添加志愿者
const addVolunteer = async () => {
    let addData = {};
    try {
        // 转换volunteerType为字符串
        const volunteerTypeStr = Array.isArray(volunteerModel.volunteerType) 
            ? volunteerModel.volunteerType.join(',') 
            : volunteerModel.volunteerType;

        addData = { 
            ...volunteerModel,
            volunteerType: volunteerTypeStr,
        };
        delete addData.createTime;
        
        const result = await AddVolunteerService(addData);
        if (result.code === 0) {
            dialogVisible.value = false;
            resetVolunteerForm();
            getAllVolunteers();
            ElMessage.success('添加成功');
        } else {
            ElMessage.error('添加失败：' + result.message);
        }
    } catch (error) {
        ElMessage.error('添加接口异常');
        console.error('添加参数：', addData);
        console.error(error);
    }
};

// 删除志愿者
const deleteVolunteer = (row) => {
    ElMessageBox.confirm(
        `确认要删除志愿者【${row.userInfo?.userName || '未知'}】吗？`, 
        '温馨提示', 
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(async () => {
        try {
            const result = await DeleteVolunteerService(row.volunteerId);
            if (result.code === 0) {
                ElMessage.success('删除成功');
                getAllVolunteers();
            } else {
                ElMessage.error('删除失败');
            }
        } catch (error) {
            ElMessage.error('删除接口异常');
            console.error(error);
        }
    }).catch(() => {
         ElMessage.info('取消删除');
    });
};

// 修改回显
const updateVolunteerEcho = async (row) => {
    title.value = '修改志愿者信息';
    const copyData = JSON.parse(JSON.stringify(row));
    copyData.userId = Number(copyData.userId);
    copyData.serviceHours = Number(copyData.serviceHours);
    Object.assign(volunteerModel, copyData);
    
    await verifyUserId();
    dialogVisible.value = true;
};

// 修改志愿者信息（包含角色更新逻辑）
const updateVolunteer = async () => {
    try {
        // 转换volunteerType为字符串
        const volunteerTypeStr = Array.isArray(volunteerModel.volunteerType) 
            ? volunteerModel.volunteerType.join(',') 
            : volunteerModel.volunteerType;

        const updateData = {
            volunteerId: volunteerModel.volunteerId,
            userId: volunteerModel.userId,
            volunteerType: volunteerTypeStr,
            serviceHours: volunteerModel.serviceHours,
            volunteerStatus: volunteerModel.volunteerStatus,
            remark: volunteerModel.remark || '',
            volunteerImage: volunteerModel.volunteerImage || ''
        };

        // 记录状态变更前的值用于判断
        const prevStatus = volunteers.value.find(v => v.volunteerId === volunteerModel.volunteerId)?.volunteerStatus;
        
        // 1. 更新志愿者状态
        const result = await UpdateVolunteerService(updateData);
        if (result.code === 0) {
            // 2. 如果是从非激活状态改为已激活，同步更新用户角色
            if (prevStatus !== '已激活' && volunteerModel.volunteerStatus === '已激活') {
                await userUpdateByUserIdService({
                    userId: volunteerModel.userId,
                    userRole: 'volunteer' // 将用户角色改为志愿者
                });
                
                // 如果当前登录用户是被修改的用户，实时更新其角色
                if (UserStore.info.userId === volunteerModel.userId) {
                    UserStore.info.userRole = 'volunteer';
                }
                
                ElMessage.success('志愿者已激活，用户角色已更新');
            } else {
                ElMessage.success('修改成功');
            }
            
            dialogVisible.value = false;
            resetVolunteerForm();
            getAllVolunteers();
        } else {
            ElMessage.error('修改失败：' + result.message);
        }
    } catch (error) {
        ElMessage.error('修改接口异常');
        console.error('修改参数：', updateData);
        console.error(error);
    }
};

// 重置志愿者表单
const resetVolunteerForm = () => {
    const formRef = unref(volunteerFormRef);
    if (formRef) {
        formRef.clearValidate();
    }
    Object.assign(volunteerModel, {
        volunteerId: null,
        userId: null,
        volunteerType: '',
        serviceHours: 0.00,
        volunteerStatus: '',
        createTime: null,
        remark: '',
        volunteerImage: ''
    });
    userInfo.value = {
        userName: '',
        userPhone: '',
        userGender: '',
        userRole: ''
    };
};

// 志愿者状态样式映射
const getStatusType = (status) => {
  switch(status) {
    case '已激活': return 'success';
    case '待认证': return 'warning';
    case '已冻结': return 'danger';
    case '已离职': return 'info';
    default: return 'info';
  }
};

// 图片路径处理
/*const getImageUrl = (path) => {
  if (!path) return '/assets/default-volunteer.png';
  if (path.startsWith('/assets/')) {
    try {
      return require('@/assets' + path);
    } catch (e) {
      console.error('无法加载图片:', path);
      return '/assets/default-volunteer.png';
    }
  }
  return path;
};*/

// 在组件中定义一个后端基础URL
const baseUrl = 'http://localhost:8080'; // 或从环境变量读取

const getImageUrl = (path) => {
  if (!path) return '/assets/default-volunteer.png';
  if (path.startsWith('http')) return path;
  // 拼接完整后端地址
  return `${baseUrl}/springboot_adoption${path}`;
};

</script>


<!--顶部-->
<template>
    <el-card class="page-container">
        <template #header>
            <div class="header">
                <span class="page-title">志愿者信息管理</span>
                <div class="operation-group">
                    <div class="search-group">
                        <el-input 
                            v-model="searchKeyword" 
                            placeholder="搜索服务类型/状态/备注..." 
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
                        @click="dialogVisible = true; title='添加志愿者信息'; resetVolunteerForm()"
                    >
                        <Plus class="icon" />
                        添加志愿者信息
                    </el-button>
                </div>
            </div>
        </template>

        <!-- 表格展示志愿者数据 -->
        <el-table :data="volunteers" style="width: 100%" border stripe>
            <el-table-column label="序号" width="100" type="index" />
            <el-table-column label="志愿者ID" prop="volunteerId" align="center" />
            <el-table-column label="关联用户ID" prop="userId" align="center" />
            <el-table-column label="用户信息" align="center" width="250">
                <template #default="{ row }">
                    <div class="user-info">
                        <div class="user-details">
                            <div class="user-name">{{ row.userInfo?.userName || '未知' }}</div>
                            <div class="user-phone">{{ row.userInfo?.userPhone || '未知' }}</div>
                            <div class="user-gender">
                                角色: <el-tag size="small">{{ row.userInfo?.userRole || 'user' }}</el-tag>
                            </div>
                        </div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="志愿者图片" width="120">
                <template #default="{ row }">
                    <img 
                        v-if="row.volunteerImage" 
                        :src="getImageUrl(row.volunteerImage)" 
                        alt="志愿者照片" 
                        class="volunteer-img"
                    >
                    <span v-else class="no-img">无图片</span>
                </template>
            </el-table-column>
            <el-table-column label="服务类型" prop="volunteerType" align="center">
                <template #default="{ row }">
                    <div class="type-tags">
                        <el-tag 
                            v-for="type in row.volunteerType.split(',')" 
                            :key="type" 
                            effect="light"
                            size="small"
                        >
                            {{ type }}
                        </el-tag>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="服务时长(小时)" prop="serviceHours" align="center" />
            <el-table-column label="状态" width="120" align="center">
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
            <el-table-column label="注册时间" prop="createTime" align="center" width="180" />
            <el-table-column label="备注" prop="remark" align="center" />
            <el-table-column label="操作" width="140" align="center">
                <template #default="{ row }">
                    <el-button 
                        :icon="Edit" 
                        circle 
                        plain 
                        type="primary" 
                        @click="updateVolunteerEcho(row)"
                        size="small"
                    ></el-button>
                    <el-button 
                        :icon="Delete" 
                        circle 
                        plain 
                        type="danger" 
                        @click="deleteVolunteer(row)"
                        size="small"
                    ></el-button>
                </template>
            </el-table-column>
            <template #empty>
                <el-empty description="暂无志愿者信息" />
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

        <!-- 添加/修改志愿者弹窗 -->
        <el-dialog v-model="dialogVisible" :title="title" width="40%" destroy-on-close>
            <el-form 
                :model="volunteerModel" 
                :rules="rules" 
                label-width="120px"
                ref="volunteerFormRef"
                class="volunteer-form"
                :validate-on-rule-change="false"
            >
                <el-form-item label="关联用户ID" prop="userId">
                    <el-input 
                        v-model.number="volunteerModel.userId" 
                        placeholder="请输入用户ID（数字）" 
                        @blur="verifyUserId"
                        type="number"
                    />
                    <el-descriptions v-if="userInfo.userName" column="1" border class="user-info-desc">
                        <el-descriptions-item label="用户信息">
                            <div class="desc-content">
                                <div class="desc-details">
                                    <div><User class="icon" /> {{ userInfo.userName }}</div>
                                    <div><Phone class="icon" /> {{ userInfo.userPhone }}</div>
                                    <div><Tickets class="icon" /> 性别: {{ userInfo.userGender }}</div>
                                    <div>
                                        <el-tag :type="userInfo.userRole === 'volunteer' ? 'success' : 'info'">
                                            当前角色: {{ userInfo.userRole }}
                                        </el-tag>
                                    </div>
                                </div>
                            </div>
                        </el-descriptions-item>
                    </el-descriptions>
                </el-form-item>
                
                <el-form-item label="服务类型" prop="volunteerType">
                    <el-select 
                        v-model="volunteerModel.volunteerType" 
                        placeholder="请选择服务类型"
                        multiple
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
                        placeholder="请输入服务时长（如10.5）"
                        min="0"
                    />
                </el-form-item>
                
                <el-form-item label="志愿者状态" prop="volunteerStatus">
                    <el-select v-model="volunteerModel.volunteerStatus" placeholder="请选择状态">
                        <el-option label="待认证" value="待认证" />
                        <el-option label="已激活" value="已激活" />
                        <el-option label="已冻结" value="已冻结" />
                        <el-option label="已离职" value="已离职" />
                    </el-select>
                </el-form-item>
                
                <el-form-item label="备注信息" prop="remark">
                    <el-input 
                        v-model="volunteerModel.remark" 
                        type="textarea" 
                        placeholder="请输入备注（如特殊技能等）"
                        rows="3"
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
                >
                    <img 
                        v-if="volunteerModel.volunteerImage" 
                        :src="getImageUrl(volunteerModel.volunteerImage)" 
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
                <span>
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button 
                        class="pink-add-btn" 
                        type="primary" 
                        @click="title === '添加志愿者信息' ? addVolunteer() : updateVolunteer()"
                    >
                        确认
                    </el-button>
                </span>
            </template>
        </el-dialog>
    </el-card>
</template>

<style lang="scss" scoped>
.page-container {
    min-height: 75vh;
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
            
            .icon {
                margin-right: 6px;
            }
        }
    }

    .user-info {
        display: flex;
        align-items: center;
        padding: 4px 0;
        justify-content: center;
        
        .user-details {
            text-align: left;
            
            .user-name {
                font-weight: 500;
                line-height: 1.2;
            }
            
            .user-phone, .user-gender {
                font-size: 12px;
                color: #666;
                line-height: 1.2;
            }
        }
    }

    .volunteer-img {
        max-width: 60px;
        max-height: 60px;
        border-radius: 4px;
        object-fit: cover;
    }

    .no-img {
        color: #999;
        font-size: 12px;
    }

    .type-tags {
        display: flex;
        flex-wrap: wrap;
        gap: 4px;
        justify-content: center;
    }

    .status-tag {
        padding: 2px 8px;
        border-radius: 4px;
    }

    .volunteer-form {
        margin-top: 10px;
    }

    .user-info-desc {
        margin-top: 10px;
        background-color: #fff5f7;
        border-color: #ffe6ea;
        
        .desc-content {
            display: flex;
            align-items: center;
            justify-content: center;
            
            .desc-details {
                display: flex;
                flex-direction: column;
                gap: 4px;
                width: 100%;
                
                .icon {
                    width: 14px;
                    height: 14px;
                    margin-right: 4px;
                    color: #ff6b81;
                }
            }
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
}
</style>
