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
package com.zedray.framework.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.zedray.framework.R;
import com.zedray.framework.ui.AllTasks;

/***
 * Utility for showing a custom Android Notification.
 */
public final class NotificationUtils {

    /** Notification ID. **/
    private static final int NOTIFICATOIN_ID = 1;
    /** Progress bar maximum value. **/
    private static final int PROGRESS_BAR_MAX = 100;

    /***
     * Private constructor to prevent instantiation.
     */
    private NotificationUtils() {
        // Do nothing.
    }

    /***
     * Display a progress bar notification.
     *
     * @param context Application Context.
     * @param progress Percent progress.
     */
    public static void notifyUserOfProgress(final Context context,
            final int progress) {
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (progress != -1) {
            Notification notification = new Notification(R.drawable.icon,
                    "Progress " + progress + "%", System.currentTimeMillis());
            notification.flags = Notification.FLAG_ONGOING_EVENT
                    & Notification.FLAG_NO_CLEAR;

            Intent notificationIntent = new Intent(context, AllTasks.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    notificationIntent, 0);
            notification.contentIntent = contentIntent;

            RemoteViews contentView = new RemoteViews(context.getPackageName(),
                    R.layout.custom_notification_layout);
            contentView.setProgressBar(R.id.progressbar, PROGRESS_BAR_MAX,
                    progress, false);
            contentView.setTextViewText(R.id.text,
                    "Running Long Task - Progress " + progress + "%");
            notification.contentView = contentView;

            mNotificationManager.notify(NOTIFICATOIN_ID, notification);

        } else {
            mNotificationManager.cancelAll();
        }
    }
}
