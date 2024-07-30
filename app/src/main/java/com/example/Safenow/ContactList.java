package com.example.Safenow;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ContactList {
    @PrimaryKey(autoGenerate = true)
    private int id;

    public String firstName;
    public String lastName;
    public String phoneNumber;
    private boolean isDefault;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ContactList(String firstName, String lastName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.isDefault = false;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public boolean isDefault() {return isDefault;}
}
