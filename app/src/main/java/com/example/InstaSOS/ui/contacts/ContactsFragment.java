package com.example.InstaSOS.ui.contacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.InstaSOS.ContactList;
import com.example.InstaSOS.ContactsViewModel;
import com.example.InstaSOS.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ContactsFragment extends Fragment{
    private static final int ADD_CONTACT_REQUEST = 1;
    private static final int EDIT_CONTACT_REQUEST = 2;

    private ContactsViewModel contactsViewModel;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contacts, container, false);

        // Initialize UI components
        RecyclerView recyclerView = root.findViewById(R.id.contactsRecyclerView);
        FloatingActionButton fab = root.findViewById(R.id.FABAdd);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), NewContactActivity.class);
            startActivityForResult(intent, ADD_CONTACT_REQUEST);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);

        final ContactsAdapter adapter = new ContactsAdapter(new ContactsAdapter.ContactListDiff());
        recyclerView.setAdapter(adapter);

        contactsViewModel = new ViewModelProvider(this).get(ContactsViewModel.class);

        //TODO: check this
        contactsViewModel.getAllContacts().observe(getViewLifecycleOwner(), adapter::submitList);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                contactsViewModel.delete(adapter.getContactAt(viewHolder.getAdapterPosition()));
                Toast.makeText(requireContext(), "Contact deleted", Toast.LENGTH_SHORT).show();
            }
        }).
                attachToRecyclerView(recyclerView);
        adapter.setOnItemClickListener(contactList -> {
            Intent intent = new Intent(requireContext(), NewContactActivity.class);
            intent.putExtra(NewContactActivity.EXTRA_ID, contactList.getId());
            intent.putExtra(NewContactActivity.EXTRA_FIRST_NAME, contactList.getFirstName());
            intent.putExtra(NewContactActivity.EXTRA_LAST_NAME, contactList.getLastName());
            intent.putExtra(NewContactActivity.EXTRA_PHONE_NUMBER, contactList.getPhoneNumber());

            startActivityForResult(intent, EDIT_CONTACT_REQUEST);

        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            String FirstName = data.getStringExtra(NewContactActivity.EXTRA_FIRST_NAME);
            String LastName = data.getStringExtra(NewContactActivity.EXTRA_LAST_NAME);
            String PhoneNUmber = data.getStringExtra(NewContactActivity.EXTRA_PHONE_NUMBER);
            ContactList model = new ContactList(FirstName, LastName, PhoneNUmber);
            contactsViewModel.insert(model);
            Toast.makeText(requireContext(), "CONTACT saved", Toast.LENGTH_SHORT).show();
        } else if (requestCode == EDIT_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            int id = data.getIntExtra(NewContactActivity.EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(requireContext(), "CONTACT can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }
            String FirstName = data.getStringExtra(NewContactActivity.EXTRA_FIRST_NAME);
            String LastName = data.getStringExtra(NewContactActivity.EXTRA_LAST_NAME);
            String PhoneNumber = data.getStringExtra(NewContactActivity.EXTRA_PHONE_NUMBER);
            ContactList model = new ContactList(FirstName, LastName, PhoneNumber);
            model.setId(id);
            contactsViewModel.update(model);
            Toast.makeText(requireContext(), "CONTACT updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "CONTACT not saved", Toast.LENGTH_SHORT).show();
        }
    }
}
