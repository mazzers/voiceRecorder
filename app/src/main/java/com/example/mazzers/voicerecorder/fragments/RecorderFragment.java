package com.example.mazzers.voicerecorder.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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

import com.example.mazzers.voicerecorder.MainActivity;
import com.example.mazzers.voicerecorder.R;
import com.example.mazzers.voicerecorder.bookmarks.WriteToXML;
import com.example.mazzers.voicerecorder.fragments.base.Recorder;
import com.example.mazzers.voicerecorder.utils.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * voiceRecorder application
 * @author Vitaliy Vashchenko A11B0529P
 * Recorder fragment with UI elements
 */
public class RecorderFragment extends Fragment {
    private final String TAG_LOG = "recorderFragment"; // fragment and log tags
    public static final String RECORDER_TAG = "RECORDER_TAG";
    private final String RecordsFolder = Environment.getExternalStorageDirectory() + "/voicerecorder/";
    private SharedPreferences sharedPreferences;
    private Handler handler = new Handler();

    private ImageButton btnRecord, btnBook, btnStop; // UI elements
    private ImageButton btnImpBook, btnQuestBook;
    private Chronometer chronometer;
    private EditText nameField;

    private int count = 0; // bookmark elements
    private String filePathAudio;
    private static String fileAudioName;
    private Long pressTime;
    private String bookMsg;

    private final int STATE_STOP = 0; // recorder elements
    private final int STATE_RECORDING = 1;
    private int state;
    private Recorder recorder;

    /**
     * Button listener
     */
    private final View.OnClickListener recorderOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnBook:
                    if (state == STATE_RECORDING) {
                        pressTime = SystemClock.elapsedRealtime() - chronometer.getBase();
                        showInputDialog();
                    } else {
                        showMessage("Can't add bookmark: no player");
                    }
                    break;
                case R.id.btnQuestBook:
                    if (state == STATE_RECORDING) {
                        pressTime = System.currentTimeMillis();
                        bookMsg = "";
                        postDialog(3);
                    } else {
                        showMessage("Can't add bookmark: no player");
                    }
                    break;
                case R.id.btnImpBook:
                    Log.d(TAG_LOG, "imp click");
                    if (state == STATE_RECORDING) {
                        pressTime = System.currentTimeMillis();
                        bookMsg = "";
                        postDialog(2);
                    } else {
                        showMessage("Can't add bookmark: no player");
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
     * @param savedInstanceState previous state
     */
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG_LOG, "onCreate");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());


    }


    /**
     * OnCreateView method
     *
     * @param inflater layout inflated
     * @param container fragment container
     * @param savedInstanceState fragment saved state
     * @return fragment view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recorder_layout, container, false);
        return rootView;
    }

    /**
     * Method called after view creation
     *
     * @param view               fragment view
     * @param savedInstanceState fragment saved state
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // define UI elements
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

        MainActivity mainActivity = (MainActivity) getActivity();
        recorder = mainActivity.getRecorder();
        // restore recorder state
        if (!recorder.isRecording()) {
            state = STATE_STOP;
        } else {
            state = STATE_RECORDING;
            startChronoAt(recorder.getBase());
            checkRecordDuration();
        }
        newChangeButtonsState();


    }


    /**
     * Record button listener
     */
    private class btnStartRecordClick implements View.OnClickListener {
        public void onClick(View arg0) {
            generateRecorder();
        }
    }

    /**
     * Thread for duration checking
     */
    private Runnable durationReached = new Runnable() {
        @Override
        public void run() {
            if (recorder.isMaxReached()) {
//                Toast.makeText(getActivity(), "Max time reached", Toast.LENGTH_SHORT).show();
                showMessage("Max time reached");
                stopRecording();
            } else {
                handler.postDelayed(this, 1000);
            }
        }
    };

    /**
     * Check if max record duration is reached
     */
    private void checkRecordDuration() {
        handler.postDelayed(durationReached, 1000);
    }

    /**
     * Generate new recorder
     */
    void generateRecorder() {
        count = 1;
        Log.i(TAG_LOG, "Generating new MediaRecorder");
        if (nameField.getText().toString().equals("")) {
            generateName();
        } else {
            fileAudioName = nameField.getText().toString().replaceAll("[^a-zA-Z0-9.-]", "_");
            if (!fileAudioName.equals(nameField.getText().toString())) {
                //Toast.makeText(getActivity(), "Special characters will be replaced with _ ", Toast.LENGTH_SHORT).show();
                showMessage("Special characters will be replaced with _ ");
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
        recorder.setMediaRecorder(filePathAudio, qualityChoice, formatChoice);
        startRecording();
    }

    /**
     * Reevaluate buttons state
     */
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

    /**
     * Start recording and reevaluate UI
     */
    void startRecording() {
        Log.i(TAG_LOG, "Recording started");
        recorder.startRecording();
        checkRecordDuration();
        state = STATE_RECORDING;
        newChangeButtonsState();
        startChrono();
    }

    /**
     * Stop recording and reevaluate UI
     */
    void stopRecording() {
        Log.i(TAG_LOG, "Recording stopped");
        recorder.stopRecording();
        state = STATE_STOP;
        handler.removeCallbacks(durationReached);
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
     * @return audio file name
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
     * Start chronometer on new base
     *
     * @param base new base
     */
    public void startChronoAt(long base) {
        chronometer.setBase(base);
        chronometer.start();
    }

    /**
     * Show message for user
     *
     * @param message message for user
     */
    void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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
                        bookMsg = editText.getText().toString();
                        postDialog(1);
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }


    /**
     * Call XML writer after bookmark button press
     *
     * @param type bookmark type
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
        showMessage("Bookmark added at time: " + tempTime);
    }


    /**
     * Stop thread on fragment detach
     */
    @Override
    public void onDetach() {
        super.onDetach();
        handler.removeCallbacks(durationReached);
        Log.i(TAG_LOG, "Fragment is detached");
    }


}
