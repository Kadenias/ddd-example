package de.adesso.schulungen.testapplication.infrasturcture;

import de.adesso.schulungen.testapplication.domain.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class RoleEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private Role name;

  public RoleEntity() {

  }

  public RoleEntity(Role name) {
    this.name = name;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Role getName() {
    return name;
  }

  public void setName(Role name) {
    this.name = name;
  }
}