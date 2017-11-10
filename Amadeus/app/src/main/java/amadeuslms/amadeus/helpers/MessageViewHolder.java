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
