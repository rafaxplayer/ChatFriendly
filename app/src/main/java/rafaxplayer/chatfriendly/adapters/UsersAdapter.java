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
        String username=listUsers.get(position).name;
        String email=listUsers.get(position).email;
        holder.name.setText(username == null|| username.equals("No Set")? "Sin nombre":username );
        holder.email.setText(email);
        ValueEventListener list = messagesRef
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count=0;
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    String to = data.child("to").getValue(String.class);
                    String from = data.child("from").getValue(String.class);
                    if((to.equals(user.getUid())&& from.equals(listUsers.get(position).uid))||((from.equals(user.getUid())&& to.equals(listUsers.get(position).uid))))
                        if(!data.child("look").getValue(Boolean.class)&&(to.equals(user.getUid())&& from.equals(listUsers.get(position).uid))){
                            count++;
                        }

                        holder.lastComment.setText( data.child("message").getValue(String.class));
                        holder.timeLastComment.setText(GlobalUtils.getDate(Long.valueOf(data.child("timestamp").getValue(String.class))));
                        int visibility= count>0?View.VISIBLE:View.GONE;
                        holder.badge.setText(String.valueOf(count));
                        holder.badge.setVisibility(visibility);
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
        public TextView badge;
        public RoundedImageView img;


        public ViewHolder(View v) {
            super(v);
            name = (TextView)v.findViewById(R.id.textUserName);
            email = (TextView)v.findViewById(R.id.textUserEmail);
            lastComment = (TextView)v.findViewById(R.id.textLastComment);
            timeLastComment = (TextView)v.findViewById(R.id.textTimeLastcomment);
            badge=(TextView) v.findViewById(R.id.textBadge);
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

