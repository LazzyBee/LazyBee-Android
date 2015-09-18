package com.born2go.lazzybee.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.gtools.ContainerHolderSingleton;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.TagManager;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SplashScreen extends Activity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        TagManager tagManager = TagManager.getInstance(this);

        // Modify the log level of the logger to print out not only
        // warning and error messages, but also verbose, debug, info messages.
        tagManager.setVerboseLoggingEnabled(true);

        PendingResult<ContainerHolder> pending =
                tagManager.loadContainerPreferNonDefault(getString(R.string.google_tag_ID),
                        R.raw.gtm_default_container);

        // The onResult method will be called as soon as one of the following happens:
        //     1. a saved container is loaded
        //     2. if there is no saved container, a network container is loaded
        //     3. the request times out. The example below uses a constant to manage the timeout period.
        pending.setResultCallback(new ResultCallback<ContainerHolder>() {
            @Override
            public void onResult(ContainerHolder containerHolder) {

                //
                ContainerHolderSingleton.setContainerHolder(containerHolder);
                Container container = containerHolder.getContainer();
                if (!containerHolder.getStatus().isSuccess()) {
                    Log.e("LazzyBee", "failure loading container for GTag");
                    displayErrorToUser(R.string.gtag_container_load_error);
                    //return;
                } else {
                    ContainerHolderSingleton.setContainerHolder(containerHolder);
                    ContainerLoadedCallback.registerCallbacksForContainer(container);
                    containerHolder.setContainerAvailableListener(new ContainerLoadedCallback());
                }
                startMainActivity();
            }
        }, SPLASH_TIME_OUT, TimeUnit.MILLISECONDS);

//        new Handler().postDelayed(new Runnable() {
//
//            /*
//             * Showing splash screen with a timer. This will be useful when you
//             * want to show case your app logo / company
//             */
//
//            @Override
//            public void run() {
//                // This method will be executed once the timer is over
//                startMainActivity();
//
//                // close this activity
//                finish();
//            }
//        }, SPLASH_TIME_OUT);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //startMainActivity();
    }

    private void startMainActivity() {
        // Start your app main activity
        Intent i = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(i);
        this.finish();
    }

    /**
     * Looks up the externalized string resource and displays it in a pop-up dialog box.
     *
     * @param stringKey
     */
    private void displayErrorToUser(int stringKey) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage(getResources().getString(stringKey));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
                "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alertDialog.show();
    }

    private static class ContainerLoadedCallback implements ContainerHolder.ContainerAvailableListener {
        @Override
        public void onContainerAvailable(ContainerHolder containerHolder, String containerVersion) {
            // We load each container when it becomes available.
            Container container = containerHolder.getContainer();
            registerCallbacksForContainer(container);
        }

        public static void registerCallbacksForContainer(Container container) {
            // Register two custom function call macros to the container.
            container.registerFunctionCallMacroCallback("increment", new CustomMacroCallback());
            container.registerFunctionCallMacroCallback("mod", new CustomMacroCallback());
            // Register a custom function call tag to the container.
            container.registerFunctionCallTagCallback("custom_tag", new CustomTagCallback());
        }
    }

    private static class CustomMacroCallback implements Container.FunctionCallMacroCallback {
        private int numCalls;

        @Override
        public Object getValue(String name, Map<String, Object> parameters) {
            if ("increment".equals(name)) {
                return ++numCalls;
            } else if ("mod".equals(name)) {
                return (Long) parameters.get("key1") % Integer.valueOf((String) parameters.get("key2"));
            } else {
                throw new IllegalArgumentException("Custom macro name: " + name + " is not supported.");
            }
        }
    }

    private static class CustomTagCallback implements Container.FunctionCallTagCallback {
        @Override
        public void execute(String tagName, Map<String, Object> parameters) {
            // The code for firing this custom tag.
            Log.i("LazzyBee", "Custom function call tag :" + tagName + " is fired.");
        }
    }
}
