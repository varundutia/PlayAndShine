package com.one.apperz.playandshine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.one.apperz.playandshine.databinding.ActivitySearchBinding;
import com.one.apperz.playandshine.helperLord.HelperLordFunctions;
import com.one.apperz.playandshine.model.Request;
import com.one.apperz.playandshine.model.UserProfile;
import com.one.apperz.playandshine.model.Walkthrough;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class Search extends AppCompatActivity {

    ActivitySearchBinding b;
    String SELECTED_TYPE = "", buffer;
    FirebaseFirestore db;
    Context context;
    ArrayList<UserProfile> searchResult = new ArrayList<>();
    ArrayList<UserProfile> requestsSent = new ArrayList<>();
    CustomAdapter adapter;
    ArrayList<String> requestSentTo;
    int position = -1;
    boolean reqSENT;
    int preLast = 0;
    DocumentSnapshot lastDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivitySearchBinding.inflate(getLayoutInflater());
        View v = b.getRoot();
        setContentView(v);
        context = this;
        db = FirebaseFirestore.getInstance();
        Paper.init(context);
        adapter = new CustomAdapter(searchResult);
        b.listResults.setAdapter(adapter);
        b.listResults.setEmptyView(b.noResult);
        requestsSent = HelperLordFunctions.getRequestsSent(context);
    }

    public void buttonBackClicked(View view) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();

    }


//    public void profileClicked(View view) {
//        Intent intent = new Intent(Search.this, DisplayProfile.class);
//        startActivityForResult(intent);
//    }

    public void professionalSelected(View view) {
        Log.d("TAG", "professionalSelected: "+view.getTag());
        if (SELECTED_TYPE.equals(view.getTag().toString()))
            return;

        buffer = SELECTED_TYPE;
        SELECTED_TYPE = view.getTag().toString();
        if (HelperLordFunctions.getProfessionalConnected(context).contains(SELECTED_TYPE)) {
            Toast.makeText(context, "You are already connected to " + SELECTED_TYPE, Toast.LENGTH_SHORT).show();
            SELECTED_TYPE = buffer;
            buffer = "";
            return;
        }
        requestSentTo = HelperLordFunctions.getMeListOfRequest(context, SELECTED_TYPE);
        searchResult.clear();

        //TODO: Paginate the search result

        Query ref;
        ref = db.collection(context.getResources().getString(R.string.users_collection))
                .whereEqualTo("type", SELECTED_TYPE);
        if (SELECTED_TYPE.equals("coach"))
            ref = db.collection(context.getResources().getString(R.string.users_collection))
                    .whereEqualTo("type", SELECTED_TYPE)
                    .whereEqualTo("sport",HelperLordFunctions.getMeUserProfile(context).getSport());

        ref.limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                UserProfile user = document.toObject(UserProfile.class);
                                if (!requestSentTo.contains(user.getUid()))
                                    searchResult.add(user);
                                lastDocument = document;
                            }
                            adapter.notifyDataSetChanged();
                            Log.d("TAG", "onComplete: " + searchResult.size());
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });

        b.listResults.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                final int lastItem = i + i1;

                if(lastItem == i2)
                {
                    if(preLast!=lastItem)
                    {
                        //to avoid multiple calls for last item
                        Log.d("Last", "Last");

                        Query ref;
                        ref = db.collection(context.getResources().getString(R.string.users_collection))
                                .whereEqualTo("type", SELECTED_TYPE);
                        if (SELECTED_TYPE.equals("coach"))
                            ref = db.collection(context.getResources().getString(R.string.users_collection))
                                    .whereEqualTo("type", SELECTED_TYPE)
                                    .whereEqualTo("sport",HelperLordFunctions.getMeUserProfile(context).getSport());

                        ref.limit(10).startAfter(lastDocument).get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d("TAG", document.getId() + " => " + document.getData());
                                                UserProfile user = document.toObject(UserProfile.class);
                                                if (!requestSentTo.contains(user.getUid()))
                                                    searchResult.add(user);
                                                lastDocument = document;
                                            }
                                            adapter.notifyDataSetChanged();
                                            b.listResults.setSelection(preLast);
                                            Log.d("TAG", "onComplete: " + searchResult.size());
                                        } else {
                                            Log.d("TAG", "Error getting documents: ", task.getException());
                                        }
                                    }
                                });


                        preLast = lastItem;
                    }
                }
            }
        });

    }

    class CustomAdapter extends BaseAdapter {

        ArrayList<UserProfile> searchResult;

        public CustomAdapter(ArrayList<UserProfile> searchResult) {
            this.searchResult = searchResult;
        }


        @Override
        public int getCount() {
            return searchResult.size();
        }

        @Override
        public Object getItem(int i) {
            return searchResult.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            View item = getLayoutInflater().inflate(R.layout.item_search_result, null);
            TextView profileName = item.findViewById(R.id.profileName);
            TextView profileTitle = item.findViewById(R.id.title);
            final TextView buttonRequestSend = item.findViewById(R.id.request_send);
            CircleImageView profileImage = item.findViewById(R.id.profileImage);
            final Boolean[] REQUEST_SENT = {false};
            final UserProfile user = searchResult.get(i);
            profileName.setText(user.getName());
            profileTitle.setText(user.getType());
            if (!user.getPhotoURL().equals(""))
                Glide.with(context).load(user.getPhotoURL())
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .into(profileImage);
            if (position==i && reqSENT) {
                buttonRequestSend.setVisibility(View.GONE);
                item.setEnabled(false);
            }

            buttonRequestSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserProfile userProfile = HelperLordFunctions.getMeUserProfile(context);
                    Request request = new Request(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                            user.getUid(), userProfile.getName(), userProfile.getSport(),
                            userProfile.getPhotoURL(), SELECTED_TYPE, "pending");

                    requestSentTo.add(user.getUid());
                    Paper.book("requests").write(SELECTED_TYPE, requestSentTo);
                    requestsSent.add(user);
                    Paper.book("requests").write("requestsSent", requestsSent);

                    db.collection(context.getResources().getString(R.string.requests_collection))
                            .add(request).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(context, "Request Sent", Toast.LENGTH_SHORT).show();
                            buttonRequestSend.setVisibility(View.GONE);
                            REQUEST_SENT[0] = true;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Could not send request now. Try again later", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            //TODO: add listener to display profile of professional

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,DisplayProfile.class);
                    intent.putExtra("from","search");
                    intent.putExtra("uid",user.getUid());
                    position = i;
                    startActivityForResult(intent,12345);
                }
            });
            return item;
        }
    }
    public void onQClicked(View view){
        Intent intent =new Intent(this,ImageActivity.class);
        startActivity(intent);
    }


}