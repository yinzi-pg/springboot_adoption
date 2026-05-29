<script setup>
import {
  Management,
  Promotion,
  UserFilled,
  User,
  Crop,
  EditPen,
  SwitchButton,
  CaretBottom,
  Bell,
  ShoppingBag,
  Help,
  Opportunity,
  Unlock,
  Chicken,
  HomeFilled,
  Lock,
  ShoppingCart,
  ArrowUp,
  ArrowDown
} from '@element-plus/icons-vue'

import avatar from '@/assets/b.png'
import { useUserInfoStore } from '@/stores/user.js'
import { useRouter, useRoute } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { ref, watchEffect, computed } from 'vue';
const BACKEND_BASE = 'http://localhost:8080/springboot_adoption';
const router = useRouter()
const route = useRoute()
const userInfoStore = useUserInfoStore()
const dropdownVisible = ref(false)

// 计算是否显示主页统计数据（仅在访问根路径时显示）
const showStats = computed(() => {
  return route.path === '/layout'
})

// 点击外部关闭下拉框
watchEffect(() => {
  const handleClickOutside = (event) => {
    const dropdown = document.querySelector('.user-dropdown');
    if (dropdown && !dropdown.contains(event.target)) {
      dropdownVisible.value = false;
    }
  };
  
  document.addEventListener('click', handleClickOutside);
  return () => {
    document.removeEventListener('click', handleClickOutside);
  };
});

// 模拟统计数据
const statsData = {
  adoptionPending: 128,
  adoptionPendingChange: 12,
  suppliesSales: 2560,
  suppliesSalesChange: 8,
  reportedPets: 63,
  reportedPetsChange: 5,
  boardingCount: 42,
  boardingCountChange: -3,
  adoptionTrend: [
    { month: '1月', count: 32 },
    { month: '2月', count: 28 },
    { month: '3月', count: 45 },
    { month: '4月', count: 52 },
    { month: '5月', count: 68 },
    { month: '6月', count: 72 }
  ],
  petTypeDistribution: [
    { name: '猫', value: 45, percentage: '45%' },
    { name: '狗', value: 35, percentage: '35%' },
    { name: '其他', value: 20, percentage: '20%' }
  ],
  recentAdoptions: [
    { id: 1, petName: '小白', type: '猫', adoptDate: '2025-09-01', adopter: '张三' },
    { id: 2, petName: '大黄', type: '狗', adoptDate: '2025-08-28', adopter: '李四' },
    { id: 3, petName: '咪咪', type: '猫', adoptDate: '2025-08-25', adopter: '王五' },
    { id: 4, petName: '小黑', type: '狗', adoptDate: '2025-08-20', adopter: '赵六' }
  ]
}

// 计算趋势图的最大高度百分比
const getBarHeight = (value) => {
  const maxValue = Math.max(...statsData.adoptionTrend.map(item => item.count));
  const height = Math.max((value / maxValue) * 100, 5);
  return `${height}%`;
}

const handleCommand = (command) => {
  dropdownVisible.value = false; // 关闭下拉框
  
  if (command === 'logout') {
    ElMessageBox.confirm('确认要退出吗？', '温馨提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      const result = await userInfoStore.removeInfo()
      if (result != 0) {
        ElMessage.success('退出成功，请重新登录')
        router.push('/login')
      } else {
        ElMessage.error('退出失败')
      }
    })
  } else {
    router.push('/user/' + command)
  }
}

// 添加首页路由项
const addHomeMenuItem = () => {
  if (!userInfoStore.info || Object.keys(userInfoStore.info).length === 0) {
    ElMessage({
      type: 'warning',
      message: '请登录以后访问主页',
    })
    router.push('/login');
  }
}

// 页面加载时检查登录状态
addHomeMenuItem()
</script>

<template>
  <el-container class="layout-container">
    <!-- 左侧菜单 -->
    <el-aside width="200px">
      <div class="el-aside__logo"></div>
      <el-menu active-text-color="#ffd04b" background-color="#309bcc" text-color="#fff" router>
        <!-- 添加首页菜单项 -->
        <el-menu-item index="/layout" v-if="userInfoStore.info">
          <el-icon>
            <HomeFilled />
          </el-icon>
          <span>首页</span>
        </el-menu-item>
        
        <!-- 其他菜单项保持不变 -->
        <el-menu-item index="/petinformation" v-if="userInfoStore.info && ['admin','volunteer'].includes(userInfoStore.info.userRole)">
          <el-icon>
            <Bell />
          </el-icon>
          <span>宠物信息管理</span>
        </el-menu-item>
        <el-menu-item index="/userPetInformation" v-if="userInfoStore.info && ['user'].includes(userInfoStore.info.userRole)">
          <el-icon>
            <Bell />
          </el-icon>
          <span>宠物信息展示</span>
        </el-menu-item>
        <el-menu-item index="/adoption" v-if="userInfoStore.info && ['admin','volunteer'].includes(userInfoStore.info.userRole)">
          <el-icon>
            <Help />
          </el-icon>
          <span>宠物领养管理</span>
        </el-menu-item>
        <el-menu-item index="/userAdoption" v-if="userInfoStore.info && ['user','volunteer'].includes(userInfoStore.info.userRole)">
          <el-icon>
            <Help />
          </el-icon>
          <span>宠物领养申请</span>
        </el-menu-item>

        <el-menu-item index="/petBoarding" v-if="userInfoStore.info && userInfoStore.info.userRole === 'admin'">
          <el-icon>
            <HomeFilled />
          </el-icon>
          <span>宠物寄养信息管理</span>
        </el-menu-item>

        <el-menu-item index="/userPetBoardingManage" v-if="userInfoStore.info && ['admin','volunteer'].includes(userInfoStore.info.userRole)">
          <el-icon>
            <HomeFilled />
          </el-icon>
          <span>用户宠物寄养信息管理</span>
        </el-menu-item>  

        <el-menu-item index="/PleaseUserPetBoarding" v-if="userInfoStore.info && ['user','volunteer'].includes(userInfoStore.info.userRole)">
          <el-icon>
            <HomeFilled />
          </el-icon>
          <span>寄养宠物申请</span>
        </el-menu-item>

        <el-menu-item index="/petSupplies" v-if="userInfoStore.info && ['admin','volunteer'].includes(userInfoStore.info.userRole)">
          <el-icon>
            <Promotion />
          </el-icon>
          <span>宠物用品管理</span>
        </el-menu-item>

        <el-menu-item index="/userPetSupply" v-if="userInfoStore.info && ['user','volunteer'].includes(userInfoStore.info.userRole)">
          <el-icon>
            <Promotion />
          </el-icon>
          <span>宠物用品</span>
        </el-menu-item>

        <el-menu-item index="/userOrderByUserId" v-if="userInfoStore.info && ['admin','user'].includes(userInfoStore.info.userRole)">
          <el-icon>
            <ShoppingBag />
          </el-icon>
          <span>我的订单管理</span>
        </el-menu-item>

        <el-menu-item index="/orderManage" v-if="userInfoStore.info && ['admin'].includes(userInfoStore.info.userRole)">
          <el-icon>
            <ShoppingBag />
          </el-icon>
          <span>订单管理</span>
        </el-menu-item>

        <el-menu-item index="/userOrderItem" v-if="userInfoStore.info && ['admin','user'].includes(userInfoStore.info.userRole)">
          <el-icon>
            <ShoppingCart />
          </el-icon>
          <span>购物车</span>
        </el-menu-item>

        <el-menu-item index="/userAnimalReport" v-if="userInfoStore.info && ['volunteer','user'].includes(userInfoStore.info.userRole)">
          <el-icon>
            <Chicken />
          </el-icon>
          <span>流浪动物信息</span>
        </el-menu-item>

        <el-menu-item index="/petsReport" v-if="userInfoStore.info && ['admin','volunteer'].includes(userInfoStore.info.userRole)">
          <el-icon>
            <Chicken />
          </el-icon>
          <span>流浪宠物上报管理</span>
        </el-menu-item>

        <el-menu-item index="/userPetReport" v-if="userInfoStore.info && ['volunteer','user'].includes(userInfoStore.info.userRole)">
          <el-icon>
            <Chicken />
          </el-icon>
          <span>流浪宠物上报</span>
        </el-menu-item>

        <el-menu-item index="/volunteer" v-if="userInfoStore.info && userInfoStore.info.userRole === 'admin'">
          <el-icon>
            <UserFilled />
          </el-icon>
          <span>志愿者信息管理</span>
        </el-menu-item>

        <el-menu-item index="/userInformation" v-if="userInfoStore.info && userInfoStore.info.userRole === 'admin'">
          <el-icon>
            <UserFilled />
          </el-icon>
          <span>用户信息管理</span>
        </el-menu-item>

        <el-menu-item index="/role" v-if="userInfoStore.info && userInfoStore.info.userRole === 'admin'">
          <el-icon>
            <Unlock />
          </el-icon>
          <span>角色管理</span>
        </el-menu-item>

        <el-menu-item index="/publicInformation" v-if="userInfoStore.info && userInfoStore.info.userRole === 'admin'">
          <el-icon>
            <User />
          </el-icon>
          <span>公告信息管理</span>
        </el-menu-item>
        <el-menu-item index="/userPublicInformation" v-if="userInfoStore.info && ['volunteer','user'].includes(userInfoStore.info.userRole)">
          <el-icon>
            <User />
          </el-icon>
          <span>公告信息</span>
        </el-menu-item>
        <el-menu-item index="/chatComponent" v-if="userInfoStore.info && ['volunteer','user'].includes(userInfoStore.info.userRole)">
          <el-icon>
            <Bell />
          </el-icon>
          <span>AI客服助手</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <!-- 右侧主区域 -->
    <el-container>
      <!-- 头部区域 -->
      <el-header>
        <div>欢迎加入拯救流浪小可爱之家：<strong>{{userInfoStore.info.userName}}</strong></div>
        <!-- 个人资料下拉框 -->
        <div class="user-dropdown">
          <div class="user-avatar" @click.stop="dropdownVisible = !dropdownVisible">
            <el-avatar :src="BACKEND_BASE+userInfoStore.info.avatar || '@/assets/default.png'" />
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
      </el-header>
      <!-- 中间区域 -->
      <el-main>
        <div class="main-content">
          <!-- 仅在访问首页时显示统计数据 -->
          <div class="stats-container" v-if="showStats">
            <!-- 统计卡片行 -->
            <el-row :gutter="20">
              <el-col :span="6">
                <el-card class="stat-card" shadow="hover">
                  <div class="card-header">
                    <el-icon color="#ff758c"><Bell /></el-icon>
                    <span>待领养宠物数</span>
                  </div>
                  <div class="card-value">{{ statsData.adoptionPending }}</div>
                  <div class="card-desc">
                    较上月
                    <span class="text-green" v-if="statsData.adoptionPendingChange > 0">
                      <ArrowUp size="14" /> {{ statsData.adoptionPendingChange }}%
                    </span>
                    <span class="text-red" v-else>
                      <ArrowDown size="14" /> {{ Math.abs(statsData.adoptionPendingChange) }}%
                    </span>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card class="stat-card" shadow="hover">
                  <div class="card-header">
                    <el-icon color="#ff758c"><ShoppingBag /></el-icon>
                    <span>宠物用品销量</span>
                  </div>
                  <div class="card-value">{{ statsData.suppliesSales }}</div>
                  <div class="card-desc">
                    较上月
                    <span class="text-green" v-if="statsData.suppliesSalesChange > 0">
                      <ArrowUp size="14" /> {{ statsData.suppliesSalesChange }}%
                    </span>
                    <span class="text-red" v-else>
                      <ArrowDown size="14" /> {{ Math.abs(statsData.suppliesSalesChange) }}%
                    </span>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card class="stat-card" shadow="hover">
                  <div class="card-header">
                    <el-icon color="#ff758c"><Chicken /></el-icon>
                    <span>流浪宠物上报数</span>
                  </div>
                  <div class="card-value">{{ statsData.reportedPets }}</div>
                  <div class="card-desc">
                    较上月
                    <span class="text-green" v-if="statsData.reportedPetsChange > 0">
                      <ArrowUp size="14" /> {{ statsData.reportedPetsChange }}%
                    </span>
                    <span class="text-red" v-else>
                      <ArrowDown size="14" /> {{ Math.abs(statsData.reportedPetsChange) }}%
                    </span>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card class="stat-card" shadow="hover">
                  <div class="card-header">
                    <el-icon color="#ff758c"><HomeFilled /></el-icon>
                    <span>寄养宠物数</span>
                  </div>
                  <div class="card-value">{{ statsData.boardingCount }}</div>
                  <div class="card-desc">
                    较上月
                    <span class="text-green" v-if="statsData.boardingCountChange > 0">
                      <ArrowUp size="14" /> {{ statsData.boardingCountChange }}%
                    </span>
                    <span class="text-red" v-else>
                      <ArrowDown size="14" /> {{ Math.abs(statsData.boardingCountChange) }}%
                    </span>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            
            <!-- 图表行 -->
            <el-row :gutter="20" style="margin-top: 20px;">
              <el-col :span="12">
                <el-card shadow="hover" class="chart-card">
                  <div class="card-title">宠物领养趋势</div>
                  <div class="trend-chart">
                    <!-- Y轴刻度 -->
                    <div class="chart-axis-y">
                      <div v-for="i in 5" :key="i" class="y-tick">
                        {{ Math.round((i * 20) / 100 * Math.max(...statsData.adoptionTrend.map(item => item.count))) }}
                      </div>
                    </div>
                    <div class="chart-content">
                      <div class="chart-axis-x">
                        <div v-for="(item, index) in statsData.adoptionTrend" :key="index" class="chart-label">
                          {{ item.month }}
                        </div>
                      </div>
                      <div class="chart-bars">
                        <div v-for="(item, index) in statsData.adoptionTrend" :key="index" class="chart-bar-container">
                          <div class="chart-bar" :style="{ height: getBarHeight(item.count) }">
                            <div class="chart-bar-value">{{ item.count }}</div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="12">
                <el-card shadow="hover" class="chart-card">
                  <div class="card-title">宠物类型分布</div>
                  <div class="pie-chart-container">
                    <div class="pie-chart">
                      <div class="pie-segment cat-segment"></div>
                      <div class="pie-segment dog-segment"></div>
                      <div class="pie-segment other-segment"></div>
                      <div class="pie-center">总计</div>
                    </div>
                    <div class="pie-legend">
                      <div class="legend-item">
                        <span class="legend-color cat-color"></span>
                        <span>{{ statsData.petTypeDistribution[0].name }} {{ statsData.petTypeDistribution[0].percentage }}</span>
                      </div>
                      <div class="legend-item">
                        <span class="legend-color dog-color"></span>
                        <span>{{ statsData.petTypeDistribution[1].name }} {{ statsData.petTypeDistribution[1].percentage }}</span>
                      </div>
                      <div class="legend-item">
                        <span class="legend-color other-color"></span>
                        <span>{{ statsData.petTypeDistribution[2].name }} {{ statsData.petTypeDistribution[2].percentage }}</span>
                      </div>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            
            <!-- 最近领养记录 -->
            <el-row style="margin-top: 20px;">
              <el-col :span="24">
                <el-card shadow="hover" class="recent-activity-card">
                  <div class="card-title">最近领养记录</div>
                  <el-table :data="statsData.recentAdoptions" border style="width: 100%">
                    <el-table-column prop="id" label="ID" width="80" />
                    <el-table-column prop="petName" label="宠物名称" />
                    <el-table-column prop="type" label="类型" />
                    <el-table-column prop="adoptDate" label="领养日期" />
                    <el-table-column prop="adopter" label="领养人" />
                  </el-table>
                </el-card>
              </el-col>
            </el-row>
          </div>
          
          <!-- 路由视图区域 - 始终显示，当访问首页时会和统计数据一起显示 -->
          <div class="router-view-container" v-if="!showStats || $route.meta.showWithStats">
            <router-view></router-view>
          </div>
        </div>
      </el-main>
      <!-- 底部版权信息 -->
        <el-footer class="page-footer">
            宠物领养中心 ©2025 让每一个生命都被温柔以待
        </el-footer>
    </el-container>
  </el-container>
</template>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
  overflow: hidden;

  .el-aside {
    background-color: #ffe6ec;

    &__logo {
      height: 120px;
      background: url('@/assets/recommend-optimized.png') no-repeat center / 120px auto;
      border-bottom: 2px dashed #ffb6c1;
    }

    .el-menu {
      border-right: none;
      background-color: transparent;

      .el-menu-item {
        &:hover {
          background-color: #ffd0d0 !important;
        }
        .el-icon {
          color: #ff758c;
          margin-right: 8px;
        }
        span {
          color: #663300;
          font-weight: 500;
        }
      }
    }
  }

  .el-container {
    .el-header {
      background-color: #fff8f8;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 20px;
      box-shadow: 0 2px 8px rgba(255, 182, 193, 0.2);

      div:first-child {
        color: #663300;
        font-size: 16px;
        font-weight: bold;
        display: flex;
        align-items: center;

        strong {
          color: #ff758c;
          margin-left: 8px;
        }
      }

      // 个人资料下拉框样式
      .user-dropdown {
        position: relative;
        z-index: 100;

        .user-avatar {
          display: flex;
          align-items: center;
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
            transition: all 0.3s ease;
            
            &:hover {
              transform: scale(1.05);
            }
          }

          .caret-icon {
            color: #ff758c;
            margin-left: 8px;
            font-size: 16px;
            transition: transform 0.3s ease;

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
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          padding: 8px 0;
          margin-top: 8px;
          opacity: 0;
          visibility: hidden;
          transform: translateY(10px);
          transition: all 0.3s ease;
          border: 1px solid #ffecf1;

          &.show {
            opacity: 1;
            visibility: visible;
            transform: translateY(0);
          }

          .dropdown-item {
            display: flex;
            align-items: center;
            padding: 10px 16px;
            color: #663300;
            cursor: pointer;
            transition: all 0.2s ease;

            &:hover {
              background-color: #fff0f0;
              color: #ff758c;
            }

            .item-icon {
              margin-right: 8px;
              font-size: 16px;
            }
          }

          .dropdown-divider {
            height: 1px;
            margin: 4px 0;
            background-color: #ffecf1;
          }

          .logout-item {
            color: #f44336;

            &:hover {
              background-color: #fff0f0;
              color: #d32f2f;
            }
          }
        }
      }
    }

    .el-main {
      padding: 20px;
      background-color: #fff8f8;
      overflow-y: auto;
      height: calc(100vh - 140px);

      .main-content {
        width: 100%;
      }

      .stat-card {
        background-color: #fff;
        border-radius: 8px;
        padding: 16px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
        transition: transform 0.3s ease, box-shadow 0.3s ease;
        
        &:hover {
          transform: translateY(-5px);
          box-shadow: 0 5px 15px rgba(255, 117, 140, 0.15);
        }
      }

      .router-view-container {
        width: 100%;
        border: 2px dashed #ffb6c1;
        border-radius: 12px;
        padding: 20px;
        background: linear-gradient(135deg, #fff8f8 0%, #ffecf1 100%);
        min-height: 300px;
        margin-top: 20px;
        
        &::before {
          content: '';
          position: absolute;
          top: 20px;
          right: 20px;
          width: 40px;
          height: 40px;
          background: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%23ffb6c1"><path d="M12 2C8 2 4 6 4 10c0 4 4 8 8 8s8-4 8-8c0-4-4-8-8-8zm0 14c-3.3 0-6-2.7-6-6s2.7-6 6-6 6 2.7 6 6-2.7 6-6 6z"/></svg>') no-repeat center;
          opacity: 0.3;
        }
      }

      // 其他样式保持不变...
      .card-header {
        display: flex;
        align-items: center;
        margin-bottom: 12px;
      }

      .card-header .el-icon {
        margin-right: 8px;
        font-size: 20px;
      }

      .card-header span {
        font-size: 14px;
        color: #663300;
      }

      .card-value {
        font-size: 28px;
        font-weight: bold;
        color: #ff758c;
        margin-bottom: 8px;
        line-height: 1.2;
      }

      .card-desc {
        font-size: 12px;
        color: #999;
        display: flex;
        align-items: center;
      }

      .text-green {
        color: #4CAF50;
        margin-left: 5px;
        display: flex;
        align-items: center;
      }

      .text-red {
        color: #f44336;
        margin-left: 5px;
        display: flex;
        align-items: center;
      }

      .chart-card {
        background-color: #fff;
        border-radius: 8px;
        padding: 16px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
        height: 100%;
      }

      .recent-activity-card {
        background-color: #fff;
        border-radius: 8px;
        padding: 16px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
      }

      .card-title {
        font-size: 16px;
        font-weight: bold;
        color: #663300;
        margin-bottom: 16px;
        padding-bottom: 8px;
        border-bottom: 1px dashed #ffb6c1;
      }

      // 趋势图样式
      .trend-chart {
        display: flex;
        height: 280px;
        padding: 10px 0;
        position: relative;
      }

      .chart-axis-y {
        width: 40px;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        padding-right: 10px;
        border-right: 1px solid #ffd0d0;
        
        .y-tick {
          font-size: 12px;
          color: #663300;
          text-align: right;
          height: 20%;
          display: flex;
          align-items: center;
          justify-content: flex-end;
        }
      }

      .chart-content {
        flex: 1;
        display: flex;
        flex-direction: column;
        padding-left: 15px;
      }

      .chart-axis-x {
        display: flex;
        justify-content: space-around;
        margin-top: 5px;
        padding-top: 5px;
      }

      .chart-label {
        text-align: center;
        font-size: 12px;
        color: #663300;
        width: 100%;
      }

      .chart-bars {
        display: flex;
        justify-content: space-around;
        align-items: flex-end;
        flex-grow: 1;
        gap: 10px;
        padding-bottom: 10px;
        position: relative;
        border-bottom: 1px solid #ffd0d0;
      }

      .chart-bar-container {
        display: flex;
        flex-direction: column;
        align-items: center;
        width: 100%;
        max-width: 40px;
      }

      .chart-bar {
        background-color: #ff758c;
        border-radius: 4px 4px 0 0;
        width: 100%;
        transition: height 0.5s ease, background-color 0.3s ease;
        position: relative;
        border: 1px solid #ff5c7c;
        min-height: 10px;
        
        &:hover {
          background-color: #ff5273;
          transform: scaleX(1.1);
        }
      }

      .chart-bar-value {
        position: absolute;
        top: -20px;
        left: 50%;
        transform: translateX(-50%);
        font-size: 12px;
        color: #663300;
        background-color: #fff;
        padding: 0 4px;
        border-radius: 2px;
        box-shadow: 0 1px 2px rgba(0,0,0,0.1);
      }

      // 饼图样式
      .pie-chart-container {
        display: flex;
        align-items: center;
        justify-content: space-around;
        height: 280px;
      }

      .pie-chart {
        width: 180px;
        height: 180px;
        border-radius: 50%;
        position: relative;
        overflow: hidden;
        border: 5px solid #fff0f0;
      }

      .pie-segment {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        transform-origin: center;
      }

      .cat-segment {
        background-color: #ff758c;
        clip-path: polygon(50% 50%, 50% 0%, 100% 0%, 100% 100%, 50% 100%);
        transform: rotate(0deg);
      }

      .dog-segment {
        background-color: #ffb6c1;
        clip-path: polygon(50% 50%, 50% 0%, 100% 0%);
        transform: rotate(162deg);
      }

      .other-segment {
        background-color: #663300;
        clip-path: polygon(50% 50%, 50% 0%, 0% 0%, 50% 50%);
        transform: rotate(288deg);
      }

      .pie-center {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        width: 70px;
        height: 70px;
        border-radius: 50%;
        background-color: #fff;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 14px;
        font-weight: bold;
        color: #663300;
        box-shadow: 0 2px 5px rgba(0,0,0,0.1);
      }

      .pie-legend {
        display: flex;
        flex-direction: column;
        gap: 15px;
      }

      .legend-item {
        display: flex;
        align-items: center;
        font-size: 14px;
        color: #663300;
      }

      .legend-color {
        width: 12px;
        height: 12px;
        border-radius: 3px;
        margin-right: 8px;
      }

      .cat-color {
        background-color: #ff758c;
      }

      .dog-color {
        background-color: #ffb6c1;
      }

      .other-color {
        background-color: #663300;
      }
    }

    .el-footer {
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 14px;
      color: #663300;
      background-color: #fff8f8;
      padding: 10px 0;
      border-top: 1px dashed #ffb6c1;

      &::before {
        content: '🐾';
        margin-right: 8px;
        font-size: 16px;
      }
    }
  }
}
</style>
