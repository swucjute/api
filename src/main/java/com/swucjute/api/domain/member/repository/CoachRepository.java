package com.swucjute.api.domain.member.repository;

import com.swucjute.api.domain.member.entity.Coach;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoachRepository extends JpaRepository<Coach, Long> {

  Optional<Coach> findByName(String name);
}
