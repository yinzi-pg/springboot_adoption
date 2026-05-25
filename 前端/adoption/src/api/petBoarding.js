import request from '@/utils/request'
//查全部
// export const getBoardingList = ()=>{
//     return request.get("/petBoarding/")
// }
export const getBoardingList = (params)=>{
    return request.get("/petBoarding/",{params:params})
}
//添加
export const boardingAddService = (boardingModel)=>{
    return request.post("/petBoarding/addPetBoarding",boardingModel);
}
//修改
export const boardingUpdateService = (boardingModel) =>{
    return request.patch("/petBoarding/updatePetBoarding",boardingModel);
}
//删除
export const boardingDeleteService = (boardingId)=>{
    return request.delete(`/petBoarding/${boardingId}`);
}
//模糊查询
export const searchBoardingService = (keyword)=>{
    return request.get("/petBoarding/searchPetBoarding",{
        params :{keyword}
    })
}