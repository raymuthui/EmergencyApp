package com.example.InstaSOS;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

class ContactsRepository {
    private ContactListDao contactListDao;
    private LiveData<List<ContactList>> allContacts;

    ContactsRepository(Application application) {
        InstaSOSRoomDatabase db = InstaSOSRoomDatabase.getDatabase(application);
        contactListDao = db.contactListDao();
        allContacts = contactListDao.getAlphabetizedContacts();
    }

    LiveData<List<ContactList>> getAllContacts() {
            return allContacts;
    }

    void insert(ContactList contactList) {
        InstaSOSRoomDatabase.databaseWriteExecutor.execute(() -> contactListDao.insert(contactList));
    }

    void update(ContactList contactList) {
        InstaSOSRoomDatabase.databaseWriteExecutor.execute(() -> contactListDao.update(contactList));
    }

    void delete(ContactList contactList) {
        InstaSOSRoomDatabase.databaseWriteExecutor.execute(() -> contactListDao.delete(contactList));
    }

}
