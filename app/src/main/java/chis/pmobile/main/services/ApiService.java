package chis.pmobile.main.services;

import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import chis.pmobile.main.model.Movie;
import chis.pmobile.main.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("/login")
    Call<ResponseBody> login(@Body User user);

    @GET("/movies")
    Call<List<Movie>> getMovies();

    @POST("/movie")
    Call<Movie> saveMovie(@Body Movie movie);

    @DELETE("/movie/{movie_id}")
    Call<ResponseBody> deleteMovie(@Path("movie_id") String movie_id);

    @POST("/update_movies")
    Call<ResponseBody> updateMovies(@Body List<Movie> localMovies);
}
