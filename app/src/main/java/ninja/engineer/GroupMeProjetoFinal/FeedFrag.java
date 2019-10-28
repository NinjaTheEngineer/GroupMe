package ninja.engineer.GroupMeProjetoFinal;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFrag extends Fragment {

    View v;
    private RecyclerView myRecyclerView;
    ArrayList<Posts> lstPosts;

    Button btnPost;

    private String groupId = null;

    public FeedFrag() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_feed, container, false);

        groupId = ApplicationClass.userGroup;
        lstPosts = ApplicationClass.posts;
        if (groupId!=null){
            Toast.makeText(this.getActivity(), groupId, Toast.LENGTH_SHORT).show();
        }

        btnPost = v.findViewById(R.id.btnPost);
        myRecyclerView = v.findViewById(R.id.recyclerView);

        //Mostrar as publicações do grupo ativo
        PostsAdapter feedAdapter = new PostsAdapter(getActivity(), lstPosts);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myRecyclerView.setAdapter(feedAdapter);

    //Evento do clique do botão post abre o post Activity
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostActivity.class);
                if(groupId != null) {
                    intent.putExtra("currentGroup", groupId);

                    try{
                        getActivity().finish();
                    }catch (NullPointerException exception){
                        Log.d("POST", exception.toString());
                    }
                    startActivity(intent);

                }else{
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
