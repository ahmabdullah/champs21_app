package com.champs21.schoolapp.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.champs21.schoolapp.R;
import com.champs21.schoolapp.model.CmartProduct;
import com.champs21.schoolapp.model.CmartProductPrice;
import com.champs21.schoolapp.utils.AppUtility;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class ProductGridAdapter extends BaseAdapter {
	private List<CmartProduct> products;
	private Activity activity;

	public ProductGridAdapter(Activity context, int textViewResourceId,
			List<CmartProduct> items) {
		this.products = items;
		this.activity = context;
	}

	public static class ViewHolder {
		public ImageView imgView;
		public TextView txtViewTitle;
		public TextView txtViewProductPriceRegular;
		public TextView txtViewProductPriceSpecial;
		public ProgressBar pb;
		public RelativeLayout imgLayout;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder view;
		LayoutInflater inflator = activity.getLayoutInflater();
		int width;
		int height;
		
			if (convertView == null) {
				view = new ViewHolder();
				convertView = inflator.inflate(R.layout.product_grid_item,null);
	
				view.txtViewTitle = (TextView) convertView
						.findViewById(R.id.textView1);
				view.imgView = (ImageView) convertView
						.findViewById(R.id.imageView1);
				
				view.txtViewProductPriceRegular=(TextView) convertView
						.findViewById(R.id.regular_price);
				view.txtViewProductPriceSpecial=(TextView) convertView
						.findViewById(R.id.special_price);
				view.pb=(ProgressBar) convertView.findViewById(R.id.pb);
				view.imgLayout=(RelativeLayout)convertView.findViewById(R.id.imageView_container);
				convertView.setTag(view);
			} else {
				view = (ViewHolder) convertView.getTag();
			}
			width=AppUtility.getProductImageWidth(activity);
			height=width;
		
		RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(width, height);
		//params.addRule(RelativeLayout.BELOW, R.id.count_container);
		view.imgLayout.setLayoutParams(params);
		
		view.txtViewTitle.setText(products.get(position).getName());
		
		final CmartProductPrice price=products.get(position).getPrice();
		final TextView regular=view.txtViewProductPriceRegular;
		final TextView special=view.txtViewProductPriceSpecial;
		
		if(TextUtils.isEmpty(price.getSpecialPrice().toString().trim()))
		{
			special.setVisibility(View.GONE);
			String regularPriceText = String.format(
				    ((Context)activity).getResources().getString(R.string.product_price_text),
				    products.get(position).getPrice().getRegularPrice());
			regular.setText(regularPriceText);
			regular.setPaintFlags(regular.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
		}
		else
		{
			special.setVisibility(View.VISIBLE);
			String specialPriceText = String.format(
				    ((Context)activity).getResources().getString(R.string.product_price_text),
				    products.get(position).getPrice().getSpecialPrice());
			special.setText(specialPriceText);
			String regularPriceText = String.format(
				    ((Context)activity).getResources().getString(R.string.product_price_text),
				    products.get(position).getPrice().getRegularPrice());
			regular.setText(regularPriceText);
			regular.setPaintFlags(regular.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		}
		
		final ProgressBar pbFinal=view.pb;
		ImageLoader.getInstance().displayImage(products.get(position).getIcon(), view.imgView, AppUtility.getOption(),new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				// TODO Auto-generated method stub
				pbFinal.setVisibility(View.VISIBLE);
				
			}
			
			@Override
			public void onLoadingFailed(String imageUri, View view,
					FailReason failReason) {
				// TODO Auto-generated method stub
				pbFinal.setVisibility(View.GONE);
			}
			
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				// TODO Auto-generated method stub
				pbFinal.setVisibility(View.GONE);
			}
			
			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				// TODO Auto-generated method stub
				
			}
		});
		//displayImage(view.imgView,products.get(position).getImgUrl(),(float)5,view.pb);
		return convertView;
	}

	
	@Override
	public int getCount() {
		return products.size();
	}

	@Override
	public Object getItem(int arg0) {
		return products.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}
}