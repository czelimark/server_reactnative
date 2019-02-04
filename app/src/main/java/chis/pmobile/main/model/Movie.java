package chis.pmobile.main.model;

public class Movie {
    private String id;
    private String title;
    private String genre;
    private String director;
    private String year;

    public Movie(String title, String genre, String director, String year) {
        this.id = null;
        this.title = title;
        this.genre = genre;
        this.director = director;
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
