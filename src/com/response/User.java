package com.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by C-ShellWin on 12/11/2014.
 */
public class User implements Parcelable {

    public String id ,firstName, lastName, firstNameInOtherLang, lastNameInOtherlang, designation, comapnyName , userEmailAddress, companyEmailAddress, website, address, city, country, mobile, telephone, fax, countryCode, messanger, userPicUrl, companyPicUrl, bussinessPicUrl, qrcodeUrl, listyouid, listyou_date;

    /**
     * A constructor that initializes the User object
     */

    public User(String id ,String firstName, String lastName, String firstNameInOtherLang, String lastNameInOtherlang, String designation, String comapnyName, String userEmailAddress, String companyEmailAddress, String website, String address, String city, String country, String mobile, String telephone, String fax, String countryCode, String messanger, String userPicUrl, String companyPicUrl, String bussinessPicUrl, String qrcodeUrl, String listyouid, String listyou_date) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.firstNameInOtherLang = firstNameInOtherLang;
        this.lastNameInOtherlang = lastNameInOtherlang;
        this.designation = designation;
        this.comapnyName = comapnyName;
        this.userEmailAddress = userEmailAddress;
        this.companyEmailAddress = companyEmailAddress;
        this.website = website;
        this.address = address;
        this.city = city;
        this.country = country;
        this.mobile = mobile;
        this.telephone = telephone;
        this.fax = fax;
        this.countryCode = countryCode;
        this.messanger = messanger;
        this.userPicUrl = userPicUrl;
        this.companyPicUrl = companyPicUrl;
        this.bussinessPicUrl = bussinessPicUrl;
        this.qrcodeUrl = qrcodeUrl;
        this.listyouid = listyouid;
        this.listyou_date = listyou_date;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Storing the User data to Parcel object
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(firstNameInOtherLang);
        dest.writeString(lastNameInOtherlang);
        dest.writeString(designation);
        dest.writeString(comapnyName);
        dest.writeString(userEmailAddress);
        dest.writeString(companyEmailAddress);
        dest.writeString(website);
        dest.writeString(address);
        dest.writeString(city);
        dest.writeString(country);
        dest.writeString(mobile);
        dest.writeString(telephone);
        dest.writeString(fax);
        dest.writeString(countryCode);
        dest.writeString(messanger);
        dest.writeString(userPicUrl);
        dest.writeString(companyPicUrl);
        dest.writeString(bussinessPicUrl);
        dest.writeString(qrcodeUrl);
        dest.writeString(listyouid);
        dest.writeString(listyou_date);
    }

    /**
     * Retrieving User data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of
     * the object CREATOR
     */

    public User(Parcel in) {
        this.id = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.firstNameInOtherLang = in.readString();
        this.lastNameInOtherlang = in.readString();
        this.designation = in.readString();
        this.comapnyName =  in.readString();
        this.userEmailAddress = in.readString();
        this.companyEmailAddress = in.readString();
        this.website = in.readString();
        this.address = in.readString();
        this.city = in.readString();
        this.country = in.readString();
        this.mobile = in.readString();
        this.telephone = in.readString();
        this.fax = in.readString();
        this.countryCode = in.readString();
        this.messanger = in.readString();
        this.userPicUrl = in.readString();
        this.companyPicUrl = in.readString();
        this.bussinessPicUrl = in.readString();
        this.qrcodeUrl =  in.readString();
        this.listyouid = in.readString();
        this.listyou_date = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
