
package com.ambow.springboot_adoption.service;

import com.ambow.springboot_adoption.vo.Adoption;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface AdoptionService {

    //查全部
    //List<Adoption> selectAllAdoption();
    IPage<Adoption> selectAllAdoptionPage(int pageNum, int pageSize);

    //增加
    int addAdoption(Adoption adoption);

    //删除
    int deleteAdoption(Integer adoptionId);

    //修改
    int updateAdoption(Adoption adoption);

    //模糊查询
    List<Adoption> findAdoptionByKeyword(String keyword);

}
