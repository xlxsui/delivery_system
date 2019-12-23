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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/order")
public class MissionController {
    // 待接单，待完成，已完成

    @Autowired
    private UserService userService;

    @Autowired
    private MissionService missionService;


    @PostMapping("/get")
    public ResponseEntity<Response> getOrder(Long missionId) {
        Mission mission = null;

        try {
            Optional<Mission> missionOptional = missionService.getMissionById(missionId);
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
    public ResponseEntity<Response> searchOrder(String uuid, String content,
                                                @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                                @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {

        User user = (User) userService.loadUserByUuid(uuid);
        Page<Mission> page = null;

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        page = missionService.listMissionByContent(user, content, pageable);

        List<Mission> list = page.getContent();

        return ResponseEntity.ok().body(new Response("success", "miao~", list));
    }

    @PostMapping("/release")
    public ResponseEntity<Response> releaseOrder(Mission mission, String uuid) {
        try {
            User user = (User) userService.loadUserByUuid(uuid);
            mission.setUser(user);
            mission.setStatus("待接单");

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
    public ResponseEntity<Response> deleteOrder(String uuid, Long missionId) {
        User user = (User) userService.loadUserByUuid(uuid);

        Optional<Mission> missionOptional = missionService.getMissionById(missionId);
        Mission mission = null;
        if (missionOptional.isPresent()) {
            mission = missionOptional.get();
        }

        // 不为空，状态为待接单，自己发布的
        if (mission != null && user != null
                && user.getUuid().equals(mission.getUser().getUuid())
                && mission.getStatus().equals("待接单")) {
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
    @PostMapping("/receive")
    public ResponseEntity<Response> receiveOrder(String uuid, Long missionId) {
        User user = (User) userService.loadUserByUuid(uuid);

        Optional<Mission> missionOptional = missionService.getMissionById(missionId);
        Mission mission = null;
        if (missionOptional.isPresent()) {
            mission = missionOptional.get();
        }

        // 不空，不为自己
        if (mission != null && user != null && !user.getUuid().equals(mission.getUser().getUuid())) {
            mission.setReceiver(user);
            mission.setStatus("待完成");
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
    public ResponseEntity<Response> cancelOrder(String uuid, Long missionId) {
        User user = (User) userService.loadUserByUuid(uuid);
        Optional<Mission> missionOptional = missionService.getMissionById(missionId);
        Mission mission = null;
        if (missionOptional.isPresent()) {
            mission = missionOptional.get();
        }

        // 不为空，是否为接单人
        if (mission != null && user != null && user.getUuid().equals(mission.getReceiver().getUuid())) {
            mission.setReceiver(null);
            mission.setStatus("待接单");
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
     * @deprecated 不用这个了，让发布者来确认就好了，2定为完成的
     */
    @PostMapping("/finish")
    public ResponseEntity<Response> finishOrder(String uuid, Long missionId) {
        User user = (User) userService.loadUserByUuid(uuid);
        Optional<Mission> missionOptional = missionService.getMissionById(missionId);
        Mission mission = null;
        if (missionOptional.isPresent()) {
            mission = missionOptional.get();
        }

        // 不为空，是否为接单人
        if (mission != null && user != null && user.getUuid().equals(mission.getReceiver().getUuid())) {
            mission.setStatus("待确认");
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
    public ResponseEntity<Response> confirmOrder(String uuid, Long missionId) {
        User user = (User) userService.loadUserByUuid(uuid);
        Optional<Mission> missionOptional = missionService.getMissionById(missionId);
        Mission mission = null;
        if (missionOptional.isPresent()) {
            mission = missionOptional.get();
        }

        // 不为空，是否为自己
        if (mission != null && user != null && user.getUuid().equals(mission.getUser().getUuid())) {
            mission.setStatus("已完成");
            missionService.saveMission(mission);
        } else {
            return ResponseEntity.ok().body(new Response("error", "确认失败"));
        }

        return ResponseEntity.ok().body(new Response("success", "miao~", mission));
    }

    /**
     * 列出发布的任务
     *
     * @param uuid
     * @return
     */
    @PostMapping("/list_order_released")
    public ResponseEntity<Response> listOrderReleased(String uuid,
                                                      @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                                      @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        User user = (User) userService.loadUserByUuid(uuid);
        if (user == null) {
            return ResponseEntity.ok().body(new Response("error", "用户不存在"));
        }
        Page<Mission> page = null;

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        page = missionService.listMissionByUser(user, pageable);

        List<Mission> list = page.getContent();

        return ResponseEntity.ok().body(new Response("success", "发布的任务", list));
    }

    /**
     * 列出接受的任务
     *
     * @param uuid
     * @return
     */
    @PostMapping("/list_order_received")
    public ResponseEntity<Response> listOrderReceived(String uuid,
                                                      @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                                      @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        User receiver = (User) userService.loadUserByUuid(uuid);

        if (receiver == null) {
            return ResponseEntity.ok().body(new Response("error", "用户不存在"));
        }
        Page<Mission> page = null;

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        page = missionService.listMissionByUserOrReceiver(null, receiver, pageable);

        List<Mission> list = page.getContent();

        return ResponseEntity.ok().body(new Response("success", "miao~", list));
    }

    /**
     * 用户发布等待接单的
     *
     * @param uuid
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @PostMapping("/list_order_waiting_4_receive")
    public ResponseEntity<Response> listOrderWaiting4Receive(String uuid,
                                                             @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                                             @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        User user = (User) userService.loadUserByUuid(uuid);

        if (user == null) {
            return ResponseEntity.ok().body(new Response("error", "用户不存在"));
        }
        Page<Mission> page = null;

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        page = missionService.listMissionByUserAndStatus(user, "待接单", pageable);

        List<Mission> list = page.getContent();

        return ResponseEntity.ok().body(new Response("success", "miao~", list));
    }

    /**
     * 用户发布等待完成的
     *
     * @param uuid
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @PostMapping("/list_order_self_waiting_4_confirm")
    public ResponseEntity<Response> listOrderSelfWaiting4Confirm(String uuid,
                                                                 @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                                                 @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        User user = (User) userService.loadUserByUuid(uuid);

        if (user == null) {
            return ResponseEntity.ok().body(new Response("error", "用户不存在"));
        }
        Page<Mission> page = null;

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        page = missionService.listMissionByUserAndStatus(user, "待完成", pageable);

        List<Mission> list = page.getContent();

        return ResponseEntity.ok().body(new Response("success", "用户发布等待完成的", list));
    }

    /**
     * 用户接受等待完成的
     *
     * @param uuid
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @PostMapping("/list_order_other_waiting_4_confirm")
    public ResponseEntity<Response> listOrderOtherWaiting4Confirm(String uuid,
                                                                  @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                                                  @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        User receiver = (User) userService.loadUserByUuid(uuid);

        if (receiver == null) {
            return ResponseEntity.ok().body(new Response("error", "用户不存在"));
        }
        Page<Mission> page = null;

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        page = missionService.listMissionByReceiverAndStatus(receiver, "待完成", pageable);

        List<Mission> list = page.getContent();

        return ResponseEntity.ok().body(new Response("success", "用户接受等待完成的", list));
    }

    /**
     * 用户发布已经完成的
     *
     * @param uuid
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @PostMapping("/list_order_self_confirmed")
    public ResponseEntity<Response> listOrderSelfConfirmed(String uuid,
                                                           @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                                           @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        User user = (User) userService.loadUserByUuid(uuid);

        if (user == null) {
            return ResponseEntity.ok().body(new Response("error", "用户不存在"));
        }
        Page<Mission> page = null;

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        page = missionService.listMissionByUserAndStatus(user, "已完成", pageable);

        List<Mission> list = page.getContent();

        return ResponseEntity.ok().body(new Response("success", "用户发布已经完成的", list));
    }

    /**
     * 用户接受已经完成的
     *
     * @param uuid
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @PostMapping("/list_order_other_confirmed")
    public ResponseEntity<Response> listOrderOtherConfirmed(String uuid,
                                                            @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                                            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        User receiver = (User) userService.loadUserByUuid(uuid);

        if (receiver == null) {
            return ResponseEntity.ok().body(new Response("error", "用户不存在"));
        }
        Page<Mission> page = null;

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        page = missionService.listMissionByReceiverAndStatus(receiver, "已完成", pageable);

        List<Mission> list = page.getContent();

        return ResponseEntity.ok().body(new Response("success", "用户接受已经完成的", list));
    }


    /**
     * 快递
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @PostMapping("/list_order_express")
    public ResponseEntity<Response> express(@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {

        String type = "快递";
        return listOrderType(type, pageIndex, pageSize);
    }

    /**
     * 外卖
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @PostMapping("/list_order_take_out")
    public ResponseEntity<Response> takeOut(@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        String type = "外卖";
        return listOrderType(type, pageIndex, pageSize);
    }

    /**
     * 帮购
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @PostMapping("/list_order_buy")
    public ResponseEntity<Response> buy(@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        String type = "帮购";
        return listOrderType(type, pageIndex, pageSize);
    }

    /**
     * 其它
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @PostMapping("/list_order_other_help")
    public ResponseEntity<Response> otherHelp(@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                              @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        String type = "其他";
        return listOrderType(type, pageIndex, pageSize);
    }

    /**
     * 根据类型返回列表
     *
     * @param type
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public ResponseEntity<Response> listOrderType(String type,
                                                  @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                                  @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {

        Page<Mission> page;

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        page = missionService.listMissionByType(type, pageable);

        List<Mission> list = page.getContent();

        return ResponseEntity.ok().body(new Response("success", type, list));
    }

    @PostMapping("/list_order_all")
    public ResponseEntity<Response> listOrderAll(@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                                 @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {

        Page<Mission> page;

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        page = missionService.listMissionAll(pageable);

        List<Mission> list = page.getContent();

        return ResponseEntity.ok().body(new Response("success", "全部的", list));
    }
}
