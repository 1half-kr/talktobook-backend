package com.lifelibrarians.lifebookshelf.classification.domain;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "categories")
@Getter
@ToString(callSuper = true, exclude = {"themes", "autobiography", "chunks"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    /* 고유 정보 { */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, name = "\"order\"")
    private Integer order;

    @Column(nullable = false, length = 100)
    private String name;
    /* } 고유 정보 */

    /* 연관 정보 { */
    /* 연관 정보 { */
    @ManyToMany(mappedBy = "categories")
    private Set<Theme> themes;
    /* } 연관 정보 */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autobiography_id", nullable = false)
    private Autobiography autobiography;

    @OneToMany(mappedBy = "category")
    private Set<Chunk> chunks;
    /* } 연관 정보 */

    /* 생성자 { */
    protected Category(Integer order, String name, Autobiography autobiography) {
        this.order = order;
        this.name = name;
        this.autobiography = autobiography;
    }

    public static Category of(Integer order, String name, Autobiography autobiography) {
        return new Category(order, name, autobiography);
    }

    public void setThemes(HashSet<Theme> themes) {
        this.themes = themes;
    }
    /* } 생성자 */
}
