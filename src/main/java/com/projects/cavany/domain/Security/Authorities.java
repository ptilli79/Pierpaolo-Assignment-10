package com.projects.cavany.domain.Security;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.security.core.GrantedAuthority;





import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

@Node
public class Authorities implements GrantedAuthority, Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    private String authority;

    @Relationship(type = "BELONGS_TO", direction = Relationship.Direction.OUTGOING)
    private WebUser user;

    @Override
    public String getAuthority() {
        return authority;
    }

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setAuthority(String authority) {
        this.authority = authority;
    }

    public WebUser getUser() {
        return user;
    }

    public void setUser(WebUser user) {
        this.user = user;
    }
}

