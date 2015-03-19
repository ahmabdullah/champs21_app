package com.champs21.schoolapp.viewhelpers;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.champs21.schoolapp.R;
import com.champs21.schoolapp.callbacks.onGradeDialogButtonClickListener;
import com.champs21.schoolapp.model.MenuData;

public class SelectCategoryDialog extends Dialog implements android.view.View.OnClickListener{

	
	ArrayList<MenuData> cats;;

	private onGradeDialogButtonClickListener listener;


	public SelectCategoryDialog(Context context,onGradeDialogButtonClickListener mListener, String selectedGrades) {
		super(context);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.dialog_select_grade);
		this.setCancelable(false);
		this.listener=mListener;





	}

	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_done:

			
		default:
			break;
		}
	}

	

}
