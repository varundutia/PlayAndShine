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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import com.one.apperz.playandshine.databinding.ActivityMainBinding;
import com.one.apperz.playandshine.helperLord.HelperLordFunctions;
import com.one.apperz.playandshine.model.ChatsItemModel;
import com.one.apperz.playandshine.model.Message;
import com.one.apperz.playandshine.model.Request;
import com.one.apperz.playandshine.model.UserProfile;
import com.one.apperz.playandshine.model.Walkthrough;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        View v = b.getRoot();
        setContentView(v);
        context = this;
        Paper.init(context);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(pref != null) {
            profileEditF = pref.getBoolean(getResources().getString(R.string.profileEditF), true);
            searchF = pref.getBoolean(getResources().getString(R.string.searchF), true);
            faq = pref.getBoolean("faq",true);
            connectF = pref.getBoolean("connectF",true);
            editor = pref.edit();
        }


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

        chats = HelperLordFunctions.getChatsList(context);
        adapter = new CustomAdapter(chats);
        b.listChats.setAdapter(adapter);
        b.listChats.setEmptyView(b.noChat);
        renderUI();

    }

    boolean walkthrough(boolean flag,int view_id,int string_id,String title,String body){
        if (flag){
            new Walkthrough(findViewById(view_id), MainActivity.this, title, body);
            if(editor != null) {
                editor.putBoolean(getResources().getString(string_id), false);
                editor.commit();
                return false;
            }
        }
        return true;
    }

    private void renderUI() {

        final UserProfile userProfile = Paper.book().read(context.getResources().getString(R.string.users_collection), new UserProfile());
        if (userProfile != null && userProfile.getType().equals("athlete")) {
            searchF=walkthrough(searchF,R.id.nav_connect_image,R.string.searchF,"Connect with Others","Press this button and have a look at the different categories of people you can connect to.");

            b.buttonRequests.setVisibility(View.GONE);
            requestsSent = HelperLordFunctions.getRequestsSent(context);
            b.actionsButton.setText("Search Professional to start connecting...");


            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

            if(firebaseFirestore != null) {
                firebaseFirestore.collection(context.getString(R.string.requests_collection))
                        .whereEqualTo("uidAthlete", userProfile.getUid())
                        .whereEqualTo("status", "accepted")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Request request = document.toObject(Request.class);
                                ChatsItemModel item = new ChatsItemModel();

                                for (UserProfile user : requestsSent) {
                                    if(request.getUidAthlete().equals("") && request.getUidProfessional().equals("")) {
                                        break;
                                    }
                                    if (request.getUidProfessional().equals(user.getUid())) {
                                        item = new ChatsItemModel(user.getName(), user.getPhotoURL(),
                                                "New Chat", user.getUid(),
                                                (Timestamp) new Timestamp(new Date()),
                                                user.getType(), true);
                                        ArrayList<String> profs = HelperLordFunctions.getProfessionalConnected(context);
                                        profs.add(user.getType());
                                        Paper.book("requests").write("professionalConnected", profs);
                                        chats.add(item);
                                        break;
                                    }
                                }

                                document.getReference().delete();
                            }

                            Collections.sort(chats, new Comparator<ChatsItemModel>() {
                                @Override
                                public int compare(ChatsItemModel chatsItemModel, ChatsItemModel t1) {
                                    return (t1.getTimestamp().compareTo(chatsItemModel.getTimestamp()));
                                }
                            });

                            //IDHAR ADD GET REQUEST WITH STATUS AS ENDTALK AND AGAIN RENDR CHATS LIST
                            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                            if(firebaseFirestore != null) {
                                firebaseFirestore.getInstance().collection(context.getString(R.string.requests_collection))
                                        .whereEqualTo("uidAthlete", userProfile.getUid())
                                        .whereEqualTo("status", "endTalk")
                                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                            Request request = doc.toObject(Request.class);
                                            ArrayList<ChatsItemModel> chatsItemModelArrayList = HelperLordFunctions.getChatsList(context);
                                            for (int i = 0; i < chatsItemModelArrayList.size(); i++) {
                                                ChatsItemModel chatsItemModel = chatsItemModelArrayList.get(i);
                                                if (chatsItemModel.getUid().equals(request.getUidProfessional()) && userProfile.getUid().equals(request.getUidAthlete())) {
                                                    chatsItemModelArrayList.remove(i);
                                                    break;
                                                }
                                            }
                                            Paper.book("chats").write("listOfChats", chatsItemModelArrayList);
                                            chats = chatsItemModelArrayList;
                                            Paper.book("requests").write(request.getType(), new ArrayList<String>());
                                            ArrayList<String> procon = HelperLordFunctions.getProfessionalConnected(context);
                                            procon.remove(request.getType());
                                            Paper.book("requests").write("professionalConnected", procon);
                                            doc.getReference().delete();
                                        }

                                        Paper.book("chats").write("listOfChats", chats);

                                        adapter.notifyDataSetChanged();

                                        updateChatsWithTime(chats);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }

                            Paper.book("chats").write("listOfChats", chats);
                            adapter.notifyDataSetChanged();

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
            connectF=walkthrough(searchF,R.id.nav_request_image,R.string.connectF,"Accept a request","Press this button and have a look at the different people who are trying to connect to you.");

            b.actionsButton.setText("Check Pending Request to start connecting...");

            Collections.sort(chats, new Comparator<ChatsItemModel>() {
                @Override
                public int compare(ChatsItemModel chatsItemModel, ChatsItemModel t1) {
                    return t1.getTimestamp().compareTo(chatsItemModel.getTimestamp());
                }
            });
//
//            Paper.book("chats").write("listOfChats", chats);
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

            if(firebaseFirestore != null) {
                firebaseFirestore.collection(context.getString(R.string.requests_collection))
                        .whereEqualTo("uidProfessional", userProfile.getUid())
                        .whereEqualTo("status", "endTalk").get()
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Request request = doc.toObject(Request.class);

                            ArrayList<ChatsItemModel> chatsItemModelArrayList = HelperLordFunctions.getChatsList(context);
                            for (int i = 0; i < chatsItemModelArrayList.size(); i++) {
                                ChatsItemModel chatsItemModel = chatsItemModelArrayList.get(i);
                                if (chatsItemModel.getUid().equals(request.getUidAthlete()) && userProfile.getUid().equals(request.getUidProfessional())) {
                                    chatsItemModelArrayList.remove(i);
                                    break;
                                }
                            }
                            doc.getReference().delete();
                            Paper.book("chats").write("listOfChats", chatsItemModelArrayList);
                            chats = chatsItemModelArrayList;
                            adapter.notifyDataSetChanged();
                            updateChatsWithTime(chats);
                        }
                    }
                });
            }

            adapter.notifyDataSetChanged();

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
                                    if (documentSnapshot.toObject(Message.class).getTimestamp().getSeconds() > chatsItemModel.getTimestamp().getSeconds()) {
                                        chatsItemModel.setNewMessage(true);
                                        Log.d("TAG", "onComplete: " + documentSnapshot.toObject(Message.class).getBody());
                                        Collections.sort(chats, new Comparator<ChatsItemModel>() {
                                            @Override
                                            public int compare(ChatsItemModel chatsItemModel, ChatsItemModel t1) {
                                                return t1.getTimestamp().compareTo(chatsItemModel.getTimestamp());
                                            }
                                        });

                                        Paper.book("chats").write("listOfChats", chats);
                                        adapter.refreshData(chats);

                                    }
                                }
                            }
                        }
                    });
        }
    }


    public void buttonRequestsClicked(View view) {
        Intent intent = new Intent(context, Requests.class);
        startActivity(intent);
    }

    public void buttonSearchClicked(View view) {
        if (searchF){
            new Walkthrough(view,MainActivity.this,"Connect with Others","Press this button and have a look at the different categories of people you can connect to.");
            if(editor != null) {
                editor.putBoolean(getResources().getString(R.string.searchF), false);
                editor.commit();
                searchF = false;
            }
        } else {
            Intent intent = new Intent(context, Search.class);
            startActivity(intent);
        }
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
            faq = walkthrough(faq,R.id.nav_faqs_image,R.string.faq,"Faq","You can see the frequently asked questions");
//            new Walkthrough(findViewById(), MainActivity.this, "Edit Profile", "You can add an avatar and make other changes to your profile.");
//            if(editor != null) {
//                editor.putBoolean(getResources().getString(R.string.faq), false);
//                editor.commit();
//                profileEditF = false;
//            }
        } else {
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
            lastMessage.setText(chatsItemModel.getType());

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
        chats = HelperLordFunctions.getChatsList(context);
        Log.d("TAG", "onStart: " + chats.size());
        adapter.refreshData(chats);
    }

}