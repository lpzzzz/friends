package com.zhuan.friends.dao;

import com.zhuan.friends.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 查询所有用户信息
     *
     * @param offset
     * @param limit
     * @return
     */
    public List<User> selectAll(@Param("offset") int offset, @Param("limit") int limit);

    public int selectUserRows();

    public User selectById(int id);

    /**
     * 根据用户查询用户信息
     *
     * @param username
     * @return
     */
    public User selectByUserName(String username);

    /**
     * 根据邮箱查询用户信息
     *
     * @param email
     * @return
     */
    public User selectByEmail(String email);


    /**
     * 查询系统中的有缘人
     *
     * @param username
     * @param location
     * @param gender
     * @param offset
     * @param limit
     * @return
     */
    public List<User> selectLuckyUser(
            @Param("username") String username,
            @Param("location") String location,
            @Param("gender") Integer gender,
            @Param("offset") int offset,
            @Param("limit") int limit);

    /**
     * 查询系统中和自己有缘的人的数量
     * @param username
     * @param location
     * @param gender
     * @return
     */
    public int selectLuckyUserRows(@Param("username") String username,
                                   @Param("location") String location,
                                   @Param("gender") Integer gender);

    /**
     * 插入用户信息
     *
     * @param user
     * @return
     */
    public int insertUser(User user);

    /**
     * 更新用户的状态
     *
     * @param id
     * @param status
     * @return
     */
    public int updateUserStatus(@Param("id") int id, @Param("status") int status);

    /**
     * 根据id修改用户信息
     *
     * @param id
     * @param headerUrl
     * @return
     */
    public int updateUserHeader(@Param("id") int id,
                                @Param("headerUrl") String headerUrl,
                                @Param("username") String username,
                                @Param("age") Integer age,
                                @Param("location") String location,
                                @Param("marriage") Integer marriage,
                                @Param("tel") String tel,
                                @Param("hobby") String hobby,
                                @Param("gender") Integer gender);

    /**
     * 根据用户id修改用户密码
     *
     * @param id
     * @param password
     * @return
     */
    public int updateUserPassword(@Param("id") int id, @Param("password") String password);

}
