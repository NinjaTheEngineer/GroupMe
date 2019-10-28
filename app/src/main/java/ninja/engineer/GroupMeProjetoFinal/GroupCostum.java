package ninja.engineer.GroupMeProjetoFinal;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.backendless.Backendless;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GroupCostum extends AppCompatActivity {

    EditText etGroupDescription; //descrição do grupo, ACABAR
    EditText etInviteId;
    Button btnSeguinte;
    ImageButton ibtnGroup;
    String profilePic;

    Boolean ready = false;
    //For profile pic
    private String imagesLink = "images/";
    private static final int Gallery_Pick = 1;
    private Uri ImageUri;
    private File file;

    String groupObjectId;
    Double inviteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_costum);

        etGroupDescription = findViewById(R.id.etGroupDescription);
        etInviteId = findViewById(R.id.etInviteId);
        btnSeguinte = findViewById(R.id.btnSeguinte);
        ibtnGroup = findViewById(R.id.ibtnGroup);
        //Vou buscar ao ecra anterior os valores guardados
        groupObjectId = getIntent().getStringExtra("extraObjectId");

        inviteId = getIntent().getDoubleExtra("extraInviteId", 3.14);

        etInviteId.setText(String.format(inviteId.toString()));

        ibtnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });
        ibtnGroup.setAdjustViewBounds(true);
        ibtnGroup.setCropToPadding(true);

        btnSeguinte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoringImageToBackendless();
                Intent intent = new Intent(GroupCostum.this, ChooseGroup.class);
                startActivity(intent);
                GroupCostum.this.finish();
            }
        });
        AddingRelation();
    }
    //Função que cria uma relação entre o utilizador e o grupo.
    private void AddingRelation(){
    //Criação dos objetos necessários para criar a relação.
        HashMap<String, Object> childObject = new HashMap<>();
        childObject.put( "objectId", ApplicationClass.user.getObjectId().toString());

        HashMap<String, Object> parentObject = new HashMap<>();
        parentObject.put( "objectId", groupObjectId);

        ArrayList<Map> children = new ArrayList<>();
        children.add(childObject);
    //Função que adiciona a relação entre as tabelas.
        Backendless.Persistence.of( "Group" ).addRelation( parentObject, "users:Users:n", children,
                new AsyncCallback<Integer>()
                {
                    @Override
                    public void handleResponse( Integer response )
                    {
                        Toast.makeText(GroupCostum.this, "Added to the group!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void handleFault( BackendlessFault fault )
                    {
                        Toast.makeText(GroupCostum.this, "Error not added to the group!", Toast.LENGTH_SHORT).show();
                    }
                } );
    }
    //Função para guardar a imagem da galeria no Backendless
    private void StoringImageToBackendless() {
                if (file != null) {
                    Backendless.Files.upload(file, imagesLink, new AsyncCallback<BackendlessFile>() {
                        @Override
                        public void handleResponse(BackendlessFile response) {
                            profilePic = response.getFileURL();
    //Função Backendless para se procurar o grupo criado
                            Backendless.Data.of(Group.class).findById(groupObjectId, new AsyncCallback<Group>() {
                                @Override
                                public void handleResponse(Group response) {
                                    response.setGroupPic(profilePic);
                                    response.setGroupDescription(etGroupDescription.getText().toString());
    //Alteração da imagem e descrição do grupo criado
                                    Backendless.Persistence.save(response, new AsyncCallback<Group>() {
                                        @Override
                                        public void handleResponse(Group response) {
                                            Toast.makeText(GroupCostum.this, "Group Pic Added!", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void handleFault(BackendlessFault fault) {
                                            Toast.makeText(GroupCostum.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    Toast.makeText(GroupCostum.this, "Error-" + fault.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(GroupCostum.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        //Override de função com o objetivo de transformar num objeto a imagem selecionada pelo utilizador
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Gallery_Pick && resultCode == RESULT_OK && data!=null){
            ImageUri = data.getData();
            ibtnGroup.setImageURI(ImageUri);
            file = null;
            try{
                file = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                InputStream inputStream = GroupCostum.this.getContentResolver().openInputStream(ImageUri);

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
        //Função para abrir a galeria do android
    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }
        //Criar um ficheiro File da imagem com a data de emissão
    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = GroupCostum.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        File mCurrentPhotoPath = new File(image.getAbsolutePath());
        return image;
    }
        //Função para copiar a Stream recebid
    public static void copyStream(InputStream input, OutputStream output) throws IOException{
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1){
            output.write(buffer, 0,bytesRead);
        }
    }
}
