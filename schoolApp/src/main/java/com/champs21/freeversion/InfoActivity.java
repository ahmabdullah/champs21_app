package com.champs21.freeversion;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.champs21.schoolapp.R;

public class InfoActivity extends ChildContainerActivity implements OnClickListener{
	
	private TextView headerTextVew, descriptionTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		initViews();
		setContent();
	}

	private void setContent() {
		// TODO Auto-generated method stub
		headerTextVew.setText(getIntent().getStringExtra("title"));
		descriptionTextView.setText(Html.fromHtml(getIntent().getStringExtra("description")));
	}

	private void initViews() {
		headerTextVew = (TextView) findViewById(R.id.tv_header_text);
		descriptionTextView = (TextView) findViewById(R.id.tv_description_body);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
	}
}
