package com.example.alvin.autoupdatedemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Response;

/**
 * Created by alvin on 2017/8/23.
 */
public class downLoadApk extends AsyncTask<String,Integer,Integer> {

    private static final int TYPE_SUCCESS    = 0;
    private static final int TYEP_FAILED     = 1;
    private static final int TYPE_PAUSED     = 2;
    private static final int TYPE_CANCELED   = 3;

    private final String Tag = "DownLoadApk";
    private final String Title = "软件更新";
    private final String Cancel = "取消";

    private ProgressBar progressbar;
    private TextView tv_progress;
    private boolean isCanceled = false;
    private boolean isPaused = false;
    private Context context = null;
    private Activity activity = null;
    private int lastProgress = 0;
    private File file = null;
    private Dialog downloadDialog = null;
    public downLoadApk(Context context,Activity activity){
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute(){
        showDownloadDialog();
    }

    @Override
    protected Integer doInBackground(String... params){
        InputStream is = null;
        RandomAccessFile savedFile = null;
        try{
            long downloadedLength = 0;
            String downloadUrl = params[0];
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            file = new File(directory+fileName);

            if(file.exists()){
                file.delete();
                downloadedLength = file.length();
            }

            long contentLength = getContentLength(downloadUrl);
            if(contentLength == 0){
                return TYEP_FAILED;
            }else if(contentLength == downloadedLength){
                return TYPE_SUCCESS;
            }

            Response response = HttpUtil.getResponse(downloadUrl);
            if(response != null){
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file,"rw");
                savedFile.seek(downloadedLength);
                byte[] buff = new byte[10240];
                int total = 0;
                int len;
                while((len = is.read(buff)) != -1){
                    if(isCanceled){
                        return TYPE_CANCELED;
                    }else if(isPaused){
                        return TYPE_PAUSED;
                    }else {
                        total += len;
                        savedFile.write(buff,0,len);
                        int progress = (int)((total + downloadedLength)*100/contentLength);
                        publishProgress(progress);
                    }
                }
                response.body().close();
                return TYPE_SUCCESS;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(is != null){
                    is.close();
                }
                if(savedFile != null){
                    savedFile.close();
                }

                if(isCanceled && file != null){
                    file.delete();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return TYPE_SUCCESS;
    }

    @Override
    protected void onProgressUpdate(Integer... values){
        int progress = values[0];
        if(progress > lastProgress){
            tv_progress.setText(progress+"%");
            progressbar.setProgress(progress);
            lastProgress = progress;
        }

    }

    @Override
    protected void onPostExecute(Integer status){
        String result = "";
        switch (status){
            case TYPE_SUCCESS:
                result = "成功";
                downloadDialog.dismiss();
                installApk(file);
                break;
            case TYEP_FAILED:
                result = "失败";
                break;
            case TYPE_PAUSED:
                result = "暂停";
                break;
            case TYPE_CANCELED:
                result = "取消";
                break;
            default:
                break;
        }

        Toast.makeText(context,"下载升级文件"+result,Toast.LENGTH_SHORT).show();
    }

    private void installApk(File file){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        activity.finish();

    }
    private long getContentLength(String downloadUrl){
        try {
            return HttpUtil.getContentLength(downloadUrl);
        }catch (Exception e){
            e.printStackTrace();
        }

        return 0;
    }

    private void showDownloadDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(Title);

        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.progress,null);
        progressbar = (ProgressBar) v.findViewById(R.id.progressbar);
        tv_progress = (TextView)v.findViewById(R.id.tv_progress);
        builder.setView(v);

        builder.setNegativeButton(Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                isCanceled = true;
            }
        });

        downloadDialog = builder.create();
        downloadDialog.show();
    }

}
