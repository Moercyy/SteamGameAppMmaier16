package at.htlgkr.steamgameapp;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.htlgkr.steam.Game;
import at.htlgkr.steam.ReportType;
import at.htlgkr.steam.SteamBackend;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import at.htlgkr.steam.Game;
import at.htlgkr.steam.ReportType;
import at.htlgkr.steam.SteamBackend;

public class MainActivity extends AppCompatActivity {
    public final static String TAG = MainActivity.class.getSimpleName();
    private static final String GAMES_CSV = "games.csv";
    private SteamBackend steamBackend = new SteamBackend();
    private Button addGameBtn;
    private Button saveBtn;
    private Button searchBtn;
    private Spinner spinner;
    private ListView listView;
    private GameAdapter gameAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addGameBtn = findViewById(R.id.addGame);
        saveBtn = findViewById(R.id.save);
        searchBtn = findViewById(R.id.search);
        spinner = findViewById(R.id.chooseReport);
        listView = findViewById(R.id.gamesList);
        loadGamesIntoListView();
        setUpReportSelection();
        setUpSearchButton();
        setUpAddGameButton();
        setUpSaveButton();
    }

    private void setUpGameAdapter() {
        setUpGameAdapter(steamBackend.getGames());
    }

    private void setUpGameAdapter(List list) {
        listView.setAdapter(gameAdapter = new GameAdapter(this, R.layout.game_item_layout, list));
    }

    private void loadGamesIntoListView() {
        try {
            steamBackend.loadGames(getAssets().open(GAMES_CSV));
            setUpGameAdapter();
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    private void setUpReportSelection() {
        List<ReportTypeSpinnerItem> reportTypeSpinnerItems = new ArrayList<>();
        reportTypeSpinnerItems.add(new ReportTypeSpinnerItem(ReportType.NONE, SteamGameAppConstants.SELECT_ONE_SPINNER_TEXT));
        reportTypeSpinnerItems.add(new ReportTypeSpinnerItem(ReportType.SUM_GAME_PRICES, SteamGameAppConstants.SUM_GAME_PRICES_SPINNER_TEXT));
        reportTypeSpinnerItems.add(new ReportTypeSpinnerItem(ReportType.AVERAGE_GAME_PRICES, SteamGameAppConstants.AVERAGE_GAME_PRICES_SPINNER_TEXT));
        reportTypeSpinnerItems.add(new ReportTypeSpinnerItem(ReportType.UNIQUE_GAMES, SteamGameAppConstants.UNIQUE_GAMES_SPINNER_TEXT));
        reportTypeSpinnerItems.add(new ReportTypeSpinnerItem(ReportType.MOST_EXPENSIVE_GAMES, SteamGameAppConstants.MOST_EXPENSIVE_GAMES_SPINNER_TEXT));

        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, reportTypeSpinnerItems));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ReportTypeSpinnerItem selectedItem = (ReportTypeSpinnerItem) parent.getSelectedItem();
                if (selectedItem.getType() != ReportType.NONE) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(selectedItem.getDisplayText())
                            .setMessage(getPrices(selectedItem)).show();
                }

            }

            private String getPrices(ReportTypeSpinnerItem selectedItem) {
                String message;
                switch (selectedItem.getType()) {
                    case SUM_GAME_PRICES:
                        message = SteamGameAppConstants.ALL_PRICES_SUM + steamBackend.sumGamePrices();
                        break;
                    case AVERAGE_GAME_PRICES:
                        message = SteamGameAppConstants.ALL_PRICES_AVERAGE + steamBackend.averageGamePrice();
                        break;
                    case UNIQUE_GAMES:
                        message = SteamGameAppConstants.UNIQUE_GAMES_COUNT + steamBackend.getUniqueGames().size();
                        break;
                    case MOST_EXPENSIVE_GAMES:
                        int gamesCount = 3;
                        List<Game> gamesDependingOnPrice = steamBackend.selectTopNGamesDependingOnPrice(gamesCount);
                        StringBuilder messageBuilder = new StringBuilder(SteamGameAppConstants.MOST_EXPENSIVE_GAMES);
                        for (Game game : gamesDependingOnPrice) {
                            messageBuilder.append(game.toString()).append("\n");
                        }
                        message = messageBuilder.toString();
                        break;
                    default:
                        message = "";
                }
                return message;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setUpSearchButton() {
        searchBtn.setOnClickListener(v -> {
            EditText editText = new EditText(this);
            editText.setId(R.id.dialog_search_field);
            new AlertDialog.Builder(this)
                    .setTitle(SteamGameAppConstants.ENTER_SEARCH_TERM)
                    .setView(editText)
                    .setPositiveButton("Search", (dialog, which) -> {
                        String filterString = editText.getText().toString().toLowerCase();
                        if (!filterString.isEmpty()) {
                            List<Game> filteredGameList = new ArrayList<>(steamBackend.getGames().size());

                            for (Game game : steamBackend.getGames()) {
                                if (game.getName().toLowerCase().contains(filterString)) {
                                    filteredGameList.add(game);
                                }
                            }
                            setUpGameAdapter(filteredGameList);
                        } else {
                            setUpGameAdapter();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void setUpAddGameButton() {
        addGameBtn.setOnClickListener(v -> {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            EditText nameField = new EditText(this);
            nameField.setId(R.id.dialog_name_field);
            linearLayout.addView(nameField);

            EditText dateField = new EditText(this);
            dateField.setId(R.id.dialog_date_field);
            linearLayout.addView(dateField);

            EditText priceField = new EditText(this);
            priceField.setId(R.id.dialog_price_field);
            linearLayout.addView(priceField);

            new AlertDialog.Builder(this)
                    .setTitle(SteamGameAppConstants.NEW_GAME_DIALOG_TITLE)
                    .setView(linearLayout)
                    .setPositiveButton("Add", (dialog, which) -> {
                        Game game = null;
                        try {
                            game = new Game(nameField.getText().toString(), Game.SIMPLE_DATE_FORMAT.parse(dateField.getText().toString()), Double.parseDouble(priceField.getText().toString()));
                        } catch (ParseException e) {
                            Log.e(TAG, Log.getStackTraceString(e));
                        }
                        steamBackend.addGame(game);
                        setUpGameAdapter();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void setUpSaveButton() {
        saveBtn.setOnClickListener(v -> {
            try {
                steamBackend.store(openFileOutput(SteamGameAppConstants.SAVE_GAMES_FILENAME, MODE_PRIVATE));
                Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        });
    }
}
