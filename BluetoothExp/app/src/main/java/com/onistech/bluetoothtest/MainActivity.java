package com.onistech.bluetoothtest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_ENABLE_BT = 1;
    private Button onBtn, offBtn, listBtn, findBtn;
    private TextView text;
    private BluetoothAdapter myBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ListView myListView;
    private ArrayAdapter<String> BTArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        offBtn = (Button)findViewById(R.id.turnOff);
        offBtn.setOnClickListener(this);
        text = (TextView)findViewById(R.id.text);
        onBtn = (Button)findViewById(R.id.turnOn);
        onBtn.setOnClickListener(this);
        listBtn = (Button)findViewById(R.id.paired);
        listBtn.setOnClickListener(this);
        findBtn = (Button)findViewById(R.id.search);
        findBtn.setOnClickListener(this);
        myListView = (ListView)findViewById(R.id.listView1);
        BTArrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        myListView.setAdapter(BTArrayAdapter);

        if (myBluetoothAdapter==null){
            onBtn.setEnabled(false);
            offBtn.setEnabled(false);
            listBtn.setEnabled(false);
            findBtn.setEnabled(false);
            text.setText("Status: not supported");
            Toast.makeText(getApplicationContext(),"Your device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();
        }



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.turnOff:
                off();
                break;
            case R.id.turnOn:
                on();
                break;
            case R.id.search:
                find();
                break;
            case R.id.paired:
                list();
        }
    }


    private void on(){
        if (!myBluetoothAdapter.isEnabled()){
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent,REQUEST_ENABLE_BT);
            Toast.makeText(this, "Bluetooth is turned On", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Bluetooth is already On", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==REQUEST_ENABLE_BT){
            if (myBluetoothAdapter.isEnabled()){
                text.setText("Status: Enabled");
            }else{
                text.setText("Status: Disabled");
            }
        }
    }

    public void list(){
        pairedDevices = myBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device:pairedDevices)
            BTArrayAdapter.add(device.getName()+"\n"+device.getAddress());

        Toast.makeText(this, "Show Paired Devices", Toast.LENGTH_SHORT).show();
    }

    public void find(){
        if (myBluetoothAdapter.isDiscovering()){
            // the button is pressed when it discovers, so cancel the discovery
            myBluetoothAdapter.cancelDiscovery();
        }else{
            BTArrayAdapter.clear();
            myBluetoothAdapter.startDiscovery();
            registerReceiver(broadcastReceiver,new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
    }

    public void off(){

        myBluetoothAdapter.disable();
        text.setText("Status: Disconnected");
        Toast.makeText(getApplicationContext(),"Bluetooth turned off",
                Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name and the MAC address of the object to the arrayAdapter
                BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                BTArrayAdapter.notifyDataSetChanged();
            }
        }
    };
}
