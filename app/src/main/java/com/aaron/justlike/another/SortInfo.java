package com.aaron.justlike.another;

import org.litepal.crud.LitePalSupport;

public class SortInfo extends LitePalSupport {

    private int id;
    private String sortType;
    private String ascendingOrder;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public String getAscendingOrder() {
        return ascendingOrder;
    }

    public void setAscendingOrder(String ascendingOrder) {
        this.ascendingOrder = ascendingOrder;
    }
}
