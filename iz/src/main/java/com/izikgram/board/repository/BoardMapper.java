package com.izikgram.board.repository;

import com.izikgram.board.entity.Board;
import com.izikgram.board.entity.BoardDto;
import com.izikgram.board.entity.CommentDto;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface BoardMapper {

    //게시판 이름
    @Select("select board_name " +
            "from iz_board_type " +
            "where board_type = #{board_type}")
    String getBoardName(@Param("board_type") int board_type);

    // 게시판 전체 조회
    @Select("<script>" +
            "SELECT b.*, COALESCE(COUNT(c.comment_id), 0) AS comment_count " +
            "FROM iz_board0${board_type} b " +
            "LEFT JOIN iz_board0${board_type}_comment c ON b.board_id = c.board_id " +
            "WHERE b.board_type = #{board_type} " +
            "GROUP BY b.board_id " +
            "<if test='sort != null'>" +
            "    <choose>" +
            "        <when test='sort == \"newest\"'> ORDER BY b.reg_date DESC </when>" +
            "        <when test='sort == \"oldest\"'> ORDER BY b.reg_date ASC </when>" +
            "        <when test='sort == \"likes\"'> ORDER BY b.like_count DESC </when>" +
            "        <when test='sort == \"comments\"'> ORDER BY comment_count DESC </when>" +
            "    </choose>" +
            "</if>" +
            "LIMIT #{limit} OFFSET #{offset} " +
            "</script>")
    List<BoardDto> getBoardList(
            @Param("board_type") int board_type,
            @Param("sort") String sort,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    // 글작성
    @Insert("insert into iz_board01(writer_id,title,content,board_type) " +
            "value(#{writer_id}, #{title}, #{content}, #{board_type})")
    void insertBoard01(@Param("writer_id") String writer_id, @Param("title") String title, @Param("content") String content,
                       @Param("board_type") int board_type);

    @Insert("insert into iz_board02(writer_id,title,content,board_type) " +
            "value(#{writer_id}, #{title}, #{content}, #{board_type})")
    void insertBoard02(@Param("writer_id") String writer_id, @Param("title") String title, @Param("content") String content,
                       @Param("board_type") int board_type);

    //게시글 상세보기
    @Select("SELECT * " +
            "FROM iz_board01  " +
            "WHERE board_id = #{board_id} ")
    Board selectBoard01(@Param("board_id") int board_id);

    @Select("SELECT * " +
            "FROM iz_board02 " +
            "WHERE board_id = #{board_id} ")
    Board selectBoard02(@Param("board_id") int board_id);

    // 게시글 상세보기 댓글 리스트 조회
    @Select("select c.comment_id, c.board_id, c.writer_id, c.comment_content, c.reg_date, " +
            "m.nickname " +
            "from iz_board01_comment c " +
            "join iz_member m " +
            "on c.writer_id = m.member_id " +
            "where board_id = #{board_id} order by comment_id desc")
    List<CommentDto> commentGetList01(@Param("board_id") int board_id);

    @Select("select c.comment_id, c.board_id, c.writer_id, c.comment_content, c.reg_date, " +
            "m.nickname " +
            "from iz_board02_comment c " +
            "join iz_member m " +
            "on c.writer_id = m.member_id " +
            "where board_id = #{board_id} order by comment_id desc")
    List<CommentDto> commentGetList02(@Param("board_id") int board_id);


    //자유/하소연 게시글 업데이트
    @Update("UPDATE iz_board01 " +
            "SET " +
            "    title = #{title}, " +
            "    content = #{content} " +
            "WHERE " +
            "    board_id = #{board_id} ")
    void updateBoard01(@Param("board_id") int board_id,
                       @Param("title") String title,
                       @Param("content")String content);

    @Update("UPDATE iz_board02 " +
            "SET " +
            "    title = #{title}, " +
            "    content = #{content} " +
            "WHERE " +
            "    board_id = #{board_id} ")
    void updateBoard02(@Param("board_id") int board_id,
                       @Param("title") String title,
                       @Param("content")String content);

    //좋아요 구현
    // 회원의 게시글 좋아요 개수
    @Select("SELECT COUNT(*) FROM iz_board01_like WHERE board_id=#{board_id} and member_id=#{member_id}")
    int countLikeByMember(@Param("board_id") int boardId, @Param("member_id") String memberId);

    // 회원의 게시글 싫어요 개수
    @Select("SELECT COUNT(*) FROM iz_board01_dislike WHERE board_id=#{board_id} and member_id=#{member_id}")
    int countDislikeByMember(@Param("board_id") int boardId, @Param("member_id") String memberId);


    // 좋아요 생성
    @Insert("INSERT INTO iz_board01_like VALUES(#{board_id}, #{member_id})")
    void insertLike(@Param("board_id") int boardId, @Param("member_id") String memberId);

    // 싫어요 생성
    @Insert("INSERT INTO iz_board01_dislike VALUES(#{board_id}, #{member_id})")
    void insertDislike(@Param("board_id") int boardId, @Param("member_id") String memberId);

    // 좋아요 삭제
    @Delete("DELETE FROM iz_board01_like WHERE board_id=#{board_id} AND member_id=#{member_id}")
    void deleteLike(@Param("board_id") int boardId, @Param("member_id") String memberId);

    // 싫어요 삭제
    @Delete("DELETE FROM iz_board01_dislike WHERE board_id=#{board_id} AND member_id=#{member_id}")
    void deleteDislike(@Param("board_id") int boardId, @Param("member_id") String memberId);


    // TODO iz_board01의 like, dislike + 1, -1
    // 게시글 like_count +1
    @Update("update iz_board01 set like_count = like_count+1 where board_id = #{board_id}")
    void upLikeCount(@Param("board_id") int boardId);

    // 게시글 like_count -1
    @Update("update iz_board01 set like_count = like_count-1 where board_id = #{board_id}")
    void downLikeCount(@Param("board_id") int boardId);

    // 게시글 disLike_count +1
    @Update("update iz_board01 set dislike_count = dislike_count+1 where board_id = #{board_id}")
    void upDislikeCount(@Param("board_id") int boardId);

    // 게시글 disLike_count -1
    @Update("update iz_board01 set dislike_count = dislike_count-1 where board_id = #{board_id}")
    void downDislikeCount(@Param("board_id") int boardId);

    // TODO iz_board01의 like, dislike 개수 반환
    // 게시글의 좋아요/싫어요 전체 수
    @Select("select * from iz_board01 where board_id = #{board_id}")
    Board totalLikeDisLike(@Param("board_id") int boardId);

    // 회원의 게시글 좋아요 개수
    @Select("SELECT COUNT(*) FROM iz_board02_like WHERE board_id=#{board_id} and member_id=#{member_id}")
    int countLikeByMember02(@Param("board_id") int boardId, @Param("member_id") String memberId);

    // 회원의 게시글 싫어요 개수
    @Select("SELECT COUNT(*) FROM iz_board02_dislike WHERE board_id=#{board_id} and member_id=#{member_id}")
    int countDislikeByMember02(@Param("board_id") int boardId, @Param("member_id") String memberId);

    // 좋아요 생성
    @Insert("INSERT INTO iz_board02_like VALUES(#{board_id}, #{member_id})")
    void insertLike02(@Param("board_id") int boardId, @Param("member_id") String memberId);

    // 싫어요 생성
    @Insert("INSERT INTO iz_board02_dislike VALUES(#{board_id}, #{member_id})")
    void insertDislike02(@Param("board_id") int boardId, @Param("member_id") String memberId);

    // 좋아요 삭제
    @Delete("DELETE FROM iz_board02_like WHERE board_id=#{board_id} AND member_id=#{member_id}")
    void deleteLike02(@Param("board_id") int boardId, @Param("member_id") String memberId);

    // 싫어요 삭제
    @Delete("DELETE FROM iz_board02_dislike WHERE board_id=#{board_id} AND member_id=#{member_id}")
    void deleteDislike02(@Param("board_id") int boardId, @Param("member_id") String memberId);

    // TODO iz_board02의 like, dislike + 1, -1
    // 게시글 like_count +1
    @Update("update iz_board02 set like_count = like_count+1 where board_id = #{board_id}")
    void upLikeCount02(@Param("board_id") int boardId);

    // 게시글 like_count -1
    @Update("update iz_board02 set like_count = like_count-1 where board_id = #{board_id}")
    void downLikeCount02(@Param("board_id") int boardId);

    // 게시글 disLike_count +1
    @Update("update iz_board02 set dislike_count = dislike_count+1 where board_id = #{board_id}")
    void upDislikeCount02(@Param("board_id") int boardId);

    // 게시글 disLike_count -1
    @Update("update iz_board02 set dislike_count = dislike_count-1 where board_id = #{board_id}")
    void downDislikeCount02(@Param("board_id") int boardId);

    // TODO iz_board01의 like, dislike 개수 반환
    // 게시글의 좋아요/싫어요 전체 수
    @Select("select * from iz_board02 where board_id = #{board_id}")
    Board totalLikeDisLike02(@Param("board_id") int boardId);


    //댓글 저장
    @Insert("insert into iz_board01_comment(board_id, writer_id, comment_content) " +
            "values(#{board_id}, #{writer_id}, #{comment_content})")
    void addComment01(@Param("board_id") int board_id, @Param("writer_id") String writer_id,
                      @Param("comment_content") String comment_content);

    @Insert("insert into iz_board02_comment(board_id, writer_id, comment_content) " +
            "values(#{board_id}, #{writer_id}, #{comment_content})")
    void addComment02(@Param("board_id") int board_id, @Param("writer_id") String writer_id,
                      @Param("comment_content") String comment_content);

    // 작성한 댓글 조회
    @Select("select c.writer_id, c.comment_id, c.comment_content, c.reg_date, m.nickname " +
            "from iz_board01_comment c " +
            "join iz_member m on c.writer_id = m.member_id " +
            "where c.board_id = #{board_id} " +
            "order by c.comment_id desc " +
            "limit 1 ")
    CommentDto getLastComment01(@Param("board_id") int boardId);

    @Select("select c.writer_id, c.comment_id, c.comment_content, c.reg_date, m.nickname " +
            "from iz_board02_comment c " +
            "join iz_member m on c.writer_id = m.member_id " +
            "where c.board_id = #{board_id} " +
            "order by c.comment_id desc " +
            "limit 1 ")
    CommentDto getLastComment02(@Param("board_id") int boardId);

    //인기게시판(자유게시판)
    @Select("SELECT b.*, " +
            "COALESCE(COUNT(c.comment_id), 0) AS comment_count " +
            "FROM iz_board_popular p " +
            "JOIN iz_board01 b ON p.board_id = b.board_id " +
            "LEFT JOIN iz_board01_comment c ON b.board_id = c.board_id " +
            "WHERE p.board_type = 1 AND b.like_count >= 5 " +
            "GROUP BY b.board_id, p.like_count, b.reg_date " +
            "ORDER BY p.like_count DESC, count(c.comment_id) desc, b.reg_date DESC " +
            "LIMIT 3")
    List<BoardDto> getPopularBoardListBoard1();

    @Select("SELECT b.*, " +
            "COALESCE(COUNT(c.comment_id), 0) AS comment_count " +
            "FROM iz_board_popular p " +
            "JOIN iz_board02 b ON p.board_id = b.board_id " +
            "LEFT JOIN iz_board02_comment c ON b.board_id = c.board_id " +
            "WHERE p.board_type = 2 AND b.like_count >= 5 " +
            "GROUP BY b.board_id, p.like_count, b.reg_date " +
            "ORDER BY p.like_count DESC, count(c.comment_id) desc, b.reg_date DESC " +
            "LIMIT 3")
    List<BoardDto> getPopularBoardListBoard2();



    // 현재 게시글의 board01 좋아요 개수 조회
    @Select("SELECT like_count FROM iz_board01 WHERE board_id = #{board_id}")
    int getLikeCount(@Param("board_id")int boardId);

    // 현재 게시글의 board02 좋아요 개수 조회
    @Select("SELECT like_count FROM iz_board02 WHERE board_id = #{board_id}")
    int getLikeCount02(@Param("board_id") int boardId);

    // 해당 게시글이 이미 인기 게시판에 등록되었는지 확인
    @Select("SELECT EXISTS(SELECT 1 FROM iz_board_popular WHERE board_id = #{board_id} and board_type = #{board_type})")
    boolean isPopularBoard(@Param("board_id")int boardI, @Param("board_type") int boardType);

    // 인기 테이블에 등록
    @Insert("INSERT INTO iz_board_popular (board_id, board_type, like_count, reg_date) " +
            "SELECT #{board_id}, #{board_type}, " +
            "  CASE " +
            "    WHEN #{board_type} = 1 THEN (SELECT IFNULL(like_count, 0) FROM iz_board01 WHERE board_id = #{board_id}) " +
            "    WHEN #{board_type} = 2 THEN (SELECT IFNULL(like_count, 0) FROM iz_board02 WHERE board_id = #{board_id}) " +
            "    ELSE 0 " +
            "  END, " +
            "  CASE " +
            "    WHEN #{board_type} = 1 THEN (SELECT reg_date FROM iz_board01 WHERE board_id = #{board_id}) " +
            "    WHEN #{board_type} = 2 THEN (SELECT reg_date FROM iz_board02 WHERE board_id = #{board_id}) " +
            "    ELSE NOW() " +
            "  END")
    void insertPopularBoard(@Param("board_id") int boardId, @Param("board_type") int boardType);

    //등록 되어있다면 업데이트
    @Update("UPDATE iz_board_popular " +
            "SET like_count = #{like_count} " +
            "WHERE board_id = #{board_id} AND board_type = #{board_type}")
    void updatePopularBoard(@Param("board_id") int boardId,
                            @Param("board_type") int boardType,
                            @Param("like_count") int likeCount);

    // 좋아요 수가 4개 이하면 인기테이블에서 삭제
    @Delete("DELETE FROM iz_board_popular WHERE board_id = #{board_id} AND board_type = #{board_type}")
    void deletePopularBoard(@Param("board_id") int boardId, @Param("board_type") int boardType);


    // 내 게시물
    @Select("SELECT b.*, " +
            "COALESCE(COUNT(c.comment_id), 0) AS comment_count " +
            "FROM iz_board_type t " +
            "JOIN iz_board01 b ON t.board_type = b.board_type " +
            "LEFT JOIN iz_board01_comment c ON b.board_id = c.board_id " +
            "WHERE b.writer_id = #{writer_id}  " +
            "GROUP BY b.board_id " +
            "ORDER BY reg_date DESC " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<BoardDto> getMyBoardList01(@Param("writer_id") String writer_id,
                                    @Param("limit") int limit,
                                    @Param("offset") int offset);

    @Select("SELECT b.*, " +
            "COALESCE(COUNT(c.comment_id), 0) AS comment_count " +
            "FROM iz_board_type t " +
            "JOIN iz_board02 b ON t.board_type = b.board_type " +
            "LEFT JOIN iz_board02_comment c ON b.board_id = c.board_id " +
            "WHERE b.writer_id = #{writer_id}  " +
            "GROUP BY b.board_id " +
            "ORDER BY reg_date DESC " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<BoardDto> getMyBoardList02(@Param("writer_id") String writer_id,
                                    @Param("limit") int limit,
                                    @Param("offset") int offset);

    //댓글 삭제
    @Delete("delete from iz_board01_comment " +
            "where comment_id = #{comment_id} and board_id = #{board_id}")
    void deleteComment01(@Param("board_id") int boardId, @Param("comment_id") int commentId);

    @Delete("delete from iz_board02_comment " +
            "where comment_id = #{comment_id} and board_id = #{board_id}")
    void deleteComment02(@Param("board_id") int boardId, @Param("comment_id") int commentId);

    //댓글 수정
    @Update("update iz_board01_comment set comment_content = #{comment_content}" +
            "where comment_id = #{comment_id} and board_id = #{board_id}")
    int updateComment01(@Param("comment_content") String comment_content,
                        @Param("board_id") int boardId,
                        @Param("comment_id") int commentId);

    @Update("update iz_board02_comment set comment_content = #{comment_content}" +
            "where comment_id = #{comment_id} and board_id = #{board_id}")
    int updateComment02(@Param("comment_content") String comment_content,
                        @Param("board_id") int boardId,
                        @Param("comment_id") int commentId);

    // 게시글 삭제
    @Delete("delete from iz_board01_comment where board_id = #{board_id}")
    void deleteBoardComment01(@Param("board_id") int board_id);

    @Delete("delete from iz_board01_dislike where board_id = #{board_id}")
    void deleteBoardLike01(@Param("board_id") int board_id);

    @Delete("delete from iz_board01_like where board_id = #{board_id}")
    void deleteBoardDislike01(@Param("board_id") int board_id);

    @Delete("delete from iz_board01 where board_id = #{board_id}")
    void deleteBoard01(@Param("board_id") int board_id);

    @Delete("delete from iz_board02_comment where board_id = #{board_id}")
    void deleteBoardComment02(@Param("board_id") int board_id);

    @Delete("delete from iz_board02_dislike where board_id = #{board_id}")
    void deleteBoardLike02(@Param("board_id") int board_id);

    @Delete("delete from iz_board02_like where board_id = #{board_id}")
    void deleteBoardDislike02(@Param("board_id") int board_id);

    @Delete("delete from iz_board02 where board_id = #{board_id}")
    void deleteBoard02(@Param("board_id") int board_id);

    @Select("select count(*) from iz_board01_like where member_id=#{member_id} and board_id=#{board_id}")
    int isLike01(@Param("member_id") String member_id, @Param("board_id") int board_id);

    @Select("select count(*) from iz_board01_dislike where member_id=#{member_id} and board_id=#{board_id}")
    int isDislike01(@Param("member_id") String member_id, @Param("board_id") int board_id);

    @Select("select count(*) from iz_board02_like where member_id=#{member_id} and board_id=#{board_id}")
    int isLike02(@Param("member_id") String member_id, @Param("board_id") int board_id);

    @Select("select count(*) from iz_board02_dislike where member_id=#{member_id} and board_id=#{board_id}")
    int isDislike02(@Param("member_id") String member_id, @Param("board_id") int board_id);
}
