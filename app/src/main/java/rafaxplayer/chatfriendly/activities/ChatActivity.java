package rafaxplayer.chatfriendly.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import rafaxplayer.chatfriendly.R;
import rafaxplayer.chatfriendly.adapters.ChatAdapter;
import rafaxplayer.chatfriendly.classes.GlobalUtils;
import rafaxplayer.chatfriendly.models.Message;
import rafaxplayer.chatfriendly.models.User;

import static rafaxplayer.chatfriendly.Chat_Friendly.messagesRef;
import static rafaxplayer.chatfriendly.Chat_Friendly.usersRef;

public class ChatActivity extends AppCompatActivity {

    private static String TAG = "ChatActivity";
    private RecyclerView chatList;
    private ValueEventListener chatListener;
    private EditText editMessage;
    private ImageButton sendMessage;
    private String remoteUserID;
    private String currentuserID;
    private List<Message> listMessages;
    private ChatAdapter chatadapter;
    private Toolbar toolbar;
    private TextView title;
    private TextView subtitle;
    private ImageView imageprofile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        if(toolbar!=null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            imageprofile=(ImageView) toolbar.findViewById(R.id.image_profile);
            title=(TextView) toolbar.findViewById(R.id.toolbar_title);
        }

        subtitle=(TextView) toolbar.findViewById(R.id.toolbar_subtitle);
        chatList = (RecyclerView) findViewById(R.id.chatList);
        editMessage = (EditText) findViewById(R.id.editMessage);
        sendMessage = (ImageButton) findViewById(R.id.sendMesage);
        chatList.setItemAnimator(new DefaultItemAnimator());
        chatList.setLayoutManager(new LinearLayoutManager(this));
        currentuserID = GlobalUtils.getCurrentUser().getUid();
        listMessages = new ArrayList<>();
        chatadapter = new ChatAdapter(ChatActivity.this, listMessages);
        chatList.setAdapter(chatadapter);
        chatListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Message message = data.getValue(Message.class);
                    if ((message.getTo().equals(remoteUserID) && message.getFrom().equals(currentuserID)) || (message.getTo().equals(currentuserID) && message.getFrom().equals(remoteUserID))) {
                        listMessages.add(message);
                    }

                }
                chatadapter.notifyDataSetChanged();
                chatList.scrollToPosition(chatList.getAdapter().getItemCount() - 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG + ":Error ", databaseError.getMessage());
            }
        };
        editMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() > 0) {
                    sendMessage.setEnabled(true);
                } else {
                    sendMessage.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage();
                    handled = true;
                }
                return handled;
            }
        });
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        messagesRef.orderByChild("timestamp").limitToFirst(100).addValueEventListener(chatListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bund = getIntent().getExtras();
        if (bund != null) {
            this.remoteUserID = bund.getString("to");
            setTolbarUserInfo(remoteUserID);
            setLookAll();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatListener != null) {
            messagesRef.removeEventListener(chatListener);
        }
    }

    private void sendMessage() {

        String key = messagesRef.push().getKey();
        Message mes = new Message();
        mes.setEmail(currentuserID);
        mes.setId(key);
        mes.setMessage(editMessage.getText().toString());
        mes.setFrom(GlobalUtils.getCurrentUser().getUid());
        mes.setTo(remoteUserID);
        mes.setTimestamp(GlobalUtils.getTimeStamp());
        mes.setLook(false);

        messagesRef.child(key).setValue(mes);
        editMessage.setText("");
    }

    private void setLookAll() {
        messagesRef.orderByChild("from").equalTo(remoteUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Message message = data.getValue(Message.class);

                    if (message.getTo().equals(currentuserID)) {
                        message.setLook(true);
                        messagesRef.child(data.getKey()).setValue(message);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG + ":Error ", databaseError.getMessage());
            }
        });

    }

    private void setTolbarUserInfo(String id) {

        usersRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(toolbar!=null) {
                    title.setText(user.name == null|| user.name.equals("No Set")? "Sin nombre":user.name );
                    subtitle.setText(user.email != null ? user.email : "...");
                    if (!TextUtils.isEmpty(user.avatar))
                        Picasso.with(ChatActivity.this).load(user.avatar).placeholder(R.drawable.user).error(R.drawable.user).into(imageprofile);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG + ":Error ", databaseError.getMessage());
            }
        });

    }


}
