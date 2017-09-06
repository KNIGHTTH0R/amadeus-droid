package amadeuslms.amadeus.bo;

import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import amadeuslms.amadeus.cache.TokenCacheController;
import amadeuslms.amadeus.models.MessageModel;
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

        String json = HttpUtils.post(context, url.toString(), content.toString(), token.getToken_type() + " " + token.getAccess_token());

        if (json != null && json.trim().length() > 0) {
            System.out.println(json);
            Type type = new TypeToken<MessageResponse>(){}.getType();

            return new Gson().fromJson(json, type);
        }

        return null;
    }

    public MessageResponse send_message(Context context, UserModel user, MessageModel message) throws Exception {
        TokenResponse token = TokenCacheController.getTokenCache(context);

        StringBuilder url = new StringBuilder();
        url.append(token.getWebserver_url());
        url.append("/api/chat/send_message/");

        Map<String, String> data = new HashMap<String, String>();
        data.put("user_two", user.getEmail());
        data.put("text", message.getText());
        data.put("email", message.getUser().getEmail());
        data.put("subject", message.getSubject() != null ? message.getSubject().getSlug() : "");
        data.put("create_date", message.getCreate_date());

        JSONObject content = new JSONObject(data);

        String json = HttpUtils.post(context, url.toString(), content.toString(), token.getToken_type() + " " + token.getAccess_token());

        if (json != null && json.trim().length() > 0) {
            System.out.println(json);
            Type type = new TypeToken<MessageResponse>(){}.getType();

            return new Gson().fromJson(json, type);
        }

        return null;
    }

    public MessageResponse send_image_message(Context context, UserModel user, MessageModel message, Uri destination) throws Exception {
        TokenResponse token = TokenCacheController.getTokenCache(context);

        StringBuilder url = new StringBuilder();
        url.append(token.getWebserver_url());
        url.append("/api/chat/send_message/");

        Map<String, String> data = new HashMap<String, String>();
        data.put("user_two", user.getEmail());
        data.put("text", message.getText());
        data.put("email", message.getUser().getEmail());
        data.put("subject", message.getSubject() != null ? message.getSubject().getSlug() : "");
        data.put("create_date", message.getCreate_date());

        JSONObject content = new JSONObject(data);

        String json = HttpUtils.postMultipart(url.toString(), content.toString(), token.getToken_type() + " " + token.getAccess_token(), destination);

        if (json != null && json.trim().length() > 0) {
            System.out.println(json);
            Type type = new TypeToken<MessageResponse>(){}.getType();

            return new Gson().fromJson(json, type);
        }

        return null;
    }
}