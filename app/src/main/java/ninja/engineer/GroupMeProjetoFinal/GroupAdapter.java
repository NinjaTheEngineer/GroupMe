package ninja.engineer.GroupMeProjetoFinal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private List<Group> groups;
    ItemClicked activity;

        public interface ItemClicked{
            void onItemClicked(int index);
        }

    public GroupAdapter (Context context, List<Group> list){
            groups = list;
            activity = (ItemClicked) context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

            TextView tvGroupName;

            public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onItemClicked(groups.indexOf((Group)v.getTag()));
                }
            });
            }
    }

    @NonNull
    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.group_recycler_view, viewGroup, false);

            return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.ViewHolder viewHolder, int i) {
            viewHolder.itemView.setTag(groups.get(i));

            viewHolder.tvGroupName.setText(groups.get(i).groupName);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }
}
