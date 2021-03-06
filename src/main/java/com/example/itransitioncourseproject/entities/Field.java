package com.example.itransitioncourseproject.entities;

import com.example.itransitioncourseproject.entities.abs.AbsEntity;
import com.example.itransitioncourseproject.enums.FieldType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "fields")
public class Field extends AbsEntity {

    @FullTextField
    @Column(nullable = false, updatable = false)
    String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    FieldType fieldType;

    @ManyToOne(fetch = FetchType.LAZY)
    Collection collection;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    List<Value> values;
}
