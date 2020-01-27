package at.htlgkr.steam;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SteamBackend {
    private List<Game> gameList = new ArrayList<>();

    public SteamBackend() {
    }

    public void loadGames(InputStream inputStream) {
        gameList = new ArrayList<>();
        try (BufferedReader bin = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = bin.readLine(); //Header
            while ((line = bin.readLine()) != null) {
                String[] split = line.split(";");
                try {
                    gameList.add(new Game(split[0], Game.SIMPLE_DATE_FORMAT.parse(split[1]), Double.parseDouble(split[2])));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void store(OutputStream fileOutputStream) {
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(fileOutputStream))) {
            for (Game game : gameList) {
                out.println(game.toCSVString());
            }
        }

    }

    public List<Game> getGames() {
        return Collections.unmodifiableList(gameList);
    }

    public void setGames(List<Game> games) {
        gameList.clear();
        gameList.addAll(games);
    }

    public void addGame(Game newGame) {
        gameList.add(newGame);
    }

    public double sumGamePrices() {
        return gameList.stream().mapToDouble(Game::getPrice).sum();
    }

    public double averageGamePrice() {
        return gameList.stream().mapToDouble(Game::getPrice).average().getAsDouble();
    }

    public List<Game> getUniqueGames() {
        return gameList.stream().distinct().collect(Collectors.toList());
    }

    public List<Game> selectTopNGamesDependingOnPrice(int n) {
        return gameList.stream().sorted((o1, o2) -> Double.compare(o2.getPrice(), o1.getPrice())).limit(n).collect(Collectors.toList());
    }
}









//mm