package com.app.upincode.getqd.networking.requests;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.upincode.getqd.logging.GQLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Runs several Volley requests concurrently, triggering the main handler once all threads
 * have been completed.
 * <p/>
 * Created by jpnauta on 15-09-18.
 */
public class RequestGroup implements Response.ErrorListener {
    private final List<Request> requests = new ArrayList<>();
    private Thread mThread;
    private boolean mFailure = false;
    private CountDownLatch mCountDownLatch;

    public interface Listener {
        /**
         * Called when all requests are completed
         */
        void success();
    }

    public void register(Request request) {
        requests.add(request);
    }

    public void success() {
        GQLog.dObj(RequestGroup.this, "Request completed");
        this.mCountDownLatch.countDown();
    }

    public void failure() {
        GQLog.dObj(RequestGroup.this, "Request group failure");
        this.mFailure = true;

        //
        for (int i = 0; i < this.requests.size(); i++) {
            this.mCountDownLatch.countDown();
        }
    }

    public void begin(RequestQueue queue, final RequestGroup.Listener listener) {
        //Add requests to queue
        for (Request request : this.requests) {
            queue.add(request);
        }

        this.mCountDownLatch = new CountDownLatch(this.requests.size());
        this.mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GQLog.dObj(RequestGroup.this, "Waiting for count down to complete");
                    mCountDownLatch.await();
                    GQLog.dObj(RequestGroup.this, "Count down complete!");
                    if (!mFailure) {
                        listener.success();
                    }
                } catch (InterruptedException e) {
                    //Some error occurred!
                    e.printStackTrace();
                }
            }
        });

        this.mThread.start();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        this.failure();
    }
}
