package com.rental.repository;

import com.rental.model.Image;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository  extends JpaRepository<Image, Integer> {


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM image_details WHERE id IN :imageIds", nativeQuery = true)
    void deleteByimageId(@Param("imageIds") List<Integer> imageIds);

}
