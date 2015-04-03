package com.example.mobiquity_challenge;


//import com.dropbox.android.sample.DBRoulette;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	private static final String APP_KEY = "dz8abek08pdgplj";
    private static final String APP_SECRET = "2kesehcfgv9a7tr";

    private static final String ACCOUNT_PREFS_NAME = "prefs";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    private static final boolean USE_OAUTH1 = false;

    static DropboxAPI<AndroidAuthSession> mApi;

    private boolean mLoggedIn;

    // Android widgets
    private Button mSubmit;


    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);
        setContentView(R.layout.activity_main);
        checkAppKeySetup();

        mSubmit = (Button)findViewById(R.id.login);
      
        
//        }

	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);

        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(session);
        return session;
    }
	
	private void loadAuth(AndroidAuthSession session) {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key == null || secret == null || key.length() == 0 || secret.length() == 0) return;

        if (key.equals("oauth2:")) {
            // If the key is set to "oauth2:", then we can assume the token is for OAuth 2.
            session.setOAuth2AccessToken(secret);
        } else {
            // Still support using old OAuth 1 tokens.
            session.setAccessTokenPair(new AccessTokenPair(key, secret));
        }
    }
	
	private void checkAppKeySetup() {
        // Check to make sure that we have a valid app key
        if (APP_KEY.startsWith("CHANGE") ||
                APP_SECRET.startsWith("CHANGE")) {
            showToast("You must apply for an app key and secret from developers.dropbox.com, and add them to the DBRoulette ap before trying it.");
            finish();
            return;
        }

        // Check if the app has set up its manifest properly.
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        String scheme = "db-" + APP_KEY;
        String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
        testIntent.setData(Uri.parse(uri));
        PackageManager pm = getPackageManager();
        if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
            showToast("URL scheme in your app's " +
                    "manifest is not set up correctly. You should have a " +
                    "com.dropbox.client2.android.AuthActivity with the " +
                    "scheme: " + scheme);
            finish();
        }
    }
	
	 private void showToast(String msg) {
	        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
	        error.show();
	    }
	 
	 public void loginHnadler(View v) {
         // This logs you out if you're logged in, or vice versa
         if (mLoggedIn) {
             logOut();
         } else {
             // Start the remote authentication
             if (USE_OAUTH1) {
                 mApi.getSession().startAuthentication(MainActivity.this);
             } else {
                 mApi.getSession().startOAuth2Authentication(MainActivity.this);
             }
         }
	 }
         
         private void logOut() {
             // Remove credentials from the session
             mApi.getSession().unlink();

             // Clear our stored keys
             clearKeys();
             // Change UI state to display logged out version
             setLoggedIn(false);
         }

         
         private void clearKeys() {
             SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
             Editor edit = prefs.edit();
             edit.clear();
             edit.commit();
         }
         
         private void setLoggedIn(boolean loggedIn) {
         	mLoggedIn = loggedIn;
         	if (loggedIn) {
         		mSubmit.setText("Log Out");
         		Button nextActivityButton=(Button)findViewById(R.id.nextActivity);
         		nextActivityButton.setEnabled(true);
         		//nextActivityButton.setVisibility();
         		
                 //mDisplay.setVisibility(View.VISIBLE);
         	} else {
         		mSubmit.setText("Log in");
         		Button nextActivityButton=(Button)findViewById(R.id.nextActivity);
         		nextActivityButton.setEnabled(false);
//                 mDisplay.setVisibility(View.GONE);
//                 mImage.setImageDrawable(null);
         	}
         }
         protected void onResume() {
             super.onResume();
             AndroidAuthSession session = mApi.getSession();

             // The next part must be inserted in the onResume() method of the
             // activity from which session.startAuthentication() was called, so
             // that Dropbox authentication completes properly.
             if (session.authenticationSuccessful()) {
                 try {
                     // Mandatory call to complete the auth
                     session.finishAuthentication();

                     // Store it locally in our app for later use
                     storeAuth(session);
                     setLoggedIn(true);
                 } catch (IllegalStateException e) {
                     showToast("Couldn't authenticate with Dropbox:" + e.getLocalizedMessage());
                     Log.i("mobiquity", "Error authenticating", e);
                 }
             }
         }         
         
         private void storeAuth(AndroidAuthSession session) {
             // Store the OAuth 2 access token, if there is one.
             String oauth2AccessToken = session.getOAuth2AccessToken();
             if (oauth2AccessToken != null) {
                 SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
                 Editor edit = prefs.edit();
                 edit.putString(ACCESS_KEY_NAME, "oauth2:");
                 edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
                 edit.commit();
                 return;
             }
             // Store the OAuth 1 access token, if there is one.  This is only necessary if
             // you're still using OAuth 1.
             AccessTokenPair oauth1AccessToken = session.getAccessTokenPair();
             if (oauth1AccessToken != null) {
                 SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
                 Editor edit = prefs.edit();
                 edit.putString(ACCESS_KEY_NAME, oauth1AccessToken.key);
                 edit.putString(ACCESS_SECRET_NAME, oauth1AccessToken.secret);
                 edit.commit();
                 return;
             }
         }
         
         public void nextActivity(View view)
         {
        	 if(mLoggedIn)
        	 {
        		 Intent nextActivity=new Intent(this,CoreFunctionality.class);
          		startActivity(nextActivity);        		 
        	 }
         }
}
