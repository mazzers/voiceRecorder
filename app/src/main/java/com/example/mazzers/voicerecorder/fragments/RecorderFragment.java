package com.example.mazzers.voicerecorder.fragments;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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
    ImageButton btnRecord, btnStop, btnBook;
    CheckBox chkQuality;
    private Chronometer chronometer;
    private int count = 0;
    File fileAudio, fileBook;
    String filePathAudio, filePathBook;
    String filePath;
    static String fileAudioName;
    private MediaRecorder mediaRecorder;
    private Long startTime,pressTime;
    private DialogFragment messageDialog;
    private String message;
    private Bundle bundle;
    private TextView bookmarkMSG;


    public RecorderFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    // @Nullable
    @Override
    //todo rework recorder view
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recorder_layout, container, false);
        Log.d(TAG_LOG, "RecorderFragment: onCreateView");
        btnRecord = (ImageButton) rootView.findViewById(R.id.btnRecord);
        btnStop = (ImageButton) rootView.findViewById(R.id.btnStop);
        btnBook = (ImageButton) rootView.findViewById(R.id.btnBook);
        bookmarkMSG = (TextView) rootView.findViewById(R.id.bookmark_message_TV);
        messageDialog = new MessageDialog();
        //todo hide record settings
        chkQuality = (CheckBox) rootView.findViewById(R.id.chkQuality);
        chronometer = (Chronometer) rootView.findViewById(R.id.chrono);
        btnRecord.setOnClickListener(new btnStartRecordClick());
        btnStop.setOnClickListener(new btnStopRecordClick());
        btnBook.setOnClickListener(new btnBookClick());
        filePath = Environment.getExternalStorageDirectory() + "/";
        bundle = new Bundle();

        mediaRecorder = new MediaRecorder();

        return rootView;
    }

    class btnStartRecordClick implements View.OnClickListener {
        public void onClick(View arg0) {
            count = 1;
            Log.d(TAG_LOG, "RecorderFragment: Start Clicked...");
            releaseRecorder();
            mediaRecorder = new MediaRecorder();
            generateName();
            startTime = System.currentTimeMillis();
            //Thread startThread = new Thread(new startRec(mediaRecorder, rgOut.getCheckedRadioButtonId(), chkQuality.isChecked(), fileAudioName, startTime));
            Thread startThread = new Thread(new startRec(mediaRecorder, chkQuality.isChecked(), fileAudioName, startTime));
            Log.d(TAG_LOG, "RecorderFragment: start Thread Created");

            startThread.start();
            Log.d(TAG_LOG, "RecorderFragment: start Recording");
            startChrono();

        }
    }

    class btnStopRecordClick implements View.OnClickListener {


        @Override
        public void onClick(View v) {
            Log.d(TAG_LOG, "RecorderFragment: onclick Stop Record");
            count = 0;
            Thread stopThread = new Thread(new stopRecording(mediaRecorder));
            stopThread.start();
            stopChrono();
        }
    }

    class btnBookClick implements View.OnClickListener {


        @Override
        public void onClick(View v) {
            //TODO return case
            if (startRec.isRecording()) {
            //try {
            //messageDialog.show(getFragmentManager(), "messageDialog");
              pressTime=System.currentTimeMillis();
              showInputDialog();

            //bundle.getString("message");
            //message = bundle.getString("message");
            //Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
//            Log.d(TAG_LOG, "RecorderFragment: OnClick bookmark");
//            filePathBook = Environment.getExternalStorageDirectory() + "/voicerecorder/bookmarks/" + fileAudioName + "_" + count + ".xml";
//            Log.d(TAG_LOG, "RecorderFragment: " + filePathBook);
//            count++;
//
//            fileBook = new File(filePathBook);
//            Thread xmlCreateThread = new Thread(new WriteToXML(fileBook, startTime));
//            xmlCreateThread.start();
//            Thread parseBookmarkFiles = new Thread(new ParseBookmarkFiles());
//            parseBookmarkFiles.start();
//            Toast.makeText(getActivity(), "Bookmark added", Toast.LENGTH_SHORT).show();
            //}catch(NullPointerException e)
            //} else {
            //    Toast.makeText(getActivity(), "Can't add bookmark: no player", Toast.LENGTH_SHORT).show();
//            }
            }else {
                Toast.makeText(getActivity(),"Can't add bookmark: no player",Toast.LENGTH_SHORT).show();
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
                        bookmarkMSG.setText(editText.getText());
                        postDialog();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                bookmarkMSG.setText("");
                                dialog.cancel();
                                postDialog();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }

    public void postDialog(){
        Log.d(TAG_LOG, "RecorderFragment: OnClick bookmark");
        filePathBook = Environment.getExternalStorageDirectory() + "/voicerecorder/bookmarks/" + fileAudioName + "_" + count + ".xml";
        Log.d(TAG_LOG, "RecorderFragment: " + filePathBook);
        count++;

        fileBook = new File(filePathBook);
        long duration = (int) ((pressTime - startTime) / 1000);
        Toast.makeText(getActivity(), "Bookmark added"+duration, Toast.LENGTH_SHORT).show();

        Thread xmlCreateThread = new Thread(new WriteToXML(fileBook, duration,getMessage()));
        xmlCreateThread.start();
        Thread parseBookmarkFiles = new Thread(new ParseBookmarkFiles());
        parseBookmarkFiles.start();
        Toast.makeText(getActivity(), "Bookmark added", Toast.LENGTH_SHORT).show();
    }

    public String getMessage(){
        return bookmarkMSG.getText().toString();
    }

}
