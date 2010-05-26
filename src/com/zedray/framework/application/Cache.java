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

import android.content.Context;
import android.content.SharedPreferences;

/***
 * Store application state information either permanently (in a properties
 * file), or in memory for the duration of the Application class lifecycle.
 */
public class Cache {
    /** Preferences file name. **/
    private static final String PREFS_FILE = "CACHE";

    /** [Optional] Preferences ID for process X. **/
    private static final String STATE_SHORT_TASK = "STATE_SHORT_TASK";

    /** [Optional] Preferences ID for process Y. **/
    private static final String STATE_LONG_TASK = "STATE_LONG_TASK";

    /** [Optional] Preferences ID for WorkerThread Queue. **/
    private static final String STATE_QUEUE = "STATE_QUEUE";

    /** [Optional] Execution state. **/
    private static final String STATE_PROCESS = "STATE_PROCESS";

    /** Cached application context. **/
    private final Context mContext;

    /***
     * Constructor stores the application context.
     *
     * @param context Application context.
     */
    protected Cache(final Context context) {
        mContext = context;
    }

    /***
     * [Optional] Set the state of short task.
     *
     * @param value State value.
     */
    public final void setStateShortTask(final String value) {
        setValue(mContext, STATE_SHORT_TASK, value);
    }

    /***
     * [Optional] Get the state of short task.
     *
     * @return State value.
     */
    public final String getStateShortTask() {
        return getValue(mContext, STATE_SHORT_TASK, null);
    }

    /***
     * [Optional] Set the state of long task.
     *
     * @param value State value.
     */
    public final void setStateLongTask(final String value) {
        setValue(mContext, STATE_LONG_TASK, value);
    }

    /***
     * [Optional] Get the state of long task.
     *
     * @return State value.
     */
    public final String getStateLongTask() {
        return getValue(mContext, STATE_LONG_TASK, null);
    }

    /***
     * [Optional] Set the state of the WorkerThread Queue.
     *
     * @param value State value.
     */
    public final void setQueue(final String value) {
        setValue(mContext, STATE_QUEUE, value);
    }

    /***
     * [Optional] Get the state of the WorkerThread Queue.
     *
     * @return State value.
     */
    public final String getQueue() {
        return getValue(mContext, STATE_QUEUE, null);
    }

    /***
     * [Optional] Set the execution state of a running Long task.
     *
     * @param value Execution state.
     */
    public final void setLongProcessState(final int value) {
        setValue(mContext, STATE_PROCESS, value);
    }

    /***
     * [Optional] Get the execution state of a running Long task.
     *
     * @return Execution state.
     */
    public final int getLongProcessState() {
        return getValue(mContext, STATE_PROCESS, -1);
    }

    /***
     * Set a value in the preferences file.
     *
     * @param context Android context.
     * @param key Preferences file parameter key.
     * @param value Preference value.
     */
    private static void setValue(final Context context,
            final String key, final String value) {
        SharedPreferences.Editor editor =
            context.getSharedPreferences(PREFS_FILE, 0).edit();
        editor.putString(key, value);
        if (!editor.commit()) {
            throw new NullPointerException(
                    "MainApplication.setValue() Failed to set key[" + key
                            + "] with value[" + value + "]");
        }
    }

    /***
     * Get a value from the preferences file.
     *
     * @param context Android context.
     * @param key Preferences file parameter key.
     * @param defaultValue Preference value.
     * @return Value as a String.
     */
    private static String getValue(final Context context, final String key,
            final String defaultValue) {
        return context.getSharedPreferences(PREFS_FILE, 0).getString(key,
                defaultValue);
    }

    /***
     * Set a value in the preferences file.
     *
     * @param context Android context.
     * @param key Preferences file parameter key.
     * @param value Preference value.
     */
    private static void setValue(final Context context, final String key,
            final int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                PREFS_FILE, 0).edit();
        editor.putInt(key, value);
        if (!editor.commit()) {
            throw new NullPointerException(
                    "MainApplication.setValue() Failed to set key[" + key
                            + "] with value[" + value + "]");
        }
    }

    /***
     * Get a value from the preferences file.
     *
     * @param context Android context.
     * @param key Preferences file parameter key.
     * @param defaultValue Preference value.
     * @return Value as a String.
     */
    private static int getValue(final Context context, final String key,
            final int defaultValue) {
        return context.getSharedPreferences(PREFS_FILE, 0).getInt(key,
                defaultValue);
    }
}
