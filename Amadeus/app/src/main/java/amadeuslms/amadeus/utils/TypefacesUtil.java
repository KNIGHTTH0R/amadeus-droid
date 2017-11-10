/*
Copyright 2016, 2017 UFPE - Universidade Federal de Pernambuco
 
Este arquivo é parte do programa Amadeus Sistema de Gestão de Aprendizagem, ou simplesmente Amadeus LMS
 
O Amadeus LMS é um software livre; você pode redistribui-lo e/ou modifica-lo dentro dos termos da Licença Pública Geral GNU como publicada pela Fundação do Software Livre (FSF); na versão 2 da Licença.
 
Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU para maiores detalhes.
 
Você deve ter recebido uma cópia da Licença Pública Geral GNU, sob o título "LICENSE", junto com este programa, se não, escreva para a Fundação do Software Livre (FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.
*/
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
