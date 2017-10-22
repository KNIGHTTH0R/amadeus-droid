package amadeuslms.amadeus.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amadeuslms.amadeus.R;
import amadeuslms.amadeus.adapters.ChatAdapter;
import amadeuslms.amadeus.adapters.ParticipantsAdapter;
import amadeuslms.amadeus.bean.ApplicationProperties;
import amadeuslms.amadeus.bo.ParticipantsBO;
import amadeuslms.amadeus.bo.UserBO;
import amadeuslms.amadeus.cache.CacheController;
import amadeuslms.amadeus.cache.TokenCacheController;
import amadeuslms.amadeus.cache.UserCacheController;
import amadeuslms.amadeus.models.SubjectModel;
import amadeuslms.amadeus.models.UserModel;
import amadeuslms.amadeus.response.ParticipantsResponse;
import amadeuslms.amadeus.response.TokenResponse;
import amadeuslms.amadeus.response.UserResponse;
import amadeuslms.amadeus.utils.HttpUtils;

public class ParticipantsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;

    private ParticipantsAdapter participantsAdapter;
    private UserModel user;
    private SubjectModel subject;

    public static final String SUBJECT = "SUBJECT";

    private String subject_slug;
    private List<UserModel> participants;

    private ActionBar actionBar;

    private boolean hasClickedNotification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        if(intent != null && intent.hasExtra(SUBJECT)) {
            subject = intent.getParcelableExtra(SUBJECT);

            actionBar.setTitle(subject.getName());

            subject_slug = subject.getSlug();

            if (UserCacheController.hasUserCache(this)) {
                user = UserCacheController.getUserCache(this);

                swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.participants_view);
                swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
                swipeRefreshLayout.setOnRefreshListener(this);

                listView = (ListView) findViewById(R.id.participants_list);
                listView.setOnItemClickListener(this);

                new AsyncParticipants(this, user, subject_slug, false).execute();

            } else {
                if(TokenCacheController.hasTokenCache(this) && TokenCacheController.getTokenCache(this).isToken_expired()) {
                    Intent newIntent = new Intent(this, ParticipantsActivity.class);
                    newIntent.putExtra(ParticipantsActivity.SUBJECT, subject);
                    TokenCacheController.getTokenCache(this).startRenewToken(intent, this);
                } else {
                    goLogin();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            if(TokenCacheController.hasTokenCache(this) && TokenCacheController.getTokenCache(this).isToken_expired()) {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                TokenCacheController.getTokenCache(this).startRenewToken(intent, this);
            } else {
                finish();
            }
            break;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(hasClickedNotification) {
            onRefresh();
            hasClickedNotification = false;
        }
    }

    @Override
    public void onRefresh() {
        if(TokenCacheController.hasTokenCache(this) && TokenCacheController.getTokenCache(this).isToken_expired()) {
            Intent intent = new Intent(this, ParticipantsActivity.class);
            intent.putExtra(SUBJECT, subject);
            TokenCacheController.getTokenCache(this).startRenewToken(intent, this);
        } else if (TokenCacheController.hasTokenCache(this) && !TokenCacheController.getTokenCache(this).isToken_expired()) {
            new AsyncParticipants(this, user, subject_slug, true).execute();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UserModel participant = ((ParticipantsAdapter) listView.getAdapter()).getItem(position);

        if (participant != null) {
            Intent intent = new Intent(view.getContext(), ChatActivity.class);
            intent.putExtra(ChatActivity.USER_TO, participant);
            intent.putExtra(ChatActivity.SUBJECT, subject);
            if (TokenCacheController.hasTokenCache(this) && TokenCacheController.getTokenCache(this).isToken_expired()) {
                TokenCacheController.getTokenCache(this).startRenewToken(intent, this);
            } else {
                startActivity(intent);
            }
            if(participant.getUnseen_msgs() > 0) {
                hasClickedNotification = true;
            }
        }
    }

    public void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void showParticipants() {
        participantsAdapter = new ParticipantsAdapter(this, participants);

        listView.setAdapter(participantsAdapter);
    }

    private class AsyncParticipants extends AsyncTask<Void, Void, ParticipantsResponse> {

        private ProgressDialog progressDialog;
        private Context context;
        private UserModel user;
        private String title, message, slug;
        private boolean isRefresh;

        public AsyncParticipants(Context context, UserModel user, String slug, boolean isRefresh) {
            this.context = context;
            this.user = user;
            this.slug = slug;
            this.isRefresh = isRefresh;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (isRefresh) {
                swipeRefreshLayout.setEnabled(false);
                swipeRefreshLayout.setRefreshing(true);
            } else {
                progressDialog = ProgressDialog.show(context, null, getString(R.string.loading_participants));
            }
        }

        @Override
        protected ParticipantsResponse doInBackground(Void... params) {
            try {
                return new ParticipantsBO().get_participants(context, user, slug);
            } catch (Exception e) {
                title = context.getString(R.string.error_box_title);
                message = context.getString(R.string.error_box_msg) + " " + e.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ParticipantsResponse participantsResponse) {
            super.onPostExecute(participantsResponse);

            if (isRefresh) {
                swipeRefreshLayout.setEnabled(true);
                swipeRefreshLayout.setRefreshing(false);
            } else {
                progressDialog.dismiss();
            }

            if (participantsResponse != null) {
                if (participantsResponse.getSuccess() && participantsResponse.getNumber() == 1) {
                    participants = participantsResponse.getData().getParticipants();

                    showParticipants();
                } else if (!TextUtils.isEmpty(participantsResponse.getTitle()) && !TextUtils.isEmpty(participantsResponse.getMessage())){
                    title = participantsResponse.getTitle();
                    message = participantsResponse.getMessage();
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
