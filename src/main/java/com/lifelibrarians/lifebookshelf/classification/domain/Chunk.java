package com.lifelibrarians.lifebookshelf.classification.domain;

import java.util.Set;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "chunks")
@Getter
@ToString(callSuper = true, exclude = {"category", "materials"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chunk {

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
    private Integer weight;
    /* } 고유 정보 */

    /* 연관 정보 { */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "chunk")
    private Set<Material> materials;
    /* } 연관 정보 */

    /* 생성자 { */
    protected Chunk(Integer order, String name, Integer weight, Category category) {
        this.order = order;
        this.name = name;
        this.weight = weight;
        this.category = category;
    }

    public static Chunk of(Integer order, String name, Integer weight, Category category) {
        return new Chunk(order, name, weight, category);
    }
    /* } 생성자 */

    /* 업데이트 메서드 { */
    public void updateWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getChunkOrder() {
        return this.order;
    }
    /* } 업데이트 메서드 */
}
