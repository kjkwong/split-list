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

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.mad.splitlist.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Viewholder class for an ItemType list item used by the FirebaseRecyclerAdapter in the
 * ListContentActivity.
 */
public class ItemTypeViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.content_item_tv_name)
    public TextView tvItemName;
    @BindView(R.id.content_item_tv_price)
    public TextView tvPrice;
    @BindView(R.id.content_item_tv_quantity)
    public TextView tvQuantity;
    @BindView(R.id.content_item_tv_total)
    public TextView tvTotal;
    @BindView(R.id.content_item_chk_selected)
    public CheckBox chkSelected;
    @BindView(R.id.content_item_iv_delete)
    public ImageView ivDelete;

    public ItemTypeViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
