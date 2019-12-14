package com.stu.delivery_system.domain;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Collection;

/**
 * 用户信息
 */
@Entity
public class User implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Id //主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增
    private Long id;

    @NotEmpty(message = "UUID不能为空")
    @Column(nullable = false, unique = true)
    private String uuid;


    @NotEmpty(message = "账号不能为空")
    @Size(min = 3, max = 20)
    @Column(nullable = false, length = 20)
    private String username; // 用户账号，用户登录时的唯一标识

    @Column(nullable = false)
    private Integer money;

    @Column(length = 64)
    private String province;

    @Column(length = 200)
    private String avatar;

    @Column(length = 20)
    private String gender;

    protected User() {

    }


    public User(@NotEmpty(message = "UUID不能为空") String uuid,
                @NotEmpty(message = "账号不能为空") @Size(min = 3, max = 20) String username,
                Integer money) {
        this.uuid = uuid;
        this.username = username;
        this.money = money;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
