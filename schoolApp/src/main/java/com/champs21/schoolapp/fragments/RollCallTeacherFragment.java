

package com.champs21.schoolapp.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.champs21.freeversion.CreateMeetingRequest;
import com.champs21.freeversion.PaidVersionHomeFragment;
import com.champs21.schoolapp.BatchSelectionChangedBroadcastReceiver;
import com.champs21.schoolapp.BatchSelectionChangedBroadcastReceiver.onBatchIdChangeListener;
import com.champs21.schoolapp.R;
import com.champs21.schoolapp.fragments.LeaveApplicationDialogFragment.LeaveApplicationStatusListener;
import com.champs21.schoolapp.model.BaseType;
import com.champs21.schoolapp.model.Batch;
import com.champs21.schoolapp.model.Picker;
import com.champs21.schoolapp.model.PickerType;
import com.champs21.schoolapp.model.StudentAttendance;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.model.Picker.PickerItemSelectedListener;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.RequestKeyHelper;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.utils.UserHelper;
import com.champs21.schoolapp.viewhelpers.UIHelper;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RollCallTeacherFragment extends Fragment implements LeaveApplicationStatusListener,onBatchIdChangeListener,OnClickListener{
	private View rootView;
	private UIHelper uiHelper;
	private List<StudentAttendance> allStudents;
	private Map<String, StudentAttendance> studentMap;
	private LinearLayout pbLayout,containerList;
	private Button saveBtn,refreshBtn;
	
	private BatchSelectionChangedBroadcastReceiver reciever=new BatchSelectionChangedBroadcastReceiver(this);
	private String batchId="42";
	
	static class ViewHolder {
	    TextView rollNumberText;
		TextView nameText;
		ImageView rightImg;
		String id;
	  }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		allStudents=new ArrayList<StudentAttendance>();
		studentMap=new HashMap<String, StudentAttendance>();
		uiHelper=new UIHelper(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.rollcall_layout,container, false);
		pbLayout=(LinearLayout)rootView.findViewById(R.id.pb);
		containerList=(LinearLayout)rootView.findViewById(R.id.container);
		saveBtn=(Button)rootView.findViewById(R.id.save_btn);
		saveBtn.setOnClickListener(this);
		refreshBtn=(Button)rootView.findViewById(R.id.reset_btn);
		refreshBtn.setOnClickListener(this);
	    return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		/*RequestParams params=new RequestParams();
		params.put(RequestKeyHelper.USER_SECRET, UserHelper.getUserSecret());
		AppRestClient.post(URLHelper.URL_GET_TEACHER_BATCH, params, getBatchEventsHandler);*/
		
		if(PaidVersionHomeFragment.selectedBatch == null)
		{
			RequestParams params=new RequestParams();
			params.put(RequestKeyHelper.USER_SECRET, UserHelper.getUserSecret());
			AppRestClient.post(URLHelper.URL_GET_TEACHER_BATCH, params, getBatchEventsHandler);
		}
		else
		{
			if(PaidVersionHomeFragment.isBatchLoaded)
				fetchData();
			else
				updateUI();
		}
		
		
		
	}
	
	
	AsyncHttpResponseHandler getBatchEventsHandler=new AsyncHttpResponseHandler()
	{

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			super.onFailure(arg0, arg1);
			//uiHelper.showMessage(arg1);
			uiHelper.dismissLoadingDialog();
		}

		@Override
		public void onStart() {
			super.onStart();
			
			if(!uiHelper.isDialogActive())
				uiHelper.showLoadingDialog(getString(R.string.loading_text));
			else
				uiHelper.updateLoadingDialog(getString(R.string.loading_text));
			
		}

		@Override
		public void onSuccess(int arg0, String responseString) {
			super.onSuccess(arg0, responseString);
			uiHelper.dismissLoadingDialog();
			Log.e("Response", responseString);
			Wrapper wrapper=GsonParser.getInstance().parseServerResponse(responseString);
			if(wrapper.getStatus().getCode()==AppConstant.RESPONSE_CODE_SUCCESS)
			{
				PaidVersionHomeFragment.isBatchLoaded=true;
				PaidVersionHomeFragment.batches.clear();
				String data=wrapper.getData().get("batches").toString();
				PaidVersionHomeFragment.batches.addAll(GsonParser.getInstance().parseBatchList(data));
				//showPicker(PickerType.TEACHER_BATCH);
				showBatchPicker(PickerType.TEACHER_BATCH);
			}
			
		}
		
	};
	
	
	private void showBatchPicker(PickerType type) {

		Picker picker = Picker.newInstance(0);
		picker.setData(type, PaidVersionHomeFragment.batches, PickerCallback , "Select Batch");
		picker.show(this.getChildFragmentManager(), null);
	}

	PickerItemSelectedListener PickerCallback = new PickerItemSelectedListener() {

		@Override
		public void onPickerItemSelected(BaseType item) {

			switch (item.getType()) {
			case TEACHER_BATCH:
				Batch selectedBatch=(Batch)item;
				PaidVersionHomeFragment.selectedBatch=selectedBatch;
				Intent i = new Intent("com.champs21.schoolapp.batch");
                i.putExtra("batch_id", selectedBatch.getId());
                getActivity().sendBroadcast(i);
                
        		if(PaidVersionHomeFragment.isBatchLoaded)
    				fetchData();
    			else
    				updateUI();

				
				break;
			default:
				break;
			}

		}
	};
	
	
	

	/*@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getActivity().registerReceiver(reciever, new IntentFilter("com.champs21.schoolapp.batch"));
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		getActivity().unregisterReceiver(reciever);
	}*/
	private void fetchData() {
		
		RequestParams params=new RequestParams();
		params.put(RequestKeyHelper.BATCH_ID,PaidVersionHomeFragment.selectedBatch.getId());
		params.put(RequestKeyHelper.USER_SECRET, UserHelper.getUserSecret());
		AppRestClient.post(URLHelper.URL_GET_STUDENTS_ATTENDANCE, params, getStudentHandler);
		
	}
	
	private void updateUI()
	{
		((TeachersAttendanceTabhostFragment)RollCallTeacherFragment.this.getParentFragment()).updateSelectedBatch();
		Log.e("Tag", allStudents.size()+"");
		for(int i=0;i<allStudents.size();i++)
		{
			StudentAttendance s=allStudents.get(i);
			containerList.addView(getListRowView(s,i+1));
		}
		
	}
	
	private View getListRowView(StudentAttendance s, int i) {
		
		RelativeLayout rowView;
		rowView=(RelativeLayout)getActivity().getLayoutInflater().inflate(R.layout.row_rollcall_layout, null);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.rollNumberText=(TextView)rowView.findViewById(R.id.roll_number_text);
		viewHolder.nameText=(TextView)rowView.findViewById(R.id.name_text);
		viewHolder.rightImg=(ImageView)rowView.findViewById(R.id.right_img);
		rowView.setTag(viewHolder);
		
		final ViewHolder holder = (ViewHolder) rowView.getTag();
		holder.rollNumberText.setText(s.getRollNo());
		holder.nameText.setText(s.getStudentName());
		holder.id=s.getId();
		
		updateViewAttr(s,holder,rowView);
		rowView.setOnClickListener(getOnclickListener(rowView));
		return rowView;
		
	}

	private void updateViewAttr(StudentAttendance s, ViewHolder holder,RelativeLayout rowView) {
		switch (s.getStatus()) {
		case PRESENT:
			rowView.setBackgroundResource(R.drawable.white_bar);
			holder.rollNumberText.setTextColor(getActivity().getResources().getColor(R.color.black));
			holder.nameText.setTextColor(getActivity().getResources().getColor(R.color.black));
			holder.rightImg.setVisibility(View.GONE);
			break;
		case LATE:
			rowView.setBackgroundResource(R.drawable.rool_call_bg);
			holder.rollNumberText.setTextColor(getActivity().getResources().getColor(R.color.late));
			holder.nameText.setTextColor(getActivity().getResources().getColor(R.color.late));
			holder.rightImg.setBackgroundResource(R.drawable.late_1);
			holder.rightImg.setVisibility(View.VISIBLE);
			break;
		case ABSENT:
			rowView.setBackgroundResource(R.drawable.rool_call_bg);
			holder.rollNumberText.setTextColor(getActivity().getResources().getColor(R.color.absent));
			holder.nameText.setTextColor(getActivity().getResources().getColor(R.color.absent));
			holder.rightImg.setBackgroundResource(R.drawable.absent_1);
			holder.rightImg.setVisibility(View.VISIBLE);
			break;
		case ON_LEAVE:
			rowView.setBackgroundResource(R.drawable.rool_call_bg);
			holder.rollNumberText.setTextColor(getActivity().getResources().getColor(R.color.leave));
			holder.nameText.setTextColor(getActivity().getResources().getColor(R.color.leave));
			holder.rightImg.setBackgroundResource(R.drawable.on_leave);
			holder.rightImg.setVisibility(View.VISIBLE);
			break;
		case APPLIED_LEAVE:
			rowView.setBackgroundResource(R.drawable.rool_call_bg);
			holder.rollNumberText.setTextColor(getActivity().getResources().getColor(R.color.leave));
			holder.nameText.setTextColor(getActivity().getResources().getColor(R.color.leave));
			holder.rightImg.setBackgroundResource(R.drawable.applied_leave);
			holder.rightImg.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
		
	}

	View.OnClickListener getOnclickListener(final RelativeLayout view)  {
	    return new View.OnClickListener() {
	        public void onClick(View v) {
	        	final ViewHolder holder = (ViewHolder) view.getTag(); 
	        	StudentAttendance s=studentMap.get(holder.id);
	        	switch (s.getStatus()) {
				case PRESENT:
					s.setStatus(0);
					break;
				case ABSENT:
					s.setStatus(2);
					break;
				case LATE:
					s.setStatus(1);
					break;
				case ON_LEAVE:
					dispatchLeaveApplicationDialog(s,holder,view,true);
					break;
				case APPLIED_LEAVE:
					dispatchLeaveApplicationDialog(s,holder,view,false);
					break;
				default:
					break;
				}
	        	updateViewAttr(s, holder, view);
	        	
	        }
	    };
	}

	private void dispatchLeaveApplicationDialog(StudentAttendance s,ViewHolder holder,RelativeLayout rowView,Boolean flag)
	{
		LeaveApplicationDialogFragment dialog = LeaveApplicationDialogFragment.newInstance(0);
		dialog.setData(this,s,holder,rowView,flag);
		dialog.show(getChildFragmentManager(), null);
	}
	
	
	AsyncHttpResponseHandler getStudentHandler = new AsyncHttpResponseHandler() {
		public void onFailure(Throwable arg0, String arg1) {
			pbLayout.setVisibility(View.GONE);
			Log.e("error", arg1);
		};

		public void onStart() {
			pbLayout.setVisibility(View.VISIBLE);
			allStudents.clear();
			studentMap.clear();
			containerList.removeAllViews();
		};

		public void onSuccess(int arg0, String responseString) {
			allStudents.clear();
			studentMap.clear();
			pbLayout.setVisibility(View.GONE);
			Log.e("Menu", responseString);
			Wrapper wrapper = GsonParser.getInstance().parseServerResponse(
					responseString);
			allStudents.addAll(GsonParser.getInstance().parseStudentList((wrapper.getData().get("batch_attendence")).toString()));
			for(StudentAttendance s:allStudents)
			{
				studentMap.put(s.getId(), s);
			}
			updateUI();
		};
	};

	@Override
	public void onAccept(StudentAttendance s,ViewHolder holder,RelativeLayout rowView) {
		updateViewAttr(s, holder, rowView);
		
		initApiCallApprove(s.getId(), String.valueOf(s.getLeaveId()), "1");
	}

	@Override
	public void onDeny(StudentAttendance s,ViewHolder holder,RelativeLayout rowView) {
		// TODO Auto-generated method stub
		updateViewAttr(s, holder, rowView);
		initApiCallApprove(s.getId(), String.valueOf(s.getLeaveId()), "0");
	}
	
	
	private void initApiCallApprove(String studentId, String leaveId, String approveCode)
	{
		RequestParams params = new RequestParams();
		params.put(RequestKeyHelper.USER_SECRET, UserHelper.getUserSecret());
		params.put(RequestKeyHelper.STUDENT_ID, studentId);
		params.put("leave_id", leaveId);
		params.put("status", approveCode);
		
		AppRestClient.post(URLHelper.URL_APPROVE_LEAVE, params,
				approveHandler);
	}
	
	AsyncHttpResponseHandler approveHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			super.onFailure(arg0, arg1);
			//uiHelper.showMessage(arg1);
			uiHelper.dismissLoadingDialog();
		}

		@Override
		public void onStart() {
			super.onStart();
			
			if(!uiHelper.isDialogActive())
				uiHelper.showLoadingDialog(getString(R.string.loading_text));
			else
				uiHelper.updateLoadingDialog(getString(R.string.loading_text));
			
		}

		@Override
		public void onSuccess(int arg0, String responseString) {
			super.onSuccess(arg0, responseString);
			uiHelper.dismissLoadingDialog();
			Log.e("Response", responseString);
			
			Wrapper wrapper=GsonParser.getInstance().parseServerResponse(responseString);
			if(wrapper.getStatus().getCode()==AppConstant.RESPONSE_CODE_SUCCESS)
			{
				
				if(PaidVersionHomeFragment.isBatchLoaded)
					fetchData();
				else
					updateUI();
				//Toast.makeText(getActivity(), "Successfully done!", Toast.LENGTH_SHORT).show();
				
			}
			
			else
			{
				
			}
		}
		
	};


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save_btn:
			saveData();
			break;
		case R.id.reset_btn:
			
			break;
		default:
			break;
		}
	}

	private void saveData() {
		RequestParams params=new RequestParams();
		params.put(RequestKeyHelper.USER_SECRET,UserHelper.getUserSecret());
		params.put(RequestKeyHelper.BATCH_ID, PaidVersionHomeFragment.selectedBatch.getId());
		String studentIds="",late="";
		for (Map.Entry<String, StudentAttendance> entry : studentMap.entrySet()) {
		    String key = entry.getKey();
		    StudentAttendance value = entry.getValue();
		    switch (value.getStatus()) {
			case LATE:
				studentIds=studentIds+key+",";
				late=late+"1,";
				break;
			case ABSENT:
				studentIds=studentIds+key+",";
				late=late+"0,";
				break;

			default:
				break;
			}
		}
		if(!studentIds.equalsIgnoreCase(""))
		{
			studentIds=studentIds.substring(0, studentIds.length()-1);
			late=late.substring(0, late.length()-1);
			Log.e("students", studentIds);
			Log.e("late", late);
			params.put("student_id", studentIds);
			params.put("late", late);
		}
		
		AppRestClient.post(URLHelper.URL_POST_ADD_ATTENDANCE, params, addAttendanceHandler);
	}
	
	AsyncHttpResponseHandler addAttendanceHandler = new AsyncHttpResponseHandler() {
		public void onFailure(Throwable arg0, String arg1) {
			pbLayout.setVisibility(View.GONE);
			Log.e("error", arg1);
		};

		public void onStart() {
			pbLayout.setVisibility(View.VISIBLE);
			
		};

		public void onSuccess(int arg0, String responseString) {
			
			pbLayout.setVisibility(View.GONE);
			uiHelper.showMessage("Todays report saved.");
			/*Wrapper wrapper = GsonParser.getInstance().parseServerResponse(
					responseString);
			allStudents.addAll(GsonParser.getInstance().parseStudentList((wrapper.getData().get("batch_attendence")).toString()));
			for(StudentAttendance s:allStudents)
			{
				studentMap.put(s.getId(), s);
			}
			updateUI();*/
		};
	};

	@Override
	public void update(String batchId, String schoolId) {
		this.batchId=batchId;
		fetchData();
	}
}