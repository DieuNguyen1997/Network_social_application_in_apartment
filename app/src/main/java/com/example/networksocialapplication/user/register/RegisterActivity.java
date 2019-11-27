package com.example.networksocialapplication.user.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.networksocialapplication.R;
import com.example.networksocialapplication.admin.setting_infor_manager.SettingInforManagerActivity;
import com.example.networksocialapplication.resident.homeapp.HomeActivity;
import com.example.networksocialapplication.resident.homeapp.setting_image_profile.SettingImageProfileActivity;
import com.example.networksocialapplication.user.login.LoginActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class RegisterActivity extends AppCompatActivity {
    private Button mBtnSignUpGoogle;
    private EditText mEdtUsername;
    private EditText mEdtPassword;
    private EditText mEdtConfirmPassword;
    private Button mBtnSignUp;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;
    private GoogleSignInOptions mSignInOptions;
    private GoogleApiClient mGoogleApiClient;

    private final static int RC_SIGN_IN = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Toast.makeText(RegisterActivity.this, "You got an error", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, mSignInOptions)
                .build();
        mProgressDialog = new ProgressDialog(this);
        init();
    }

    private void init() {
        mEdtUsername = findViewById(R.id.edt_username_rs);
        mEdtPassword = findViewById(R.id.edt_password_rs);
        mEdtConfirmPassword = findViewById(R.id.edt_repassword_rs);
        mBtnSignUp = findViewById(R.id.btn_signup);
        mBtnSignUpGoogle = findViewById(R.id.btn_signup_google);

        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccount();
                mProgressDialog.setTitle("Đăng ký tài khoản");
                mProgressDialog.setMessage("Vui lòng đợi trong giây lát");
                mProgressDialog.show();
            }
        });
        mBtnSignUpGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });
    }

    private void signInWithGoogle() {
        mProgressDialog.setMessage("Đang đăng nhập bằng google. Vui lòng đợi trong giây lát!!");
        mProgressDialog.show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (googleSignInResult.isSuccess()) {

                GoogleSignInAccount account = googleSignInResult.getSignInAccount();

                firebaseAuthWithGoogle(account);
            }

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        Toast.makeText(RegisterActivity.this, "" + authCredential.getProvider(), Toast.LENGTH_LONG).show();

        mAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task AuthResultTask) {
                        if (AuthResultTask.isSuccessful()) {
                            mProgressDialog.dismiss();
                            // Getting Current Login user details.
                            Intent intent = new Intent(RegisterActivity.this, SettingImageProfileActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void createNewAccount() {
        final String email = mEdtUsername.getText().toString();
        final String password = mEdtPassword.getText().toString();
        String repass = mEdtConfirmPassword.getText().toString();


        if (!TextUtils.isEmpty(email)) {
            if (!TextUtils.isEmpty(password)) {
                if (!TextUtils.isEmpty(repass)) {
                    if (repass.equals(password)) {
                        if (email.equals("admin@gmail.com") && password.equals("admin123")) {
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                mProgressDialog.dismiss();
                                                Intent intent = new Intent(RegisterActivity.this, SettingInforManagerActivity.class);
                                                startActivity(intent);
                                            } else {
                                                mProgressDialog.dismiss();
                                                String message = task.getException().getMessage();
                                                Toast.makeText(RegisterActivity.this, "Bạn gặp vấn đề :" + message, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                mProgressDialog.dismiss();
                                                Intent intent = new Intent(RegisterActivity.this, SettingImageProfileActivity.class);
                                                startActivity(intent);
                                            } else {
                                                mProgressDialog.dismiss();
                                                String message = task.getException().getMessage();
                                                Toast.makeText(RegisterActivity.this, "Bạn gặp vấn đề :" + message, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }

                    } else {
                        mProgressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Mật khẩu không trùng khớp!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập lại mật khẩu!", Toast.LENGTH_SHORT).show();

                }
            } else {
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Bạn chưa nhập mật khẩu!", Toast.LENGTH_SHORT).show();
            }
        } else {
            mProgressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập email!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void OnClickTxt(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
