package com.one.apperz.playandshine;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.one.apperz.playandshine.databinding.ActivityMainBinding;
import com.one.apperz.playandshine.helperLord.HelperLordFunctions;
import com.one.apperz.playandshine.model.ChatsItemModel;
import com.one.apperz.playandshine.model.Message;
import com.one.apperz.playandshine.model.Request;
import com.one.apperz.playandshine.model.UserProfile;
import com.one.apperz.playandshine.model.Walkthrough;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

//    public static void setFontAs(String font, TextView textView, Context context) {
//        AssetManager assetManager = context.getApplicationContext().getAssets();
//        Typeface typeface = Typeface.createFromAsset(assetManager, String.format(Locale.US, "fonts/%s", font));
//        textView.setTypeface(typeface);
//    }
//
//    public static void setFontAs(String font, Button button, Context context) {
//        AssetManager assetManager = context.getApplicationContext().getAssets();
//        Typeface typeface = Typeface.createFromAsset(assetManager, String.format(Locale.US, "fonts/%s", font));
//        button.setTypeface(typeface);
//    }

    Context context;
    ActivityMainBinding b;
    FirebaseUser currentUser;
    CollectionReference chatsRef;
    ArrayList<UserProfile> requestsSent;
    ArrayList<Request> requestsAccepted;
    ArrayList<ChatsItemModel> chats;
    CustomAdapter adapter;
    private boolean searchF = false;
    private boolean profileEditF = false;
    private boolean connectF = false;
    private boolean faq = false;
    SharedPreferences.Editor editor;
    HashSet<String> chatIds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        View v = b.getRoot();
        setContentView(v);
        context = this;
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.swipe);
        Paper.init(context);
        chats=new ArrayList<>();
        chatIds=new HashSet<>();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(pref != null) {
            profileEditF = pref.getBoolean(getResources().getString(R.string.profileEditF), true);
            searchF = pref.getBoolean(getResources().getString(R.string.searchF), true);
            faq = pref.getBoolean("faq",true);
            connectF = pref.getBoolean("connectF",true);
            editor = pref.edit();
        }

//        filterChats(chats);


        //            new Walkthrough(view,MainActivity.this,"Connect with Others",);
//
//        if (profileEditF){
//            new Walkthrough(findViewById(R.id.buttonRequests), MainActivity.this, "Edit Profile", "You can add an avatar and make other changes to your profile.");
//            if(editor != null) {
//                editor.putBoolean(getResources().getString(R.string.profileEditF), false);
//                editor.commit();
//                profileEditF = false;
//            }
//        }
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        // Build prompt dialog
//        builder.setTitle("Welcome to the USC Residential Experience App")
//                .setMessage("Would you like to view a brief tutorial on how to use the app?")
//                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // do something if user clicked YES
////                        Intent intent = new Intent(MainActivity.this, TutorialActivity.class);
////                        startActivity(intent);
//                    }
//                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                })
//                .show();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth != null) {
            currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
            else {
                FirebaseMessaging.getInstance().subscribeToTopic(currentUser.getUid());
            }
        }
        final UserProfile userProfile = Paper.book().read(context.getResources().getString(R.string.users_collection), new UserProfile());

//        if(searchF && userProfile != null && userProfile.getType().equals("athlete")) {
//            searchF = walkthrough(searchF, R.id.buttonSearch, R.string.searchF, "Connect with Others", "Press this button and have a look at the different categories of people you can connect to.");
//            if(editor != null){
//                searchF=true;
//                editor.putBoolean(getResources().getString(R.string.searchF),true);
//                editor.commit();
//            }
//        }
//        if (userProfile != null && userProfile.getType().equals("athlete")) {
//            searchF = walkthrough(searchF, R.id.buttonSearch, R.string.searchF, "Connect with Others", "Press this button and have a look at the different categories of people you can connect to.");
//        }

//        chats = HelperLordFunctions.getChatsList(context);
        adapter = new CustomAdapter(chats);
        b.listChats.setAdapter(adapter);
        b.listChats.setEmptyView(b.noChat);
        renderUI();
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                chats.clear();
                chatIds.clear();
                renderUI();
                pullToRefresh.setRefreshing(false);
            }
        });

    }

    boolean walkthrough(boolean flag,int view_id,int string_id,String title,String body){
        if (flag){
            new Walkthrough(findViewById(view_id), MainActivity.this, title, body);
            if(editor != null) {
                editor.putBoolean(getResources().getString(string_id), false);
                editor.commit();
                return false;
            }else{
                return true;
            }
        }
        return flag;
    }

    private void renderUI() {

        final UserProfile userProfile = Paper.book().read(context.getResources().getString(R.string.users_collection), new UserProfile());
        if (userProfile != null) {
            if (userProfile.getType().equals("athlete")) {
                searchF = walkthrough(searchF, R.id.nav_connect_image, R.string.searchF, "Connect with Others", "Press this button and have a look at the different categories of people you can connect to.");

                b.buttonRequests.setVisibility(View.GONE);
                requestsSent = HelperLordFunctions.getRequestsSent(context);
                b.actionsButton.setText("Search Professional to start connecting...");


                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                if (firebaseFirestore != null) {
                    firebaseFirestore.collection(context.getString(R.string.requests_collection))
                            .whereEqualTo("uidAthlete", userProfile.getUid())
                            .whereEqualTo("status", "accepted")
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d("chatstask", "achieved");
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("chats", "entered");
                                    Request chat = document.toObject(Request.class);
                                    String uid = chat.getUidProfessional();
//                                    CollectionReference c = (CollectionReference) document.get("messages");
//                                    Task<QuerySnapshot> task1 = c.get();
//                                    Message message=new Message();
//                                    for(QueryDocumentSnapshot d:task1.getResult()){
//                                        message=d.toObject(Message.class);
//                                        Log.d("messageVarun",message.getBody());
//                                    }
//                                    final Message m=message;
//                                    for (UserProfile user : requestsSent) {
//                                        if (request.getUidAthlete().equals("") && request.getUidProfessional().equals("")) {
//                                            break;
//                                        }
//                                        if (request.getUidAthlete().equals(user.getUid())) {//add athlete uid
//                                            item = new ChatsItemModel(user.getName(), user.getPhotoURL(),
//                                                    "New Chat", user.getUid(),
//                                                    (Timestamp) new Timestamp(new Date()),
//                                                    user.getType(), true);
////                                            ArrayList<String> profs = HelperLordFunctions.getProfessionalConnected(context);
////                                            profs.add(user.getType());
////                                            Paper.book("requests").write("athletesConnected", profs);
//                                            chats.add(item);
//                                            break;
//                                        }
//                                    }
                                    FirebaseFirestore.getInstance().collection(context.getResources().getString(R.string.users_collection)).document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                UserProfile user = task.getResult().toObject(UserProfile.class);
                                                ChatsItemModel item = new ChatsItemModel();
                                                item = new ChatsItemModel(user.getName(), user.getPhotoURL(),
                                                        "New Chat", user.getUid(),
                                                        new Timestamp(new Date()),
                                                        user.getType(), true);
//                                                chats.add(item);
                                                updateChatWithTime(item,userProfile);

                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("exceptionChat", e.getMessage());
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        }
//                                    } {
//                                        @Override
//                                        public void onSuccess(QuerySnapshot documentSnapshot) {
//                                            for (DocumentSnapshot doc: documentSnapshot){
//
//                                            }
//                                        }
                                    });


//                                    document.getReference().delete();
                                }
//                                updateChatsWithTime(chats);
//                                filterChats(chats);
//
//                                if (adapter != null) {
////                                    Paper.book("chats").write("listOfChats", chats);
//                                    adapter.refreshData(chats);
////                                    adapter.notifyDataSetChanged();
//                                }

                                //IDHAR ADD GET REQUEST WITH STATUS AS ENDTALK AND AGAIN RENDR CHATS LIST
//

                            }
                        }
                    });
                }

            } else {
                b.buttonSearch.setVisibility(View.GONE);
//            requestsAccepted = HelperLordFunctions.getRequestsAccepted(context);
//            for (Request request : requestsAccepted) {
//                ChatsItemModel item = new ChatsItemModel(request.getName(), request.getPhotoURL(),
//                        "New Chat", request.getUidAthlete(), new Timestamp(new Date()), "athlete");
//                chats.add(item);
//            }
                connectF = walkthrough(connectF, R.id.nav_request_image, R.string.connectF, "Accept a request", "Press this button and have a look at the different people who are trying to connect to you.");

                b.actionsButton.setText("Check Pending Request to start connecting...");
                requestsSent = HelperLordFunctions.getRequestsSent(context);
                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                if (firebaseFirestore != null) {
                    firebaseFirestore.collection(context.getString(R.string.requests_collection))
                            .whereEqualTo("uidProfessional", userProfile.getUid())
                            .whereEqualTo("status", "accepted")
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d("chatstask", "achieved");
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("chats", "entered");
                                    Request chat = document.toObject(Request.class);
                                    String uid = chat.getUidAthlete();
//                                    CollectionReference c = (CollectionReference) document.get("messages");
//                                    Task<QuerySnapshot> task1 = c.get();
//                                    Message message=new Message();
//                                    for(QueryDocumentSnapshot d:task1.getResult()){
//                                        message=d.toObject(Message.class);
//                                        Log.d("messageVarun",message.getBody());
//                                    }
//                                    final Message m=message;
//                                    for (UserProfile user : requestsSent) {
//                                        if (request.getUidAthlete().equals("") && request.getUidProfessional().equals("")) {
//                                            break;
//                                        }
//                                        if (request.getUidAthlete().equals(user.getUid())) {//add athlete uid
//                                            item = new ChatsItemModel(user.getName(), user.getPhotoURL(),
//                                                    "New Chat", user.getUid(),
//                                                    (Timestamp) new Timestamp(new Date()),
//                                                    user.getType(), true);
////                                            ArrayList<String> profs = HelperLordFunctions.getProfessionalConnected(context);
////                                            profs.add(user.getType());
////                                            Paper.book("requests").write("athletesConnected", profs);
//                                            chats.add(item);
//                                            break;
//                                        }
//                                    }
                                    FirebaseFirestore.getInstance().collection(context.getResources().getString(R.string.users_collection)).document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                UserProfile user = task.getResult().toObject(UserProfile.class);
                                                ChatsItemModel item = new ChatsItemModel();
                                                item = new ChatsItemModel(user.getName(), user.getPhotoURL(),
                                                        "New Chat", user.getUid(),
                                                        new Timestamp(new Date()),
                                                        user.getType(), true);
                                                updateChatWithTime(item,userProfile);
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("exceptionChat", e.getMessage());
                                        }
                                    });


//                                    document.getReference().delete();
                                }
//                                updateChatsWithTime(chats);


                            }
                        }
                    });
                }
//                if (firebaseFirestore != null) {
//                    firebaseFirestore.collection(context.getString(R.string.requests_collection))
//                            .whereEqualTo("uidProfessional", userProfile.getUid())
//                            .whereEqualTo("status", "endTalk").get()
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//
//                                }
//                            }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                        @Override
//                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
//                                Request request = doc.toObject(Request.class);
//
////                                ArrayList<ChatsItemModel> chatsItemModelArrayList = HelperLordFunctions.getChatsList(context);
//                                for (int i = 0; i < chats.size(); i++) {
//                                    ChatsItemModel chatsItemModel = chats.get(i);
//                                    if (chatsItemModel.getUid().equals(request.getUidAthlete()+request.getUidProfessional()) && userProfile.getUid().equals(request.getUidProfessional())) {
//                                        chats.remove(i);
//                                        break;
//                                    }
//                                }
////                                doc.getReference().delete();
//
////                                Paper.book("chats").write("listOfChats", chatsItemModelArrayList);
////                                chats = chatsItemModelArrayList;
////                                adapter.notifyDataSetChanged();
////                                updateChatsWithTime(chats);
//                            }
//                        }
//                    });
//                }
//                adapter.refreshData(chats);
//                Paper.book("chats").write("listOfChats", chats);
//
//            }

//            else {
//                b.buttonSearch.setVisibility(View.GONE);
////            requestsAccepted = HelperLordFunctions.getRequestsAccepted(context);
////            for (Request request : requestsAccepted) {
////                ChatsItemModel item = new ChatsItemModel(request.getName(), request.getPhotoURL(),
////                        "New Chat", request.getUidAthlete(), new Timestamp(new Date()), "athlete");
////                chats.add(item);
////            }
//                connectF = walkthrough(connectF, R.id.nav_request_image, R.string.connectF, "Accept a request", "Press this button and have a look at the different people who are trying to connect to you.");
//
//                b.actionsButton.setText("Check Pending Request to start connecting...");
//
//
////            filterChats(chats);
//
//                if (adapter != null) {
//                    Paper.book("chats").write("listOfChats", chats);
//                    adapter.refreshData(chats);
//                    adapter.notifyDataSetChanged();
//                }
////
////            Paper.book("chats").write("listOfChats", chats);
//                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
//
//                if (firebaseFirestore != null) {
//                    firebaseFirestore.collection(context.getString(R.string.requests_collection))
//                            .whereEqualTo("uidProfessional", userProfile.getUid())
//                            .whereEqualTo("status", "endTalk").get()
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//
//                                }
//                            }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                        @Override
//                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
//                                Request request = doc.toObject(Request.class);
//
//                                ArrayList<ChatsItemModel> chatsItemModelArrayList = HelperLordFunctions.getChatsList(context);
//                                for (int i = 0; i < chatsItemModelArrayList.size(); i++) {
//                                    ChatsItemModel chatsItemModel = chatsItemModelArrayList.get(i);
//                                    if (chatsItemModel.getUid().equals(request.getUidAthlete()) && userProfile.getUid().equals(request.getUidProfessional())) {
//                                        chatsItemModelArrayList.remove(i);
//                                        break;
//                                    }
//                                }
//                                doc.getReference().delete();
//
//                                Paper.book("chats").write("listOfChats", chatsItemModelArrayList);
//                                chats = chatsItemModelArrayList;
//                                adapter.notifyDataSetChanged();
//                                updateChatsWithTime(chats);
//                            }
//                        }
//                    });
//                }
//
//                adapter.notifyDataSetChanged();
//
//            }
            }


//
//        chatsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Log.d("TAG", document.getId() + " => " + document.getData());
//
//                        if (document.getData().get("notifier").equals("New Chat")) {
//
//                            ChatsItemModel c = new ChatsItemModel();
//
//                            label:
//                            if (userProfile.getType().equals("athlete")) {
//                                for (UserProfile user : requestsSent) {
//                                    if (document.getId().equals(user.getUid())) {
//                                        c = new ChatsItemModel(user.getName(), user.getPhotoURL(),
//                                                "New Chat", user.getUid(), true,
//                                                (Timestamp) document.getData().get("timeStamp"),
//                                                user.getType());
//                                        break label;
//                                    }
//                                }
//                            } else {
//                                for (Request request : requestsAccepted) {
//                                    if (document.getId().equals(request.getUidAthlete())) {
//                                        c = new ChatsItemModel(request.getName(), request.getPhotoURL(),
//                                                "New Chat", request.getUidAthlete(), true,
//                                                (Timestamp) document.getData().get("timeStamp"),
//                                                "athlete");
//
//                                        break label;
//                                    }
//                                }
//                            }
//
//                            boolean isRead =  false;
//                            for (ChatsItemModel chat: chats){
//                                if (chat.getUid().equals(c.getUid())) {
//                                    isRead = true;
//                                    break;
//                                }
//                            }
//
//                            if(!isRead) {
//                                chats.add(c);
//                                Paper.book("chats").write("listOfChats", chats);
//                            }
//
//                        } else {
//                            for (ChatsItemModel chatsItemModel : chats) {
//                                if (chatsItemModel.getUid().equals(document.getId())) {
//                                    chatsItemModel.setNewMessage(true);
//                                    chatsItemModel.setLastMessage((String) document.getData().get("notifier"));
//                                    chatsItemModel.setTimestamp((Timestamp) document.getData().get("timeStamp"));
//                                }
//                            }
//                        }
//
//                    }
//
//
//                    Collections.sort(chats, new Comparator<ChatsItemModel>() {
//                        @Override
//                        public int compare(ChatsItemModel chatsItemModel, ChatsItemModel t1) {
//                            return chatsItemModel.getTimestamp().compareTo(t1.getTimestamp());
//                        }
//                    });
//                    for (ChatsItemModel c:chats)
//                        Log.d("TAG", "onClick: "+c.getType());
//
//                    adapter.notifyDataSetChanged();
//
//                } else {
//                    Log.d("TAG", "Error getting documents: ", task.getException());
//                }
//            }
//        });


        }
    }

    private void updateChatsWithTime(final ArrayList<ChatsItemModel> chats) {

        for(final ChatsItemModel chatsItemModel : chats) {
            String ref;
            Log.d("TAG", "getReference: " + chatsItemModel.getType());
            if (chatsItemModel.getType().equals("athlete"))
                ref = chatsItemModel.getUid() + FirebaseAuth.getInstance().getCurrentUser().getUid();
            else
                ref = FirebaseAuth.getInstance().getCurrentUser().getUid() + chatsItemModel.getUid();

            FirebaseFirestore.getInstance().collection(context.getString(R.string.chat_collection))
                    .document(ref)
                    .collection(context.getString(R.string.message_collection))
                    .orderBy("timestamp", Query.Direction.DESCENDING).limit(1).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                    Message message = documentSnapshot.toObject(Message.class);
                                    if (message.getTimestamp().getSeconds() > chatsItemModel.getTimestamp().getSeconds()) {
                                        chatsItemModel.setNewMessage(true);
                                        Log.d("TAG", "onComplete: " + documentSnapshot.toObject(Message.class).getBody());

//                                        filterChats(chats);

//                                        Paper.book("chats").write("listOfChats", chats);
//                                        adapter.refreshData(chats);

                                    }
                                    chatsItemModel.setTimestamp(message.getTimestamp());
                                    chatsItemModel.setLastMessage(message.getBody());
                                }
                            }
                        }
                    });
        }
    }
    private void updateChatWithTime(final ChatsItemModel chatsItemModel,UserProfile userProfile) {
            String ref,type;
            Log.d("TAG", "getReference: " + chatsItemModel.getType());
            if (chatsItemModel.getType().equals("athlete")) {
                ref = chatsItemModel.getUid() + userProfile.getUid();
                type = "uidProfessional";
            }
            else {
                ref = userProfile.getUid() + chatsItemModel.getUid();
                type = "uidAthlete";
            }
            FirebaseFirestore.getInstance().collection(context.getString(R.string.chat_collection))
                    .document(ref)
                    .collection(context.getString(R.string.message_collection))
                    .orderBy("timestamp", Query.Direction.DESCENDING).limit(1).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                    Message message = documentSnapshot.toObject(Message.class);
                                    if (message.getTimestamp().getSeconds() > chatsItemModel.getTimestamp().getSeconds()) {
                                        chatsItemModel.setNewMessage(true);
                                        Log.d("TAG", "onComplete: " + documentSnapshot.toObject(Message.class).getBody());

//                                        filterChats(chats);

//                                        Paper.book("chats").write("listOfChats", chats);
//                                        adapter.refreshData(chats);

                                    }
                                    chatsItemModel.setTimestamp(message.getTimestamp());
                                    chatsItemModel.setLastMessage(message.getBody());
                                }
                                if (chatIds.contains(chatsItemModel.getUid())) {
                                    chats.remove(chatsItemModel);
                                }
                                    chats.add(chatsItemModel);
                                    chatIds.add(chatsItemModel.getUid());
//                                filterChats(chats);
                                    Collections.sort(chats, new Comparator<ChatsItemModel>() {
                                        @Override
                                        public int compare(ChatsItemModel chatsItemModel, ChatsItemModel t1) {
                                            Log.d("Time",String.valueOf(chatsItemModel.getTimestamp())+" "+String.valueOf(t1.getTimestamp()));
                                            return (t1.getTimestamp().compareTo(chatsItemModel.getTimestamp()));
                                        }
                                    });
                                    removeEndedChats(userProfile,chats,type);
                                    if (adapter != null) {
                                        Paper.book("chats").write("listOfChats", chats);
                                        adapter.refreshData(chats);
    //                                    adapter.notifyDataSetChanged();
                                    }
                                }
                            }

                    });

        }
    void removeEndedChats(UserProfile userProfile,ArrayList<ChatsItemModel> chats,String type){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        if (firebaseFirestore != null) {
            firebaseFirestore.getInstance().collection(context.getString(R.string.requests_collection))
                    .whereEqualTo(type, userProfile.getUid())
                    .whereEqualTo("status", "endTalk")
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Request request = doc.toObject(Request.class);
//                                                ArrayList<ChatsItemModel> chatsItemModelArrayList = HelperLordFunctions.getChatsList(context);
                        String id = type.equals("uidAthlete")?request.getUidAthlete():request.getUidProfessional();
                        for (int i = 0; i < chats.size(); i++) {
                            ChatsItemModel chatsItemModel = chats.get(i);

                            if (chatsItemModel.getUid().equals(request.getUidAthlete()+request.getUidProfessional()) && userProfile.getUid().equals(id)) {
                                chats.remove(i);
                                break;
                            }
                        }
//                                                Paper.book("chats").write("listOfChats", chatsItemModelArrayList);

                        if (type.equals("uidAthlete")) {
                            Paper.book("requests").write(request.getType(), new ArrayList<String>());
                            ArrayList<String> procon = HelperLordFunctions.getProfessionalConnected(context);
                            procon.remove(request.getType());
                            Paper.book("requests").write("professionalConnected", procon);
                        }

//                                                doc.getReference().delete();
                    }
                    Paper.book("chats").write("listOfChats", chats);
                    adapter.refreshData(chats);
                    updateChatsWithTime(chats);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

    }

    private void filterChats(ArrayList<ChatsItemModel> chats) {
        HashSet<String> chatsSet = new HashSet<>();

        int index = 0;
        for(ChatsItemModel chat : chats) {
            if(chatsSet.contains(chat.getUid())) {
                chats.remove(index);
            }
            else {
                chatsSet.add(chat.getUid());
            }

            index++;
        }

        Collections.sort(chats, new Comparator<ChatsItemModel>() {
            @Override
            public int compare(ChatsItemModel chatsItemModel, ChatsItemModel t1) {
                Log.d("Time",String.valueOf(chatsItemModel.getTimestamp())+" "+String.valueOf(t1.getTimestamp()));
                return (t1.getTimestamp().compareTo(chatsItemModel.getTimestamp()));
            }
        });
    }

    public void buttonRequestsClicked(View view) {
        Intent intent = new Intent(context, Requests.class);
        startActivity(intent);
    }

    public void buttonSearchClicked(View view) {
        Intent intent = new Intent(context, Search.class);
        startActivity(intent);
    }

    public void buttonProfileClicked(View view) {
        if (profileEditF){
           profileEditF = walkthrough(profileEditF,R.id.buttonProfile,R.string.profileEditF,"Edit Profile","You can add an avatar and make other changes to your profile.");
//            new Walkthrough(view, MainActivity.this, "Edit Profile", "You can add an avatar and make other changes to your profile.");
//            if(editor != null) {
//                editor.putBoolean(getResources().getString(R.string.profileEditF), false);
//                editor.commit();
//                profileEditF = false;
//            }
        } else {
            Intent intent = new Intent(context, EditProfile.class);
            startActivity(intent);
        }
    }

    public void buttonRefreshClicked(View view) {
        if (faq){
            faq = walkthrough(faq,R.id.nav_faqs_image,R.string.faq,"FAQs","You can see the frequently asked questions");
//            new Walkthrough(findViewById(), MainActivity.this, "Edit Profile", "You can add an avatar and make other changes to your profile.");
//            if(editor != null) {
//                editor.putBoolean(getResources().getString(R.string.faq), false);
//                editor.commit();
//                profileEditF = false;
//            }
        }
        else {
            startActivity(new Intent(context,HelpActivity.class));
        }
    }

    class CustomAdapter extends BaseAdapter {
        ArrayList<ChatsItemModel> chatsItem;

        public CustomAdapter(ArrayList<ChatsItemModel> chatsItem) {
            this.chatsItem = chatsItem;
        }

        public void refreshData(ArrayList<ChatsItemModel> chata){
            this.chatsItem = chata;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return chatsItem.size();
        }

        @Override
        public Object getItem(int i) {
            return chatsItem.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View item = getLayoutInflater().inflate(R.layout.item_chats_list, null);

            final TextView profileName = item.findViewById(R.id.profileName);
            final TextView lastMessage = item.findViewById(R.id.lastMessage);
            final CircleImageView profileImage = (CircleImageView) item.findViewById(R.id.profile_image);

            final ChatsItemModel chatsItemModel = (ChatsItemModel) chatsItem.get(i);

            if (chatsItemModel.isNewMessage()) {
                profileName.setTextColor(context.getResources().getColor(R.color.pnsBlue));
                profileName.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                lastMessage.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
            profileName.setText(chatsItemModel.getName());
            lastMessage.setText(chatsItemModel.getLastMessage());

            if (!chatsItemModel.getPhotoURL().equals(""))
                Glide.with(context).load(chatsItemModel.getPhotoURL())
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .into(profileImage);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ChatBox.class);
                    intent.putExtra("content", chatsItemModel);
                    startActivityForResult(intent, 65415);
                }
            });

            return item;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 51) {
            chats = HelperLordFunctions.getChatsList(context);
            adapter.notifyDataSetChanged();
            Log.d("TAG", "sdfd: " + adapter.getCount());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        chats = HelperLordFunctions.getChatsList(context);
        Log.d("TAG", "onStart: " + chats.size());
//        filterChats(chats);
//        if (adapter != null) {
////            Paper.book("chats").write("listOfChats", chats);
//            adapter.refreshData(chats);
////            adapter.notifyDataSetChanged();
//        }

    }

}