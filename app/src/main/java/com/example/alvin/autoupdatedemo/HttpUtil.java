package com.example.alvin.autoupdatedemo;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by alvin on 2017/8/23.
 */
public class HttpUtil {
    private static OkHttpClient client = new OkHttpClient();

    private static Response _getAsyn(String url)throws IOException{
        Response response = null;
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            response = client.newCall(request).execute();
        }catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }

    private static String _getAsString(String url)throws IOException{
        Response response = _getAsyn(url);

        String Data = response.body().string();
        response.close();

        return Data;
    }

    private static long _getContentLength(String url)throws IOException{
        Response response = _getAsyn(url);
        if(response!= null && response.isSuccessful()){
            long contentLength = response.body().contentLength();
            response.close();
            return contentLength;
        }

        return 0;
    }


    public static Response getResponse(String url)throws IOException{
        return _getAsyn(url);
    }

    public static String getAsString(String url)throws IOException{
        return _getAsString(url);
    }

    public static long getContentLength(String url)throws IOException{
        return _getContentLength(url);
    }


}
