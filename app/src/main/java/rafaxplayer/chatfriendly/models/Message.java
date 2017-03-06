package rafaxplayer.chatfriendly.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by rafax on 15/02/2017.
 */
@IgnoreExtraProperties
public class Message {
    public String avatar;
    public String email;
    public String message;
    public Long timestamp;
    public String from;
    public String to;
    public String id;
    public boolean look;


    public Message(){

    }



    public Message(String avatar, String email, String message, Long timestamp, String from, String to, String id, boolean look) {
        this.email = email;
        this.message = message;
        this.timestamp = timestamp;
        this.from = from;
        this.to = to;
        this.id=id;
        this.look=look;
        this.avatar=avatar;
    }
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
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

    public boolean isLook() {
        return look;
    }

    public void setLook(boolean look) {
        this.look = look;
    }

}
