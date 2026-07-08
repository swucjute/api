package com.swucjute.api.domain.auth.repository;

import com.swucjute.api.domain.auth.entity.RefreshToken;
import com.swucjute.api.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  Optional<RefreshToken> findByMember(Member member);

  Optional<RefreshToken> findByTokenHash(String tokenHash);
}
