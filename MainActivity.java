package com.example.wallet;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONException;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        path = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWLOADS).getPath();
        fileName = Memory.getString( this,"ETH_WALLET_FILE_NAME", "null");
        user_address_textview= findViewById(R.id.user_address);
        user_amount_textview= findViewById(R.id.user_amount);
        transfer_btn= findViewById(R.id.transfer_btn);
        transfer_btn.setOnClickListener(this);
        requestAppPermissions();
        checkWalletStatus();
    }
private void checkWalletStatus(){
        try{
            final JSONObject ethkey = new JSONObject(readFromFile(path+"/"+filename));
            final String address =ethkey.getString("address");
            user_address_textview.setText("0x"+address);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Admin web3j = AdminFactory.build(new HttpService("https://kovan.infura.io/v3/40f8309fa79c4b7a8ca53e7b26c8c74d"));
                        final BigDecimal balance new BigDecimal(getTokenBalance(web3j,"0x"+address,"0xd90A06cB48B3BdAc85558D086A3279645acc103E"));
                        runOnUiThread(new Runnable() {
                            public void run() {
                                user_amount_textview.setText(String.valueOf(balance.divide(BigDecimal.valueOf(1000))));
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                user_amount_textview.setText("0");
                            }
                        });
                    }
                }
            });
            thread.start();
        }catch (JSONException e){
            e.printStackTrace();
        }
     }

private void requestAppPermissions(){
    if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
        return;
       }
    ActivityCompat.requestPermissions(this,
            new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                 }, REQUEST_WRITE_STORAGE_REQUEST_CODE);//your request code
    checkWalletStatus();
            );
    }

private void onRequestPermissionResult(int requestCode,String permission[],int[] grantResults){
    switch (requestCode){
        case REQUEST_WRITE_STORAGE_REQUEST_CODE:{
            if(grantResults.length>0
                  && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                
            }
        }
      }
    }
}
