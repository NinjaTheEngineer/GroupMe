package ninja.engineer.GroupMeProjetoFinal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Posts> modelFeedArrayList = new ArrayList<>();
    private RequestManager glide;

    public PostsAdapter(Context context, ArrayList<Posts> modelFeedArrayList){
        this.context = context;
        this.modelFeedArrayList = modelFeedArrayList;
        glide = Glide.with(context);
    }

    public PostsAdapter(){

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_feed_layout, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.tvName.setText(modelFeedArrayList.get(i).getName());
        myViewHolder.tvStatus.setText(modelFeedArrayList.get(i).getDescription());

        glide.load(modelFeedArrayList.get(i).getPropic()).into(myViewHolder.ivProfPic);

        if(modelFeedArrayList.get(i).getPostpic() == null){
            myViewHolder.ivPostPic.setVisibility(View.GONE);
        }else{
            myViewHolder.ivPostPic.setVisibility(View.VISIBLE);
            glide.load(modelFeedArrayList.get(i).getPostpic()).into(myViewHolder.ivPostPic);
        }
    }

    @Override
    public int getItemCount() {
        return modelFeedArrayList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvComments, tvStatus;
        ImageView ivProfPic, ivPostPic;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfPic = itemView.findViewById(R.id.ivProfPic);
            ivPostPic = itemView.findViewById(R.id.ivPostPic);
            tvName = itemView.findViewById(R.id.tvName);
            tvComments = itemView.findViewById(R.id.tvComment);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
