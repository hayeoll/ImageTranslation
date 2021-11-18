package com.example.imagetranslation;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import android.widget.TextView.OnEditorActionListener;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class webview extends AppCompatActivity implements OnEditorActionListener {
    private InputMethodManager imm;
    private WebView webview;
    private long backBtnTime = 0;
    public Button menuSC, menuBack, menuForward, menuRefresh, buttonGo;
    private EditText et_url;

    private Bitmap bitmap;
    private String encodedImg;
    private String imgPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        // 액션바 제거
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        et_url= (EditText)findViewById(R.id.enter_url);
        buttonGo = findViewById(R.id.buttonGO);

        et_url.setOnEditorActionListener(this);

        menuSC = findViewById(R.id.menuSC);
        menuBack = findViewById(R.id.menuBack);
        menuForward = findViewById(R.id.menuForward);
        menuRefresh = findViewById(R.id.menuRefresh);

        Intent secondIntent = getIntent();
        String get_url = secondIntent.getStringExtra("url");

        // 웹뷰 시작
        webview = (WebView)findViewById(R.id.webView);
        WebSettings webSettings = webview.getSettings(); // 세부 세팅
        webSettings.setJavaScriptEnabled(true); // 웹페이지 자바 스크립트 허용
        webSettings.setSupportMultipleWindows(false); // 새 창 띄우기 허용
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false); // 자바스크립트 멀티뷰 허용
        webSettings.setLoadWithOverviewMode(true); // 메타태그 허용
        webSettings.setUseWideViewPort(true); // 화면 사이즈 맞추기 허용
        webSettings.setSupportZoom(false); // 화면 줌 허용
        webSettings.setBuiltInZoomControls(false); // 화면 확대 축소 허용
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 컨텐츠 사이즈 맞추기
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 허용 여부
        webSettings.setDomStorageEnabled(true); // 로컬저장소 허용 여부
        webview.setWebChromeClient(new WebChromeClient()); // 크롬 브라우저

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webview.loadUrl(get_url);

        menuBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(webview.canGoBack()) {
                    webview.goBack();
                }
            }
        });
        menuForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(webview.canGoForward()) {
                    webview.goForward();
                }
            }
        });

        menuRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webview.reload();
            }
        });

        // Model Button
        menuSC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //drawing cache 허용.
                //cache에 실제로 cache저장
                //cache에 저장된 bitmap을 가져온다.
                webview.getDrawingCache(true);
                webview.buildDrawingCache();
                Bitmap captureView = webview.getDrawingCache();
                FileOutputStream fos = null;

                //dir이름과 path지정. dir가 없을 경우 dir 생성
                String CAPTURE_PATH = "/CAPTURE_TEST";
                String strFolderPath = "/data/data/com.example.imagetranslation" + CAPTURE_PATH;
                File folder = new File(strFolderPath);
                if(!folder.exists()){
                    folder.mkdirs();
                }

                //생성될 img이름 지정
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                Date currentTime = new Date();
                String dateString = formatter.format(currentTime);

                String strFilePath = strFolderPath + "/" + dateString +".png";
                File fileCacheItem = new File(strFilePath);

                try {
                    fos = new FileOutputStream(fileCacheItem);
                    captureView.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e){
                    e.printStackTrace();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), dateString + ".png 저장", Toast.LENGTH_LONG).show();
                webview.getDrawingCache(false);
                webview.destroyDrawingCache();



/*
                Log.d("SC","SC Start");

                // Screen Capture
                imgPath="file:///storage/emulated/0/Download/1.png";
                Log.d("SC", "이미지 패스: " +  imgPath);

                // 이미지 가져오기
                try {
                    getIMG(imgPath);
                    Log.d("SC", "uri : " + fileUri(imgPath));
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("IMG", "이미지 가져오기 실패");
                }
                // OCR
                if (encodedImg != null) {
                    Vision vi = new Vision();
                    vi.OCR(encodedImg);

                    // 테스트용 토스트
                    if (vi.text != null) {
                        Log.i("OCR", "OCR 추출 : " + vi.text);
                        Toast.makeText(webview.this, "OCR 추출 : " + vi.text, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("OCR", "OCR 실패");
                    }
                }


                // 번역
                // TTS 초기화
                // TTS
*/

            }
        });

        // webview 내 주소 입력창
        buttonGo = findViewById(R.id.buttonGO);
        buttonGo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                et_url = (EditText) findViewById(R.id.enter_url);
                String str_url = et_url.getText().toString();
                if (!str_url.startsWith("https://") && !str_url.startsWith("http://")) {
                    str_url = "http://" + str_url;
                }
                webview.loadUrl(str_url);
            }
        });

    }

    // 뒤로가기 2번 입력시 webview 종료
    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;
        if (webview.canGoBack()) {
            webview.goBack();
        }
        else if (0 <= gapTime && 2000 >= gapTime) {
            super.onBackPressed();
        }
        else {
            backBtnTime = curTime;
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 키보드 엔터 키 입력시 키보드 내림
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        // TODO Auto-generated method stub

        //오버라이드한 onEditorAction() 메소드

        if (v.getId() == R.id.enter_url && actionId == EditorInfo.IME_ACTION_DONE) { // 뷰의 id를 식별, 키보드의 완료 키 입력 검출

            String str_url = et_url.getText().toString();
            if (!str_url.startsWith("https://") && !str_url.startsWith("http://")) {
                str_url = "http://" + str_url;
            }
            webview.loadUrl(str_url);

        }

        return false;
    }

    // view의 아무 부분 클릭시 키보드 내림
    public final void hideKeyboard(View v) {
        InputMethodManager var10000 = this.imm;
        if (var10000 != null) {
            var10000.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    // filePath -> uri 변환
    private Uri fileUri(String fp){
        String fileName= "file:///storage/emulated/0/Download/1.png";

        Uri fileUri = Uri.parse( fileName );

        String filePath = fileUri.getPath();

        Cursor c = getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,

                null, "_data = '" + filePath + "'", null, null );

        c.moveToNext();

        int id = c.getInt( c.getColumnIndex( "_id" ) );

        Uri uri = ContentUris.withAppendedId( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id );


        return uri;
   }

    // 이미지 초기화
    private void getIMG(String fp) throws IOException {
        String path = fp;
        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri(path));
        bitmap = scaleBitmapDown(bitmap, 640);
        // Convert bitmap to base64 encoded string
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String base64encoded = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        encodedImg = base64encoded;
    }

    // 비트맵 스케일 다운
    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }
}