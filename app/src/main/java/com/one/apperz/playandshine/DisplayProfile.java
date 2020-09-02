package com.one.apperz.playandshine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.one.apperz.playandshine.databinding.ActivityDisplayProfileBinding;
import com.one.apperz.playandshine.helperLord.HelperLordFunctions;
import com.one.apperz.playandshine.model.ChatsItemModel;
import com.one.apperz.playandshine.model.Request;
import com.one.apperz.playandshine.model.UserProfile;

import java.util.ArrayList;

import io.paperdb.Paper;

public class DisplayProfile extends AppCompatActivity {

    Context context;
    ActivityDisplayProfileBinding b;
    String uid, from;
    UserProfile profile, userProfile;
    Intent i;
    boolean requestSENT = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityDisplayProfileBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        context = this;
        Paper.init(context);
        userProfile = HelperLordFunctions.getMeUserProfile(context);
        i = getIntent();
        uid = i.getStringExtra("uid");
        from = i.getStringExtra("from");
        FirebaseFirestore.getInstance().collection(context.getString(R.string.users_collection))
                .document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                profile = documentSnapshot.toObject(UserProfile.class);
                renderViews();
            }
        });
    }

    private void renderViews() {

        switch (from) {
            case "chatbox":
                b.buttonSendRequest.setVisibility(View.GONE);
                b.buttonSendRequestIcon.setVisibility(View.GONE);
                break;
            case "search":
                b.textButton.setVisibility(View.GONE);
                b.contactInfo.setVisibility(View.GONE);
                b.buttonSendRequest.setVisibility(View.GONE);
                b.buttonSendRequestIcon.setVisibility(View.GONE);
                break;
            case "request":
                b.textButton.setVisibility(View.GONE);
                b.contactInfo.setVisibility(View.GONE);
                b.buttonSendRequestIcon.setVisibility(View.GONE);
                b.buttonSendRequest.setVisibility(View.GONE);
                break;
        }

        if (!profile.getPhotoURL().equals(""))
            Glide.with(context).load(profile.getPhotoURL())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(b.profileImage);

        b.profileExperience.setText("Experience: " + profile.getExperience());
        b.profileName.setText(profile.getName());
        b.profileSport.setText(profile.getSport());
        b.profileType.setText(profile.getType());
        b.profileAchievement.setText(profile.getAchievement());
        b.profileContact.setText("Phone: " + profile.getPhone() + "\nEmail: " + profile.getEmail());
        b.profilePersonalDetails.setText("Age: " + profile.getAge() + "\nLocation: " + profile.getLocation());


    }


    public void buttonBackClicked(View view) {
//        if (from.equals("search")){
//            Intent intent = new Intent();
//            intent.putExtra("reqSent",requestSENT);
//            setResult(1456,intent);
//        }
        finish();
    }

    public void sendRequest(View view) {
//        if (from.equals("search")) {
//            Toast.makeText(context, "sent request", Toast.LENGTH_SHORT).show();
//            UserProfile userProfile = HelperLordFunctions.getMeUserProfile(context);
//            Request request = new Request(userProfile.getUid(),
//                    profile.getUid(), userProfile.getName(), userProfile.getSport(),
//                    userProfile.getPhotoURL(), userProfile.getType(), "pending");
//            ArrayList<String> requestSentTo = HelperLordFunctions.getMeListOfRequest(context, profile.getType());
//            requestSentTo.add(profile.getUid());
//            Paper.book("requests").write(profile.getType(), requestSentTo);
//
//            ArrayList<UserProfile> requestsSent = HelperLordFunctions.getRequestsSent(context);
//            requestsSent.add(profile);
//            Paper.book("requests").write("requestsSent", requestsSent);
//
//            FirebaseFirestore.getInstance().collection(context.getResources().getString(R.string.requests_collection))
//                    .add(request).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                @Override
//                public void onSuccess(DocumentReference documentReference) {
//                    Toast.makeText(context, "Request Sent", Toast.LENGTH_SHORT).show();
//                    b.buttonSendRequestIcon.setVisibility(View.GONE);
//                    b.buttonSendRequest.setVisibility(View.GONE);
//                    requestSENT = true;
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(context, "Could not send request now. Try again later", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//        }
    }

    public void endTalk(View view) {
        final Request request;
        if (userProfile.getType().equals("athlete"))
            request = new Request(userProfile.getUid(), profile.getUid(), "", "", "", profile.getType(), "endTalk");
        else
            request = new Request(profile.getUid(), userProfile.getUid(), "", "", "", profile.getType(), "endTalk");

        FirebaseFirestore.getInstance().collection(context.getString(R.string.requests_collection))
                .add(request).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

                if (userProfile.getType().equals(getResources().getString(R.string.athlete))) {
                    ArrayList<ChatsItemModel> chatsItemModelArrayList = HelperLordFunctions.getChatsList(context);
                    for (int i = 0; i < chatsItemModelArrayList.size(); i++) {
                        ChatsItemModel chatsItemModel = chatsItemModelArrayList.get(i);
                        if (chatsItemModel.getUid().equals(request.getUidProfessional()) && userProfile.getUid().equals(request.getUidAthlete())) {
                            chatsItemModelArrayList.remove(i);
                            break;
                        }
                    }
                    Paper.book("chats").write("listOfChats", chatsItemModelArrayList);
                    Paper.book("requests").write(request.getType(), new ArrayList<String>());
                    ArrayList<String> procon = HelperLordFunctions.getProfessionalConnected(context);
                    procon.remove(request.getType());
                    Paper.book("requests").write("professionalConnected", procon);

                } else {
                    ArrayList<ChatsItemModel> chatsItemModelArrayList = HelperLordFunctions.getChatsList(context);
                    for (int i = 0; i < chatsItemModelArrayList.size(); i++) {
                        ChatsItemModel chatsItemModel = chatsItemModelArrayList.get(i);
                        if (chatsItemModel.getUid().equals(request.getUidAthlete()) && userProfile.getUid().equals(request.getUidProfessional())) {
                            chatsItemModelArrayList.remove(i);
                            break;
                        }
                    }
                    Paper.book("chats").write("listOfChats", chatsItemModelArrayList);

                }

                Intent i = new Intent();
                i.putExtra("isEndTalk", true);
                setResult(6515, i);
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Error! Try again", Toast.LENGTH_SHORT).show();
            }
        });


    }
}