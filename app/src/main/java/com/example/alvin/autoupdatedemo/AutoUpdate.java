package com.example.alvin.autoupdatedemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.SAXParserFactory;

/**
 * Created by alvin on 2017/8/23.
 */
public class AutoUpdate extends AsyncTask<Void,Boolean,Void> {

    private final String Title = "软件更新";
    private final String Infor = "检测到新版本，是否更新";
    private final String UpdateNow = "立即更新";
    private final String UpdateLater = "稍后更新";
    private final String szPackage = "com.example.alvin.autoupdatedemo";
    private final String szUrl = "http://192.168.1.103:8080/image/package/update.xml";
    private final String Tag = "AutoUpdate";
    private final ContentHandle handle = new ContentHandle();
    private Context context = null;
    private Activity activity = null;


    public AutoUpdate(Context context,Activity activity){
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Void... params){

        Boolean isUpdate = checkUpdate();

        publishProgress(isUpdate);

        return null;
    }

    @Override
    protected void onProgressUpdate(Boolean... Params){
        Boolean isUpdate = Params[0];
        if(isUpdate){
            showNoticeDialog();
        }
    }

    @Override
    protected void onPostExecute(Void Params){

    }
    private Boolean checkUpdate(){
        if(isUpdate()){
            return true;
        }
        return false;
    }


    private int getRemoteVercode(){
        String Date = null;

        try{
            Date = HttpUtil.getAsString(szUrl);
        }catch (IOException e){
            e.printStackTrace();
        }

        parseXmlWithSAX(Date);

        return handle.getVersionID();
    }

    private void parseXmlWithSAX(String xmlData){
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            XMLReader xmlReader = factory.newSAXParser().getXMLReader();
            xmlReader.setContentHandler(handle);

            xmlReader.parse(new InputSource(new StringReader(xmlData)));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean isUpdate(){
        int localVersion = getLocalVersion(context);
        int remoteVersion = getRemoteVercode();

        if(localVersion < remoteVersion){
            Log.e(Tag,"localVersion = "+localVersion);
            Log.e(Tag,"remoteVersion = "+remoteVersion);
            return  true;
        }

        return false;
    }
    private int getLocalVersion(Context context){
        int versionCode = 0;
        try{
            versionCode = context.getPackageManager().getPackageInfo(szPackage,0).versionCode;
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }

        return versionCode;
    }

    private void showNoticeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(Title);
        builder.setMessage(Infor);

        builder.setPositiveButton(UpdateNow, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                downloadApk();
            }
        });

        builder.setNegativeButton(UpdateLater, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Dialog noticeDialog = builder.create();
        try{
            noticeDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }


        Log.e(Tag,"End of show Notify Dialog");
    }

    private String getApkPath(){
       return handle.getApkPath();
    }
    private void downloadApk(){
        String szApkPath = getApkPath();
        new downLoadApk(context,activity).execute(szApkPath);
    }
}
