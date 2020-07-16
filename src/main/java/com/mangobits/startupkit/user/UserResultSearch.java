package com.mangobits.startupkit.user;

import java.util.List;

public class UserResultSearch {

    private List<UserCard> list;

    private long totalAmount;

    private long pageQuantity;

    public UserResultSearch() {
    }

    public List<UserCard> getList() {
        return list;
    }

    public void setList(List<UserCard> list) {
        this.list = list;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getPageQuantity() {
        return pageQuantity;
    }

    public void setPageQuantity(long pageQuantity) {
        this.pageQuantity = pageQuantity;
    }



}
