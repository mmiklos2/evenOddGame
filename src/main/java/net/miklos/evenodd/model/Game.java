package net.miklos.evenodd.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "game")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Game {

    @Id
    @GeneratedValue
    @Column(name = "gameID")
    private Integer gameID;

    @ManyToOne(targetEntity = Admin.class)
    @JoinColumn(name = "IDadmin1")
    private Admin adminIDFK1;

    @ManyToOne(targetEntity = Admin.class)
    @JoinColumn(name = "IDadmin2")
    private Admin adminIDFK2;

    @Column(name = "player1Choice")
    private Integer player1Choice;

    @Column(name = "player2choice")
    private Integer player2Choice;

    @Column(name = "score")
    private String score;

    public Game(Admin adminIDFK1){
        this.adminIDFK1 = adminIDFK1;
        this.adminIDFK2 = null;
        score = "0:0";
    }
}
