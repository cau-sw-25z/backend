package com.example.BE.repository;

import com.example.BE.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveAndFindUser() {
        // unique 제약조건 충돌을 피하기 위해 매번 다른 이메일 생성
        String email = "test" + System.currentTimeMillis() + "@example.com";
        String nickname = "테스트유저";

        // User 엔티티 생성
        User user = new User(email, nickname);

        // DB에 User 저장
        User savedUser = userRepository.saveAndFlush(user);

        // 이메일로 User 다시 조회
        Optional<User> foundUser = userRepository.findByEmail(email);

        // 저장된 User의 id가 DB에서 자동 생성되었는지 확인
        assertThat(savedUser.getId()).isNotNull();

        // 이메일로 조회한 User가 존재하는지 확인
        assertThat(foundUser).isPresent();

        // 조회된 User의 email이 저장한 email과 같은지 확인
        assertThat(foundUser.get().getEmail()).isEqualTo(email);

        // 조회된 User의 nickname이 저장한 nickname과 같은지 확인
        assertThat(foundUser.get().getNickname()).isEqualTo(nickname);

        // createdAt 값이 자동으로 들어갔는지 확인
        assertThat(foundUser.get().getCreatedAt()).isNotNull();
    }
}