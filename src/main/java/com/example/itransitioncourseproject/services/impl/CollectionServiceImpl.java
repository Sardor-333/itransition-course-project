package com.example.itransitioncourseproject.services.impl;

import com.example.itransitioncourseproject.entities.CloudinaryResource;
import com.example.itransitioncourseproject.entities.Collection;
import com.example.itransitioncourseproject.entities.Field;
import com.example.itransitioncourseproject.entities.User;
import com.example.itransitioncourseproject.enums.UserRole;
import com.example.itransitioncourseproject.mappers.CollectionMapper;
import com.example.itransitioncourseproject.mappers.FieldMapper;
import com.example.itransitioncourseproject.pagination.Paged;
import com.example.itransitioncourseproject.pagination.Paging;
import com.example.itransitioncourseproject.payloads.request.FieldDto;
import com.example.itransitioncourseproject.payloads.request.collection.CollectionCreateDto;
import com.example.itransitioncourseproject.payloads.request.collection.CollectionEditDto;
import com.example.itransitioncourseproject.payloads.response.ApiResponse;
import com.example.itransitioncourseproject.projections.CollectionProjection;
import com.example.itransitioncourseproject.repositories.CloudinaryResourceRepo;
import com.example.itransitioncourseproject.repositories.CollectionRepo;
import com.example.itransitioncourseproject.repositories.FieldRepo;
import com.example.itransitioncourseproject.services.CollectionService;
import com.example.itransitioncourseproject.services.MultipartService;
import com.example.itransitioncourseproject.utils.AuthUtils;
import com.example.itransitioncourseproject.utils.PageSizeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepo collectionRepo;

    private final FieldRepo fieldRepo;

    private final CollectionMapper collectionMapper;

    private final FieldMapper fieldMapper;

    private final MultipartService multipartService;

    private final ResourceBundleMessageSource messageSource;

    private final CloudinaryResourceRepo cloudinaryResourceRepo;

    private final AuthUtils authUtils;

    @Override
    public List<CollectionProjection> getTop5LargestCollections() {
        return collectionRepo.getTop5BiggestCollections();
    }

    @Override
    public Paged<CollectionProjection> getCollections(Integer page, Integer size) {
        PageSizeUtils.validatePageAndSize(page, size, collectionRepo);
        Page<CollectionProjection> collectionsPage = collectionRepo.getCollectionsPageable(PageRequest.of(page - 1, size));
        return new Paged<>(collectionsPage, Paging.of(collectionsPage.getTotalPages(), page, size));
    }

    @Override
    public Paged<CollectionProjection> getMyCollections(Integer page, Integer size, User currentUser) {
        PageSizeUtils.validatePageAndSize(page, size, collectionRepo);
        Page<CollectionProjection> collectionsPage = collectionRepo.getMyCollections(currentUser.getId(), PageRequest.of(page - 1, size));
        return new Paged<>(collectionsPage, Paging.of(collectionsPage.getTotalPages(), page, size));
    }

    @Override
    public CollectionProjection getCollectionById(Long collectionId) {
        return collectionRepo.getCollectionById(collectionId);
    }

    @Override
    public ApiResponse createCollection(CollectionCreateDto collectionCreateDto, MultipartFile photo, User currentUser) {
        if (collectionRepo.existsByNameAndUserId(collectionCreateDto.getName(), currentUser.getId()))
            return new ApiResponse(false, messageSource.getMessage("error.userAlreadyHasCollectionWithName", new Object[]{collectionCreateDto.getName()}, Locale.getDefault()));

        Collection collection = collectionMapper.mapFromCreateDtoToEntity(collectionCreateDto);
        collection.setUser(currentUser);
        collection.setImg(saveImgOnCreate(photo));
        collection = collectionRepo.save(collection);
        saveFieldsOnCreate(collectionCreateDto.getFieldDtoList(), collection);

        return new ApiResponse(true, messageSource.getMessage("ok.collectionCreated", null, Locale.getDefault()));
    }

    @Override
    public ApiResponse editCollection(Long collectionId, CollectionEditDto collectionEditDto, MultipartFile img, User currentUser) {
        Collection collection = collectionRepo.findById(collectionId).orElse(null);
        boolean userHasAccessToCollection = authUtils.userHasAccessToCollection(collection, currentUser);
        if (!userHasAccessToCollection)
            return new ApiResponse(false, messageSource.getMessage("error.accessDenied", null, Locale.getDefault()));

        collectionMapper.mapFromEditDtoToEntity(collectionEditDto, collection);
        setImgOnEdit(collection, img);
        collectionRepo.save(collection);

        return new ApiResponse(true, messageSource.getMessage("ok.collectionEdited", null, Locale.getDefault()));
    }

    @Override
    public ApiResponse deleteCollection(Long collectionId, User currentUser) {
        Optional<Collection> collectionOptional = collectionRepo.findById(collectionId);
        if (collectionOptional.isPresent()) {
            Collection collection = collectionOptional.get();

            if (authUtils.hasRole(currentUser, UserRole.ROLE_USER) && !collection.getUser().getId().equals(currentUser.getId()))
                return new ApiResponse(false, messageSource.getMessage("error.accessDenied", null, Locale.getDefault()));

            collectionRepo.delete(collection);
            return new ApiResponse(true, messageSource.getMessage("ok.collectionDeleted", null, Locale.getDefault()));
        }
        return new ApiResponse(false, messageSource.getMessage("error.objectNotFound", new Object[]{"Collection", collectionId}, Locale.getDefault()));
    }

    private void saveFieldsOnCreate(List<FieldDto> fieldDtoList, Collection collection) {
        if (fieldDtoList == null || fieldDtoList.isEmpty())
            return;

        fieldDtoList
                .stream()
                .filter(fieldDto -> !fieldDto.getName().equals("name")
                        && !fieldRepo.existsByNameAndCollectionId(fieldDto.getName(), collection.getId()))
                .forEach(fieldDto -> {
                    Field field = fieldMapper.mapFromCreateDtoToEntity(fieldDto);
                    field.setCollection(collection);
                    fieldRepo.save(field);
                });
    }

    private CloudinaryResource saveImgOnCreate(MultipartFile img) {
        try {
            if (multipartService.isValidMultipart(img)) {
                CloudinaryResource cloudinaryResource = multipartService.generateCloudinaryResourceFromMultipart(img);
                return cloudinaryResourceRepo.save(cloudinaryResource);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setImgOnEdit(Collection collection, MultipartFile img) {
        try {
            if (multipartService.isValidMultipart(img)) {
                if (collection.getImg() != null) {
                    cloudinaryResourceRepo.delete(collection.getImg());
                }
                CloudinaryResource cloudinaryResource = cloudinaryResourceRepo.save(multipartService.generateCloudinaryResourceFromMultipart(img));
                collection.setImg(cloudinaryResource);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
