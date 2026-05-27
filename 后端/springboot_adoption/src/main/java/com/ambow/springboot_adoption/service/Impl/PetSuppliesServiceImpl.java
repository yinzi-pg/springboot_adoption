package com.ambow.springboot_adoption.service.Impl;

import com.ambow.springboot_adoption.dao.PetSuppliesMapper;
import com.ambow.springboot_adoption.service.PetSuppliesService;
import com.ambow.springboot_adoption.vo.PetSupplies;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PetSuppliesServiceImpl implements PetSuppliesService {

    @Autowired
    private PetSuppliesMapper petSuppliesMapper;

    @Override
    public IPage<PetSupplies> selectAllPetSuppliers(int pageNum, int pageSize) {
        IPage<PetSupplies> page = new Page<>(pageNum,pageSize);
        IPage<PetSupplies> petSuppliesIPage =  petSuppliesMapper.selectAllPetSupplies(page);
        return petSuppliesIPage;
    }
}
