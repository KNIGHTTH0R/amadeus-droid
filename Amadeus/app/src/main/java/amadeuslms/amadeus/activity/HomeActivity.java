package amadeuslms.amadeus.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;

import amadeuslms.amadeus.R;
import amadeuslms.amadeus.bean.ApplicationProperties;
import amadeuslms.amadeus.cache.CacheController;
import amadeuslms.amadeus.cache.TokenCacheController;
import amadeuslms.amadeus.cache.UserCacheController;
import amadeuslms.amadeus.models.UserModel;
import amadeuslms.amadeus.utils.CircleTransformUtils;
import amadeuslms.amadeus.utils.ImageUtils;

public class HomeActivity extends AppCompatActivity {

    private ImageView ivPhoto;
    private TextView tvName;
    private LinearLayout actionBarCustom;

    private Menu _menu;

    private ActionBar actionBar;
    private ActionBar.LayoutParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);

        params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.LEFT
        );

        actionBarCustom = (LinearLayout) getLayoutInflater().inflate(R.layout.custom_actionbar, null);
        actionBar.setCustomView(actionBarCustom, params);

        if (UserCacheController.hasUserCache(this)) {
            UserModel user = UserCacheController.getUserCache(this);

            tvName = (TextView) actionBarCustom.findViewById(R.id.user_name);
            tvName.setText(user.getDisplayName());

            ivPhoto = (ImageView) actionBarCustom.findViewById(R.id.user_image);

            ImageUtils img = new ImageUtils(this);

            if(user.getImage() != null && !user.getImage().equals("") && TokenCacheController.hasTokenCache(this)){
                String path = TokenCacheController.getTokenCache(this).getWebserver_url() + user.getImage();

                Picasso.with(this).load(path).transform(new CircleTransformUtils()).into(ivPhoto);
            }else{
                try{
                    final InputStream is = this.getAssets().open("images/no_image.png");

                    Bitmap bmp = BitmapFactory.decodeStream(is);

                    ivPhoto.setImageBitmap(img.roundCornerImage(bmp, 40));
                }catch(IOException e){
                    System.out.println("Erro: " + e.getMessage());
                }
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_home, menu);
        _menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            CacheController.clearCache(this);

            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
