package com.swucjute.api.domain.member.entity;

import com.swucjute.api.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
      @Index(name = "idx_church_members_phone_number", columnList = "phone_number"),
      @Index(name = "idx_church_members_cell_id", columnList = "cell_id")
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChurchMember extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 50)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private Gender gender;

  @Column(name = "birth_date", nullable = false)
  private LocalDate birthDate;

  @Column(name = "phone_number", nullable = false, length = 20)
  private String phoneNumber;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cell_id", foreignKey = @ForeignKey(name = "fk_church_members_cell"))
  private Cell cell;

  public void assignCell(Cell cell) {
    this.cell = cell;
  }

  /** 최초로 연결되는 프로필의 성별을 채운다. 이미 값이 있으면(교적 원본에 이미 기재된 경우) 덮어쓰지 않는다. */
  public void assignGenderIfAbsent(Gender gender) {
    if (this.gender == null) {
      this.gender = gender;
    }
  }
}
