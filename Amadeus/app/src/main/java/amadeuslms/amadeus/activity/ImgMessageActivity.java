package amadeuslms.amadeus.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import amadeuslms.amadeus.R;
import amadeuslms.amadeus.utils.TypefacesUtil;

public class ImgMessageActivity extends AppCompatActivity implements View.OnClickListener {

    private Bitmap img;

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
            byte[] bytes = intent.getByteArrayExtra(ChatActivity.SELECTED_IMG);
            img = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            ivImg = (ImageView) findViewById(R.id.full_img);
            ivImg.setImageBitmap(img);

            etMsg = (EditText) findViewById(R.id.sender_msg);

            btnSend = (Button) findViewById(R.id.sender_btn);
            btnSend.setOnClickListener(this);
            TypefacesUtil.setFontAwesome(this, btnSend);
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
