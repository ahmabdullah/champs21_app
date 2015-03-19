package com.champs21.schoolapp.utils;

import java.util.HashMap;
import java.util.Map;

import com.champs21.schoolapp.model.Reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		ReminderHelper.getInstance().constructReminderFromSharedPreference();
		for (HashMap.Entry<String, Reminder> entry : ReminderHelper.getInstance().reminder_map.entrySet()) {
		    String key = entry.getKey();
		    Reminder rm = entry.getValue();
		    ReminderHelper.getInstance().setReminder(key, rm.getReminderTitle(), rm.getReminderDescription(), rm.getReminderTime(), context);
		}
	}

}
