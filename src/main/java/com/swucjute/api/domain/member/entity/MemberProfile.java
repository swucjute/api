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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
    name = "member_profiles",
    uniqueConstraints =
        @UniqueConstraint(name = "uk_member_profiles_member_id", columnNames = "member_id"),
    indexes = {
      @Index(name = "idx_member_profiles_name", columnList = "name"),
      @Index(name = "idx_member_profiles_phone_number", columnList = "phone_number"),
      @Index(name = "idx_member_profiles_department", columnList = "department"),
      @Index(name = "idx_member_profiles_church_member_id", columnList = "church_member_id")
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberProfile extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "member_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_member_profiles_member"))
  private Member member;

  @Column(nullable = false, length = 50)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private Gender gender;

  @Column(name = "birth_date", nullable = false)
  private LocalDate birthDate;

  @Column(name = "phone_number", nullable = false, length = 20)
  private String phoneNumber;

  @Column(name = "profile_image_url", length = 500)
  private String profileImageUrl;

  @Column(name = "bank_name", length = 30)
  private String bankName;

  @Column(name = "account_number", length = 40)
  private String accountNumber;

  @Column(length = 30)
  private String department;

  @Column(length = 50)
  private String position;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "church_member_id",
      foreignKey = @ForeignKey(name = "fk_member_profiles_church_member"))
  private ChurchMember churchMember;
}
