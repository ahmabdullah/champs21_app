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

public class PickerAdapter extends BaseAdapter{

	private static class ViewHolder {
		public TextView text;
		public TextView id;
	}
	
	private List<BaseType> items;
	private Activity activity;
	
	public PickerAdapter(Activity context, int textViewResourceId,
			List<BaseType> items,BaseType item) {
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
		BaseType item=items.get(index);
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			ViewHolder viewHolder = new ViewHolder();
			if(item.getType()==PickerType.TEACHER_STUDENT)
			{
				rowView = inflater.inflate(R.layout.custom_picker_row_item, null);
				viewHolder.id=(TextView)rowView.findViewById(R.id.tv_roll_number);
			}
			else
			{
				rowView = inflater.inflate(R.layout.picker_row_item, null);
			}
			viewHolder.text=(TextView)rowView.findViewById(R.id.label);
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		holder.text.setText(capitalize(item.getText().toString()));
		if(item.getType()==PickerType.TEACHER_STUDENT)
		{
			//holder.id.setText(((StudentAttendance)item).getRollNo());
		}
		return rowView;
	}
	private String capitalize(final String line) {
		   return Character.toUpperCase(line.charAt(0)) + line.substring(1);
		}
	
} 