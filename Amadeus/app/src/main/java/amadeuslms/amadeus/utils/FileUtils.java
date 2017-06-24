package amadeuslms.amadeus.utils;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zambom on 23/06/17.
 */

public class FileUtils {

    public static void CopyStream(InputStream is, OutputStream os){
        final int buffer_size = 1024;

        try{
            byte[] bytes = new byte[buffer_size];

            for(;;){
                int count = is.read(bytes, 0, buffer_size);

                if(count == -1){
                    break;
                }

                os.write(bytes, 0, count);
            }
        }catch(Exception e){
            System.out.println("Erro: "+e.getMessage());
        }
    }

    public static String StringFileName(String filename){
        filename = filename.toLowerCase();
        filename = filename.replaceAll("([^a-zA-Z]|\\s)", " ");
        filename = filename.replaceAll(" ","_");

        return filename;
    }

    public static String fileExt(String url){
        if(url.indexOf("?") > -1){
            url = url.substring(0, url.indexOf("?"));
        }

        if(url.lastIndexOf(".") == -1){
            return null;
        }else{
            String ext = url.substring(url.lastIndexOf("."));

            if(ext.indexOf("%") > -1){
                ext = ext.substring(0, ext.indexOf("%"));
            }

            if(ext.indexOf("/") > -1){
                ext = ext.substring(0, ext.indexOf("/"));
            }

            return ext.toLowerCase();
        }
    }
}
