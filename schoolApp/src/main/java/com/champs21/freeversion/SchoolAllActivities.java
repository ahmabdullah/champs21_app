package com.champs21.freeversion;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.champs21.schoolapp.R;
import com.champs21.schoolapp.model.SchoolActivities;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.SchoolApp;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.viewhelpers.UIHelper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SchoolAllActivities extends ChildContainerActivity {

	//private UIHelper uiHelper;
	private ListView listView;
	private ListAdapter adapter;
	
	private String schoolId = "";
	
	private List<SchoolActivities> listActivities = new ArrayList<SchoolActivities>();
	
	private ProgressBar progressBar;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.school_seeall_activities);
		
		//uiHelper = new UIHelper(this);
		
		Bundle bundle = getIntent().getExtras();
		if(bundle != null)
		{
			this.schoolId = bundle.getString(AppConstant.SCHOOL_ID);
		}

		iniView();
		
		initApiCall();

	}

	private void iniView() 
	{
		this.listView = (ListView) this.findViewById(R.id.listView);
		this.progressBar = (ProgressBar)this.findViewById(R.id.progressBar);
		
	}
	
	
	private void initApiCall()
	{
		
		RequestParams params = new RequestParams();
		params.put("school_id", this.schoolId); //2 should be replaced by this.schoolId
		
		
		AppRestClient.post(URLHelper.URL_FREE_VERSION_SCHOOL_ACTIVITIES, params, 
				schoolActivityHandler);
	}
	
	
	private AsyncHttpResponseHandler schoolActivityHandler = new AsyncHttpResponseHandler(){

		@Override
		public void onFailure(Throwable arg0, String arg1) 
		{
			//uiHelper.showMessage(arg1);
			//uiHelper.dismissLoadingDialog();
		};

		@Override
		public void onStart() 
		{
			//uiHelper.showLoadingDialog("Please wait...");
		};

		@Override
		public void onSuccess(String responseString) 
		{
			//Log.e("FREE_HOME", "data: "+responseString);
			listActivities.clear();
			
			//uiHelper.dismissLoadingDialog();
			

			Wrapper modelContainer = GsonParser.getInstance().parseServerResponse(responseString);

			if (modelContainer.getStatus().getCode() == 200)
			{
				Log.e("SCHOOL_ACT", "data is: "+modelContainer.getData().getAsJsonObject());
				
				JsonArray arrayActivity = modelContainer.getData().getAsJsonObject().get("activity").getAsJsonArray();
				
				Log.e("SCHOOL_ACT_ARR", "data is: "+arrayActivity.toString());
				
				
				
				
				
				for(int i=0; i<arrayActivity.size(); i++)
				{
					JsonArray arrayGallery = arrayActivity.get(i).getAsJsonObject().get("gallery").getAsJsonArray();
					List<String> listGallery = new ArrayList<String>();
					
					for(int j=0;j<arrayGallery.size();j++)
					{
						listGallery.add(arrayGallery.get(j).getAsString());
					}
					
					if(arrayActivity.get(i).getAsJsonObject().get("gallery").getAsJsonArray().size() > 0)
					{
						
						listActivities.add(new SchoolActivities(arrayActivity.get(i).getAsJsonObject().get("title").getAsString(), arrayActivity.get(i).getAsJsonObject().get("content").getAsString(),
								arrayActivity.get(i).getAsJsonObject().get("summary").getAsString(), 
								arrayActivity.get(i).getAsJsonObject().get("gallery").getAsJsonArray().get(0).getAsString(), listGallery));
					}
					else
					{
						listActivities.add(new SchoolActivities(arrayActivity.get(i).getAsJsonObject().get("title").getAsString(), arrayActivity.get(i).getAsJsonObject().get("content").getAsString(),
								arrayActivity.get(i).getAsJsonObject().get("summary").getAsString(), 
								"", listGallery));
					}
					
					
				}
				
				if(listActivities.size() > 0)
					progressBar.setVisibility(View.GONE);
				
				adapter = new ListAdapter(listActivities);
				listView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				
				initListAction();
			} 

			else 
			{

			}
		};
	};
	
	
	
	public class ListAdapter extends BaseAdapter {

		private List<SchoolActivities> listActivities;

		public ListAdapter(List<SchoolActivities> listActivities) 
		{
			this.listActivities = listActivities;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listActivities.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listActivities.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LayoutInflater mInflater = (LayoutInflater) SchoolAllActivities.this
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			
			ViewHolder holder = null;
			
			if (convertView == null) 
			{
				convertView = mInflater.inflate(R.layout.row_school_activity_list, null);
				holder = new ViewHolder();
				
				holder.imgView = (ImageView)convertView.findViewById(R.id.imgView);
				holder.txtSummery  = (TextView)convertView.findViewById(R.id.txtSummery);
				holder.txtTitle = (TextView)convertView.findViewById(R.id.txtTitle);
				holder.progressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);
				
				convertView.setTag(holder);
			} 
			
			else 
			{
				holder = (ViewHolder) convertView.getTag();
			}
			
			
			if(listActivities.get(position).getImgUrl().length() > 0)
			{
				
				//SchoolApp.getInstance().displayUniversalImage(listActivities.get(position).getImgUrl(), holder.imgView);
				SchoolApp.getInstance().displayUniversalImage(listActivities.get(position).getImgUrl(), holder.imgView, holder.progressBar);
			}
			else
			{
				
			}
			
			
			
			
			holder.txtSummery.setText(listActivities.get(position).getSummary());
			holder.txtTitle.setText(listActivities.get(position).getTitle());
			
			return convertView;
		}

	}
	
	private class ViewHolder 
	{
       ImageView imgView;
       TextView txtSummery;
       TextView txtTitle;
       ProgressBar progressBar;
    }
	
	private void initListAction()
	{
		this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
				    int position, long id) {
				// TODO Auto-generated method stub
				
				SchoolActivities activity = (SchoolActivities)adapter.getItem(position);
				
				Intent intent = new Intent(SchoolAllActivities.this, SchoolPopulationActivity.class);				
				String val = new Gson().toJson(activity);
				intent.putExtra(AppConstant.ACTIVITY_SINGLE, val);
				startActivity(intent);
				
				
			}
		});
	}

}
