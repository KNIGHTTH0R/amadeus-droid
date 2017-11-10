/*
Copyright 2016, 2017 UFPE - Universidade Federal de Pernambuco
 
Este arquivo é parte do programa Amadeus Sistema de Gestão de Aprendizagem, ou simplesmente Amadeus LMS
 
O Amadeus LMS é um software livre; você pode redistribui-lo e/ou modifica-lo dentro dos termos da Licença Pública Geral GNU como publicada pela Fundação do Software Livre (FSF); na versão 2 da Licença.
 
Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU para maiores detalhes.
 
Você deve ter recebido uma cópia da Licença Pública Geral GNU, sob o título "LICENSE", junto com este programa, se não, escreva para a Fundação do Software Livre (FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.
*/
package amadeuslms.amadeus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import amadeuslms.amadeus.R;
import amadeuslms.amadeus.helpers.SubjectViewHolder;
import amadeuslms.amadeus.models.SubjectModel;
import amadeuslms.amadeus.utils.TypefacesUtil;

/**
 * Created by zambom on 07/07/17.
 */

public class SubjectAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private LayoutInflater inflater;

    private List<SubjectModel> subjectList;
    private List<SubjectModel> filterSubjectList;
    private CustomFilter filter;

    public SubjectAdapter(Context context, List<SubjectModel> subjectList) {
        this.context = context;
        this.subjectList = subjectList;
        this.filterSubjectList = subjectList;

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

    @Override
    public Filter getFilter() {

        if(filter ==  null) {
            filter = new CustomFilter();
        }
        return filter;
    }

    class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if(constraint != null && constraint.length() > 0) {

                constraint = constraint.toString().toUpperCase();

                List<SubjectModel> filters = new ArrayList<SubjectModel>();

                for (int i = 0; i < filterSubjectList.size(); i++) {
                    if(filterSubjectList.get(i).getName().toUpperCase().contains(constraint)) {
                        SubjectModel sm = new SubjectModel(filterSubjectList.get(i).getName(), filterSubjectList.get(i).getSlug(), filterSubjectList.get(i).isVisible(), filterSubjectList.get(i).getNotifications());
                        filters.add(sm);
                    }
                }

                results.count = filters.size();
                results.values = filters;
            } else {
                results.count = filterSubjectList.size();
                results.values = filterSubjectList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            subjectList = (List<SubjectModel>) results.values;
            notifyDataSetChanged();
        }
    }
}
