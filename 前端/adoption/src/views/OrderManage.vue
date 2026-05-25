<script setup>
import {
    Edit,
    Delete,
    Search,
    RefreshLeft,
    ChatLineSquare,
    ShoppingCart,
    User,
    Phone,
    Money,
    Clock,
    Document,
    CollectionTag
} from '@element-plus/icons-vue'
import { ref, reactive, unref } from 'vue'
import { ElMessage, ElMessageBox, ElForm } from 'element-plus'

// 导入订单管理相关接口
import { 
    getOrderList, 
    orderAddService, 
    orderDeleteService, 
    orderUpdateService, 
    searchOrderService 
} from '@/api/orderManage'

//分页条数据模型
const pageNum = ref(1);//当前页码
const pageSize = ref(5);//每页条数
const total = ref(0);//总条数
//每页条数改变
const onSizeChange = (size) => {
    pageSize.value = size;
    getAllOrders();
}
//当前页码改变
const onCurrentChange = (num) => {
    pageNum.value = num;
    getAllOrders();
}

// 订单列表数据存储
const orders = ref([]);
// 弹窗控制相关状态
const dialogVisible = ref(false);
const dialogTitle = ref('');
const orderFormRef = ref(null);
const detailVisible = ref(false);
const detailModel = reactive({
    orderId: '',
    userId: '',
    userName: '',
    userPhone: '',
    totalAmount: 0.00,
    status: '',
    createTime: '',
    remark: ''
});

// 搜索关键词
const searchKeyword = ref('');

// 订单表单数据模型
const orderModel = reactive({
    orderId: null,
    userId: '',
    userName: '',
    userPhone: '',
    totalAmount: 0.00,
    status: '待支付',
    createTime: '',
    remark: ''
});

// 订单表单校验规则
const orderRules = reactive({
    userId: [
        { required: true, message: '请输入用户ID', trigger: 'blur' },
        { pattern: /^[0-9]*$/, message: '用户ID必须为数字', trigger: 'blur' }
    ],
    userName: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 1, max: 20, message: '用户名长度需在1-20位之间', trigger: 'blur' }
    ],
    userPhone: [
        { required: true, message: '请输入用户手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号', trigger: 'blur' }
    ],
    totalAmount: [
        { required: true, message: '请输入订单金额', trigger: 'blur' },
        
    ],
    status: [
        { required: true, message: '请选择订单状态', trigger: 'change' }
    ],
    createTime: [
        { required: true, message: '请选择创建时间', trigger: 'change' }
    ]
});

/**
 * 初始化加载所有订单数据
 */
const getAllOrders = async () => {
    try {
        let params = {
        pageNum: pageNum.value,
        pageSize: pageSize.value
    }
        const result = await getOrderList(params);
        if (result.code === 0 ) {
            orders.value = result.data.items;
            total.value = result.data.total;
        } else {
            orders.value = [];
            ElMessage.warning('未获取到订单数据');
        }
    } catch (error) {
        ElMessage.error('获取订单列表失败，请重试');
        console.error('订单列表接口异常:', error);
    }
};

// 页面初始化时自动加载订单列表
getAllOrders();

/**
 * 搜索订单
 */
const handleSearch = async () => {
    const keyword = searchKeyword.value.trim();
    if (!keyword) {
        getAllOrders();
        return;
    }

    try {
        const result = await searchOrderService(keyword );
        if (result.code === 0) {
            orders.value = result.data;
            if (orders.value.length === 0) {
                ElMessage.info(`未找到包含"${keyword}"的订单`);
            }
        } else {
            ElMessage.error('搜索订单失败，请重试');
        }
    } catch (error) {
        ElMessage.error('搜索接口异常');
        console.error('订单搜索异常:', error);
    }
};

/**
 * 重置搜索
 */
const resetSearch = () => {
    searchKeyword.value = '';
    getAllOrders();
    ElMessage.success('已重置为全部订单');
};

/**
 * 点击"添加订单"按钮
 */
const handleAddClick = () => {
    resetOrderForm();
    dialogTitle.value = '添加订单信息';
    dialogVisible.value = true;
};

/**
 * 编辑订单回显
 */
const handleUpdateEcho = (row) => {
    Object.assign(orderModel, JSON.parse(JSON.stringify(row)));
    dialogTitle.value = '编辑订单信息';
    dialogVisible.value = true;
};

/**
 * 删除订单
 */
const handleDelete = (row) => {
    ElMessageBox.confirm(
        `确认要删除订单【${row.orderId}】吗？删除后不可恢复！`,
        '删除确认',
        {
            confirmButtonText: '确认删除',
            cancelButtonText: '取消',
            type: 'danger',
            center: true
        }
    ).then(async () => {
        try {
            const result = await orderDeleteService(row.orderId);
            if (result.code === 0) {
                ElMessage.success('订单删除成功');
                getAllOrders();
            } else {
                ElMessage.error(`删除失败：${result.message || '未知错误'}`);
            }
        } catch (error) {
            ElMessage.error('删除接口异常，请重试');
            console.error('订单删除异常:', error);
        }
    }).catch(() => {
        ElMessage.info('已取消删除订单');
    });
};

/**
 * 查看订单详情
 */
const handleViewDetail = (row) => {
    Object.assign(detailModel, JSON.parse(JSON.stringify(row)));
    detailVisible.value = true;
};

/**
 * 表单提交
 */
const handleFormSubmit = async () => {
    const formRef = unref(orderFormRef);
    if (!formRef) return;

    try {
        await formRef.validate();
        
        if (dialogTitle.value === '添加订单信息') {
            delete orderModel.orderId;
            await handleAddOrder();
        } else {
            if (!orderModel.orderId) {
                ElMessage.error('编辑订单缺少订单ID，请刷新页面重试');
                return;
            }
            await handleUpdateOrder();
        }
    } catch (error) {
        console.error('表单校验失败:', error);
    }
};

/**
 * 处理添加订单逻辑
 */
const handleAddOrder = async () => {
    try {
        const result = await orderAddService(orderModel);
        if (result.code === 0) {
            ElMessage.success('订单添加成功');
            dialogVisible.value = false;
            resetOrderForm();
            getAllOrders();
        } else {
            ElMessage.error(`添加失败：${result.message || '未知错误'}`);
        }
    } catch (error) {
        ElMessage.error('添加订单接口异常，请重试');
        console.error('订单添加异常:', error);
    }
};

/**
 * 处理编辑订单逻辑
 */
const handleUpdateOrder = async () => {
    try {
        const result = await orderUpdateService(orderModel);
        if (result.code === 0) {
            ElMessage.success('订单修改成功');
            dialogVisible.value = false;
            resetOrderForm();
            getAllOrders();
        } else {
            ElMessage.error(`修改失败：${result.message || '未知错误'}`);
        }
    } catch (error) {
        ElMessage.error('修改订单接口异常，请重试');
        console.error('订单修改异常:', error);
    }
};

/**
 * 重置订单表单
 */
const resetOrderForm = () => {
    Object.assign(orderModel, {
        orderId: null,
        userId: '',
        userName: '',
        userPhone: '',
        totalAmount: 0.00,
        status: '待支付',
        createTime: '',
        remark: ''
    });

    const formRef = unref(orderFormRef);
     if (formRef && typeof formRef.clearValidate === 'function') {
        formRef.clearValidate(); // 确保方法存在再调用
    }
};

/**
 * 订单状态样式映射
 */
const getStatusType = (status) => {
    switch (status) {
        case '已支付':
        case '已完成':
            return 'success';
        case '已取消':
            return 'danger';
        case '待支付':
            return 'warning';
        default:
            return 'info';
    }
};
</script>

<template>
    <el-card class="page-container">
        <template #header>
            <div class="header">
                <span class="page-title">订单信息管理</span>
                <div class="operation-group">
                    <div class="search-group">
                        <el-input 
                            v-model="searchKeyword" 
                            placeholder="搜索订单号/用户名/手机号..." 
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
                        @click="handleAddClick"
                    >
                        添加订单信息
                    </el-button>
                </div>
            </div>
        </template>

        <!-- 表格展示订单数据 -->
        <el-table :data="orders" style="width: 100%" border stripe>
            <el-table-column label="序号" width="100" type="index" align="center" />
            <el-table-column label="订单ID" prop="orderId" align="center" />
            <el-table-column label="用户ID" prop="userId" align="center" />
            <el-table-column label="用户名" prop="userName" align="center" />
            <el-table-column label="用户手机号" prop="userPhone" align="center" />
            <el-table-column label="订单金额(元)" prop="totalAmount" align="center">
                <template #default="{ row }">
                    {{ row.totalAmount.toFixed(2) }}
                </template>
            </el-table-column>
            <el-table-column label="订单状态" width="120" align="center">
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
            <el-table-column label="创建时间" prop="createTime" align="center" width="180" />
            <el-table-column label="订单备注" prop="remark" align="center" show-overflow-tooltip />
            <el-table-column label="操作" width="180" align="center">
                <template #default="{ row }">
                    <el-button :icon="Edit" circle plain type="primary" @click="handleUpdateEcho(row)" size="small" title="编辑"></el-button>
                    <el-button :icon="Delete" circle plain type="danger" @click="handleDelete(row)" size="small" title="删除"></el-button>
                    <el-button :icon="ChatLineSquare" circle plain type="info" @click="handleViewDetail(row)" size="small" title="查看详情"></el-button>
                  </template>
            </el-table-column>
            <template #empty>
                <el-empty description="暂无订单信息" />
            </template>
        </el-table>
        <!-- 分页条 -->
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[1, 3, 5]"
            layout="jumper, total, sizes, prev, pager, next" background :total="total" @size-change="onSizeChange"
            @current-change="onCurrentChange" style="margin-top: 20px; justify-content: flex-end" />

        <!-- 添加/编辑订单弹窗 -->
        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="40%" destroy-on-close>
            <el-form :model="orderModel" :rules="orderRules" ref="orderFormRef" label-width="120px" class="order-form">
                <el-form-item label="用户ID" prop="userId">
                    <el-input v-model="orderModel.userId" placeholder="请输入用户ID" />
                </el-form-item>
                <el-form-item label="用户名" prop="userName">
                    <el-input v-model="orderModel.userName" placeholder="请输入用户名" />
                </el-form-item>
                <el-form-item label="用户手机号" prop="userPhone">
                    <el-input v-model="orderModel.userPhone" placeholder="请输入11位手机号" />
                </el-form-item>
                <el-form-item label="订单金额" prop="totalAmount">
                    <el-input v-model="orderModel.totalAmount" type="number" step="0.01" placeholder="请输入订单金额" />
                </el-form-item>
                <el-form-item label="订单状态" prop="status">
                    <el-select v-model="orderModel.status" placeholder="请选择订单状态">
                        <el-option label="待支付" value="待支付" />
                        <el-option label="已支付" value="已支付" />
                        <el-option label="已取消" value="已取消" />
                        <el-option label="已完成" value="已完成" />
                    </el-select>
                </el-form-item>
                <el-form-item label="创建时间" prop="createTime">
                    <el-date-picker 
                        v-model="orderModel.createTime" 
                        type="datetime" 
                        placeholder="选择创建时间" 
                        value-format="YYYY-MM-DD HH:mm:ss"
                    />
                </el-form-item>
                <el-form-item label="订单备注" prop="remark">
                    <el-input v-model="orderModel.remark" type="textarea" rows="3" placeholder="请输入订单备注（可选）" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span>
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button class="pink-add-btn" type="primary" @click="handleFormSubmit">确认</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- 美化后的订单详情弹窗 -->
        <el-dialog 
            v-model="detailVisible" 
            title="订单详情" 
            width="50%" 
            destroy-on-close
            :close-on-click-modal="false"
            class="detail-dialog"
        >
            <!-- 详情头部：订单编号+装饰线 -->
            <div class="detail-header">
                <div class="detail-header__title">
                    <ShoppingCart class="icon-cart" />
                    <span>订单编号：{{ detailModel.orderId }}</span>
                </div>
                <div class="detail-header__line"></div>
            </div>

            <!-- 详情内容：卡片式布局 -->
            <div class="detail-content">
                <!-- 第一行：用户信息卡片 -->
                <div class="detail-card">
                    <div class="detail-card__title">
                        <User class="icon-title" />
                        <span>用户信息</span>
                    </div>
                    <div class="detail-card__content">
                        <div class="detail-item">
                            <span class="detail-label">用户ID：</span>
                            <span class="detail-value">{{ detailModel.userId }}</span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">用户名：</span>
                            <span class="detail-value">{{ detailModel.userName }}</span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">联系电话：</span>
                            <span class="detail-value">
                                <Phone class="icon-small" />
                                {{ detailModel.userPhone }}
                            </span>
                        </div>
                    </div>
                </div>

                <!-- 第二行：订单信息卡片 -->
                <div class="detail-card">
                    <div class="detail-card__title">
                        <CollectionTag class="icon-title" />
                        <span>订单信息</span>
                    </div>
                    <div class="detail-card__content">
                        <div class="detail-item">
                            <span class="detail-label">订单金额：</span>
                            <span class="detail-value">
                                <Money class="icon-small" />
                                {{ detailModel.totalAmount.toFixed(2) }} 元
                            </span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">订单状态：</span>
                            <span class="detail-value">
                                <el-tag :type="getStatusType(detailModel.status)" effect="light" class="status-tag">
                                    {{ detailModel.status }}
                                </el-tag>
                            </span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">创建时间：</span>
                            <span class="detail-value">
                                <Clock class="icon-small" />
                                {{ detailModel.createTime }}
                            </span>
                        </div>
                    </div>
                </div>

                <!-- 第三行：备注信息卡片 -->
                <div class="detail-card">
                    <div class="detail-card__title">
                        <Document class="icon-title" />
                        <span>备注信息</span>
                    </div>
                    <div class="detail-card__content detail-remark">
                        {{ detailModel.remark || '无备注信息' }}
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

    .pet-img {
        max-width: 60px;
        max-height: 60px;
        border-radius: 4px;
    }

    .no-img {
        color: #999;
        font-size: 12px;
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
            
            .icon-cart {
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

    .detail-remark {
        line-height: 24px;
        padding: 8px 12px;
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

    .status-tag {
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