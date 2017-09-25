package amadeuslms.amadeus.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import amadeuslms.amadeus.cache.TokenCacheController;
import amadeuslms.amadeus.cache.UserCacheController;

/**
 * Created by zambom on 09/06/17.
 */

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIMEOUT = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context context = this;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = null;

                if (!UserCacheController.hasUserCache(context)) {
                    intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    intent = new Intent(context, HomeActivity.class);
                    if(TokenCacheController.hasTokenCache(context) && TokenCacheController.getTokenCache(context).isToken_expired()) {
                        TokenCacheController.getTokenCache(context).startRenewToken(intent, context);
                    } else {
                        startActivity(intent);
                        finish();
                    }
                }

            }
        }, SPLASH_TIMEOUT);

    }
}
