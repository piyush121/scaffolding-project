package com.scaffolding.config;

import com.scaffolding.dao.MessageDao;
import com.scaffolding.model.Message;
import java.util.Date;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Inserts one sample message the first time the app starts against an empty
 * database. MongoDB keeps data between restarts, so it only seeds when the
 * collection is empty.
 */
public final class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private DataSeeder() {
    }

    public static void seedMessages(MessageDao messageDao) {
        if (messageDao.count() == 0) {
            messageDao.save(new Message(UUID.randomUUID(), "Hello World", new Date()));
            log.info("Seeded the messages collection with a sample message");
        }
    }
}
