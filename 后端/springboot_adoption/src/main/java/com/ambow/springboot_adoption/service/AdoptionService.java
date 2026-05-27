package com.ambow.springboot_adoption.service;

import com.ambow.springboot_adoption.vo.Adoption;
import com.ambow.springboot_adoption.vo.PageBean;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AdoptionService extends IService<Adoption> {
    PageBean<Adoption> getAdoptionList(Integer page, Integer pageSize);
    void addAdoption(Adoption adoption);
    void updateAdoption(Adoption adoption);
    void deleteAdoption(Integer adoptionId);
    PageBean<Adoption> searchAdoptionService(String keyword, Integer page, Integer pageSize);
}
