package com.ksacp2022.dalili;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ArticlesListAdapter extends RecyclerView.Adapter<ArticleCard> {
    List<HistoryArticle> articleList;
    Context context;
    FirebaseFirestore firestore;

    public ArticlesListAdapter(List<HistoryArticle> articleList, Context context) {
        this.articleList = articleList;
        this.context = context;

        firestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ArticleCard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_card,parent,false);

        return new ArticleCard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleCard holder, int position) {
        int pos=position;
        HistoryArticle article = articleList.get(pos);
        holder.title.setText(article.getTitle());

        holder.article_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,ViewArticleActivity. class);
                intent.putExtra("article_id",article.getId());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }
}

class ArticleCard extends RecyclerView.ViewHolder{

    TextView title;
    LinearLayoutCompat article_card;


    public ArticleCard(@NonNull View itemView) {
        super(itemView);

        title=itemView.findViewById(R.id.title);
        article_card=itemView.findViewById(R.id.article_card);

    }
}
