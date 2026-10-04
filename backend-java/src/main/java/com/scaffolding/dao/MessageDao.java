package com.scaffolding.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;
import com.scaffolding.model.Message;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bson.Document;

/** Data access for the "messages" collection, using the MongoDB Java driver directly. */
public class MessageDao {

    private static final String COLLECTION = "messages";
    private static final String ID = "_id";
    private static final String CONTENT = "content";
    private static final String CREATED_AT = "created_at";

    private final MongoCollection<Document> collection;

    public MessageDao(MongoDatabase database) {
        this.collection = database.getCollection(COLLECTION);
        this.collection.createIndex(Indexes.descending(CREATED_AT));
    }

    /** Newest first, capped at {@code limit} documents. */
    public List<Message> findLatestMessages(int limit) {
        List<Message> messages = new ArrayList<>();
        if (limit <= 0) {
            return messages;
        }
        for (Document document : collection.find().sort(Sorts.descending(CREATED_AT)).limit(limit)) {
            messages.add(toMessage(document));
        }
        return messages;
    }

    /** Inserts the message, or replaces the existing document with the same id. */
    public Message save(Message message) {
        collection.replaceOne(Filters.eq(ID, message.getId()), toDocument(message), new ReplaceOptions().upsert(true));
        return message;
    }

    public long count() {
        return collection.countDocuments();
    }

    public void deleteAll() {
        collection.deleteMany(new Document());
    }

    private static Document toDocument(Message message) {
        return new Document(ID, message.getId())
                .append(CONTENT, message.getContent())
                .append(CREATED_AT, message.getCreatedAt());
    }

    private static Message toMessage(Document document) {
        return new Message(
                document.get(ID, UUID.class),
                document.getString(CONTENT),
                document.getDate(CREATED_AT)
        );
    }
}
