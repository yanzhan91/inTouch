package com.yzdevelopment.inTouch;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.InputType;
import android.util.Log;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yzdevelopment.inTouch.dao.UserInfoDAO;
import com.yzdevelopment.inTouch.model.Field;

import android.provider.ContactsContract;

public class ChangeInfo extends ActionBarActivity {

    // location of saved data
	private static final String INFORMATION_RAW = "inTouch_information.raw";
	private static final String IMAGE_PNG = "inTouch_image.png";
	// Activity Result for choosing images from gallery
	private static final int LOAD_IMAGE_RESULTS = 1;
    private static final int CHANGE_INFO_SELECTION = 3;
    public static final String COMPANY = "Company";
    public static final String TITLE = "Title";
    private final String[] fields = {"name","mobile","home","work","email","company","job"};
    private UserInfoDAO dao;
    private EditText[] fieldViews = null;
    private LinearLayout layout;
	
	private final Context context = this;
	private ImageView iView;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        Contact contact = new Contact();

        retrieveNameImageFromProfile(contact);
        retrievePhoneFromProfile(contact);
        retrieveEmailFromProfile(contact);
        retrieveOrganizationFromProfile(contact);
        Log.i("profile", contact.toString());

        setContentView(R.layout.info);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setUpImage();

        layout = (LinearLayout) findViewById(R.id.infoLayout);
        layout.findFocus();

        dao = new UserInfoDAO(getApplicationContext());

//        restoreFields();

        Button infoSelectionButton = (Button) findViewById(R.id.info_selection_button);
        infoSelectionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                final Map<String, Boolean> changes = new HashMap<>();
                final Cursor cursor = dao.getAllFieldsForSelection();
                AlertDialog.Builder builder = new AlertDialog.Builder(ChangeInfo.this);
                builder.setTitle("Edit Fields")
                        .setMultiChoiceItems(cursor, "selected", "field_name", new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                                cursor.moveToPosition(which);
                                changes.put(cursor.getString(0), isChecked);
                            }
                        })
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dao.updateVisibleFields(changes);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // do nothing
                            }
                        });

                builder.create().show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_info_actions,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.changeImg:
                iView.performClick();
                return true;
            case R.id.removeImg:
                removeProfileImage();
                return true;
            case R.id.clearInfo:
                clearInfo();
                return true;
            case R.id.saveInfo:
                saveInfo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void retrieveNameImageFromProfile(Contact contact) {
        Cursor c = getApplication().getContentResolver().query(
                Uri.withAppendedPath(
                        ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
                new String[] {
                        ContactsContract.Profile.DISPLAY_NAME,
                        ContactsContract.Profile.PHOTO_THUMBNAIL_URI
                },
                ContactsContract.Contacts.Data.MIMETYPE + " = ?",
                new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE},
                null);

        if (c.moveToFirst()) {
            contact.setDisplay_name(c.getString(c.getColumnIndex(ContactsContract.Profile.DISPLAY_NAME)));
            contact.setImageUri(c.getString(c.getColumnIndex(ContactsContract.Profile.PHOTO_THUMBNAIL_URI)));
        }

        c.close();
    }

    private void retrievePhoneFromProfile(Contact contact) {
        Cursor c = getApplication().getContentResolver().query(
                Uri.withAppendedPath(
                        ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
                new String[] {
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.TYPE
                },
                ContactsContract.Contacts.Data.MIMETYPE + " = ?",
                new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE},
                null);

        while (c.moveToNext()) {
            switch (c.getInt(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))) {
                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                    contact.setMobile_phone(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                    contact.setHome_phone(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                    contact.setWork_phone(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    break;
                default:
                    break;
            }
        }
        c.close();
    }

    private void retrieveEmailFromProfile(Contact contact) {
        Cursor c = getApplication().getContentResolver().query(
                Uri.withAppendedPath(
                        ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
                new String[] {
                        ContactsContract.CommonDataKinds.Email.ADDRESS,
                        ContactsContract.CommonDataKinds.Email.IS_PRIMARY
                },
                ContactsContract.Contacts.Data.MIMETYPE + " = ?",
                new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE},
                null);

        while (c.moveToNext()) {
            if (c.getInt(c.getColumnIndex(ContactsContract.CommonDataKinds.Email.IS_PRIMARY)) == 1) {
                contact.setEmail(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                break;
            } else if (contact.getEmail() == null) {
                contact.setEmail(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
            }
        }
        c.close();
    }

    private void retrieveOrganizationFromProfile(Contact contact) {
        Cursor c = getApplication().getContentResolver().query(
                Uri.withAppendedPath(
                        ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
                new String[] {
                        ContactsContract.CommonDataKinds.Organization.COMPANY,
                        ContactsContract.CommonDataKinds.Organization.TITLE
                },
                ContactsContract.Contacts.Data.MIMETYPE + " = ?",
                new String[]{ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE},
                null);

        if (c.moveToFirst()) {
            contact.setCompany(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY)));
            contact.setJobTitle(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE)));
        }
        c.close();
    }

    private void removeProfileImage() {
        new AlertDialog.Builder(context)
                .setTitle("Deleting image...")
                .setMessage("Are you sure you want to delete your profile image?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        boolean imgDeleteResult = deleteFile(IMAGE_PNG);
                        if (!imgDeleteResult) {
                            new AlertDialog.Builder(context)
                                    .setTitle("Error")
                                    .setMessage("Image failed to delete")
                                    .setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        } else {
                            iView.setImageResource(R.drawable.default_contact_image);
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    private void clearInfo() {
        new AlertDialog.Builder(context)
                .setTitle("Clearing Info...")
                .setMessage("Are you sure you want to clear your information?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        for (EditText et : fieldViews) {
                            et.setText("");
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    private void saveInfo() {
        //Log.i("Yan", "Saving...");

        if (fieldViews[0].getText().toString().equals("")) {
            new AlertDialog.Builder(context)
                    .setTitle("Error")
                    .setMessage("Must have contact name!")
                    .setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_menu_save)
                    .show();
            return;
        }

        try {
            FileOutputStream f = openFileOutput(INFORMATION_RAW, Context.MODE_PRIVATE);
            collectFields();
            PrintWriter pw = new PrintWriter(f);

            for (EditText et : fieldViews) {
                String s = et.getText().toString();

                if (s.equals("")) {
                    //Log.i("Yan", "__empty");
                    pw.println("__empty");
                } else {
                    //Log.i("Yan", s);
                    pw.println(s);
                }
            }
            pw.flush();
            pw.close();
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new AlertDialog.Builder(context)
                .setTitle("Information saved")
                .setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish(); // go back to home screen
                    }
                })
                .setIcon(android.R.drawable.ic_menu_save)
                .show();
    }

	private void setUpImage() {
    	
    	iView = (ImageView) findViewById(R.id.profileView);
        
        iView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// Create the Intent for Image Gallery.
		        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		         
		        // Start new activity with the LOAD_IMAGE_RESULTS to handle back the results when image is picked from the Image Gallery.
		        startActivityForResult(i, LOAD_IMAGE_RESULTS);
			}
		});
        
        try {
			FileInputStream f = openFileInput(IMAGE_PNG);
			Bitmap myBitmap = BitmapFactory.decodeStream(f);
        	iView.setImageBitmap(myBitmap);
		} catch (FileNotFoundException e) {
			iView.setImageResource(R.drawable.default_contact_image);
		}
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        final int PIC_CROP = 2;
        
        if (requestCode == LOAD_IMAGE_RESULTS && resultCode == RESULT_OK && data != null) {
            Uri pickedImage = data.getData();
            performCrop(pickedImage);
        } else if (requestCode == PIC_CROP) {
        	//get the returned data
        	Bundle extras = data.getExtras();
        	//get the cropped bitmap
        	Bitmap thePic = extras.getParcelable("data");
            try {
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(thePic, 82, 82, false);
                FileOutputStream f = openFileOutput(IMAGE_PNG, Context.MODE_PRIVATE);
                resizedBitmap.compress(CompressFormat.PNG, 100, f);
            } catch (RuntimeException e) {
                // do nothing
                return;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
           
        	iView.setImageBitmap(thePic); 
        } else if (requestCode == CHANGE_INFO_SELECTION) {
            System.out.println(requestCode);
            System.out.println(resultCode);
            System.out.println(data.getStringArrayListExtra("info_selection").size());
        }
    }
    
    private void performCrop(Uri data){
    	final int PIC_CROP = 2;
    	
    	try {
    	    //call the standard crop action intent (the user device may not support it)
    		Intent cropIntent = new Intent("com.android.camera.action.CROP"); 
    		//indicate image type and Uri
    		cropIntent.setDataAndType(data, "image/*");
		    //set crop properties
    		cropIntent.putExtra("crop", "true");
		    //indicate aspect of desired crop
    		cropIntent.putExtra("aspectX", 1);
    		cropIntent.putExtra("aspectY", 1);
		    //indicate output X and Y
    		cropIntent.putExtra("outputX", 256);
    		cropIntent.putExtra("outputY", 256);
		    //retrieve data on return
    		cropIntent.putExtra("return-data", true);
		    //start the activity - we handle returning in onActivityResult
    		startActivityForResult(cropIntent, PIC_CROP);
    	}
    	catch(ActivityNotFoundException anfe){
    	    //display an error message
    	    String errorMessage = "Whoops - your device doesn't support the crop action!";
    	    Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
    	    toast.show();
    	}
    }
    
    public void restoreFields() {

        List<Field> selectedFields = dao.getAllSelectedFields();
        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
                );
        for (Field field : selectedFields) {
            Log.i("Yan", "restoreFields: " + field.getField_name());
            Log.i("Yan", "restoreFields: " + layout.getChildCount());
            TextView tv = new TextView(getApplicationContext());
            tv.setLayoutParams(layoutParams);
            tv.setTextSize(18);
            tv.setTextColor(getResources().getColor(R.color.infoTextView));
            tv.setText(field.getField_name());
            layout.addView(tv);

            EditText et = new EditText(getApplicationContext());
            et.setLayoutParams(layoutParams);
            et.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            et.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rect_text_edit));
            et.setPadding(5,2,0,2);
            et.setText(field.getField_value());
            layout.addView(et);
        }
    }
    
    public void collectFields() {
    	if (fieldViews != null) {
    		return;
    	}
    	
    	fieldViews = new EditText[fields.length];
    	
    	for (int i = 0; i < fields.length; i++) {
        	fieldViews[i] = (EditText)findViewById(getResources().getIdentifier(fields[i]+"ET", "id", getPackageName()));
        }
        fieldViews[1].addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        fieldViews[2].addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        fieldViews[3].addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }
    
    @Override
    public void onResume() {
        super.onResume();
        PendingIntent pendingIntent = PendingIntent.getActivity( this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, pendingIntent, null, null);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);
    }
}