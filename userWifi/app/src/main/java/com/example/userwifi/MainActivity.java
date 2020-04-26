package com.example.userwifi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

   Button btnEstado, btnEnviar,btnDiscover;
   EditText editMensaje, editEscribir;
   ListView lista;
   TextView titulo;

   WifiManager wifiManager;
   WifiP2pManager mManager;
   WifiP2pManager.Channel mChannel;
   BroadcastReceiver broadcastReceiver;
   IntentFilter intentFilter;
   List<WifiP2pDevice> listaPeers = new ArrayList<>();
   String []arregloNombreDispocitivo;
   WifiP2pDevice[] arregloDispocitivos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDiscover = findViewById(R.id.BtnDiscoveri);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnEstado = findViewById(R.id.BtnOfOn);
        editEscribir = findViewById(R.id.editEnviar);
        editMensaje = findViewById(R.id.editMensaje);
        titulo = findViewById(R.id.TextTitulo);

        //INICIALIZAMOS EL WIFI MANAGER
        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mManager = (WifiP2pManager) getApplicationContext().getSystemService(WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this,getMainLooper(),null);

        //INSTANCIA ASIA LA CLASE DEL BROADCAST
        broadcastReceiver = new WiFiDirectBroadcastReceiver(mManager,mChannel,null);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


        //VALIDAMOS QUE EL WIFI ESTE ENSENDIDO(el boton del programa que simula el estado del wifi)

        EstadoWIfi();

    }

    private void EstadoWIfi() {
        //
    btnEstado.setOnClickListener(new View.OnClickListener() {
        //SI EL WIFI DE NUESTRO DISPOSITIVO ESTA ENCENDIDO NO PASARA NADA, SI NO NOS PEDIRA QUE LO ACTIVEMOS
        @Override
        public void onClick(View v) {
            if (wifiManager.isWifiEnabled()){
                wifiManager.setWifiEnabled(false);
                btnEstado.setText("ON");
            }else{
                wifiManager.setWifiEnabled(true);
                btnEstado.setText("OF");
            }
        }
    });

    btnDiscover.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                titulo.setText("Dispositivo Iniciado");
                }

                @Override
                public void onFailure(int reason) {
                    titulo.setText("Iniciacion del Dispocitivo fallida");
                }
            });
        }
    });
    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            if (!peerList.getDeviceList().equals(listaPeers)){
                listaPeers.clear();
                listaPeers.addAll(peerList.getDeviceList());

                arregloNombreDispocitivo = new String[peerList.getDeviceList().size()];
                arregloDispocitivos = new WifiP2pDevice[peerList.getDeviceList().size()];
                int index = 0;

                for (WifiP2pDevice device : peerList.getDeviceList()){
                    arregloNombreDispocitivo[index] = device.deviceName;
                    arregloDispocitivos[index] = device;
                    index ++;
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                            android.R.layout.simple_list_item_1, arregloNombreDispocitivo);
                    lista.setAdapter(adapter);

                    if (listaPeers.size() == 0 ){
                        Toast.makeText(MainActivity.this, "No encontrado el dispositivo",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

}
