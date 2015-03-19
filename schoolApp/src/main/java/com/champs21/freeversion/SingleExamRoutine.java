package com.champs21.freeversion;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.champs21.schoolapp.R;
import com.champs21.schoolapp.model.AcademicCalendarDataItem;
import com.champs21.schoolapp.model.ExamRoutine;
import com.champs21.schoolapp.model.HomeworkData;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.RequestKeyHelper;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.utils.UserHelper;
import com.champs21.schoolapp.utils.UserHelper.UserTypeEnum;
import com.champs21.schoolapp.viewhelpers.UIHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SingleExamRoutine extends ChildContainerActivity {
	
	

	private UIHelper uiHelper;
	private ListView listViewExamData;
	private UserHelper userHelper;
	private List<ExamRoutine> listData;
	private EfficientAdapter mAdapter;
	private TextView examName;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.fragment_examroutine);
		initView();
		uiHelper = new UIHelper(this);
		listData = new ArrayList<ExamRoutine>();
		mAdapter = new EfficientAdapter(this);
		userHelper = new UserHelper(this, this);
		listViewExamData.setAdapter(mAdapter);
		examName = (TextView)findViewById(R.id.tv_report_exam_name);
		examName.setText(getIntent().getExtras().getString("term_name"));
		fetchExamRoutine();
	}
	private void fetchExamRoutine() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		// app.showLog("Secret before sending", app.getUserSecret());
		params.put(RequestKeyHelper.USER_SECRET, UserHelper.getUserSecret());
		params.put("exam_id", getIntent().getExtras().getString(AppConstant.ID_SINGLE_CALENDAR_EVENT));
		if(userHelper.getUser().getType()==UserTypeEnum.PARENTS){
			params.put(RequestKeyHelper.SCHOOL,userHelper.getUser().getPaidInfo().getSchoolId());
			params.put(RequestKeyHelper.BATCH_ID, userHelper.getUser().getSelectedChild().getBatchId());
			params.put(RequestKeyHelper.STUDENT_ID, userHelper.getUser().getSelectedChild().getProfileId());
		}
		AppRestClient.post(URLHelper.URL_ROUTINE_EXAM, params,
				examRoutineHandler);
	}


	AsyncHttpResponseHandler examRoutineHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			super.onFailure(arg0, arg1);
			// uListener.onServerAuthenticationFailed(arg1);
			uiHelper.showMessage(arg1);
			uiHelper.dismissLoadingDialog();
		}

		@Override
		public void onStart() {
			super.onStart();
			// uListener.onServerAuthenticationStart();
			uiHelper.showLoadingDialog("Please wait...");
		}

		// u44tk4p199mvhgi8gf378ui510
		// 08e9344b9eb6b0fcc56717c5efa6e2d6e08e9e84ad9403ea45816188c5600f89
		@Override
		public void onSuccess(int arg0, String responseString) {
			super.onSuccess(arg0, responseString);
			uiHelper.dismissLoadingDialog();
			Log.e("RESPONSE ROUTINE ", responseString);
			// uiHelper.showMessage(responseString);
			Wrapper modelContainer = GsonParser.getInstance()
					.parseServerResponse(responseString);
			if (modelContainer.getStatus().getCode() == 200) {
				listData = GsonParser.getInstance().parseExam(
						modelContainer.getData()
								.getAsJsonArray("exam_time_table").toString());

				Log.e("ListData SIZE: ", listData.size()+"");
			} else {

			}
			mAdapter.notifyDataSetChanged();
			// Log.e("GSON NOTICE TYPE TEXT:", modelContainer.getData()
			// .getAllNotice().get(0).getNoticeTypeText());
		}
	};

	private void initView() {
		// TODO Auto-generated method stub
		listViewExamData = (ListView) findViewById(R.id.listViewExamData);
	}

	private class EfficientAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public EfficientAdapter(Context context) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(context);
		}

		/**
		 * The number of items in the list is determined by the number of
		 * speeches in our array.
		 * 
		 * @see android.widget.ListAdapter#getCount()
		 */
		public int getCount() {
			// return syllabusMap.get(currentTabKey).size();
			return listData.size();
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

		/**
		 * Make a view to hold each row.
		 * 
		 * @see android.widget.ListAdapter#getView(int, android.view.View,
		 *      android.view.ViewGroup)
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			// A ViewHolder keeps references to children views to avoid
			// unneccessary calls
			// to findViewById() on each row.
			ViewHolder holder;
			ExamRoutine item = listData.get(position);

			// When convertView is not null, we can reuse it directly, there is
			// no need
			// to reinflate it. We only inflate a new View when the convertView
			// supplied
			// by ListView is null.
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.fragment_examroutine_singledata, null);

				// Creates a ViewHolder and store references to the two children
				// views
				// we want to bind data to.
				holder = new ViewHolder();
				holder.tvSubject = (TextView) convertView
						.findViewById(R.id.txtSubject);
				holder.tvStartTime = (TextView) convertView
						.findViewById(R.id.txtStartTime);
				//holder.tvEndTime = (TextView) convertView.findViewById(R.id.txtEndTime);

				holder.tvDay = (TextView) convertView.findViewById(R.id.txtDay);

				holder.tvDate = (TextView) convertView
						.findViewById(R.id.txtDate);

				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}
			
			Log.e("ITEM SUBJECT", item.getExam_subject_name());
			holder.tvSubject.setText(item.getExam_subject_name());
			holder.tvStartTime.setText(item.getExam_start_time() + " - "+item.getExam_end_time());
			//holder.tvEndTime.setText(item.getExam_end_time());
			holder.tvDate.setText(item.getExam_date());
			holder.tvDay.setText(item.getExam_day());
			
			
			return convertView;
		}

		class ViewHolder {
			TextView tvSubject;
			TextView tvStartTime;
			//TextView tvEndTime;
			TextView tvDay;
			TextView tvDate;
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
