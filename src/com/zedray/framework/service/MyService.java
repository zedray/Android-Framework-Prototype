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

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.zedray.framework.application.Cache;
import com.zedray.framework.application.MyApplication;
import com.zedray.framework.application.ServiceQueue;
import com.zedray.framework.application.UiQueue;
import com.zedray.framework.utils.Type;

/***
 * Service class currently performs background work in response to incoming
 * messages from the UI, although this can be expanded to respond to other
 * events (Alarms, Broadcast receivers, etc).
 */
public class MyService extends Service {
    /** Performs all long running tasks in a separate thread. **/
    private WorkerThread mWorkerThread;
    /** Synchronisation lock for the WorkerThread. **/
    private final Object mWorkerThreadLock = new Object();
    /** Pointer to the Application Cache. **/
    private Cache mCache;
    /** Pointer to the Application UiQueue. **/
    private UiQueue mUiQueue;
    /** Pointer to the Application ServiceQueue. **/
    private ServiceQueue mServiceQueue;
    /** Handler for receiving all messages from the ServiceQueue. **/
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(final Message message) {
            Message messageCopy = new Message();
            messageCopy.copyFrom(message);
            processMessage(messageCopy);
        }
    };

    /***
     * Reacts to any incoming message by passing it to the WorkerThread,
     * creating a new one if necessary.
     *
     * @param message
     *            Message from UI.
     */
    private void processMessage(final Message message) {
        synchronized (mWorkerThreadLock) {
            if (mWorkerThread == null || mWorkerThread.isStopping()) {
                mWorkerThread = new WorkerThread(mCache, mUiQueue, this);
                mWorkerThread.add(message);
                mWorkerThread.start();
            } else {
                mWorkerThread.add(message);
            }
        }
    }

    @Override
    public final void onStart(final Intent intent, final int startId) {
        Log.i(MyApplication.LOG_TAG, "MyService.onStart()");
        super.onStart(intent, startId);
    }

    @Override
    public final void onCreate() {
        MyApplication myApplication = (MyApplication) getApplication();
        Log.i(MyApplication.LOG_TAG, "MyService.onCreate() " + "myApplication["
                + myApplication + "]");
        mCache = myApplication.getCache();
        mUiQueue = myApplication.getUiQueue();
        mServiceQueue = myApplication.getServiceQueue();
        
        /**
         * Resister with the ServiceQueue that the Service is now ready to
         * handle incoming messages.
         */
        mServiceQueue.registerServiceHandler(mHandler);

        /** Recreate an unresolved execution state. **/
        int state = mCache.getLongProcessState();
        if (state != -1) {
            Bundle bundle = new Bundle();
            bundle.putInt(WorkerThread.PROCESS_STATE, state);
            mServiceQueue.postToService(Type.DO_LONG_TASK, bundle);
        }

        super.onCreate();
    }

    @Override
    public final void onDestroy() {
        Log.i(MyApplication.LOG_TAG, "MyService.MyBinder.onDestroy()");
        mServiceQueue.registerServiceHandler(null);
        super.onDestroy();
    }

    @Override
    public final IBinder onBind(final Intent intent) {
        return null;
    }
}
