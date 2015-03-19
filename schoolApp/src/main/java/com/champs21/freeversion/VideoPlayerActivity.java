package com.champs21.freeversion;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.VideoView;

import com.champs21.schoolapp.R;
import com.champs21.schoolapp.callbacks.ChooseFolderDilogClickListener;
import com.champs21.schoolapp.callbacks.onCreateFolderDialogButtonClickListener;
import com.champs21.schoolapp.model.Folder;
import com.champs21.schoolapp.model.FolderList;
import com.champs21.schoolapp.model.FreeVersionPost;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.AppUtility;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.RequestKeyHelper;
import com.champs21.schoolapp.utils.SchoolApp;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.utils.UserHelper;
import com.champs21.schoolapp.viewhelpers.ChooseGoodReadDialog;
import com.champs21.schoolapp.viewhelpers.CreateFolderDialog;
import com.champs21.schoolapp.viewhelpers.CustomButton;
import com.champs21.schoolapp.viewhelpers.CustomMediaController;
import com.champs21.schoolapp.viewhelpers.PopupDialog;
import com.champs21.schoolapp.viewhelpers.UIHelper;
import com.champs21.schoolapp.viewhelpers.CustomMediaController.IClickFullScreenCallBack;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;





public class VideoPlayerActivity extends ChildContainerActivity implements IClickFullScreenCallBack{
	
	private String videoUrl = "";
	private String id = "";
	private String secondCategoryId = "";
	private FreeVersionPost data;
	
	
	private VideoView videoView;
	private CustomMediaController mediaController;
	private ProgressBar progressBar;
	
	private ListView listVideo;
	private List<FreeVersionPost> listOfVideo = new ArrayList<FreeVersionPost>();
	private VideoAdapter adapter;
	private int preLast;
	private boolean hasNext = false;
	private int pageNumber = 1;
	private ProgressBar progressBarList;
	
	private UIHelper uiHelper;
	
	private TextView txtTitle;
	private TextView txtAuthor;
	private TextView txtViewCount;
	
	private RelativeLayout relativeLayoutVideo;
	private int layoutVideoHeight = 0;
	
	
	
	private LinearLayout layoutUpperPanelHolder;
	private RelativeLayout layoutLowerPanelHolder;
	
	private CustomButton btnShare;
	private CustomButton btnGoodRead;
	
	private ArrayList<Folder> folderList;
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
        homeBtn.setVisibility(View.VISIBLE);
        logo.setVisibility(View.GONE);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_player);
		
		uiHelper = new UIHelper(VideoPlayerActivity.this);
		
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
		
		Intent intent = getIntent();
		//videoUrl = intent.getExtras().getString(AppConstant.VIDEO_URL);
		//id = intent.getExtras().getString(AppConstant.VIDEO_POST_ID);
		
		
		String val = intent.getExtras().getString(AppConstant.VIDEO_ROOT_OBJECT);
		data = new Gson().fromJson(val, FreeVersionPost.class);
		
		videoUrl = data.getVideoUrl();
		id = data.getId();
		
		initApiSinglePost();
		
		initView();
		initAction();
		
		
	}
	
	
	private void initApiSinglePost()
	{
		if(!TextUtils.isEmpty(id))
		{
			RequestParams params = new RequestParams();
			params.put("id", id);
			
			AppRestClient.post(URLHelper.URL_FREE_VERSION_SINGLENEWS, params,  singleNewsHandler);
		}
		
	}
	
	
	private AsyncHttpResponseHandler singleNewsHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			
		};

		@Override
		public void onStart() {
			
		};

		@Override
		public void onSuccess(String responseString) {
			// Log.e("FREE_HOME", "data: "+responseString);

			

			Wrapper modelContainer = GsonParser.getInstance()
					.parseServerResponse(responseString);

			if (modelContainer.getStatus().getCode() == 200) {
				
				secondCategoryId = modelContainer.getData().getAsJsonObject().get("post").getAsJsonObject().get("second_category_id").getAsString();
				
				Log.e("SECOND_CAT_ID", "is: "+secondCategoryId);
				
				
				initApiCall(pageNumber, secondCategoryId);
				
				
				
			}

			else {

			}

		};

	};
	
	
	
	
	
	private void initView()
	{
		this.videoView = (VideoView)this.findViewById(R.id.videoView);
		this.progressBar = (ProgressBar)this.findViewById(R.id.progressBar);
		
		this.listVideo = (ListView)this.findViewById(R.id.listVideo);
		this.progressBarList = (ProgressBar)this.findViewById(R.id.progressBarList);
		
		
		this.txtTitle = (TextView)this.findViewById(R.id.txtTitle);
		this.txtAuthor = (TextView)this.findViewById(R.id.txtAuthor);
		this.txtViewCount = (TextView)this.findViewById(R.id.txtViewCount);
		
		this.layoutUpperPanelHolder = (LinearLayout)this.findViewById(R.id.layoutUpperPanelHolder);
		this.layoutLowerPanelHolder = (RelativeLayout)this.findViewById(R.id.layoutLowerPanelHolder);
		
		this.relativeLayoutVideo = (RelativeLayout)this.findViewById(R.id.relativeLayoutVideo);
		this.layoutVideoHeight = this.relativeLayoutVideo.getLayoutParams().height;
		
		this.btnShare = (CustomButton)this.findViewById(R.id.btnShare);
		this.btnGoodRead = (CustomButton)this.findViewById(R.id.btnGoodRead);
	}
	
	
	private void doReadLater(int i) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();

		params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());
		params.put(RequestKeyHelper.POST_ID, listOfVideo.get(i)
				.getId());

		AppRestClient.post(URLHelper.URL_FREE_VERSION_READLATER, params,
				readLaterHandler);
	}
	
	AsyncHttpResponseHandler readLaterHandler = new AsyncHttpResponseHandler() {
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
				uiHelper.showMessage("Post has been added to GoodRead");
			}
		};
	};
	
	
	
	
	
	
	
	private void toggleShowHide(boolean show)
	{
		if(show == true)
		{
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, (int) AppUtility.getDeviceIndependentDpFromPixel(VideoPlayerActivity.this, 200));
			videoView.setLayoutParams(lp);
			
			
			this.layoutUpperPanelHolder.setVisibility(View.VISIBLE);
			this.layoutLowerPanelHolder.setVisibility(View.VISIBLE);
			
		}
		else
		{
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			videoView.setLayoutParams(lp);
			
			
			
			this.layoutUpperPanelHolder.setVisibility(View.GONE);
			this.layoutLowerPanelHolder.setVisibility(View.GONE);
			
		}
		
	}
	
	
	
	
	private void initAction()
	{
		Uri vidUri = Uri.parse(videoUrl);
		this.videoView.setVideoURI(vidUri);
		//this.videoView.setMediaController(new MediaController(this)); 
		
		mediaController = new CustomMediaController(this);
		mediaController.setListener(this);
		this.videoView.setMediaController(mediaController);  
		
		this.videoView.start();
		
		videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				progressBar.setVisibility(View.GONE);
			}
		});
		
		this.adapter = new VideoAdapter();
		this.listVideo.setAdapter(this.adapter);
		
		
		
		this.txtTitle.setText(data.getTitle());
		this.txtAuthor.setText(data.getAuthor());
		this.txtViewCount.setText(data.getSeenCount());
		
		this.btnShare.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				share(videoUrl);
			}
		});
		
		this.btnGoodRead.setOnClickListener(new View.OnClickListener() {
			
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
				uiHelper.showSuccessDialog(
						"Folder added and post added to the folder!", "Success");
			}
		};
	};
	
	public void folderize(Folder f) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();

		params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());
		params.put(RequestKeyHelper.POST_ID, data.getId());
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
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);

	    // Checks the orientation of the screen
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	        ///Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
	        
	    	toggleShowHide(false);
	        getActionBar().hide();
	        
	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	       // Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
	    	toggleShowHide(true);
	        getActionBar().show();
	    }
	}


	@Override
	public void onClickFullScreen() {
		// TODO Auto-generated method stub
		Log.e("INTERFACE", "called");
		
		if(getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
		{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); 
		}
		else
		{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
		}
		
		
	}
			
	
	
	
	private void initApiCall(int pageNumber, String catId)
	{
		
		RequestParams params = new RequestParams();
		params.put("category_id", catId);
		params.put("page_number", String.valueOf(pageNumber));
		
		AppRestClient.post(URLHelper.URL_FREE_VERSION_CATEGORY, params,  videoHandler);
	}
	
	AsyncHttpResponseHandler videoHandler = new AsyncHttpResponseHandler() {
		public void onFailure(Throwable arg0, String arg1) {
			
			//Log.e("error", arg1);
		};

		public void onStart() {
			
		};

		public void onSuccess(int arg0, String responseString) {
		
			listOfVideo.clear();
			
			progressBarList.setVisibility(View.GONE);
			
			Wrapper modelContainer = GsonParser.getInstance().parseServerResponse(responseString);
			
			hasNext = modelContainer.getData().get("has_next").getAsBoolean();
			
			if (modelContainer.getStatus().getCode() == AppConstant.RESPONSE_CODE_SUCCESS) 
			{
				
				listOfVideo = GsonParser.getInstance().parseFreeVersionPost(
								modelContainer.getData().getAsJsonArray("post").toString());

				adapter.notifyDataSetChanged();
				
				
				populateVideoList();
				
			}
			
		};
	};
	
	
	private void populateVideoList()
	{
		
		listVideo.setOnScrollListener(new AbsListView.OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				final int lastItem = firstVisibleItem + visibleItemCount;
		        if(lastItem == totalItemCount) 
		        {
		           if(preLast!=lastItem)
		           { 	//to avoid multiple calls for last item
		        	   Log.e("Last", "Last");
		        	   preLast = lastItem;
		        	 
		        	   if(hasNext)
		        	   {
		        		   pageNumber++;
		        		   
		        		   initApiCall(pageNumber, secondCategoryId);
		        		   
		        		   
		        		   
		        	   }
		        	   
		        	  
		           }
		        }
			}
			
		});
		
		
		this.listVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, 
					long id) {
				// TODO Auto-generated method stub
				
				videoView.stopPlayback();
				progressBar.setVisibility(View.VISIBLE);
				
				
				//Uri vidUri = Uri.parse(data.getVideoUrl());
				Uri vidUri = Uri.parse(listOfVideo.get(position).getVideoUrl());
				videoView.setVideoURI(vidUri);
				videoView.start();
				videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
					
					@Override
					public void onPrepared(MediaPlayer mp) {
						// TODO Auto-generated method stub
						progressBar.setVisibility(View.GONE);
					}
				});
				
			}

		
		
		
		});
		
	}
	
	
	public void showCustomDialog(String headerText, int imgResId,
			String descriptionText) {

		PopupDialog picker = PopupDialog.newInstance(0);
		picker.setData(headerText, descriptionText, imgResId, VideoPlayerActivity.this);
		picker.show(getSupportFragmentManager(), null);
	}
	
	public class VideoAdapter extends BaseAdapter
	{

		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listOfVideo.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listOfVideo.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final ViewHolder holder;
			
			if(convertView == null)
			{
				holder = new ViewHolder();

				convertView = LayoutInflater.from(VideoPlayerActivity.this).inflate(R.layout.row_video_list, parent, false);
				
				holder.progressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);
				holder.imgThumbnail = (ImageView)convertView.findViewById(R.id.imgThumbnail);
				holder.txtTitle = (TextView)convertView.findViewById(R.id.txtTitle);
				holder.txtCategoryName = (TextView)convertView.findViewById(R.id.txtCategoryName);
				holder.txtViewCount = (TextView)convertView.findViewById(R.id.txtViewCount);
				holder.imgShareDots = (ImageView)convertView.findViewById(R.id.imgShareDots);
				
				
				convertView.setTag(holder);
				
			}
			else
			{
				holder = (ViewHolder)convertView.getTag();
			}
			
			if(listOfVideo.size() > 0)
			{
				holder.txtTitle.setText(listOfVideo.get(position).getTitle());
				holder.txtCategoryName.setText(listOfVideo.get(position).getCategoryName());
				holder.txtViewCount.setText(listOfVideo.get(position).getViewCount());
			
				if(listOfVideo.get(position).getImages().size() > 0)
					SchoolApp.getInstance().displayUniversalImage(listOfVideo.get(position).getImages().get(0), holder.imgThumbnail, holder.progressBar);
			}
			
			
			holder.imgShareDots.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.e("CLICKED", "sahre dots");
					
					PopupMenu popup = new PopupMenu(VideoPlayerActivity.this, holder.imgShareDots);
					popup.getMenuInflater().inflate(R.menu.popup_menu_share_video, popup.getMenu());  
					 
					popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {  
			             public boolean onMenuItemClick(MenuItem item) {  
			              //Toast.makeText(SchoolSearchFragment.this.getActivity(),"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();  
			             
			              Log.e("POP_UP", "title: "+ item.getTitle());
			              switch (item.getItemId()) {
			              
			              	case R.id.itemShare:
			              		Log.e("POP_UP", "id: "+ item.getItemId());
			              		
			              		share(listOfVideo.get(position).getVideoUrl());
			              		
							break;
							
			              	case R.id.itemViewLater:
			              		Log.e("POP_UP", "title: "+ item.getItemId());
			              		
			              		if (UserHelper.isLoggedIn())
									doReadLater(position);
								else
									showCustomDialog(
											"READ LATER",
											R.drawable.read_later_red_icon,
											getResources().getString(
													R.string.read_later_msg)
													+ "\n"
													+ getResources().getString(
															R.string.not_logged_in_msg));
			              		
			              		
								break;
							

						default:
							break;
						}
			             
			              
			              return true;  
			             }  
			            });  

			        popup.show();
					
					
				}
			});
			
			
			
			return convertView;
		}
		
	}
	
	
	class ViewHolder
	{
		ProgressBar progressBar;
		ImageView imgThumbnail;
		TextView txtTitle;
		TextView txtCategoryName;
		TextView txtViewCount;
		ImageView imgShareDots;
		
	}
	
	
	private void share(String url)
	{
		Intent share = new Intent(android.content.Intent.ACTION_SEND);
		share.setType("text/plain");
		share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

	
		share.putExtra(Intent.EXTRA_TEXT, url);

		startActivity(Intent.createChooser(share, "Share this video via"));
	}
	
	@Override
	protected void onDestroy() {
	    videoView.stopPlayback();
	    super.onDestroy();
	}

	
	
	
}
