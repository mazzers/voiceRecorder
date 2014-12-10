package com.example.mazzers.voicerecorder.bookmarks;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;

/**
 * Created by mazzers on 24. 11. 2014.
 */
public class ReadFromXML {
    private String path, fileName;
    private int time;
    public volatile boolean parsingComplete = true;
    private XmlPullParserFactory xmlFactoryObject;
    private StringReader stringReader;
    private FileInputStream fileInputStream;
    private File inputFile;
    private String TAG_LOG = "myLogs";

    public ReadFromXML(File inputFile) {
        this.inputFile = inputFile;
    }


    public void parseXMLAndStoreIt(XmlPullParser myParser) {
        Log.d(TAG_LOG, "ReadFromXML: call parseXMLandStoreIT ");
        int event;
        try {
            event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String name=myParser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("path")) {
                            path = myParser.getAttributeValue(null, "value");
                        } else if (name.equals("fileName")) {
                            fileName = myParser.getAttributeValue(null, "value");
                        } else if (name.equals("time")) {
                            time = Integer.parseInt(myParser.getAttributeValue(null, "value"));
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

    public void fetchXML() {
        Log.d(TAG_LOG, "ReadFromXML: start of fetch XML");
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

    public String getPath() {
        return path;
    }

    public int getTime() {
        return time;
    }

    public String getName() {
        return fileName;
    }
}
