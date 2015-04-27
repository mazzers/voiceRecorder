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
import com.example.mazzers.voicerecorder.bookmarks.WriteToXML;
import com.example.mazzers.voicerecorder.utils.Utils;

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
    private final String TAG_LOG = "recorderFragment";
    public static final String RECORDER_TAG = "RECORDER_TAG";
    private ImageButton btnRecord, btnBook, btnStop;
    private ImageButton btnImpBook, btnQuestBook;
    private Chronometer chronometer;
    private EditText nameField;
    private int count = 0;
    private String filePathAudio;
    private String filePath;
    private static String fileAudioName;
    private Long pressTime;
    private String bookMsg;
    private SharedPreferences sharedPreferences;
    private final int STATE_STOP = 0;
    private final int STATE_RECORDING = 1;
    private final int STATE_PAUSE = 2;
    private final int STATE_ERROR = 3;
    private int state;
    private final String RecordsFolder = Environment.getExternalStorageDirectory() + "/voicerecorder/";
    private MediaRecorder mediaRecorder;

    private final View.OnClickListener recorderOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnBook:
                    if (state == STATE_RECORDING) {
                        pressTime = SystemClock.elapsedRealtime() - chronometer.getBase();
                        showInputDialog();
                    } else {
                        Toast.makeText(getActivity(), "Can't add bookmark: no player", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btnQuestBook:
                    if (state == STATE_RECORDING) {
                        pressTime = System.currentTimeMillis();
                        bookMsg = "";
                        postDialog(3);
                    } else {
                        Toast.makeText(getActivity(), "Can't add bookmark: no player", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btnImpBook:
                    Log.d(TAG_LOG, "imp click");
                    if (state == STATE_RECORDING) {
                        pressTime = System.currentTimeMillis();
                        bookMsg = "";
                        //Log.d(TAG_LOG,"MediaRecorder Amplitude"+ String.valueOf(mediaRecorder.getMaxAmplitude()));
                        postDialog(2);
                    } else {
                        Toast.makeText(getActivity(), "Can't add bookmark: no player", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btnStopRecord:
                    if (state == STATE_RECORDING) {
                        stopRecording();
                    }

                    break;
                default:
                    break;
            }
        }
    };

    /**
     * OnCreate method
     *
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG_LOG, "onCreate");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        //filePath = Environment.getExternalStorageDirectory() + "/";
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mediaRecorder = new MediaRecorder();


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recorder_layout, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG_LOG, "onViewCreated");
        btnRecord = (ImageButton) view.findViewById(R.id.btnRecord);
        btnBook = (ImageButton) view.findViewById(R.id.btnBook);
        btnImpBook = (ImageButton) view.findViewById(R.id.btnImpBook);
        btnQuestBook = (ImageButton) view.findViewById(R.id.btnQuestBook);
        nameField = (EditText) view.findViewById(R.id.fileNameEt);
        btnStop = (ImageButton) view.findViewById(R.id.btnStopRecord);
        chronometer = (Chronometer) view.findViewById(R.id.chrono);
        btnRecord.setOnClickListener(new btnStartRecordClick());
        btnBook.setOnClickListener(recorderOnClickListener);
        btnImpBook.setOnClickListener(recorderOnClickListener);
        btnQuestBook.setOnClickListener(recorderOnClickListener);
        btnStop.setOnClickListener(recorderOnClickListener);
        state = STATE_STOP;
        newChangeButtonsState();

//        if (state == STATE_RECORDING || state == STATE_PAUSE) {
//            Log.d(TAG_LOG, "RESTORING PREV STATE");
//            restoreRecordingState();
//        } else {
//            Log.d(TAG_LOG, "STATE_NOT_RECORDING");
//            state = STATE_STOP;
//        }
        //changeButtonsState();
    }

    /**
     * Record button listener
     */
    private class btnStartRecordClick implements View.OnClickListener {
        public void onClick(View arg0) {
            if (mediaRecorder == null) {
                mediaRecorder = new MediaRecorder();
            }
            generateNewMediaRecorder();
        }
    }

    private void generateNewMediaRecorder() {
        //todo check if 3gpp working
        count = 1;
        Log.i(TAG_LOG, "Generating new MediaRecorder");
        if (nameField.getText().toString().equals("")) {
            generateName();
        } else {
            fileAudioName = nameField.getText().toString().replaceAll("[^a-zA-Z0-9.-]", "_");
            if (!fileAudioName.equals(nameField.getText().toString())) {
                Toast.makeText(getActivity(), "Special characters will be replaced with _ ", Toast.LENGTH_SHORT).show();
            }
        }
        String quality = sharedPreferences.getString("quality", "2");
        String format = sharedPreferences.getString("format", "2");
        int qualityChoice = quality.equalsIgnoreCase("2") ? 44100 : 22050;
        int formatChoice;
        if (format.equalsIgnoreCase("mp4")) {
            formatChoice = MediaRecorder.OutputFormat.MPEG_4;
            filePathAudio = RecordsFolder + fileAudioName + ".mp4";
        } else {
            formatChoice = MediaRecorder.OutputFormat.THREE_GPP;
            filePathAudio = RecordsFolder + fileAudioName + ".3gpp";
        }


        mediaRecorder.release();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setOutputFormat(formatChoice);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setAudioEncodingBitRate(16);
        mediaRecorder.setAudioSamplingRate(qualityChoice);
        mediaRecorder.setOutputFile(filePathAudio);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            Log.d(TAG_LOG, "prepare fail");
            e.printStackTrace();
        }
        startRecording();
    }

    void newChangeButtonsState() {
        switch (state) {
            case STATE_STOP:
                btnRecord.setEnabled(true);
                btnStop.setEnabled(false);
                btnBook.setEnabled(false);
                btnImpBook.setEnabled(false);
                btnQuestBook.setEnabled(false);
                nameField.setEnabled(true);
                break;
            case STATE_RECORDING:
                btnRecord.setEnabled(false);
                btnStop.setEnabled(true);
                btnBook.setEnabled(true);
                btnImpBook.setEnabled(true);
                btnQuestBook.setEnabled(true);
                nameField.setEnabled(false);
                break;
            default:
                btnRecord.setEnabled(true);
                btnStop.setEnabled(false);
                btnBook.setEnabled(false);
                btnImpBook.setEnabled(false);
                btnQuestBook.setEnabled(false);
                nameField.setEnabled(true);
                break;

        }
    }

    void startRecording() {
        Log.i(TAG_LOG, "Recording started");
        mediaRecorder.start();
        state = STATE_RECORDING;
        newChangeButtonsState();
        startChrono();
    }

    void stopRecording() {
        Log.i(TAG_LOG, "Recording stopped");
        mediaRecorder.stop();
        mediaRecorder.reset();
        state = STATE_STOP;
        newChangeButtonsState();
        stopChrono();
    }


    /**
     * Generate audioFile name if empty.
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
    void startChrono() {
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
     * Show input dialog for message
     */
    void showInputDialog() {

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
    void postDialog(int type) {
        String filePathBook = RecordsFolder + "bookmarks/" + fileAudioName + "_" + count + ".xml";
        count++;
        File fileBook = new File(filePathBook);
        long chronoTime;
        if (type == 1) {
            chronoTime = pressTime;
        } else {
            chronoTime = SystemClock.elapsedRealtime() - chronometer.getBase();
        }
        long duration = (int) (chronoTime / 1000);
        Thread xmlCreateThread = new Thread(new WriteToXML(fileBook, duration, type, bookMsg, fileAudioName, filePathAudio));
        xmlCreateThread.start();
        String tempTime = Utils.timeToString((int) duration);
        Toast.makeText(getActivity(), "Bookmark added at time: " + tempTime, Toast.LENGTH_SHORT).show();
    }


    public static RecorderFragment createNewInstance() {
        return new RecorderFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
