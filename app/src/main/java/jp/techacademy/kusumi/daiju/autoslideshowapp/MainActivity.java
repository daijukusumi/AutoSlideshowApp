package jp.techacademy.kusumi.daiju.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    ImageView imageViewSmallMinus_2, imageViewSmallMinus_1, imageViewSmallZero, imageViewSmallPlus_1, imageViewSmallPlus_2, imageViewMain;
    Button buttonNext, buttonBack, buttonPlayStop;
    TextView textViewPictureNum;

    private Cursor cursor;

    int pic_size;
    int pic_selected = 0;
    int pic_count = 0;
    int pic_id = 0;
    int pic_id_Zero, pic_id_Plus_1, pic_id_Plus_2, pic_id_Minus_2, pic_id_Minus_1;

    String playStatus;
    Timer timer;


    //ArrayListを用意し、idとuriを連携
    //TODO String を　URIに修正
    ArrayList<Uri> uriList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewSmallMinus_2 = (ImageView) findViewById(R.id.imageViewSmallMinus_2);
        imageViewSmallMinus_1 = (ImageView) findViewById(R.id.imageViewSmallMinus_1);
        imageViewSmallZero = (ImageView) findViewById(R.id.imageViewSmallZero);
        imageViewSmallPlus_1 = (ImageView) findViewById(R.id.imageViewSmallPlus_1);
        imageViewSmallPlus_2 = (ImageView) findViewById(R.id.imageViewSmallPlus_2);
        imageViewMain = (ImageView) findViewById(R.id.imageViewMain);

        buttonNext = (Button) findViewById(R.id.buttonNext);
        buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonPlayStop = (Button) findViewById(R.id.buttonPlayStop);

        textViewPictureNum = (TextView) findViewById(R.id.textViewPictureNum);

        buttonNext.setOnClickListener(this);
        buttonBack.setOnClickListener(this);
        buttonPlayStop.setOnClickListener(this);

        buttonPlayStop.setText(getString(R.string.button_play));
        playStatus = getString(R.string.button_play);

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
                setImages();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
            setImages();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }

    private void getContentsInfo() {

        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        //uriListを初期化する
        uriList.clear();

        if (cursor.moveToFirst()) {
            do {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                Long id = cursor.getLong(fieldIndex);
                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                uriList.add(imageUri);

            } while (cursor.moveToNext());
        }
        cursor.close();
        pic_size = uriList.size();
    }
    private void setImages() {
        //選択した写真をimageViewに反映するためのidを設定
        pic_id_Zero = pic_selected;

        //写真が5枚以上ある場合、画面上部のimageViewに反映
        if (pic_size >= 5) {
            //上部の画面に反映する5枚の写真のidを設定
            if (pic_selected < pic_size - 1) {
                pic_id_Plus_1 = pic_selected + 1;
            } else {
                pic_id_Plus_1 = 0;
            }

            if (pic_id_Plus_1 < pic_size - 1 && pic_id_Plus_1 > 0) {
                pic_id_Plus_2 = pic_selected + 2;
            } else if (pic_id_Plus_1 == pic_size - 1) {
                pic_id_Plus_2 = 0;
            } else if (pic_id_Plus_1 == 0) {
                pic_id_Plus_2 = pic_id_Plus_1 + 1;
            }

            if (pic_selected - 2 >= 0) {
                pic_id_Minus_2 = pic_selected - 2;
            } else {
                pic_id_Minus_2 = pic_size - 2;
            }

            if (pic_selected - 1 >= 0) {
                pic_id_Minus_1 = pic_selected - 1;
            } else {
                pic_id_Minus_1 = pic_size - 1;
            }
        }

        //選択した写真をメインのimageViewにセットする
        imageViewMain.setImageURI(uriList.get(pic_id_Zero));
        imageViewMain.setScaleType(ImageView.ScaleType.FIT_XY);

        //写真が5枚以上ある場合、画面上部のimageViewに写真をセットする
        if (pic_size >= 5) {
            imageViewSmallZero.setImageURI(uriList.get(pic_id_Zero));
            imageViewSmallZero.setScaleType(ImageView.ScaleType.FIT_XY);

            imageViewSmallPlus_1.setImageURI(uriList.get(pic_id_Plus_1));
            imageViewSmallPlus_1.setScaleType(ImageView.ScaleType.FIT_XY);

            imageViewSmallPlus_2.setImageURI(uriList.get(pic_id_Plus_2));
            imageViewSmallPlus_2.setScaleType(ImageView.ScaleType.FIT_XY);

            imageViewSmallMinus_2.setImageURI(uriList.get(pic_id_Minus_2));
            imageViewSmallMinus_2.setScaleType(ImageView.ScaleType.FIT_XY);

            imageViewSmallMinus_1.setImageURI(uriList.get(pic_id_Minus_1));
            imageViewSmallMinus_1.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        //写真の枚数と表示中の写真の番号を表示する
        textViewPictureNum.setText(String.valueOf(pic_selected + 1) + "/" + String.valueOf(pic_size));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.buttonNext) {
            nextPicture();
            setImages();
        } else if (id == R.id.buttonBack) {
            backPicture();
            setImages();

        } else if (id == R.id.buttonPlayStop) {
            if (playStatus == getString(R.string.button_play)) {

                //Next, Backボタンをタップ不可にする
                buttonNext.setEnabled(false);
                buttonBack.setEnabled(false);
                buttonNext.setVisibility(View.INVISIBLE);
                buttonBack.setVisibility(View.INVISIBLE);

                //タイマーをセット
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                nextPicture();
                                setImages();

                            }
                        });
                    }
                }, 2000, 2000);
                buttonPlayStop.setText(R.string.button_stop);
                playStatus = getString(R.string.button_stop);


            } else {
                //Next, Backボタンをタップ可に
                buttonNext.setEnabled(true);
                buttonBack.setEnabled(true);
                buttonNext.setVisibility(View.VISIBLE);
                buttonBack.setVisibility(View.VISIBLE);

                //タイマーをキャンセル
                timer.cancel();
                buttonPlayStop.setText(R.string.button_play);
                playStatus = getString(R.string.button_play);


            }
        }
    }
    private void nextPicture() {
        if (pic_selected < pic_size - 1) {
            pic_selected++;
        } else if (pic_selected == pic_size - 1) {
            pic_selected = 0;
        }
    }
    private void backPicture() {
        if (pic_selected > 0) {
            pic_selected--;
        } else if (pic_selected <= 0) {
            pic_selected = pic_size - 1;
        }
    }
}