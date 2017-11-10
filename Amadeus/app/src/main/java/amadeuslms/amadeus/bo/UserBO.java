/*
Copyright 2016, 2017 UFPE - Universidade Federal de Pernambuco
 
Este arquivo é parte do programa Amadeus Sistema de Gestão de Aprendizagem, ou simplesmente Amadeus LMS
 
O Amadeus LMS é um software livre; você pode redistribui-lo e/ou modifica-lo dentro dos termos da Licença Pública Geral GNU como publicada pela Fundação do Software Livre (FSF); na versão 2 da Licença.
 
Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU para maiores detalhes.
 
Você deve ter recebido uma cópia da Licença Pública Geral GNU, sob o título "LICENSE", junto com este programa, se não, escreva para a Fundação do Software Livre (FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.
*/
package amadeuslms.amadeus.bo;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import amadeuslms.amadeus.bean.ApplicationProperties;
import amadeuslms.amadeus.cache.TokenCacheController;
import amadeuslms.amadeus.models.UserModel;
import amadeuslms.amadeus.response.GenericResponse;
import amadeuslms.amadeus.response.TokenResponse;
import amadeuslms.amadeus.response.UserResponse;
import amadeuslms.amadeus.utils.HttpUtils;

/**
 * Created by zambom on 16/06/17.
 */

public class UserBO {

    public UserResponse login(Context context, String host, String email, String password) throws Exception {

        StringBuilder url = new StringBuilder();
        url.append(host);
        url.append("/api/token");

        ApplicationProperties.setWebServiceURL(context, host);

        Map<String, String> data = new HashMap<String, String>();
        data.put("email", email);
        data.put("password", password);

        JSONObject content = new JSONObject(data);

        String json = HttpUtils.post(context, url.toString(), content.toString(), "");

        if (json != null && json.trim().length() > 0) {
            System.out.println(json);
            Type type = new TypeToken<TokenResponse>(){}.getType();

            TokenResponse token = new Gson().fromJson(json, type);

            if (token != null) {
                token.setData(email, password);
                token.setTime_stamp();
                token.setWebserver_url(host);

                TokenCacheController.setTokenCache(context, token);

                url = new StringBuilder();
                url.append(host);
                url.append("/api/users/login/");

                json = HttpUtils.post(context, url.toString(), content.toString(), token.getToken_type() + " " + token.getAccess_token());

                if (json != null && json.trim().length() > 0) {
                    System.out.println(json);
                    type = new TypeToken<UserResponse>() {
                    }.getType();

                    return new Gson().fromJson(json, type);
                }
            }
        }

        return null;
    }

    public GenericResponse registerDevice(Context context, UserModel user, String device) throws Exception {
        TokenResponse token = TokenCacheController.getTokenCache(context);

        StringBuilder url = new StringBuilder();
        url.append(token.getWebserver_url());
        url.append("/api/users/register_device/");

        Map<String, String> data = new HashMap<String, String>();
        data.put("email", user.getEmail());
        data.put("device", device);

        JSONObject content = new JSONObject(data);

        String json = HttpUtils.post(context, url.toString(), content.toString(), token.getToken_type() + " " + token.getAccess_token());

        if (json != null && json.trim().length() > 0) {
            Type type = new TypeToken<GenericResponse>(){}.getType();

            return new Gson().fromJson(json, type);
        }
        return null;
    }
}
