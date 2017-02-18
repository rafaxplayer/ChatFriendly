package rafaxplayer.chatfriendly.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by rafax on 15/02/2017.
 */
@IgnoreExtraProperties
public class Message {
    public String email;
    public String message;
    public String timestamp;
    public String from;
    public String to;
    public String id;


    public Message(){

    }

    public Message(String email, String message, String timestamp, String from, String to, String id) {
        this.email = email;
        this.message = message;
        this.timestamp = timestamp;
        this.from = from;
        this.to = to;
        this.id=id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
