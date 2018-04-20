package net.miklos.evenodd.service;

import net.miklos.evenodd.model.Admin;
import net.miklos.evenodd.model.UserRoles;
import net.miklos.evenodd.repository.UserRoleRepository;
import net.miklos.evenodd.service.impl.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class UserToUserDetails implements Converter<Admin, UserDetails> {

    @Autowired
    private
    UserRoleRepository userRoleRepository;

    @Override
    public UserDetails convert(Admin user) {
        UserDetailsImpl userDetails = new UserDetailsImpl();
        List<UserRoles> roles;
        if (user != null) {
            userDetails.setUsername(user.getUserName());
            userDetails.setPassword(user.getPassword());
            roles = userRoleRepository.findByUserId(user);

            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            for (UserRoles userRoles : roles) {
                authorities.add(new SimpleGrantedAuthority(userRoles.getRole()));
            }
            userDetails.setAuthorities(authorities);
        }

        return userDetails;
    }
}
