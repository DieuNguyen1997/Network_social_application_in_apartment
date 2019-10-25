package com.example.networksocialapplication.reset_password;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.networksocialapplication.R;
import com.example.networksocialapplication.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mEdtEmail;
    private Button mBtnSend;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();
        initToolbar();
        initView();
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Quên mật khẩu");

    }

    private void initView() {
        mEdtEmail = findViewById(R.id.edt_email_reset_pass);
        mBtnSend = findViewById(R.id.btn_send_email_reset_pass);

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEdtEmail.getText().toString();
                if (TextUtils.isEmpty(email)){
                    mEdtEmail.setError("Email không được để trống!");
                }
                else {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Hãy kiểm tra email của bạn", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(), "Errorr" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
