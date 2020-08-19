package com.orion3shoppy.bodamtaani.controllers;

import android.os.Environment;

import java.io.File;

/**
 *
 */

public class AppConstants {

    /**
     * Connection timeout duration
     */
    public static final int CONNECT_TIMEOUT = 60 * 1000;
    public static final File AD_IMAGE_STORAGE = new File(Environment.getExternalStorageDirectory().getPath() + "/.boda_mtaani/");


}
