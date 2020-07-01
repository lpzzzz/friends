package com.zhuan.friends.service;

import com.zhuan.friends.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 实现点赞功能
     *
     * @param userId
     * @param entityType
     * @param entityId
     * @param entityUserId
     */
    public void like(int userId, int entityType, int entityId, int entityUserId, int cheerUserId) {
        /*// 获得redisKey
        String likeEntityKey = RedisKeyUtil.getLikeEntityKey(entityType, entityId);
        // 判断用户是否已经对该实体点赞
        Boolean isMember = redisTemplate.opsForSet().isMember(likeEntityKey, userId);
        if (isMember) { // 如果已经点赞
            // 再次点表示取消点赞
            redisTemplate.opsForSet().remove(likeEntityKey, userId);
        } else { // 否则就是进行点赞
            redisTemplate.opsForSet().add(likeEntityKey, userId);
        }*/
        // 重构点赞方法 由于是对两个键的操作所以需要使用事务
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //点赞redisKey
                String likeEntityKey = RedisKeyUtil.getLikeEntityKey(entityType, entityId);
                // 获赞redisKey
                String likeEntityUserKey = RedisKeyUtil.getLikeUserKey(entityUserId);
                String likeCheerUserKey = RedisKeyUtil.getLikeUserKey(cheerUserId);
                // 判断该用户 是否点赞

                // 查询语句需要写在 事务之外 因为事务之内不能执行查询
                Boolean isMember = operations.opsForSet().isMember(likeEntityKey, userId);

                // 开启事务
                operations.multi();

                if (isMember) {
                    // 已点赞
                    operations.opsForSet().remove(likeEntityKey, userId);
                    // 获赞者赞数量减一
                    operations.opsForValue().decrement(likeEntityUserKey);
                    // 因为这里 存在为加油站加油 或者 为 活动点赞 所以需要判断 如果 传递的 cheerUserId为0 则不进行加 减
                    if (cheerUserId != 0) {
                        operations.opsForValue().decrement(likeCheerUserKey);
                    }
                } else {
                    // 未点赞
                    operations.opsForSet().add(likeEntityKey, userId);
                    // 将获赞者的赞数量 加一
                    operations.opsForValue().increment(likeEntityUserKey);
                    if (cheerUserId != 0) {
                        operations.opsForValue().increment(likeCheerUserKey);
                    }
                }
                return operations.exec();
            }
        });
    }


    /**
     * 获取实体的点赞数量
     *
     * @param entityType
     * @param entityId
     * @return
     */
    public long findEntityLikeCount(int entityType, int entityId) {
        String likeEntityKey = RedisKeyUtil.getLikeEntityKey(entityType, entityId);
        return redisTemplate.opsForSet().size(likeEntityKey);
    }

    /**
     * 获取用户对实体的点赞
     * 如果已经点赞返回 1 为点赞返回 0
     *
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String likeEntityKey = RedisKeyUtil.getLikeEntityKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(likeEntityKey, userId) ? 1 : 0;
    }


    /**
     * 返回获得的赞
     * @param userId
     * @return
     */
    public int findUserLikeCount(int userId) {
        String likeUserKey = RedisKeyUtil.getLikeUserKey(userId);
        Integer likeCount = (Integer) redisTemplate.opsForValue().get(likeUserKey);
        // 如果likeCount为 null 返回 0 否则 返回获赞数量的整数形式
        return likeCount == null ? 0 : likeCount.intValue();
    }

}
