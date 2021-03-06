package com.example.itransitioncourseproject.mappers;

import com.example.itransitioncourseproject.entities.Item;
import com.example.itransitioncourseproject.entities.Tag;
import com.example.itransitioncourseproject.exceptions.ObjectNotFoundException;
import com.example.itransitioncourseproject.payloads.request.item.ItemDto;
import com.example.itransitioncourseproject.repositories.TagRepo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class ItemMapper {

    @Autowired
    private TagRepo tagRepo;

    @Mapping(expression = "java(src.getName())", target = "name")
    @Mapping(expression = "java(getItemTags(src.getTagIdList()))", target = "tags")
    public abstract Item mapFromCreateDtoToEntity(ItemDto src);

    @Named("getItemTags")
    protected Set<Tag> getItemTags(Set<Long> tagIdList) {
        if (tagIdList == null || tagIdList.isEmpty())
            return null;

        return tagIdList
                .stream()
                .map(tagId -> tagRepo.findById(tagId).orElseThrow(() -> new ObjectNotFoundException("Tag with id: " + tagId + " not found!")))
                .collect(Collectors.toSet());
    }
}
