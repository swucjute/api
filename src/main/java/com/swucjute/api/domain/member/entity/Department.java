package com.swucjute.api.domain.member.entity;

/** 회원 소속 구분. */
public enum Department {
  YOUTH("청년"),
  COACH("코치"),
  HELPER("시니어헬퍼"),
  PASTOR("교역자");

  private final String label;

  Department(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
