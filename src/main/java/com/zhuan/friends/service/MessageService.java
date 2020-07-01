package com.zhuan.friends.service;

import com.zhuan.friends.dao.MessageMapper;
import com.zhuan.friends.entity.Message;
import com.zhuan.friends.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    /**
     * 查询会话的数量
     *
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<Message> selectConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }


    /**
     * 查询某个用户的会话数量
     *
     * @param userId
     * @return
     */
    public int selectConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    /**
     * 查询某个用户的私信数量
     *
     * @param conversationId
     * @param offset
     * @param limit
     * @return
     */
    public List<Message> selectLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }


    /**
     * 查询私信的数量
     *
     * @param conversationId
     * @return
     */
    public int selectLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    public int selectUnReadLetter(int userId, String conversationId) {
        return messageMapper.selectLetterUnread(userId, conversationId);
    }


    /**
     * 添加消息到数据库 在添加之前对标签和敏感词进行过滤
     *
     * @param message
     * @return
     */
    public int addMessages(Message message) {
        // 对敏感词进行过滤
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    /**
     * 修改消息的状态 如果是 已读消息需要修改为 1
     *
     * @param ids
     * @param status
     * @return
     */
    public int updateMessageStatus(List<Integer> ids, int status) {
        return messageMapper.updateMessageStatus(ids, status);
    }


    /**
     * 查询某一主题下的所有通知
     *
     * @param userId
     * @param topic
     * @return
     */
    public Message getLatestNotice(int userId, String topic) {
        return messageMapper.getLatestNotice(userId, topic);
    }

    /**
     * 查询某一主题下的消息数量
     *
     * @param userId
     * @param topic
     * @return
     */
    public int selectNoticeCount(int userId, String topic) {
        return messageMapper.selectNoticeCount(userId, topic);
    }


    /**
     * 查询某一主题下的未读消息数量或者 全部的未读消息数量 如果传递的topic 为null 表示查询的是所有未读通知数量
     *
     * @param userId
     * @param topic
     * @return
     */
    public int selectUnreadNoticeCount(int userId, String topic) {
        return messageMapper.selectUnreadNoticeCount(userId, topic);
    }

    /**
     * 查询通知详情列表
     *
     * @param userId
     * @param topic
     * @param offset
     * @param limit
     * @return
     */
    public List<Message> selectNotices(int userId, String topic, int offset, int limit) {
        return messageMapper.selectNotices(userId, topic, offset, limit);
    }

}
