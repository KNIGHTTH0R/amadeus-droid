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
import amadeuslms.amadeus.response.SubjectResponse;
import amadeuslms.amadeus.response.TokenResponse;
import amadeuslms.amadeus.utils.HttpUtils;

/**
 * Created by zambom on 30/06/17.
 */

public class SubjectBO {

    public SubjectResponse get_subjects(Context context, UserModel user) throws Exception {
        TokenResponse token = TokenCacheController.getTokenCache(context);

        StringBuilder url = new StringBuilder();
        url.append(token.getWebserver_url());
        url.append("/api/subjects/get_subjects/");

        Map<String, String> data = new HashMap<String, String>();
        data.put("email", user.getEmail());

        JSONObject content = new JSONObject(data);

        String json = HttpUtils.post(context, url.toString(), content.toString(), token.getToken_type() + " " + token.getAccess_token());

        if (json != null && json.trim().length() > 0) {
            Type type = new TypeToken<SubjectResponse>(){}.getType();

            return new Gson().fromJson(json, type);
        }

        return null;
    }
}
