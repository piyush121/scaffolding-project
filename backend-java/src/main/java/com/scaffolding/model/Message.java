package com.scaffolding.model;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * A message as stored in the "messages" collection (mapped to and from BSON by MessageDao).
 * Also returned as-is by the REST API; createdAt is serialized as epoch milliseconds.
 */
public class Message {

    private UUID id;

    private String content = "Hello World";

    private Date createdAt;

    public Message(UUID id, String content, Date createdAt) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Message(id=" + id + ", content=" + content + ", createdAt=" + createdAt + ")";
    }
}
