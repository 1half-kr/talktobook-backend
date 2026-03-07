package com.lifelibrarians.lifebookshelf.classification.domain;

/**
 * theme.name ENUM 값 매핑용
 * DB ENUM('family','love', ... ) 과 일치하도록 소문자로 맞춤.
 */
public enum ThemeNameType {
    family("가족사 전반"),
    love("사랑과 결혼"),
    caring("돌봄과 양육"),
    local("집과 동네의 기억"),
    trait("나의 성격과 습관"),
    friend("관계망과 우정"),
    career("커리어 여정"),
    growing("성장의 여정"),
    crisis("위기·회복·성찰"),
    money("돈과 선택"),
    hobby("취미·여가·정체성"),
    pet("반려동물과 함께한 삶"),
    philosophy("가치관과 철학"),
    community("공동체와 일상"),
    parent("부모 이야기 집중");
    
    private final String koreanName;
    
    ThemeNameType(String koreanName) {
        this.koreanName = koreanName;
    }
    
    public String getKoreanName() {
        return koreanName;
    }
    
    public static ThemeNameType fromKoreanName(String koreanName) {
        for (ThemeNameType type : values()) {
            if (type.koreanName.equals(koreanName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown theme name: " + koreanName);
    }
}
