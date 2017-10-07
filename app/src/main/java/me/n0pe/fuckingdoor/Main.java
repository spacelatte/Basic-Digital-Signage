package me.n0pe.fuckingdoor;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Locale;
import java.util.Set;

public class Main extends Activity {
	static SeekBar seekBar = null;
	String TAG = getClass().getSimpleName();
	TextToSpeech tts = null;
	SensorManager manager = null;
	ShakeDetector detector = null;
	long last = new Date().getTime();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//	final String lang = Locale.getDefault().getDisplayLanguage();
		TextView view = new TextView(this);
		view.setTextAppearance(R.style.TextAppearance_AppCompat_Large);
		view.setKeepScreenOn(true);
		view.setAllCaps(true);
		view.setTextSize(72.0F);
		view.setText(R.string.main);
		view.setGravity(Gravity.CENTER);
		view.setLayoutParams(new RelativeLayout.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT
		));
		view.setTextColor(Color.WHITE);
		view.setBackgroundColor(Color.DKGRAY);
		//	RelativeLayout layout = new RelativeLayout(this);
		//	layout.setGravity(Gravity.CENTER);
		//	layout.addView(view);
		setContentView(view);
		tts = new TextToSpeech(Main.this, new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int i) {
				Log.d(TAG, "onInit: " + i);
				return;
			}
		});
		manager = (SensorManager) getSystemService(SENSOR_SERVICE);
		detector = new ShakeDetector(new ShakeDetector.Listener() {
			@Override
			public void hearShake() {
				warn();
				return;
			}
		}, getPreferences(0).getInt("t", 250), 50);
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				detector.start(manager);
				return;
			}
		});
		//	startLockTask();
		return;
	}
	
	void warn() {
		if (new Date().getTime() - last < 2222) {
			return;
		}
		last = new Date().getTime();
		Set<Voice> set = tts.getVoices();
		if (set != null && set.size() > 1) {
			Voice[] voices = new Voice[set.size()];
			set.toArray(voices);
			Voice voice = null;
			while (!(voice = voices[(int) (Math.random() * voices.length)]).getLocale().equals(Locale.getDefault())) ;
			tts.setVoice(voice);
		}
		tts.speak(
			getString(R.string.main).replaceAll("\\s+", " "),
			TextToSpeech.QUEUE_FLUSH,
			Bundle.EMPTY,
			TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
		//	try { Thread.sleep(16); } catch (Exception e) { ; }
		return;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			warn();
		}
		return super.onTouchEvent(event);
	}
	
	@Override
	public void onBackPressed() {
		seekBar = new SeekBar(this);
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				if (b) {
					Log.d(TAG, "onProgressChanged: " + b + " " + i);
					detector.threshold = i;
					getPreferences(0)
						.edit()
						.putInt("t", i)
						.apply();
				}
				return;
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				return;
			}
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Toast
					.makeText(Main.this, "" + detector.threshold, Toast.LENGTH_SHORT)
					.show();
				return;
			}
		});
		seekBar.setMax(1000);
		seekBar.setProgress(detector.threshold);
		new AlertDialog.Builder(this)
			.setView(seekBar)
			.show();
		return;
		//super.onBackPressed();
	}
}
