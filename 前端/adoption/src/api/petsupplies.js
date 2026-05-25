import request from '@/utils/request'
//查全部
// export const getSupplyList = ()=>{
//     return request.get("/petSupplies/")
// }
export const getSupplyList = (params)=>{
    return request.get("/petSupplies/",{params:params})
}
//根据id查询商品详情
export const getSupplyById = (supplyId) =>{
    return request.get(`/petSupplies/selectSupplyBySupplyId/${supplyId}`)
}
//添加
export const supplyAddService = (supplyModel)=>{
    return request.post("/petSupplies/addSupplies",supplyModel);
}
//修改
export const supplyUpdateService = (supplyModel) =>{
    return request.patch("/petSupplies/updateSupplies",supplyModel);
}
//删除
export const supplyDeleteService = (supplyId)=>{
    return request.delete(`/petSupplies/${supplyId}`);
}
//模糊查询
export const searchSuppliesService = (keyword)=>{
    return request.get("/petSupplies/searchSupplies",{
        params :{keyword}
    })
}