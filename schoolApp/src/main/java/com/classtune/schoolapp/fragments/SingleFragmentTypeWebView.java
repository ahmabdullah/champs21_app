package com.classtune.schoolapp.fragments;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.classtune.freeversion.ObservableScrollView;
import com.classtune.freeversion.SingleItemShowActivity;
import com.classtune.schoolapp.R;
import com.classtune.schoolapp.callbacks.ChooseFolderDilogClickListener;
import com.classtune.schoolapp.callbacks.onCreateFolderDialogButtonClickListener;
import com.classtune.schoolapp.model.Folder;
import com.classtune.schoolapp.model.FolderList;
import com.classtune.schoolapp.model.FreeVersionPost;
import com.classtune.schoolapp.model.FreeVersionPost.Attachment;
import com.classtune.schoolapp.model.FreeVersionPost.Language;
import com.classtune.schoolapp.model.FreeVersionPost.Tags;
import com.classtune.schoolapp.model.Wrapper;
import com.classtune.schoolapp.networking.AppRestClient;
import com.classtune.schoolapp.utils.AppConstant;
import com.classtune.schoolapp.utils.AppUtility;
import com.classtune.schoolapp.utils.GsonParser;
import com.classtune.schoolapp.utils.RequestKeyHelper;
import com.classtune.schoolapp.utils.SchoolApp;
import com.classtune.schoolapp.utils.URLHelper;
import com.classtune.schoolapp.utils.UserHelper;
import com.classtune.schoolapp.viewhelpers.ChooseGoodReadDialog;
import com.classtune.schoolapp.viewhelpers.CreateFolderDialog;
import com.classtune.schoolapp.viewhelpers.CustomRhombusIcon;
import com.classtune.schoolapp.viewhelpers.PopupDialog;
import com.classtune.schoolapp.viewhelpers.UIHelper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SingleFragmentTypeWebView extends Fragment implements
		ObservableScrollView.Callbacks {

	/*****
	 * 
	 * common variable declaration for all fragments ## start
	 * 
	 * 
	 * *****/

	private FreeVersionPost data;
	private JsonObject objectPost;
	private JsonObject objectRoot;

	private CustomRhombusIcon rhombus;
	private TextView txtCategoryName;
	private TextView txtSecondCatName;
	private ImageButton btnPrevious;
	private ImageButton btnNext;
	private TextView txtTitle;
	private TextView auther_name_txt;
	private TextView since_txt;
	private View goodreadBtn;
	private TextView txtgoodRead;
	private ImageButton btnShareUpper;
	ArrayList<Folder> folderList;
	private UserHelper userHelper;
	private UIHelper uiHelper;
	private List<Tags> listTags = new ArrayList<Tags>();
	private String tagsString = "";
	private LinearLayout layoutTagRoot;
	private LinearLayout layoutTagHolder;
	// private ImageButton btnGoodRead, btnCandle, btnCmart;

	private ObservableScrollView mObservableScrollView;
	private LinearLayout mStickyView;
	private View mPlaceholderView;

	private LinearLayout layoutAttachContainer;
	private WebView webViewAttachContent;
	private List<Attachment> listAttachments = new ArrayList<Attachment>();

	private LinearLayout layoutSolutionButtonHolder;
	private ImageView btnSolution;
	private WebView webViewSolution;
	private boolean isSolutionWebViewSelected = false;

	private String goodReadFolderId = "";
	private boolean isAlreadyGoodRead = false;

	private List<Language> listLanguage;
	private boolean isAnotherLangAvailabe = false;
	private ImageButton btnLanguage;
	private String mainId = "";
	private String otherLangId = "";
	private RelativeLayout relatedPostPanel;

	/*****
	 * 
	 * ## end
	 * 
	 * *****/

	// private ImageView imgView;

	private ProgressBar progressBar;

	/**
	 * 
	 * this area is for declaring web view contents
	 * 
	 * 
	 */
	private WebView webView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		String rootObj = getArguments().getString(AppConstant.POST_FRAG_OBJECT);
		data = new Gson().fromJson(rootObj, FreeVersionPost.class);

		String rootObj2 = getArguments().getString(
				AppConstant.POST_FRAG_OBJECT2);
		objectPost = new Gson().fromJson(rootObj2, JsonObject.class);

		String totalObjString = getArguments().getString(
				AppConstant.POST_FRAG_OBJECT_TOTAL);
		objectRoot = new Gson().fromJson(totalObjString, JsonObject.class);

		userHelper = new UserHelper(getActivity());
		uiHelper = new UIHelper(getActivity());

		Log.e("DATA_FRAG", "is: " + data.getTitle());
		Log.e("DATA_FRAG", "is: " + data.getFullContent());
		Log.e("DATA_FRAG_JSON", "is: " + objectPost.toString());
		Log.e("DATA_FRAG_JSON_ROOT", "is: " + objectRoot.toString());

		Log.e("DATA_FRAG_JSON_ROOT_BOOL", "is: "
				+ objectRoot.get("next_id").getAsBoolean());
		Log.e("DATA_FRAG_JSON_ROOT_STR", "is: "
				+ objectRoot.get("next_id").getAsString());

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.fragment_singlefragment_type_webview, null);

		initView(view);
		initViewAction();

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Fragment testFragment = RelatedPostFragment.newInstance(data.getId());
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		transaction.add(R.id.related_post_panel, testFragment).commit();
	}
	
	private void initView(View view) {
		rhombus = (CustomRhombusIcon) view.findViewById(R.id.rhombus);
		rhombus.setIconImage(AppUtility.getResourceImageId(Integer.parseInt(data.getCategoryId()), true, false));
		
		rhombus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getActivity().finish();
			}
		});

		this.relatedPostPanel=(RelativeLayout)view.findViewById(R.id.related_post_panel);
		this.txtCategoryName = (TextView) view
				.findViewById(R.id.txtCategoryName);
		this.txtSecondCatName = (TextView) view
				.findViewById(R.id.txtSecondCatName);

		this.btnPrevious = (ImageButton) view.findViewById(R.id.btnPrevious);
		this.btnNext = (ImageButton) view.findViewById(R.id.btnNext);

		this.txtTitle = (TextView) view.findViewById(R.id.txtTitle);
		this.auther_name_txt = (TextView) view
				.findViewById(R.id.auther_name_txt);
		this.since_txt = (TextView) view.findViewById(R.id.since_txt);

		this.btnShareUpper = (ImageButton) view
				.findViewById(R.id.btnShareUpper);
		this.goodreadBtn = (ImageButton) view
				.findViewById(R.id.button_goodread);
		this.txtgoodRead = (TextView) view.findViewById(R.id.txtgoodRead);

		this.layoutTagRoot = (LinearLayout) view
				.findViewById(R.id.layoutTagRoot);
		this.layoutTagHolder = (LinearLayout) view
				.findViewById(R.id.layoutTagHolder);

		/*
		 * btnGoodRead = (ImageButton)view.findViewById(R.id.btn_goodread);
		 * btnCandle = (ImageButton)view.findViewById(R.id.btn_candle); btnCmart
		 * = (ImageButton)view.findViewById(R.id.btn_cmart);
		 */

		// for sticky
		mObservableScrollView = (ObservableScrollView) view
				.findViewById(R.id.scroll_view);
		mObservableScrollView.setCallbacks(this);
		mStickyView = (LinearLayout) view.findViewById(R.id.sticky);
		mPlaceholderView = view.findViewById(R.id.placeholder);

		mObservableScrollView.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						onScrollChanged(mObservableScrollView.getScrollY());
					}
				});

		this.layoutAttachContainer = (LinearLayout) view
				.findViewById(R.id.layoutAttachContainer);
		this.webViewAttachContent = (WebView) view
				.findViewById(R.id.webViewAttachContent);

		this.layoutSolutionButtonHolder = (LinearLayout) view
				.findViewById(R.id.layoutSolutionButtonHolder);
		this.btnSolution = (ImageView) view.findViewById(R.id.btnSolution);
		this.webViewSolution = (WebView) view
				.findViewById(R.id.webViewSolution);

		this.btnLanguage = (ImageButton) view.findViewById(R.id.btnLanguage);

		/***
		 * 
		 * ## start local fragment variable
		 * 
		 * ****/
		// this.imgView = (ImageView)view.findViewById(R.id.imgView);

		this.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

		/**
		 * 
		 * this area is for declaring web view contents
		 * 
		 * 
		 */

		this.webView = (WebView) view.findViewById(R.id.webView);

	}

	private void initViewAction() {
		this.txtCategoryName.setText(data.getCategoryName());

		String temp = data.getSecondCategoryName();
		if (!TextUtils.isEmpty(temp)) {
			this.txtSecondCatName.setText(temp);
			this.txtSecondCatName.setVisibility(View.VISIBLE);

		} else {
			this.txtSecondCatName.setVisibility(View.GONE);
		}

		this.btnNext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (!objectRoot.get("next_id").getAsString()
						.equalsIgnoreCase("false")) {
					((SingleItemShowActivity) getActivity()).initApiCall(
							objectRoot.get("next_id").getAsString(), null);
					getActivity().getSupportFragmentManager()
							.beginTransaction()
							.remove(SingleFragmentTypeWebView.this).commit();
				} else {
					Toast.makeText(getActivity(), "No next data available",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		this.btnPrevious.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!objectRoot.get("previous_id").getAsString()
						.equalsIgnoreCase("false")) {
					((SingleItemShowActivity) getActivity()).initApiCall(
							objectRoot.get("previous_id").getAsString(), null);
					getActivity().getSupportFragmentManager()
							.beginTransaction()
							.remove(SingleFragmentTypeWebView.this).commit();
				} else {
					Toast.makeText(getActivity(), "No previous data available",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		this.txtTitle.setText(data.getTitle());

		this.auther_name_txt.setSelected(true);
		if (!TextUtils.isEmpty(data.getAuthor())) {
			this.auther_name_txt.setVisibility(View.VISIBLE);
			this.auther_name_txt.setText("By " + data.getAuthor());
		}

		else {
			this.auther_name_txt.setVisibility(View.GONE);
		}

		this.since_txt.setText(data.getPublishedDateString() + " ago");

		this.btnShareUpper.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				//sharePost(data);
				 Activity activity = getActivity();
				 if(activity instanceof SingleItemShowActivity)
				 {
					 SingleItemShowActivity myactivity = (SingleItemShowActivity) activity;
				      myactivity.shareAll();
				 }
				
			}
		});

		if (UserHelper.isLoggedIn()) {
			if (!TextUtils.isEmpty(objectRoot.get("good_read").getAsString())) {
				goodReadFolderId = objectRoot.get("good_read").getAsString();
				goodreadBtn.setBackgroundResource(R.drawable.remove_normal);
				txtgoodRead.setText("Remove\nGood Read");

				isAlreadyGoodRead = true;
			} else {
				isAlreadyGoodRead = false;

			}

		}

		goodreadBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isAlreadyGoodRead == true) {
					removeGoodread(data.getId(), goodReadFolderId);
				} else {
					if (UserHelper.isLoggedIn())
						fetchGoodReadFolders();
					else
						// uiHelper.showErrorDialog(getString(R.string.not_logged_in_msg));
						showCustomDialog(
								"GOOD READ",
								R.drawable.goodread_popup_icon,
								getResources()
										.getString(R.string.good_read_msg)
										+ "\n"
										+ getResources().getString(
												R.string.not_logged_in_msg));
				}

			}
		});

		JsonArray arrayTags = objectPost.get("tags").getAsJsonArray();

		if (arrayTags.size() > 0 || arrayTags != null) {
			listTags = parseTags(arrayTags.toString());

			for (int i = 0; i < listTags.size(); i++) {
				Log.e("ARR_IMG", "arr tags: " + listTags.get(i).getName());

				tagsString = tagsString + listTags.get(i).getName() + " ";

			}
		}

		if (listTags.size() > 0)
			layoutTagRoot.setVisibility(View.VISIBLE);
		else
			layoutTagRoot.setVisibility(View.GONE);

		LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		for (int i = 0; i < listTags.size(); i++) {
			if (i <= 3) {
				TextView tag = new TextView(getActivity());
				tag.setClickable(true);
				tag.setGravity(Gravity.CENTER);

				tag.setLayoutParams(parms);
				tag.setText(listTags.get(i).getName());
				tag.setTextSize(10);
				tag.setBackgroundColor(getResources().getColor(R.color.gray_2));
				tag.setPadding(10, 5, 10, 5);

				layoutTagHolder.addView(tag);
			}

		}

		JsonArray arrayAttachments = objectPost.get("attachment")
				.getAsJsonArray();
		if (arrayAttachments.size() > 0 || arrayAttachments != null) {
			listAttachments = parseAttachMents(arrayAttachments.toString());

		}

		webViewAttachContent.getSettings().setJavaScriptEnabled(true);
		final String mimeType = "text/html";
		final String encoding = "UTF-8";
		webViewAttachContent.getSettings().setPluginState(PluginState.ON);
		webViewAttachContent.setWebChromeClient(new WebChromeClient());
		WebViewClient client = new ChildBrowserClient(data);
		webViewAttachContent.setWebViewClient(new WebViewClient());
		WebSettings settings = webViewAttachContent.getSettings();
		settings.setJavaScriptEnabled(true);

		settings.setJavaScriptCanOpenWindowsAutomatically(false);
		settings.setBuiltInZoomControls(true);

		settings.setDomStorageEnabled(true);
		webViewAttachContent.setId(5);

		webViewAttachContent.requestFocus();
		webViewAttachContent.requestFocusFromTouch();

		String strDownloadMerge = "";

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		if (listAttachments.size() > 0) {
			layoutAttachContainer.setVisibility(View.VISIBLE);

			for (int i = 0; i < listAttachments.size(); i++) {
				if (listAttachments.get(i).getShow().equals("1")) {
					strDownloadMerge = strDownloadMerge
							+ listAttachments.get(i).getContent();

					WebView wv = new WebView(getActivity());
					/*wv.setWebViewClient(new WebViewClient() {
						public boolean shouldOverrideUrlLoading(WebView view,
								String url) {
							view.loadUrl(url);
							return false; // prevents the default action -
											// opening in system browser
						}
					});*/
					wv.setWebChromeClient(new WebChromeClient());
					wv.getSettings().setJavaScriptEnabled(true);
					
					// wv.setId(5);
					wv.loadData(listAttachments.get(i).getContent(), mimeType,
							encoding);
					layoutAttachContainer.addView(wv, params);
				}

			}

			Log.e("PDF", "data: " + strDownloadMerge);

			// webViewAttachContent.loadData(strDownloadMerge, mimeType,
			// encoding);

			for (int i = 0; i < listAttachments.size(); i++) {
				loaddDownloadAttachment(listAttachments.get(i)
						.getDownloadLink(), layoutAttachContainer,
						listAttachments.get(i).getShow(), listAttachments
								.get(i).getCaption());
			}

		} else {
			layoutAttachContainer.setVisibility(View.GONE);
		}

		btnSolution.setBackgroundResource(R.drawable.btn_solution_normal);
		showWebViewContent("", webViewSolution);
		webViewSolution.setVisibility(View.GONE);

		if (!TextUtils.isEmpty(data.getSolution())) {
			layoutSolutionButtonHolder.setVisibility(View.VISIBLE);
			btnSolution.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// txtSolution.setText(Html.fromHtml(data.getSolution()));
					isSolutionWebViewSelected = !isSolutionWebViewSelected;
					if (isSolutionWebViewSelected) {
						btnSolution
								.setBackgroundResource(R.drawable.btn_solution_tap);
						showWebViewContent(data.getSolution(), webViewSolution);
						webViewSolution.setVisibility(View.VISIBLE);
					} else {
						btnSolution
								.setBackgroundResource(R.drawable.btn_solution_normal);
						showWebViewContent("", webViewSolution);
						webViewSolution.setVisibility(View.GONE);
					}

					// showWebViewContent(data.getSolution(), webViewSolution);
				}
			});

		} else {
			layoutSolutionButtonHolder.setVisibility(View.GONE);
		}

		listLanguage = parseLanguage(objectRoot.get("language")
				.getAsJsonArray().toString());
		mainId = objectRoot.get("main_id").getAsString();
		// Log.e("LANGUAGE", "is: "+listLanguage.get(0).getLanguage());
		// Log.e("LANGUAGE_SIZE", "is: "+listLanguage.size());

		if (listLanguage.size() > 1) {
			isAnotherLangAvailabe = true;
			btnLanguage.setVisibility(View.VISIBLE);

			for (int i = 0; i < listLanguage.size(); i++) {
				if (!data.getId().equals(listLanguage.get(i).getId())) {
					otherLangId = listLanguage.get(i).getId();
				} else if (data.getId().equals(listLanguage.get(i).getId())) {
					if (listLanguage.get(i).getLanguage()
							.equalsIgnoreCase("en")) {
						btnLanguage.setImageResource(R.drawable.lang_bangla);
					} else if (listLanguage.get(i).getLanguage()
							.equalsIgnoreCase("bn")) {
						btnLanguage.setImageResource(R.drawable.lang_english);
					}
				}

			}

			// ((SingleItemShowActivity)getActivity()).initApiCall(objectRoot.get("next_id").getAsString(),
			// null);
			// getActivity().getSupportFragmentManager().beginTransaction().remove(SingleFragmentTypeZero.this).commit();
		} else {
			isAnotherLangAvailabe = false;
			btnLanguage.setVisibility(View.GONE);
		}

		btnLanguage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((SingleItemShowActivity) getActivity()).initApiCall(
						otherLangId, mainId, null);
				getActivity().getSupportFragmentManager().beginTransaction()
						.remove(SingleFragmentTypeWebView.this).commit();
			}
		});

		/*
		 * btnGoodRead.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub if (UserHelper.isLoggedIn()) { startActivity(new
		 * Intent(getActivity(), GoodReadActivity.class)); } else {
		 * uiHelper.showSuccessDialog( getString(R.string.not_logged_in_msg),
		 * "Dear User"); showCustomDialog("GOOD READ",
		 * R.drawable.goodread_popup_icon
		 * ,getResources().getString(R.string.good_read_msg)+"\n"+
		 * getResources() .getString(R.string.not_logged_in_msg));
		 * 
		 * } } });
		 * 
		 * btnCandle.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { if
		 * (UserHelper.isLoggedIn()) { startActivity(new Intent(getActivity(),
		 * CandleActivity.class)); } else { uiHelper.showSuccessDialog(
		 * getString(R.string.not_logged_in_msg), "Dear User");
		 * showCustomDialog("CANDLE",
		 * R.drawable.candle_popup_icon,getResources()
		 * .getString(R.string.candle_msg)+"\n"+ getResources()
		 * .getString(R.string.not_logged_in_msg));
		 * 
		 * }
		 * 
		 * } });
		 * 
		 * btnCmart.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub uiHelper.showMessage("Sit tight....Coming Soon!"); } });
		 */

		/***
		 * 
		 * ## start local fragment variable
		 * 
		 * ****/
		// SchoolApp.getInstance().displayUniversalImage(data.getImages().get(0),
		// this.imgView, this.progressBar);

		/**
		 * 
		 * this area is for declaring web view contents
		 * 
		 * 
		 */
		this.webView.setVisibility(View.VISIBLE);
		showWebViewContent(data.getContent(), this.webView);
	}

	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	private void showWebViewContent(String text, WebView webView) {

		final String mimeType = "text/html";
		final String encoding = "UTF-8";

		webView.loadDataWithBaseURL("", text, mimeType, encoding, null);
		WebSettings webViewSettings = webView.getSettings();
		webViewSettings.setJavaScriptEnabled(true);
		webView.setWebChromeClient(new WebChromeClient());

	}

	protected void removeGoodread(String postId, String folderId) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();

		params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());
		params.put(RequestKeyHelper.POST_ID, postId);
		params.put(RequestKeyHelper.FOLDER_ID, folderId);

		AppRestClient.post(URLHelper.URL_FREE_VERSION_REMOVE_GOODREAD, params,
				removeGoodReadHandler);
	}

	AsyncHttpResponseHandler removeGoodReadHandler = new AsyncHttpResponseHandler() {
		public void onStart() {
			uiHelper.showLoadingDialog("Please wait...");
		};

		public void onFailure(Throwable arg0, String arg1) {
			uiHelper.showMessage(arg1);
			if (uiHelper.isDialogActive()) {
				uiHelper.dismissLoadingDialog();
			}
		};

		public void onSuccess(int arg0, String responseString) {
			if (uiHelper.isDialogActive()) {
				uiHelper.dismissLoadingDialog();
			}

			Wrapper wrapper = GsonParser.getInstance().parseServerResponse(
					responseString);

			if (wrapper.getStatus().getCode() == 200) {

				uiHelper.showMessage("Goodread removed successfully!");

				// show good read button here
				goodreadBtn.setBackgroundResource(R.drawable.icon_read);
				txtgoodRead.setText("Good Read");

				isAlreadyGoodRead = false;

			}
		};
	};

	public class ChildBrowserClient extends WebViewClient {

		private FreeVersionPost data;

		public ChildBrowserClient(FreeVersionPost data) {
			this.data = data;
		}

		@SuppressLint("InlinedApi")
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			boolean value = true;
			String extension = MimeTypeMap.getFileExtensionFromUrl(url);
			if (extension != null) {
				MimeTypeMap mime = MimeTypeMap.getSingleton();
				String mimeType = mime.getMimeTypeFromExtension(extension);
				if (mimeType != null) {

					DownloadManager mdDownloadManager = (DownloadManager) getActivity()
							.getSystemService(Context.DOWNLOAD_SERVICE);
					DownloadManager.Request request = new DownloadManager.Request(
							Uri.parse(url));
					File destinationFile = new File(
							Environment.getExternalStorageDirectory(),
							getFileName(url));
					request.setDescription("Downloading via "
							+ getApplicationName(getActivity()));
					request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
					request.setDestinationUri(Uri.fromFile(destinationFile));
					mdDownloadManager.enqueue(request);
					value = false;

					final String mimeType2 = "text/html";
					final String encoding = "UTF-8";
					webViewAttachContent.loadData(data.getAttachContent(),
							mimeType2, encoding);

				}
				if (value) {
					view.loadUrl(url);
				}
			}
			return value;
		}

		public String getApplicationName(Context context) {
			int stringId = context.getApplicationInfo().labelRes;
			return context.getString(stringId);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
		}

		/**
		 * Notify the host application that a page has started loading.
		 * 
		 * @param view
		 *            The webview initiating the callback.
		 * @param url
		 *            The url of the page.
		 */
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}

		public String getFileName(String url) {

			final String[] separated = url.split("/");
			final String myFile = separated[separated.length - 1];

			String filenameWithoutExtension = "";
			filenameWithoutExtension = String.valueOf(myFile);
			return filenameWithoutExtension;
		}
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void loaddDownloadAttachment(final String downloadlink,
			LinearLayout layoutDownloadTextHolder, String show, String caption) {

		LinearLayout.LayoutParams lParam = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lParam.setMargins(10, 5, 10, 5);

		ImageButton btnDownload = new ImageButton(getActivity());
		btnDownload.setLayoutParams(lParam);
		btnDownload.setBackgroundResource(R.drawable.btn_download);

		btnDownload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse(downloadlink)));
			}
		});

		layoutDownloadTextHolder.addView(btnDownload);

		TextView txtCaption = new TextView(getActivity());
		txtCaption.setLayoutParams(lParam);
		txtCaption.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		txtCaption.setTextColor(Color.BLACK);
		txtCaption.setText(caption);
		layoutDownloadTextHolder.addView(txtCaption);

	}

	

	public void showCustomDialog(String headerText, int imgResId,
			String descriptionText) {

		PopupDialog picker = PopupDialog.newInstance(0);
		picker.setData(headerText, descriptionText, imgResId, getActivity());
		picker.show(getActivity().getSupportFragmentManager(), null);
	}

	private void fetchGoodReadFolders() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();

		folderList = new ArrayList<Folder>();
		// folderAdapter = new FolderEfficientAdapter(this, folderList);

		params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());

		AppRestClient.post(URLHelper.URL_FREE_VERSION_GOODREAD_FOLDER, params,
				goodreadFolderHandler);
	}

	AsyncHttpResponseHandler goodreadFolderHandler = new AsyncHttpResponseHandler() {
		public void onStart() {
			uiHelper.showLoadingDialog("Please wait...");
		};

		public void onFailure(Throwable arg0, String arg1) {
			uiHelper.showMessage(arg1);
			if (uiHelper.isDialogActive()) {
				uiHelper.dismissLoadingDialog();
			}
		};

		public void onSuccess(int arg0, String responseString) {
			if (uiHelper.isDialogActive()) {
				uiHelper.dismissLoadingDialog();
			}

			Log.e("Folders...", responseString);

			Wrapper wrapper = GsonParser.getInstance().parseServerResponse(
					responseString);

			if (wrapper.getStatus().getCode() == 200) {
				FolderList folderArray = GsonParser.getInstance()
						.parseFolderList(wrapper.getData().toString());

				folderList.clear();
				folderList.addAll(folderArray.getFolderList());

				Log.e("list.size", folderList.size() + "");

				showFolderList();
			}
		};
	};

	protected void showFolderList() {

		new ChooseGoodReadDialog(getActivity(),
				new ChooseFolderDilogClickListener() {

					@Override
					public void folderClicked(Folder f) {
						folderize(f);

						goodReadFolderId = f.getId();

						goodreadBtn
								.setBackgroundResource(R.drawable.remove_normal);
						txtgoodRead.setText("Remove\nGood Read");
						isAlreadyGoodRead = true;
					}

					@Override
					public void addFolderBtnClicked() {

						createFolder();
					}
				}, folderList).show();

	}

	protected void createFolder() {
		// TODO Auto-generated method stub
		new CreateFolderDialog(getActivity(),
				new onCreateFolderDialogButtonClickListener() {

					@Override
					public void onCreateBtnClick(
							CreateFolderDialog createFolderDialog,
							String folderName, TextView tvError) {
						// TODO Auto-generated method stub

						boolean isExist = false;

						for (Folder folder : folderList) {
							if (folderName.equalsIgnoreCase(folder.getTitle())) {
								isExist = true;

							}
						}

						if (isExist) {
							tvError.setText("* Foldername already exists!");
							tvError.setVisibility(View.VISIBLE);
						} else {
							createFolderDialog.dismiss();

							createFolderName(folderName);
						}
					}

					@Override
					public void onCancelBtnClick(
							CreateFolderDialog createFolderDialog) {
						// TODO Auto-generated method stub
						createFolderDialog.dismiss();
					}
				}).show();
	}

	public void folderize(Folder f) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();

		params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());
		params.put(RequestKeyHelper.POST_ID, data.getId());
		params.put(RequestKeyHelper.FOLDER_ID, f.getId());

		Log.e("FINAL_FOLDER", "id: " + f.getId());

		AppRestClient.post(URLHelper.URL_FREE_VERSION_FOLDERIZE_GOODREAD,
				params, folderizeHandler);
	}

	AsyncHttpResponseHandler folderizeHandler = new AsyncHttpResponseHandler() {
		public void onStart() {
			uiHelper.showLoadingDialog("Please wait...");
		};

		public void onFailure(Throwable arg0, String arg1) {
			uiHelper.showMessage(arg1);
			if (uiHelper.isDialogActive()) {
				uiHelper.dismissLoadingDialog();
			}
		};

		public void onSuccess(int arg0, String responseString) {
			if (uiHelper.isDialogActive()) {
				uiHelper.dismissLoadingDialog();
			}

			Log.e("Folders", responseString);

			Wrapper wrapper = GsonParser.getInstance().parseServerResponse(
					responseString);

			if (wrapper.getStatus().getCode() == 200) {
                uiHelper.showMessage("The article is in folder now.");

				// show remove button here
				goodreadBtn.setBackgroundResource(R.drawable.remove_normal);
				txtgoodRead.setText("Remove\nGood Read");
				isAlreadyGoodRead = true;

			}
		};
	};

	protected void createFolderName(String folderName) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();

		params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());
		params.put(RequestKeyHelper.POST_ID, data.getId());
		params.put(RequestKeyHelper.FOLDER_NAME, folderName);

		AppRestClient.post(URLHelper.URL_FREE_VERSION_FOLDERIZE_GOODREAD,
				params, createFolderHandler);
	}

	AsyncHttpResponseHandler createFolderHandler = new AsyncHttpResponseHandler() {
		public void onStart() {
			uiHelper.showLoadingDialog("Please wait...");
		};

		public void onFailure(Throwable arg0, String arg1) {
			uiHelper.showMessage(arg1);
			if (uiHelper.isDialogActive()) {
				uiHelper.dismissLoadingDialog();
			}
		};

		public void onSuccess(int arg0, String responseString) {
			if (uiHelper.isDialogActive()) {
				uiHelper.dismissLoadingDialog();
			}

			Log.e("Folders", responseString);

			Wrapper wrapper = GsonParser.getInstance().parseServerResponse(
					responseString);

			if (wrapper.getStatus().getCode() == 200) {
				/*
				 * uiHelper.showMessage("New folder has been created!");
				 * 
				 * fetchGoodReadFolders();
				 */

				goodReadFolderId = wrapper.getData().getAsJsonObject()
						.get("folder_id").getAsString();

				goodreadBtn.setBackgroundResource(R.drawable.remove_normal);
				txtgoodRead.setText("Remove\nGood Read");
				isAlreadyGoodRead = true;

				uiHelper.showSuccessDialog(
						"Folder added and post added to the folder!", "Success");
			}
		};
	};

	public List<Tags> parseTags(String object) {

		List<Tags> tags = new ArrayList<Tags>();
		Type listType = new TypeToken<List<Tags>>() {
		}.getType();
		tags = (List<Tags>) new Gson().fromJson(object, listType);
		return tags;
	}

	public List<Attachment> parseAttachMents(String object) {

		List<Attachment> att = new ArrayList<Attachment>();
		Type listType = new TypeToken<List<Attachment>>() {
		}.getType();
		att = (List<Attachment>) new Gson().fromJson(object, listType);
		return att;
	}

	public List<Language> parseLanguage(String object) {

		List<Language> att = new ArrayList<Language>();
		Type listType = new TypeToken<List<Language>>() {
		}.getType();
		att = (List<Language>) new Gson().fromJson(object, listType);
		return att;
	}

	public class ImageViewPagerAdapter extends PagerAdapter {

		private List<String> listImage;

		public ImageViewPagerAdapter(List<String> listImage) {
			this.listImage = listImage;

		}

		public int getCount() {
			return listImage.size();
		}

		public Object instantiateItem(View collection, int position) {
			ImageView view = new ImageView(getActivity());
			view.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
			view.setScaleType(ScaleType.FIT_XY);

			SchoolApp.getInstance().displayUniversalImage(
					listImage.get(position), view, progressBar);

			Log.e("LIST_GAL", "is: " + listImage.get(position));

			((ViewPager) collection).addView(view, 0);
			return view;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == ((View) arg1);

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

	}

	@Override
	public void onScrollChanged(int scrollY) {
		// TODO Auto-generated method stub
		if (getActivity() != null)
			mStickyView.setTranslationY(Math.max(
					mPlaceholderView.getTop(),
					scrollY
							- AppUtility.getDeviceIndependentDpFromPixel(
									getActivity(), 10)));
	}

	@Override
	public void onDownMotionEvent() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpOrCancelMotionEvent() {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * this area is for web view content
	 * 
	 * 
	 **/
	
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		
		Log.e("ON_BACK_DETACH", "pressed");
		webView.loadUrl("about:blank");
		super.onDetach();
	}
	

}
