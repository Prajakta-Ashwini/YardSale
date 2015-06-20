package com.android.yardsale.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.helpers.YardSaleApplication;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import jp.wasabeef.picasso.transformations.BlurTransformation;


public class LoginActivity extends ActionBarActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private ImageView backgroundImage;
    private YardSaleApplication client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);

//        DisplayMetrics displayMetrics = getBaseContext().getResources().getDisplayMetrics();
//
//        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
//        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        Picasso.with(this)
                .load(R.drawable.login_background_1)
                .fit().centerInside()
                .skipMemoryCache()
                .transform(new BlurTransformation(getBaseContext(), 10))
                .into((ImageView) findViewById(R.id.background_image));

        client = new YardSaleApplication(this);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.login(String.valueOf(etEmail.getText()), String.valueOf(etPassword.getText()));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void loginWithFB(View view) {
        final List<String> permissions = Arrays.asList("public_profile", "email");
        Toast.makeText(LoginActivity.this, "logging in with FB", Toast.LENGTH_LONG).show();
        client.signUpAndLoginWithFacebook(permissions);
    }

    public void onBack(View view) {
        finish();
    }

    public void onSignUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}
