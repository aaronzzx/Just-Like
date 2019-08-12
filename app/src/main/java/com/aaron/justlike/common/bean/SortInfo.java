package com.aaron.justlike.common.bean;

import org.litepal.crud.LitePalSupport;

public class SortInfo extends LitePalSupport {

    private String sortType;
    private String ascendingOrder;

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
