package com.example.mazzers.voicerecorder.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mazzers.voicerecorder.R;
import com.example.mazzers.voicerecorder.bookmarks.ParseBookmarkFiles;
import com.example.mazzers.voicerecorder.bookmarks.WriteToXML;
import com.example.mazzers.voicerecorder.recorder.startRec;
import com.example.mazzers.voicerecorder.recorder.stopRecording;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mazzers on 26. 11. 2014.
 */
public class RecorderFragment extends Fragment {
    private String TAG_LOG = "myLogs";
    private ImageButton btnRecord, btnBook;
    private ImageButton btnImpBook, btnQuestBook;
    private Chronometer chronometer;
    private EditText nameField;
    private int count = 0;
    private File  fileBook;
    private String filePathAudio, filePathBook;
    private String filePath;
    public static String fileAudioName;
    private MediaRecorder mediaRecorder;
    private Long startTime, pressTime;
    //private DialogFragment messageDialog;
    //private String message;
    //private Bundle bundle;
    private String bookMsg;
    private SharedPreferences sharedPreferences;


    public RecorderFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    //todo rework recorder view
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recorder_layout, container, false);
        Log.d(TAG_LOG, "RecorderFragment: onCreateView");
        btnRecord = (ImageButton) rootView.findViewById(R.id.btnRecord);
        btnBook = (ImageButton) rootView.findViewById(R.id.btnBook);
        btnImpBook = (ImageButton) rootView.findViewById(R.id.btnImpBook);
        btnQuestBook = (ImageButton) rootView.findViewById(R.id.btnQuestBook);
        nameField = (EditText) rootView.findViewById(R.id.fileNameEt);
        //todo hide record settings
        chronometer = (Chronometer) rootView.findViewById(R.id.chrono);
        btnRecord.setOnClickListener(new btnStartRecordClick());
        btnBook.setOnClickListener(new btnBookClick());
        btnImpBook.setOnClickListener(new btnImpBookClick());
        btnQuestBook.setOnClickListener(new btnQuestClick());
        filePath = Environment.getExternalStorageDirectory() + "/";

        //bundle = new Bundle();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mediaRecorder = new MediaRecorder();
        btnBook.setEnabled(false);
        btnImpBook.setEnabled(false);
        btnQuestBook.setEnabled(false);
        return rootView;
    }

    class btnStartRecordClick implements View.OnClickListener {
        public void onClick(View arg0) {
            if (!startRec.isRecording()) {
                count = 1;
                Log.d(TAG_LOG, "RecorderFragment: Start Clicked...");
                releaseRecorder();
                mediaRecorder = new MediaRecorder();
                if (nameField.getText().toString().equals("")){
                    generateName();
                }else {

                    fileAudioName = nameField.getText().toString().replaceAll("[^a-zA-Z0-9.-]", "_");
                    if(!fileAudioName.equals(nameField.getText().toString())){
                        Toast.makeText(getActivity(),"Special characters will be replaced with _ ",Toast.LENGTH_SHORT).show();
                    }
                   //fileAudioName=nameField.getText().toString();
                }
                startTime = System.currentTimeMillis();
                btnQuestBook.setEnabled(true);
                btnBook.setEnabled(true);
                btnImpBook.setEnabled(true);
                boolean quality = sharedPreferences.getBoolean("quality_checkbox", false);
                Thread startThread = new Thread(new startRec(mediaRecorder, quality, fileAudioName, startTime));
                Log.d(TAG_LOG, "RecorderFragment: start Thread Created");

                startThread.start();
                Log.d(TAG_LOG, "RecorderFragment: start Recording");
                startChrono();
                btnRecord.setBackgroundResource(R.drawable.new_stop_2);

            }else {
                Log.d(TAG_LOG, "RecorderFragment: onclick Stop Record");
                count = 0;
                btnImpBook.setEnabled(false);
                btnQuestBook.setEnabled(false);
                btnBook.setEnabled(false);
                Thread stopThread = new Thread(new stopRecording(mediaRecorder));
                stopThread.start();
                stopChrono();
                btnRecord.setBackgroundResource(R.drawable.new_micro);
            }


        }
    }


    class btnImpBookClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (startRec.isRecording()) {
                pressTime = System.currentTimeMillis();
                bookMsg="";
                postDialog(2);
            } else {
                Toast.makeText(getActivity(), "Can't add bookmark: no player", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class btnQuestClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (startRec.isRecording()) {
                pressTime = System.currentTimeMillis();
                bookMsg="";
                postDialog(3);
            } else {
                Toast.makeText(getActivity(), "Can't add bookmark: no player", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class btnBookClick implements View.OnClickListener {


        @Override
        public void onClick(View v) {
            //TODO return case
            if (startRec.isRecording()) {
                pressTime = System.currentTimeMillis();
                showInputDialog();


            } else {
                Toast.makeText(getActivity(), "Can't add bookmark: no player", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void generateName() {

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            Date now = new Date();
            fileAudioName = formatter.format(now);
        } catch (Exception e) {
            Log.d(TAG_LOG, "RecorderFragment: " + e.toString());
        }

    }

    public static String getFileAudioName() {
        return fileAudioName;
    }

    public void startChrono() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }

    public void stopChrono() {
        //Log.d(TAG_LOG, String.valueOf(chronometer.getBase()));
        chronometer.stop();
    }

    private void releaseRecorder() {
        Log.d(TAG_LOG, "RecorderFragment: Release method");
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    protected void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.message_dialog_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView
                .findViewById(R.id.message_editText);
        // setup a dialog window
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //bookmarkMSG.setText(editText.getText());
                        bookMsg = editText.getText().toString();
                        postDialog(1);
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //bookmarkMSG.setText("");
                                dialog.cancel();
                                //postDialog(1);
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }

    public void postDialog(int type) {
        Log.d(TAG_LOG, "RecorderFragment: OnClick bookmark");
        filePathBook = Environment.getExternalStorageDirectory() + "/voicerecorder/bookmarks/" + fileAudioName + "_" + count + ".xml";
        Log.d(TAG_LOG, "RecorderFragment: " + filePathBook);
        count++;

        fileBook = new File(filePathBook);
        long duration = (int) ((pressTime - startTime) / 1000);
        //Toast.makeText(getActivity(), "Bookmark added " + duration, Toast.LENGTH_SHORT).show();

        Thread xmlCreateThread = new Thread(new WriteToXML(fileBook, duration, type, bookMsg));
        xmlCreateThread.start();
        Thread parseBookmarkFiles = new Thread(new ParseBookmarkFiles());
        parseBookmarkFiles.start();
        //Toast.makeText(getActivity(), "Bookmark added", Toast.LENGTH_SHORT).show();
    }




}
