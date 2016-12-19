package camera.from.pic.get.getpicfromcamerademo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;

public class MainActivity extends Activity implements View.OnClickListener {

    private TextView txt_abandon;
    private TextView txt_content_count;
    private Button btn_confirm;
    private ImageView iv_share;
    private EditText et_share_content;
    private CheckBox checkbox_show_record_page;

    private Uri mUri; //图片uri
    private String mFilePath;//图片地址

    public static final int REQUEST_CODE_CAMERA = 9527;
    private static final int REQUEST_CODE_CROP = 9528;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //
        //
        // 在  manifest 中需要首先配置 开启摄像头的权限  和  读写 外部文件的权限
        //
        //
        //

        initViews();

    }

    private void initViews() {
        txt_abandon = (TextView) findViewById(R.id.txt_abandon);
        txt_content_count = (TextView) findViewById(R.id.txt_content_count);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        iv_share = (ImageView) findViewById(R.id.iv_share);
        et_share_content = (EditText) findViewById(R.id.et_share_content);
        checkbox_show_record_page = (CheckBox) findViewById(R.id.checkbox_show_record_page);

        txt_abandon.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        et_share_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s != null) {
                    if (s.toString().length() >= 0) {
                        //显示还能输入多少字
                        txt_content_count.setText("(字数" + s.toString().length() + "/" + 300 + ")");
                    }
                }


            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.txt_abandon:
                Toast.makeText(this, "放弃", Toast.LENGTH_LONG).show();

                break;
            case R.id.btn_confirm:
                Toast.makeText(this, "完成", Toast.LENGTH_LONG).show();

                break;
            case R.id.iv_share:
                //调取摄像头拍照

                if (isSdcardExisting()) {

                    //开启相机，
                    Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                    mUri = getImageUri();
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
                    cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                    startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);

                } else {
                    Toast.makeText(this, "asdfasdf", Toast.LENGTH_LONG).show();

                }


                break;


        }
    }

    public Uri getImageUri() {
        return Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "/img.jpg"));
    }

    /**
     * 判断是否存在sd卡，或者说外部环境
     * @return
     */
    public boolean isSdcardExisting() {
        final String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //处理返回的图片,并且显示
        //这里是 判断是否是 相机返回的照片

        // 需要将 requestCode  和data 传递给 fragment 的某一个自定义方法中进行处理。
        //处理的过程就是下面这些

        if (REQUEST_CODE_CAMERA == requestCode) {

            //参考资料是 下面的文章
            //http://blog.csdn.net/harvic880925/article/details/43163175
            //
            resizeImage(mUri);


        } else if (REQUEST_CODE_CROP == requestCode) {//判断是否是 剪裁返回的 图片 ，并显示

            if (data != null) {
                //显示图片， 这里要替换成 框架中集成的 image加载库
                // 需要注意一下  图片大小 有可能引起崩溃的问题
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mUri));
                    iv_share.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }



        }


    }

    /**
     * 调用系统的剪切照片
     *
     * @param uri
     */
    public void resizeImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_CODE_CROP);
    }


}
