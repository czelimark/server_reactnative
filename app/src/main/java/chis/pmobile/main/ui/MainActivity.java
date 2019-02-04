package chis.pmobile.main.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import chis.pmobile.R;
import chis.pmobile.main.model.Movie;
import chis.pmobile.main.services.ApiController;


public class MainActivity extends AppCompatActivity
     {

    private final String FILE = "pmobile.share.txt";
    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;
    private ApiController apiController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences(FILE,  Context.MODE_PRIVATE);
        if (! sharedPreferences.getBoolean("LOGGED_IN", false)) {
            this.finish();
        }

        recyclerView = findViewById(R.id.movies_list);
        apiController = new ApiController(this, recyclerView);
        apiController.updateList();
        apiController.saveMoviesToLocal();

        startTimer();

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::item_selection);
    }

    private void startTimer() {
        Timer timerAsync = new Timer();
        TimerTask timerTaskAsync = new TimerTask() {
           @Override
           public void run() {
               runOnUiThread(() -> {
                   List<Movie> localMovies = apiController.getMoviesFromLocal();
                   apiController.updateMoviesFromServer(localMovies);
               });
           }
       };
        timerAsync.schedule(timerTaskAsync, 0, 10000);
    }

    private boolean item_selection(MenuItem menuItem) {

        int id = menuItem.getItemId();
        if (id == R.id.home_item) {
            Toast.makeText(this, "already there", Toast.LENGTH_LONG).show();
        }
        else if (id == R.id.add_item) {
            Intent intent = new Intent(this, AddMovieActivity.class);
            startActivity(intent);
            this.finish();
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
        apiController.updateList();
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}
