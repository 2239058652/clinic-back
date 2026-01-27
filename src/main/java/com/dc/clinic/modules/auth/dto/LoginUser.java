package com.dc.clinic.modules.auth.dto;

import com.dc.clinic.modules.system.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginUser implements UserDetails {

    private User user;
    private Set<String> permissions;

    /**
     * 注意：这里一定要加 @JsonIgnore，防止 Jackson 序列化 authorities 字段
     * Security 权限校验是在 Filter 里实时通过 getAuthorities() 获取的，不需要存入 Redis
     */
    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (permissions == null)
            return null;
        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @JsonIgnore // 👈 加忽略
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore // 👈 加忽略
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore // 👈 加忽略
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore // 👈 加忽略
    @Override
    public boolean isEnabled() {
        return "ACTIVE".equals(user.getStatus());
    }
}