package rafaxplayer.chatfriendly.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import rafaxplayer.chatfriendly.R;
import rafaxplayer.chatfriendly.RoundedImageView;
import rafaxplayer.chatfriendly.adapters.UsersAdapter;
import rafaxplayer.chatfriendly.classes.GlobalUtils;
import rafaxplayer.chatfriendly.models.User;

import static rafaxplayer.chatfriendly.Chat_Friendly.mAuth;
import static rafaxplayer.chatfriendly.Chat_Friendly.messagesRef;
import static rafaxplayer.chatfriendly.Chat_Friendly.usersRef;

public class UsersActivity extends AppCompatActivity {
    private static String TAG = "UsersActivity";
    private FirebaseAuth.AuthStateListener mAuthListener;
    private RoundedImageView avatar;
    private TextView useremail;
    private TextView username;
    private LinearLayout profile;
    private RecyclerView usersList;
    private ValueEventListener userListener;
    private UsersAdapter usersAdapter;
    private String currentUserId;
    private List<User> listUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        profile = (LinearLayout) findViewById(R.id.profile);
        avatar = (RoundedImageView) findViewById(R.id.avatar);
        username = (TextView) findViewById(R.id.textName);
        useremail = (TextView) findViewById(R.id.textEmail);
        usersList = (RecyclerView) findViewById(R.id.chatlist);
        usersList.setItemAnimator(new DefaultItemAnimator());
        usersList.setLayoutManager(new LinearLayoutManager(this));
        listUsers = new ArrayList<>();
        usersAdapter = new UsersAdapter(UsersActivity.this, listUsers);
        usersList.setAdapter(usersAdapter);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    currentUserId = user.getUid();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    updateProfile(user);
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(UsersActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            }
        };
        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int lastFirstVisiblePosition = ((LinearLayoutManager) usersList.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

                listUsers.removeAll(listUsers);
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    User user = data.getValue(User.class);
                    if (!data.getKey().equals(currentUserId)) {

                        listUsers.add(user);
                    }

                }
                usersAdapter.notifyDataSetChanged();
                usersList.getLayoutManager().scrollToPosition(lastFirstVisiblePosition);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG + ":Error ", databaseError.getMessage());
            }
        };
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser()!=null) {
                    usersRef.child(mAuth.getCurrentUser().getUid()).removeValue();
                    mAuth.signOut();
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        if(GlobalUtils.getCurrentUser()!=null)
            usersRef.addValueEventListener(userListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        if (userListener != null) {
            usersRef.removeEventListener(userListener);
        }
        // remove listeners of adaptaer
        List<ValueEventListener> list = ((UsersAdapter) usersList.getAdapter()).getListeners();
        if (list.size() > 0) {
            for (ValueEventListener listener : list) {
                messagesRef.removeEventListener(listener);
            }
        }
    }

    private void updateProfile(FirebaseUser user) {
        if (user.getPhotoUrl() != null) {
            Picasso.with(this).load(user.getPhotoUrl()).into(avatar);
        }
        username.setText(TextUtils.isEmpty(user.getDisplayName()) ? "Bienvenido" : user.getDisplayName());
        useremail.setText(user.getEmail());

    }
}
