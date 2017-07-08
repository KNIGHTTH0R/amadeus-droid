package amadeuslms.amadeus.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.TextView;

import java.util.Hashtable;

/**
 * Created by zambom on 07/07/17.
 */

public class TypefacesUtil {

    private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

    public static Typeface get(Context c, String assetPath){
        synchronized (cache){
            if(!cache.containsKey(assetPath)){
                try{
                    Typeface typeface = Typeface.createFromAsset(c.getAssets(), assetPath);

                    cache.put(assetPath, typeface);
                }catch(Exception e){
                    System.out.println("Erro ao colocar font no cache: "+e.getMessage());
                }
            }

            return cache.get(assetPath);
        }
    }

    public static void setFontAwesome(Context context, Button btn){
        if(btn.getTypeface() == null || (btn.getTypeface() != null && !btn.getTypeface().equals(TypefacesUtil.get(context, "fonts/fontawesome.ttf")))){
            btn.setTypeface(TypefacesUtil.get(context, "fonts/fontawesome.ttf"));
        }
    }

    public static void setFontAwesome(Context context, TextView txt){
        if(txt.getTypeface() == null || (txt.getTypeface() != null && !txt.getTypeface().equals(TypefacesUtil.get(context, "fonts/fontawesome.ttf")))){
            txt.setTypeface(TypefacesUtil.get(context, "fonts/fontawesome.ttf"));
        }
    }
}
