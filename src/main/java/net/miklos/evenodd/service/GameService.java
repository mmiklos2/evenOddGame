package net.miklos.evenodd.service;

import net.miklos.evenodd.model.ChatMessage;
import net.miklos.evenodd.model.Game;

public interface GameService {

    ChatMessage updateGameState(ChatMessage chatMessage);

    Game determineGame(ChatMessage chatMessage);
}
