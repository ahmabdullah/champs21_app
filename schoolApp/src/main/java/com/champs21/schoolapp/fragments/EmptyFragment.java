/**
 * 
 */
package com.champs21.schoolapp.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.champs21.schoolapp.R;
import com.champs21.schoolapp.adapters.AcademicCalendarListAdapter;
import com.champs21.schoolapp.model.AcademicCalendarDataItem;
import com.champs21.schoolapp.model.UserAuthListener;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.AppUtility;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.RequestKeyHelper;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.utils.UserHelper;
import com.champs21.schoolapp.viewhelpers.UIHelper;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 *
 */
public class EmptyFragment extends UserVisibleHintFragment{
	
	private static final String TAG="Empty Fragment";
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}

	

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		return inflater.inflate(R.layout.fragment_academic_calendar_exam, container, false);
	}
	
	

	@Override
	protected void onVisible() {
		Log.e(TAG, "Visible hoise ar Content loaded");
	}

	@Override
	protected void onInvisible() {
		// TODO Auto-generated method stub
		
	}
}