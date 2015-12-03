package com.classtune.schoolapp.fragments;

import java.util.ArrayList;
import java.util.List;

import com.classtune.schoolapp.R;
import com.classtune.schoolapp.adapters.ClubNewsListAdapter;
import com.classtune.schoolapp.model.ClubNews;
import com.classtune.schoolapp.model.UserAuthListener;
import com.classtune.schoolapp.model.Wrapper;
import com.classtune.schoolapp.model.WrapperClubNews;
import com.classtune.schoolapp.networking.AppRestClient;
import com.classtune.schoolapp.utils.AppUtility;
import com.classtune.schoolapp.utils.GsonParser;
import com.classtune.schoolapp.utils.RequestKeyHelper;
import com.classtune.schoolapp.utils.URLHelper;
import com.classtune.schoolapp.utils.UserHelper;
import com.classtune.schoolapp.utils.UserHelper.UserTypeEnum;
import com.classtune.schoolapp.viewhelpers.UIHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

@SuppressLint("ValidFragment")
public class ClubNewsFragment extends Fragment implements UserAuthListener{


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		
	}

	private Context context;
	private PullToRefreshListView clubNewsList;
	private ClubNewsListAdapter adapter;
	private View view;
	//private TicketListAdapter adapter;
	private List<ClubNews> items;
	private UIHelper uiHelper;
	private int pageNumber=1;
	private int pageSize=3;
	private boolean isRefreshing=false;
	private boolean loading = false;
	private boolean stopLoadingData=false;
	private UserHelper userHelper;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		init();
	}

	private void initializePageing()
	{
		pageNumber=1;
		isRefreshing=false;
		loading= false;
		stopLoadingData=false;
	}
	
	private void init() {
		context=getActivity();
		items=new ArrayList<ClubNews>();
		uiHelper=new UIHelper(getActivity());
		userHelper=new UserHelper(this, context);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.fragment_club_news, container, false);
		clubNewsList = (PullToRefreshListView) view.findViewById(R.id.club_listview);
		setUpList();
		loadDataInToList();
		return view;
	}
	
	
	
	private void setUpList() {
		
		initializePageing();
		clubNewsList.setMode(Mode.BOTH);
		// Set a listener to be invoked when the list should be refreshed.
		clubNewsList.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(context, System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				
				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				
				
				Mode m=clubNewsList.getCurrentMode();
				if(m==Mode.PULL_FROM_START)
				{
					stopLoadingData=false;
					isRefreshing=true;
					pageNumber=1;
					loading = true;
	                loadDataInToList();
				}
				else if(!stopLoadingData)
				{
					pageNumber++;
					loading = true;
	                loadDataInToList();
				}
				else
				{
					new NoDataTask().execute();
				}
				
			}

			
		});
		items.clear();
		adapter=new ClubNewsListAdapter(context, items,uiHelper);
		clubNewsList.setAdapter(adapter);
	
	}
	
	
	private void loadDataInToList() {
		if(AppUtility.isInternetConnected())
		{
			fetchDataFromServer();
		}
		else
			uiHelper.showMessage(context.getString(R.string.internet_error_text));
	}
	
	private void fetchDataFromServer() {
		
			RequestParams params=new RequestParams();
			params.put("user_secret",UserHelper.getUserSecret());
			
			if (userHelper.getUser().getType() == UserTypeEnum.STUDENT) {
				params.put("school",userHelper.getUser().getPaidInfo().getSchoolId());
			}
			
			
			if (userHelper.getUser().getType() == UserTypeEnum.PARENTS) {
				params.put(RequestKeyHelper.STUDENT_ID, userHelper.getUser().getSelectedChild().getProfileId());
				params.put(RequestKeyHelper.BATCH_ID, userHelper.getUser().getSelectedChild().getBatchId());
				params.put("school",userHelper.getUser().getSelectedChild().getSchoolId());
			}
			
			params.put("page_number", pageNumber+"");
			params.put("page_size", pageSize+"");
			
			AppRestClient.post(URLHelper.URL_CLUB_LIST, params, getClubsHandler);
			
		
	}
	
	AsyncHttpResponseHandler getClubsHandler=new AsyncHttpResponseHandler()
	{
		@Override
		public void onFailure(Throwable arg0, String arg1) {
			super.onFailure(arg0, arg1);
			
		}

		@Override
		public void onStart() {
			super.onStart();
			if(pageNumber==0 && !isRefreshing){
			if(!uiHelper.isDialogActive())
				uiHelper.showLoadingDialog(getString(R.string.loading_text));
			else
				uiHelper.updateLoadingDialog(getString(R.string.loading_text));
			}
		}

		@Override
		public void onSuccess(int arg0, String responseString) {
			super.onSuccess(arg0, responseString);
			uiHelper.dismissLoadingDialog();
			Wrapper wrapper=GsonParser.getInstance().parseServerResponse(responseString);
			if(wrapper.getStatus().getCode()==200)
			{
				WrapperClubNews clubWrapper=GsonParser.getInstance().parseClubWrapper(wrapper.getData().toString());
				if(!clubWrapper.isHasnext())
					stopLoadingData=true;
				if(pageNumber==1)
					items.clear();
				items.addAll(clubWrapper.getClubs());
				adapter.notifyDataSetChanged();
				if(pageNumber!=0 || isRefreshing)
				{
					clubNewsList.onRefreshComplete();
					loading=false;
				}
			}
			else
			{
				
			}
			Log.e("Events", responseString);
			
			
		}
		
	};
	
	private class NoDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			
			adapter.notifyDataSetChanged();

			// Call onRefreshComplete when the list has been refreshed.
			clubNewsList.onRefreshComplete();

			super.onPostExecute(result);
		}
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
	
}
