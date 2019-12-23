package com.stu.delivery_system.domain;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

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

    @Size(max = 100)
    @Column
    private String origin;

    @NotEmpty(message = "终点不能为空")
    @Size(max = 100)
    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    @CreationTimestamp
    private Timestamp releaseTime;

    @Size(max = 100)
    @Column
    private String releaseTimeString;

    @NotEmpty(message = "截止时间不能为空")
    @Column(nullable = false)
    private String deadline;

    @Size(max = 100)
    @Column
    private String deadlineString;

    @Column(nullable = false)
    private Float money;

    @NotEmpty(message = "状态不能为空")
    @Column(nullable = false)
    private String status;

    @NotEmpty(message = "类型不能为空")
    @Column(nullable = false)
    private String type;

    @NotEmpty(message = "订单号不能为空")
    @Size(max = 100)
    @Column(nullable = false)
    private String orderID;

    protected Mission() {
    }

    public Mission(User user,
                   @NotEmpty(message = "内容不能为空") @Size(min = 2) String content,
                   @NotEmpty(message = "终点不能为空") @Size(max = 100) String destination,
                   @NotEmpty(message = "截止时间不能为空") String deadline,
                   Float money,
                   @NotEmpty(message = "类型不能为空") String type) {
        this.user = user;
        this.content = content;
        this.destination = destination;
        this.deadline = deadline;
        this.money = money;
        this.type = type;
        this.orderID = UUID.randomUUID().toString().toUpperCase().replace("-", "").substring(0, 20);

        this.releaseTimeString = Mission.parseDate(new Date());
        this.deadlineString = Mission.parseDateString(deadline);
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

    public Float getMoney() {
        return money;
    }

    public void setMoney(Float money) {
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

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getReleaseTimeString() {
        return releaseTimeString;
    }

    public void setReleaseTimeString(String releaseTimeString) {
        this.releaseTimeString = releaseTimeString;
    }

    public String getDeadlineString() {
        return deadlineString;
    }

    public void setDeadlineString(String deadlineString) {
        this.deadlineString = deadlineString;
    }

    @Override
    public String toString() {
        return "Mission{" +
                "id=" + id +
                ", user=" + user +
                ", receiver=" + receiver +
                ", content='" + content + '\'' +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", releaseTime=" + releaseTime +
                ", releaseTimeString='" + releaseTimeString + '\'' +
                ", deadline='" + deadline + '\'' +
                ", deadlineString='" + deadlineString + '\'' +
                ", money=" + money +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", orderID='" + orderID + '\'' +
                '}';
    }

    public static String parseDateString(String s) {
        if (s != null) {
            Date time = null;
            try {
                time = new SimpleDateFormat("MM月dd日 kk:mm", Locale.ENGLISH)
                        .parse(s);// 从字符串得到一个Date
                time.setYear(new Date().getYear());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar today = Calendar.getInstance(); // 今天
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DAY_OF_YEAR, -1); // 昨天
            Calendar bYesterday = Calendar.getInstance();
            bYesterday.add(Calendar.DAY_OF_YEAR, -2); // 前天
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_YEAR, 1); // 明天
            Calendar aTomorrow = Calendar.getInstance();
            aTomorrow.add(Calendar.DAY_OF_YEAR, 1); // 后天

            Calendar postDay = Calendar.getInstance();
            postDay.setTime(time); // your date

            String timeString;
/*
            if (today.get(Calendar.YEAR) == postDay.get(Calendar.YEAR)
                    && today.get(Calendar.DAY_OF_YEAR) == postDay.get(Calendar.DAY_OF_YEAR)) {
                timeString = new SimpleDateFormat("今天 kk:mm", Locale.ENGLISH).format(time);
            } else if (yesterday.get(Calendar.YEAR) == postDay.get(Calendar.YEAR)
                    && yesterday.get(Calendar.DAY_OF_YEAR) == postDay.get(Calendar.DAY_OF_YEAR)) {
                timeString = new SimpleDateFormat("昨天 kk:mm", Locale.ENGLISH).format(time);
            } else if (bYesterday.get(Calendar.YEAR) == postDay.get(Calendar.YEAR)
                    && bYesterday.get(Calendar.DAY_OF_YEAR) == postDay.get(Calendar.DAY_OF_YEAR)) {
                timeString = new SimpleDateFormat("前天 kk:mm", Locale.ENGLISH).format(time);
            } else if (tomorrow.get(Calendar.YEAR) == postDay.get(Calendar.YEAR)
                    && tomorrow.get(Calendar.DAY_OF_YEAR) == postDay.get(Calendar.DAY_OF_YEAR)) {
                timeString = new SimpleDateFormat("明天 kk:mm", Locale.ENGLISH).format(time);
            } else if (aTomorrow.get(Calendar.YEAR) == postDay.get(Calendar.YEAR)
                    && aTomorrow.get(Calendar.DAY_OF_YEAR) == postDay.get(Calendar.DAY_OF_YEAR)) {
                timeString = new SimpleDateFormat("后天 kk:mm", Locale.ENGLISH).format(time);
            } else {
                timeString = new SimpleDateFormat("MM-dd kk:mm", Locale.ENGLISH).format(time);
            }

 */
            timeString = new SimpleDateFormat("MM月dd日 kk:mm", Locale.ENGLISH).format(time);
            return timeString;
        } else return null;
    }


    public static String parseDate(Date date) {
        if (date != null) {

            Calendar today = Calendar.getInstance(); // 今天
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DAY_OF_YEAR, -1); // 昨天
            Calendar bYesterday = Calendar.getInstance();
            bYesterday.add(Calendar.DAY_OF_YEAR, -2); // 前天
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_YEAR, 1); // 明天
            Calendar aTomorrow = Calendar.getInstance();
            aTomorrow.add(Calendar.DAY_OF_YEAR, 1); // 后天

            Calendar postDay = Calendar.getInstance();
            postDay.setTime(date); // your date

            String timeString;
/*
            if (today.get(Calendar.YEAR) == postDay.get(Calendar.YEAR)
                    && today.get(Calendar.DAY_OF_YEAR) == postDay.get(Calendar.DAY_OF_YEAR)) {
                timeString = new SimpleDateFormat("今天 kk:mm", Locale.ENGLISH).format(date);
            } else if (yesterday.get(Calendar.YEAR) == postDay.get(Calendar.YEAR)
                    && yesterday.get(Calendar.DAY_OF_YEAR) == postDay.get(Calendar.DAY_OF_YEAR)) {
                timeString = new SimpleDateFormat("昨天 kk:mm", Locale.ENGLISH).format(date);
            } else if (bYesterday.get(Calendar.YEAR) == postDay.get(Calendar.YEAR)
                    && bYesterday.get(Calendar.DAY_OF_YEAR) == postDay.get(Calendar.DAY_OF_YEAR)) {
                timeString = new SimpleDateFormat("前天 kk:mm", Locale.ENGLISH).format(date);
            } else if (tomorrow.get(Calendar.YEAR) == postDay.get(Calendar.YEAR)
                    && tomorrow.get(Calendar.DAY_OF_YEAR) == postDay.get(Calendar.DAY_OF_YEAR)) {
                timeString = new SimpleDateFormat("明天 kk:mm", Locale.ENGLISH).format(date);
            } else if (aTomorrow.get(Calendar.YEAR) == postDay.get(Calendar.YEAR)
                    && aTomorrow.get(Calendar.DAY_OF_YEAR) == postDay.get(Calendar.DAY_OF_YEAR)) {
                timeString = new SimpleDateFormat("后天 kk:mm", Locale.ENGLISH).format(date);
            } else {
                timeString = new SimpleDateFormat("MM-dd kk:mm", Locale.ENGLISH).format(date);
            }

 */
            timeString = new SimpleDateFormat("MM月dd日 kk:mm", Locale.ENGLISH).format(date);
            return timeString;
        } else return null;
    }
}
