package com.example.InstaSOS.ui.contacts;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.InstaSOS.ContactList;
import com.example.InstaSOS.ContactsViewHolder;

public class ContactsAdapter extends ListAdapter<ContactList, ContactsViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(ContactList contactList);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public ContactsAdapter(DiffUtil.ItemCallback<ContactList> diffCallback) {
        super(diffCallback);
    }

    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ContactsViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, int position) {
        ContactList contact = getContactAt(position);
        //TODO: Review contact viewmodel
        holder.bind(contact);
    }

    public ContactList getContactAt(int position) {
       return getItem(position);
    }


    static class ContactListDiff extends DiffUtil.ItemCallback<ContactList> {

        @Override
        public boolean areItemsTheSame(@NonNull ContactList oldItem, @NonNull ContactList newItem) {
            return oldItem.getId() == newItem.getId();
        }
        @Override
        public boolean areContentsTheSame(@NonNull ContactList oldItem, @NonNull ContactList newItem) {
            return oldItem.getFirstName().equals(newItem.getFirstName()) &&
                    oldItem.getLastName().equals(newItem.getLastName()) &&
                    oldItem.getPhoneNumber().equals(newItem.getPhoneNumber());
        }
    }
}
