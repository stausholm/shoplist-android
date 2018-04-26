package org.projects.shoppinglist;


import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

    String name;
    int quantity;
    String variant;

    public Product() {} //Empty constructor we will need later!

    public Product(String name, int quantity, String variant)
    {
        this.name = name;
        this.quantity = quantity;
        this.variant = variant;
    }

    @Override
    public String toString() {
        return name+" "+quantity+" "+variant;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(quantity);
        dest.writeString(variant);
    }

    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    // "De-parcel object
    public Product(Parcel in) {
        name = in.readString();
        quantity = in.readInt();
        variant = in.readString();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }
}

