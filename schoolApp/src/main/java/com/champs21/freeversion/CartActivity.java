package com.champs21.freeversion;

import java.text.ParseException;

import com.champs21.schoolapp.R;
import com.champs21.schoolapp.R.layout;
import com.champs21.schoolapp.R.menu;
import com.champs21.schoolapp.model.AttendenceEvents;
import com.champs21.schoolapp.model.CmartProductAttribute;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.RequestKeyHelper;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.utils.UserHelper;
import com.champs21.schoolapp.utils.UserHelper.UserTypeEnum;
import com.champs21.schoolapp.viewhelpers.UIHelper;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class CartActivity extends Activity {

	private UIHelper uiHelper;
	private UserHelper userHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cart);
		init();
		getCartItems();
		//fetchAttendenceData();
	}

	private void init() {
		uiHelper=new UIHelper(CartActivity.this);
		userHelper=new UserHelper(CartActivity.this);
	}

	private void getCartItems() {
		String url = URLHelper.BASE_URL_CMART + "cart";
		RequestParams params=new RequestParams();
		
		
		AppRestClient.postCmart(url, params, cartDataHandler);
	}
	
	
	AsyncHttpResponseHandler cartDataHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			if (uiHelper.isDialogActive()) {
				uiHelper.dismissLoadingDialog();

			}
			uiHelper.showMessage(arg1);
		}

		@Override
		public void onStart() {
			uiHelper.showLoadingDialog(getString(R.string.loading_text));
		}

		@Override
		public void onSuccess(int arg0, String responseString) {
			Log.e("INDEX", responseString);
			Log.e("here comes parsing",
					"---------------------------------------------");
			
			Log.e("here end parsing",
					"---------------------------------------------");
			if (uiHelper.isDialogActive()) {
				uiHelper.dismissLoadingDialog();
			}
		}

	};

private void fetchAttendenceData() {
		
		RequestParams params=new RequestParams();
		//Log.e("Secret", app.getUserSecret());
		
		params.put(RequestKeyHelper.USER_SECRET, UserHelper.getUserSecret());
		
		Log.e("params attendance", params.toString());
		
		AppRestClient.post(URLHelper.URL_GET_ATTENDENCE_EVENTS, params,getAttendenceEventsHandler);
		
	}
	
	AsyncHttpResponseHandler getAttendenceEventsHandler=new AsyncHttpResponseHandler()
	{

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			super.onFailure(arg0, arg1);
			//uiHelper.showMessage(arg1);
			uiHelper.dismissLoadingDialog();
			Log.e("Error", arg1);
		}

		@Override
		public void onStart() {
			super.onStart();
			//initializeCountValues();
			if(!uiHelper.isDialogActive())
				uiHelper.showLoadingDialog(getString(R.string.loading_text));
			else
				uiHelper.updateLoadingDialog(getString(R.string.loading_text));
			
		}

		@Override
		public void onSuccess(int arg0, String responseString) {
			super.onSuccess(arg0, responseString);
			uiHelper.dismissLoadingDialog();
			Log.e("Response", responseString);
			
			
		}
		
	};
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cart, menu);
		return true;
	}

}
