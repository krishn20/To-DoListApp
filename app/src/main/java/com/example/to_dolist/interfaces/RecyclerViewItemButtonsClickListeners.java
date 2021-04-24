package com.example.to_dolist.interfaces;

public interface RecyclerViewItemButtonsClickListeners
{

    void onItemClick(int position);
    void onLongItemClick(int position);
    void onEditBtnClick(int position);
    void onDeleteBtnClick(int position);
    void onFinishBtnClick(int position);

}
