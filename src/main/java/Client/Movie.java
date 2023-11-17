package Client;

public class Movie {
    int movieNum = 0;
    String title;
    String[] genre = new String[2];

    public Movie() {}

    public Movie(int movieNum,String title, String genre1, String genre2) {
        this.movieNum = movieNum;
        this.title = title;
        this.genre[0] = genre1;
        this.genre[1] = genre2;
    }

    @Override
    public String toString() {
        return this.movieNum + "." + this.title + "\t   " + this.genre[0] +" / "+ this.genre[1];
    }
}
