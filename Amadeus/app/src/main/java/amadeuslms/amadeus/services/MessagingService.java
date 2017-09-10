package amadeuslms.amadeus.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;

import amadeuslms.amadeus.R;
import amadeuslms.amadeus.activity.ChatActivity;
import amadeuslms.amadeus.cache.TokenCacheController;
import amadeuslms.amadeus.events.NewMessageEvent;
import amadeuslms.amadeus.response.MessageResponse;

/**
 * Created by zambom on 04/08/17.
 */

public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> data = remoteMessage.getData();

        String type = data.get("type").toString();

        if (type.equals("chat")) {
            String user_talk = data.get("user_from").toString();

            if (ChatActivity.IS_ON_TOP && ChatActivity.talk_user.equals(user_talk)) {
                EventBus.getDefault().post(new NewMessageEvent(data));
            } else {
                setNotification(remoteMessage);
            }
        } else if (type.equals("mural")) {
            setMuralNotification(data);
        } else if (type.equals("pendency")) {
            setPendencyNotification(data);
        }
    }

    private void setNotification(RemoteMessage remoteMessage) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        Map<String, String> data = remoteMessage.getData();

        Type type = new TypeToken<MessageResponse>(){}.getType();

        MessageResponse response = new Gson().fromJson(data.get("response").toString(), type);

        String user_img = data.get("user_img").toString();

        builder.setTicker(data.get("title").toString());
        builder.setContentTitle(data.get("user_name").toString());
        builder.setContentText(data.get("body").toString());
        builder.setSmallIcon(R.drawable.ic_logo_vector_white);
        builder.setAutoCancel(true);

        if (!TextUtils.isEmpty(user_img)) {
            String path = TokenCacheController.getTokenCache(this).getWebserver_url() + user_img;

            try {
                builder.setLargeIcon(Picasso.with(this).load(path).get());
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            try{
                final InputStream is = this.getAssets().open("images/no_image.png");

                Bitmap bmp = BitmapFactory.decodeStream(is);

                builder.setLargeIcon(bmp);
            }catch(IOException e){
                System.out.println("Error: " + e.getMessage());
            }
        }

        long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};

        builder.setVibrate(pattern);

        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.USER_TO, response.getData().getMessage_sent().getUser());
        intent.putExtra(ChatActivity.SUBJECT, response.getData().getMessage_sent().getSubject());
        intent.putExtra("FROM_NOTIFICATION", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(6534, builder.build());
    }

    private void setMuralNotification(Map<String, String> data) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        String user_img = data.get("user_img").toString();

        builder.setContentTitle(data.get("title").toString());
        builder.setContentText(data.get("body").toString());
        builder.setSmallIcon(R.drawable.ic_logo_vector_white);
        builder.setAutoCancel(true);

        if (!TextUtils.isEmpty(user_img)) {
            String path = TokenCacheController.getTokenCache(this).getWebserver_url() + user_img;

            try {
                builder.setLargeIcon(Picasso.with(this).load(path).get());
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            try{
                final InputStream is = this.getAssets().open("images/no_image.png");

                Bitmap bmp = BitmapFactory.decodeStream(is);

                builder.setLargeIcon(bmp);
            }catch(IOException e){
                System.out.println("Error: " + e.getMessage());
            }
        }

        long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};

        builder.setVibrate(pattern);

        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(6535, builder.build());
    }

    private void setPendencyNotification(Map<String, String> data) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        int notify = Integer.parseInt(data.get("body").toString());

        builder.setContentTitle(getString(R.string.pendency_notify_title));
        builder.setContentText(getResources().getQuantityString(R.plurals.pendency_notify_message, notify, notify));
        builder.setSmallIcon(R.drawable.ic_logo_vector_white);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo_vector));
        builder.setAutoCancel(true);

        long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};

        builder.setVibrate(pattern);

        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(6536, builder.build());
    }
}
