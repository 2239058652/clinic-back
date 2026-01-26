package com.dc.clinic.modules.auth.dto;

import com.dc.clinic.modules.system.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUser implements UserDetails {
    private User user; // 我们自己的数据库实体类

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 这里暂时返回 null，后续做权限控制（角色、权限）时再补全
        return null;
    }

    @Override
    public String getPassword() { return user.getPassword(); }

    @Override
    public String getUsername() { return user.getUsername(); }

    // 账号是否没过期、没锁定、凭证没过期、是否启用（都返回 true）
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return "ACTIVE".equals(user.getStatus()); }
}