package amadeuslms.amadeus.bo;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import amadeuslms.amadeus.bean.ApplicationProperties;
import amadeuslms.amadeus.response.UserResponse;
import amadeuslms.amadeus.utils.HttpUtils;

/**
 * Created by zambom on 16/06/17.
 */

public class UserBO {

    public UserResponse login(Context context, String host, String email, String password) throws Exception {

        StringBuilder url = new StringBuilder();
        url.append(host);
        url.append("login");

        ApplicationProperties.setWebServiceURL(context, host);

        String json = HttpUtils.post(url.toString(), "");

        if (json != null && json.trim().length() > 0) {
            Type type = new TypeToken<UserResponse>(){}.getType();

            return new Gson().fromJson(json, type);
        }

        return null;
    }
}
