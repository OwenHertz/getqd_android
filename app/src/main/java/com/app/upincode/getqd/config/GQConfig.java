package com.app.upincode.getqd.config;

import android.content.res.AssetManager;

import com.app.upincode.getqd.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Contains all important settings for entire project.
 */
public final class GQConfig {
    public static final String DEFAULT_PROPERTIES_PATH = "config/default.properties";
    public static final String DEBUG_PROPERTIES_PATH = "config/debug.properties";
    public static final String PRODUCTION_PROPERTIES_PATH = "config/production.properties";
    public static final String LOCAL_PROPERTIES_PATH = "config/local.properties";

    //Holds system current config properties
    // - Initialized when application is created
    public static GQConfig PROPERTIES;

    //////////////// BEGIN AVAILABLE CONSTANTS ////////////////

    // Indicates if system is in debug mode
    public final boolean DEBUG;

    // Domain url/protocol to use to send request to GetQd's server (e.g. https://getqd.com/)
    public final String BASE_URL;

    //////////////// END AVAILABLE CONSTANTS ////////////////

    public static void initialize(AssetManager assetManager) throws IOException {
        GQConfig.PROPERTIES = new GQConfig(assetManager);
    }

    protected GQConfig(AssetManager assetManager) throws IOException {
        //Load configuration from appropriate .properties files
        Properties properties = this.importPropertiesFile(assetManager, DEFAULT_PROPERTIES_PATH);
        if (BuildConfig.DEBUG) {
            // Load debug properties
            properties.putAll(this.importPropertiesFile(assetManager, DEBUG_PROPERTIES_PATH));

            //Load local settings, if added
            try {
                properties.putAll(this.importPropertiesFile(assetManager, LOCAL_PROPERTIES_PATH));
            } catch (IOException e) {
                // No local settings file found; ignore
            }
        }
        else {
            //Load production properties
            properties.putAll(this.importPropertiesFile(assetManager, PRODUCTION_PROPERTIES_PATH));
        }

        //Initialize settings
        this.DEBUG = BuildConfig.DEBUG;
        this.BASE_URL = (String) properties.get("BASE_URL");
    }

    /**
     * Imports the specified .properties file
     *
     * @param assetManager accessor class to assets
     * @param filePath     the path to file in assets/ folder
     * @return hash map containing properties found
     * @throws IOException
     */
    protected Properties importPropertiesFile(AssetManager assetManager, String filePath) throws IOException {

        InputStream inputStream = assetManager.open(filePath);
        Properties properties = new Properties();
        properties.load(inputStream);

        return properties;
    }
}
