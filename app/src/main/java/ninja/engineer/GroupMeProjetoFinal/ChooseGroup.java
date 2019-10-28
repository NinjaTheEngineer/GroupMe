package ninja.engineer.GroupMeProjetoFinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.persistence.LoadRelationsQueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class ChooseGroup extends AppCompatActivity implements GroupAdapter.ItemClicked {

    RecyclerView rvList;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;

    ArrayList<Posts> postToPass = new ArrayList<>();

    private String groupObjectId;
    private String channelName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_group);

        rvList = findViewById(R.id.rvGroups);
        rvList.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(layoutManager);

        String whereClause = "users.objectId = '" + ApplicationClass.user.getUserId() + "'" ;

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);

        //Função para procurar os grupos aos que o utilizador pertence
        Backendless.Persistence.of(Group.class).find(queryBuilder, new AsyncCallback<List<Group>>() {
            @Override
            public void handleResponse(List<Group> response) {

                ApplicationClass.groups = response;
        //Se pertencer apenas a um grupo será enviado diretamente para esse grupo
                if(ApplicationClass.groups.size() == 1){
                    groupObjectId = ApplicationClass.groups.get(0).getObjectId();
                    channelName = ApplicationClass.groups.get(0).getChannelName();
                    ApplicationClass.userCurrentChannel = channelName;

                    findPosts();

        //Caso tenho mais de um grupo é feita um lista de selecção
                }else{
                    myAdapter = new GroupAdapter(ChooseGroup.this, response);
                    rvList.setAdapter(myAdapter);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(ChooseGroup.this, "Error " + fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClicked(final int index) {
        groupObjectId = ApplicationClass.groups.get(index).getObjectId();
        channelName = ApplicationClass.groups.get(index).getChannelName();
        ApplicationClass.userCurrentChannel = channelName;

        findPosts();
    }
        //Função para procurar todas as publicações do grupo
    private void findPosts(){
        LoadRelationsQueryBuilder<Posts> loadRelationsQueryBuilder;
        loadRelationsQueryBuilder = LoadRelationsQueryBuilder.of(Posts.class);
        loadRelationsQueryBuilder.setRelationName( "posts" );

        Backendless.Data.of("Group").loadRelations(groupObjectId, loadRelationsQueryBuilder, new AsyncCallback<List<Posts>>() {
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
                Intent intent = new Intent(ChooseGroup.this, MainActivity.class);

                ApplicationClass.userGroup = groupObjectId;

                ApplicationClass.posts = postToPass;
                startActivity(intent);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.d("INFO","failed to search");
                Toast.makeText(ChooseGroup.this, "Error on gathering posts", Toast.LENGTH_SHORT).show();

            }
        });

    }
}
