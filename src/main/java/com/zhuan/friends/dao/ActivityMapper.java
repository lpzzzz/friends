package com.zhuan.friends.dao;

import com.zhuan.friends.entity.Activity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Mapper
public interface ActivityMapper {

    /**
     * 查询活动信息 根据 userId 传递的值 进行判断查询如果是 0 表示不拼接该条件 直接查询所有
     * 如果userId是一个具体的用户id则拼接上该id进行查询
     *
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<Activity> selectActivitys(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 查询活动信息 根据 userId 传递的值 进行判断查询如果是 0 表示不拼接该条件 直接查询所有
     * 如果userId是一个具体的用户id则拼接上该id进行查询
     *
     * @param userId
     * @return
     */
    public int selectActivityRows(@Param("userId") int userId);

    /**
     * 发布一个活动
     *
     * @param activity
     * @return
     */
    public int insertActivity(Activity activity);

    /**
     * 根据活动id查询活动
     * @param activityId
     * @return
     */
    public Activity selectActivityById(int activityId);

    public int updateActivityCommentCount(@Param("id") int id , @Param("commentCount") int commentCount);
}
