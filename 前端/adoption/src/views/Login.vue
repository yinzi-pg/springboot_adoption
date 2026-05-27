<template>
    <el-row class="login-page">
        <el-col :span="24" class="bg"></el-col>
        <el-col :span="7" :offset="0" class="form">
            <el-form ref="form" size="large" autocomplete="off" :model="registerData" v-if="isLogin">
                <el-form-item>
                    <h1 class="login-title">登录</h1>
                </el-form-item>

                <el-form-item class="form-item">
                    <label class="form-label">账号：</label>
                    <el-input 
                        placeholder="请输入用户名" 
                        :prefix-icon="User" 
                        v-model="registerData.userName"
                        class="form-input"
                    ></el-input>
                </el-form-item>

                <el-form-item class="form-item">
                    <label class="form-label">密码：</label>
                    <el-input 
                        placeholder="请输入密码"  
                        type="password" 
                        :prefix-icon="Lock" 
                        v-model="registerData.userPassword"
                        class="form-input"
                    ></el-input>
                </el-form-item>

                <el-form-item class="form-item">
                    <label class="form-label">验证码：</label>
                    <el-input 
                        v-model="captchaInput" 
                        placeholder="请输入验证码"
                        :prefix-icon="Picture"
                        class="form-input"
                    >
                    </el-input>
                </el-form-item>
                
                <el-form-item class="captcha-item">
                    <div class="captcha-container" @click="generateCaptcha">
                        <div class="captcha-img" v-html="captchaDisplay"></div>
                        <el-icon class="refresh-icon"><Refresh /></el-icon>
                    </div>
                    <div class="captcha-hint">点击图片刷新验证码</div>
                </el-form-item>

                <el-form-item>
                    <el-button class="login-btn" type="primary" @click="login">登录</el-button>
                </el-form-item>

                <el-form-item class="register-link">
                    <el-link type="info" :underline="false" @click="register">
                        没有账号请注册->
                    </el-link>       
                </el-form-item>
            </el-form>
        </el-col>
    </el-row>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { User, Lock, Picture, Refresh } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useUserInfoStore } from '@/stores/user.js';
import { loginService } from '@/api/user';

// 验证码相关
const captchaCode = ref('') // 实际的验证码
const captchaDisplay = ref('') // 用于显示的验证码HTML
const captchaInput = ref('') // 用户输入的验证码

// 生成随机验证码
const generateCaptcha = () => {
    captchaInput.value = ''
    
    const chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'
    const length = 4
    let code = ''
    
    for (let i = 0; i < length; i++) {
        code += chars.charAt(Math.floor(Math.random() * chars.length))
    }
    captchaCode.value = code
    
    let html = ''
    const colors = ['#333', '#666', '#999', '#3498db', '#e74c3c', '#2ecc71']
    const rotations = [-15, -10, -5, 0, 5, 10, 15]
    
    for (let i = 0; i < code.length; i++) {
        const color = colors[Math.floor(Math.random() * colors.length)]
        const rotation = rotations[Math.floor(Math.random() * rotations.length)]
        const fontSize = 16 + Math.random() * 8
        html += `<span style="
            display: inline-block; 
            color: ${color};
            transform: rotate(${rotation}deg);
            font-size: ${fontSize}px;
            margin: 0 3px;
            font-weight: bold;
        ">${code[i]}</span>`
    }
    
    // 添加干扰线
    for (let i = 0; i < 6; i++) {
        const color = colors[Math.floor(Math.random() * colors.length)]
        const left = Math.random() * 80
        const top = Math.random() * 80
        const width = 2000 + Math.random() * 30
        const rotation = Math.random() * 360
        html += `<div style="
            position: absolute;
            background-color: ${color};
            opacity: 0.5;
            width: ${width}px;
            height:5px;
            left: ${left}%;
            top: ${top}%;
            transform: rotate(${rotation}deg);
            transform-origin: 0 0;
        "></div>`
    }
    
    captchaDisplay.value = html
}

// 验证验证码
const validateCaptcha = () => {
    if (!captchaInput.value) {
        ElMessage.warning('请输入验证码')
        return false
    }
    
    if (captchaInput.value.toUpperCase() !== captchaCode.value.toUpperCase()) {
        ElMessage.error('验证码不正确')
        generateCaptcha()
        return false
    }
    
    return true
}

onMounted(() => {
    generateCaptcha()
    
    // 路由检查
    if (router.currentRoute.value.path !== '/login') {
        if (!userInfoStore.info.userName || !userInfoStore.info.password) {
            ElMessage.error('请先登录')
            router.push('/login')
        } else {
            router.push('/layout')
        }
    }
})

const UserStore = useUserInfoStore()
const isLogin = ref(true)
const router = useRouter()

// 注册相关数据
const registerData = ref({
    userName: '',
    userPassword: '',
    userGender: '',
    userAge: '',
    userPhone: '',
    userEmail: '',
    userRole: ''
})

// 登录方法
const login = async () => {
    // 先验证验证码
    if (!validateCaptcha()) {
        return
    }

    // try {
    //     const result = await loginService(registerData.value);
    //     if (result.code === 0) {
    //         ElMessage.success("登录成功");
    //         console.log(result.data);
    //         UserStore.info = result.data; // 存储用户信息（包含userRole）
            
    //         // 根据角色动态跳转布局页
    //         if (['admin', 'volunteer'].includes(result.data.userRole)) {
    //             // 管理员跳转到/layout
    //             router.push('/layout');
    //         } else if (result.data.userRole === 'user') {
    //             // 普通用户或志愿者跳转到/UserLayout
    //             router.push('/UserLayout');
    //         } else {
    //             // 处理未定义的角色（可选）
    //             ElMessage.warning('角色未配置，跳转到默认页面');
    //             router.push('/layout'); // 或其他默认页面
    //         }
    //     } else {
    //         ElMessage.error(result.msg || "登录失败");
    //     }
    // } catch (error) {
    //     ElMessage.error("登录过程出错，请重试");
    //     console.error(error);
    // }

     try {
        const result = await loginService(registerData.value);
        console.log(result)
        if (result.code === 0) {
            ElMessage.success("登录成功");
            console.log("用户信息：", result.data);
            
            // 存储用户信息（包含后端返回的最新角色userRole）
            UserStore.setInfo(result.data); // 用你现有的setInfo方法存储
            
            // 关键：根据后端返回的最新角色判断跳转
            // 当管理员激活后，后端会将userRole从user改为volunteer
            if (result.data.userRole === 'volunteer') {
                // 志愿者跳转到对应的页面（比如/layout）
                router.push('/layout');
            } else if (result.data.userRole === 'user') {
                // 普通用户跳转到用户页面（比如/UserLayout）
                router.push('/UserLayout');
            } else if (result.data.userRole === 'admin') {
                // 管理员页面（如果需要）
                router.push('/layout');
            }
            
        } else {
            ElMessage.error(result.message || "登录失败");
        }
    } catch (error) {
        ElMessage.error("登录过程出错，请重试");
        console.error(error);
    }


}

const register = () => {
    router.push('/register')
}
</script>

<style lang="scss" scoped>
.login-page {
    height: 100vh;
    width: 100%;
    background: url('@/assets/image.png') no-repeat center center / cover; 
    position: relative;
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;

    &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(255, 255, 255, 0.6);
        z-index: 0;
    }

    .bg {
        background: 
            url('@/assets/logo2.png') no-repeat center center / cover,
            url('@/assets/login_bg.jpg') no-repeat center / cover;
        border-radius: 0 20px 20px 0;
        position: relative;
        z-index: 1;
    }

    .form {
        display: flex;
        flex-direction: column;
        justify-content: center;
        user-select: none;
        background: rgba(255, 255, 255, 0.95);
        padding: 2.5rem 2rem;
        border-radius: 16px;
        box-shadow: 0 8px 30px rgba(255, 107, 129, 0.12);
        margin: 1.5rem;
        position: relative;
        z-index: 2;
        backdrop-filter: blur(5px);
        width: 90%;
        max-width: 450px;

        .login-title {
            margin: 0 auto 2rem;
            color: #ff6b81;
            font-size: 2rem;
            font-weight: 600;
            position: relative;
            padding-bottom: 0.8rem;
            
            &::after {
                content: '';
                position: absolute;
                bottom: 0;
                left: 50%;
                transform: translateX(-50%);
                width: 60px;
                height: 3px;
                background: #ff6b81;
            }
        }

        // 统一表单项样式，确保对齐
        .form-item {
            display: flex;
            align-items: center;
            margin-bottom: 1.2rem;
            width: 100%;
            
            .form-label {
                width: 80px;  // 固定标签宽度，确保对齐
                text-align: right;
                padding-right: 12px;
                color: #666;
                font-size: 15px;
            }
            
            .form-input {
                flex: 1;  // 输入框占剩余空间
                --el-input-height: 50px;
            }
        }

        // 验证码图片区域样式
        .captcha-item {
            margin-left: 92px;  // 与输入框对齐
            margin-bottom: 1.2rem;
            
            .captcha-container {
                display: flex;
                align-items: center;
                width: 100%;
                
                .captcha-img {
                    width: 100%;
                    height: 50px;
                    line-height: 50px;
                    text-align: center;
                    background-color: #f5f5f5;
                    border-radius: 4px;
                    cursor: pointer;
                    position: relative;
                    overflow: hidden;
                }
                
                .refresh-icon {
                    margin-left: 10px;
                    cursor: pointer;
                    color: #606266;
                    font-size: 18px;
                    
                    &:hover {
                        color: #ff6b81;
                    }
                }
            }
            
            .captcha-hint {
                margin-top: 5px;
                font-size: 12px;
                color: #909399;
            }
        }

        // 登录按钮样式
        .login-btn {
            width: 100%;
            padding: 12px;
            font-size: 16px;
            background: linear-gradient(135deg, #ff6b81, #ff8fab);
            border: none;
            border-radius: 8px;
            transition: all 0.3s ease;
            margin-bottom: 1rem;
            
            &:hover {
                background: linear-gradient(135deg, #ff4d6d, #ff769a);
                transform: translateY(-2px);
                box-shadow: 0 4px 12px rgba(255, 107, 129, 0.25);
            }
        }

        // 注册链接样式
        .register-link {
            text-align: center;
            margin-top: 0.5rem;
            
            .el-link {
                color: #ff6b81;
                font-size: 14px;
                
                &:hover {
                    color: #ff4d6d;
                }
            }
        }

        .el-input {
            --el-input-bg-color: rgba(255, 255, 255, 0.9);
            --el-input-text-color: #333;
            --el-input-hover-border-color: #ff6b81;
            --el-input-focus-border-color: #ff6b81;
            --el-input-prefix-icon-color: #ff6b81;
        }
    }
}
</style>
