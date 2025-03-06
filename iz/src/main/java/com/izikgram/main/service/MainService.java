package com.izikgram.main.service;

import com.izikgram.board.entity.Board;
import com.izikgram.main.repository.MainMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MainService {

    @Autowired
    private MainMapper mainMapper;

    public List<Board> getPopularBoardList() {
        return mainMapper.getPopularBoardList();
    }

    public Integer getStressNum(String member_id) {  // int -> Integer로 변경
        Integer stressNum = mainMapper.getStressNum(member_id);
        return stressNum;  // null이 반환될 수 있음
    }

    // 참고로 getMonthlyStress, getPayday 함수 no usages 아닙니당 쓰이는데 저렇게 나오네요

    public List<Map<String, Object>> getMonthlyStress(String member_id, String date) {
        List<Map<String, Object>> stressList = mainMapper.getMonthlyStress(member_id, date);
//        System.out.println("stressList size: " + stressList.size());
        for (Map<String, Object> stress : stressList) {
//            System.out.println("stress: " + stress);
        }
        return stressList;
    }

    public int getPayday(String member_id) {
        return mainMapper.getPayday(member_id);
    }

    public String getStartTime(String member_id) {
        String startTime = mainMapper.getStartTime(member_id);
        return startTime.substring(0, 5);
//        return mainMapper.getStartTime(member_id);
    }

    public String getLunchTime(String member_id) {
        String lunchTime = mainMapper.getLunchTime(member_id);
        return lunchTime.substring(0, 5);
    }

    public String getEndTime(String member_id) {
        String endTime = mainMapper.getEndTime(member_id);
        return endTime.substring(0, 5);
    }
}