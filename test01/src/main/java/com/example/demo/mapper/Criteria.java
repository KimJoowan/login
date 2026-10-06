package com.example.demo.mapper;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Criteria {
	private long pageNum;
	private long amount;	  
	private String keyword = "";
	private String searchType = "all";

	public void setKeyword(String keyword) {
		this.keyword = keyword == null ? "" : keyword.strip();
	}

	public void setSearchType(String searchType) {
		this.searchType = java.util.Set.of("title", "author").contains(searchType == null ? "" : searchType)
				? searchType : "all";
	}
	
	public Criteria() {
		this(1, 10);
	}
		
	public Criteria(int pageNum, int amount) {
		super();
		this.pageNum = pageNum;
		this.amount = amount;
	}
	
	public long getPageStart() {
	    return (this.pageNum - 1) * this.amount;
	}
	
	
	
}
