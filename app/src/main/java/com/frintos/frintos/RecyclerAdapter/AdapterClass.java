package com.frintos.frintos.RecyclerAdapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.frintos.frintos.DisplayThisUser;
import com.frintos.frintos.Model.MyUserData;
import com.frintos.frintos.R;
import com.frintos.frintos.Utility.VerifiedUsers;

import java.util.ArrayList;

public class AdapterClass extends RecyclerView.Adapter<AdapterClass.MyViewHolder> {

    ArrayList<MyUserData> usersArrayList;
    Context context;
    VerifiedUsers verifiedUsers;

    public AdapterClass(ArrayList<MyUserData> usersArrayList, Context context, VerifiedUsers verifiedUsers) {
        this.usersArrayList = usersArrayList;
        this.context = context;
        this.verifiedUsers = verifiedUsers;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MyUserData ud=usersArrayList.get(position);
        holder.textView.setText(ud.getName());
        holder.textView1.setText(ud.getStatus());
        if(verifiedUsers.isVerified(ud.getUid())){
            int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            switch (nightModeFlags) {
                case Configuration.UI_MODE_NIGHT_YES:
                    holder.imageViewVerified.setImageResource(R.drawable.verified_night);
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                    holder.imageViewVerified.setImageResource(R.drawable.verified);
                    break;
            }
            holder.imageViewVerified.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewVerified.setVisibility(View.GONE);
        }
        RequestOptions options = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_account_circle_black_24dp);
        Glide.with(context.getApplicationContext()).load(ud.getThumb()).apply(options).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView,textView1;
        ImageView imageView,imageViewVerified;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textView=itemView.findViewById(R.id.textView13);
            textView1=itemView.findViewById(R.id.textView14);
            imageView=itemView.findViewById(R.id.imageView5);
            cardView=itemView.findViewById(R.id.cardView);
            imageViewVerified=itemView.findViewById(R.id.imageView18);
        }

        @Override
        public void onClick(View v) {
            int pos=this.getAdapterPosition();
            String uid=usersArrayList.get(pos).getUid();
            Intent intent=new Intent(context,DisplayThisUser.class);
            intent.putExtra("uid",uid);
            intent.putExtra("verified",verifiedUsers.isVerified(uid));
            context.startActivity(intent);
        }
    }
}
