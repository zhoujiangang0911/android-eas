package com.zhoujg77.eas;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by zhoujiangang on 15/11/29.
 */
public class DetailActivity extends AppCompatActivity {

    private TextView qcode_detail = null;
    private TextView state_detail = null;
    private TextView date_detail = null;
    private ImageView ImgReceipt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();

        qcode_detail = (TextView) findViewById(R.id.detail_qcode);
        date_detail = (TextView) findViewById(R.id.detail_date);
        state_detail = (TextView) findViewById(R.id.detail_type);
        ImgReceipt = (ImageView) findViewById(R.id.detailed_receipt);

        qcode_detail.setText(intent
                .getStringExtra(MainActivity.EXTRA_MESSAGE_QCODE));
        date_detail.setText(intent
                .getStringExtra(MainActivity.EXTRA_MESSAGE_DATE));
        String type = null;
        String state = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_STATE);
        if ("20".equals(state)) {
            type = getString(R.string.type_send);

            TextView textReceipt = (TextView) findViewById(R.id.receipt);
            textReceipt.setVisibility(View.GONE);

        } else if ("30".equals(state)) {
            type = getString(R.string.type_receipt);

            final String path = MainActivity.localWholeImgPath
                    + intent.getStringExtra(MainActivity.EXTRA_MESSAGE_PATH);
            Bitmap bitmap = getLoacalBitmap(path);
            ImgReceipt.setImageBitmap(bitmap); //

            ImgReceipt.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View arg0, MotionEvent arg1) {
                    // TODO Auto-generated method stub
                    if (arg1.getAction() == arg1.ACTION_DOWN) {

                        Intent it = new Intent(Intent.ACTION_VIEW);
                        it.setAction(android.content.Intent.ACTION_VIEW);
                        File file = new File(path);
                        it.setDataAndType(Uri.fromFile(file), "image/*");
                        startActivity(it);
                        return true;
                    }
                    return false;
                }
            });
        }
        state_detail.setText(type);

    }

    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis); // /����ת��ΪBitmapͼƬ

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_main, container,
                    false);
            return rootView;
        }
    }




}
