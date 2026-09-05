package com.swucjute.api.domain.member.repository;

import com.swucjute.api.domain.member.entity.Cell;
import com.swucjute.api.domain.member.entity.Coach;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CellRepository extends JpaRepository<Cell, Long> {

  Optional<Cell> findByNameAndCoach(String name, Coach coach);

  List<Cell> findByCoach(Coach coach);
}
