package ninja.engineer.GroupMeProjetoFinal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Register extends AppCompatActivity {

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;
    private ImageButton btnProfilePic;

    //For profile pic
    private String imagesLink = "images/";
    private static final int Gallery_Pick = 1;
    private Uri ImageUri;
    private File file;

    EditText etName, etEmail, etPassword, etReEnter;
    Button btnRegister;

    private String email;
    private String name;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etReEnter = findViewById(R.id.etReEnter);
        btnRegister = findViewById(R.id.btnRegister);
        btnProfilePic = findViewById(R.id.imageButton);



        btnProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });
        btnProfilePic.setAdjustViewBounds(true);
        btnProfilePic.setCropToPadding(true);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StoringImageToBackendlessAndRegisteringUser();

            }

        });
    }
    private void StoringImageToBackendlessAndRegisteringUser() {
        if (etName.getText().toString().isEmpty() || etEmail.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty() ||
                etReEnter.getText().toString().isEmpty()) {
            Toast.makeText(Register.this, "Por favor preencha todos os campos.", Toast.LENGTH_SHORT).show();
        } else {
            if (etPassword.getText().toString().trim().equals(etReEnter.getText().toString().trim())) {

                if (file != null) {
                    Backendless.Files.upload(file, imagesLink, new AsyncCallback<BackendlessFile>() {
                        @Override
                        public void handleResponse(BackendlessFile response) {
                            BackendlessUser user = new BackendlessUser();

                            name = etName.getText().toString().trim();
                            email = etEmail.getText().toString().trim();
                            password = etPassword.getText().toString().trim();

                            user.setEmail(email);
                            user.setPassword(password);
                            user.setProperty("name", name);
                            user.setProperty("profilePic", response.getFileURL());
                            btnProfilePic.setVisibility(View.GONE);
                            showProgress(true);
                            tvLoad.setText("A registar o utilizador, por favor aguarde...");

                            Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
                                @Override
                                public void handleResponse(BackendlessUser response) {
                                    Toast.makeText(Register.this, "Utilizador registado com sucesso!", Toast.LENGTH_SHORT).show();
                                    Register.this.finish();
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    Toast.makeText(Register.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                    showProgress(false);
                                }
                            });
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(Register.this, "Error: !", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //Se não for selecionada uma imagem.
                } else {
                    BackendlessUser user = new BackendlessUser();

                    name = etName.getText().toString().trim();
                    email = etEmail.getText().toString().trim();
                    password = etPassword.getText().toString().trim();

                    user.setEmail(email);
                    user.setPassword(password);
                    user.setProperty("name", name);
                    showProgress(true);
                    tvLoad.setText("A registar o utilizador, por favor aguarde...");

                    Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) {
                            Toast.makeText(Register.this, "Utilizador registado com sucesso!", Toast.LENGTH_SHORT).show();
                            Register.this.finish();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(Register.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }
                    });
                }
            } else {
                Toast.makeText(Register.this, "As palavras-passe devem ser iguais!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Gallery_Pick && resultCode == RESULT_OK && data!=null){
            ImageUri = data.getData();
            btnProfilePic.setImageURI(ImageUri);
            file = null;
            try{
                file = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                InputStream inputStream = Register.this.getContentResolver().openInputStream(ImageUri);

                FileOutputStream fileOutputStream = new FileOutputStream(file);

                copyStream(inputStream, fileOutputStream);
                fileOutputStream.close();
                inputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Register.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        File mCurrentPhotoPath = new File(image.getAbsolutePath());
        return image;
    }

    public static void copyStream(InputStream input, OutputStream output) throws IOException{
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1){
            output.write(buffer, 0,bytesRead);
        }
    }
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