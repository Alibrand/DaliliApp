package com.ksacp2022.dalili;

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
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AdminArticlesListAdapter extends RecyclerView.Adapter<AdminArticleCard> {
    List<HistoryArticle> articleList;
    Context context;
    FirebaseFirestore firestore;

    public AdminArticlesListAdapter(List<HistoryArticle> articleList, Context context) {
        this.articleList = articleList;
        this.context = context;

        firestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public AdminArticleCard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_article_card,parent,false);

        return new AdminArticleCard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminArticleCard holder, int position) {
        int pos=position;
        HistoryArticle article = articleList.get(pos);
        holder.title.setText(article.getTitle());


        holder.article_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,AdminEditArticleActivity. class);
                intent.putExtra("article_id",article.getId());
                context.startActivity(intent);
            }
        });



        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("history")
                        .document(article.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                articleList.remove(article);
                                notifyItemRemoved(pos);
                                notifyItemRangeChanged(pos, articleList.size());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context,"Failed to remove item" , Toast.LENGTH_LONG).show();
                            }
                        });


            }
        });

    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }
}

class AdminArticleCard extends RecyclerView.ViewHolder{

    TextView title;
    ImageButton delete;
    LinearLayoutCompat article_card;

    public AdminArticleCard(@NonNull View itemView) {
        super(itemView);

        title=itemView.findViewById(R.id.title);
        delete=itemView.findViewById(R.id.delete);
        article_card=itemView.findViewById(R.id.article_card);
    }
}
