package amadeuslms.amadeus.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by zambom on 16/06/17.
 */

public class UserModel implements Parcelable {

    private String email, social_name, username, last_name, image_url, description, last_update, date_created;
    private boolean is_staff, is_active;

    public UserModel(Parcel in) {
        String[] data = new String[10];

        in.readStringArray(data);

        this.setEmail(data[0]);
        this.setSocial_name(data[1]);
        this.setUsername(data[2]);
        this.setLast_name(data[3]);
        this.setImage_url(data[4]);
        this.setDescription(data[5]);
        this.setLast_update(data[6]);
        this.setDate_created(data[7]);
        this.setIs_staff(Boolean.parseBoolean(data[8]));
        this.setIs_active(Boolean.parseBoolean(data[9]));
    }

    public static final Parcelable.Creator<UserModel> CREATOR = new Parcelable.Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel source) {
            return new UserModel(source);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSocial_name() {
        return social_name;
    }

    public void setSocial_name(String social_name) {
        this.social_name = social_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public boolean is_staff() {
        return is_staff;
    }

    public void setIs_staff(boolean is_staff) {
        this.is_staff = is_staff;
    }

    public boolean is_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public String getDisplayName() {
        if (TextUtils.isEmpty(social_name)) {
            return username + " " + last_name;
        }

        return social_name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.getEmail(),
                this.getSocial_name(),
                this.getUsername(),
                this.getLast_name(),
                this.getImage_url(),
                this.getDescription(),
                this.getLast_update(),
                this.getDate_created(),
                String.valueOf(this.is_staff()),
                String.valueOf(this.is_active())
        });
    }
}
