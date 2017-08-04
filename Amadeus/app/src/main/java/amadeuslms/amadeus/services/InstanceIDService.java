package amadeuslms.amadeus.services;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by zambom on 04/08/17.
 */

public class InstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        String refresh_token = FirebaseInstanceId.getInstance().getToken();
        
        sendRegistrationServer(refresh_token);
    }

    private void sendRegistrationServer(String refresh_token) {

    }
}
