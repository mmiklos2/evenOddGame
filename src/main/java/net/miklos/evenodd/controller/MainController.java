package net.miklos.evenodd.controller;

import net.miklos.evenodd.model.ChatMessage;
import net.miklos.evenodd.model.Game;
import net.miklos.evenodd.service.GameService;
import net.miklos.evenodd.util.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;


@Controller
@SessionAttributes("map")
public class MainController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    private final
    GameService gameService;

    @Autowired
    public MainController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping({"/", "/play"})
    public ModelAndView play() {
        ModelMap map = new ModelMap();
        String username = Utilities.getUser();
        map.put("username", username);

        return new ModelAndView("play", map);
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/active")
    public ChatMessage distributeMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/sendGameData")
    public void receiveData(@Payload ChatMessage chatMessage) {
        chatMessage = gameService.updateGameState(chatMessage);
        String[] messages = chatMessage.getContent().split("\\|");
        messagingTemplate.convertAndSendToUser(messages[1], "/queue/messages", chatMessage);
        messagingTemplate.convertAndSendToUser(messages[2], "/queue/messages", chatMessage);
    }

    @MessageMapping("/requestGame")
    public void receiveGameRequest(@Payload ChatMessage chatMessage) {
        Game playerGame = gameService.determineGame(chatMessage);
        ChatMessage gameMessage = null;

        String username = chatMessage.getSender();
        if (playerGame.getAdminIDFK2() != null) {
            gameMessage = new ChatMessage(ChatMessage.MessageType.GAME, "active|" + playerGame.getAdminIDFK1().getUserName() + "|" + playerGame.getAdminIDFK2().getUserName(), "server");
            startGame(playerGame);
        } else
            gameMessage = new ChatMessage(ChatMessage.MessageType.GAME, "inactive|" + username + "|Waiting for an opponent", "server");
        messagingTemplate.convertAndSendToUser(username, "/queue/messages", gameMessage);
    }

    private void startGame(Game currentGame) {
        ChatMessage chatMessage = new ChatMessage(ChatMessage.MessageType.START, currentGame.getAdminIDFK1().getUserName() + "|" + currentGame.getAdminIDFK2().getUserName(), "server");
        messagingTemplate.convertAndSendToUser(currentGame.getAdminIDFK1().getUserName(), "/queue/messages", chatMessage);
        messagingTemplate.convertAndSendToUser(currentGame.getAdminIDFK2().getUserName(), "/queue/messages", chatMessage);
    }

    @MessageMapping("/connectPlayer")
    @SendTo("/global")
    public ChatMessage connectPlayer(@Payload ChatMessage chatMessage,
                                     SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

}
