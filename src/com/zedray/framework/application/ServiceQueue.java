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

package com.zedray.framework.application;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.zedray.framework.service.MyService;
import com.zedray.framework.utils.Type;

/***
 * Queue for all messages being sent from the UI to the Service. A queue is
 * required to setup the binding with the service (starting the service if
 * necessary), waiting for the binding to complete, and then sending the waiting
 * message.
 */
public class ServiceQueue {
    /** Cached application context. **/
    private final Context mContext;
    /** Handler of the service to which we can send messages. **/
    private Handler mHandler;
    /** Queue of messages waiting to be sent to the service. **/
    private final List<Message> queue;

    /***
     * Constructor, which caches the application context and creates the message
     * queue.
     *
     * @param context Application context.
     */
    protected ServiceQueue(final Context context) {
        mContext = context;
        queue = new ArrayList<Message>();
    }

    /***
     * Start the Service by calling an bindService linked to my
     * ServiceConnection.
     */
    private void startService() {
        Log.i(MyApplication.LOG_TAG, "ServiceQueue.startService()");
        mContext.startService(new Intent(mContext, MyService.class));
    }

    /***
     * Post a message to the Service if it is connected. If the service is not
     * connected, add the message to the queue and then initiate a service
     * binding.
     *
     * @param type Message Type.
     * @param bundle Optional Bundle of extra message information, NULL
     *            otherwise.
     */
    public final void postToService(final Type type, final Bundle bundle) {
        if (type == null) {
            throw new InvalidParameterException("ServiceQueue.postToService() "
                    + "Type cannot be NULL");
        }

        /** Create new message object **/
        Message message = Message.obtain();
        message.what = type.ordinal();
        message.obj = bundle;

        if (mHandler != null) {
            /** Send now. **/
            mHandler.sendMessage(message);

        } else {
            /** Send later. **/
            synchronized (queue) {
                queue.add(message);
            }
            startService();
        }
    }

    /***
     * Called by the Service to register its handler, allowing the Service
     * Queue to communicate with the Service.
     *
     * @param handler Service handler, or NULL to unregister.
     */
    public final void registerServiceHandler(final Handler handler) {
        mHandler = handler;
        if (mHandler != null) {
            synchronized (queue) {
                for (Message message : queue) {
                    mHandler.sendMessage(message);
                }
                queue.clear();
            }
        }
    }
}
