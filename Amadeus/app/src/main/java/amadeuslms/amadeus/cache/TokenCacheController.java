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
import amadeuslms.amadeus.response.TokenResponse;

/**
 * Created by zambom on 23/06/17.
 */

public class TokenCacheController {

    private static final String TOKEN_PREFERENCE_KEY = "TOKEN_PREFERENCE_KEY";

    private static TokenResponse model;

    public static TokenResponse getTokenCache(Context context) {
        try {
            if (model != null) {
                return model;
            }

            SharedPreferences sharedPreferences = SharedPreferencesContainer.getSharedPreferences(context);

            if(sharedPreferences.contains(TOKEN_PREFERENCE_KEY)){
                String json = sharedPreferences.getString(TOKEN_PREFERENCE_KEY, "");

                if(!TextUtils.isEmpty(json)){
                    TokenResponse tokenFromJson = new Gson().fromJson(json, TokenResponse.class);

                    if(tokenFromJson != null){
                        model = tokenFromJson;
                        return model;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public static boolean hasTokenCache(Context context){
        try{
            if(model != null){
                return true;
            }

            TokenResponse token = getTokenCache(context);

            if(token != null){
                return true;
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        return false;
    }

    public static void setTokenCache(Context context, TokenResponse token){
        try{
            String json = new Gson().toJson(token);

            SharedPreferences.Editor editor = SharedPreferencesContainer.getEditor(context);
            editor.putString(TOKEN_PREFERENCE_KEY, json);
            editor.commit();

            model = token;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void removeTokenCache(Context context){
        try{
            SharedPreferences.Editor editor = SharedPreferencesContainer.getEditor(context);
            editor.remove(TOKEN_PREFERENCE_KEY);
            editor.commit();

            model = null;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
