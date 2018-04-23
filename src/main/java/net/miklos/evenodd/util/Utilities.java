package net.miklos.evenodd.util;

import net.miklos.evenodd.KeyStorage.KeyStorage;
import net.miklos.evenodd.model.Admin;
import net.miklos.evenodd.model.ChatMessage;
import net.miklos.evenodd.model.Game;
import net.miklos.evenodd.repository.AdminRepository;
import net.miklos.evenodd.service.impl.UserDetailsImpl;
import net.miklos.evenodd.util.encryption.RSAUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.owasp.validator.html.*;
import org.owasp.validator.html.Policy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.File;
import java.io.IOException;
import java.security.*;
import java.util.List;


public class Utilities {


    private Utilities() {
    }

    public static String getUser() {

        return ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    }

    public static Admin getAdmin(AdminRepository adminRepository) {
        return adminRepository.findByUserName(getUser());
    }

    public static ChatMessage determineScore(Game currentGame) {
        ChatMessage chatMessage;
        String score = "";
        if ((currentGame.getPlayer1Choice() + currentGame.getPlayer2Choice()) % 2 == 0) {
            score = currentGame.getScore();
            score = (Integer.valueOf(score.substring(0, 1)) + 1) + score.substring(1);
        } else {
            score = currentGame.getScore();
            score = score.substring(0, 2) + (Integer.valueOf(score.substring(2)) + 1);
        }
        chatMessage = new ChatMessage(ChatMessage.MessageType.SCORE, score + "|" + currentGame.getAdminIDFK1().getUserName() + "|" + currentGame.getAdminIDFK2().getUserName() + "|" + currentGame.getPlayer1Choice() + "|" + currentGame.getPlayer2Choice(), "server");

        return chatMessage;
    }

    public static ChatMessage decryptKeyPayload(ChatMessage chatMessage) {
        String[] messages = chatMessage.getContent().split("\\|");
        PrivateKey privateKey = KeyStorage.getInstance().getPrivate();
        messages[1] = RSAUtils.decrypt(Base64.decodeBase64(messages[1]), privateKey);
        messages[2] = new String(Base64.decodeBase64(messages[2]));
        String sender = RSAUtils.decrypt(Base64.decodeBase64(chatMessage.getSender()), privateKey);
        chatMessage.setSender(new String(Base64.decodeBase64(sender)));
        chatMessage.setContent(messages[0] + "|" + messages[1] + "|" + messages[2]);
        return chatMessage;
    }

    public static ChatMessage decryptChatMessage(ChatMessage chatMessage, AdminRepository adminRepository) {
        PrivateKey privateKey = KeyStorage.getInstance().getPrivate();
        String sender = RSAUtils.decrypt(Base64.decodeBase64(chatMessage.getSender()), privateKey);
        Admin admin = adminRepository.findByUserName(new String(Base64.decodeBase64(sender)));
//        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.decodeBase64(admin.getPersonalAES()), "AES");
        chatMessage.setContent(new String(Base64.decodeBase64(RSAUtils.decrypt(Base64.decodeBase64(chatMessage.getContent()), privateKey))));
        chatMessage.setSender(new String(Base64.decodeBase64(sender)));
        return chatMessage;
    }

    public static void encryptAndSend(ChatMessage chatMessage, List<UserDetailsImpl> admins, SimpMessagingTemplate messagingTemplate, AdminRepository adminRepository){
        ChatMessage encryptedMessage = new ChatMessage(ChatMessage.MessageType.CHAT, "", "");
        Admin admin;
        for(UserDetailsImpl user : admins){
            admin =adminRepository.findByUserName(user.getUsername());
            encryptedMessage.setContent(RSAUtils.encryptAsString(chatMessage.getContent(), admin.getPublicKey()));
            encryptedMessage.setSender(RSAUtils.encryptAsString(chatMessage.getSender(), admin.getPublicKey()));
            messagingTemplate.convertAndSendToUser(admin.getUserName(), "/queue/messages", encryptedMessage);
        }
    }

    public static ChatMessage encryptMessage(ChatMessage chatMessage, Admin admin){
        chatMessage.setSender(RSAUtils.encryptAsString(chatMessage.getSender(), admin.getPublicKey()));
        chatMessage.setContent(RSAUtils.encryptAsString(chatMessage.getContent(), admin.getPublicKey()));
        return chatMessage;
    }

    public static String sanitizeString(String input){
        Policy policy = null;
        CleanResults cr=null;
        try {
            policy = Policy.getInstance( new ClassPathResource("public/antisamy-slashdot.xml").getFile());
            AntiSamy as = new AntiSamy();
            cr = as.scan(input, policy);
        } catch (PolicyException e) {
            e.printStackTrace();
        } catch (ScanException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cr.getCleanHTML();
    }


}
