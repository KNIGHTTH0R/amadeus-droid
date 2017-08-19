package amadeuslms.amadeus.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import amadeuslms.amadeus.R;
import amadeuslms.amadeus.bo.UserBO;
import amadeuslms.amadeus.cache.UserCacheController;
import amadeuslms.amadeus.models.UserModel;
import amadeuslms.amadeus.response.GenericResponse;

/**
 * Created by zambom on 04/08/17.
 */

public class InstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseIIDService";
    public static final String USER_ID_KEY_TOKEN = "USER_ID_KEY_TOKEN";

    @Override
    public void onTokenRefresh() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String refresh_token = FirebaseInstanceId.getInstance().getToken();

        UserModel user = UserCacheController.getUserCache(getApplicationContext());

        String id = sharedPreferences.getString(USER_ID_KEY_TOKEN, "");

        synchronized (TAG) {
            if (TextUtils.isEmpty(id) || (user != null && !id.equals(user.getEmail()))) {
                sendRegistrationServer(refresh_token, user);
            }
        }
    }

    private void sendRegistrationServer(String refresh_token, UserModel user) {
        new RegisterDevice(getApplicationContext(), user, refresh_token).execute();
    }

    public void sendRegistrationServer(Context context, UserModel user) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String id = sharedPreferences.getString(USER_ID_KEY_TOKEN, "");

        synchronized (TAG) {
            if (TextUtils.isEmpty(id) || (user != null && !id.equals(user.getEmail()))) {
                String token = FirebaseInstanceId.getInstance().getToken();

                new RegisterDevice(context, user, token).execute();
            }
        }
    }

    private class RegisterDevice extends AsyncTask<Void, Void, GenericResponse> {
        private Context context;
        private UserModel user;
        private String title, message, device;

        public RegisterDevice(Context context, UserModel user, String device) {
            this.context = context;
            this.user = user;
            this.device = device;
        }

        @Override
        protected GenericResponse doInBackground(Void... params) {
            try {
                return new UserBO().registerDevice(context, user, device);
            } catch (Exception e) {
                title = context.getString(R.string.error_box_title);
                message = context.getString(R.string.error_box_msg) + " " + e.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(GenericResponse genericResponse) {
            super.onPostExecute(genericResponse);

            if(genericResponse != null){
                if(genericResponse.getSuccess() && genericResponse.getNumber() == 1){
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                    editor.putString(USER_ID_KEY_TOKEN, user.getEmail());
                    editor.commit();
                }else if(!TextUtils.isEmpty(genericResponse.getTitle()) && !TextUtils.isEmpty(genericResponse.getMessage())){
                    title = genericResponse.getTitle();
                    message = genericResponse.getMessage();
                }
            }

            if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(message)) {
                System.out.println(title);
                System.out.println(message);

            }
        }
    }
}
