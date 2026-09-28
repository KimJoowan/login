package com.example.demo.ratelimit.storage;

public class CapacityExceededException extends RuntimeException  {
	
	private static final long serialVersionUID = 1L;

	public CapacityExceededException() {
		super("Rate limit bucket storage is full");
	}
}
