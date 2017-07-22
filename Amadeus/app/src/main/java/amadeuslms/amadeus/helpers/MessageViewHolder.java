package amadeuslms.amadeus.helpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import amadeuslms.amadeus.R;

/**
 * Created by zambom on 20/07/17.
 */

public class MessageViewHolder extends RecyclerView.ViewHolder {

    private Context context;

    public TextView tvMsgSent, tvMsgReceived, tvDateSent, tvDateReceived, tvDate;
    public ImageView ivImgSent, ivImgReceived;
    public ProgressBar pbSent, pbReceived;
    public LinearLayout llDate;
    public FrameLayout flSent, flReceived, flImgSent, flImgReceived;

    public MessageViewHolder(Context context, View itemView) {
        super(itemView);

        this.context = context;

        tvMsgSent       =   (TextView)      itemView.findViewById(R.id.sent_text);
        tvMsgReceived   =   (TextView)      itemView.findViewById(R.id.received_text);
        tvDateSent      =   (TextView)      itemView.findViewById(R.id.sent_date);
        tvDateReceived  =   (TextView)      itemView.findViewById(R.id.received_date);
        tvDate          =   (TextView)      itemView.findViewById(R.id.msg_date);
        ivImgSent       =   (ImageView)     itemView.findViewById(R.id.sent_img);
        ivImgReceived   =   (ImageView)     itemView.findViewById(R.id.received_img);
        pbSent          =   (ProgressBar)   itemView.findViewById(R.id.sent_progress);
        pbReceived      =   (ProgressBar)   itemView.findViewById(R.id.received_progress);
        llDate          =   (LinearLayout)  itemView.findViewById(R.id.date_container);
        flSent          =   (FrameLayout)   itemView.findViewById(R.id.sent_container);
        flReceived      =   (FrameLayout)   itemView.findViewById(R.id.received_container);
        flImgSent       =   (FrameLayout)   itemView.findViewById(R.id.sent_img_container);
        flImgReceived   =   (FrameLayout)   itemView.findViewById(R.id.received_img_container);
    }
}
