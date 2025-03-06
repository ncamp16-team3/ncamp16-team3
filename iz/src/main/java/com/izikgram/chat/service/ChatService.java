package com.izikgram.chat.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.izikgram.chat.config.ChatConfig;
import com.izikgram.chat.dto.Message;
import com.izikgram.user.repository.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private final ChatConfig chatConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final UserMapper usermapper;

    @Autowired
    public ChatService(ChatConfig chatConfig, RestTemplate restTemplate, ObjectMapper objectMapper, UserMapper userMapper) {
        this.chatConfig = chatConfig;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.usermapper = userMapper;
    }

    public String sendMessageToClova(List<Message> messages, String memberId) {
        String apiUrl = chatConfig.getApiUrl();
        String apiKey = chatConfig.getApiKey();


        // Clova API 요청 본문 구성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("messages", messages);

        // 필요한 설정 추가
        Map<String, Object> config = new HashMap<>();
        config.put("temperature", 0.7);  // 적절한 값으로 조정
        config.put("maxTokens", 800);    // 적절한 값으로 조정
        requestBody.put("config", config);

        // 모델 ID 설정
        requestBody.put("modelId", "HyperCLOVA X");

        // Clova API 호출을 위한 헤더 설정 (Postman 설정과 일치하도록 수정)
        HttpHeaders headers = new HttpHeaders();
        // Authorization 베어러 토큰 설정
        headers.set("Authorization", "Bearer " + apiKey);

        // 요청 ID 설정 (필요시 UUID로 생성)
        String requestId = UUID.randomUUID().toString().replace("-", "");
        headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", requestId);

        // Content-Type 및 Accept 헤더 설정
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "text/event-stream");  // 스트리밍 응답을 받기 위한 설정

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

//        log.info("ChatService 요청 정보: {}", entity);

        try {
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
            String responseBody = response.getBody();
//            logger.info("Clova API 응답: {}", response.getBody());
            String content = extractContentFromStreamResponse(responseBody);
//            logger.info("추출된 content: {}", content);

            processFeelChatResponse(content, memberId);

            return content;
        } catch (HttpClientErrorException e) {
            logger.error("Clova API 오류 응답: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Clova API 호출 중 오류 발생: " + e.getResponseBodyAsString(), e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    public Map<String, Object> processFeelChatResponse(List<Message> messages, String memberId) {
    public void processFeelChatResponse(String content, String memberId) {
//        String responseContent = sendMessageToClova(messages);

//        Map<String, Object> result = new HashMap<>();
//        result.put("responseMessage", responseContent);

        // 퇴사점수(stress_num) 추출
        Pattern pattern = Pattern.compile("오늘의 퇴사지수는 (\\d+)입니다");
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            int stressNum = Integer.parseInt(matcher.group(1));
//            result.put("stressNum", stressNum);
//            logger.info("추출된 퇴사지수(stress_num): {}", stressNum);

            // DB에 저장/업데이트
            saveOrUpdateStressNum(memberId, stressNum, LocalDate.now().toString());
        }
    }

    private void saveOrUpdateStressNum(String memberId, int stressNum, String date) {
        try {

            // 오늘 날짜의 데이터가 있는지 확인
            Integer existingStressNum = usermapper.getStressNum(memberId, date);

            if (existingStressNum != null) {
                // 이미 존재하면 업데이트
                usermapper.updateStressInfo(memberId, stressNum, date);
//                logger.info("기존 stress_num 업데이트 - 회원: {}, 날짜: {}, 점수: {}",
//                        memberId, date, stressNum);
            } else {
                // 없으면 새로 생성
                usermapper.insertStressInfo(memberId, stressNum, date);
//                logger.info("새 stress_num 저장 - 회원: {}, 날짜: {}, 점수: {}",
//                        memberId, date, stressNum);
            }
        } catch (Exception e) {
            logger.error("stress_num 저장/업데이트 실패: {}", e.getMessage(), e);
        }
    }

    private String extractContentFromStreamResponse(String responseBody) throws IOException {
        // event:result 부분을 찾아서 해당 data 라인의 JSON에서 content 추출
        BufferedReader reader = new BufferedReader(new StringReader(responseBody));
        String line;
        String dataLine = null;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("event:result")) {
                // 다음 라인이 data 라인일 것으로 예상
                dataLine = reader.readLine();
                break;
            }
        }

        if (dataLine != null && dataLine.startsWith("data:")) {
            // data: 접두사 제거하고 JSON 파싱
            String jsonStr = dataLine.substring(5);
            JsonNode rootNode = objectMapper.readTree(jsonStr);

            if (rootNode.has("message") && rootNode.get("message").has("content")) {
                return rootNode.get("message").get("content").asText();
            }
        }

        // content를 찾지 못한 경우 원본 응답 반환
        logger.warn("응답에서 content를 찾을 수 없습니다");
        return responseBody;
    }
}