package amadeuslms.amadeus.helpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import amadeuslms.amadeus.R;

/**
 * Created by zambom on 07/07/17.
 */

public class ParticipantsViewHolder extends RecyclerView.ViewHolder {

    private Context context;

    public TextView tvName;
    public ImageView imPic;

    public ParticipantsViewHolder(Context context, View itemView) {
        super(itemView);

        this.context = context;

        tvName = (TextView) itemView.findViewById(R.id.sub_item);
        imPic = (ImageView) itemView.findViewById(R.id.item_image);
    }
}
