package com.example.mazzers.voicerecorder.bookmarks;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;

/**
 * Vashchenko Vitaliy A11B0529P
 * PRJ5 - Voice bookmarks
 * XML file reader. Create bookmark from XML
 */
public class ReadFromXML {
    private String path, fileName, message, bookmarkPath;
    private int time;
    private int type;
    public volatile boolean parsingComplete = true;
    private XmlPullParserFactory xmlFactoryObject;
    //private StringReader stringReader;
    private FileInputStream fileInputStream;
    private File inputFile;
    private String TAG_LOG = "Read";

    /**
     * Reader constructor
     *
     * @param inputFile - Bookmark XML file
     */
    public ReadFromXML(File inputFile) {
        this.inputFile = inputFile;
    }

    /**
     * get bookmark file path
     *
     * @return
     */
    public String getBookmarkPath() {
        return bookmarkPath;
    }

    /**
     * Create bookmark object from XML
     *
     * @param myParser
     */
    public void parseXMLAndStoreIt(XmlPullParser myParser) {
        //Log.d(TAG_LOG, "ReadFromXML: call parseXMLandStoreIT ");
        int event;
        try {
            event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myParser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.END_TAG:
                        switch (name) {
                            case "path":
                                path = myParser.getAttributeValue(null, "value");
                                break;
                            case "fileName":
                                fileName = myParser.getAttributeValue(null, "value");
                                break;
                            case "time":
                                time = Integer.parseInt(myParser.getAttributeValue(null, "value"));
                                break;
                            case "message":
                                message = myParser.getAttributeValue(null, "value");
                                break;
                            case "type":
                                type = Integer.parseInt(myParser.getAttributeValue(null, "value"));
                                break;
                            case "bookmark_path":
                                bookmarkPath = myParser.getAttributeValue(null, "value");
                                break;
                            default:
                                break;


                        }

                        break;
                }
                event = myParser.next();

            }
            parsingComplete = false;
        } catch (Exception e) {
            Log.d(TAG_LOG, "ReadFromXML: parse error");
            Log.d(TAG_LOG, e.toString());
            e.printStackTrace();
        }

    }

    /**
     * Create XML reader
     */
    public void fetchXML() {
        //Log.d(TAG_LOG, "ReadFromXML: start of fetch XML");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    fileInputStream = new FileInputStream(inputFile);
                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    xmlFactoryObject.setNamespaceAware(true);
                    XmlPullParser myparser = xmlFactoryObject.newPullParser();

                    myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES
                            , false);
                    myparser.setInput(fileInputStream, null);
                    parseXMLAndStoreIt(myparser);
                    fileInputStream.close();
                } catch (Exception e) {

                    Log.d(TAG_LOG, e.toString());
                    e.printStackTrace();
                }
            }
        });

        thread.start();


    }

    /**
     * Get patch from XML
     *
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * Get time from XML
     *
     * @return
     */
    public int getTime() {
        return time;
    }

    /**
     * Get name from XML
     *
     * @return
     */
    public String getName() {
        return fileName;
    }

    /**
     * Get message from XML
     *
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get type from XML
     *
     * @return
     */
    public int getType() {
        return type;
    }
}
