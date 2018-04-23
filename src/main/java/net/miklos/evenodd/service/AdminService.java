package net.miklos.evenodd.service;

import net.miklos.evenodd.model.ChatMessage;

public interface AdminService {
    void addEncryptionVariablesToDatabase(ChatMessage chatMessage);
}
