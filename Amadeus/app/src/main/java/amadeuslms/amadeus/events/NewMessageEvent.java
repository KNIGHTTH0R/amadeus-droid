package amadeuslms.amadeus.events;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Map;

import amadeuslms.amadeus.response.MessageResponse;

/**
 * Created by zambom on 04/09/17.
 */

public class NewMessageEvent {

    public final MessageResponse response;

    public NewMessageEvent(final Map<String, String> data) {
        Type type = new TypeToken<MessageResponse>(){}.getType();

        this.response = new Gson().fromJson(data.get("response").toString(), type);
    }
}
