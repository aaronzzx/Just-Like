package com.aaron.justlike.common.http.unsplash.entity.collection;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchCollectionResult implements Parcelable {

    /**
     * total : 237
     * total_pages : 12
     */
    @SerializedName("total")
    private int total;
    @SerializedName("total_pages")
    private int totalPages;
    @SerializedName("results")
    private List<Collection> results;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<Collection> getResults() {
        return results;
    }

    public void setResults(List<Collection> results) {
        this.results = results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.total);
        dest.writeInt(this.totalPages);
        dest.writeTypedList(this.results);
    }

    public SearchCollectionResult() {
    }

    protected SearchCollectionResult(Parcel in) {
        this.total = in.readInt();
        this.totalPages = in.readInt();
        this.results = in.createTypedArrayList(Collection.CREATOR);
    }

    public static final Parcelable.Creator<SearchCollectionResult> CREATOR = new Parcelable.Creator<SearchCollectionResult>() {
        @Override
        public SearchCollectionResult createFromParcel(Parcel source) {
            return new SearchCollectionResult(source);
        }

        @Override
        public SearchCollectionResult[] newArray(int size) {
            return new SearchCollectionResult[size];
        }
    };
}
