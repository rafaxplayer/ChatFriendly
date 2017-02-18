package rafaxplayer.chatfriendly.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import rafaxplayer.chatfriendly.R;
import rafaxplayer.chatfriendly.RoundedImageView;
import rafaxplayer.chatfriendly.activities.ChatActivity;
import rafaxplayer.chatfriendly.classes.GlobalUtils;
import rafaxplayer.chatfriendly.models.Message;
import rafaxplayer.chatfriendly.models.User;

import static rafaxplayer.chatfriendly.Chat_Friendly.messagesRef;

/**
 * Created by rafax on 15/02/2017.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>{

    private Context con;
    private List<User> listUsers;
    private FirebaseUser user;
    private List<ValueEventListener> listListeners;

    public UsersAdapter(Context con, List<User> listUsers) {
        this.con = con;
        this.listUsers = listUsers;
        this.user = GlobalUtils.getCurrentUser();
        this.listListeners= new ArrayList<>();
    }

    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_user_chat, parent, false);
                return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final UsersAdapter.ViewHolder holder, final int position) {
        holder.name.setText(listUsers.get(position).name);
        holder.email.setText(listUsers.get(position).email);
        ValueEventListener list = messagesRef.orderByChild("from").equalTo(listUsers.get(position).uid)
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot data:dataSnapshot.getChildren()){
                    Message mes = data.getValue(Message.class);
                    if(mes.getTo().equals(user.getUid()))

                        holder.lastComment.setText(mes.getMessage());
                        holder.timeLastComment.setText(GlobalUtils.getDate(Long.valueOf(mes.getTimestamp())));
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        listListeners.add(list);
        if(!TextUtils.isEmpty(listUsers.get(position).avatar))
            Picasso.with(con).load(listUsers.get(position).avatar).placeholder(R.drawable.user).error(R.drawable.user).into(holder.img);

    }

    public List<ValueEventListener> getListeners(){
        return listListeners;
    }
    @Override
    public int getItemCount() {

        return listUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name;
        public TextView email;
        public TextView lastComment;
        public TextView timeLastComment;
        public RoundedImageView img;


        public ViewHolder(View v) {
            super(v);
            name = (TextView)v.findViewById(R.id.textUserName);
            email = (TextView)v.findViewById(R.id.textUserEmail);
            lastComment = (TextView)v.findViewById(R.id.textLastComment);
            timeLastComment = (TextView)v.findViewById(R.id.textTimeLastcomment);
            img=(RoundedImageView)v.findViewById(R.id.imgUser);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            Intent intent = new Intent(con,ChatActivity.class);
            intent.putExtra("to",listUsers.get(ViewHolder.this.getLayoutPosition()).uid);
            con.startActivity(intent);
        }
    }


    }

