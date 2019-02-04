package chis.pmobile.main.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

import chis.pmobile.main.adapter.ItemClickListener;
import chis.pmobile.main.adapter.MovieListAdapter;
import chis.pmobile.main.model.Movie;
import chis.pmobile.main.model.User;
import chis.pmobile.main.ui.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiController implements ItemClickListener {

    private final String FILE = "pmobile.share.txt";


    private Context activityContext;
    private ApiService apiService;
    private MovieListAdapter movieListAdapter;

    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;


    public ApiController(Context activityContext) {
        this.activityContext = activityContext;
        sharedPreferences = activityContext.getSharedPreferences(FILE,  Context.MODE_PRIVATE);
        apiService = ServiceGenerator.createService(ApiService.class);
    }

    public ApiController (Context activityContext, RecyclerView recyclerView) {
        this.activityContext = activityContext;
        this.recyclerView = recyclerView;
        sharedPreferences = activityContext.getSharedPreferences(FILE,  Context.MODE_PRIVATE);
        apiService = ServiceGenerator.createService(ApiService.class);
    }


    @Override
    public void onClick(View view, int position) {
        AlertDialog alertDialog = new AlertDialog.Builder(activityContext)
                .setTitle("Warning")
                .setMessage("Delete movie")
                .setPositiveButton("YES", (dialog, which) -> deleteMovie(position))
                .setNegativeButton("NO", (dialog, which) -> {})
                .create();
        alertDialog.show();
    }


    public void login(User user) {
        Call<ResponseBody> call = apiService.login(user);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(activityContext, "Succesfull login", Toast.LENGTH_LONG).show();
                sharedPreferences.edit().putBoolean("LOGGED_IN", true).apply();
                startMainActivity();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(activityContext, "Can't login", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(activityContext, MainActivity.class);
        activityContext.startActivity(intent);
    }

    public void updateList() {
        Call<List<Movie>> call = apiService.getMovies();
        call.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                List<Movie> movies = response.body();
                movieListAdapter = new MovieListAdapter(activityContext, movies);
                    recyclerView.setAdapter(movieListAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(activityContext));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                movieListAdapter.setClickListener(ApiController.this);
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                Toast.makeText(activityContext, "failure retrofit: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateMoviesFromServer(List<Movie> localMovies) {
        Call<ResponseBody> call = apiService.updateMovies(localMovies);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //TODO
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //TODO
            }
        });
    }

    public void addMovieToServer(Movie movie) {

        Call<Movie> call = apiService.saveMovie(movie);
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                //TODO empty response
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                //Toast.makeText(activityContext, "FaileD to connect", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void addMovieToLocal(Movie movie) {
        List<Movie> movies = getMoviesFromLocal();
        movie.setId(UUID.randomUUID().toString());
        movies.add(movie);

        Gson gson = new Gson();
        String json = gson.toJson(movies);
        sharedPreferences.edit().putString("pmobile_lista", json).apply();
    }

    public void deleteMovie(int position) {
        deleteMovieFromServer(position);
        deleteMovieFromLocal(position);
        updateList();
    }

    public void deleteMovieFromServer(int position) {
        Movie movie = movieListAdapter.getItem(position);
        Call<ResponseBody> call = apiService.deleteMovie(movie.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //
                updateList();
                //
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //Toast.makeText(activityContext, "Error deleting movie from server",
                //       Toast.LENGTH_LONG).show();
            }
        });
    }

    public void deleteMovieFromLocal(int position) {
        Movie movie = movieListAdapter.getItem(position);
        List<Movie> movies = movieListAdapter.getMovieList();
        movies.remove(movie);

        Gson gson = new Gson();
        String json = gson.toJson(movies);
        sharedPreferences.edit().putString("pmobile_lista", json).apply();
    }

    public List<Movie> getMoviesFromLocal() {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Movie>>(){}.getType();
        String json = sharedPreferences.getString("pmobile_lista","");
        return gson.fromJson(json, type);
    }


    public void updateMoviesFromLocal(List<Movie> movies) {
        Gson gson = new Gson();
        String json = gson.toJson(movies);
        sharedPreferences.edit().putString("pmobile_lista", json).apply();
    }

    public void saveMoviesToLocal() {
        Call<List<Movie>> call = apiService.getMovies();
        call.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                List<Movie> movies = response.body();

                updateMoviesFromLocal(movies);
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                Toast.makeText(activityContext, "failure retrofit: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
