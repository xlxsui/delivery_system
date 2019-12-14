package com.stu.delivery_system.service;


import com.stu.delivery_system.domain.Mission;
import com.stu.delivery_system.domain.User;
import com.stu.delivery_system.repository.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.util.Optional;

/**
 * 业务逻辑层，处理订单，调用Repository处理数据
 */
@Service
public class MissionServiceImpl implements MissionService {

    @Autowired
    private MissionRepository missionRepository;

    @Transactional
    @Override
    public Mission saveMission(Mission mission) {
        Mission returnMission = missionRepository.save(mission);
        return returnMission;
    }

    @Transactional
    @Override
    public void removeMission(Long id) {
        missionRepository.deleteById(id);
    }

    @Override
    public Optional<Mission> getMissionById(Long id) {
        return missionRepository.findById(id);
    }

    @Override
    public Page<Mission> listMissionByContent(User user, String content, Pageable pageable) {
        content = "%" + content + "%";
        String origin = content;
        String destination = content;

        Page<Mission> missions = missionRepository.findByContentLikeOrUserOrOriginLikeOrDestinationLikeOrderByReleaseTimeDesc(
                content, user, origin, destination, pageable
        );
        return missions;
    }


}
