package net.miklos.pkl;

import net.miklos.evenodd.model.*;
import net.miklos.evenodd.repository.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EvenOddApplicationTests {

	@Autowired
    AdminRepository adminRepository;

    @Test
    public void loadAdminTest() {
        final Admin object = adminRepository.findOne(11);
        assertThat(object).isNotNull();
    }



	
}
