package com.example.mazzers.voicerecorder;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mazzers.voicerecorder.bookmarks.ParseBookmarkFiles;
import com.example.mazzers.voicerecorder.fragments.BookmarkFragment;
import com.example.mazzers.voicerecorder.fragments.PlayerFragment;
import com.example.mazzers.voicerecorder.fragments.RecorderFragment;

import java.io.File;


public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;

    //private static final int RESULT_SETTINGS = 1;
//    private Chronometer chronometer;
//    Button btnRecord, btnStop, btnBook, btnCallPlayer, btnShowBookmarks;
//    RadioGroup rgOut;
//    RadioButton btnAMR, btn3Gpp;
//    CheckBox chkQuality;
//    //TextView timeView;
//    File fileAudio, fileBook;
//    String filePathAudio, filePathBook;
//    String filePath;
//    String fileAudioName;
    final String TAG_LOG = "myLogs";
//    private MediaRecorder mediaRecorder;
//    FileOutputStream outputStream;
//    //BufferedWriter bWriter;
//    private Long startTime;
//    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG_LOG, "Start");
        File recordsDirectory = new File(Environment.getExternalStorageDirectory() + "/voicerecorder/bookmarks/");
        if (!recordsDirectory.exists()){
            Log.d(TAG_LOG,"Main activity: directory not exist");
            recordsDirectory.mkdirs();
            if (!recordsDirectory.mkdirs()){
                Toast.makeText(this,"Не создал",Toast.LENGTH_SHORT).show();
                Log.d(TAG_LOG,"Пошёл нахуй");
            }
            Log.d(TAG_LOG,"Main activity: directories created");
        }
        Thread parseBookmarkFiles = new Thread(new ParseBookmarkFiles());
        parseBookmarkFiles.start();
        //if(ParseBookmarkFiles.traverseFinish) BookmarkFragment.fillStrings();
//        btnRecord = (Button) findViewById(R.id.btnRecord);
//        btnStop = (Button) findViewById(R.id.btnStop);
//        btnBook = (Button) findViewById(R.id.btnBook);
//        btnCallPlayer = (Button) findViewById(R.id.btnCallPlayer);
//        rgOut = (RadioGroup) findViewById(R.id.rgOut);
//        btn3Gpp = (RadioButton) findViewById(R.id.btn3GPP);
//        btnAMR = (RadioButton) findViewById(R.id.btnAMR);
//        chkQuality = (CheckBox) findViewById(R.id.chkQuality);
//        btnShowBookmarks = (Button) findViewById(R.id.btnShowBookmarks);
//
//        chronometer = (Chronometer) findViewById(R.id.chrono);
//        filePath = Environment.getExternalStorageDirectory() + "/";
//        try {
//            btnRecord.setOnClickListener(new btnStartRecordClick());
//            btnStop.setOnClickListener(new btnStopRecordClick());
//            btnBook.setOnClickListener(new btnBookClick());
//
//        }catch (Exception e){
//            Log.d(TAG_LOG,"error set up onclick listener");
//            Log.d(TAG_LOG,e.toString());
//        }
//
//        mediaRecorder = new MediaRecorder();
//        File recordsDirectory = new File(Environment.getExternalStorageDirectory() + "/voicerecorder/bookmarks/");
//        if (!recordsDirectory.exists()){
//            recordsDirectory.mkdirs();
//        }
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


        Log.d(TAG_LOG, "onCreate");

    }
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Log.d(TAG_LOG,"onNavigationDrawerItemSelected");
        Fragment fragment;

        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        switch (position){
            case 0:
                fragment = new RecorderFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment).commit();
                mTitle = getString(R.string.title_section1);
                break;
            case 1:
                fragment = new PlayerFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment).commit();
                mTitle = getString(R.string.title_section2);
                break;
            case 2:
                fragment = new BookmarkFragment();
                //fragment = new LineupFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment).commit();
                mTitle = getString(R.string.title_section3);
                break;


        }


    }



    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Toast.makeText(this,"test",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


//    class btnStartRecordClick implements View.OnClickListener {
//        public void onClick(View arg0) {
//            count = 1;
//            Log.d(TAG_LOG, "Start Clicked...");
//            releaseRecorder();
//            mediaRecorder = new MediaRecorder();
//            generateName();
//            startTime = System.currentTimeMillis();
//            Thread startThread = new Thread(new startRec(mediaRecorder, rgOut.getCheckedRadioButtonId(), chkQuality.isChecked(), fileAudioName, startTime));
//            Log.d(TAG_LOG, "start Thread Created");
//
//            startThread.start();
//            Log.d(TAG_LOG, "start Recording");
//            startChrono();
//
//        }
//    }
//
//    class btnStopRecordClick implements View.OnClickListener {
//
//
//        @Override
//        public void onClick(View v) {
//            Log.d(TAG_LOG,"onclick Stop Record");
//            count = 0;
//            Thread stopThread = new Thread(new stopRecording(mediaRecorder));
//            stopThread.start();
//            stopChrono();
//        }
//    }
//
//    class btnBookClick implements View.OnClickListener {
//
//
//        @Override
//        public void onClick(View v) {
//            Log.d(TAG_LOG,"OnClick bookmark");
//            filePathBook = Environment.getExternalStorageDirectory() + "/voicerecorder/bookmarks/" + fileAudioName +"_"+ count + ".xml";
//            Log.d(TAG_LOG,filePathBook);
//            count++;
//
//            fileBook = new File(filePathBook);
//            Thread xmlCreateThread = new Thread(new WriteToXML(fileBook, fileAudioName, startTime));
//            xmlCreateThread.start();
//
//        }
//    }

//    private void generateName() {
//
//        try {
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
//            Date now = new Date();
//            fileAudioName = formatter.format(now);
//        } catch (Exception e) {
//            Log.d(TAG_LOG, e.toString());
//        }
//        //filePathBook = Environment.getExternalStorageDirectory() + "/" + fileAudioName + ".xml";
//        //return fileAudioNameTemp;
//    }

//    public void startRecord(View v) {
//        try {
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
//            Date now = new Date();
//            fileAudioName = formatter.format(now);
//        } catch (Exception e) {
//            Log.d(TAG_LOG, e.toString());
//        }
//        String fileAudioFormat = "";
//
//        Log.d(TAG_LOG, "Before Release");
//        releaseRecorder();
//
//
//        Log.d(TAG_LOG, "Start Record");
//
//
//        startTime = System.currentTimeMillis();
//        Log.d(TAG_LOG, startTime.toString());
//        mediaRecorder = new MediaRecorder();
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        //mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        Log.d(TAG_LOG, "Set mic source");
//        switch (rgOut.getCheckedRadioButtonId()) {
//            case R.id.btn3GPP:
//                Log.d(TAG_LOG, "3gpp");
//                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//                filePathAudio = Environment.getExternalStorageDirectory() + "/" + fileAudioName + ".3gpp";
//                break;
//            case R.id.btnAMR:
//                Log.d(TAG_LOG, "Amr");
//                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
//                filePathAudio = Environment.getExternalStorageDirectory() + "/" + fileAudioName + ".amr";
//                fileAudioFormat = ".amr";
//                break;
//            default:
//                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//                filePathAudio = Environment.getExternalStorageDirectory() + "/" + fileAudioName + ".3gpp";
//                break;
//        }
//
//        if (chkQuality.isChecked()) {
//            Log.d(TAG_LOG, "Quality checked");
//            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
//        } else {
//            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//        }
//
//        //filePathAudio = filePath + fileAudioName + fileAudioFormat;
//        //Log.d(TAG_LOG, fileAudio.toString());
//        // Log.d(TAG_LOG, fileAudioFormat.toString());
//        fileAudio = new File(filePathAudio);
//        Log.d(TAG_LOG, "blalalala");
//        Log.d(TAG_LOG, filePathAudio);
//        mediaRecorder.setOutputFile(filePathAudio);
//
//        Log.d(TAG_LOG, "Try to prepare");
//        try {
//            Log.d(TAG_LOG, "In try");
//            mediaRecorder.prepare();
//            Log.d(TAG_LOG, "After prepare");
//        } catch (IOException e) {
//            Log.d(TAG_LOG, "Prerape fail");
//            Log.d(TAG_LOG, e.toString());
//        }
//        Log.d(TAG_LOG, "Prepare OK");
//        mediaRecorder.start();
//        //doTimerTask();
//        startChrono();
//        Log.d(TAG_LOG, "After start");
//
//        //createBookmarkFile(filePathAudio);
//        //createXML();
//        Log.d(TAG_LOG, "After createBookmarkFile");
//
//
//    }

//    private void releaseRecorder() {
//        Log.d(TAG_LOG, "Release method");
//        if (mediaRecorder != null) {
//            mediaRecorder.release();
//            mediaRecorder = null;
//        }
//    }

//    public void stopRecord(View v) {
//        Log.d(TAG_LOG, "Stop Record");
//        //stopTask();
//        stopChrono();
//        if (mediaRecorder != null) {
//            mediaRecorder.stop();
//        }
//    }

//    public String fillXML(int AddCase) throws IllegalArgumentException, IllegalStateException, IOException {
//        Log.d(TAG_LOG,"fillXML");
//
//
//        XmlSerializer xmlSt = Xml.newSerializer();
//        StringWriter writer = new StringWriter();
//        xmlSt.setOutput(writer);
//        switch (AddCase) {
//            case 0:
//                xmlSt.startDocument("UTF-8", true);
//
//                xmlSt.startTag("","bookmark");
//                xmlSt.startTag("", "path");
//                xmlSt.attribute("", "value", filePathAudio);
//                xmlSt.endTag("", "path");
//                xmlSt.endTag("","bookmark");
//
//
//                xmlSt.endDocument();
//                break;
//            case 1:
//                long duration = (int) ((System.currentTimeMillis() - startTime) / 1000);
//                xmlSt.startTag("", "time");
//                xmlSt.attribute("", "value",String.valueOf(duration));
//                xmlSt.endTag("", "time");
//                xmlSt.endTag("","bookmarks");
//
//        }
//
//        return writer.toString();
//
//    }


//    public void createXML() {
//        filePathBook = Environment.getExternalStorageDirectory() + "/" + fileAudioName + ".xml";
//        fileBook = new File(filePathBook);
//        try {
//            outputStream = new FileOutputStream(fileBook);
//            outputStream.write((fillXML(0)+"\n").getBytes());
//            Log.d(TAG_LOG, "In try createBookmarkFile: write");
//            outputStream.close();
//            Log.d(TAG_LOG, "Closed");
//
//        } catch (Exception e) {
//
//            Log.d(TAG_LOG, "create xml error");
//            Log.d(TAG_LOG, e.toString());
//
//        }
//
//
//    }

//    public void makeBookmark(View v) {
//        Log.d(TAG_LOG,"makeBookmark");
//        RandomAccessFile raFile = null;
//        //final boolean fileExists = inFile.exists();
//        String lastLine = null;
//
//        try {
//            Log.d(TAG_LOG,"in try makeBookmark");
//            raFile = new RandomAccessFile(fileBook, "rw");
//            raFile.seek(0);
//            if (raFile != null) {
//                final Scanner scanner = new Scanner(fileBook);
//                int lastLineOffset = 0;
//                int lastLineLength = 0;
//                while (scanner.hasNextLine()) {
//                    // +1 is for end line symbol
//                    lastLine = scanner.nextLine();
//                    lastLineLength = lastLine.length() + 2;
//                    lastLineOffset += lastLineLength;
//                }
//                lastLineOffset -= lastLineLength;
//                raFile.seek(lastLineOffset);
//                raFile.writeBytes(fillXML(1)+"\n");
//                raFile.close();
//
//            }
//
//        } catch (FileNotFoundException e) {
//            Log.d(TAG_LOG,e.toString());
//            Log.e("FileNotFoundException", "can't create FileOutputStream");
//        } catch (IOException e) {
//            Log.d(TAG_LOG,e.toString());
//            Log.e("IOException", "Failed to find last line");
//        }
//
//
//    }


    public void showBookmarks(View v) {

    }
//    public Long getStartTime(){
//        return startTime;
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //releasePlayer();
        //releaseRecorder();
//        if (mediaRecorder != null) {
//            mediaRecorder.release();
//            mediaRecorder = null;
//        }
    }

//    public void startChrono() {
//        chronometer.setBase(SystemClock.elapsedRealtime());
//        chronometer.start();
//    }
//
//    public void stopChrono() {
//        //Log.d(TAG_LOG, String.valueOf(chronometer.getBase()));
//        chronometer.stop();
//    }
//
//    public void callPlayer(View v) {
//        Intent intent = new Intent(this, Player.class);
//        //intent.putExtra("audioFile", fileAudio.getPath());
//        startActivity(intent);
//    }

}
