package com.swucjute.api.global.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TokenHashUtilTest {

  @Test
  @DisplayName("같은 입력은 같은 64자 해시를 만들고 원문과 다르다")
  void deterministicHash() {
    String hash1 = TokenHashUtil.sha256("some.refresh.token");
    String hash2 = TokenHashUtil.sha256("some.refresh.token");

    assertThat(hash1).isEqualTo(hash2).hasSize(64).isNotEqualTo("some.refresh.token");
  }

  @Test
  @DisplayName("다른 입력은 다른 해시를 만든다")
  void differentInputDifferentHash() {
    assertThat(TokenHashUtil.sha256("token-a")).isNotEqualTo(TokenHashUtil.sha256("token-b"));
  }
}
