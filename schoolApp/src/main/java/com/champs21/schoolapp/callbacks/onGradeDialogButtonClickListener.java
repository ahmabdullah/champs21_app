package com.champs21.schoolapp.callbacks;

import java.util.List;

import com.champs21.schoolapp.viewhelpers.GradeDialog;

public interface onGradeDialogButtonClickListener {

	public void onDoneBtnClick(GradeDialog gradeDialog, String gradeStr,List<Integer> grades);
	
}
