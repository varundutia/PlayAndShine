package com.one.apperz.playandshine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.one.apperz.playandshine.databinding.ActivitySearchBinding;
import com.one.apperz.playandshine.helperLord.HelperLordFunctions;
import com.one.apperz.playandshine.model.Request;
import com.one.apperz.playandshine.model.UserProfile;
import com.one.apperz.playandshine.model.Walkthrough;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private static final String URL = "https://fcm.googleapis.com/fcm/send";
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivitySearchBinding.inflate(getLayoutInflater());
        View v = b.getRoot();
        setContentView(v);
        mRequestQueue = Volley.newRequestQueue(this);
//        FirebaseMessaging.getInstance().subscribeToTopic(selfUID);
        context = this;
        db = FirebaseFirestore.getInstance();
        Paper.init(context);
        adapter = new CustomAdapter(searchResult);
        b.listResults.setAdapter(adapter);
        b.listResults.setEmptyView(b.noResult);
        requestsSent = HelperLordFunctions.getRequestsSent(context);

        // Default selection
        professionalSelected(b.cview);
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

        findViewById(R.id.cview).setBackgroundColor(Color.parseColor("#ffffff"));
        findViewById(R.id.ptrain).setBackgroundColor(Color.parseColor("#ffffff"));
        findViewById(R.id.vnutri).setBackgroundColor(Color.parseColor("#ffffff"));
        findViewById(R.id.vdiet).setBackgroundColor(Color.parseColor("#ffffff"));
        findViewById(R.id.vpsycho).setBackgroundColor(Color.parseColor("#ffffff"));
        findViewById(R.id.vphysio).setBackgroundColor(Color.parseColor("#ffffff"));
        findViewById(R.id.vothers).setBackgroundColor(Color.parseColor("#ffffff"));

        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.background_select_you_are_connect) );
        } else {
            view.setBackground(ContextCompat.getDrawable(context, R.drawable.background_select_you_are_connect));
        }

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
                sendNotification("New Request","request",user.getUid());
                Toast.makeText(context,i+" "+user.getName(),Toast.LENGTH_LONG).show();
                buttonRequestSend.setVisibility(View.GONE);
                item.setEnabled(false);
            }

            buttonRequestSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int sdk = android.os.Build.VERSION.SDK_INT;
                    if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.background_selected_you_are) );
                    } else {
                        view.setBackground(ContextCompat.getDrawable(context, R.drawable.background_selected_you_are));
                    }
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
                            Toast.makeText(context, "Request Sent"+user.getUid(), Toast.LENGTH_SHORT).show();
                            sendNotification("New Request",user.getName(),user.getUid());
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
//    public void onQClicked(View view){
//        Intent intent =new Intent(this,ImageActivity.class);
//        startActivity(intent);
//    }
//private void sendNotification(String name,String body) {
//    JSONObject main = new JSONObject();
//    try {
//        main.put("to", "/topics/" + chatsItemModel.getUid());
//        JSONObject notification = new JSONObject();
//        notification.put("title", name);
//        notification.put("body", body);
//        main.put("notification", notification);
//        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.POST, URL,
//                main, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                //onsucces
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //onerror
//            }
//        }
//        ) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> m = new HashMap<>();
//                m.put("content-type", "application/json");
//                m.put("authorization", "key=AAAAuT3P1Y0:APA91bH6o60pA0vgd0njPmp1VogCgRGEPdyeKNazXFP21ogi_IvVy7L9Bsk4FNaEoesJDGDjo45TosZMSL8p0R4ebPHp3nwfsftdaJKzrMlgjdKPk5aE36GsERo8ubQbO340fxRnAKyN");
//                return m;
//            }
//        };
//        mRequestQueue.add(request);
//    }catch(Exception e){
//        e.printStackTrace();
//    }
//}

    private void sendNotification(String name,String body,String uuid) {
        JSONObject main = new JSONObject();
        try {
            main.put("to", "/topics/" + uuid);
            JSONObject notification = new JSONObject();
            notification.put("title", name);
            notification.put("body", body);
            main.put("notification", notification);
            JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.POST, URL,
                    main, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //onsucces
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //onerror
                }
            }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> m = new HashMap<>();
                    m.put("content-type", "application/json");
                    m.put("authorization", "key=AAAAuT3P1Y0:APA91bH6o60pA0vgd0njPmp1VogCgRGEPdyeKNazXFP21ogi_IvVy7L9Bsk4FNaEoesJDGDjo45TosZMSL8p0R4ebPHp3nwfsftdaJKzrMlgjdKPk5aE36GsERo8ubQbO340fxRnAKyN");
                    return m;
                }
            };
            mRequestQueue.add(request);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}