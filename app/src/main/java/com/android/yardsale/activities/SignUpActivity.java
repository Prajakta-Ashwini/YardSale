package com.android.yardsale.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.helpers.YardSaleApplication;
import com.parse.ParseFacebookUtils;

import java.util.Arrays;
import java.util.List;

public class SignUpActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        final List<String> permissions = Arrays.asList("public_profile", "email");
        final EditText etSignUpUsername = (EditText) findViewById(R.id.etSignUpUserName);
        final EditText etSignUpPassword = (EditText) findViewById(R.id.etSignUpPassword);
        Button btnSaveUser = (Button) findViewById(R.id.btnSaveUser);
        final YardSaleApplication client = new YardSaleApplication(this);

        btnSaveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.manualSignUp(String.valueOf(etSignUpUsername.getText()),
                        String.valueOf(etSignUpPassword.getText()));
            }
        });

        ImageButton btnLoginWithFB = (ImageButton) findViewById(R.id.btnLoginWithFB);
        btnLoginWithFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignUpActivity.this, "logging in with FB", Toast.LENGTH_LONG).show();
                client.signUpAndLoginWithFacebook(permissions);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void onBack(View view) {
        Intent intent = new Intent(this, HowItWorksActivity.class);
        startActivity(intent);
    }

    public void onLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
