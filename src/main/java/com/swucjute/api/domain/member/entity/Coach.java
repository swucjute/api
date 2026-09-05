package com.swucjute.api.domain.member.entity;

import com.swucjute.api.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 코치조. 교적부(ChurchMember)를 셀 단위로 묶는 상위 그룹으로, 로스터 성격의 이름표 데이터만 가진다. */
@Getter
@Entity
@Table(
    name = "coaches",
    uniqueConstraints = @UniqueConstraint(name = "uk_coaches_name", columnNames = "name"),
    indexes = {@Index(name = "idx_coaches_name", columnList = "name")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coach extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 50)
  private String name;

  private Coach(String name) {
    this.name = name;
  }

  public static Coach create(String name) {
    return new Coach(name);
  }

  public void updateName(String name) {
    this.name = name;
  }
}
