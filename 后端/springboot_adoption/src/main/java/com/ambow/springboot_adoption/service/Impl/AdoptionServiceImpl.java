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

    //查全部
    //public List<Adoption> selectAllAdoption(){
        //return adoptionMapper.selectAllAdoption();
   // }
    public IPage<Adoption> selectAllAdoptionPage(int pageNum, int pageSize){
        Page<Adoption> page = new Page<>(pageNum, pageSize);
        return adoptionMapper.selectAllAdoptionPage(page);
    }

    //增加
    public int addAdoption(Adoption adoption){
        return adoptionMapper.addAdoption(adoption);
    }

    //删除
    public int deleteAdoption(Integer adoptionId){
        return adoptionMapper.deleteAdoption(adoptionId);
    }

    //修改
    public int updateAdoption(Adoption adoption){
        return adoptionMapper.updateAdoption(adoption);
    }

    //模糊查询
    public List<Adoption> findAdoptionByKeyword(String keyword){
        return adoptionMapper.findAdoptionByKeyword(keyword);
    }

}
