package security;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import com.projects.cavany.domain.WebUser;

import java.util.Collection;

import java.util.stream.Collectors;



public class CustomSecurityUser implements UserDetails {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final WebUser webUser;

    public CustomSecurityUser(WebUser webUser) {
        this.webUser = webUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return webUser.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return webUser.getPassword();
    }

    @Override
    public String getUsername() {
        return webUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        // Implement the logic to determine if the account is non-expired
        // Return true if the account is non-expired, false otherwise
        // Example: return !webUser.isExpired();
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Implement the logic to determine if the account is non-locked
        // Return true if the account is non-locked, false otherwise
        // Example: return !webUser.isLocked();
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Implement the logic to determine if the credentials are non-expired
        // Return true if the credentials are non-expired, false otherwise
        // Example: return !webUser.isCredentialsExpired();
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Implement the logic to determine if the account is enabled
        // Return true if the account is enabled, false otherwise
        // Example: return webUser.isEnabled();
        return true;
    }
}