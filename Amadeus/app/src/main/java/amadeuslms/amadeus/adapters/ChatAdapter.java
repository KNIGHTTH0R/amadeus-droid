package amadeuslms.amadeus.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import amadeuslms.amadeus.R;
import amadeuslms.amadeus.cache.TokenCacheController;
import amadeuslms.amadeus.helpers.MessageViewHolder;
import amadeuslms.amadeus.models.MessageModel;
import amadeuslms.amadeus.models.UserModel;
import amadeuslms.amadeus.utils.StringUtils;

/**
 * Created by zambom on 20/07/17.
 */

public class ChatAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private Context context;
    private UserModel user;
    private List<MessageModel> messageList;

    public ChatAdapter(Context context, UserModel user, List<MessageModel> messageList) {
        this.context = context;
        this.user = user;
        this.messageList = messageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_list_item, parent, false);

        MessageViewHolder viewHolder = new MessageViewHolder(context, view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {
        final MessageModel message = messageList.get(position);

        if (message != null) {
            holder.flSent.setVisibility(FrameLayout.GONE);
            holder.flReceived.setVisibility(FrameLayout.GONE);
            holder.llDate.setVisibility(LinearLayout.GONE);

            if (message.getUser().getEmail().equals(user.getEmail())) {
                holder.flSent.setVisibility(FrameLayout.VISIBLE);
                holder.tvMsgSent.setText(StringUtils.stripTags(message.getText()));
                holder.tvDateSent.setText(message.getCreate_date());

                if (message.getImage() != null && !message.getImage().equals("") && TokenCacheController.hasTokenCache(context)) {
                    String path = TokenCacheController.getTokenCache(context).getWebserver_url() + message.getImage();

                    final ProgressBar progressBar = holder.pbSent;

                    Picasso.with(context).load(path).into(holder.ivImgSent, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(ProgressBar.GONE);
                        }

                        @Override
                        public void onError() {
                            progressBar.setVisibility(ProgressBar.GONE);
                            holder.flImgSent.setVisibility(FrameLayout.GONE);
                        }
                    });

                    holder.flImgSent.setVisibility(FrameLayout.VISIBLE);
                } else {
                    holder.flImgSent.setVisibility(FrameLayout.GONE);
                }
            } else {
                holder.flReceived.setVisibility(FrameLayout.VISIBLE);
                holder.tvMsgReceived.setText(StringUtils.stripTags(message.getText()));
                holder.tvDateReceived.setText(message.getCreate_date());

                if (message.getImage() != null && !message.getImage().equals("") && TokenCacheController.hasTokenCache(context)) {
                    String path = TokenCacheController.getTokenCache(context).getWebserver_url() + message.getImage();

                    final ProgressBar progressBar = holder.pbReceived;

                    Picasso.with(context).load(path).into(holder.ivImgReceived, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(ProgressBar.GONE);
                        }

                        @Override
                        public void onError() {
                            progressBar.setVisibility(ProgressBar.GONE);
                            holder.flImgReceived.setVisibility(FrameLayout.GONE);
                        }
                    });

                    holder.flImgReceived.setVisibility(FrameLayout.VISIBLE);
                } else {
                    holder.flImgReceived.setVisibility(FrameLayout.GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList != null ? messageList.size() : 0;
    }
}
