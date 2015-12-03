package com.classtune.schoolapp.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.classtune.schoolapp.R;
import com.classtune.schoolapp.callbacks.onGradeDialogButtonClickListener;
import com.classtune.schoolapp.model.SchoolJoinAdditionalInfo;
import com.classtune.schoolapp.model.SchoolJoinObject;
import com.classtune.schoolapp.utils.UserHelper.UserTypeEnum;
import com.classtune.schoolapp.viewhelpers.GradeDialog;

public class SchoolJoinGetAdditionalInfoFragment extends DialogFragment {

	private onInfoListener listener;
	private int typeOrdinal;
	private LinearLayout classPanel, rollNoPanel, employeePanel, batchPanel,
			contactNoPanel, studentIdPanel;
	private Button selectGradeBtn;
	private EditText sectionEditText, rollNoEditText, employeeIdEditText,
			contactNoEditText, batchEditText, studentIdEditText;

	private String gradeStr = "";
	private List<Integer> selectedGrades;
	private ImageButton submit;

	public void setData(onInfoListener lis) {
		this.listener = lis;

	}
	
	public interface onInfoListener
	{
		public void onInfoEntered(SchoolJoinObject obj);
	}
	
	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		typeOrdinal = getArguments().getInt("type");
		selectedGrades = new ArrayList<Integer>();

	}

	private ImageButton cancelBtn;

	public SchoolJoinGetAdditionalInfoFragment() {

	}

	public static SchoolJoinGetAdditionalInfoFragment newInstance(int type) {
		SchoolJoinGetAdditionalInfoFragment frag = new SchoolJoinGetAdditionalInfoFragment();
		Bundle args = new Bundle();
		args.putInt("type", type);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		final View view = inflater.inflate(R.layout.school_join_info_popup,
				container, false);
		classPanel = (LinearLayout) view.findViewById(R.id.class_name_panel);
		rollNoPanel = (LinearLayout) view.findViewById(R.id.roll_no_panel);
		employeePanel = (LinearLayout) view
				.findViewById(R.id.employee_id_panel);
		batchPanel = (LinearLayout) view.findViewById(R.id.batch_panel);
		contactNoPanel = (LinearLayout) view
				.findViewById(R.id.contact_no_panel);
		studentIdPanel = (LinearLayout) view
				.findViewById(R.id.student_id_panel);
		sectionEditText = (EditText) view.findViewById(R.id.edit_text_section);
		rollNoEditText = (EditText) view.findViewById(R.id.edit_text_roll);
		employeeIdEditText = (EditText) view
				.findViewById(R.id.edit_text_employee_id);
		contactNoEditText = (EditText) view
				.findViewById(R.id.edit_text_contact_no);
		batchEditText = (EditText) view.findViewById(R.id.edit_text_batch);
		studentIdEditText = (EditText) view
				.findViewById(R.id.edit_text_student_ids);
		submit=(ImageButton)view.findViewById(R.id.btn_submit);
		submit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				doSubmit();
			}
		});
		selectGradeBtn = (Button) view.findViewById(R.id.btn_select_grade);
		selectGradeBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectGrades();
			}
		});
		updateUI();

		cancelBtn = (ImageButton) view.findViewById(R.id.cancel_btn);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getDialog().dismiss();
			}
		});
		return view;
	}

	private void updateUI() {
		// TODO Auto-generated method stub
		UserTypeEnum value = UserTypeEnum.values()[typeOrdinal];
		switch (value) {
		case STUDENT:
			classPanel.setVisibility(View.VISIBLE);
			rollNoPanel.setVisibility(View.VISIBLE);
			break;
		case TEACHER:
			classPanel.setVisibility(View.VISIBLE);
			employeePanel.setVisibility(View.VISIBLE);
			contactNoPanel.setVisibility(View.VISIBLE);
			break;
		case PARENTS:
			classPanel.setVisibility(View.VISIBLE);
			studentIdPanel.setVisibility(View.VISIBLE);
			contactNoPanel.setVisibility(View.VISIBLE);
			break;
		case ALUMNI:
			batchPanel.setVisibility(View.VISIBLE);
			contactNoPanel.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	private void selectGrades() {
		// TODO Auto-generated method stub
		new GradeDialog(getActivity(), new onGradeDialogButtonClickListener() {

			@Override
			public void onDoneBtnClick(GradeDialog gradeDialog,
					String gradeString, List<Integer> grades) {

				gradeDialog.dismiss();

				gradeStr = new String(gradeString);
				selectedGrades.clear();
				selectedGrades.addAll(grades);

				selectGradeBtn.setText("Grades: "
						+ getFormattedGradeString(grades));
			}
		}, gradeStr).show();
	}

	public String getFormattedGradeString(List<Integer> data) {
		String temp = "";
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i) == -1) {
				temp = temp + "Playgroup";
			} else if (data.get(i) == 0) {
				temp = temp + "KG";
			} else {
				temp = temp + data.get(i);
			}

			if (i != data.size() - 1) {
				temp = temp + ",";
			}
		}
		return temp;
	}

	private void doSubmit() {
		SchoolJoinObject joinObject = new SchoolJoinObject();
		SchoolJoinAdditionalInfo info = new SchoolJoinAdditionalInfo();
		UserTypeEnum value = UserTypeEnum.values()[typeOrdinal];
		boolean flag = true;
		String temp;
		switch (value) {
		case STUDENT:
			joinObject.setType(value);
			joinObject.setGradeIDs(getFormattedGradeString(selectedGrades));
			temp = sectionEditText.getText().toString().trim();
			if (TextUtils.isEmpty(temp)) {
				sectionEditText.setError("Enter Section!");
				flag = false;
			} else
				info.setUserSection(temp);
			
			temp = rollNoEditText.getText().toString().trim();
			if (TextUtils.isEmpty(temp)) {
				rollNoEditText.setError("Enter roll!");
				flag = false;
			} else	
				info.setRollNo(temp);
			
			
			joinObject.setAdditionalInfo(info);

			break;
		case TEACHER:
			joinObject.setType(value);
			joinObject.setGradeIDs(getFormattedGradeString(selectedGrades));
			temp = sectionEditText.getText().toString().trim();
			if (TextUtils.isEmpty(temp)) {
				sectionEditText.setError("Enter Section!");
				flag = false;
			} else
				info.setUserSection(temp);
			
			temp = employeeIdEditText.getText().toString().trim();
			if (TextUtils.isEmpty(temp)) {
				employeeIdEditText.setError("Enter employee no!");
				flag = false;
			} else
				info.setEmployeeId(temp);
			
			temp = contactNoEditText.getText().toString().trim();
			if (TextUtils.isEmpty(temp)) {
				contactNoEditText.setError("Enter Contact No!");
				flag = false;
			} else
				info.setContactNo(temp);
			joinObject.setAdditionalInfo(info);
			break;
		case PARENTS:
			joinObject.setType(value);
			joinObject.setGradeIDs(getFormattedGradeString(selectedGrades));
			temp = sectionEditText.getText().toString().trim();
			if (TextUtils.isEmpty(temp)) {
				sectionEditText.setError("Enter Section!");
				flag = false;
			} else
				info.setUserSection(temp);
			
			temp = studentIdEditText.getText().toString().trim();
			if (TextUtils.isEmpty(temp)) {
				studentIdEditText.setError("Enter student Id!");
				flag = false;
			} else
				info.setStudentId(temp);
			
			temp = contactNoEditText.getText().toString().trim();
			if (TextUtils.isEmpty(temp)) {
				contactNoEditText.setError("Enter Contact No!");
				flag = false;
			} else
				info.setContactNo(temp);
			joinObject.setAdditionalInfo(info);
			break;
		case ALUMNI:
			joinObject.setType(value);
			joinObject.setGradeIDs("111");
			temp = batchEditText.getText().toString().trim();
			if (TextUtils.isEmpty(temp)) {
				batchEditText.setError("Enter batch");
				flag = false;
			} else
				info.setBatch(temp);
			
			temp = contactNoEditText.getText().toString().trim();
			if (TextUtils.isEmpty(temp)) {
				contactNoEditText.setError("Enter Contact No!");
				flag = false;
			} else
				info.setContactNo(temp);
			joinObject.setAdditionalInfo(info);
			break;
		default:
			break;
		}
		
		if(flag)
		{
			
			listener.onInfoEntered(joinObject);
			getDialog().dismiss();
		}

	}
}
