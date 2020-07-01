package com.zhuan.friends.service;

import com.zhuan.friends.dao.UserCaseMapper;
import com.zhuan.friends.entity.UserCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserCaseService {

    @Autowired
    private UserCaseMapper userCaseMapper;


    public List<UserCase> selectAllUserCase(int userId, int status, int offset, int limit) {
        return userCaseMapper.selectAllUserCase(userId, status, offset, limit);
    }

    public int insertUserCase(UserCase userCase) {
        return userCaseMapper.insertCase(userCase);
    }

    public int selectUserCaseCount(int userId, int status) {
        return userCaseMapper.selectUserCaseCount(userId, status);
    }


    public UserCase selectUserCaseById(int userCaseId) {
        return userCaseMapper.selectUserCaseById(userCaseId);
    }

    /**
     * 根据conversationId查询用户关系
     *
     * @param conversationId
     * @return
     */
    public UserCase selectUserCaseByConversationId(String conversationId) {
        return userCaseMapper.selectUserCaseByConversationId(conversationId);
    }

    /**
     * 根据id修改用户关系的状态
     *
     * @param id
     * @param status
     * @return
     */
    public int updateUserCaseStatus(int id, int status, Date cheerTime, Date successTime, String content) {
        return userCaseMapper.updateUserCaseStatus(id, status, cheerTime, successTime, content);
    }


    /**
     * 根据id更新 用户关系的分数
     *
     * @param id
     * @param score
     * @return
     */
    public int updateUserCaseScore(int id, double score) {
        return userCaseMapper.updateUserCaseScore(id, score);
    }

}
