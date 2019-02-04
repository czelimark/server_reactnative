package chis.pmobile.main.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import chis.pmobile.R;
import chis.pmobile.main.model.Movie;
import chis.pmobile.main.services.ApiController;

public class AddMovieActivity extends AppCompatActivity {

    private final String FILE = "pmobile.share.txt";

    private EditText titleEditText;
    private EditText genreEditText;
    private EditText directorEditText;
    private EditText yearEditText;
    private Button addButton;
    private SharedPreferences sharedPreferences;

    private ApiController apiController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);

        sharedPreferences = this.getSharedPreferences(FILE,  Context.MODE_PRIVATE);
        if (! sharedPreferences.getBoolean("LOGGED_IN", false)) {
            this.finish();
        }

        apiController = new ApiController(this);

        titleEditText = findViewById(R.id.title_edit_text);
        genreEditText = findViewById(R.id.genre_edit_text);
        directorEditText = findViewById(R.id.director_edit_text);
        yearEditText = findViewById(R.id.year_edit_text);
        addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(this::on_add_press);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::item_selection);
    }

    private void on_add_press(View view) {
        String title = titleEditText.getText().toString();
        String genre = genreEditText.getText().toString();
        String director = directorEditText.getText().toString();
        String year = yearEditText.getText().toString();
        String message = String.format("(%s, %s, %s %s)", title, genre, director, year);
        Movie movie = new Movie(title, genre, director, year);

        if (isNetworkConnected()) {
            Toast.makeText(this, "Movie added to server&local : " + message, Toast.LENGTH_LONG).show();

            saveToServer(movie);
            saveToLocal(movie);
        }
        else {
            Toast.makeText(this, "Movie added just to local : " + message, Toast.LENGTH_LONG).show();
            saveToLocal(movie);
        }
    }

    private boolean item_selection(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.home_item) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
        else if (id == R.id.add_item) {
            Toast.makeText(this, "already there", Toast.LENGTH_LONG).show();
        }
        else if (id == R.id.logout_item) {
            sharedPreferences.edit().putBoolean("LOGGED_IN", false).apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            this.finish();
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (! sharedPreferences.getBoolean("LOGGED_IN", false)) {
            this.finish();
        }
    }

    private void saveToServer(Movie movie) {
        apiController.addMovieToServer(movie);
    }
    private void saveToLocal(Movie movie) {
        apiController.addMovieToLocal(movie);
    }

    //TODO iSNetworkConnected - just one
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
