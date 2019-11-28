package com.novance.novance;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.storage.StorageReference;

public class User implements Parcelable {
    String email;
    String password;
    String username;
    String fullName;
    String id;
    String profileImageUri;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User() {

    }

    protected User(Parcel in) {
        email = in.readString();
        password = in.readString();
        username = in.readString();
        fullName = in.readString();
        id = in.readString();
        String temp = in.readString();
        if (temp != null) {
            profileImageUri = temp;
        } else {
            profileImageUri = null;
        }
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setProfileImageUri(Uri imageUri) {
        profileImageUri = imageUri.toString();
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getId() {
        return id;
    }

    public Uri getProfileImageUri() {
        if (profileImageUri != null)
            return Uri.parse(profileImageUri);
        else
            return null;
    }

    @Override
    public String toString() {
        String s = id + "\n" + email + "\n" + password + "\n" + fullName + "\n" + username + "\n" + profileImageUri;
        return s;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(username);
        dest.writeString(fullName);
        dest.writeString(id);
        if (profileImageUri != null) {
            dest.writeString(profileImageUri);
        } else {
            dest.writeString(null);
        }
    }
}
