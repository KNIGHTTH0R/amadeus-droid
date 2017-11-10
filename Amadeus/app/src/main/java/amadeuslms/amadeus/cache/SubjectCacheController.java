/*
Copyright 2016, 2017 UFPE - Universidade Federal de Pernambuco
 
Este arquivo é parte do programa Amadeus Sistema de Gestão de Aprendizagem, ou simplesmente Amadeus LMS
 
O Amadeus LMS é um software livre; você pode redistribui-lo e/ou modifica-lo dentro dos termos da Licença Pública Geral GNU como publicada pela Fundação do Software Livre (FSF); na versão 2 da Licença.
 
Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU para maiores detalhes.
 
Você deve ter recebido uma cópia da Licença Pública Geral GNU, sob o título "LICENSE", junto com este programa, se não, escreva para a Fundação do Software Livre (FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.
*/
package amadeuslms.amadeus.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.List;

import amadeuslms.amadeus.lists.SubjectList;
import amadeuslms.amadeus.models.SubjectModel;
import amadeuslms.amadeus.models.UserModel;

/**
 * Created by zambom on 30/06/17.
 */

public class SubjectCacheController {

    private static final String SUBJECT_PREFERENCE_KEY = "SUBJECT_PREFERENCE_KEY";

    private static SubjectList model;

    public static SubjectList getSubjectCache(Context context) {
        try {
            if (model != null) {
                return model;
            }

            SharedPreferences sharedPreferences = SharedPreferencesContainer.getSharedPreferences(context);

            if(sharedPreferences.contains(SUBJECT_PREFERENCE_KEY)){
                String json = sharedPreferences.getString(SUBJECT_PREFERENCE_KEY, "");

                if(!TextUtils.isEmpty(json)){
                    SubjectList subjectFromJson = new Gson().fromJson(json, SubjectList.class);

                    if(subjectFromJson != null){
                        model = subjectFromJson;
                        return model;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public static boolean hasSubjectCache(Context context){
        try{
            if(model != null){
                return true;
            }

            SubjectList subjects = getSubjectCache(context);

            if(subjects != null){
                return true;
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        return false;
    }

    public static void setSubjectCache(Context context, SubjectList subjects){
        try{
            String json = new Gson().toJson(subjects);

            SharedPreferences.Editor editor = SharedPreferencesContainer.getEditor(context);
            editor.putString(SUBJECT_PREFERENCE_KEY, json);
            editor.commit();

            model = subjects;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void removeSubjectCache(Context context){
        try{
            SharedPreferences.Editor editor = SharedPreferencesContainer.getEditor(context);
            editor.remove(SUBJECT_PREFERENCE_KEY);
            editor.commit();

            model = null;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
