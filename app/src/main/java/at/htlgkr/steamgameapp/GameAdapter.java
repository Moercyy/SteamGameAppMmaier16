package at.htlgkr.steamgameapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

import at.htlgkr.steam.Game;

public class GameAdapter extends BaseAdapter {
    private Context ctx;
    private List<Game> gameList;
    private int layoutId;
    private LayoutInflater inflater;

    public GameAdapter(Context context, int listViewItemLayoutId, List<Game> games) {
        this.ctx = context;
        this.layoutId = listViewItemLayoutId;
        this.gameList = games;
        this.inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return gameList.size();
    }

    @Override
    public Object getItem(int position) {
        return gameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View givenView, ViewGroup parent) {
        Game game = gameList.get(position);
        View listItem = (givenView == null) ? inflater.inflate(this.layoutId, null) : givenView;
        ((TextView) listItem.findViewById(R.id.gameName)).setText(game.getName());
        ((TextView) listItem.findViewById(R.id.gameReleaseDate)).setText(Game.SIMPLE_DATE_FORMAT.format(game.getReleaseDate()));
        ((TextView) listItem.findViewById(R.id.gamePrice)).setText(String.valueOf(game.getPrice()));
        return listItem;
    }
}

