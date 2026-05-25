<script setup>
import { useRouter } from 'vue-router';
import { 
  ArrowRight, Bell, ShoppingBag, HomeFilled, Star,
  PieChart, User, Search, Edit, Check, Phone, 
  Message, Position, EditPen, Open
} from '@element-plus/icons-vue';
import { useUserInfoStore } from '@/stores/user.js';
import { onMounted, ref, onBeforeUnmount } from 'vue';
import { getPublicListNoPage } from '@/api/publicInformation';
import { ElMessage } from 'element-plus';

const router = useRouter();
const userInfoStore = useUserInfoStore();
const publicList = ref([]);
const isAdvantageAnimate = ref(false); // 控制领养优势区域动画

onMounted(() => {
  if (userInfoStore.info && Object.keys(userInfoStore.info).length > 0) {
    router.push('/petinformation');
  }
  fetchPublicList();
  // 监听滚动，判断领养优势区域是否进入可视范围
  window.addEventListener('scroll', handleScroll);
  // 初始检查一次
  handleScroll();
});

onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll);
});

const fetchPublicList = async () => {
  try {
    const response = await getPublicListNoPage();
    if (response.data && Array.isArray(response.data)) {
      publicList.value = response.data;
    } else if (response.data && response.data.list) {
      publicList.value = response.data.list;
    } else {
      publicList.value = [];
    }
  } catch (error) {
    console.error('获取公告失败:', error);
    ElMessage.error('获取公告失败，请稍后重试');
  }
};

const handleScroll = () => {
  const advantagesSection = document.querySelector('.adoption-advantages');
  if (advantagesSection) {
    const rect = advantagesSection.getBoundingClientRect();
    const windowHeight = window.innerHeight || document.documentElement.clientHeight;
    // 当区域进入可视范围（顶部距离小于窗口高度的 80%）时，触发动画
    if (rect.top < windowHeight * 0.8 && rect.bottom > 0) {
      isAdvantageAnimate.value = true;
    }
  }
};

// 适配 Vite 环境的图片加载函数
const getImageUrl = (path, isLocal = false) => {
  // 成功故事的本地图片（src/assets 目录下）
  if (isLocal) {
    if (!path) return new URL('/src/assets/a.png', import.meta.url).href;
    
    try {
      // Vite 中通过 new URL() 加载本地图片
      return new URL(`/src/assets${path.startsWith('/') ? path : '/' + path}`, import.meta.url).href;
    } catch (e) {
      console.error('本地图片加载失败:', path);
      return new URL('/src/assets/a.png', import.meta.url).href; // 默认图
    }
  }
  
  // 公告信息的图片（不处理）
  return path || '/assets/a.png';
};

const formatDate = (dateString) => {
  if (!dateString) return '未知日期';
  try {
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  } catch (e) {
    return dateString;
  }
};

const goToLogin = () => {
  router.push('/login');
};

const goToRegister = () => {
  router.push('/register');
};
</script>
<template>
  <div class="home-container">
    <!-- 顶部导航栏 -->
    <header class="navbar">
      <div class="logo">
        <img src="@/assets/recommend-optimized.png" alt="宠物领养中心" />
        <h1>宠物领养中心</h1>
      </div>
      <div class="auth-buttons">
        <el-button type="default" @click="goToLogin">登录</el-button>
        <el-button type="primary" @click="goToRegister">注册</el-button>
      </div>
    </header>

    <!-- 主要内容区 -->
    <main class="main-content">
      <!-- 英雄区域 -->
      <section class="hero">
        <!-- 装饰元素 -->
        <div class="decor-element decor-1"></div>
        <div class="decor-element decor-2"></div>
        <div class="decor-element decor-3"></div>
        <div class="decor-element decor-4"></div>
        
        <!-- 漂浮图形装饰 -->
        <div class="floating-shapes">
          <div class="shape paw-shape" :style="{top: '15%', left: '10%', animationDelay: '0s'}"></div>
          <div class="shape heart-shape" :style="{top: '60%', left: '85%', animationDelay: '0.5s'}"></div>
          <div class="shape circle-shape" :style="{top: '30%', left: '80%', animationDelay: '1s'}"></div>
          <div class="shape paw-shape" :style="{top: '70%', left: '15%', animationDelay: '1.5s'}"></div>
        </div>
        
        <div class="hero-content">
          <div class="text-container">
            <h2>每一个生命都值得被温柔以待</h2>
            <p>欢迎来到宠物领养中心，这里有许多可爱的生命等待一个温暖的家。通过领养，您不仅能获得一个忠实的伙伴，还能拯救一个生命。</p>
            <div class="action-buttons">
              <el-button type="primary" size="large" @click="goToLogin">
                登录后探索 <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
            
            <!-- 统计数据 -->
            <div class="stats">
              <div class="stat-item">
                <span class="stat-number">500+</span>
                <span class="stat-label">等待领养的宠物</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">1200+</span>
                <span class="stat-label">成功领养案例</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">30+</span>
                <span class="stat-label">合作救助站</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="hero-image">
          <div class="image-frame">
            <img src="@/assets/b.png" alt="可爱的宠物们" />
            <div class="image-badge">
              <span>本月新到</span>
            </div>
          </div>
        </div>
      </section>

      <!-- 公告轮播区域 -->
      <section class="announcement-section">
        <h3 class="announcement-title">最新公告</h3>
        <div class="carousel-container">
          <template v-if="publicList.length > 0">
            <el-carousel 
              :interval="5000" 
              arrow="always" 
              indicator-position="bottom"
              height="500px"
            >
              <el-carousel-item v-for="(item, index) in publicList" :key="item.id || index">
                <div class="announcement-card">
                  <div class="announcement-image">
                    <img 
                      :src="getImageUrl(item.publicImage)" 
                      :alt="item.title" 
                      class="card-image"
                    >
                  </div>
                  
                  <div class="announcement-text">
                    <h4 class="announcement-title-item">{{ item.title || '无标题' }}</h4>
                    <p class="announcement-content">{{ item.content || '无内容' }}</p>
                    <div class="announcement-date">{{ formatDate(item.createTime) }}</div>
                  </div>
                </div>
              </el-carousel-item>
            </el-carousel>
          </template>
          <template v-else>
            <div class="empty-announcement">
              <el-empty description="暂无公告信息" />
            </div>
          </template>
        </div>
      </section>

      <!-- 领养优势区域 -->
      <section class="adoption-advantages">
        <h3 class="section-title">领养优势</h3>
        <p class="section-subtitle">选择领养，给毛孩子一个家，也给自己一份特别的温暖</p>
        <div class="advantages-container">
          <div class="advantage-item" :class="{ 'fade-in-up': isAdvantageAnimate }">
            <div class="advantage-icon">
              <el-icon><Star /></el-icon>
            </div>
            <h4>拯救生命</h4>
            <p>每一次领养，都是对一个生命的拯救，让流浪的毛孩子重获家庭的温暖。</p>
          </div>
          <div class="advantage-item" :class="{ 'fade-in-up': isAdvantageAnimate }" style="animation-delay: 0.2s">
            <div class="advantage-icon">
              <el-icon><HomeFilled /></el-icon>
            </div>
            <h4>已有健康基础</h4>
            <p>领养中心的宠物通常已完成基础体检、疫苗接种等，更健康更省心。</p>
          </div>
          <div class="advantage-item" :class="{ 'fade-in-up': isAdvantageAnimate }" style="animation-delay: 0.4s">
            <div class="advantage-icon">
              <el-icon><Check /></el-icon>
            </div>
            <h4>性格更适配</h4>
            <p>工作人员会了解宠物性格，帮助你找到与自己生活习惯最契合的伙伴。</p>
          </div>
          <div class="advantage-item" :class="{ 'fade-in-up': isAdvantageAnimate }" style="animation-delay: 0.6s">
            <div class="advantage-icon">
              <el-icon><ShoppingBag /></el-icon>
            </div>
            <h4>节省费用</h4>
            <p>领养相比购买，能节省不少成本，同时还能收获满满的爱与陪伴。</p>
          </div>
        </div>
      </section>

      <!-- 服务区域 -->
      <section class="features">
        <h3 class="section-title">我们的服务</h3>
        <p class="section-subtitle">全方位为您和您的宠物提供支持与服务</p>
        
        <div class="feature-cards">
          <el-card class="feature-card">
            <div class="feature-icon-container">
              <el-icon class="feature-icon"><PieChart /></el-icon>
            </div>
            <h4>宠物领养</h4>
            <p>为流浪动物寻找合适的家庭，严格的领养流程确保每只宠物都能找到最适合的家</p>
            <div class="feature-divider"></div>
            <el-button type="text" class="feature-more">了解领养流程，领养更安心</el-button>
          </el-card>
          
          <el-card class="feature-card">
            <div class="feature-icon-container">
              <el-icon class="feature-icon"><ShoppingBag /></el-icon>
            </div>
            <h4>宠物用品</h4>
            <p>提供优质的宠物用品，所有收入将用于支持流浪动物救助工作</p>
            <div class="feature-divider"></div>
            <el-button type="text" class="feature-more">售卖宠物用品，让您一键式体验领养全流程</el-button>
          </el-card>
          
          <el-card class="feature-card">
            <div class="feature-icon-container">
              <el-icon class="feature-icon"><HomeFilled /></el-icon>
            </div>
            <h4>宠物寄养</h4>
            <p>临时照顾您的宠物，专业人员全天候看护，让您出行无忧</p>
            <div class="feature-divider"></div>
            <el-button type="text" class="feature-more">预约寄养服务，让您随走随行，不必担心毛孩子</el-button>
          </el-card>
          
          <el-card class="feature-card">
            <div class="feature-icon-container">
              <el-icon class="feature-icon"><User /></el-icon>
            </div>
            <h4>志愿者招募</h4>
            <p>欢迎加入我们的志愿者团队，一起为流浪动物提供关爱与帮助</p>
            <div class="feature-divider"></div>
            <el-button type="text" class="feature-more">成为志愿者，为毛孩子保驾护航</el-button>
          </el-card>
        </div>
      </section>

      <!-- 领养流程 -->
      <section class="adoption-process">
        <h3 class="section-title">领养流程</h3>
        <p class="section-subtitle">简单几步，开启您与毛孩子的幸福生活</p>
        
        <div class="process-steps">
          <div class="process-step step-1">
            <div class="step-number">1</div>
            <div class="step-icon">
              <el-icon><Search /></el-icon>
            </div>
            <h4>浏览挑选</h4>
            <p>浏览待领养宠物信息，找到心仪的伙伴</p>
          </div>
          
          <div class="process-connector"></div>
          
          <div class="process-step step-2">
            <div class="step-number">2</div>
            <div class="step-icon">
              <el-icon><Edit /></el-icon>
            </div>
            <h4>提交申请</h4>
            <p>填写领养申请表，提供您的基本信息</p>
          </div>
          
          <div class="process-connector"></div>
          
          <div class="process-step step-3">
            <div class="step-number">3</div>
            <div class="step-icon">
              <el-icon><Check /></el-icon>
            </div>
            <h4>审核面谈</h4>
            <p>工作人员审核信息并安排面谈，确保双方匹配</p>
          </div>
          
          <div class="process-connector"></div>
          
          <div class="process-step step-4">
            <div class="step-number">4</div>
            <div class="step-icon">
              <el-icon><HomeFilled /></el-icon>
            </div>
            <h4>带回家</h4>
            <p>签署领养协议，带新伙伴回家开始新生活</p>
          </div>
        </div>
      </section>
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-content">
        <div class="footer-logo">
          <img src="@/assets/recommend-optimized.png" alt="宠物领养中心">
          <h3>宠物领养中心</h3>
        </div>
        
        <div class="footer-links">
          <div class="link-group">
            <h4>关于我们</h4>
            <ul>
              <li><a href="#">中心介绍</a></li>
              <li><a href="#">团队成员</a></li>
              <li><a href="#">联系方式</a></li>
              <li><a href="#">加入我们</a></li>
            </ul>
          </div>
          
          <div class="link-group">
            <h4>领养服务</h4>
            <ul>
              <li><a href="#">领养流程</a></li>
              <li><a href="#">常见问题</a></li>
              <li><a href="#">领养协议</a></li>
              <li><a href="#">后续支持</a></li>
            </ul>
          </div>
          
          <div class="link-group">
            <h4>帮助中心</h4>
            <ul>
              <li><a href="#">宠物护理</a></li>
              <li><a href="#">行为训练</a></li>
              <li><a href="#">健康咨询</a></li>
              <li><a href="#">捐赠支持</a></li>
            </ul>
          </div>
        </div>
        
        <div class="footer-contact">
          <div class="contact-item">
            <el-icon><Phone /></el-icon>
            <span>400-123-4567</span>
          </div>
          <div class="contact-item">
            <el-icon><Message /></el-icon>
            <span>contact@petadopt.com</span>
          </div>
          <div class="contact-item">
            <el-icon><Position /></el-icon>
            <span>北京市朝阳区宠物街123号</span>
          </div>
        </div>
      </div>
      
      <div class="footer-bottom">
        <p>© 2025 宠物领养中心 - 让每一个生命都被温柔以待</p>
        <div class="social-icons">
          <a href="#" class="social-icon"><el-icon><EditPen /></el-icon></a>
          <a href="#" class="social-icon"><el-icon><Open /></el-icon></a>
          <a href="#" class="social-icon"><el-icon><Bell /></el-icon></a>
        </div>
      </div>
    </footer>
  </div>
</template>

<style lang="scss" scoped>
// 动画定义
@keyframes float {
  0% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(5deg); }
  100% { transform: translateY(0px) rotate(0deg); }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInUpAdv {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInRight {
  from {
    opacity: 0;
    transform: translateX(30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.05); }
  100% { transform: scale(1); }
}

@keyframes float-shape {
  0% { transform: translateY(0px) rotate(0deg); opacity: 0.6; }
  50% { transform: translateY(-30px) rotate(10deg); opacity: 0.8; }
  100% { transform: translateY(0px) rotate(0deg); opacity: 0.6; }
}

@keyframes shine {
  0% { background-position: -100px; }
  60%, 100% { background-position: 320px; }
}

.home-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f9f9f9;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  position: relative;
  overflow-x: hidden;
}

.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 50px;
  height: 80px;
  background-color: white;
  box-shadow: 0 2px 15px rgba(255, 107, 129, 0.1);
  transition: all 0.3s ease;
  position: relative;
  z-index: 10;

  &:hover {
    box-shadow: 0 4px 20px rgba(255, 107, 129, 0.15);
  }

  .logo {
    display: flex;
    align-items: center;
    gap: 15px;

    img {
      height: 50px;
      width: auto;
      transition: transform 0.3s ease;
      
      &:hover {
        transform: scale(1.05);
      }
    }

    h1 {
      font-size: 24px;
      color: #ff6b81;
      margin: 0;
      font-weight: 700;
      letter-spacing: 0.5px;
    }
  }

  .auth-buttons {
    display: flex;
    gap: 15px;

    .el-button {
      border-radius: 25px;
      padding: 8px 20px;
      font-weight: 500;
      transition: all 0.3s ease;
      
      &:nth-child(1) {
        color: #ff6b81;
        border-color: #ffccd5;
        
        &:hover {
          background-color: #fff5f7;
          border-color: #ff6b81;
          transform: translateY(-2px);
        }
      }
      
      &:nth-child(2) {
        background-color: #ff6b81;
        border-color: #ff6b81;
        
        &:hover {
          background-color: #ff4d6d;
          border-color: #ff4d6d;
          transform: translateY(-2px);
        }
      }
    }
  }
}

.main-content {
  flex: 1;
}

.hero {
  display: flex;
  align-items: center;
  padding: 60px 100px;
  background: linear-gradient(135deg, #fff0f3, #ffe6eb);
  position: relative;
  overflow: hidden;
  min-height: 600px;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-image: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffccd5' fill-opacity='0.1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
    opacity: 0.5;
  }

  // 装饰元素
  .decor-element {
    position: absolute;
    background-color: rgba(255, 107, 129, 0.1);
    border-radius: 50%;
    filter: blur(20px);
    z-index: 0;
  }
  
  .decor-1 {
    width: 150px;
    height: 150px;
    top: 30%;
    left: 15%;
    animation: float 6s ease-in-out infinite;
  }
  
  .decor-2 {
    width: 200px;
    height: 200px;
    bottom: 20%;
    left: 20%;
    animation: float 8s ease-in-out infinite 1s;
  }
  
  .decor-3 {
    width: 120px;
    height: 120px;
    top: 40%;
    left: 30%;
    animation: float 7s ease-in-out infinite 0.5s;
  }
  
  .decor-4 {
    width: 180px;
    height: 180px;
    bottom: 30%;
    left: 25%;
    animation: float 9s ease-in-out infinite 1.5s;
  }

  // 漂浮图形装饰
  .floating-shapes {
    position: absolute;
    width: 100%;
    height: 100%;
    top: 0;
    left: 0;
    pointer-events: none;
    z-index: 0;
  }
  
  .shape {
    position: absolute;
    opacity: 0.6;
    animation: float-shape 10s ease-in-out infinite;
  }
  
  .paw-shape {
    width: 60px;
    height: 60px;
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='%23ff6b81'%3E%3Cpath d='M12 2c-.55 0-1 .45-1 1v3H8c-1.66 0-3 1.34-3 3v5c0 1.1.9 2 2 2h1v5c0 .55.45 1 1 1s1-.45 1-1v-5h2v5c0 .55.45 1 1 1s1-.45 1-1v-5h1c1.1 0 2-.9 2-2v-5c0-1.66-1.34-3-3-3h-3V3c0-.55-.45-1-1-1zm-5 9c-.55 0-1-.45-1-1s.45-1 1-1 1 .45 1 1-.45 1-1 1zm9 0c-.55 0-1-.45-1-1s.45-1 1-1 1 .45 1 1-.45 1-1 1zm-9-4h2v2H8V7zm4 0h2v2h-2V7z'/%3E%3C/svg%3E");
    background-repeat: no-repeat;
  }
  
  .heart-shape {
    width: 50px;
    height: 50px;
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='%23ff6b81'%3E%3Cpath d='M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z'/%3E%3C/svg%3E");
    background-repeat: no-repeat;
  }
  
  .circle-shape {
    width: 40px;
    height: 40px;
    border: 3px solid #ff6b81;
    border-radius: 50%;
    opacity: 0.3;
  }

  .hero-content {
    flex: 1;
    padding-right: 50px;
    position: relative;
    z-index: 1;

    .text-container {
      position: relative;
      padding: 30px;
      border-radius: 15px;
      background-color: rgba(255, 255, 255, 0.7);
      backdrop-filter: blur(10px);
      box-shadow: 0 10px 30px rgba(255, 107, 129, 0.1);
      transition: all 0.5s ease;
      
      &:hover {
        box-shadow: 0 15px 35px rgba(255, 107, 129, 0.15);
        transform: translateY(-5px);
      }
      
      &::before {
        content: '';
        position: absolute;
        width: 8px;
        height: 40px;
        background-color: #ff6b81;
        left: 0;
        top: 30px;
        border-radius: 0 4px 4px 0;
      }
    }

    h2 {
      font-size: 42px;
      color: #333;
      margin-bottom: 20px;
      line-height: 1.3;
      text-shadow: 0 2px 4px rgba(0,0,0,0.05);
      padding-left: 20px;
      animation: fadeInUp 1s ease forwards;
      opacity: 0;
      
      @media (max-width: 768px) {
        font-size: 32px;
      }
    }

    p {
      font-size: 18px;
      color: #666;
      margin-bottom: 30px;
      max-width: 600px;
      line-height: 1.7;
      padding-left: 20px;
      animation: fadeInUp 1s ease 0.3s forwards;
      opacity: 0;
    }

    .action-buttons {
      margin-top: 20px;
      padding-left: 20px;
      animation: fadeInUp 1s ease 0.6s forwards;
      opacity: 0;
      
      .el-button {
        border-radius: 30px;
        padding: 12px 30px;
        font-size: 16px;
        background-color: #ff6b81;
        border-color: #ff6b81;
        transition: all 0.3s ease;
        
        &:hover {
          background-color: #ff4d6d;
          border-color: #ff4d6d;
          transform: translateY(-3px) scale(1.02);
          box-shadow: 0 10px 20px rgba(255, 107, 129, 0.2);
        }
        
        .el-icon {
          margin-left: 8px;
          transition: transform 0.3s ease;
        }
        
        &:hover .el-icon {
          transform: translateX(3px);
        }
      }
    }
    
    // 统计数据
    .stats {
      display: flex;
      justify-content: flex-start;
      gap: 40px;
      margin-top: 40px;
      padding-left: 20px;
      animation: fadeInUp 1s ease 0.9s forwards;
      opacity: 0;
      
      .stat-item {
        text-align: center;
        
        .stat-number {
          display: block;
          font-size: 28px;
          font-weight: 700;
          color: #ff6b81;
          margin-bottom: 5px;
          position: relative;
          
          &::after {
            content: '';
            position: absolute;
            bottom: -5px;
            left: 50%;
            transform: translateX(-50%);
            width: 30px;
            height: 2px;
            background-color: #ff6b81;
            border-radius: 1px;
          }
        }
        
        .stat-label {
          color: #666;
          font-size: 14px;
        }
      }
    }
  }

  .hero-image {
    flex: 0 0 35%;
    text-align: center;
    padding: 20px;
    position: relative;
    z-index: 1;
    animation: fadeInRight 1.5s ease forwards;
    opacity: 0;

    .image-frame {
      border-radius: 12px;
      overflow: hidden;
      box-shadow: 0 15px 35px rgba(255, 107, 129, 0.2);
      transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
      position: relative;
      
      &:hover {
        transform: translateY(-10px) scale(1.03);
        box-shadow: 0 20px 40px rgba(255, 107, 129, 0.3);
      }
    }
    
    .image-badge {
      position: absolute;
      top: 20px;
      right: -20px;
      background-color: #ff6b81;
      color: white;
      padding: 5px 25px;
      border-radius: 20px;
      font-weight: 600;
      font-size: 14px;
      box-shadow: 0 3px 10px rgba(255, 107, 129, 0.3);
      transform: rotate(5deg);
      transition: all 0.3s ease;
      
      .image-frame:hover & {
        transform: rotate(0deg) scale(1.1);
      }
    }

    img {
      width: 100%;
      height: auto;
      display: block;
      transition: all 0.7s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    }
  }
}

/* 公告轮播样式 */
.announcement-section {
  padding: 60px 100px;
  background-color: white;
  position: relative;
  
  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 1px;
    background: linear-gradient(90deg, transparent, #ffccd5, transparent);
  }

  .announcement-title {
    font-size: 28px;
    color: #333;
    margin-bottom: 30px;
    text-align: center;
    position: relative;
    font-weight: 600;

    &::after {
      content: '';
      display: block;
      width: 80px;
      height: 3px;
      background-color: #ff6b81;
      margin: 15px auto 0;
      border-radius: 3px;
    }
  }

  .carousel-container {
    max-width: 800px;
    margin: 0 auto;
  }

  .announcement-card {
    height: 100%;
    padding: 25px;
    border-radius: 15px;
    background-color: #fff8f8;
    border: 1px solid #ffecd2;
    display: flex;
    flex-direction: column;
    gap: 15px;
    overflow: hidden;
    box-shadow: 0 5px 15px rgba(255, 107, 129, 0.08);
    transition: all 0.3s ease;
    
    &:hover {
      box-shadow: 0 10px 30px rgba(255, 107, 129, 0.15);
      transform: translateY(-5px);
    }
  }

  .announcement-image {
    flex: 0 0 280px;
    border-radius: 10px;
    overflow: hidden;
    background-color: #f5f5f5;
    width: 100%;
    transition: transform 0.3s ease;
    position: relative;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: rgba(0,0,0,0.03);
      z-index: 1;
      transition: opacity 0.3s ease;
    }
    
    &:hover {
      transform: translateY(-5px);
    }
    
    &:hover::before {
      opacity: 0;
    }

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      display: block;
      transition: transform 0.7s cubic-bezier(0.175, 0.885, 0.32, 1.275);
      
      &:hover {
        transform: scale(1.05);
      }
    }
  }

  .announcement-text {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    padding: 10px 5px;
  }

  .announcement-title-item {
    font-size: 22px;
    color: #ff6b81;
    margin: 0 0 10px 0;
    font-weight: 600;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    padding-bottom: 8px;
    border-bottom: 1px solid rgba(255, 204, 213, 0.3);
  }

  .announcement-content {
    flex: 1;
    color: #666;
    line-height: 1.8;
    margin: 0 0 10px 0;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    padding: 10px 0;
  }

  .announcement-date {
    color: #999;
    font-size: 14px;
    text-align: right;
    margin-top: auto;
    padding-top: 10px;
    border-top: 1px dashed #ffccd5;
    font-style: italic;
  }

  .empty-announcement {
    padding: 80px 0;
    text-align: center;
    
    .el-empty {
      transform: scale(1.1);
    }
  }
  
  .el-carousel {
    .el-carousel__indicators {
      bottom: 15px;
      
      .el-carousel__indicator {
        width: 10px;
        height: 10px;
        border-radius: 50%;
        background-color: #ffccd5;
        margin: 0 6px;
        
        &.is-active {
          background-color: #ff6b81;
          width: 30px;
          border-radius: 5px;
        }
      }
    }
    
    .el-carousel__arrow {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      background-color: rgba(255, 255, 255, 0.8);
      color: #ff6b81;
      box-shadow: 0 2px 10px rgba(255, 107, 129, 0.2);
      
      &:hover {
        background-color: white;
        color: #ff4d6d;
        transform: scale(1.1);
      }
    }
  }
}

/* 领养优势区域 */
.adoption-advantages {
  padding: 80px 50px;
  text-align: center;
  background-color: #fff8f8;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 1px;
    background: linear-gradient(90deg, transparent, #ffccd5, transparent);
  }
  
  .section-title {
    font-size: 32px;
    color: #333;
    margin-bottom: 15px;
    text-align: center;
    position: relative;
    font-weight: 600;

    &::after {
      content: '';
      display: block;
      width: 80px;
      height: 3px;
      background-color: #ff6b81;
      margin: 15px auto 0;
      border-radius: 3px;
    }
  }
  
  .section-subtitle {
    text-align: center;
    color: #666;
    font-size: 16px;
    margin-bottom: 50px;
    max-width: 700px;
    margin-left: auto;
    margin-right: auto;
  }
  
  .advantages-container {
    display: flex;
    justify-content: center;
    gap: 30px;
    flex-wrap: wrap;
  }
  
  .advantage-item {
    width: 280px;
    padding: 35px;
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    border-radius: 15px;
    border: none;
    background-color: white;
    box-shadow: 0 5px 15px rgba(0,0,0,0.05);
    opacity: 0;
    transform: translateY(30px);
    animation-fill-mode: forwards;
  }
  
  .fade-in-up {
    animation: fadeInUpAdv 0.8s ease forwards;
  }
  
  .advantage-icon {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    background-color: rgba(255, 107, 129, 0.1);
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 20px;
    transition: all 0.3s ease;
    
    .advantage-item:hover & {
      background-color: rgba(255, 107, 129, 0.2);
      transform: scale(1.1);
    }
  }
  
  .advantage-icon .el-icon {
    font-size: 28px;
    color: #ff6b81;
  }
  
  h4 {
    font-size: 20px;
    margin-bottom: 15px;
    color: #333;
    font-weight: 600;
  }
  
  p {
    color: #666;
    line-height: 1.6;
    font-size: 15px;
    margin-bottom: 0;
  }
}

/* 服务区域 */
.features {
  padding: 80px 50px;
  text-align: center;
  background-color: white;
  position: relative;
  
  .section-title {
    font-size: 32px;
    color: #333;
    margin-bottom: 15px;
    text-align: center;
    position: relative;
    font-weight: 600;

    &::after {
      content: '';
      display: block;
      width: 80px;
      height: 3px;
      background-color: #ff6b81;
      margin: 15px auto 0;
      border-radius: 3px;
    }
  }
  
  .section-subtitle {
    text-align: center;
    color: #666;
    font-size: 16px;
    margin-bottom: 50px;
    max-width: 700px;
    margin-left: auto;
    margin-right: auto;
  }

  .feature-cards {
    display: flex;
    justify-content: center;
    gap: 30px;
    flex-wrap: wrap;
  }

  .feature-card {
    width: 280px;
    padding: 35px;
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    border-radius: 15px;
    border: none;
    background-color: #f9f9f9;
    box-shadow: 0 5px 15px rgba(0,0,0,0.05);
    animation: fadeInUp 0.8s ease forwards;
    opacity: 0;
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 4px;
      background: linear-gradient(90deg, #ff6b81, #ffb3c1);
      transform: scaleX(0);
      transition: transform 0.3s ease;
      transform-origin: left center;
    }
    
    &:hover::before {
      transform: scaleX(1);
    }
    
    &:nth-child(1) { animation-delay: 0.1s; }
    &:nth-child(2) { animation-delay: 0.3s; }
    &:nth-child(3) { animation-delay: 0.5s; }
    &:nth-child(4) { animation-delay: 0.7s; }
    
    &:hover {
      transform: translateY(-10px);
      box-shadow: 0 15px 30px rgba(255, 107, 129, 0.15);
    }

    .feature-icon-container {
      width: 70px;
      height: 70px;
      border-radius: 50%;
      background-color: rgba(255, 107, 129, 0.1);
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 20px;
      transition: all 0.3s ease;
      
      .feature-card:hover & {
        background-color: rgba(255, 107, 129, 0.2);
        transform: scale(1.1);
      }
    }
    
    .feature-icon {
      font-size: 32px;
      color: #ff6b81;
    }

    h4 {
      font-size: 20px;
      margin-bottom: 15px;
      color: #333;
      font-weight: 600;
    }

    p {
      color: #666;
      line-height: 1.6;
      font-size: 15px;
      margin-bottom: 25px;
      min-height: 70px;
    }
    
    .feature-divider {
      height: 1px;
      background-color: rgba(255, 204, 213, 0.3);
      margin: 15px 0;
    }
    
    .feature-more {
      color: #ff6b81;
      padding: 0;
      font-size: 14px;
      font-weight: 500;
      transition: all 0.3s ease;
      
      &:hover {
        color: #ff4d6d;
        letter-spacing: 0.5px;
      }
    }
  }
}

/* 领养流程区域 */
.adoption-process {
  padding: 80px 50px;
  background-color: #fff8f8;
  text-align: center;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 1px;
    background: linear-gradient(90deg, transparent, #ffccd5, transparent);
  }
  
  .section-title {
    font-size: 32px;
    color: #333;
    margin-bottom: 15px;
    text-align: center;
    position: relative;
    font-weight: 600;

    &::after {
      content: '';
      display: block;
      width: 80px;
      height: 3px;
      background-color: #ff6b81;
      margin: 15px auto 0;
      border-radius: 3px;
    }
  }
  
  .section-subtitle {
    text-align: center;
    color: #666;
    font-size: 16px;
    margin-bottom: 50px;
    max-width: 700px;
    margin-left: auto;
    margin-right: auto;
  }
  
  .process-steps {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-wrap: wrap;
    position: relative;
    max-width: 1200px;
    margin: 0 auto;
  }
  
  .process-step {
    flex: 1;
    min-width: 200px;
    padding: 30px 20px;
    position: relative;
    z-index: 1;
    
    @media (max-width: 768px) {
      margin-bottom: 40px;
    }
    
    .step-number {
      width: 50px;
      height: 50px;
      border-radius: 50%;
      background-color: #ff6b81;
      color: white;
      font-size: 20px;
      font-weight: 700;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 20px;
      box-shadow: 0 4px 15px rgba(255, 107, 129, 0.3);
      transition: all 0.3s ease;
      
      .process-step:hover & {
        transform: scale(1.15) rotate(5deg);
        box-shadow: 0 6px 20px rgba(255, 107, 129, 0.4);
      }
    }
    
    .step-icon {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      background-color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 20px;
      box-shadow: 0 3px 10px rgba(0,0,0,0.1);
      color: #ff6b81;
      font-size: 24px;
      transition: all 0.3s ease;
      
      .process-step:hover & {
        transform: translateY(-5px);
      }
    }
    
    h4 {
      font-size: 18px;
      color: #333;
      margin-bottom: 10px;
      font-weight: 600;
    }
    
    p {
      color: #666;
      font-size: 14px;
      line-height: 1.6;
    }
  }
  
  .process-connector {
    width: 40px;
    height: 2px;
    background-color: #ffccd5;
    display: none;
    
    @media (min-width: 769px) {
      display: block;
    }
  }
}

/* 页脚样式 */
.footer {
  background-color: #333;
  color: white;
  padding: 60px 50px 30px;
  margin-top: auto;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 5px;
    background: linear-gradient(90deg, #ff6b81, #ffb3c1, #ff6b81);
  }
  
  .footer-content {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    gap: 40px;
    margin-bottom: 40px;
  }
  
  .footer-logo {
    flex: 1;
    min-width: 250px;
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    
    img {
      height: 60px;
      width: auto;
      margin-bottom: 15px;
      filter: brightness(1.5);
      transition: transform 0.3s ease;
      
      &:hover {
        transform: scale(1.05);
      }
    }
    
    h3 {
      font-size: 22px;
      color: white;
      margin: 0 0 15px 0;
      font-weight: 600;
    }
  }
  
  .footer-links {
    flex: 2;
    min-width: 500px;
    display: flex;
    justify-content: center;
    gap: 50px;
    flex-wrap: wrap;
    
    .link-group {
      min-width: 120px;
      
      h4 {
        font-size: 16px;
        color: #ffccd5;
        margin-bottom: 20px;
        font-weight: 600;
        position: relative;
        
        &::after {
          content: '';
          position: absolute;
          bottom: -8px;
          left: 0;
          width: 30px;
          height: 2px;
          background-color: #ff6b81;
        }
      }
      
      ul {
        list-style: none;
        padding: 0;
        margin: 0;
        
        li {
          margin-bottom: 12px;
          
          a {
            color: #ccc;
            text-decoration: none;
            font-size: 14px;
            transition: all 0.3s ease;
            
            &:hover {
              color: white;
              padding-left: 5px;
              color: #ffb3c1;
            }
          }
        }
      }
    }
  }
  
  .footer-contact {
    flex: 1;
    min-width: 250px;
    display: flex;
    flex-direction: column;
    gap: 20px;
    
    .contact-item {
      display: flex;
      align-items: center;
      gap: 15px;
      color: #ccc;
      font-size: 14px;
      
      .el-icon {
        color: #ff6b81;
        font-size: 18px;
        width: 24px;
        height: 24px;
      }
      
      &:hover {
        color: white;
        
        .el-icon {
          transform: scale(1.2);
        }
      }
    }
  }
  
  .footer-bottom {
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    padding-top: 30px;
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    align-items: center;
    gap: 20px;
    
    p {
      margin: 0;
      font-size: 14px;
      color: #999;
      
      &:hover {
        color: #ccc;
      }
    }
    
    .social-icons {
      display: flex;
      gap: 15px;
      
      .social-icon {
        width: 36px;
        height: 36px;
        border-radius: 50%;
        background-color: rgba(255, 255, 255, 0.1);
        display: flex;
        align-items: center;
        justify-content: center;
        color: #ccc;
        transition: all 0.3s ease;
        
        &:hover {
          background-color: #ff6b81;
          color: white;
          transform: translateY(-5px);
        }
      }
    }
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .navbar {
    padding: 0 20px;
    
    .logo h1 {
      font-size: 20px;
    }
  }

  .hero {
    flex-direction: column;
    padding: 30px 20px;
    min-height: auto;
    
    .decor-element {
      display: none;
    }
    
    .hero-content {
      padding-right: 0;
      margin-bottom: 30px;
      text-align: center;
      
      .text-container {
        &::before {
          display: none;
        }
      }
      
      h2, p, .action-buttons, .stats {
        padding-left: 0;
      }
      
      .stats {
        justify-content: center;
        gap: 20px;
      }
    }
    
    .hero-image {
      flex: 0 0 auto;
      width: 100%;
    }
  }
  
  .announcement-section {
    padding: 40px 20px;
    
    .carousel-container {
      max-width: 100%;
    }
    
    .announcement-card {
      flex-direction: column;
      padding: 15px;
    }
    
    .announcement-image {
      flex: 0 0 200px;
    }
  }
  
  .adoption-advantages {
    padding: 60px 20px;
    
    h3 {
      font-size: 26px;
    }
    
    .advantage-item {
      width: 100%;
      max-width: 300px;
    }
  }

  .features {
    padding: 60px 20px;
    
    h3 {
      font-size: 26px;
    }
    
    .feature-card {
      width: 100%;
      max-width: 300px;
    }
  }
  
  .adoption-process {
    padding: 60px 20px;
    
    .process-steps {
      flex-direction: column;
    }
    
    .process-connector {
      width: 2px;
      height: 30px;
    }
  }
  
  .footer {
    padding: 40px 20px 20px;
    
    .footer-content {
      flex-direction: column;
      align-items: center;
    }
    
    .footer-links {
      min-width: 100%;
      justify-content: center;
      gap: 30px;
    }
    
    .footer-bottom {
      flex-direction: column;
    }
  }
}
</style>
