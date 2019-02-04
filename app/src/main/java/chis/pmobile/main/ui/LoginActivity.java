package chis.pmobile.main.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import chis.pmobile.R;
import chis.pmobile.main.model.User;
import chis.pmobile.main.services.ApiController;

public class LoginActivity extends AppCompatActivity {

    private final String FILE = "pmobile.share.txt";

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    private SharedPreferences sharedPreferences;

    private ApiController apiController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = this.getSharedPreferences(FILE,  Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("LOGGED_IN", false)) {
            startMainActivity();
        }

        apiController = new ApiController(this);

        usernameEditText = findViewById(R.id.username_editText);
        passwordEditText = findViewById(R.id.password_editText);
        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(this::login_user);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        this.finish();
    }

    private void login_user(View view) {
        if (isNetworkConnected()) {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("Login error")
                        .setMessage("Please fill username and password")
                        .setPositiveButton("OK", (dialog, which) -> {})
                        .create();
                alertDialog.show();
            } else {
                User user = new User(username, password);
                passwordEditText.setText("");
                apiController.login(user);
            }
        }
        else {
            Toast.makeText(this, "You are offline", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
