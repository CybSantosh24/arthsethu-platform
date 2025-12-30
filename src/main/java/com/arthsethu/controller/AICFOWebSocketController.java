package com.arthsethu.controller;

import com.arthsethu.dto.AICFORequest;
import com.arthsethu.model.BusinessProfile;
import com.arthsethu.model.DailyMetrics;
import com.arthsethu.model.SubscriptionTier;
import com.arthsethu.model.User;
import com.arthsethu.repository.BusinessProfileRepository;
import com.arthsethu.repository.DailyMetricsRepository;
import com.arthsethu.repository.UserRepository;
import com.arthsethu.service.AICFOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class AICFOWebSocketController {
    
    private final AICFOService aicfoService;
    private final UserRepository userRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final DailyMetricsRepository dailyMetricsRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    public AICFOWebSocketController(AICFOService aicfoService,
                                   UserRepository userRepository,
                                   BusinessProfileRepository businessProfileRepository,
                                   DailyMetricsRepository dailyMetricsRepository,
                                   SimpMessagingTemplate messagingTemplate) {
        this.aicfoService = aicfoService;
        this.userRepository = userRepository;
        this.businessProfileRepository = businessProfileRepository;
        this.dailyMetricsRepository = dailyMetricsRepository;
        this.messagingTemplate = messagingTemplate;
    }
    
    @MessageMapping("/ai-cfo/chat")
    public void handleChatMessage(AICFORequest request, SimpMessageHeaderAccessor headerAccessor) {
        Principal user = headerAccessor.getUser();
        if (user == null) {
            sendErrorToUser(headerAccessor.getSessionId(), "Authentication required");
            return;
        }
        
        Optional<User> userOpt = userRepository.findByEmail(user.getName());
        if (userOpt.isEmpty()) {
            sendErrorToUser(headerAccessor.getSessionId(), "User not found");
            return;
        }
        
        User currentUser = userOpt.get();
        
        // Check tier access
        if (currentUser.getTier() != SubscriptionTier.SHIKHAR) {
            sendErrorToUser(headerAccessor.getSessionId(), "Shikhar tier required for AI CFO access");
            return;
        }
        
        // Get business profile
        Optional<BusinessProfile> profileOpt = businessProfileRepository.findByUser(currentUser);
        if (profileOpt.isEmpty()) {
            sendErrorToUser(headerAccessor.getSessionId(), "Business profile required");
            return;
        }
        
        BusinessProfile profile = profileOpt.get();
        List<DailyMetrics> history = dailyMetricsRepository
            .findLast30DaysMetrics(currentUser, java.time.LocalDate.now().minusDays(30));
        
        // Process the query asynchronously
        processQueryAsync(request.getQuery(), profile, history, headerAccessor.getSessionId());
    }
    
    private void processQueryAsync(String query, BusinessProfile profile, List<DailyMetrics> history, String sessionId) {
        // Run AI processing in a separate thread to avoid blocking WebSocket
        new Thread(() -> {
            try {
                String response = aicfoService.processQuery(query, profile, history);
                
                Map<String, Object> result = new HashMap<>();
                result.put("type", "response");
                result.put("content", response);
                result.put("success", true);
                
                messagingTemplate.convertAndSendToUser(sessionId, "/topic/ai-cfo", result);
                
            } catch (Exception e) {
                Map<String, Object> error = new HashMap<>();
                error.put("type", "error");
                error.put("content", "Unable to process your query at this time");
                error.put("success", false);
                
                messagingTemplate.convertAndSendToUser(sessionId, "/topic/ai-cfo", error);
            }
        }).start();
    }
    
    private void sendErrorToUser(String sessionId, String errorMessage) {
        Map<String, Object> error = new HashMap<>();
        error.put("type", "error");
        error.put("content", errorMessage);
        error.put("success", false);
        
        messagingTemplate.convertAndSendToUser(sessionId, "/topic/ai-cfo", error);
    }
}