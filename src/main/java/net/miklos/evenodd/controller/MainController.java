package net.miklos.evenodd.controller;

import net.miklos.evenodd.KeyStorage.KeyStorage;
import net.miklos.evenodd.model.Admin;
import net.miklos.evenodd.model.ChatMessage;
import net.miklos.evenodd.model.Game;
import net.miklos.evenodd.repository.AdminRepository;
import net.miklos.evenodd.service.AdminService;
import net.miklos.evenodd.service.GameService;
import net.miklos.evenodd.service.impl.UserDetailsImpl;
import net.miklos.evenodd.util.Utilities;
import net.miklos.evenodd.util.encryption.RSAUtils;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;


@Controller
@SessionAttributes("map")
public class MainController {

    @Autowired
    private SessionRegistry sessionRegistry;


    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final AdminService adminService;
    private final GameService gameService;
    private final AdminRepository adminRepository;

    @Autowired
    public MainController(GameService gameService, AdminService adminService, AdminRepository adminRepository) {
        this.gameService = gameService;
        this.adminService = adminService;
        this.adminRepository = adminRepository;
    }

    @GetMapping({"/", "/play"})
    public ModelAndView play() {
        ModelMap map = new ModelMap();
        String username = Utilities.getUser();
        map.put("username", username);

        return new ModelAndView("play", map);
    }

    @MessageMapping("/sendMessage")
//    @SendTo("/topic/active")
    public void distributeMessage(@Payload ChatMessage chatMessage) {
        chatMessage = Utilities.decryptChatMessage(chatMessage, adminRepository);
        chatMessage.setContent(Utilities.sanitizeString(chatMessage.getContent()));
        chatMessage.setSender(Utilities.sanitizeString(chatMessage.getSender()));
        Utilities.encryptAndSend(chatMessage, listLoggedInUsers(), messagingTemplate, adminRepository);
//        return chatMessage;
    }

    @MessageMapping("/sendGameData")
    public void receiveData(@Payload ChatMessage chatMessage) {
        chatMessage = gameService.updateGameState(chatMessage);
        chatMessage.setContent(Utilities.sanitizeString(chatMessage.getContent()));
        chatMessage.setSender(Utilities.sanitizeString(chatMessage.getSender()));
        String[] messages = chatMessage.getContent().split("\\|");
        Admin admin = adminRepository.findByUserName(messages[1]);
        String messageSender = chatMessage.getSender();
        String messageContent = chatMessage.getContent();
        ChatMessage.MessageType messageType = chatMessage.getType();
        ChatMessage encryptedMessage = Utilities.encryptMessage(chatMessage, admin);
        messagingTemplate.convertAndSendToUser(messages[1], "/queue/messages", encryptedMessage);
        chatMessage = new ChatMessage(messageType, messageContent, messageSender);
        admin = adminRepository.findByUserName(messages[2]);
        encryptedMessage = Utilities.encryptMessage(chatMessage, admin);
        messagingTemplate.convertAndSendToUser(messages[2], "/queue/messages", encryptedMessage);
    }

    @MessageMapping("/requestGame")
    public void receiveGameRequest(@Payload ChatMessage chatMessage) {
        chatMessage = Utilities.decryptChatMessage(chatMessage, adminRepository);
        chatMessage.setContent(Utilities.sanitizeString(chatMessage.getContent()));
        chatMessage.setSender(Utilities.sanitizeString(chatMessage.getSender()));
        Game playerGame = gameService.determineGame(chatMessage);
        ChatMessage gameMessage = null;

        String username = chatMessage.getSender();
        Admin currentUser = adminRepository.findByUserName(username);
        if (playerGame.getAdminIDFK2() != null) {
            gameMessage = new ChatMessage(ChatMessage.MessageType.GAME, "active|" + playerGame.getAdminIDFK1().getUserName() + "|" + playerGame.getAdminIDFK2().getUserName(), "server");
            startGame(playerGame);
        } else
            gameMessage = new ChatMessage(ChatMessage.MessageType.GAME, "inactive|" + username + "|Waiting for an opponent", "server");
        gameMessage = Utilities.encryptMessage(gameMessage, currentUser);
        messagingTemplate.convertAndSendToUser(username, "/queue/messages", gameMessage);
    }

    private void startGame(Game currentGame) {
        ChatMessage chatMessage = new ChatMessage(ChatMessage.MessageType.START, currentGame.getAdminIDFK1().getUserName() + "|" + currentGame.getAdminIDFK2().getUserName(), "server");
        chatMessage.setContent(Utilities.sanitizeString(chatMessage.getContent()));
        chatMessage.setSender(Utilities.sanitizeString(chatMessage.getSender()));
        ChatMessage encryptedMessage = Utilities.encryptMessage(chatMessage, currentGame.getAdminIDFK1());
        messagingTemplate.convertAndSendToUser(currentGame.getAdminIDFK1().getUserName(), "/queue/messages", encryptedMessage);
        chatMessage = new ChatMessage(ChatMessage.MessageType.START, currentGame.getAdminIDFK1().getUserName() + "|" + currentGame.getAdminIDFK2().getUserName(), "server");
        encryptedMessage = Utilities.encryptMessage(chatMessage, currentGame.getAdminIDFK2());
        messagingTemplate.convertAndSendToUser(currentGame.getAdminIDFK2().getUserName(), "/queue/messages", encryptedMessage);
    }

    @MessageMapping("/connectPlayer")
    @SendTo("/global")
    public ChatMessage connectPlayer(@Payload ChatMessage chatMessage,
                                     SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        PublicKey serverPublicKey = KeyStorage.getInstance().getPublic();
        String pKey = Base64.encodeBase64String(serverPublicKey.getEncoded());
        messagingTemplate.convertAndSendToUser(chatMessage.getSender(), "/queue/messages", new ChatMessage(ChatMessage.MessageType.PKEY, pKey, "server"));
        return chatMessage;
    }

    @MessageMapping("/sendKeys")
    public void receiveKeys(@Payload ChatMessage chatMessage){
        adminService.addEncryptionVariablesToDatabase(chatMessage);
    }

    private List<UserDetailsImpl> listLoggedInUsers() {
        final List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
        final List<UserDetailsImpl> admins = new ArrayList<>();
        for(final Object principal : allPrincipals) {
            if(principal instanceof UserDetailsImpl) {
                final UserDetailsImpl user = (UserDetailsImpl) principal;
                admins.add(user);
            }
        }
        return admins;
    }

}
