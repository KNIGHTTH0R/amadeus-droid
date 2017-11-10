/*
Copyright 2016, 2017 UFPE - Universidade Federal de Pernambuco
 
Este arquivo é parte do programa Amadeus Sistema de Gestão de Aprendizagem, ou simplesmente Amadeus LMS
 
O Amadeus LMS é um software livre; você pode redistribui-lo e/ou modifica-lo dentro dos termos da Licença Pública Geral GNU como publicada pela Fundação do Software Livre (FSF); na versão 2 da Licença.
 
Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU para maiores detalhes.
 
Você deve ter recebido uma cópia da Licença Pública Geral GNU, sob o título "LICENSE", junto com este programa, se não, escreva para a Fundação do Software Livre (FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.
*/
package amadeuslms.amadeus.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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

        if (model.getUnseen_msgs() > 0) {
            viewHolder.tvBadge.setVisibility(TextView.VISIBLE);
            viewHolder.tvBadge.setText(model.getUnseen_msgs() > 99 ? "+99" : String.valueOf(model.getUnseen_msgs()));
        } else {
            viewHolder.tvBadge.setVisibility(TextView.GONE);
        }

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
