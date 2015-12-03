package com.classtune.freeversion;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomButtonsController;

import com.classtune.schoolapp.R;
import com.classtune.schoolapp.callbacks.ChooseFolderDilogClickListener;
import com.classtune.schoolapp.callbacks.onCreateFolderDialogButtonClickListener;
import com.classtune.schoolapp.model.Folder;
import com.classtune.schoolapp.model.FolderList;
import com.classtune.schoolapp.model.FreeVersionPost;
import com.classtune.schoolapp.model.FreeVersionPost.Attachment;
import com.classtune.schoolapp.model.FreeVersionPost.Tags;
import com.classtune.schoolapp.model.Wrapper;
import com.classtune.schoolapp.networking.AppRestClient;
import com.classtune.schoolapp.utils.AppConstant;
import com.classtune.schoolapp.utils.AppUtility;
import com.classtune.schoolapp.utils.GsonParser;
import com.classtune.schoolapp.utils.RequestKeyHelper;
import com.classtune.schoolapp.utils.URLHelper;
import com.classtune.schoolapp.utils.UserHelper;
import com.classtune.schoolapp.viewhelpers.ChooseGoodReadDialog;
import com.classtune.schoolapp.viewhelpers.CreateFolderDialog;
import com.classtune.schoolapp.viewhelpers.CustomButton;
import com.classtune.schoolapp.viewhelpers.CustomRhombusIcon;
import com.classtune.schoolapp.viewhelpers.PagerContainer;
import com.classtune.schoolapp.viewhelpers.PopupDialog;
import com.classtune.schoolapp.viewhelpers.UIHelper;
import com.classtune.schoolapp.viewhelpers.UninterceptableViewPager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class SingleItemShowActivity_old extends HomeBaseActivity {

	
	private final static String TAG="Single Item Show Activity";
	private View folderCreateView;
	private ImageButton btnGoodRead, btnCandle, btnMySchool;
	private String itemId = "";
	private String itemCategoryId = "";

	private boolean isFromGoodRead = false;

	private UIHelper uiHelper;

	private int arraySize = 0;

	private JsonArray array;

	private JsonObject objectPost;

	private WebView webView;
	private WebView webViewAttachContent;
	private ImageButton btnPrevious;
	private ImageButton btnNext;

	private TextView txtTitle;
	private TextView txtCategoryName;

	private List<String> listImage = new ArrayList<String>();
	private List<Tags> listTags = new ArrayList<Tags>();
	private List<Attachment> listAttachments = new ArrayList<Attachment>();
	private String tagsString = "";

	private int currentPosition = 0;

	private PagerContainer container;

	private UninterceptableViewPager viewPager;

	private DisplayImageOptions options;
	private com.nostra13.universalimageloader.core.ImageLoader imageLoader;
	private ImagePagerAdapter adapter;
	private TextView txtTags;
	private TextView txtContentPlain;
	private ScrollView scrollView;
	private TextView txtSecondCatName;
	private UserHelper userHelper;
	private CustomRhombusIcon rhombus;

	private TextView auther_name_txt;
	private TextView since_txt;

	private LinearLayout layoutTagHolder;

	private LinearLayout layoutTagRoot;



	private CustomButton btnRemove;
	private LinearLayout dividerRemoveBtn;

	private List<TextView> listTagTxtView = new ArrayList<TextView>();
	ArrayList<Folder> folderList;
	private View goodreadBtn;
	
	private CustomButton btnShare;
	
	private LinearLayout layoutSolutionButtonHolder;
	private ImageView btnSolution;
	private WebView webViewSolution;
	private boolean isSolutionWebViewSelected = false;
	
	private LinearLayout layoutDownloadTextHolder;
	
	
	
	private String getItemIdFromUrl()
	{
		Uri data = getIntent().getData();
	    Log.d(TAG, data.toString());
	    String scheme = data.getScheme(); // "http"
	    Log.d(TAG, scheme);
	    String host = data.getHost(); // "twitter.com"
	    Log.d(TAG, host);
	    String inurl = data.toString();
	    String[] tokens=inurl.split("-");
	    return tokens[tokens.length-1];
	   /* List<String> params = data.getPathSegments();
	    String first = params.get(0); // "status"
	    String second = params.get(1); // "1234"
	    */		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_freeversion_singleitemshow_2);
		
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			if (bundle.containsKey(AppConstant.GOING_GOODREAD)) {
				this.isFromGoodRead = true;
			} else
				this.isFromGoodRead = false;
		}

		initViews();
		userHelper = new UserHelper(this);
		uiHelper = new UIHelper(this);

		if (bundle != null) {
			if (bundle.containsKey(AppConstant.ITEM_ID))
			{
				this.itemId = bundle.getString(AppConstant.ITEM_ID);
				this.itemCategoryId = bundle.getString(AppConstant.ITEM_CAT_ID);
			}
			else
			{
				this.itemId=getItemIdFromUrl();
			}
			/*
			 * this.isFromGoodRead =
			 * bundle.getBoolean(AppConstant.GOING_GOODREAD);
			 */
			Log.e("SINGLE_NEWS", "cat_id: " + this.itemCategoryId + " id: "
					+ itemId);

			initApiCall(this.itemId, this.itemCategoryId);
		}
	}

	private void initViews() {
		
		this.btnShare = (CustomButton)this.findViewById(R.id.btnShare);
		
		
		
		
		this.webView = (WebView) this.findViewById(R.id.webView);
		btnCandle = (ImageButton) findViewById(R.id.btn_candle);
		btnCandle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (UserHelper.isLoggedIn()) {
					startActivity(new Intent(SingleItemShowActivity_old.this,
							CandleActivity.class));
				} else {
					/*uiHelper.showSuccessDialog(
							getString(R.string.not_logged_in_msg), "Dear User");*/
					showCustomDialog("CANDLE",
							R.drawable.candle_popup_icon,getResources().getString(R.string.candle_msg)+"\n"+ getResources()
									.getString(R.string.not_logged_in_msg));
					
				}

			}
		});
		goodreadBtn =(ImageButton) findViewById(R.id.button_goodread);
		btnMySchool = (ImageButton) findViewById(R.id.btn_myschool);
		
		btnMySchool.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//uiHelper.showMessage("Sit tight....Coming Soon!");
				
			}
		});
		btnGoodRead = (ImageButton) findViewById(R.id.btn_goodread);
		btnGoodRead.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (UserHelper.isLoggedIn()) {
					startActivity(new Intent(SingleItemShowActivity_old.this,
							GoodReadActivity.class));
				} else {
					/*uiHelper.showSuccessDialog(
							getString(R.string.not_logged_in_msg), "Dear User");*/
					showCustomDialog("GOOD READ",
							R.drawable.goodread_popup_icon,getResources().getString(R.string.good_read_msg)+"\n"+ getResources()
							.getString(R.string.not_logged_in_msg));
					
				}
			}
		});
		rhombus = (CustomRhombusIcon) findViewById(R.id.rhombus);
		rhombus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		// zoomControl(this.webView);

		this.btnPrevious = (ImageButton) this.findViewById(R.id.btnPrevious);
		this.btnNext = (ImageButton) this.findViewById(R.id.btnNext);

		this.txtTitle = (TextView) this.findViewById(R.id.txtTitle);
		this.container = (PagerContainer) this
				.findViewById(R.id.pager_container);
		this.viewPager = (UninterceptableViewPager) container.getViewPager();
		// this.txtTags = (TextView) this.findViewById(R.id.txtTags);

		this.txtSecondCatName = (TextView) this
				.findViewById(R.id.txtSecondCatName);

		this.txtContentPlain = (TextView) this
				.findViewById(R.id.txtContentPlain);

		viewPager.setPageMargin(25);

		// If hardware acceleration is enabled, you should also remove
		// clipping on the pager for its children.
		viewPager.setClipChildren(true);

		this.txtCategoryName = (TextView) this
				.findViewById(R.id.txtCategoryName);
		this.scrollView = (ScrollView) this.findViewById(R.id.scrollView);
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisc(true).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).threadPriority(Thread.NORM_PRIORITY - 2)
				.defaultDisplayImageOptions(defaultOptions)
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				// .writeDebugLogs() // Remove for release app
				.build();

		imageLoader = com.nostra13.universalimageloader.core.ImageLoader
				.getInstance();
		imageLoader.init(config);

		options = new DisplayImageOptions.Builder()
				.resetViewBeforeLoading(true).cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		goodreadBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (UserHelper.isLoggedIn())
					fetchGoodReadFolders();
				else
					//uiHelper.showErrorDialog(getString(R.string.not_logged_in_msg));
					showCustomDialog("GOOD READ",
							R.drawable.goodread_popup_icon,getResources().getString(R.string.good_read_msg)+"\n"+ getResources()
							.getString(R.string.not_logged_in_msg));

			}
		});


		this.layoutTagHolder = (LinearLayout)this.findViewById(R.id.layoutTagHolder);
		this.layoutTagRoot = (LinearLayout)this.findViewById(R.id.layoutTagRoot);
		this.dividerRemoveBtn  = (LinearLayout)this.findViewById(R.id.dividerRemoveBtn);
		
		this.btnRemove = (CustomButton)this.findViewById(R.id.btnRemove);
		this.auther_name_txt = (TextView) this
				.findViewById(R.id.auther_name_txt);

		this.since_txt = (TextView) this.findViewById(R.id.since_txt);


		this.layoutTagHolder = (LinearLayout) this
				.findViewById(R.id.layoutTagHolder);

		this.layoutTagRoot = (LinearLayout) this
				.findViewById(R.id.layoutTagRoot);

		this.dividerRemoveBtn = (LinearLayout) this
				.findViewById(R.id.dividerRemoveBtn);

		this.btnRemove = (CustomButton) this.findViewById(R.id.btnRemove);

		this.webViewAttachContent = (WebView)this.findViewById(R.id.webViewAttachContent);
		
		
		if(this.isFromGoodRead == false)
		{

		
		if (this.isFromGoodRead == false) {

			this.btnRemove.setVisibility(View.GONE);
			this.dividerRemoveBtn.setVisibility(View.GONE);
		} else {
			this.btnRemove.setVisibility(View.VISIBLE);
			this.dividerRemoveBtn.setVisibility(View.VISIBLE);
		}
		
		
		
		
		}
		
		
		
		layoutSolutionButtonHolder = (LinearLayout)this.findViewById(R.id.layoutSolutionButtonHolder);
        btnSolution = (ImageView)this.findViewById(R.id.btnSolution);
        webViewSolution = (WebView)this.findViewById(R.id.webViewSolution);
        
        layoutDownloadTextHolder = (LinearLayout)this.findViewById(R.id.layoutDownloadTextHolder);
        
	}

	

	// FolderEfficientAdapter folderAdapter;

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

			Log.e("Folders", responseString);

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

	PopupWindow window;

	protected void showFolderList() {

		new ChooseGoodReadDialog(this, new ChooseFolderDilogClickListener() {

			@Override
			public void folderClicked(Folder f) {
				folderize(f);
			}

			@Override
			public void addFolderBtnClicked() {

				createFolder();
			}
		}, folderList).show();

	}

	protected void createFolder() {
		// TODO Auto-generated method stub
		new CreateFolderDialog(this,
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

	protected void createFolderName(String folderName) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();

		params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());
		params.put(RequestKeyHelper.POST_ID, this.itemId);
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
				uiHelper.showSuccessDialog(
						"Folder added and post added to the folder!", "Success");
			}
		};
	};

	public void folderize(Folder f) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();

		params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());
		params.put(RequestKeyHelper.POST_ID, this.itemId);
		params.put(RequestKeyHelper.FOLDER_ID, f.getId());

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
				uiHelper.showMessage("The post has been folderized!");
			}
		};
	};

	private class FolderEfficientAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private ArrayList<Folder> list;

		public FolderEfficientAdapter(Context context, ArrayList<Folder> list) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(context);
			this.list = list;
		}

		/**
		 * The number of items in the list is determined by the number of
		 * speeches in our array.
		 * 
		 * @see android.widget.ListAdapter#getCount()
		 */
		public int getCount() {
			return list.size();

		}

		/**
		 * Since the data comes from an array, just returning the index is
		 * sufficent to get at the data. If we were using a more complex data
		 * structure, we would return whatever object represents one row in the
		 * list.
		 * 
		 * @see android.widget.ListAdapter#getItem(int)
		 */
		public Object getItem(int position) {
			return position;
		}

		/**
		 * Use the array index as a unique id.
		 * 
		 * @see android.widget.ListAdapter#getItemId(int)
		 */
		public long getItemId(int position) {
			return position;
			// dfsdf
		}

		class ViewHolder {
			TextView tvFolderName;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final int i = position;
			// A ViewHolder keeps references to children views to avoid
			// unneccessary calls
			// to findViewById() on each row.
			ViewHolder holder;
			// When convertView is not null, we can reuse it directly, there is
			// no need
			// to reinflate it. We only inflate a new View when the convertView
			// supplied
			// by ListView is null.
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.row_create_folder,
						null);

				// Creates a ViewHolder and store references to the two children
				// views
				// we want to bind data to.
				holder = new ViewHolder();

				holder.tvFolderName = (TextView) convertView
						.findViewById(R.id.tv_folder_name);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Log.e("foldername", list.get(position).getTitle());

			holder.tvFolderName.setText(list.get(position).getTitle());

			return convertView;
		}

		public ArrayList<Folder> getList() {
			return list;
		}
	}

	private void initApiCall(String itemId, String itemCategoryId) {

		Log.e("SINGLE_NEWS_INITAPICALL", "cat_id: " + this.itemCategoryId
				+ " id: " + itemId);

		RequestParams params = new RequestParams();
		params.put("id", itemId);
		params.put("category_id", itemCategoryId);

		
		
		//params.put("id", "143");
		//params.put("category_id", "6");
		
		
		
		if(UserHelper.isLoggedIn())

		if (UserHelper.isLoggedIn())

			params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());

		AppRestClient.post(URLHelper.URL_FREE_VERSION_SINGLENEWS, params,
				singleNewsHandler);
	}

	private AsyncHttpResponseHandler singleNewsHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			uiHelper.showMessage(arg1);
			uiHelper.dismissLoadingDialog();
		};

		@Override
		public void onStart() {
			uiHelper.showLoadingDialog("Please wait...");
		};

		@Override
		public void onSuccess(String responseString) {
			// Log.e("FREE_HOME", "data: "+responseString);

			uiHelper.dismissLoadingDialog();

			Wrapper modelContainer = GsonParser.getInstance()
					.parseServerResponse(responseString);

			if (modelContainer.getStatus().getCode() == 200) {

				listImage.clear();
				listTags.clear();
				tagsString = "";

				layoutTagHolder.removeAllViews();
				layoutDownloadTextHolder.removeAllViews();

				Log.e("SINGLE_NEWS", "data: "
						+ modelContainer.getData().getAsJsonObject().toString());

				if (array == null) {
					array = modelContainer.getData().getAsJsonObject()
							.get("allpostid").getAsJsonArray();

					arraySize = array.size();
				}

				currentPosition = indexOfArray(itemId, array);
				// itemId =
				// array.get(positionArray).getAsJsonObject().get("id").getAsString();

				Log.e("POS_POS", "is: " + indexOfArray(itemId, array));

				Log.e("SINGLE_NEWS", "array size: " + arraySize);

				objectPost = modelContainer.getData().getAsJsonObject()
						.get("post").getAsJsonObject();

				// modelContainer.getData().getAsJsonObject().get("post").getAsJsonObject();
				// modelContainer.getData().getAsJsonObject().get("subcategory").getAsJsonArray();

				JsonArray arrayImage = objectPost.get("images")
						.getAsJsonArray();

				for (int i = 0; i < arrayImage.size(); i++) {
					Log.e("ARR_IMG", "arr str: "
							+ arrayImage.get(i).getAsString());

					listImage.add(arrayImage.get(i).getAsString());
				}

				Log.e("ARR_IMG", "arr: " + arrayImage.toString() + " size: "
						+ arrayImage.size());

				JsonArray arrayTags = objectPost.get("tags").getAsJsonArray();

				if (arrayTags.size() > 0 || arrayTags != null) {
					listTags = parseTags(arrayTags.toString());

					for (int i = 0; i < listTags.size(); i++) {
						Log.e("ARR_IMG", "arr tags: "
								+ listTags.get(i).getName());

						tagsString = tagsString + listTags.get(i).getName()
								+ " ";

					}
				}
				
				
				JsonArray arrayAttachments = objectPost.get("attachment").getAsJsonArray();
				if (arrayAttachments.size() > 0 || arrayAttachments != null) {
					listAttachments = parseAttachMents(arrayAttachments.toString());

					
				}
				

				Log.e("ARR_IMG", "arr tags string: " + tagsString);

				initViewActions(objectPost);

			}

			else {

			}

		};

	};

	private void initViewActions(JsonObject object) {
		final FreeVersionPost data = new Gson().fromJson(object.toString(),
				FreeVersionPost.class);
		
		this.btnShare.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
		
				sharePostUniversal(data);
			}
		});
		

		if (data.getPostType().equals("1")) {
			this.webView.setVisibility(View.VISIBLE);
			//this.webView.seton
			this.scrollView.setVisibility(View.GONE);
		} else {
			this.webView.setVisibility(View.GONE);
			this.scrollView.setVisibility(View.VISIBLE);

			this.txtContentPlain.setText(data.getContent());
		}

		showWebViewContent(data.getContent(), this.webView);

		this.btnNext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (currentPosition + 1 < arraySize) {
					itemId = array.get(currentPosition + 1).getAsJsonObject()
							.get("id").getAsString();

					itemCategoryId = objectPost.get("category_id")
							.getAsString();

					initApiCall(itemId, itemCategoryId);
				}

				else {
					Toast.makeText(SingleItemShowActivity_old.this,
							"No next data available", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});

		this.btnPrevious.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (currentPosition - 1 >= 0) {
					itemId = array.get(currentPosition - 1).getAsJsonObject()
							.get("id").getAsString();

					itemCategoryId = objectPost.get("category_id")
							.getAsString();

					initApiCall(itemId, itemCategoryId);
				}

				else {
					Toast.makeText(SingleItemShowActivity_old.this,
							"No previous data available", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		this.txtTitle.setText(data.getTitle());
		// this.txtTags.setText(tagsString);

		this.adapter = new ImagePagerAdapter(listImage);

		this.viewPager.setAdapter(adapter);
		adapter.notifyDataSetChanged();

		this.txtCategoryName.setText(data.getCategoryName());
		String temp = data.getSecondCategoryName();
		if (!TextUtils.isEmpty(temp)) {
			this.txtSecondCatName.setText(temp);
			this.txtSecondCatName.setVisibility(View.VISIBLE);

		} else {
			this.txtSecondCatName.setVisibility(View.GONE);
		}

		
		if(data.getAuthor().length() > 0)
		{
			this.auther_name_txt.setVisibility(View.VISIBLE);
			this.auther_name_txt.setText("By "+data.getAuthor());
		}
			
		else
		{
			this.auther_name_txt.setVisibility(View.GONE);
		}
			
		
		this.since_txt.setText(data.getPublishedDateString()+" ago");
		
		
		/*if(data.getSecondCategoryName().length() > 0)
		{
			this.divider_name.setVisibility(View.VISIBLE);
=======

		if (data.getAuthor().trim().length() > 0) {
			this.auther_name_txt.setVisibility(View.VISIBLE);
			this.auther_name_txt.setText("By " + data.getAuthor());
		} else {
			this.auther_name_txt.setVisibility(View.GONE);
>>>>>>> .r306
		}

		this.since_txt.setText(data.getPublishedDateString() + " ago");

		/*
		 * if(data.getSecondCategoryName().length() > 0) {
		 * this.divider_name.setVisibility(View.VISIBLE); } else {
		 * this.divider_name.setVisibility(View.GONE); }
		 */

		LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// parms.setMargins(3, 3, 3, 3);
		// parms.weight = 1;
		

		for (int i = 0; i < listTags.size(); i++) {
			if (i <= 3) {
				TextView tag = new TextView(this);
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

		if(listTags.size() > 0)
			layoutTagRoot.setVisibility(View.VISIBLE);
		else
			layoutTagRoot.setVisibility(View.GONE);
		
		

		
		//showWebViewContent(data.getAttachContent(), this.webViewAttachContent);
		
		webViewAttachContent.getSettings().setJavaScriptEnabled(true);
		final String mimeType = "text/html";
		final String encoding = "UTF-8";
		
		//webViewAttachContent.loadData(data.getAttachContent(), mimeType, encoding);
		
		webViewAttachContent.setWebChromeClient(new WebChromeClient());  
        WebViewClient client = new ChildBrowserClient(data);  
        webViewAttachContent.setWebViewClient(client);  
        WebSettings settings = webViewAttachContent.getSettings();  
        settings.setJavaScriptEnabled(true);  
        //webViewAttachContent.setInitialScale(1);  
       // webViewAttachContent.getSettings().setUseWideViewPort(true);  
        settings.setJavaScriptCanOpenWindowsAutomatically(false);  
        settings.setBuiltInZoomControls(true);  
        settings.setPluginState(PluginState.ON);  
        settings.setDomStorageEnabled(true);  
        webViewAttachContent.setId(5);  
        //webViewAttachContent.setInitialScale(0);  
        webViewAttachContent.requestFocus();  
        webViewAttachContent.requestFocusFromTouch();  
		
        //webViewAttachContent.loadData(data.getAttachContent(), mimeType, encoding);
        
        
        String strDownloadMerge = "";
        
        if(listAttachments.size() > 0)
        {
        	webViewAttachContent.setVisibility(View.VISIBLE);
        	
        	
        	for(int i=0;i<listAttachments.size();i++)
        	{
        		if(listAttachments.get(i).getShow().equals("1"))
        			strDownloadMerge = strDownloadMerge+listAttachments.get(i).getContent();
        	}
        
        	webViewAttachContent.loadData(strDownloadMerge, mimeType, encoding);
        	
        	
        	for(int i=0;i<listAttachments.size();i++)
        	{
        		loaddDownloadAttachment(listAttachments.get(i).getDownloadLink(), layoutDownloadTextHolder, listAttachments.get(i).getShow(), listAttachments.get(i).getCaption());
        	}
        	
        }
        else
        {
        	webViewAttachContent.setVisibility(View.GONE);
        }
        
        	
        
        
        
        
        
        
        
        
        
        
        
        btnSolution.setBackgroundResource(R.drawable.btn_solution_normal);
		showWebViewContent("", webViewSolution);
		webViewSolution.setVisibility(View.GONE);
		
        if(!TextUtils.isEmpty(data.getSolution()))
        {
        	layoutSolutionButtonHolder.setVisibility(View.VISIBLE);
        	btnSolution.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//txtSolution.setText(Html.fromHtml(data.getSolution()));
					isSolutionWebViewSelected = !isSolutionWebViewSelected;
					if(isSolutionWebViewSelected)
					{
						btnSolution.setBackgroundResource(R.drawable.btn_solution_tap);
						showWebViewContent(data.getSolution(), webViewSolution);
						webViewSolution.setVisibility(View.VISIBLE);
					}
					else
					{
						btnSolution.setBackgroundResource(R.drawable.btn_solution_normal);
						showWebViewContent("", webViewSolution);
						webViewSolution.setVisibility(View.GONE);
					}
					
					
					//showWebViewContent(data.getSolution(), webViewSolution);
				}
			});
        	
        }
        else
        {
        	layoutSolutionButtonHolder.setVisibility(View.GONE);
        }

	}
	
	
	@SuppressLint("SetJavaScriptEnabled")
	private void loaddDownloadAttachment(final String downloadlink, LinearLayout layoutDownloadTextHolder, String show, String caption)
	{
		
		LinearLayout.LayoutParams  lParam= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    	lParam.setMargins(10, 5, 10, 5);
    	
    	ImageButton btnDownload = new ImageButton(this);
    	btnDownload.setLayoutParams(lParam);
    	btnDownload.setBackgroundResource(R.drawable.btn_download);
    	
    	btnDownload.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(downloadlink)));
			}
		});
    	
    	layoutDownloadTextHolder.addView(btnDownload);
    	
		TextView txtCaption = new TextView(this);
		txtCaption.setLayoutParams(lParam);
		txtCaption.setTextSize(AppUtility.getDeviceIndependentDpFromPixel(this, 11));
		txtCaption.setTextColor(Color.BLACK);
		txtCaption.setText(caption);
		layoutDownloadTextHolder.addView(txtCaption);
	
    	
    	
    	
    	
	}
	
	
	
	

	public class ChildBrowserClient extends WebViewClient {  
		
		private FreeVersionPost data;
		
		public ChildBrowserClient(FreeVersionPost data)
		{
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
                         
                            DownloadManager mdDownloadManager = (DownloadManager) SingleItemShowActivity_old.this  
                                      .getSystemService(Context.DOWNLOAD_SERVICE);  
                            DownloadManager.Request request = new DownloadManager.Request(  
                                      Uri.parse(url));  
                            File destinationFile = new File(  
                                      Environment.getExternalStorageDirectory(),  
                                      getFileName(url));  
                            request.setDescription("Downloading via "+getApplicationName(SingleItemShowActivity_old.this));  
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);  
                            request.setDestinationUri(Uri.fromFile(destinationFile));  
                            mdDownloadManager.enqueue(request);  
                            value = false;  
                        
                            final String mimeType2 = "text/html";
                    		final String encoding = "UTF-8";
                            webViewAttachContent.loadData(data.getAttachContent(), mimeType2, encoding);
                            
                            
                  }  
                  if (value) {  
                       view.loadUrl(url);  
                  }  
             }  
             return value;  
        }  
        
        
        public  String getApplicationName(Context context) {
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
         *      The webview initiating the callback.  
         * @param url  
         *      The url of the page.  
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
	
	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	private void showWebViewContent(String text, WebView webView) {
		
		final String mimeType = "text/html";
		final String encoding = "UTF-8";

		webView.loadDataWithBaseURL("", text, mimeType, encoding, null);
		/*webView.getSettings().setLayoutAlgorithm(
				LayoutAlgorithm.SINGLE_COLUMN);*/
		
		WebSettings webViewSettings = webView.getSettings();
		webViewSettings.setJavaScriptEnabled(true);

		//webViewSettings.setPluginState(WebSettings.PluginState.ON);
		
		webView.setWebChromeClient(new WebChromeClient());


		
		//webViewSettings.setUseWideViewPort(true);
		//webViewSettings.setLoadWithOverviewMode(true);
		
		//webViewSettings.setDefaultFontSize(18);
		//webViewSettings.setTextZoom(90);
		
		//(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getResources().getDisplayMetrics())
		//webViewSettings.setMinimumFontSize(18);
		//webViewSettings.setMinimumLogicalFontSize(18);
		
		
		//webView.getSettings().setLoadWithOverviewMode(true);
		//webView.getSettings().setUseWideViewPort(true);

		/*webViewSettings.setUseWideViewPort(true);
		webViewSettings.setLoadWithOverviewMode(true);*/
		
		
				
	}

	public int indexOfArray(String searchString, JsonArray domain) {
		for (int i = 0; i < domain.size(); i++) {
			if (searchString.equals(array.get(i).getAsJsonObject().get("id")
					.getAsString()))
				return i;
		}

		return -1;
	}

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
	
	
	
	

	private class ImagePagerAdapter extends PagerAdapter {

		private List<String> images;
		private LayoutInflater inflater;

		ImagePagerAdapter(List<String> images) {
			this.images = images;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image,
					view, false);
			assert imageLayout != null;
			ImageView imageView = (ImageView) imageLayout
					.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout
					.findViewById(R.id.loading);

			imageLoader.displayImage(images.get(position), imageView, options,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							spinner.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							String message = null;
							switch (failReason.getType()) {
							case IO_ERROR:
								message = "Input/Output error";
								break;
							case DECODING_ERROR:
								message = "Image can't be decoded";
								break;
							case NETWORK_DENIED:
								message = "Downloads are denied";
								break;
							case OUT_OF_MEMORY:
								message = "Out Of Memory error";
								break;
							case UNKNOWN:
								message = "Unknown error";
								break;
							}
							Toast.makeText(SingleItemShowActivity_old.this,
									message, Toast.LENGTH_SHORT).show();

							spinner.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							spinner.setVisibility(View.GONE);
						}
					});

			view.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}
	}

	@SuppressLint("NewApi")
	private void zoomControl(final WebView wv) {
		wv.getSettings().setSupportZoom(true);
		wv.getSettings().setBuiltInZoomControls(true);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			// Use the API 11+ calls to disable the controls
			// Use a seperate class to obtain 1.6 compatibility
			new Runnable() {
				public void run() {
					wv.getSettings().setDisplayZoomControls(false);
				}
			}.run();
		} else {
			ZoomButtonsController zoom_controll = null;
			try {
				zoom_controll = (ZoomButtonsController) wv.getClass()
						.getMethod("getZoomButtonsController").invoke(wv, null);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			zoom_controll.getContainer().setVisibility(View.GONE);
		}
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			webView.loadUrl("about:blank");
			
			SingleItemShowActivity_old.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void showCustomDialog(String headerText, int imgResId,
			String descriptionText) {

			PopupDialog picker = PopupDialog.newInstance(0);
			picker.setData(headerText,descriptionText,imgResId,this);
			picker.show(getSupportFragmentManager(), null);
	}
	
}
