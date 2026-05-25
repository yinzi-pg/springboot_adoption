import request from '@/utils/request';
//登录
export const loginService = (registerData) =>{
    var params = new URLSearchParams();
    for(var key in registerData){
        params.append(key,registerData[key]);
    }
    return request.post("/user/login",params);//("/user/login")和后台idea中ssm_demo中userController中的路径要一致

}

//注册
export const registerService = (registerData) =>{
    console.log(registerData);
    return request.post("/user/register",registerData);//直接将数据拿进来
}