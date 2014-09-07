package com.yingshi.toutiao.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Pagination<T extends Parcelable> implements Parcelable{
	
	public static final Parcelable.Creator<Pagination<?>> CREATOR = new Parcelable.Creator<Pagination<?>>() {
		@SuppressWarnings("rawtypes")
		public Pagination<?> createFromParcel(Parcel in) {
			return new Pagination(in);
		}

		public Pagination<?>[] newArray(int size) {
			return new Pagination[size];
		}
	};

	public Pagination(){}
	
	private Pagination(Parcel in) {
		currentPage = in.readInt();
		totalCounts = in.readInt();
		numPerPage = in.readInt();
		in.readList(items, getClass().getClassLoader());
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(currentPage);
		dest.writeInt(totalCounts);
		dest.writeInt(numPerPage);
		dest.writeList(items);
	}
	
    private int currentPage;
    private int totalCounts;
    private int numPerPage;
    private List<T> items = new ArrayList<T>();

    public int getCurrentPage() {
        return currentPage;
    }
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    public int getTotalCounts() {
        return totalCounts;
    }
    public void setTotalCounts(int totalCounts) {
        this.totalCounts = totalCounts;
    }
    public int getNumPerPage() {
        return numPerPage;
    }
    public void setNumPerPage(int numPerPage) {
        this.numPerPage = numPerPage;
    }
    public List<T> getItems() {
        return items;
    }
    public void setItems(List<T> items) {
        this.items = items;
    }
}
