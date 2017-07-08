package amadeuslms.amadeus.helpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import amadeuslms.amadeus.R;

/**
 * Created by zambom on 07/07/17.
 */

public class SubjectViewHolder extends RecyclerView.ViewHolder {

    private Context context;

    public TextView tvTitle, tvArrow;

    public SubjectViewHolder(Context context, View itemView) {
        super(itemView);

        this.context = context;

        tvTitle = (TextView) itemView.findViewById(R.id.sub_header);
        tvArrow = (TextView) itemView.findViewById(R.id.item_arrow);
    }
}
