package com.example.mazzers.voicerecorder.fragments;

import android.app.Activity;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Vashchenko Vitaliy A11B0529P
 * PRJ5 - Voice bookmarks
 * <p/>
 * Recorder fragment. Handles recording actions
 */
public class RecorderFragment extends Fragment {
    private String TAG_LOG = "recorderFragment";
    private String TAG_RECORDER = "RECORDER_TAG";
    private ImageButton btnRecord, btnBook;
    private ImageButton btnImpBook, btnQuestBook;
    private Chronometer chronometer;
    private EditText nameField;
    private int count = 0;
    private File fileBook, fileAudio;
    private String filePathAudio, filePathBook;
    private String filePath;
    public static String fileAudioName;
    private MediaRecorder mediaRecorder, prevRecorder;
    private Long startTime, pressTime;
    private String bookMsg;
    private SharedPreferences sharedPreferences;
    //private int cnt;
    //private TextView tvTimer;
    //private CountDownTimer timer;
    private long chronoBase;
    //private ParseTask parseTask;
    //private Thread startThread;
    //private Thread stopThread;
    private boolean isRecording = false;
//    private AudioReceiver audioReceiver;
//    private AudioFormatInfo audioFormatInfo;


    /**
     * OnCreate method
     *
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG_LOG, "onCreate");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        filePath = Environment.getExternalStorageDirectory() + "/";
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());


    }


    /**
     * OnCreateView method
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    //todo rework recorder view
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recorder_layout, container, false);

        //parseTask = new ParseTask();
        Log.d(TAG_LOG, "onCreateView");
        btnRecord = (ImageButton) rootView.findViewById(R.id.btnRecord);
        btnBook = (ImageButton) rootView.findViewById(R.id.btnBook);
        btnImpBook = (ImageButton) rootView.findViewById(R.id.btnImpBook);
        btnQuestBook = (ImageButton) rootView.findViewById(R.id.btnQuestBook);
        nameField = (EditText) rootView.findViewById(R.id.fileNameEt);
        //tvTimer = (TextView) rootView.findViewById(R.id.tvTimer);


        chronometer = (Chronometer) rootView.findViewById(R.id.chrono);
        btnRecord.setOnClickListener(new btnStartRecordClick());
        btnBook.setOnClickListener(new btnBookClick());
        btnImpBook.setOnClickListener(new btnImpBookClick());
        btnQuestBook.setOnClickListener(new btnQuestClick());

        if (isRecording) {
            restoreRecordingState();


        } else {
            btnBook.setEnabled(false);
            btnImpBook.setEnabled(false);
            btnQuestBook.setEnabled(false);
        }

        //bundle = new Bundle();


//        cnt =0;
//        timer = new CountDownTimer(Long.MAX_VALUE,1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                cnt++;
//                String time = new Integer(cnt).toString();
//
//                long millis = cnt;
//                int seconds = (int) (millis / 60);
//                int minutes = seconds / 60;
//                seconds     = seconds % 60;
//
//                tvTimer.setText(String.format("%d:%02d:%02d", minutes, seconds,millis));
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        };

//        audioFormatInfo = new AudioFormatInfo();
//        audioReceiver= new AudioReceiver(audioFormatInfo);
        return rootView;
    }

    /**
     * Record button listener
     */
    class btnStartRecordClick implements View.OnClickListener {
        public void onClick(View arg0) {
//
            if (!isRecording) {
                count = 1;
                Log.d(TAG_LOG, "Start Clicked...");
                releaseRecorder();
                mediaRecorder = new MediaRecorder();
                if (nameField.getText().toString().equals("")) {
                    generateName();
                } else {

                    fileAudioName = nameField.getText().toString().replaceAll("[^a-zA-Z0-9.-]", "_");
                    if (!fileAudioName.equals(nameField.getText().toString())) {
                        Toast.makeText(getActivity(), "Special characters will be replaced with _ ", Toast.LENGTH_SHORT).show();
                    }
                    //fileAudioName=nameField.getText().toString();
                }
                startTime = System.currentTimeMillis();
                btnQuestBook.setEnabled(true);
                btnBook.setEnabled(true);
                nameField.setEnabled(false);
                btnImpBook.setEnabled(true);
                //boolean quality = sharedPreferences.getBoolean("quality_checkbox", false);
                //int quality_type = Integer.parseInt(sharedPreferences.getString("quality", "2"));


                //Log.d(TAG_LOG, "start Thread Created");
                startRecording();

                Log.d(TAG_LOG, "start Recording");


//                audioReceiver.startRecording();
                startChrono();
                //timer.start();
                btnRecord.setBackgroundResource(R.drawable.new_stop_2);

            } else {
                Log.d(TAG_LOG, "onclick Stop Record");
                count = 0;
                btnImpBook.setEnabled(false);
                btnQuestBook.setEnabled(false);
                btnBook.setEnabled(false);
                nameField.setEnabled(true);
                //stopThread = new Thread(new stopRecording(mediaRecorder));
                //stopThread.start();
                //mediaRecorder.stop();
                //startRec.stpRecording();
                stopRecording();
//                audioReceiver.stopRecording();
                stopChrono();
                btnRecord.setBackgroundResource(R.drawable.new_micro);
            }


        }
    }

    /**
     * Bookmark button listener
     */
    class btnImpBookClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Log.d(TAG_LOG, "imp click");
            if (isRecording) {
                pressTime = System.currentTimeMillis();
                bookMsg = "";
                //Log.d(TAG_LOG,"MediaRecorder Amplitude"+ String.valueOf(mediaRecorder.getMaxAmplitude()));
                postDialog(2);
            } else {
                Toast.makeText(getActivity(), "Can't add bookmark: no player", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Bookmark button listener
     */
    class btnQuestClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (isRecording) {
                pressTime = System.currentTimeMillis();
                bookMsg = "";
                postDialog(3);
            } else {
                Toast.makeText(getActivity(), "Can't add bookmark: no player", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Bookmark button listener
     */
    class btnBookClick implements View.OnClickListener {


        @Override
        public void onClick(View v) {
            //TODO return case
            if (isRecording) {
                pressTime = System.currentTimeMillis();
                showInputDialog();
            } else {
                Toast.makeText(getActivity(), "Can't add bookmark: no player", Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * Generate audiofile name if empty.
     */
    private void generateName() {

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            Date now = new Date();
            fileAudioName = formatter.format(now);
        } catch (Exception e) {
            Log.d(TAG_LOG, "" + e.toString());
        }

    }

    /**
     * Get audioFile name
     *
     * @return
     */
    public static String getFileAudioName() {
        return fileAudioName;
    }

    /**
     * Start time widget
     */
    public void startChrono() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }

    /**
     * Stop time widget
     */
    public void stopChrono() {
        //Log.d(TAG_LOG, String.valueOf(chronometer.getBase()));
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
    }

    /**
     * Release recorder
     */
    private void releaseRecorder() {
        Log.d(TAG_LOG, " Release method");
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    /**
     * Show input dialog for message
     */
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

    /**
     * Call XML writer after bookmark button press
     *
     * @param type
     */
    public void postDialog(int type) {
        Log.d(TAG_LOG, "OnClick bookmark");
        filePathBook = Environment.getExternalStorageDirectory() + "/voicerecorder/bookmarks/" + fileAudioName + "_" + count + ".xml";
        Log.d(TAG_LOG, filePathBook);
        count++;

        fileBook = new File(filePathBook);
        long duration = (int) ((pressTime - startTime) / 1000);
        //Toast.makeText(getActivity(), "Bookmark added " + duration, Toast.LENGTH_SHORT).show();
        //Log.d(TAG_LOG,"\n"+filePathBook+"\n"+duration+"\n"+type+"\n"+bookMsg);
        Thread xmlCreateThread = new Thread(new WriteToXML(fileBook, duration, type, bookMsg, fileAudioName, filePathAudio));
        xmlCreateThread.start();
        Thread parseBookmarkFiles = new Thread(new ParseBookmarkFiles());
        parseBookmarkFiles.start();
        //ParseTask newParseTask = new ParseTask();
        //newParseTask.execute();
        Toast.makeText(getActivity(), "Bookmark added", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG_RECORDER, "RECORDER on attach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (isRecording) {
            chronoBase = chronometer.getBase();
            prevRecorder = mediaRecorder;
        }
        Log.d(TAG_RECORDER, "RECORDER onDetach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG_RECORDER, "RECORDER onDestroy");
    }

    public static RecorderFragment createNewInstance() {
        RecorderFragment recorderFragment = new RecorderFragment();
//        Bundle args = new Bundle();
//        args.putString("fragment_tag",tag);
//        recorderFragment.setArguments(args);
        return recorderFragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (isRecording) {
            Log.d(TAG_LOG, "is recording");
            if (startTime != null) {
                Log.d(TAG_LOG, "startTime not null");
                outState.putLong("startTime", startTime);
                outState.putLong("chronoBase", chronometer.getBase());
            }
        }

        //outState.putLong("pressTime",pressTime);

        Log.d(TAG_LOG, "save view data");


        super.onSaveInstanceState(outState);
    }

    public void startRecording() {
        //todo settings debug

        if (mediaRecorder == null) Log.d(TAG_LOG, "mediaRecorder is null");
        Log.d(TAG_LOG, "startRec: IN Method start Recording");
        int quality_type = Integer.parseInt(sharedPreferences.getString("quality", "2"));
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        } catch (Exception e) {
            Log.d(TAG_LOG, e.toString());
        }
        Log.d(TAG_LOG, "startRec: Set mic source");

//        if (quality) {
//            Log.d(TAG_LOG, "startRec: Quality checked");
//            filePathAudio = Environment.getExternalStorageDirectory() + "/voicerecorder/" + fileAudioName + ".mp4";
//            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        } else {
//            filePathAudio = Environment.getExternalStorageDirectory() + "/voicerecorder/" + fileAudioName + ".3gpp";
//            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//
//        }

        switch (quality_type) {
            case 1:
                Log.d(TAG_LOG, "Poor quality");
                filePathAudio = Environment.getExternalStorageDirectory() + "/voicerecorder/" + fileAudioName + ".3gpp";

                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setAudioSamplingRate(8000);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                break;
            case 2:
                Log.d(TAG_LOG, "Good quality");
                filePathAudio = Environment.getExternalStorageDirectory() + "/voicerecorder/" + fileAudioName + ".mp4";

                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mediaRecorder.setAudioSamplingRate(44100);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                break;

        }

        //recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        fileAudio = new File(filePathAudio);
        Log.d(TAG_LOG, "startRec: " + filePathAudio);
        mediaRecorder.setOutputFile(filePathAudio);

        Log.d(TAG_LOG, "startRec: Try to prepare");
        try {
            Log.d(TAG_LOG, "startRec: In try");
            mediaRecorder.prepare();
            Log.d(TAG_LOG, "startRec: After prepare");
        } catch (IOException e) {
            Log.d(TAG_LOG, "startRec: Prepare fail");
            Log.d(TAG_LOG, e.toString());
        }
        Log.d(TAG_LOG, "startRec: Prepare OK");
        try {
            mediaRecorder.start();
            isRecording = true;
        } catch (Exception e) {
            Log.e(TAG_LOG, e.toString());
        }


    }

    public void stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                isRecording = false;
            } catch (Exception e) {
                Log.e(TAG_LOG, e.toString());
            }

        }
    }

    public void restoreRecordingState() {
        Log.d(TAG_LOG, "Restore previous recording state");
        chronometer.setBase(chronoBase);
        chronometer.start();
        btnRecord.setBackgroundResource(R.drawable.new_stop_2);
        mediaRecorder = prevRecorder;
        nameField.setEnabled(false);
        btnRecord.setEnabled(true);
        btnImpBook.setEnabled(true);
        btnQuestBook.setEnabled(true);
        btnBook.setEnabled(true);
    }

}
