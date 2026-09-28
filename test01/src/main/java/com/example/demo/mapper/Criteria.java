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
