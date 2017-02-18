package rafaxplayer.chatfriendly.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.util.HashMap;

import rafaxplayer.chatfriendly.R;

import static rafaxplayer.chatfriendly.Chat_Friendly.getCurrentUser;
import static rafaxplayer.chatfriendly.Chat_Friendly.mAuth;
import static rafaxplayer.chatfriendly.Chat_Friendly.usersRef;

public class LoginActivity extends AppCompatActivity {

    Button buttonSigin;
    EditText password;
    EditText email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        buttonSigin=(Button)findViewById(R.id.buttonSigIn);
        password=(EditText) findViewById(R.id.editPassword);
        email=(EditText) findViewById(R.id.editEmail);
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
    private void goToMain() {
        Intent intent = new Intent(LoginActivity.this, UsersActivity.class);
        startActivity(intent);
        finish();
    }


}
