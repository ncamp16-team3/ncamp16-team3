package com.izikgram.restaurant.controller;

import com.izikgram.restaurant.entity.Restaurant;
import com.izikgram.restaurant.service.NaverCloudMapService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
@RequestMapping("/user")
public class RestaurantController {

    private NaverCloudMapService naverCloudMapService;

    public RestaurantController(NaverCloudMapService naverCloudMapService) {
        this.naverCloudMapService = naverCloudMapService;
    }

    // 페이지 로드를 위한 메서드 추가
    @GetMapping("/nearbyRestaurants")
    public String showNearbyRestaurantsPage() {
        return "user/nearbyRestaurants";  // nearbyRestaurants.html로 이동
    }

    @GetMapping("/api/search-restaurants")
    public @ResponseBody List<Restaurant> findNearbyRestaurants(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "맛집") String category // 추가된 카테고리 파라미터
    ) throws UnsupportedEncodingException {

        return naverCloudMapService.searchNearbyRestaurants(
                latitude,
                longitude,
                category
        );
    }
}