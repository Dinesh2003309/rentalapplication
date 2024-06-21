package com.rental.repository;

import com.rental.enums.Status;
import com.rental.model.Applicants;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicantRepository extends JpaRepository<Applicants, Integer> {
    @Query("SELECT a FROM Applicants a WHERE a.user.id = :userId AND a.HouseId = :houseId")
    Optional<Applicants> findByUserIdAndHouseId(@Param("userId") Integer userId, @Param("houseId") Integer houseId);

    @Query(value = "SELECT json_build_object('ApplicationId', a.id, 'houseId', a.house_id, 'status', a.status, 'userId', a.user_id, 'ownerId', a.owner_id,'createdAt', a.created_at, 'updatedAt', a.updated_at, " +
            "'username', CONCAT(u.firstname, ' ', u.lastname), 'email', u.email ) " +
            "FROM house_applicants a LEFT JOIN users u ON a.user_id = u.id " +
            "WHERE a.house_id = ?1 AND a.status <> 'REJECTED' ", nativeQuery = true)
    Page<String> applicantsByHouseId(@Param("houseId") Integer houseId, Pageable pageable);

    @Query(value = "select count(id) from house_applicants where house_id = ?1 and status != 'REJECTED' ", nativeQuery = true)
    long countApplicantsByHouseId(Integer houseId);


    @Query(value = "SELECT json_build_object('ApplicationId', a.id, 'houseId', a.house_id, 'status', a.status, 'userId', a.user_id, 'ownerId', a.owner_id,'createdAt', a.created_at, 'updatedAt', a.updated_at, 'username', CONCAT(u.firstname, ' ', u.lastname), 'email', u.email) " +
            "FROM house_applicants a LEFT JOIN users u ON a.user_id = u.id " +
            "WHERE a.house_id = ?1 AND a.status <> 'REJECTED' ", nativeQuery = true)
    List<String> applicantsByrentalId(@Param("houseId") Integer houseId);


    @Query(value = "select json_build_object('ApplicationId', a.id, 'houseId', a.house_id, 'status', a.status,"
            + "'userId', a.user_id, 'ownerId', a.owner_id,'createdAt', a.created_at, 'updatedAt', a.updated_at) "
            + "from house_applicants a where a.id=?1",nativeQuery = true)
    String findApplicantById(@Param("id") Integer id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM house_applicants WHERE house_id = ?1", nativeQuery = true)
    void deleteByhouseId(Integer houseId);

    @Query("SELECT a FROM Applicants a WHERE a.HouseId = :houseId AND a.status = :status")
    Optional<Applicants> findByHouseIdAndStatus(@Param("houseId") Integer houseId, @Param("status") Status status);

    @Modifying
    @Transactional
    @Query(value = "update house_applicants set status = 'REJECTED' where house_id = ?1 and user_id != ?2 and (status != 'ACCEPTED' and status != 'REJECTED') returning user_id ", nativeQuery = true)
    List<Integer> rejectOtherApplicants(Integer houseId,Integer applicantUserId);


@Query(value = "SELECT json_build_object(" +
        "'id', r.id, " +
        "'title', r.title, " +
        "'address', r.address, " +
        "'streetAddress', r.street_address, " +
        "'apartmentName', r.apartment_name, " +
        "'houselatitude', r.houselatitude, " +
        "'houselongitude', r.houselongitude, " +
        "'propertytype', r.propertytype, " +
        "'sqft', r.sqft, " +
        "'garage', r.garage, " +
        "'bedrooms', r.bedrooms, " +
        "'bathrooms', r.bathrooms, " +
        "'constructiontype', r.constructiontype, " +
        "'yearbuilt', r.yearbuilt, " +
        "'expectedrent', r.expectedrent, " +
        "'deposit', r.deposit, " +
        "'availabilityfrom', r.availabilityfrom, " +
        "'availabilitytill', r.availabilitytill, " +
        "'description', r.description, " +
        "'smokingpolicy', r.smokingpolicy, " +
        "'vegetarianpreference', r.vegetarianpreference, " +
        "'utilities', r.utilities, " +
        "'petsFriendly', r.pets_friendly, " +
        "'addaminities', r.addaminities, " +
        "'deleted', r.deleted, " +
        "'status', r.status, " +
        "'rentType', r.rent_type, " +
        "'createdAt', r.created_at, " +
        " 'addimages',json_agg(json_build_object('id', i.id, 'imageSize', i.image_size, 'imageUrl', i.image_url) order by i.id desc),"+
        "'updatedAt', r.updated_at, " +
        "'userId', r.user_id," +
        "'applicationStatus', a.status " +
        ") " +
        "FROM rental r LEFT JOIN image_details i ON r.id = i.house_details_id " +
        "LEFT JOIN users u ON r.user_id = u.id " +
        "JOIN house_applicants a ON r.id = a.house_id " +
        "LEFT JOIN users au ON a.user_id = au.id " +
        " WHERE a.user_id = :userId  GROUP BY r.id, u.id, au.id, a.user_id ", nativeQuery = true)
 List<String> findHouseIdsByUserId(@Param("userId") Integer userId);

    // rental accepted by user
    @Query(value = "SELECT json_build_object(" +
            "'id', h.id, " +
            "'title', h.title, " +
            "'streetAddress', h.street_address, " +
            "'apartmentName', h.apartment_name, " +
            "'address', h.address, " +
            "'houselatitude', h.houselatitude, " +
            "'houselongitude', h.houselongitude, " +
            "'propertytype', h.propertytype, " +
            "'sqft', h.sqft, " +
            "'garage', h.garage, " +
            "'bedrooms', h.bedrooms, " +
            "'bathrooms', h.bathrooms, " +
            "'constructiontype', h.constructiontype, " +
            "'yearbuilt', h.yearbuilt, " +
            "'expectedrent', h.expectedrent, " +
            "'deposit', h.deposit, " +
            "'availabilityfrom', h.availabilityfrom, " +
            "'availabilitytill', h.availabilitytill, " +
            "'description', h.description, " +
            "'smokingpolicy', h.smokingpolicy, " +
            "'vegetarianpreference', h.vegetarianpreference, " +
            "'utilities', h.utilities, " +
            "'petsFriendly', h.pets_friendly, " +
            "'addaminities', h.addaminities, " +
            "'deleted', h.deleted, " +
            "'status', h.status, " +
            "'rentType', h.rent_type, " +
            "'validityStatus', h.validity_status, " +
            "'createdAt', h.created_at, " +
            "'updatedAt', h.updated_at, " +
            "'applicationStatus', ha.status, " +
            " 'addimages',json_agg(json_build_object('id', i.id, 'imageSize', i.image_size, 'imageUrl', i.image_url,'imageName', i.image_name) order by i.id desc),"+
            "'userId', h.user_id,'username', u.firstname || ' ' || u.lastname," +
            "'acceptedId', ha.user_id,'acceptedname',au.firstname || ' ' || au.lastname" +
            ") " +
            "FROM house_applicants ha " +
            "LEFT JOIN users au ON ha.user_id = au.id " +
            "LEFT JOIN rental h ON ha.house_id = h.id " +
            "LEFT JOIN users u ON h.user_id = u.id " +
            " LEFT JOIN image_details i ON h.id = i.house_details_id WHERE ha.user_id = :userId AND h.deleted = false  GROUP BY h.id, u.id, au.id, ha.user_id, ha.status ORDER BY h.created_at DESC ", nativeQuery = true)
    Page<String> findAppliedHouseByUserId(@Param("userId") Integer userId, Pageable pageable);

    @Query(value = "select count(ha.id) from house_applicants ha LEFT JOIN rental h ON ha.house_id = h.id where ha.user_id = ?1 and h.deleted = false ", nativeQuery = true)
    long countAppliedHouseByUserId(int userId);


//offer made filter
    @Query(value =
            "SELECT json_build_object(" +
                    "'id', h.id,'title', h.title, 'address', h.address, " +
                    "'streetAddress', h.street_address, 'apartmentName', h.apartment_name,'houselatitude', h.houselatitude, 'houselongitude', h.houselongitude,'propertytype', h.propertytype, " +
                    "'sqft', h.sqft, 'garage', h.garage,'bedrooms', h.bedrooms,'bathrooms', h.bathrooms,'constructiontype', h.constructiontype, 'yearbuilt', h.yearbuilt, " +
                    "'expectedrent', h.expectedrent,'deposit', h.deposit, 'availabilityfrom', h.availabilityfrom, 'availabilitytill', h.availabilitytill, 'description', h.description, 'smokingpolicy', h.smokingpolicy, " +
                    "'vegetarianpreference', h.vegetarianpreference, 'utilities', h.utilities, 'petsFriendly', h.pets_friendly,'addaminities', h.addaminities, 'deleted', h.deleted, " +
                    "'status', h.status, 'validityStatus', h.validity_status, 'rentType', h.rent_type, 'createdAt', h.created_at, 'updatedAt', h.updated_at,'applicationStatus', ha.status, " +
                    "'addimages',json_agg(json_build_object('id', i.id, 'imageSize', i.image_size, 'imageUrl', i.image_url, 'imageName', i.image_name ) order by i.id desc),"+
                    "'userId', h.user_id,'username', u.firstname || ' ' || u.lastname," +
                    "'acceptedId', ha.user_id,'acceptedname',au.firstname || ' ' || au.lastname" +
                    ") " +
            "FROM house_applicants ha " +
            "LEFT JOIN users au ON ha.user_id = au.id " +
            "LEFT JOIN rental h ON ha.house_id = h.id " +
            "LEFT JOIN users u ON h.user_id = u.id " +
            " LEFT JOIN image_details i ON h.id = i.house_details_id WHERE ha.user_id = ?1 " +
                    "AND (?2 IS NULL OR h.address = ?2) " +
                    "AND (?3 IS NULL OR h.propertytype = ?3) " +
                    "AND (?4 IS NULL OR h.constructiontype = ?4) " +
                    "AND (COALESCE(?5, h.expectedrent) <= h.expectedrent OR h.expectedrent IS NULL) " +
                    "AND (COALESCE(?6, h.expectedrent) >= h.expectedrent OR h.expectedrent IS NULL) " +
            " AND h.deleted = false  GROUP BY h.id, u.id, au.id, ha.user_id, ha.status ORDER BY h.created_at DESC ", nativeQuery = true)
    Page<String> findAppliedFilterData(
            Integer userId, String location, String propertytype, String constructiontype, Double maxexpectedrent, Double minexpectedrent, Pageable pageable);

    @Query(value = "select count(ha.id) from house_applicants ha " +
            "LEFT JOIN rental h ON ha.house_id = h.id " +
            "WHERE ha.user_id = ?1 " +
            "AND (?2 IS NULL OR h.address = ?2) " +
            "AND (?3 IS NULL OR h.propertytype = ?3) " +
            "AND (?4 IS NULL OR h.constructiontype = ?4) " +
            "AND (COALESCE(?5, h.expectedrent) <= h.expectedrent OR h.expectedrent IS NULL) " +
            "AND (COALESCE(?6, h.expectedrent) >= h.expectedrent OR h.expectedrent IS NULL) " +
            " AND h.deleted = false " ,nativeQuery = true)
    long countAppliedFilterData(Integer userId, String location, String propertytype, String constructiontype, Double maxexpectedrent, Double minexpectedrent);

}