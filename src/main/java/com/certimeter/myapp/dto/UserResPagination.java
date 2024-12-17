package com.certimeter.myapp.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserResPagination {
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private List<UserDTO> data;

    private UserResPagination(Builder builder) {
        this.pageNo = builder.pageNo;
        this.pageSize = builder.pageSize;
        this.totalElements = builder.totalElements;
        this.totalPages = builder.totalPages;
        this.last = builder.last;
        this.data = builder.data;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int pageNo;
        private int pageSize;
        private long totalElements;
        private int totalPages;
        private boolean last;
        private List<UserDTO> data;

        public Builder pageNo(int pageNo) {
            this.pageNo = pageNo;
            return this;
        }

        public Builder pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder totalElements(long totalElements) {
            this.totalElements = totalElements;
            return this;
        }

        public Builder totalPages(int totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public Builder last(boolean last) {
            this.last = last;
            return this;
        }

        public Builder data(List<UserDTO> data) {
            this.data = data;
            return this;
        }

        public UserResPagination build() {
            return new UserResPagination(this);
        }
    }
}
