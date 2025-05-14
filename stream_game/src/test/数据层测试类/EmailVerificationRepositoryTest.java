package com.xtu.stream_game.repository;

import com.xtu.stream_game.entity.EmailVerification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EmailVerificationRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private EmailVerificationRepository repo;

    @Test
    void findByEmailAndVerificationCode_WhenValid_ShouldReturnEntity() {
        EmailVerification ev = new EmailVerification();
        ev.setEmail("test@xtu.com");
        ev.setVerificationCode("123456");
        em.persist(ev);
        em.flush();

        Optional<EmailVerification> result = repo.findByEmailAndVerificationCode("test@xtu.com", "123456");
        assertThat(result).isPresent().containsSame(ev);
    }

    @Test
    void findByEmailOrderByIdDesc_ShouldReturnOrderedResults() {
        EmailVerification ev1 = new EmailVerification();
        ev1.setEmail("test@xtu.com");
        ev1.setVerificationCode("111");
        em.persist(ev1);

        EmailVerification ev2 = new EmailVerification();
        ev2.setEmail("test@xtu.com");
        ev2.setVerificationCode("222");
        em.persist(ev2);
        em.flush();

        List<EmailVerification> results = repo.findByEmailOrderByIdDesc("test@xtu.com");
        assertThat(results).extracting(EmailVerification::getVerificationCode)
                          .containsExactly("222", "111");
    }
}
