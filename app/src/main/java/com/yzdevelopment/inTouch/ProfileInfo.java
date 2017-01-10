package com.yzdevelopment.inTouch;

import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.provider.ContactsContract;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ProfileInfo extends ActionBarActivity {

    public static final String NOT_SET = "Not Set";

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.profile);
        setUpActionBar();
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.brandPrimaryColor)));
        actionBar.setDisplayShowTitleEnabled(false);

        final Drawable leftArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        leftArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(leftArrow);
    }

    private void displayProfile() {
        Contact contact = new Contact();
        retrieveProfile(contact);
        setupImage(contact);
        setupCheckboxList(contact);
    }

    private void setupImage(Contact contact) {
        ImageView iv = (ImageView) findViewById(R.id.profileView);
        if (contact.getImageUri() != null) {
            Log.i("profile", contact.getImageUri());
            iv.setImageURI(null);
            iv.setImageURI(Uri.parse(contact.getImageUri()));
        } else {
            iv.setImageURI(null);
            iv.setImageResource(R.drawable.web_hi_res_512_blue);
        }
    }

    private void setupCheckboxList(Contact contact) {
        List<CheckboxInfo> checkboxList = createCheckList(contact);
        createCheckboxListView(checkboxList);
    }

    private List<CheckboxInfo> createCheckList(Contact contact) {
        List<CheckboxInfo> checkboxList = new ArrayList<>();

        String displayName = contact.getDisplay_name() == null ? NOT_SET : contact.getDisplay_name();
        String mobilePhone = contact.getMobile_phone() == null ? NOT_SET : contact.getMobile_phone();
        String homePhone = contact.getHome_phone() == null ? NOT_SET : contact.getHome_phone();
        String workPhone = contact.getWork_phone() == null ? NOT_SET : contact.getWork_phone();
        String email = contact.getEmail() == null ? NOT_SET : contact.getEmail();
        String company = contact.getCompany() == null ? NOT_SET : contact.getCompany();
        String title = contact.getJobTitle() == null ? NOT_SET : contact.getJobTitle();

        CheckboxInfo checkInfo = new CheckboxInfo(Constants.DISPLAY_NAME, displayName, contact.getDisplay_name() != null);
        checkboxList.add(checkInfo);
        checkInfo = new CheckboxInfo(Constants.MOBILE_PHONE, mobilePhone, contact.getMobile_phone() != null);
        checkboxList.add(checkInfo);
        checkInfo = new CheckboxInfo(Constants.HOME_PHONE, homePhone, contact.getHome_phone() != null);
        checkboxList.add(checkInfo);
        checkInfo = new CheckboxInfo(Constants.WORK_PHONE, workPhone, contact.getWork_phone() != null);
        checkboxList.add(checkInfo);
        checkInfo = new CheckboxInfo(Constants.EMAIL, email, contact.getEmail() != null);
        checkboxList.add(checkInfo);
        checkInfo = new CheckboxInfo(Constants.COMPANY, company, contact.getCompany() != null);
        checkboxList.add(checkInfo);
        checkInfo = new CheckboxInfo(Constants.TITLE, title, contact.getJobTitle() != null);
        checkboxList.add(checkInfo);

        return checkboxList;
    }


    private void createCheckboxListView(List<CheckboxInfo> checkboxList) {
        LinearLayout list = (LinearLayout)findViewById(R.id.profileList);
        list.removeAllViews();
        for (CheckboxInfo cbInfo : checkboxList) {
            View v = getLayoutInflater().inflate(R.layout.profile_line, null, false);

            CheckBox cb = (CheckBox) v.findViewById(R.id.profile_line_checkbox);
            cb.setChecked(cbInfo.isSelected());

            TextView tvKey = (TextView) v.findViewById(R.id.profile_line_key);
            tvKey.setText(cbInfo.getKey());

            TextView tvValue = (TextView) v.findViewById(R.id.profile_line_value);
            tvValue.setText(cbInfo.getValue());

            list.addView(v);
        }
    }

    private void retrieveProfile(Contact contact) {
        retrieveNameImageFromProfile(contact);
        retrievePhoneFromProfile(contact);
        retrieveEmailFromProfile(contact);
        retrieveOrganizationFromProfile(contact);
    }

    private void retrieveNameImageFromProfile(Contact contact) {
        Cursor c = getApplication().getContentResolver().query(
                Uri.withAppendedPath(
                        ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
                new String[] {
                        ContactsContract.Profile.DISPLAY_NAME,
                        ContactsContract.Profile.PHOTO_THUMBNAIL_URI,
                        ContactsContract.Profile._ID
                },
                ContactsContract.Contacts.Data.MIMETYPE + " = ?",
                new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE},
                null);

        if (c != null) {
            if (c.moveToFirst()) {
                contact.setDisplay_name(c.getString(c.getColumnIndex(ContactsContract.Profile.DISPLAY_NAME)));
                Log.i("profile", "-- " + c.getString(c.getColumnIndex(ContactsContract.Profile.PHOTO_THUMBNAIL_URI)));
                contact.setImageUri(c.getString(c.getColumnIndex(ContactsContract.Profile.PHOTO_THUMBNAIL_URI)));
                contact.setContactID(c.getString(c.getColumnIndex(ContactsContract.Profile._ID)));
            }
            c.close();
        }
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

        if (c != null) {
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

        if (c != null) {
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

        if (c != null) {
            if (c.moveToFirst()) {
                contact.setCompany(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY)));
                contact.setJobTitle(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE)));
            }
            c.close();
        }
    }

    public void editProfile() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(ContactsContract.Profile.CONTENT_URI);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_profile_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_edit_button:
                editProfile();
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                break;
        }
        return true;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        PendingIntent pendingIntent = PendingIntent.getActivity( this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, pendingIntent, null, null);

        displayProfile();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);
    }
}