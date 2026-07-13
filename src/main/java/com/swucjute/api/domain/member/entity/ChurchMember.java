package com.swucjute.api.domain.member.entity;

import com.swucjute.api.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
    name = "church_members",
    indexes = {
      @Index(name = "idx_church_members_name", columnList = "name"),
      @Index(name = "idx_church_members_birth_date", columnList = "birth_date"),
      @Index(name = "idx_church_members_phone_number", columnList = "phone_number")
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChurchMember extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 50)
  private String name;

  @Column(name = "birth_date", nullable = false)
  private LocalDate birthDate;

  @Column(name = "phone_number", nullable = false, length = 20)
  private String phoneNumber;

  @Column(name = "coach_group_name", length = 50)
  private String coachGroupName;

  @Column(name = "cell_name", length = 50)
  private String cellName;
}
