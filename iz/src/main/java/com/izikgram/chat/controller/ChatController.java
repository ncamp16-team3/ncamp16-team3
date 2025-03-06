package com.izikgram.chat.controller;

import com.izikgram.chat.dto.ChatRequest;
import com.izikgram.chat.dto.ChatResponse;
import com.izikgram.chat.service.ChatService;
import com.izikgram.global.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/feelchat")
    public ResponseEntity<?> feelchat(@AuthenticationPrincipal CustomUserDetails loginUser, @RequestBody ChatRequest chatRequest) {
        try {
//            log.info("chatRequest : {}", chatRequest);
            String responseMessage = chatService.sendMessageToClova(chatRequest.getMessages(), loginUser.getUsername());
            return ResponseEntity.ok(new ChatResponse(responseMessage));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("클로바 API 호출 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류");
        }
    }

//    @PostMapping("/stress")
//    public ResponseEntity<?> saveStress() {
//
//    }
}
