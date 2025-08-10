package com.example.onlineshop.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onlineshop.Domain.UserModel;
import com.example.onlineshop.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        setVariable();
    }

    private void setVariable() {
        binding.registerBtn.setOnClickListener(v -> {
            String name = binding.nameEdt.getText().toString().trim();
            String email = binding.emailEdt.getText().toString().trim();
            String password = binding.passwordEdt.getText().toString().trim();

            if (name.isEmpty()) {
                binding.nameEdt.setError("Name is required");
                binding.nameEdt.requestFocus();
                return;
            }
            if (email.isEmpty()) {
                binding.emailEdt.setError("Email is required");
                binding.emailEdt.requestFocus();
                return;
            }
            if (password.length() < 6) {
                binding.passwordEdt.setError("Password must be at least 6 characters");
                binding.passwordEdt.requestFocus();
                return;
            }

            binding.progressBar.setVisibility(View.VISIBLE);
            binding.registerBtn.setVisibility(View.GONE);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            UserModel user = new UserModel(name, email, userId);

                            databaseReference.child("Users").child(userId).setValue(user).addOnCompleteListener(dbTask -> {
                                binding.progressBar.setVisibility(View.GONE);
                                binding.registerBtn.setVisibility(View.VISIBLE);

                                if (dbTask.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Failed to save user data: " + dbTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });

                        } else {
                            Toast.makeText(RegisterActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            binding.progressBar.setVisibility(View.GONE);
                            binding.registerBtn.setVisibility(View.VISIBLE);
                        }
                    });
        });

        binding.loginTxt.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class))
        );
    }
}