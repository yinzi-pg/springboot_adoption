package com.ambow.springboot_adoption.service.Impl;

import com.ambow.springboot_adoption.dao.CZMapper;
import com.ambow.springboot_adoption.vo.PublicInformation;
import com.ambow.springboot_adoption.vo.Role;
import com.ambow.springboot_adoption.vo.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CZServiceImpl {
    @Autowired
    private CZMapper czMapper;


    //模糊查询
    public IPage<User> selectUserByKeyword(String keyword, int pageNum, int pageSize){
        Page<User> page = new Page<>(pageNum, pageSize);
        return czMapper.findUserByKeyword(keyword,page);
    }
    //分页查询
    public IPage<User> selectUserByPage(int pageNum, int pageSize){
        Page<User> page = new Page<>(pageNum,pageSize);
        return czMapper.selectUserByPage(page);
    }
    //修改信息
    public int updateUser(User user){
        return czMapper.updateUser(user);
    }
    //删除
    public int deleteUser(Integer userId){

        return czMapper.deleteUser(userId);
    }



    //查全部
    public IPage<Role> selectAllRolePage(int pageNum, int pageSize){
        Page<Role> page = new Page<>(pageNum,pageSize);
        return czMapper.selectAllRole(page);
    }
    //添加
    public int addRole(Role role){
        return czMapper.addRole(role);
    }
    //修改
    public int updateRole(Role role){
        return czMapper.updateRole(role);
    }
    //删除
    public int deleteRole(Integer roleId){
        return czMapper.deleteRole(roleId);
    }
    //模糊查询
    public List<Role> findRoleByKeyword(String keyword){
        return czMapper.findRoleByKeyword(keyword);
    }


    //查全部(有分页)
    public IPage<PublicInformation> selectAllPublicInformationPage(int pageNum, int pageSize){
        Page<PublicInformation> page = new Page<>(pageNum, pageSize);
        return czMapper.selectAllPublicInformation(page);
    }
    //添加
    public int addPublicInformation(PublicInformation publicInformation){
        return czMapper.addInformation(publicInformation);
    }
    //修改
    public int updatePublicInformation(PublicInformation publicInformation){
        return czMapper.updateInformation(publicInformation);
    }
    //删除
    public int deletePublicInformation(Integer publicId){
        return czMapper.deleteInformation(publicId);
    }
    //模糊查询
    public IPage<PublicInformation> findPublicByKeyword(String keyword , int pageNum, int pageSize){
        Page<PublicInformation> page = new Page<>(pageNum, pageSize);
        return czMapper.findInformationByKeyword(keyword,page);
    }
}
