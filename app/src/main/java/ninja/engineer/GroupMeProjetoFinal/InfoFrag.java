package ninja.engineer.GroupMeProjetoFinal;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.LoadRelationsQueryBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFrag extends Fragment {

    View v;
    private RecyclerView recyclerView;
    private String groupObjectId;
    ArrayList<Members> members = new ArrayList<>();

    private ImageButton btnChange;
    private ImageView ivPerfil, ivGroupImg;
    private ConstraintLayout clMember, clGroup;
    private TextView tvMemberName, tvGroupName, tvGroupBio, tvButton, tvCode;
    private EditText etBio;

    private RequestManager glide;

    public InfoFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.fragment_info, container, false);

        groupObjectId = ApplicationClass.userGroup;
        recyclerView = v.findViewById(R.id.rvMembers);

        btnChange = v.findViewById(R.id.btnChange);
        ivPerfil = v.findViewById(R.id.ivProPic);
        ivGroupImg = v.findViewById(R.id.ivGroup);
        clMember = v.findViewById(R.id.profileLayout);
        clGroup = v.findViewById(R.id.groupLayout);
        tvCode = v.findViewById(R.id.tvCode);

        tvButton = v.findViewById(R.id.tvButton);
        tvGroupName = v.findViewById(R.id.tvGroupName);
        tvGroupBio = v.findViewById(R.id.groupBio);
        tvMemberName = v.findViewById(R.id.tvUserName);
        etBio = v.findViewById(R.id.etBio);

        tvMemberName.setText(ApplicationClass.user.getProperty("name").toString());

        glide = Glide.with(this);

        ivPerfil.setImageResource(R.drawable.ic_person_black_24dp);
        ivGroupImg.setImageResource(R.drawable.ic_group);

        glide.load(ApplicationClass.user.getProperty("profilePic")).into(ivPerfil);
    //Função Backendless para procurar o grupo ativo e aceder ao seu código convite, descrição, e imagem
        Backendless.Data.of(Group.class).findById(ApplicationClass.userGroup, new AsyncCallback<Group>() {
            @Override
            public void handleResponse(Group response){
                if(response.getGroupPic() != null) {
                    glide.load(response.getGroupPic()).into(ivGroupImg);
                }
                tvCode.setText("497257.71290990355");
                tvGroupName.setText(response.getGroupName());
                tvGroupBio.setText(response.getGroupDescription());
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(getContext(), "Group Image Error", Toast.LENGTH_SHORT).show();
            }
        });
        ivPerfil.setCropToPadding(true);
        ivPerfil.setAdjustViewBounds(true);

    //Função do Botão que troca entre o perfil e o perfil de grupo
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clMember.getVisibility() == View.VISIBLE){
                    clMember.setVisibility(View.GONE);
                    clGroup.setVisibility(View.VISIBLE);
                    tvButton.setText("Perfil");
                    btnChange.setImageResource(R.drawable.ic_person_black_24dp);

                }else{
                    clGroup.setVisibility(View.GONE);
                    clMember.setVisibility(View.VISIBLE);
                    tvButton.setText("Grupo");
                    btnChange.setImageResource(R.drawable.ic_group);
                }
            }
        });

        findMembers(groupObjectId);

        return v;
    }
    //Função para procurar os membros relacionados com o grupo
    private void findMembers(String groupObjectId){
        LoadRelationsQueryBuilder<Map<String,Object>> loadRelationsQueryBuilder;
        loadRelationsQueryBuilder = LoadRelationsQueryBuilder.ofMap();
        loadRelationsQueryBuilder.setRelationName( "users" );

        Backendless.Data.of("Group").loadRelations(groupObjectId, loadRelationsQueryBuilder, new AsyncCallback<List<Map<String, Object>>>() {
            @Override
            public void handleResponse(List<Map<String, Object>> response) {
                for(Map e: response){
                    Members mem = new Members();
                    mem.setObjectId(e.get("objectId").toString());
                    try{
                        mem.setProPic(e.get("propic").toString());
                    }catch(NullPointerException exc){
                        Log.d("Exception", exc.toString());
                    }
                    mem.setName(e.get("name").toString());
                    members.add(mem);
                }
                MembersAdapter membersAdapter = new MembersAdapter(getActivity(), members);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(membersAdapter);

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.d("INFO","failed to search");
                Toast.makeText(getContext(), "Error on gathering posts", Toast.LENGTH_SHORT).show();

            }
        });

    }

}
