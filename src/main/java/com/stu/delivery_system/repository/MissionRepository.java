package com.stu.delivery_system.repository;


import com.stu.delivery_system.domain.Mission;
import com.stu.delivery_system.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 数据访问层，读写数据库
 */
public interface MissionRepository extends JpaRepository<Mission, Long> {

    /**
     * 根据内容、用户名、起点、终点查询订单内容
     *
     * @param content
     * @param user
     * @param origin
     * @param destination
     * @return
     */
    Page<Mission> findByContentLikeOrUserOrOriginLikeOrDestinationLikeOrderByReleaseTimeDesc(String content, User user, String origin, String destination, Pageable pageable);

    /**
     * 发出订单查询
     *
     * @param status
     * @param user
     * @param pageable
     * @return
     */
    Page<Mission> findByStatusLikeAndUserOrderByReleaseTimeDesc(String status, User user, Pageable pageable);

    /**
     * 接收订单查询
     *
     * @param status
     * @param receiver
     * @param pageable
     * @return
     */
    Page<Mission> findByStatusLikeAndReceiverOrderByReleaseTimeDesc(String status, User receiver, Pageable pageable);

    /**
     * 获取全部接收和发出的订单
     *
     * @param user
     * @param receiver
     * @param pageable
     * @return
     */
    Page<Mission> findByUserOrReceiverOrderByReleaseTimeDesc(User user, User receiver, Pageable pageable);

    Page<Mission> findByUserOrderByReleaseTimeDesc(User user, Pageable pageable);

    Page<Mission> findByStatusLikeAndTypeOrderByReleaseTimeDesc(String status, String type, Pageable pageable);

    Page<Mission> findByStatusLikeOrderByReleaseTimeDesc(String status, Pageable pageable);


}
