package com.swucjute.api.domain.member.entity;

/** 계좌 은행 구분. 계좌번호와 함께 회원 프로필에 저장한다. */
public enum BankName {
  KB("KB국민은행"),
  SHINHAN("신한은행"),
  WOORI("우리은행"),
  HANA("하나은행"),
  NH("NH농협은행"),
  IBK("IBK기업은행"),
  SC("SC제일은행"),
  KAKAO("카카오뱅크"),
  TOSS("토스뱅크"),
  KBANK("케이뱅크"),
  BUSAN("부산은행"),
  DAEGU("iM뱅크"),
  GWANGJU("광주은행"),
  JEONBUK("전북은행"),
  JEJU("제주은행"),
  KYONGNAM("경남은행"),
  SUHYUP("수협은행"),
  SAEMAUL("새마을금고"),
  SHINHYUP("신협"),
  POST("우체국");

  private final String label;

  BankName(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
