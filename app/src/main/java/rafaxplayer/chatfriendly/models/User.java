package rafaxplayer.chatfriendly.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by rafax on 15/02/2017.
 */
@IgnoreExtraProperties
public class User {
    public String name;
    public String email;
    public String uid;
    public String avatar;

    public User() {

    }

    public User(String name, String email, String uid, String img) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.avatar=img;
    }
}
