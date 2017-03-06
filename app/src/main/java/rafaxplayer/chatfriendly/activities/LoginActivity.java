package rafaxplayer.chatfriendly.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import rafaxplayer.chatfriendly.R;

import static rafaxplayer.chatfriendly.Chat_Friendly.mAuth;
import static rafaxplayer.chatfriendly.Chat_Friendly.usersRef;
import static rafaxplayer.chatfriendly.classes.GlobalUtils.getCurrentUser;

public class LoginActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private static String TAG = ".LoginActivity";
    private static int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private Button buttonSigin;
    private LoginButton facebookSigin;
    private SignInButton googleSigin;
    private EditText password;
    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        hash();
        logOutFacebook();
        buttonSigin=(Button)findViewById(R.id.buttonSigIn);
        password=(EditText) findViewById(R.id.editPassword);
        email=(EditText) findViewById(R.id.editEmail);
        facebookSigin=(LoginButton) findViewById(R.id.login_button);
        googleSigin=(SignInButton) findViewById(R.id.sigin_google);
        callbackManager = CallbackManager.Factory.create();
        facebookSigin.setReadPermissions("email","public_profile");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //facebook
        facebookSigin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "facebook:onSuccess:" + loginResult.getAccessToken().getToken());
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.e(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e(TAG, "facebook:onError", exception);
            }
        });

        //Google
        googleSigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });

        //email
        buttonSigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(email.getText())) {
                    Toast.makeText(LoginActivity.this, "Required email",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password.getText())) {
                    Toast.makeText(LoginActivity.this, "Required password",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Ok Login correct!", Toast.LENGTH_SHORT).show();
                                    HashMap<String,Object> map = new HashMap<String, Object>();
                                    map.put("email",getCurrentUser().getEmail());
                                    map.put("name",TextUtils.isEmpty(getCurrentUser().getDisplayName())?"No Set":getCurrentUser().getDisplayName());
                                    map.put("avatar",getCurrentUser().getPhotoUrl() == null ? "No Set" : getCurrentUser().getPhotoUrl().toString());
                                    map.put("uid",getCurrentUser().getUid());
                                    usersRef.child(getCurrentUser().getUid()).setValue(map);
                                    goToMain();
                                } else {

                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    email.setText("");
                                    password.setText("");
                                }

                            }
                        });

            }
        });
    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.e(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication Facebook OK.",
                                    Toast.LENGTH_SHORT).show();
                            saveUser();
                            goToMain();
                        }else{
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,e.getMessage());
            }
        });
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication in Google OK.",
                                    Toast.LENGTH_SHORT).show();
                            saveUser();
                            goToMain();
                        }else{
                            Log.e(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication in Google failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,e.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void goToMain() {
        Intent intent = new Intent(LoginActivity.this, UsersActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveUser(){
        HashMap<String,Object> map = new HashMap<>();
        map.put("email",getCurrentUser().getEmail());
        map.put("name",TextUtils.isEmpty(getCurrentUser().getDisplayName())? "No Set":getCurrentUser().getDisplayName());
        map.put("avatar",getCurrentUser().getPhotoUrl() == null ? "No Set" : getCurrentUser().getPhotoUrl().toString());
        map.put("uid",getCurrentUser().getUid());
        usersRef.child(getCurrentUser().getUid()).setValue(map);
    }

    private void logOutFacebook(){

        LoginManager.getInstance().logOut();
    }

    private void hash(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "rafaxplayer.chatfriendly",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("NameNotFoundException", e.getMessage());

        } catch (NoSuchAlgorithmException e) {
            Log.e("NoSuchException", e.getMessage());
        }
    }
}
