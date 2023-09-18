package com.mail;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 需要添加依赖
 <dependency>
     <groupId>javax.mail</groupId>
     <artifactId>mail</artifactId>
     <version>1.4</version>
 </dependency>

 */
public class JavaMailSenderDemos {
    static JavaMailSenderDemos INSTANCE;
    static Properties properties;
    static Account account;
    static Content content;
    static {
        INSTANCE = new JavaMailSenderDemos();
        try {
            properties = PropertiesLoaderUtils.loadAllProperties("changingedu.properties");
        } catch (IOException e) { e.printStackTrace(); }
        account = INSTANCE.new Account(
                properties.getProperty("mailsend.from.server.host"),
                Integer.parseInt(properties.getProperty("mailsend.from.server.port")),
                properties.getProperty("mailsend.from"),
                properties.getProperty("mailsend.from.username"),
                properties.getProperty("mailsend.from.password"));
        content = INSTANCE.new Content("测试邮件标题222","<h2>测试邮件内容</h2>", null, "附件名字", null);
        content.setTos(properties.getProperty("mailsend.tos").split(","));
        content.setAttachmentFile(new File(properties.getProperty("mailsend.file.pdf.path")));
    }
    @Data @AllArgsConstructor
    class Account{
        private String host;
        private Integer port;
        private String from;
        private String username;
        private String password;
    }
    @Data @AllArgsConstructor
    class Content{
        private String title;
        private String content;
        private String[] tos;
        private String attachmentName;
        private File attachmentFile;
    }

    public static void checkAuthLogin() throws MessagingException {
        Properties sessionProps = new Properties();
        sessionProps.put("mail.smtp.host", account.getHost());
        sessionProps.put("mail.smtp.port", account.getPort());
        sessionProps.put("mail.smtp.auth", true);
        Session sendMailSession = Session.getInstance(sessionProps);
        Transport transport = sendMailSession.getTransport("smtp");
        transport.connect(account.getHost(), account.getPort(), account.getUsername(), account.getPassword());
    }

    public static void main(String[] args) throws MessagingException {
        checkAuthLogin();
    }

    /**
     原生的使用javax进行邮件发送
     */
    static class JavaxMailSender{
        public static void main(String[] args) throws Exception {
            sendHtmlContent();
        }

        public static void sendTextContent(){

        }

        public static void sendHtmlContent() throws Exception {
            Properties sessionProps = new Properties();
            sessionProps.put("mail.smtp.host", account.getHost());
            sessionProps.put("mail.smtp.port", account.getPort());
            sessionProps.put("mail.smtp.auth", true);
            Session sendMailSession = Session.getInstance(properties);
            Transport transport = sendMailSession.getTransport("smtp");
            transport.connect(account.getHost(), account.getPort(), account.getUsername(), account.getPassword());

            MimeMessage mailMessage = new MimeMessage(sendMailSession);
            // mailMessage.setHeader("Message-ID","12113");

            Address from = new InternetAddress(account.getFrom());
            mailMessage.setFrom(from);
            String[] toAddrs = content.getTos();
            Address[] toAddresses = new InternetAddress[toAddrs.length];
            for (int i = 0; i < toAddrs.length; i++) {
                toAddresses[i] = new InternetAddress(toAddrs[i]);
            }
            // InternetAddress[] froms = InternetAddress.parse(content.getTos());
            // InternetAddress raw = froms[0];
            // InternetAddress internetAddress = new InternetAddress(raw.getAddress(), raw.getPersonal(), "UTF-8");
            //RecipientType 有TO CC BCC
            mailMessage.setRecipients(RecipientType.TO, toAddresses);
            mailMessage.setSubject(content.getTitle());
            mailMessage.setSentDate(new Date());

            Multipart mimeMultipart = new MimeMultipart();

            MimeBodyPart htmlBodyPart = new MimeBodyPart();
            htmlBodyPart.setContent(content.getContent(),"text/html; charset=utf-8");
            mimeMultipart.addBodyPart(htmlBodyPart);

            File attachmentFile = content.getAttachmentFile();
            FileInputStream fileInputStream = new FileInputStream(attachmentFile);
            BodyPart attachmentBodyPart = new MimeBodyPart();
            DataSource dataSource = new ByteArrayDataSource(fileInputStream, "application/pdf");
            DataHandler dataHandler = new DataHandler(dataSource);
            attachmentBodyPart.setDataHandler(dataHandler);
            attachmentBodyPart.setFileName(MimeUtility.encodeText(content.getAttachmentName()));

            mimeMultipart.addBodyPart(attachmentBodyPart);

            mailMessage.setContent(mimeMultipart);

            transport.sendMessage(mailMessage, mailMessage.getAllRecipients());
        }

    }

    /**
     spring里提供了邮件发送工具 JavaMailSenderImpl
     发现MimeMessageHelper.setTo传递多个时会出错,进一步检查发现只能发送到changingedu的邮箱
     */
    static class SpringMailSender{
        static JavaMailSenderImpl javaMailSender;
        static {
            javaMailSender = new JavaMailSenderImpl();
            javaMailSender.setHost(account.getHost());
            javaMailSender.setPort(account.getPort());
            javaMailSender.setUsername(account.getUsername());
            javaMailSender.setPassword(account.getPassword());
        }

        public static void sendHtmlContent() throws MessagingException {
            MimeMessage mimeMsg = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMsgHelper = new MimeMessageHelper(mimeMsg, true, "UTF-8");
            mimeMsgHelper.setSentDate(new Date());
            mimeMsgHelper.setFrom(account.getFrom());
            mimeMsgHelper.setTo(content.getTos());
            mimeMsgHelper.setSubject(content.getTitle());
            mimeMsgHelper.setText(content.getContent(), true);
            // mimeMsgHelper.addAttachment("这是附件", content.getAttachmentFile());
            javaMailSender.send(mimeMsg);
        }

        public static void main(String[] args) throws MessagingException {
            sendHtmlContent();
        }
    }

}
