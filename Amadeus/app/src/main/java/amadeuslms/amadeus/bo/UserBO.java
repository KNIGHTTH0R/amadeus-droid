package amadeuslms.amadeus.bo;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import amadeuslms.amadeus.bean.ApplicationProperties;
import amadeuslms.amadeus.cache.TokenCacheController;
import amadeuslms.amadeus.models.UserModel;
import amadeuslms.amadeus.response.GenericResponse;
import amadeuslms.amadeus.response.TokenResponse;
import amadeuslms.amadeus.response.UserResponse;
import amadeuslms.amadeus.utils.HttpUtils;

/**
 * Created by zambom on 16/06/17.
 */

public class UserBO {

    public UserResponse login(Context context, String host, String email, String password) throws Exception {

        StringBuilder url = new StringBuilder();
        url.append(host);
        url.append("/api/token");

        ApplicationProperties.setWebServiceURL(context, host);

        Map<String, String> data = new HashMap<String, String>();
        data.put("email", email);
        data.put("password", password);

        JSONObject content = new JSONObject(data);

        String json = HttpUtils.post(context, url.toString(), content.toString(), "");

        if (json != null && json.trim().length() > 0) {
            System.out.println(json);
            Type type = new TypeToken<TokenResponse>(){}.getType();

            TokenResponse token = new Gson().fromJson(json, type);

            if (token != null) {
                token.setTime_stamp();                      //Added for test
                token.setWebserver_url(host);

                TokenCacheController.setTokenCache(context, token);

                url = new StringBuilder();
                url.append(host);
                url.append("/api/users/login/");

                json = HttpUtils.post(context, url.toString(), content.toString(), token.getToken_type() + " " + token.getAccess_token());

                if (json != null && json.trim().length() > 0) {
                    System.out.println(json);
                    type = new TypeToken<UserResponse>() {
                    }.getType();

                    return new Gson().fromJson(json, type);
                }
            }
        }

        return null;
    }

    public GenericResponse registerDevice(Context context, UserModel user, String device) throws Exception {
        TokenResponse token = TokenCacheController.getTokenCache(context);

        StringBuilder url = new StringBuilder();
        url.append(token.getWebserver_url());
        url.append("/api/users/register_device/");

        Map<String, String> data = new HashMap<String, String>();
        data.put("email", user.getEmail());
        data.put("device", device);

        JSONObject content = new JSONObject(data);

        String json = HttpUtils.post(context, url.toString(), content.toString(), token.getToken_type() + " " + token.getAccess_token());

        if (json != null && json.trim().length() > 0) {
            Type type = new TypeToken<GenericResponse>(){}.getType();

            return new Gson().fromJson(json, type);
        }
        return null;
    }
}
