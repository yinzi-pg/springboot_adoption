import request from '@/utils/request';

// //展示所有
// export const getPetList = ()=>{
//     return request.get("/pet/");
// }

export const getPetList = (params) => {
  return request.get("/pet/", { params:params });
};


//添加
export const petAddService = (petModel) =>{
    return request.post("/pet/addPet",petModel);
}

//删除
export const petDeleteService = (petId) =>{
    return request.delete(`/pet/${petId}`);
}
//修改
export const petUpdateService = (petModel) =>{
    return request.patch("/pet/updatePet",petModel);
}

//模糊查询
export const searchPetService = (keyword,params) =>{
    return request.get("/pet/search",{
        params: {
            keyword:keyword,
            params:params}
    })
}

// // 模糊查询（支持分页）
// export const searchPetService = (params) => {
//   return request.get("/pet/search", { params });
// };


