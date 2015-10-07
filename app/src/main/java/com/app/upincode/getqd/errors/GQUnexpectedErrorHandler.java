package com.app.upincode.getqd.errors;

import android.app.Activity;
import android.content.Context;

import com.app.upincode.getqd.config.GQConfig;

/**
 * Error handler that handles exceptions that should not have occurred, but did.
 * Should be used for any try-catch blocks that are expected to 'never happen'.
 */
public class GQUnexpectedErrorHandler extends GQBaseErrorHandler {
    private Exception error;

    public GQUnexpectedErrorHandler(Exception error) {
        super();
        this.error = error;
    }

    public String getMessage() {
        String message = "An unexpected error occurred! We will fix this as soon as possible";
        if (GQConfig.PROPERTIES.DEBUG) {  // If debug mode, show error
            message = this.error.getMessage();
        }
        return message;
    }

    public void handle(Context context) {
        //TODO report error to dev team
        this.error.printStackTrace();
        this.showAlert(context, "Internal Error", this.getMessage());
    }
}
