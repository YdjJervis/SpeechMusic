package com.aispeech.aios.music.util.download;

import com.aispeech.ailog.AILog;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;

/**
 * @desc 文件下载器
 * @auth AISPEECH
 * @date 2016-02-21
 * @copyright aispeech.com
 */
public class FileDownLoader {

    private static final String TAG = "FileDownLoader";

    public interface DownLoaderListener {
        void onResult(int res, String s);
    }

    public FileDownLoader() {
    }

    //download file
    public void download(String uri, String savePath, DownLoaderListener downLoaderListener) {
        // 指定文件类型
        HttpClientUtil.get(uri, new MusicResponseHandler(savePath, downLoaderListener));
    }

    public class MusicResponseHandler extends AsyncHttpResponseHandler {
        private String mSavePathString;
        private DownLoaderListener mDownLoaderListener;

        public MusicResponseHandler(String path, DownLoaderListener downLoaderListener) {
            super();
            mSavePathString = path;
            mDownLoaderListener = downLoaderListener;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
            AILog.i(TAG, "download success");
            AILog.i(TAG, " statusCode=========" + statusCode);
            AILog.i(TAG, " statusCode====binaryData len=====" + binaryData.length);
            if (statusCode == 200 && binaryData != null && binaryData.length > 0) {
                boolean success = saveFile(binaryData, mSavePathString);
                if (success) {
                    mDownLoaderListener.onResult(0, mSavePathString);
                } else {
                    //fail
                    mDownLoaderListener.onResult(-1, mSavePathString);
                }
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
            AILog.e(TAG, "download failed");
            AILog.i(TAG, " statusCode=========" + statusCode);
            AILog.i(TAG, " statusCode====binaryData len=====" + binaryData);
            mDownLoaderListener.onResult(-1, mSavePathString);
        }

        private boolean saveFile(byte[] binaryData, String savePath) {
            AILog.i(TAG, "save path:" + savePath);

            File file = new File(savePath);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                out.write(binaryData);
            } catch (Exception e) {
                AILog.e(TAG, e.toString());
                return false;
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        AILog.e(TAG, e.toString());
                    }
                }
            }
            return true;
        }

    }

}
