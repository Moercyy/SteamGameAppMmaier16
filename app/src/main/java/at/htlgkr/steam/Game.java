package at.htlgkr.steam;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Game {
    public static final String DATE_FORMAT = "dd.MM.yyyy";
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT);

    private String name;
    private Date releaseDate;
    private double price;

    public Game(String name, Date releaseDate, double price) {
        this.name = name;
        this.releaseDate = releaseDate;
        this.price = price;
    }

    public Game() {
        // dieser Konstruktor muss existieren - ok
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "[" + SIMPLE_DATE_FORMAT.format(getReleaseDate()) + "] " + getName() + " " + getPrice();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Game game = (Game) o;

        return name.equals(game.name);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name.hashCode();
        result = 31 * result + releaseDate.hashCode();
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public String toCSVString() {
        return getName() + ";" + SIMPLE_DATE_FORMAT.format(getReleaseDate()) + ";" + getPrice();
    }
}

