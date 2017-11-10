/*
Copyright 2016, 2017 UFPE - Universidade Federal de Pernambuco
 
Este arquivo é parte do programa Amadeus Sistema de Gestão de Aprendizagem, ou simplesmente Amadeus LMS
 
O Amadeus LMS é um software livre; você pode redistribui-lo e/ou modifica-lo dentro dos termos da Licença Pública Geral GNU como publicada pela Fundação do Software Livre (FSF); na versão 2 da Licença.
 
Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU para maiores detalhes.
 
Você deve ter recebido uma cópia da Licença Pública Geral GNU, sob o título "LICENSE", junto com este programa, se não, escreva para a Fundação do Software Livre (FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.
*/
package amadeuslms.amadeus.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.view.Display;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import amadeuslms.amadeus.helpers.FileCacheHelper;
import amadeuslms.amadeus.helpers.MemoryCacheHelper;

/**
 * Created by zambom on 23/06/17.
 */

public class ImageUtils {

    Handler handler = new Handler();
    MemoryCacheHelper memoryCache = new MemoryCacheHelper();

    FileCacheHelper  fileCache;
    ExecutorService executorService;

    private Context context;
    private Map<ImageView, String> imgViews = Collections.synchronizedMap(new HashMap<ImageView, String>());

    public ImageUtils(Context context) {
        this.context = context;

        fileCache = new FileCacheHelper(context);

        executorService = Executors.newFixedThreadPool(5);
    }

    public static Point getDisplaySize(Display display) {
        Point size = new Point();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
            display.getSize(size);
        }else{
            int width = display.getWidth();
            int height = display.getHeight();
            size = new Point(width, height);
        }

        return size;
    }

    public void DisplayImage(String url, ImageView imageView, boolean rounder, int round){
        imgViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);

        if(bitmap != null){
            if(rounder){
                imageView.setImageBitmap(roundCornerImage(bitmap, round));
            }else{
                imageView.setImageBitmap(bitmap);
            }
        }else{
            queuePhoto(url, imageView, rounder, round);
        }
    }

    public static Bitmap roundCornerImage(Bitmap src, int round){
        Bitmap sbmp;

        if(round != 0 && (src.getWidth() != round || src.getHeight() != round)){
            if(src.getWidth() > src.getHeight()) {
                float ratio_img = src.getWidth() / src.getHeight();
                sbmp = ratio_img > 0.0 ? Bitmap.createScaledBitmap(src, round, (int) (round / ratio_img), true) : src;
            }else{
                float ratio_img = src.getHeight() / src.getWidth();
                sbmp = ratio_img > 0.0 ? Bitmap.createScaledBitmap(src, (int) (round / ratio_img), round, true) : src;
            }
        }else{
            sbmp = src;
        }

        int width = sbmp.getWidth();
        int height = sbmp.getHeight();

        Bitmap result = Bitmap.createBitmap(round, round, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(result);
        canvas.drawARGB(0, 0, 0, 0);

        final Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        canvas.drawARGB(0, 0, 0, 0);

        paint.setStrokeWidth(10);
        paint.setColor(Color.WHITE);

        canvas.drawCircle(round / 2, round / 2, round / 2 + 0.1f, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

        canvas.drawBitmap(sbmp, ((round - width) / 2), ((round - height) / 2), paint);

        paint.setStyle(Paint.Style.STROKE);

        canvas.drawCircle(round / 2,  round / 2, round / 2 + 0.1f, paint);

        return result;
    }

    private void queuePhoto(String url, ImageView imageView, boolean rounder, int round){
        PhotoToLoad p = new PhotoToLoad(url, imageView, rounder, round);
        executorService.submit(new PhotosLoader(p));
    }

    public Bitmap getBitmap(String url, boolean rounder, int round){
        File f = fileCache.getFile(url);

        Bitmap b = decodeFile(f, rounder, round);

        if (b != null) {
            return b;
        }else{
            f = new File(context.getCacheDir(), "image" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        }

        try{
            Bitmap bitmap = null;

            URL imageUrl = new URL(url);

            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);

            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);

            FileUtils.CopyStream(is, os);
            os.close();

            bitmap = decodeFile(f, rounder, round);

            return bitmap;
        }catch(OutOfMemoryError e) {
            memoryCache.clear();

            return null;
        }catch(Exception e){
            e.printStackTrace();

            return null;
        }
    }

    private Bitmap decodeFile(File f, boolean rounder, int round){
        try{
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            FileInputStream stream1 = new FileInputStream(f);

            BitmapFactory.decodeStream(stream1, null, o);

            stream1.close();

            final int REQUIRED_SIZE = 300;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;

            if(height_tmp > REQUIRED_SIZE || width_tmp > REQUIRED_SIZE){
                final int halfHeight = height_tmp / 2;
                final int halfWidth = width_tmp / 2;

                while((halfHeight / scale) > REQUIRED_SIZE && (halfWidth / scale) > REQUIRED_SIZE){
                    scale *= 2;
                }
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            FileInputStream stream2 = new FileInputStream(f);

            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);

            stream2.close();

            if(rounder){
                bitmap = roundCornerImage(bitmap, round);
            }

            return bitmap;
        }catch(FileNotFoundException e) {
            System.out.println("File not found");
        }catch(IOException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private class PhotoToLoad{

        public String url;
        public ImageView imageView;
        public boolean rounder;
        public int round;

        public PhotoToLoad(String url, ImageView imageView, boolean rounder, int round){
            this.url = url;
            this.imageView = imageView;
            this.rounder = rounder;
            this.round = round;
        }
    }

    class PhotosLoader implements Runnable{

        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run(){
            if(imageViewReused(photoToLoad)){
                return;
            }

            Bitmap bmp = getBitmap(photoToLoad.url, photoToLoad.rounder, photoToLoad.round);

            memoryCache.put(photoToLoad.url, bmp);

            if(imageViewReused(photoToLoad)){
                return;
            }

            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);

            handler.post(bd);
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag = imgViews.get(photoToLoad.imageView);

        if (tag == null || !tag.equals(photoToLoad.url)) {
            return true;
        }

        return false;
    }

    class BitmapDisplayer implements Runnable{
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap bitmap, PhotoToLoad photoToLoad){
            this.bitmap = bitmap;
            this.photoToLoad = photoToLoad;
        }

        public void run(){
            if(imageViewReused(photoToLoad)){
                return;
            }

            if(bitmap != null){
                photoToLoad.imageView.setImageBitmap(bitmap);
            }else{
                try{
                    final InputStream is = photoToLoad.imageView.getContext().getAssets().open("images/no_image.png");

                    if(photoToLoad.rounder){
                        Bitmap bmp = BitmapFactory.decodeStream(is);
                        bmp = roundCornerImage(bmp, photoToLoad.round);

                        photoToLoad.imageView.setImageBitmap(bmp);
                    }else{
                        Drawable d = Drawable.createFromStream(is, null);

                        photoToLoad.imageView.setImageDrawable(d);
                    }
                }catch(IOException e){
                    System.out.println("Error bitmap display: " + e.getMessage());
                }
            }
        }
    }

    public void clearCache(){
        memoryCache.clear();
        fileCache.clear();
    }
}
