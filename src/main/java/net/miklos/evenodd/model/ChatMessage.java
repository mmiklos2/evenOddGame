package net.miklos.evenodd.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChatMessage {
    private MessageType type;
    private String content;
    private String sender;

    public enum MessageType {
        CHAT,
        GAME,
        JOIN,
        LEAVE,
        SCORE,
        START,
        QUIT,
        PKEY
    }

}