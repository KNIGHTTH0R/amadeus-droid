package amadeuslms.amadeus.services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.util.Map;

import amadeuslms.amadeus.events.NewMessageEvent;

/**
 * Created by zambom on 04/08/17.
 */

public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> data = remoteMessage.getData();

        EventBus.getDefault().post(new NewMessageEvent(data));
    }
}
