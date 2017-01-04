package com.yzdevelopment.inTouch;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.Locale;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class BeamData extends ActionBarActivity {

	private NfcAdapter mNfcAdapter;
	private NdefMessage mNdefMessage;
	private static final String IMAGE_PNG = "inTouch_image.png";

	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);

		setContentView(R.layout.transfer);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView mTextView = (TextView)findViewById(R.id.tv);

        mTextView.setText("Sending...");

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		Intent intent = getIntent();
		String message = intent.getStringExtra("com.yzdevelopment.inTouch.beam_message");
		
		Bitmap bmp;
		try {
			FileInputStream f = openFileInput(IMAGE_PNG);
			bmp = BitmapFactory.decodeStream(f);
            //Log.i("Yan", "Initial Size: " + bmp.getRowBytes() * bmp.getHeight());

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG,100,stream);
            byte[] byteArray = stream.toByteArray();

            //Log.i("Yan", "Sending Size: " + bmp.getRowBytes() * bmp.getHeight());

            // create an NDEF message with two records of plain text type
            mNdefMessage = new NdefMessage(
                    new NdefRecord[] {
                            createNewTextRecord(message, Locale.ENGLISH, true),
                            NdefRecord.createMime("image/png", byteArray)
                    }
            );
        } catch (FileNotFoundException e) {
            mNdefMessage = new NdefMessage(
                    new NdefRecord[] {
                            createNewTextRecord(message, Locale.ENGLISH, true)
                    }
            );
		}
	}
	
	public static NdefRecord createNewTextRecord(String text, Locale locale, boolean encodeInUtf8) {
		byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

		Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
		byte[] textBytes = text.getBytes(utfEncoding);

		int utfBit = encodeInUtf8 ? 0 : (1 << 7);
		char status = (char)(utfBit + langBytes.length);

		byte[] data = new byte[1 + langBytes.length + textBytes.length]; 
		data[0] = (byte)status;
		System.arraycopy(langBytes, 0, data, 1, langBytes.length);
		System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

		return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
	}

	@Override
	public void onResume() {
		super.onResume();
		mNfcAdapter.setNdefPushMessage(mNdefMessage, this);
        PendingIntent pendingIntent = PendingIntent.getActivity( this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNfcAdapter.enableForegroundDispatch(this,pendingIntent,null,null);
	}

	@Override
	public void onPause() {
		super.onPause();
		mNfcAdapter.setNdefPushMessage(mNdefMessage, this);
        mNfcAdapter.disableForegroundDispatch(this);
	}
}