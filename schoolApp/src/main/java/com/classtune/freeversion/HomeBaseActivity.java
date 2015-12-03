package com.classtune.freeversion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
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
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.classtune.schoolapp.R;
import com.classtune.schoolapp.SocialBaseActivity;
import com.classtune.schoolapp.adapters.CropOptionAdapter;
import com.classtune.schoolapp.fragments.AlbumStorageDirFactory;
import com.classtune.schoolapp.fragments.BaseAlbumDirFactory;
import com.classtune.schoolapp.fragments.FroyoAlbumDirFactory;
import com.classtune.schoolapp.model.CropOption;
import com.classtune.schoolapp.model.User;
import com.classtune.schoolapp.model.WrapAllData;
import com.classtune.schoolapp.model.Wrapper;
import com.classtune.schoolapp.networking.AppRestClient;
import com.classtune.schoolapp.utils.AppConstant;
import com.classtune.schoolapp.utils.AppUtility;
import com.classtune.schoolapp.utils.GsonParser;
import com.classtune.schoolapp.utils.SPKeyHelper;
import com.classtune.schoolapp.utils.SchoolApp;
import com.classtune.schoolapp.utils.URLHelper;
import com.classtune.schoolapp.utils.UserHelper;
import com.classtune.schoolapp.viewhelpers.CustomRhombusIcon;
import com.classtune.schoolapp.viewhelpers.ExpandablePanel;
import com.classtune.schoolapp.viewhelpers.UIHelper;
import com.google.gson.JsonArray;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class HomeBaseActivity extends SocialBaseActivity implements
		OnQueryTextListener, OnClickListener, SearchView.OnCloseListener {

	private static final String TAG = HomeBaseActivity.class.getSimpleName();
	private boolean mState = false;
	private DrawerLayout mDrawerLayout;
	private LinearLayout mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mDrawerItmes;
	private ActionBar actionBar;
	ScheduleTask r = null;
	ReschedulableTimer rescheduleTimer = null;
	private UIHelper uiHelper;
	private SearchView mSearchView;
	LinearLayout layoutSearch;
	private PopupWindow popup;
	private int keyHeight = 0;
	private View popupView;
	private WrapAllData dataWrap;
	private static int positionPager = 0;
	private ImageView aboutUsBtn;
	private ImageView profileEditBtn;
	private ExpandablePanel accountSettingPanel;
	private RelativeLayout preferenceSettingsPanel, editProfileSettingsPanel,
			logoutSettingsPanel;
	private LinearLayout basicInfoPanel;
	private View divider1, divider2, divider3, divider7;
	private User user;
	private ImageView profilePic;
	private RelativeLayout logoutLayout;
	private EditText etCurrentPass;
	private ImageView imgEditCurrentPass;
	private ProgressBar pb;
	private EditText etNewPass;
	private ImageView imgEditNewPass;
	private EditText etRePass;
	private ImageView imgEditRePass;
	private TextView userNameTextView;
	private TextView userTypeTextView;
	private RelativeLayout aboutUsPanel;
	private RelativeLayout termsAndPolicyPanel;
	private RelativeLayout assessmentScoresPanel;
	private TextView passwordEmainTextView;
	private ImageButton saveButton;
	public ImageView homeBtn,logo;

	// for image file chooser
	private final int REQUEST_CODE_CAMERA = 110;
	private final int REQUEST_CODE_GELLERY = 111;
	private final int REQUEST_CODE_CROP = 112;
	private static File schoolDirectory = null;
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	private Uri uri = null;
	private String mCurrentPhotoPath;

	private String selectedImagePath = "";
	private String selectedImagePath2 = "";

	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	
	private boolean isHome=true;

	private void updateUI() {
		if (UserHelper.isLoggedIn()) {
			mState = true;
			user = userHelper.getUser();

			ImageLoader.getInstance().displayImage(user.getProfilePicsUrl(),
					profilePic, AppUtility.getOptionForUserImage(),
					new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String imageUri, View view) {
							pb.setVisibility(View.VISIBLE);

						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							// spinner.setVisibility(View.GONE);
							pb.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							pb.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {

						}
					});
			accountSettingPanel
					.setOnExpandListener(new ExpandablePanel.OnExpandListener() {
						public void onCollapse(View handle, View content) {
							RelativeLayout view = (RelativeLayout) handle;
							ImageView btn = (ImageView) view
									.findViewById(R.id.arrow_btn);
							btn.setImageResource(R.drawable.arrow_down);
						}

						public void onExpand(View handle, View content) {
							RelativeLayout view = (RelativeLayout) handle;
							ImageView btn = (ImageView) view
									.findViewById(R.id.arrow_btn);
							btn.setImageResource(R.drawable.arrow_up);
						}
					});

			// SETUP THE USER DETAILS
			Log.e("USER EMAI@@@@@@@@@@@@@@@@@@@", userHelper.getUser()
					.getEmail());

			accountSettingPanel.setVisibility(View.VISIBLE);
			preferenceSettingsPanel.setVisibility(View.VISIBLE);
			editProfileSettingsPanel.setVisibility(View.VISIBLE);
			logoutSettingsPanel.setVisibility(View.VISIBLE);
			basicInfoPanel.setVisibility(View.VISIBLE);
			divider1.setVisibility(View.VISIBLE);
			divider2.setVisibility(View.VISIBLE);
			divider3.setVisibility(View.VISIBLE);
			divider7.setVisibility(View.VISIBLE);
			assessmentScoresPanel.setVisibility(View.VISIBLE);

			if (!TextUtils.isEmpty(userHelper.getUser().getNickName())) {
				switch (Integer.parseInt(userHelper.getUser().getNickName())) {
				case 1:
					setUserName(userHelper.getUser().getFirstName());
					break;
				case 2:
					setUserName(userHelper.getUser().getMiddleName());
					break;
				case 3:
					setUserName(userHelper.getUser().getLastName());
					break;
				default:
					break;
				}
			} else {
				userNameTextView.setText(userHelper.getUser().getEmail());
			}

			passwordEmainTextView.setText(userHelper.getUser().getEmail());
			userTypeTextView.setText(userHelper.getUser().getType().toString());

		} else {
			accountSettingPanel.setVisibility(View.GONE);
			preferenceSettingsPanel.setVisibility(View.GONE);
			editProfileSettingsPanel.setVisibility(View.GONE);
			logoutSettingsPanel.setVisibility(View.GONE);
			basicInfoPanel.setVisibility(View.GONE);
			divider1.setVisibility(View.GONE);
			divider2.setVisibility(View.GONE);
			divider3.setVisibility(View.GONE);
			divider7.setVisibility(View.GONE);
			assessmentScoresPanel.setVisibility(View.GONE);

		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		schoolDirectory = new File(this.getFilesDir().getPath() + "/champs21");
		schoolDirectory.mkdirs(); // create folders where write files

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
		updateUI();
		invalidateOptionsMenu();
	}

	public void setUserName(String name) {
		if (!TextUtils.isEmpty(name))
			userNameTextView.setText(name);
		else
			userNameTextView.setText(userHelper.getUser().getEmail());
	}

	/*public void setIsHome(boolean isHome)
	{
		this.isHome=isHome;
	}*/
	
	@Override
	public void setContentView(final int layoutResID) {
		DrawerLayout layout = (DrawerLayout) getLayoutInflater().inflate(
				R.layout.activity_base_layout, null); // Your base layout here
		FrameLayout actContent = (FrameLayout) layout
				.findViewById(R.id.content_frame);
		getLayoutInflater().inflate(layoutResID, actContent, true); // Setting
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

	private void refreshToHome() {
		Intent intent = new Intent(this, HomePageFreeVersionNew.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}

	private void doBaseTask() {
		uiHelper = new UIHelper(HomeBaseActivity.this);
		setUpActionBar();
		setUpDialog();
		mTitle = mDrawerTitle = getTitle();/*
		mDrawerItmes = getResources().getStringArray(R.array.drawer_titles);*/
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (LinearLayout) findViewById(R.id.right_drawer);
		userNameTextView = (TextView) mDrawerList.findViewById(R.id.tv_name);
		userTypeTextView = (TextView) mDrawerList.findViewById(R.id.tv_class);
		aboutUsBtn = (ImageView) findViewById(R.id.about_us_btn);
		passwordEmainTextView = (TextView) findViewById(R.id.tv_pass_email);
		accountSettingPanel = (ExpandablePanel) findViewById(R.id.acount_settings_panel);
		preferenceSettingsPanel = (RelativeLayout) findViewById(R.id.pref_setting_panel);
		preferenceSettingsPanel.setOnClickListener(this);
		editProfileSettingsPanel = (RelativeLayout) findViewById(R.id.profile_setting_panel);
		editProfileSettingsPanel.setOnClickListener(this);
		saveButton = (ImageButton) mDrawerList.findViewById(R.id.save_btn);
		saveButton.setOnClickListener(this);
		aboutUsPanel = (RelativeLayout) findViewById(R.id.about_us_panel);
		aboutUsPanel.setOnClickListener(this);
		termsAndPolicyPanel = (RelativeLayout) findViewById(R.id.terms_and_policy_panel);
		termsAndPolicyPanel.setOnClickListener(this);

		assessmentScoresPanel = (RelativeLayout) findViewById(R.id.assessment_score_panel);
		assessmentScoresPanel.setOnClickListener(this);

		logoutSettingsPanel = (RelativeLayout) findViewById(R.id.logout_setting_panel);
		basicInfoPanel = (LinearLayout) findViewById(R.id.basic_info_panel);
		divider1 = (View) findViewById(R.id.divider1);
		divider2 = (View) findViewById(R.id.divider2);
		divider3 = (View) findViewById(R.id.divider3);
		divider7 = (View) findViewById(R.id.divider7);
		profileEditBtn = (ImageView) findViewById(R.id.profile_edit_btn);
		logoutLayout = (RelativeLayout) findViewById(R.id.logout_setting_panel);
		logoutLayout.setOnClickListener(this);
		profileEditBtn.setOnClickListener(this);
		profilePic = (ImageView) findViewById(R.id.profile_image);
		profilePic.setOnClickListener(this);
		pb = (ProgressBar) findViewById(R.id.profile_pics_spinner);
		pb.setVisibility(View.GONE);
		// set a custom shadow that overlays the main content when the drawer
		// oepns
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.END);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		setUpEditProfileView();
	}

	private void setUpDialog() {

		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		popupView = layoutInflater.inflate(R.layout.layout_dialog, null);

		popup = new PopupWindow(popupView,
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutSearch = (LinearLayout) popupView.findViewById(R.id.layoutSearch);

		keyHeight = popupView.getHeight();

		// adjustPopupHeight();
		final View root = getWindow().getDecorView().findViewById(
				android.R.id.content);
		root.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						// TODO Auto-generated method stub
						Rect r = new Rect();
						root.getWindowVisibleDisplayFrame(r);

						int screenHeight = root.getRootView().getHeight();
						int heightDifference = screenHeight
								- (r.bottom - r.top);
						//Log.e("Keyboard Size", "Size: " + heightDifference);
						// Log.e("Keyboard Size popup", "height: " +
						// popup.getHeight());

						// popup.setHeight(heightDifference);

						if (heightDifference > 100) {

							popup.setHeight((int) (screenHeight
									- heightDifference - (int) TypedValue
									.applyDimension(
											TypedValue.COMPLEX_UNIT_DIP, 85,
											getResources().getDisplayMetrics())));
						}

						else {
							popup.setHeight(screenHeight);
						}
					}
				});
	}

	private void adjustPopupHeight() {
		final View root = getWindow().getDecorView().findViewById(
				android.R.id.content);

		root.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						// TODO Auto-generated method stub
						Rect r = new Rect();
						root.getWindowVisibleDisplayFrame(r);

						int screenHeight = root.getRootView().getHeight();
						int heightDifference = screenHeight
								- (r.bottom - r.top);
						//Log.e("Keyboard Size", "Size: " + heightDifference);
						// Log.e("Keyboard Size popup", "height: " +
						// popup.getHeight());

						// popup.setHeight(heightDifference);

						if (heightDifference > 100) {

							WindowManager.LayoutParams params = (WindowManager.LayoutParams) popupView
									.getLayoutParams();
							popupView.getLayoutParams();
							params.height = (int) (screenHeight
									- heightDifference - (int) TypedValue
									.applyDimension(
											TypedValue.COMPLEX_UNIT_DIP, 85,
											getResources().getDisplayMetrics()));
							popupView.setLayoutParams(params);
						}

						else {
							WindowManager.LayoutParams params = (WindowManager.LayoutParams) popupView
									.getLayoutParams();
							popupView.getLayoutParams();
							params.height = screenHeight;
							popupView.setLayoutParams(params);
						}

						// boolean visible = heightDiff > screenHeight / 3;
					}
				});
	}

	private void setUpActionBar(){
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(false);
		View cView = getLayoutInflater().inflate(R.layout.actionbar_view, null);
		homeBtn = (ImageView) cView.findViewById(R.id.back_btn_home);
		homeBtn.setOnClickListener(this);
		logo = (ImageView) cView.findViewById(R.id.logo);
		actionBar.setCustomView(cView);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		

	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionbar_menu, menu);

		// Associate searchable configuration with the SearchView
		/*SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		final SearchView searchView = (SearchView) menu.findItem(
				R.id.action_search).getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		searchView.setLayoutParams(new ActionBar.LayoutParams(Gravity.RIGHT));
		if (mState) {
			menu.getItem(1).setVisible(false);
		}

		int searchPlateId = searchView.getContext().getResources()
				.getIdentifier("android:id/search_plate", null, null);
		View searchPlate = searchView.findViewById(searchPlateId);
		searchPlate.setBackgroundColor(Color.parseColor("#4b5459"));

		// search_close_btn
		int closeBtnId = searchView.getContext().getResources()
				.getIdentifier("android:id/search_close_btn", null, null);
		ImageView closeButton = (ImageView) searchView.findViewById(closeBtnId);
		closeButton.setImageResource(R.drawable.btn_search_close);

		closeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!mSearchView.isIconified()) {
					mSearchView.setIconified(true);

					if (popup != null) {
						popup.dismiss();
						layoutSearch.removeAllViews();
					}
				}
			}
		});

		int id = searchView.getContext().getResources()
				.getIdentifier("android:id/search_src_text", null, null);
		EditText editText = (EditText) searchView.findViewById(id);

		searchManager.setOnCancelListener(new SearchManager.OnCancelListener() {

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
				if (popup != null) {
					popup.dismiss();
				}
				layoutSearch.removeAllViews();

			}
		});

		MenuItem menuItem = menu.findItem(R.id.action_search);
		menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				Log.e("SHOWING_SEARCH", "yes");

				return true;
			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				Log.e("SHOWING_SEARCH", "no");

				if (popup != null) {
					popup.dismiss();
				}
				layoutSearch.removeAllViews();

				return true;
			}
		});*/

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.action_drawer) {
			if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
				mDrawerLayout.closeDrawer(Gravity.RIGHT);
			} else {
				mDrawerLayout.openDrawer(Gravity.RIGHT);
			}
		} /*else if (item.getItemId() == R.id.action_login) {
			startActivity(new Intent(HomeBaseActivity.this, LoginActivity.class));
		}*/
		// Handle your other action bar items...

		return super.onOptionsItemSelected(item);
		/*
		 * if (item != null && item.getItemId() == android.R.id.home) { if
		 * (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
		 * mDrawerLayout.closeDrawer(Gravity.RIGHT); } else {
		 * mDrawerLayout.openDrawer(Gravity.RIGHT); } } return
		 * super.onOptionsItemSelected(item);
		 */
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		/*MenuItem searchViewMenuItem = menu.findItem(R.id.action_search);
		mSearchView = (SearchView) searchViewMenuItem.getActionView();
		int searchImgId = getResources().getIdentifier(
				"android:id/search_button", null, null);
		ImageView v = (ImageView) mSearchView.findViewById(searchImgId);
		v.setImageResource(R.drawable.ic_action_search);
		mSearchView.setOnQueryTextListener(this);
		mSearchView.setOnCloseListener(this);

		int id = mSearchView.getContext().getResources()
				.getIdentifier("android:id/search_src_text", null, null);
		TextView textView = (TextView) mSearchView.findViewById(id);
		textView.setTextColor(Color.WHITE);*/

		return super.onPrepareOptionsMenu(menu);

	}

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub

		Log.e("TEXT_CHANGE_QUERY", "is: " + newText);

		if (rescheduleTimer == null) {
			r = new ScheduleTask();
			rescheduleTimer = new ReschedulableTimer();

			r.setTerm(newText);

			rescheduleTimer.schedule(r, 2 * 1000);

		}

		else {
			r.setTerm(newText);

			rescheduleTimer.reschedule(2 * 1000);
		}

		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		// TODO Auto-generated method stub

		Log.e("SUBMITTED_QUERY", "is: " + query);

		return false;
	}

	private void initApiCall(String term) {

		RequestParams params = new RequestParams();
		params.put("term", term);
		AppRestClient.post(URLHelper.URL_FREE_VERSION_SEARCH, params,
				searchHandler);
	}

	private AsyncHttpResponseHandler searchHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			uiHelper.showMessage(arg1);
			uiHelper.dismissLoadingDialog();
		};

		@Override
		public void onStart() {
			if (uiHelper != null)
				uiHelper.showLoadingDialog("Please wait...");
		};

		@Override
		public void onSuccess(String responseString) {
			// Log.e("FREE_HOME", "data: "+responseString);
			layoutSearch.removeAllViews();

			if (popup != null)
				popup.dismiss();

			if (mSearchView.isIconified()) {
				if (popup != null) {
					popup.dismiss();
					layoutSearch.removeAllViews();
				}
			}

			if (uiHelper != null)
				uiHelper.dismissLoadingDialog();

			Wrapper modelContainer = GsonParser.getInstance()
					.parseServerResponse(responseString);

			if (modelContainer.getStatus().getCode() == 200) {

				Log.e("SEARCH", "data: "
						+ modelContainer.getData().getAsJsonObject().toString());

				JsonArray arrayPost = modelContainer.getData()
						.getAsJsonObject().get("post").getAsJsonArray();

				dataWrap = new WrapAllData(arrayPost.toString());

				for (int i = 0; i < dataWrap.getListPost().size(); i++) {

					createSearchRow(i, dataWrap);
				}

				if (popup != null) {
					popup.showAsDropDown(mSearchView);
					adjustPopupHeight();
				}
			}

			else {

			}

		};

	};

	public class ScheduleTask implements Runnable {

		private String term = "";

		public void run() {
			// Do schecule task
			Log.e("TIMER", "run finished called");

			if (!TextUtils.isEmpty(getTerm()))
				initApiCall(getTerm());

		}

		public String getTerm() {
			return term;
		}

		public void setTerm(String term) {
			this.term = term;
		}

		public ScheduleTask() {

		}

	}

	class ReschedulableTimer extends Timer {
		private Runnable task;
		private TimerTask timerTask;

		public void schedule(Runnable runnable, long delay) {
			task = runnable;
			timerTask = new TimerTask() {
				public void run() {
					task.run();
				}
			};

			this.schedule(timerTask, delay);
		}

		public void reschedule(long delay) {

			timerTask.cancel();
			timerTask = new TimerTask() {
				public void run() {
					task.run();
				}
			};
			this.schedule(timerTask, delay);
		}
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.profile_setting_panel:
			mDrawerLayout.closeDrawer(Gravity.RIGHT);
			Intent i = new Intent(HomeBaseActivity.this,
					CompleteProfileActivityContainer.class);
			i.putExtra(SPKeyHelper.USER_TYPE, user.getType().ordinal());
			startActivity(i);
			break;
		case R.id.logout_setting_panel:
			mDrawerLayout.closeDrawer(Gravity.RIGHT);
			UserHelper.setLoggedIn(false);
			UserHelper.saveIsJoinedSchool(false);
			Intent intent = new Intent(HomeBaseActivity.this,
					HomePageFreeVersion.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION
					| Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);

			finish();
			overridePendingTransition(0, 0);

			startActivity(intent);
			overridePendingTransition(0, 0);

			break;
		case R.id.pref_setting_panel:
			mDrawerLayout.closeDrawer(Gravity.RIGHT);
			Intent prefIntent = new Intent(HomeBaseActivity.this,
					PreferenceSettingsActivity.class);
			startActivity(prefIntent);

			break;

		case R.id.about_us_panel:
			mDrawerLayout.closeDrawer(Gravity.RIGHT);
			Intent aboutUsIntent = new Intent(HomeBaseActivity.this,
					InfoActivity.class);
			aboutUsIntent.putExtra("title", "About Us");
			aboutUsIntent.putExtra("description",
					getResources().getString(R.string.about_use_text));
			startActivity(aboutUsIntent);

			break;
		case R.id.terms_and_policy_panel:
			mDrawerLayout.closeDrawer(Gravity.RIGHT);
			Intent termsIntent = new Intent(HomeBaseActivity.this,
					InfoActivity.class);
			termsIntent.putExtra("title", "Terms & Policy");
			termsIntent.putExtra("description",
					getResources().getString(R.string.termsandpolicy_text));
			startActivity(termsIntent);
			break;

		case R.id.assessment_score_panel:
			mDrawerLayout.closeDrawer(Gravity.RIGHT);
			Intent assessmentScoreIntent = new Intent(HomeBaseActivity.this,
					AssesmentHistoryActivity.class);
			startActivity(assessmentScoreIntent);
			break;

		case R.id.save_btn:
			if (TextUtils.isEmpty(etCurrentPass.getText().toString().trim())) {
				uiHelper.showErrorDialog("Password Cannot be empty!");
			} else if (TextUtils.isEmpty(etNewPass.getText().toString().trim())) {
				uiHelper.showErrorDialog("Password Cannot be empty!");
			} else if (TextUtils.isEmpty(etRePass.getText().toString().trim())) {
				uiHelper.showErrorDialog("Password Cannot be empty!");
			} else {
				if (!etNewPass.getText().toString().trim()
						.equals(etRePass.getText().toString().trim())) {
					uiHelper.showErrorDialog("Password doesn't match!");
				} else {
					userHelper.updatePassword(userHelper.getUser(), etNewPass
							.getText().toString(), etCurrentPass.getText()
							.toString().trim());
				}
			}

			break;
		case R.id.profile_image:

			// gallery/camera chooser here

			showPicChooserDialog();

			break;
		case R.id.back_btn_home:
			finish();
			break;
		default:
			break;
		}
		super.onClick(view);
	}

	private void showPicChooserDialog() {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				HomeBaseActivity.this);

		alertDialogBuilder
				.setMessage("Change Profile Picture")
				.setCancelable(false)
				.setPositiveButton("Gallery",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(final DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();

								/*
								 * Intent intent = new Intent();
								 * intent.setType("image/*");
								 * intent.setAction(Intent.ACTION_GET_CONTENT);
								 * startActivityForResult
								 * (Intent.createChooser(intent,
								 * "Select Picture"), 1);
								 */

								dispatchOpenGelleryApp();

							}
						})
				.setNegativeButton("Camera",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(final DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();

								dispatchTakePictureIntent();

								/*
								 * Intent takePicture = new Intent(
								 * MediaStore.ACTION_IMAGE_CAPTURE);
								 * startActivityForResult(takePicture, 0);
								 */
							}
						});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.setCancelable(true);

		alertDialog.show();

	}

	public void dispatchOpenGelleryApp() {

		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),
				REQUEST_CODE_GELLERY);

	}

	public void dispatchTakePictureIntent() {

		PackageManager pm = this.getPackageManager();
		if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false) {
			Toast.makeText(this, "Camera Nai", Toast.LENGTH_SHORT).show();
			return;
		}

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File f = null;

		try {
			f = setUpPhotoFile();
			mCurrentPhotoPath = f.getAbsolutePath();
			takePictureIntent
					.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

		} catch (IOException e) {
			e.printStackTrace();
			f = null;
			mCurrentPhotoPath = null;
		}

		startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);
	}

	private File setUpPhotoFile() throws IOException {

		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();
		// Uri.fromFile(f);
		return f;
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX,
				albumF);
		return imageF;
	}

	private File getAlbumDir() {
		File storageDir = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			storageDir = mAlbumStorageDirFactory
					.getAlbumStorageDir(getAlbumName());

			if (storageDir != null) {
				if (!storageDir.mkdirs()) {
					if (!storageDir.exists()) {
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}

		} else {
			Log.v(getString(R.string.app_name),
					"External storage is not mounted READ/WRITE.");
		}

		return storageDir;
	}

	private String getAlbumName() {
		return getString(R.string.album_name);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_CAMERA:
			if (resultCode == this.RESULT_OK) {
				// Log.e("addAdvertiseController.mCurrentPhotoPath",""+addAdvertiseController.mCurrentPhotoPath);
				dispatchCropIntent(Uri.fromFile(new File(mCurrentPhotoPath)));
			}
			if (resultCode == this.RESULT_CANCELED) {
				return;
			}
			break;
		case REQUEST_CODE_GELLERY:
			if (resultCode == this.RESULT_OK) {
				// addAdvertiseController.mCurrentPhotoPath=getFilePath(data.getData());
				// Log.e("addAdvertiseController.mCurrentPhotoPath2",""+addAdvertiseController.mCurrentPhotoPath);

				dispatchCropIntent(data.getData());
			}
			if (resultCode == this.RESULT_CANCELED) {
				return;
			}
			break;

		case REQUEST_CODE_CROP:
			if (resultCode == this.RESULT_OK) {
				// Log.e("addAdvertiseController.mCurrentPhotoPath3",""+addAdvertiseController.mCurrentPhotoPath);
				File file = new File(mCurrentPhotoPath);
				Log.e("Normal file size:", "Image size before compress:"
						+ (file.length() / 1024) + "");

				handleBigCameraPhoto(false,
						Uri.fromFile(new File(mCurrentPhotoPath)));
				// }
			}

			if (resultCode == this.RESULT_CANCELED) {
				return;
			}
			break;

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void dispatchCropIntent(Uri uriParam) {

		uri = uriParam;
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");

		List<ResolveInfo> list = this.getPackageManager()
				.queryIntentActivities(intent, 0);

		int size = list.size();
		if (size == 0) {
			// Toast.makeText(this, "Can not find image crop app",
			// Toast.LENGTH_SHORT).show();
			handleBigCameraPhoto(true, uriParam);
			return;
		} else {
			intent.setData(uri);
			int height = AppUtility.getImageViewerImageHeight(this);
			intent.putExtra("outputX", 600);
			intent.putExtra("outputY", 600);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", false);
			intent.putExtra("noFaceDetection", true);

			File f = null;
			try {
				f = setUpPhotoFile();
				mCurrentPhotoPath = f.getAbsolutePath();

			} catch (IOException e) {
				e.printStackTrace();
				f = null;
				mCurrentPhotoPath = null;
			}
			intent.putExtra("output", Uri.fromFile(f));

			if (size == 1) {
				Intent i = new Intent(intent);
				ResolveInfo res = list.get(0);

				i.setComponent(new ComponentName(res.activityInfo.packageName,
						res.activityInfo.name));

				startActivityForResult(i, REQUEST_CODE_CROP);
			} else {
				for (ResolveInfo res : list) {
					final CropOption co = new CropOption();

					co.title = this.getPackageManager().getApplicationLabel(
							res.activityInfo.applicationInfo);
					co.icon = this.getPackageManager().getApplicationIcon(
							res.activityInfo.applicationInfo);
					co.appIntent = new Intent(intent);

					co.appIntent
							.setComponent(new ComponentName(
									res.activityInfo.packageName,
									res.activityInfo.name));

					cropOptions.add(co);
				}

				CropOptionAdapter adapter = new CropOptionAdapter(
						this.getApplicationContext(), cropOptions);

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Choose Crop App");
				builder.setAdapter(adapter,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								startActivityForResult(
										cropOptions.get(item).appIntent,
										REQUEST_CODE_CROP);
							}
						});

				builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {

						if (uri != null) {
							HomeBaseActivity.this.getContentResolver().delete(
									uri, null, null);
							uri = null;
						}
					}
				});

				AlertDialog alert = builder.create();

				alert.show();
			}
		}

	}

	private void handleBigCameraPhoto(boolean resizeFlag, Uri uriParam) {

		if (uriParam != null) {
			setPic(resizeFlag, uriParam);
			selectedImagePath = mCurrentPhotoPath;
			mCurrentPhotoPath = null;

			if (!selectedImagePath.equalsIgnoreCase("")) {

				File myPicFile = new File(selectedImagePath);
				UserHelper userHelper = new UserHelper(HomeBaseActivity.this,
						HomeBaseActivity.this);
				userHelper.updateProfilePicture(myPicFile);

			}
		}

	}

	public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth,
			int reqHeight) { // BEST QUALITY MATCH

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		// Calculate inSampleSize
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		int inSampleSize = 1;

		if (height > reqHeight) {
			inSampleSize = Math.round((float) height / (float) reqHeight);
		}

		int expectedWidth = width / inSampleSize;

		if (expectedWidth > reqWidth) {
			// if(Math.round((float)width / (float)reqWidth) > inSampleSize) //
			// If bigger SampSize..
			inSampleSize = Math.round((float) width / (float) reqWidth);
		}

		options.inSampleSize = inSampleSize;

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(path, options);
	}

	private void setPic(boolean resizeFlag, Uri uriParam) {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;

		ContentResolver res = this.getContentResolver();
		Bitmap bitmap = null;
		InputStream is = null;
		try {
			is = res.openInputStream(uriParam);
			if (resizeFlag == true) {
				bitmap = BitmapFactory.decodeStream(is, null, bmOptions);

			} else {
				bitmap = BitmapFactory.decodeStream(is);
			}
			is.close();
		} catch (FileNotFoundException e1) {

			e1.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		/* Figure out which way needs to be reduced less */
		/* Get the size of the ImageView */
		/*
		 * int targetW = mImageView.getWidth(); int targetH =
		 * mImageView.getHeight();
		 */
		if (resizeFlag) {
			int photoW = bmOptions.outWidth;
			int photoH = bmOptions.outHeight;
			int value = AppUtility.getImageViewerImageHeight(this);
			int targetW = value;
			int targetH = value;

			int scaleFactor = 1;
			if ((targetW > 0) || (targetH > 0)) {
				scaleFactor = Math.min(photoW / targetW, photoH / targetH);
			}

			/* Set bitmap options to scale the image decode target */
			bmOptions.inJustDecodeBounds = false;
			bmOptions.inSampleSize = scaleFactor;
			bmOptions.inPurgeable = true;

			/* Decode the JPEG file into a Bitmap */
			try {
				is = res.openInputStream(uriParam);
				bitmap = BitmapFactory.decodeStream(is, null, bmOptions);
				is.close();
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		// force orientation to portrait
		if (bitmap.getWidth() > bitmap.getHeight()) {
			Matrix matrix = new Matrix();
			matrix.postRotate(90);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
		}
		// Log.e("Aise", "Aise");

		// File file = new File(addAdvertiseController.mCurrentPhotoPath);
		// Log.e("Normal file size:", file.length() + "");
		// Toast.makeText(AddAdvertiseActivity.this, file.length()+"",
		// Toast.LENGTH_SHORT).show();

		FileOutputStream fOut = null;
		try {
			long timestamp = System.currentTimeMillis();
			File ezpsaImageFile = new File(schoolDirectory,
					getString(R.string.album_name) + timestamp + ".png");

			fOut = new FileOutputStream(ezpsaImageFile);

			int quality = 40;
			int increament = 10;
			int maxFileSize = 100 * 1024;
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fOut);

			while (ezpsaImageFile.length() > maxFileSize) {
				quality += increament;
				bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fOut);
			}

			// Log.e("Compressed file size:", file.length() + "");

			// Toast.makeText(AddAdvertiseActivity.this, file.length()+"",
			// Toast.LENGTH_SHORT).show();
			fOut.flush();
			fOut.close();
			// b.recycle();
			bitmap.recycle();
			mCurrentPhotoPath = ezpsaImageFile.getPath();

		} catch (Exception e) { // TODO
			e.printStackTrace();
		}

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);

	}

	private class DrawerItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.v(TAG, "ponies");
			navigateTo(position);
		}
	}

	private void navigateTo(int position) {
		Log.v(TAG, "List View Item: " + position);

		switch (position) {
		case 0:
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.content_frame,
							HomePageFreeVersionNew.newInstance(),
							HomePageFreeVersionNew.TAG).commit();
			break;
		case 1:
			break;
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	private View createSearchRow(final int position, final WrapAllData dataWrap) {

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = inflater.inflate(R.layout.row_search_single_item, null);

		LinearLayout layoutSearchContainer = (LinearLayout) view
				.findViewById(R.id.layoutSearchContainer);
		// layoutSearchContainer.setBackgroundResource(R.drawable.tap_search_container);
		TextView txtTitleName = (TextView) view.findViewById(R.id.txtTitleName);
		txtTitleName.setText(dataWrap.getListPost().get(position).getTitle());

		TextView txtCategoryName = (TextView) view
				.findViewById(R.id.txtCategoryName);
		txtCategoryName.setText(dataWrap.getListPost().get(position)
				.getCategoryName());

		ImageView imageViewImageLogo = (ImageView) view
				.findViewById(R.id.imageViewImageLogo);
		CustomRhombusIcon imageViewImageLogoRombus = (CustomRhombusIcon) view
				.findViewById(R.id.imageViewImageLogoRombus);

		if (!TextUtils.isEmpty(dataWrap.getListPost().get(position).getImage())) {

			imageViewImageLogoRombus.setVisibility(View.GONE);
			imageViewImageLogo.setVisibility(View.VISIBLE);

			SchoolApp.getInstance().displayUniversalImage(
					dataWrap.getListPost().get(position).getImage(),
					imageViewImageLogo);

		} else {

			imageViewImageLogoRombus.setVisibility(View.VISIBLE);
			imageViewImageLogo.setVisibility(View.GONE);

			imageViewImageLogoRombus.setIconImage(AppUtility
					.getResourceImageId(
							Integer.parseInt(dataWrap.getListPost()
									.get(position).getCategoryId()), true,
							false));

		}

		// imageViewImageLogo.setImageResource(AppUtility.getResourceImageId(Integer.parseInt(dataWrap.getListPost().get(position).getCategoryId()),
		// true));
		// SchoolApp.getInstance().displayUniversalImage(dataWrap.getListPost().get(position).getCategoryIconUrl(),
		// imageViewImageLogo);

		layoutSearch.addView(view);

		final LinearLayout layoutRightRedBar = (LinearLayout) view
				.findViewById(R.id.layoutRightRedBar);

		layoutSearchContainer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// layoutRightRedBar.setVisibility(View.VISIBLE);

				Log.e("TXT_VIEW", "click tv post");

				Intent intent = new Intent(HomeBaseActivity.this,
						SingleItemShowActivity.class);
				intent.putExtra(AppConstant.ITEM_ID, dataWrap.getListPost()
						.get(position).getId());
				intent.putExtra(AppConstant.ITEM_CAT_ID, dataWrap.getListPost()
						.get(position).getCategoryId());
				startActivity(intent);

				if (popup != null) {
					popup.dismiss();
					layoutSearch.removeAllViews();
				}

			}
		});

		layoutSearchContainer.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					layoutRightRedBar.setVisibility(View.VISIBLE);

				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					layoutRightRedBar.setVisibility(View.VISIBLE);

				}

				else // if (event.getAction() == MotionEvent.ACTION_UP)
				{
					layoutRightRedBar.setVisibility(View.INVISIBLE);
				}
				return false;
			}
		});

		return view;

	}

	private InputMethodManager im;

	private void setUpEditProfileView() {
		this.etCurrentPass = (EditText) mDrawerList
				.findViewById(R.id.et_current_pass);
		this.imgEditCurrentPass = (ImageView) mDrawerList
				.findViewById(R.id.imgEditCurrentPass);

		this.etNewPass = (EditText) mDrawerList.findViewById(R.id.et_new_pass);
		this.imgEditNewPass = (ImageView) mDrawerList
				.findViewById(R.id.imgEditNewPass);

		this.etRePass = (EditText) mDrawerList.findViewById(R.id.et_re_pass);
		this.imgEditRePass = (ImageView) mDrawerList
				.findViewById(R.id.imgEditRePass);

		im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(etCurrentPass.getWindowToken(), 0);
		im.hideSoftInputFromWindow(etNewPass.getWindowToken(), 0);
		im.hideSoftInputFromWindow(etRePass.getWindowToken(), 0);

		if (Build.VERSION.SDK_INT >= 11) {
			etCurrentPass.setRawInputType(InputType.TYPE_CLASS_TEXT);
			etCurrentPass.setTextIsSelectable(true);

			etNewPass.setRawInputType(InputType.TYPE_CLASS_TEXT);
			etNewPass.setTextIsSelectable(true);

			etRePass.setRawInputType(InputType.TYPE_CLASS_TEXT);
			etRePass.setTextIsSelectable(true);

		} else {
			etCurrentPass.setRawInputType(InputType.TYPE_NULL);
			etCurrentPass.setFocusable(true);

			etNewPass.setRawInputType(InputType.TYPE_NULL);
			etNewPass.setFocusable(true);

			etRePass.setRawInputType(InputType.TYPE_NULL);
			etRePass.setFocusable(true);
		}

		etCurrentPass.setFocusable(false);
		etNewPass.setFocusable(false);
		etRePass.setFocusable(false);

		showSoftKeyboard(im, etCurrentPass, imgEditCurrentPass);
		showSoftKeyboard(im, etNewPass, imgEditNewPass);
		showSoftKeyboard(im, etRePass, imgEditRePass);

		etRePass.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				// doSomething();

				if (etRePass.getText().toString().length() >= etNewPass
						.getText().toString().length()
						&& !etRePass
								.getText()
								.toString()
								.equalsIgnoreCase(
										etNewPass.getText().toString()))
					etRePass.setError("Please enter correct password");

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				// doSomething();
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				// doSomething();
				etRePass.setError(null);

			}

		});

	}

	private void showSoftKeyboard(final InputMethodManager im,
			final EditText et, ImageView editIcon) {
		editIcon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				im.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
			}
		});

		et.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				et.setFocusableInTouchMode(true);
				im.hideSoftInputFromWindow(et.getWindowToken(), 0);
				return false;
			}
		});
	}

	@Override
	public void onAuthenticationStart() {
		// TODO Auto-generated method stub
		uiHelper.showLoadingDialog("Please wait...");
	}

	@Override
	public void onAuthenticationSuccessful() {
		if (uiHelper.isDialogActive()) {
			uiHelper.dismissLoadingDialog();
		}
		updateUI();
		// Toast.makeText(this, "Successfully updated Password",
		// Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onAuthenticationFailed(String msg) {
		// TODO Auto-generated method stub
		if (uiHelper.isDialogActive()) {
			uiHelper.dismissLoadingDialog();
		}
		uiHelper.showMessage(msg);
	}

	@Override
	public boolean onClose() {
		// TODO Auto-generated method stub

		return false;
	}

	@Override
	public void onBackPressed() {
		if (!mSearchView.isIconified()) {
			mSearchView.setIconified(true);

			if (popup != null) {
				popup.dismiss();
				layoutSearch.removeAllViews();
			}

		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (popupView != null) {
			popupView = null;

		}
		if (popup.isShowing()) {
			popup.dismiss();
			popup = null;
		}

		if (uiHelper != null) {
			uiHelper.dismissLoadingDialog();
			uiHelper = null;
		}
	}

	private void clearAccountSettingsData() {
		etCurrentPass.setText("");
		etNewPass.setText("");
		etRePass.setText("");
	}

	@Override
	public void onPaswordChanged() {
		if (uiHelper.isDialogActive()) {
			uiHelper.dismissLoadingDialog();
		}
		clearAccountSettingsData();
		uiHelper.showMessage("Your password is changed successfully.");
		mDrawerLayout.closeDrawer(Gravity.RIGHT);
	}

}