package com.example.mazzers.voicerecorder.bookmarks;

import android.util.Log;
import android.util.Xml;

import com.example.mazzers.voicerecorder.fragments.RecorderFragment;
import com.example.mazzers.voicerecorder.recorder.startRec;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;


public class WriteToXML implements Runnable {
    //private String audioName, message, filePathBook;
    private File fileBook;
    private FileOutputStream outputStream;
    private String TAG_LOG = "myLogs";
    private Long time;

    public WriteToXML(File fileBook,long time) {
        //this.audioName = audioName;
        this.fileBook = fileBook;
        this.time = time;
        //this.message = null;

    }


    @Override
    public void run() {
        Log.d(TAG_LOG, "WriteToXML: writexml run");
        createXML();


    }
    private String WriteDataToXML() throws IOException {
        //String pathToAudio = startRec.getFilePathAudio();
        long duration = (int) ((System.currentTimeMillis() - time) / 1000);
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writter = new StringWriter();
        xmlSerializer.setOutput(writter);
        xmlSerializer.startDocument("UTF-8",true);
        xmlSerializer.startTag("","root");
        xmlSerializer.startTag("","path");
        xmlSerializer.attribute("","value",startRec.getFilePathAudio());
        xmlSerializer.endTag("","path");
        xmlSerializer.startTag("","fileName");
        xmlSerializer.attribute("","value", RecorderFragment.getFileAudioName());
        xmlSerializer.endTag("","fileName");
        xmlSerializer.startTag("","time");
        xmlSerializer.attribute("","value",String.valueOf(duration));
        xmlSerializer.endTag("","time");
        xmlSerializer.endTag("","root");
        xmlSerializer.endDocument();

        return writter.toString();
    }

    public void createXML() {

        try {
            outputStream = new FileOutputStream(fileBook);
            outputStream.write(WriteDataToXML().getBytes());
            Log.d(TAG_LOG, "WriteToXML: In try createBookmarkFile: write");
            outputStream.close();
            Log.d(TAG_LOG, "WriteToXML: Closed");

        } catch (Exception e) {

            Log.d(TAG_LOG, "WriteToXML: create xml error");
            Log.d(TAG_LOG, e.toString());

        }


    }
}
