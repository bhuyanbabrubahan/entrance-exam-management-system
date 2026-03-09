package com.jee.publicapi.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jee.publicapi.entity.User;
import com.jee.publicapi.service.UserService;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userService.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        // ✅ Ensure ROLE_ is added only once
        String role = user.getRole(); // e.g., "ADMIN" or "USER"
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority(role)
        );

        System.out.println("Authorities for login: " + authorities); // Should now print ROLE_ADMIN or ROLE_USER

		/*
		 * return new org.springframework.security.core.userdetails.User(
		 * user.getEmail(), user.getPassword(), user.isEnabled(), true, //
		 * accountNonExpired true, // credentialsNonExpired user.isAccountNonLocked(),
		 * authorities );
		 */
        
        System.out.println("Authorities for login: [" + role + "]");
        System.out.println("Returning CustomUserDetails object");

        return new CustomUserDetails(user);  // ✅ DO NOT REMOVE
        
        
    }
}