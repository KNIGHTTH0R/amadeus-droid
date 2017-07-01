package amadeuslms.amadeus.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import amadeuslms.amadeus.R;
import amadeuslms.amadeus.cache.TokenCacheController;
import amadeuslms.amadeus.models.SubjectModel;
import amadeuslms.amadeus.models.UserModel;
import amadeuslms.amadeus.utils.CircleTransformUtils;
import amadeuslms.amadeus.utils.ImageUtils;

/**
 * Created by zambom on 30/06/17.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<SubjectModel> headers;

    public ExpandableListAdapter(Context context, List<SubjectModel> headers) {
        this.context = context;
        this.headers = headers;
    }

    @Override
    public int getGroupCount() {
        return this.headers.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.headers.get(groupPosition).getParticipants().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.headers.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.headers.get(groupPosition).getParticipants().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        SubjectModel parentHeader = (SubjectModel) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.accordion_header, null);
        }

        TextView lbHeader = (TextView) convertView.findViewById(R.id.sub_header);
        lbHeader.setText(parentHeader.getName());

        if (!parentHeader.isVisible()) {
            convertView.setBackgroundColor(this.context.getResources().getColor(R.color.subjectHeaderInvisible));
        } else {
            convertView.setBackgroundColor(this.context.getResources().getColor(R.color.subjectHeader));
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        UserModel child = (UserModel) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.accordion_item, null);
        }

        TextView lbChild = (TextView) convertView.findViewById(R.id.sub_item);
        lbChild.setText(child.getDisplayName());

        ImageView imChild = (ImageView) convertView.findViewById(R.id.item_image);

        ImageUtils img = new ImageUtils(this.context);

        if(child.getImage() != null && !child.getImage().equals("") && TokenCacheController.hasTokenCache(this.context)){
            String path = TokenCacheController.getTokenCache(this.context).getWebserver_url() + child.getImage();

            Picasso.with(this.context).load(path).transform(new CircleTransformUtils()).into(imChild);
        }else{
            try{
                final InputStream is = this.context.getAssets().open("images/no_image.png");

                Bitmap bmp = BitmapFactory.decodeStream(is);

                imChild.setImageBitmap(img.roundCornerImage(bmp, 40));
            }catch(IOException e){
                System.out.println("Erro: " + e.getMessage());
            }
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
