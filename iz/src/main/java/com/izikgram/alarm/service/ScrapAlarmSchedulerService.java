package com.izikgram.alarm.service;

import com.izikgram.alarm.entity.AlarmDto;
import com.izikgram.alarm.repository.AlarmMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ScrapAlarmSchedulerService {

    private final AlarmService alarmService;
    private final AlarmMapper alarmMapper;
    private final SseEmitterService sseEmitterService;

    public ScrapAlarmSchedulerService(AlarmMapper alarmMapper, SseEmitterService sseEmitterService, AlarmService alarmService) {
        this.alarmMapper = alarmMapper;
        this.sseEmitterService = sseEmitterService;
        this.alarmService = alarmService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void sendScrapExpiration() {
        List<AlarmDto> expiringScrapList = alarmMapper.findExpiringScrapJobs(); // 스크랩한 공고 3일전 list
        for(AlarmDto alarmDto : expiringScrapList) {
            if(alarmDto != null) {
                String content = "[" + alarmDto.getCompany_name() + "] 공고가 곧 마감될 예정입니다! 기회를 놓치지 마세요!";
                sseEmitterService.ScrapExpirationTimestampSend(alarmDto.getMember_id(), content);
                alarmService.ScrapExpirationSave(
                        alarmDto.getMember_id(),
                        alarmDto.getJob_rec_id(),
                        alarmDto.getCompany_name(),
                        alarmDto.getExpiration_timestamp(),
                        content
                );
            }
        }
    }
}
