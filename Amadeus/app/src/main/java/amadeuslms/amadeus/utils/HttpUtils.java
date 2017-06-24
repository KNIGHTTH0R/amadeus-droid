package amadeuslms.amadeus.utils;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zambom on 16/06/17.
 */

public class HttpUtils {

    public static String post(String address, String json, String token) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request.Builder builder = new Request.Builder();

        builder.url(address);

        if (token != "") {
            builder.addHeader("Authorization", token);
        }

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(mediaType, json);

        builder.post(body);

        Request request = builder.build();

        Response response = client.newCall(request).execute();

        String jsonResponse = response.body().string();

        return jsonResponse;
    }

    public static String get(String address) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(address).build();

        Response response = client.newCall(request).execute();

        String jsonResponse = response.body().string();

        return jsonResponse;
    }
}
