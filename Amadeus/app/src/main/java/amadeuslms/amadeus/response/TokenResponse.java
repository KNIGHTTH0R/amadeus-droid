/*
Copyright 2016, 2017 UFPE - Universidade Federal de Pernambuco
 
Este arquivo é parte do programa Amadeus Sistema de Gestão de Aprendizagem, ou simplesmente Amadeus LMS
 
O Amadeus LMS é um software livre; você pode redistribui-lo e/ou modifica-lo dentro dos termos da Licença Pública Geral GNU como publicada pela Fundação do Software Livre (FSF); na versão 2 da Licença.
 
Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU para maiores detalhes.
 
Você deve ter recebido uma cópia da Licença Pública Geral GNU, sob o título "LICENSE", junto com este programa, se não, escreva para a Fundação do Software Livre (FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.
*/
package amadeuslms.amadeus.response;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.lang.System;

import amadeuslms.amadeus.R;
import amadeuslms.amadeus.activity.LoginActivity;
import amadeuslms.amadeus.bo.UserBO;
import amadeuslms.amadeus.cache.CacheController;
import amadeuslms.amadeus.cache.TokenCacheController;
import amadeuslms.amadeus.cache.UserCacheController;
import amadeuslms.amadeus.utils.HttpUtils;

/**
 * Created by zambom on 16/06/17.
 */

public class TokenResponse extends GenericResponse {

    private String token_type, refresh_token, access_token, scope, webserver_url, email, password;
    private int expires_in;
    private long time_stamp;

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getWebserver_url() {
        return webserver_url;
    }

    public void setWebserver_url(String webserver_url) {
        this.webserver_url = webserver_url;
    }

    public long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp() {
        this.time_stamp = System.currentTimeMillis();
    }

    public void setData(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public boolean isToken_expired() {
        return (System.currentTimeMillis() - time_stamp)/1000 >= expires_in - 1800;
        //1800 will work like a tolerance, allowing token to be renovate before it's expire, reducing chances of some error.
        //You can change this value anytime. Actually it's 5% of the time of a token.
    }

    public void startRenewToken(Intent intent, Context context) {
        RenewToken mAuthTask = null;
        CacheController.clearCache(context);
        mAuthTask = new RenewToken(intent, context);
        mAuthTask.execute((Void) null);
    }

    private class RenewToken extends AsyncTask<Void, Void, UserResponse> {

        private Intent intent;
        private Context context;

        public RenewToken(Intent intent, Context context) {
            this.intent = intent;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected UserResponse doInBackground(Void... params) {
            try {
                return new UserBO().login(context, webserver_url, email, password);
            } catch (Exception e) {
                System.out.println(e);
                goLogin(context);
            }

            return null;
        }

        @Override
        protected void onPostExecute(UserResponse userResponse) {
            super.onPostExecute(userResponse);

            if (userResponse != null) {
                if (userResponse.getSuccess() && userResponse.getNumber() == 1) {
                    UserCacheController.setUserCache(context, userResponse.getData());
                    context.startActivity(intent);
                }
            }
        }
    }

    public void startRenewToken_ForResult(Intent intent, Context context, int integer) {
        RenewToken_ForResult mAuthTask = null;
        CacheController.clearCache(context);
        mAuthTask = new RenewToken_ForResult(intent, context, integer);
        mAuthTask.execute((Void) null);
    }

    private class RenewToken_ForResult extends AsyncTask<Void, Void, UserResponse> {

        private Intent intent;
        private Context context;
        private int integer;

        public RenewToken_ForResult(Intent intent, Context context, int integer) {
            this.intent = intent;
            this.context = context;
            this.integer = integer;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected UserResponse doInBackground(Void... params) {
            try {
                return new UserBO().login(context, webserver_url, email, password);
            } catch (Exception e) {
                System.out.println(e);
                goLogin(context);
            }

            return null;
        }

        @Override
        protected void onPostExecute(UserResponse userResponse) {
            super.onPostExecute(userResponse);

            if (userResponse != null) {
                if (userResponse.getSuccess() && userResponse.getNumber() == 1) {
                    UserCacheController.setUserCache(context, userResponse.getData());
                    ((Activity)context).startActivityForResult(intent, integer);
                }
            }
        }
    }

    public void goLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        (context).startActivity(intent);
    }
}
