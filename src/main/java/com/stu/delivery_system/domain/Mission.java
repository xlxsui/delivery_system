package com.stu.delivery_system.domain;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 交易记录
 */
@Entity
public class Mission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Lob  // 大对象，映射 MySQL 的 Long Text 类型
    @Basic(fetch = FetchType.LAZY) // 懒加载
    @NotEmpty(message = "内容不能为空")
    @Size(min = 2)
    @Column(nullable = false) // 映射为字段，值不能为空
    private String content;

    @NotEmpty(message = "起点不能为空")
    @Size(max = 100)
    @Column(nullable = false)
    private String origin;

    @NotEmpty(message = "终点不能为空")
    @Size(max = 100)
    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    @CreationTimestamp
    private Timestamp releaseTime;

    @NotEmpty(message = "截止时间不能为空")
    @Column(nullable = false)
    private String deadline;

    @Column(nullable = false)
    private Integer money;

    @NotEmpty(message = "状态不能为空")
    @Column(nullable = false)
    private String status;

    @NotEmpty(message = "类型不能为空")
    @Column(nullable = false)
    private String type;

    protected Mission() {
    }

    public Mission(User user,
                   @NotEmpty(message = "内容不能为空") @Size(min = 2) String content,
                   @NotEmpty(message = "起点不能为空") @Size(max = 100) String origin,
                   @NotEmpty(message = "终点不能为空") @Size(max = 100) String destination,
                   @NotEmpty(message = "截止时间不能为空") String deadline,
                   Integer money,
                   @NotEmpty(message = "类型不能为空") String type) {
        this.user = user;
        this.content = content;
        this.origin = origin;
        this.destination = destination;
        this.deadline = deadline;
        this.money = money;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Timestamp getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Timestamp release_time) {
        this.releaseTime = release_time;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
