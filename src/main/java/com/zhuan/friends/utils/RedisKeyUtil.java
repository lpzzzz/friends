package com.zhuan.friends.utils;

/**
 * 编写 生成 redisKey工具类
 */
public class RedisKeyUtil {

    private static final String SPLIT = ":";

    private static final String PREFIX_LIKE_ENTITY = "like:entity";

    private static final String PREFIX_LIKE_USER = "like:user"; // 用户获得的赞

    /**
     * @param entityType
     * @param entityId
     * @return
     */
    // 返回 点赞的 redisKey
    public static String getLikeEntityKey(int entityType, int entityId) {
        return PREFIX_LIKE_ENTITY + SPLIT + entityType + SPLIT + entityId;
    }


    /**
     * 用户获得的赞
     *
     * @param userId
     * @return
     */
    public static String getLikeUserKey(int userId) {
        return PREFIX_LIKE_USER + SPLIT + userId;
    }
}
