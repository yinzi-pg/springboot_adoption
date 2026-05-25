<script setup>
import {
  Edit,
  Delete,
  Search,
  RefreshLeft,
  Download
} from '@element-plus/icons-vue'
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPetList } from '@/api/petInformation'
import * as XLSX from 'xlsx'

// 导入领养记录相关接口
import { 
  getAdoptionList, 
  adoptionApplyService, 
  adoptionDeleteService, 
  adoptionUpdateService, 
  searchAdoptionService 
} from '@/api/adoption'

// 宠物列表数据存储
const pets = ref([])
const petsMap = ref(new Map()) // 宠物ID到宠物对象的映射表
const adoptionFormRef = ref(null)

// 分页相关
const pageNum = ref(1)
const pageSize = ref(3)
const total = ref(0)

// 领养记录列表数据
const adoptions = ref([]);
const dialogVisible = ref(false);
const title = ref('')
const loading = ref(false)

// 宠物下拉框相关：绑定宠物ID而非对象
const selectedPetId = ref(''); 

// 领养记录数据模型
const adoptionModel = ref({
    adoptionId: null,
    applicantName: '',
    applicantPhone: '',
    petId: '',
    petName: '',
    status: '',
    remark: '',
    applyDate: '',
    reviewDate: ''
});

// 日期格式化
const formatDate = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// Excel导出功能
const exportToExcel = () => {
  const exportData = []
  
  if (adoptionModel.value.applicantName || adoptionModel.value.petId) {
    exportData.push({
      '申请ID': adoptionModel.value.adoptionId || '未提交（当前表单）',
      '申请人姓名': adoptionModel.value.applicantName || '未填写',
      '联系电话': adoptionModel.value.applicantPhone || '未填写',
      '宠物ID': adoptionModel.value.petId || '未填写',
      '宠物名称': adoptionModel.value.petName || '未匹配',
      '领养原因': adoptionModel.value.remark || '未填写',
      '备注信息': adoptionModel.value.remark || '无',
      '申请日期': adoptionModel.value.applyDate || formatDate(new Date()),
      '审核状态': adoptionModel.value.status || '待提交',
      '审核时间': adoptionModel.value.reviewDate || '未审核'
    })
  }
  
  if (adoptions.value.length > 0) {
    adoptions.value.forEach(record => {
      const petName = getPetName(record.petId)
      exportData.push({
        '申请ID': record.adoptionId || '无',
        '申请人姓名': record.applicantName || '未知',
        '联系电话': record.applicantPhone || '未知',
        '宠物ID': record.petId || '无',
        '宠物名称': petName || '未知宠物',
        '领养原因': record.remark || '无',
        '备注信息': record.remark || '无',
        '申请日期': record.applyDate || '未知',
        '审核状态': record.status || '未知',
        '审核时间': record.reviewDate || '未审核'
      })
    })
  }
  
  if (exportData.length === 0) {
    return ElMessage.warning('暂无表单数据可导出，请先填写表单或提交申请')
  }

  const workbook = XLSX.utils.book_new()
  const worksheet = XLSX.utils.json_to_sheet(exportData, {
    header: ['申请ID', '申请人姓名', '联系电话', '宠物ID', '宠物名称', '领养原因', '备注信息', '申请日期', '审核状态', '审核时间']
  })
  XLSX.utils.book_append_sheet(workbook, worksheet, '领养申请记录')
  
  const fileName = `宠物领养申请记录_${formatDate(new Date())}.xlsx`
  XLSX.writeFile(workbook, fileName)
  ElMessage.success(`Excel文件已导出：${fileName}`)
}

// 加载宠物数据方法
const loadPetData = async () => {
  try {
    const res = await getPetList({ pageNum: 1, pageSize: 1000 })
    if (res.code === 0) {
      pets.value = res.data.items;
      // 构建宠物ID到宠物对象的映射表，使用字符串ID作为键
      const map = new Map()
      res.data.items.forEach(pet => {
        map.set(String(pet.petId), pet)
      })
      petsMap.value = map
      ElMessage.success('宠物数据加载成功')
    } else {
      ElMessage.error('加载宠物数据失败: ' + (res.msg || '未知错误'))
    }
  } catch (error) {
    console.error('宠物数据加载异常:', error)
    ElMessage.error('宠物数据接口异常，请检查网络')
  }
}

// 页面加载时初始化
onMounted(() => {
  loadPetData()
  getAllAdoptions();
});

// 根据petId获取宠物名称
const getPetName = (petId) => {
  if (!petId) return '未知宠物'
  const pet = petsMap.value.get(String(petId))
  return pet ? pet.petName : '未知宠物'
}

// 选择宠物后更新表单
const handlePetChange = (petId) => {
  selectedPetId.value = petId;
  const pet = petsMap.value.get(String(petId))
  if (pet) {
    adoptionModel.value.petId = petId;
    adoptionModel.value.petName = pet.petName;
  } else {
    adoptionModel.value.petId = '';
    adoptionModel.value.petName = '';
  }
};

// 重置领养表单 - 确保所有字段都被重置
const resetAdoptionForm = () => {
  // 重置表单验证状态
  if (adoptionFormRef.value) {
    adoptionFormRef.value.resetFields()
  }
  
  // 重置数据模型
  adoptionModel.value = {
      adoptionId: null,
      applicantName: '',
      applicantPhone: '',
      petId: '',
      petName: '',
      status: '待审核', // 设置默认状态
      remark: '',
      applyDate: '',
      reviewDate: ''
  };
  
  // 重置下拉框选择
  selectedPetId.value = '';
};

// 分页事件
const onSizeChange = (size) => {
  pageSize.value = size;
  getAllAdoptions();
}

const onCurrentChange = (num) => {
  pageNum.value = num;
  getAllAdoptions();
}

// 表单验证规则
const rules = ref({
  applicantName: [
    { required: true, message: '请输入申请人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在2到20个字符', trigger: 'blur' }
  ],
  applicantPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  petId: [
    { required: true, message: '请选择宠物', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
});

// 获取所有领养记录
const getAllAdoptions = async () => {
  loading.value = true
  try {
    const result = await getAdoptionList({
      pageNum: pageNum.value,
      pageSize: pageSize.value
    });
    if (result.code === 0) {
      adoptions.value = result.data.items;
      total.value = result.data.total
    } else {
      ElMessage.error('获取领养记录失败');
    }
  } catch (error) {
    ElMessage.error('获取领养记录接口异常');
    console.error(error);
  } finally {
    loading.value = false
  }
};

// 搜索领养记录
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    getAllAdoptions();
    return;
  }
  try {
    loading.value = true
    const result = await searchAdoptionService(searchKeyword.value.trim());
    if (result.code === 0) {
      adoptions.value = result.data;
      if (result.data.length === 0) {
        ElMessage.info('未找到匹配的领养记录');
      }
    } else {
      ElMessage.error('搜索失败，请重试');
    }
  } catch (error) {
    ElMessage.error('搜索接口异常');
    console.error(error);
  } finally {
    loading.value = false
  }
};

// 重置搜索
const resetSearch = () => {
  searchKeyword.value = '';
  getAllAdoptions();
  ElMessage.success('已重置为全部领养记录');
};

// 添加领养记录
const addAdoption = async () => {
  if (!adoptionFormRef.value) return
  try {
    await adoptionFormRef.value.validate()
  } catch (error) {
    return ElMessage.warning('请完善表单信息后再提交')
  }

  try {
    loading.value = true
    const result = await adoptionApplyService(adoptionModel.value);
    if (result.code === 0) {
      dialogVisible.value = false;
      resetAdoptionForm();
      getAllAdoptions();
      ElMessage.success('添加成功');
    } else {
      ElMessage.error('添加失败: ' + (result.msg || '未知错误'));
    }
  } catch (error) {
    ElMessage.error('添加接口异常');
    console.error(error);
  } finally {
    loading.value = false
  }
};

// 打开添加领养记录弹窗 - 专门的方法确保重置
const openAddDialog = () => {
  // 先关闭弹窗再打开，确保状态刷新
  dialogVisible.value = false;
  
  // 使用定时器确保DOM更新后再执行重置
  setTimeout(() => {
    resetAdoptionForm();
    title.value = '添加领养记录';
    dialogVisible.value = true;
  }, 100);
};

// 删除领养记录
const deleteAdoption = (row) => {
  ElMessageBox.confirm('确认要删除该领养记录？', '温馨提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      loading.value = true
      const result = await adoptionDeleteService(row.adoptionId);
      if (result.code === 0) {
        ElMessage.success('删除成功');
        getAllAdoptions();
      } else {
        ElMessage.error('删除失败');
      }
    } catch (error) {
      ElMessage.error('删除接口异常');
      console.error(error);
    } finally {
      loading.value = false
    }
  }).catch(() => {
    ElMessage.info('取消删除');
  });
};

// 修改回显
const updateAdoptionEcho = (row) => {
  // 先关闭弹窗再打开
  dialogVisible.value = false;
  
  setTimeout(() => {
    // 复制数据
    adoptionModel.value = { ...row };
    
    // 设置选中的宠物ID
    selectedPetId.value = String(row.petId || '');
    
    // 从映射表中获取宠物名称
    const pet = petsMap.value.get(String(row.petId))
    if (pet) {
      adoptionModel.value.petName = pet.petName;
    } else {
      adoptionModel.value.petName = row.petName || `未知宠物 (ID: ${row.petId})`;
    }
    
    title.value = '修改领养记录';
    dialogVisible.value = true;
  }, 100);
};

// 修改领养记录
const updateAdoption = async () => {
  if (!adoptionFormRef.value) return
  try {
    await adoptionFormRef.value.validate()
  } catch (error) {
    return ElMessage.warning('请完善表单信息后再提交')
  }

  try {
    loading.value = true
    const result = await adoptionUpdateService(adoptionModel.value);
    if (result.code === 0) {
      dialogVisible.value = false;
      resetAdoptionForm();
      getAllAdoptions();
      ElMessage.success('修改成功');
    } else {
      ElMessage.error('修改失败: ' + (result.msg || '未知错误'));
    }
  } catch (error) {
    ElMessage.error('修改接口异常');
    console.error(error);
  } finally {
    loading.value = false
  }
};

// 状态样式映射
const getStatusType = (status) => {
  switch (status) {
    case '待审核': return 'warning';
    case '已通过': return 'success';
    case '已拒绝': return 'danger';
    default: return 'info';
  }
};

// 搜索相关变量
const searchKeyword = ref('')

// 监听宠物ID变化，自动更新宠物名称
watch(selectedPetId, (newPetId) => {
  const pet = petsMap.value.get(newPetId)
  if (pet) {
    adoptionModel.value.petName = pet.petName;
  } else {
    adoptionModel.value.petName = '';
  }
})
</script>

<template>
    <el-card class="page-container">
        <template #header>
            <div class="header">
                <span class="page-title">领养记录管理</span>
                <div class="operation-group">
                    <div class="search-group">
                        <el-input 
                            v-model="searchKeyword" 
                            placeholder="搜索申请人/宠物ID..." 
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
                        <!-- 导出Excel按钮 -->
                        <el-button 
                            type="success" 
                            :icon="Download"
                            @click="exportToExcel"
                            class="export-btn"
                            :disabled="loading.value" 
                        >
                            导出Excel
                        </el-button>
                    </div>
                    <el-button 
                        class="pink-add-btn" 
                        type="primary" 
                        @click="openAddDialog"
                    >
                        添加领养记录
                    </el-button>
                </div>
            </div>
        </template>

        <!-- 表格展示领养记录数据 -->
        <el-table 
            :data="adoptions" 
            style="width: 100%" 
            v-loading="loading"
            element-loading-text="加载中..."
        >
            <el-table-column label="序号" width="100" type="index" />
            <el-table-column label="申请ID" prop="adoptionId" />
            <el-table-column label="申请人" prop="applicantName" />
            <el-table-column label="联系电话" prop="applicantPhone" />
            <el-table-column label="宠物ID" prop="petId" />
            <el-table-column label="宠物名称">
                <template #default="{ row }">
                    {{ getPetName(row.petId) }}
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
            <el-table-column label="审核时间" prop="reviewDate">
                <template #default="{ row }">
                    {{ row.reviewDate || '未审核' }}
                </template>
            </el-table-column>
            <el-table-column label="备注" prop="remark" />
            <el-table-column label="操作" width="120">
                <template #default="{ row }">
                    <el-button 
                        :icon="Edit" 
                        circle 
                        plain 
                        type="primary" 
                        @click="updateAdoptionEcho(row)"
                        title="编辑"
                    ></el-button>
                    <el-button 
                        :icon="Delete" 
                        circle 
                        plain 
                        type="danger" 
                        @click="deleteAdoption(row)"
                        title="删除"
                    ></el-button>
                </template>
            </el-table-column>
            <template #empty>
                <el-empty description="暂无领养记录" />
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

        <!-- 添加/修改领养记录弹窗 -->
        <el-dialog v-model="dialogVisible" :title="title" width="30%">
            <el-form 
                :model="adoptionModel" 
                :rules="rules" 
                label-width="100px"
                ref="adoptionFormRef"
            >
                <el-form-item label="申请人姓名" prop="applicantName">
                    <el-input v-model="adoptionModel.applicantName" />
                </el-form-item>
                <el-form-item label="联系电话" prop="applicantPhone">
                    <el-input v-model="adoptionModel.applicantPhone" />
                </el-form-item>
                <!-- 宠物下拉框：绑定ID而非对象 -->
                <el-form-item label="宠物名称" prop="petId">
                    <el-select
                        v-model="selectedPetId"
                        placeholder="请选择宠物"
                        clearable
                        @change="handlePetChange"
                        style="width: 100%"
                    >
                        <el-option
                            v-for="pet in pets"
                            :key="pet.petId"
                            :label="`${pet.petName}（品种：${pet.petType || '未知'}）`"
                            :value="String(pet.petId)" 
                        ></el-option>
                    </el-select>
                </el-form-item>
                <!-- 已选宠物信息显示 -->
                <el-form-item label="已选宠物">
                    <el-input 
                        v-model="adoptionModel.petName" 
                        disabled 
                        placeholder="选择宠物后自动显示"
                    />
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-select v-model="adoptionModel.status" placeholder="请选择状态">
                        <el-option label="待审核" value="待审核" />
                        <el-option label="已通过" value="已通过" />
                        <el-option label="已拒绝" value="已拒绝" />
                    </el-select>
                </el-form-item>
                <el-form-item label="备注信息" prop="remark">
                    <el-input v-model="adoptionModel.remark" type="textarea" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span>
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button 
                        class="pink-add-btn" 
                        type="primary" 
                        @click="title === '添加领养记录' ? addAdoption() : updateAdoption()"
                    >
                        确认
                    </el-button>
                </span>
            </template>
        </el-dialog>
    </el-card>
</template>

<style lang="scss" scoped>
// 导出按钮样式
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
}

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

            border-radius: 8px !important;
            padding: 12px 24px !important;
        }
    }

    .status-tag {
        font-weight: 500;
        border-radius: 12px;
        padding: 0 10px;
    }

    .el-table {
        margin-top: 20px;
        
        :deep(.cell) {
            padding: 12px 10px;
        }
    }
}
</style>
