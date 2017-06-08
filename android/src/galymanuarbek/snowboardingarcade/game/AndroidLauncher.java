package galymanuarbek.snowboardingarcade.game;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class AndroidLauncher extends AndroidApplication {
	private InterstitialAd mInterstitial;
	private static final String TAG = "AndroidLauncher";
	protected AdView adView;
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		RelativeLayout layout = new RelativeLayout(this);
		mInterstitial = new InterstitialAd(this);
		mInterstitial.setAdUnitId("ca-app-pub-4662717428050576/6301629844");
		AdRequest request = new AdRequest.Builder()
				//.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				//.addTestDevice("1F715513D36995349A9D85B2008A74F6")
				.build();
		mInterstitial.loadAd(request);
		if (mInterstitial.isLoaded()){
			mInterstitial.show();
		}
		View gameView = initializeForView(new myGame(), config);
		layout.addView(gameView);

		adView = new AdView(this);
		adView.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				int visibility = adView.getVisibility();
				adView.setVisibility(AdView.GONE);
				adView.setVisibility(visibility);
				Log.i(TAG, "Ad loaded...");
			}
		});
		adView.setAdSize(AdSize.SMART_BANNER);
		adView.setAdUnitId("ca-app-pub-4662717428050576/1474477442");

		AdRequest.Builder builder = new AdRequest.Builder();
		//builder.addTestDevice("1F715513D36995349A9D85B2008A74F6");
		RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT
		);

		layout.addView(adView, adParams);
		adView.loadAd(builder.build());

		setContentView(layout);

		config.useAccelerometer = false;
		config.useCompass = false;
		//initialize(new myGame(), config);

	}
}

