package com.stu.delivery_system.controller;

import com.stu.delivery_system.domain.Mission;
import com.stu.delivery_system.domain.User;
import com.stu.delivery_system.service.MissionService;
import com.stu.delivery_system.service.UserService;
import com.stu.delivery_system.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/order")
public class MissionController {
    // 0：待接单，1：待完成，2：待确认，3：已完成

    @Autowired
    private UserService userService;

    @Autowired
    private MissionService missionService;


    @PostMapping("/get")
    public ResponseEntity<Response> getMission(Long id) {
        Mission mission = null;

        try {
            Optional<Mission> missionOptional = missionService.getMissionById(id);
            if (missionOptional.isPresent()) {
                mission = missionOptional.get();
            }

            if (mission == null) {
                return ResponseEntity.ok().body(new Response("error", "未找到"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response("error", e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response("success", "miao~", mission));
    }

    @PostMapping("/search")
    public ResponseEntity<Response> searchMission(String uuid, String content,
                                                  @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                                  @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {

        User user = (User) userService.loadUserByUuid(uuid);
        Page<Mission> page = null;

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        page = missionService.listMissionByContent(user, content, pageable);

        List<Mission> list = page.getContent();

        return ResponseEntity.ok().body(new Response("success", "miao~", list));
    }

    @PostMapping("/add")
    public ResponseEntity<Response> addMission(Mission mission, String uuid) {
        try {
            User user = (User) userService.loadUserByUuid(uuid);
            mission.setUser(user);
            mission.setStatus("0");

            String error = "";
            if (mission.getUser() == null) {
                error = "用户不能为空";
            }
            if (mission.getMoney() <= 0) {
                error = "钱不能为空";
            }
            if (!error.equals("")) {
                return ResponseEntity.ok().body(new Response("error", error));
            }

            missionService.saveMission(mission);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response("error", e.getMessage()));
        }

        return ResponseEntity.ok().body(new Response("success", "miao~", mission));
    }

    /**
     * 删除任务
     *
     * @param uuid      发布者的uuid
     * @param missionId
     * @return
     */
    @PostMapping("/delete")
    public ResponseEntity<Response> deleteMission(String uuid, Long missionId) {
        User user = (User) userService.loadUserByUuid(uuid);

        Optional<Mission> missionOptional = missionService.getMissionById(missionId);
        Mission mission = null;
        if (missionOptional.isPresent()) {
            mission = missionOptional.get();
        }

        // 不为空，状态为待接单，自己发布的
        if (mission != null && user != null
                && user.getUuid().equals(mission.getUser().getUuid())
                && mission.getStatus().equals("0")) {
            missionService.removeMission(missionId);
        } else {
            return ResponseEntity.ok().body(new Response("error", "删除失败"));
        }

        return ResponseEntity.ok().body(new Response("success", "删除成功"));
    }

    /**
     * 接单
     *
     * @param uuid      接单者uuid
     * @param missionId
     * @return
     */
    @PostMapping("/accept")
    public ResponseEntity<Response> acceptMission(String uuid, Long missionId) {
        User user = (User) userService.loadUserByUuid(uuid);

        Optional<Mission> missionOptional = missionService.getMissionById(missionId);
        Mission mission = null;
        if (missionOptional.isPresent()) {
            mission = missionOptional.get();
        }

        // 不空，不为自己
        if (mission != null && user != null && !user.getUuid().equals(mission.getUser().getUuid())) {
            mission.setReceiver(user);
            mission.setStatus("1");
            missionService.saveMission(mission);
        } else {
            return ResponseEntity.ok().body(new Response("error", "接单失败"));
        }

        return ResponseEntity.ok().body(new Response("success", "miao~", mission));
    }


    /**
     * 取消接单
     *
     * @param uuid      接单者uuid
     * @param missionId
     * @return
     */
    @PostMapping("/cancel")
    public ResponseEntity<Response> cancelMission(String uuid, Long missionId) {
        User user = (User) userService.loadUserByUuid(uuid);
        Optional<Mission> missionOptional = missionService.getMissionById(missionId);
        Mission mission = null;
        if (missionOptional.isPresent()) {
            mission = missionOptional.get();
        }

        // 不为空，是否为接单人
        if (mission != null && user != null && user.getUuid().equals(mission.getReceiver().getUuid())) {
            mission.setReceiver(null);
            mission.setStatus("0");
            missionService.saveMission(mission);
        } else {
            return ResponseEntity.ok().body(new Response("error", "取消失败"));
        }

        return ResponseEntity.ok().body(new Response("success", "miao~", mission));
    }


    /**
     * 接单者完成单子
     *
     * @param uuid      接单者uuid
     * @param missionId
     * @return
     */
    @PostMapping("/finish")
    public ResponseEntity<Response> finishMission(String uuid, Long missionId) {
        User user = (User) userService.loadUserByUuid(uuid);
        Optional<Mission> missionOptional = missionService.getMissionById(missionId);
        Mission mission = null;
        if (missionOptional.isPresent()) {
            mission = missionOptional.get();
        }

        // 不为空，是否为接单人
        if (mission != null && user != null && user.getUuid().equals(mission.getReceiver().getUuid())) {
            mission.setStatus("2");
            missionService.saveMission(mission);
        } else {
            return ResponseEntity.ok().body(new Response("error", "完成失败"));
        }

        return ResponseEntity.ok().body(new Response("success", "miao~", mission));
    }


    /**
     * 发布者确认完成单子
     *
     * @param uuid      发布者uuid
     * @param missionId
     * @return
     */
    @PostMapping("/confirm")
    public ResponseEntity<Response> confirmMission(String uuid, Long missionId) {
        User user = (User) userService.loadUserByUuid(uuid);
        Optional<Mission> missionOptional = missionService.getMissionById(missionId);
        Mission mission = null;
        if (missionOptional.isPresent()) {
            mission = missionOptional.get();
        }

        // 不为空，是否为自己
        if (mission != null && user != null && user.getUuid().equals(mission.getUser().getUuid())) {
            mission.setStatus("3");
            missionService.saveMission(mission);
        } else {
            return ResponseEntity.ok().body(new Response("error", "确认失败"));
        }

        return ResponseEntity.ok().body(new Response("success", "miao~", mission));
    }



}
