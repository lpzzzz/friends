package com.zhuan.friends.event;

import com.alibaba.fastjson.JSONObject;
import com.zhuan.friends.dao.MessageMapper;
import com.zhuan.friends.entity.Event;
import com.zhuan.friends.entity.Message;
import com.zhuan.friends.service.MessageService;
import com.zhuan.friends.utils.FriendsConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 事件的消费者
 */
@Component
public class EventConsumer implements FriendsConstant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageMapper messageMapper;

    @KafkaListener(topics = {TOPIC_LIKE, TOPIC_COMMENT,TOPIC_ADD_USER_CASE})
    public void handleSomeMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息内容为空!");
            return;
        }

        // 将消息的Json格式转换为Event对象
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误!");
            return;
        }

        // 封装站内消息
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);//所有系统消息都是由系统用户发送的
        message.setStatus(0); // 默认消息状态为有效
        message.setToId(event.getEntityUserId()); // 消息发送给实体拥有者
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        // 封装消息的内容
        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        // 事件中封装的额外的信息
        if (!event.getData().isEmpty()) { // 如果事件中封装的额外消息不为空 遍历存入到消息的内容中
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }

        // 将封装的content装换为json设置到 message中
        message.setContent(JSONObject.toJSONString(content));

        messageMapper.insertMessage(message);
    }

}
