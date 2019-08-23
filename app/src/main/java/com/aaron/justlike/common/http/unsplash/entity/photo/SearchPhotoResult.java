package com.aaron.justlike.common.http.unsplash.entity.photo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchPhotoResult implements Parcelable {

    /**
     * total : 237
     * total_pages : 12
     */
    @SerializedName("total")
    private int total;
    @SerializedName("total_pages")
    private int totalPages;
    @SerializedName("results")
    private List<Photo> results;

    @Override
    public String toString() {
        return "SearchPhotoResult{" +
                "total=" + total +
                ", totalPages=" + totalPages +
                ", results=" + results +
                '}';
    }

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

    public List<Photo> getResults() {
        return results;
    }

    public void setResults(List<Photo> results) {
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

    public SearchPhotoResult() {
    }

    protected SearchPhotoResult(Parcel in) {
        this.total = in.readInt();
        this.totalPages = in.readInt();
        this.results = in.createTypedArrayList(Photo.CREATOR);
    }

    public static final Parcelable.Creator<SearchPhotoResult> CREATOR = new Parcelable.Creator<SearchPhotoResult>() {
        @Override
        public SearchPhotoResult createFromParcel(Parcel source) {
            return new SearchPhotoResult(source);
        }

        @Override
        public SearchPhotoResult[] newArray(int size) {
            return new SearchPhotoResult[size];
        }
    };
}
