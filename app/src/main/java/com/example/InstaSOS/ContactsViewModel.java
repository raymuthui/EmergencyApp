package com.example.InstaSOS;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ContactsViewModel extends AndroidViewModel {

    private ContactsRepository mRepository;

    private final LiveData<List<ContactList>> allContacts;

    public ContactsViewModel (Application application) {
        super(application);
        mRepository = new ContactsRepository(application);
        allContacts = mRepository.getAllContacts();
    }

    public LiveData<List<ContactList>> getAllContacts() { return allContacts; }

    public void insert(ContactList contacts) { mRepository.insert(contacts); }
    public void update(ContactList contacts) { mRepository.update(contacts); }
    public void delete(ContactList contacts) { mRepository.delete(contacts); }
}
