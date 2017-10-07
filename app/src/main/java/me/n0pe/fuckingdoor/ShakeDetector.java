
// based on squareup's code

package me.n0pe.fuckingdoor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;

public class ShakeDetector implements SensorEventListener {
	
	private final Listener listener;
	int threshold, window;
	ArrayList list = new ArrayList();
	private SensorManager sensorManager;
	private Sensor accelerometer;
	
	public ShakeDetector(Listener listener, int threshold, int window) {
		this.threshold = threshold;
		this.listener = listener;
		this.window = window;
		return;
	}
	
	/**
	 * Starts listening for shakes on devices with appropriate hardware.
	 *
	 * @return true if the device supports shake detection.
	 */
	public boolean start(SensorManager sensorManager) {
		// Already started?
		if (accelerometer != null) {
			return true;
		}
		
		accelerometer = sensorManager.getDefaultSensor(
			Sensor.TYPE_ACCELEROMETER);
		
		// If this phone has an accelerometer, listen to it.
		if (accelerometer != null) {
			this.sensorManager = sensorManager;
			sensorManager.registerListener(this, accelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
		}
		return accelerometer != null;
	}
	
	/**
	 * Stops listening.  Safe to call when already stopped.  Ignored on devices
	 * without appropriate hardware.
	 */
	public void stop() {
		if (accelerometer != null) {
			sensorManager.unregisterListener(this, accelerometer);
			sensorManager = null;
			accelerometer = null;
		}
	}
	
	double average(List<Double> list) {
		double res = 0;
		for (Double d : list) {
			res += d / list.size();
			continue;
		}
		return res;
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		double avg = average(list);
		double val = (
			+Math.pow(event.values[0], 2)
				+ Math.pow(event.values[1], 2)
				+ Math.pow(event.values[2], 2)
		) * 100;
		if (Main.seekBar != null) {
			Main.seekBar.setSecondaryProgress((int) Math.abs(val - avg));
			//	Log.d("onSensorChanged", "" + Math.abs(val-avg) + " " + val + " " + avg);
		}
		if (Math.abs(val - avg) > threshold) {
			listener.hearShake();
		}
		list.add(val);
		while (list.size() > window) {
			list.remove(0);
			continue;
		}
		return;
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		return;
	}
	
	public interface Listener {
		void hearShake();
	}
}
