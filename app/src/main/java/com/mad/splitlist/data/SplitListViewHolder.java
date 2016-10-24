/*
 * Copyright (C) 2016 Kenneth Wong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mad.splitlist.data;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mad.splitlist.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Viewholder class for a Split List card used by the FirebaseRecyclerAdapter in the MainActivity.
 */
public class SplitListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.splitlist_cv)
    public CardView cardSplitList;
    @BindView(R.id.splitlist_tv_name)
    public TextView tvListName;
    @BindView(R.id.splitlist_tv_numofitems)
    public TextView tvNumOfItems;
    @BindView(R.id.splitlist_tv_total)
    public TextView tvTotal;
    @BindView(R.id.splitlist_layout_overflow)
    public RelativeLayout layoutOverflow;
    @BindView(R.id.splitlist_btn_share)
    public Button btnShare;

    public SplitListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
