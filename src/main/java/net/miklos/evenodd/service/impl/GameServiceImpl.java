package net.miklos.evenodd.service.impl;

import net.miklos.evenodd.model.Admin;
import net.miklos.evenodd.model.ChatMessage;
import net.miklos.evenodd.model.Game;
import net.miklos.evenodd.repository.AdminRepository;
import net.miklos.evenodd.repository.GameRepository;
import net.miklos.evenodd.service.GameService;
import net.miklos.evenodd.util.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameServiceImpl implements GameService {

    private AdminRepository adminRepository;

    private GameRepository gameRepository;

    @Autowired
    public GameServiceImpl(AdminRepository adminRepository, GameRepository gameRepository) {
        this.adminRepository = adminRepository;
        this.gameRepository = gameRepository;
    }

    @Override
    public ChatMessage updateGameState(ChatMessage chatMessage) {
        Admin participatingPlayer = adminRepository.findByUserName(chatMessage.getSender());
        Game currentGame = gameRepository.findCurrentGame(participatingPlayer);
        String SENDER = "server";
        String INACTIVE = "inactive";
        String ACTIVE = "active";
        if (chatMessage.getType() == ChatMessage.MessageType.GAME) {
            // if he's player 1 in a game,
            if (gameRepository.findByAdminIDFK1(participatingPlayer) != null) {
                if (currentGame.getPlayer1Choice() != null) {
                    return new ChatMessage(ChatMessage.MessageType.GAME, ACTIVE + "|" + currentGame.getAdminIDFK1().getUserName() + "|" + currentGame.getAdminIDFK2().getUserName(), SENDER);
                }
                currentGame.setPlayer1Choice(Integer.valueOf(chatMessage.getContent()));
                gameRepository.save(currentGame);
                if (currentGame.getPlayer2Choice() != null) {
                    return resetGame(currentGame);
                }

                return new ChatMessage(ChatMessage.MessageType.GAME, INACTIVE + "|" + currentGame.getAdminIDFK1().getUserName() + "|" + currentGame.getAdminIDFK2().getUserName(), SENDER);
            } else {
                if (currentGame.getPlayer2Choice() != null)
                    return new ChatMessage(ChatMessage.MessageType.GAME, ACTIVE + "|" + currentGame.getAdminIDFK1().getUserName() + "|" + currentGame.getAdminIDFK2().getUserName(), SENDER);
                currentGame.setPlayer2Choice(Integer.valueOf(chatMessage.getContent()));
                gameRepository.save(currentGame);
                if (currentGame.getPlayer1Choice() != null) {
                    return resetGame(currentGame);
                }
                return new ChatMessage(ChatMessage.MessageType.GAME, INACTIVE + "|" + currentGame.getAdminIDFK1().getUserName() + "|" + currentGame.getAdminIDFK1().getUserName(), SENDER);
            }
        } else if (chatMessage.getType() == ChatMessage.MessageType.QUIT) {
            gameRepository.delete(currentGame);
            return new ChatMessage(ChatMessage.MessageType.QUIT, currentGame.getScore()+"|"+currentGame.getAdminIDFK1().getUserName()+"|"+currentGame.getAdminIDFK2().getUserName(), SENDER);
        }
        return new ChatMessage(ChatMessage.MessageType.GAME, INACTIVE, SENDER);
    }

    @Override
    public Game determineGame(ChatMessage chatMessage) {
        Admin participatingPlayer = adminRepository.findByUserName(chatMessage.getSender());
        List<Game> games = gameRepository.findAll();
        Game game = null;
        if (games.isEmpty())
            game = new Game(participatingPlayer);
        else {
            game = games.get(games.size() - 1);
            if (game.getAdminIDFK2() == null)
                game.setAdminIDFK2(participatingPlayer);
            else
                game = new Game(participatingPlayer);
        }
        gameRepository.save(game);
        return game;
    }

    private ChatMessage resetGame(Game currentGame) {
        ChatMessage chatMessage = Utilities.determineScore(currentGame);
        currentGame.setPlayer2Choice(null);
        currentGame.setPlayer1Choice(null);
        currentGame.setScore(chatMessage.getContent().split("\\|")[0]);
        gameRepository.save(currentGame);
        return chatMessage;
    }

}
