package ai.boubaker.hoc.Models;

/**
 * Created by Salim on 5/15/2018.
 */


public class BaseMessage {
    String message;
    User sender;
    long createdAt;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}


