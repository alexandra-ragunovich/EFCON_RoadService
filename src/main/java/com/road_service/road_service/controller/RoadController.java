package com.road_service.road_service.controller;

import com.road_service.road_service.dto.request.RouteBuildRequest;
import com.road_service.road_service.dto.response.FullRouteResponse;
import com.road_service.road_service.service.RoutePlanningService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/road/")
@RequiredArgsConstructor
public class RoadController {

    private final RoutePlanningService routePlanningService;

    @PostMapping("/build")
    public FullRouteResponse buildRoute(@RequestBody RouteBuildRequest request) {
        return routePlanningService.buildRoute(request);
    }
}