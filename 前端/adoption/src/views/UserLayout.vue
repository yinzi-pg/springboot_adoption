<script setup>
import {
  User,
  EditPen,
  SwitchButton,
  CaretBottom,
  Bell,
  ShoppingCart,
  ArrowRight,
  Calendar,
  Document,
  Key,
  Check,
  Phone,
  Message,
  Position,
  Open
} from '@element-plus/icons-vue'

// 引入默认图片（用于加载失败兜底）
import defaultPetImg from '@/assets/a.png';
import defaultAnnouncementImg from '@/assets/recommend-optimized.png';
import avatar from '@/assets/b.png'

import { useUserInfoStore } from '@/stores/user.js'
import { useRouter, useRoute } from 'vue-router';
import { ElMessage, ElMessageBox, ElAvatar, ElIcon, ElButton, ElMenu, ElMenuItem } from 'element-plus';
import { ref, watchEffect, computed, onMounted } from 'vue';
// 引入获取公告信息的方法
import { getPublicListNoPage } from '@/api/publicInformation'
const BACKEND_BASE = 'http://localhost:8080/springboot_adoption';
const router = useRouter()
const route = useRoute()
const userInfoStore = useUserInfoStore()
const dropdownVisible = ref(false)
const mobileMenuOpen = ref(false) // 移动端菜单状态
const currentSlide = ref(0) // 轮播图当前索引
const isScrolled = ref(false) // 滚动状态监测

// 公告轮播数据
const announcements = ref([])

// 假宠物信息数据（原有）
const fakePetList = ref([
  {
    petId: 'fake-1',
    petName: '小白',
    petBreed: '萨摩耶',
    petAge: '1岁',
    petGender: '公',
    petDescription: '性格温顺，喜欢互动，已接种疫苗，会简单指令',
    petImage: '/assets/g.png',
    adoptStatus: '待领养'
  },
  {
    petId: 'fake-2',
    petName: '花花',
    petBreed: '布偶猫',
    petAge: '8个月',
    petGender: '母',
    petDescription: '颜值超高，粘人乖巧，已绝育，适合家庭饲养',
    petImage: '/assets/d.png',
    adoptStatus: '待领养'
  },
  {
    petId: 'fake-3',
    petName: '阿黄',
    petBreed: '中华田园犬',
    petAge: '2岁',
    petGender: '公',
    petDescription: '忠诚护主，体质好，适应能力强，已完成驱虫',
    petImage: '/assets/e.png',
    adoptStatus: '待领养'
  },
  {
    petId: 'fake-4',
    petName: '拉布拉多',
    petBreed: '拉布拉多',
    petAge: '2岁',
    petGender: '公',
    petDescription: '忠诚护主，体质好，适应能力强，已完成驱虫',
    petImage: '/assets/f.png',
    adoptStatus: '待领养'
  }
])

// 流浪动物信息数据（4个卡片，图片统一用a.png）
const strayPetList = ref([
  {
    strayPetId: 'stray-1',
    strayPetName: '小黑',
    strayPetBreed: '中华田园猫',
    strayPetAge: '10个月',
    strayPetGender: '公',
    strayPetDescription: '流浪街头2个月，性格警惕但亲人，已做基础体检，无传染病',
    strayPetImage: '/assets/L.png',
    strayStatus: '待救助'
  },
  {
    strayPetId: 'stray-2',
    strayPetName: '灰灰',
    strayPetBreed: '柯基串',
    strayPetAge: '1岁半',
    strayPetGender: '母',
    strayPetDescription: '被遗弃在小区门口，温顺亲人，会跟人走，已驱虫',
    strayPetImage: '/assets/h.png',
    strayStatus: '待救助'
  },
  {
    strayPetId: 'stray-3',
    strayPetName: '咪咪',
    strayPetBreed: '橘猫',
    strayPetAge: '6个月',
    strayPetGender: '母',
    strayPetDescription: '流浪幼猫，体型较小，需要细心照顾，已接种第一针疫苗',
    strayPetImage: '/assets/k.png',
    strayStatus: '待领养'
  },
  {
    strayPetId: 'stray-4',
    strayPetName: '大黑',
    strayPetBreed: '德牧串',
    strayPetAge: '3岁',
    strayPetGender: '公',
    strayPetDescription: '流浪狗救助站暂养，护卫意识强，适合有饲养经验的家庭',
    strayPetImage: '/assets/i.png',
    strayStatus: '待领养'
  }
])

// 计算是否显示主页内容
const showStats = computed(() => {
  return route.path.startsWith('/UserLayout')
})

// 轮播图控制
const nextSlide = () => {
  currentSlide.value = (currentSlide.value + 1) % announcements.value.length
}

const prevSlide = () => {
  currentSlide.value = (currentSlide.value - 1 + announcements.value.length) % announcements.value.length
}


onMounted(() => {
  // 自动轮播：增加空数据判断，避免报错
  const interval = setInterval(() => {
    if (announcements.value.length > 0) nextSlide()
  }, 5000)
  
  // 滚动监听
  const handleScroll = () => {
    isScrolled.value = window.scrollY > 10
  }
  window.addEventListener('scroll', handleScroll)
  
  // 滚动动画监听（包含流浪动物模块卡片、寄养流程）
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('visible');
      }
    });
  }, { threshold: 0.1 });
  
  document.querySelectorAll('.animate-on-scroll').forEach(el => {
    observer.observe(el);
  });
  
  // 页面初始化
  addHomeMenuItem()
  getPublicAnnouncements()
  
  // 清理函数
  return () => {
    clearInterval(interval)
    window.removeEventListener('scroll', handleScroll);
    observer.disconnect();
  }
})

// 图片路径处理逻辑
const getImageUrl = (path, type = 'announcement') => {
  // 处理空路径或无效路径
  if (!path || path.trim() === '' || path === 'null' || path === 'undefined') {
    return type === 'pet' ? defaultPetImg : defaultAnnouncementImg;
  }

  // 处理 "/assets/xxx.png" 格式路径（Vue CLI/Vite 兼容）
  if (path.startsWith('/assets/')) {
    try {
      const fileName = path.replace('/assets/', '');
      return new URL(`../assets/${fileName}`, import.meta.url).href;
    } catch (e) {
      console.error('图片加载失败，使用默认图:', path, e);
      return type === 'pet' ? defaultPetImg : defaultAnnouncementImg;
    }
  }

  // 处理绝对路径或网络路径
  if (path.startsWith('http://') || path.startsWith('https://') || path.startsWith('data:image/')) {
    return path;
  }

  // 默认返回兜底图
  return type === 'pet' ? defaultPetImg : defaultAnnouncementImg;
}

// 获取公告信息：增加错误捕获细节
const getPublicAnnouncements = async () => {
  try {
    const result = await getPublicListNoPage()
    if (result?.code === 0 && Array.isArray(result.data)) {
      announcements.value = result.data;
      currentSlide.value = 0;
    } else {
      ElMessage.error('获取公告信息失败：数据格式异常');
      announcements.value = [];
    }
  } catch (error) {
    ElMessage.error('网络请求异常：请检查接口连接');
    console.error('公告接口请求错误:', error);
    announcements.value = [];
  }
}

// 点击外部关闭下拉框
watchEffect(() => {
  const handleClickOutside = (event) => {
    const dropdown = document.querySelector('.user-dropdown');
    const avatar = document.querySelector('.user-avatar');
    if (dropdown && avatar && !dropdown.contains(event.target) && !avatar.contains(event.target)) {
      dropdownVisible.value = false;
    }
  };
  
  document.addEventListener('click', handleClickOutside);
  return () => {
    document.removeEventListener('click', handleClickOutside);
  };
});

const handleCommand = (command) => {
  dropdownVisible.value = false;
  
  if (command === 'logout') {
    ElMessageBox.confirm('确认要退出吗？', '温馨提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        const result = await userInfoStore.removeInfo()
        if (result !== 0) {
          ElMessage.success('退出成功，请重新登录')
          router.push('/login')
        } else {
          ElMessage.error('退出失败：状态清除异常')
        }
      } catch (error) {
        ElMessage.error('退出失败：网络异常')
        console.error('退出登录错误:', error);
      }
    }).catch(() => {
      // 取消退出时不提示
    })
  } else {
    router.push('/user/' + command)
  }
}

// 检查登录状态
const addHomeMenuItem = () => {
  if (!userInfoStore || !userInfoStore.info || Object.keys(userInfoStore.info).length === 0) {
    ElMessage({
      type: 'warning',
      message: '请登录以后访问主页',
      duration: 2000
    })
    setTimeout(() => {
      router.push('/login');
    }, 1000);
  }
}

// 跳转宠物详情页（原有）
const goToPetDetail = (petId) => {
  if (!petId || typeof petId !== 'string') {
    ElMessage.warning('无效的宠物ID');
    return;
  }
  router.push({
    path: '/userPetInformation',
    query: { petId: petId }
  })
}

// 跳转流浪动物详情页
const goToStrayPetDetail = (strayPetId) => {
  if (!strayPetId || typeof strayPetId !== 'string') {
    ElMessage.warning('无效的流浪动物ID');
    return;
  }
  router.push({
    path: '/userPetInformation',
    query: { strayPetId: strayPetId, type: 'stray' }
  })
}

// 快捷申请领养（原有）
const quickAdopt = (pet) => {
  if (!pet || !pet.petId || !pet.petName) {
    ElMessage.warning('宠物信息不完整，无法申请领养');
    return;
  }
  router.push({
    path: '/userAdoption',
    query: { 
      petId: pet.petId,
      petName: pet.petName 
    }
  })
}

// 快捷申请救助流浪动物
const quickRescue = (strayPet) => {
  if (!strayPet || !strayPet.strayPetId || !strayPet.strayPetName) {
    ElMessage.warning('流浪动物信息不完整，无法申请救助');
    return;
  }
  router.push({
    path: '/userAdoption',
    query: { 
      strayPetId: strayPet.strayPetId,
      strayPetName: strayPet.strayPetName,
      type: 'rescue'
    }
  })
}

// 查看更多宠物信息（原有）
const goToPetList = () => {
  router.push('/userPetInformation')
}

// 查看更多流浪动物信息
const goToStrayPetList = () => {
  router.push({
    path: '/userPetInformation',
    query: { type: 'stray' }
  })
}

// 跳转寄养申请页面
const goToBoardingApply = () => {
  router.push('/PleaseUserPetBoarding')
}
</script>

<template>
  <el-container class="user-layout-container">
    <!-- 顶部导航栏 -->
    <el-header class="top-header" :class="{ 'scrolled': isScrolled }">
      <div class="header-content">
        <div class="logo">
          <img 
            :src="getImageUrl('@/assets/favicon.ico', 'announcement')"
            alt="宠物领养中心" 
            @error="(e) => e.target.src = defaultAnnouncementImg"
          />
          <h1>宠物领养中心</h1>
        </div>
        
        <!-- 主导航菜单 -->
        <el-menu mode="horizontal" active-text-color="#ff758c" background-color="transparent" text-color="#663300" router class="main-nav">
          <el-menu-item index="/UserLayout" v-if="userInfoStore?.info" class="nav-item">首页</el-menu-item>
          <el-menu-item 
            index="/userPetInformation" 
            v-if="userInfoStore?.info && ['admin', 'user','volunteer'].includes(userInfoStore.info.userRole)" 
            class="nav-item"
          >
            宠物信息
          </el-menu-item>
          <el-menu-item 
            index="/userAdoption" 
            v-if="userInfoStore?.info && ['admin','user','volunteer'].includes(userInfoStore.info.userRole)" 
            class="nav-item"
          >
            领养申请
          </el-menu-item>
          <el-menu-item 
            index="/userPetSupply" 
            v-if="userInfoStore?.info && ['admin', 'user','volunteer'].includes(userInfoStore.info.userRole)" 
            class="nav-item"
          >
            宠物用品
          </el-menu-item>
          <el-menu-item 
            index="/userOrderItem" 
            v-if="userInfoStore?.info && ['admin', 'user','volunteer'].includes(userInfoStore.info.userRole)" 
            class="nav-item"
          >
            我的购物车
          </el-menu-item>

          <el-menu-item 
            index="/userOrderByUserId" 
            v-if="userInfoStore?.info && ['admin','user','volunteer'].includes(userInfoStore.info.userRole)" 
            class="mobile-menu-item"
          >
            <span>我的订单管理</span>
          </el-menu-item>
          <el-menu-item 
            index="/userPublicInformation" 
            v-if="userInfoStore?.info && ['admin','user','volunteer'].includes(userInfoStore.info.userRole)" 
            class="nav-item"
          >
            公告信息
          </el-menu-item>

          <el-menu-item 
            index="/userVolunteer" 
            v-if="userInfoStore?.info && ['admin','user','volunteer'].includes(userInfoStore.info.userRole)" 
            class="nav-item"
          >
            申请成为志愿者
          </el-menu-item>

          <el-menu-item 
            index="/baiduMap" 
            v-if="userInfoStore?.info && ['admin','user','volunteer'].includes(userInfoStore.info.userRole)" 
            class="nav-item"
          >
            我们的位置
          </el-menu-item>
        </el-menu>
        
        <!-- 右侧功能区 -->
        <div class="header-actions">
          <!-- 用户信息 -->
          <div class="user-info">
            <span class="welcome-text" v-if="userInfoStore?.info">欢迎：<strong>{{ userInfoStore.info.userName }}</strong></span>
            
            <!-- 个人资料下拉框 -->
            <div class="user-dropdown">
              <div class="user-avatar" @click.stop="dropdownVisible = !dropdownVisible">
                <el-avatar :src="BACKEND_BASE+userInfoStore?.info?.avatar ? getImageUrl(BACKEND_BASE+userInfoStore.info.avatar) : defaultAnnouncementImg" :size="36" />
                <el-icon class="caret-icon" :class="{ 'rotate': dropdownVisible }">
                  <CaretBottom />
                </el-icon>
              </div>
              <div class="dropdown-menu" v-show="dropdownVisible" :class="{ 'show': dropdownVisible }">
                <div class="dropdown-item" @click="handleCommand('personal')">
                  <el-icon class="item-icon">
                    <User />
                  </el-icon>
                  <span>基本资料</span>
                </div>
                <div class="dropdown-divider"></div>
                <div class="dropdown-item logout-item" @click="handleCommand('logout')">
                  <el-icon class="item-icon">
                    <SwitchButton />
                  </el-icon>
                  <span>退出登录</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-header>

    <!-- 主内容区域 -->
    <el-main class="content-area">
      <div class="main-content">
        <!-- 公告轮播图 - 仅在有公告数据时显示 -->
        <div class="banner-carousel" v-if="showStats && announcements.length > 0">
          <div class="carousel-container">
            <!-- 轮播项 -->
            <div 
              class="carousel-slide" 
              v-for="(item, index) in announcements" 
              :key="item.publicId"
              :class="{ 'active': index === currentSlide }"
            >
              <div class="slide-content">
                <div class="slide-image">
                  <img 
                    :src="getImageUrl(item.publicImage)" 
                    :alt="item.title"
                    @error="(e) => e.target.src = defaultAnnouncementImg"
                  />
                </div>
                <div class="slide-text">
                  <h2 class="slide-title">{{ item.title }}</h2>
                  <p class="slide-date">发布日期：{{ item.createDate }}</p>
                  <el-button 
                    class="slide-button" 
                    @click.stop="$router.push('/userPublicInformation')"
                  >
                    了解详情
                  </el-button>
                </div>
              </div>
            </div>
            
            <!-- 轮播控制按钮 -->
            <button class="carousel-control prev" @click.stop="prevSlide">
              <span class="control-icon">&lsaquo;</span>
            </button>
            <button class="carousel-control next" @click.stop="nextSlide">
              <span class="control-icon">&rsaquo;</span>
            </button>
            
            <!-- 轮播指示器 -->
            <div class="carousel-indicators">
              <button 
                v-for="(item, index) in announcements" 
                :key="item.publicId"
                @click.stop="currentSlide = index"
                :class="{ 'active': index === currentSlide }"
                :aria-label="`Go to slide ${index + 1}`"
              ></button>
            </div>
          </div>
        </div>
        
        <!-- 快捷链接区域 -->
        <div class="quick-links" v-if="showStats">
          <div class="links-grid">
            <div class="link-card" @click="$router.push('/pleaseUserPetBoarding')">
             
              <div class="link-text">宠物寄养申请</div>
            </div>
            <div class="link-card" @click="$router.push('/userPetReport')">
              
              <div class="link-text">流浪宠物上报</div>
            </div>
            <div class="link-card" @click="$router.push('/userPetSupply')">
              
              <div class="link-text">浏览宠物用品</div>
            </div>
            <div class="link-card" @click="$router.push('/chatComponent')">
              
              <div class="link-text">联系客服助手</div>
            </div>
          </div>
        </div>
        
        <!-- 宠物信息卡片区域（原有） -->
        <div class="pet-cards-container animate-on-scroll" v-if="showStats">
          <div class="section-title">
            <h2>待领养宠物</h2>
            <p>每一个生命都值得被温柔以待</p>
          </div>
          
          <!-- 宠物卡片网格 -->
          <div class="pet-cards-grid">
            <div 
              class="pet-card animate-on-scroll" 
              v-for="pet in fakePetList" 
              :key="pet.petId"
              @click="goToPetDetail(pet.petId)"
            >
              <div class="pet-image-container">
                <img 
                  :src="getImageUrl(pet.petImage, 'pet')" 
                  :alt="pet.petName" 
                  class="pet-image"
                  @error="(e) => e.target.src = defaultPetImg"
                />
                <div class="pet-badge available">
                  {{ pet.adoptStatus }}
                </div>
              </div>
              
              <div class="pet-info">
                <div class="pet-name">{{ pet.petName }}</div>
                <div class="pet-details">
                  <span class="detail-item">{{ pet.petBreed }}</span>
                  <span class="detail-item">{{ pet.petAge }}</span>
                  <span class="detail-item">{{ pet.petGender }}</span>
                </div>
                
                <div class="pet-description">
                  {{ pet.petDescription.length > 60 
                    ? pet.petDescription.substring(0, 60) + '...' 
                    : pet.petDescription 
                  }}
                </div>
                
                <div class="pet-actions">
                  <el-button 
                    size="small" 
                    class="adopt-btn"
                    @click.stop="quickAdopt(pet)"
                  >
                    申请领养
                  </el-button>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 查看更多按钮 -->
          <div class="view-more-container">
            <el-button 
              class="view-more-btn"
              @click="goToPetList"
            >
              查看更多宠物 <ArrowRight class="arrow-icon" />
            </el-button>
          </div>
        </div>
        
        <!-- 流浪动物卡片区域 -->
        <div class="stray-pet-cards-container animate-on-scroll" v-if="showStats">
          <div class="section-title">
            <h2>流浪动物</h2>
            <p>伸出援手，给它们一个温暖的家</p>
          </div>
          
          <!-- 流浪动物卡片网格 -->
          <div class="pet-cards-grid">
            <div 
              class="pet-card animate-on-scroll" 
              v-for="strayPet in strayPetList" 
              :key="strayPet.strayPetId"
              @click="goToStrayPetDetail(strayPet.strayPetId)"
            >
              <div class="pet-image-container">
                <img 
                  :src="getImageUrl(strayPet.strayPetImage, 'pet')" 
                  :alt="strayPet.strayPetName" 
                  class="pet-image"
                  @error="(e) => e.target.src = defaultPetImg"
                />
                <div class="pet-badge" :class="strayPet.strayStatus === '待救助' ? 'rescue' : 'available'">
                  {{ strayPet.strayStatus }}
                </div>
              </div>
              
              <div class="pet-info">
                <div class="pet-name">{{ strayPet.strayPetName }}</div>
                <div class="pet-details">
                  <span class="detail-item">{{ strayPet.strayPetBreed }}</span>
                  <span class="detail-item">{{ strayPet.strayPetAge }}</span>
                  <span class="detail-item">{{ strayPet.strayPetGender }}</span>
                </div>
                
                <div class="pet-description">
                  {{ strayPet.strayPetDescription.length > 60 
                    ? strayPet.strayPetDescription.substring(0, 60) + '...' 
                    : strayPet.strayPetDescription 
                  }}
                </div>
                
                <div class="pet-actions">
                  <el-button 
                    size="small" 
                    class="rescue-btn"
                    @click.stop="quickRescue(strayPet)"
                  >
                    {{ strayPet.strayStatus === '待救助' ? '申请救助' : '申请领养' }}
                  </el-button>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 查看更多按钮 -->
          <div class="view-more-container">
            <el-button 
              class="view-more-btn"
              @click="goToStrayPetList"
            >
              查看更多流浪动物 <ArrowRight class="arrow-icon" />
            </el-button>
          </div>
        </div>
        
        <!-- 新增：寄养宠物流程 -->
        <div class="boarding-process-container animate-on-scroll" v-if="showStats">
          <div class="section-title">
            <h2>一站式寄养宠物 让您随走随行</h2>
            <p>便捷可靠的宠物寄养服务，解决您出行的后顾之忧</p>
          </div>
          
          <section class="boarding-process">
            <div class="process-steps">
              <div class="process-step step-2 animate-on-scroll">
                <div class="step-number">1</div>
                <div class="step-icon">
                  <el-icon><Document /></el-icon>
                </div>
                <h4>提交寄养宠物信息</h4>
                <p>填写宠物基本信息、健康状况和特殊需求</p>
              </div>
              
              <div class="process-connector"></div>

              <div class="process-step step-1 animate-on-scroll">
                <div class="step-number">2</div>
                <div class="step-icon">
                  <el-icon><Calendar /></el-icon>
                </div>
                <h4>提交寄养订单信息</h4>
                <p>选择寄养时间段和服务类型，提交预约申请</p>
              </div>
              
              
              
              <div class="process-connector"></div>
              
              <div class="process-step step-3 animate-on-scroll">
                <div class="step-number">3</div>
                <div class="step-icon">
                  <el-icon><Key /></el-icon>
                </div>
                <h4>送宠入住</h4>
                <p>按约定时间送宠物到寄养中心，完成交接手续</p>
              </div>
              
              <div class="process-connector"></div>
              
              <div class="process-step step-4 animate-on-scroll">
                <div class="step-number">4</div>
                <div class="step-icon">
                  <el-icon><Check /></el-icon>
                </div>
                <h4>接宠回家</h4>
                <p>服务结束后，凭有效证件接回您的爱宠</p>
              </div>
            </div>
            
            <div class="process-action animate-on-scroll">
              <el-button 
                class="start-boarding-btn"
                @click="goToBoardingApply"
              >
                立即申请寄养
              </el-button>
            </div>
          </section>
        </div>
        
        <!-- 路由视图区域 -->
        <div class="router-view-container" v-if="!showStats || $route.meta.showWithStats">
          <router-view></router-view>
        </div>
      </div>
    </el-main>

    <!-- 占位元素：确保内容不足时页脚贴底 -->
    <div class="footer-spacer"></div>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-content">
        <div class="footer-logo">
          <img 
            :src="getImageUrl('@/assets/favicon.ico', 'announcement')"
            alt="宠物领养中心" 
            @error="(e) => e.target.src = defaultAnnouncementImg"
          />
          <h3>宠物领养中心</h3>
        </div>

        <div class="footer-links">
          <div class="link-group">
            <h4>关于我们</h4>
            <ul>
              <li><a href="/UserLayout">中心介绍</a></li>
              <li><a href="/UserLayout">团队成员</a></li>
              <li><a href="/UserLayout">联系方式</a></li>
              <li><a href="/UserLayout">加入我们</a></li>
            </ul>
          </div>

          <div class="link-group">
            <h4>领养服务</h4>
            <ul>
              <li><a href="/UserLayout">领养流程</a></li>
              <li><a href="/UserLayout">常见问题</a></li>
              <li><a href="/UserLayout">领养协议</a></li>
              <li><a href="/UserLayout">后续支持</a></li>
            </ul>
          </div>

          <div class="link-group">
            <h4>帮助中心</h4>
            <ul>
              <li><a href="/UserLayout">宠物护理</a></li>
              <li><a href="/UserLayout">行为训练</a></li>
              <li><a href="/UserLayout">健康咨询</a></li>
              <li><a href="/UserLayout">捐赠支持</a></li>
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
          <a href="/UserLayout" class="social-icon"><el-icon><EditPen /></el-icon></a>
          <a href="/UserLayout" class="social-icon"><el-icon><Open /></el-icon></a>
          <a href="/UserLayout" class="social-icon"><el-icon><Bell /></el-icon></a>
        </div>
      </div>
    </footer>
  </el-container>
</template>

<style lang="scss" scoped>
// 页脚样式
.footer {
  background-color: #fff;
  color: #663300;
  padding: 30px 0;
  font-size: 14px;
  border-top: 1px dashed #ffb6c1;

  .footer-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    gap: 20px;
  }

  .footer-logo {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;

    img {
      height: 40px;
      width: auto;
    }

    h3 {
      font-size: 18px;
      color: #663300;
      margin: 0;
      font-weight: 600;
    }
  }

  .footer-links {
    display: flex;
    gap: 40px;
    flex-wrap: wrap;

    .link-group {
      min-width: 180px;

      h4 {
        font-size: 16px;
        color: #ff758c;
        margin-bottom: 12px;
      }

      ul {
        list-style: none;
        padding: 0;
        margin: 0;

        li {
          margin-bottom: 8px;

          a {
            color: #663300;
            text-decoration: none;
            transition: color 0.2s;

            &:hover {
              color: #ff758c;
              text-decoration: underline;
            }
          }
        }
      }
    }
  }

  .footer-contact {
    display: flex;
    flex-direction: column;
    gap: 12px;

    .contact-item {
      display: flex;
      align-items: center;
      gap: 8px;

      el-icon {
        color: #ff758c;
        font-size: 18px;
      }

      span {
        color: #663300;
      }
    }
  }

  .footer-bottom {
    max-width: 1200px;
    margin: 20px auto 0;
    padding: 0 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-top: 1px solid #ffecf1;
    padding-top: 20px;

    p {
      color: #999;
      margin: 0;
    }

    .social-icons {
      display: flex;
      gap: 16px;

      .social-icon {
        color: #ff758c;
        font-size: 20px;
        transition: transform 0.3s;

        &:hover {
          transform: scale(1.2);
        }
      }
    }
  }
}

// 全局动画定义
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

// 基础布局
.user-layout-container {
  display: flex;
  flex-direction: column;
  background-color: #fff8f8;
  min-height: 100vh; // 保留最小高度为视口高度，避免内容过少时页脚上移
}

// 占位元素：确保内容不足时页脚贴底
.footer-spacer {
  flex: 1; // 自动填充剩余空间，内容不足时将页脚顶到底部
}

// 顶部导航栏样式
.top-header {
  padding: 0 24px;
  height: 64px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(255, 182, 193, 0.2);
  z-index: 10;
  transition: all 0.3s ease;

  .header-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 100%;
  }

  &.scrolled {
    height: 56px;
    box-shadow: 0 4px 12px rgba(255, 182, 193, 0.3);
  }

  .logo {
    display: flex;
    align-items: center;
    gap: 12px;

    img {
      height: 40px;
      width: auto;
    }

    h1 {
      font-size: 18px;
      color: #663300;
      margin: 0;
      font-weight: 600;
    }
  }

  .main-nav {
    flex: 1;
    margin: 0 20px;

    .nav-item {
      font-size: 14px;
      padding: 0 20px;
      height: 64px;
      line-height: 64px;
      transition: all 0.2s;

      &:hover {
        background-color: #fff0f0;
      }

      &.is-active {
        color: #ff758c;
        font-weight: 500;
      }
    }
  }

  .header-actions {
    display: flex;
    align-items: center;
    gap: 20px;
  }

  .search-box {
    position: relative;

    .search-input {
      width: 200px;
      background-color: #fff0f0;
      border-color: #ffb6c1;
      transition: all 0.3s;

      &:focus {
        width: 240px;
        background-color: #fff;
        border-color: #ff758c;
        box-shadow: none;
      }
    }
  }

  .user-info {
    display: flex;
    align-items: center;
    gap: 16px;
    color: #663300;

    .welcome-text {
      font-size: 14px;
      display: none;

      @media (min-width: 1024px) {
        display: block;
      }

      strong {
        color: #ff758c;
      }
    }
  }

  .mobile-menu-btn {
    display: none;
    cursor: pointer;
    color: #ff758c;

    @media (max-width: 768px) {
      display: block;
    }
  }

  .mobile-menu {
    position: absolute;
    top: 64px;
    left: 0;
    right: 0;
    background-color: #fff8f8;
    box-shadow: 0 4px 12px rgba(255, 182, 193, 0.3);
    z-index: 100;
    transition: all 0.3s;

    .mobile-menu-item {
      padding: 12px 24px;

      .el-icon {
        color: #ff758c;
        margin-right: 8px;
      }
    }
  }
}

// 主内容区域样式
.content-area {
  flex: 1; // 让主内容区自动填充剩余空间
  padding: 0;
  background-color: #fff8f8;
  scroll-behavior: smooth;

  &::-webkit-scrollbar {
    width: 8px;
    height: 8px;
  }

  &::-webkit-scrollbar-track {
    background: #fff0f0;
    border-radius: 4px;
  }

  &::-webkit-scrollbar-thumb {
    background: #ffb6c1;
    border-radius: 4px;
    
    &:hover {
      background: #ff758c;
    }
  }

  .main-content {
    max-width: 1200px;
    margin: 0 auto;
    width: 100%;
    padding: 0 15px;
  }

  // 轮播图样式
  .banner-carousel {
    width: 100%;
    overflow: hidden;
    position: relative;
    margin: 20px auto;
    border-radius: 12px;
    box-shadow: 0 8px 24px rgba(255, 182, 193, 0.15);

    .carousel-container {
      position: relative;
      width: 100%;
      height: 500px;

      @media (max-width: 768px) {
        height: 350px;
      }
    }

    .carousel-slide {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      opacity: 0;
      transition: opacity 0.8s ease;

      &.active {
        opacity: 1;
        z-index: 1;
        position: relative;
      }

      .slide-content {
        display: flex;
        flex-direction: column;
        height: 100%;
        background-color: #fff;
        border-radius: 12px;
        overflow: hidden;
      }

      .slide-image {
        width: 100%;
        height: 75%;
        overflow: hidden;

        @media (max-width: 768px) {
          height: 65%;
        }

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          transition: transform 8s ease;

          &:hover {
            transform: scale(1.05);
          }
        }
      }

      .slide-text {
        padding: 16px 24px;
        text-align: center;
        height: 25%;
        display: flex;
        flex-direction: column;
        justify-content: center;
        background-color: #fff;

        .slide-title {
          font-size: 18px;
          font-weight: 600;
          color: #663300;
          margin: 0 0 8px 0;
          line-height: 1.3;
        }

        .slide-date {
          font-size: 12px;
          color: #999;
          margin-bottom: 12px;
        }

        .slide-button {
          background-color: #ff758c;
          color: #fff;
          border-color: #ff758c;
          padding: 6px 16px;
          font-size: 13px;
          border-radius: 4px;
          cursor: pointer;
          transition: all 0.3s;
          align-self: center;
          width: auto;

          &:hover {
            background-color: #ff5c7c;
            border-color: #ff5c7c;
            transform: translateY(-2px);
          }
        }
      }
    }

    .carousel-control {
      position: absolute;
      top: 50%;
      transform: translateY(-50%);
      background-color: rgba(255, 255, 255, 0.8);
      border: 1px solid #ffb6c1;
      width: 40px;
      height: 40px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      z-index: 2;
      transition: all 0.3s;

      &:hover {
        background-color: #fff;
        box-shadow: 0 2px 8px rgba(255, 117, 140, 0.3);
        color: #ff758c;
        transform: translateY(-50%) scale(1.1);
      }

      .control-icon {
        font-size: 20px;
        color: #663300;
      }
    }

    .prev {
      left: 20px;
    }

    .next {
      right: 20px;
    }

    .carousel-indicators {
      position: absolute;
      bottom: 15px;
      left: 0;
      right: 0;
      display: flex;
      justify-content: center;
      gap: 8px;
      z-index: 2;

      button {
        width: 10px;
        height: 10px;
        border-radius: 50%;
        border: 1px solid #ffb6c1;
        background-color: rgba(255, 255, 255, 0.5);
        cursor: pointer;
        transition: all 0.3s;

        &.active {
          background-color: #ff758c;
          width: 30px;
          border-radius: 5px;
          border-color: #ff758c;
        }
      }
    }
  }

  // 快捷链接区域样式
  .quick-links {
    padding: 0 20px 30px;

    .links-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 16px;
      max-width: 900px;
      margin: 0 auto;

      @media (max-width: 768px) {
        grid-template-columns: repeat(2, 1fr);
        gap: 12px;
      }

      .link-card {
        background-color: #fff;
        border-radius: 10px;
        padding: 18px 12px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        text-align: center;
        cursor: pointer;
        transition: all 0.3s;
        box-shadow: 0 3px 10px rgba(255, 182, 193, 0.1);
        border: 1px solid #ffecf1;
        position: relative;
        overflow: hidden;

        &::after {
          content: '';
          position: absolute;
          bottom: 0;
          left: 0;
          width: 100%;
          height: 3px;
          background-color: #ff758c;
          transform: scaleX(0);
          transition: transform 0.3s ease;
        }

        &:hover {
          transform: translateY(-5px);
          box-shadow: 0 10px 20px rgba(255, 117, 140, 0.15);
          border-color: #ffb6c1;

          &::after {
            transform: scaleX(1);
          }
        }

        .link-icon {
          font-size: 28px;
          color: #ff758c;
          margin-bottom: 12px;
          transition: transform 0.3s;
        }

        &:hover .link-icon {
          transform: scale(1.1) rotate(5deg);
        }

        .link-text {
          font-size: 14px;
          color: #663300;
          font-weight: 500;
        }
      }
    }
  }

  // 宠物卡片区域样式（原有和新增共用）
  .pet-cards-container, .stray-pet-cards-container, .boarding-process-container {
    padding: 20px 0 40px;
    margin-top: 10px;

    .section-title {
      text-align: center;
      margin-bottom: 30px;

      h2 {
        font-size: 24px;
        color: #663300;
        margin-bottom: 8px;
        font-weight: 600;
      }

      p {
        color: #999;
        font-size: 14px;
        margin: 0;
      }
    }

    .pet-cards-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: 24px;
      margin-bottom: 30px;
    }

    .pet-card {
      background-color: #fff;
      border-radius: 12px;
      overflow: hidden;
      box-shadow: 0 5px 15px rgba(255, 182, 193, 0.1);
      transition: all 0.3s ease;
      cursor: pointer;
      border: 1px solid #ffecf1;
      opacity: 0;
      transform: translateY(20px);
      transition: all 0.5s ease;

      &.visible {
        opacity: 1;
        transform: translateY(0);
      }

      &:nth-child(2n) {
        transition-delay: 0.1s;
      }

      &:nth-child(3n) {
        transition-delay: 0.2s;
      }

      &:hover {
        transform: translateY(-8px);
        box-shadow: 0 12px 20px rgba(255, 117, 140, 0.15);
        border-color: #ffb6c1;
      }

      .pet-image-container {
        position: relative;
        height: 200px;
        overflow: hidden;

        .pet-image {
          width: 100%;
          height: 100%;
          object-fit: cover;
          transition: transform 0.5s ease;
        }

        &:hover .pet-image {
          transform: scale(1.08);
        }

        .pet-badge {
          position: absolute;
          top: 12px;
          right: 12px;
          padding: 4px 10px;
          border-radius: 12px;
          font-size: 12px;
          font-weight: 500;
          color: #fff;

          &.available {
            background-color: #4cd964;
          }
          
          &.rescue {
            background-color: #ff9500;
          }

          &.adopted {
            background-color: #ff9500;
          }
        }
      }

      .pet-info {
        padding: 16px;

        .pet-name {
          font-size: 18px;
          font-weight: 600;
          color: #663300;
          margin-bottom: 8px;
          display: flex;
          justify-content: space-between;
          align-items: center;
        }

        .pet-details {
          display: flex;
          flex-wrap: wrap;
          gap: 8px;
          margin-bottom: 12px;

          .detail-item {
            font-size: 12px;
            color: #999;
            background-color: #fff0f0;
            padding: 2px 8px;
            border-radius: 4px;
          }
        }

        .pet-description {
          font-size: 13px;
          color: #666;
          line-height: 1.5;
          margin-bottom: 16px;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .pet-actions {
          display: flex;
          justify-content: center;

          .adopt-btn, .rescue-btn {
            width: 100%;
            transition: all 0.3s;
          }
          
          .adopt-btn {
            background-color: #ff758c;
            color: #fff;
            border-color: #ff758c;
            
            &:hover {
              background-color: #ff5c7c;
              border-color: #ff5c7c;
            }
          }
          
          .rescue-btn {
            background-color: #ff9500;
            color: #fff;
            border-color: #ff9500;
            
            &:hover {
              background-color: #fa8907;
              border-color: #fa8907;
            }
          }
        }
      }
    }

    .view-more-container {
      text-align: center;
      margin-top: 10px;

      .view-more-btn {
        background-color: transparent;
        color: #ff758c;
        border-color: #ff758c;
        padding: 8px 24px;
        border-radius: 20px;
        transition: all 0.3s;

        &:hover {
          background-color: #ff758c;
          color: #fff;
        }

        .arrow-icon {
          margin-left: 6px;
          font-size: 14px;
        }
      }
    }
  }

  // 寄养流程样式
  .boarding-process {
    max-width: 1000px;
    margin: 0 auto;
    padding: 30px 20px;
    background-color: #fff;
    border-radius: 12px;
    box-shadow: 0 5px 20px rgba(255, 182, 193, 0.1);
    border: 1px solid #ffecf1;

    .process-steps {
      display: flex;
      justify-content: space-between;
      position: relative;
      margin-bottom: 40px;

      @media (max-width: 768px) {
        flex-direction: column;
        gap: 30px;
      }

      .process-connector {
        position: absolute;
        top: 30px;
        left: 100px;
        right: 100px;
        height: 2px;
        background-color: #ffb6c1;
        z-index: 1;

        @media (max-width: 768px) {
          width: 2px;
          height: calc(100% - 60px);
          top: 60px;
          left: 30px;
          right: auto;
        }
      }

      .process-step {
        flex: 1;
        text-align: center;
        position: relative;
        z-index: 2;
        opacity: 0;
        transform: translateY(20px);
        transition: all 0.5s ease;

        &.visible {
          opacity: 1;
          transform: translateY(0);
        }

        &.step-1 { transition-delay: 0.1s; }
        &.step-2 { transition-delay: 0.2s; }
        &.step-3 { transition-delay: 0.3s; }
        &.step-4 { transition-delay: 0.4s; }

        .step-number {
          width: 60px;
          height: 60px;
          border-radius: 50%;
          background-color: #fff0f0;
          color: #ff758c;
          border: 2px solid #ffb6c1;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 20px;
          font-weight: bold;
          margin: 0 auto 15px;
          transition: all 0.3s;

          &:hover {
            background-color: #ff758c;
            color: #fff;
            transform: scale(1.1);
          }
        }

        .step-icon {
          font-size: 30px;
          color: #ff758c;
          margin-bottom: 15px;
        }

        h4 {
          color: #663300;
          margin-bottom: 8px;
          font-size: 16px;
        }

        p {
          color: #999;
          font-size: 13px;
          max-width: 200px;
          margin: 0 auto;
        }
      }
    }

    .process-action {
      text-align: center;
      margin-top: 20px;
      opacity: 0;
      transform: translateY(20px);
      transition: all 0.5s ease 0.5s;

      &.visible {
        opacity: 1;
        transform: translateY(0);
      }

      .start-boarding-btn {
        background-color: #ff758c;
        color: #fff;
        border-color: #ff758c;
        padding: 10px 30px;
        font-size: 16px;
        border-radius: 30px;
        transition: all 0.3s;

        &:hover {
          background-color: #ff5c7c;
          border-color: #ff5c7c;
          transform: translateY(-3px);
          box-shadow: 0 5px 15px rgba(255, 117, 140, 0.3);
        }
      }
    }
  }

  // 路由视图容器样式
  .router-view-container {
    padding: 20px;
    border: 2px dashed #ffb6c1;
    border-radius: 12px;
    background: linear-gradient(135deg, #fff8f8 0%, #ffecf1 100%);
    min-height: 300px;
    margin: 20px;
  }
}

// 用户下拉菜单样式
.user-dropdown {
  position: relative;

  .user-avatar {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    padding: 4px 8px;
    border-radius: 24px;
    background-color: #fff;
    box-shadow: 0 2px 5px rgba(255, 182, 193, 0.2);
    transition: all 0.3s ease;

    &:hover {
      background-color: #fff0f0;
      transform: translateY(-2px);
    }

    .el-avatar {
      width: 36px;
      height: 36px;
      border: 2px solid #ffb6c1;
    }

    .caret-icon {
      transition: transform 0.3s;
      color: #ff758c;

      &.rotate {
        transform: rotate(180deg);
      }
    }
  }

  .dropdown-menu {
    position: absolute;
    top: 100%;
    right: 0;
    min-width: 180px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(255, 182, 193, 0.2);
    padding: 8px 0;
    margin-top: 8px;
    z-index: 100;
    opacity: 0;
    visibility: hidden;
    transform: translateY(10px);
    transition: all 0.2s;
    border: 1px solid #ffecf1;

    &.show {
      opacity: 1;
      visibility: visible;
      transform: translateY(0);
    }

    .dropdown-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 10px 16px;
      color: #663300;
      cursor: pointer;
      transition: background-color 0.2s;

      &:hover {
        background-color: #fff0f0;
        color: #ff758c;
      }

      .item-icon {
        color: #ff758c;
        font-size: 16px;
      }
    }

    .dropdown-divider {
      height: 1px;
      background-color: #ffecf1;
      margin: 4px 0;
    }

    .logout-item {
      color: #f5222d;

      .item-icon {
        color: #f5222d;
      }
    }
  }
}


</style>
