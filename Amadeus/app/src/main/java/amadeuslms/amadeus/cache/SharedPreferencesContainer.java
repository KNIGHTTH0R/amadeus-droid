package amadeuslms.amadeus.cache;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zambom on 22/06/17.
 */

public class SharedPreferencesContainer {

    private static final String PREFERENCES_NAME = "AMADEUS_PREFERENCES";

    private static SharedPreferences sharedPreferences;

    private static SharedPreferences.Editor editor;

    public static SharedPreferences getSharedPreferences(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public static SharedPreferences.Editor getEditor(Context context) {
        if (editor == null) {
            editor = getSharedPreferences(context).edit();
        }

        return editor;
    }
}
