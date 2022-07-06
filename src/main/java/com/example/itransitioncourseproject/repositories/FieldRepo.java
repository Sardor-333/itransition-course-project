package com.example.itransitioncourseproject.repositories;

import com.example.itransitioncourseproject.entities.Field;
import com.example.itransitioncourseproject.projections.FieldProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface FieldRepo extends JpaRepository<Field, Long> {

    boolean existsByNameAndCollectionId(String name, Long collection_id);

    @Query(
            nativeQuery = true,
            value = "select f.id         as id,\n" +
                    "       f.name       as name,\n" +
                    "       f.field_type as fieldType\n" +
                    "from fields f\n" +
                    "         join collections c on c.id = f.collection_id\n" +
                    "where c.id = :collectionId"
    )
    List<FieldProjection> getCollectionFields(@Param("collectionId") Long collectionId);

    Optional<Field> findByCollectionIdAndName(Long collection_id, String name);
}
