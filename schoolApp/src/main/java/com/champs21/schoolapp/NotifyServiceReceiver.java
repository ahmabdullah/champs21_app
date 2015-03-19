package com.champs21.schoolapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotifyServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		Log.e("Broasjgdkashdk", "Yahooooooooooo");
		
		
		Intent service1 = new Intent(arg0, NotifyService.class);
		arg0.startService(service1);
		
		
		
	}

}
