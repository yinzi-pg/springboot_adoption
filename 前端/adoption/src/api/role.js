import request from '@/utils/request'
//查全部
// export const getRoleList = ()=>{
//     return request.get("/role/")
// }
export const getRoleList = (params)=>{
    return request.get("/role/",{params:params})
}
//添加
export const roleAddService = (roleModel)=>{
    return request.post("/role/addRole",roleModel);
}
//修改
export const roleUpdateService = (roleModel) =>{
    return request.patch("/role/updateRole",roleModel);
}
//删除
export const roleDeleteService = (roleId)=>{
    return request.delete(`/role/${roleId}`);
}
//模糊查询
export const searchRoleService = (keyword)=>{
    return request.get("/role/searchRole",{
        params :{keyword}
    })
}