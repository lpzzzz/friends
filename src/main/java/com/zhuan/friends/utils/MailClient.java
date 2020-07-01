package com.zhuan.friends.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component // 该类需要由Spring去进行管理 是一个通用的Bean
public class MailClient {
    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * @param to      邮件的接受者
     * @param subject 邮件的主题
     * @param content 邮件的内容
     */
    public void sendMail(String to, String subject, String content) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // 设置支持Html格式的文本内容
            mailSender.send(helper.getMimeMessage()); // 调用send方法发送邮件
        } catch (MessagingException e) {
            logger.error("发送邮件出现异常" + e.getMessage());
        }
    }
}
