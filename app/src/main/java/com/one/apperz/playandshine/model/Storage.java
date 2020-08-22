package com.one.apperz.playandshine.model;

import android.content.Intent;

import com.google.firebase.firestore.DocumentSnapshot;
import com.one.apperz.playandshine.LoginActivity;
import com.one.apperz.playandshine.MainActivity;
import com.one.apperz.playandshine.helperLord.HelperLordConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class Storage {

    ArrayList<ChatsItemModel> chatsList;
    ArrayList<Request> requestAccepted;
    ArrayList<String> connectedProfs;
    ArrayList<UserProfile> requestSent;
    Map<String, ArrayList<String>> list;

    public Storage() {
    }

    public Storage(ArrayList<ChatsItemModel> chatsList, ArrayList<Request> requestAccepted, ArrayList<String> connectedProfs, ArrayList<UserProfile> requestSent, Map<String, ArrayList<String>> list) {
        this.chatsList = chatsList;
        this.requestAccepted = requestAccepted;
        this.connectedProfs = connectedProfs;
        this.requestSent = requestSent;
        this.list = list;
    }

    public ArrayList<ChatsItemModel> getChatsList() {
        return chatsList;
    }

    public void setChatsList(ArrayList<ChatsItemModel> chatsList) {
        this.chatsList = chatsList;
    }

    public ArrayList<Request> getRequestAccepted() {
        return requestAccepted;
    }

    public void setRequestAccepted(ArrayList<Request> requestAccepted) {
        this.requestAccepted = requestAccepted;
    }

    public ArrayList<String> getConnectedProfs() {
        return connectedProfs;
    }

    public void setConnectedProfs(ArrayList<String> connectedProfs) {
        this.connectedProfs = connectedProfs;
    }

    public ArrayList<UserProfile> getRequestSent() {
        return requestSent;
    }

    public void setRequestSent(ArrayList<UserProfile> requestSent) {
        this.requestSent = requestSent;
    }

    public Map<String, ArrayList<String>> getList() {
        return list;
    }

    public void setList(Map<String, ArrayList<String>> list) {
        this.list = list;
    }
}
