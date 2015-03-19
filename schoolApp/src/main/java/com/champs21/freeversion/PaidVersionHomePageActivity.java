package com.champs21.freeversion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.client.HttpResponseException;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.SupportV4App;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.champs21.schoolapp.LoginActivity;
import com.champs21.schoolapp.R;
import com.champs21.schoolapp.model.FreeVersionPost;
import com.champs21.schoolapp.model.FreeVersionPost.Authors;
import com.champs21.schoolapp.model.FreeVersionPost.Categories;
import com.champs21.schoolapp.model.FreeVersionPost.Keywords;
import com.champs21.schoolapp.model.FreeVersionPost.Tags;
import com.champs21.schoolapp.model.WrapAllData;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.ReminderHelper;
import com.champs21.schoolapp.utils.SharedPreferencesHelper;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.viewhelpers.CustomButtonFree;
import com.champs21.schoolapp.viewhelpers.PagerSlidingTabStrip;
import com.champs21.schoolapp.viewhelpers.UIHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

@SuppressLint("NewApi")
public class PaidVersionHomePageActivity extends HomeBaseActivity{

	private static final String LOG_TAG = PaidVersionHomePageActivity.class
			.getSimpleName();

	private static final int[] POW_2 = { 1, 2, 4, 8, 16, 32, 64, 128, 256, 512,
			1024, 2048, 4096 };
	// 16 bits available at all
	private static final int CHAIN_BITS_FOR_INDEX = 3; // adjustable constant,
														// use value 3 or 4
	private static final int CHAIN_BITS_COUNT = 9; // adjustable constant, use
													// value 9 or 12
	private static final int CHAIN_INDEX_MASK = ~(0x80000000 >> (31 - CHAIN_BITS_FOR_INDEX));
	// max allowed depth of fragments
	private static final int CHAIN_MAX_DEPTH = CHAIN_BITS_COUNT
			/ CHAIN_BITS_FOR_INDEX;
	// bits for external usage
	private static final int REQUEST_CODE_EXT_BITS = 16 - CHAIN_BITS_COUNT;
	private static final int REQUEST_CODE_MASK = ~(0x80000000 >> (31 - REQUEST_CODE_EXT_BITS));
	// we have to add +1 for every index
	// because we could not determine 0 index at all
	private static final int FRAGMENT_MAX_COUNT = POW_2[CHAIN_BITS_FOR_INDEX] - 1;

	public void startActivityFromFragment(Fragment fragment, Intent intent,
			int requestCode) {
		if ((requestCode & (~REQUEST_CODE_MASK)) != 0) {
			Log.w(LOG_TAG, "Can only use lower " + REQUEST_CODE_EXT_BITS
					+ " bits for requestCode, int value in range 1.."
					+ (POW_2[REQUEST_CODE_EXT_BITS] - 1));
			super.startActivityFromFragment(fragment, intent, requestCode);
			return;
		}

		int chain = 0;
		int depth = 0;

		Fragment node = fragment;

		do {
			if (depth > CHAIN_MAX_DEPTH) {
				throw new IllegalStateException(
						"Too deep structure of fragments, max "
								+ CHAIN_MAX_DEPTH);
			}

			int index = SupportV4App.fragmentIndex(node);
			if (index < 0) {
				throw new IllegalStateException(
						"Fragment is out of FragmentManager: " + node);
			}

			if (index >= FRAGMENT_MAX_COUNT) {
				throw new IllegalStateException(
						"Too many fragments inside (max " + FRAGMENT_MAX_COUNT
								+ "): " + node.getParentFragment());
			}

			chain = (chain << CHAIN_BITS_FOR_INDEX) + (index + 1);
			node = node.getParentFragment();
			depth += 1;
		} while (node != null);

		int newCode = (chain << REQUEST_CODE_EXT_BITS)
				+ (requestCode & REQUEST_CODE_MASK);

		super.startActivityForResult(intent, newCode);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((requestCode & 0xffff0000) != 0) {
			Log.w(LOG_TAG,
					"Activity result requestCode does not correspond restrictions: 0x"
							+ Integer.toHexString(requestCode));
			super.onActivityResult(requestCode, resultCode, data);
			return;
		}

		SupportV4App.activityFragmentsNoteStateNotSaved(this);

		int chain = requestCode >>> REQUEST_CODE_EXT_BITS;
		if (chain != 0) {
			ArrayList<Fragment> active = SupportV4App
					.activityFragmentsActive(this);
			Fragment fragment;

			do {
				int index = (chain & CHAIN_INDEX_MASK) - 1;
				if (active == null || index < 0 || index >= active.size()) {
					Log.e(LOG_TAG,
							"Activity result fragment chain out of range: 0x"
									+ Integer.toHexString(requestCode));
					super.onActivityResult(requestCode, resultCode, data);
					return;
				}

				fragment = active.get(index);
				if (fragment == null) {
					break;
				}

				active = SupportV4App
						.fragmentChildFragmentManagerActive(fragment);
				chain = chain >>> CHAIN_BITS_FOR_INDEX;
			} while (chain != 0);

			if (fragment != null) {
				fragment.onActivityResult(requestCode & REQUEST_CODE_MASK,
						resultCode, data);
			} else {
				Log.e(LOG_TAG,
						"Activity result no fragment exists for chain: 0x"
								+ Integer.toHexString(requestCode));
			}
		}
		else
		{
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
    
    




	
	
	private void navigateTo(int position) {
		//Log.v(TAG, "List View Item: " + position);

		switch (position) {
		case 0:
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.pager_frame,
							PaidVersionHomeFragment.newInstance(),
							PaidVersionHomeFragment.TAG).commit();
			break;
		case 1:
			break;
		}
	}
	

   
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_homepage_paidversion);
		navigateTo(0);
          

        context = getApplicationContext();
        ReminderHelper.getInstance().constructReminderFromSharedPreference();
       
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    


    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

   

   
	

}

	
	
	



