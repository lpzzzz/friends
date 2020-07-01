package com.zhuan.friends.service;

import com.zhuan.friends.dao.ActivityMapper;
import com.zhuan.friends.entity.Activity;
import com.zhuan.friends.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

@Service
public class ActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter; // 过滤敏感词

    /**
     * 查询活动信息 根据 userId 传递的值 进行判断查询如果是 0 表示不拼接该条件 直接查询所有
     * 如果userId是一个具体的用户id则拼接上该id进行查询
     *
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<Activity> selectActivitys(int userId, int offset, int limit) {
        return activityMapper.selectActivitys(userId, offset, limit);
    }

    /**
     * 查询活动信息 根据 userId 传递的值 进行判断查询如果是 0 表示不拼接该条件 直接查询所有
     * 如果userId是一个具体的用户id则拼接上该id进行查询
     *
     * @param userId
     * @return
     */
    public int selectActivityRows(int userId) {
        return activityMapper.selectActivityRows(userId);
    }

    /**
     * @param activity
     * @return
     */
    public int insertActivity(Activity activity) {
        if (activity == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        // 对输入的标签进行处理
        activity.setTitle(HtmlUtils.htmlEscape(activity.getTitle()));
        activity.setContent(HtmlUtils.htmlEscape(activity.getContent()));

        // 对铭感词进行过滤
        activity.setTitle(sensitiveFilter.filter(activity.getTitle()));
        activity.setContent(sensitiveFilter.filter(activity.getContent()));
        // 设置默认的封面
        activity.setFaceImage(String.format("http://images.nowcoder.com/head/%dt.png",
                new Random().nextInt(1000)));
        return activityMapper.insertActivity(activity);
    }

    /**
     * 根据活动id查询活动
     *
     * @param activityId
     * @return
     */
    public Activity selectActivityById(int activityId) {
        return activityMapper.selectActivityById(activityId);
    }

    /**
     * 添加评论时更新评论数量
     *
     * @param id
     * @param commentCount
     * @return
     */
    public int updateCommentCount(int id, int commentCount) {
        return activityMapper.updateActivityCommentCount(id, commentCount);
    }
}
