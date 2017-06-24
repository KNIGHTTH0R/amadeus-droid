package amadeuslms.amadeus.cache;

import android.content.Context;

/**
 * Created by zambom on 23/06/17.
 */

public class CacheController {

    public static void clearCache(Context context) {
        UserCacheController.removeUserCache(context);
        TokenCacheController.removeTokenCache(context);
    }
}
