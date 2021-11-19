package com.example.imagetranslation;

import android.content.Context;
import android.util.Log;
import android.widget.*;
import androidx.annotation.NonNull;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = null;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private SignInButton signInButton;

    public static Context context_main;
    public String selectLang;

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

        signInButton = findViewById(R.id.signInButton);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            signInButton.setVisibility(View.GONE);
        }
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

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
    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("login", "로그인 성공"+account.getId());
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("login", "로그인 실패");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.w("login", "로그인 성공");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("login", "로그인 실패");
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) { //update ui code here
        if (user != null) {
            signInButton.setVisibility(View.GONE);

        }
    }

}