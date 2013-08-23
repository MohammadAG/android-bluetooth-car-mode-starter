package com.mohammadag.bluetoothcarmodestarter;

import java.util.Set;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.app.UiModeManager;

public class BluetoothReceiver extends BroadcastReceiver {

	private SharedPreferences mPreferences = null;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		mPreferences = context.getSharedPreferences(Common.PREFS_NAME, 0);
		
		Set<String> bluetoothDevicesThatInvokeCarMode = mPreferences.getStringSet(Common.SETTINGS_KEY_BLUETOOTH_ADDRESSES, null);
		
		if (bluetoothDevicesThatInvokeCarMode == null)
			return;
		
		final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		String bluetoothAddress = device.getAddress();
		
		if (!bluetoothDevicesThatInvokeCarMode.contains(bluetoothAddress))
			return;
		
		String action = intent.getAction();
		
		if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
			enableCarModeIfNeeded(context);
		} else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
			disableCarModeIfNeeded(context);
		}
	}
	
	private void enableCarModeIfNeeded(Context context) {
		UiModeManager manager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
		if (manager.getCurrentModeType() == Configuration.UI_MODE_TYPE_CAR) {
			return;
		}
		
		manager.enableCarMode(UiModeManager.ENABLE_CAR_MODE_GO_CAR_HOME);
	}
	
	private void disableCarModeIfNeeded(Context context) {
		UiModeManager manager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
		if (manager.getCurrentModeType() != Configuration.UI_MODE_TYPE_CAR) {
			return;
		}
		
		manager.disableCarMode(UiModeManager.DISABLE_CAR_MODE_GO_HOME);
	}

}
