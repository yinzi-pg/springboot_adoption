import request from '@/utils/request';

// //展示所有
// export const getPetList = ()=>{
//     return request.get("/pet/");
// }

export const getVolunteerList = (params) => {
  return request.get("/volunteer/", { params:params });
};
//根据user_id查询志愿者信息
export const selectVolunteerByUserId = (userId) => {
  return request.get(`/volunteer/byUserId/${userId}`);
};

//添加
export const AddVolunteerService = (volunteerModel) =>{
    return request.post("/volunteer/addVolunteer",volunteerModel);
}

//删除
export const DeleteVolunteerService = (volunteerId) =>{
    return request.delete(`/volunteer/${volunteerId}`);
}
//修改
export const UpdateVolunteerService = (volunteerModel) =>{
    return request.patch("/volunteer/updateVolunteer",volunteerModel);
}

//模糊查询
export const searchVolunteerService = (keyword) =>{
    return request.get("/volunteer/searchVolunteer",{
        params: { keyword }
    })
}


