package com.classtune.freeversion;

import java.util.ArrayList;
import java.util.List;

import com.classtune.freeversion.xmlparsers.SingleProductParserHandler;
import com.classtune.freeversion.xmlparsers.XmlParser;
import com.classtune.schoolapp.R;
import com.classtune.schoolapp.model.BaseType;
import com.classtune.schoolapp.model.CmartProduct;
import com.classtune.schoolapp.model.CmartProductAttribute;
import com.classtune.schoolapp.model.Picker;
import com.classtune.schoolapp.model.PickerType;
import com.classtune.schoolapp.model.Picker.PickerItemSelectedListener;
import com.classtune.schoolapp.networking.AppRestClient;
import com.classtune.schoolapp.utils.AppUtility;
import com.classtune.schoolapp.utils.URLHelper;
import com.classtune.schoolapp.viewhelpers.UIHelper;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CmartProductDetaillActivity extends HomeBaseActivity {
	private ImageView productImage;
	private TextView tvProductTitle, tvProductSummary, tvColor, tvSize,
			tvQuantity, tvPrice;
	private ImageButton ibAddToCart;
	private UIHelper uiHelper;
	private String productId;
	private CmartProduct product;
	private List<BaseType> colorAttrs;
	private List<BaseType> sizeAttrs;
	private BaseType selectedColor, selectedSize;
	public ProgressBar pb;
	private LinearLayout colorContainer, sizeContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cmart_product_detaill);
		init();
		getIntentData();
		fetchProductsFromServer();
	}

	private void getIntentData() {
		productId = getIntent().getStringExtra("PRODUCT_ID");
	}

	public void showPicker(PickerType type, List<BaseType> data) {

		Picker picker = Picker.newInstance(0);
		picker.setData(type, data, PickerCallback, "");
		picker.show(getSupportFragmentManager(), null);
	}

	PickerItemSelectedListener PickerCallback = new PickerItemSelectedListener() {

		@Override
		public void onPickerItemSelected(BaseType item) {

			switch (item.getType()) {
			case CMART_ATTR_COLOR:
				selectedColor = item;
				if (selectedColor != null) {
					tvColor.setText(selectedColor.getText());
					setSelectedSize();
					if (selectedSize != null)
						tvSize.setText(selectedSize.getText());
				}
				break;
			case CMART_ATTR_SIZE:
				selectedSize = item;
				if (selectedSize != null)
					tvSize.setText(selectedSize.getText());
				break;
			default:
				break;
			}
		}
	};

	private void init() {
		uiHelper = new UIHelper(CmartProductDetaillActivity.this);
		productImage = (ImageView) findViewById(R.id.product_image);
		tvProductTitle = (TextView) findViewById(R.id.title_text);
		tvProductSummary = (TextView) findViewById(R.id.details_text);
		tvPrice = (TextView) findViewById(R.id.tv_price);
		tvColor = (TextView) findViewById(R.id.color_btn);
		pb = (ProgressBar) findViewById(R.id.pb);
		colorContainer = (LinearLayout) findViewById(R.id.color_container);
		sizeContainer = (LinearLayout) findViewById(R.id.size_container);
		ibAddToCart =(ImageButton)findViewById(R.id.btn_add_to_cart);
		ibAddToCart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(AppUtility.isInternetConnected())
					addToCart();
			
			}

		});
		tvColor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPicker(PickerType.CMART_ATTR_COLOR, colorAttrs);
			}
		});
		tvSize = (TextView) findViewById(R.id.size_btn);
		tvSize.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPicker(PickerType.CMART_ATTR_SIZE, product.getAttributes()
						.get(selectedColor));
			}
		});
	}


	private void addToCart() {
		String url = URLHelper.BASE_URL_CMART + "cart/add";
		RequestParams params=new RequestParams();
		if(selectedColor!=null)
		{
			CmartProductAttribute attr=((CmartProductAttribute)selectedColor);
			String key="super_attribute["+attr.getSuperAttributeId()+"]";
			String value=attr.getCode()+"";
			params.put(key, value);
		}
		
		if(selectedSize!=null)
		{
			CmartProductAttribute attr=((CmartProductAttribute)selectedSize);
			String key="super_attribute["+attr.getSuperAttributeId()+"]";
			String value=attr.getCode()+"";
			params.put(key, value);
		}
		
		params.put("product", productId);
		params.put("qty", "1");
		
		AppRestClient.postCmart(url, params, addToCartHandler);
		
	}
	
	AsyncHttpResponseHandler addToCartHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			if (uiHelper.isDialogActive()) {
				uiHelper.dismissLoadingDialog();

			}
			uiHelper.showMessage(arg1);
		}

		@Override
		public void onStart() {
			uiHelper.showLoadingDialog(getString(R.string.loading_text));
		}

		@Override
		public void onSuccess(int arg0, String responseString) {
			Log.e("INDEX", responseString);
			Log.e("here comes parsing",
					"---------------------------------------------");
			
			Log.e("here end parsing",
					"---------------------------------------------");
			if (uiHelper.isDialogActive()) {
				uiHelper.dismissLoadingDialog();
				startActivity(new Intent(CmartProductDetaillActivity.this, CartActivity.class));
			}
		}

	};
	
	
	private void fetchProductsFromServer() {
		String url = URLHelper.BASE_URL_CMART + "catalog/product/id/"
				+ productId;
		Log.e("URL:", url);
		AppRestClient.postCmart(url, null, singleProductFetchHandler);
	}

	AsyncHttpResponseHandler singleProductFetchHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			if (uiHelper.isDialogActive()) {
				uiHelper.dismissLoadingDialog();

			}
			uiHelper.showMessage(arg1);
		}

		@Override
		public void onStart() {
			uiHelper.showLoadingDialog(getString(R.string.loading_text));
		}

		@Override
		public void onSuccess(int arg0, String responseString) {
			Log.e("INDEX", responseString);
			Log.e("here comes parsing",
					"---------------------------------------------");
			SingleProductParserHandler handler = XmlParser.getInstance()
					.parseSingleProduct(responseString);
			product = handler.product;
			colorAttrs = new ArrayList<BaseType>();
			for (BaseType key : product.getAttributes().keySet()) {
				Log.e("Label", key.getText());
				colorAttrs.add(key);
			}
			if (colorAttrs.size() > 0) {
				selectedColor = colorAttrs.get(0);
				setSelectedSize();
			}
			Log.e("here end parsing",
					"---------------------------------------------");
			if (uiHelper.isDialogActive()) {
				uiHelper.dismissLoadingDialog();
			}
			upadateUI();
		}

	};

	private void setSelectedSize() {
		selectedSize = product.getAttributes().get(selectedColor).get(0);
	} 

	private void upadateUI() {

		ImageLoader.getInstance().displayImage(product.getIcon(), productImage,
				AppUtility.getOption(), new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						pb.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						pb.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						pb.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						// TODO Auto-generated method stub
					}
				});
		tvProductTitle.setText(product.getName());
		tvProductSummary.setText(product.getShortDescription());
		if (selectedColor != null)
			tvColor.setText(selectedColor.getText());
		else {
			colorContainer.setVisibility(View.GONE);
		}
		if (selectedSize != null)
			tvSize.setText(selectedSize.getText());
		else {
			sizeContainer.setVisibility(View.GONE);
		}
		String regularPriceText;
		if (TextUtils.isEmpty(product.getPrice().getSpecialPrice().toString()
				.trim())) {
			regularPriceText = String.format(
					getResources().getString(R.string.product_price_text),
					product.getPrice().getRegularPrice());
		} else {
			regularPriceText = String.format(
					getResources().getString(R.string.product_price_text),
					product.getPrice().getSpecialPrice());
		}
		tvPrice.setText(regularPriceText);
	}
}
