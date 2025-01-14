package com.classtune.schoolapp;

import java.util.List;

import com.classtune.freeversion.HomeBaseActivity;
import com.classtune.freeversion.PaidVersionHomeFragment;
import com.classtune.schoolapp.adapters.StudentListPickerAdapter;
import com.classtune.schoolapp.model.StudentAttendance;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

public class StudentListActivity extends HomeBaseActivity {

	private TextView titleText, batchText, dateText;
	private ListView studentListView;
	private List<StudentAttendance> students;
	private String date, title;
	private StudentListPickerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_student_list);
		studentListView = (ListView) findViewById(R.id.student_list);
		titleText = (TextView) findViewById(R.id.title_text);
		batchText = (TextView) findViewById(R.id.batch_text);
		dateText = (TextView) findViewById(R.id.date_text);

		students = getIntent().getParcelableArrayListExtra("students");
		date = getIntent().getStringExtra("date");
		title = getIntent().getStringExtra("title");
		batchText.setText(PaidVersionHomeFragment.selectedBatch.getName());
		dateText.setText(date);
		titleText.setText(title);
		adapter = new StudentListPickerAdapter(StudentListActivity.this, 0, students);
		studentListView.setAdapter(adapter);

	}

}
