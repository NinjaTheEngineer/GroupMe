package ninja.engineer.GroupMeProjetoFinal;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.persistence.LoadRelationsQueryBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    private ImageButton imgBtn;
    private EditText etCommnet;
    private Button btnPost;

    private ArrayList<Posts> postToPass = new ArrayList<>();

    private File file;

    private String userName;

    private String imagesLink = "images/";
    private static final int Gallery_Pick = 1;
    private String description;
    private Uri ImageUri;
    private String currentGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        currentGroup = getIntent().getStringExtra("currentGroup");
        imgBtn = findViewById(R.id.imagBtn);
        etCommnet = findViewById(R.id.etComent);
        btnPost = findViewById(R.id.btnPost);

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });

        userName = ApplicationClass.user.getProperty("name").toString();

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidatePostInfo();
            }
        });
    }
        //Função que valida se o publicação é válido, se for, guarda-o na base de dados
    private void ValidatePostInfo() {
        description = etCommnet.getText().toString();

        if(ImageUri == null){
            Toast.makeText(this, "Por favor selecione uma imagem", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(description)){
            Toast.makeText(this, "Por favor descreva de alguma forma.", Toast.LENGTH_SHORT).show();
        }
        else{
            StoringImageToBackendless();
        }


    }
        //Guarda a publicação na base de dados
    private void StoringImageToBackendless() {
        if(file != null){
            Backendless.Files.upload(file, imagesLink, new AsyncCallback<BackendlessFile>() {
                @Override
                public void handleResponse(BackendlessFile response) {
                    Toast.makeText(PostActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                    HashMap post = new HashMap();
                    post.put("name", userName);
                    post.put("propic", ApplicationClass.user.getProperty("profilePic"));
                    post.put("postpic", response.getFileURL());
                    post.put("description", description);
        //Guarda a publicação na tabela "Posts" do Backendless
                    Backendless.Data.of("Posts").save(post, new AsyncCallback<Map>() {
                        @Override
                        public void handleResponse(Map response) {
                            Toast.makeText(PostActivity.this, "Post SAVED!", Toast.LENGTH_SHORT).show();

                            HashMap<String, Object> child = new HashMap<>();
                            child.put("objectId", response.get("objectId").toString());

                            HashMap<String, Object> parent = new HashMap<>();
                            parent.put("objectId", currentGroup);

                            ArrayList<Map> childArray = new ArrayList<>();
                            childArray.add(child);
        //Adiciona uma relação entre o grupo e a publicação guardada
                            Backendless.Persistence.of("Group").addRelation(parent, "posts:Posts:n", childArray, new AsyncCallback<Integer>() {
                                @Override
                                public void handleResponse(Integer response) {
                                    Toast.makeText(PostActivity.this, "Post Linked", Toast.LENGTH_SHORT).show();
                                    findPosts();
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    Toast.makeText(PostActivity.this, "not linked", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(PostActivity.this, fault.getExtendedData() + fault.getMessage() + fault.getDetail() + fault.getCode(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Toast.makeText(PostActivity.this, "dead!", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this, "No files added!", Toast.LENGTH_SHORT).show();
        }
    }
        //Função para abrir a galeria
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
            imgBtn.setImageURI(ImageUri);
           file = null;
           try{
               file = createImageFile();
           } catch (IOException e) {
               e.printStackTrace();
           }
           try {
               InputStream inputStream = PostActivity.this.getContentResolver().openInputStream(ImageUri);

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

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = PostActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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

    private void findPosts(){
        LoadRelationsQueryBuilder<Posts> loadRelationsQueryBuilder;
        loadRelationsQueryBuilder = LoadRelationsQueryBuilder.of(Posts.class);
        loadRelationsQueryBuilder.setRelationName( "posts" );

        Backendless.Data.of("Group").loadRelations(currentGroup, loadRelationsQueryBuilder, new AsyncCallback<List<Posts>>() {
            @Override
            public void handleResponse(List<Posts> response) {
                for (Posts p : response) {
                    Posts pT = new Posts();
                    pT.setName(p.getName());
                    pT.setDescription(p.getDescription());
                    pT.setPostpic(p.getPostpic());
                    pT.setPropic(p.getPropic());
                    postToPass.add(pT);
                }
                Intent intent = new Intent(PostActivity.this, MainActivity.class);
                ApplicationClass.posts = postToPass;
                startActivity(intent);
                PostActivity.this.finish();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.d("INFO","failed to search");
                Toast.makeText(PostActivity.this, "Error on gathering posts", Toast.LENGTH_SHORT).show();

            }
        });

    }
}

