package rafaxplayer.chatfriendly;


import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by rafax on 14/02/2017.
 */

public class Chat_Friendly extends Application {
    public static FirebaseAuth mAuth;
    public static FirebaseDatabase database;
    public static DatabaseReference usersRef;
    public static DatabaseReference messagesRef;
    public void onCreate() {
        super.onCreate();
        mAuth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        usersRef = database.getReference("/users");
        messagesRef = database.getReference("/messages");


    }

    public static FirebaseUser getCurrentUser(){
        return mAuth.getCurrentUser();
    }
}
