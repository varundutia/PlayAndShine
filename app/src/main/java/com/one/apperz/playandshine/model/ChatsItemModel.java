package com.one.apperz.playandshine.model;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.Timestamp;
import java.util.Date;

public class ChatsItemModel implements Parcelable {

    private String name;
    private String photoURL;
    private String lastMessage;
    private String uid;
    private Timestamp timestamp;
    private String type;
private boolean newMessage;

    public ChatsItemModel() {
    }

    public ChatsItemModel(String name, String photoURL, String lastMessage, String uid, Timestamp timestamp,String type, boolean newMessage) {
        this.name = name;
        this.photoURL = photoURL;
        this.lastMessage = lastMessage;
        this.uid = uid;
        this.timestamp = timestamp;
        this.type = type;
        this.newMessage = newMessage;
    }

    protected ChatsItemModel(Parcel in) {
        name = in.readString();
        photoURL = in.readString();
        lastMessage = in.readString();
        uid = in.readString();
        type = in.readString();
        timestamp = new Timestamp(new Date());
    }



    public static final Creator<ChatsItemModel> CREATOR = new Creator<ChatsItemModel>() {
        @Override
        public ChatsItemModel createFromParcel(Parcel in) {
            return new ChatsItemModel(in);
        }

        @Override
        public ChatsItemModel[] newArray(int size) {
            return new ChatsItemModel[size];
        }
    };

    public Timestamp getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhotoURL() {
        return photoURL;
    }
    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }
    public String getLastMessage() {
        return lastMessage;
    }
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public boolean isNewMessage() {
        return newMessage;
    }

    public void setNewMessage(boolean newMessage) {
        this.newMessage = newMessage;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(photoURL);
        parcel.writeString(lastMessage);
        parcel.writeString(uid);
        parcel.writeString(type);
    }


}
