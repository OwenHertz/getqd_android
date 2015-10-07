package com.app.upincode.getqd.usr;

import android.content.Context;

import com.app.upincode.getqd.config.GQConstants;
import com.app.upincode.getqd.networking.GQNetworkQueue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Handles everything to do with the user's properties file
 * <p>
 * Created by jpnauta on 15-09-18.
 */
public class GQUserPropertiesUtils {

    private static File getPropertiesFile(Context ctxt) {
        String fileName = GQConstants.PROPERTIES_FILE_NAME;
        return new File(ctxt.getExternalFilesDir(null), fileName);
    }

    private static void ensurePropertiesFileExists(Context ctxt) throws IOException {
        File propertiesFile = getPropertiesFile(ctxt);
        if (!propertiesFile.exists()) {
            propertiesFile.createNewFile();
        }
    }

    /**
     * Sets a value in the user's property file
     *
     * @param ctxt  app context
     * @param key   the key to set
     * @param value the value to set the key to
     */
    private static void setProfileValue(Context ctxt, String key, String value) {
        File propertiesFile = getPropertiesFile(ctxt);

        Properties prop = new Properties();
        OutputStream output = null;
        try {
            ensurePropertiesFileExists(ctxt);

            FileInputStream fis = new FileInputStream(propertiesFile);
            prop.load(fis);

            // set the properties value
            prop.setProperty(key, value);
            // save properties to project root folder

            output = new FileOutputStream(propertiesFile);
            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * Gets a value in the user's property file
     *
     * @param ctxt  app context
     * @param key   the key to set
     */
    public static String getProfileValue(Context ctxt, String key) {
        String value = null;
        Properties prop = new Properties();
        FileInputStream fis = null;

        try {
            ensurePropertiesFileExists(ctxt);
            File propertiesFile = getPropertiesFile(ctxt);

            fis = new FileInputStream(propertiesFile);
            prop.load(fis);
            value = prop.getProperty(key);
            return value;
        } catch (FileNotFoundException io) {
            io.printStackTrace();
            return value;
        } catch (IOException io) {
            io.printStackTrace();
            return value;

        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return value;
        }
    }

    public static void setLoginToken(Context ctxt, String value) {
        setProfileValue(ctxt, GQConstants.PROPERTIES_USER_TOKEN_KEY, value);
    }

    public static void deleteLoginToken(Context ctxt) {
        setProfileValue(ctxt, GQConstants.PROPERTIES_USER_TOKEN_KEY, "");
    }

    public static String getLoginToken(Context ctxt) {
        return getProfileValue(ctxt, GQConstants.PROPERTIES_USER_TOKEN_KEY);
    }

    public static void clearCache(Context context) {
        GQNetworkQueue.getInstance(context).clearCache();
    }
}
