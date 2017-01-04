package com.yzdevelopment.inTouch;

//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import android.app.AlertDialog;
//import android.app.PendingIntent;
//import android.content.ContentProviderOperation;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.ContentUris;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.nfc.NdefMessage;
//import android.nfc.NdefRecord;
//import android.nfc.NfcAdapter;
//import android.nfc.Tag;
//import android.nfc.tech.NfcF;
//import android.os.Bundle;
//import android.os.Parcelable;
//import android.provider.ContactsContract;
//import android.provider.ContactsContract.Data;
import android.support.v7.app.ActionBarActivity;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ScrollView;
//import android.widget.TableLayout;
//import android.widget.TableRow;
//import android.widget.TextView;
//
public class TagDispatch extends ActionBarActivity {
//
//	private NfcAdapter mNfcAdapter;
//	private PendingIntent mPendingIntent;
//	private IntentFilter[] mIntentFilters;
//	private String[][] mNFCTechLists;
//
//	@Override
//	public void onCreate(Bundle savedState) {
//
//		super.onCreate(savedState);
//
//		setContentView(R.layout.transfer);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//
//		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
//
//        TextView mTextView = (TextView)findViewById(R.id.tv);
//
//		mTextView.setText("Receiving...");
//
//		// create an intent with tag data and deliver to this activity
//		mPendingIntent = PendingIntent.getActivity(this, 0,
//				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
//
//		// set an intent filter for all MIME data
//		IntentFilter ndefIntent = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
//		try {
//			ndefIntent.addDataType("*/*");
//			mIntentFilters = new IntentFilter[] { ndefIntent };
//		} catch (Exception e) {
//			Log.e("TagDispatch", e.toString());
//		}
//
//		mNFCTechLists = new String[][] { new String[] { NfcF.class.getName() } };
//	}
//
//	@Override
//	public void onNewIntent(Intent intent) {
//		String action = intent.getAction();
//		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//
//		String s = action + "\n\n" + tag.toString();
//
//		// parse through all NDEF messages and their records and pick text type only
//		Parcelable[] data = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
//
//		byte[] imagePayload = null;
//
//		if (data != null) {
//			try {
//				for (int i = 0; i < data.length; i++) {
//					NdefRecord [] recs = ((NdefMessage)data[i]).getRecords();
//					for (int j = 0; j < recs.length; j++) {
//						if (recs[j].getTnf() == NdefRecord.TNF_WELL_KNOWN &&
//								Arrays.equals(recs[j].getType(), NdefRecord.RTD_TEXT)) {
//
//							byte[] payload = recs[j].getPayload();
//							String textEncoding = ((payload[0] & 0x80) == 0) ? "UTF-8" : "UTF-16";
//							int langCodeLen = payload[0] & 0x3F;
//
//							s += ("\n\nNdefMessage[" + i + "], NdefRecord[" + j + "]:\n\"" +
//									new String(payload, langCodeLen + 1,
//											payload.length - langCodeLen - 1, textEncoding) +
//											"\"");
//						} else if (recs[j].getTnf() == NdefRecord.TNF_MIME_MEDIA) {
//							imagePayload = recs[j].getPayload();
//						}
//					}
//				}
//				if (imagePayload != null) {
//					addContact(s, BitmapFactory.decodeByteArray(imagePayload , 0, imagePayload.length));
//				} else {
//					addContact(s, BitmapFactory.decodeResource(getResources(),R.drawable.default_contact_image));
//				}
//			} catch (Exception e) {
//				Log.e("TagDispatch", e.toString());
//			}
//
//		}
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//
//		//Log.i("Yan", "Resuming...");
//		mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, mNFCTechLists);
//	}
//
//	@Override
//	public void onPause() {
//		super.onPause();
//
//		//Log.i("Yan", "Pausing...");
//		mNfcAdapter.disableForegroundDispatch(this);
//	}
//
//    public void addContact(String s, final String imageUri) {
//        //Log.i("Yan", "Received Size: " + imageUri.getRowBytes() * imageUri.getHeight());
//        //Log.i("Yan", "Received Message: " + s);
//        s = s.substring(s.indexOf('"')+1, s.lastIndexOf('"'));
//
//        final String[] infoArray = s.split(",");
//        final Contact receivedContact = new Contact(infoArray[0],infoArray[1],
//                infoArray[2],infoArray[3],infoArray[4],infoArray[5],infoArray[6], imageUri);
//
//        List<Contact> similarContacts = findSimilarContacts(receivedContact.getDisplay_name());
//
//        if (similarContacts.size() != 0) {
//            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            LinearLayout main_layout = (LinearLayout) inflater.inflate(R.layout.similar_contact_alert_main, null);
//
//            ImageView newContactIv = (ImageView) main_layout.findViewById(R.id.new_contacts_image);
//            newContactIv.setImageBitmap(imageUri);
//            TextView newContactTv = (TextView) main_layout.findViewById(R.id.new_contacts_infomation);
//            newContactTv.setText(receivedContact.dispInAlert());
//            // TODO
//            // no idea why bottom works while center_vertical doesn't
//            newContactTv.setGravity(Gravity.BOTTOM);
//
//            ScrollView parent = (ScrollView) main_layout.findViewById(R.id.main_scrollview);
//
//            TableLayout table1 = (TableLayout) parent.findViewById(R.id.table);
//
//            // Similar Contacts
//            for (Contact currentContact : similarContacts) {
//                TableRow custom = (TableRow) inflater.inflate(R.layout.existing_similar_contact_alert, null);
//                ImageView iv = (ImageView) custom.findViewById(R.id.similar_contacts_image);
//                iv.setImageBitmap(currentContact.getImage());
//                TextView tv = (TextView) custom.findViewById(R.id.similar_contacts_infomation);
//                tv.setText(currentContact.dispInAlert());
//                // TODO
//                // Lots of problems with gravity
//                tv.setGravity(Gravity.CENTER_VERTICAL);
//                table1.addView(custom);
//                TableRow black_line = (TableRow) inflater.inflate(R.layout.black_line,null);
//                table1.addView(black_line);
//            }
//
//            new AlertDialog.Builder(this)
//                    .setTitle("Contact Conflict")
//                    .setPositiveButton("Add Anyway", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            createNewContact(receivedContact,imageUri);
//                        }
//                    })
//                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // do nothing
//                        }
//                    })
//                    .setView(main_layout)
//                    .show();
//
//        } else {
//            createNewContact(receivedContact, imageUri);
//        }
//    }
//
//	public void createNewContact(Contact receivedContact, String image) {
//
//		ArrayList<ContentProviderOperation> ops = new ArrayList<>();
//
//		ops.add(ContentProviderOperation
//				.newInsert(ContactsContract.RawContacts.CONTENT_URI)
//				.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
//				.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
//				.build());
//
//		// ------------------------------------------------------ Names
//		if (receivedContact.getDisplay_name() != null) {
//			ops.add(ContentProviderOperation
//					.newInsert(ContactsContract.Data.CONTENT_URI)
//					.withValueBackReference(
//							ContactsContract.Data.RAW_CONTACT_ID, 0)
//					.withValue(
//							ContactsContract.Data.MIMETYPE,
//							ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
//					.withValue(
//							ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
//                            receivedContact.getDisplay_name()).build());
//		}
//
//		// ------------------------------------------------------ Mobile Number
//		if (receivedContact.getMobile_phone() != null) {
//			ops.add(ContentProviderOperation
//					.newInsert(ContactsContract.Data.CONTENT_URI)
//					.withValueBackReference(
//							ContactsContract.Data.RAW_CONTACT_ID, 0)
//					.withValue(
//							ContactsContract.Data.MIMETYPE,
//							ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//					.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
//                            receivedContact.getMobile_phone())
//					.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
//							ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
//					.build());
//		}
//
//		// ------------------------------------------------------ Home Numbers
//		if (receivedContact.getHome_phone() != null) {
//			ops.add(ContentProviderOperation
//					.newInsert(ContactsContract.Data.CONTENT_URI)
//					.withValueBackReference(
//							ContactsContract.Data.RAW_CONTACT_ID, 0)
//					.withValue(
//							ContactsContract.Data.MIMETYPE,
//							ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//					.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
//                            receivedContact.getHome_phone())
//					.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
//							ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
//					.build());
//		}
//
//		// ------------------------------------------------------ Work Numbers
//		if (receivedContact.getWork_phone() != null) {
//			ops.add(ContentProviderOperation
//					.newInsert(ContactsContract.Data.CONTENT_URI)
//					.withValueBackReference(
//							ContactsContract.Data.RAW_CONTACT_ID, 0)
//					.withValue(
//							ContactsContract.Data.MIMETYPE,
//							ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//					.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
//                            receivedContact.getWork_phone())
//					.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
//							ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
//					.build());
//		}
//
//		// ------------------------------------------------------ Email
//		if (receivedContact.getEmail() != null) {
//			ops.add(ContentProviderOperation
//					.newInsert(ContactsContract.Data.CONTENT_URI)
//					.withValueBackReference(
//							ContactsContract.Data.RAW_CONTACT_ID, 0)
//					.withValue(
//							ContactsContract.Data.MIMETYPE,
//							ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
//					.withValue(ContactsContract.CommonDataKinds.Email.DATA,
//                            receivedContact.getEmail())
//					.withValue(ContactsContract.CommonDataKinds.Email.TYPE,
//							ContactsContract.CommonDataKinds.Email.TYPE_HOME)
//					.build());
//		}
//
//		// ------------------------------------------------------ Organization
//
//		boolean writeCompany = receivedContact.getCompany() != null;
//		boolean writeTitle = receivedContact.getJobTitle() != null;
//
//		if (writeCompany && writeTitle) {
//			ops.add(ContentProviderOperation
//					.newInsert(ContactsContract.Data.CONTENT_URI)
//					.withValueBackReference(
//							ContactsContract.Data.RAW_CONTACT_ID, 0)
//					.withValue(
//							ContactsContract.Data.MIMETYPE,
//							ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
//					.withValue(
//							ContactsContract.CommonDataKinds.Organization.COMPANY,
//                            receivedContact.getCompany())
//					.withValue(
//							ContactsContract.CommonDataKinds.Organization.TYPE,
//							ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
//					.withValue(
//							ContactsContract.CommonDataKinds.Organization.TITLE,
//                            receivedContact.getJobTitle())
//					.withValue(
//							ContactsContract.CommonDataKinds.Organization.TYPE,
//							ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
//					.build());
//		} else if (writeCompany) {
//			ops.add(ContentProviderOperation
//					.newInsert(ContactsContract.Data.CONTENT_URI)
//					.withValueBackReference(
//							ContactsContract.Data.RAW_CONTACT_ID, 0)
//					.withValue(
//							ContactsContract.Data.MIMETYPE,
//							ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
//					.withValue(
//							ContactsContract.CommonDataKinds.Organization.COMPANY,
//                            receivedContact.getCompany())
//					.withValue(
//							ContactsContract.CommonDataKinds.Organization.TYPE,
//							ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
//					.build());
//		} else {
//			ops.add(ContentProviderOperation
//					.newInsert(ContactsContract.Data.CONTENT_URI)
//					.withValueBackReference(
//							ContactsContract.Data.RAW_CONTACT_ID, 0)
//					.withValue(
//							ContactsContract.Data.MIMETYPE,
//							ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
//					.withValue(
//							ContactsContract.CommonDataKinds.Organization.TITLE,
//                            receivedContact.getJobTitle())
//					.withValue(
//							ContactsContract.CommonDataKinds.Organization.TYPE,
//							ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
//					.build());
//		}
//
//		if(image != null){
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            image.compress(Bitmap.CompressFormat.PNG, 100, baos);
//            ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
//            .withValueBackReference(Data.RAW_CONTACT_ID, 0)
//            .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
//            .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, baos.toByteArray())
//            .build());
//        }
//
//		// Asking the Contact provider to create a new contact
//		try {
//			getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return;
//		}
//
//        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        LinearLayout confirmationLayout = (LinearLayout) inflater.inflate(R.layout.new_contact_confirmation_dialog, null);
//        ImageView confImg = (ImageView) confirmationLayout.findViewById(R.id.contact_added_conf_image);
//        TextView confName = (TextView) confirmationLayout.findViewById(R.id.contact_added_conf_name);
//        TextView confInfo = (TextView) confirmationLayout.findViewById(R.id.contact_added_conf_info);
//        confImg.setImageBitmap(image);
//        confName.setText(receivedContact.dispInConfirmationName());
//        confInfo.setText(receivedContact.dispInConfirmationInfo());
//
//        new AlertDialog.Builder(this)
//                .setTitle("Contact Added Successfully")
//                .setNeutralButton(android.R.string.yes,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,
//                                                int which) {
//                                finish();
//                            }
//                        })
//                .setIcon(android.R.drawable.ic_input_add)
//                .setView(confirmationLayout)
//                .show();
//	}
//
//    private List<Contact> findSimilarContacts(String name) {
//
//        List<Contact> similarContacts = new ArrayList<>();
//
//        // Get contact id with name
//        Cursor idCursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
//                new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.HAS_PHONE_NUMBER},"display_name = ?",new String[]{name},null);
//
//        while (idCursor.moveToNext()) {
//            Contact newContact = new Contact();
//            String id = idCursor.getString(idCursor.getColumnIndex(ContactsContract.Contacts._ID));
//            boolean success = false;
//
//            if (idCursor.getInt(idCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) != 0) {
//                success = findNumbers(newContact,id);
//            }
//
//            if (!success) {
//                success = findEmails(newContact,id);
//            }
//
//            if (!success) {
//                success = findOrganizations(newContact,id);
//            }
//
//            if (success) {
//                findPhoto(newContact, id);
//                newContact.setDisplay_name(name);
//                similarContacts.add(newContact);
//            }
//        }
//        idCursor.close();
//        return similarContacts;
//    }
//
//    private boolean findOrganizations(Contact contact, String id) {
//        Cursor orgCursor = getContentResolver().query(
//                Data.CONTENT_URI,
//                new String[]{
//                        ContactsContract.CommonDataKinds.Organization.COMPANY,
//                        ContactsContract.CommonDataKinds.Organization.TITLE,
//                        ContactsContract.CommonDataKinds.Organization.MIMETYPE
//                },
//                Data.CONTACT_ID + " = ? ",
//                new String[]{id},
//                null);
//
//        while (orgCursor.moveToNext()) {
//            String company = orgCursor.getString(orgCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA1));
//            String title = orgCursor.getString(orgCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA4));
//            String mimeType = orgCursor.getString(orgCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.MIMETYPE));
//
//            if (mimeType.equals("vnd.android.cursor.item/organization") && (company != null || title != null)) {
//                contact.setCompany(company);
//                contact.setJobTitle(title);
//                orgCursor.close();
//                return true;
//            }
//        }
//        orgCursor.close();
//        return false;
//    }
//
//    private boolean findEmails(Contact contact, String id) {
//        Cursor emailCursor = getContentResolver().query(
//                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
//                new String[]{ContactsContract.CommonDataKinds.Email.DATA},
//                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
//                new String[]{id},
//                null);
//        boolean successful = false;
//        if (emailCursor.moveToFirst()) {
//            contact.setEmail(emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
//            successful = true;
//        }
//        emailCursor.close();
//        return successful;
//    }
//
//    private boolean findNumbers(Contact contact, String id) {
//        boolean isSuccessful = false;
//        boolean earlyFinish = false;
//        Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.CommonDataKinds.Phone.TYPE},ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?" ,new String[]{id},null);
//        while (phoneCursor.moveToNext() && !earlyFinish) {
//            switch (phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))) {
//                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
//                    contact.setHome_phone(phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
//                    isSuccessful = true;
//                    break;
//                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
//                    contact.setMobile_phone(phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
//                    isSuccessful = true;
//                    earlyFinish = true;
//                    break;
//                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
//                    contact.setWork_phone(phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
//                    isSuccessful = true;
//                    break;
//                default:
//                    // do nothing
//            }
//        }
//        phoneCursor.close();
//        return isSuccessful;
//    }
//
//    private void findPhoto(Contact contact, String id) {
//        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id));
//        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
//        Cursor cursor = getContentResolver().query(photoUri,
//                new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
//        if (cursor != null) {
//            try {
//                if (cursor.moveToFirst()) {
//                    byte[] data = cursor.getBlob(0);
//                    if (data != null) {
//                        contact.setImage(BitmapFactory.decodeStream(new ByteArrayInputStream(data)));
//                    }
//                }
//            } finally {
//                cursor.close();
//            }
//        }
//    }
}