package com.yzdevelopment.inTouch.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
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

import com.yzdevelopment.inTouch.model.CheckboxInfo;
import com.yzdevelopment.inTouch.Constants;
import com.yzdevelopment.inTouch.model.Contact;
import com.yzdevelopment.inTouch.ProfileAccessObject;
import com.yzdevelopment.inTouch.R;

import java.util.ArrayList;
import java.util.List;

public class ProfileInfo extends ActionBarActivity {

    public static final String NOT_SET = "Not Set";
    private List<CheckboxInfo> checkboxList;

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
        Contact contact = ProfileAccessObject.getProfile(getApplicationContext());
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
        checkboxList = new ArrayList<>();

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
//                NavUtils.navigateUpFromSameTask(this);
                Intent resultIntent = new Intent();
                for (CheckboxInfo cb : checkboxList) {
                    if (cb.isSelected() && !cb.getValue().equals(NOT_SET)) {
                        resultIntent.putExtra(cb.getKey(), cb.getValue());
                    }
                }
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
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