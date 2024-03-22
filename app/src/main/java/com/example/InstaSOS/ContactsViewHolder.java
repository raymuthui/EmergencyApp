package com.example.InstaSOS;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ContactsViewHolder extends RecyclerView.ViewHolder {

    private final TextView firstName;
    private final TextView lastName;
    private final TextView phoneNumber;

    public ContactsViewHolder(@NonNull View itemView) {
        super(itemView);
        firstName = itemView.findViewById(R.id.idTVfirstName);
        lastName = itemView.findViewById(R.id.idTVlastName);
        phoneNumber = itemView.findViewById(R.id.idTVphoneNumber);
    }

    //TODO: fix binding issues
    public void bind(ContactList contact) {
        firstName.setText(contact.getFirstName());
        lastName.setText(contact.getLastName());
        phoneNumber.setText(contact.getPhoneNumber());
    }

    public static ContactsViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ContactsViewHolder(view);
    }
}
