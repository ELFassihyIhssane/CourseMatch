package ma.kaoutar.userservice.dao.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String role;

    @Column(columnDefinition = "text")
    private String interestsJson; // à la place de List<String> interests

    @Column(columnDefinition = "text")
    private String levelsByInterestJson; // à la place de Map<String,String> levelsByInterest

    public User() {}

    // Getters
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getInterestsJson() { return interestsJson; }
    public String getLevelsByInterestJson() { return levelsByInterestJson; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
    public void setInterestsJson(String interestsJson) { this.interestsJson = interestsJson; }
    public void setLevelsByInterestJson(String levelsByInterestJson) { this.levelsByInterestJson = levelsByInterestJson; }
}
