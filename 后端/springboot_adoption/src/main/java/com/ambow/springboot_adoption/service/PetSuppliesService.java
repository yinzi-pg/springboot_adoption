package com.ambow.springboot_adoption.service;

import com.ambow.springboot_adoption.vo.PetSupplies;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface PetSuppliesService {

    IPage<PetSupplies> selectAllPetSuppliers(int pageNum, int pageSize);
}
