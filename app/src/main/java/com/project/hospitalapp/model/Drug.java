package com.project.hospitalapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Drug implements Parcelable {
    public static String itemNameText;
    public String entpNameText;
    public int itemSeqText;
    public String efcyQesitmText;
    public String useMethodQesitmText;
    public String atpnWarnQesitmText;
    public String atpnQesitmText;
    public String depositMethodQesitmText;
    public String itemImageUrl;
    public String intrcQesitmText;

    public Drug() {
        // 기본 생성자
    }

    // Parcelable 인터페이스 구현
    protected Drug(Parcel in) {
        itemNameText = in.readString();
        entpNameText = in.readString();
        itemSeqText = in.readInt();
        efcyQesitmText = in.readString();
        useMethodQesitmText = in.readString();
        atpnWarnQesitmText = in.readString();
        atpnQesitmText = in.readString();
        depositMethodQesitmText = in.readString();
        itemImageUrl = in.readString();
        intrcQesitmText = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemNameText);
        dest.writeString(entpNameText);
        dest.writeInt(itemSeqText);
        dest.writeString(efcyQesitmText);
        dest.writeString(useMethodQesitmText);
        dest.writeString(atpnWarnQesitmText);
        dest.writeString(atpnQesitmText);
        dest.writeString(depositMethodQesitmText);
        dest.writeString(itemImageUrl);
        dest.writeString(intrcQesitmText);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Drug> CREATOR = new Creator<Drug>() {
        @Override
        public Drug createFromParcel(Parcel in) {
            return new Drug(in);
        }

        @Override
        public Drug[] newArray(int size) {
            return new Drug[size];
        }
    };
}
