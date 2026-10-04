package com.scaffolding.service;

import com.scaffolding.model.Message;
import com.scaffolding.dao.MessageDao;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MessageService {

    private final MessageDao messageDao;

    public MessageService(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    public List<Message> getLatestMessages(int limit) {
        return messageDao.findLatestMessages(limit);
    }

    public Message createMessage() {
        Message message = new Message(
                UUID.randomUUID(),
                "Hello World",
                new Date()
        );
        return messageDao.save(message);
    }
}
