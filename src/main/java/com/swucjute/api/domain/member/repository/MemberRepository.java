package com.swucjute.api.domain.member.repository;

import com.swucjute.api.domain.member.entity.AuthProvider;
import com.swucjute.api.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId);

  Optional<Member> findByEmail(String email);

  boolean existsByProviderAndProviderUserId(AuthProvider provider, String providerUserId);
}
