package com.ambow.springboot_adoption.service;

import com.ambow.springboot_adoption.vo.PetReport;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface PetReportService {

    //查全部
    //List<PetReport> seleectAllPetReport();
    IPage<PetReport> selectAllPetReportPage(int pageNum, int pageSize);
    //添加
    int addPetReport(PetReport petReport);
    //修改
    int updatePetReport(PetReport petReport);
    //删除
    int deletePetReport(Integer reportId);
    //模糊查询
    List<PetReport> findPetReportByKeyword(String keyword);

}
