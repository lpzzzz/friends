USE makeFriends;

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `user` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) DEFAULT NULL,
  `password` VARCHAR(50) DEFAULT NULL,
  `age` INT(4) DEFAULT NULL ,
  `location` VARCHAR(100) COMMENT '地区',
  `marriage` INT(20) DEFAULT NULL COMMENT '0-已婚; 1-未婚; 2-丧偶;', 
  `tel` VARCHAR(30) COMMENT '电话号码', 
  `salt` VARCHAR(50) DEFAULT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  `type` INT(11) DEFAULT NULL COMMENT '0-普通用户; 1-超级管理员;',
  `status` INT(11) DEFAULT NULL COMMENT '0-未激活; 1-已激活;',
  `activation_code` VARCHAR(100) DEFAULT NULL,
  `header_url` VARCHAR(200) DEFAULT NULL,
  `create_time` TIMESTAMP NULL DEFAULT NULL,
  `hobby` VARCHAR(300) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_username` (`username`(20)),
  KEY `index_email` (`email`(20))
) ENGINE=INNODB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8;



-- 用户 案例表 ：其中保存的是用户与用户的关系 ，根据 状态的不同关系也不同 
DROP TABLE IF EXISTS `case`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `case` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `request_id` INT(11) DEFAULT NULL COMMENT '好友请求者id;',
  `response_id` INT(11) DEFAULT NULL COMMENT '好友接收者id;',
  `conversation_id` VARCHAR(45) NOT NULL,
  `content` TEXT,
  `status` INT(11) DEFAULT NULL COMMENT '0-删除;1-成为好友;2-参加活动;3-成功牵手;',
  `face_image` VARCHAR(200) DEFAULT NULL COMMENT '封面;',
  `create_time` TIMESTAMP NULL DEFAULT NULL,
  `score` DOUBLE DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_from_id` (`request_id`),
  KEY `index_to_id` (`response_id`),
  KEY `index_conversation_id` (`conversation_id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `message` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `from_id` INT(11) DEFAULT NULL,
  `to_id` INT(11) DEFAULT NULL,
  `conversation_id` VARCHAR(45) NOT NULL,
  `content` TEXT,
  `status` INT(11) DEFAULT NULL COMMENT '0-未读;1-已读;2-删除;',
  `create_time` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_from_id` (`from_id`),
  KEY `index_to_id` (`to_id`),
  KEY `index_conversation_id` (`conversation_id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `comment` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) DEFAULT NULL,
  `entity_type` INT(11) DEFAULT NULL,
  `entity_id` INT(11) DEFAULT NULL,
  `target_id` INT(11) DEFAULT NULL,
  `content` TEXT,
  `status` INT(11) DEFAULT NULL,
  `create_time` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_user_id` (`user_id`) /*!80000 INVISIBLE */,
  KEY `index_entity_id` (`entity_id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


DROP TABLE IF EXISTS `login_ticket`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `login_ticket` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL,
  `ticket` VARCHAR(45) NOT NULL,
  `status` INT(11) DEFAULT '0' COMMENT '0-有效; 1-无效;',
  `expired` TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`),
  KEY `index_ticket` (`ticket`(20))
) ENGINE=INNODB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `activity` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(45) DEFAULT NULL,
  `title` VARCHAR(100) DEFAULT NULL,
  `content` TEXT,
  `type` INT(11) DEFAULT NULL COMMENT '0-普通; 1-置顶;',
  `status` INT(11) DEFAULT NULL COMMENT '0-正常; 1-精华; 2-拉黑;',
  `create_time` TIMESTAMP NULL DEFAULT NULL,
  `comment_count` INT(11) DEFAULT NULL,
  `score` DOUBLE DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_user_id` (`user_id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;