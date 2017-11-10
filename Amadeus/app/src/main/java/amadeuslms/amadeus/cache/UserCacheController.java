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

import amadeuslms.amadeus.models.UserModel;

/**
 * Created by zambom on 22/06/17.
 */

public class UserCacheController {

    private static final String USER_PREFERENCE_KEY = "USER_ID_PREFERENCE_KEY";

    private static UserModel model;

    public static UserModel getUserCache(Context context) {
        try {
            if (model != null) {
                return model;
            }

            SharedPreferences sharedPreferences = SharedPreferencesContainer.getSharedPreferences(context);

            if(sharedPreferences.contains(USER_PREFERENCE_KEY)){
                String json = sharedPreferences.getString(USER_PREFERENCE_KEY, "");

                if(!TextUtils.isEmpty(json)){
                    UserModel userFromJson = new Gson().fromJson(json, UserModel.class);

                    if(userFromJson != null){
                        model = userFromJson;
                        return model;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public static boolean hasUserCache(Context context){
        try{
            if(model != null){
                return true;
            }

            UserModel user = getUserCache(context);

            if(user != null){
                return true;
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        return false;
    }

    public static void setUserCache(Context context, UserModel user_logged){
        try{
            String json = new Gson().toJson(user_logged);

            SharedPreferences.Editor editor = SharedPreferencesContainer.getEditor(context);
            editor.putString(USER_PREFERENCE_KEY, json);
            editor.commit();

            model = user_logged;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void removeUserCache(Context context){
        try{
            SharedPreferences.Editor editor = SharedPreferencesContainer.getEditor(context);
            editor.remove(USER_PREFERENCE_KEY);
            editor.commit();

            model = null;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
