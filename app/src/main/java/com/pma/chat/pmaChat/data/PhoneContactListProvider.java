package com.pma.chat.pmaChat.data;


import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.content.CursorLoader;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.pma.chat.pmaChat.model.PhoneContact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PhoneContactListProvider {

    private final Context context;

    private static final String[] CONTACTS_PROJECTION = new String[]{
            Contacts._ID,
            Contacts.LOOKUP_KEY,
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    Contacts.DISPLAY_NAME_PRIMARY :
                    Contacts.DISPLAY_NAME
    };

    // The column index for the _ID column
    private static final int CONTACT_ID_INDEX = 0;
    // The column index for the LOOKUP_KEY column
    private static final int CONTACT_LOOKUP_KEY_INDEX = 1;
    // The column index for the DISPLAY_NAME column
    private static final int CONTACT_NAME_INDEX = 2;


    private static final String[] PHONE_PROJECTION = new String[]{
            Phone.NUMBER,
            Phone.CONTACT_ID,
    };

    // The column index for the NUMBERcolumn
    private static final int PHONE_NUMBER_INDEX = 0;
    // The column index for the CONTACT_ID column
    private static final int PHONE_CONTACT_ID_INDEX = 1;

    public PhoneContactListProvider(Context context) {
        this.context = context;
    }

    public ArrayList<PhoneContact> fetchAll() {

        CursorLoader cursorLoader = new CursorLoader(context,
                Contacts.CONTENT_URI,
                CONTACTS_PROJECTION, // the columns to retrieve
                null, // the selection criteria (none)
                null, // the selection args (none)
                null // the sort order (default)
        );

        Cursor cursor = cursorLoader.loadInBackground();

        final Map<Long, PhoneContact> contactsMap = new HashMap<>(cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                Long contactId = Long.parseLong(cursor.getString(CONTACT_ID_INDEX));
                String contactLookupKey = cursor.getString(CONTACT_LOOKUP_KEY_INDEX);
                String contactDisplayName = cursor.getString(CONTACT_NAME_INDEX);
                PhoneContact contact = new PhoneContact(contactId, contactLookupKey, contactDisplayName);
                contactsMap.put(contactId, contact);
            } while (cursor.moveToNext());
        }

        cursor.close();

        matchContactNumbers(contactsMap);

        return new ArrayList<>(contactsMap.values());
    }


    public void matchContactNumbers(Map<Long, PhoneContact> contactsMap) {

        Cursor cursor = new CursorLoader(context,
                Phone.CONTENT_URI,
                PHONE_PROJECTION,
                null,
                null,
                null).loadInBackground();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                final String number = cursor.getString(PHONE_NUMBER_INDEX);
                final Long contactId = Long.parseLong(cursor.getString(PHONE_CONTACT_ID_INDEX));
                PhoneContact contact = contactsMap.get(contactId);
                if (contact != null) {
                    contact.setPhoneNumber(number);
                    cursor.moveToNext();
                }
            }
        }

        cursor.close();
    }
}
