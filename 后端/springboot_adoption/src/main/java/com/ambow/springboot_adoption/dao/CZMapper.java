package com.ambow.springboot_adoption.dao;

import com.ambow.springboot_adoption.vo.PublicInformation;
import com.ambow.springboot_adoption.vo.Role;
import com.ambow.springboot_adoption.vo.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CZMapper {

    /*
    用户信息管理
     */

    //模糊查询用户
    @Select("select * from user where\n" +
            "    user_name   like concat('%', #{keyword}, '%') or\n" +
            "    user_phone  like concat('%', #{keyword}, '%') or\n" +
            "    user_email  like concat('%', #{keyword}, '%') or\n" +
            "    user_role   like concat('%', #{keyword}, '%')")
    IPage<User> findUserByKeyword(@Param("keyword") String keyword,IPage<User> page);

    //分页查询
    @Select("select * from user")
    IPage<User> selectUserByPage(IPage<User> page);

    //修改
    @Update("update user set user_name =#{userName},user_password = #{userPassword}," +
            " user_gender = #{userGender},user_age = #{userAge},user_phone = #{userPhone}," +
            "user_email = #{userEmail},user_role = #{userRole},avatar = #{avatar},money = #{money} where user_id = #{userId}")
    int updateUser(User user);

    //删除
    @Delete("delete from user where user_id = #{userId}")
    int deleteUser(Integer userId);


    /*
    角色管理
     */

    //查全部
    @Select("select * from role")
    IPage<Role> selectAllRole(IPage<Role> page);

    //添加
    @Insert("INSERT INTO role (\n" +
            "  role_name, role_code, permissions, role_desc, create_date\n" +
            ") VALUES (\n" +
            "  #{roleName}, #{roleCode}, #{permissions}, #{roleDesc}, now()\n" +
            ")")
    int addRole(Role role);

    //修改
    @Update("UPDATE role SET \n" +
            "  role_name = #{roleName},\n" +
            "  role_code = #{roleCode},\n" +
            "  permissions = #{permissions},\n" +
            "  role_desc = #{roleDesc},\n" +
            "  create_date = now()\n" +
            "WHERE role_id = #{roleId}")
    int updateRole(Role role);

    //删除
    @Delete("delete from role where role_id = #{roleId}")
    int deleteRole(Integer roleId);

    // 模糊查询
    @Select("SELECT \n" +
            "  role_id AS roleId,\n" +
            "  role_name AS roleName,\n" +
            "  role_code AS roleCode,\n" +
            "  permissions,\n" +
            "  role_desc AS roleDesc,\n" +
            "  create_date AS createDate\n" +
            "FROM role \n" +
            "WHERE \n" +
            "  role_name LIKE CONCAT('%', #{keyword}, '%') OR\n" +
            "  role_code LIKE CONCAT('%', #{keyword}, '%') OR\n" +
            "  role_desc LIKE CONCAT('%', #{keyword}, '%')")
    List<Role> findRoleByKeyword(String keyword);

    /*
    公告信息
     */

    // 查询全部
    @Select("select * from public_information")
    IPage<PublicInformation> selectAllPublicInformation(IPage<PublicInformation> page);

    // 新增（create_date使用now()）
    @Insert("INSERT INTO public_information (\n" +
            "  title, content, author, status, \n" +
            "  publish_time, create_date,public_image\n" +
            ") VALUES (\n" +
            "  #{title}, #{content}, #{author}, #{status}, \n" +
            "  #{publishTime},now(),#{publicImage}\n" +
            ")")
    int addInformation(PublicInformation information);

    // 删除
    @Delete("DELETE FROM public_information WHERE public_id = #{publicId}")
    int deleteInformation(Integer publicId);

    // 修改
    @Update("UPDATE public_information SET \n" +
            "  title = #{title},\n" +
            "  content = #{content},\n" +
            "  author = #{author},\n" +
            "  status = #{status},\n" +
            "  publish_time = #{publishTime},\n" +
            "  create_date = now(),\n" +
            "  public_image = #{publicImage}" +
            "  WHERE public_id = #{publicId}")
    int updateInformation(PublicInformation information);

    // 模糊查询
    @Select("SELECT \n" +
            "  public_id AS publicId,\n" +
            "  title,\n" +
            "  content,\n" +
            "  author,\n" +
            "  status,\n" +
            "  publish_time AS publishTime,\n" +
            "  create_date AS createDate," +
            "  public_image AS publicImage\n" +
            "FROM public_information \n" +
            "WHERE \n" +
            "  title LIKE CONCAT('%', #{keyword}, '%') OR\n" +
            "  content LIKE CONCAT('%', #{keyword}, '%') OR\n" +
            "  author LIKE CONCAT('%', #{keyword}, '%') OR\n" +
            "  status LIKE CONCAT('%', #{keyword}, '%')")
    IPage<PublicInformation> findInformationByKeyword(String keyword,IPage<PublicInformation> page);
}
