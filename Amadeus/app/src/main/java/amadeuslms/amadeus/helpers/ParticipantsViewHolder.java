/*
Copyright 2016, 2017 UFPE - Universidade Federal de Pernambuco
 
Este arquivo é parte do programa Amadeus Sistema de Gestão de Aprendizagem, ou simplesmente Amadeus LMS
 
O Amadeus LMS é um software livre; você pode redistribui-lo e/ou modifica-lo dentro dos termos da Licença Pública Geral GNU como publicada pela Fundação do Software Livre (FSF); na versão 2 da Licença.
 
Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU para maiores detalhes.
 
Você deve ter recebido uma cópia da Licença Pública Geral GNU, sob o título "LICENSE", junto com este programa, se não, escreva para a Fundação do Software Livre (FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.
*/
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

    public TextView tvName, tvBadge;
    public ImageView imPic;

    public ParticipantsViewHolder(Context context, View itemView) {
        super(itemView);

        this.context = context;

        tvName = (TextView) itemView.findViewById(R.id.sub_item);
        imPic = (ImageView) itemView.findViewById(R.id.item_image);
        tvBadge = (TextView) itemView.findViewById(R.id.item_badge);
    }
}
