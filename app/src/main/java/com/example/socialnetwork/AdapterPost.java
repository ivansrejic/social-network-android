package com.example.socialnetwork;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.MyHolder> {

    Context context;
    List<PostModel> postovi;

    String emailUser;
    //String time;

    public AdapterPost(Context context, List<PostModel> postovi)
    {
        this.context = context;
        this.postovi = postovi;
        emailUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_post, parent, false );

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        //String pid1 = postovi.get(position).getPid();
        String desciption1 = postovi.get(position).getDescription();
        //String uid1 = postovi.get(position).getUid();
        String title1 = postovi.get(position).getTitle();
        String username1 = postovi.get(position).getUsername();
        String email1 = postovi.get(position).getEmail();
        String time = postovi.get(position).getTime();

        holder.username.setText(username1);
        holder.email.setText(email1);
        holder.description.setText(desciption1);
        holder.title.setText(title1);



        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deletePost(time);
                //Toast.makeText(context,"Dobro je",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void deletePost(String vreme) {

        Query query = FirebaseDatabase.getInstance().getReference("Posts")
                .orderByChild("time").equalTo(vreme);


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    if(ds.child("email").getValue(String.class).equals(emailUser))
                    {
                        ds.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context,"Ne mozes to da obrises",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return postovi.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView username,title,description,email;
        ImageButton buttonDelete;
        //ImageView imageOnPost;



        public MyHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.usernameUsername);
            title = itemView.findViewById(R.id.titleAjdi);
            email = itemView.findViewById(R.id.emailUser);
            description = itemView.findViewById(R.id.descriptionAjdi);

            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            //imageOnPost = itemView.findViewById(R.id.userPicture);

        }
    }
}
