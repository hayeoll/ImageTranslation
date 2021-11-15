package com.example.imagetranslation;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button buttonN;
    private Button buttonG;
    private Button buttonY;
    private Button buttonD;
    private Button buttonH;
    private Button buttonGo;
    private Button buttonP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 네이버 즐겨찾기
        buttonN = findViewById(R.id.buttonN);
        buttonN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), webview.class);
                intent.putExtra("url", "https://www.naver.com");
                startActivity(intent);
            }
        });

        // 구글 즐겨찾기
        buttonG = findViewById(R.id.buttonG);
        buttonG.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), webview.class);
                intent.putExtra("url", "https://www.google.com");
                startActivity(intent);
            }
        });

        // 유튜브 즐겨찾기
        buttonY = findViewById(R.id.buttonY);
        buttonY.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), webview.class);
                intent.putExtra("url", "https://www.youtube.com");
                startActivity(intent);
            }
        });

        // 다음 즐겨찾기
        buttonD = findViewById(R.id.buttonD);
        buttonD.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), webview.class);
                intent.putExtra("url", "https://www.daum.net");
                startActivity(intent);
            }
        });

        // 홍익대학교 즐겨찾기
        buttonH = findViewById(R.id.buttonH);
        buttonH.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), webview.class);
                intent.putExtra("url", "http://www.hongik.ac.kr");
                startActivity(intent);
            }
        });

        //setting
        buttonP = findViewById(R.id.buttonP);
        buttonP.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.buttonP:
                        Intent intent = new Intent(MainActivity.this, setting.class);
                        startActivityForResult(intent, 1);

                        break;
                }
            }
        });

        // URL 입력받아 이동
        buttonGo = findViewById(R.id.buttonGO);
        buttonGo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText et_url = (EditText) findViewById(R.id.enter_url);
                String str_url = et_url.getText().toString();
                if (!str_url.startsWith("https://") && !str_url.startsWith("http://")) {
                    str_url = "http://" + str_url;
                }
                Intent intent = new Intent(getApplicationContext(), webview.class);
                intent.putExtra("url", str_url);
                startActivity(intent);
            }
        });
    }
}