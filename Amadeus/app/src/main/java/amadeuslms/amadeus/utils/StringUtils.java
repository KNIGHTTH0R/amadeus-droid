package amadeuslms.amadeus.utils;

import android.os.Build;
import android.text.Html;

/**
 * Created by zambom on 20/07/17.
 */

public class StringUtils {

    public static String stripTags(String html) {
        return Html.fromHtml(html).toString().replaceAll("\n", "").trim();
    }
}
