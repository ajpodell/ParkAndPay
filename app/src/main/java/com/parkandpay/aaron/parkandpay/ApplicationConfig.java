package com.parkandpay.aaron.parkandpay;


import android.app.Application;
import com.parse.Parse;

/**
 * Created by joshmerle on 3/10/15.
 */
public class ApplicationConfig extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "D3e1FmZef0xReoXZlWFVGgxIosiuUfusQ9jgHT7y", "FFUhtwW99qsT4ExUj2kepUhKVAibc3MsMJYJGf2x");

    }
}
