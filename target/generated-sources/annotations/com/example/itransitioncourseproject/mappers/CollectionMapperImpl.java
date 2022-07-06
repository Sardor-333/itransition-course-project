package com.example.itransitioncourseproject.mappers;

import com.example.itransitioncourseproject.entities.Collection;
import com.example.itransitioncourseproject.payloads.request.collection.CollectionCreateDto;
import com.example.itransitioncourseproject.payloads.request.collection.CollectionEditDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-07-06T15:48:43+0500",
    comments = "version: 1.5.1.Final, compiler: javac, environment: Java 17.0.1 (Oracle Corporation)"
)
@Component
public class CollectionMapperImpl extends CollectionMapper {

    @Override
    public Collection mapFromCreateDtoToEntity(CollectionCreateDto src) {
        if ( src == null ) {
            return null;
        }

        Collection collection = new Collection();

        collection.setName( src.getName() );
        collection.setDescription( src.getDescription() );

        collection.setTopic( getTopic(src.getTopicId()) );

        return collection;
    }

    @Override
    public void mapFromEditDtoToEntity(CollectionEditDto src, Collection collection) {
        if ( src == null ) {
            return;
        }

        collection.setName( src.getName() );
        collection.setDescription( src.getDescription() );

        collection.setTopic( getTopic(src.getTopicId()) );
    }
}
