package com.frintos.frintos.RecyclerAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.frintos.frintos.DisplayThisUser;
import com.frintos.frintos.Model.TossModel;
import com.frintos.frintos.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;
import java.util.Objects;

public class TossAdaptor extends RecyclerView.Adapter<TossAdaptor.MyViewOnHolder> {
    Context context;
    List<TossModel> tossModels;
    String myuid;

    public TossAdaptor(Context context, List<TossModel> tossModels, String myUid) {
        this.context = context;
        this.tossModels = tossModels;
        this.myuid=myUid;
    }


    @NonNull
    @Override
    public MyViewOnHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.toss_template,parent,false);
        return new MyViewOnHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewOnHolder holder, int position) {
        final TossModel tossModel=tossModels.get(position);
        holder.textView.setText(tossModel.getSender());
        holder.textView1.setText(tossModel.getToss_mes());
        if(!tossModel.getThumb().equals("default"))
        {
            RequestOptions options = new RequestOptions()
                    .circleCrop()
                    .placeholder(R.drawable.ic_account_circle_black_24dp)
                    .error(R.drawable.ic_account_circle_black_24dp);
            Glide.with(context.getApplicationContext()).load(tossModel.getThumb()).apply(options).into(holder.imageView);
        }
        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("toss").child(myuid).child(tossModel.getSender_id());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    TossModel tossModel1 = snapshot.getValue(TossModel.class);
                    if(tossModel.isUpvoted())
                    {
                        holder.imageButton1.setEnabled(false);
                        holder.upt=true;
                        holder.imageButton1.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.ic_baseline_keyboard_arrow_up_24_green));
                    }
                    if(tossModel.isReported())
                    {
                        holder.imageButton2.setEnabled(false);
                        holder.rpt=true;
                        holder.imageButton2.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.ic_baseline_keyboard_arrow_down_24_red));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.setListners();
    }

    @Override
    public int getItemCount() {
        return tossModels.size();
    }

    public class MyViewOnHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView, textView1;
        ImageButton imageButton1, imageButton2, imageButton3;
        ImageView imageView;
        boolean upt=false;
        boolean rpt=false;
        String upvotes;
        public MyViewOnHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.textView25);
            textView1=itemView.findViewById(R.id.textView26);
            imageButton1=itemView.findViewById(R.id.imageButton5);
            imageButton2=itemView.findViewById(R.id.imageButton6);
            imageButton3=itemView.findViewById(R.id.imageButton7);
            imageView=itemView.findViewById(R.id.imageView13);
        }
        public void setListners(){
            imageButton1.setOnClickListener(MyViewOnHolder.this);
            imageButton2.setOnClickListener(MyViewOnHolder.this);
            imageButton3.setOnClickListener(MyViewOnHolder.this);
            imageView.setOnClickListener(MyViewOnHolder.this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            TossModel tossModel = tossModels.get(pos);
            int id = v.getId();
            if (id == R.id.imageButton5) {
                upvoteUser(v, tossModel.getSender_id());
            } else if (id == R.id.imageButton6) {
                ReportUser(v, tossModel.getSender_id());
            } else if (id == R.id.imageButton7) {
                DeleteUser(v, tossModel.getSender_id(), pos);
            } else if (id == R.id.imageView13) {
                Intent intent = new Intent(context, DisplayThisUser.class);
                intent.putExtra("uid", tossModel.getSender_id());
                context.startActivity(intent);
            }
        }

        private void upvoteUser(View v, final String sender_id) {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(sender_id).child("upvotes");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        upvotes= Objects.requireNonNull(snapshot.getValue()).toString();
                        int upvt = Integer.parseInt(upvotes);
                        upvt+=1;
                        upvotes = String.valueOf(upvt);
                        databaseReference.setValue(upvotes).addOnCompleteListener(task -> {
                            if(task.isSuccessful())
                            {
                                FirebaseDatabase.getInstance().getReference().child("toss").child(myuid).child(sender_id).child("upvoted").setValue(true).addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful())
                                    {
                                        imageButton1.setEnabled(false);
                                        Toast.makeText(context, "Succesfully Upvoted", Toast.LENGTH_SHORT).show();
                                        imageButton1.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.ic_baseline_keyboard_arrow_up_24_green));
                                    }
                                    else {
                                        Toast.makeText(context, "Failed to upvote", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(context, "Failed to upvote", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "DataBase Error: "+error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        private void DeleteUser(View v, String sender_id, final int pos) {
            FirebaseDatabase.getInstance().getReference().child("toss").child(myuid).child(sender_id).removeValue().addOnCompleteListener(task -> {
                if(task.isSuccessful())
                {
                    tossModels.remove(pos);
                    notifyItemRemoved(pos);
                }
            });
        }
        private void ReportUser(View v, String sender_id) {
            FirebaseDatabase.getInstance().getReference().child("toss").child(myuid).child(sender_id).child("reported").setValue(true).addOnCompleteListener(task -> {
                if(task.isSuccessful())
                {
                    imageButton2.setEnabled(false);
                    imageButton2.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.ic_baseline_keyboard_arrow_down_24_red));
                    Toast.makeText(context, "You have reported for the user... We will now look into the matter and will update you soon\nThanks for collaborating to make Frintos clean", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
