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

import android.app.Application;

/***
 * Application class persists for the duration of the JRE, and is used to store
 * all the persistence classes (database + cache) and for storing the message
 * handling framework (ServiceQueue + UiQueue).
 */
public class MyApplication extends Application {
    /** [Optional] Tag for all application logs. **/
    public static final String LOG_TAG = "MyApplication";
    /** Lazy loaded ServiceQueue. **/
    private ServiceQueue mServiceQueue;
    /** Lazy loaded UiQueue. **/
    private UiQueue mUiQueue;
    /** Lazy loaded Cache. **/
    private Cache mCache;
    /** Lazy loaded Database **/
    // private DatabaseHelper mDb;

    /***
     * Returns the Lazy loaded ServiceQueue.
     *
     * @return ServiceQueue
     */
    public final synchronized ServiceQueue getServiceQueue() {
        if (mServiceQueue == null) {
            mServiceQueue = new ServiceQueue(this);
        }
        return mServiceQueue;
    }

    /***
     * Returns the Lazy loaded UiQueue.
     *
     * @return UiQueue
     */
    public final synchronized UiQueue getUiQueue() {
        if (mUiQueue == null) {
            mUiQueue = new UiQueue();
        }
        return mUiQueue;
    }

    /***
     * Returns the Lazy loaded Cache.
     *
     * @return Cache
     */
    public final synchronized Cache getCache() {
        if (mCache == null) {
            mCache = new Cache(this);
        }
        return mCache;
    }

    /***
     * Returns the Lazy loaded DatabaseHelper.
     *
     * @return DatabaseHelper
     */
    public final synchronized DatabaseHelper getDb() {
        // TODO Implement database.
        return null;
    }
}