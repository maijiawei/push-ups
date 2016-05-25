package com.maijiawei.push_ups.ui.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.maijiawei.push_ups.R;
import com.maijiawei.push_ups.util.CommonUtils;

/**
 * Created by Administrator on 2016/4/30.
 */
public class CompleteDialog extends DialogFragment {

    TextView mTvMsg,mTvTime,mTvNumber,mTvKcal,mTvBtn;

    View.OnClickListener mListener;

    int mTime;
    ArrayList<Integer> mNumbers;
    double mKcal;
    String mMsg = "恭喜你完成训练";

    public CompleteDialog setMsg(String msg){
        mMsg = msg;
        return this;
    }

    public CompleteDialog setTime(int time){
        mTime = time;
        return this;
    }

    public CompleteDialog setNumbers(ArrayList<Integer> numbers){
        mNumbers = numbers;
        return this;
    }

    public CompleteDialog setListener(View.OnClickListener listener){
        mListener = listener;
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_complete, container,false);
        mTvMsg = (TextView) view.findViewById(R.id.msg);
        mTvTime = (TextView) view.findViewById(R.id.time);
        mTvNumber = (TextView) view.findViewById(R.id.number);
        mTvKcal = (TextView) view.findViewById(R.id.kcal);
        mTvBtn = (TextView) view.findViewById(R.id.btn);

        int total = 0;
        String numberStr = "";
        for (int i=0;i<mNumbers.size();i++){
            total += mNumbers.get(i);
            if((i+1) == mNumbers.size()){
                numberStr += mNumbers.get(i);
            }else{
                numberStr += mNumbers.get(i) + "+";
            }
        }

        mKcal = (total * 180) / 1000.00;
        mTvMsg.setText(mMsg);
        mTvTime.setText(getResources().getString(R.string.complete_time, mTime / 60,mTime % 60));
        mTvNumber.setText(getResources().getString(R.string.complete_number,numberStr));
        mTvKcal.setText(getResources().getString(R.string.complete_kcal,mKcal));

        mTvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mListener.onClick(v);
            }
        });

        getDialog().setCanceledOnTouchOutside(false);
        return view;
    }

    @Override
    public void onStart() {
        getDialog().getWindow().setLayout(CommonUtils.dp2px(getActivity(),250), CommonUtils.dp2px(getActivity(),240));
        super.onStart();
    }
}
