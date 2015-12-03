package com.classtune.freeversion;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.classtune.freeversion.xmlparsers.CatelogParserHandler;
import com.classtune.freeversion.xmlparsers.IndexParserHandler;
import com.classtune.freeversion.xmlparsers.XmlParser;
import com.classtune.schoolapp.R;
import com.classtune.schoolapp.adapters.ProductGridAdapter;
import com.classtune.schoolapp.model.BaseType;
import com.classtune.schoolapp.model.CmartCategory;
import com.classtune.schoolapp.model.CmartProduct;
import com.classtune.schoolapp.model.Picker;
import com.classtune.schoolapp.model.Picker.PickerItemSelectedListener;
import com.classtune.schoolapp.model.PickerType;
import com.classtune.schoolapp.networking.AppRestClient;
import com.classtune.schoolapp.utils.SPKeyHelper;
import com.classtune.schoolapp.utils.SharedPreferencesHelper;
import com.classtune.schoolapp.utils.URLHelper;
import com.classtune.schoolapp.viewhelpers.CustomRhombusIcon;
import com.classtune.schoolapp.viewhelpers.UIHelper;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class CmartMainActivity extends HomeBaseActivity implements OnClickListener{

	private UIHelper uiHelper;
	private String catagoryId="2";
	private static final String TAG = CmartMainActivity.class.getName();
	private GridView productGrid;
	private List<CmartProduct> products;
	private List<BaseType> cats;
	private static final int numberPerPage = 30;
	private int page = 0;
	private ProductGridAdapter adapter;
	private boolean loading = true;
	private boolean stopLoadingData = false;
	private int previousTotal = 0;
	private boolean refreshFlag = false;
	private boolean positionFlag = false;
	private ImageButton btnCategorySelection;
	private TextView tvSelectedCatText;
	private LinearLayout pb;
	private CustomRhombusIcon myCartBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cmart_main);
		init();
		getAllCategories();
		setUpGrid();
		fetchProductsFromServer();

	}



	private void getAllCategories() {
		String catagoryStr=SharedPreferencesHelper.getInstance().getString(SPKeyHelper.CAMRT_CATS, "");
		if(!TextUtils.isEmpty(catagoryStr))
		{
			IndexParserHandler handler=XmlParser.getInstance().parseCmartIndex(catagoryStr);
			cats.clear();
			CmartCategory cat=new CmartCategory();
			cat.setLabel("All");
			cat.setEntity_id("2");
			cats.add(cat);
			cats.addAll(handler.categories);
		}
	}

	private void fetchProductsFromServer() {
		String url=URLHelper.BASE_URL_CMART+"catalog/category/id/"+catagoryId+"/offset/"+page*numberPerPage+"/count/"+numberPerPage;
		Log.e("URL:", url);
		AppRestClient.postCmart(url, null, catalog);
	}

	private void initiateServerParameters() {
		loading = true;
		stopLoadingData = false;
		page=0;
	}

	private void init() {
		uiHelper = new UIHelper(CmartMainActivity.this);
		productGrid=(GridView)findViewById(R.id.product_grid);
		productGrid.setOnScrollListener(new EndlessScrollListener());
		
		btnCategorySelection=(ImageButton)findViewById(R.id.btn_category_selection);
		btnCategorySelection.setOnClickListener(this);
		tvSelectedCatText=(TextView)findViewById(R.id.tv_selected_cat_text);
		products=new ArrayList<CmartProduct>();
		cats=new ArrayList<BaseType>();
		pb=(LinearLayout)findViewById(R.id.pb);
		myCartBtn=(CustomRhombusIcon)findViewById(R.id.my_cart_btn);
		myCartBtn.setOnClickListener(this);
		
	}

	private void setUpGrid() {
		adapter = new ProductGridAdapter(this, 0, products);
		productGrid.setAdapter(adapter);
		productGrid.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				
				return false;
			}
		});

		productGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				if(pb.getVisibility()==View.GONE)
				{
					Log.e("Position", position+"");
					Intent intent=new Intent(CmartMainActivity.this, CmartProductDetaillActivity.class);
					intent.putExtra("PRODUCT_ID", products.get(position).getId());
					startActivity(intent);
				}
				else
					;
			}
		});

	}

	public class EndlessScrollListener implements OnScrollListener {

		private int visibleThreshold = 7;

		public EndlessScrollListener() {
		}

		public EndlessScrollListener(int visibleThreshold) {
			this.visibleThreshold = visibleThreshold;
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if (loading) {
				if (totalItemCount > previousTotal || refreshFlag) {
					loading = false;
					refreshFlag = false;
					previousTotal = totalItemCount;
					page++;
				}
			}
			if (!stopLoadingData
					&& !loading
					&& (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
				loading = true;
				// requestDataFromWeb();
				fetchProductsFromServer();
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}
	}

	AsyncHttpResponseHandler catalog = new AsyncHttpResponseHandler() {

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			
		}

		@Override
		public void onStart() {
			if(products.size()==0 || page==0)
				pb.setVisibility(View.VISIBLE);
		}

		@Override
		public void onSuccess(int arg0, String responseString) {
			pb.setVisibility(View.GONE);
			Log.e("INDEX", responseString);
			Log.e("here comes parsing",
					"---------------------------------------------");
			CatelogParserHandler handler = XmlParser.getInstance()
					.parseCmartCatelogData(responseString);
			if(!handler.hasMoreItems)
			{
				stopLoadingData=true;
			}
			if(page==0)
			{
				products.clear();
			}
			products.addAll(handler.products);
			adapter.notifyDataSetChanged();
		}

	};


	public void showPicker(PickerType type) {

		Picker picker = Picker.newInstance(0);
		picker.setData(type, cats, PickerCallback,"");
		picker.show(getSupportFragmentManager(), null);
	}

	private void refreshPage()
	{
		initiateServerParameters();
		fetchProductsFromServer();
	}
	
	PickerItemSelectedListener PickerCallback = new PickerItemSelectedListener() {

		@Override
		public void onPickerItemSelected(BaseType item) {

			switch (item.getType()) {
			case CMART_CATEGORY:
				CmartCategory cat = (CmartCategory)item;
				catagoryId=cat.getEntity_id();
				tvSelectedCatText.setText(cat.getText());
				refreshPage();
				break;
			default:
				break;
			}

		}
	};
	
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.btn_category_selection:
			showPicker(PickerType.CMART_CATEGORY);
			break;
		case R.id.my_cart_btn:
			startActivity(new Intent(CmartMainActivity.this, CartActivity.class));
			break;

		default:
			break;
		}
		
	}
}
