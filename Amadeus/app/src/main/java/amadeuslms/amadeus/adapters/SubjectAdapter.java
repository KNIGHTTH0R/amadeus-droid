package amadeuslms.amadeus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import amadeuslms.amadeus.R;
import amadeuslms.amadeus.helpers.SubjectViewHolder;
import amadeuslms.amadeus.models.SubjectModel;
import amadeuslms.amadeus.utils.TypefacesUtil;

/**
 * Created by zambom on 07/07/17.
 */

public class SubjectAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;

    private List<SubjectModel> subjectList;

    public SubjectAdapter(Context context, List<SubjectModel> subjectList) {
        this.context = context;
        this.subjectList = subjectList;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SubjectModel model = subjectList.get(position);

        SubjectViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.subject_list_item, parent, false);

            viewHolder = new SubjectViewHolder(context, convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SubjectViewHolder) convertView.getTag();
        }

        viewHolder.tvTitle.setText(model.getName());
        viewHolder.tvArrow.setText(context.getString(R.string.fa_angle_right));

        TypefacesUtil.setFontAwesome(context, viewHolder.tvArrow);

        if (!model.isVisible()) {
            viewHolder.itemView.setBackgroundColor(this.context.getResources().getColor(R.color.primaryGray));
        } else {
            viewHolder.itemView.setBackgroundColor(this.context.getResources().getColor(R.color.primaryBlue));
        }

        if (model.getNotifications() > 0) {
            viewHolder.tvBadge.setVisibility(TextView.VISIBLE);
            viewHolder.tvBadge.setText(model.getNotifications() > 99 ? "+99" : String.valueOf(model.getNotifications()));
        } else {
            viewHolder.tvBadge.setVisibility(TextView.GONE);
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return subjectList != null ? subjectList.size() : 0;
    }

    @Override
    public SubjectModel getItem(int position) {
        return subjectList.get(position);
    }

    @Override
    public  long getItemId(int position) {
        return position;
    }
}
