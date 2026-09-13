package com.swucjute.api.domain.member.entity;

// 회원 권환
public enum MemberRole {
  USER("회원"),
  LEADER("리더"),
  EXECUTIVE("임원"),
  ADMIN("어드민");

  private final String label;

  MemberRole(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
