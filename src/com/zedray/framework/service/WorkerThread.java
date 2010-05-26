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
import com.zedray.framework.utils.NotificationUtils;
import com.zedray.framework.utils.Type;

/***
 * Used by the Service to perform long running tasks (e.g. network
 * connectivity) in a single separate thread.  This implementation uses a
 * single thread to pop items off the end of a ServiceQueue, providing a clear
 * finishing point (i.e. current task complete & queue empty) where the
 * Services own stopSelf() method can then be called to terminate the
 * background part of the Application.
 */
public class WorkerThread extends Thread {

    /**
     * [Optional] Execution state of the currently running long process, used by
     * the service to recover the state after the Service has been abnormally
     * terminated.
     */
    public static final String PROCESS_STATE = "PROCESS_STATE";
    /** [Optional] Size of long task increment. **/
    private static final int LONG_TASK_INCREMENT = 10;
    /** [Optional] End of long task. **/
    private static final int LONG_TASK_COMPLETE = 100;
    /***
     * [Optional] Configures how much time (in milliseconds) should be wasted
     * between UI updates - for test use only.
     */
    private static final int WASTE_TIME = 2000;
    /** [Optional] Synchronisation lock for the Thread Sleep. **/
    private final Object mWakeLock = new Object();
    /** Queue of incoming messages. **/
    private final List<Message> mWorkQueue = new ArrayList<Message>();
    /** Pointer to the Application Cache. **/
    private final Cache mCache;
    /** Pointer to the Application UiQueue. **/
    private final UiQueue mUiQueue;
    /** Pointer to the parent Service.. **/
    private MyService mMyService;
    /***
     * TRUE when the WorkerThread can no longer handle incoming messages,
     * because it is shutting down or dead.
     */
    private boolean stopping = false;

    /***
     * Constructor which stores pointers to the Application Cache, UiQueue and
     * parent Service.
     *
     * @param cache Application Cache.
     * @param uiQueue UiQueue.
     * @param myService MyService.
     */
    protected WorkerThread(final Cache cache, final UiQueue uiQueue,
            final MyService myService) {
        mCache = cache;
        mUiQueue = uiQueue;
        mMyService = myService;
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
            case DO_SHORT_TASK:
                doShortTask(bundle);
                break;

            case DO_LONG_TASK:
                doLongTask(bundle);
                break;

            default:
                // Do nothing.
                break;
            }
        }

        mCache.setStateLongTask("");
        mUiQueue.postToUi(Type.UPDATE_SHORT_TASK, null, true);
        mCache.setStateShortTask("");
        mUiQueue.postToUi(Type.UPDATE_LONG_TASK, null, true);

        stopping = true;
        mMyService.stopSelf();
    }

    /***
     * [Optional] Example task which takes time to complete and repeatedly
     * updates the UI.
     *
     * @param bundle
     *            Bundle of extra information.
     */
    private void doShortTask(final Bundle bundle) {
        mCache.setStateShortTask("Loading short task");
        mUiQueue.postToUi(Type.UPDATE_SHORT_TASK, null, true);
        wasteTime(WASTE_TIME);
        mCache.setStateShortTask("Running short task");
        mUiQueue.postToUi(Type.UPDATE_SHORT_TASK, null, true);
        wasteTime(WASTE_TIME);
        mCache.setStateShortTask("Finishing short task");
        mUiQueue.postToUi(Type.UPDATE_SHORT_TASK, null, true);
        wasteTime(WASTE_TIME);
        mCache.setStateShortTask("Finished short task");
        mUiQueue.postToUi(Type.UPDATE_SHORT_TASK, null, true);

        if (bundle != null) {
            Bundle outBundle = new Bundle();
            outBundle.putString("TEXT",
                    "The short task has finished. Called from ["
                            + bundle.getString("TEXT") + "]");
            mUiQueue.postToUi(Type.SHOW_DIALOG, outBundle, false);
        }
    }

    /***
     * [Optional] Example task which takes time to complete and repeatedly
     * updates the UI.
     *
     * @param bundle Bundle of extra information.
     */
    private void doLongTask(final Bundle bundle) {
        mCache.setStateLongTask("Loading long task");
        mUiQueue.postToUi(Type.UPDATE_LONG_TASK, null, true);
        wasteTime(WASTE_TIME);

        int i = 0;
        if (bundle != null) {
            i = bundle.getInt(PROCESS_STATE);
        } else {
            i = 0;
        }

        for (; i <= LONG_TASK_COMPLETE; i += LONG_TASK_INCREMENT) {
            mCache.setStateLongTask("Long task " + i + "% complete");
            mUiQueue.postToUi(Type.UPDATE_LONG_TASK, null, true);
            NotificationUtils.notifyUserOfProgress(mMyService
                    .getApplicationContext(), i);
            wasteTime(WASTE_TIME);
            mCache.setLongProcessState(i);
        }
        /** Clear Long Process state. **/
        mCache.setLongProcessState(-1);

        mCache.setStateLongTask("Long task done");
        mUiQueue.postToUi(Type.UPDATE_LONG_TASK, null, true);
        NotificationUtils.notifyUserOfProgress(mMyService
                .getApplicationContext(), -1);
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
     *
     * @param time
     *            Amount of time to waste.
     */
    private void wasteTime(final long time) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() < startTime + time) {
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
