package amadeuslms.amadeus.helpers;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by zambom on 23/06/17.
 */

public class FileCacheHelper {

    private File cacheDir;

    public FileCacheHelper(Context context) {
        if (android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "tempImg");
        } else {
            cacheDir = context.getCacheDir();
        }

        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }

    public File getFile(String url) {
        String filename = String.valueOf(url.hashCode());

        File file = new File(cacheDir, filename);

        return file;
    }

    public void clear() {
        File[] files = cacheDir.listFiles();

        if (files == null) {
            return;
        }

        for (File file:files) {
            file.delete();
        }
    }
}
