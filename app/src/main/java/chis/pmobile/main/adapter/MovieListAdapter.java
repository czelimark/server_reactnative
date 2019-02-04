package chis.pmobile.main.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import chis.pmobile.R;
import chis.pmobile.main.model.Movie;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {

    private Context context;
    private List<Movie> movieList;
    private ItemClickListener clickListener;

    public MovieListAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    public List<Movie> getMovieList() {
        return movieList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        return new ViewHolder(LayoutInflater
                        .from(context)
                        .inflate(R.layout.movie_listview_cell, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.title.setText(movie.getTitle());
        holder.genre.setText(movie.getGenre());
        holder.director.setText(movie.getDirector());
        holder.year.setText(movie.getYear());
    }

    @Override
    public int getItemCount() {
        return this.movieList.size();
    }


    public Movie getItem(int position) {
        return movieList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private TextView genre;
        private TextView director;
        private TextView year;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.title = view.findViewById(R.id.movie_title);
            this.genre = view.findViewById(R.id.genre);
            this.director = view.findViewById(R.id.director);
            this.year = view.findViewById(R.id.year);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null)
                clickListener.onClick(view, getAdapterPosition());
        }
    }
}
