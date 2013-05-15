package org.neuro4j.web.console.springsecurity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;



public class UserDetailsServiceImpl implements UserDetailsService {


//	private UserManager userMgr;
	
    public UserDetailsServiceImpl() {
//    	userMgr = UserManager.getInstance();
    }
    
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    	GrantedAuthority[] grantedAuthorities = new GrantedAuthority[]{new GrantedAuthorityImpl("USER")};
        

        return new User("username", "password", true, true, true, true, grantedAuthorities);
    }

}
