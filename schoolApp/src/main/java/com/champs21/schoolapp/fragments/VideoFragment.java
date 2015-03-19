package com.champs21.schoolapp.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Video.VideoColumns;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.champs21.freeversion.HomePageFreeVersion;
import com.champs21.freeversion.VideoPlayerActivity;
import com.champs21.schoolapp.R;
import com.champs21.schoolapp.model.BaseType;
import com.champs21.schoolapp.model.FreeVersionPost;
import com.champs21.schoolapp.model.Picker;
import com.champs21.schoolapp.model.Picker.PickerItemSelectedListener;
import com.champs21.schoolapp.model.PickerType;
import com.champs21.schoolapp.model.VideoSubCat;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.RequestKeyHelper;
import com.champs21.schoolapp.utils.SchoolApp;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.utils.UserHelper;
import com.champs21.schoolapp.viewhelpers.CustomButton;
import com.champs21.schoolapp.viewhelpers.PopupDialog;
import com.champs21.schoolapp.viewhelpers.UIHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class VideoFragment extends Fragment {

	private View view;
	private int pageNumber = 1;
	private ListView listVideo;
	private List<FreeVersionPost> listOfVideo = new ArrayList<FreeVersionPost>();
	private VideoAdapter adapter;

	private ProgressBar progressBar;
	private int preLast;
	private boolean hasNext = false;

	private List<BaseType> listVideoSubCat = new ArrayList<BaseType>();

	private CustomButton btnSelectCategory;
	private String catId = "49";
	private TextView txtCategoryTitle;

	private int position = 0;

	private UIHelper uiHelper;
	private ImageButton btnHomeHome;

	public static VideoFragment newInstance(int index) {
		VideoFragment f = new VideoFragment();
		// Supply index input as an argument.
		Bundle args = new Bundle();
		args.putInt("categoryIndex", index);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		uiHelper = new UIHelper(getActivity());

		catId = String.valueOf(getArguments().getInt("categoryIndex", 0));

		initApiCall(pageNumber, catId);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		view = inflater.inflate(R.layout.fragment_video, container, false);

		initView(view);

		return view;
	}

	private void initView(View view) {
		this.txtCategoryTitle = (TextView) view
				.findViewById(R.id.txtCategoryTitle);
		this.btnHomeHome = (ImageButton) view.findViewById(R.id.btn_home_home);
		this.btnHomeHome.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((HomePageFreeVersion)getActivity()).loadHome();
			}
		});
		this.btnSelectCategory = (CustomButton) view
				.findViewById(R.id.btnSelectCategory);
		this.btnSelectCategory.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPicker(PickerType.VIDEO_CATEGORY_TYPE);
			}
		});

		this.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

		this.listVideo = (ListView) view.findViewById(R.id.listVideo);

		this.adapter = new VideoAdapter();
		this.listVideo.setAdapter(this.adapter);

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
				if (lastItem == totalItemCount) {
					if (preLast != lastItem) { // to avoid multiple calls for
												// last item
						Log.e("Last", "Last");
						preLast = lastItem;

						if (hasNext) {
							pageNumber++;
							initApiCall(pageNumber, catId);

						}

					}
				}
			}

		});

		this.listVideo
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub

						FreeVersionPost data = (FreeVersionPost) adapter
								.getItem(position);

						Log.e("DATA_ITEM_CLICKED", "data: ");
						Log.e("DATA_ITEM_CLICKED", "data id: " + data.getId());

						Intent intent = new Intent(VideoFragment.this
								.getActivity(), VideoPlayerActivity.class);

						String rootObj = new Gson().toJson(data);

						intent.putExtra(AppConstant.VIDEO_ROOT_OBJECT, rootObj);

						// intent.putExtra(AppConstant.VIDEO_URL,
						// data.getVideoUrl());
						// intent.putExtra(AppConstant.VIDEO_POST_ID,
						// data.getId());

						startActivity(intent);

					}

				});

	}

	private void initApiCall(int pageNumber, String catId) {

		RequestParams params = new RequestParams();
		params.put("category_id", catId);
		params.put("page_number", String.valueOf(pageNumber));

		AppRestClient.post(URLHelper.URL_FREE_VERSION_CATEGORY, params,
				videoHandler);
	}

	AsyncHttpResponseHandler videoHandler = new AsyncHttpResponseHandler() {
		public void onFailure(Throwable arg0, String arg1) {

			// Log.e("error", arg1);
		};

		public void onStart() {

		};

		public void onSuccess(int arg0, String responseString) {

			listOfVideo.clear();

			progressBar.setVisibility(View.GONE);

			Wrapper modelContainer = GsonParser.getInstance()
					.parseServerResponse(responseString);

			hasNext = modelContainer.getData().get("has_next").getAsBoolean();

			if (modelContainer.getStatus().getCode() == AppConstant.RESPONSE_CODE_SUCCESS) {

				listOfVideo = GsonParser.getInstance().parseFreeVersionPost(
						modelContainer.getData().getAsJsonArray("post")
								.toString());

				adapter.notifyDataSetChanged();

				if (modelContainer.getData().has("subcategory")) {
					if (modelContainer.getData().getAsJsonArray("subcategory")
							.size() > 0) {
						listVideoSubCat.clear();
						listVideoSubCat
								.addAll(parseVideoSubCatCategory(modelContainer
										.getData()
										.getAsJsonArray("subcategory")
										.toString()));
					}

				}

				adapter.notifyDataSetChanged();
			}

		};
	};

	public void showPicker(PickerType type) {

		Picker picker = Picker.newInstance(0);
		picker.setData(type, listVideoSubCat, PickerCallback,
				"Select your Category");
		picker.show(getChildFragmentManager(), null);
	}

	PickerItemSelectedListener PickerCallback = new PickerItemSelectedListener() {

		@Override
		public void onPickerItemSelected(BaseType item) {

			switch (item.getType()) {
			case VIDEO_CATEGORY_TYPE:
				VideoSubCat mdata = (VideoSubCat) item;
				Log.e("SUB_CAT", "id is: " + mdata.getId());
				Log.e("SUB_CAT", "name is: " + mdata.getName());
				pageNumber = 1;
				initApiCall(pageNumber, mdata.getId());

				txtCategoryTitle.setText(mdata.getName());

				break;
			default:
				break;
			}

		}
	};

	public ArrayList<VideoSubCat> parseVideoSubCatCategory(String object) {
		ArrayList<VideoSubCat> allSubCategory = new ArrayList<VideoSubCat>();
		allSubCategory = new Gson().fromJson(object,
				new TypeToken<ArrayList<VideoSubCat>>() {
				}.getType());
		return allSubCategory;
	}

	private void doReadLater(int i) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();

		params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());
		params.put(RequestKeyHelper.POST_ID, listOfVideo.get(i).getId());

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

	public class VideoAdapter extends BaseAdapter {

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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			final ViewHolder holder;

			if (convertView == null) {
				holder = new ViewHolder();

				convertView = LayoutInflater.from(
						VideoFragment.this.getActivity()).inflate(
						R.layout.row_video_list, parent, false);

				holder.progressBar = (ProgressBar) convertView
						.findViewById(R.id.progressBar);
				holder.imgThumbnail = (ImageView) convertView
						.findViewById(R.id.imgThumbnail);
				holder.txtTitle = (TextView) convertView
						.findViewById(R.id.txtTitle);
				holder.txtCategoryName = (TextView) convertView
						.findViewById(R.id.txtCategoryName);
				holder.txtViewCount = (TextView) convertView
						.findViewById(R.id.txtViewCount);
				holder.imgShareDots = (ImageView) convertView
						.findViewById(R.id.imgShareDots);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (listOfVideo.size() > 0) {
				holder.txtTitle.setText(listOfVideo.get(position).getTitle());
				holder.txtCategoryName.setText(listOfVideo.get(position)
						.getCategoryName());
				holder.txtViewCount.setText(listOfVideo.get(position)
						.getViewCount());

				if (listOfVideo.get(position).getImages().size() > 0)
					SchoolApp.getInstance().displayUniversalImage(
							listOfVideo.get(position).getImages().get(0),
							holder.imgThumbnail, holder.progressBar);
			}

			holder.imgShareDots.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.e("CLICKED", "sahre dots");

					PopupMenu popup = new PopupMenu(VideoFragment.this
							.getActivity(), holder.imgShareDots);
					popup.getMenuInflater().inflate(
							R.menu.popup_menu_share_video, popup.getMenu());

					popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
						public boolean onMenuItemClick(MenuItem item) {
							// Toast.makeText(SchoolSearchFragment.this.getActivity(),"You Clicked : "
							// + item.getTitle(),Toast.LENGTH_SHORT).show();

							Log.e("POP_UP", "title: " + item.getTitle());
							switch (item.getItemId()) {

							case R.id.itemShare:
								Log.e("POP_UP", "id: " + item.getItemId());

								share(listOfVideo.get(position).getVideoUrl());

								break;

							case R.id.itemViewLater:
								Log.e("POP_UP", "title: " + item.getItemId());

								if (UserHelper.isLoggedIn())
									doReadLater(position);
								else
									showCustomDialog(
											"READ LATER",
											R.drawable.read_later_red_icon,
											getResources().getString(
													R.string.read_later_msg)
													+ "\n"
													+ getResources()
															.getString(
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

	class ViewHolder {
		ProgressBar progressBar;
		ImageView imgThumbnail;
		TextView txtTitle;
		TextView txtCategoryName;
		TextView txtViewCount;
		ImageView imgShareDots;

	}

	private void share(String url) {
		Intent share = new Intent(android.content.Intent.ACTION_SEND);
		share.setType("text/plain");
		share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

		share.putExtra(Intent.EXTRA_TEXT, url);

		startActivity(Intent.createChooser(share, "Share this video via"));
	}

	public void showCustomDialog(String headerText, int imgResId,
			String descriptionText) {

		PopupDialog picker = PopupDialog.newInstance(0);
		picker.setData(headerText, descriptionText, imgResId, getActivity());
		picker.show(getChildFragmentManager(), null);
	}

}
