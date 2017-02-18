package rafaxplayer.chatfriendly.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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

    private RecyclerView chatList;
    private ValueEventListener chatListener;
    private EditText editMessage;
    private ImageButton sendMessage;
    private String remoteUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatList = (RecyclerView) findViewById(R.id.chatList);
        editMessage = (EditText) findViewById(R.id.editMessage);
        sendMessage = (ImageButton) findViewById(R.id.sendMesage);
        chatList.setItemAnimator(new DefaultItemAnimator());
        chatList.setLayoutManager(new LinearLayoutManager(this));
        chatListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Message> listMessages = new ArrayList<>();
                String currentuserID=GlobalUtils.getCurrentUser().getUid();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Message message = data.getValue(Message.class);
                    if ((message.getTo().equals(remoteUserID) && message.getFrom().equals(currentuserID)) || (message.getTo().equals(currentuserID) && message.getFrom().equals(remoteUserID))) {
                        listMessages.add(message);
                    }

                }
                ChatAdapter chatadapter = new ChatAdapter(ChatActivity.this, listMessages);
                chatList.setAdapter(chatadapter);
                chatList.scrollToPosition(chatList.getAdapter().getItemCount()-1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
    private void sendMessage(){

        String key = messagesRef.push().getKey();
        Message mes= new Message();
        mes.setEmail(GlobalUtils.getCurrentUser().getEmail());
        mes.setId(key);
        mes.setMessage(editMessage.getText().toString());
        mes.setFrom(GlobalUtils.getCurrentUser().getUid());
        mes.setTo(remoteUserID);
        mes.setTimestamp(GlobalUtils.getTimeStamp());

        messagesRef.child(key).setValue(mes);
        editMessage.setText("");
    }

    private void setTolbarUserInfo(String id){

        usersRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                ChatActivity.this.setTitle(user.name != null ? user.name : "Usuario");
                ChatActivity.this.getSupportActionBar().setSubtitle(user.email!= null ? user.email : "...");


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        messagesRef.orderByChild("timestamp").addValueEventListener(chatListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bund = getIntent().getExtras();
        if (bund != null) {
            this.remoteUserID = bund.getString("to");
            setTolbarUserInfo(remoteUserID);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatListener != null) {
            messagesRef.removeEventListener(chatListener);
        }
    }




}
