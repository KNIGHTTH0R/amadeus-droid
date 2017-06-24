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
