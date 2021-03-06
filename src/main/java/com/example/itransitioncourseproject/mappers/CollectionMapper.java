package com.example.itransitioncourseproject.mappers;

import com.example.itransitioncourseproject.entities.Collection;
import com.example.itransitioncourseproject.entities.Topic;
import com.example.itransitioncourseproject.payloads.request.collection.CollectionCreateDto;
import com.example.itransitioncourseproject.payloads.request.collection.CollectionEditDto;
import com.example.itransitioncourseproject.repositories.TopicRepo;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class CollectionMapper {

    @Autowired
    private TopicRepo topicRepo;

    @Mapping(target = "topic", expression = "java(getTopic(src.getTopicId()))")
    public abstract Collection mapFromCreateDtoToEntity(CollectionCreateDto src);

    @Mapping(target = "topic", expression = "java(getTopic(src.getTopicId()))")
    public abstract void mapFromEditDtoToEntity(CollectionEditDto src, @MappingTarget Collection collection);

    @Named("getTopic")
    public Topic getTopic(Long topicId) {
        return topicId == null ? null : topicRepo.findById(topicId).orElse(null);
    }
}
