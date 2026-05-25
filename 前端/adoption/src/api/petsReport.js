import request from '@/utils/request'
//查全部
// export const getReportList = ()=>{
//     return request.get("/petReport/")
// }
export const getReportList = (params)=>{
    return request.get("/petReport/",{ params:params})
}
//添加
export const reportAddService = (reportModel)=>{
    return request.post("/petReport/addPetReport",reportModel);
}
//修改
export const reportUpdateService = (reportModel) =>{
    return request.patch("/petReport/updatePetReport",reportModel);
}
//删除
export const reportDeleteService = (reportId)=>{
    return request.delete(`/petReport/${reportId}`);
}
//模糊查询
export const searchReportService = (keyword)=>{
    return request.get("/petReport/searchPetReport",{
        params :{keyword}
    })
}