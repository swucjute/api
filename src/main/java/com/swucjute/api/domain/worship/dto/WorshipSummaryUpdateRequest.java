package com.swucjute.api.domain.worship.dto;

import java.util.List;

public record WorshipSummaryUpdateRequest(
    String sermonSummary, String prayerSummary, List<String> prayerTopics) {}
