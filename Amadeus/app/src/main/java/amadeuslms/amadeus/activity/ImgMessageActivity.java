package amadeuslms.amadeus.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;

import amadeuslms.amadeus.R;
import amadeuslms.amadeus.utils.TypefacesUtil;

public class ImgMessageActivity extends AppCompatActivity implements View.OnClickListener {

    private Bitmap img;
    private Uri uri;

    private EditText etMsg;
    private ImageView ivImg;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Dark);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_message);

        Intent intent = getIntent();

        if (intent != null) {
            if (intent.hasExtra(ChatActivity.SELECTED_IMG)) {
                uri = intent.getParcelableExtra(ChatActivity.SELECTED_IMG);

                try {
                    img = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                    ivImg = (ImageView) findViewById(R.id.full_img);
                    ivImg.setImageBitmap(img);

                    etMsg = (EditText) findViewById(R.id.sender_msg);

                    btnSend = (Button) findViewById(R.id.sender_btn);
                    btnSend.setOnClickListener(this);
                    TypefacesUtil.setFontAwesome(this, btnSend);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnSend.getId()) {
            String text = etMsg.getText().toString().trim();

            if (!text.isEmpty()) {
                Intent intent = new Intent();
                intent.putExtra(ChatActivity.MESSAGE_TEXT, text);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }
}
