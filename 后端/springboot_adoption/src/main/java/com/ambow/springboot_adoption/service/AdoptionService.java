package com.ambow.springboot_adoption.service;

import com.ambow.springboot_adoption.vo.Adoption;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface AdoptionService {
    IPage<Adoption> selectAllAdoptionPage(int pageNum, int pageSize);
    int addAdoption(Adoption adoption);
    int deleteAdoption(Integer adoptionId);
    int updateAdoption(Adoption adoption);
    IPage<Adoption> findAdoptionByKeyword(int pageNum, int pageSize, String keyword);

}
