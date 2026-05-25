import request from '@/utils/request'
//展示所有
// export const getAdoptionList = ()=>{
//     return request.get("/adoption/");
// }
export const getAdoptionList = (params)=>{
    return request.get("/adoption/",{params:params});
}
//添加
export const adoptionApplyService = (adoptionModel)=>{
    return request.post("/adoption/addAdoption",adoptionModel)
}
//修改
export const adoptionUpdateService = (adoptionModel)=>{
  return request.patch("/adoption/updateAdoption",adoptionModel)
}
//删除
export const adoptionDeleteService = (adoptionId) =>{
    return request.delete(`/adoption/${adoptionId}`);
}
//模糊查询
export const searchAdoptionService = (keyword) =>{
    return request.get("/adoption/search",{
        params :{keyword}
    })
}