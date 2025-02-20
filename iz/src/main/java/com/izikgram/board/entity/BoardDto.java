package com.izikgram.board.entity;

import lombok.Data;

@Data
public class BoardDto {

    // 게시판 종류 테이블
    private int board_type;
    private String board_name;

    // 게시판 테이블
    private int board_id;
    private String writer_id;
    private String title;
    private String content;
    private String reg_date;

    // 좋아요/싫어요
    private int like_count;
    private int dislike_count;

    private int comment_count;
}
