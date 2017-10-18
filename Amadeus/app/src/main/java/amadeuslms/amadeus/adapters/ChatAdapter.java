package amadeuslms.amadeus.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import amadeuslms.amadeus.R;
import amadeuslms.amadeus.activity.FullImageActivity;
import amadeuslms.amadeus.cache.TokenCacheController;
import amadeuslms.amadeus.helpers.MessageViewHolder;
import amadeuslms.amadeus.models.MessageModel;
import amadeuslms.amadeus.models.UserModel;
import amadeuslms.amadeus.utils.DateUtils;
import amadeuslms.amadeus.utils.StringUtils;
import amadeuslms.amadeus.utils.TypefacesUtil;

/**
 * Created by zambom on 20/07/17.
 */

public class ChatAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private Context context;
    private UserModel user;
    private List<MessageModel> messageList, messagesSelected = new ArrayList<MessageModel>();
    private ArrayList<String> mSelectedPosition = new ArrayList<String>();

    public ChatAdapter(Context context, UserModel user, List<MessageModel> messageList) {
        this.context = context;
        this.user = user;
        this.messageList = messageList;
    }

    public interface OnMessageSelectedListener {
        void onSelected();
        void onDeselected();
    }

    OnMessageSelectedListener mMsgListener;
    public void setOnMessageSelectedListener(OnMessageSelectedListener onMessageSelectedListener) {
        mMsgListener = onMessageSelectedListener;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_list_item, parent, false);

        MessageViewHolder viewHolder = new MessageViewHolder(context, view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, final int position) {
        final MessageModel message = messageList.get(position);

        if (message != null) {
            String older = "0000-00-00T00:00:00.0000000Z";

            if (position < (messageList.size() - 1)) {
                older = messageList.get(position + 1).getCreate_date();
            }

            String this_date = message.getCreate_date();

            String date = DateUtils.displayDate(context, older, this_date);

            if (!date.equals("")) {
                holder.tvDate.setText(date);
                holder.llDate.setVisibility(LinearLayout.VISIBLE);
            } else {
                holder.llDate.setVisibility(LinearLayout.GONE);
            }

            holder.flSent.setVisibility(FrameLayout.GONE);
            holder.flReceived.setVisibility(FrameLayout.GONE);

            if (message.getUser().getEmail().equals(user.getEmail())) {

                if(mSelectedPosition.contains(String.valueOf(position))) {
                    holder.flSent.setActivated(true);
                } else {
                    holder.flSent.setActivated(false);
                }

                holder.flSent.setVisibility(FrameLayout.VISIBLE);
                holder.tvMsgSent.setText(StringUtils.stripTags(message.getText()));
                if(message.getFavorite()) {
                    holder.tvDateSent.setText("\uf08d" + " " + DateUtils.getHour(message.getCreate_date()));
                } else {
                    holder.tvDateSent.setText(DateUtils.getHour(message.getCreate_date()));
                }
                TypefacesUtil.setFontAwesome(context, holder.tvDateSent);
                //MARK: - Set action when select message
                holder.flSent.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        message.setIsSelected(!message.getIsSelected());
                        if(message.getIsSelected()) {
                            if(mSelectedPosition.size() == 0 && mMsgListener != null) {
                                mMsgListener.onSelected();
                            }
                            mSelectedPosition.add(String.valueOf(position));
                        } else {
                            mSelectedPosition.remove(String.valueOf(position));
                            if(mSelectedPosition.size() == 0 && mMsgListener != null) {
                                mMsgListener.onDeselected();
                            }
                        }

                        notifyDataSetChanged();

                        return true;
                    }
                });
                holder.flSent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!mSelectedPosition.isEmpty()) {
                            message.setIsSelected(!message.getIsSelected());
                            if(message.getIsSelected()) {
                                mSelectedPosition.add(String.valueOf(position));
                            } else {
                                mSelectedPosition.remove(String.valueOf(position));
                                if(mSelectedPosition.size() == 0 && mMsgListener != null) {
                                    mMsgListener.onDeselected();
                                }
                            }
                            notifyDataSetChanged();
                        }
                    }
                });
                //END MARK
                if (message.getImage_url() != null && !message.getImage_url().equals("") && TokenCacheController.hasTokenCache(context)) {
                    final String path = TokenCacheController.getTokenCache(context).getWebserver_url() + message.getImage_url();

                    final ProgressBar progressBar = holder.pbSent;

                    if (path.indexOf("gif") > -1) {
                        Glide.with(context).asGif().load(path).listener(new RequestListener<GifDrawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(ProgressBar.GONE);
                                holder.flImgSent.setVisibility(FrameLayout.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                                progressBar.setVisibility(ProgressBar.GONE);
                                return false;
                            }
                        }).into(holder.ivImgSent);
                    } else {
                        Picasso.with(context).load(path).into(holder.ivImgSent, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(ProgressBar.GONE);
                            }

                            @Override
                            public void onError() {
                                System.out.println("error");
                                progressBar.setVisibility(ProgressBar.GONE);
                                holder.flImgSent.setVisibility(FrameLayout.GONE);
                            }
                        });
                    }

                    holder.ivImgSent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), FullImageActivity.class);
                            intent.putExtra(FullImageActivity.FULL_IMAGE, path);
                            intent.putExtra(FullImageActivity.SENDER, message.getUser().getDisplayName());

                            v.getContext().startActivity(intent);
                        }
                    });

                    holder.flImgSent.setVisibility(FrameLayout.VISIBLE);
                } else {
                    holder.flImgSent.setVisibility(FrameLayout.GONE);
                }
            } else {

                if(mSelectedPosition.contains(String.valueOf(position))) {
                    holder.flReceived.setActivated(true);
                } else {
                    holder.flReceived.setActivated(false);
                }

                holder.flReceived.setVisibility(FrameLayout.VISIBLE);
                holder.tvMsgReceived.setText(StringUtils.stripTags(message.getText()));
                if(message.getFavorite()) {
                    holder.tvDateReceived.setText("\uf08d" + " " + DateUtils.getHour(message.getCreate_date()));
                } else {
                    holder.tvDateReceived.setText(DateUtils.getHour(message.getCreate_date()));
                }
                TypefacesUtil.setFontAwesome(context, holder.tvDateReceived);
                //MARK: - Set action when select message
                holder.flReceived.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        message.setIsSelected(!message.getIsSelected());
                        if(message.getIsSelected()) {
                            if(mSelectedPosition.size() == 0 && mMsgListener != null) {
                                mMsgListener.onSelected();
                            }
                            mSelectedPosition.add(String.valueOf(position));
                        } else {
                            mSelectedPosition.remove(String.valueOf(position));
                            if(mSelectedPosition.size() == 0 && mMsgListener != null) {
                                mMsgListener.onDeselected();
                            }
                        }
                        notifyDataSetChanged();
                        return true;
                    }
                });
                holder.flReceived.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!mSelectedPosition.isEmpty()) {
                            message.setIsSelected(!message.getIsSelected());
                            if(message.getIsSelected()) {
                                mSelectedPosition.add(String.valueOf(position));
                            } else {
                                mSelectedPosition.remove(String.valueOf(position));
                                if(mSelectedPosition.size() == 0 && mMsgListener != null) {
                                    mMsgListener.onDeselected();
                                }
                            }
                            notifyDataSetChanged();
                        }
                    }
                });
                //END MARK
                if (message.getImage_url() != null && !message.getImage_url().equals("") && TokenCacheController.hasTokenCache(context)) {
                    final String path = TokenCacheController.getTokenCache(context).getWebserver_url() + message.getImage_url();

                    final ProgressBar progressBar = holder.pbReceived;

                    if (path.indexOf("gif") > -1) {
                        Glide.with(context).asGif().load(path).listener(new RequestListener<GifDrawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(ProgressBar.GONE);
                                holder.flImgReceived.setVisibility(FrameLayout.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                                progressBar.setVisibility(ProgressBar.GONE);
                                return false;
                            }
                        }).into(holder.ivImgReceived);
                    } else {
                        Picasso.with(context).load(path).into(holder.ivImgReceived, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(ProgressBar.GONE);
                            }

                            @Override
                            public void onError() {
                                System.out.println("error");
                                progressBar.setVisibility(ProgressBar.GONE);
                                holder.flImgReceived.setVisibility(FrameLayout.GONE);
                            }
                        });
                    }

                    holder.ivImgReceived.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), FullImageActivity.class);
                            intent.putExtra(FullImageActivity.FULL_IMAGE, path);
                            intent.putExtra(FullImageActivity.SENDER, message.getUser().getDisplayName());

                            v.getContext().startActivity(intent);
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

    public void addListItem(MessageModel msg, int position){
        if(!messageList.contains(msg)) {
            messageList.add(position, msg);
            notifyItemInserted(position);
        }
    }
    //MARK: - About list of selected messages
    public List<MessageModel> getSelected_messages() {
        for(int i = 0; i < mSelectedPosition.size(); ++i) {
            messagesSelected.add(messageList.get(Integer.valueOf(mSelectedPosition.get(i))));
        }
        return messagesSelected;
    }

    public void updateFavorites(boolean favor) {
        for(int i = 0; i < mSelectedPosition.size(); ++i) {
            messageList.get(Integer.valueOf(mSelectedPosition.get(i))).setFavorite(favor);
        }
    }

    public void clearSelection() {
        for(int i = 0; i < messageList.size(); ++i) {
            messageList.get(i).setIsSelected(false);
        }
        mSelectedPosition.clear();
        messagesSelected.clear();
        notifyDataSetChanged();
    }
    //END MARK
}
