import request from '@/utils/request';
import UserOrderItem from '@/views/UserOrderItem.vue';

//展示所有
// export const getOrderList = ()=>{
//     return request.get("/order/");
// }
export const getOrderList = (params)=>{
    return request.get("/order/",{params:params});
}
//根据用户id查询该id下的订单
export const selectOrderById = (userId) =>{
    return request.get(`/order/selectOrderById/${userId}`);
}
//添加
export const orderAddService = (orderModel) =>{
    return request.post("/order/addOrder",orderModel);
}

//删除
export const orderDeleteService = (orderId) =>{
    return request.delete(`/order/${orderId}`);
}

//修改
export const orderUpdateService = (orderModel) =>{
    return request.patch("/order/updateOrder",orderModel);
}

//模糊查询
export const searchOrderService = (keyword) =>{
    return request.get("/order/search",{
        params : {keyword}
    })
}