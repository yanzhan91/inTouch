package com.yzdevelopment.inTouch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.Locale;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;

public class BeamData extends ActionBarActivity {

	private NfcAdapter mNfcAdapter = null;
	private NdefMessage mNdefMessage = null;

	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);

		setContentView(R.layout.transfer);
		ActionBar actionBar = getSupportActionBar();
		actionBar.show();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.brandPrimaryColor)));
		actionBar.setDisplayShowTitleEnabled(false);

        TextView mTextView = (TextView)findViewById(R.id.tv);

        mTextView.setText("Ready to send");

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		Intent intent = getIntent();
		String imageUri = intent.getStringExtra("com.yzdevelopment.inTouch.beam_imageUri");
		String json = intent.getStringExtra("com.yzdevelopment.inTouch.beam_contact");

		if (imageUri != null && !imageUri.isEmpty()) {
			try {

                Bitmap bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(imageUri)));

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bmp.compress(Bitmap.CompressFormat.PNG,0,stream);

				byte[] byteArray = stream.toByteArray();

				mNdefMessage = new NdefMessage(
					new NdefRecord[] {
						createNewRecord(json, Locale.ENGLISH, true),
						new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "image/png".getBytes(), null, byteArray)
					}
				);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
                imageUri = null;
			}
		}

		if (imageUri == null && mNdefMessage == null) {
			mNdefMessage = new NdefMessage(
				new NdefRecord[] {
						createNewRecord(json, Locale.ENGLISH, true)
				}
			);
		}
	}
	
	public static NdefRecord createNewRecord(String text, Locale locale, boolean encodeInUtf8) {
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