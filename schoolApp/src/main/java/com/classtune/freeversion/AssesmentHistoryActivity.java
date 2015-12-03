package com.classtune.freeversion;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.classtune.schoolapp.R;
import com.classtune.schoolapp.model.AssessmentHistory;
import com.classtune.schoolapp.model.Wrapper;
import com.classtune.schoolapp.networking.AppRestClient;
import com.classtune.schoolapp.utils.AppConstant;
import com.classtune.schoolapp.utils.GsonParser;
import com.classtune.schoolapp.utils.RequestKeyHelper;
import com.classtune.schoolapp.utils.URLHelper;
import com.classtune.schoolapp.utils.UserHelper;
import com.classtune.schoolapp.viewhelpers.UIHelper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class AssesmentHistoryActivity extends ChildContainerActivity implements View.OnClickListener{
	
	
	private UIHelper uiHelper;
	private UserHelper userHelper;
	
	
	private ListView listViewScore;
	
	private List<AssessmentHistory> listHistory =  new ArrayList<AssessmentHistory>();
	private AssessmentMarksAdapter adapter;
	
	private String assessmentId = "";
	
	private TextView txtMessage;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_assesment_history);
		
		
		listHistory.clear();
		
		userHelper = new UserHelper(this);
		uiHelper = new UIHelper(this);
		
		initView();
		initAction();
		
		initApiCall();
		
		
	}
	
	
	
	
	private void initView()
	{
		this.listViewScore = (ListView)this.findViewById(R.id.listViewScore);
		
		this.adapter = new AssessmentMarksAdapter();
		this.listViewScore.setAdapter(this.adapter);
		
		this.txtMessage = (TextView)this.findViewById(R.id.txtMessage);
	}
	
	private void initAction()
	{
		
		
	}
	
	
	private void initApiCall() 
	{

		RequestParams params = new RequestParams();
		params.put(RequestKeyHelper.USER_ID, userHelper.getUser().getUserId());
		

		AppRestClient.post(URLHelper.URL_ASSESSMENT_HISTORY, params,
				assessmentHandler);
	}
	
	private AsyncHttpResponseHandler assessmentHandler = new AsyncHttpResponseHandler() {

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
			//Log.e("ASSESSMENT", "data: "+responseString);

			uiHelper.dismissLoadingDialog();

			Wrapper modelContainer = GsonParser.getInstance()
					.parseServerResponse(responseString);

			if (modelContainer.getStatus().getCode() == 200) {
				
				JsonArray assessment = modelContainer.getData().get("assesment").getAsJsonArray();
				
				
				
				listHistory = parseAssessmentMarks(assessment.toString());
				
				if(listHistory.size() <=0 )
				{
					//Toast.makeText(AssesmentHistoryActivity.this, "No score found!", Toast.LENGTH_SHORT).show();
					txtMessage.setVisibility(View.VISIBLE);
				}
				else
					txtMessage.setVisibility(View.GONE);
				
				
			}

			else {

			}
			
			adapter.notifyDataSetChanged();

		};

	};
	
	

	public List<AssessmentHistory> parseAssessmentMarks(String object) {

		List<AssessmentHistory> tags = new ArrayList<AssessmentHistory>();
		Type listType = new TypeToken<List<AssessmentHistory>>() {
		}.getType();
		tags = (List<AssessmentHistory>) new Gson().fromJson(object, listType);
		return tags;
	}
	
	
	public class AssessmentMarksAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listHistory.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listHistory.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public void clearList() {
			//listStatus.clear();
		}
		
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;

			if (convertView == null) {
				holder = new ViewHolder();

				convertView = LayoutInflater.from(AssesmentHistoryActivity.this).inflate(R.layout.row_assessment_history, parent, false);

				holder.txtPosition = (TextView)convertView.findViewById(R.id.txtPosition);
				holder.txtTitle = (TextView)convertView.findViewById(R.id.txtTitle);
				holder.txtMarks = (TextView)convertView.findViewById(R.id.txtMarks);
				holder.txtDate = (TextView)convertView.findViewById(R.id.txtDate);
				holder.txtTopic = (TextView)convertView.findViewById(R.id.txtTopic);
				holder.btnRetry = (Button)convertView.findViewById(R.id.btnRetry);
				holder.btnLeaderBoard = (Button)convertView.findViewById(R.id.btnLeaderBoard);
				
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.txtPosition.setText(String.valueOf(position+1)+". ");
			
			holder.txtTitle.setText(listHistory.get(position).getTitle());
			holder.txtMarks.setText("Score: "+listHistory.get(position).getMark()+"/"+String.valueOf(listHistory.get(position).getTotal()));
			holder.txtDate.setText("Date: "+listHistory.get(position).getCreatedDate());
			holder.txtTopic.setText("Topic: "+listHistory.get(position).getTopic());
			holder.btnRetry.setTag(position);
			holder.btnLeaderBoard.setTag(position);
			
			
			
			holder.btnRetry.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Button btn = ((Button)v);
					/*Intent intent = new Intent(AssesmentHistoryActivity.this, AssesmentActivity.class);
					intent.putExtra("from_assessment_history", listHistory.get((Integer) btn.getTag()).getId());
					startActivity(intent);*/
					
					Intent intent = new Intent(AssesmentHistoryActivity.this,
							SingleItemShowActivity.class);
					intent.putExtra(AppConstant.ITEM_ID, listHistory.get((Integer) btn.getTag()).getPid());
					startActivity(intent);
				}
			});
			
			
			holder.btnLeaderBoard.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Button btn = ((Button)v);
					
					Intent intent = new Intent(AssesmentHistoryActivity.this, AssessmentLeaderBoardActivity.class);
					intent.putExtra("assessment_id", listHistory.get((Integer) btn.getTag()).getId());
					startActivity(intent);
				}
			});
			
			
			
			
			return convertView;
		}

	}


	class ViewHolder {
		
		TextView txtPosition;
		TextView txtTitle;
		TextView txtMarks;
		TextView txtDate;
		TextView txtTopic;
		Button btnRetry;
		Button btnLeaderBoard;
		
	}
	
	
	

}
