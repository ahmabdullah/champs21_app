package com.champs21.freeversion;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import com.champs21.schoolapp.R;
import com.champs21.schoolapp.fragments.ParentAttendenceFragment;
import com.champs21.schoolapp.fragments.YearlyAttendanceReportFragment;

public class AnyFragmentLoadActivity extends ChildContainerActivity {

	public static String PACKAGE_NAME = "com.champs21.schoolapp.fragments";

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		homeBtn.setVisibility(View.VISIBLE);
		logo.setVisibility(View.GONE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anyfragmentload);

		if (getIntent().getExtras() != null) {
			String batchId = getIntent().getExtras().getString("batch_id");
			if (batchId != null) {
				getSupportFragmentManager()
						.beginTransaction()
						.replace(
								R.id.pager_frame,
								YearlyAttendanceReportFragment.newInstance(
										batchId, getIntent().getExtras()
												.getString("student_id")), "")
						.commit();
			} else {
				String value = getIntent().getExtras().getString("class_name");
				loadFragment(value);
			}

		}

	}

	private void loadFragment(String name) {
		String className = PACKAGE_NAME + "." + name;
		try {
			// Object xyz = Class.forName(className).newInstance();

			Constructor<?> ctor = Class.forName(className).getConstructor();
			Fragment object = (Fragment) ctor.newInstance(new Object[] {});
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.pager_frame, object, "").commit();

		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
