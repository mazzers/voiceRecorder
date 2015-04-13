package com.example.mazzers.voicerecorder.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import com.example.mazzers.voicerecorder.bookmarks.ScanFiles;
import com.example.mazzers.voicerecorder.bookmarks.WriteToXML;
import com.github.lassana.recorder.AudioRecorder;

import java.io.File;
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
    public static final String RECORDER_TAG = "RECORDER_TAG";
    private ImageButton btnRecord, btnBook, btnStop;
    private ImageButton btnImpBook, btnQuestBook;
    private Chronometer chronometer;
    private EditText nameField;
    private int count = 0;
    private File fileBook;
    private String filePathAudio, filePathBook;
    private String filePath;
    public static String fileAudioName;
    private Long startTime, pressTime;
    private String bookMsg;
    private SharedPreferences sharedPreferences;
    private AudioRecorder mAudioRecorder, prevAudioRecorder;
    private final int STATE_NOT_RECORDING = 0;
    private final int STATE_RECORDING = 1;
    private final int STATE_PAUSE = 3;
    private int state;
    private long chronoPauseBase;
    private String mActiveRecordFileName;
    private long chronoBase;
    private boolean isRecording = false;
    private File dir;

    private View.OnClickListener recorderOnClickListener = new View.OnClickListener() {
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
                    if (state == STATE_RECORDING || state == STATE_PAUSE) {
                        stopRecord();
                        changeButtonsState();
                    }

                    break;
                default:
                    break;
            }
        }
    };
//    private AudioReceiver audioReceiver;
//    private AudioFormatInfo audioFormatInfo;


    /**
     * OnCreate method
     *
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        //todo different qualities
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
        //btnRecord.setImageResource(R.mipmap.micro_disabled);

        if (state == STATE_RECORDING || state == STATE_PAUSE) {
            restoreRecordingState();
        } else {
            state = STATE_NOT_RECORDING;
        }
        changeButtonsState();
    }

    /**
     * Record button listener
     */
    class btnStartRecordClick implements View.OnClickListener {
        public void onClick(View arg0) {
            if (mAudioRecorder == null) {
                generateNewAudioRecorder();
            }
            switch (mAudioRecorder.getStatus()) {
                case STATUS_READY_TO_RECORD:
                    Log.d(TAG_LOG, "STATUS_READY_TO_RECORD");
                    startTime = System.currentTimeMillis();
                    startRecord();
                    startChrono();
                    //btnRecord.setBackgroundResource(R.drawable.new_pause);
                    btnRecord.setBackgroundResource(R.drawable.pause_icon);
                    break;
                case STATUS_RECORDING:
                    Log.d(TAG_LOG, "STATUS_RECORDING");
                    //todo bookmarks on pause?
                    pauseRecord();
                    pauseChrono();
                    break;
                case STATUS_RECORD_PAUSED:
                    Toast.makeText(getActivity(), "RESTORE RECORDING", Toast.LENGTH_SHORT).show();
                    Log.d(TAG_LOG, "STATUS_RECORD_PAUSED");
                    startRecord();
                    resumeChrono(SystemClock.elapsedRealtime() + chronoPauseBase);
                    break;
                case STATUS_UNKNOWN:
                    Log.d(TAG_LOG, "STATUS_UNKNOWN");
                    changeButtonsState();
                    break;
                default:
                    break;

            }


        }
    }

    private void generateNewAudioRecorder() {
        count = 1;
        Log.d(TAG_LOG, "Start Clicked...");
        if (nameField.getText().toString().equals("")) {
            generateName();
        } else {

            fileAudioName = nameField.getText().toString().replaceAll("[^a-zA-Z0-9.-]", "_");
            if (!fileAudioName.equals(nameField.getText().toString())) {
                Toast.makeText(getActivity(), "Special characters will be replaced with _ ", Toast.LENGTH_SHORT).show();
            }
            //fileAudioName=nameField.getText().toString();
        }
        filePathAudio = Environment.getExternalStorageDirectory() + "/voicerecorder/" + fileAudioName + ".mp4";
        mAudioRecorder = AudioRecorder.build(getActivity(), filePathAudio);
    }

    private void changeButtonsState() {
        if (mAudioRecorder == null) {
            Log.d(TAG_LOG, "mAudioRecorder == null");
            btnRecord.setEnabled(true);
            btnStop.setEnabled(false);
            btnQuestBook.setEnabled(false);
            btnImpBook.setEnabled(false);
            btnBook.setEnabled(false);
            nameField.setEnabled(true);
        } else {
            switch (mAudioRecorder.getStatus()) {
                case STATUS_UNKNOWN:
                    btnRecord.setEnabled(false);
                    btnStop.setEnabled(false);
                    btnQuestBook.setEnabled(false);
                    btnImpBook.setEnabled(false);
                    btnBook.setEnabled(false);
                    nameField.setEnabled(false);
                    break;
                case STATUS_READY_TO_RECORD:
                    btnRecord.setEnabled(true);
                    btnStop.setEnabled(false);
                    btnQuestBook.setEnabled(false);
                    btnImpBook.setEnabled(false);
                    btnBook.setEnabled(false);
                    nameField.setEnabled(true);
                    break;
                case STATUS_RECORDING:
                    btnRecord.setEnabled(true);
                    //btnRecord.setBackgroundResource(R.drawable.new_pause);
                    btnRecord.setBackgroundResource(R.drawable.pause_icon);
                    btnStop.setEnabled(true);
                    btnQuestBook.setEnabled(true);
                    btnImpBook.setEnabled(true);
                    btnBook.setEnabled(true);
                    nameField.setEnabled(false);
                    break;
                case STATUS_RECORD_PAUSED:
                    btnRecord.setEnabled(true);
                    //btnRecord.setBackgroundResource(R.drawable.new_micro);
                    btnRecord.setBackgroundResource(R.drawable.micro_icon);
                    btnStop.setEnabled(true);
                    btnQuestBook.setEnabled(false);
                    btnImpBook.setEnabled(false);
                    btnBook.setEnabled(false);
                    nameField.setEnabled(false);
                    break;
                default:
                    break;
            }
        }
    }

    private void startRecord() {
        mAudioRecorder.start(new AudioRecorder.OnStartListener() {
            @Override
            public void onStarted() {
                changeButtonsState();
                state = STATE_RECORDING;
            }

            @Override
            public void onException(Exception e) {
                changeButtonsState();
                Log.e(TAG_LOG, e.toString());
                state = STATE_NOT_RECORDING;
            }
        });
    }

    private void pauseRecord() {
        //todo pause not working(stop is canceling pause)
        mAudioRecorder.pause(new AudioRecorder.OnPauseListener() {
            @Override
            public void onPaused(String activeRecordFileName) {
                mActiveRecordFileName = activeRecordFileName;

                state = STATE_PAUSE;
                chronoBase = chronometer.getBase();
                changeButtonsState();
            }

            @Override
            public void onException(Exception e) {
                changeButtonsState();
                Log.e(TAG_LOG, e.toString());
                state = STATE_NOT_RECORDING;
            }
        });

    }

    private void stopRecord() {
        Thread scanFiles = new Thread(new ScanFiles(Environment.getExternalStorageDirectory() + "/voicerecorder/"));
        scanFiles.start();
        pauseRecord();
        mAudioRecorder = null;
        count = 0;
        btnRecord.setBackgroundResource(R.drawable.micro_icon);
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
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
    }

    public void resumeChrono(long base) {
        chronometer.setBase(base);
        chronometer.start();
    }

    public void pauseChrono() {
        chronoPauseBase = chronometer.getBase() - SystemClock.elapsedRealtime();
        chronometer.stop();
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
        //Log.d(TAG_LOG, "OnClick bookmark");
        filePathBook = Environment.getExternalStorageDirectory() + "/voicerecorder/bookmarks/" + fileAudioName + "_" + count + ".xml";
        //Log.d(TAG_LOG, filePathBook);
        count++;
        fileBook = new File(filePathBook);
        long chronoTime;
        if (type == 1) {
            chronoTime = pressTime;
        } else {
            chronoTime = SystemClock.elapsedRealtime() - chronometer.getBase();
        }
        long duration = (int) (chronoTime / 1000);
        Thread xmlCreateThread = new Thread(new WriteToXML(fileBook, duration, type, bookMsg, fileAudioName, filePathAudio));
        xmlCreateThread.start();
        Thread parseBookmarkFiles = new Thread(new ParseBookmarkFiles());
        parseBookmarkFiles.start();
        Toast.makeText(getActivity(), "Bookmark added at time: " + duration, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG_LOG, "RECORDER on attach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (state == STATE_RECORDING || state == STATE_PAUSE) {
            chronoBase = chronometer.getBase();
            prevAudioRecorder = mAudioRecorder;
        }
        Log.d(TAG_LOG, "RECORDER onDetach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG_LOG, "RECORDER onDestroy");
    }

    public static RecorderFragment createNewInstance() {
        return new RecorderFragment();
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

        Log.d(TAG_LOG, "save view data");


        super.onSaveInstanceState(outState);
    }


    public void restoreRecordingState() {
        Log.d(TAG_LOG, "Restore previous recording state");
        chronometer.setBase(chronoBase);
        chronometer.start();
        changeButtonsState();
        mAudioRecorder = prevAudioRecorder;
    }

}
