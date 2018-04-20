package net.miklos.evenodd.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "userRoles")
@Data
@NoArgsConstructor
public class UserRoles {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Admin userId;

    @Column(name = "role")
    private String role;
}
