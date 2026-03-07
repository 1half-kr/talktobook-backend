package com.lifelibrarians.lifebookshelf.classification.domain;

import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "materials")
@Getter
@ToString(callSuper = true, exclude = {"chunk"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Material {

    /* 고유 정보 { */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, name = "\"order\"")
    private Integer order;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer example;

    @Column(nullable = false, name = "similar_event")
    private Integer similarEvent;

    @Column(nullable = false)
    private Integer count;

    @Lob
    @Column(nullable = false)
    private String principle;
    /* } 고유 정보 */

    @Column(nullable = true, name = "image_url")
    private String imageUrl;

    /* 연관 정보 { */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chunk_id", nullable = false)
    private Chunk chunk;
    /* } 연관 정보 */

    /* 생성자 { */
    protected Material(Integer order, String name,
                       Integer example, Integer similarEvent,
                       Integer count, String principle,
                       Chunk chunk, String imageUrl) {
        this.order = order;
        this.name = name;
        this.example = example;
        this.similarEvent = similarEvent;
        this.count = count;
        this.principle = principle;
        this.chunk = chunk;
        this.imageUrl = imageUrl;
    }

    public static Material of(Integer order, String name,
                              Integer example, Integer similarEvent,
                              Integer count, String principle,
                              Chunk chunk, String imageUrl) {
        return new Material(order, name, example, similarEvent, count, principle, chunk, imageUrl);
    }
    /* } 생성자 */

    /* 업데이트 메서드 { */
    public void updateExample(Integer example) {
        this.example = example;
    }

    public void updateSimilarEvent(Integer similarEvent) {
        this.similarEvent = similarEvent;
    }

    public void updateCount(Integer count) {
        this.count = count;
    }

    public void updatePrinciple(String principle) {
        this.principle = principle;
    }

    public Integer getMaterialOrder() {
        return this.order;
    }
    /* } 업데이트 메서드 */
}
