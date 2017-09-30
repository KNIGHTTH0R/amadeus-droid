package amadeuslms.amadeus.models;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by zambom on 30/06/17.
 */

public class SubjectModel implements Parcelable {

    private String name, slug;
    private boolean visible;
    private int notifications;

    public SubjectModel(Parcel in) {
        String[] data = new String[4];

        in.readStringArray(data);

        this.setName(data[0]);
        this.setSlug(data[1]);
        this.setVisible(Boolean.parseBoolean(data[2]));
        this.setNotifications(Integer.parseInt(data[3]));
    }

    public SubjectModel(String name, String slug, boolean visible, int notifications) {
        this.name = name;
        this.slug = slug;
        this.visible = visible;
        this.notifications = notifications;
    }

    public static final Parcelable.Creator<SubjectModel> CREATOR = new Parcelable.Creator<SubjectModel>() {
        @Override
        public SubjectModel createFromParcel(Parcel source) {
            return new SubjectModel(source);
        }

        @Override
        public SubjectModel[] newArray(int size) {
            return new SubjectModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getNotifications() {
        return notifications;
    }

    public void setNotifications(int notifications) {
        this.notifications = notifications;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.getName(),
                this.getSlug(),
                String.valueOf(this.isVisible()),
                String.valueOf(this.getNotifications())
        });
    }
}
