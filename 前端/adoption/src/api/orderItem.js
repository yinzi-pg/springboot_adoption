import request from '@/utils/request'

//查全部
export const getOrderItemList = (params)=>{
    return request.get("/orderItem/",{params:params});
}
//根据userId查询购物车
export const getOrderByUserId = (queryParams) => {
    return request.get(`/orderItem/${queryParams.userId}`, { params: queryParams });
};
//根据订单id查询订单详情
export const getOrderByOrderId = (orderId) =>{
    return request.get(`/orderItem/order/{${orderId}`)
}
//添加
export const addOrderItemService = (orderItemModel)=>{
    return request.post("/orderItem/addOrderItem",orderItemModel)
}
//修改
export const updateOrderItemService = (orderItemModel)=>{
  return request.patch("/orderItem/updateOrderItem",orderItemModel)
}
//删除
export const deleteOrderItemService = (orderItemId) =>{
    return request.delete(`/orderItem/${orderItemId}`);
}
//模糊查询
export const searchAdoptionService = (keyword) =>{
    return request.get("/orderItem/search",{
        params :{keyword}
    })
}
