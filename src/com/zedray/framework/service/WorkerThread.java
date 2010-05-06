/*
 * Copyright 2010 Mark Brady
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zedray.framework.service;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.zedray.framework.application.Cache;
import com.zedray.framework.application.MyApplication;
import com.zedray.framework.application.UiQueue;
import com.zedray.framework.utils.Type;

/***
 * WorkerThread queues incoming messages and performs all long running tasks in
 * its own thread to avoid blocking the UI.
 */
public class WorkerThread extends Thread {
    /***
     * [Optional] Configures how much time (in milliseconds) should be wasted
     * between UI updates - for test use only.
     */
    private static final int WASTE_TIME = 3000;
    /** Synchronisation lock for the Thread Sleep. **/
    private final Object mWakeLock = new Object();
    /** Queue of incoming messages. **/
    private final List<Message> mWorkQueue = new ArrayList<Message>();
    /** Pointer to the Application Cache. **/
    private final Cache mCache;
    /** Pointer to the Application UiQueue. **/
    private final UiQueue mUiQueue;
    /***
     * TRUE when the WorkerThread can no longer handle incoming messages,
     * because it is dead or shutting down.
     */
    private boolean stopping = false;

    /***
     * Constructor which caches the Application Cache and UiQueue.
     *
     * @param cache Application Cache.
     * @param uiQueue UiQueue.
     */
    protected WorkerThread(final Cache cache, final UiQueue uiQueue) {
        mCache = cache;
        mUiQueue = uiQueue;
    }

    /***
     * Add a message to the work queue.
     *
     * @param message Message containing a description of work to be done.
     */
    protected final void add(final Message message) {
        synchronized (mWorkQueue) {
            Log.i(MyApplication.LOG_TAG, "WorkerThread.add() "
                    + "Message type[" + Type.getType(message.what) + "]");
            mWorkQueue.add(message);
        }
        showQueue();
     }

    /***
     * Returns the current state of the WorkerThread.
     *
     * @return TRUE when the WorkerThread can no longer handle incoming
     *         messages, because it is dead or shutting down, FALSE otherwise.
     */
    public final boolean isStopping() {
        return stopping;
    }

    /***
     * Main run method, where all the queued messages are executed.
     */
    public final void run() {
        setName("WorkerThread");
        while (mWorkQueue.size() > 0) {
            Type type;
            Bundle bundle = null;
            synchronized (mWorkQueue) {
                Message message = mWorkQueue.remove(0);
                Log.i(MyApplication.LOG_TAG, "WorkerThread.run() "
                        + "Message type[" + Type.getType(message.what) + "]");
                type = Type.getType(message.what);
                if (message.obj != null
                        && message.obj.getClass() == Bundle.class) {
                    bundle = (Bundle) message.obj;
                }
            }
            showQueue();

            switch (type) {
                case DO_X:
                    doX(bundle);
                    break;

                case DO_Y:
                    doY(bundle);
                    break;

                default:
                    // Do nothing.
                    break;
            }
        }
        stopping = true;
    }

    /***
     * [Optional] Example task which takes time to complete and repeatedly
     * updates the UI.
     *
     * @param bundle Bundle of extra information.
     */
    private void doX(final Bundle bundle) {
        mCache.setX("Loading X");
        mUiQueue.postToUi(Type.UPDATE_X, null, true);
        wasteTime();
        mCache.setX("Running X");
        mUiQueue.postToUi(Type.UPDATE_X, null, true);
        wasteTime();
        mCache.setX("Finishing X");
        mUiQueue.postToUi(Type.UPDATE_X, null, true);
        wasteTime();
        mCache.setX("Finished X");
        mUiQueue.postToUi(Type.UPDATE_X, null, true);
    }

    /***
     * [Optional] Example task which takes time to complete and repeatedly
     * updates the UI.
     *
     * @param bundle Bundle of extra information.
     */
    private void doY(final Bundle bundle) {
        mCache.setY("Loading Y");
        mUiQueue.postToUi(Type.UPDATE_Y, null, true);
        wasteTime();
        mCache.setY("Running Y");
        Bundle outBundle = new Bundle();
        outBundle.putString("TEXT", "Message from service. Responding to ["
                + bundle.getString("TEXT") + "]");
        mUiQueue.postToUi(Type.SHOW_DIALOG, outBundle, false);
        mUiQueue.postToUi(Type.UPDATE_Y, null, true);
        wasteTime();
        mCache.setY("Finishing Y");
        mUiQueue.postToUi(Type.UPDATE_Y, null, true);
        wasteTime();
        mCache.setY("Finished Y");
        mUiQueue.postToUi(Type.UPDATE_Y, null, true);
    }

    /***
     * [Optional] Example task which sends the current state of the queue to the
     * UI.
     */
    private void showQueue() {
        StringBuffer stringBuffer = new StringBuffer();
        for (Message message : mWorkQueue) {
            stringBuffer.append("Message type[");
            stringBuffer.append(Type.getType(message.what));
            stringBuffer.append("]\n");
        }
        mCache.setQueue(stringBuffer.toString());
        mUiQueue.postToUi(Type.UPDATE_QUEUE, null, true);
    }

    /***
     * [Optional] Slow down the running task - for test use only.
     */
    private void wasteTime() {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() < startTime + WASTE_TIME) {
            synchronized (mWakeLock) {
                try {
                    mWakeLock.wait(startTime + WASTE_TIME
                            - System.currentTimeMillis());
                } catch (InterruptedException e) {
                    // Do nothing.
                }
            }
        }
    }
}