package com.one.apperz.playandshine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.model.Document;
import com.one.apperz.playandshine.databinding.ActivityRequestsBinding;
import com.one.apperz.playandshine.helperLord.HelperLordFunctions;
import com.one.apperz.playandshine.model.ChatsItemModel;
import com.one.apperz.playandshine.model.Request;
import com.one.apperz.playandshine.model.UserProfile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class Requests extends AppCompatActivity {

    RelativeLayout layout;
    Context context;
    ActivityRequestsBinding b;
    FirebaseFirestore db;
    CustomAdapter adapter;
    ArrayList<Request> requests;
    ArrayList<DocumentReference> refOfDocumentsRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityRequestsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        refOfDocumentsRequests = new ArrayList<>();
        context = this;
        db = FirebaseFirestore.getInstance();
        if(db == null){
            Intent requests = new Intent(this,Requests.class);
            startActivity(requests);
        }
        requests = new ArrayList<>();
        adapter = new CustomAdapter(requests);
        b.listRequests.setAdapter(adapter);
        b.listRequests.setEmptyView(b.noRequest);
        Paper.init(context);

        getRequests();

    }

    private void getRequests() {

        db.collection(context.getResources().getString(R.string.requests_collection))
                .whereEqualTo("uidProfessional", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereEqualTo("status","pending").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                Request user = document.toObject(Request.class);
                                requests.add(user);
                                refOfDocumentsRequests.add(document.getReference());
                            }
                            adapter.notifyDataSetChanged();
                            Log.d("TAG", "onComplete: " + requests.size());
                        } else {
                            Toast.makeText(context, "Error fetching requests. Try again later", Toast.LENGTH_SHORT).show();
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void buttonBackClicked(View view) {
        finish();
    }

    public void profileClicked(View view) {
        Intent intent = new Intent(Requests.this, DisplayProfile.class);
        startActivity(intent);
    }

//    @Override
//    public void onBackPressed() {
//        finish();
//        startActivity(new Intent(context, MainActivity.class));
//    }

    class CustomAdapter extends BaseAdapter {
        ArrayList<Request> requests;

        public CustomAdapter(ArrayList<Request> requests) {
            this.requests = requests;
        }

        @Override
        public int getCount() {
            return requests.size();
        }

        @Override
        public Object getItem(int i) {
            return requests.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View item = getLayoutInflater().inflate(R.layout.item_request, null);
            CircleImageView profileImage = (CircleImageView) item.findViewById(R.id.profileImage);
            TextView profileName = item.findViewById(R.id.profileName);
            TextView profileTitle = item.findViewById(R.id.title);
            TextView buttonAccept = item.findViewById(R.id.request_accept);
            TextView buttonReject = item.findViewById(R.id.request_reject);

            final int position = i;
            final Request request = requests.get(i);
            profileName.setText(request.getName());
            profileTitle.setText(request.getTitle());

            if (!request.getPhotoURL().equals(""))
                Glide.with(context).load(request.getPhotoURL())
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .into(profileImage);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,DisplayProfile.class);
                    intent.putExtra("from","request");
                    intent.putExtra("uid",request.getUidAthlete());
                    intent.putExtra("refOfDocument", refOfDocumentsRequests.get(position).getId());

                    startActivity(intent);
                }
            });

            buttonAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show();

                    Map<String, Object> newChat = new HashMap<>();
                    newChat.put("status", "accepted");

                    refOfDocumentsRequests.get(position).set(newChat, SetOptions.merge());
                    newChat.clear();
                    newChat.put(getResources().getString(R.string.athlete), request.getUidAthlete());
                    newChat.put(getResources().getString(R.string.professional), request.getUidProfessional());

                    db.collection(context.getString(R.string.chat_collection))
                            .document(request.getUidAthlete()+request.getUidProfessional())
                            .set(newChat);

//                    //deleting all the request sent by athlete of respective type of professional
//                    db.collection(context.getString(R.string.requests_collection))
//                            .whereEqualTo("uidAthlete", request.getUidAthlete())
//                            .whereEqualTo("type", request.getType())
//                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                        @Override
//                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
//                                document.getReference().delete();
//                            }
//                        }
//                    });


                    ArrayList<ChatsItemModel> chats = HelperLordFunctions.getChatsList(context);
                    ChatsItemModel item = new ChatsItemModel(request.getName(), request.getPhotoURL(),
                            "New Chat", request.getUidAthlete(), new Timestamp(new Date()), "athlete",true);
                    chats.add(item);
                    Paper.book("chats").write("listOfChats", chats);
                    requests.remove(position);
                    ArrayList<Request> req = HelperLordFunctions.getRequestsAccepted(context);
                    req.add(request);
                    Paper.book("requests").write("requestsAccepted",req);
                    adapter.notifyDataSetChanged();

                }
            });

            buttonReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Request Rejected", Toast.LENGTH_SHORT).show();
                    db.collection(context.getString(R.string.requests_collection))
                            .whereEqualTo("uidAthlete", request.getUidAthlete())
                            .whereEqualTo("uidProfessional", request.getUidProfessional())
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                document.getReference().delete();
                            }
                        }
                    });
                    requests.remove(position);
                    adapter.notifyDataSetChanged();
                }
            });


            return item;
        }
    }

}