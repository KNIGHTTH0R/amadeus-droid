package amadeuslms.amadeus.response;

import java.lang.System;

/**
 * Created by zambom on 16/06/17.
 */

public class TokenResponse extends GenericResponse {

    private String token_type, refresh_token, access_token, scope, webserver_url;
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

    public boolean isToken_expired() {
        return (System.currentTimeMillis() - time_stamp)/1000 >= expires_in; 
    }
}
