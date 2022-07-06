package com.example.itransitioncourseproject.mappers;

import com.example.itransitioncourseproject.entities.Item;
import com.example.itransitioncourseproject.payloads.request.item.ItemCreateDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-07-06T17:37:23+0500",
    comments = "version: 1.5.1.Final, compiler: javac, environment: Java 17.0.1 (Oracle Corporation)"
)
@Component
public class ItemMapperImpl extends ItemMapper {

    @Override
    public Item mapFromCreateDtoToEntity(ItemCreateDto src) {
        if ( src == null ) {
            return null;
        }

        Item item = new Item();

        item.setName( src.getName() );
        item.setTags( getItemTags(src.getTagIdList()) );

        return item;
    }
}
