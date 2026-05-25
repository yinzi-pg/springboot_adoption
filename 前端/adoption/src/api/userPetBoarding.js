import request from '@/utils/request'
//查全部
// export const getBoardingList = ()=>{
//     return request.get("/petBoarding/")
// }
export const getUserPetBoardingList = (params)=>{
    return request.get("/userPetBoarding/",{params:params})
}
//根据userId查询用户宠物寄养信息
export const getUserPetBoardingByUserId = (userId) =>{
    return request.get(`/userPetBoarding/byUserId/${userId}`);
}
//根据user_pet_id查询petSize
export const getUserPetBoardingByUserPetId = (userPetId) =>{
    return request.get(`/userPetBoarding/byUserPetId/${userPetId}`)
}
//根据user_pet_id查询petImage
export const getImageByUserPetId = (userPetId) =>{
    return request.get(`/userPetBoarding/GetImageByUserPetId/${userPetId}`)
}
//添加
export const addUserPetBoardingService = (userPetBoardingModel)=>{
    return request.post("/userPetBoarding/addUserPetBoarding",userPetBoardingModel);
}
//修改
export const updateUserPetBoardingService = (userPetBoardingModel) =>{
    return request.patch("/userPetBoarding/updateUserPetBoarding",userPetBoardingModel);
}
//删除
export const deleteUserPetBoardingService = (userPetId)=>{
    return request.delete(`/userPetBoarding/${userPetId}`);
}
//模糊查询
export const searchUserPetBoardingService = (keyword)=>{
    return request.get("/userPetBoarding/searchUserPetBoarding",{
        params :{keyword}
    })
}