package com.ksacp2022.dalili;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatBotActivity extends AppCompatActivity {

    EditText question_text;
    CardView suggestions_panel;
    RecyclerView chat,suggestions;

    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    List<Question> questionList;
    List<Question> suggestionsList;

    List<ChatMessage> chatMessageList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);
        question_text = findViewById(R.id.question);
        suggestions_panel = findViewById(R.id.suggestions_panel);
        suggestions = findViewById(R.id.suggestions);
        chat = findViewById(R.id.chat);


        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        firestore.collection("questions")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        questionList=new ArrayList<>();
                        for (DocumentSnapshot doc:queryDocumentSnapshots.getDocuments()
                             ) {
                            Map<String,Object> data=doc.getData();
                            Question question=new Question();
                            question.setQuestion(data.get("question").toString());
                            question.setAnswer(data.get("answer").toString());
                            question.setKeywords(data.get("keywords").toString());

                            questionList.add(question);
                        }
                        progressDialog.dismiss();

                        makeText(ChatBotActivity.this,"I'm Ready :)" , LENGTH_LONG).show();



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        makeText(ChatBotActivity.this,"Failed to load inforamtion" , LENGTH_LONG).show();
                    finish();
                    }
                });


        question_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String q= question_text.getText().toString();
                if(q.isEmpty())
                    return;
                suggestionsList=new ArrayList<>();
                for (Question quest:questionList
                     ) {
                    if(quest.getKeywords().contains(q) || quest.getQuestion().contains(q))
                        suggestionsList.add(quest);

                }
                Log.d("quest",suggestionsList.size()+"hhh");
                if(suggestionsList.size()>0)
                {
                    suggestions_panel.setVisibility(View.VISIBLE);
                    SuggestionsListAdapter adapter=new SuggestionsListAdapter(suggestionsList,ChatBotActivity.this);
                    suggestions.setAdapter(adapter);
                }
                else{
                    suggestions_panel.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



    }
    public void ask_bot(Question question) {
        ChatMessage question_message=new ChatMessage(question.getQuestion(), "me");
        ChatMessage answer_message=new ChatMessage(question.getAnswer(), "bot");
        chatMessageList.add(question_message);
        chatMessageList.add(answer_message);
        MessagesListAdapter adapter=new MessagesListAdapter(chatMessageList,ChatBotActivity.this);
        chat.setAdapter(adapter);
        suggestions_panel.setVisibility(View.INVISIBLE);
        question_text.setText("");
        chat.scrollToPosition(chatMessageList.size()-1);
    }
}