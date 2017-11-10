/*
Copyright 2016, 2017 UFPE - Universidade Federal de Pernambuco
 
Este arquivo é parte do programa Amadeus Sistema de Gestão de Aprendizagem, ou simplesmente Amadeus LMS
 
O Amadeus LMS é um software livre; você pode redistribui-lo e/ou modifica-lo dentro dos termos da Licença Pública Geral GNU como publicada pela Fundação do Software Livre (FSF); na versão 2 da Licença.
 
Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU para maiores detalhes.
 
Você deve ter recebido uma cópia da Licença Pública Geral GNU, sob o título "LICENSE", junto com este programa, se não, escreva para a Fundação do Software Livre (FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.
*/
package amadeuslms.amadeus.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.packageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.Manifest;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import amadeuslms.amadeus.R;
import amadeuslms.amadeus.adapters.ChatAdapter;
import amadeuslms.amadeus.bo.MessageBO;
import amadeuslms.amadeus.cache.TokenCacheController;
import amadeuslms.amadeus.cache.UserCacheController;
import amadeuslms.amadeus.events.NewMessageEvent;
import amadeuslms.amadeus.models.MessageModel;
import amadeuslms.amadeus.models.SubjectModel;
import amadeuslms.amadeus.models.UserModel;
import amadeuslms.amadeus.response.MessageResponse;
import amadeuslms.amadeus.utils.CircleTransformUtils;
import amadeuslms.amadeus.utils.DateUtils;
import amadeuslms.amadeus.utils.ImageUtils;
import amadeuslms.amadeus.utils.TypefacesUtil;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, ActionMode.Callback {

    public static final String USER_TO = "USER_TO";
    public static final String SUBJECT = "SUBJECT";
    public static final String SELECTED_IMG = "SELECTED_IMG";
    public static final String MESSAGE_TEXT = "MESSAGE_TEXT";

    public static boolean IS_ON_TOP;
    public static String talk_user;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    private static final int IMAGE_MESSAGE = 3;

    static final int WRITE_EXST = 3;
    static final int CAMERA = 5;

    private static final int PAGE_SIZE = 20;

    public boolean WRITE_GRANTED = false;
    public boolean CAMERA_GRANTED = false;

    private boolean hideMenu = false;
    private boolean fav_msgChecked = false;
    private boolean my_msgChecked = false;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    private int actual_page = 0;
    private double unseen_msgs = 0;

    private ChatAdapter adapter;
    private RecyclerView recyclerView;

    private UserModel user, user_to;
    private SubjectModel subject;
    private List<MessageModel> messageList;

    private TextView tvUser;
    private EditText etMsg;
    private ImageView ivImg;
    private Button btnSend, btnImg, btnFav;

    private LinearLayout actionBarCustom, llBack, selectionBarCustom, backSelect;
    private LinearLayoutManager linearLayoutManager;

    private ActionBar actionBar;
    private ActionBar.LayoutParams params;

    private Bitmap final_img;

    private Uri imageUri, destination;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(USER_TO) && intent.hasExtra(SUBJECT)) {
            user_to = intent.getParcelableExtra(USER_TO);
            subject = intent.getParcelableExtra(SUBJECT);

            talk_user = user_to.getEmail();

            actionBar = getSupportActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(false);

            params = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT,
                    Gravity.LEFT
            );

            actionBarCustom = (LinearLayout) getLayoutInflater().inflate(R.layout.custom_chat_actionbar, null);
            selectionBarCustom = (LinearLayout) getLayoutInflater().inflate(R.layout.chat_selection_actionbar, null);

            tvUser = (TextView) actionBarCustom.findViewById(R.id.chat_user);
            ivImg = (ImageView) actionBarCustom.findViewById(R.id.menu_image);
            llBack = (LinearLayout) actionBarCustom.findViewById(R.id.back_btn);

            tvUser.setText(user_to.getDisplayName());

            ImageUtils img = new ImageUtils(this);

            if(user_to.getImage_url() != null && !user_to.getImage_url().equals("") && TokenCacheController.hasTokenCache(this) && !TokenCacheController.getTokenCache(this).isToken_expired()){
                String path = TokenCacheController.getTokenCache(this).getWebserver_url() + user_to.getImage_url();

                Picasso.with(this).load(path).transform(new CircleTransformUtils()).into(ivImg);
            }else{
                try{
                    final InputStream is = this.getAssets().open("images/no_image.png");

                    Bitmap bmp = BitmapFactory.decodeStream(is);

                    ivImg.setImageBitmap(img.roundCornerImage(bmp, 40));
                }catch(IOException e){
                    System.out.println("Erro: " + e.getMessage());
                }
            }

            llBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            actionBar.setCustomView(actionBarCustom, params);
            toolbar.setContentInsetsAbsolute(0, 0);

            backSelect = (LinearLayout) selectionBarCustom.findViewById(R.id.cancel_selection);
            backSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionBar.setCustomView(actionBarCustom, params);
                    hideMenu = false;
                    invalidateOptionsMenu();
                    adapter.clearSelection();   //Deselect all messages
                }
            });

            etMsg = (EditText) findViewById(R.id.sender_msg);
            btnSend = (Button) findViewById(R.id.sender_btn);
            btnImg = (Button) findViewById(R.id.sender_img);
            TypefacesUtil.setFontAwesome(this, btnSend);
            TypefacesUtil.setFontAwesome(this, btnImg);

            btnSend.setOnClickListener(this);
            btnImg.setOnClickListener(this);

            btnFav = (Button) selectionBarCustom.findViewById(R.id.fav_btn_text);
            TypefacesUtil.setFontAwesome(this, btnFav);
            btnFav.setOnClickListener(this);

            recyclerView    = (RecyclerView)    findViewById(R.id.chat_recycler);

            recyclerView.setHasFixedSize(true);

            linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            linearLayoutManager.setReverseLayout(true);

            recyclerView.addOnScrollListener(recyclerViewOnScrollListener);

            recyclerView.setLayoutManager(linearLayoutManager);

            if (UserCacheController.hasUserCache(this)) {
                user = UserCacheController.getUserCache(this);
                unseen_msgs = user_to.getUnseen_msgs();

                context = this;

                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(unseen_msgs > PAGE_SIZE) {
                            int missing_unseen_pages = (int) Math.ceil(((double) user_to.getUnseen_msgs()) / PAGE_SIZE);
                            int pageSize = PAGE_SIZE * missing_unseen_pages;
                            new LoadChat(context, user, user_to, pageSize).execute();
                            actual_page = actual_page + missing_unseen_pages - 1;
                        } else {
                            new LoadChat(context, user, user_to, PAGE_SIZE).execute();
                        }
                    }
                });
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
        } else {
            finish();
        }
    }

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx,  int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = linearLayoutManager.getChildCount();
            int totalItemCount = linearLayoutManager.getItemCount();
            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

            if(!isLoading && !isLastPage) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE) {
                    isLoading = true;
                    new LoadPage(context, user, user_to, actual_page+1).execute();
                }
            }
        }
    };
    
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(ChatActivity.this, permission) != packageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ChatActivity.this, permission)) {
                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(ChatActivity.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(ChatActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            switch (requestCode) {
                case 3:
                    WRITE_GRANTED = true;
                    break;
                case 5:
                    CAMERA_GRANTED = true;
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == packageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case 3:
                    WRITE_GRANTED = true;
                    break;
                case 5:
                    CAMERA_GRANTED = true;
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if(!hideMenu) {
            getMenuInflater().inflate(R.menu.menu_chat, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.favorite_messages:
                item.setChecked(!item.isChecked());
                if(!item.isChecked()) { //User has unchecked
                    fav_msgChecked = false;
                    adapter = new ChatAdapter(context, user, filterMessages());
                } else { //User has checked
                    fav_msgChecked = true;
                    adapter = new ChatAdapter(context, user, filterMessages());
                }
                setAdapterListener();
                recyclerView.setAdapter(adapter);
                break;
            case R.id.my_messages:
                item.setChecked(!item.isChecked());
                if(!item.isChecked()) { //User has unchecked
                    my_msgChecked = false;
                    adapter = new ChatAdapter(context, user, filterMessages());
                } else { //User has checked
                    my_msgChecked = true;
                    adapter = new ChatAdapter(context, user, filterMessages());
                }
                setAdapterListener();
                recyclerView.setAdapter(adapter);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (hideMenu) {
            actionBar.setCustomView(actionBarCustom, params);
            hideMenu = false;
            invalidateOptionsMenu();
            adapter.clearSelection();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume(){
        super.onResume();

        IS_ON_TOP = true;
    }

    @Override
    protected void onPause(){
        super.onPause();

        IS_ON_TOP = false;
    }

    @Override
    protected void onStop(){
        EventBus.getDefault().unregister(this);

        super.onStop();
    }

    public void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        if(!TokenCacheController.getTokenCache(context).isToken_expired()) {
            if (v.getId() == btnSend.getId()) {
                String text = etMsg.getText().toString().trim();
                if (!text.isEmpty()) {
                    etMsg.setEnabled(false);
                    btnSend.setEnabled(false);

                    MessageModel message = new MessageModel();
                    message.setText(text);
                    message.setUser(user);
                    message.setSubject(subject);
                    message.setCreate_date(DateUtils.currentDate());
                    message.setFavorite(false);

                    new SendMessage(this, user_to, message).execute();
                }
            } else if (v.getId() == btnImg.getId() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,WRITE_EXST);
                    if(WRITE_GRANTED) {
                        askForPermission(Manifest.permission.CAMERA,CAMERA);
                        if(CAMERA_GRANTED) {
                            onClickPermissionsGranted(v);
                        }
                    }
                }
            } else if (v.getId() == btnImg.getId()) {
                onClickPermissionsGranted(v);
            } else if (v.getId() == btnFav.getId()) {
                List<MessageModel> mMsg = adapter.getSelected_messages();
                boolean has_noFavorite = false;
                for(int i = 0; i < mMsg.size(); ++i) {
                    if(!mMsg.get(i).getFavorite()) {
                        has_noFavorite = true;
                        break;
                    }
                }

                new UpdateMessage(this, user, mMsg, has_noFavorite).execute();

                actionBar.setCustomView(actionBarCustom, params);
                hideMenu = false;
                invalidateOptionsMenu();

                adapter.updateFavorites(has_noFavorite);    //Update favorite states just for user view
                adapter.clearSelection();                   //Deselect all messages

                if(fav_msgChecked || my_msgChecked) {       //Clear filters
                    fav_msgChecked = false;
                    my_msgChecked = false;
                    adapter = new ChatAdapter(context, user, filterMessages());
                    setAdapterListener();
                    recyclerView.setAdapter(adapter);
                }
            }
        } else {
            Intent intent = new Intent(v.getContext(), ChatActivity.class);
            TokenCacheController.getTokenCache(this).startRenewToken(intent, this);
        }
    }

    public void onClickPermissionsGranted(View v) {        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.image_chooser_title);
        builder.setItems(new CharSequence[]{getString(R.string.image_gallery_option), getString(R.string.image_camera_option)}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent_gallery = new Intent();

                        intent_gallery.setType("image/*");
                        intent_gallery.setAction(Intent.ACTION_GET_CONTENT);

                        startActivityForResult(Intent.createChooser(intent_gallery, getString(R.string.chooser_title)), PICK_FROM_FILE);

                        break;

                    case 1:
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "tmp_image_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                        try {
                            intent.putExtra("return-data", true);

                            startActivityForResult(intent, PICK_FROM_CAMERA);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        }

                        break;
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case PICK_FROM_CAMERA:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.crop_dialog_title);
                builder.setMessage(R.string.crop_dialog_msg);
                builder.setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        doCrop();
                    }
                });
                builder.setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputStream is = null;

                        try {
                            is = getContentResolver().openInputStream(imageUri);

                            Bitmap bitmap = BitmapFactory.decodeStream(is);

                            ExifInterface exif = new ExifInterface(imageUri.getPath());
                            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                            int rotate = exifToDegrees(rotation);

                            Matrix matrix = new Matrix();

                            if (rotation != 0f) {
                                matrix.postRotate(rotate);
                            }

                            final_img = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                            destination = Uri.fromFile(new File(getCacheDir(), "cropped_" + String.valueOf(System.currentTimeMillis() + ".jpg")));

                            OutputStream os = getContentResolver().openOutputStream(destination);

                            if (os != null) {
                                final_img.compress(Bitmap.CompressFormat.JPEG, 90, os);

                                os.flush();
                                os.close();

                                getTextMsg(destination);
                            }
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }  catch (OutOfMemoryError e) {
                            System.out.println(e.getMessage());
                        }
                    }
                });

                builder.create().show();

                break;

            case PICK_FROM_FILE:
                imageUri = data.getData();

                AlertDialog.Builder builder_file = new AlertDialog.Builder(this);
                builder_file.setTitle(R.string.crop_dialog_title);
                builder_file.setMessage(R.string.crop_dialog_msg);
                builder_file.setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        doCrop();
                    }
                });
                builder_file.setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputStream is = null;

                        try {
                            is = getContentResolver().openInputStream(imageUri);

                            final_img = BitmapFactory.decodeStream(is);

                            destination = Uri.fromFile(new File(getCacheDir(), "cropped_" + String.valueOf(System.currentTimeMillis() + ".jpg")));

                            OutputStream os = getContentResolver().openOutputStream(destination);

                            if (os != null) {
                                final_img.compress(Bitmap.CompressFormat.JPEG, 90, os);

                                os.flush();
                                os.close();

                                getTextMsg(destination);
                            }
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }  catch (OutOfMemoryError e) {
                            System.out.println(e.getMessage());
                        }
                    }
                });

                builder_file.create().show();

                break;

            case Crop.REQUEST_CROP:
                destination = Crop.getOutput(data);

                getTextMsg(destination);

                break;

            case IMAGE_MESSAGE:
                if (data.hasExtra(MESSAGE_TEXT)) {
                    String text = data.getStringExtra(MESSAGE_TEXT);

                    MessageModel message = new MessageModel();
                    message.setText(text);
                    message.setUser(user);
                    message.setSubject(subject);
                    message.setCreate_date(DateUtils.currentDate());

                    new SendImageMessage(this, user_to, message, destination).execute();
                }

                break;
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }

    private void doCrop(){
        destination = Uri.fromFile(new File(getCacheDir(), "cropped_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

        Crop.of(imageUri, destination).withMaxSize(500, 500).asSquare().start(this);
    }

    private void getTextMsg(Uri img) {
        Intent intent = new Intent();
        intent.setClass(context, ImgMessageActivity.class);
        intent.setFlags(0);
        intent.putExtra(SELECTED_IMG, img);
        if(!TokenCacheController.getTokenCache(context).isToken_expired()) {
            startActivityForResult(intent, IMAGE_MESSAGE);
        } else {
            TokenCacheController.getTokenCache(context).startRenewToken_ForResult(intent, context, IMAGE_MESSAGE);
        }
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessage(NewMessageEvent event) {
        MessageModel received = event.response.getData().getMessage_sent();

        ((ChatAdapter) recyclerView.getAdapter()).addListItem(received, 0);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.smoothScrollToPosition(0);
    }

    private class UpdateMessage extends AsyncTask<Void, Void, MessageResponse> {
        private Context context;
        private UserModel user;
        private List<MessageModel> message_upd;
        private String title, message;
        private boolean favor;

        public UpdateMessage(Context context, UserModel user, List<MessageModel> message_upd, boolean favor) {
            this.context = context;
            this.user = user;
            this.message_upd = message_upd;
            this.favor = favor;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected MessageResponse doInBackground(Void... params) {
            if(!TokenCacheController.getTokenCache(context).isToken_expired()) {
                try {
                    return new MessageBO().favorite_messages(context, user, message_upd, favor);
                } catch (Exception e){
                    title = context.getString(R.string.error_box_title);
                    message = context.getString(R.string.error_box_msg) + " " + e.getMessage();
                }
            } else {
                Intent intent = new Intent(context, ChatActivity.class);
                TokenCacheController.getTokenCache(context).startRenewToken(intent, context);
            }
            return null;
        }

        @Override
        protected void onPostExecute(MessageResponse messageResponse) {
            super.onPostExecute(messageResponse);

            if (messageResponse != null) {
                if (messageResponse.getSuccess() && messageResponse.getNumber() == 1) {

                } else if (!TextUtils.isEmpty(messageResponse.getTitle()) && !TextUtils.isEmpty(messageResponse.getMessage())){
                    title = messageResponse.getTitle();
                    message = messageResponse.getMessage();
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

    private class LoadChat extends AsyncTask<Void, Void, MessageResponse> {
        private Context context;
        private UserModel user,  user_to;
        private String title, message;
        private int pageSize;

        public LoadChat(Context context, UserModel user, UserModel user_to, int pageSize) {
            this.context = context;
            this.user = user;
            this.user_to = user_to;
            this.pageSize = pageSize;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected MessageResponse doInBackground(Void... params) {
            if(!TokenCacheController.getTokenCache(context).isToken_expired()) {
                try {
                    return new MessageBO().get_messages(context, user, user_to, 1, pageSize);
                } catch (Exception e){
                    title = context.getString(R.string.error_box_title);
                    message = context.getString(R.string.error_box_msg) + " " + e.getMessage();
                }
            } else {
                Intent intent = new Intent(context, ChatActivity.class);
                TokenCacheController.getTokenCache(context).startRenewToken(intent, context);
            }
            return null;
        }

        @Override
        protected void onPostExecute(MessageResponse messageResponse) {
            super.onPostExecute(messageResponse);

            if (messageResponse != null) {
                if (messageResponse.getSuccess() && messageResponse.getNumber() == 1) {
                    actual_page += 1;
                    messageList = messageResponse.getData().getMessages();

                    adapter = new ChatAdapter(context, user, messageList);
                    setAdapterListener();

                    recyclerView.setAdapter(adapter);
                } else if (!TextUtils.isEmpty(messageResponse.getTitle()) && !TextUtils.isEmpty(messageResponse.getMessage())){
                    title = messageResponse.getTitle();
                    message = messageResponse.getMessage();
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

    private class LoadPage extends AsyncTask<Void, Void, MessageResponse> {
        private Context context;
        private UserModel user,  user_to;
        private String title, message;
        private int page_number;

        public LoadPage(Context context, UserModel user, UserModel user_to, int page_number) {
            this.context = context;
            this.user = user;
            this.user_to = user_to;
            this.page_number = page_number;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected MessageResponse doInBackground(Void... params) {
            if(!TokenCacheController.getTokenCache(context).isToken_expired()) {
                try {
                    return new MessageBO().get_messages(context, user, user_to, page_number, PAGE_SIZE);
                } catch (Exception e){
                    title = context.getString(R.string.error_box_title);
                    message = context.getString(R.string.error_box_msg) + " " + e.getMessage();
                }
            } else {
                Intent intent = new Intent(context, ChatActivity.class);
                TokenCacheController.getTokenCache(context).startRenewToken(intent, context);
            }
            return null;
        }

        @Override
        protected void onPostExecute(MessageResponse messageResponse) {
            super.onPostExecute(messageResponse);

            if (messageResponse != null) {
                if (messageResponse.getSuccess() && messageResponse.getNumber() == 1) {

                    actual_page += 1;

                    List<MessageModel> messagesLoaded = messageResponse.getData().getMessages();

                    if(!messagesLoaded.isEmpty()) {

                        for(int i = 0; i < messagesLoaded.size(); ++i) {
                            ((ChatAdapter) recyclerView.getAdapter()).addListItem(messagesLoaded.get(i), ((ChatAdapter) recyclerView.getAdapter()).getItemCount());
                        }

                        if(messagesLoaded.size() < PAGE_SIZE) {
                            isLastPage = true;
                        }
                    } else {
                        isLastPage = true;
                    }
                    isLoading = false;

                    recyclerView.smoothScrollToPosition((actual_page-1) * PAGE_SIZE);

                } else if (!TextUtils.isEmpty(messageResponse.getTitle()) && !TextUtils.isEmpty(messageResponse.getMessage())){
                    title = messageResponse.getTitle();
                    message = messageResponse.getMessage();
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

    private class SendMessage extends AsyncTask<Void, Void, MessageResponse> {

        private Context context;
        private MessageModel msg;
        private UserModel user;
        private String title, message;

        public SendMessage(Context context, UserModel user, MessageModel msg) {
            this.context = context;
            this.user = user;
            this.msg = msg;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            etMsg.setEnabled(false);
            btnSend.setEnabled(false);
            btnImg.setEnabled(false);
        }

        @Override
        protected MessageResponse doInBackground(Void... params) {
            if(!TokenCacheController.getTokenCache(context).isToken_expired()) {
                try {
                    return new MessageBO().send_message(context, user, msg);
                } catch (Exception e) {
                    title = context.getString(R.string.error_box_title);
                    message = context.getString(R.string.error_box_msg) + " " + e.getMessage();
                }
            } else {
                Intent intent = new Intent(context, ChatActivity.class);
                TokenCacheController.getTokenCache(context).startRenewToken(intent, context);
            }

            return null;
        }

        @Override
        protected void onPostExecute(MessageResponse messageResponse) {
            super.onPostExecute(messageResponse);

            etMsg.setEnabled(true);
            btnSend.setEnabled(true);
            btnImg.setEnabled(true);

            if (messageResponse != null) {
                if (messageResponse.getSuccess() && messageResponse.getNumber() == 1) {
                    etMsg.setText("");

                    MessageModel sent = messageResponse.getData().getMessage_sent();

                    ((ChatAdapter) recyclerView.getAdapter()).addListItem(sent, 0);

                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.smoothScrollToPosition(0);

                }  else if (!TextUtils.isEmpty(messageResponse.getTitle()) && !TextUtils.isEmpty(messageResponse.getMessage())){
                    title = messageResponse.getTitle();
                    message = messageResponse.getMessage();
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

    private class SendImageMessage extends AsyncTask<Void, Void, MessageResponse> {

        private Context context;
        private MessageModel msg;
        private UserModel user;
        private Uri destination;
        private String title, message;

        public SendImageMessage(Context context, UserModel user, MessageModel msg, Uri destination) {
            this.context = context;
            this.user = user;
            this.msg = msg;
            this.destination = destination;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            etMsg.setEnabled(false);
            btnSend.setEnabled(false);
            btnImg.setEnabled(false);
        }

        @Override
        protected MessageResponse doInBackground(Void... params) {
            if(!TokenCacheController.getTokenCache(context).isToken_expired()) {
                try {
                    return new MessageBO().send_image_message(context, user, msg, destination);
                } catch (Exception e) {
                    title = context.getString(R.string.error_box_title);
                    message = context.getString(R.string.error_box_msg) + " " + e.getMessage();
                }
            } else {
                Intent intent = new Intent(context, ChatActivity.class);
                TokenCacheController.getTokenCache(context).startRenewToken(intent, context);
            }
            return null;
        }

        @Override
        protected void onPostExecute(MessageResponse messageResponse) {
            super.onPostExecute(messageResponse);

            etMsg.setEnabled(true);
            btnSend.setEnabled(true);
            btnImg.setEnabled(true);

            if (messageResponse != null) {
                if (messageResponse.getSuccess() && messageResponse.getNumber() == 1) {
                    etMsg.setText("");

                    MessageModel sent = messageResponse.getData().getMessage_sent();

                    ((ChatAdapter) recyclerView.getAdapter()).addListItem(sent, 0);

                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.smoothScrollToPosition(0);

                }  else if (!TextUtils.isEmpty(messageResponse.getTitle()) && !TextUtils.isEmpty(messageResponse.getMessage())){
                    title = messageResponse.getTitle();
                    message = messageResponse.getMessage();
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
    //MARK: - Function to filter the messages
    public List<MessageModel> filterMessages() {
        List<MessageModel> filtered = new ArrayList<MessageModel>();
        if (messageList == null) return null;
        if(my_msgChecked && !fav_msgChecked) {
            for(int i = 0; i < messageList.size(); ++i) {
                if(messageList.get(i).getUser().getEmail().equals(user.getEmail())) {
                    filtered.add(messageList.get(i));
                }
            }
        } else if(!my_msgChecked && fav_msgChecked) {
            for(int i = 0; i < messageList.size(); ++i) {
                if(messageList.get(i).getFavorite()) {
                    filtered.add(messageList.get(i));
                }
            }
        } else if(my_msgChecked && fav_msgChecked) {
            for(int i = 0; i < messageList.size(); ++i) {
                if(messageList.get(i).getUser().getEmail().equals(user.getEmail()) && messageList.get(i).getFavorite()) {
                    filtered.add(messageList.get(i));
                }
            }
        } else {
            filtered = messageList;
        }
        return filtered;
    }

    public void setAdapterListener() {
        if(adapter != null) {
            adapter.setOnMessageSelectedListener(new ChatAdapter.OnMessageSelectedListener() {
                public void onSelected() {
                    hideMenu = true;
                    invalidateOptionsMenu();
                    actionBar.setCustomView(selectionBarCustom, params);
                }
                public void onDeselected() {
                    actionBar.setCustomView(actionBarCustom, params);
                    hideMenu = false;
                    invalidateOptionsMenu();
                    adapter.clearSelection();   //Deselect all messages
                }
            });
        }
    }
}