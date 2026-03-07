package com.lifelibrarians.lifebookshelf.classification.domain;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "theme")
@Getter
@ToString(callSuper = true, exclude = {"categories"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Theme {

    /* 고유 정보 { */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, name = "\"order\"")
    private Integer order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ThemeNameType name;
    /* } 고유 정보 */

    /* 연관 정보 { */
    @ManyToMany
    @JoinTable(
            name = "theme_category",
            joinColumns = @JoinColumn(name = "theme_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories;
    /* } 연관 정보 */

    /* 생성자 { */
    protected Theme(Integer order, ThemeNameType name) {
        this.order = order;
        this.name = name;
    }

    public static Theme of(Integer order, ThemeNameType name) {
        return new Theme(order, name);
    }

    public void addCategory(Category category) {
        if (this.categories == null) this.categories = new HashSet<>();
        if (category.getThemes() == null) category.setThemes(new HashSet<>());

        this.categories.add(category);
        category.getThemes().add(this);
    }
    /* } 생성자 */
}
