package com.champs21.schoolapp.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.champs21.freeversion.AssesmentActivity;
import com.champs21.freeversion.GoodReadActivity;
import com.champs21.freeversion.HomeContainerActivity;
import com.champs21.freeversion.SingleItemShowActivity;
import com.champs21.schoolapp.R;
import com.champs21.schoolapp.model.AddData;
import com.champs21.schoolapp.model.FreeVersionPost;
import com.champs21.schoolapp.model.GoodReadPost;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.AppUtility;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.RequestKeyHelper;
import com.champs21.schoolapp.utils.SchoolApp;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.utils.UserHelper;
import com.champs21.schoolapp.viewhelpers.CustomButton;
import com.champs21.schoolapp.viewhelpers.CustomRhombusIcon;
import com.champs21.schoolapp.viewhelpers.PagerContainer;
import com.champs21.schoolapp.viewhelpers.PopupDialog;
import com.champs21.schoolapp.viewhelpers.ResizableImageView;
import com.champs21.schoolapp.viewhelpers.UIHelper;
import com.champs21.schoolapp.viewhelpers.UninterceptableViewPager;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class GoodReadPostFragment extends Fragment implements OnClickListener {

	UIHelper uiHelper;
	private ListView listGoodread;
	private GoodReadAdapter fitnessAdapter;
	private int goodReadFolderIndex = 0;
	private String folderName = "";
	private String currentTabKey = "";
	private ArrayList<FreeVersionPost> allGooadReadPost = new ArrayList<FreeVersionPost>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UIHelper(getActivity());
		setUpImageUtis();
		fitnessAdapter = new GoodReadAdapter(getActivity(),
				new ArrayList<FreeVersionPost>());
		goodReadFolderIndex = getArguments().getInt("folderIndex");
		folderName = getArguments().getString("folderName");
		allGooadReadPost.clear();
		allGooadReadPost.addAll(((GoodReadActivity) getActivity())
				.getPostList(goodReadFolderIndex));
		setupListAndNotify();
		Log.e("SIZE OF ALLGOODREADPOST:", "" + allGooadReadPost.size());
	}

	private void setupListAndNotify() {
		for (int i = 0; i < allGooadReadPost.size(); i++) {

			if (allGooadReadPost.get(i).getPostType().equals("2")) {
				fitnessAdapter.addSeparatorItem(allGooadReadPost.get(i));

			} else if (allGooadReadPost.get(i).getPostType().equals("1")) {

				switch (allGooadReadPost.get(i).getNormal_post_type()) {
				case 0:
					if (allGooadReadPost.get(i).getImages().size() == 1) {
						fitnessAdapter
								.addSampleOriginalImageItem(allGooadReadPost
										.get(i));
					} else
						fitnessAdapter.addItem(allGooadReadPost.get(i));
					break;
				case 1:
					fitnessAdapter.addSampleOneItem(allGooadReadPost.get(i));
					break;
				case 2:
					fitnessAdapter.addSampleTwoItem(allGooadReadPost.get(i));
					break;
				case 3:
					fitnessAdapter.addSampleThreeItem(allGooadReadPost.get(i));
					break;
				case 4:
					fitnessAdapter.addSampleFourItem(allGooadReadPost.get(i));
					break;
				case 5:
					fitnessAdapter.addSampleFiveItem(allGooadReadPost.get(i));
					break;
				case 6:
					fitnessAdapter.addSampleSixItem(allGooadReadPost.get(i));
					break;
				case 7:
					fitnessAdapter.addSampleSevenItem(allGooadReadPost.get(i));
					break;
				default:
					break;
				}
			} else if (allGooadReadPost.get(i).getPostType().equals("0")) {
				allGooadReadPost.get(i).setCategoryId("-1");
				Log.e("Banner ITEM FOUND", "");
				fitnessAdapter.addBannerItem(allGooadReadPost.get(i));
			} else if (allGooadReadPost.get(i).getPostType().equals("3")) {
				fitnessAdapter.addSpellItem(allGooadReadPost.get(i));
			} else if (allGooadReadPost.get(i).getPostType().equals("4")) {
				fitnessAdapter.addQuizItem(allGooadReadPost.get(i));
			}
		}
		fitnessAdapter.notifyDataSetChanged();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_goodread_post_view,
				container, false);
		listGoodread = (ListView) view.findViewById(R.id.list_goodread);
		listGoodread.setAdapter(fitnessAdapter);
		fitnessAdapter.notifyDataSetChanged();
		return view;
	}

	public static GoodReadPostFragment newInstance(int index,
			ArrayList<FreeVersionPost> allpost, String folderName) {
		GoodReadPostFragment f = new GoodReadPostFragment();
		Bundle args = new Bundle();
		args.putInt("folderIndex", index);
		args.putString("folderName", folderName);
		f.setArguments(args);
		return f;
	}

	private void doWow(int i) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());
		params.put(RequestKeyHelper.POST_ID, allGooadReadPost.get(i).getId());
		allGooadReadPost.get(i).setCan_wow(0);
		AppRestClient.post(URLHelper.URL_FREE_VERSION_ADDWOW, params,
				wowHandler);
	}

	AsyncHttpResponseHandler wowHandler = new AsyncHttpResponseHandler() {
		public void onStart() {
			// uiHelper.showLoadingDialog("Please wait...");
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
				// uiHelper.showMessage("Successfully added wow");
				Log.e("WOW HOISE", "HEHEHEHE");
			}
		};
	};

	public void showCustomDialog(String headerText, int imgResId,
			String descriptionText) {

		PopupDialog picker = PopupDialog.newInstance(0);
		picker.setData(headerText, descriptionText, imgResId, getActivity());
		picker.show(getChildFragmentManager(), null);
	}

	/*
	 * public class GoodReadAdapter extends BaseAdapter {
	 * 
	 * private Context context;
	 * 
	 * public GoodReadAdapter(Context context) { this.context = context; }
	 * 
	 * @Override public int getCount() { return allGooadReadPost.size(); }
	 * 
	 * @Override public Object getItem(int position) { return
	 * allGooadReadPost.get(position); }
	 * 
	 * @Override public long getItemId(int position) { return position; }
	 * 
	 * @Override public View getView(int position, View convertView, ViewGroup
	 * parent) {
	 * 
	 * final int i = position;
	 * 
	 * ViewHolder holder;
	 * 
	 * if (convertView == null) { holder = new ViewHolder();
	 * 
	 * convertView = LayoutInflater.from(this.context).inflate(
	 * R.layout.fragment_freeversion_home_adapter, parent, false);
	 * holder.imgViewCategoryMenuIcon = (CustomRhombusIcon) convertView
	 * .findViewById(R.id.imgViewCategoryMenuIcon); holder.txtCategoryName =
	 * (TextView) convertView .findViewById(R.id.txtCategoryName);
	 * holder.txtPublishedDateString = (TextView) convertView
	 * .findViewById(R.id.txtPublishedDateString); holder.container =
	 * (PagerContainer) convertView .findViewById(R.id.pager_container_row);
	 * holder.viewPager = (UninterceptableViewPager) holder.container
	 * .getViewPager(); // holder.viewPager.setPageMargin(10);
	 * 
	 * holder.txtSummary = (TextView) convertView
	 * .findViewById(R.id.txtSummary); holder.btnWow = (CustomButton)
	 * convertView .findViewById(R.id.btnWow); holder.btnShare = (CustomButton)
	 * convertView .findViewById(R.id.btnShare); holder.seenTextView =
	 * (TextView) convertView .findViewById(R.id.fv_post_tv_seen);
	 * holder.btnRemove = (CustomButton) convertView
	 * .findViewById(R.id.btnReadLater);
	 * holder.btnRemove.setTitleText("Remove");
	 * holder.btnRemove.setImage(R.drawable.remove_normal);
	 * holder.btnShare.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { if
	 * (AppUtility.isInternetConnected()) { ((HomeBaseActivity)
	 * getActivity()).sharePostUniversal(allGooadReadPost
	 * .get(Integer.parseInt(v.getTag().toString()))); } } });
	 * holder.btnWow.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { if (UserHelper.isLoggedIn()){
	 * doWow(Integer.parseInt(v.getTag().toString())); CustomButton w =
	 * (CustomButton)v; w.setTitleColor(getResources().getColor(R.color.red));
	 * w.setImage(R.drawable.wow_icon_tap); }else showCustomDialog( "WOW",
	 * R.drawable.read_later_red_icon, getResources().getString(
	 * R.string.wow_msg) + "\n" + getResources().getString(
	 * R.string.not_logged_in_msg)); } });
	 * holder.btnRemove.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub String postId =
	 * allGooadReadPost.get(Integer.parseInt(v.getTag().toString())).getId();
	 * removeGoodread(postId);
	 * allGooadReadPost.remove(allGooadReadPost.get(Integer
	 * .parseInt(v.getTag().toString()))); } }); convertView.setTag(holder); }
	 * 
	 * else { holder = (ViewHolder) convertView.getTag(); }
	 * 
	 * if (allGooadReadPost.size() > 0) {
	 * 
	 * // if(map.get(currentTabKey).get(position).getCategoryMenuIconUrl() // !=
	 * null || //
	 * map.get(currentTabKey).get(position).getCategoryMenuIconUrl().length() //
	 * > 0) //
	 * SchoolApp.getInstance().displayUniversalImage(map.get(currentTabKey
	 * ).get(position).getCategoryMenuIconUrl(), //
	 * holder.imgViewCategoryMenuIcon);
	 * 
	 * int resId = Integer.parseInt(allGooadReadPost.get(position)
	 * .getCategoryId()); if (resId != -1) {
	 * holder.imgViewCategoryMenuIcon.setIconImage(AppUtility
	 * .getResourceImageId(resId, true, false)); }
	 * 
	 * holder.txtCategoryName.setText(allGooadReadPost.get(position)
	 * .getCategoryName());
	 * holder.txtPublishedDateString.setText(allGooadReadPost.get(position)
	 * .getPublishedDateString());
	 * holder.txtSummary.setText(allGooadReadPost.get(position).getSummary());
	 * holder.seenTextView.setText(allGooadReadPost.get(position)
	 * .getSeenCount()); holder.btnRemove.setTag(position);
	 * holder.btnWow.setTag(""+position);
	 * if(allGooadReadPost.get(position).getCan_wow()==0){
	 * holder.btnWow.setTitleColor(getResources().getColor(R.color.red));
	 * holder.btnWow.setImage(R.drawable.wow_icon_tap); }else {
	 * holder.btnWow.setTitleColor(getResources().getColor(R.color.black));
	 * holder.btnWow.setImage(R.drawable.wow_icon_normal); }
	 * 
	 * // if(map.get(currentTabKey).get(position).getMobileImageUrl() // != null
	 * || // map.get(currentTabKey).get(position).getMobileImageUrl().length()
	 * // > 0) //
	 * SchoolApp.getInstance().displayUniversalImage(map.get(currentTabKey
	 * ).get(position).getMobileImageUrl(), // holder.imgMobileImage);
	 * 
	 * Log.e("PUBLISHED_DATE", "is:" +
	 * allGooadReadPost.get(position).getPublishedDateString());
	 * 
	 * ArrayList<String> imgPaths = allGooadReadPost.get(position).getImages();
	 * holder.imgAdapter = new ImagePagerAdapter(imgPaths);
	 * 
	 * // Log.e("IMAGE PATH",listData.get(position).getImages().get(0)+"");
	 * 
	 * // If hardware acceleration is enabled, you should also remove //
	 * clipping on the pager for its children.
	 * 
	 * holder.viewPager.setAdapter(holder.imgAdapter);
	 * holder.viewPager.setTag("" + position); //
	 * holder.imgAdapter.notifyDataSetChanged();
	 * holder.viewPager.setOffscreenPageLimit(holder.imgAdapter .getCount()); //
	 * A little space between pages holder.viewPager.setPageMargin(15);
	 * 
	 * // If hardware acceleration is enabled, you should also remove //
	 * clipping on the pager for its children.
	 * holder.viewPager.setClipChildren(false); //
	 * holder.viewPager.setOnTouchListener(new OnTouchListener() { private float
	 * pointX; private float pointY; private int tolerance = 50;
	 * 
	 * @Override public boolean onTouch(View v, MotionEvent event) { switch
	 * (event.getAction()) { case MotionEvent.ACTION_MOVE: return false; // This
	 * is important, if you return // TRUE the action of swipe will not // take
	 * place. case MotionEvent.ACTION_DOWN: pointX = event.getX(); pointY =
	 * event.getY(); break; case MotionEvent.ACTION_UP: boolean sameX = pointX +
	 * tolerance > event.getX() && pointX - tolerance < event.getX(); boolean
	 * sameY = pointY + tolerance > event.getY() && pointY - tolerance <
	 * event.getY(); if (sameX && sameY) {
	 * 
	 * FreeVersionPost item = (FreeVersionPost) adapter
	 * .getItem(Integer.parseInt(v.getTag() .toString()));
	 * 
	 * Intent intent = new Intent( getActivity(), SingleItemShowActivity.class);
	 * intent.putExtra(AppConstant.ITEM_ID, item.getId());
	 * intent.putExtra(AppConstant.ITEM_CAT_ID, item.getCategoryId());
	 * intent.putExtra(AppConstant.GOING_GOODREAD, "OK"); startActivity(intent);
	 * } } return false; } });
	 * 
	 * } else { Log.e("FREE_HOME_API", "array is empty!"); }
	 * 
	 * return convertView; } }
	 */

	/*
	 * class ViewHolder { CustomRhombusIcon imgViewCategoryMenuIcon; TextView
	 * txtCategoryName; TextView txtPublishedDateString; PagerContainer
	 * container; UninterceptableViewPager viewPager; TextView txtSummary;
	 * CustomButton btnWow; CustomButton btnShare; ImagePagerAdapter imgAdapter;
	 * TextView seenTextView; CustomButton btnRemove;
	 * 
	 * }
	 */

	protected void removeGoodread(String postId) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		Log.e("USERID ", UserHelper.getUserFreeId() + " " + postId + " "
				+ folderName);
		params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());
		params.put(RequestKeyHelper.POST_ID, postId);
		params.put(RequestKeyHelper.FOLDER_NAME, folderName);

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
				fitnessAdapter.notifyDataSetChanged();

			}
		};
	};
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	private class GoodReadAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		// ViewHolder holder;

		private ArrayList<FreeVersionPost> list;
		private static final int TYPE_ITEM = 0;
		private static final int TYPE_ADD = 1;
		private static final int TYPE_BANNER = 2;
		private static final int TYPE_SPELLING_GAME = 3;
		private static final int TYPE_QUIZ_GAME = 4;
		private static final int TYPE_ONE = 5;
		private static final int TYPE_TWO = 6;
		private static final int TYPE_THREE = 7;
		private static final int TYPE_FOUR = 8;
		private static final int TYPE_FIVE = 9;
		private static final int TYPE_SIX = 10;
		private static final int TYPE_SEVEN = 11;
		private static final int TYPE_ORIGINAL_IMAGE =12;
		private static final int TYPE_MAX_COUNT = 13;
		private TreeSet<Integer> mSeparatorSet = new TreeSet<Integer>();
		private TreeSet<Integer> mBannerSet = new TreeSet<Integer>();
		private TreeSet<Integer> mSpellSet = new TreeSet<Integer>();
		private TreeSet<Integer> mQuizSet = new TreeSet<Integer>();
		private TreeSet<Integer> mSampleOne = new TreeSet<Integer>();
		private TreeSet<Integer> mSampleTwo = new TreeSet<Integer>();
		private TreeSet<Integer> mSampleThree = new TreeSet<Integer>();
		private TreeSet<Integer> mSampleFour = new TreeSet<Integer>();
		private TreeSet<Integer> mSampleFive = new TreeSet<Integer>();
		private TreeSet<Integer> mSampleSix = new TreeSet<Integer>();
		private TreeSet<Integer> mSampleSeven = new TreeSet<Integer>();
		private TreeSet<Integer> mSampleOriginalImage = new TreeSet<Integer>();

		public GoodReadAdapter(Context context, ArrayList<FreeVersionPost> list) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(context);
			this.list = list;
		}

		public void clearList() {
			this.list.clear();
			this.mSeparatorSet.clear();
			this.mBannerSet.clear();
			this.mSpellSet.clear();
			this.mQuizSet.clear();
			this.mSampleOne.clear();
			this.mSampleTwo.clear();
			this.mSampleThree.clear();
			this.mSampleFour.clear();
			this.mSampleFive.clear();
			this.mSampleSix.clear();
			this.mSampleSeven.clear();
			this.mSampleOriginalImage.clear();
		}

		public void addItem(final FreeVersionPost item) {
			list.add(item);
			// The notification is not necessary since the items are not added
			// dynamically
			// notifyDataSetChanged();
		}

		public void addSeparatorItem(final FreeVersionPost item) {
			list.add(item);
			// Save separator position
			// This is used to check whether the element is a separator or an
			// item
			mSeparatorSet.add(list.size() - 1);
			// The notification is not necessary since the separators are not
			// added dynamically
			// notifyDataSetChanged();
		}

		public void addSampleOneItem(final FreeVersionPost item) {
			list.add(item);
			mSampleOne.add(list.size() - 1);
		}

		public void addSampleTwoItem(final FreeVersionPost item) {
			list.add(item);
			mSampleTwo.add(list.size() - 1);
		}

		public void addSampleThreeItem(final FreeVersionPost item) {
			list.add(item);
			mSampleThree.add(list.size() - 1);
		}

		public void addSampleFourItem(final FreeVersionPost item) {
			list.add(item);
			mSampleFour.add(list.size() - 1);
		}

		public void addSampleFiveItem(final FreeVersionPost item) {
			list.add(item);
			mSampleFive.add(list.size() - 1);
		}

		public void addSampleSixItem(final FreeVersionPost item) {
			list.add(item);
			mSampleSix.add(list.size() - 1);
		}

		public void addSampleSevenItem(final FreeVersionPost item) {
			list.add(item);
			mSampleSeven.add(list.size() - 1);
		}
		
		public void addSampleOriginalImageItem(final FreeVersionPost item) {
			list.add(item);
			mSampleOriginalImage.add(list.size() - 1);
		}
		
		public void addBannerItem(final FreeVersionPost item) {
			list.add(item);
			mBannerSet.add(list.size() - 1);
		}

		public void addSpellItem(final FreeVersionPost item) {
			list.add(item);
			mSpellSet.add(list.size() - 1);
		}

		public void addQuizItem(final FreeVersionPost item) {
			list.add(item);
			mQuizSet.add(list.size() - 1);
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
		public FreeVersionPost getItem(int position) {
			return list.get(position);
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

		/**
		 * Make a view to hold each row.
		 * 
		 * @see android.widget.ListAdapter#getView(int, android.view.View,
		 *      android.view.ViewGroup)
		 */
		// public View getView(int position, View convertView, ViewGroup parent)
		// {
		//
		//
		// }

		class ViewHolder {
			CustomRhombusIcon imgViewCategoryMenuIcon;
			TextView txtCategoryName;
			TextView txtPublishedDateString;
			PagerContainer container;
			UninterceptableViewPager viewPager;
			TextView txtSummary;
			TextView txtTitle;
			CustomButton btnWow;
			CustomButton btnShare;
			ImagePagerAdapter imgAdapter;
			TextView seenTextView;
			ImageView bannerView;
			ImageView gameImage;
			ImageView gameImageIcon;
			LinearLayout pagerBlock;
			LinearLayout textLay;
			CustomButton btnRemove;
			ResizableImageView singleImage;
			LinearLayout wowCommentLayout;

			TextView wowCountTextView;
			// TextView commentCountTextView;
			// new variables
			TextView tvHomeSummery;
			TextView tvHomeTitle;
			TextView tvHomeAuthorName, tvHomeShortTitle;

			ImageView ivBg;
			ImageView ivCenter;
			ImageView ivHomeImageOne, ivHomeImageTwo, ivHomeImageThree;

			ImageView relHomeAuthorPic;
			ProgressBar pb1, pb2, pb3;
			public ImageView assesmentIcon;
			public ProgressBar singlePb;

		}

		@Override
		public int getItemViewType(int position) {
			if (mSeparatorSet.contains(position))
				return TYPE_ADD;
			if (mBannerSet.contains(position))
				return TYPE_BANNER;
			if (mSpellSet.contains(position))
				return TYPE_SPELLING_GAME;
			if (mQuizSet.contains(position))
				return TYPE_QUIZ_GAME;
			if (mSampleOne.contains(position))
				return TYPE_ONE;
			if (mSampleTwo.contains(position))
				return TYPE_TWO;
			if (mSampleThree.contains(position))
				return TYPE_THREE;
			if (mSampleFour.contains(position))
				return TYPE_FOUR;
			if (mSampleFive.contains(position))
				return TYPE_FIVE;
			if (mSampleSix.contains(position))
				return TYPE_SIX;
			if (mSampleSeven.contains(position))
				return TYPE_SEVEN;
			if (mSampleOriginalImage.contains(position))
				return TYPE_ORIGINAL_IMAGE;
			return TYPE_ITEM;

		}
		
		public void removeItemViewType(int position){
			if (mSeparatorSet.contains(position))
				mSeparatorSet.remove(position);
			if (mBannerSet.contains(position))
				mBannerSet.remove(position);
			if (mSpellSet.contains(position))
				mSpellSet.remove(position);
			if (mQuizSet.contains(position))
				mQuizSet.remove(position);
			if (mSampleOne.contains(position))
				mSampleOne.remove(position);
			if (mSampleTwo.contains(position))
				mSampleTwo.remove(position);
			if (mSampleThree.contains(position))
				mSampleThree.remove(position);
			if (mSampleFour.contains(position))
				mSampleFour.remove(position);
			if (mSampleFive.contains(position))
				mSampleFive.remove(position);
			if (mSampleSix.contains(position))
				mSampleSix.remove(position);
			if (mSampleSeven.contains(position))
				mSampleSeven.remove(position);
			if (mSampleOriginalImage.contains(position))
				mSampleOriginalImage.remove(position);
			

		}
		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return TYPE_MAX_COUNT;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final int i = position;
			// FreeVersionPost post=list.get(position);
			// A ViewHolder keeps references to children views to avoid
			// unneccessary calls
			// to findViewById() on each row.
			ViewHolder holder = null;
			int type = getItemViewType(position);
			// Log.e("TYPETYPE TYPE", "" + type);
			// When convertView is not null, we can reuse it directly, there is
			// no need
			// to reinflate it. We only inflate a new View when the convertView
			// supplied
			// by ListView is null.
			if (convertView == null) {

				// Creates a ViewHolder and store references to the two children
				// views
				// we want to bind data to.
				holder = new ViewHolder();

				switch (type) {
				case TYPE_ITEM:
					convertView = mInflater.inflate(
							R.layout.fragment_freeversion_home_adapter, parent,
							false);
					setHolder(holder, convertView, i);

					holder.container = (PagerContainer) convertView
							.findViewById(R.id.pager_container_row);
					holder.viewPager = (UninterceptableViewPager) holder.container
							.getViewPager();
					break;
				case TYPE_ORIGINAL_IMAGE:
					convertView = mInflater.inflate(
							R.layout.fragment_freeversion_sample_original_image, parent,
							false);
					setHolder(holder, convertView, i);
					holder.singlePb = (ProgressBar) convertView
							.findViewById(R.id.mypb);
					holder.singleImage = (ResizableImageView) convertView.findViewById(R.id.iv_single_image);
					break;
				case TYPE_ONE:
					convertView = mInflater.inflate(
							R.layout.fragment_freeversion_sample_one, parent,
							false);
					setHolder(holder, convertView, i);
					holder.textLay.setVisibility(View.GONE);
					holder.tvHomeSummery = (TextView) convertView
							.findViewById(R.id.tv_home_summery);
					holder.tvHomeTitle = (TextView) convertView
							.findViewById(R.id.tv_home_title);
					holder.tvHomeAuthorName = (TextView) convertView
							.findViewById(R.id.tv_home_author_name);
					holder.ivBg = (ImageView) convertView
							.findViewById(R.id.iv_bg);
					holder.pb1 = (ProgressBar) convertView
							.findViewById(R.id.pb1);
					break;

				case TYPE_TWO:
					convertView = mInflater.inflate(
							R.layout.fragment_freeversion_sample_two, parent,
							false);
					setHolder(holder, convertView, i);
					holder.textLay.setVisibility(View.GONE);
					holder.ivBg = (ImageView) convertView
							.findViewById(R.id.iv_bg);
					holder.ivCenter = (ImageView) convertView
							.findViewById(R.id.iv_center);
					holder.tvHomeSummery = (TextView) convertView
							.findViewById(R.id.tv_home_summery);
					holder.pb1 = (ProgressBar) convertView
							.findViewById(R.id.pb1);
					break;

				case TYPE_THREE:
					convertView = mInflater.inflate(
							R.layout.fragment_freeversion_sample_three, parent,
							false);
					setHolder(holder, convertView, i);
					holder.ivHomeImageOne = (ImageView) convertView
							.findViewById(R.id.iv_home_img1);
					holder.ivHomeImageTwo = (ImageView) convertView
							.findViewById(R.id.iv_home_img2);
					holder.ivHomeImageThree = (ImageView) convertView
							.findViewById(R.id.iv_home_img3);
					holder.pb1 = (ProgressBar) convertView
							.findViewById(R.id.pb1);
					holder.pb2 = (ProgressBar) convertView
							.findViewById(R.id.pb2);
					holder.pb3 = (ProgressBar) convertView
							.findViewById(R.id.pb3);
					break;

				case TYPE_FOUR:

					convertView = mInflater.inflate(
							R.layout.fragment_freeversion_sample_four, parent,
							false);
					setHolder(holder, convertView, i);
					holder.textLay.setVisibility(View.GONE);
					holder.relHomeAuthorPic = (ImageView) convertView
							.findViewById(R.id.rel_home_author_pic);
					holder.tvHomeShortTitle = (TextView) convertView
							.findViewById(R.id.tv_home_short_title);
					holder.tvHomeTitle = (TextView) convertView
							.findViewById(R.id.tv_home_title);
					holder.tvHomeSummery = (TextView) convertView
							.findViewById(R.id.tv_home_summery);
					break;

				case TYPE_FIVE:

					convertView = mInflater.inflate(
							R.layout.fragment_freeversion_sample_five, parent,
							false);
					setHolder(holder, convertView, i);
					holder.textLay.setVisibility(View.GONE);
					holder.ivBg = (ImageView) convertView
							.findViewById(R.id.iv_bg);
					holder.tvHomeShortTitle = (TextView) convertView
							.findViewById(R.id.tv_home_short_title);
					holder.pb1 = (ProgressBar) convertView
							.findViewById(R.id.pb1);

					break;

				case TYPE_SIX:
					convertView = mInflater.inflate(
							R.layout.fragment_freeversion_sample_six, parent,
							false);
					setHolder(holder, convertView, i);
					holder.textLay.setVisibility(View.GONE);
					holder.tvHomeTitle = (TextView) convertView
							.findViewById(R.id.tv_home_title);
					holder.tvHomeShortTitle = (TextView) convertView
							.findViewById(R.id.tv_home_short_title);
					holder.ivHomeImageOne = (ImageView) convertView
							.findViewById(R.id.iv_home_img1);
					holder.ivHomeImageTwo = (ImageView) convertView
							.findViewById(R.id.iv_home_img2);
					holder.pb1 = (ProgressBar) convertView
							.findViewById(R.id.pb1);
					holder.pb2 = (ProgressBar) convertView
							.findViewById(R.id.pb2);
					break;

				case TYPE_SEVEN:
					convertView = mInflater.inflate(
							R.layout.fragment_freeversion_sample_seven, parent,
							false);
					setHolder(holder, convertView, i);
					holder.textLay.setVisibility(View.GONE);
					holder.ivHomeImageOne = (ImageView) convertView
							.findViewById(R.id.iv_home_img1);
					holder.tvHomeShortTitle = (TextView) convertView
							.findViewById(R.id.tv_home_short_title);
					holder.tvHomeSummery = (TextView) convertView
							.findViewById(R.id.tv_home_summery);
					holder.pb1 = (ProgressBar) convertView
							.findViewById(R.id.pb1);
					break;
				case TYPE_ADD:
					convertView = mInflater.inflate(R.layout.pager_add_view,
							parent, false);

					holder.container = (PagerContainer) convertView
							.findViewById(R.id.pager_container_row);
					holder.viewPager = (UninterceptableViewPager) holder.container
							.getViewPager();
					break;
				case TYPE_BANNER:

					convertView = mInflater.inflate(R.layout.banner_view,
							parent, false);
					holder.bannerView = (ImageView) convertView
							.findViewById(R.id.iv_banner);

					break;
				case TYPE_SPELLING_GAME:

					convertView = mInflater.inflate(R.layout.spelling_view,
							parent, false);
					holder.gameImage = (ImageView) convertView
							.findViewById(R.id.post_iv_game_banner);
					holder.gameImageIcon = (ImageView) convertView
							.findViewById(R.id.post_iv_game_icon);
					holder.gameImage
							.setImageResource(R.drawable.words_of_the_day);
					holder.gameImageIcon
							.setImageResource(R.drawable.words_of_the_day_icon);
					break;

				case TYPE_QUIZ_GAME:
					convertView = mInflater.inflate(R.layout.spelling_view,
							parent, false);
					holder.gameImage = (ImageView) convertView
							.findViewById(R.id.post_iv_game_banner);
					holder.gameImageIcon = (ImageView) convertView
							.findViewById(R.id.post_iv_game_icon);
					holder.gameImage
							.setImageResource(R.drawable.dailyquiz_banner);
					holder.gameImageIcon.setImageResource(R.drawable.quiz_icon);

					break;
				default:
					break;
				}

				/*
				 * holder.container = (PagerContainer) convertView
				 * .findViewById(R.id.pager_container_row); holder.viewPager =
				 * (UninterceptableViewPager) holder.container .getViewPager();
				 */
				// holder.viewPager.setPageMargin(-10);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			int resId = Integer.parseInt(list.get(position).getCategoryId());
			if (list.size() > 0) {
				FreeVersionPost itm = list.get(position);
				switch (type) {
				case TYPE_ITEM:
					// if (list.get(position).getCategoryMenuIconUrl() != null
					// || list.get(position).getCategoryMenuIconUrl()
					// .length() > 0)
					// SchoolApp.getInstance().displayUniversalImage(
					// list.get(position).getCategoryMenuIconUrl(),
					// holder.imgViewCategoryMenuIcon);

					setDataToView(holder, resId, position);

					if (list.get(position).getImages().size() == 0) {
						holder.pagerBlock.setVisibility(View.GONE);
						holder.txtTitle.setPadding(0, 20, 0, 0);
					} else
						holder.pagerBlock.setVisibility(View.VISIBLE);
					if(!list.get(position).getAssessment_id().equals("")){
						holder.assesmentIcon.setVisibility(View.VISIBLE);
					}else holder.assesmentIcon.setVisibility(View.GONE);
					setAddAndPostPager(holder.imgAdapter, holder.viewPager,
							position);
					break;
				case TYPE_ORIGINAL_IMAGE:
					if (list.get(position).getImages().size() == 0) {
						holder.pagerBlock.setVisibility(View.GONE);
						holder.txtTitle.setPadding(0, 20, 0, 0);
					} else
						holder.pagerBlock.setVisibility(View.VISIBLE);
					setDataToView(holder, resId, position);
					holder.pagerBlock
					.setOnClickListener(GoodReadPostFragment.this);
					
					if(itm.getImages().size()>0){
						SchoolApp.getInstance().displayUniversalImageSingle(
								itm.getImages().get(0), holder.singleImage,
								holder.singlePb);
					}
					if(!list.get(position).getAssessment_id().equals("")){
						holder.assesmentIcon.setVisibility(View.VISIBLE);
					}else holder.assesmentIcon.setVisibility(View.GONE);
					break;
				case TYPE_ONE:
					setDataToView(holder, resId, position);
					holder.pagerBlock
							.setOnClickListener(GoodReadPostFragment.this);
					// summery,title,author,ibg
					holder.tvHomeSummery.setText(itm.getSummary());
					holder.tvHomeTitle.setText(itm.getTitle());
					if (!TextUtils.isEmpty(itm.getAuthor()))
						holder.tvHomeAuthorName.setText(itm.getAuthor());
					if (itm.getImages().size() > 0)
						SchoolApp.getInstance()
								.displayUniversalImage(itm.getImages().get(0),
										holder.ivBg, holder.pb1);
					if(!list.get(position).getAssessment_id().equals("")){
						holder.assesmentIcon.setVisibility(View.VISIBLE);
					}else holder.assesmentIcon.setVisibility(View.GONE);
					break;

				case TYPE_TWO:
					setDataToView(holder, resId, position);
					holder.pagerBlock
							.setOnClickListener(GoodReadPostFragment.this);
					if (itm.getImages().size() > 0)
						SchoolApp.getInstance()
								.displayUniversalImage(itm.getImages().get(0),
										holder.ivBg, holder.pb1);
					if (!TextUtils.isEmpty(itm.getInside_image()))
						SchoolApp.getInstance().displayUniversalImage(
								itm.getInside_image(), holder.ivCenter);
					holder.tvHomeSummery.setText(itm.getSummary());
					if(!list.get(position).getAssessment_id().equals("")){
						holder.assesmentIcon.setVisibility(View.VISIBLE);
					}else holder.assesmentIcon.setVisibility(View.GONE);
					break;

				case TYPE_THREE:
					setDataToView(holder, resId, position);
					holder.pagerBlock
							.setOnClickListener(GoodReadPostFragment.this);

					if (itm.getImages().size() >= 3) {
						SchoolApp.getInstance().displayUniversalImage(
								itm.getImages().get(0), holder.ivHomeImageOne,
								holder.pb1);
						SchoolApp.getInstance().displayUniversalImage(
								itm.getImages().get(1), holder.ivHomeImageTwo,
								holder.pb2);
						SchoolApp.getInstance().displayUniversalImage(
								itm.getImages().get(2),
								holder.ivHomeImageThree, holder.pb3);
					}
					if(!list.get(position).getAssessment_id().equals("")){
						holder.assesmentIcon.setVisibility(View.VISIBLE);
					}else holder.assesmentIcon.setVisibility(View.GONE);
					break;

				case TYPE_FOUR:
					setDataToView(holder, resId, position);
					holder.pagerBlock
							.setOnClickListener(GoodReadPostFragment.this);
					if (!TextUtils.isEmpty(itm.getAuthor_image()))
						SchoolApp.getInstance().displayUniversalImage(
								itm.getAuthor_image(), holder.relHomeAuthorPic);
					holder.tvHomeShortTitle.setText(itm.getShort_title());
					holder.tvHomeTitle.setText(itm.getTitle());
					holder.tvHomeSummery.setText(itm.getSummary());
					if(!list.get(position).getAssessment_id().equals("")){
						holder.assesmentIcon.setVisibility(View.VISIBLE);
					}else holder.assesmentIcon.setVisibility(View.GONE);
					break;

				case TYPE_FIVE:
					setDataToView(holder, resId, position);
					holder.pagerBlock
							.setOnClickListener(GoodReadPostFragment.this);
					if (itm.getImages().size() > 0)
						SchoolApp.getInstance()
								.displayUniversalImage(itm.getImages().get(0),
										holder.ivBg, holder.pb1);
					holder.tvHomeShortTitle.setText(itm.getShort_title());
					if(!list.get(position).getAssessment_id().equals("")){
						holder.assesmentIcon.setVisibility(View.VISIBLE);
					}else holder.assesmentIcon.setVisibility(View.GONE);
					break;

				case TYPE_SIX:
					setDataToView(holder, resId, position);
					holder.pagerBlock
							.setOnClickListener(GoodReadPostFragment.this);
					holder.tvHomeShortTitle.setText(itm.getShort_title());
					holder.tvHomeTitle.setText(itm.getTitle());
					holder.textLay.setVisibility(View.GONE);
					if (itm.getImages().size() >= 2) {
						SchoolApp.getInstance().displayUniversalImage(
								itm.getImages().get(0), holder.ivHomeImageOne,
								holder.pb1);
						SchoolApp.getInstance().displayUniversalImage(
								itm.getImages().get(1), holder.ivHomeImageTwo,
								holder.pb2);
					}
					if(!list.get(position).getAssessment_id().equals("")){
						holder.assesmentIcon.setVisibility(View.VISIBLE);
					}else holder.assesmentIcon.setVisibility(View.GONE);
					break;

				case TYPE_SEVEN:
					holder.pagerBlock
							.setOnClickListener(GoodReadPostFragment.this);
					setDataToView(holder, resId, position);
					holder.tvHomeSummery.setText(itm.getSummary());
					holder.tvHomeShortTitle.setText(itm.getShort_title());
					if (itm.getImages().size() > 0) {
						SchoolApp.getInstance().displayUniversalImage(
								itm.getImages().get(0), holder.ivHomeImageOne,
								holder.pb1);
					}
					if(!list.get(position).getAssessment_id().equals("")) {
						holder.assesmentIcon.setVisibility(View.VISIBLE);
					}else holder.assesmentIcon.setVisibility(View.GONE);
					break;
				case TYPE_ADD:
					setAddAndPostPager(holder.imgAdapter, holder.viewPager,
							position);
					break;

				case TYPE_BANNER:
					/*
					 * holder.bannerView.setImageResource(AppUtility
					 * .getResourceImageId(resId, false, false));
					 */
					// Uri uri = Uri.parse("drawable://" +
					// AppUtility.getResourceImageId(resId, false, false));
					/*
					 * imageLoader.displayImage( "drawable://" +
					 * AppUtility.getResourceImageId(resId, false, false),
					 * holder.bannerView);
					 */
					break;
				default:

					break;
				}
				// Log.e("PUBLISHED_DATE", "is: "
				// + list.get(position).getPublishedDateString());
			} else {
				// Log.e("FREE_HOME_API", "array is empty!");
			}

			return convertView;
		}

		private void setDataToView(ViewHolder holder, int resId, int position) {
			// TODO Auto-generated method stub
			if (resId != -1) {
				Log.e("resource id", " " + resId);
				holder.imgViewCategoryMenuIcon.setIconImage(AppUtility
						.getResourceImageId(resId, true, false));

			}
			holder.txtCategoryName
					.setText(list.get(position).getCategoryName());
			holder.txtPublishedDateString.setText(list.get(position)
					.getPublishedDateString());
			holder.txtSummary.setText(list.get(position).getSummary());
			holder.txtTitle.setText(list.get(position).getTitle());
			if (Integer.parseInt(list.get(position).getSeenCount()) >= 1000)
				holder.seenTextView.setText(AppUtility.coolFormat(
						Double.parseDouble(list.get(position).getSeenCount()),
						0));
			else
				holder.seenTextView.setText(list.get(position).getSeenCount());

			if (list.get(position).getWow_count().equals("0")) {
				holder.wowCountTextView.setVisibility(View.GONE);
			} else
				holder.wowCountTextView.setVisibility(View.VISIBLE);

			holder.wowCountTextView.setText(list.get(position).getWow_count()
					+ " WoW");
			// holder.commentCountTextView.setText(list.get(position).)
			holder.txtSummary.setTag("" + position);
			holder.pagerBlock.setTag("" + position);
			holder.btnShare.setTag("" + position);

			holder.wowCommentLayout.setTag("" + position);
			holder.wowCountTextView.setTag("" + position);
			// holder.commentCountTextView.setTag(""+position);

			holder.btnWow.setTag("" + position);
			holder.btnRemove.setTag("" + position);
			/*
			 * if (list.get(position).getCan_wow() == 0) {
			 * holder.btnWow.setTitleColor(getResources()
			 * .getColor(R.color.red));
			 * holder.btnWow.setImage(R.drawable.wow_icon_tap); } else {
			 * holder.btnWow.setTitleColor(getResources().getColor(
			 * R.color.black));
			 * holder.btnWow.setImage(R.drawable.wow_icon_normal); }
			 */
		}

		private void setHolder(ViewHolder holder, View convertView, final int i) {
			// TODO Auto-generated method stub
			holder.textLay = (LinearLayout) convertView
					.findViewById(R.id.textlay);
			holder.textLay.setVisibility(View.VISIBLE);
			holder.imgViewCategoryMenuIcon = (CustomRhombusIcon) convertView
					.findViewById(R.id.imgViewCategoryMenuIcon);
			holder.txtCategoryName = (TextView) convertView
					.findViewById(R.id.txtCategoryName);
			holder.txtPublishedDateString = (TextView) convertView
					.findViewById(R.id.txtPublishedDateString);
			holder.assesmentIcon = (ImageView) convertView
					.findViewById(R.id.iv_assesment_icon);
			holder.txtSummary = (TextView) convertView
					.findViewById(R.id.txtSummary);
			holder.txtTitle = (TextView) convertView
					.findViewById(R.id.txtHeading);
			holder.pagerBlock = (LinearLayout) convertView
					.findViewById(R.id.middle);
			holder.txtSummary.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int pos = Integer.parseInt(v.getTag().toString());
					View mother = (View) v.getParent().getParent().getParent();
					FreeVersionPost item = (FreeVersionPost) fitnessAdapter
							.getItem(pos);

					// assesment

					if (item.getForceAssessment().equalsIgnoreCase("1")) {
						Intent intent = new Intent(GoodReadPostFragment.this
								.getActivity(), AssesmentActivity.class);
						startActivity(intent);
					} else {
						if (item.getPostType().equals("1")) {
							Intent intent = new Intent(getActivity(),
									SingleItemShowActivity.class);
							intent.putExtra(AppConstant.ITEM_ID, item.getId());
							intent.putExtra(AppConstant.ITEM_CAT_ID,
									item.getCategoryId());
							startActivity(intent);
							item.setSeenCount((1 + Integer.parseInt(item.getSeenCount()) + ""));
							fitnessAdapter.notifyDataSetChanged();
							/*if (Integer.parseInt(fitnessAdapter.getItem(pos)
									.getSeenCount()) < 1000) {
								TextView tv = (TextView) mother
										.findViewById(R.id.fv_post_tv_seen);
								int kk = Integer.parseInt(tv.getText()
										.toString()) + 1;
								tv.setText(kk + "");
							}*/
						}
					}
				}
			});
			holder.btnWow = (CustomButton) convertView
					.findViewById(R.id.btnWow);
			holder.wowCommentLayout = (LinearLayout) convertView
					.findViewById(R.id.wow_comment_layout);
			holder.wowCountTextView = (TextView) convertView
					.findViewById(R.id.wow_count);
			holder.btnRemove = (CustomButton) convertView
					.findViewById(R.id.btnReadLater);
			holder.btnRemove.setTitleText("Remove");
			holder.btnRemove.setImage(R.drawable.remove_normal);
			holder.btnRemove.setVisibility(View.VISIBLE);
			holder.btnRemove.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String postId = allGooadReadPost.get(
							Integer.parseInt(v.getTag().toString())).getId();
					removeGoodread(postId);
					Log.e("CURRENT POSITION", v.getTag().toString());
					allGooadReadPost.remove(Integer.parseInt(v.getTag().toString()));
					fitnessAdapter.clearList();
					setupListAndNotify();
				}
			});
			// holder.commentCountTextView = (TextView)
			// convertView.findViewById(R.id.comment_count);
			/*
			 * holder.wowCommentLayout.setOnClickListener(new OnClickListener()
			 * {
			 * 
			 * @Override public void onClick(View v) { int pos =
			 * Integer.parseInt(v.getTag().toString()); CommentDialog dialog =
			 * CommentDialog.newInstance(Integer
			 * .parseInt(fitnessAdapter.getItem(pos).getId()), "1"
			 * .equals(fitnessAdapter.getItem(pos) .getCan_comment()));
			 * dialog.show(getChildFragmentManager(), null);
			 * 
			 * } });
			 */
			holder.btnWow.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// if (UserHelper.isLoggedIn()) {
					doWow(Integer.parseInt(v.getTag().toString()));
					/*
					 * CustomButton w = (CustomButton) v;
					 * w.setTitleColor(getResources().getColor(R.color.red));
					 * w.setImage(R.drawable.wow_icon_tap);
					 */
					View mother = (View) v.getParent().getParent().getParent()
							.getParent();
					TextView count = (TextView) mother
							.findViewById(R.id.wow_count);
					int n = Integer.parseInt(count.getText().toString()
							.split(" ")[0]);
					if (n == 0)
						count.setVisibility(View.VISIBLE);
					count.setText((n + 1) + " WoW");

					/*
					 * } else showCustomDialog( "WOW", R.drawable.wow_red,
					 * getResources().getString(R.string.wow_msg) + "\n" +
					 * getResources().getString( R.string.not_logged_in_msg));
					 */

				}
			});

			holder.btnShare = (CustomButton) convertView
					.findViewById(R.id.btnShare);
			holder.btnShare.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (AppUtility.isInternetConnected()) {
						((HomeContainerActivity) getActivity())
								.sharePostUniversal(list.get(Integer.parseInt(v
										.getTag().toString())));
					}
				}
			});
			holder.seenTextView = (TextView) convertView
					.findViewById(R.id.fv_post_tv_seen);

		}

		public void setAddAndPostPager(ImagePagerAdapter imgAdapter,
				UninterceptableViewPager viewPager, int position) {
			ArrayList<String> imgPaths;
			ArrayList<AddData> addData;
			switch (Integer.parseInt(list.get(position).getPostType())) {
			case 2:
				addData = list.get(position).getAdd_images();
				imgAdapter = new ImagePagerAdapter(addData);
				break;
			case 1:
				imgPaths = list.get(position).getImages();
				imgAdapter = new ImagePagerAdapter(imgPaths, 1);
				break;
			default:
				break;
			}

			// Log.e("IMAGE Category ID", list.get(position).getPostType()
			// + "");

			// If hardware acceleration is enabled, you should also remove
			// clipping on the pager for its children.

			viewPager.setAdapter(imgAdapter);
			viewPager.setTag(position + "");
			// holder.imgAdapter.notifyDataSetChanged();
			viewPager.setOffscreenPageLimit(imgAdapter.getCount());
			// A little space between pages
			viewPager.setPageMargin(-10);

			// If hardware acceleration is enabled, you should also remove
			// clipping on the pager for its children.
			viewPager.setClipChildren(false);
			viewPager.setOnTouchListener(new OnTouchListener() {
				private float pointX;
				private float pointY;
				private int tolerance = 20;

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_MOVE:
						return false; // This is important, if you return
										// TRUE the action of swipe will not
										// take place.
					case MotionEvent.ACTION_DOWN:
						pointX = event.getX();
						pointY = event.getY();
						break;
					case MotionEvent.ACTION_UP:
						boolean sameX = pointX + tolerance > event.getX()
								&& pointX - tolerance < event.getX();
						boolean sameY = pointY + tolerance > event.getY()
								&& pointY - tolerance < event.getY();
						if (sameX && sameY) {
							int pos = Integer.parseInt(v.getTag().toString());
							View mother = (View) v.getParent().getParent()
									.getParent().getParent();
							FreeVersionPost item = (FreeVersionPost) fitnessAdapter
									.getItem(pos);

							// assesment

							if (item.getForceAssessment().equalsIgnoreCase("1")) {
								Intent intent = new Intent(
										GoodReadPostFragment.this.getActivity(),
										AssesmentActivity.class);
								startActivity(intent);
							} else {
								if (item.getPostType().equals("1")) {

									if (!TextUtils.isEmpty(item
											.getEmbeddedUrl())) {
										String string = item.getEmbeddedUrl()
												.replace("embed/", "watch?v=");

										startActivity(new Intent(
												Intent.ACTION_VIEW,
												Uri.parse("http:" + string)));
										// startActivity(new
										// Intent(Intent.ACTION_VIEW,
										// Uri.parse(item.getEmbeddedUrl())));

									}

									else {
										Intent intent = new Intent(
												getActivity(),
												SingleItemShowActivity.class);
										intent.putExtra(AppConstant.ITEM_ID,
												item.getId());
										intent.putExtra(
												AppConstant.ITEM_CAT_ID,
												item.getCategoryId());
										startActivity(intent);
										item.setSeenCount((1 + Integer.parseInt(item.getSeenCount()) + ""));
										fitnessAdapter.notifyDataSetChanged();
										/*if (Integer.parseInt(fitnessAdapter
												.getItem(pos).getSeenCount()) < 1000) {
											TextView tv = (TextView) mother
													.findViewById(R.id.fv_post_tv_seen);
											int kk = Integer.parseInt(tv
													.getText().toString()) + 1;
											tv.setText(kk + "");
										}*/

									}

								} else if (item.getPostType().equals("2")) {
									ViewPager vp = (ViewPager) v;
									Intent i = new Intent(Intent.ACTION_VIEW);
									i.setData(Uri.parse(item.getAdd_images()
											.get(vp.getCurrentItem())
											.getAd_image_link()));
									startActivity(i);
								}
							}

						}
					}
					return false;
				}
			});
		}

		public ArrayList<FreeVersionPost> getList() {
			return list;
		}

	}

	private class ImagePagerAdapter extends PagerAdapter {

		private List<String> images;
		private LayoutInflater inflater;
		private List<AddData> addData;
		private int type = 2;

		ImagePagerAdapter(List<String> images, int type) {
			this.images = images;
			this.type = type;
			inflater = getActivity().getLayoutInflater();
		}

		ImagePagerAdapter(List<AddData> addData) {
			this.addData = addData;
			inflater = getActivity().getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			if (type == 2)
				return addData.size();
			else if (type == 1)
				return images.size();
			return images.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = null;
			if (this.type == 2) {
				imageLayout = inflater.inflate(R.layout.add_view_list, view,
						false);
				
			} else if (this.type == 1) {
				imageLayout = inflater.inflate(R.layout.item_pager_image, view,
						false);
			}

			assert imageLayout != null;
			ImageView imageView = (ImageView) imageLayout
					.findViewById(R.id.image);

			switch (type) {
			case 2:
				displayImage(imageLayout, imageView, addData.get(position)
						.getAd_image());
				break;
			case 1:
				displayImage(imageLayout, imageView, images.get(position));
				break;

			default:
				break;
			}

			view.addView(imageLayout, 0);
			return imageLayout;
		}

		public void displayImage(View imageLayout, ImageView imageView,
				String url) {
			final ProgressBar spinner = (ProgressBar) imageLayout
					.findViewById(R.id.loading);
			imageLoader.displayImage(url, imageView, options,
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
							Toast.makeText(getActivity(), message,
									Toast.LENGTH_SHORT).show();

							spinner.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							spinner.setVisibility(View.GONE);
						}
					});
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.middle:
			View mother = (View) v.getParent().getParent().getParent();
			FreeVersionPost item = (FreeVersionPost) fitnessAdapter
					.getItem(Integer.parseInt(v.getTag().toString()));

			if (item.getForceAssessment().equalsIgnoreCase("1")) {
				Intent intent = new Intent(
						GoodReadPostFragment.this.getActivity(),
						AssesmentActivity.class);
				startActivity(intent);
			} else {
				if (item.getPostType().equals("1")) {
					Intent intent = new Intent(getActivity(),
							SingleItemShowActivity.class);
					intent.putExtra(AppConstant.ITEM_ID, item.getId());
					intent.putExtra(AppConstant.ITEM_CAT_ID,
							item.getCategoryId());
					startActivity(intent);
					item.setSeenCount((1 + Integer.parseInt(item.getSeenCount()) + ""));
					fitnessAdapter.notifyDataSetChanged();
					/*if (Integer.parseInt(fitnessAdapter.getItem(
							Integer.parseInt(v.getTag().toString()))
							.getSeenCount()) < 1000) {
						TextView tv = (TextView) mother
								.findViewById(R.id.fv_post_tv_seen);
						int kk = Integer.parseInt(tv.getText().toString()) + 1;
						tv.setText(kk + "");
					}*/

				}
			}
			break;

		default:
			break;
		}
	}

	private void setUpImageUtis() {
		// TODO Auto-generated method stub
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisc(true).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getActivity()).threadPriority(Thread.NORM_PRIORITY - 2)
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
	}

}
