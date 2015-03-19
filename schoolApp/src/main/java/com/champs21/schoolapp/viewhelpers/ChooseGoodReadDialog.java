package com.champs21.schoolapp.viewhelpers;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.champs21.schoolapp.R;
import com.champs21.schoolapp.callbacks.ChooseFolderDilogClickListener;
import com.champs21.schoolapp.model.Folder;

public class ChooseGoodReadDialog extends Dialog implements android.view.View.OnClickListener {

	private ChooseFolderDilogClickListener listener;
	private List<Folder> folders;
	private Button addBtn;

	public ChooseGoodReadDialog(Context context,ChooseFolderDilogClickListener mListener,List<Folder> list) {
		super(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.popup_create_folder);
		this.setCancelable(true);
		this.listener=mListener;
		folders=list;
		addBtn=(Button)findViewById(R.id.btn_create_folder);
		addBtn.setOnClickListener(this);
		LinearLayout staticPanel=(LinearLayout)findViewById(R.id.fixed_container);
		LinearLayout customPanel=(LinearLayout)findViewById(R.id.custom_container);
		
		for(int i=0;i<3;i++)
		{
			View view=inflater.inflate(R.layout.fixed_folder_layout, null);
			ImageView image=(ImageView)view.findViewById(R.id.icon);
			TextView text=(TextView)view.findViewById(R.id.title_text);
			text.setText(folders.get(i).getTitle());
			view.setTag(folders.get(i));
			if(i==1)
				image.setImageResource(R.drawable.articles_icon);
			if(i==2)
				image.setImageResource(R.drawable.recipes_icon);
			if(i==3)
				image.setImageResource(R.drawable.resources_icon);
			if(i<2)
			{
				LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,1.0f);
				params.setMargins(0, 0, 2, 0);
				view.setLayoutParams(params);
			}
			view.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dismiss();
					listener.folderClicked((Folder)v.getTag());
				}
			});
			staticPanel.addView(view);
		}
		
		for(int i=3;i<folders.size();i++)
		{
			View view=inflater.inflate(R.layout.dynamic_folder_layout, null);
			ImageView image=(ImageView)view.findViewById(R.id.icon);
			TextView text=(TextView)view.findViewById(R.id.title_text);
			text.setText(folders.get(i).getTitle());
			view.setTag(folders.get(i));
			view.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dismiss();
					listener.folderClicked((Folder)v.getTag());
				}
			});
			customPanel.addView(view);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_create_folder:
			dismiss();
			listener.addFolderBtnClicked();
			break;

		default:
			break;
		}
	}

	
}
