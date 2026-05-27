package com.ambow.springboot_adoption.service.Impl;

import com.ambow.springboot_adoption.dao.PetSuppliesMapper;
import com.ambow.springboot_adoption.service.PetSuppliesService;
import com.ambow.springboot_adoption.vo.PetSupplies;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetSuppliesServiceImpl implements PetSuppliesService {
    @Autowired
    private PetSuppliesMapper petSuppliesMapper;
    //查全部
    //public List<PetSupplies> selectAllPetSupplies(){
//        return petSuppliesMapper.selectAllPetSupplies();
//    }
    public IPage<PetSupplies> selectAllPetSuppliesPage(int pageNum, int pageSize){
        Page<PetSupplies> page = new Page<>(pageNum, pageSize);
        return petSuppliesMapper.selectAllPetSupplies(page);
    }
    //根据订单id查询订单详情
    public PetSupplies getSupplyById(Integer supplyId){
        return petSuppliesMapper.getSupplyById(supplyId);
    }
    //添加
    public int addPetSupplies(PetSupplies petSupplies){
        return petSuppliesMapper.addPetSupplies(petSupplies);
    }
    //修改
    public int updatePetSupplies(PetSupplies petSupplies){
        return petSuppliesMapper.updatePetSupplies(petSupplies);
    }
    //删除
    public int deletePetSupplies(Integer supplyId){
        return petSuppliesMapper.deletePetSupplies(supplyId);
    }
    //模糊查询
    public List<PetSupplies> findPetSuppliesByKeyword(String keyword){
        return petSuppliesMapper.findPetSuppliesByKeyword(keyword);
    }
}
