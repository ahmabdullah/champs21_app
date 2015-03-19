package com.champs21.schoolapp.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.champs21.schoolapp.R;
import com.champs21.schoolapp.model.BaseType;
import com.champs21.schoolapp.model.PickerType;
import com.champs21.schoolapp.model.Picker.PickerItemSelectedListener;
import com.champs21.schoolapp.utils.UserHelper.UserTypeEnum;

public class SchoolJoinUserTypeSelection extends DialogFragment {

	private UserTypeSelectedListener listener;

	@Override
	public void onActivityCreated(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onActivityCreated(arg0);
		// getDataFromDb();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	private ImageButton cancelBtn;
	private LinearLayout studentBtn, teacherBtn, parentsBtn, alumniBtn;

	public SchoolJoinUserTypeSelection() {

	}

	public static SchoolJoinUserTypeSelection newInstance() {
		SchoolJoinUserTypeSelection frag = new SchoolJoinUserTypeSelection();
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		final View view = inflater.inflate(
				R.layout.school_join_user_type_selection, container, false);
		studentBtn = (LinearLayout) view.findViewById(R.id.student_layout_btn);
		studentBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getDialog().dismiss();
				listener.onUserTypeSelected(UserTypeEnum.STUDENT);
			}
		});
		teacherBtn = (LinearLayout) view.findViewById(R.id.teacher_layout_btn);
		teacherBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getDialog().dismiss();
				listener.onUserTypeSelected(UserTypeEnum.TEACHER);
			}
		});
		parentsBtn = (LinearLayout) view.findViewById(R.id.parents_layout_btn);
		parentsBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getDialog().dismiss();
				listener.onUserTypeSelected(UserTypeEnum.PARENTS);
			}
		});
		alumniBtn = (LinearLayout) view.findViewById(R.id.alumni_layout_btn);
		alumniBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getDialog().dismiss();
				listener.onUserTypeSelected(UserTypeEnum.ALUMNI);
			}
		});
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

	public void setData(UserTypeSelectedListener lis) {
		this.listener = lis;

	}

	public interface UserTypeSelectedListener {
		public void onUserTypeSelected(UserTypeEnum type);
	}

}
