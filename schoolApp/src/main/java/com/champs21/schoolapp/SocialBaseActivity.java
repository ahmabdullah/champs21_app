package com.champs21.schoolapp;
import roboguice.activity.RoboFragmentActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.champs21.freeversion.GoodReadActivity;
import com.champs21.schoolapp.model.FreeVersionPost;
import com.champs21.schoolapp.model.User;
import com.champs21.schoolapp.model.UserAuthListener;
import com.champs21.schoolapp.model.UserWrapper;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.social.PlusClientFragment;
import com.champs21.schoolapp.social.PlusClientFragment.OnSignInListener;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.AppUtility;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.RequestKeyHelper;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.utils.UserHelper;
import com.champs21.schoolapp.utils.UserHelper.UserAccessType;
import com.champs21.schoolapp.viewhelpers.PopupDialogGoodReadDelete;
import com.champs21.schoolapp.viewhelpers.PopupDialogShare;
import com.champs21.schoolapp.viewhelpers.PopupDialogShare.IShareButtonclick;
import com.champs21.schoolapp.viewhelpers.UIHelper;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Feed;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.entities.Profile.Properties;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;
import com.sromku.simple.fb.listeners.OnPublishListener;


public abstract class SocialBaseActivity extends RoboFragmentActivity implements
		OnSignInListener, View.OnClickListener,UserAuthListener,IShareButtonclick{

	

	@Override
	public void onSocialButtonClick() {
		sharePostAll(post);
	}

	@Override
	public void onMyschoolButtonClick() {
		sharePostToMySchool();
	}

	private void sharePostToMySchool()
	{
		RequestParams params=new RequestParams();
		params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());
		params.put("id", post.getId());
		AppRestClient.post(URLHelper.URL_FREE_VERSION_SHARE_TO_MY_SCHOOL, params, postToSchoolHandler);
		
	}
	public void parentSetResult(){
		setResult(RESULT_OK,new Intent());
	}
	
	private AsyncHttpResponseHandler postToSchoolHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			super.onFailure(arg0, arg1);
			Log.e("Response****", arg1);
			if(uiHelper.isDialogActive())
			{
				uiHelper.dismissLoadingDialog();
			}

		}

		@Override
		public void onStart() {
			super.onStart();
			uiHelper.showLoadingDialog("Posting to my school...");
		}

		@Override
		public void onSuccess(int arg0, String responseString) {
			super.onSuccess(arg0, responseString);

			Log.e("Response****", responseString);

			if(uiHelper.isDialogActive())
			{
				uiHelper.dismissLoadingDialog();
			}
			Wrapper wrapper = GsonParser.getInstance().parseServerResponse(
					responseString);
			if (wrapper.getStatus().getCode() == 200) {
				uiHelper.showMessage("Post is successfully sent.");
			} else {
				
			}

		}

	};
	
	private SimpleFacebook mSimpleFacebook;
	private FreeVersionPost post;
	private boolean shareFlag=false;
	
	private static final String FACEOOK_TAG="Facebook";
	
	/**
	 * Code used to identify the login request to the {@link PlusClientFragment}
	 * .
	 */
	public static final int REQUEST_CODE_PLUS_CLIENT_FRAGMENT = 0;

	/** Delegate responsible for handling Google sign-in. */
	protected PlusClientFragment mPlus;

	/** Profile as returned by the PhotoHunt service. */
	protected User mPhotoUser;

	

	/** Image cache which manages asynchronous loading and caching of images. */
	protected ImageLoader mImageLoader;

	/** Person as returned by Google Play Services. */
	protected Person mPlusPerson;

	

	/**
	 * Stores the pending click listener which should be executed if the user
	 * successfully authenticates. {@link #mPendingClick} is populated if a user
	 * performs an action which requires authentication but has not yet
	 * successfully authenticated.
	 */
	protected View.OnClickListener mPendingClick;

	/**
	 * Stores the {@link View} which corresponds to the pending click listener
	 * and is supplied as an argument when the action is eventually resolved.
	 */
	protected View mPendingView;

	/**
	 * Stores the @link com.google.android.gms.common.SignInButton} for use in
	 * the action bar.
	 */
	protected SignInButton mSignInButton;
	
	public UIHelper uiHelper;
	public UserHelper userHelper;
	
	private boolean shouldRegisterToServer=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create the PlusClientFragment which will initiate authentication if
		// required.
		// AuthUtil.SCOPES describe the permissions that we are requesting of
		// the user to access
		// their information and write to their moments vault.
		// AuthUtil.VISIBLE_ACTIVITIES describe the types of moment which we can
		// read from or write
		// to the user's vault.
		mPlus = PlusClientFragment.getPlusClientFragment(this, AppConstant.SCOPES,
				AppConstant.VISIBLE_ACTIVITIES);
		uiHelper=new UIHelper(SocialBaseActivity.this);
		userHelper=new UserHelper(this, SocialBaseActivity.this);
		/*mSignInButton = (SignInButton) getLayoutInflater().inflate(
				R.layout.sign_in_button, null);
		mSignInButton.setOnClickListener(this);*/
	}

	@Override
	public void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		

		// Delegate onActivityResult handling to PlusClientFragment to resolve
		// authentication
		// failures, eg. if the user must select an account or grant our
		// application permission to
		// access the information we have requested in AuthUtil.SCOPES and
		// AuthUtil.VISIBLE_ACTIVITIES
		boolean isProcessed = false;
		if (requestCode != 110 && requestCode != 111 && requestCode != 112) {
			isProcessed=true;
			mPlus.handleOnActivityResult(requestCode, responseCode, intent);
			mSimpleFacebook.onActivityResult(this, requestCode, responseCode,
					intent);
			
		}
		
		if(!isProcessed)
		{
			///SchoolAllFragment
			super.onActivityResult(requestCode, responseCode, intent);
		}
	}
	
	
	public void sharePost1(FreeVersionPost post)
	{
		this.post=post;
		doFaceBookLogin(true);
		//Log.e("test", post.getTitle());
		
	}
	
	public void showSharePicker(FreeVersionPost post)
	{
		this.post=post;
		PopupDialogShare picker = PopupDialogShare.newInstance(0);
		picker.setData(this);
		picker.show(getSupportFragmentManager(), null);
	}
	
	public void sharePostUniversal(FreeVersionPost post){
		if(post.getCan_share()==1){
			showSharePicker(post);
		}else{
			sharePostAll(post);
		}
	}
	
	public void sharePostAll(FreeVersionPost postt){
		this.post = postt;
		Intent intent=new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

		// Add data to the intent, the receiving app will decide what to do with it.
		intent.putExtra(Intent.EXTRA_SUBJECT, post.getTitle());
		intent.putExtra(Intent.EXTRA_TEXT, post.getShareLink().getLink());
		startActivity(Intent.createChooser(intent, "CHAMPS21"));

	}
	
	public void publishFeed()
	{	
		String imageUrl = "https://raw.github.com/sromku/android-simple-facebook/master/Refs/android_facebook_sdk_logo.png";
		if(post.getImages().size()!=0){
			imageUrl = post.getImages().get(0);
		}
		Feed feed = new Feed.Builder()
	    //.setMessage("Clone it out...")
	    .setName(post.getTitle().toString())
	    .setCaption("www.champs21.com")
	    .setDescription(post.getSummary().toString())
	    .setPicture(imageUrl)
	    .setLink(post.getShareLink().getLink())
	    .build();
		
		mSimpleFacebook.publish(feed, true, onPublishListener);
		
	}

	OnPublishListener onPublishListener = new OnPublishListener() {
	    @Override
	    public void onComplete(String postId) {
	        Log.i("Share", "Published successfully. The new post id = " + postId);
	        uiHelper.showMessage("Your post has been published successfully");
	    }
	    public void onException(Throwable throwable) {
	    	uiHelper.showMessage("Error! Please try again later.");
	    };

	    /* 
	     * You can override other methods here: 
	     * onThinking(), onFail(String reason), onException(Throwable throwable)
	     */
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		mSimpleFacebook = SimpleFacebook.getInstance(this);
		
	}

	public void doAppLogin(User user)
	{
		userHelper.doLogIn(user);
	}
	
	public void doAppRegister(User user)
	{
		//userHelper.doRegister(user);
	}
	
	public void doFaceBookLogin(boolean flag)
	{
		Log.e("Login", "doFaceBookLogin");
		shareFlag=flag;
		mSimpleFacebook.login(mOnFaceBookLoginListener);
	}
	
	
	private OnLoginListener mOnFaceBookLoginListener = new OnLoginListener() {

		@Override
		public void onFail(String reason) {
			Log.w(FACEOOK_TAG, "Failed to login");
			uiHelper.showMessage(reason);
		}

		@Override
		public void onException(Throwable throwable) {
			Log.e(FACEOOK_TAG, "Bad thing happened", throwable);
			uiHelper.showMessage(throwable.getMessage());
		}

		@Override
		public void onThinking() {
			// show progress bar or something to the user while login is
			// happening
			/*if(!uiHelper.isDialogActive())
			{
				uiHelper.showLoadingDialog("Logging in...");
			}*/
			
		}

		@Override
		public void onLogin() {
			// change the state of the button or do whatever you want
			if(uiHelper.isDialogActive())
				uiHelper.dismissLoadingDialog();
			Log.e(FACEOOK_TAG, "LoggedIn");
			Profile.Properties properties = new Profile.Properties.Builder()
		    .add(Properties.ID)
		    .add(Properties.FIRST_NAME)
		    .add(Properties.LAST_NAME)
		    .add(Properties.MIDDLE_NAME)
		    .add(Properties.PICTURE)
		    .add(Properties.BIRTHDAY)
		    .add(Properties.EMAIL)
		    .add(Properties.GENDER)
		    .add(Properties.PICTURE)
		    .build();
			if(!shareFlag)
				mSimpleFacebook.getProfile(properties,onFacebookProfileListener);
			else
				publishFeed();
		}

		@Override
		public void onNotAcceptingPermissions(Permission.Type type) {
			
		}
	};
	
	
	
	// listener for profile request
			final OnProfileListener onFacebookProfileListener = new OnProfileListener() {

				@Override
				public void onFail(String reason) {
					
					// insure that you are logged in before getting the profile
					uiHelper.dismissLoadingDialog();
				}

				@Override
				public void onException(Throwable throwable) {
					uiHelper.dismissLoadingDialog();
					Log.e(FACEOOK_TAG, "Bad thing happened", throwable);
				}

				@Override
				public void onThinking() {
					if(!uiHelper.isDialogActive())
						uiHelper.showLoadingDialog("Fetching ur profile...");
				}

				@Override
				public void onComplete(Profile profile) {
					
					Log.e(FACEOOK_TAG, "My profile id = " + profile.getId() + "Email:"+ profile.getEmail() + "First Name:" + profile.getFirstName() + "Sex:"+ profile.getGender() + "BirthDay:"+ profile.getBirthday() + "Contry:"+profile.getHometown());
					
					if(uiHelper.isDialogActive())
						uiHelper.dismissLoadingDialog();
					User user=new User();
					user.setEmail(profile.getEmail());
					user.setFbId(profile.getId());
					user.setFirstName(profile.getFirstName());
					user.setLastName(profile.getLastName());
					user.setMiddleName(profile.getMiddleName());
					user.setGender(profile.getGender());
					if(!TextUtils.isEmpty(profile.getBirthday()))
						user.setDob(AppUtility.getDateString(profile.getBirthday(), AppUtility.DATE_FORMAT_SERVER, AppUtility.DATE_FORMAT_FACEBOOK));
					user.setProfilePicsUrl(profile.getPicture());
					
					userHelper.fbLogIn(user);
				}
			};
	
			
	
	@Override
	public void onStop() {
		super.onStop();

		// Reset any asynchronous tasks we have running.
		resetTaskState();
	}

	/*private void createUserObjectToRegister(LogInTypeEnum type,User user)
	{
		switch (type) {
		case GOOGLE_PLUS:
			user.setType(LogInTypeEnum.GOOGLE_PLUS.value);
			break;
		case FACEBOOK:
			user.setType(LogInTypeEnum.FACEBOOK.value);
			break;
		default:
			break;
		}
		userHelper.doRegister(user);
	}*/
	
	
	
	
	
	
	/**
	 * Invoked when the {@link PlusClientFragment} delegate has successfully
	 * authenticated the user.
	 * 
	 * @param plusClient
	 *            The connected PlusClient which gives us access to the Google+
	 *            APIs.
	 */
	
	@Override
	public void onSignedIn(PlusClient plusClient) {
		if (plusClient.isConnected()) {
			mPlusPerson = plusClient.getCurrentPerson();
			//Log.e("PERSON:", "Account: "+plusClient.getAccountName()+ " Gender: "+mPlusPerson.getGender()+ " DisplayName: "+mPlusPerson.getDisplayName()+ " NickName: "+mPlusPerson.getNickname()+ " URL: "+mPlusPerson.getUrl());
			
			
			
			// Retrieve the account name of the user which allows us to retrieve
			// the OAuth access
			// token that we securely pass over to the PhotoHunt service to
			// identify and
			// authenticate our user there.
			if(shouldRegisterToServer){
				shouldRegisterToServer=false;
				final String email = plusClient.getAccountName();
				User user = new User();
				user.setEmail(email);
				user.setgPlusId(mPlusPerson.getId());
				user.setFirstName(mPlusPerson.getDisplayName());
				user.setDob(mPlusPerson.getBirthday());
				user.setProfilePicsUrl(mPlusPerson.getImage().getUrl());
				
				Log.e("Profile Pic:", mPlusPerson.getImage().getUrl());
				userHelper.gPlusLogIn(user);
			}
		}
	}

	
	
	
	/**
	 * Invoked when the {@link PlusClientFragment} delegate has failed to
	 * authenticate the user. Failure to authenticate will often mean that the
	 * user has not yet chosen to sign in.
	 * 
	 * The default implementation resets the PhotoHunt profile to null.
	 */
	@Override
	public void onSignInFailed() {
		setAuthenticatedProfile(null);
		update();
	}

	/**
	 * Invoked when the PhotoHunt profile has been successfully retrieved for an
	 * authenticated user.
	 * 
	 * @param profile
	 */
	public void setAuthenticatedProfile(User profile) {
		mPhotoUser = profile;
	}

	/**
	 * Update the user interface to reflect the current application state. This
	 * function is called whenever this Activity's state has been modified.
	 * 
	 * {@link BaseActivity} calls this method when user authentication succeeds
	 * or fails.
	 */
	public void update() {
		supportInvalidateOptionsMenu();
	}

	/**
	 * Execute actions which are pending; eg. because they were waiting for the
	 * user to authenticate.
	 */
	protected void executePendingActions() {
		// On successful authentication we resolve any pending actions
		if (mPendingClick != null) {
			mPendingClick.onClick(mPendingView);
			mPendingClick = null;
			mPendingView = null;
		}
	}

	

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.google_login_btn) {
			mPlus.signIn(REQUEST_CODE_PLUS_CLIENT_FRAGMENT);
		}
	}

	public void doGooglePlusLogin()
	{
		shouldRegisterToServer=true;
		mPlus.signIn(REQUEST_CODE_PLUS_CLIENT_FRAGMENT);
		
	}
	
	
	/**
	 * Provides a guard to ensure that a user is authenticated.
	 */
	protected boolean requireSignIn() {
		if (!mPlus.isAuthenticated()) {
			mPlus.signIn(REQUEST_CODE_PLUS_CLIENT_FRAGMENT);
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @return true if the user is currently authenticated through Google
	 *         sign-in 
	 */
	public boolean isAuthenticated() {
		return mPlus.isAuthenticated() && mPhotoUser != null;
	}

	/**
	 * @return true if the user is currently being authenticated through Google
	 *         sign-in or if the the user's PhotoHunt profile is being fetched.
	 */
	public boolean isAuthenticating() {
		return mPlus.isConnecting() || mPlus.isAuthenticated()
				&& mPhotoUser == null;
	}

	/**
	 * Resets the state of asynchronous tasks used by this activity.
	 */
	protected void resetTaskState() {
		
	}
	


}
