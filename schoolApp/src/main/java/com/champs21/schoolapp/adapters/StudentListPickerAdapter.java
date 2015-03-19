package com.champs21.schoolapp.adapters;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.champs21.schoolapp.R;
import com.champs21.schoolapp.model.BaseType;
import com.champs21.schoolapp.model.MenuData;
import com.champs21.schoolapp.model.PickerType;
import com.champs21.schoolapp.model.StudentAttendance;

public class StudentListPickerAdapter extends BaseAdapter{

	private static class ViewHolder {
		public TextView nameText;
		public TextView rollNoText;
	}
	
	private List<StudentAttendance> items;
	private Activity activity;
	
	public StudentListPickerAdapter(Activity context, int textViewResourceId,
			List<StudentAttendance> items) {
		this.items = items;
		this.activity = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return items.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup viewGroup) {
		
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.student_row_item, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.nameText=(TextView)rowView.findViewById(R.id.label);
			viewHolder.rollNoText=(TextView)rowView.findViewById(R.id.roll_no_text);
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		StudentAttendance item=items.get(index);
		
		
		holder.nameText.setText(item.getStudentName());
		holder.rollNoText.setText(item.getRollNo());
			
		
		return rowView;
	}
	
} 