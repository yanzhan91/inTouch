package com.yzdevelopment.inTouch;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

public class ProfileAccessObject {

    private static ProfileAccessObject instance = null;

    private ProfileAccessObject() {}

    public static Contact getProfile(Context context) {
        if (instance == null) {
            instance = new ProfileAccessObject();
        }
        return instance.retrieveProfile(context);
    }

    private Contact retrieveProfile(Context context) {
        Contact contact = new Contact();

        retrieveNameImageFromProfile(contact, context);
        retrievePhoneFromProfile(contact, context);
        retrieveEmailFromProfile(contact, context);
        retrieveOrganizationFromProfile(contact, context);

        return contact;
    }

    private void retrieveNameImageFromProfile(Contact contact, Context context) {
        Cursor c = context.getContentResolver().query(
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

    private void retrievePhoneFromProfile(Contact contact, Context context) {
        Cursor c = context.getContentResolver().query(
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

    private void retrieveEmailFromProfile(Contact contact, Context context) {
        Cursor c = context.getContentResolver().query(
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

    private void retrieveOrganizationFromProfile(Contact contact, Context context) {
        Cursor c = context.getContentResolver().query(
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
}
