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

import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MyViewHolderTwo> {

    private Context context;
    private List<Members> members;
    private RequestManager glide;

    public MembersAdapter(){

    }

    public MembersAdapter(Context context, List<Members> members) {
        this.members = members;
        this.context = context;
        glide = Glide.with(context);
    }

    public class MyViewHolderTwo extends RecyclerView.ViewHolder{

        TextView tvMember, tvStatus;
        ImageView ivMemberPic;

        public MyViewHolderTwo(@NonNull View itemView) {
            super(itemView);

            tvMember = itemView.findViewById(R.id.tvMemberName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            ivMemberPic = itemView.findViewById(R.id.ivMemberPic);
        }
    }

    @NonNull
    @Override
    public MyViewHolderTwo onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.members_layout, viewGroup, false);

        return new MyViewHolderTwo(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderTwo viewHolder, int i) {
        Members mem = members.get(i);

        viewHolder.tvMember.setText(mem.getName());

        viewHolder.ivMemberPic.setImageResource(R.drawable.ic_android);
        viewHolder.ivMemberPic.setAdjustViewBounds(true);
        viewHolder.ivMemberPic.setCropToPadding(true);

    }

    @Override
    public int getItemCount() {
        return members.size();
    }

}
