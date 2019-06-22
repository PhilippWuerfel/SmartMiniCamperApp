package com.example.smartminicamper.webservice.download.data;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class DownloadProgressResponseBody extends ResponseBody {

    private ResponseBody responseBody;
    private DownloadCallbacks mListener;
    private BufferedSource bufferedSource;

    private static final int DEFAULT_BUFFER_SIZE = 2048;

    public interface DownloadCallbacks {
        void onProgressUpdate(long bytesRead, long contentLength, boolean done);

        void onError();

        void onFinish();
    }

    public DownloadProgressResponseBody(ResponseBody responseBody, DownloadCallbacks downloadCallbacks) {
        this.responseBody = responseBody;
        this.mListener = downloadCallbacks;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {

        if(bufferedSource == null){
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }

        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;

                if (null != mListener) {
                    mListener.onProgressUpdate(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                }
                return bytesRead;
            }
        };

    }

}
