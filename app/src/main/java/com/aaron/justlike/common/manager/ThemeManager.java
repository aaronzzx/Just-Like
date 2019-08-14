package com.aaron.justlike.common.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.aaron.justlike.R;

public class ThemeManager {

    private static final String PREFERENCES_NAME = "justlike_theme";
    private static final String CURRENT_THEME = "current_theme";

    private Theme mCurrentTheme;

    private ThemeManager() {

    }

    public static ThemeManager getInstance() {
        return Holder.INSTANCE;
    }

    public Theme getCurrentTheme() {
        return mCurrentTheme;
    }

    public void setTheme(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String type = sharedPreferences.getString(CURRENT_THEME, null);
        if (type == null) {
            context.setTheme(R.style.WhiteTheme);
            return;
        }
        mCurrentTheme = Theme.valueOf(type);
        switch (mCurrentTheme) {
            case WHITE:
                context.setTheme(R.style.WhiteTheme);
                break;
            case BLACK:
                context.setTheme(R.style.BlackTheme);
                break;
            case GREY:
                context.setTheme(R.style.GreyTheme);
                break;
            case GREEN:
                context.setTheme(R.style.GreenTheme);
                break;
            case RED:
                context.setTheme(R.style.RedTheme);
                break;
            case PINK:
                context.setTheme(R.style.PinkTheme);
                break;
            case BLUE:
                context.setTheme(R.style.BlueTheme);
                break;
            case PURPLE:
                context.setTheme(R.style.PurpleTheme);
                break;
            case ORANGE:
                context.setTheme(R.style.OrangeTheme);
                break;
            case JUST_LIKE:
                context.setTheme(R.style.JustLikeTheme);
                break;
        }
    }

    public enum Theme {
        WHITE, BLACK, GREY, GREEN, RED, PINK, BLUE, PURPLE, ORANGE, JUST_LIKE
    }

    private static class Holder {
        private static final ThemeManager INSTANCE = new ThemeManager();
    }
}
