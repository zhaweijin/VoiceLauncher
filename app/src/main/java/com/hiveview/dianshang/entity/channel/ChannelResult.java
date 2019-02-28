package com.hiveview.dianshang.entity.channel;

import java.util.List;

public class ChannelResult {
    private List<ChannelData> records;
    private int total;
    private int size;
    private int current;
    private boolean searchCount;
    private int pages;

    public List<ChannelData> getRecords() {
        return records;
    }

    public void setRecords(List<ChannelData> records) {
        this.records = records;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public boolean isSearchCount() {
        return searchCount;
    }

    public void setSearchCount(boolean searchCount) {
        this.searchCount = searchCount;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}
