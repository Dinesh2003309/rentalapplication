package com.rental.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rental.model.HouseDetails;

@Repository
public interface HousedetailsRepository extends JpaRepository<HouseDetails, Integer> {

	//working
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
			"'updatedAt', r.updated_at, " +
			" 'addimages',json_agg(json_build_object('id', i.id, 'imageSize', i.image_size, 'imageUrl', i.image_url, 'imageName', i.image_name) order by i.id desc),"+
			"'userId', r.user_id,'username', u.firstname || ' ' || u.lastname" +
			") " +
			"FROM rental r " +
			"LEFT JOIN users u ON r.user_id = u.id " +
			"LEFT JOIN image_details i ON r.id = i.house_details_id WHERE r.user_id = ?1 AND r.deleted = false AND r.status <> 'COMPLETED' GROUP BY r.id , u.id ORDER BY r.created_at DESC", nativeQuery = true)
	Page<String> findHouseDetailsByUserId(int userId, Pageable pageable);

	@Query(value = "SELECT COUNT(*) FROM house_applicants a WHERE a.house_id = ?1 AND a.status != 'REJECTED'", nativeQuery = true)
	long applicantCount(Integer houseId);

	@Query(value = "select count(id) from rental where user_id = ?1 and deleted = false and status != 'COMPLETED' ", nativeQuery = true)
	long countHouseDetailsByUserId(int userId);

	// working
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
			"'updatedAt', r.updated_at, " +
			" 'addimages',json_agg(json_build_object('id', i.id, 'imageSize', i.image_size, 'imageUrl', i.image_url, 'imageName', i.image_name) order by i.id desc),"+
			"'userId', r.user_id,'username', u.firstname || ' ' || u.lastname" +
			") " +
			"FROM rental r LEFT JOIN image_details i ON r.id = i.house_details_id LEFT JOIN users u ON r.user_id = u.id " +
			"WHERE r.deleted = false AND r.status <> 'COMPLETED' AND r.user_id <> :userId " +
			"AND r.id NOT IN (SELECT a.house_id FROM house_applicants a WHERE a.user_id = :userId) " +
			" GROUP BY r.id, u.id ORDER BY r.created_at DESC ", nativeQuery = true)
	Page<String> findAllActiveHouseDetailsByOtherUser(@Param("userId") int userId, Pageable pageable);

	@Query(value = "select count(r.id) from rental r where r.user_id != ?1 and r.deleted = false and r.status != 'COMPLETED'" +
			"and r.id not in (select a.house_id from house_applicants a where a.user_id = ?1) ", nativeQuery = true)
	long countAllActiveHouseDetailsByOtherUser(int userId);


	//working
	@Query(value = "SELECT json_build_object(" +
		"'id', h.id, " +
		"'title', h.title, " +
		"'address', h.address, " +
			"'streetAddress', h.street_address, " +
			"'apartmentName', h.apartment_name, " +
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
			"'validityStatus', h.validity_status, " +
			"'rentType', h.rent_type, " +
		"'createdAt', h.created_at, " +
		"'updatedAt', h.updated_at, " +
		" 'addimages',json_agg(json_build_object('id', i.id, 'imageSize', i.image_size, 'imageUrl', i.image_url, 'imageName', i.image_name) order by i.id desc),"+
		"'userId', h.user_id,'username', u.firstname || ' ' || u.lastname," +
		"'acceptedId', ha.user_id,'acceptedname',au.firstname || ' ' || au.lastname" +
		") " +
		"FROM rental h LEFT JOIN image_details i ON h.id = i.house_details_id " +
		"LEFT JOIN users u ON h.user_id = u.id " +
		"LEFT JOIN house_applicants ha ON h.id = ha.house_id AND ha.status = 'ACCEPTED'  " +
		"LEFT JOIN users au ON ha.user_id = au.id " +
		"WHERE h.deleted = false AND h.status = 'COMPLETED' AND h.user_id = :userId GROUP BY h.id, u.id, au.id, ha.user_id ORDER BY h.created_at DESC ", nativeQuery = true)
Page<String> findCompletedHouseDetailsByUserId(@Param("userId") Integer userId, Pageable pageable);

	@Query(value = "select count(id) from rental where user_id = ?1 and deleted = false and status = 'COMPLETED' ", nativeQuery = true)
	long countCompletedHouseDetailsByUserId(int userId);


	//working
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
			"'updatedAt', r.updated_at, " +
			" 'addimages',json_agg(json_build_object('id', i.id, 'imageSize', i.image_size, 'imageUrl', i.image_url, 'imageName', i.image_name) order by i.id desc),"+
			"'userId', r.user_id,'username', u.firstname || ' ' || u.lastname," +
			"'acceptedId', ha.user_id,'acceptedname',au.firstname || ' ' || au.lastname" +
			") " +
			"FROM rental r LEFT JOIN image_details i ON r.id = i.house_details_id LEFT JOIN users u ON r.user_id = u.id " +
			"LEFT JOIN house_applicants ha ON r.id = ha.house_id " +
			"LEFT JOIN users au ON ha.user_id = au.id " +
			"WHERE r.deleted = false AND r.status = 'COMPLETED' AND r.user_id <> :userId GROUP BY r.id, u.id, au.id, ha.user_id ORDER BY r.created_at DESC ", nativeQuery = true)
	Page<String> findAllCompletedHouseDetailsByOtherUser(@Param("userId") int userId, Pageable pageable);


// working
	@Query(value = "SELECT json_build_object(" +
			"'id', h.id, " +
			"'title', h.title, " +
			"'address', h.address, " +
			"'streetAddress', h.street_address, " +
			"'apartmentName', h.apartment_name, " +
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
			"'createdAt', h.created_at, " +
			"'updatedAt', h.updated_at, " +
			" 'addimages',json_agg(json_build_object('id', i.id, 'imageSize', i.image_size, 'imageUrl', i.image_url, 'imageName' ,i.image_name) order by i.id desc),"+
			"'userId', h.user_id,'username', CONCAT(u.firstname, ' ', u.lastname)" +
			") " +
			"FROM rental h " +
			"LEFT JOIN users u ON h.user_id = u.id " +
			" LEFT JOIN image_details i ON h.id = i.house_details_id WHERE h.id = ?1 GROUP BY h.id ,u.id ", nativeQuery = true)
	String findHouseDetailsById(Integer houseId);

	//working
	@Query(value =
			"SELECT json_build_object(" +
					"'id', h.id,'title', h.title, 'address', h.address, " +
					"'streetAddress', h.street_address, 'apartmentName', h.apartment_name,'houselatitude', h.houselatitude, 'houselongitude', h.houselongitude,'propertytype', h.propertytype, " +
					"'sqft', h.sqft, 'garage', h.garage,'bedrooms', h.bedrooms,'bathrooms', h.bathrooms,'constructiontype', h.constructiontype, 'yearbuilt', h.yearbuilt, " +
					"'expectedrent', h.expectedrent,'deposit', h.deposit, 'availabilityfrom', h.availabilityfrom, 'availabilitytill', h.availabilitytill, 'description', h.description, 'smokingpolicy', h.smokingpolicy, " +
					"'vegetarianpreference', h.vegetarianpreference, 'utilities', h.utilities, 'petsFriendly', h.pets_friendly,'addaminities', h.addaminities, 'deleted', h.deleted, " +
					"'status', h.status, 'validityStatus', h.validity_status, 'rentType', h.rent_type, 'createdAt', h.created_at, 'updatedAt', h.updated_at, " +
					"'addimages',json_agg(json_build_object('id', i.id, 'imageSize', i.image_size, 'imageUrl', i.image_url, 'imageName', i.image_name ) order by i.id desc),"+
					"'userId', h.user_id,'username', u.firstname || ' ' || u.lastname) " +
					"FROM rental h LEFT JOIN image_details i ON h.id = i.house_details_id LEFT JOIN users u ON h.user_id = u.id " +
					"WHERE h.user_id = ?1 " +
					"AND (?2 IS NULL OR h.address = ?2) " +
					"AND (?3 IS NULL OR h.propertytype = ?3) " +
					"AND (?4 IS NULL OR h.constructiontype = ?4) " +
					"AND (COALESCE(?5, h.expectedrent) <= h.expectedrent OR h.expectedrent IS NULL) " +
					"AND (COALESCE(?6, h.expectedrent) >= h.expectedrent OR h.expectedrent IS NULL) " +
					"AND h.deleted = false and h.status != 'COMPLETED' GROUP BY h.id , u.id ORDER BY h.created_at DESC" , nativeQuery = true)
	Page<String> myhelpsfilterhouse(
			Integer userId, String location, String propertytype, String constructiontype, Double maxexpectedrent, Double minexpectedrent, Pageable pageable);

	@Query(value = "select count(h.id) from rental h " +
			"WHERE h.user_id = ?1 " +
			"AND (?2 IS NULL OR h.address = ?2) " +
			"AND (?3 IS NULL OR h.propertytype = ?3) " +
			"AND (?4 IS NULL OR h.constructiontype = ?4) " +
			"AND (COALESCE(?5, h.expectedrent) <= h.expectedrent OR h.expectedrent IS NULL) " +
			"AND (COALESCE(?6, h.expectedrent) >= h.expectedrent OR h.expectedrent IS NULL) " +
			"and h.deleted = false and h.status != 'COMPLETED' ", nativeQuery = true)
	long countmyhelpsfilterhouse(Integer userId, String location, String propertytype, String constructiontype, Double maxexpectedrent, Double minexpectedrent);

	//working

	@Query(value =
			"SELECT json_build_object(" +
					"'id', h.id,'title', h.title, 'address', h.address, " +
					"'streetAddress', h.street_address, 'apartmentName', h.apartment_name,'houselatitude', h.houselatitude, 'houselongitude', h.houselongitude,'propertytype', h.propertytype, " +
					"'sqft', h.sqft, 'garage', h.garage,'bedrooms', h.bedrooms,'bathrooms', h.bathrooms,'constructiontype', h.constructiontype, 'yearbuilt', h.yearbuilt, " +
					"'expectedrent', h.expectedrent,'deposit', h.deposit, 'availabilityfrom', h.availabilityfrom, 'availabilitytill', h.availabilitytill, 'description', h.description, 'smokingpolicy', h.smokingpolicy, " +
					"'vegetarianpreference', h.vegetarianpreference, 'utilities', h.utilities, 'petsFriendly', h.pets_friendly,'addaminities', h.addaminities, 'deleted', h.deleted, " +
					"'status', h.status, 'validityStatus', h.validity_status, 'rentType', h.rent_type, 'createdAt', h.created_at, 'updatedAt', h.updated_at, " +
					"'addimages',json_agg(json_build_object('id', i.id, 'imageSize', i.image_size, 'imageUrl', i.image_url, 'imageName', i.image_name ) order by i.id desc),"+
					"'userId', h.user_id, 'username', u.firstname || ' ' || u.lastname" +
					") " +
					"FROM rental h LEFT JOIN image_details i ON h.id = i.house_details_id " +
					"LEFT JOIN users u ON h.user_id = u.id " +
					"WHERE " +
					"h.user_id != ?1 AND h.status != 'COMPLETED' " +
					"AND (?2 IS NULL OR h.address = ?2) " +
					"AND (?3 IS NULL OR h.propertytype = ?3) " +
					"AND (?4 IS NULL OR h.constructiontype = ?4) " +
					"AND (COALESCE(?5, h.expectedrent) <= h.expectedrent OR h.expectedrent IS NULL) " +
					"AND (COALESCE(?6, h.expectedrent) >= h.expectedrent OR h.expectedrent IS NULL) " +
					"AND h.id NOT IN (SELECT a.house_id FROM house_applicants a WHERE a.user_id = ?1) " +
			"and h.deleted = false  GROUP BY h.id, u.id ORDER BY h.created_at DESC"
			, nativeQuery = true)
	Page<String> exploresfilterhouse(
			Integer userId, String location, String propertytype, String constructiontype, Double maxexpectedrent, Double minexpectedrent, Pageable pageable);

	@Query(value = "select count(h.id) from rental h where " +
			"h.user_id != ?1 AND h.status != 'COMPLETED' " +
			"AND (?2 IS NULL OR h.address = ?2) " +
			"AND (?3 IS NULL OR h.propertytype = ?3) " +
			"AND (?4 IS NULL OR h.constructiontype = ?4) " +
			"AND (COALESCE(?5, h.expectedrent) <= h.expectedrent OR h.expectedrent IS NULL) " +
			"AND (COALESCE(?6, h.expectedrent) >= h.expectedrent OR h.expectedrent IS NULL) " +
			"AND h.id NOT IN (SELECT a.house_id FROM house_applicants a WHERE a.user_id = ?1) " +
			"and h.deleted = false", nativeQuery = true)
	long countExploreFilter(Integer userId, String location, String propertytype, String constructiontype, Double maxexpectedrent, Double minexpectedrent);

	@Query(value =
			"SELECT json_build_object(" +
					"'id', h.id,'title', h.title, 'address', h.address, " +
					"'streetAddress', h.street_address, 'apartmentName', h.apartment_name,'houselatitude', h.houselatitude, 'houselongitude', h.houselongitude,'propertytype', h.propertytype, " +
					"'sqft', h.sqft, 'garage', h.garage,'bedrooms', h.bedrooms,'bathrooms', h.bathrooms,'constructiontype', h.constructiontype, 'yearbuilt', h.yearbuilt, " +
					"'expectedrent', h.expectedrent,'deposit', h.deposit, 'availabilityfrom', h.availabilityfrom, 'availabilitytill', h.availabilitytill, 'description', h.description, 'smokingpolicy', h.smokingpolicy, " +
					"'vegetarianpreference', h.vegetarianpreference, 'utilities', h.utilities, 'petsFriendly', h.pets_friendly,'addaminities', h.addaminities, 'deleted', h.deleted, " +
					"'status', h.status, 'validityStatus', h.validity_status, 'rentType', h.rent_type, 'createdAt', h.created_at, 'updatedAt', h.updated_at, " +
					"'addimages',json_agg(json_build_object('id', i.id, 'imageSize', i.image_size, 'imageUrl', i.image_url, 'imageName', i.image_name ) order by i.id desc),"+
					"'userId', h.user_id,'username', u.firstname || ' ' || u.lastname," +
					"'acceptedId', ha.user_id,'acceptedname',au.firstname || ' ' || au.lastname" +
					") " +
					"FROM rental h LEFT JOIN image_details i ON h.id = i.house_details_id " +
					"LEFT JOIN users u ON h.user_id = u.id " +
					"LEFT JOIN house_applicants ha ON h.id = ha.house_id AND ha.status = 'ACCEPTED'  " +
					"LEFT JOIN users au ON ha.user_id = au.id " +
					"WHERE h.deleted = false AND h.status = 'COMPLETED' AND h.user_id = ?1 " +
					"AND (?2 IS NULL OR h.address = ?2) " +
					"AND (?3 IS NULL OR h.propertytype = ?3) " +
					"AND (?4 IS NULL OR h.constructiontype = ?4) " +
					"AND (COALESCE(?5, h.expectedrent) <= h.expectedrent OR h.expectedrent IS NULL) " +
					"AND (COALESCE(?6, h.expectedrent) >= h.expectedrent OR h.expectedrent IS NULL) " +
			"GROUP BY h.id, u.id, au.id, ha.user_id ORDER BY h.created_at DESC ", nativeQuery = true)
	Page<String> findCompletedFilterData(
			Integer userId, String location, String propertytype, String constructiontype, Double maxexpectedrent, Double minexpectedrent, Pageable pageable);

	@Query(value = "select count(h.id) from rental h " +
			"WHERE h.deleted = false AND h.status = 'COMPLETED' AND h.user_id = ?1 " +
			"AND (?2 IS NULL OR h.address = ?2) " +
			"AND (?3 IS NULL OR h.propertytype = ?3) " +
			"AND (?4 IS NULL OR h.constructiontype = ?4) " +
			"AND (COALESCE(?5, h.expectedrent) <= h.expectedrent OR h.expectedrent IS NULL) " +
			"AND (COALESCE(?6, h.expectedrent) >= h.expectedrent OR h.expectedrent IS NULL) ", nativeQuery = true)
	long countCompletedFilterData(Integer userId, String location, String propertytype, String constructiontype, Double maxexpectedrent, Double minexpectedrent);



}
