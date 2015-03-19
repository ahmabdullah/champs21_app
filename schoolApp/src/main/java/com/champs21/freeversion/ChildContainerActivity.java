package com.champs21.freeversion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.champs21.schoolapp.LoginActivity;
import com.champs21.schoolapp.R;
import com.champs21.schoolapp.SocialBaseActivity;
import com.champs21.schoolapp.adapters.CropOptionAdapter;
import com.champs21.schoolapp.adapters.DrawerExpandableListViewAdapter;
import com.champs21.schoolapp.fragments.AlbumStorageDirFactory;
import com.champs21.schoolapp.fragments.BaseAlbumDirFactory;
import com.champs21.schoolapp.fragments.FroyoAlbumDirFactory;
import com.champs21.schoolapp.model.CropOption;
import com.champs21.schoolapp.model.DrawerChildBase;
import com.champs21.schoolapp.model.DrawerChildMenu;
import com.champs21.schoolapp.model.DrawerChildSettings;
import com.champs21.schoolapp.model.DrawerGroup;
import com.champs21.schoolapp.model.User;
import com.champs21.schoolapp.model.WrapAllData;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.AppUtility;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.SchoolApp;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.utils.UserHelper;
import com.champs21.schoolapp.viewhelpers.CustomRhombusIcon;
import com.champs21.schoolapp.viewhelpers.UIHelper;
import com.google.gson.JsonArray;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class ChildContainerActivity extends SocialBaseActivity implements
		OnQueryTextListener, OnClickListener, SearchView.OnCloseListener {

    
	private static final String TAG = ChildContainerActivity.class.getSimpleName();
	private ActionBar actionBar;
	public ImageView homeBtn,logo;

	@Override
	protected void onResume() {
		super.onResume();
		invalidateOptionsMenu();
	}

	
	
	
	@Override
	public void setContentView(final int layoutResID) {
		FrameLayout layout = (FrameLayout) getLayoutInflater().inflate(
				R.layout.activity_child_container_layout, null); // Your base layout here
		
		getLayoutInflater().inflate(layoutResID, layout, true); // Setting
																	// the
																	// content
																	// of layout
																	// your
																	// provided
																	// to the
																	// act_content
																	// frame
		super.setContentView(layout);
		doBaseTask();
		// here you can get your drawer buttons and define how they should
		// behave and what must they do, so you won't be needing to repeat it in
		// every activity class
	}


	private void doBaseTask() {
		setUpActionBar();
		
	}

	
	private void setUpActionBar(){
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(false);
		View cView = getLayoutInflater().inflate(R.layout.actionbar_view, null);
		homeBtn = (ImageView) cView.findViewById(R.id.back_btn_home);
		homeBtn.setOnClickListener(this);
		homeBtn.setVisibility(View.VISIBLE);
		logo = (ImageView) cView.findViewById(R.id.logo);
		logo.setVisibility(View.GONE);
		actionBar.setCustomView(cView);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		

	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionbar_menu_child, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		

		return super.onOptionsItemSelected(item);
		
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		

		return super.onPrepareOptionsMenu(menu);

	}

	
	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.back_btn_home:
			finish();
			break;
		default:
			break;
		}
		super.onClick(view);
	}




	@Override
	public void onAuthenticationStart() {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void onAuthenticationSuccessful() {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void onAuthenticationFailed(String msg) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void onPaswordChanged() {
		// TODO Auto-generated method stub
		
	}




	@Override
	public boolean onClose() {
		// TODO Auto-generated method stub
		return false;
	}




	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		return false;
	}




	@Override
	public boolean onQueryTextSubmit(String query) {
		// TODO Auto-generated method stub
		return false;
	}



}