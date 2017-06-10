package amadeuslms.amadeus.splashscreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import amadeuslms.amadeus.login.LoginActivity;

/**
 * Created by zambom on 09/06/17.
 */

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIMEOUT = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIMEOUT);

    }
}
