package com.mangobits.startupkit.user;

import org.hibernate.search.annotations.IndexedEmbedded;

import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import java.util.List;

public class UserResultSearch {


    @IndexedEmbedded
    @ElementCollection(
            fetch = FetchType.EAGER
    )
    private List<UserCard> list;

    private int totalAmount;
    private int pageQuantity;

    public UserResultSearch() {
    }

    public List<UserCard> getList() {
        return list;
    }

    public void setList(List<UserCard> list) {
        this.list = list;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getPageQuantity() {
        return pageQuantity;
    }

    public void setPageQuantity(int pageQuantity) {
        this.pageQuantity = pageQuantity;
    }



}
