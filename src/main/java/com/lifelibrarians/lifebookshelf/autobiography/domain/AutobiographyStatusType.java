package com.lifelibrarians.lifebookshelf.autobiography.domain;

public enum AutobiographyStatusType {
    // autobiography가 최초로 생성된, 아무 interview history가 없는 단계
    EMPTY,
    // 1개 이상의 interview가 존재하는 단계
    PROGRESSING,
    //자서전 생성 가능 조건(인터뷰 종료 조건)을 채운 단계, UI에서 확인 가능
    ENOUGH,
    // 자서전을 생성하고 있는 단계, 비동기로 자서전을 조각내 병렬 처리
    CREATING,
    //자서전 생성이 완료된 단계
    FINISH
}