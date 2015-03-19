package com.champs21.freeversion;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.champs21.schoolapp.R;
import com.champs21.schoolapp.model.HomeworkData;
import com.champs21.schoolapp.model.ModelContainer;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.AppUtility;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.MyTagHandler;
import com.champs21.schoolapp.utils.RequestKeyHelper;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.utils.UserHelper;
import com.champs21.schoolapp.utils.UserHelper.UserTypeEnum;
import com.champs21.schoolapp.viewhelpers.CustomButton;
import com.champs21.schoolapp.viewhelpers.ExpandableTextView;
import com.champs21.schoolapp.viewhelpers.UIHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SingleHomeworkActivity extends ChildContainerActivity {
	
	private UIHelper uiHelper;
	private String id;
	private TextView tvLesson;
	//private WebView webViewContent;
	
	private ExpandableTextView txtContent;
	private TextView tvSubject;
	private TextView tvDate;
	private TextView section;
	private CustomButton btnDone;
	private CustomButton btnReminder;
	private ImageView ivSubjectIcon;
	
	private LinearLayout bottmlay;
	
	private HomeworkData data;
	private Gson gson;
	
	private ImageButton btnDownload;
	private LinearLayout layoutDownloadHolder;
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
        homeBtn.setVisibility(View.VISIBLE);
        logo.setVisibility(View.GONE);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_homework);
		
		gson = new Gson();
		
		uiHelper = new UIHelper(SingleHomeworkActivity.this);
		
		if(getIntent().getExtras() != null)
			this.id = getIntent().getExtras().getString(AppConstant.ID_SINGLE_HOMEWORK);
		
		initView();
		initApicall();
		
		
	}
	
	
	private void initView()
	{
		//this.webViewContent = (WebView)this.findViewById(R.id.webViewContent);
		this.txtContent = (ExpandableTextView)this.findViewById(R.id.txtContent);
		this.tvLesson = (TextView) this.findViewById(R.id.tv_homework_content);
		this.tvSubject = (TextView) this.findViewById(R.id.tv_teacher_feed_subject_name);
		this.tvDate = (TextView) this.findViewById(R.id.tv_teacher_homewrok_feed_date);
		this.section = (TextView) this.findViewById(R.id.tv_teavher_homework_feed_section);
		this.btnDone = (CustomButton) this.findViewById(R.id.btn_done);
		this.btnReminder = (CustomButton) this.findViewById(R.id.btn_reminder);
		this.ivSubjectIcon = (ImageView) this.findViewById(R.id.imgViewCategoryMenuIcon);
		this.bottmlay = (LinearLayout)this.findViewById(R.id.bottmlay);
		this.btnDownload = (ImageButton)this.findViewById(R.id.btnDownload);
		this.layoutDownloadHolder = (LinearLayout)this.findViewById(R.id.layoutDownloadHolder);
	}
	
	
	
	private void initAction()
	{
		this.tvLesson.setText(data.getName());
		this.txtContent.setText(Html.fromHtml(data.getContent(), null, new MyTagHandler()));
		
		
		if ( data.getIsDone().equalsIgnoreCase(AppConstant.ACCEPTED) || 
				data.getIsDone().equalsIgnoreCase(AppConstant.SUBMITTED)) {
			btnDone.setImage(R.drawable.done_tap);
			btnDone.setTitleColor(this.getResources().getColor(R.color.maroon));

			btnDone.setEnabled(false);
		} else {

			btnDone.setImage(R.drawable.done_normal);
			btnDone.setTitleColor(this.getResources().getColor(R.color.gray_1));

			if (userHelper.getUser().getType() == UserTypeEnum.STUDENT) {
				btnDone.setEnabled(true);
			}
			if (userHelper.getUser().getType() == UserTypeEnum.PARENTS) {
				btnDone.setEnabled(false);
			}

			btnDone.setTag( data.getId());
		}
		
		
		this.tvSubject.setText(data.getSubject());
		
		String[] parts = data.getDueDate().split(" ");
		String part1 = parts[0];
		this.tvDate.setText(part1);
		
		this.section.setText("By "+data.getTeacherName());
		
		this.ivSubjectIcon.setImageResource(AppUtility.getImageResourceId(data.getSubject_icon_name(), this));
		
		if(data.getTimeOver() == 0)
		{
			bottmlay.setVisibility(View.VISIBLE);
		}
		else if(data.getTimeOver() == 1)
		{
			bottmlay.setVisibility(View.GONE);
		}
		
		
		btnDone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				processDoneButton((CustomButton) v);
			}
		});
		
		btnReminder.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AppUtility.showDateTimePicker(AppConstant.KEY_HOMEWORK+data.getId(), data.getSubject()+ ": " + AppConstant.NOTIFICATION_HOMEWORK, data.getName(), SingleHomeworkActivity.this);
			}
		});
		
		
		
		if(!TextUtils.isEmpty(data.getAttachmentFileName()))
		{
			layoutDownloadHolder.setVisibility(View.VISIBLE);
		}
		else
		{
			layoutDownloadHolder.setVisibility(View.GONE);
		}
		
		
		
		btnDownload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//http://api.champs21.com/api/freeuser/downloadattachment?id=47
				
				
				startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://api.champs21.com/api/freeuser/downloadattachment?id="+data.getId())));
			}
		});
		
		
	}
	
	protected void processDoneButton(CustomButton button) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();

		Log.e("User secret", UserHelper.getUserSecret());
		Log.e("Ass_ID", button.getTag().toString());

		params.put(RequestKeyHelper.USER_SECRET, UserHelper.getUserSecret());
		params.put(RequestKeyHelper.ASSIGNMENT_ID, button.getTag().toString());

		AppRestClient.post(URLHelper.URL_HOMEWORK_DONE, params, doneBtnHandler);
	}
	
	AsyncHttpResponseHandler doneBtnHandler = new AsyncHttpResponseHandler() {
		public void onFailure(Throwable arg0, String arg1) {
			Log.e("button", "failed");
			uiHelper.showMessage(arg1);
			uiHelper.dismissLoadingDialog();
		};

		public void onStart() {
			Log.e("button", "onstart");
			uiHelper.showLoadingDialog("Please wait...");
		};

		public void onSuccess(int arg0, String response) {
			Log.e("response", response);
			Log.e("button", "success");
			uiHelper.dismissLoadingDialog();

			ModelContainer modelContainer = GsonParser.getInstance().parseGson(response);

			if (modelContainer.getStatus().getCode() == 200) {
				data.setIsDone(AppConstant.ACCEPTED);
				
				btnDone.setImage(R.drawable.done_tap);
				btnDone.setTitleColor(SingleHomeworkActivity.this.getResources().getColor(R.color.maroon));

				btnDone.setEnabled(false);
				
			} else {
				uiHelper.showMessage("Error in operation!");
			}



			Log.e("status code", modelContainer.getStatus().getCode() + "");
		};
	};
	
	
	
	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	private void showWebViewContent(String text, WebView webView) {
		
		final String mimeType = "text/html";
		final String encoding = "UTF-8";

		webView.loadDataWithBaseURL("", text, mimeType, encoding, null);
		WebSettings webViewSettings = webView.getSettings();
		webViewSettings.setJavaScriptEnabled(true);
		webView.setWebChromeClient(new WebChromeClient());

				
	}
	
	
	
	private List<HomeworkData> parseHomeworkData(String object) {

		List<HomeworkData> tags = new ArrayList<HomeworkData>();
		Type listType = new TypeToken<List<HomeworkData>>() {
		}.getType();
		tags = (List<HomeworkData>) new Gson().fromJson(object, listType);
		return tags;
	}
	
	
	private void initApicall()
	{
		RequestParams params = new RequestParams();

		//app.showLog("adfsdfs", app.getUserSecret());

		params.put(RequestKeyHelper.USER_SECRET, UserHelper.getUserSecret());
		params.put("id", this.id);
		
		if (userHelper.getUser().getType() == UserTypeEnum.PARENTS) 
		{
			params.put(RequestKeyHelper.STUDENT_ID, userHelper.getUser().getSelectedChild().getProfileId());
			params.put(RequestKeyHelper.BATCH_ID, userHelper.getUser().getSelectedChild().getBatchId());
			
			Log.e("STU_ID", userHelper.getUser().getSelectedChild().getId());
			Log.e("STU_PROFILE_ID", userHelper.getUser().getSelectedChild().getProfileId());
			Log.e("STU_BATCH_ID", userHelper.getUser().getSelectedChild().getBatchId());
		}
		
		
		AppRestClient.post(URLHelper.URL_SINGLE_HOMEWORK, params, singleHomeWorkHandler);
	}
	
	AsyncHttpResponseHandler singleHomeWorkHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			uiHelper.showMessage(arg1);
			if (uiHelper.isDialogActive()) {
				uiHelper.dismissLoadingDialog();
			}
		};

		@Override
		public void onStart() {
			
				uiHelper.showLoadingDialog("Please wait...");
			

		};

		@Override
		public void onSuccess(int arg0, String responseString) {
			

			uiHelper.dismissLoadingDialog();


			Wrapper modelContainer = GsonParser.getInstance()
					.parseServerResponse(responseString);

			if (modelContainer.getStatus().getCode() == 200) {
				
				JsonObject objHomework = modelContainer.getData().get("homework").getAsJsonObject();
				data = gson.fromJson(objHomework.toString(), HomeworkData.class);
				
				Log.e("HHH", "data: "+data.getName());
				
				initAction();
				
			}
			
			else {

			}
			
			

		};
	};
	
	/*@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		webViewContent.destroy();
	};*/

}
