package com.example.rememberwords;

import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LayoutAdapter extends RecyclerView.Adapter<LayoutAdapter.LayoutViewHolder> {

    List<Word> allWords = new ArrayList<>();
    private boolean useCardView;
    private WordViewModel wordViewModel;

    public LayoutAdapter(boolean useCardView,WordViewModel wordViewModel) {
        this.useCardView = useCardView;
        this.wordViewModel = wordViewModel;
    }

    public void setAllWords(List<Word> allWords) {
        this.allWords = allWords;
    }

    //与RecyclerView绑定时调用
    @NonNull
    @Override
    public LayoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView;
        if (useCardView) {
            itemView = layoutInflater.inflate(R.layout.card_layout_2,parent,false);
        }else {
            itemView = layoutInflater.inflate(R.layout.normal_layout_2,parent,false);
        }
        return  new LayoutViewHolder(itemView);
    }

    //创建ViewHolder时调用
    @Override
    public void onBindViewHolder(@NonNull final LayoutViewHolder holder, int position) {
        final Word word = allWords.get(position);
        holder.TvNumber.setText(String.valueOf(position + 1));
        holder.TvEnglish.setText(word.getWord());
        holder.TvChinese.setText(word.getChineseMeaning());

        holder.aSwitchHiddenHhinese.setOnCheckedChangeListener(null);
        if (word.isHiddenChinese()){
            holder.TvChinese.setVisibility(View.GONE);
            holder.aSwitchHiddenHhinese.setChecked(true);
        }else {
            holder.TvChinese.setVisibility(View.VISIBLE);
            holder.aSwitchHiddenHhinese.setChecked(false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.baidu.com/sf_fanyi/?aldtype=30710#en/zh/" + holder.TvEnglish.getText());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                holder.itemView.getContext().startActivity(intent);
            }
        });

        holder.aSwitchHiddenHhinese.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    holder.TvChinese.setVisibility(View.GONE);
                    word.setHiddenChinese(true);
                    wordViewModel.updateWords(word);
                }else {
                    holder.TvChinese.setVisibility(View.VISIBLE);
                    word.setHiddenChinese(false);
                    wordViewModel.updateWords(word);
                }
            }
        });
    }

    //返回列表数据个数
    @Override
    public int getItemCount() {
        return allWords.size();
    }

    //管理RecyclerView界面布局
       static class LayoutViewHolder extends RecyclerView.ViewHolder {
        TextView TvNumber,TvEnglish,TvChinese;
        Switch aSwitchHiddenHhinese;
        public LayoutViewHolder(@NonNull View itemView) {
            super(itemView);
            TvNumber = itemView.findViewById(R.id.tv_number);
            TvEnglish = itemView.findViewById(R.id.tv_english);
            TvChinese = itemView.findViewById(R.id.tv_chinese);
            aSwitchHiddenHhinese = itemView.findViewById(R.id.switch_hidden_chinese);
        }
    }
}
