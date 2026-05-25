import request from '@/utils/request'
//查全部
// export const getPublicList = ()=>{
//     return request.get("/public/")
// }
export const getPublicList = (params)=>{
    return request.get("/public/",{params:params})
}
//查全部不用分页
export const getPublicListNoPage = ()=>{
    return request.get("/public/getPublicNoPage")
}
//添加
export const publicAddService = (publicModel)=>{
    return request.post("/public/addPublic",publicModel);
}
//修改
export const publicUpdateService = (publicModel) =>{
    return request.patch("/public/updatePublic",publicModel);
}
//删除
export const publicDeleteService = (publicId)=>{
    return request.delete(`/public/${publicId}`);
}
//模糊查询
export const searchPublicService = (keyword)=>{
    return request.get("/public/searchPublic",{
        params :{keyword}
    })
}