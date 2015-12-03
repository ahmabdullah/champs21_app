package com.classtune.schoolapp.fragments;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.classtune.freeversion.SchoolCandleSettingActivity;
import com.classtune.schoolapp.R;
import com.classtune.schoolapp.adapters.CropOptionAdapter;
import com.classtune.schoolapp.model.BaseType;
import com.classtune.schoolapp.model.CropOption;
import com.classtune.schoolapp.model.MenuData;
import com.classtune.schoolapp.model.Picker;
import com.classtune.schoolapp.model.Picker.PickerItemSelectedListener;
import com.classtune.schoolapp.model.PickerType;
import com.classtune.schoolapp.model.SchoolCandleSetting;
import com.classtune.schoolapp.model.Wrapper;
import com.classtune.schoolapp.networking.AppRestClient;
import com.classtune.schoolapp.utils.AppConstant;
import com.classtune.schoolapp.utils.AppUtility;
import com.classtune.schoolapp.utils.GsonParser;
import com.classtune.schoolapp.utils.RequestKeyHelper;
import com.classtune.schoolapp.utils.URLHelper;
import com.classtune.schoolapp.utils.UserHelper;
import com.classtune.schoolapp.viewhelpers.CustomButton;
import com.classtune.schoolapp.viewhelpers.UIHelper;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SchoolCandleFragment extends Fragment implements OnClickListener{

	private View view;
	private CustomButton attachFileBtn, pictureBtn, sendBtn;
	private ImageButton settingButton;
	private final static int REQUEST_CODE_FILE_CHOOSER = 101;
	private String selectedFilePath = "";
	// private String imageFilePath="";
	private LinearLayout fileNameContainer, imageNameContainer;
	private TextView tvFileName, tvImageName, tvSelectedCategory;
	private ImageView fileCross, imageCross;
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	private static File schoolDirectory = null;
	private Uri uri = null;
	private String mCurrentPhotoPath;
	private String selectedImagePath = "";
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private final int REQUEST_CODE_CAMERA = 102;
	private final int REQUEST_CODE_GELLERY = 103;
	private final int REQUEST_CODE_CROP = 104;
	private UIHelper uiHelper;
	private ImageButton btnSelectCat;
	private List<BaseType> cats;
	private String catId = "";
	private EditText etCandleTitle, etCandleDescription;
	private UserHelper userHelper;
	private LinearLayout selectCatPanel;
	private SchoolCandleSetting settingsData = new SchoolCandleSetting(0,0,new ArrayList<Integer>());
	private String schoolId;
	private final static int REQUEST_CODE_CANDLE_SETTING = 50;

	
	public static SchoolCandleFragment newInstance(int schoolId) {
		SchoolCandleFragment f = new SchoolCandleFragment();
		// Supply index input as an argument.
		Bundle args = new Bundle();
		args.putInt("school_id", schoolId);
		f.setArguments(args);
		return f;
	}
	
	// private List<String> picsUrls;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		updateFilenamePanel(false);
		updateImagenamePanel(false);
		schoolDirectory = new File(getActivity().getFilesDir().getPath()
				+ "/champs21");
		schoolDirectory.mkdirs(); // create folders where write files

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}

		// picsUrls=new ArrayList<String>();
	}

	private void clearInputs() {
		fileNameContainer.setVisibility(View.GONE);
		imageNameContainer.setVisibility(View.GONE);
		tvSelectedCategory.setText("");
		etCandleTitle.setText("");
		etCandleDescription.setText("");
	}

	

	

	public void showPicker(PickerType type) {

		Picker picker = Picker.newInstance(0);
		picker.setData(type, cats, PickerCallback , "Select your type");
		picker.show(getChildFragmentManager(), null);
	}

	PickerItemSelectedListener PickerCallback = new PickerItemSelectedListener() {

		@Override
		public void onPickerItemSelected(BaseType item) {

			switch (item.getType()) {
			case CANDLE_CATEGORY:
				MenuData mdata = (MenuData)item;
				tvSelectedCategory.setText(mdata.getTitle());
				catId = mdata.getId();
				break;
			default:
				break;
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {

            if(bundle.containsKey("key_school_id"))
            {
                schoolId = String.valueOf(bundle.getInt("key_school_id", 0));
            }


        }
        else
            schoolId= String.valueOf(getArguments().getInt("school_id", 0));

		uiHelper = new UIHelper(getActivity());
		cats = new ArrayList<BaseType>();
		userHelper = new UserHelper(getActivity());
		loadTypeData();
	}

	public void loadTypeData()
	{
		cats.clear();
		cats.add(new MenuData("1","General"));
		cats.add(new MenuData("2","Question"));
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_candle_school, null);
		attachFileBtn = (CustomButton) view
				.findViewById(R.id.btn_candle_attach_files);
		attachFileBtn.setOnClickListener(this);
		pictureBtn = (CustomButton) view
				.findViewById(R.id.btn_candle_upload_pic);
		pictureBtn.setOnClickListener(this);
		sendBtn = (CustomButton) view.findViewById(R.id.btn_candle_submit);
		sendBtn.setOnClickListener(this);
		fileNameContainer = (LinearLayout) view
				.findViewById(R.id.file_attached_layout);
		imageNameContainer = (LinearLayout) view
				.findViewById(R.id.image_attached_layout);
		fileCross = (ImageView) view.findViewById(R.id.btn_cross_file);
		fileCross.setOnClickListener(this);
		imageCross = (ImageView) view.findViewById(R.id.btn_cross_image);
		imageCross.setOnClickListener(this);
		tvFileName = (TextView) view.findViewById(R.id.tv_file_name);
		tvImageName = (TextView) view.findViewById(R.id.tv_image_name);
		btnSelectCat = (ImageButton) view
				.findViewById(R.id.btn_candle_category_state);
		btnSelectCat.setOnClickListener(this);
		tvSelectedCategory = (TextView) view
				.findViewById(R.id.tv_candle_cateogry);
		etCandleTitle = (EditText) view.findViewById(R.id.et_candle_title);
		etCandleDescription = (EditText) view
				.findViewById(R.id.et_candle_description);
		selectCatPanel=(LinearLayout)view.findViewById(R.id.select_cat_panel);
		selectCatPanel.setOnClickListener(this);
		settingButton=(ImageButton)view.findViewById(R.id.settings_btn);
		settingButton.setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_candle_attach_files:
			showChooser();
			break;
		case R.id.btn_candle_upload_pic:
			showPicChooserDialog();
			break;
		case R.id.btn_candle_submit:
			validateAndPostData();
			break;
		case R.id.btn_cross_file:
			selectedFilePath = "";
			updateFilenamePanel(false);
			break;
		case R.id.btn_cross_image:
			selectedImagePath = "";
			updateImagenamePanel(false);
			break;
		case R.id.btn_candle_category_state:
			showPicker(PickerType.CANDLE_CATEGORY);
			break;
		case R.id.select_cat_panel:
			showPicker(PickerType.CANDLE_CATEGORY);
			break;
		case R.id.settings_btn:
			Intent intent = new Intent(getActivity(), SchoolCandleSettingActivity.class);
			intent.putExtra("settings", settingsData);
			startActivityForResult(intent, REQUEST_CODE_CANDLE_SETTING);
			break;
		default:
			break;
		}

	}

	
	private void showPicChooserDialog() {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());

		

		alertDialogBuilder
				.setMessage("Select source")
				.setCancelable(false)
				.setPositiveButton("Gallery",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(final DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();

								/*Intent intent = new Intent();
								intent.setType("image/*");
								intent.setAction(Intent.ACTION_GET_CONTENT);
								startActivityForResult(Intent.createChooser(intent, "Select Picture"),
										1);*/
								
								dispatchOpenGelleryApp();

							}
						})
				.setNegativeButton("Camera",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(final DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
								
								dispatchTakePictureIntent();

								/*Intent takePicture = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								startActivityForResult(takePicture, 0);*/
							}
						});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.setCancelable(true);

		alertDialog.show();

	}
	
	private void validateAndPostData() {
		String temp = etCandleTitle.getText().toString().trim();
		String tempDescription = etCandleDescription.getText().toString()
				.trim();
		if (TextUtils.isEmpty(temp)) {
			etCandleTitle.requestFocus();
			etCandleTitle.setError(Html.fromHtml("<font color='red'>"
					+ getString(R.string.empty_title_error) + "</font>"));

		}

		else if (TextUtils.isEmpty(tempDescription)) {
			etCandleDescription.requestFocus();
			etCandleDescription.setError(Html.fromHtml("<font color='red'>"
					+ getString(R.string.empty_description_error) + "</font>"));

		} else if (catId.equalsIgnoreCase("")) {
			uiHelper.showErrorDialog(getString(R.string.select_category_error));

		} else
			postData();

	}

	private void postData() {

		RequestParams params = new RequestParams();
		
		
		
		params.put(RequestKeyHelper.USERNAME, "Plabon Modak");
		params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());
		params.put("school_id", schoolId);
		
		params.put(RequestKeyHelper.CANDLE_TYPE, catId);
		params.put(RequestKeyHelper.HEADLINE, etCandleTitle.getText()
				.toString().trim());
		params.put(RequestKeyHelper.CONTENT, etCandleDescription.getText()
				.toString().trim());
		
		for(int i=0;i<settingsData.getTypeList().size();i++)
		{
			params.put("type[" + i + "]", settingsData.getTypeList().get(i)+"");	
		}
		params.put("can_comment", settingsData.getCanComment()+"");
		params.put("show_comment_to_all", settingsData.getIsShow()+"");
				
		if (!selectedFilePath.equalsIgnoreCase("")) {
			File myFile = new File(selectedFilePath);
			try {
				params.put(RequestKeyHelper.ATTACHFILE, myFile);
			} catch (FileNotFoundException e) {
			}
		}

		if (!selectedImagePath.equalsIgnoreCase("")) {
			File myImage = new File(selectedImagePath);
			try {
				
				
				params.put(RequestKeyHelper.IMAGEFILE, myImage);
			} catch (FileNotFoundException e) {
			}
		}

		AppRestClient.post(URLHelper.URL_FREE_VERSION_POST_SCHOOL_CANDLE, params,
				candleHandle);

	}

	AsyncHttpResponseHandler candleHandle = new AsyncHttpResponseHandler() {
		public void onStart() {
			uiHelper.showLoadingDialog(getString(R.string.loading_text));
		};

		public void onSuccess(int arg0, String arg1) {
			uiHelper.dismissLoadingDialog();
			Wrapper wrapper = GsonParser.getInstance()
					.parseServerResponse(arg1);
			if (wrapper.getStatus().getCode() == AppConstant.RESPONSE_CODE_SUCCESS) {
				uiHelper.showMessage("Post saved and sent request for publish.");
				clearInputs();
			} else {
				uiHelper.showMessage(wrapper.getStatus().toString());
			}

			Log.e("Response", arg1);
		};

		public void onFailure(Throwable arg0, String arg1) {
			uiHelper.dismissLoadingDialog();
			uiHelper.showMessage(arg1);
			Log.e("Response", arg1);

		};
	};

	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_CANDLE_SETTING:
			if (resultCode == getActivity().RESULT_OK) {
				settingsData= (SchoolCandleSetting)data.getParcelableExtra("settings");
				
				//Log.e("GET_SEETTING", myParcelableObject.toString());
			}
			
			break;
		case REQUEST_CODE_CAMERA:
			if (resultCode == getActivity().RESULT_OK) {
				// Log.e("addAdvertiseController.mCurrentPhotoPath",""+addAdvertiseController.mCurrentPhotoPath);
				dispatchCropIntent(Uri.fromFile(new File(mCurrentPhotoPath)));
			}
			if (resultCode == getActivity().RESULT_CANCELED) {
				return;
			}
			break;
		case REQUEST_CODE_GELLERY:
			if (resultCode == getActivity().RESULT_OK) {
				// addAdvertiseController.mCurrentPhotoPath=getFilePath(data.getData());
				// Log.e("addAdvertiseController.mCurrentPhotoPath2",""+addAdvertiseController.mCurrentPhotoPath);
				dispatchCropIntent(data.getData());
			}
			if (resultCode == getActivity().RESULT_CANCELED) {
				return;
			}
			break;

		case REQUEST_CODE_CROP:
			if (resultCode == getActivity().RESULT_OK) {
				// Log.e("addAdvertiseController.mCurrentPhotoPath3",""+addAdvertiseController.mCurrentPhotoPath);
				File file = new File(mCurrentPhotoPath);
				Log.e("Normal file size:", "Image size before compress:"
						+ (file.length() / 1024) + "");
				/*
				 * if((file.length()/1024)<300) { AlertDialog_big_image_size();
				 * } else {
				 */
				handleBigCameraPhoto(false,
						Uri.fromFile(new File(mCurrentPhotoPath)));
				// }
			}

			if (resultCode == getActivity().RESULT_CANCELED) {
				return;
			}
			break;

		case REQUEST_CODE_FILE_CHOOSER:
			// If the file selection was successful
			if (resultCode == getActivity().RESULT_OK) {
				if (data != null) {
					// Get the URI of the selected file
					final Uri uri = data.getData();
					if (uri.getLastPathSegment().endsWith("doc")
							|| uri.getLastPathSegment().endsWith("docx")
							|| uri.getLastPathSegment().endsWith("pdf")) {
						try {
							// Get the file path from the URI
							final String path = FileUtils.getPath(
									getActivity(), uri);
							selectedFilePath = path;
							updateFilenamePanel(true);
						} catch (Exception e) {
							Log.e("FileSelectorTestActivity",
									"File select error", e);
						}
					} else {
						Toast.makeText(getActivity(), "Invalid file type",
								Toast.LENGTH_SHORT).show();
					}
					Log.e("File", "Uri = " + uri.toString());

				}
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void updateFilenamePanel(boolean isVisible) {
		if (isVisible) {
			fileNameContainer.setVisibility(View.VISIBLE);
			tvFileName.setText(getFileNameFromPath(selectedFilePath));
		} else {
			fileNameContainer.setVisibility(View.GONE);
		}
	}

	private void updateImagenamePanel(boolean isVisible) {
		if (isVisible) {
			imageNameContainer.setVisibility(View.VISIBLE);
			tvImageName.setText(getFileNameFromPath(selectedImagePath));
		} else {
			imageNameContainer.setVisibility(View.GONE);
		}
	}

	private String getFileNameFromPath(String path) {
		String[] tokens = path.split("/");
		return tokens[tokens.length - 1];
	}

	private void showChooser() {
		// Use the GET_CONTENT intent from the utility class
		Intent target = FileUtils.createGetContentIntent();
		// Create the chooser Intent
		Intent intent = Intent.createChooser(target,
				getString(R.string.chooser_title));
		try {
			startActivityForResult(intent, REQUEST_CODE_FILE_CHOOSER);
		} catch (ActivityNotFoundException e) {
			// The reason for the existence of aFileChooser
		}
	}

	public void dispatchOpenGelleryApp() {

		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),
				REQUEST_CODE_GELLERY);

	}

	public void dispatchTakePictureIntent() {

		PackageManager pm = getActivity().getPackageManager();
		if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false) {
			Toast.makeText(getActivity(), "Camera Nai", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File f = null;

		try {
			f = setUpPhotoFile();
			mCurrentPhotoPath = f.getAbsolutePath();
			takePictureIntent
					.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

		} catch (IOException e) {
			e.printStackTrace();
			f = null;
			mCurrentPhotoPath = null;
		}

		startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);
	}

	private File setUpPhotoFile() throws IOException {

		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();
		// Uri.fromFile(f);
		return f;
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX,
				albumF);
		return imageF;
	}

	private String getAlbumName() {
		return getString(R.string.album_name);
	}

	private File getAlbumDir() {
		File storageDir = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			storageDir = mAlbumStorageDirFactory
					.getAlbumStorageDir(getAlbumName());

			if (storageDir != null) {
				if (!storageDir.mkdirs()) {
					if (!storageDir.exists()) {
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}

		} else {
			Log.v(getString(R.string.app_name),
					"External storage is not mounted READ/WRITE.");
		}

		return storageDir;
	}

	private void handleBigCameraPhoto(boolean resizeFlag, Uri uriParam) {

		if (uriParam != null) {
			setPic(resizeFlag, uriParam);
			selectedImagePath = mCurrentPhotoPath;
			updateImagenamePanel(true);
			// galleryAddPic();
			mCurrentPhotoPath = null;
		}

	}

	public void AlertDialog_big_image_size() {

		AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
		ad.setIcon(R.drawable.cross);
		ad.setTitle(R.string.big_image_size);
		ad.setMessage(R.string.cannot_add_image);

		ad.setPositiveButton("OK",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int arg1) {
						// OK

						// Toast.makeText(ProductDetailsActivity.this, "ok",
						// 3000).show();
					}
				});

		ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
			}
		});

		ad.show();

	}

	private void galleryAddPic() {
		Intent mediaScanIntent = new Intent(
				"android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		getActivity().sendBroadcast(mediaScanIntent);
	}

	private void setPic(boolean resizeFlag, Uri uriParam) {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;

		ContentResolver res = getActivity().getContentResolver();
		Bitmap bitmap = null;
		InputStream is = null;
		try {
			is = res.openInputStream(uriParam);
			if (resizeFlag == true) {
				bitmap = BitmapFactory.decodeStream(is, null, bmOptions);

			} else {
				bitmap = BitmapFactory.decodeStream(is);
			}
			is.close();
		} catch (FileNotFoundException e1) {

			e1.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		/* Figure out which way needs to be reduced less */
		/* Get the size of the ImageView */
		/*
		 * int targetW = mImageView.getWidth(); int targetH =
		 * mImageView.getHeight();
		 */
		if (resizeFlag) {
			int photoW = bmOptions.outWidth;
			int photoH = bmOptions.outHeight;
			int value = AppUtility.getImageViewerImageHeight(getActivity());
			int targetW = value;
			int targetH = value;

			int scaleFactor = 1;
			if ((targetW > 0) || (targetH > 0)) {
				scaleFactor = Math.min(photoW / targetW, photoH / targetH);
			}

			/* Set bitmap options to scale the image decode target */
			bmOptions.inJustDecodeBounds = false;
			bmOptions.inSampleSize = scaleFactor;
			bmOptions.inPurgeable = true;

			/* Decode the JPEG file into a Bitmap */
			try {
				is = res.openInputStream(uriParam);
				bitmap = BitmapFactory.decodeStream(is, null, bmOptions);
				is.close();
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		// force orientation to portrait
		if (bitmap.getWidth() > bitmap.getHeight()) {
			Matrix matrix = new Matrix();
			matrix.postRotate(90);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
		}
		// Log.e("Aise", "Aise");

		// File file = new File(addAdvertiseController.mCurrentPhotoPath);
		// Log.e("Normal file size:", file.length() + "");
		// Toast.makeText(AddAdvertiseActivity.this, file.length()+"",
		// Toast.LENGTH_SHORT).show();

		FileOutputStream fOut = null;
		try {
			long timestamp = System.currentTimeMillis();
			File ezpsaImageFile = new File(schoolDirectory,
					getString(R.string.album_name) + timestamp + ".png");

			fOut = new FileOutputStream(ezpsaImageFile);

			int quality = 40;
			int increament = 10;
			int maxFileSize = 100 * 1024;
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fOut);

			while (ezpsaImageFile.length() > maxFileSize) {
				quality += increament;
				bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fOut);
			}

			// Log.e("Compressed file size:", file.length() + "");

			// Toast.makeText(AddAdvertiseActivity.this, file.length()+"",
			// Toast.LENGTH_SHORT).show();
			fOut.flush();
			fOut.close();
			// b.recycle();
			bitmap.recycle();
			mCurrentPhotoPath = ezpsaImageFile.getPath();

		} catch (Exception e) { // TODO
			e.printStackTrace();
		}

	}

	private void dispatchCropIntent(Uri uriParam) {

		uri = uriParam;
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");

		List<ResolveInfo> list = getActivity().getPackageManager()
				.queryIntentActivities(intent, 0);

		int size = list.size();
		if (size == 0) {
			// Toast.makeText(this, "Can not find image crop app",
			// Toast.LENGTH_SHORT).show();
			handleBigCameraPhoto(true, uriParam);
			return;
		} else {
			intent.setData(uri);
			int height = AppUtility.getImageViewerImageHeight(getActivity());
			intent.putExtra("outputX", 400);
			intent.putExtra("outputY", 200);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", false);
			intent.putExtra("noFaceDetection", true);

			File f = null;
			try {
				f = setUpPhotoFile();
				mCurrentPhotoPath = f.getAbsolutePath();

			} catch (IOException e) {
				e.printStackTrace();
				f = null;
				mCurrentPhotoPath = null;
			}
			intent.putExtra("output", Uri.fromFile(f));

			if (size == 1) {
				Intent i = new Intent(intent);
				ResolveInfo res = list.get(0);

				i.setComponent(new ComponentName(res.activityInfo.packageName,
						res.activityInfo.name));

				startActivityForResult(i, REQUEST_CODE_CROP);
			} else {
				for (ResolveInfo res : list) {
					final CropOption co = new CropOption();

					co.title = getActivity().getPackageManager()
							.getApplicationLabel(
									res.activityInfo.applicationInfo);
					co.icon = getActivity().getPackageManager()
							.getApplicationIcon(
									res.activityInfo.applicationInfo);
					co.appIntent = new Intent(intent);

					co.appIntent
							.setComponent(new ComponentName(
									res.activityInfo.packageName,
									res.activityInfo.name));

					cropOptions.add(co);
				}

				CropOptionAdapter adapter = new CropOptionAdapter(getActivity()
						.getApplicationContext(), cropOptions);

				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle("Choose Crop App");
				builder.setAdapter(adapter,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								startActivityForResult(
										cropOptions.get(item).appIntent,
										REQUEST_CODE_CROP);
							}
						});

				builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {

						if (uri != null) {
							getActivity().getContentResolver().delete(uri,
									null, null);
							uri = null;
						}
					}
				});

				AlertDialog alert = builder.create();

				alert.show();
			}
		}
	}
}
