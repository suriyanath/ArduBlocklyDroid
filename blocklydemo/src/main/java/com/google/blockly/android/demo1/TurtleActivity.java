/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.blockly.android.demo1;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.blockly.android.AbstractBlocklyActivity;
import com.google.blockly.android.BlocklySectionsActivity;
import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.google.blockly.android.control.BlocklyController;
import com.google.blockly.model.Block;
import com.google.blockly.model.DefaultBlocks;
import com.google.blockly.util.JavascriptUtil;
import com.google.blockly.utils.BlockLoadingException;
import com.physicaloid.lib.Boards;
import com.physicaloid.lib.Physicaloid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;


/**
 * Demo app with the Blockly Games turtle game in a webview.
 */
public class TurtleActivity extends BlocklySectionsActivity {


    private static final String TAG = "TurtleActivity";

    private static final String SAVE_FILENAME = "turtle_workspace.xml";
    private static final String AUTOSAVE_FILENAME = "turtle_workspace_temp.xml";
    private TextView mGeneratedTextView;
    private FrameLayout mGeneratedFrameLayout;
    private String mNoCodeText;

    static final List<String> TURTLE_BLOCK_DEFINITIONS = Arrays.asList(
            DefaultBlocks.COLOR_BLOCKS_PATH,
            DefaultBlocks.LOGIC_BLOCKS_PATH,
            DefaultBlocks.LOOP_BLOCKS_PATH,
            DefaultBlocks.MATH_BLOCKS_PATH,
            DefaultBlocks.TEXT_BLOCKS_PATH,
            DefaultBlocks.VARIABLE_BLOCKS_PATH,
            "turtle/turtle_blocks.json"
    );
    static final List<String> TURTLE_BLOCK_GENERATORS = Arrays.asList(
            "turtle/generators.js"
    );
    private static final int MAX_LEVELS = 2;
    private static final String[] LEVEL_TOOLBOX = new String[MAX_LEVELS];

    static {
        LEVEL_TOOLBOX[0] = "arduino_basic.xml";
        LEVEL_TOOLBOX[1] = "arduino_advanced.xml";
    }

    private final Handler mHandler = new Handler();
    //private WebView mTurtleWebview;
    private final CodeGenerationRequest.CodeGeneratorCallback mCodeGeneratorCallback =
            new CodeGenerationRequest.CodeGeneratorCallback() {
                @Override
                public void onFinishCodeGeneration(final String generatedCode) {

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mGeneratedTextView.setText(generatedCode);
                            updateTextMinWidth();
                        }
                    });

                    if(generatedCode.contains("alert")) {
                       String[] alert = generatedCode.split("!!");
                       Toast.makeText(getApplicationContext(), alert[2],Toast.LENGTH_LONG).show();
                    }
                    else {

                        // Sample callback.
                        //Log.i(TAG, "generatedCode:\n" + generatedCode);
                        // System.out.println( "generatedCode:\n" + generatedCode);
                        //Toast.makeText(getApplicationContext(), generatedCode,Toast.LENGTH_LONG).show();
                    /*ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("label", generatedCode);
                    clipboard.setPrimaryClip(clip);*/
                        // Intent launchIntent = getPackageManager().getLaunchIntentForPackage("name.antonsmirnov.android.arduinodroid2");
                        //   if (launchIntent != null) {
                        // mPhysicaloid.upload(Boards.ARDUINO_UNO, "/storage/emulated/0/code/Blink.hex");
                        try {
                          //  get_ports();
                            create_file(generatedCode);
                            //  execute_shell("ls");


                            execute_shell("touch Blink.cpp");
                            execute_shell("cp hardware/arduino/cores/arduino/main.cpp Blink.cpp");

                            execute_shell("sed -i wBlink1.cpp Blink.cpp files/Blink.ino");


                            //execute_shell("cat Blink1.cpp");
                            // execute_shell("/storage/emulated/0/code/hardware/tools/avr/bin/avr-g++");
                            // execute_shell("sh -c avr-g++");
                            //  execute_shell_2(new String[]{"sh -c", "/data/data/com.google.blockly.demo/hardware/tools/avr/bin/avr-g++"});
                            //execute_shell_2(new String[]{"sh", "/storage/emulated/0/code/hardware/tools/avr/bin/avr-g++"});
                            //  execute_shell(new String[] {"avr-g++","-x", "c++", "-MMD", "-c", "-mmcu=atmega328p", "-Wall", "-DF_CPU=16000000L", "-DARDUINO=160", "-DARDUINO_ARCH_AVR", "-D__PROG_TYPES_COMPAT__", "-I/data/data/com.google.blockly.demo/hardware/arduino/cores/arduino", "-I/data/data/com.google.blockly.demo/hardware/arduino/variants/standard", "-Wall", "-Os", "Blink1.cpp"});



                              execute_shell("avr-g++ -x c++ -MMD -c -mmcu=atmega328p -Wall -DF_CPU=16000000L -DARDUINO=160 -DARDUINO_ARCH_AVR -D__PROG_TYPES_COMPAT__ -I/data/data/com.google.blockly.demo/hardware/arduino/cores/arduino -I/data/data/com.google.blockly.demo/hardware/arduino/variants/standard -Wall -Os Blink1.cpp");
                        /*  execute_shell("avr-g++ -x c++ -MMD -c -mmcu=atmega328p -Wall -DF_CPU=16000000L -DARDUINO=160 -DARDUINO_ARCH_AVR -D__PROG_TYPES_COMPAT__ -I/data/data/com.google.blockly.demo/hardware/arduino/cores/arduino -I/data/data/com.google.blockly.demo/hardware/arduino/variants/standard -Wall -Os /data/data/com.google.blockly.demo/hardware/arduino/cores/arduino/wiring_digital.c");
                            execute_shell("avr-g++ -x c++ -MMD -c -mmcu=atmega328p -Wall -DF_CPU=16000000L -DARDUINO=160 -DARDUINO_ARCH_AVR -D__PROG_TYPES_COMPAT__ -I/data/data/com.google.blockly.demo/hardware/arduino/cores/arduino -I/data/data/com.google.blockly.demo/hardware/arduino/variants/standard -Wall -Os /data/data/com.google.blockly.demo/hardware/arduino/cores/arduino/wiring.c");
                            execute_shell("avr-g++ -x c++ -MMD -c -mmcu=atmega328p -Wall -DF_CPU=16000000L -DARDUINO=160 -DARDUINO_ARCH_AVR -D__PROG_TYPES_COMPAT__ -I/data/data/com.google.blockly.demo/hardware/arduino/cores/arduino -I/data/data/com.google.blockly.demo/hardware/arduino/variants/standard -Wall -Os /data/data/com.google.blockly.demo/hardware/arduino/cores/arduino/wiring_analog.c");

                            execute_shell("avr-g++ -x c++ -MMD -c -mmcu=atmega328p -Wall -DF_CPU=16000000L -DARDUINO=160 -DARDUINO_ARCH_AVR -D__PROG_TYPES_COMPAT__ -I/data/data/com.google.blockly.demo/hardware/arduino/cores/arduino -I/data/data/com.google.blockly.demo/hardware/arduino/variants/standard -Wall -Os /data/data/com.google.blockly.demo/hardware/arduino/cores/arduino/wiring_pulse.c");
                            execute_shell("avr-g++ -x c++ -MMD -c -mmcu=atmega328p -Wall -DF_CPU=16000000L -DARDUINO=160 -DARDUINO_ARCH_AVR -D__PROG_TYPES_COMPAT__ -I/data/data/com.google.blockly.demo/hardware/arduino/cores/arduino -I/data/data/com.google.blockly.demo/hardware/arduino/variants/standard -Wall -Os /data/data/com.google.blockly.demo/hardware/arduino/cores/arduino/wiring_shift.c");

                            //execute_shell("avr-g++ -x c++ -MMD -c -mmcu=atmega328p -Wall -DF_CPU=16000000L -DARDUINO=160 -DARDUINO_ARCH_AVR -D__PROG_TYPES_COMPAT__ -I/data/data/com.google.blockly.demo/hardware/arduino/cores/arduino -I/data/data/com.google.blockly.demo/hardware/arduino/variants/standard -Wall -Os /data/data/com.google.blockly.demo/hardware/arduino/cores/arduino/CDC.cpp");
                            execute_shell("avr-g++ -x c++ -MMD -c -mmcu=atmega328p -Wall -DF_CPU=16000000L -DARDUINO=160 -DARDUINO_ARCH_AVR -D__PROG_TYPES_COMPAT__ -I/data/data/com.google.blockly.demo/hardware/arduino/cores/arduino -I/data/data/com.google.blockly.demo/hardware/arduino/variants/standard -Wall -Os /data/data/com.google.blockly.demo/hardware/arduino/cores/arduino/HardwareSerial.cpp");

                            execute_shell("avr-g++ -x c++ -MMD -c -mmcu=atmega328p -Wall -DF_CPU=16000000L -DARDUINO=160 -DARDUINO_ARCH_AVR -D__PROG_TYPES_COMPAT__ -I/data/data/com.google.blockly.demo/hardware/arduino/cores/arduino -I/data/data/com.google.blockly.demo/hardware/arduino/variants/standard -Wall -Os /data/data/com.google.blockly.demo/hardware/arduino/cores/arduino/WString.cpp");
                            execute_shell("avr-g++ -x c++ -MMD -c -mmcu=atmega328p -Wall -DF_CPU=16000000L -DARDUINO=160 -DARDUINO_ARCH_AVR -D__PROG_TYPES_COMPAT__ -I/data/data/com.google.blockly.demo/hardware/arduino/cores/arduino -I/data/data/com.google.blockly.demo/hardware/arduino/variants/standard -Wall -Os /data/data/com.google.blockly.demo/hardware/arduino/cores/arduino/WMath.cpp");

                            execute_shell("avr-g++ -x c++ -MMD -c -mmcu=atmega328p -Wall -DF_CPU=16000000L -DARDUINO=160 -DARDUINO_ARCH_AVR -D__PROG_TYPES_COMPAT__ -I/data/data/com.google.blockly.demo/hardware/arduino/cores/arduino -I/data/data/com.google.blockly.demo/hardware/arduino/variants/standard -Wall -Os /data/data/com.google.blockly.demo/hardware/arduino/cores/arduino/WInterrupts.c");
                           // execute_shell("avr-g++ -x c++ -MMD -c -mmcu=atmega328p -Wall -DF_CPU=16000000L -DARDUINO=160 -DARDUINO_ARCH_AVR -D__PROG_TYPES_COMPAT__ -I/data/data/com.google.blockly.demo/hardware/arduino/cores/arduino -I/data/data/com.google.blockly.demo/hardware/arduino/variants/standard -Wall -Os /data/data/com.google.blockly.demo/hardware/arduino/cores/arduino/USBCore.cpp");

                            execute_shell("avr-g++ -x c++ -MMD -c -mmcu=atmega328p -Wall -DF_CPU=16000000L -DARDUINO=160 -DARDUINO_ARCH_AVR -D__PROG_TYPES_COMPAT__ -I/data/data/com.google.blockly.demo/hardware/arduino/cores/arduino -I/data/data/com.google.blockly.demo/hardware/arduino/variants/standard -Wall -Os /data/data/com.google.blockly.demo/hardware/arduino/cores/arduino/Tone.cpp");
                            execute_shell("avr-g++ -x c++ -MMD -c -mmcu=atmega328p -Wall -DF_CPU=16000000L -DARDUINO=160 -DARDUINO_ARCH_AVR -D__PROG_TYPES_COMPAT__ -I/data/data/com.google.blockly.demo/hardware/arduino/cores/arduino -I/data/data/com.google.blockly.demo/hardware/arduino/variants/standard -Wall -Os /data/data/com.google.blockly.demo/hardware/arduino/cores/arduino/Stream.cpp");

                            execute_shell("avr-g++ -x c++ -MMD -c -mmcu=atmega328p -Wall -DF_CPU=16000000L -DARDUINO=160 -DARDUINO_ARCH_AVR -D__PROG_TYPES_COMPAT__ -I/data/data/com.google.blockly.demo/hardware/arduino/cores/arduino -I/data/data/com.google.blockly.demo/hardware/arduino/variants/standard -Wall -Os /data/data/com.google.blockly.demo/hardware/arduino/cores/arduino/Print.cpp");
                            execute_shell("avr-g++ -x c++ -MMD -c -mmcu=atmega328p -Wall -DF_CPU=16000000L -DARDUINO=160 -DARDUINO_ARCH_AVR -D__PROG_TYPES_COMPAT__ -I/data/data/com.google.blockly.demo/hardware/arduino/cores/arduino -I/data/data/com.google.blockly.demo/hardware/arduino/variants/standard -Wall -Os /data/data/com.google.blockly.demo/hardware/arduino/cores/arduino/IPAddress.cpp");
*/
                          //  execute_shell("avr-g++ -x c++ -MMD -c -mmcu=atmega328p -Wall -DF_CPU=16000000L -DARDUINO=160 -DARDUINO_ARCH_AVR -D__PROG_TYPES_COMPAT__ -I/data/data/com.google.blockly.demo/hardware/arduino/cores/arduino -I/data/data/com.google.blockly.demo/hardware/arduino/variants/standard -Wall -Os /data/data/com.google.blockly.demo/hardware/arduino/cores/arduino/HID.cpp");
                            execute_shell("avr-g++ -x c++ -MMD -c -mmcu=atmega328p -Wall -DF_CPU=16000000L -DARDUINO=160 -DARDUINO_ARCH_AVR -D__PROG_TYPES_COMPAT__ -I/data/data/com.google.blockly.demo/hardware/arduino/cores/arduino -I/data/data/com.google.blockly.demo/hardware/arduino/variants/standard -Wall -Os /data/data/com.google.blockly.demo/hardware/arduino/cores/arduino/LiquidCrystal.cpp");

                         //   execute_shell("avr-ar rcs libcore.a wiring.o wiring_digital.o wiring_analog.o wiring_shift.o wiring_pulse.o WMath.o WString.o WInterrupts.o Tone.o Stream.o Print.o IPAddress.o HardwareSerial.o");
                            execute_shell("avr-ar rcs core.a CDC.cpp.o LiquidCrystal.o HardwareSerial.cpp.o HID.cpp.o IPAddress.cpp.o malloc.c.o new.cpp.o main.cpp.o new.cpp.o Print.cpp.o realloc.c.o Stream.cpp.o Tone.cpp.o Tone.cpp.o USBCore.cpp.o WInterrupts.c.o wiring.c.o wiring_analog.c.o wiring_digital.c.o wiring_pulse.c.o wiring_shift.c.o WMath.cpp.o WString.cpp.o");
                           execute_shell("avr-gcc -mmcu=atmega328p -Wl,--gc-sections -Os -o Blink1.elf Blink1.o core.a -lc -lm");
                            execute_shell("avr-objcopy -O ihex -R .eeprom Blink1.elf Blink1.hex");
                            Toast.makeText(getApplicationContext(), "Compilation Success, trying to upload code!!",Toast.LENGTH_LONG).show();


                            // execute_shell("chmod -R 700 hardware");
                            //execute_shell("echo hi");
                            // execute_shell("rm -rf Blink.cpp");
                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(), "Error Compiling", Toast.LENGTH_LONG).show();
                        }


                         upload_code("/data/data/com.google.blockly.demo/Blink1.hex");


                        //  startActivity(launchIntent);//null pointer check in case package name was not found
                        // }
                    /*mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            String encoded = "Turtle.execute("
                                    + JavascriptUtil.makeJsString(generatedCode) + ")";
                            mTurtleWebview.loadUrl("javascript:" + encoded);
                        }
                    });*/
                    }

                }
            };

    public void upload_code(String file){
        Physicaloid mPhysicaloid = new Physicaloid(this);
            mPhysicaloid.upload(Boards.ARDUINO_UNO,file);

           //Toast.makeText(getApplicationContext(), "Check if Program uploaded ", Toast.LENGTH_LONG).show();

    }

  /*  public void get_ports() {
        PeripheralManager manager = PeripheralManager.getInstance();
        List<String> deviceList = manager.getUartDeviceList();
        if (deviceList.isEmpty()) {
            Log.i(TAG, "No UART port available on this device.");
        } else {
            Log.i(TAG, "List of available devices: " + deviceList);
            System.out.println(deviceList);
        }
    }*/

    public void create_file(String fileContents){
        String filename = "Blink.ino";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void execute_shell(String cmd) throws IOException{
    String s = null;
        String[] path = {"PATH=/sbin:/vendor/bin:/system/sbin:/system/bin:/system/xbin:/data/data/"+this.getPackageName()+"/local/bin"};
        Process p = Runtime.getRuntime().exec(cmd,path,new File("/data/data/"+this.getPackageName()));//+"/hardware/tools/avr/bin/"));
    //Toast.makeText(getApplicationContext(), getApplicationInfo().dataDir,Toast.LENGTH_LONG).show();
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

    }
      String  ori_str = str.toString();
    if(!ori_str.equals(null) && !ori_str.equals("") && !ori_str.contains("warning")){
        System.out.println(str.toString());
        Toast.makeText(getApplicationContext(), ori_str,Toast.LENGTH_LONG).show();
    }


}

    @Override
    public void onLoadWorkspace() {

        mBlocklyActivityHelper.loadWorkspaceFromAppDirSafely(SAVE_FILENAME);
    }

    @Override
    public void onCodeWorkspace() {
        if(mGeneratedTextView.getVisibility()!= View.VISIBLE){
            mGeneratedTextView.setVisibility(View.VISIBLE);
            updateTextMinWidth();
        }
        else {
            mGeneratedTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveWorkspace() {
        mBlocklyActivityHelper.saveWorkspaceToAppDirSafely(SAVE_FILENAME);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return onDemoItemSelected(item, this) || super.onOptionsItemSelected(item);
    }

    static boolean onDemoItemSelected(MenuItem item, AbstractBlocklyActivity activity) {
        BlocklyController controller = activity.getController();
        int id = item.getItemId();
        boolean loadWorkspace = false;
        String filename = "";
        if (id == R.id.action_demo_android) {
            loadWorkspace = true;
            filename = "android.xml";
        } else if (id == R.id.action_demo_lacey_curves) {
            loadWorkspace = true;
            filename = "lacey_curves.xml";
        } else if (id == R.id.action_demo_paint_strokes) {
            loadWorkspace = true;
            filename = "paint_strokes.xml";
        }

        if (loadWorkspace) {
            String assetFilename = "turtle/demo_workspaces/" + filename;
            try {
                controller.loadWorkspaceContents(activity.getAssets().open(assetFilename));
            } catch (IOException | BlockLoadingException e) {
                throw new IllegalStateException(
                        "Couldn't load demo workspace from assets: " + assetFilename, e);
            }
            addDefaultVariables(controller);
            return true;
        }

        return false;
    }

    /**
     * Estimate the pixel size of the longest line of text, and set that to the TextView's minimum
     * width.
     */
    private void updateTextMinWidth() {
        String text = mGeneratedTextView.getText().toString();
        int maxline = 0;
        int start = 0;
        int index = text.indexOf('\n', start);
        while (index > 0) {
            maxline = Math.max(maxline, index - start);
            start = index + 1;
            index = text.indexOf('\n', start);
        }
        int remainder = text.length() - start;
        if (remainder > 0) {
            maxline = Math.max(maxline, remainder);
        }

        float density = getResources().getDisplayMetrics().density;
        mGeneratedTextView.setMinWidth((int) (maxline * 15 * density));
    }

    @NonNull
    @Override
    protected List<String> getBlockDefinitionsJsonPaths() {
        // Use the same blocks for all the levels. This lets the user's block code carry over from
        // level to level. The set of blocks shown in the toolbox for each level is defined by the
        // toolbox path below.
        return TURTLE_BLOCK_DEFINITIONS;
    }

    @Override
    protected int getActionBarMenuResId() {
        return R.menu.turtle_actionbar;
    }

    @NonNull
    @Override
    protected List<String> getGeneratorsJsPaths() {
        return TURTLE_BLOCK_GENERATORS;
    }

    @NonNull
    @Override
    protected String getToolboxContentsXmlPath() {
        // Expose a different set of blocks to the user at each level.
        return "turtle/" + LEVEL_TOOLBOX[getCurrentSectionIndex()];
    }

    @Override
    protected void onInitBlankWorkspace() {
        addDefaultVariables(getController());
    }

    @NonNull
    @Override
    protected ListAdapter onCreateSectionsListAdapter() {
        // Create the game levels with the labels "Level 1", "Level 2", etc., displaying
        // them as simple text items in the sections drawer.
        String[] levelNames = new String[MAX_LEVELS];

            levelNames[0] = "ArduBasic";
            levelNames[1] = "ArduAdvanced";

        return new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                levelNames);
    }

    @Override
    protected boolean onSectionChanged(int oldSection, int newSection) {
        reloadToolbox();
        return true;
    }

    @Override
    protected View onCreateContentView(int parentId) {
        View root = getLayoutInflater().inflate(R.layout.split_content, null);
        mGeneratedFrameLayout = root.findViewById(R.id.generated_workspace);
        mGeneratedTextView = (TextView) root.findViewById(R.id.generated_code);
        updateTextMinWidth();

        mNoCodeText = mGeneratedTextView.getText().toString(); // Capture initial value.
      /*  mTurtleWebview = (WebView) root.findViewById(R.id.turtle_runtime);
        mTurtleWebview.getSettings().setJavaScriptEnabled(true);
        mTurtleWebview.setWebChromeClient(new WebChromeClient());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        mTurtleWebview.loadUrl("file:///android_asset/turtle/turtle.html");
*/
        return root;
    }

    @Override
    public void onClearWorkspace() {
        super.onClearWorkspace();
        mGeneratedTextView.setText(mNoCodeText);
        updateTextMinWidth();
    }

    @NonNull
    @Override
    protected CodeGenerationRequest.CodeGeneratorCallback getCodeGenerationCallback() {
        return mCodeGeneratorCallback;
    }

    static void addDefaultVariables(BlocklyController controller) {
        // TODO: (#22) Remove this override when variables are supported properly
        controller.addVariable("item");
        controller.addVariable("count");
        controller.addVariable("marshmallow");
        controller.addVariable("lollipop");
        controller.addVariable("kitkat");
        controller.addVariable("android");
    }

    /**
     * Optional override of the save path, since this demo Activity has multiple Blockly
     * configurations.
     * @return Workspace save path used by this Activity.
     */
    @Override
    @NonNull
    protected String getWorkspaceSavePath() {
        return SAVE_FILENAME;
    }

    /**
     * Optional override of the auto-save path, since this demo Activity has multiple Blockly
     * configurations.
     * @return Workspace auto-save path used by this Activity.
     */
    @Override
    @NonNull
    protected String getWorkspaceAutosavePath() {
        return AUTOSAVE_FILENAME;
    }
}
