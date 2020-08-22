package com.one.apperz.playandshine.helperLord;

import android.content.Context;

import com.one.apperz.playandshine.R;
import com.one.apperz.playandshine.model.ChatsItemModel;
import com.one.apperz.playandshine.model.Message;
import com.one.apperz.playandshine.model.Request;
import com.one.apperz.playandshine.model.UserProfile;

import java.util.ArrayList;

import io.paperdb.Paper;

public class HelperLordFunctions {

    public static UserProfile getMeUserProfile(Context context) {
        Paper.init(context);
        return Paper.book().read(context.getResources().getString(R.string.users_collection), new UserProfile());
    }

    public static ArrayList<String> getMeListOfRequest(Context context, String SELECTED_PROFESSIONAL) {
        Paper.init(context);
        return Paper.book("requests").read(SELECTED_PROFESSIONAL, new ArrayList<String>());
    }

    public static ArrayList<String> getProfessionalConnected(Context context) {
        Paper.init(context);
        return Paper.book("requests").read("professionalConnected", new ArrayList<String>());
    }

    public static ArrayList<UserProfile> getRequestsSent(Context context) {
        Paper.init(context);
        return Paper.book("requests").read("requestsSent", new ArrayList<UserProfile>());

    }

    public static ArrayList<Request> getRequestsAccepted(Context context) {
        Paper.init(context);
        return Paper.book("requests").read("requestsAccepted", new ArrayList<Request>());

    }

    public static ArrayList<ChatsItemModel> getChatsList(Context context) {

        Paper.init(context);
        return Paper.book("chats").read("listOfChats", new ArrayList<ChatsItemModel>());

    }


}
