package net.miklos.evenodd.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Table(name = "admin")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Admin {

    @Id
    @GeneratedValue
    @Column(name = "adminID")
    private Integer adminID;

    @Column(name = "username")
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "personalAES")
    private String personalAES;

    @Column(name = "publicKey")
    private String publicKey;

    @Column(name = "initializationVector")
    private String initializationVector;

}

