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

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.zedray.framework.utils.Type;

/***
 * Queue for all messages being sent from the Service to the UI. A queue is
 * required as sometimes all of our Activities are in the background and cannot
 * react with appropriate UI. If an activity comes into the foreground it will
 * subscribe to the UiQueue and receive any waiting messages. The foreground
 * Activity will then receive all incoming messages until it calls unsubscribe.
 * When all Activities are unsubscribed, all messages flagged as update will be
 * suppressed. The queue size is set to one, with higher priority messages
 * overwriting lower priority pending messages.
 */
public class UiQueue {
    /** Handler of the currently subscribed Activity. **/
    private Handler mHandler;
    /**
     * Queue of messages waiting to be sent to the UI. Note: Queue size is set
     * to 1.
     */
    private Message queue;
    /** Synchronisation lock for the queue. **/
    private final Object mQueueLock = new Object();

    /***
     * Called by the BaseActivity to start receiving messages. Any queued
     * messages will be sent immediately.
     *
     * @param handler Handler of the subscribing Activity.
     */
    public final void subscribe(final Handler handler) {
        if (handler == null) {
            throw new NullPointerException("ServiceQueue.subscribe() "
                    + "Handler cannot be NULL");
        }

        mHandler = handler;
        if (queue != null) {
            synchronized (mQueueLock) {
                mHandler.sendMessage(queue);
                queue = null;
            }
        }
    }

    /***
     * Called by the BaseActivity to stop receiving messages.
     *
     * @param handler Handler of the unsubscribing Activity.
     */
    public final void unsubscribe(final Handler handler) {
        if (handler == null) {
            throw new NullPointerException("ServiceQueue.unsubscribe() "
                    + "Handler cannot be NULL.");
        }
        if (handler != mHandler) {
            Log.w(MyApplication.LOG_TAG, "ServiceQueue.unsubscribe() "
                    + "Activity is trying to unsubscribe with a different "
                    + "handler");
        } else {
            mHandler = null;
        }
    }

    /***
     * Called by the service to post a message to the UI. Messages will be sent
     * immediately if an Activity is currently subscribed. Otherwise they will
     * be either (a) suppressed if they are flagged as update, (b) ignored if
     * they are of a lower priority than an existing pending message, or (c)
     * queued. Queued messages can be overridden by incoming messages with
     * higher priority.
     *
     * @param type Message type.
     * @param bundle Optional Bundle, or NULL otherwise.
     * @param update Set this to TRUE for messages that only update the UI so
     *            they can be suppressed while no activities are on screen. This
     *            is useful as it is assumed that activities will update
     *            themselves inside of their onResume() method.
     */
    public final void postToUi(final Type type, final Bundle bundle,
            final boolean update) {
        if (type == null) {
            throw new InvalidParameterException("ServiceQueue.postToUi() "
                    + "Type cannot be NULL");
        }

        Message message = Message.obtain();
        message.what = type.ordinal();
        message.obj = bundle;

        if (mHandler != null) {
            /** Send now. **/
            mHandler.sendMessage(message);

        } else if (update) {
            /** Suppress update. **/
            Log.w(MyApplication.LOG_TAG, "UiQueue.postToUi() Suppressing "
                    + "message[" + message.what + "], as update requests "
                    + "should not be queued");

        } else {
            /** Send later. **/
            synchronized (mQueueLock) {
                if (queue == null || message.what < queue.what) {
                    queue = message;
                } else {
                    Log.w(MyApplication.LOG_TAG, "UiQueue.postToUi() "
                            + "Ignoring message[" + message.what + "], as "
                            + "highter priority message[" + message.what
                            + "] is already pending");
                }
            }
        }
    }
}