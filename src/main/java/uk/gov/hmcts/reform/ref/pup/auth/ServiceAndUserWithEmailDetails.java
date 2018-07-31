package uk.gov.hmcts.reform.ref.pup.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

public class ServiceAndUserWithEmailDetails extends org.springframework.security.core.userdetails.User {

    private static final long serialVersionUID = 1L;

    private final String servicename;
    private final String email;

    public ServiceAndUserWithEmailDetails(String username, String email, String token, Collection<String> authorities, String servicename) {
        super(username, token, toGrantedAuthorities(authorities));
        this.servicename = servicename;
        this.email = email;
    }

    private static Collection<? extends GrantedAuthority> toGrantedAuthorities(Collection<String> roles) {
        return roles.stream().map(SimpleGrantedAuthority::new).collect(toList());
    }

    public String getServicename() {
        return servicename;
    }
    
    public String getEmail() {
        return email;
    }
}