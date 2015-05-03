package com.example.mazzers.voicerecorder.bookmarks;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

/**
 * voiceRecorder application
 *
 * @author Vitaliy Vashchenko A11B0529P
 *         Create XML file from new data
 */
public class WriteToXML implements Runnable {
    private final File fileBook;
    private final String message;
    private final String fileAudioName;
    private final String filePathAudio;
    private final Long duration;
    private final int type;


    /**
     * Bookmark file constructor
     *
     * @param fileBook path to bookmark
     * @param duration bookmark time
     * @param type bookmark type
     * @param message bookmark message
     */
    public WriteToXML(File fileBook, long duration, int type, String message, String fileAudioName, String filePathAudio) {
        this.fileBook = fileBook;
        this.duration = duration;
        this.message = message;
        this.type = type;
        this.fileAudioName = fileAudioName;
        this.filePathAudio = filePathAudio;

    }

    /**
     * Thread run method
     */
    @Override
    public void run() {
        createXML();


    }

    /**
     * Write XML file from user input
     *
     * @return formatted string
     * @throws IOException
     */
    private String WriteDataToXML() throws IOException {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        xmlSerializer.setOutput(writer);
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

        return writer.toString();
    }

    /**
     * Create XML writer
     */
    void createXML() {
        try {
            FileOutputStream outputStream = new FileOutputStream(fileBook); // set output stream
            outputStream.write(WriteDataToXML().getBytes()); // write string to output stream
            outputStream.close(); // close stream
        } catch (Exception e) {
            String TAG_LOG = "WriteToXML";
            Log.e(TAG_LOG, e.toString());
        }
    }
}
