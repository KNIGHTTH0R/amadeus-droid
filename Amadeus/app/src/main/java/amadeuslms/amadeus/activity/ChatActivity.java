package amadeuslms.amadeus.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import amadeuslms.amadeus.R;
import amadeuslms.amadeus.adapters.ChatAdapter;
import amadeuslms.amadeus.bo.MessageBO;
import amadeuslms.amadeus.cache.TokenCacheController;
import amadeuslms.amadeus.cache.UserCacheController;
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

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    private static final int IMAGE_MESSAGE = 3;

    private ChatAdapter adapter;
    private RecyclerView recyclerView;

    private UserModel user, user_to;
    private SubjectModel subject;
    private List<MessageModel> messageList;

    private TextView tvUser;
    private EditText etMsg;
    private ImageView ivImg;
    private Button btnSend, btnImg;

    private LinearLayout actionBarCustom, llBack;

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

            tvUser = (TextView) actionBarCustom.findViewById(R.id.chat_user);
            ivImg = (ImageView) actionBarCustom.findViewById(R.id.menu_image);
            llBack = (LinearLayout) actionBarCustom.findViewById(R.id.back_btn);

            tvUser.setText(user_to.getDisplayName());

            ImageUtils img = new ImageUtils(this);

            if(user_to.getImage_url() != null && !user_to.getImage_url().equals("") && TokenCacheController.hasTokenCache(this)){
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

            etMsg = (EditText) findViewById(R.id.sender_msg);
            btnSend = (Button) findViewById(R.id.sender_btn);
            btnImg = (Button) findViewById(R.id.sender_img);
            TypefacesUtil.setFontAwesome(this, btnSend);
            TypefacesUtil.setFontAwesome(this, btnImg);

            btnSend.setOnClickListener(this);
            btnImg.setOnClickListener(this);

            recyclerView    = (RecyclerView)    findViewById(R.id.chat_recycler);

            recyclerView.setHasFixedSize(true);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            linearLayoutManager.setReverseLayout(true);

            recyclerView.setLayoutManager(linearLayoutManager);

            if (UserCacheController.hasUserCache(this)) {
                user = UserCacheController.getUserCache(this);

                context = this;

                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        new LoadChat(context, user, user_to).execute();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            finish();
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

    @Override
    public void onClick(View v) {
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

                new SendMessage(this, user_to, message).execute();
            }
        } else if (v.getId() == btnImg.getId()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select image:");
            builder.setItems(new CharSequence[]{"Gallery", "Camera"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            Intent intent_gallery = new Intent();

                            intent_gallery.setType("image/*");
                            intent_gallery.setAction(Intent.ACTION_GET_CONTENT);

                            startActivityForResult(Intent.createChooser(intent_gallery, "Complete action using:"), PICK_FROM_FILE);

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
        startActivityForResult(intent, IMAGE_MESSAGE);
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    private class LoadChat extends AsyncTask<Void, Void, MessageResponse> {
        private Context context;
        private UserModel user,  user_to;
        private String title, message;

        public LoadChat(Context context, UserModel user, UserModel user_to) {
            this.context = context;
            this.user = user;
            this.user_to = user_to;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected MessageResponse doInBackground(Void... params) {
            try {
                return new MessageBO().get_messages(context, user, user_to);
            } catch (Exception e){
                title = context.getString(R.string.error_box_title);
                message = context.getString(R.string.error_box_msg) + " " + e.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(MessageResponse messageResponse) {
            super.onPostExecute(messageResponse);

            if (messageResponse != null) {
                if (messageResponse.getSuccess() && messageResponse.getNumber() == 1) {
                    messageList = messageResponse.getData().getMessages();

                    adapter = new ChatAdapter(context, user, messageList);

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

            //flLoading.setVisibility(FrameLayout.VISIBLE);
            etMsg.setEnabled(false);
            btnSend.setEnabled(false);
            btnImg.setEnabled(false);
        }

        @Override
        protected MessageResponse doInBackground(Void... params) {
            try {
                return new MessageBO().send_message(context, user, msg);
            } catch (Exception e) {
                title = context.getString(R.string.error_box_title);
                message = context.getString(R.string.error_box_msg) + " " + e.getMessage();
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

            //flLoading.setVisibility(FrameLayout.VISIBLE);
            etMsg.setEnabled(false);
            btnSend.setEnabled(false);
            btnImg.setEnabled(false);
        }

        @Override
        protected MessageResponse doInBackground(Void... params) {
            try {
                return new MessageBO().send_image_message(context, user, msg, destination);
            } catch (Exception e) {
                title = context.getString(R.string.error_box_title);
                message = context.getString(R.string.error_box_msg) + " " + e.getMessage();
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
}
