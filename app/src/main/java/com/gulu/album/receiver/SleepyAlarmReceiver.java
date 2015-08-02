package com.gulu.album.receiver;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.gulu.album.service.ServantService;

public class SleepyAlarmReceiver extends WakefulBroadcastReceiver {

	private AlarmManager mAlarmManager;

	private PendingIntent mSleepyIntent;

	@Override
	public void onReceive(Context context, Intent intent) {
		ComponentName component = new ComponentName(context, ServantService.class);
		intent.setComponent(component);
		intent.setAction(ServantService.ACTION_SHUT_DOWN_DEVICE);
		startWakefulService(context, intent);
	}

	public void setAlarm(Context context) {
		mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		Intent sleepy = new Intent(context, SleepyAlarmReceiver.class);
		// put the song th play
		// sleepy.putExtra(name, value)

		mSleepyIntent = PendingIntent.getBroadcast(context, 0, sleepy, 0);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 21);
		calendar.set(Calendar.MINUTE, 30);

		mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, mSleepyIntent);

		PackageManager pm = context.getPackageManager();
		ComponentName cn = new ComponentName(context, BootCompleteReceiver.class);
		pm.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
	}

	public void cancelAlarm(Context context) {
		if (mAlarmManager != null) {
			mAlarmManager.cancel(mSleepyIntent);
		}

		PackageManager pm = context.getPackageManager();
		ComponentName cn = new ComponentName(context, BootCompleteReceiver.class);
		pm.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

	}
}
