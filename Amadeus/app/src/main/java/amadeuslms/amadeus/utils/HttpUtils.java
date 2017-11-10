/*
Copyright 2016, 2017 UFPE - Universidade Federal de Pernambuco
 
Este arquivo é parte do programa Amadeus Sistema de Gestão de Aprendizagem, ou simplesmente Amadeus LMS
 
O Amadeus LMS é um software livre; você pode redistribui-lo e/ou modifica-lo dentro dos termos da Licença Pública Geral GNU como publicada pela Fundação do Software Livre (FSF); na versão 2 da Licença.
 
Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU para maiores detalhes.
 
Você deve ter recebido uma cópia da Licença Pública Geral GNU, sob o título "LICENSE", junto com este programa, se não, escreva para a Fundação do Software Livre (FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.
*/
package amadeuslms.amadeus.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

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

    public static String post(Context context, String address, String json, String token) throws IOException {
        SSLContext sslContext;
        TrustManager[] trustManagers;

        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            InputStream certInputStream = context.getAssets().open("server.pem");
            BufferedInputStream bis = new BufferedInputStream(certInputStream);
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

            while (bis.available() > 0) {
                Certificate cert = certificateFactory.generateCertificate(bis);
                keyStore.setCertificateEntry("www.amadeuslms.univasf.edu.br", cert);
            }

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            trustManagers = trustManagerFactory.getTrustManagers();

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, null);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .sslSocketFactory(sslContext.getSocketFactory())
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
