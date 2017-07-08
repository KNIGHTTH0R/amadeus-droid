package amadeuslms.amadeus.models;

import java.util.List;

/**
 * Created by zambom on 30/06/17.
 */

public class SubjectModel {

    private String name, slug;
    private boolean visible;

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
}
