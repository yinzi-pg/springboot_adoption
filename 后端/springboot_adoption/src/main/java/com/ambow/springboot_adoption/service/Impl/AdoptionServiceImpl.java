package com.ambow.springboot_adoption.service.impl;

import com.ambow.springboot_adoption.dao.AdoptionMapper;
import com.ambow.springboot_adoption.service.AdoptionService;
import com.ambow.springboot_adoption.vo.Adoption;
import com.ambow.springboot_adoption.vo.PageBean;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AdoptionServiceImpl extends ServiceImpl<AdoptionMapper, Adoption> implements AdoptionService {

    @Override
    public PageBean<Adoption> getAdoptionList(Integer pageNum, Integer pageSize) {
        Page<Adoption> page = new Page<>(pageNum == null ? 1 : pageNum, pageSize == null ? 10 : pageSize);
        Page<Adoption> resultPage = this.page(page);
        return new PageBean<>(resultPage.getTotal(), resultPage.getRecords());
    }

    @Override
    public void addAdoption(Adoption adoption) {
        if (adoption == null || adoption.getPetId() == null) {
            throw new IllegalArgumentException("领养申请信息和宠物ID不能为空");
        }
        adoption.setApplyDate(LocalDateTime.now());
        if (adoption.getStatus() == null) {
            adoption.setStatus("待审核");
        }
        this.save(adoption);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAdoption(Adoption adoption) {
        if (adoption == null || adoption.getAdoptionId() == null) {
            throw new IllegalArgumentException("领养申请信息或ID不能为空");
        }
        adoption.setReviewDate(LocalDateTime.now());
        
        // 校验如果审批通过，更新对应宠物状态为"已领养"
        if ("已通过".equals(adoption.getStatus())) {
            // Need to get the petId if not provided in the model
            Adoption existing = this.getById(adoption.getAdoptionId());
            if (existing != null && existing.getPetId() != null) {
                this.baseMapper.updatePetStatus(existing.getPetId(), "已领养");
            }
        }
        this.updateById(adoption);
    }

    @Override
    public void deleteAdoption(Integer adoptionId) {
        if (adoptionId == null) {
            throw new IllegalArgumentException("领养申请ID不能为空");
        }
        this.removeById(adoptionId);
    }

    @Override
    public PageBean<Adoption> searchAdoptionService(String keyword, Integer pageNum, Integer pageSize) {
        Page<Adoption> page = new Page<>(pageNum == null ? 1 : pageNum, pageSize == null ? 10 : pageSize);
        QueryWrapper<Adoption> queryWrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper.like("applicant_name", keyword)
                        .or().like("applicant_phone", keyword)
                        .or().like("status", keyword)
                        .or().like("remark", keyword);
        }
        Page<Adoption> resultPage = this.page(page, queryWrapper);
        return new PageBean<>(resultPage.getTotal(), resultPage.getRecords());
    }
}
