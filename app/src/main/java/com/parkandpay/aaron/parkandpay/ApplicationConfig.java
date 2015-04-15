package com.parkandpay.aaron.parkandpay;


import android.app.Application;
import com.parse.Parse;
import java.util.Date;

/**
 * Created by joshmerle on 3/10/15.
 */
public class ApplicationConfig extends Application {

    private static String spotTaken = "";
    private static String lotName = "";
    private static Date paidAt = null;
    private static double totalCost = 0.0;

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

    public static boolean hasPaidAt() {
        return paidAt != null;
    }

    public static void setPaidAt(Date d) {
        paidAt = d;
    }

    public static void resetPaidAt() {
        paidAt = null;
    }

    public static Date getPaidAt() {
        return paidAt;
    }

    public static void updateSpotData(Date expirationTime) {
        Date now = new Date();
        if(expirationTime.after(now)) {
            paidAt = null;
            resetSpot();
        }
    }

    public static void addCost(double cost) { totalCost += cost; }
    public static void reduceCost(double reduction) { totalCost -= reduction; }
    public static double getCost() { return totalCost; }
    public static void resetCost() { totalCost = 0; }

}
