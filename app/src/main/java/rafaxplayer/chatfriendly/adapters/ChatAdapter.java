package rafaxplayer.chatfriendly.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import rafaxplayer.chatfriendly.R;
import rafaxplayer.chatfriendly.classes.GlobalUtils;
import rafaxplayer.chatfriendly.models.Message;

/**
 * Created by rafax on 15/02/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context con;
    private List<Message> listMessages;


    public ChatAdapter(Context con, List<Message> listMessages) {
        this.con = con;
        this.listMessages = listMessages;

    }

    @Override
    public int getItemViewType(int position) {

        if (listMessages.get(position).from.equals(GlobalUtils.getCurrentUser().getUid())) {

            return 0;
        }
        return 1;
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == 0) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_local_user, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_remote_user, parent, false);
        }

        return new ViewHolder(v);

    }


    @Override
    public void onBindViewHolder(ChatAdapter.ViewHolder holder, final int position) {
        holder.message.setText(listMessages.get(position).getMessage());
        holder.timestamp.setText(GlobalUtils.getDate(Long.valueOf(listMessages.get(position).getTimestamp())));
        if (getItemViewType(position) == 0) {
            int resource = listMessages.get(position).look ? R.drawable.look_true : R.drawable.look_false;
            holder.imglook.setImageResource(resource);
        }

    }


    @Override
    public int getItemCount() {

        return listMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public TextView timestamp;
        public ImageView imglook;


        public ViewHolder(View v) {
            super(v);
            message = (TextView) v.findViewById(R.id.textMessage);
            timestamp = (TextView) v.findViewById(R.id.textTime);
            imglook = (ImageView) v.findViewById(R.id.imageLook);
        }


    }
}
