package com.example.itransitioncourseproject.services;

import com.example.itransitioncourseproject.entities.Item;
import com.example.itransitioncourseproject.entities.User;
import com.example.itransitioncourseproject.payloads.request.SearchDto;
import com.example.itransitioncourseproject.payloads.request.item.ItemDto;
import com.example.itransitioncourseproject.payloads.response.ApiResponse;
import com.example.itransitioncourseproject.payloads.response.SearchResult;
import com.example.itransitioncourseproject.projections.FieldProjection;
import com.example.itransitioncourseproject.projections.ItemDetailProjection;
import com.example.itransitioncourseproject.projections.ItemProjection;

import java.util.List;

public interface ItemService {

    List<ItemProjection> get5LatestAddedItems();

    List<ItemProjection> getItems();

    List<ItemProjection> getItemsByCollectionId(Long collectionId);

    List<ItemProjection> getItemsByTagId(Long tagId);

    ItemDetailProjection getItemDetailsById(Long itemId, User currentUser);

    List<FieldProjection> getCollectionFields(Long collectionId);


    ApiResponse createItem(Long collectionId, ItemDto itemDto, User currentUser);

    ApiResponse deleteItemById(Long itemId, User currentUser);

    SearchResult<Item> searchItems(SearchDto searchDto);
}
