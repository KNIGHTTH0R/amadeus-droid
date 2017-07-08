package amadeuslms.amadeus.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import amadeuslms.amadeus.R;
import amadeuslms.amadeus.cache.TokenCacheController;
import amadeuslms.amadeus.helpers.ParticipantsViewHolder;
import amadeuslms.amadeus.models.UserModel;
import amadeuslms.amadeus.utils.CircleTransformUtils;
import amadeuslms.amadeus.utils.ImageUtils;

/**
 * Created by zambom on 07/07/17.
 */

public class ParticipantsAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;

    private List<UserModel> participantsList;

    public ParticipantsAdapter(Context context, List<UserModel> participantsList) {
        this.context = context;
        this.participantsList = participantsList;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final UserModel model = participantsList.get(position);

        ParticipantsViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.participants_list_item, parent, false);

            viewHolder = new ParticipantsViewHolder(context, convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ParticipantsViewHolder) convertView.getTag();
        }

        ImageUtils img = new ImageUtils(context);

        if(model.getImage_url() != null && !model.getImage_url().equals("") && TokenCacheController.hasTokenCache(context)){
            String path = TokenCacheController.getTokenCache(context).getWebserver_url() + model.getImage_url();

            Picasso.with(context).load(path).transform(new CircleTransformUtils()).into(viewHolder.imPic);
        }else{
            try{
                final InputStream is = context.getAssets().open("images/no_image.png");

                Bitmap bmp = BitmapFactory.decodeStream(is);

                viewHolder.imPic.setImageBitmap(img.roundCornerImage(bmp, 40));
            }catch(IOException e){
                System.out.println("Erro: " + e.getMessage());
            }
        }

        viewHolder.tvName.setText(model.getDisplayName());

        return convertView;
    }

    @Override
    public int getCount() {
        return participantsList != null ? participantsList.size() : 0;
    }

    @Override
    public UserModel getItem(int position) {
        return participantsList.get(position);
    }

    @Override
    public  long getItemId(int position) {
        return position;
    }
}
