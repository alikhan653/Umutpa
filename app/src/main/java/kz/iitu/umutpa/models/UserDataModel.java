package kz.iitu.umutpa.models;

import androidx.annotation.Keep;

@Keep
public class UserDataModel {
    public String name, dob, email, imageUrl,role;

    public UserDataModel(String name, String dob, String email, String imageUrl, String role) {

        this.name = name;
        this.dob = dob;
        this.email = email;
        this.imageUrl = imageUrl;
        this.role = role;
    }

    public UserDataModel() {
        //This is needed for getting data
    }
}
