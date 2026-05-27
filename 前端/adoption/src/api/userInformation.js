import request from '@/utils/request'
//查全部
// export const getUserList = ()=>{
//     return request.get("/user/")
// }
export const getUserList = (params)=>{
    return request.get("/user/",{params:params})
}
//根据userId查询用户
export const getUserById = (userId) => {
  return request.get(`/user/${userId}`)  // 使用路径参数
}
//添加
export const userAddService = (userModel)=>{
    return request.post("/user/addPetReport",userModel);
}
// 充值 - 正确传递URL参数（匹配后端@RequestParam）
export const userAddMoney = ({ userId, amount }) => {
    // 使用params传递URL参数，而非请求体
    return request.post("/user/addMoney", null, {
        params: {
            userId: Number(userId),  // 确保是数字类型
            amount: Number(amount)   // 转换为数字，后端会自动转为BigDecimal
        }
    })
}

// 扣款 - 正确传递URL参数（匹配后端@RequestParam）
export const userDeleteMoney = ({ userId, amount }) => {
    return request.post("/user/deleteMoney", null, {
        params: {
            userId: Number(userId),
            amount: Number(amount)
        }
    })
}
//修改
export const userUpdateService = (userModel) =>{
    return request.patch("/user/updateUser",userModel);
}
//根据userid修改user_role
export const userUpdateByUserIdService = (userModel) =>{
    return request.patch("/user/updateUserByUserId",userModel);
}
//删除
export const userDeleteService = (userId)=>{
    return request.delete(`/user/${userId}`);
}
//模糊查询
export const searchUserService = (keyword,pageNum,pageSize)=>{
    return request.get("/user/selectUserByKeyWord",{
        params :{keyword,
                pageNum,
                pageSize}
    })
}