package amadeuslms.amadeus.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import amadeuslms.amadeus.R;
import amadeuslms.amadeus.utils.ImageUtils;

/**
 * Created by zambom on 22/07/17.
 */

public class FullImageActivity extends AppCompatActivity {

    public static final String FULL_IMAGE = "FULL_IMAGE";
    public static final String SENDER = "SENDER";

    private ViewPager pager;
    private ImageView ivImg;

    private String resource, sender;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Dark);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullimage);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setShowHideAnimationEnabled(true);

        Intent intent = getIntent();

        if (intent != null) {
            pager = (ViewPager) findViewById(R.id.pager);
            ivImg = (ImageView) findViewById(R.id.full_img);

            if (intent.hasExtra(FULL_IMAGE) && intent.hasExtra(SENDER)) {
                resource = intent.getStringExtra(FULL_IMAGE);
                sender = intent.getStringExtra(SENDER);

                final Point displaySize = ImageUtils.getDisplaySize(getWindowManager().getDefaultDisplay());
                final int size = (int) Math.ceil(Math.sqrt(displaySize.x * displaySize.y));

                if (resource.indexOf("gif") > -1) {
                    Glide.with(this).asGif().load(resource).into(ivImg);
                } else {
                    Picasso.with(this).load(resource).resize(size, size).centerInside().into(ivImg);
                }

                actionBar.setTitle(sender);

                ivImg.setVisibility(ImageView.VISIBLE);
                pager.setVisibility(ViewPager.GONE);
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
