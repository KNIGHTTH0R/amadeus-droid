/*
Copyright 2016, 2017 UFPE - Universidade Federal de Pernambuco
 
Este arquivo é parte do programa Amadeus Sistema de Gestão de Aprendizagem, ou simplesmente Amadeus LMS
 
O Amadeus LMS é um software livre; você pode redistribui-lo e/ou modifica-lo dentro dos termos da Licença Pública Geral GNU como publicada pela Fundação do Software Livre (FSF); na versão 2 da Licença.
 
Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU para maiores detalhes.
 
Você deve ter recebido uma cópia da Licença Pública Geral GNU, sob o título "LICENSE", junto com este programa, se não, escreva para a Fundação do Software Livre (FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.
*/
package amadeuslms.amadeus.bo;

import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
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

    public MessageResponse get_messages(Context context, UserModel user, UserModel user_to, int page, int page_size) throws Exception {
        TokenResponse token = TokenCacheController.getTokenCache(context);

        StringBuilder url = new StringBuilder();
        url.append(token.getWebserver_url());
        url.append("/api/chat/get_messages/");

        Map<String, String> data = new HashMap<String, String>();
        data.put("email", user.getEmail());
        data.put("user_two", user_to.getEmail());
        data.put("page", String.valueOf(page)); //Number of page to request, first page is 1
        data.put("page_size", String.valueOf(page_size));

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

    public MessageResponse favorite_messages(Context context, UserModel user, List<MessageModel> message, boolean favor) throws Exception {
        TokenResponse token = TokenCacheController.getTokenCache(context);

        StringBuilder url = new StringBuilder();
        url.append(token.getWebserver_url());
        url.append("/api/chat/favorite_messages/");

        Map<String, String> data = new HashMap<String, String>();
        data.put("email", user.getEmail());
        data.put("list_size", String.valueOf(message.size()));
        data.put("favor", String.valueOf(favor));

        for(int i = 0; i < message.size(); ++i) {
            data.put(String.valueOf(i), String.valueOf(message.get(i).getId()));
        }

        JSONObject content = new JSONObject(data);

        String json = HttpUtils.post(context, url.toString(), content.toString(), token.getToken_type() + " " + token.getAccess_token());

        if (json != null && json.trim().length() > 0) {
            System.out.println(json);
            Type type = new TypeToken<MessageResponse>(){}.getType();

            return new Gson().fromJson(json, type);
        }

        return null;
    }
}
