package com.yzdevelopment.inTouch;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.provider.Settings;
import android.os.Build;

import android.app.PendingIntent;

public class HomePage extends Activity {

	private NfcAdapter mNfcAdapter = null;
    private TextView nfcStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
        new SimpleEula(this).show();

        nfcStatus = (TextView) findViewById(R.id.nfcStatus);
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter != null) {
            if (mNfcAdapter.isEnabled()) {
                nfcStatus.setText("ON");
                nfcStatus.setTextColor(Color.parseColor("green"));
            } else {
                nfcStatus.setText("OFF");
                nfcStatus.setTextColor(Color.parseColor("red"));
            }
        } else {
            nfcStatus.setText("UNAVAILABLE");
            nfcStatus.setTextColor(Color.parseColor("red"));
        }
	}
	
	public void showAlert(String title, String msg) {
		new AlertDialog.Builder(this)
				.setTitle(title)
				.setMessage(msg)
				.setNeutralButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// do nothing
							}
						})
                .setIcon(android.R.drawable.ic_delete)
                .show();
	}

	public void viewProfile(View v) {
		if (mNfcAdapter == null) {
			showAlert("Error", "No NFC adapter found on device");
		} else {
			startActivity(new Intent(HomePage.this, ProfileInfo.class));
		}
	}

	public void changeNFC(View v) {
		if (mNfcAdapter == null) {
			showAlert("Error", "No NFC adapter found on device");
		} else {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
				startActivity(intent);
			} else {
				Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
				startActivity(intent);
			}
		}
	}

	public void beamContact(View v) {
		if (mNfcAdapter == null || !mNfcAdapter.isEnabled()) {
			showAlert("Error", "Please turn on NFC in NFC Settings");
		} else if (!mNfcAdapter.isNdefPushEnabled()) {
			showAlert("Error", "Android Beam is disabled. Go to NFC settings to turn it on.");
		} else {
			Intent intent = new Intent(HomePage.this, BeamData.class);
			String s = "";//retrieveInfo();
			if (s != null) {
				intent.putExtra("com.yzdevelopment.inTouch.beam_message", s);
				startActivity(intent);
			} else {
				// show alert indicating contact information not filled
				showAlert("Error","Information not filled.");
			}
		}
	}

	public void receiveContact(View v) {
		if (mNfcAdapter == null || !mNfcAdapter.isEnabled()) {
			showAlert("Error", "Please turn on NFC in NFC Settings");
		} else {
			startActivity(new Intent(HomePage.this, TagDispatch.class));
		}
	}

     @Override
     public void onResume() {
         super.onResume();
         if (mNfcAdapter != null) {
             if (mNfcAdapter.isEnabled()) {
                 nfcStatus.setText("ON");
                 nfcStatus.setTextColor(Color.parseColor("green"));
             } else {
                 nfcStatus.setText("OFF");
                 nfcStatus.setTextColor(Color.parseColor("red"));
             }
         }

         PendingIntent pendingIntent = PendingIntent.getActivity( this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
         if (mNfcAdapter != null) {
             mNfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
         }
     }

     @Override
     public void onPause() {
         super.onPause();
         if (mNfcAdapter != null) {
             mNfcAdapter.disableForegroundDispatch(this);
         }
     }
}