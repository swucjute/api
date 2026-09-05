package com.swucjute.api.domain.member.entity;

import com.swucjute.api.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 셀. 코치조(Coach) 산하에 소속되며, 교적부(ChurchMember)가 최종적으로 속하는 최소 단위 그룹이다. */
@Getter
@Entity
@Table(
    name = "cells",
    indexes = {
      @Index(name = "idx_cells_name", columnList = "name"),
      @Index(name = "idx_cells_coach_id", columnList = "coach_id")
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cell extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 50)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "coach_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_cells_coach"))
  private Coach coach;

  private Cell(String name, Coach coach) {
    this.name = name;
    this.coach = coach;
  }

  public static Cell create(String name, Coach coach) {
    return new Cell(name, coach);
  }

  public void updateName(String name) {
    this.name = name;
  }

  public void changeCoach(Coach coach) {
    this.coach = coach;
  }
}
