package com.mohammadag.bluetoothcarmodestarter;

import java.util.ArrayList;
import java.util.Set;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class BluetoothDeviceArrayAdapter extends ArrayAdapter<BluetoothDevice> {

    private ArrayList<BluetoothDevice> mBluetoothDevices;
    private Context mContext;
    private ArrayList<Integer> mSelectedPositions;
    private SparseArray<CheckBox> mCheckboxArray;
	private Set<String> mDevicesInPreferences = null;;

    public BluetoothDeviceArrayAdapter(Context context, int textViewResourceId, ArrayList<BluetoothDevice> items, Set<String> storedDevices) {
            super(context, textViewResourceId, items);
            mBluetoothDevices = items;
            mContext = context;
            mSelectedPositions = new ArrayList<Integer>();
            mCheckboxArray = new SparseArray<CheckBox>();
            mDevicesInPreferences  = storedDevices;
    }
    
    public boolean isItemAtPositionSelected(int position) {
    	return mSelectedPositions.contains(position);
    }
    
    public void setCheckboxCheckedForPosition(int position, boolean checked) {
    	CheckBox checkbox = mCheckboxArray.get(position);
    	if (checkbox != null)
    		checkbox.setChecked(checked);
    	
    	if (checked) {
    		mSelectedPositions.add(position);
    	} else {
    		if (mSelectedPositions.contains(position))
    			mSelectedPositions.remove((Integer)position);
    	}
    }
    
    public boolean getIsCheckboxCheckedForPosition(int position) {
    	CheckBox checkbox = mCheckboxArray.get(position);
    	if (checkbox != null)
    		return checkbox.isChecked();
    	
    	return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.bluetooth_list_view, null);
            }
            final BluetoothDevice bluetoothDevice = mBluetoothDevices.get(position);
            if (bluetoothDevice != null) {
                    TextView deviceName = (TextView) v.findViewById(R.id.bluetooth_device_name);
                    TextView deviceAddress = (TextView) v.findViewById(R.id.bluetooth_device_address);
                    CheckBox checkbox = (CheckBox) v.findViewById(R.id.checkbox);
                    checkbox.setTag(position);
                    mCheckboxArray.append(position, checkbox);
                    if (mDevicesInPreferences != null) {
                    	if (mDevicesInPreferences.contains(bluetoothDevice.getAddress()))
                    		setCheckboxCheckedForPosition(position, true);
                    }	
                    
                    checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						
						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							Integer checkedPosition = (Integer) buttonView.getTag();
							if (isChecked) {
								mSelectedPositions.add(checkedPosition);
							} else {
								if (mSelectedPositions.contains(checkedPosition)) {
									mSelectedPositions.remove(checkedPosition);
									mDevicesInPreferences.remove(bluetoothDevice.getAddress());
								}
							}
						}
					});
                    if (deviceName != null) {
                    	deviceName.setText(bluetoothDevice.getName());
                    }
                    if (deviceAddress != null) {
                    	deviceAddress.setText(bluetoothDevice.getAddress());
                    }
                    
            }
            return v;
    }
}