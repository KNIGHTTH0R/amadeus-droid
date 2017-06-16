package amadeuslms.amadeus.bean;

import android.content.Context;

import java.io.InputStream;
import java.util.Properties;

import amadeuslms.amadeus.R;

/**
 * Created by zambom on 16/06/17.
 */

public class ApplicationProperties {

    private static Properties applicationProperties;

    public static void getApplicationProperties(Context context) {
        if (applicationProperties == null) {
            try {
                applicationProperties = new Properties();

                InputStream rawSource = context.getResources().openRawResource(R.raw.application);
                applicationProperties.load(rawSource);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static String getWebServiceURL(Context context){
        getApplicationProperties(context);

        return applicationProperties.getProperty("webservice.url");
    }

    public static void setWebServiceURL(Context context, String url) {
        getApplicationProperties(context);

        applicationProperties.setProperty("webservice.url", url);
    }
}
