package com.ambow.springboot_adoption.service.impl;

import com.ambow.springboot_adoption.dao.PetSuppliesMapper;
import com.ambow.springboot_adoption.service.PetSuppliesService;
import com.ambow.springboot_adoption.vo.PageBean;
import com.ambow.springboot_adoption.vo.PetSupplies;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PetSuppliesServiceImpl extends ServiceImpl<PetSuppliesMapper, PetSupplies> implements PetSuppliesService {

    @Override
    public PageBean<PetSupplies> getSupplyList(Integer pageNum, Integer pageSize) {
        Page<PetSupplies> page = new Page<>(pageNum == null ? 1 : pageNum, pageSize == null ? 10 : pageSize);
        Page<PetSupplies> resultPage = this.page(page);
        return new PageBean<>(resultPage.getTotal(), resultPage.getRecords());
    }

    @Override
    public PetSupplies getSupplyById(Integer supplyId) {
        if (supplyId == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        return this.getById(supplyId);
    }

    @Override
    public void supplyAddService(PetSupplies supplyModel) {
        if (supplyModel == null) {
            throw new IllegalArgumentException("商品信息不能为空");
        }
        if (supplyModel.getPrice() != null && supplyModel.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("商品价格不能小于0");
        }
        if (supplyModel.getStock() != null && supplyModel.getStock() < 0) {
            throw new IllegalArgumentException("库存不能小于0");
        }
        supplyModel.setCreateDate(LocalDateTime.now());
        if (supplyModel.getStatus() == null) {
            supplyModel.setStatus("上架");
        }
        this.save(supplyModel);
    }

    @Override
    public void supplyUpdateService(PetSupplies supplyModel) {
        if (supplyModel == null || supplyModel.getSupplyId() == null) {
            throw new IllegalArgumentException("商品信息或ID不能为空");
        }
        if (supplyModel.getPrice() != null && supplyModel.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("商品价格不能小于0");
        }
        if (supplyModel.getStock() != null && supplyModel.getStock() < 0) {
            throw new IllegalArgumentException("库存不能小于0");
        }
        this.updateById(supplyModel);
    }

    @Override
    public void supplyDeleteService(Integer supplyId) {
        if (supplyId == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        this.removeById(supplyId);
    }

    @Override
    public PageBean<PetSupplies> searchSuppliesService(String keyword, Integer pageNum, Integer pageSize) {
        Page<PetSupplies> page = new Page<>(pageNum == null ? 1 : pageNum, pageSize == null ? 10 : pageSize);
        QueryWrapper<PetSupplies> queryWrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper.like("supply_name", keyword)
                        .or().like("supply_type", keyword)
                        .or().like("brand", keyword)
                        .or().like("description", keyword);
        }
        Page<PetSupplies> resultPage = this.page(page, queryWrapper);
        return new PageBean<>(resultPage.getTotal(), resultPage.getRecords());
    }
}
