package com.zedray.framework.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.zedray.framework.R;
import com.zedray.framework.ui.AllTasks;

public class NotificationUtils {

	/** Notification ID. **/
	private static final int NOTIFICATOIN_ID = 1;

	/***
	 * Display a progress bar notification.
	 *
	 * @param context Application Context.
	 * @param progress Percent progress.
	 */
	public static void notifyUserOfProgress(final Context context,
			final int progress) {
		NotificationManager mNotificationManager =
			(NotificationManager) context.getSystemService(
					Context.NOTIFICATION_SERVICE);

		if (progress != -1) {
			Notification notification = new Notification(R.drawable.icon,
					"Progress " + progress + "%", System.currentTimeMillis());
			notification.flags = Notification.FLAG_ONGOING_EVENT & Notification.FLAG_NO_CLEAR;

			Intent notificationIntent = new Intent(context, AllTasks.class);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
			notification.contentIntent = contentIntent;

			RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.custom_notification_layout);
			contentView.setProgressBar(R.id.progressbar, 100, progress, false);
			contentView.setTextViewText(R.id.text, "Running Long Task - Progress " + progress + "%");
			notification.contentView = contentView;

			mNotificationManager.notify(NOTIFICATOIN_ID, notification);

		} else {
			mNotificationManager.cancelAll();
		}
	}
}