package amadeuslms.amadeus.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import amadeuslms.amadeus.R;
import amadeuslms.amadeus.adapters.ExpandableListAdapter;
import amadeuslms.amadeus.bean.ApplicationProperties;
import amadeuslms.amadeus.bo.SubjectBO;
import amadeuslms.amadeus.cache.CacheController;
import amadeuslms.amadeus.cache.SubjectCacheController;
import amadeuslms.amadeus.cache.TokenCacheController;
import amadeuslms.amadeus.cache.UserCacheController;
import amadeuslms.amadeus.models.SubjectModel;
import amadeuslms.amadeus.models.UserModel;
import amadeuslms.amadeus.response.SubjectResponse;
import amadeuslms.amadeus.utils.CircleTransformUtils;
import amadeuslms.amadeus.utils.ImageUtils;

public class HomeActivity extends AppCompatActivity {
    private ExpandableListAdapter listAdapter;
    private ExpandableListView listView;
    private List<SubjectModel> headers;
    private HashMap<String, List<String>> children;

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

            listView = (ExpandableListView) findViewById(R.id.subject_list);

            if (SubjectCacheController.hasSubjectCache(this)) {
                headers = SubjectCacheController.getSubjectCache(this).getSubjects();

                removeRequestUser(user);
                showSubjects();
            } else {
                new AsyncSubjects(this, user).execute();
            }
        } else {
            goLogin();
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

            goLogin();
        }

        return super.onOptionsItemSelected(item);
    }

    public void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void removeRequestUser(UserModel user) {
        for (SubjectModel subject : headers) {
            int pos = -1;

            for (UserModel u : subject.getParticipants()) {
                if (u.getEmail().equals(user.getEmail())) {
                    pos = subject.getParticipants().indexOf(u);
                }
            }

            if (pos != -1) {
                subject.getParticipants().remove(pos);
            }
        }
    }

    private void showSubjects() {
        listAdapter = new ExpandableListAdapter(this, headers);

        listView.setAdapter(listAdapter);

        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < headers.size(); ++i) {
                    if (i != groupPosition) {
                        listView.collapseGroup(i);
                    }
                }
            }
        });
    }

    private class AsyncSubjects extends AsyncTask<Void, Void, SubjectResponse> {

        private ProgressDialog progressDialog;
        private Context context;
        private UserModel user;
        private String title, message;

        public AsyncSubjects(Context context, UserModel user) {
            this.context = context;
            this.user = user;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(context, null, getString(R.string.loading_subjects));
        }

        @Override
        public SubjectResponse doInBackground(Void... params) {
            try {
                return new SubjectBO().get_subjects(context, user);
            } catch (Exception e) {
                title = context.getString(R.string.error_box_title);
                message = context.getString(R.string.error_box_msg) + " " + e.getMessage();
            }

            return null;
        }

        @Override
        public void onPostExecute(SubjectResponse subjectResponse) {
            super.onPostExecute(subjectResponse);

            progressDialog.dismiss();

            if (subjectResponse != null) {
                if (subjectResponse.getSuccess() && subjectResponse.getNumber() == 1) {
                    SubjectCacheController.setSubjectCache(context, subjectResponse.getData());
                    headers = subjectResponse.getData().getSubjects();

                    removeRequestUser(this.user);
                    showSubjects();
                } else if (!TextUtils.isEmpty(subjectResponse.getTitle()) && !TextUtils.isEmpty(subjectResponse.getMessage())){
                    title = subjectResponse.getTitle();
                    message = subjectResponse.getMessage();
                }
            }

            if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(message)){
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(title);
                builder.setMessage(message);

                builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.create().show();
            }
        }
    }
}
