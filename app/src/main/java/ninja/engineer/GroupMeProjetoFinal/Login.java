package ninja.engineer.GroupMeProjetoFinal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.local.UserIdStorageFactory;

public class Login extends AppCompatActivity {

    //Inicializar variáveis, neste caso
    private Toolbar lToolBar;

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    EditText etMail, etPassword;
    Button btnLogin, btnRegisterL;
    TextView tvReset;


    @Override //onCreate é a primeira coisa a ser corrida ao entrarmos no ecrã
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        lToolBar = findViewById(R.id.login_page_toolbar);
        setSupportActionBar(lToolBar);
        getSupportActionBar().setTitle("groupMe");

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);

        etMail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegisterL = findViewById(R.id.btnRegisterL);
        tvReset = findViewById(R.id.tvReset);

        //Botão de login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etMail.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty())
                    Toast.makeText(Login.this, "Por favor insira os seus dados!", Toast.LENGTH_SHORT).show();

                else{
                    String email = etMail.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();

                    showProgress(true);
                    tvLoad.setText("Logging you in, please wait...");

                    Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) { //Correu bem
                            ApplicationClass.user = response;
                            ApplicationClass.userName = response.getProperty("name").toString();

                            Intent intent = new Intent(Login.this, FirstGroup.class);

                            startActivity(intent);
                            Login.this.finish();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) { //Correu mal, login inválido
                            Toast.makeText(Login.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }
                    }, true);
                }


            }
        });

        //Função da botão do registo
        btnRegisterL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Login.this, Register.class)); //Abrir o register
            }
        });
        //Função do texto de renovar a palavra esquecida
        tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etMail.getText().toString().isEmpty()){
                    Toast.makeText(Login.this, "Please enter your email address in the email field.", Toast.LENGTH_SHORT).show();
                }else{
                    String email = etMail.getText().toString().trim();

                    showProgress(true);
                    tvLoad.setText("A enviar email de restauro de palavra-chave...");
        //Restauro de password
                    Backendless.UserService.restorePassword(email, new AsyncCallback<Void>() {
                        @Override
                        public void handleResponse(Void response) { //Correu bem
                            showProgress(false);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) { //Correu mal
                            Toast.makeText(Login.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }
                    });
                }

            }
        });

        //Verificação de utilizador
        Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
            @Override
            public void handleResponse(Boolean response) { //Deu certo
                if(response){
                    showProgress(true);
                    tvLoad.setText("Checking login credentials, please wait...");
                    final String userObjectId = getUserObjectId();

                    tvLoad.setText("Logging you in, please wait...");
                    Backendless.Data.of(BackendlessUser.class).findById(userObjectId, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) {
                            ApplicationClass.user = response;
                            Intent intent = new Intent(Login.this, ChooseGroup.class);

                            ApplicationClass.userName = response.getProperty("name").toString();

                            startActivity(intent);
                            Login.this.finish();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) { //Deu mal
                            Toast.makeText(Login.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }
                    });

                }else{
                    showProgress(false);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(Login.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                showProgress(false);
            }
        });
    }

    private String getUserObjectId() {
        final String userObjectId = UserIdStorageFactory.instance().getStorage().get();
        return userObjectId;
    }
        //
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
                                //if "show" true view = gone|if false view = visible
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


}
