package com.mariekd.letsplay.authentication.entities;

import com.mariekd.letsplay.app.entities.Ad;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "app_user", uniqueConstraints = @UniqueConstraint(columnNames = { "name", "email" }) )
public class User {

    @Id
    @Column(updatable = false, nullable = false, name="user_id")
    @GeneratedValue
    private UUID id;

    @Column(length = 50, nullable = false, name="name")
    private String name;

    @Column(length = 50, nullable = false, name="email")
    private String email;

    @Column(length = 255, nullable = false, name="password")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "postedBy", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Ad> ads;

    public User(UUID id, String name, String email, String password, Role role, Set<Ad> ads) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.ads = ads;
        this.roles = new HashSet<>();
        if (role != null) {
            this.roles.add(role);
            role.getUsers().add(this);
        }
    }

    public User() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Ad> getAds() {
        return ads;
    }

    public void setAds(Set<Ad> ads) {
        this.ads = ads;
    }
}
