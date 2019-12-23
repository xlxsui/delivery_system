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

    /**
     * 搜索，根据内容，地点，uuid
     *
     * @param user
     * @param content
     * @param pageable
     * @return
     */
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


    /**
     * 列出发布用户某状态的单子
     *
     * @param user
     * @param status
     * @param pageable
     * @return
     */
    @Override
    public Page<Mission> listMissionByUserAndStatus(User user, String status, Pageable pageable) {

        Page<Mission> missions = missionRepository.findByStatusLikeAndUserOrderByReleaseTimeDesc(
                status, user, pageable
        );

        return missions;
    }

    /**
     * 列出接收者某状态的订单
     *
     * @param receiver
     * @param status
     * @param pageable
     * @return
     */
    @Override
    public Page<Mission> listMissionByReceiverAndStatus(User receiver, String status, Pageable pageable) {

        Page<Mission> missions = missionRepository.findByStatusLikeAndReceiverOrderByReleaseTimeDesc(
                status, receiver, pageable
        );

        return missions;
    }

    /**
     * 列出某人发布或者接收的订单
     *
     * @param user
     * @param receiver
     * @param pageable
     * @return
     */
    @Override
    public Page<Mission> listMissionByUserOrReceiver(User user, User receiver, Pageable pageable) {
        Page<Mission> missions = missionRepository.findByUserOrReceiverOrderByReleaseTimeDesc(
                user, receiver, pageable
        );

        return missions;
    }

    @Override
    public Page<Mission> listMissionByUser(User user, Pageable pageable) {
        Page<Mission> missions = missionRepository.findByUserOrderByReleaseTimeDesc(
                user, pageable
        );

        return missions;
    }

    @Override
    public Page<Mission> listMissionByType(String type, Pageable pageable) {
        Page<Mission> missions = missionRepository.findByStatusLikeAndTypeOrderByReleaseTimeDesc(
                "待接单", type, pageable);

        return missions;
    }

    @Override
    public Page<Mission> listMissionAll(Pageable pageable) {
        Page<Mission> missions = missionRepository.findByStatusLikeOrderByReleaseTimeDesc("待接单", pageable);

        return missions;
    }


}
