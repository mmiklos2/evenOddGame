package net.miklos.evenodd.util;

import net.miklos.evenodd.model.Admin;
import net.miklos.evenodd.model.ChatMessage;
import net.miklos.evenodd.model.Game;
import net.miklos.evenodd.repository.AdminRepository;
import net.miklos.evenodd.service.impl.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;


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
        String score ="";
        if ((currentGame.getPlayer1Choice() + currentGame.getPlayer2Choice()) % 2 == 0) {
            score = currentGame.getScore();
            score = (Integer.valueOf(score.substring(0, 1)) + 1) + score.substring(1);
        } else {
            score = currentGame.getScore();
            score = score.substring(0, 2) + (Integer.valueOf(score.substring(2)) + 1);
        }
        chatMessage = new ChatMessage(ChatMessage.MessageType.SCORE, score+"|"+currentGame.getAdminIDFK1().getUserName()+"|"+currentGame.getAdminIDFK2().getUserName(), "server");

        return chatMessage;
    }
}
