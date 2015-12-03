package com.classtune.spellingbee;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.widget.WebDialog;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.entities.Feed;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnPublishListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

public class AndroidFacebookConnectActivity extends Activity implements Session.StatusCallback {


	// Your Facebook APP ID
	private static String APP_ID = "850059515022967"; // Replace with your App
														// ID

	// Instance of Facebook Class
	private Facebook facebook = new Facebook(APP_ID);
	private AsyncFacebookRunner mAsyncRunner;
	String FILENAME = "AndroidSSO_data";
	private SharedPreferences mPrefs;

	// Buttons
	Button btnFbLogin;
	Button btnFbGetProfile;
	Button btnPostToWall;
	Button btnShowAccessTokens;

	private static final String TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	private static final String KEY = "facebook-credentials";
	private static final String[] PERMISSIONS = new String[] { "publish_actions" };


	private String currentScore = "";


	private SimpleFacebook mSimpleFacebook;



	@Override
	public void onResume() {
		super.onResume();
		mSimpleFacebook = SimpleFacebook.getInstance(this);
	}



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.main);

		if(getIntent().getExtras() != null)
		{
			currentScore = getIntent().getExtras().getString(SpellingbeeConstants.KEY_SCORE_FOR_FB_SHARE);
		}

		mSimpleFacebook = SimpleFacebook.getInstance();
		//mSimpleFacebook.login(onLoginListener);

		/*btnFbLogin = (Button) findViewById(R.id.btn_fblogin);
		btnFbGetProfile = (Button) findViewById(R.id.btn_get_profile);
		btnPostToWall = (Button) findViewById(R.id.btn_fb_post_to_wall);
		btnShowAccessTokens = (Button) findViewById(R.id.btn_show_access_tokens);
		

		*//**
		 * Login button Click event
		 * *//*
		btnFbLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("Image Button", "button Clicked");
				// loginToFacebook();

				loginAndPostToWall();
				
			}
		});

		*//**
		 * Getting facebook Profile info
		 * *//*
		btnFbGetProfile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				getProfileInformation();
			}
		});

		*//**
		 * Posting to Facebook Wall
		 * *//*
		btnPostToWall.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				postToWall();
			}
		});

		*//**
		 * Showing Access Tokens
		 * *//*
		btnShowAccessTokens.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showAccessTokens();
			}
		});*/
		Session.openActiveSession(AndroidFacebookConnectActivity.this, true, AndroidFacebookConnectActivity.this);
		
		//loginAndPostToWall();

		/*Session session = Session.getActiveSession();

		if(session==null){
			// try to restore from cache
			session = Session.openActiveSessionFromCache(this);
		}

		if(session!=null && session.isOpened()){
			publishFeedDialog();
		}
		else{
			facebook.authorize(this, PERMISSIONS,  Facebook.FORCE_DIALOG_AUTH,
					new LoginDialogListener());
		}

		publishFeedDialog();*/
	}

	private void publishFeedDialog() {

		//I have scored " + score + " in Spell Bangladesh at www.champs21.com. Beat me if you can!


		String str = "I have scored " + currentScore + " in Spell Champs. Play and share yours!!";

		Bundle params = new Bundle();

		params.putString("name", "Meet the new spelling genius!");
		params.putString("caption", "Spelling Bee Season 4");
		params.putString("description", str);
		//params.putString("link", "https://play.google.com/store/apps/details?id=com.champs21.schoolapp");
		params.putString("link", "https://play.google.com/store/apps/developer?id=Team+Creative");

		/*params.putString("description", "I scored " + currentScore + " in Spelling Bee and climbed the ranks! Divisionals here I come! The Bee is Buzzing!!");*/

		params.putString("picture", "http://www.champs21.com/swf/spellingbee_2015/icon_250.png");
		//params.putString("picture", "http://www.champs21.com/swf/spellingbee_2015/sbee.png");



		WebDialog feedDialog = (
				new WebDialog.FeedDialogBuilder(this,
						/*Session.getActiveSession(),*/
						Session.getActiveSession(),


						params))
				.setOnCompleteListener(new WebDialog.OnCompleteListener() {
					@Override
					public void onComplete(Bundle values, FacebookException error) {

						//Toast.makeText(AndroidFacebookConnectActivity.this, AppMessages.ANDROIDFACEBOOKCONNECTACTIVITY_POSTED_TO_FB, Toast.LENGTH_SHORT).show();
						if (error == null) {
							// When the story is posted, echo the success
							// and the post Id.
							final String postId = values.getString("post_id");
							if (postId != null) {
								//Toast.makeText(PhotoViewer.this,"Posted story, id: "+postId,Toast.LENGTH_SHORT).show();
								Toast.makeText(getApplicationContext(), "Publish Successfully!", Toast.LENGTH_SHORT).show();
							} else {
								// User clicked the Cancel button
								Toast.makeText(getApplicationContext(), "Publish cancelled", Toast.LENGTH_SHORT).show();
							}
						} else if (error instanceof FacebookOperationCanceledException) {
							// User clicked the "x" button
							Toast.makeText(getApplicationContext(), "Publish cancelled", Toast.LENGTH_SHORT).show();
						} else {
							// Generic, ex: network error
							Toast.makeText(getApplicationContext(),"Error posting story",Toast.LENGTH_SHORT).show();
						}

						AndroidFacebookConnectActivity.this.finish();

					}
				})
				.build();


		feedDialog.show();
	}

	/**
	 * Function to login into facebook
	 * */

	public void loginAndPostToWall() {
		facebook.authorize(this, PERMISSIONS,  Facebook.FORCE_DIALOG_AUTH,
				new LoginDialogListener());

		//mSimpleFacebook.login(onLoginListener);
	}

	@Override
	public void call(Session session, SessionState state, Exception exception) {

		if (session.isOpened()) {
			publishFeedDialog();
		}

	}

	class LoginDialogListener implements DialogListener {

		public void onComplete(Bundle values) {
			saveCredentials(facebook);

			// postToWall(messageToPost);
			/*mAsyncRunner = new AsyncFacebookRunner(facebook);

			final Bundle params = new Bundle();

			params.putString("message", "I scored " + currentScore + " in Spelling Bee and climbed the ranks! Divisionals here I come! The Bee is Buzzing!!");
			mAsyncRunner.request("me/feed", params, "POST", new WallPostListener(), null);*/




			//publishFeedDialog();



		}

		public void onFacebookError(FacebookError error) {
			showToast(AppMessages.ANDROIDFACEBOOKCONNECTACTIVITY_AUTH_FAILED_TOAST);
			finish();
		}

		public void onError(DialogError error) {
			showToast(AppMessages.ANDROIDFACEBOOKCONNECTACTIVITY_AUTH_FAILED_TOAST);
			finish();
		}

		public void onCancel() {
			showToast(AppMessages.ANDROIDFACEBOOKCONNECTACTIVITY_AUTH_CANCELED_TOAST);
			finish();
		}
	}


	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}*/


	OnLoginListener onLoginListener = new OnLoginListener() {
		@Override
		public void onLogin() {

			Permission[] permissions = new Permission[] {
					Permission.PUBLISH_ACTION
			};

			SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
					.setAppId(APP_ID)
					.setNamespace("champs_schoolapp")
					.setPermissions(permissions)
					.setDefaultAudience(SessionDefaultAudience.EVERYONE)
					.setAskForAllPermissionsAtOnce(false)
					.build();

			SimpleFacebook.setConfiguration(configuration);



			Feed feed = new Feed.Builder()
					.setMessage("Clone it out...")
					.setName("Simple Facebook for Android")
					.setCaption("Code less, do the same.")
					.setDescription("The Simple Facebook library project makes the life much easier by coding less code for being able to login, publish feeds and open graph stories, invite friends and more.")
					.setPicture("https://raw.github.com/sromku/android-simple-facebook/master/Refs/android_facebook_sdk_logo.png")
					.setLink("https://github.com/sromku/android-simple-facebook")
					.build();

			mSimpleFacebook.getInstance().publish(feed, onPublishListener);


		}

		@Override
		public void onNotAcceptingPermissions(Permission.Type type) {

		}

		@Override
		public void onThinking() {

		}

		@Override
		public void onException(Throwable throwable) {

		}

		@Override
		public void onFail(String reason) {

		}
	};


	OnPublishListener onPublishListener = new OnPublishListener() {
		@Override
		public void onComplete(String postId) {
			Log.e("FB_OVI", "Published successfully. The new post id = " + postId);
		}

		@Override
		public void onException(Throwable throwable) {
			super.onException(throwable);
		}

		@Override
		public void onFail(String reason) {
			super.onFail(reason);
			Log.e("FB_OVI", "reason: "+reason);
		}

		/*
     * You can override other methods here:
     * onThinking(), onFail(String reason), onException(Throwable throwable)
     */
	};

	private Handler mRunOnUi = new Handler();
	private final class WallPostListener implements RequestListener {

		@Override
		public void onComplete(String response, Object state) {
			// TODO Auto-generated method stub
			mRunOnUi.post(new Runnable() {
        		@Override
        		public void run() {
        			
        			
        			Toast.makeText(AndroidFacebookConnectActivity.this, AppMessages.ANDROIDFACEBOOKCONNECTACTIVITY_POSTED_TO_FB, Toast.LENGTH_SHORT).show();
        			
        			logoutFromFacebook();
        			AndroidFacebookConnectActivity.this.finish();
        			
        			
        		}
        	});
		}

		@Override
		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub
			
		}
        
    }
	
	
	public boolean saveCredentials(Facebook facebook) {
		Editor editor = getApplicationContext().getSharedPreferences(KEY,
				Context.MODE_PRIVATE).edit();
		editor.putString(TOKEN, facebook.getAccessToken());
		editor.putLong(EXPIRES, facebook.getAccessExpires());
		return editor.commit();
	}

	public boolean restoreCredentials(Facebook facebook) {
		SharedPreferences sharedPreferences = getApplicationContext()
				.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
		facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
		return facebook.isSessionValid();
	}

	private void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
				.show();
	}

	/*public void loginToFacebook() {

		mPrefs = getPreferences(MODE_PRIVATE);
		String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);

		if (access_token != null) {
			facebook.setAccessToken(access_token);

			btnFbLogin.setVisibility(View.INVISIBLE);

			// Making get profile button visible
			btnFbGetProfile.setVisibility(View.VISIBLE);

			// Making post to wall visible
			btnPostToWall.setVisibility(View.VISIBLE);

			// Making show access tokens button visible
			btnShowAccessTokens.setVisibility(View.VISIBLE);

			Log.d("FB Sessions", "" + facebook.isSessionValid());
		}

		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}

		if (!facebook.isSessionValid()) {
			facebook.authorize(this,
					new String[] { "email", "publish_stream" },
					new DialogListener() {

						@Override
						public void onCancel() {
							// Function to handle cancel event
						}

						@Override
						public void onComplete(Bundle values) {
							// Function to handle complete event
							// Edit Preferences and update facebook acess_token
							SharedPreferences.Editor editor = mPrefs.edit();
							editor.putString("access_token",
									facebook.getAccessToken());
							editor.putLong("access_expires",
									facebook.getAccessExpires());
							editor.commit();

							// Making Login button invisible
							btnFbLogin.setVisibility(View.INVISIBLE);

							// Making logout Button visible
							btnFbGetProfile.setVisibility(View.VISIBLE);

							// Making post to wall visible
							btnPostToWall.setVisibility(View.VISIBLE);

							// Making show access tokens button visible
							btnShowAccessTokens.setVisibility(View.VISIBLE);
						}

						@Override
						public void onError(DialogError error) {
							// Function to handle error

						}

						@Override
						public void onFacebookError(FacebookError fberror) {
							// Function to handle Facebook errors

						}

					});
		}
	}*/

	/*@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		facebook.authorizeCallback(requestCode, resultCode, data);
	}*/


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}



	/**
	 * Get Profile information by making request to Facebook Graph API
	 * */
	/*public void getProfileInformation() {
		mAsyncRunner.request("me", new RequestListener() {
			@Override
			public void onComplete(String response, Object state) {
				Log.d("Profile", response);
				String json = response;
				try {
					// Facebook Profile JSON data
					JSONObject profile = new JSONObject(json);

					// getting name of the user
					final String name = profile.getString("name");

					// getting email of the user
					final String email = profile.getString("email");

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(getApplicationContext(),
									"Name: " + name + "\nEmail: " + email,
									Toast.LENGTH_LONG).show();
						}

					});

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onIOException(IOException e, Object state) {
			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
			}

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
			}
		});
	}*/

	/**
	 * Function to post to facebook wall
	 * */
	/*public void postToWall() {
		// post on user's wall.
		final Bundle params = new Bundle();
		params.putString("message", "message to show on the user's wall");
		try {
			this.facebook.request("me/feed", params, "POST");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		facebook.dialog(this, "feed", new DialogListener() {

			@Override
			public void onFacebookError(FacebookError e) {
			}

			@Override
			public void onError(DialogError e) {
			}

			@Override
			public void onComplete(Bundle values) {
			}

			@Override
			public void onCancel() {
			}
		});

	}*/

	/**
	 * Function to show Access Tokens
	 * */
	public void showAccessTokens() {
		String access_token = facebook.getAccessToken();

		Toast.makeText(getApplicationContext(),
				"Access Token: " + access_token, Toast.LENGTH_LONG).show();
	}

	/**
	 * Function to Logout user from Facebook
	 * */
	private void logoutFromFacebook() {
		mAsyncRunner.logout(this, new RequestListener() {
			@Override
			public void onComplete(String response, Object state) {
				Log.d("Logout from Facebook", response);
				if (Boolean.parseBoolean(response) == true) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// make Login button visible
							btnFbLogin.setVisibility(View.VISIBLE);

							// making all remaining buttons invisible
							btnFbGetProfile.setVisibility(View.INVISIBLE);
							btnPostToWall.setVisibility(View.INVISIBLE);
							btnShowAccessTokens.setVisibility(View.INVISIBLE);
						}

					});

				}
			}

			@Override
			public void onIOException(IOException e, Object state) {
			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
			}

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
			}
		});
	}

	
	

	   
	
	
	
}