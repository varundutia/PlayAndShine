package com.one.apperz.playandshine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.one.apperz.playandshine.databinding.ActivityLoginBinding;
import com.one.apperz.playandshine.helperLord.HelperLordConstant;
import com.one.apperz.playandshine.model.ChatsItemModel;
import com.one.apperz.playandshine.model.Request;
import com.one.apperz.playandshine.model.Storage;
import com.one.apperz.playandshine.model.UserProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;
public class LoginActivity extends AppCompatActivity {

    Context context;
    private ActivityLoginBinding binding;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        context = this;
        binding.linkWebsite.setMovementMethod(LinkMovementMethod.getInstance());
        binding.socialFacebook.setMovementMethod(LinkMovementMethod.getInstance());
        binding.socialInstagram.setMovementMethod(LinkMovementMethod.getInstance());
        binding.socialYoutube.setMovementMethod(LinkMovementMethod.getInstance());
        mAuth = FirebaseAuth.getInstance();

        if(mAuth == null) {
            Log.d("TAG", "Firebase Instance Not Found");
        }
        else {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && !user.getEmail().equals("")) {
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }
        }

        binding.showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // show password
                    binding.inputPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    // hide password
                    binding.inputPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                binding.inputPassword.setSelection(binding.inputPassword.getText().length());
            }
        });

    }

    @Override
    public void onBackPressed() {

    }

    public void createAccount(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    public void login(View view) {

        String EMAIL = binding.inputEmail.getText().toString();
        boolean valid = true;
        if (EMAIL.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(EMAIL).matches()) {
            binding.inputEmail.setError("Enter a valid email address!");
            valid = false;
        } else {
            binding.inputEmail.setError(null);
        }

        if (valid && mAuth != null) {
            mAuth.signInWithEmailAndPassword(EMAIL, binding.inputPassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", getResources().getString(R.string.signInSuccess));
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", getResources().getString(R.string.signInFailure), task.getException());
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                                // ...
                            }

                            // ...
                        }
                    });

        }

    }

    private void updateUI(final FirebaseUser user) {
        if (user != null) {
            binding.pleaseWait.setVisibility(View.VISIBLE);
            FirebaseFirestore.getInstance().collection(context.getResources().getString(R.string.users_collection)).document(user.getUid()).get().addOnSuccessListener(
                    new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            UserProfile profile = documentSnapshot.toObject(UserProfile.class);
                            Paper.init(context);
                            Paper.book().write(context.getResources().getString(R.string.users_collection), profile);
                            binding.pleaseWait.setVisibility(View.GONE);
                            FirebaseFirestore.getInstance().collection("storage").document(user.getUid())
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        try {
                                            Storage storage = task.getResult().toObject(Storage.class);

                                            Paper.book("chats").write("listOfChats", storage.getChatsList());
                                            Paper.book("requests").write("requestsAccepted", storage.getRequestAccepted());
                                            Paper.book("requests").write("professionalConnected", storage.getConnectedProfs());
                                            Paper.book("requests").write("requestsSent", storage.getRequestSent());
                                            Map<String, ArrayList<String>> list = storage.getList();
                                            for (String pro : HelperLordConstant.TYPE_OF_PROFESSIONAL)
                                                Paper.book("requests").write(pro, list.get(pro));
                                            task.getResult().getReference().delete();
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            finish();
                                        } catch (Exception e) {

                                            Log.d("TAG", "onComplete: " + e.getMessage());
                                            Paper.book("chats").write("listOfChats", new ArrayList<ChatsItemModel>());
                                            Paper.book("requests").write("requestsAccepted", new ArrayList<Request>());
                                            Paper.book("requests").write("professionalConnected", new ArrayList<String>());
                                            Paper.book("requests").write("requestsSent", new ArrayList<UserProfile>());
                                            for (String pro : HelperLordConstant.TYPE_OF_PROFESSIONAL)
                                                Paper.book("requests").write(pro, new ArrayList<String>());
                                            task.getResult().getReference().delete();
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            finish();

                                        }
                                    }
                                }
                            });
                        }
                    }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Error Logging in. Try again", Toast.LENGTH_SHORT).show();
                    if(mAuth != null) {
                        mAuth.signOut();
                    }
                }
            });
        }
    }
}