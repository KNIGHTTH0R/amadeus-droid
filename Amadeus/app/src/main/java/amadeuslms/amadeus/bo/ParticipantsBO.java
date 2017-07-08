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
import amadeuslms.amadeus.response.ParticipantsResponse;
import amadeuslms.amadeus.response.TokenResponse;
import amadeuslms.amadeus.utils.HttpUtils;

/**
 * Created by zambom on 07/07/17.
 */

public class ParticipantsBO {

    public ParticipantsResponse get_participants(Context context, UserModel user, String subject_slug) throws Exception {
        TokenResponse token = TokenCacheController.getTokenCache(context);

        StringBuilder url = new StringBuilder();
        url.append(token.getWebserver_url());
        url.append("/api/participants/get_participants/");

        Map<String, String> data = new HashMap<String, String>();
        data.put("email", user.getEmail());
        data.put("subject_slug", subject_slug);

        JSONObject content = new JSONObject(data);

        String json = HttpUtils.post(url.toString(), content.toString(), token.getToken_type() + " " + token.getAccess_token());

        if (json != null && json.trim().length() > 0) {
            Type type = new TypeToken<ParticipantsResponse>(){}.getType();

            return new Gson().fromJson(json, type);
        }

        return null;
    }
}
