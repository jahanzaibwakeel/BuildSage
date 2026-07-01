package com.buildsage.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {
    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String channel;

    @Column(nullable = false, length = 4000)
    private String message;

    @Column(nullable = false)
    private boolean readFlag;

    protected Notification() {}

    public Notification(UUID userId, String channel, String message) {
        this.userId = userId;
        this.channel = channel;
        this.message = message;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }

    public boolean isReadFlag() {
        return readFlag;
    }

    public void markRead() {
        this.readFlag = true;
    }
}
