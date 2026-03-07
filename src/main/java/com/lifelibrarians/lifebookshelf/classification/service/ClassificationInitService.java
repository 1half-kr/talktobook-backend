package com.lifelibrarians.lifebookshelf.classification.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.classification.domain.*;
import com.lifelibrarians.lifebookshelf.classification.repository.CategoryRepository;
import com.lifelibrarians.lifebookshelf.classification.repository.ChunkRepository;
import com.lifelibrarians.lifebookshelf.classification.repository.MaterialRepository;
import com.lifelibrarians.lifebookshelf.classification.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ClassificationInitService {

    private final ThemeRepository themeRepository;
    private final CategoryRepository categoryRepository;
    private final ChunkRepository chunkRepository;
    private final MaterialRepository materialRepository;

    public void initializeFromAiData(Autobiography autobiography) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode themeData = mapper.readTree(new ClassPathResource("data/theme.json").getInputStream());
            JsonNode materialData = mapper.readTree(new ClassPathResource("data/material.json").getInputStream());

            // 1. 모든 Category 먼저 생성 (중복 제거)
            Map<Integer, Category> categoryMap = new HashMap<>();
            JsonNode materialCategories = materialData.get("category");
            for (JsonNode categoryNode : materialCategories) {
                String categoryName = categoryNode.get("name").asText();
                int categoryOrder = categoryNode.get("order").asInt();
                
                Category category = Category.of(categoryOrder, categoryName, autobiography);
                categoryRepository.save(category);
                categoryMap.put(categoryOrder, category);
                
                // Chunk & Material 생성
                createChunksAndMaterials(categoryNode, category);
            }

            // 2. Theme 생성 및 Category 연결
            JsonNode themes = themeData.get("theme");
            for (JsonNode themeNode : themes) {
                String themeName = themeNode.get("name").asText();
                int themeOrder = themeNode.get("order").asInt();

                Theme theme = Theme.of(themeOrder, ThemeNameType.fromKoreanName(themeName));
                themeRepository.save(theme);  // Theme 먼저 저장
                
                // Theme에 Category들 연결
                JsonNode categories = themeNode.get("category");
                for (JsonNode categoryRef : categories) {
                    int categoryOrder = categoryRef.get("order").asInt();
                    Category category = categoryMap.get(categoryOrder);
                    if (category != null) {
                        theme.addCategory(category);
                    }
                }
                
                themeRepository.save(theme);  // 연결 후 다시 저장
            }
        } catch (Exception e) {
            throw new RuntimeException("AI 데이터 초기화 실패", e);
        }
    }

    private void createChunksAndMaterials(JsonNode categoryNode, Category category) {
        JsonNode chunks = categoryNode.get("chunk");
        
        for (JsonNode chunkNode : chunks) {
            String chunkName = chunkNode.get("name").asText();
            int chunkOrder = chunkNode.get("order").asInt();

            Chunk chunk = Chunk.of(chunkOrder, chunkName, 0, category);
            chunkRepository.save(chunk);

            JsonNode materials = chunkNode.get("material");
            for (JsonNode materialNode : materials) {
                String materialName = materialNode.get("name").asText();
                int materialOrder = materialNode.get("order").asInt();

                Material material = Material.of(
                        materialOrder, materialName, 0, 0, 0,
                        Arrays.toString(new int[]{0,0,0,0,0,0}), chunk, null
                );
                materialRepository.save(material);
            }
        }
    }
}