/**
 * Created by Mohammad Khalid Hasan|| BJIT-R&D
 * Since: 4/24/2024
 * Version: 1.0
 */

package org.bjit.oauth.service;

import org.bjit.oauth.model.UserEntity;
import org.bjit.oauth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);


    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    //This method is called when we need to retrieve user details for authentication
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Entering in loadUserByUsername Method...");
        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(()->new UsernameNotFoundException("User Not Found"));
        logger.info("User found Successfully..!!!");
        return user;
    }
}
