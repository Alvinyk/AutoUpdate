package com.example.alvin.autoupdatedemo;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by alvin on 2017/8/23.
 */
public class ContentHandle extends DefaultHandler {
    private String nodeName;
    private StringBuilder version;
    private StringBuilder appname;
    private StringBuilder appurl;


    private int versionID = 0;
    private String szApkPath = "";

    @Override
    public void startDocument()throws SAXException{
        version = new StringBuilder();
        appname = new StringBuilder();
        appurl = new StringBuilder();
    }
    @Override
    public void startElement(String uri, String localName, String qName
            , Attributes attributes)throws SAXException{
        nodeName = localName;
    }

    @Override
    public void characters(char[] ch,int start,int length)throws SAXException{
        if("version".equals(nodeName)){
            version.append(ch,start,length);
        }else if("name".equals(nodeName)){
            appname.append(ch,start,length);
        }else if("url".equals(nodeName)){
            appurl.append(ch,start,length);
        }
    }

    @Override
    public void endElement(String url,String localName,String qName)
        throws SAXException{
        if("update".equals(localName)){
            versionID =Integer.parseInt(version.toString().trim());
            szApkPath = appurl.toString().trim();

            version.setLength(0);
            appname.setLength(0);
            appurl.setLength(0);
        }
    }

    @Override
    public void endDocument()throws SAXException{
        super.endDocument();
    }

    public int getVersionID() {
        return versionID;
    }

    public String getApkPath() {
        return szApkPath;
    }
}
