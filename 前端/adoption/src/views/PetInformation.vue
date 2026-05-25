<script setup>
import {
    Edit,
    Delete,
    Search,
    RefreshLeft,
    Plus
} from '@element-plus/icons-vue'
import { ref } from 'vue'

// 导入宠物信息管理相关方法
import { getPetList, petAddService, petDeleteService ,petUpdateService, searchPetService } from '@/api/petInformation'
import { ElMessage, ElMessageBox } from 'element-plus'

// 图片上传前校验（与注册页完全一致）
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

// 上传成功回调（后端返回格式与注册页一致）
const handleImageUploadSuccess = (result) => {
  // 后端返回格式：{ code: 0, data: "http://localhost:8080/..." }
  if (result.code === 0) {
    petModel.value.petImage = result.data; // 绑定图片URL到宠物模型
    ElMessage.success('图片上传成功');
  } else {
    ElMessage.error('图片上传失败：' + result.msg);
  }
};

//分页条数据模型
const pageNum = ref(1);//当前页码
const pageSize = ref(5);//每页条数
const total = ref(0);//总条数
//每页条数改变
const onSizeChange = (size) => {
    pageSize.value = size;
    getAllPets();
}
//当前页码改变
const onCurrentChange = (num) => {
    pageNum.value = num;
    getAllPets();
}

// 宠物信息列表数据
const pets = ref([]);
const dialogVisible = ref(false);
const title = ref('')
const petModel = ref({
    petName: '',
    petType: '',
    petGender: '',
    petAge: '',
    petStatus: '',
    petDesc: '',
    petImage: '',
    petId: null
});
const rules = ref({
    petName: [
        { required: true, message: '请输入宠物名字', trigger: 'blur' },
        { minlength: 1, maxlength: 20, message: '长度1-20位', trigger: 'blur' }
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
    petStatus: [
        { required: true, message: '请选择宠物状态', trigger: 'change' }
    ]
});

// 搜索相关变量
const searchKeyword = ref('')

// 获取所有宠物信息
const getAllPets = async () => {
    let params = {
        pageNum: pageNum.value,
        pageSize: pageSize.value
    }
    let result = await getPetList(params);
    if (result.code === 0) {
        pets.value = result.data.items;
        total.value = result.data.total;
    } else {
        ElMessage.error('获取宠物信息失败');
    }
};
getAllPets(); // 初始化加载

// 模糊查询宠物
const handleSearch = async () => {
    if (!searchKeyword.value.trim()) {
        getAllPets();
        return;
    }
    try {
        const result = await searchPetService(searchKeyword.value.trim());
        if (result.code === 0) {
            pets.value = result.data;
            if (result.data.length === 0) {
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

// 重置搜索（返回初始页面）
const resetSearch = () => {
    searchKeyword.value = '';
    getAllPets();
    ElMessage.success('已重置为全部宠物');
};

// 添加宠物
const addPet = async () => {
    let result = await petAddService(petModel.value);
    if( result.code === 0){
        dialogVisible.value = false;
        petModel.value = {
            petName: '',
            petType: '',
            petGender: '',
            petAge: '',
            petStatus: '',
            petDesc: '',
            petId: null
        };
        getAllPets();
        ElMessage.success('添加成功');
    }else{
        ElMessage.error('添加失败');
    }
};

// 删除宠物
const deletePet = (row) => {
    ElMessageBox.confirm('确认要删除该宠物？', '温馨提示', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        let result = await petDeleteService(row.petId);
        if (result.code === 0) {
            ElMessage.success('删除成功');
            getAllPets();
        } else {
            ElMessage.error('删除失败');
        }
    }).catch(() => {
         ElMessage.info('取消删除');
    });
};

// 修改回显
const updatePetInformationEcho = (row) =>{
    title.value = '修改宠物信息';
    dialogVisible.value = true;
    petModel.value = { ...row };
};

// 修改宠物信息
const updatePet = async () =>{
    let result = await petUpdateService(petModel.value);
    if( result.code === 0){
        dialogVisible.value = false;
        petModel.value = {
            petId: '',
            petName: '',
            petType: '',
            petAge: '',
            petGender: '',
            petStatus: '',
            petDesc: '',
            petImage: ''
        };
        getAllPets();
        ElMessage.success('修改成功');
    }else {
        ElMessage.error('修改失败');
    }
};

// 宠物状态样式映射
const getStatusType = (status) => {
  switch(status) {
    case '可领养': return 'success';
    case '已领养': return 'danger';
    case '寻主中': return 'warning';
    default: return 'info';
  }
};

// 图片路径处理
const getImageUrl = (path) => {
  if (path?.startsWith('/assets/')) {
    try {
      return require('@/assets' + path);
    } catch (e) {
      console.error('无法加载图片:', path);
      return '/assets/default-pet.png';
    }
  }
  return path || '/assets/default-pet.png';
};
</script>

<template>
    <el-card class="page-container">
        <template #header>
            <div class="header">
                <!-- 标题文字与"流浪宠物上报管理"样式统一 -->
                <span class="page-title">宠物信息管理</span>
                <div class="operation-group">
                    <div class="search-group">
                        <el-input 
                            v-model="searchKeyword" 
                            placeholder="搜索宠物名称/种类..." 
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
                        @click="dialogVisible = true; title='添加宠物信息'"
                    >
                        添加宠物信息
                    </el-button>
                </div>
            </div>
        </template>

        <!-- 表格展示宠物数据 -->
        <el-table :data="pets" style="width: 100%">
            <el-table-column label="序号" width="100" type="index" />
            <el-table-column label="宠物ID" prop="petId" />
            <el-table-column label="宠物名字" prop="petName" />
            <el-table-column label="宠物图片" width="120">
                <template #default="{ row }">
                    <img 
                        v-if="row.petImage" 
                        :src="getImageUrl(row.petImage)" 
                        alt="宠物照片" 
                        class="pet-img"
                    >
                    <span v-else class="no-img">无图片</span>
                </template>
            </el-table-column>
            <el-table-column label="宠物种类" prop="petType" />
            <el-table-column label="宠物性别" prop="petGender" />
            <el-table-column label="宠物年龄" prop="petAge" />
            <el-table-column label="宠物状态" width="120">
                <template #default="{ row }">
                    <el-tag 
                        :type="getStatusType(row.petStatus)" 
                        effect="light"
                        class="status-tag"
                    >
                        {{ row.petStatus }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="宠物介绍" prop="petDesc" />
            
            <el-table-column label="创建时间" prop="createDate" />
            <el-table-column label="操作" width="120">
                <template #default="{ row }">
                    <el-button :icon="Edit" circle plain type="primary" @click="updatePetInformationEcho(row)"></el-button>
                    <el-button :icon="Delete" circle plain type="danger" @click="deletePet(row)"></el-button>
                </template>
            </el-table-column>
            <template #empty>
                <el-empty description="暂无宠物信息" />
            </template>
        </el-table>
        <!-- 分页条 -->
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[1, 3, 5]"
            layout="jumper, total, sizes, prev, pager, next" background :total="total" @size-change="onSizeChange"
            @current-change="onCurrentChange" style="margin-top: 20px; justify-content: flex-end" />

        <!-- 添加宠物弹窗 -->
        <el-dialog v-model="dialogVisible" :title="title" width="30%">
            <el-form :model="petModel" :rules="rules" label-width="100px">
                <el-form-item label="宠物名字" prop="petName">
                    <el-input v-model="petModel.petName" />
                </el-form-item>
                <el-form-item label="宠物种类" prop="petType">
                    <el-select v-model="petModel.petType" placeholder="请选择宠物种类">
                        <el-option label="犬类" value="犬类" />
                        <el-option label="猫类" value="猫类" />
                        <el-option label="其他" value="其他" />
                    </el-select>
                </el-form-item>
                <el-form-item label="宠物性别" prop="petGender">
                    <el-select v-model="petModel.petGender" placeholder="请选择宠物性别">
                        <el-option label="公" value="公" />
                        <el-option label="母" value="母" />
                    </el-select>
                </el-form-item>
                <el-form-item label="宠物年龄" prop="petAge">
                    <el-input v-model="petModel.petAge" type="number" />
                </el-form-item>
                <el-form-item label="宠物状态" prop="petStatus">
                    <el-select v-model="petModel.petStatus" placeholder="请选择宠物状态">
                        <el-option label="可领养" value="可领养" />
                        <el-option label="已领养" value="已领养" />
                        <el-option label="寻主中" value="寻主中" />
                    </el-select>
                </el-form-item>
                <el-form-item label="宠物介绍" prop="petDesc">
                    <el-input v-model="petModel.petDesc" type="textarea" />
                </el-form-item>
                <el-form-item label="宠物图片" prop="petImage">
                <el-upload 
                    class="avatar-uploader"
                    :auto-upload="true"
                    :show-file-list="false"
                    action="http://localhost:8080/springboot_adoption/upload" 
                    :on-success="handleImageUploadSuccess"
                    :before-upload="beforeUpload"
                    name="file"
                >
                    <img v-if="petModel.petImage" :src="getImageUrl(petModel.petImage)" class="avatar" />
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
                    <el-button class="pink-add-btn" type="primary" @click="title === '添加宠物信息'? addPet():updatePet()">确认</el-button>
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

        // 统一标题样式（与流浪宠物上报管理一致）
        .page-title {
            font-size: 18px;
            font-weight: 600;
            color: #333; /* 与流浪宠物上报管理标题文字颜色统一 */
            line-height: 1; /* 确保行高一致 */
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

    .pet-img {
        max-width: 60px;
        max-height: 60px;
        border-radius: 4px;
    }

    .no-img {
        color: #999;
        font-size: 12px;
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

/* 宠物图片上传样式（复用现有粉色主题） */
.avatar-uploader {
  width: 120px;
  height: 120px;
  border-radius: 50%; /* 圆形上传框，与注册页一致 */
  border: 2px dashed #ffb6c1; /* 粉色虚线边框 */
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;

  &:hover {
    border-color: #ff758c; /*  hover时变色，与其他按钮一致 */
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
    color: #ffb6c1; /* 粉色图标，与主题统一 */
  }
}

}
</style>