package com.parkandpay.aaron.parkandpay;


import android.app.Application;
import com.parse.Parse;

/**
 * Created by joshmerle on 3/10/15.
 */
public class ApplicationConfig extends Application {

    private static String spotTaken = "";
    private static String lotName = "";

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "D3e1FmZef0xReoXZlWFVGgxIosiuUfusQ9jgHT7y", "FFUhtwW99qsT4ExUj2kepUhKVAibc3MsMJYJGf2x");

    }

    public static String getSpotTaken() {
        return spotTaken;
    }

    public static void setSpotTaken(String spot) {
        spotTaken = spot;
    }

    public static boolean hasSpot() {
        return !spotTaken.isEmpty();
    }

    public static void resetSpot() {
        spotTaken = "";
        lotName = "";
    }

    public static String getLotName() {
        return lotName;
    }

    public static void setLotName(String lot_name) {
        lotName = lot_name;
    }

}
