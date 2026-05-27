package com.ambow.springboot_adoption.service;

import com.ambow.springboot_adoption.vo.PageBean;
import com.ambow.springboot_adoption.vo.PetSupplies;
import com.baomidou.mybatisplus.extension.service.IService;

public interface PetSuppliesService extends IService<PetSupplies> {
    PageBean<PetSupplies> getSupplyList(Integer page, Integer pageSize);
    PetSupplies getSupplyById(Integer supplyId);
    void supplyAddService(PetSupplies supplyModel);
    void supplyUpdateService(PetSupplies supplyModel);
    void supplyDeleteService(Integer supplyId);
    PageBean<PetSupplies> searchSuppliesService(String keyword, Integer page, Integer pageSize);
}
