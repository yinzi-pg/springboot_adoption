package com.ambow.springboot_adoption.service.Impl;

import com.ambow.springboot_adoption.dao.PetReportMapper;
import com.ambow.springboot_adoption.service.PetReportService;
import com.ambow.springboot_adoption.vo.PetReport;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetReportServiceImpl implements PetReportService {

    @Autowired
    private PetReportMapper petReportMapper;

    //查全部
//    public List<PetReport> seleectAllPetReport(){
//        return petReportMapper.selectAllPetReport();
//    }
    public IPage<PetReport> selectAllPetReportPage(int pageNum, int pageSize){
        Page<PetReport> page = new Page<>(pageNum, pageSize);
        return petReportMapper.selectAllPetReport(page);
    }
    //添加
    public int addPetReport(PetReport petReport){
        return petReportMapper.addPetReport(petReport);
    }
    //修改
    public int updatePetReport(PetReport petReport){
        return petReportMapper.updatePetReport(petReport);
    }
    //删除
    public int deletePetReport(Integer reportId){
        return petReportMapper.deletePetReport(reportId);
    }
    //模糊查询
    public List<PetReport> findPetReportByKeyword(String keyword){
        return petReportMapper.findPetReportByKeyword(keyword);
    }

}
