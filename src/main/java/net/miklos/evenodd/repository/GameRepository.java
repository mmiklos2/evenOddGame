package net.miklos.evenodd.repository;

import net.miklos.evenodd.model.Admin;
import net.miklos.evenodd.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameRepository extends JpaRepository<Game, Integer> {

    @Query("select game from Game game where game.adminIDFK1 = :admin or game.adminIDFK2 = :admin ")
    Game findCurrentGame(@Param("admin") Admin admin);

    Game findByAdminIDFK1(Admin IDadmin1);

}
