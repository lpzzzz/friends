package com.zhuan.friends.service;

import com.zhuan.friends.dao.CommentMapper;
import com.zhuan.friends.entity.Comment;
import com.zhuan.friends.utils.FriendsConstant;
import com.zhuan.friends.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService implements FriendsConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter; // 实现敏感词过滤

    @Autowired
    private ActivityService activityService;


    /**
     * 查询所有评论且实现分页查询
     *
     * @param entityType
     * @param entityId
     * @param offset
     * @param limit
     * @return
     */
    public List<Comment> selectComments(int entityType, int entityId,
                                        int offset, int limit) {
        return commentMapper.selectCommentByEntity(entityType, entityId, offset, limit);
    }


    /**
     * 根据id查询评论
     * @param id
     * @return
     */
    public Comment selectCommentById(int id) {
        return commentMapper.selectCommentById(id);
    }


    /**
     * 查询所有评论数量
     *
     * @param entityType
     * @param entityId
     * @return
     */
    public int selectCommentCount(int entityType, int entityId) {
        return commentMapper.selectCommentCountByEntity(entityType, entityId);
    }

    public int insertComment(Comment comment) {
        return commentMapper.insertCommentByEntity(comment);
    }

    /**
     * 设置事务的隔离级别和传播机制
     *
     * @param comment
     * @return
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new RuntimeException("参数不能为空!");
        }

        // 添加评论 对评论的内容进行过滤
        comment.setContent(HtmlUtils.htmlUnescape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.insertCommentByEntity(comment);
        // 更新活动的评论数量
        if (comment.getEntityType() == ENTITY_TYPE_ACTIVITY) { // 如果是对活动的评论才进行更新
            int count = commentMapper.selectCommentCountByEntity(comment.getEntityType(), comment.getEntityId());
            activityService.updateCommentCount(comment.getEntityId(), count);
        }
        return rows;
    }

}
