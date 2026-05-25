//导入 vue-router
// 对应的创建路由 对应的路由规则
import { createRouter ,createWebHistory } from "vue-router";
// 导入组件
import LoginVue from '@/views/Login.vue'
import LayoutVue from '@/views/Layout.vue'
import RegisterVue from '@/views/Register.vue'
import PetInformationVue from '@/views/PetInformation.vue'
import AdoptionVue from '@/views/Adoption.vue'
import PersonalVue from '@/views/Personal.vue'
import PetBoardingVue from "@/views/PetBoarding.vue";
import PetSuppliesVue from "@/views/PetSupplies.vue";
import OrderManageVue from "@/views/OrderManage.vue";
import PetsReportVue from "@/views/PetsReport.vue";
import UserInformationVue from "@/views/UserInformation.vue"; 
import Role from "@/views/Role.vue";
import PublicInformationVue from "@/views/PublicInformation.vue";
import UserPetInformationVue from "@/views/UserPetInformation.vue";
import UserPetSupplyVue from "@/views/UserPetSupply.vue";
import UserAdoptionVue from "@/views/UserAdoption.vue";
import UserOrderItemVue from "@/views/UserOrderItem.vue";
import UserPublicInformationVue from "@/views/UserPublicInformation.vue";
import HomeVue from "@/views/Home.vue"
import ChatComponentVue from "@/views/ChatComponent.vue";
import UserPetBoardingManageVue from "@/views/UserPetBoardingManage.vue";
import PleaseUserPetBoardingVue from "@/views/PleaseUserPetBoarding.vue";
import UserPetReportVue from "@/views/UserPetReport.vue";
import UserAnimalReportVue from "@/views/UserAnimalReport.vue";
import UserOrderByUserIdVue from "@/views/UserOrderByUserId.vue";
import UserLayoutVue from "@/views/UserLayout.vue";
import VolunteerVue from "@/views/Volunteer.vue";
import UserVolunteerVue from "@/views/UserVolunteer.vue";
import BaiduMapVue from "@/views/BaiduMap.vue";


//定义路由关系
// []数组
const routes =([
        {path:'/',component:HomeVue},
        {path:'/login',component:LoginVue},
        {
            path:'/layout',
            component:LayoutVue,
             children :[
                {path:'/petinformation',component:PetInformationVue},
                {path:'/adoption',component:AdoptionVue},
                {path:'/petBoarding',component:PetBoardingVue},
                {path:'/petSupplies',component:PetSuppliesVue},
                {path:'/orderManage',component:OrderManageVue},
                {path:'/petsReport',component:PetsReportVue},
                {path:'/userInformation',component:UserInformationVue},
                {path:'/role',component:Role},
                {path:'/publicInformation',component:PublicInformationVue},
                {path:'/volunteer',component:VolunteerVue},
                {path:'/userPetBoardingManage',component:UserPetBoardingManageVue},
                
                
           
            ],

        },
        {path:'/Register',component:RegisterVue},
        {path:'/UserLayout',component:UserLayoutVue,
            children :[
                {path:'/userPetInformation',component:UserPetInformationVue},
                {path:'/userPetSupply',component:UserPetSupplyVue},
                {path:'/userAdoption',component:UserAdoptionVue},
                {path:'/userOrderItem',component:UserOrderItemVue},
                {path:'/userPublicInformation',component:UserPublicInformationVue},
                {path:'/chatComponent',component:ChatComponentVue},
                {path:'/pleaseUserPetBoarding',component:PleaseUserPetBoardingVue},
                {path:'/userPetReport',component:UserPetReportVue},
                {path:'/userAnimalReport',component:UserAnimalReportVue},
                {path:'/userOrderByUserId',component:UserOrderByUserIdVue},
                {path:'/user/personal',component:PersonalVue},
                {path:'/userVolunteer',component:UserVolunteerVue},
                {path:'/baiduMap',component:BaiduMapVue},

            ]
        },
       
        // LayoutVue和上方导入组件的 LayoutVue一致
        
])
// 创建路由器
const router = createRouter({
    history:createWebHistory(),
    routes:routes 
    // 和上方定义路由里的 routes 一致 
})
export default router
