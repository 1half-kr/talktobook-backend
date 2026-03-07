package com.lifelibrarians.lifebookshelf.queue.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
public class CategoriesPayloadResponseDto {
    private Long autobiographyId;
    private Long userId;
    private int themeId;     // 추가
    private int categoryId;  // 추가
    private List<ChunksPayload> chunks = new ArrayList<>();
    private List<MaterialsPayload> materials = new ArrayList<>();

    @Data
    @NoArgsConstructor
    public static class ChunksPayload {
        private int categoryId;
        private int chunkOrder;
        private int weight;
        private LocalDateTime timestamp;
    }

    @Data
    @NoArgsConstructor
    public static class MaterialsPayload {
        private int chunkId;
        private int materialOrder;
        private int example;
        private int similarEvent;
        private int count;
        private List<Integer> principle = Arrays.asList(0, 0, 0, 0, 0, 0);
        private LocalDateTime timestamp;
    }
}