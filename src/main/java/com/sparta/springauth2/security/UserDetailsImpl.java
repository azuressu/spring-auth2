package com.sparta.springauth2.security;

import com.sparta.springauth2.entity.User;
import com.sparta.springauth2.entity.UserRoleEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class UserDetailsImpl implements UserDetails {
    
    // UserDetailsService와 UserDetails를 직접 구현해 사용하는 경우, Security의 default로 로그인 기능을 사용하지 않겠다는 설정이 되어
    // Security의 password를 더 이상 제공하지 않음을 확인할 수 있음
    // POST "/api/user/login"을 로그인 인증 URL로 설정했기 때문에 이제 해당 요청이 들어오면 우리가 직접 구현한 UserDetailsService를 통해 인증 확인 작업이 이루어지고
    // 인증 객체에 직접 구현한 UserDetails가 담기게 됨

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRoleEnum role = user.getRole();
        String authority = role.getAuthority();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);

        return authorities;
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


}
