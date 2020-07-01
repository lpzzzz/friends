package com.zhuan.friends.dao;

import com.zhuan.friends.entity.UserCase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 好友关系持久层
 */
@Mapper
public interface UserCaseMapper {

    /**
     * 插入一个case
     *
     * @param aUserCase
     * @return
     */
    public int insertCase(UserCase aUserCase);

    /**
     * 查询我的好友
     *
     * @param userId
     * @param status
     * @param offset
     * @param limit
     * @return
     */
    public List<UserCase> selectAllUserCase(@Param("userId") int userId, @Param("status") int status,
                                            @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 查询我的好友数量
     *
     * @param userId
     * @return
     */
    public int selectUserCaseCount(@Param("userId") int userId, @Param("status") int status);

    /**
     * 查询用户关系详情
     *
     * @param userCaseId
     * @return
     */
    public UserCase selectUserCaseById(int userCaseId);


    /**
     * 根据ConversationId查询用户关系
     *
     * @param conversationId
     * @return
     */
    public UserCase selectUserCaseByConversationId(String conversationId);

    /**
     * 根据id修改用户关系状态
     *
     * @param id
     * @param status
     * @return
     */
    public int updateUserCaseStatus(@Param("id") int id,
                                    @Param("status") int status,
                                    @Param("cheerTime") Date cheerTime,
                                    @Param("successTime") Date successTime,
                                    @Param("content") String content);

    /**
     * 更新好友关系的分数
     * @param id
     * @param score
     * @return
     */
    public int updateUserCaseScore(@Param("id") int id, @Param("score") double score);
}
