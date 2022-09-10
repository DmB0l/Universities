package com.example.universities.autorization;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.universities.R;
import com.example.universities.base.BaseActivity;
import com.example.universities.uniList.UniversitiesList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is answer for user profile (sign in, sign up, user information, like universities)
 */
public class Authorization extends BaseActivity {
    static final int GALLERY_REQUEST = 1;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private EditText loginTextEdit;
    private EditText passwordTextEdit;
    private Button signInButton;
    private Button signUpButton;
    private Button favouritesButton;

    private TextView mLoginTextView;
    private TextView userNameTextView;
    private EditText userNameEditText;
    private Button addUserNameButton;
    private Button exitButton;

    private ImageButton imageButton;
    private int imageButtonWidth;
    private int imageButtonHeight;

    private SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onStart() {
        super.onStart();
        signScreen();
    }

    private void init() {
        loginTextEdit = findViewById(R.id.email_address_edit_t);
        passwordTextEdit = findViewById(R.id.password_edit_t);
        signInButton = findViewById(R.id.sign_in_button);
        signUpButton = findViewById(R.id.sign_up_button);
        favouritesButton = findViewById(R.id.favouritesButton);
        mLoginTextView = findViewById(R.id.email_View);
        exitButton = findViewById(R.id.exit_button);
        userNameTextView = findViewById(R.id.nameUserTextView);
        userNameEditText = findViewById(R.id.nameUserEditText);
        addUserNameButton = findViewById(R.id.addNameUserButton);
        imageButton = findViewById(R.id.imageButton);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        pref = getSharedPreferences("IMAGE", MODE_PRIVATE);

        imageButtonWidth = imageButton.getWidth();
        imageButtonHeight = imageButton.getHeight();
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_authorization;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.person;
    }

    public void signUp(View v) {
        if (!(loginTextEdit.getText().toString().isEmpty() && passwordTextEdit.getText().toString().isEmpty())) {
            String login = loginTextEdit.getText().toString();
            String password = passwordTextEdit.getText().toString();
            if (password.length() >= 6) {
                mAuth.createUserWithEmailAndPassword(login, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //mDatabase.child("users").child(deleteErrorStringsFromString2(login)).setValue("");
                                    sendEmailVer();
                                    Toast.makeText(getApplicationContext(), "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "При регистрации произошли ошибки", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(getApplicationContext(), "Пароль должен быть больше 6 символов", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Введите email и пароль", Toast.LENGTH_SHORT).show();
        }
    }

    public void signIn(View v) {
        if (!(loginTextEdit.getText().toString().isEmpty() && passwordTextEdit.getText().toString().isEmpty())) {
            mAuth.signInWithEmailAndPassword(loginTextEdit.getText().toString(), passwordTextEdit.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null && user.isEmailVerified()) {
                                    String displayName = user.getDisplayName();
                                    if (displayName != null && !(displayName.trim().isEmpty())) {
                                        signScreen();
                                    } else {
                                        nameScreen();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Пройдите верификацию по ссылке на почте", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                                notSignScreen();
                            }
                        }
                    });
        }
    }

    public void signOut(View v) {
        FirebaseAuth.getInstance().signOut();
        imageButton.setImageResource(R.drawable.person);
        notSignScreen();
    }

    private void sendEmailVer() {
        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Для доступа в аккаунт перейдите по ссылке на вашей почте", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "...", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void notSignScreen() {
        mLoginTextView.setVisibility(View.GONE);
        exitButton.setVisibility(View.GONE);
        userNameTextView.setVisibility(View.GONE);
        userNameEditText.setVisibility(View.GONE);
        addUserNameButton.setVisibility(View.GONE);
        favouritesButton.setVisibility(View.GONE);
        imageButton.setVisibility(View.GONE);

        loginTextEdit.setVisibility(View.VISIBLE);
        passwordTextEdit.setVisibility(View.VISIBLE);
        signUpButton.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.VISIBLE);
    }

    private void nameScreen() {
        userNameEditText.setVisibility(View.VISIBLE);
        addUserNameButton.setVisibility(View.VISIBLE);
        exitButton.setVisibility(View.VISIBLE);

        mLoginTextView.setVisibility(View.GONE);
        userNameTextView.setVisibility(View.GONE);
        favouritesButton.setVisibility(View.GONE);
        loginTextEdit.setVisibility(View.GONE);
        passwordTextEdit.setVisibility(View.GONE);
        signUpButton.setVisibility(View.GONE);
        signInButton.setVisibility(View.GONE);
    }

    private void signScreen() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            if (currentUser.getDisplayName() != null) {
                String str = "email: " + currentUser.getEmail();
                mLoginTextView.setText(str);
                imageButton.setVisibility(View.VISIBLE);

                File f = new File(getApplicationContext().getCacheDir(), currentUser.getDisplayName());
                if (f.exists()) {
                    String filePath = f.getPath();
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    //imageButton.setImageBitmap(bitmap);

                    int targetW = imageButton.getDrawable().getBounds().width();
                    int targetH = imageButton.getDrawable().getBounds().height();

                    int layoutW = imageButton.getLayoutParams().width;
                    int layoutH = imageButton.getLayoutParams().height;
                    targetW = Math.max(targetW, layoutW);
                    targetH = Math.max(targetW, layoutH);

                    android.graphics.Rect r = new android.graphics.Rect();
                    imageButton.getDrawingRect(r);
                    int rectW = r.width();
                    int rectH = r.height();

                    targetW = Math.max(targetW, rectW);
                    targetH = Math.max(targetH, rectH);

                    imageButton.setImageBitmap(Bitmap.createScaledBitmap(bitmap, targetW, targetH, false));
                }

                mLoginTextView.setVisibility(View.VISIBLE);
                userNameTextView.setVisibility(View.VISIBLE);
                exitButton.setVisibility(View.VISIBLE);
                favouritesButton.setVisibility(View.VISIBLE);

                String string = "Имя пользователя: " + currentUser.getDisplayName();
                userNameTextView.setText(string);

                userNameEditText.setVisibility(View.GONE);
                addUserNameButton.setVisibility(View.GONE);
                loginTextEdit.setVisibility(View.GONE);
                passwordTextEdit.setVisibility(View.GONE);
                signUpButton.setVisibility(View.GONE);
                signInButton.setVisibility(View.GONE);
            } else nameScreen();
        } else notSignScreen();
    }

    public void addDisplayName(View view) {
        FirebaseUser user = mAuth.getCurrentUser();
        String userName = userNameEditText.getText().toString().trim();
        if (user != null && !(userNameEditText.getText().toString().trim().isEmpty())) {

            List<String> names = new ArrayList<>();

            mDatabase.child("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        if (!names.isEmpty()) names.clear();
                        for (DataSnapshot ds : task.getResult().getChildren()) {
                            User value = ds.getValue(User.class);
                            if (value != null)
                                names.add(value.userName);
                        }

                        if (!names.contains(userName)) {
                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(userName).build();

                            user.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.i("Testing", "User Profile Updated");
                                        User us = new User(userName, null);
                                        mDatabase.child("users").child(deleteErrorStringsFromString2(user.getEmail())).setValue(us);
                                        signScreen();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Это имя уже занято, попробуйте другое", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    public void favouritesButtonListener(View view) {
        Intent intent = new Intent(getApplicationContext(), UniversitiesList.class);
        mDatabase.child("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                FirebaseUser currentUser = mAuth.getCurrentUser();

                if (currentUser != null) {
                    if (currentUser.getDisplayName() != null) {
                        User user = task.getResult().child(deleteErrorStringsFromString2(currentUser.getEmail())).getValue(User.class);

                        if (user != null) {
                            List<String> favUnis = user.favouritesUniversities;

                            if (favUnis != null)
                                intent.putExtra("favourites", favUnis.toArray(new String[0]));

                            else
                                intent.putExtra("favourites", new String[0]);

                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Введите имя пользователя", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    public void imageButtonListener(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        photoPickerIntent.setType("image/*");
        galleryActionResultLauncher.launch(photoPickerIntent);
    }

    private ActivityResultLauncher<Intent> galleryActionResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent data = result.getData();
                        Uri image = data.getData();

                        Log.i("app", image.toString());
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image);
                            imageButton.setImageBitmap(Bitmap.createScaledBitmap(bitmap, imageButton.getWidth(), imageButton.getHeight(), false));

                            File f = new File(getApplicationContext().getCacheDir(), user.getDisplayName());
                            try {
                                f.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                            byte[] bitmapdata = bos.toByteArray();

                            FileOutputStream fos = new FileOutputStream(f);
                            fos.write(bitmapdata);
                            fos.flush();
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "...", Toast.LENGTH_SHORT).show();
                    }
                }
            });

}