package com.classtune.freeversion;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.classtune.schoolapp.R;
import com.classtune.schoolapp.model.FreeVersionPost;
import com.classtune.schoolapp.model.GoodReadPostAll;
import com.classtune.schoolapp.model.Wrapper;
import com.classtune.schoolapp.networking.AppRestClient;
import com.classtune.schoolapp.utils.GsonParser;
import com.classtune.schoolapp.utils.RequestKeyHelper;
import com.classtune.schoolapp.utils.SchoolApp;
import com.classtune.schoolapp.utils.URLHelper;
import com.classtune.schoolapp.utils.UserHelper;
import com.classtune.schoolapp.viewhelpers.CustomButton;
import com.classtune.schoolapp.viewhelpers.CustomRhombusIcon;
import com.classtune.schoolapp.viewhelpers.PagerContainer;
import com.classtune.schoolapp.viewhelpers.PopupDialogGoodReadDelete;
import com.classtune.schoolapp.viewhelpers.PopupDialogGoodReadDelete.IButtonclick;
import com.classtune.schoolapp.viewhelpers.UIHelper;
import com.classtune.schoolapp.viewhelpers.UninterceptableViewPager;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class GoodReadActivity extends ChildContainerActivity implements OnLongClickListener,IButtonclick{

	private PagerSlidingTabStripGoodRead tabsFirst;
	private TabPagerAdapterGoodRead adapter;
	private ViewPager pager;
	private String folderNameDelete;

	private static int positionPager = 0;

	UIHelper uiHelper;
	SchoolApp app;
	UserHelper userHelper;
	private String currentTabKey = "";
	private CustomButton current;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goodread);
		tabsFirst = (PagerSlidingTabStripGoodRead) findViewById(R.id.tab);
		pager = (ViewPager) findViewById(R.id.pager);
		fetchGoodReadData();
		uiHelper = new UIHelper(this);
		userHelper = new UserHelper(this);
	}

	public ArrayList<FreeVersionPost> getPostList(int id) {
		return postList.getGoodreadPostList().get(id).getPost();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
       
	}

	private void fetchGoodReadData() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();

		params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());
		Log.e("USER ID GOODREAD", userHelper.getUserFreeId());
		AppRestClient.post(URLHelper.URL_FREE_VERSION_GOODREAD_ALL, params,
				goodReadAllHandler);
	}

	GoodReadPostAll postList;

	AsyncHttpResponseHandler goodReadAllHandler = new AsyncHttpResponseHandler() {
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

				Log.e("Good Read Post", wrapper.getData().toString());
				postList=new GoodReadPostAll();
				postList = GsonParser.getInstance().parseGoodreadPostAll(
						wrapper.getData().toString());
				setUp();
				
				
				
				tabsFirst.setViewPager(pager, tabsFirst);
				tabsFirst.setLongClickListener(GoodReadActivity.this);
				// createTabFolders();

				// listGoodread.setAdapter(adapter);
			}
		};
	};

	LayoutInflater inflater;
	ArrayList<CustomButton> folderBtnList;

	/*
	 * protected void createTabFolders() { // TODO Auto-generated method stub
	 * 
	 * inflater = LayoutInflater.from(this); map = new HashMap<String,
	 * ArrayList<FreeVersionPost>>(postList .getGoodreadPostList().size()); for
	 * (int i = 0; i < postList.getGoodreadPostList().size(); i++) { String
	 * folderName = postList.getGoodreadPostList().get(i) .getFolderName();
	 * 
	 * View view = inflater.inflate(R.layout.goodread_tab_button, null);
	 * 
	 * CustomButton button = (CustomButton)
	 * view.findViewById(R.id.btn_goodread);
	 * 
	 * button.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub CustomButton b = (CustomButton) v; currentTabKey = (String)
	 * b.getTag(); setTabSelected(b); adapter.notifyDataSetChanged(); } });
	 * 
	 * if(i==0){ current = button; setTabSelected(button); }else
	 * button.setButtonSelected(false, getResources().getColor(R.color.black),
	 * R.drawable.term_icon_normal); button.setImageGone();
	 * button.setTitleText(folderName);
	 * 
	 * button.setTag(folderName);
	 * 
	 * folderBtnList.add(button);
	 * 
	 * scrollerBtns.addView(view);
	 * 
	 * // if (i == postList.getGoodreadPostList().size() - 1) { // LinearLayout
	 * grayBar = (LinearLayout) view // .findViewById(R.id.gray_bar); //
	 * grayBar.setVisibility(View.GONE); // }
	 * 
	 * map.put(folderName, postList.getGoodreadPostList().get(i).getPost()); }
	 * 
	 * folderBtnList.get(0).performClick(); }
	 */
	private void setUp() {

		adapter = new TabPagerAdapterGoodRead(getSupportFragmentManager(),
				postList);
		pager.setOffscreenPageLimit(1);
		pager.setAdapter(adapter);

		pager.setCurrentItem(positionPager);
	}

	/*@SuppressWarnings("unchecked")
	public void setTabSelected(View v) {
		CustomButton btn = (CustomButton) v;

		current.setButtonSelected(false,
				getResources().getColor(R.color.black),
				R.drawable.term_icon_normal);

		btn.setButtonSelected(true, getResources().getColor(R.color.black),
				R.drawable.term_icon_tap);
		current = btn;
	}*/

	class ViewHolder {
		CustomRhombusIcon imgViewCategoryMenuIcon;
		TextView txtCategoryName;
		TextView txtPublishedDateString;
		PagerContainer container;
		UninterceptableViewPager viewPager;
		TextView txtSummary;
		CustomButton btnLike;
		CustomButton btnShare;
		ImagePagerAdapter imgAdapter;
		TextView seenTextView;
		CustomButton btnRemove;

	}

	protected void removeGoodread(String postId) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();

		params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());
		params.put(RequestKeyHelper.POST_ID, postId);
		params.put(RequestKeyHelper.FOLDER_NAME, currentTabKey);

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

				GoodReadActivity.this.onResume();
			}
		};
	};

	private class ImagePagerAdapter extends PagerAdapter {

		private List<String> images;
		private LayoutInflater inflater;

		ImagePagerAdapter(List<String> images) {
			this.images = images;
			inflater = GoodReadActivity.this.getLayoutInflater();
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
			SchoolApp.getInstance().displayUniversalImage(images.get(position),
					imageView);

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

	private void deleteFolder(String folderName)
	{
		RequestParams params=new RequestParams();
		params.put(RequestKeyHelper.FOLDER_NAME, folderName);
		params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());
		AppRestClient.post(URLHelper.URL_FREE_VERSION_REMOVE_GOODREAD_FOLDER, params, removeGoodReadFolderHandler);
	}
	
	AsyncHttpResponseHandler removeGoodReadFolderHandler = new AsyncHttpResponseHandler() {
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

				uiHelper.showMessage("Folder is deleted successfully!");
				fetchGoodReadData();
				//GoodReadActivity.this.onResume();
			}
		};
	};
	
	
	@Override
	public boolean onLongClick(View v) {
		folderNameDelete=((TextView)v).getTag().toString();
		//Log.e("Onk khon press korse",);
		PopupDialogGoodReadDelete picker = PopupDialogGoodReadDelete.newInstance(0);
		picker.setData("Delete","Do you really want to delete "+folderNameDelete+"folder",R.drawable.folder_icon, GoodReadActivity.this,GoodReadActivity.this);
		picker.show(getSupportFragmentManager(), null);
		
		return false;
	}

	@Override
	public void onNoButtonClick() {
		
	}

	@Override
	public void onYesButtonClick() {
		deleteFolder(folderNameDelete);
	}

}
