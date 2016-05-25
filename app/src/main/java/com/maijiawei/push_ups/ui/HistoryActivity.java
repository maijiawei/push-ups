package com.maijiawei.push_ups.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.maijiawei.push_ups.model.HistoryModel;
import com.maijiawei.push_ups.R;

/**
 * Created by Administrator on 2016/4/30.
 */
public class HistoryActivity extends BaseActivity {

    RecyclerView mRecyclerView;
    ImageButton mImgBtnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mImgBtnBack = (ImageButton) findViewById(R.id.back);
        mImgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter adapter = new MyAdapter();
        adapter.update(HistoryModel.getAll());
        mRecyclerView.setAdapter(adapter);
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        List<HistoryModel> mData = new ArrayList<>();

        public MyAdapter(){

        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyAdapter.ViewHolder holder, int position) {
            final HistoryModel history = mData.get(position);

            int total = 0;
            String numberStr = "";
            String[] numbers = history.val.split("-");
            for (int i=0;i<numbers.length;i++){
                total += Integer.parseInt(numbers[i]);
                if((i+1) == numbers.length){
                    numberStr += numbers[i];
                }else{
                    numberStr += numbers[i] + "+";
                }
            }

            double kcal = (total * 180) / 1000.00;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
            String sd = sdf.format(new Date(history.createTime));

            holder.createTime.setText(sd);
            holder.time.setText(getResources().getString(R.string.complete_time, history.time / 60,history.time % 60));
            holder.number.setText(getResources().getString(R.string.complete_number,numberStr));
            holder.kcal.setText(getResources().getString(R.string.complete_kcal,kcal));

            holder.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    CharSequence[] options = {"删除"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                    builder.setItems(options, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            HistoryModel.delete(HistoryModel.class,history.getId());
                            update(HistoryModel.getAll());
                        }
                    });
                    builder.show();

                    return true;
                }
            });

        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public void update(List<HistoryModel> data){
            mData = data;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView createTime,time,number,kcal;
            View view;

            public ViewHolder(View itemView) {
                super(itemView);
                view = itemView;
                createTime = (TextView) itemView.findViewById(R.id.create_time);
                time = (TextView) itemView.findViewById(R.id.time);
                number = (TextView) itemView.findViewById(R.id.number);
                kcal = (TextView) itemView.findViewById(R.id.kcal);
            }
        }
    }

}

