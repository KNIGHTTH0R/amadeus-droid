package amadeuslms.amadeus.utils;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zambom on 16/06/17.
 */

public class HttpUtils {

    public static String post(String address, String json, String token) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

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

    public static String postMultipart(String address, String json, String token, Uri destination) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request.Builder builder = new Request.Builder();

        builder.url(address);

        if (token != "") {
            builder.addHeader("Authorization", token);
        }

        MediaType mediaImage = MediaType.parse("image/*");

        File image = new File(destination.getPath());

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", image.getName(), RequestBody.create(mediaImage, image))
                .addFormDataPart("data", json)
                .build();

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
