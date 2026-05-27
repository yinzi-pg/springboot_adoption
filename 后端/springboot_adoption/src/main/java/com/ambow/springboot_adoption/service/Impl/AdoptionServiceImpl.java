package com.ambow.springboot_adoption.service.Impl;

import com.ambow.springboot_adoption.dao.AdoptionMapper;
import com.ambow.springboot_adoption.service.AdoptionService;
import com.ambow.springboot_adoption.vo.Adoption;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AdoptionServiceImpl implements AdoptionService {
    @Autowired
    private AdoptionMapper adoptionMapper;
    @Override
    public IPage<Adoption> selectAllAdoptionPage(int pageNum, int pageSize){
        Page<Adoption> page = new Page<>(pageNum, pageSize);
        return adoptionMapper.selectAllAdoptionPage(page);
    }

    @Override
    public int addAdoption(Adoption adoption) {
        return adoptionMapper.addAdoption(adoption);
    }

    @Override
    public int deleteAdoption(Integer adoptionId) {
        return adoptionMapper.deleteAdoption(adoptionId);
    }

    @Override
    public int updateAdoption(Adoption adoption) {
        return adoptionMapper.updateAdoption(adoption);
    }

    @Override
    public IPage<Adoption> findAdoptionByKeyword(int pageNum, int pageSize, String keyword) {
        Page<Adoption> page = new Page<>(pageNum, pageSize);
        return adoptionMapper.findAdoptionByKeyword(page, keyword);
    }
}
