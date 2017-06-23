package amadeuslms.amadeus.response;

import amadeuslms.amadeus.models.UserModel;

/**
 * Created by zambom on 16/06/17.
 */

public class UserResponse extends GenericResponse {

    private UserModel data;


    public UserModel getData() {
        return data;
    }

    public void setData(UserModel data) {
        this.data = data;
    }
}
