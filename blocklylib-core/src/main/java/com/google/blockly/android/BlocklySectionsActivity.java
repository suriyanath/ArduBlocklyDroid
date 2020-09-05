/*
 *  Copyright 2015 Google Inc. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.google.blockly.android;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.blockly.model.Workspace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Activity holding a full-screen Blockly workspace with multiple sections in the navigation menu.
 */
public abstract class BlocklySectionsActivity extends AbstractBlocklyActivity
        implements AdapterView.OnItemClickListener {
    private static final String TAG = "BlocklySectionsActivity";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    protected int mCurrentSection = 0;
    protected ListView mSectionsListView;
    protected ListAdapter mListAdapter;
    String TARGET_BASE_PATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);

            boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
            if (isFirstRun) {
                copyFileOrDir(""); // Code to run once
                //try {
//                    execute_shell("chmod -R 700 busybox");
//                    execute_shell("chmod -R 700 local.tar.gz");
//                    execute_shell("./busybox gzip -d local.tar.gz");
//                    execute_shell("./busybox tar -xvf local.tar");
//                    execute_shell("chmod -R 700 local");
//                    execute_shell("chmod -R 700 hardware");
               // } catch (IOException e) {
                 //   e.printStackTrace();
                //}
                SharedPreferences.Editor editor = wmbPreference.edit();
                editor.putBoolean("FIRSTRUN", false);
                editor.commit();
            }

            super.onCreate(savedInstanceState);

    }


    private void copyFileOrDir(String path) {
        TARGET_BASE_PATH = "/data/data/"+this.getPackageName()+"/";
       // TARGET_BASE_PATH = "/storage/emulated/0/code/blocklyfiles/";
        AssetManager assetManager = this.getAssets();
        String assets[] = null;
        try {
            Log.i("tag", "copyFileOrDir() "+path);
            assets = assetManager.list(path);
            String fullPath =  TARGET_BASE_PATH + path;
            Log.i("tag", "path="+fullPath);
            File dir = new File(fullPath);
            if (assets.length == 0) {
                if(!dir.exists()){
                    copyFile(path);
                }
            } else {

                if (!dir.exists() && !path.startsWith("images") && !path.startsWith("sounds") && !path.startsWith("webkit"))
                    if (!dir.mkdirs())
                        Log.i("tag", "could not create dir "+fullPath);
                for (int i = 0; i < assets.length; ++i) {
                    String p;
                    if (path.equals(""))
                        p = "";
                    else
                        p = path + "/";

                    if (!path.startsWith("images") && !path.startsWith("sounds") && !path.startsWith("webkit"))
                        copyFileOrDir( p + assets[i]);
                }
            }
        } catch (IOException ex) {
            Log.e("tag", "I/O Exception", ex);
        }

    }

    public void execute_shell(String cmd) throws IOException{
        String s = null;
        String[] path = {"PATH=/sbin:/vendor/bin:/system/sbin:/system/bin:/system/xbin"};
        Process p = Runtime.getRuntime().exec(cmd,path,new File("/data/data/"+this.getPackageName()));//+"/hardware/tools/avr/bin/"));

        StringBuilder str = new StringBuilder();
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(p.getErrorStream()));

        // read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        while ((s = stdInput.readLine()) != null) {
            str.append(s+"\n");
            //System.out.println(s);
        }

        // read any errors from the attempted command
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            str.append(s);
            //System.out.println(s);
        }
        //System.out.println(str.toString());
    }


    private void copyFile(String filename) {
        AssetManager assetManager = this.getAssets();

        InputStream in = null;
        OutputStream out = null;
        String newFileName = null;
        try {
            Log.i("tag", "copyFile() "+filename);
            in = assetManager.open(filename);
            if (filename.endsWith(".jpg")) // extension was added to avoid compression on APK file
                newFileName = TARGET_BASE_PATH + filename.substring(0, filename.length()-4);
            else
                newFileName = TARGET_BASE_PATH + filename;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e("tag", "Exception in copyFile() of "+newFileName);
            Log.e("tag", "Exception in copyFile() "+e.toString());
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    /**
     * Creates {@link #mSectionsListView} and configures it with
     * {@link #onCreateSectionsListAdapter()} to use as the activity's navigation menu.
     *
     * @return {@link #mSectionsListView} for the navigation menu.
     */
    @Override
    protected View onCreateAppNavigationDrawer() {
        mSectionsListView = (ListView) getLayoutInflater().inflate(R.layout.sections_list, null);
        mListAdapter = onCreateSectionsListAdapter();
        mSectionsListView.setAdapter(mListAdapter);
        mSectionsListView.setOnItemClickListener(this);

        return mSectionsListView;
    }

    /**
     * Handles clicks to {@link #mSectionsListView} by calling {@link #onSectionItemClicked} with
     * the selected position.
     *
     * @param parent The {@link ListView}.
     * @param view The selected item view.
     * @param position The position in the list.
     * @param id The id of the list item.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        onSectionItemClicked(position);
    }

    /**
     * Handles the selection of a section, including closing the navigation drawer.
     * @param sectionIndex
     */
    public void onSectionItemClicked(int sectionIndex) {
        mSectionsListView.setItemChecked(sectionIndex, true);
        setNavDrawerOpened(false);
        if (mCurrentSection == sectionIndex) {
            return;
        }

        changeSection(sectionIndex);
        mCurrentSection = sectionIndex;
    }

    /**
     * @return The title of the current workspace / section.
     */
    @NonNull
    protected CharSequence getWorkspaceTitle() {
        int section = getCurrentSectionIndex();
        if (section < getSectionCount()) {
            return (String) mListAdapter.getItem(section);
        } else {
            // Use the Activity name.
            return getTitle();
        }
    }

    /**
     * Populate the navigation menu with the list of available sections.
     *
     * @return An adapter of sections for the navigation menu.
     */
    @NonNull
    abstract protected ListAdapter onCreateSectionsListAdapter();

    /**
     * @return The section that is currently displayed.
     */
    public final int getCurrentSectionIndex() {
        return mCurrentSection;
    }

    /**
     * Called to load a new Section. If you don't want to re-use the previous section's
     * code {@link Workspace#loadWorkspaceContents(InputStream)} should be called here.
     *
     * @param oldSection The previous level.
     * @param newSection The level that was just configured.
     * @return True if the new section was successfully loaded. Otherwise false.
     */
    abstract protected boolean onSectionChanged(int oldSection, int newSection);

    /**
     * @return The number of sections in this activity.
     */
    protected int getSectionCount() {
        return mListAdapter.getCount();
    }

    private void changeSection(int level) {
        int oldLevel = mCurrentSection;
        mCurrentSection = level;
        if (onSectionChanged(oldLevel, level)) {
            resetBlockFactory();
            reloadToolbox();
        } else {
            mCurrentSection = oldLevel;
        }
    }
}
