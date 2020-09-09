package com.one.apperz.playandshine;

//import android.app.Notification;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.one.apperz.playandshine.databinding.ActivityChatBoxBinding;
import com.one.apperz.playandshine.helperLord.HelperLordFunctions;
import com.one.apperz.playandshine.model.ChatsItemModel;
import com.one.apperz.playandshine.model.Message;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.paperdb.Paper;

public class ChatBox extends AppCompatActivity {

    Context context;
    ChatsItemModel chatsItemModel;
    ActivityChatBoxBinding b;
    String selfUID;
    Query query;
    MessageAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityChatBoxBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        selfUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        context = this;
        chatsItemModel = getIntent().getParcelableExtra("content");
        sendNotification(context,1000);
        query = getReference().orderBy("timestamp", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message.class).build();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        b.chatsLists.setLayoutManager(linearLayoutManager);
        adapter = new MessageAdapter(options);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                b.chatsLists.scrollToPosition(adapter.getItemCount() - 1);
            }
        });
        b.chatsLists.setAdapter(adapter);
        note(context);


        if (!chatsItemModel.getPhotoURL().equals(""))
            Glide.with(context).load(chatsItemModel.getPhotoURL())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(b.profileImage);


//        messageRef = FirebaseFirestore.getInstance().collection(context.getString(R.string.users_collection))
//                .document(selfUID).collection(context.getString(R.string.chat_collection))
//                .document(chatsItemModel.getUid()).collection(context.getString(R.string.message_collection));
//
//        messageRefOther = FirebaseFirestore.getInstance().collection(context.getString(R.string.users_collection))
//                .document(chatsItemModel.getUid()).collection(context.getString(R.string.chat_collection))
//                .document(selfUID).collection(context.getString(R.string.message_collection));
//
//        messages = HelperLordFunctions.getMessages(context, chatsItemModel.getUid());

        renderUI();

    }

    private CollectionReference getReference() {
        String ref;
        Log.d("TAG", "getReference: " + chatsItemModel.getType());
        if (chatsItemModel.getType().equals("athlete"))
            ref = chatsItemModel.getUid() + selfUID;
        else
            ref = selfUID + chatsItemModel.getUid();
        return FirebaseFirestore.getInstance().collection(context.getString(R.string.chat_collection))
                .document(ref)
                .collection(context.getString(R.string.message_collection));
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

//        registration = messageRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                try {
//                    if (e != null) {
//                        Toast.makeText(context, "Error loading message", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//                        messages.add(doc.toObject(Message.class));
//                        docsToDelete.add(doc.getReference());
//                    }
//                    Log.d("TAG", "onEvent: "+messages.size());
//                    adapter.notifyDataSetChanged();
//                } catch (Exception ea) {
//                    ea.printStackTrace();
//                }
//            }
//        });

    }
    private void note(Context context){
        try {

            FirebaseFirestore cloudInstance = FirebaseFirestore.getInstance();

            CollectionReference chatCollection = getReference();

            //chatCollection.document("S0SbTmZ7d6ih293itd5T");

            chatCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w("TAG", "Listen failed.", e);
                        return;
                    }

                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();

                        Toast.makeText(context , "Current data: " + documentChanges.get(documentChanges.size() - 1).getDocument(), Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "Current data: " + queryDocumentSnapshots.getDocumentChanges().get(0));
                    } else {
                        Log.d("TAG", "Current data: null");
                    }
                }
            });

        }
        catch (Exception e) {
            Log.d("Exception", "Cloud Firestore Chats Exception");
        }
    }
    private void sendNotification(Context applicationContext, double saved_price) {

        NotificationManager NM;
        NM = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent pending = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(),0);

        Notification notify = new Notification.Builder(applicationContext)
                .setContentTitle("Price went below " + String.valueOf(saved_price))
                .setContentText("Price Alert")
                .setSmallIcon(R.drawable.logo)
                .build();

        NM.notify(0, notify);

    }

    private void renderUI() {
        b.profileName.setText(chatsItemModel.getName());
        b.profileTitle.setText(chatsItemModel.getType());

    }

    public void profileClicked(View view) {

        Intent intent = new Intent(context, DisplayProfile.class);
        intent.putExtra("from", "chatbox");
        intent.putExtra("uid", chatsItemModel.getUid());
        startActivityForResult(intent, 6654);
    }

    public void buttonBackClicked(View view) {
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Paper.init(context);

        ArrayList<ChatsItemModel> chats = HelperLordFunctions.getChatsList(context);
        int i = 0;
        for (i = 0; i < chats.size(); i++) {
            if (chats.get(i).getUid().equals(chatsItemModel.getUid())) {
                break;
            }
        }
        Log.d("TAG", "onStop: " + chats.get(i).getName());
        chats.set(i, chatsItemModel);

        Paper.book("chats").write("listOfChats", chats);
        adapter.stopListening();
    }

    public void sendMessage(View view) {
        String body = b.messageEditText.getText().toString();
        if (body.isEmpty()) {
            return;
        }

        Message message = new Message(selfUID, body, new Timestamp(new Date()));
        getReference().add(message).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                b.messageEditText.setText("");
            }
        });


    }


    public class MessageAdapter extends FirestoreRecyclerAdapter<Message, MessageAdapter.ViewHolder> {


        public static final int MSG_TYPE_LEFT = 0;
        public static final int MSG_TYPE_RIGHT = 1;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        public MessageAdapter(@NonNull FirestoreRecyclerOptions<Message> options) {
            super(options);
        }

        @Override
        public int getItemCount() {

            if (super.getItemCount()==0){
                b.noChat.setVisibility(View.VISIBLE);
            }else
                b.noChat.setVisibility(View.GONE);

            return super.getItemCount();
        }

        @Override
        protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Message model) {
            holder.message.setText(model.getBody());
            SimpleDateFormat sfd = new SimpleDateFormat("HH:mm");
            holder.timestamp.setText(sfd.format(model.getTimestamp().toDate()));
            chatsItemModel.setTimestamp(model.getTimestamp());
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            if (viewType == MSG_TYPE_RIGHT)
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            else
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public int getItemViewType(int position) {
            if (getItem(position).getFrom().equals(user.getUid()))
                return MSG_TYPE_RIGHT;
            else
                return MSG_TYPE_LEFT;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView message, timestamp;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                message = itemView.findViewById(R.id.message);
                timestamp = itemView.findViewById(R.id.timestamp);

            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == 6515 && data.getBooleanExtra("isEndTalk", false)) {
            Intent i = new Intent();
            i.putExtra("isEndTalk", true);
            setResult(51, i);
            finish();
        }
    }
}