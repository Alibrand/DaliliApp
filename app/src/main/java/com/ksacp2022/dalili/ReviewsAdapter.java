package com.ksacp2022.dalili;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewCard> {
    List<Review> reviewList;

    public ReviewsAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewCard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_card,parent,false);

        return new ReviewCard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewCard holder, int position) {
        Review review= reviewList.get(position);
        holder.author.setText(review.getAuthor());
        float rate=Float.parseFloat(review.getRating()) ;
        holder.rating.setRating(rate);
        holder.review.setText(review.getReview());


    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }
}

class ReviewCard extends RecyclerView.ViewHolder {
    TextView author,review;
    RatingBar rating;

    public ReviewCard(@NonNull View itemView) {
        super(itemView);
        author=itemView.findViewById(R.id.author);
        review=itemView.findViewById(R.id.review);
        rating=itemView.findViewById(R.id.rating);
    }
}
