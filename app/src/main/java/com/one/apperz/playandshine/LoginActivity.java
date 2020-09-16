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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.one.apperz.playandshine.databinding.ActivityLoginBinding;
import com.one.apperz.playandshine.helperLord.HelperLordConstant;
import com.one.apperz.playandshine.model.ChatsItemModel;
import com.one.apperz.playandshine.model.Request;
import com.one.apperz.playandshine.model.Storage;
import com.one.apperz.playandshine.model.UserProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;
public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    Context context;
    private ActivityLoginBinding binding;
    FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
//            mAuth.getInstance().signOut();
            startActivity(new Intent(this,MainActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        context = this;
//        binding.linkWebsite.setMovementMethod(LinkMovementMethod.getInstance());
//        binding.socialFacebook.setMovementMethod(LinkMovementMethod.getInstance());
//        binding.socialInstagram.setMovementMethod(LinkMovementMethod.getInstance());
//        binding.socialYoutube.setMovementMethod(LinkMovementMethod.getInstance());
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
//        createGoogleRequest();
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

    private void createGoogleRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }
    private void signIn() {
        createGoogleRequest();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);

            // Signed in successfully, show authenticated UI.
//            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.fetchSignInMethodsForEmail(account.getEmail()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                Log.d("email_exists",""+task.getResult().getSignInMethods().size());
                if (task.getResult().getSignInMethods().size() == 0){
                    // email not existed
                    UserProfile user =new UserProfile();
                    user.setName(account.getDisplayName());
                    user.setEmail(account.getEmail());
                    user.setPhotoURL(String.valueOf(account.getPhotoUrl()));
                    Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                    intent.putExtra("user",user);
                    intent.putExtra("gSign","true");
                    startActivity(intent);
                }else {
                    // email existed
                    binding.inputEmail.setText(account.getEmail());
                    binding.inputPassword.setText("000000");
                    login1();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
//        Toast.makeText(this,user.getName(),Toast.LENGTH_SHORT).show();
//        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
////            if(task.isSuccessful()){
////                FirebaseUser user = mAuth.getCurrentUser();
////                startActivity(new Intent(LoginActivity.this, MainActivity.class));
////            }else{
////                Toast.makeText(LoginActivity.this,"failed",Toast.LENGTH_LONG).show();
////            }
////        });


    }

    @Override
    public void onBackPressed() {

    }

    public void createAccount(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }
    void login1(){
        String EMAIL = binding.inputEmail.getText().toString();
        String password = binding.inputPassword.getText().toString();
        boolean valid = true;
        if (EMAIL.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(EMAIL).matches()) {
            binding.inputEmail.setError("Enter a valid email address!");
            valid = false;
        } else {
            binding.inputEmail.setError(null);
        }
//        if(password.equals("000000")){
//            binding.inputPassword.setError("Wrong Password");
//        }
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

    public void login(View view) {
        login1();
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
    public void fbLoginButton(View view) {
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("fblog", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("fblog", "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("fblog", "facebook:onError", error);
                // ...
            }
        });
    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("fbb", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("fbb", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("fbb", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void googleLoginButton(View view){
        signIn();
    }

}