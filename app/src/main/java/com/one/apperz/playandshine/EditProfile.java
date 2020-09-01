package com.one.apperz.playandshine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.one.apperz.playandshine.databinding.ActivityEditProfileBinding;
import com.one.apperz.playandshine.helperLord.HelperLordConstant;
import com.one.apperz.playandshine.helperLord.HelperLordFunctions;
import com.one.apperz.playandshine.model.ChatsItemModel;
import com.one.apperz.playandshine.model.Request;
import com.one.apperz.playandshine.model.Storage;
import com.one.apperz.playandshine.model.UserProfile;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class EditProfile extends AppCompatActivity {

    ActivityEditProfileBinding b;
    Context context;
    String TYPE;
    UserProfile userProfile;
    String photoURL = "";
    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityEditProfileBinding.inflate(getLayoutInflater());
        View view = b.getRoot();
        setContentView(view);
        context = this;
        Paper.init(this);
        renderUI();

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this, android.R.layout.simple_list_item_1,
                        HelperLordConstant.LIST_OF_SPORTS);

        AutoCompleteTextView sportProfile = (AutoCompleteTextView) findViewById(R.id.profileSport);
        sportProfile.setAdapter(adapter);

    }

    private void renderUI() {
        Log.d("TAG", "renderUI: ");
        userProfile = Paper.book().read(context.getResources().getString(R.string.users_collection), new UserProfile());
        int x = userProfile.getName().indexOf(" ");
        Log.d("Nameindex",userProfile.getName());
        if (x != -1) {
            b.profileFirstName.getEditText().setText(userProfile.getName().substring(0, x));
            b.profileLastName.getEditText().setText(userProfile.getName().substring(x + 1));
        } else {
            b.profileFirstName.getEditText().setText(userProfile.getName());
            b.profileLastName.getEditText().setText("");
        }
        b.profileExperience.getEditText().setText(userProfile.getExperience());
        b.profileNumber.getEditText().setText(userProfile.getPhone());
        b.profileEmail.getEditText().setText(userProfile.getEmail());
        b.profileAge.getEditText().setText(userProfile.getAge() + "");
        b.profileSport.setText(userProfile.getSport());
        b.profileAchievement.getEditText().setText(userProfile.getAchievement());
        b.profileLocation.getEditText().setText(userProfile.getLocation());
        TYPE = userProfile.getType();
        if (!userProfile.getPhotoURL().equals("")) {
            Glide.with(context).load(userProfile.getPhotoURL())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(b.profileImage);
        }
        b.profileAge.getEditText().setFilters(new InputFilter[]{new InputFilterMinMax("0", "99")});

    }

    public void buttonBackClicked(View view) {
        finish();
    }

    public void signOut(View view) {

        ArrayList<ChatsItemModel> chatsList = HelperLordFunctions.getChatsList(context);
        ArrayList<Request> requestAccepted = HelperLordFunctions.getRequestsAccepted(context);
        ArrayList<String> connectedProfs = HelperLordFunctions.getProfessionalConnected(context);
        ArrayList<UserProfile> requestsSent = HelperLordFunctions.getRequestsSent(context);

        Map<String, ArrayList<String>> list = new HashMap<>();
        for (String prof : HelperLordConstant.TYPE_OF_PROFESSIONAL) {
            ArrayList<String> listOfRequest = HelperLordFunctions.getMeListOfRequest(context, prof);
            list.put(prof, listOfRequest);
        }

        Storage storage = new Storage(chatsList, requestAccepted, connectedProfs, requestsSent, list);

        FirebaseFirestore.getInstance().collection("storage").document(userProfile.getUid())
                .set(storage).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Paper.book().destroy();
                Paper.book("requests").destroy();
                Paper.book("users").destroy();
                Paper.book("chats").destroy();
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(EditProfile.this, LoginActivity.class);
//Clear all activities and start new task
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

    }

    public void saveClicked(View view) {

        String fname = b.profileFirstName.getEditText().getText().toString();
        String lname = b.profileLastName.getEditText().getText().toString();
        String phone = b.profileNumber.getEditText().getText().toString();
        String email = b.profileEmail.getEditText().getText().toString();
        int age = Integer.parseInt(b.profileAge.getEditText().getText().toString());
        String location = b.profileLocation.getEditText().getText().toString();
        String sport = b.profileSport.getText().toString();
        String acheivement = b.profileAchievement.getEditText().getText().toString();
        String experience = b.profileAchievement.getEditText().getText().toString();

        UserProfile user = new UserProfile(FirebaseAuth.getInstance().getCurrentUser().getUid(), fname + lname, email, sport, TYPE, photoURL,
                acheivement, age, phone, location, experience);

        Paper.book().write(context.getResources().getString(R.string.users_collection), user);
        FirebaseFirestore.getInstance().collection(context.getResources().getString(R.string.users_collection))
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(user).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Profile Updated.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
        );

    }

    public void changeProfileImage(View view) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultURI = result.getUri();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultURI);

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference spaceRef = storage.getReference().child("profileImages/" + userProfile.getUid() + ".jpg");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] dataa = baos.toByteArray();

                    UploadTask uploadTask = spaceRef.putBytes(dataa);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(context, "Upload Failed. Try Again!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    photoURL = uri.toString();
                                    userProfile.setPhotoURL(photoURL);
                                    RequestOptions requestOptions = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL);
                                    Glide.with(context).load(photoURL).apply(requestOptions).into(b.profileImage);
                                    Paper.init(context);
                                    Log.d("TAG", "onSuccess: " + userProfile.getPhotoURL());
                                    Paper.book().write(context.getString(R.string.users_collection), userProfile);
                                    FirebaseFirestore.getInstance().collection(context.getString(R.string.users_collection))
                                            .document(userProfile.getUid()).set(userProfile);
                                }
                            });
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void shareApp(View view) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Play and Shine");
            String shareMessage = "Hi!\n" +
                    "Connect with other sports enthusiasts and sports professionals to receive mentorship and build a strong value laden network to take your sports journey ahead with our app - Play and Shine.\n" +
                    "Download here : ";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            Log.d("TAG", "shareApp: " + e.toString());
        }
    }

    public void helpClicked(View view) {
        startActivity(new Intent(EditProfile.this, HelpActivity.class));
    }

    public class InputFilterMinMax implements InputFilter {
        private int min, max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public InputFilterMinMax(String min, String max) {
            this.min = Integer.parseInt(min);
            this.max = Integer.parseInt(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) {
            }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }

}
