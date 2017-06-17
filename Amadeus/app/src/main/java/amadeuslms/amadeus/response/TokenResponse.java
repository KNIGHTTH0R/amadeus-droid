package amadeuslms.amadeus.response;

/**
 * Created by zambom on 16/06/17.
 */

public class TokenResponse extends GenericResponse {

    private String type_token, access, refresh, scope;
    private int expires;

    @Override
    public String getType() {
        return type_token;
    }

    @Override
    public void setType(String type) {
        this.type_token = type;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getRefresh() {
        return refresh;
    }

    public void setRefresh(String refresh) {
        this.refresh = refresh;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public int getExpires() {
        return expires;
    }

    public void setExpires(int expires) {
        this.expires = expires;
    }
}
