package com.example.demo.mapper;

import lombok.Data;

@Data
public class PageDto {
	private static final int PAGE_BLOCK_SIZE = 5;

	private long startPage;
	private long endPage;
	private boolean prev, next;

	private long total;
	private Criteria cri;

	public PageDto(Criteria cri, long total) {
		this.cri = cri;
		this.total = total;

		// 페이지당 글 수를 1~50으로 제한
		cri.setAmount(Math.max(1, Math.min(50, cri.getAmount())));

		// 실제 마지막 페이지 계산: 글이 없어도 현재 페이지는 1
		long realEnd = Math.max(1, total / cri.getAmount() + (total % cri.getAmount() == 0 ? 0 : 1));

		// 요청한 페이지를 유효한 범위로 보정
		cri.setPageNum(Math.max(1, Math.min(realEnd, cri.getPageNum())));

		// 보정한 페이지를 기준으로 페이지 번호 묶음 계산
		this.startPage = ((cri.getPageNum() - 1) / PAGE_BLOCK_SIZE) * PAGE_BLOCK_SIZE + 1;

		this.endPage = Math.min(this.startPage + PAGE_BLOCK_SIZE - 1, realEnd);

		this.prev = this.startPage > 1;
		this.next = this.endPage < realEnd;
	}

}
