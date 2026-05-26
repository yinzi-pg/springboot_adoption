<script setup>
import { ref } from 'vue';
import { User, Lock, Plus } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useUserInfoStore } from '@/stores/user';
import { registerService } from '@/api/user';

const UserStore = useUserInfoStore();
const router = useRouter();

const loginService = () => {
    router.push('/login');
};
const BACKEND_BASE = 'http://localhost:8080/springboot_adoption';
const registerData = ref({
    userName: '',
    userPassword: '',
    userGender: 'male',
    userAge: '',
    userPhone: '',
    userEmail: '',
    userRole: '',
    avatar: ''
});

// 头像上传前的校验
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

const register = async () => {
    let result = await registerService(registerData.value);
  console.log(result)
    if (result.code === 0) {
        ElMessage.success("注册成功");
        router.push('/login');
    } else {
        ElMessage.error(result.message);
    }
};

const uploadSuccess = (result) => {
    registerData.value.avatar = result.data;
};
</script>

<template>
    <div class="register-page">
        <!-- 背景装饰层 -->
        <div class="background-decoration"></div>
        
        <!-- 主容器 -->
        <div class="container">
            <!-- 左侧品牌展示区 -->
            <div class="brand-section">
                <div class="brand-content">
                    <img src="@/assets/recommend-optimized.png" alt="宠物领养Logo" class="brand-logo" />
                    <h2 class="brand-slogan">宠物领养中心</h2>
                    <p class="brand-description">每一个生命都值得被温柔以待，加入我们，一起给它们一个家</p>
                    <div class="paw-prints">
                        <span class="paw">🐾</span>
                        <span class="paw">🐾</span>
                        <span class="paw">🐾</span>
                    </div>
                </div>
            </div>


            <!-- 右侧注册表单区 -->
            <div class="form-section">
                <el-card class="register-card" shadow="hover">
                    <el-form ref="form" size="large" autocomplete="off" :model="registerData" class="register-form">
                        <el-form-item class="title-item">
                            <h1 class="form-title">用户注册</h1>
                            
                            <!-- 头像上传区域 - 精确放在标题下划线正下方 -->
                            <div class="avatar-container">
                                <el-upload 
                                    class="avatar-uploader" 
                                    :auto-upload="true" 
                                    :show-file-list="false"
                                    action="http://localhost:8080/springboot_adoption/upload" 
                                    :on-success="uploadSuccess" 
                                    name="file"
                                    :before-upload="beforeUpload">
                                    <img v-if="registerData.avatar" :src="BACKEND_BASE+registerData.avatar" class="avatar" />
                                    <div v-else class="upload-placeholder">
                                        <el-icon class="upload-icon">
                                            <Plus />
                                        </el-icon>
                                    </div>
                                </el-upload>
                                <div class="avatar-label">上传头像</div>
                            </div>
                        </el-form-item>

                        <el-form-item label="账号：" prop="userName" :rules="[{ required: true, message: '请输入用户名', trigger: 'blur' }]">
                            <el-input placeholder="请输入用户名" :prefix-icon="User" v-model="registerData.userName"></el-input>
                        </el-form-item>

                        <el-form-item label="密码：" prop="userPassword" :rules="[{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 3, message: '密码长度不能少于3位', trigger: 'blur' }]">
                            <el-input placeholder="请输入密码" :prefix-icon="Lock" v-model="registerData.userPassword" type="password"></el-input>
                        </el-form-item>

                        <el-form-item label="性别：" prop="userGender">
                            <el-radio-group v-model="registerData.userGender">
                                <el-radio label="male">男</el-radio>
                                <el-radio label="female">女</el-radio>
                            </el-radio-group>
                        </el-form-item>

                        <el-form-item label="年龄：" prop="userAge" :rules="[{ required: true, message: '请输入年龄', trigger: 'blur' }, { type: 'number', min: 1, max: 120, message: '请输入有效的年龄', trigger: 'blur' }]">
                            <el-input placeholder="请输入年龄" v-model.number="registerData.userAge"></el-input>
                        </el-form-item>

                        <el-form-item label="电话：" prop="userPhone" :rules="[{ required: true, message: '请输入电话号码', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号码', trigger: 'blur' }]">
                            <el-input placeholder="请输入手机号码" v-model="registerData.userPhone"></el-input>
                        </el-form-item>

                        <el-form-item label="邮箱：" prop="userEmail" :rules="[{ required: true, message: '请输入邮箱', trigger: 'blur' }, { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }]">
                            <el-input placeholder="请输入邮箱地址" v-model="registerData.userEmail"></el-input>
                        </el-form-item>

                        <el-form-item label="角色：" prop="userRole" :rules="[{ required: true, message: '请选择角色', trigger: 'change' }]">
                            <el-select placeholder="请选择角色" v-model="registerData.userRole">
                                <el-option label="管理员" value="admin"></el-option>
                                <el-option label="普通用户" value="user"></el-option>
                            </el-select>
                        </el-form-item>

                        <el-form-item class="form-actions">
                            <el-button type="primary" @click="register" class="register-btn">注册</el-button>
                            <el-button type="default" @click="loginService" class="back-btn">返回登录</el-button>
                        </el-form-item>
                    </el-form>
                </el-card>
            </div>
        </div>
        
        <!-- 底部版权信息 -->
        <el-footer class="page-footer">
            宠物领养中心 ©2025 让每一个生命都被温柔以待
        </el-footer>
    </div>
</template>

<style lang="scss" scoped>
.register-page {
    min-height: 100vh;
    width: 100%;
    position: relative;
    overflow: hidden;
    display: flex;
    flex-direction: column;

    // 背景装饰 - 与layout保持一致的粉色调
    .background-decoration {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: url('@/assets/image.png') no-repeat center center / cover;
        filter: brightness(0.95) saturate(0.9);
        z-index: 0;

        &::after {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: linear-gradient(135deg, rgba(255, 248, 248, 0.9), rgba(255, 236, 241, 0.85));
        }
    }

    // 主容器
    .container {
        position: relative;
        z-index: 1;
        width: 100%;
        max-width: 1200px;
        flex: 1;
        margin: 0 auto;
        padding: 20px;
        display: flex;
        flex-wrap: wrap; // 允许换行
        align-items: center;
        justify-content: center;
    }

    // 品牌展示区
    .brand-section {
        flex: 1;
        min-width: 300px;
        padding: 40px;
        display: flex;
        align-items: center;
        justify-content: center;

        .brand-content {
            text-align: center;
            max-width: 400px;

            .brand-logo {
                width: 180px;
                height: 180px;
                object-fit: contain;
                margin-bottom: 30px;
                animation: float 6s ease-in-out infinite;
                filter: drop-shadow(0 5px 10px rgba(255, 117, 140, 0.3));
            }

            .brand-slogan {
                font-size: 28px;
                color: #ff758c; // 与layout一致的粉橙色
                margin-bottom: 15px;
                font-weight: 600;
                text-shadow: 0 2px 4px rgba(255, 182, 193, 0.3);
            }

            .brand-description {
                font-size: 16px;
                color: #663300; // 深棕色文字，与layout一致
                line-height: 1.6;
                margin-bottom: 20px;
            }

            .paw-prints {
                display: flex;
                justify-content: center;
                gap: 15px;
                font-size: 24px;
                opacity: 0.7;
                animation: wave 3s ease-in-out infinite;
            }
        }
    }

    // 表单区域
    .form-section {
        flex: 1;
        min-width: 300px;
        padding: 20px;
        display: flex;
        justify-content: center;
        align-items: center;

        .register-card {
            width: 100%;
            max-width: 450px;
            border-radius: 12px;
            overflow: hidden;
            transition: all 0.3s ease;
            border: 1px solid #ffd0d0; // 浅粉色边框
            padding: 30px 15px;

            &:hover {
                transform: translateY(-5px);
                box-shadow: 0 15px 30px rgba(255, 182, 193, 0.2); // 粉色阴影，与layout一致
            }
        }

        .register-form {
            padding: 0 30px;

            .title-item {
                // 确保标题和头像区域作为一个整体居中
                display: flex;
                flex-direction: column;
                align-items: center;
                margin-bottom: 25px;
                border: none;
                padding: 0;
            }

            .form-title {
                text-align: center;
                color: #ff758c; // 粉橙色标题
                margin-bottom: 15px; // 减小标题与下划线的距离
                font-weight: 700;
                position: relative;
                padding-bottom: 15px;
                width: 100%; // 确保标题宽度与表单一致

                &::after {
                    content: '';
                    position: absolute;
                    bottom: 0;
                    left: 50%;
                    transform: translateX(-50%);
                    width: 60px;
                    height: 3px;
                    background: #ff758c;
                    border-radius: 3px;
                }
            }

            // 头像上传容器 - 精确放在标题下划线正下方
            .avatar-container {
                display: flex;
                flex-direction: column;
                align-items: center;
                margin: 20px auto 0; // 确保与下划线保持适当距离并居中
                position: relative;
                width: 120px; // 与下划线宽度匹配居中

                .avatar-uploader {
                    width: 120px;
                    height: 120px;
                    border-radius: 50%;
                    border: 2px dashed #ffb6c1; // 粉色虚线边框
                    transition: all 0.3s ease;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    overflow: hidden;

                    &:hover {
                        border-color: #ff758c; // 粉橙色边框
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
                        color: #ffb6c1; // 粉色图标
                    }
                }

                .avatar-label {
                    margin-top: 10px;
                    color: #663300; // 深棕色文字
                    font-size: 14px;
                    text-align: center;
                }
            }

            .el-form-item {
                margin-bottom: 20px;

                &.form-actions {
                    margin-top: 30px;
                    display: flex;
                    gap: 15px;
                }

                .el-form-item__label {
                    color: #663300; // 标签文字颜色
                }
            }

            .register-btn {
                flex: 1;
                background: #ff758c; // 粉橙色按钮
                border-color: #ff758c;
                padding: 12px 0;
                font-size: 16px;
                transition: all 0.3s ease;

                &:hover {
                    background: #ff5c7a; // 深一点的粉色
                    border-color: #ff5c7a;
                    transform: translateY(-2px);
                    box-shadow: 0 4px 12px rgba(255, 117, 140, 0.3);
                }
            }

            .back-btn {
                flex: 1;
                background: transparent;
                color: #ff758c; // 粉橙色文字
                border-color: #ff758c; // 粉橙色边框
                padding: 12px 0;
                font-size: 16px;
                transition: all 0.3s ease;

                &:hover {
                    background: rgba(255, 117, 140, 0.1); // 浅粉色背景
                }
            }

            .el-input, .el-select {
                width: 100%;

                &:focus-within {
                    --el-input-focus-border-color: #ff758c; // 焦点边框颜色
                }
            }

            .el-input__prefix {
                color: #ff758c; // 前缀图标颜色
            }

            .el-radio-group {
                display: flex;
                gap: 20px;
                padding-top: 5px;

                .el-radio {
                    --el-radio-checked-color: #ff758c; // 单选框选中颜色
                    color: #663300; // 单选框文字颜色
                }
            }

            .el-select {
                .el-select__input {
                    color: #663300;
                }
            }
        }
    }

    // 页脚样式 - 与layout保持一致
    .page-footer {
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 14px;
        color: #663300;
        background-color: #fff8f8;
        padding: 15px 0;
        border-top: 1px dashed #ffb6c1;
        position: relative;
        z-index: 1;

        &::before {
            content: '🐾';
            margin-right: 8px;
            font-size: 16px;
        }
    }
}

// 动画效果
@keyframes float {
    0% { transform: translateY(0px); }
    50% { transform: translateY(-15px); }
    100% { transform: translateX(0); }
}

@keyframes wave {
    0%, 100% { transform: translateX(0); }
    50% { transform: translateX(10px); }
}

// 响应式调整
@media (max-width: 768px) {
    .container {
        flex-direction: column;
        padding: 30px 15px;
    }

    .brand-section {
        padding: 20px 15px;
        margin-bottom: 30px;
    }

    .brand-content .brand-logo {
        width: 140px;
        height: 140px;
    }

    .brand-slogan {
        font-size: 22px !important;
    }

    .form-section {
        padding: 15px;
    }

    .register-form {
        padding: 0 15px !important;
    }

    .form-title {
        font-size: 24px !important;
        margin-bottom: 10px !important;
    }

    .el-form-item {
        margin-bottom: 15px !important;
    }
}

@media (max-width: 480px) {
    .form-actions {
        flex-direction: column;
    }

    .avatar-uploader {
        width: 100px !important;
        height: 100px !important;
    }

    .page-footer {
        font-size: 12px;
        padding: 10px 5px;
    }
}
</style>
