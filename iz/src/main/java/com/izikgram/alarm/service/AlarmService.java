package com.izikgram.alarm.service;

import com.izikgram.alarm.entity.AlarmDto;
import com.izikgram.alarm.entity.AlarmType;
import com.izikgram.alarm.repository.AlarmMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class AlarmService {

    @Autowired
    private AlarmMapper alarmMapper;

    public void save(String member_id, int board_type, int board_id, String title, AlarmType alarm_type) {
        alarmMapper.save(member_id, board_type, board_id, title, alarm_type);
    }

    public void ScrapSave(String member_id, String job_rec_id, String company_name, String expiration_timestamp, String content, String url) {
        alarmMapper.ScrapSave(member_id, job_rec_id, company_name, expiration_timestamp, content, url);
    }

    public void ScrapExpirationSave(String member_id, String job_rec_id, String company_name, String expiration_timestamp, String content) {
        alarmMapper.ScrapExpirationSave(member_id, job_rec_id, company_name, expiration_timestamp, content);
    }

    public List<AlarmDto> findAllAlarmsByUser(String member_id) {
        return alarmMapper.findAllAlarmsByUser(member_id);
    }

    public void checkRead(int alarm_id) {
        alarmMapper.checkRead(alarm_id);
    }

    public void checkScrapRead(int alarm_id) {
        alarmMapper.checkScrapRead(alarm_id);
    }

    // 이미 인기 게시글에 대한 알림이 있었는지 확인하는 메서드 (읽음 상태 상관없이)
    public boolean hasPopularAlarm(int board_id) {
        // iz_alarm_board에서 해당 board_id와 AlarmType.POPULAR에 대한 알림이 있는지 확인
        return alarmMapper.countPopularAlarm(board_id) > 0;
    }

    public int countTotalAlarm(String member_id) {
        return alarmMapper.countTotalAlarm(member_id);
    }

}
