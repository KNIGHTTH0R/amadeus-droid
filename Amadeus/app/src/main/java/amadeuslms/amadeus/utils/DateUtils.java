/*
Copyright 2016, 2017 UFPE - Universidade Federal de Pernambuco
 
Este arquivo é parte do programa Amadeus Sistema de Gestão de Aprendizagem, ou simplesmente Amadeus LMS
 
O Amadeus LMS é um software livre; você pode redistribui-lo e/ou modifica-lo dentro dos termos da Licença Pública Geral GNU como publicada pela Fundação do Software Livre (FSF); na versão 2 da Licença.
 
Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU para maiores detalhes.
 
Você deve ter recebido uma cópia da Licença Pública Geral GNU, sob o título "LICENSE", junto com este programa, se não, escreva para a Fundação do Software Livre (FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.
*/
package amadeuslms.amadeus.utils;

import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import amadeuslms.amadeus.R;

/**
 * Created by zambom on 22/07/17.
 */

public class DateUtils {

    public static String currentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        return sdf.format(new Date());
    }

    public static String getHour(String date){
        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date_time = simpleDateFormat.parse(date);

            SimpleDateFormat out = new SimpleDateFormat("HH:mm");

            return out.format(date_time);
        }catch (ParseException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static String displayDate(Context context, String last_date, String new_date){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date _last_date = sdf.parse(last_date);
            Date _new_date = sdf.parse(new_date);

            Calendar prev_date = Calendar.getInstance();
            prev_date.setTime(_last_date);

            prev_date.set(Calendar.HOUR_OF_DAY, 0);
            prev_date.set(Calendar.MINUTE, 0);
            prev_date.set(Calendar.SECOND, 0);
            prev_date.set(Calendar.MILLISECOND, 0);

            Calendar next_date = Calendar.getInstance();
            next_date.setTime(_new_date);

            next_date.set(Calendar.HOUR_OF_DAY, 0);
            next_date.set(Calendar.MINUTE, 0);
            next_date.set(Calendar.SECOND, 0);
            next_date.set(Calendar.MILLISECOND, 0);

            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DAY_OF_MONTH, -1);

            yesterday.set(Calendar.HOUR_OF_DAY, 0);
            yesterday.set(Calendar.MINUTE, 0);
            yesterday.set(Calendar.SECOND, 0);
            yesterday.set(Calendar.MILLISECOND, 0);

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            if (!prev_date.equals(next_date)) {
                if (next_date.equals(today)) {
                    return context.getResources().getString(R.string.today);
                } else if (next_date.equals(yesterday)) {
                    return context.getResources().getString(R.string.yesterday);
                } else {
                    return DateFormat.getDateInstance(DateFormat.LONG).format(_new_date);
                }
            } else {
                return "";
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }

        return "";
    }
}
