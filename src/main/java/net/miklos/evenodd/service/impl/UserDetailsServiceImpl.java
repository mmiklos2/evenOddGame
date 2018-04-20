package net.miklos.evenodd.service.impl;

import net.miklos.evenodd.model.Admin;
import net.miklos.evenodd.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;
    private Converter<Admin, UserDetails> userUserDetailsConverter;

    @Autowired
    @Qualifier(value = "userToUserDetails")
    public void setAdminUserDetailsConverter(Converter<Admin, UserDetails> userUserDetailsConverter) {
        this.userUserDetailsConverter = userUserDetailsConverter;
    }

    @Override
    @Cacheable(cacheNames = "loggedUser", key = "#username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Admin admin = adminRepository.findByUserName(username);
        UserDetails details = null;
        if (admin != null)
            details = userUserDetailsConverter.convert(admin);
        return details;
    }
}
