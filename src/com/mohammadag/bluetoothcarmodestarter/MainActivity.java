package com.mohammadag.bluetoothcarmodestarter;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends Activity {
	
	private BluetoothDeviceArrayAdapter mArrayAdapter;
	private SharedPreferences mPreferences = null;
	private ListView mListView = null;
	private Button mSaveButton = null;
	private BluetoothAdapter mBluetoothAdapter = null;
	
	private int mCurrentAttempt = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getViews();
		
		mPreferences = getSharedPreferences(Common.PREFS_NAME, Context.MODE_PRIVATE);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mArrayAdapter.setCheckboxCheckedForPosition(position, !mArrayAdapter.getIsCheckboxCheckedForPosition(position));
			}
		});
		
		loadListOfDevicesIntoUi();
	}
	
	private void getViews() {
		mSaveButton = (Button) findViewById(R.id.saveButton);
		mListView = (ListView) findViewById(R.id.bluetoothDevicesListView);
	}
	
	private void loadListOfDevicesIntoUi() {
		Set<String> enabledDevices = mPreferences.getStringSet(Common.SETTINGS_KEY_BLUETOOTH_ADDRESSES, null);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (!mBluetoothAdapter.isEnabled()) {
			if (mCurrentAttempt > Common.MAX_RETRIES) {
				rageAboutNoBluetooth();
				return;
			}
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, Common.BLUETOOTH_ENABLE_REQUEST_CODE);
			mCurrentAttempt++;
		} else {
			mArrayAdapter = new BluetoothDeviceArrayAdapter(getApplicationContext(), R.layout.bluetooth_list_view, getBluetoothDevices(), enabledDevices);
			mListView.setAdapter(mArrayAdapter);
			mSaveButton.setEnabled(true);

			mSaveButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					saveDevices();
				}
			});
		}
	}
	
	private void rageAboutNoBluetooth() {
		Toast.makeText(getApplicationContext(), R.string.permission_to_enable_bt_denied, Toast.LENGTH_LONG).show();
		mSaveButton.setText(R.string.restart_activity);
		mSaveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				reloadActivity();
			}
		});
		mSaveButton.setEnabled(true);
	}
	
    private void reloadActivity() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

        overridePendingTransition(0, 0);
        startActivity(intent);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Common.BLUETOOTH_ENABLE_REQUEST_CODE) {
			loadListOfDevicesIntoUi();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void saveDevices() {
        Set<String> bluetoothDevices = mArrayAdapter.getCheckedBluetoothDevices();

        SharedPreferences.Editor editor = mPreferences.edit();
        editor.clear();
        editor.putStringSet(Common.SETTINGS_KEY_BLUETOOTH_ADDRESSES, bluetoothDevices).apply();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    private ArrayList<BluetoothDevice> getBluetoothDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        ArrayList<BluetoothDevice> array = new ArrayList<>();
        for (BluetoothDevice device : pairedDevices) {
            array.add(device);
        }
        return array;
    }
	
	public void showAbout() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this)
		        .setTitle(R.string.about_dialog_title)
		        .setMessage(R.string.about_text);
		
        alertDialog.show();
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                showAbout();
                return true;
            case R.id.menu_donate:
    			Intent intent = new Intent(Intent.ACTION_VIEW, 
    					Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=5CVA3PL4GP6LN"));
    			startActivity(intent);
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
