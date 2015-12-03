package com.classtune.schoolapp.callbacks;

import java.util.List;

import com.classtune.schoolapp.viewhelpers.GradeDialog;

public interface onGradeDialogButtonClickListener {

	public void onDoneBtnClick(GradeDialog gradeDialog, String gradeStr,List<Integer> grades);
	
}
