import { defineStore } from "pinia";
import { ref } from "vue";
// defineStore是Pinia用来创建store的函数，第一个参数是store的id，这里是'userInfo'
export const useUserInfoStore = defineStore('userInfo',()=>{
    // 用户信息 定义info获取所输的字符串 就是用户信息
    // const info = ref('')。这里定义了一个info，初始值为空字符串。
    //  ，这个状态应该用来存储用户的登录信息，比如用户名。
    const info = ref({})
    // 定义修改用户信息的方法 定义setInfo方法接收data参数 并将其输赋值给info.value 这样就可以更新用户信息。
    const setInfo =(data) =>{
        info.value = data
    }
    // 定义清空用户信息的方法
    // removeInfo方法则将info.value重置为空字符串，用于清空用户信息。
    const removeInfo =() =>{
        info.value={}
    }
    return {info,setInfo,removeInfo}
},
    // 第二个参数 这里启用了持久化插件
  {
    // 启动pinia的持久化插件，将用户信息存储到浏览器的localStorage中
    persist :true
})