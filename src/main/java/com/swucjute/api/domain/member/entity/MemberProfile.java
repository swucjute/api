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

/**
 * 회원 프로필. 실명/성별/생년월일/연락처는 항상 본인이 직접 등록한 값을 갖고 있다. {@code churchMember}는 청년부(YOUTH) 등록 시에만 매칭돼 채워지는
 * 선택적 교적부 연결이며, 그 외 소속으로 등록하면 비어 있다.
 */
@Getter
@Entity
@Table(
    name = "member_profiles",
    uniqueConstraints = {
      @UniqueConstraint(name = "uk_member_profiles_member_id", columnNames = "member_id"),
      @UniqueConstraint(
          name = "uk_member_profiles_church_member_id",
          columnNames = "church_member_id")
    },
    indexes = {
      @Index(name = "idx_member_profiles_name", columnList = "name"),
      @Index(name = "idx_member_profiles_phone_number", columnList = "phone_number"),
      @Index(name = "idx_member_profiles_department", columnList = "department")
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

  @Enumerated(EnumType.STRING)
  @Column(name = "bank_name", length = 30)
  private BankName bankName;

  @Column(name = "account_number", length = 40)
  private String accountNumber;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private Department department;

  @Column(length = 50)
  private String position;

  /** 청년부 등록 경로에서 매칭된 교적부. 일반 등록에서는 null. */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "church_member_id",
      foreignKey = @ForeignKey(name = "fk_member_profiles_church_member"))
  private ChurchMember churchMember;

  private MemberProfile(
      Member member,
      String name,
      Gender gender,
      LocalDate birthDate,
      String phoneNumber,
      String profileImageUrl,
      Department department,
      String position,
      BankName bankName,
      String accountNumber,
      ChurchMember churchMember) {
    this.member = member;
    this.name = name;
    this.gender = gender;
    this.birthDate = birthDate;
    this.phoneNumber = phoneNumber;
    this.profileImageUrl = profileImageUrl;
    this.department = department;
    this.position = position;
    this.bankName = bankName;
    this.accountNumber = accountNumber;
    this.churchMember = churchMember;
  }

  /** 초기 프로필 등록. churchMember는 청년부(YOUTH) 등록 시 매칭된 교적부이며, 그 외에는 null이다. */
  public static MemberProfile create(
      Member member,
      String name,
      Gender gender,
      LocalDate birthDate,
      String phoneNumber,
      String profileImageUrl,
      Department department,
      String position,
      BankName bankName,
      String accountNumber,
      ChurchMember churchMember) {
    return new MemberProfile(
        member,
        name,
        gender,
        birthDate,
        phoneNumber,
        profileImageUrl,
        department,
        position,
        bankName,
        accountNumber,
        churchMember);
  }

  /** 내 정보 수정: 실명(name)/성별은 변경하지 않고, 전달된 값만 부분 갱신한다. */
  public void updateContact(
      LocalDate birthDate,
      String phoneNumber,
      String profileImageUrl,
      BankName bankName,
      String accountNumber) {
    if (birthDate != null) {
      this.birthDate = birthDate;
    }
    if (phoneNumber != null) {
      this.phoneNumber = phoneNumber;
    }
    if (profileImageUrl != null) {
      this.profileImageUrl = profileImageUrl;
    }
    if (bankName != null) {
      this.bankName = bankName;
    }
    if (accountNumber != null) {
      this.accountNumber = accountNumber;
    }
  }

  /** 관리자 소속/직분 변경. */
  public void updateDepartment(Department department, String position) {
    this.department = department;
    this.position = position;
  }
}
