package net.miklos.evenodd.service.impl;

import net.miklos.evenodd.model.Admin;
import net.miklos.evenodd.model.ChatMessage;
import net.miklos.evenodd.repository.AdminRepository;
import net.miklos.evenodd.service.AdminService;
import net.miklos.evenodd.util.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    private AdminRepository adminRepository;

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public void addEncryptionVariablesToDatabase(ChatMessage chatMessage) {
        chatMessage = Utilities.decryptKeyPayload(chatMessage);
        Admin admin = adminRepository.findByUserName(chatMessage.getSender());
        String[] messages = chatMessage.getContent().split("\\|");
        admin.setPublicKey(messages[0]);
        admin.setPersonalAES(messages[1]);
        admin.setInitializationVector(messages[2]);
        adminRepository.save(admin);
    }
}
