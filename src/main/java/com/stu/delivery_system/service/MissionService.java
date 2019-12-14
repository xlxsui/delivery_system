package com.stu.delivery_system.service;

import com.stu.delivery_system.domain.Mission;
import com.stu.delivery_system.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MissionService {
    Mission saveMission(Mission mission);

    void removeMission(Long id);

    Optional<Mission> getMissionById(Long id);

    Page<Mission> listMissionByContent(User user, String content, Pageable pageable);
}
