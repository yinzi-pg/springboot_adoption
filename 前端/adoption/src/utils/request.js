 import axios from "axios";//导入模块
const baseURL = "http://localhost:8080/springboot_adoption/"//连后台用的(路径)
const instance = axios.create({
     baseURL,
     withCredentials: true
});
//添加一个响应拦截器 response响应 request请求
instance.interceptors.response.use(
    result => {
        return result.data;
    },
    err =>{
        alert("错误！服务异常！");
        return Promise.reject(err);
    }
);
export default instance; 
