package com.thaid.asylum.Blinds;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thaid.asylum.R;

public class BlindsAdapter extends BaseAdapter{
    private BlindsFragment.Blind[] blinds;
    private LayoutInflater layoutInflater;
    private BlindsFragment blindsFragment;

    public BlindsAdapter(BlindsFragment blindsFragment, Context context, BlindsFragment.Blind[] blinds){
        this.blinds = blinds;
        layoutInflater = (LayoutInflater.from(context));
        this.blindsFragment = blindsFragment;
    }


    @Override
    public int getCount() {
        return blinds.length;
    }

    @Override
    public Object getItem(int i) {
        return blinds[i];
    }

    @Override
    public long getItemId(int i) {
        return blinds[i].getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row;
        ViewHolder holder;

        if(view == null) {
            row = layoutInflater.inflate(R.layout.blind, viewGroup, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        }else{
            row = view;
            holder = (ViewHolder) row.getTag();
        }

        holder.blindName.setText(blinds[i].getNameId());
        if(i==0){
            blindsFragment.setButtonListeners(holder.buttonOpen, holder.buttonClose, holder.buttonStop, i, true);
        }else {
            blindsFragment.setButtonListeners(holder.buttonOpen, holder.buttonClose, holder.buttonStop, i, false);
        }

        return row;
    }

    private static class ViewHolder{

        private final TextView blindName;
        private final FloatingActionButton buttonOpen;
        private final FloatingActionButton buttonStop;
        private final FloatingActionButton buttonClose;

        private ViewHolder(View row){
            blindName = row.findViewById((R.id.blindName));
            buttonOpen = row.findViewById((R.id.buttonOpen));
            buttonStop = row.findViewById((R.id.buttonStop));
            buttonClose = row.findViewById((R.id.buttonClose));
        }
    }
}
