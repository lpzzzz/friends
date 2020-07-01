package com.zhuan.friends.dao;

import com.zhuan.friends.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {

    /**
     * 插入一个登录凭证
     *
     * @param loginTicket
     * @return
     */
    @Insert({
            "insert into login_ticket(user_id , ticket,status,expired) ", // 注意在每一个字符串前后加上空格防止拼接之后没有间隔
            " values(#{userId},#{ticket},#{status},#{expired}) "
    })
    @Options(useGeneratedKeys = true, keyProperty = "id") // 这里需要设置主键自增 和 主键的名称
    public int insertLoginTicket(LoginTicket loginTicket);

    /**
     * 根据登录凭证查询
     *
     * @param ticket
     * @return
     */
    @Select({
            "select id , user_id , ticket , status , expired ",
            " from login_ticket ",
            " where ticket = #{ticket}"
    })
    public LoginTicket selectLoginTicket(String ticket);

    /**
     * 根据登录凭证修改登录状态
     *
     * @param ticket
     * @param status
     * @return
     */
    @Update({
            // 在注解中书写 if 标签的方式
            "<script> ",
            " update login_ticket set status = #{status} where  ticket = #{ticket} ",
            "<if test=\"ticket!=null\"> ",
            "and 1 = 1 ",
            " </if> ",
            " </script> "
    })
    public int updateLoginTicket(@Param("ticket") String ticket, @Param("status") int status);
}
