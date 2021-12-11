package com.example.imagetranslation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Base64;
import android.util.Log;
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

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class webview extends AppCompatActivity implements OnEditorActionListener {
    private InputMethodManager imm;
    private WebView webview;
    private long backBtnTime = 0;
    public Button menuSC, menuBack, menuForward, menuRefresh, buttonGo;
    private EditText et_url;

    private FirebaseStorage storage;

    private TextToSpeech tts;
    private String encodedImg;
    private String imgPath;
    private String imgUri;
    private String ocrText;

    private String ocrApiGwUrl;
    private String ocrSecretKey;
    private String nmtClientId;
    private String nmtClientSecret;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        SharedPreferences sharedPref = getSharedPreferences("PREF", Context.MODE_PRIVATE);
        ocrApiGwUrl = sharedPref.getString("ocr_api_gw_url", "https://75b7e8ede5b84d4280e129049fa60039.apigw.ntruss.com/custom/v1/12543/2ad8a7049d7c5511ac254f5f51fe70a046ebd884729056f0fe57f5160d467153/general");
        ocrSecretKey = sharedPref.getString("ocr_secret_key", "c1dPTVltRVFobFV6UXVjQXdFaWZsb1lTbHJ0T0Z6d1Q=");
        nmtClientId = "pBQUOpspBX4fPLfJYidC";
        nmtClientSecret = "hDELh4QLQC";

        imgUri = "https://firebasestorage.googleapis.com/v0/b/it-browser-eec3a.appspot.com/o/images%2F20211125181227.png?alt=media&token=d05f5ac5-27d2-4f95-bdb7-7fcfffa07ed3";


        // 스토리지 생성
        storage = FirebaseStorage.getInstance();

        // 액션바 제거
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        et_url = (EditText) findViewById(R.id.enter_url);
        buttonGo = findViewById(R.id.buttonGO);

        et_url.setOnEditorActionListener(this);

        menuSC = findViewById(R.id.menuSC);
        menuBack = findViewById(R.id.menuBack);
        menuForward = findViewById(R.id.menuForward);
        menuRefresh = findViewById(R.id.menuRefresh);

        Intent secondIntent = getIntent();
        String get_url = secondIntent.getStringExtra("url");

        // 웹뷰 시작
        webview = (WebView) findViewById(R.id.webView);
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

        //TTS 객체 초기화
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int state) {
                if (state == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.KOREAN);
                } else {
                    Log.e("TTS", "TTS 객체 초기화 오류");
                }
            }
        });

        menuBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webview.canGoBack()) {
                    webview.goBack();
                }
            }
        });
        menuForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webview.canGoForward()) {
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
                try {
                    screenCapture();
                    uploadStorage(imgPath);
                    webview.OcrTask ocrTask = new webview.OcrTask();
                    ocrTask.execute(ocrApiGwUrl, ocrSecretKey, imgUri);
                    webview.NmtTask task = new NmtTask();
                    task.execute(ocrText, "en", "kr", nmtClientId, nmtClientSecret);
                    Log.i("OCR","detectedText:"+ ocrText);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    // 파파고 핸들러
    @SuppressLint("HandlerLeak")
    Handler papago_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String resultWord = bundle.getString("resultWord");
            Log.i("papago", resultWord);
            tts.speak(resultWord, TextToSpeech.QUEUE_FLUSH, null);
        }
    };

    private void screenCapture() {
        webview.getDrawingCache(true);
        webview.buildDrawingCache();
        Bitmap captureView = webview.getDrawingCache();
        FileOutputStream fos = null;

        //dir이름과 path지정. dir가 없을 경우 dir 생성
        String CAPTURE_PATH = "/CAPTURE_TEST";
        String strFolderPath = "/data/data/com.example.imagetranslation" + CAPTURE_PATH;
        File folder = new File(strFolderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        //생성될 img이름 지정
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date currentTime = new Date();
        String dateString = formatter.format(currentTime);

        String strFilePath = strFolderPath + "/" + dateString + ".png";
        imgPath = strFilePath;
        File fileCacheItem = new File(strFilePath);

        try {
            fos = new FileOutputStream(fileCacheItem);
            captureView.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), dateString + ".png 저장", Toast.LENGTH_LONG).show();
        webview.getDrawingCache(false);
        webview.destroyDrawingCache();
    }

    // 스토리지 업로드
    private void uploadStorage(String uri) throws FileNotFoundException {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        Uri file = Uri.fromFile(new File(uri));
        final StorageReference riversRef = storageRef.child("images/" + file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return riversRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(webview.this, file.getLastPathSegment() + "저장", Toast.LENGTH_SHORT).show();
                    imgUri = task.getResult().toString();
                    Log.i("SC", imgUri + "업로드 성공");

                } else {
                    Log.e("SC", "업로드 실패");
                }
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
        } else if (0 <= gapTime && 2000 >= gapTime) {
            super.onBackPressed();
        } else {
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

    public class OcrTask extends AsyncTask<String, String, String> {
        @Override
        public String doInBackground(String... strings) {
            Log.d("OCR", "ocr doInBack");
            return OcrProc.main(strings[0], strings[1], strings[2]);
        }
        @Override
        protected void onPostExecute(String result) {
            ReturnOcrResult(result);
        }
    }

    private void ReturnOcrResult(String result) {
        String detectedText = "";
        String rlt = result;
        try {
            JSONObject jsonObject = new JSONObject(rlt);
            JSONArray jsonArray = jsonObject.getJSONArray("images");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONArray jsonArray_fields = jsonArray.getJSONObject(i).getJSONArray("fields");
                for (int j = 0; j < jsonArray_fields.length(); j++) {
                    String inferText = jsonArray_fields.getJSONObject(j).getString("inferText");
                    detectedText += inferText;
                    detectedText += " ";
                }
            }
            Log.i("OCR","detectedText:"+ detectedText);
            CSSExecute(detectedText);
        } catch (Exception e) {
            Log.e("OCR","ocr 실패");

        }
    }

    private void CSSExecute(String result) {
        webview.NmtTask task = new NmtTask();
        task.execute(result, "en", "kr", nmtClientId, nmtClientSecret);
        Log.d("OCR", "CSS");
        Log.i("OCR", "message:"+result);
    }


    public class NmtTask extends AsyncTask<String, String, String> {
        @Override
        public String doInBackground(String... strings) {
            Log.d("OCR", "nmt doInBack");
            return NmtProc.main(strings[0], strings[1], strings[2], strings[3], strings[4]);
        }
        @Override
        protected void onPostExecute(String result) {
            ReturnNmtResult(result);
            Log.d("OCR", "nmt returnResult");
        }
    }

    public void ReturnNmtResult(String result) {
        //{"message":{"@type":"response","@service":"naverservice.nmt.proxy","@version":"1.0.0","result":{"srcLangType":"ko","tarLangType":"en","translatedText":"Hello."}}}
        String rlt = result;
        Log.d("OCR","rnr rlt" + rlt);
        try {
            JSONObject jsonObject = new JSONObject(rlt);
            String text = jsonObject.getString("message");
            jsonObject = new JSONObject(text);
            jsonObject = new JSONObject(jsonObject.getString("result"));
            text = jsonObject.getString("translatedText");
            //System.out.println(text);

            Log.i("OCR","text:"+text);
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            Log.d("OCR", "Nmt 결과: " + text);
        } catch (Exception e) {
            Log.e("OCR", "Nmt 오류");
        }
    }

}


