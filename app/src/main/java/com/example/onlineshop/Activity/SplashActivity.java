package com.example.onlineshop.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.example.onlineshop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private AppCompatButton startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        startBtn = findViewById(R.id.startBtn);

        startBtn.setOnClickListener(v -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser != null) {
                goToActivity(MainActivity.class);
            } else {
                goToActivity(LoginActivity.class);
            }
        });
    }
    private void goToActivity(Class<?> activityClass) {
        Intent intent = new Intent(SplashActivity.this, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
