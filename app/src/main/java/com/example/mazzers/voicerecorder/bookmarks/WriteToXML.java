package com.example.mazzers.voicerecorder.bookmarks;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Vashchenko Vitaliy A11B0529P
 * PRJ5 - Voice bookmarks
 * XML writer. Create  user bookmarks.
 */

public class WriteToXML implements Runnable {
    //private String audioName, message, filePathBook;
    private File fileBook;
    private FileOutputStream outputStream;
    private String TAG_LOG = "Write&Parse";
    private Long startTime, pressTime;
    private String message, fileAudioName, filePathAudio;
    private Long duration;
    private int type;


    /**
     * Bookmark constructor
     *
     * @param fileBook
     * @param duration
     * @param type
     * @param message
     */
    public WriteToXML(File fileBook, long duration, int type, String message, String fileAudioName, String filePathAudio) {
        this.fileBook = fileBook;
        this.duration = duration;
        //this.startTime = startTime;
        //this.pressTime = pressTime;
        this.message = message;
        this.type = type;
        this.fileAudioName = fileAudioName;
        this.filePathAudio = filePathAudio;

    }


    @Override
    public void run() {
        Log.d(TAG_LOG, "writexml run");
        createXML();


    }

    /**
     * Write XML file from user input
     *
     * @return
     * @throws IOException
     */
    private String WriteDataToXML() throws IOException {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writter = new StringWriter();
        xmlSerializer.setOutput(writter);
        xmlSerializer.startDocument("UTF-8", true);
        xmlSerializer.startTag("", "root");
        xmlSerializer.startTag("", "path");
        xmlSerializer.attribute("", "value", filePathAudio);
        xmlSerializer.endTag("", "path");
        xmlSerializer.startTag("", "bookmark_path");
        xmlSerializer.attribute("", "value", fileBook.getPath());
        xmlSerializer.endTag("", "bookmark_path");
        xmlSerializer.startTag("", "fileName");
        xmlSerializer.attribute("", "value", fileAudioName);
        xmlSerializer.endTag("", "fileName");
        xmlSerializer.startTag("", "type");
        xmlSerializer.attribute("", "value", String.valueOf(type));
        xmlSerializer.endTag("", "type");
        xmlSerializer.startTag("", "time");
        xmlSerializer.attribute("", "value", String.valueOf(duration));
        xmlSerializer.endTag("", "time");
        xmlSerializer.startTag("", "message");
        xmlSerializer.attribute("", "value", message);
        xmlSerializer.endTag("", "message");
        xmlSerializer.endTag("", "root");
        xmlSerializer.endDocument();

        return writter.toString();
    }

    /**
     * Create XML writer
     */
    public void createXML() {

        try {
            outputStream = new FileOutputStream(fileBook);

            outputStream.write(WriteDataToXML().getBytes());
            Log.d(TAG_LOG, "In try createBookmarkFile: write");
            outputStream.close();
            Log.d(TAG_LOG, "Closed");

        } catch (Exception e) {

            e.getCause().toString();

        }


    }
}
