package com.example.universities.uniInfo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.universities.R;
import com.example.universities.autorization.User;
import com.example.universities.base.BaseActivity;
import com.example.universities.db.DatabaseHelperUniversities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Information about university, like button, comment
 */
public class UniversityInfo extends BaseActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseFireBase;

    private ImageView imageUniView;
    private TextView nameUniView;
    private TextView coordsView;
    private TextView professionsView;
    private TextView kafedraUni;
    private TextView dopInf;
    private TextView contactInf;
    private TextView rating;
    private TextView comment;
    private TextView noComment;
    private EditText editTextComment;
    private Button buttonComment;
    private LikeButton likeButtonHeart;

    private DatabaseHelperUniversities mDBHelper;
    private SQLiteDatabase sqLiteDatabase;

    private int position;
    String nameUni;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        fillComponentsFromDb();
    }

    private void init() {
        imageUniView = findViewById(R.id.imageUni);
        nameUniView = findViewById(R.id.nameUni);
        coordsView = findViewById(R.id.coordsUni);
        professionsView = findViewById(R.id.professionsUni);
        kafedraUni = findViewById(R.id.kafedraUni);
        dopInf = findViewById(R.id.dopInf);
        contactInf = findViewById(R.id.contactInf);
        rating = findViewById(R.id.rating);
        comment = findViewById(R.id.comment);
        editTextComment = findViewById(R.id.editTextComment);
        buttonComment = findViewById(R.id.buttonComment);
        noComment = findViewById(R.id.noMyComment);
        likeButtonHeart = findViewById(R.id.likeButtonHeart);

        mDBHelper = new DatabaseHelperUniversities(this);

        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        sqLiteDatabase = mDBHelper.getReadableDatabase();

        mAuth = FirebaseAuth.getInstance();
        mDatabaseFireBase = FirebaseDatabase.getInstance().getReference();
    }

    private void fillComponentsFromDb() {
        String urlImage, coords, bakalavr, specialitet, magistr, hostel, rulesUrl, adress, metro, phones, website, urlOtzovic;
        String[] professions, kaf;

        FirebaseUser currentUser = mAuth.getCurrentUser();

        Bundle arguments = getIntent().getExtras();
        position = arguments.getInt("position");

        // Отправляем запрос в БД
        Cursor cursor = sqLiteDatabase.query("universities", new String[]{"name", "image", "koords", "professions", "fak", "namesbak", "namespec",
                        "namemag", "hostel", "rulesurl", "adress", "metro", "phones", "website", "urlOtzovic"},
                null, null, null, null, null);
        cursor.moveToPosition(position);

        nameUni = cursor.getString(0);
        urlImage = cursor.getString(1);
        coords = cursor.getString(2);

        String str = cursor.getString(3);
        str = changeStringsWithCommaToSemicolon(str);
        professions = str.split(";");

        str = cursor.getString(4);
        str = changeStringsWithCommaToSemicolon(str);
        kaf = str.split(";");

        bakalavr = cursor.getString(5);
        specialitet = cursor.getString(6);
        magistr = cursor.getString(7);
        hostel = cursor.getString(8);
        rulesUrl = cursor.getString(9);
        adress = cursor.getString(10);
        metro = cursor.getString(11);
        phones = cursor.getString(12);
        website = cursor.getString(13);
        urlOtzovic = cursor.getString(14);

        cursor.close();

        if (currentUser == null)
            likeButtonHeart.setVisibility(View.GONE);
        else likeButtonHeart.setVisibility(View.VISIBLE);


        /**
         * кнопка лайк закрашивается, если универ уже в избранном
         */
        mDatabaseFireBase.child("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                FirebaseUser currentUser = mAuth.getCurrentUser();

                if (currentUser != null) {
                    for (DataSnapshot ds : task.getResult().getChildren()) {

                        if (ds.getKey().equals(deleteErrorStringsFromString2(currentUser.getEmail()))) {
                            User user = ds.getValue(User.class);

                            if (user != null) {
                                List<String> favUnis = user.favouritesUniversities;

                                if (favUnis != null) {
                                    if (favUnis.contains(deleteErrorStringsFromString(nameUni))) {
                                        likeButtonHeart.setLiked(true);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });


        /**
         * обработчик событий при нажатии на кнопку лайк
         */
        likeButtonHeart.setOnLikeListener(new loveButtonListener());


        /**
         * загрузка изображения и рейтинга
         */
        loadImageFromUrl(urlImage, imageUniView);
        new DownloadRatingOtzovik().execute(urlOtzovic);


        /**
         * загрузка всего остального
         */
        nameUniView.setText(nameUni);
        coordsView.setText(coords);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < professions.length; i++) {
            if (professions[i].charAt(0) == ' ') professions[i] = professions[i].substring(1);
            if (!(professions[i].charAt(0) == '-')) sb.append("-");
            if (!(professions[i].charAt(1) == ' ')) sb.append(" ");
            sb.append(professions[i]).append('\n');
        }
        professionsView.setText(sb.toString());

        sb = new StringBuilder();
        for (int i = 0; i < kaf.length; i++) {
            while (kaf[i].charAt(0) == ' ') kaf[i] = kaf[i].substring(1);
            if (!(kaf[i].charAt(0) == '-')) sb.append("-");
            if (kaf[i].length() > 1 && !(kaf[i].charAt(1) == ' ')) sb.append(" ");
            sb.append(kaf[i]).append('\n');
        }
        kafedraUni.setText(sb.toString());

        sb = new StringBuilder();
        sb.append("Бакалавриат: ");
        if (bakalavr.length() > 2) sb.append("Да");
        else sb.append("Нет");
        sb.append('\n');
        sb.append("Специалитет: ");
        if (specialitet.length() > 2) sb.append("Да");
        else sb.append("Нет");
        sb.append('\n');
        sb.append("Магистратура: ");
        if (magistr.length() > 2) sb.append("Да");
        else sb.append("Нет");
        sb.append('\n');
        sb.append("Общежитие: ").append(hostel).append('\n');
        sb.append("Правила приема: ").append(rulesUrl);
        dopInf.setText(sb.toString());

        sb = new StringBuilder();
        sb.append("Адрес: ").append(adress).append('\n');
        sb.append("Станция метро: ").append(metro).append('\n');
        sb.append("Телефон: ").append(phones).append('\n');
        sb.append("Сайт: ").append(website);
        contactInf.setText(sb.toString());

        readFirebaseDatabase();

        if (currentUser == null) {
            noCommentScreen("Чтобы оставлять комментарии, войдите в аккаунт");
        } else {
            String displayName = currentUser.getDisplayName();
            if (displayName != null && !(displayName.trim().isEmpty())) {
                agreeCommentScreen();
            } else {
                noCommentScreen("Введите имя в личном кабинете, которое будет отображаться в комментариях");
            }
        }
    }

    private void loadImageFromUrl(String url, ImageView iv) {
        Picasso.get().load(url).resize(1200, 650).error(R.drawable.no_photo3).into(iv);
    }

    private class DownloadRatingOtzovik extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            if (urls[0] != null) {
                try {
                    URLConnection connection = new URL(urls[0]).openConnection();
                    connection.addRequestProperty("User-Agent", "Mozilla");
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(5000);
                    InputStream response = connection.getInputStream();
                    Scanner scanner = new Scanner(response);
                    while (scanner.hasNext()) {
                        String str = scanner.nextLine().trim();
                        if (str.startsWith("<meta itemprop=\"ratingValue\" content")) {
                            Log.i("string html", str);
                            if (str.length() < 43)
                                str = str.substring(str.indexOf("t=\"") + 3, str.indexOf("t=\"") + 4);
                            else
                                str = str.substring(str.indexOf("t=\"") + 3, str.indexOf("t=\"") + 6);
                            return ("Рейтинг с сайта otzovik.com: " + str + " из 5");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(String result) {
            if (result != null)
                rating.setText(result);
            else rating.setText("Рейтинг не был загружен");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sqLiteDatabase.close();
        mDBHelper.close();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void readFirebaseDatabase() {
        mDatabaseFireBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                StringBuilder sb = new StringBuilder();
                for (DataSnapshot ds : dataSnapshot.child("comments").getChildren()) {
                    if (ds.getKey().equals(deleteErrorStringsFromString(nameUni))) {
                        for (DataSnapshot ds2 : ds.getChildren()) {
                            String value = ds2.getValue(String.class);
                            sb.append("Отзыв пользователя \"").append(ds2.getKey()).append("\": ").append('\n')
                                    .append(value).append('\n').append('\n');
                            Log.i("TAG", "Value is: " + value);
                        }
                    }
                }
                if (sb.length() != 0) comment.setText(sb.toString());
                else comment.setText("Никто еще не оставил комментариев");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

    private void noCommentScreen(String str) {
        noComment.setText(str);
        noComment.setVisibility(View.VISIBLE);
        editTextComment.setVisibility(View.GONE);
        buttonComment.setVisibility(View.GONE);
    }

    private void agreeCommentScreen() {
        noComment.setVisibility(View.GONE);
        editTextComment.setVisibility(View.VISIBLE);
        buttonComment.setVisibility(View.VISIBLE);
    }

    public void addComment(View view) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String textComment = editTextComment.getText().toString().trim();
            String userName = currentUser.getDisplayName();
            if (userName != null && !(textComment.isEmpty()))
                mDatabaseFireBase.child("comments").child(deleteErrorStringsFromString(nameUni)).child(userName).setValue(textComment);
        }
    }

    private class loveButtonListener implements OnLikeListener {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        @Override
        public void liked(LikeButton likeButton) {
            if (currentUser == null) {
                Toast.makeText(getApplicationContext(), "Зарегестрируйтесь, чтобы добавлять в избранное", Toast.LENGTH_SHORT).show();
            } else {
                mDatabaseFireBase.child("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {

                        for (DataSnapshot ds : task.getResult().getChildren()) {

                            Log.i("app", ds.getKey());
                            Log.i("app", task.getResult().getKey());

                            if (ds.getKey().equals(deleteErrorStringsFromString2(currentUser.getEmail()))) {
                                User value = ds.getValue(User.class);

                                if (value != null) {
                                    List<String> favUnis = value.favouritesUniversities;

                                    if (favUnis == null)
                                        favUnis = new ArrayList<>();

                                    favUnis.add(deleteErrorStringsFromString(nameUni));

                                    mDatabaseFireBase.child("users").child(deleteErrorStringsFromString2(currentUser.getEmail()))
                                            .child("favouritesUniversities").setValue(favUnis);
                                    break;
                                }
                            }
                        }
                    }
                });
            }
        }

        @Override
        public void unLiked(LikeButton likeButton) {
            if (currentUser == null) {
                Toast.makeText(getApplicationContext(), "Зарегестрируйтесь, чтобы добавлять в избранное", Toast.LENGTH_SHORT).show();
            } else {
                mDatabaseFireBase.child("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {

                        for (DataSnapshot ds : task.getResult().getChildren()) {

                            Log.i("app", ds.getKey());
                            Log.i("app", task.getResult().getKey());

                            if (ds.getKey().equals(deleteErrorStringsFromString2(currentUser.getEmail()))) {
                                User value = ds.getValue(User.class);

                                if (value != null) {
                                    List<String> favUnis = value.favouritesUniversities;

                                    if (favUnis == null)
                                        favUnis = new ArrayList<>();

                                    favUnis.remove(deleteErrorStringsFromString(nameUni));

                                    mDatabaseFireBase.child("users").child(deleteErrorStringsFromString2(currentUser.getEmail()))
                                            .child("favouritesUniversities").setValue(favUnis);
                                    break;
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_university_info;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.home;
    }
}