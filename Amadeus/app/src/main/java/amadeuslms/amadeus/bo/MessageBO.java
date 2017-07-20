package amadeuslms.amadeus.bo;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import amadeuslms.amadeus.cache.TokenCacheController;
import amadeuslms.amadeus.models.UserModel;
import amadeuslms.amadeus.response.MessageResponse;
import amadeuslms.amadeus.response.TokenResponse;
import amadeuslms.amadeus.utils.HttpUtils;

/**
 * Created by zambom on 20/07/17.
 */

public class MessageBO {

    public MessageResponse get_messages(Context context, UserModel user, UserModel user_to) throws Exception {
        TokenResponse token = TokenCacheController.getTokenCache(context);

        StringBuilder url = new StringBuilder();
        url.append(token.getWebserver_url());
        url.append("/api/chat/get_messages/");

        Map<String, String> data = new HashMap<String, String>();
        data.put("email", user.getEmail());
        data.put("user_two", user_to.getEmail());

        JSONObject content = new JSONObject(data);

        String json = HttpUtils.post(url.toString(), content.toString(), token.getToken_type() + " " + token.getAccess_token());

        if (json != null && json.trim().length() > 0) {
            System.out.println(json);
            Type type = new TypeToken<MessageResponse>(){}.getType();

            return new Gson().fromJson(json, type);
        }

        return null;
    }
}
