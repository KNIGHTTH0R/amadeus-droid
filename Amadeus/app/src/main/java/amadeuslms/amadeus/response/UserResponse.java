package amadeuslms.amadeus.response;

import amadeuslms.amadeus.models.UserModel;

/**
 * Created by zambom on 16/06/17.
 */

public class UserResponse extends GenericResponse {

    private UserModel data;
    private String image;

    public UserModel getData() {
        return data;
    }

    public void setData(UserModel data) {
        this.data = data;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
