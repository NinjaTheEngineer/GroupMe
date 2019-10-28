package ninja.engineer.GroupMeProjetoFinal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.MessageStatus;
import com.backendless.persistence.DataQueryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstGroup extends AppCompatActivity {

    Button btnCreateGroup, btnJoinGroup;
    EditText etGroupId, etGroupName;
    String userObjectId, groupName, channelName;
    double x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_group);

        userObjectId = ApplicationClass.user.getObjectId();

        btnCreateGroup = findViewById(R.id.btnCreateGroup);
        btnJoinGroup = findViewById(R.id.btnJoinGroup);
        etGroupId = findViewById(R.id.etGroupId);
        etGroupName = findViewById(R.id.etGroupName);

    //Função do botão para criar um grupo
        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etGroupName.getText().toString().isEmpty()){
                    Toast.makeText(FirstGroup.this, "Por favor escreva o nome do grupo", Toast.LENGTH_SHORT).show();
                }
                else{
                    groupName = etGroupName.getText().toString();
                    x = getRandomIntBetweenRange(10000, 999999);
                    userObjectId = ApplicationClass.user.getObjectId();

                    // save object synchronously
                    final HashMap group = new HashMap();

                    group.put("groupName", groupName);
                    group.put("inviteID", x);
                    channelName = groupName + x;
                    group.put("channelName", channelName);


    //Função Backendless para guardar o grupo na base de dados
                    Backendless.Data.of("Group").save(group, new AsyncCallback<Map>() {
                        @Override
                        public void handleResponse(Map response) {
                            Intent intent = new Intent(FirstGroup.this, GroupCostum.class);

                            intent.putExtra("extraObjectId", response.get("objectId").toString());
                            intent.putExtra("extraInviteId", x);
    //Função Backendless para criar o channel do grupo.
                            Backendless.Messaging.publish(channelName, "Channel Up!", new AsyncCallback<MessageStatus>() {
                                @Override
                                public void handleResponse(MessageStatus response) {
                                    Log.i("MYAPP", "Message has been published");
                                    Backendless.Messaging.subscribe(channelName);
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    Log.e("MYAPP", "Server reported an error " + fault);
                                }
                            });

                            startActivity(intent);
                            FirstGroup.this.finish();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {

                        }
                    });
                }
            }
        });
    //Função do botão para se juntar a um grupo
        btnJoinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etGroupId.getText().toString().isEmpty()) {
                    Toast.makeText(FirstGroup.this, "Invite EMPTY!", Toast.LENGTH_SHORT).show();
                } else {
                    String groupInviteId = etGroupId.getText().toString().trim();

                    String whereClause = "inviteId = " + groupInviteId;
                    DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                    queryBuilder.setWhereClause(whereClause);
    //Procurar o grupo pelo código convite inserido
                    Backendless.Data.of("Group").find(queryBuilder, new AsyncCallback<List<Map>>() {
                        @Override
                        public void handleResponse(List<Map> response) { //Se houver um grupo com o mesmo invite
                            Map responseThis = response.get(0);
                            String groupObjectId = responseThis.get("objectId").toString();

                            HashMap<String, Object> childObject = new HashMap<>();
                            childObject.put( "objectId", ApplicationClass.user.getObjectId()); //id do user

                            HashMap<String, Object> parentObject = new HashMap<>();
                            parentObject.put( "objectId", groupObjectId); //id do grupo neste caso já existente

                            ArrayList<Map> children = new ArrayList<>();
                            children.add(childObject); //cria uma classe criança do utilizador
    //Adicionar o utilizador ao grupo encontrado com o código convite
                            Backendless.Persistence.of( "Group" ).addRelation( parentObject, "users:Users:n", children,
                                    new AsyncCallback<Integer>()
                                    {
                                        @Override
                                        public void handleResponse( Integer response ) //Se for inserido...
                                        {
                                            Toast.makeText(FirstGroup.this, "Added to the group!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(FirstGroup.this, ChooseGroup.class);
                                            startActivity(intent);
                                            FirstGroup.this.finish();
                                        }

                                        @Override
                                        public void handleFault( BackendlessFault fault ) //Não deu certo
                                        {
                                            Toast.makeText(FirstGroup.this, "Not added to the group!", Toast.LENGTH_SHORT).show();
                                    }
                                    } );

                        }

                        @Override
                        public void handleFault(BackendlessFault fault) { //não havia grupo igual
                        }
                    });
                }

            }
        });
    }
    public static double getRandomIntBetweenRange(double min, double max){
        double x = (Math.random()*((max-min)+1))+min;
        return x;
    }
}
