package com.rental.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import com.rental.constants.Declarations;
import com.rental.constants.ToastMessage;
import com.rental.dtos.*;
import com.rental.enums.HouseStatus;
import com.rental.enums.RentalValidityStatus;
import com.rental.model.*;
import com.rental.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.rental.enums.Status;
import com.rental.errorhandler.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.rental.payload.Response;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class HouseDetailsServiceImpl implements HouseDetailsService {

    @Value("${AWS_S3_UPLOADURL}")
    private String awsBucketName;

    @Value("${AWS_S3_VIEW_URL}")
    private String viewUrl;

    private final AmazonS3 amazonS3;

    private final HousedetailsRepository housedetailsRepository;

    private final UserRepository userRepository;

    private final ApplicantRepository applicantRepository;

    private final ImageRepository imageRepository;


    public static final Logger logger = LoggerFactory.getLogger(HouseDetailsServiceImpl.class);


    /**
     * Adds house details to the database and performs validation checks on the input data.
     *
     * @param request The HTTP request object containing the user ID.
     * @param houseDetailsDTO The DTO object containing the house details to be added.
     * @return The response object containing the success status, message, and the created house details.
     */
    @Override
    public Response   addHouseDetailsResponse(HttpServletRequest request, HouseDetailsDTO houseDetailsDTO) {
        try {
            Integer userId = (Integer) request.getAttribute(Declarations.USER_ID);
            User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(ToastMessage.USER_NOT_FOUND));
            HouseDetails houseDetails = HouseDetails.builder()
                    .title(houseDetailsDTO.getTitle())
                    .address(houseDetailsDTO.getAddress())
                    .streetAddress(houseDetailsDTO.getStreetAddress())
                    .apartmentName(houseDetailsDTO.getApartmentName())
                    .propertytype(houseDetailsDTO.getPropertytype())
                    .sqft(houseDetailsDTO.getSqft())
                    .garage(houseDetailsDTO.getGarage())
                    .bedrooms(houseDetailsDTO.getBedrooms())
                    .bathrooms(houseDetailsDTO.getBathrooms())
                    .constructiontype(houseDetailsDTO.getConstructiontype())
                    .yearbuilt(houseDetailsDTO.getYearbuilt())
                    .expectedrent(houseDetailsDTO.getExpectedrent())
                    .deposit(houseDetailsDTO.getDeposit())
                    .availabilityfrom(parseDate(houseDetailsDTO.getAvailabilityfrom()))
                    .availabilitytill(parseDate(houseDetailsDTO.getAvailabilitytill()))
                    .description(houseDetailsDTO.getDescription())
                    .smokingpolicy(houseDetailsDTO.getSmokingpolicy())
                    .vegetarianpreference(houseDetailsDTO.getVegetarianpreference())
                    .petsFriendly(houseDetailsDTO.getPetsFriendly())
                    .addaminities(houseDetailsDTO.getAddaminities())
                    .utilities(houseDetailsDTO.getUtilities())
                    .rentType(houseDetailsDTO.getRentType())
                    .userId(user)
                    .houselatitude(houseDetailsDTO.getHouselatitude())
                    .houselongitude(houseDetailsDTO.getHouselongitude())
                    .housestatus(HouseStatus.UNCOMPLETED)
                    .rentalValidityStatus(RentalValidityStatus.INPROGRESS)
                    .build();
            if (parseDate(houseDetailsDTO.getAvailabilityfrom()).isBefore(Instant.now().truncatedTo(ChronoUnit.DAYS))) {
                return Response.builder().message("Availability from date cannot be in the past.").status(HttpStatus.BAD_REQUEST.value()).success(false).build();
            }
            if (parseDate(houseDetailsDTO.getAvailabilitytill()).isBefore(houseDetailsDTO.getAvailabilityfrom())) {
                return Response.builder().message("Availability till should not be before availability from.").status(HttpStatus.BAD_REQUEST.value()).success(false).build();
            }
            if (parseDate(houseDetailsDTO.getAvailabilitytill()).equals(parseDate(houseDetailsDTO.getAvailabilityfrom()))) {
                return Response.builder().message("Availability from and Availability till dates cannot be the same.").status(HttpStatus.BAD_REQUEST.value()).success(false).build();
            }
            if (houseDetailsDTO.getYearbuilt() > Year.now().getValue()) {
                return Response.builder().message("Year built cannot be in the future.").status(HttpStatus.BAD_REQUEST.value()).success(false).build();
            }
            int currentYear = Year.now().getValue();
            if (houseDetailsDTO.getYearbuilt() < (currentYear - 150)) {
                return Response.builder().message("The year built cannot be more than 150 years ago. Please enter a valid year within the acceptable range").status(HttpStatus.BAD_REQUEST.value()).success(false).build();
            }
            List<Image> imageReqbody = houseDetailsDTO.getAddimages();

            if (imageReqbody == null || imageReqbody.isEmpty()) {
                return Response.builder()
                        .message(ToastMessage.ONE_IMAGE_REQUIRED).status(HttpStatus.BAD_REQUEST.value()).success(false).build();
            }

            if (imageReqbody.size() > 8) {
                return Response.builder().message(ToastMessage.TOO_MANY_IMAGES).status(HttpStatus.BAD_REQUEST.value()).success(false).build();
            }

            if (imageReqbody != null && !imageReqbody.isEmpty()) {
                List<Image> imageDetails = imageReqbody.stream()
                        .map(detail -> Image.builder()
                                .imageUrl(detail.getImageUrl())
                                .imageSize(detail.getImageSize())
                                .imageName(detail.getImageName())
                                .houseDetails(houseDetails)
                                .build())
                        .toList();
                houseDetails.setAddimages(imageDetails);
            }

            housedetailsRepository.save(houseDetails);
            String createdHouse = housedetailsRepository.findHouseDetailsById(houseDetails.getId());
            return Response.builder().message("Rental details Added Successfully.").success(true).status(HttpStatus.CREATED.value()).data(new JSONObject(createdHouse).toMap()).build();


        } catch (Exception e) {
            e.printStackTrace();
            logger.error(ToastMessage.BAD_REQUEST + e.getMessage(), e);
            return Response.builder().message(e.getMessage()).status(HttpStatus.BAD_REQUEST.value()).success(false).build();
        }
    }

    /**
     * Retrieves the details of a house by its ID.
     *
     * @param id The ID of the house to retrieve details for.
     * @return The response object containing the status, success flag, message, and data (house details).
     */
    @Override
    public Response getHouseDetailById(int id) {
        try {
            Optional<HouseDetails> houseDetailsOptional = housedetailsRepository.findById(id);
            if (houseDetailsOptional.isPresent()) {
                String houseDetails = housedetailsRepository.findHouseDetailsById(id);
                return Response.builder().message("Rental Detail Retrieved Successfully.").success(true).status(HttpStatus.OK.value()).data(new JSONObject(houseDetails).toMap()).build();
            }else{
                return Response.builder().message("Rental details not found .").success(false).status(HttpStatus.NOT_FOUND.value()).build();

            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error fetching house details - " + e.getMessage(), e);
            return Response.builder().message(e.getMessage()).status(HttpStatus.BAD_REQUEST.value()).success(false).build();
        }
    }


    /**
     * Retrieves house details for a specific user.
     *
     * @param request The HTTP request object containing the user ID.
     * @return The response object containing the retrieved house details.
     */
    @Override
    public Response getHouseDetailsByUserId(HttpServletRequest request, PaginationDto pagination) {
        try {
            Pageable pageable = PageRequest.of(pagination.getPage()-1, pagination.getLimit());
            Integer userId = (Integer) request.getAttribute(Declarations.USER_ID);
            Optional<User> user = userRepository.findById(userId);
            if(!user.isPresent()){
                throw  new UserNotFoundException(ToastMessage.USER_NOT_FOUND);
            }
            Page<String> houseDetailsList = housedetailsRepository.findHouseDetailsByUserId(userId, pageable);

            List<Map<String, Object>> gethousebyid = new ArrayList<>();
            houseDetailsList.forEach(list -> {
                Map<String, Object> houseMap = new JSONObject(list).toMap();
                Integer houseId = (Integer) houseMap.get("id");
                long count = housedetailsRepository.applicantCount(houseId);
                houseMap.put("applicantCount", count);
                gethousebyid.add(houseMap);
            });
            long totalCount = housedetailsRepository.countHouseDetailsByUserId(userId);
            return Response.builder().message("Rental Details Retrieved Successfully.").success(true).status(HttpStatus.OK.value()).data(gethousebyid).count(totalCount).build();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(ToastMessage.BAD_REQUEST + e.getMessage(), e);
            return Response.builder().message(e.getMessage()).status(HttpStatus.BAD_REQUEST.value()).success(false).build();
        }
    }


    /**
     * Retrieves all active house details created by other users.
     *
     * @param request The HTTP request object containing the user ID.
     * @return A response object containing the list of active house details created by other users.
     */
    @Override
    public Response getAllActiveHouseDetailsByOtherUser(HttpServletRequest request, PaginationDto pagination) {
        try {
            Pageable pageable = PageRequest.of(pagination.getPage()-1, pagination.getLimit());
            Integer userId = (Integer) request.getAttribute(Declarations.USER_ID);
            Optional<User> user = userRepository.findById(userId);
            if(!user.isPresent()){
                throw  new UserNotFoundException(ToastMessage.USER_NOT_FOUND);
            }
            Page<String> houseDetailsList = housedetailsRepository.findAllActiveHouseDetailsByOtherUser(userId,pageable);
            List<Map<String, Object>> gethouse = new ArrayList<>();
            houseDetailsList.forEach(list -> gethouse.add(new JSONObject(list).toMap()));
            long totalCount = housedetailsRepository.countAllActiveHouseDetailsByOtherUser(userId);
            return Response.builder().message("List of rental details created by other users").success(true).status(HttpStatus.OK.value()).data(gethouse).count(totalCount).build();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(ToastMessage.BAD_REQUEST+ e.getMessage(), e);
            return Response.builder().message(e.getMessage()).status(HttpStatus.BAD_REQUEST.value()).success(false).build();
        }
    }


    /**
     * Soft deletes a house detail by setting the 'deleted' flag to true in the database.
     *
     * @param id The ID of the house detail to be soft deleted.
     * @return A response indicating the status of the soft deletion operation and the soft deleted house detail data, if applicable.
     */
    @Override
    public Response softDeleteHouseDetailsById(int id) {
        try {
            Optional<HouseDetails> houseDetailsOptional = housedetailsRepository.findById(id);
            if (houseDetailsOptional.isPresent()) {
                HouseDetails houseDetails = houseDetailsOptional.get();
                if (houseDetails.getHousestatus() == HouseStatus.COMPLETED) {
                    return Response.builder()
                            .message("Cannot delete rental details as it is already COMPLETED.")
                            .success(false).status(HttpStatus.BAD_REQUEST.value()).data(null).build();
                }
                List<String> applicants = applicantRepository.applicantsByrentalId(id);
                if (!applicants.isEmpty()) {
                    return Response.builder().message("Cannot delete rental property as there are associated applicants.")
                            .status(HttpStatus.BAD_REQUEST.value()).success(false).build();
                }
                if (houseDetails.isDeleted()) {
                    return Response.builder().message("Rental detail was already deleted.").success(false).status(HttpStatus.BAD_REQUEST.value()).data(null).build();
                }
                houseDetails.setDeleted(true);
                housedetailsRepository.save(houseDetails);
                String softdeletehouse = housedetailsRepository.findHouseDetailsById(houseDetails.getId());
                return Response.builder().message("Rental details deleted successfully.").success(true).status(HttpStatus.OK.value()).data(new JSONObject(softdeletehouse).toMap()).build();
            } else {
                return Response.builder().message("rental details not found").success(false).status(HttpStatus.NOT_FOUND.value()).data(null).build();
            }
        }catch (Exception e) {
            e.printStackTrace();
            logger.error(ToastMessage.BAD_REQUEST+ e.getMessage(), e);
            return Response.builder().message( e.getMessage()).status(HttpStatus.BAD_REQUEST.value()).success(false).build();
        }
    }

    /**
     * This method is used to edit the details of a house in the system.
     *
     * @param id The ID of the house to be edited.
     * @param updatedto An object containing the updated details of the house.
     * @return A Response object containing the status, success, message, and data of the operation.
     */
    @Override
    public Response editHouseDetails(HttpServletRequest request, int id, EditHousedetailsDTO updatedto) {
        try {

            Integer userId = (Integer) request.getAttribute(Declarations.USER_ID);
            User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(ToastMessage.USER_NOT_FOUND));
            Optional<HouseDetails> existingHouseDetails = housedetailsRepository.findById(id);
            if (!existingHouseDetails.isPresent()) {
                return Response.builder().message(ToastMessage.RENTAL_NOT_FOUND).success(false).status(HttpStatus.NOT_FOUND.value()).data(null).build();
            }
            HouseDetails currentHouseDetails = existingHouseDetails.get();
            if (!currentHouseDetails.getUserId().getId().equals(userId)) {
                return Response.builder()
                        .message("User does not have permission to edit this rental property.").status(HttpStatus.FORBIDDEN.value()).success(false).build();
            }
            List<String> applicants = applicantRepository.applicantsByrentalId(id);
            if (!applicants.isEmpty()) {
                return Response.builder().message("Cannot edit rental details as there are associated applicants.")
                        .status(HttpStatus.BAD_REQUEST.value()).success(false).build();
            }
            if(existingHouseDetails.isEmpty()){
                return Response.builder().message("rental detail not found").status(HttpStatus.NOT_FOUND.value()).success(false).build();
            }
            if (currentHouseDetails.getHousestatus() == HouseStatus.COMPLETED) {
                return Response.builder()
                        .message("Cannot edit rental property as its status is COMPLETED.").status(HttpStatus.BAD_REQUEST.value()).success(false).build();
            }
            if(updatedto.getTitle()!=null) {
                currentHouseDetails.setTitle(updatedto.getTitle());
            }
            if(updatedto.getAddress()!=null) {
                currentHouseDetails.setAddress(updatedto.getAddress());
            }
            if(updatedto.getStreetAddress()!=null) {
                currentHouseDetails.setStreetAddress(updatedto.getStreetAddress());
            }
            if(updatedto.getApartmentName()!=null) {
                currentHouseDetails.setApartmentName(updatedto.getApartmentName());
            }
            if(updatedto.getPropertytype()!=null) {
                currentHouseDetails.setPropertytype(updatedto.getPropertytype());
            }
            if (updatedto.getSqft() != null) {
                currentHouseDetails.setSqft(updatedto.getSqft());
            }
            if(updatedto.getGarage()!=null) {
                currentHouseDetails.setGarage(updatedto.getGarage());
            }
            if(updatedto.getBedrooms()!=null) {
                currentHouseDetails.setBedrooms(updatedto.getBedrooms());
            }
            if(updatedto.getBathrooms()!=null) {
                currentHouseDetails.setBathrooms(updatedto.getBathrooms());
            }
            if(updatedto.getConstructiontype()!=null) {
                currentHouseDetails.setConstructiontype(updatedto.getConstructiontype());
            }
            if(updatedto.getYearbuilt()!=null) {
                currentHouseDetails.setYearbuilt(updatedto.getYearbuilt());
            }
            if(updatedto.getExpectedrent()!=null) {
                currentHouseDetails.setExpectedrent(updatedto.getExpectedrent());
            }
            if(updatedto.getDeposit()!=null) {
                currentHouseDetails.setDeposit(updatedto.getDeposit());
            }
            if(updatedto.getAvailabilityfrom()!=null) {
                currentHouseDetails.setAvailabilityfrom(parseDate(updatedto.getAvailabilityfrom()));
            }
            if(updatedto.getAvailabilitytill()!=null) {
                currentHouseDetails.setAvailabilitytill(parseDate(updatedto.getAvailabilitytill()));
            }
            if(updatedto.getDescription()!=null) {
                currentHouseDetails.setDescription(updatedto.getDescription());
            }
            if(updatedto.getSmokingpolicy()!=null) {
                currentHouseDetails.setSmokingpolicy(updatedto.getSmokingpolicy());
            }
            if(updatedto.getVegetarianpreference()!=null) {
                currentHouseDetails.setVegetarianpreference(updatedto.getVegetarianpreference());
            }
            if(updatedto.getPetsFriendly()!=null) {
                currentHouseDetails.setPetsFriendly(updatedto.getPetsFriendly());
            }
            if(currentHouseDetails.getHouselatitude() != updatedto.getHouselatitude()) {
                currentHouseDetails.setHouselatitude(updatedto.getHouselatitude());
            }
            if(currentHouseDetails.getHouselongitude()!= updatedto.getHouselongitude()) {
                currentHouseDetails.setHouselongitude(updatedto.getHouselongitude());
            }
            if(updatedto.getUtilities()!=null) {
                currentHouseDetails.setUtilities(updatedto.getUtilities());
            }
            if(updatedto.getAddaminities()!=null) {
                currentHouseDetails.setAddaminities(updatedto.getAddaminities());
            }
            if(updatedto.getRentType()!=null) {
                currentHouseDetails.setRentType(updatedto.getRentType());
            }

            List<Image> existingImages = currentHouseDetails.getAddimages();
            Map<Integer, Image> existingImageMap = existingImages.stream()
                    .collect(Collectors.toMap(Image::getId, image -> image));

            if (updatedto.getAddimages().size() > 8) {
                return Response.builder().message(ToastMessage.TOO_MANY_IMAGES).status(HttpStatus.BAD_REQUEST.value()).success(false).build();
            }

            List<Integer> imageIdsInRequestBody = updatedto.getAddimages().stream()
                    .map(Image::getId)
                    .toList();

            List<Integer> imageIdsToDelete = currentHouseDetails.getAddimages().stream()
                    .map(Image::getId)
                    .filter(existingImageId -> !imageIdsInRequestBody.contains(existingImageId))
                    .toList();
            logger.info("Deleting image IDs: {}", imageIdsToDelete);
            imageRepository.deleteByimageId(imageIdsToDelete);

            List<Image> updatedImages = new ArrayList<>();

            if (updatedto.getAddimages() == null || updatedto.getAddimages().isEmpty()) {
                return Response.builder()
                        .message(ToastMessage.ONE_IMAGE_REQUIRED).status(HttpStatus.BAD_REQUEST.value()).success(false).build();
            }
            for (Image reqImage : updatedto.getAddimages()) {
                if (reqImage.getId() != null && existingImageMap.containsKey(reqImage.getId())) {
                    Image existingImage = existingImageMap.get(reqImage.getId());

                    logger.info("imageDto========>{}", reqImage.getId());

                    if (reqImage.getImageUrl() != null) {
                        existingImage.setImageUrl(reqImage.getImageUrl());
                    }
                    if (reqImage.getImageSize() != 0) {
                        existingImage.setImageSize(reqImage.getImageSize());
                    }
                    if (reqImage.getImageName() != null){
                        existingImage.setImageName(reqImage.getImageName());
                    }

                    updatedImages.add(existingImage);
                }
                if (reqImage.getId() == null) {
                    Image newImage = new Image();
                    if (reqImage.getImageUrl() != null) {
                        newImage.setImageUrl(reqImage.getImageUrl());
                    }
                    else{
                        return Response.builder().message("New image url cannot be null").status(HttpStatus.BAD_REQUEST.value()).success(false).build();
                    }
                    newImage.setImageSize(reqImage.getImageSize());
                    if (reqImage.getImageName() != null) {
                        newImage.setImageName(reqImage.getImageName());
                    }
                    else{
                        return Response.builder().message("New image name cannot be null").status(HttpStatus.BAD_REQUEST.value()).success(false).build();
                    }
                    newImage.setHouseDetails(currentHouseDetails);
                    updatedImages.add(newImage);
                }
            }

            currentHouseDetails.setAddimages(updatedImages);
            housedetailsRepository.save(currentHouseDetails);
            String updatedhouse = housedetailsRepository.findHouseDetailsById(currentHouseDetails.getId());

            if (updatedImages.isEmpty()) {
                return Response.builder().message(ToastMessage.ONE_IMAGE_REQUIRED).status(HttpStatus.BAD_REQUEST.value()).success(false).build();
            }
            if (updatedto.getAvailabilityfrom() != null && updatedto.getAvailabilitytill() != null) {
                Instant now = Instant.now();
                Instant availabilityFrom = updatedto.getAvailabilityfrom().truncatedTo(ChronoUnit.DAYS);
                Instant availabilityTill = updatedto.getAvailabilitytill();

                if (parseDate(availabilityFrom).isBefore(now.truncatedTo(ChronoUnit.DAYS))) {
                    return Response.builder().message("Availability from date cannot be in the past").status(HttpStatus.BAD_REQUEST.value()).success(false).build();
                }
                if (parseDate(availabilityTill).isBefore(parseDate(availabilityFrom))) {
                    return Response.builder().message("Availability Till date cannot be before Availability From date").status(HttpStatus.BAD_REQUEST.value()).success(false).build();
                }
                if (parseDate(availabilityTill).equals(parseDate(availabilityFrom))) {
                    return Response.builder().message("Availability from and Availability till dates cannot be the same.").status(HttpStatus.BAD_REQUEST.value()).success(false).build();
                }
                if (updatedto.getYearbuilt() > Year.now().getValue()) {
                    return Response.builder().message("Year built cannot be in the future.").status(HttpStatus.BAD_REQUEST.value()).success(false).build();
                }

                currentHouseDetails.setAvailabilityfrom(parseDate(availabilityFrom));
                currentHouseDetails.setAvailabilitytill(parseDate(availabilityTill));
            }
            return Response.builder()
                    .message("Rental Details updated successfully")
                    .success(true).status(HttpStatus.OK.value()).data(new JSONObject(updatedhouse).toMap()).build();

        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error(ToastMessage.BAD_REQUEST+ e.getMessage(), e);
            return Response.builder().message(e.getMessage()).status(HttpStatus.BAD_REQUEST.value()).success(false).build();
        }}



    /**
     * Retrieves the completed house details for a specific user.
     *
     * @param request The HTTP request object containing the user ID.
     * @return A response object containing the list of completed house details for the user.
     */
    @Override
    public Response getCompletedHouseDetailsByUserId(HttpServletRequest request, PaginationDto pagination ) {
        try {
            Pageable pageable = PageRequest.of(pagination.getPage()-1, pagination.getLimit());
            Integer userId = (Integer) request.getAttribute(Declarations.USER_ID);
           Optional<User> user = userRepository.findById(userId);
           if(!user.isPresent()){
               throw new UserNotFoundException(ToastMessage.USER_NOT_FOUND);
           }
            Page<String> houseDetailsList = housedetailsRepository.findCompletedHouseDetailsByUserId(userId, pageable);
            List<Map<String, Object>> getcompletedbyuser = new ArrayList<>();
            houseDetailsList.forEach(list -> getcompletedbyuser.add(new JSONObject(list).toMap()));
            long totalCount = housedetailsRepository.countCompletedHouseDetailsByUserId(userId);
            return Response.builder().message("List of completed rental Details created by the user").success(true).status(HttpStatus.OK.value()).data(getcompletedbyuser).count(totalCount).build();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(ToastMessage.BAD_REQUEST+ e.getMessage(), e);
            return Response.builder().message(e.getMessage()).status(HttpStatus.BAD_REQUEST.value()).success(false).build();
        }
    }

    /**
     * Retrieves all completed house details created by other users.
     *
     * @param request The HTTP request object containing the user ID.
     * @return A response object containing the list of completed house details created by other users.
     */
    @Override
    public Response getAllCompletedHouseDetailsByOtherUser(HttpServletRequest request, PaginationDto pagination) {
        try {
            Pageable pageable = PageRequest.of(pagination.getPage()-1, pagination.getLimit());
            Integer userId = (Integer) request.getAttribute(Declarations.USER_ID);
            Optional<User> user = userRepository.findById(userId);
            if(!user.isPresent()){
                throw  new UserNotFoundException(ToastMessage.USER_NOT_FOUND);
            }
            Page<String> completedHouseDetailsList = housedetailsRepository.findAllCompletedHouseDetailsByOtherUser(userId, pageable);
            List<Map<String, Object>> getcompletedbyothers = new ArrayList<>();
            completedHouseDetailsList.forEach(list -> getcompletedbyothers.add(new JSONObject(list).toMap()));
            return Response.builder().message("List of completed rentals created by other users").success(true).status(HttpStatus.OK.value()).data(getcompletedbyothers).build();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(ToastMessage.BAD_REQUEST+ e.getMessage(), e);
            return Response.builder().message(e.getMessage()).status(HttpStatus.BAD_REQUEST.value()).build();
        }
    }

    /**
     * Adds an image file to a house details object.
     *
     * @param request The HTTP request object containing the user ID.
     * @param file The image file to be uploaded.
     * @return The response object containing the image URL.
     */


    @Override
    public Response addhouseimage(HttpServletRequest request, MultipartFile file) {
        try {
            Optional<User> user = userRepository.findById((Integer) request.getAttribute(Declarations.USER_ID));
            if(!user.isPresent()){
                throw  new UserNotFoundException(ToastMessage.USER_NOT_FOUND);
            }
            long limit = 5 * 1024 * 1024;
            if (file.getSize() <= limit) {
                String contentType = file.getContentType();
                logger.info("ContentType: {}",contentType);
                if(contentType != null) {
                    if (contentType.equals("image/jpg") || contentType.equals("image/jpeg") || contentType.equals("image/png")) {
                        String bucketName = awsBucketName + "/housephoto";
                        String key = UUID.randomUUID().toString();
                        ObjectMetadata metadata = new ObjectMetadata();
                        metadata.setContentType(file.getContentType());
                        metadata.setContentLength(file.getSize());
                        amazonS3.putObject(bucketName, key, file.getInputStream(), metadata);
                        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
                        String url = amazonS3.getUrl(bucketName, key).toString();
                        data.put("image", url);
                        data.put("fileName", file.getOriginalFilename());
                        return Response.builder().data(data).message("Image uploaded successfully").success(true).status(HttpStatus.OK.value()).build();
                    } else {
                        return Response.builder().message("Invalid File format").status(HttpStatus.BAD_REQUEST.value()).success(false).build();
                    }
                }else{
                    return Response.builder().message("Please Upload a file").status(HttpStatus.BAD_REQUEST.value()).success(false).build();
                }
            } else {
                return Response.builder().message("File size exceeds, upload below 5Mb").success(false).status(HttpStatus.BAD_REQUEST.value()).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(ToastMessage.BAD_REQUEST+ e.getMessage(), e);
            return Response.builder().message(e.getMessage()).status(HttpStatus.BAD_REQUEST.value()).success(false).build();
        }
    }

    /**
     * Apply for a house by creating an applicant object and saving it to the database.
     *
     * @param request The HTTP request object containing user information.
     * @param id The ID of the house the user wants to apply for.
     * @return The response object containing the result of the application process.
     */
    @Override
    public Response applyForHouse(HttpServletRequest request, Integer id) {
        try {
            Integer userId = (Integer) request.getAttribute(Declarations.USER_ID);
            User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(ToastMessage.USER_NOT_FOUND));
            Optional<HouseDetails> houseDetailsOptional = housedetailsRepository.findById(id);
            if (!houseDetailsOptional.isPresent()) {
                return Response.builder().message(ToastMessage.RENTAL_NOT_FOUND).success(false).status(HttpStatus.NOT_FOUND.value()).data(null).build();
            }
            HouseDetails houseDetails = houseDetailsOptional.get();
            if (houseDetails.getUserId().getId().equals(userId)) {
                return Response.builder().message("Cannot apply to your own posted rental property.").success(false).status(HttpStatus.BAD_REQUEST.value()).data(null).build();
            }
            if (houseDetails.isDeleted() || houseDetails.getHousestatus().equals(HouseStatus.COMPLETED)) {
                return Response.builder().message("Property is not available for application.").success(false).status(HttpStatus.BAD_REQUEST.value()).data(null).build();
            }
            Optional<Applicants> existingApplicant = applicantRepository.findByUserIdAndHouseId(user.getId(), id);
            if (existingApplicant.isPresent()) {
                return Response.builder().message("You've already applied for the property")
                        .status(HttpStatus.BAD_REQUEST.value()).success(false).build();
            }
            Applicants applicant = Applicants.builder().user(user)
                    .HouseId(id).owner(houseDetails.getUserId())
                    .status(Status.APPLIED).build();
            applicantRepository.save(applicant);
            String appliedhouse = applicantRepository.findApplicantById(applicant.getId());
            return Response.builder().message("Application submitted successfully.").success(true).status(HttpStatus.OK.value()).data(new JSONObject(appliedhouse).toMap()).build();

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(ToastMessage.BAD_REQUEST+ e.getMessage(), e);
            return Response.builder().message(e.getMessage()).status(HttpStatus.BAD_REQUEST.value()).success(false).build();
        }
    }


    /**
     * Retrieves a list of applicants for a specific house ID.
     *
     * @param request The HTTP request object containing user information.
     * @param houseId The ID of the house for which to retrieve the applicants.
     * @return A response object containing the list of applicants for the given house ID.
     */
    @Override
    public Response  getApplicantsByHouseId(HttpServletRequest request, Integer houseId, PaginationDto pagination) {
        try {
            Optional<User> user = userRepository.findById((Integer) request.getAttribute(Declarations.USER_ID));
            if(!user.isPresent()){
                throw new UserNotFoundException(ToastMessage.USER_NOT_FOUND);
            }
            Pageable pageable = PageRequest.of(pagination.getPage()-1, pagination.getLimit());
            Page<String> applicantsList = applicantRepository.applicantsByHouseId(houseId,pageable);
            List<Map<String, Object>> getapplicantsList = new ArrayList<>();
            applicantsList.forEach(list -> getapplicantsList.add(new JSONObject(list).toMap()));
            long totalCount = applicantRepository.countApplicantsByHouseId(houseId);
            return Response.builder().message("List of Applicants retrived sucessfully").count(totalCount)
                    .success(true).status(HttpStatus.OK.value())
                    .data(getapplicantsList).build();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(ToastMessage.BAD_REQUEST+ e.getMessage(), e);
            return Response.builder().message(e.getMessage()).status(HttpStatus.BAD_REQUEST.value()).success(false).build();
        }
    }

    /**
     * Accepts an application for a house rental and updates the status of the application and the house details accordingly.
     *
     * @param request          The HTTP servlet request object.
     * @param houseId          The ID of the house for which the application is being accepted.
     * @param applicantUserId  The ID of the user who submitted the application.
     * @return                 The response object containing the status, message, and data of the operation.
     */
    @Override
    public Response acceptApplication(HttpServletRequest request, Integer houseId, Integer applicantUserId) {
        try {
            Integer ownerId = (Integer) request.getAttribute(Declarations.USER_ID);
            User owner = userRepository.findById(ownerId).orElseThrow(() -> new UserNotFoundException(ToastMessage.USER_NOT_FOUND));
            Optional<HouseDetails> houseDetailsOptional = housedetailsRepository.findById(houseId);
            if (!houseDetailsOptional.isPresent()) {
                return Response.builder().message("Rental details not found .").success(false).status(HttpStatus.NOT_FOUND.value()).data(null).build();
            }
            HouseDetails houseDetails = houseDetailsOptional.get();
            if (!houseDetails.getUserId().equals(owner)) {
                return Response.builder().message("You are not the owner of this property.").success(false).status(HttpStatus.FORBIDDEN.value()).data(null).build();
            }
            if (houseDetails.isDeleted() || houseDetails.getHousestatus().equals(HouseStatus.COMPLETED)) {
                return Response.builder().message("Property is not available for acceptance.").success(false).status(HttpStatus.BAD_REQUEST.value()).data(null).build();
            }
            Optional<Applicants> applicantOptional = applicantRepository.findByUserIdAndHouseId(applicantUserId, houseId);
            if (!applicantOptional.isPresent()) {
                return Response.builder().message("Applicant not found for the given user and property.").success(false).status(HttpStatus.NOT_FOUND.value()).data(null).build();
            }
            Optional<Applicants> acceptedApplicant = applicantRepository.findByHouseIdAndStatus(houseId, Status.ACCEPTED);
            if (acceptedApplicant.isPresent()) {
                return Response.builder().message("Another applicant has already been accepted for this property.").success(false).status(HttpStatus.BAD_REQUEST.value()).data(null).build();
            }
            Applicants applicant = applicantOptional.get();
            if (!applicant.getStatus().equals(Status.APPLIED)) {
                return Response.builder().message("Applicant have not applied for this property.").success(false).status(HttpStatus.BAD_REQUEST.value()).data(null).build();
            }
            applicant.setStatus(Status.ACCEPTED);
            applicantRepository.save(applicant);
            List<Integer>rejectedIds = applicantRepository.rejectOtherApplicants(houseId, applicantUserId);
            houseDetails.setHousestatus(HouseStatus.COMPLETED);
            housedetailsRepository.save(houseDetails);
            String appliedhouse = applicantRepository.findApplicantById(applicant.getId());
            return Response.builder().message("Application accepted successfully. And rental detail moved to Completed.").success(true).status(HttpStatus.OK.value()).data(new JSONObject(appliedhouse).toMap()).build();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(ToastMessage.BAD_REQUEST+ e.getMessage(), e);
            return Response.builder().message(e.getMessage()).status(HttpStatus.BAD_REQUEST.value()).success(false).build();
        }
    }

    /**
     * Rejects the application for a rental property.
     *
     * @param request         the HttpServletRequest object containing the request information
     * @param houseId         the ID of the rental property
     * @param applicantUserId the ID of the applicant user
     * @return a Response object indicating the status of the operation
     * @throws UserNotFoundException if the user is not found
     */
    @Override
    public Response rejectApplication(HttpServletRequest request, Integer houseId, Integer applicantUserId) {
        try {
            Integer ownerId = (Integer) request.getAttribute(Declarations.USER_ID);
            User owner = userRepository.findById(ownerId).orElseThrow(() -> new UserNotFoundException(ToastMessage.USER_NOT_FOUND));
            Optional<HouseDetails> houseDetailsOptional = housedetailsRepository.findById(houseId);
            if (!houseDetailsOptional.isPresent()) {
                return Response.builder().message(ToastMessage.RENTAL_NOT_FOUND).success(false).status(HttpStatus.NOT_FOUND.value()).data(null).build();
            }
            HouseDetails houseDetails = houseDetailsOptional.get();
            if (!houseDetails.getUserId().equals(owner)) {
                return Response.builder().message("You are not the owner of this property.").success(false).status(HttpStatus.FORBIDDEN.value()).data(null).build();
            }
            Optional<Applicants> applicantOptional = applicantRepository.findByUserIdAndHouseId(applicantUserId, houseId);
            if (!applicantOptional.isPresent()) {
                return Response.builder().message("Applicant not found for the given user and property.").success(false).status(HttpStatus.NOT_FOUND.value()).data(null).build();
            }
            Applicants applicant = applicantOptional.get();
            if (!applicant.getStatus().equals(Status.APPLIED)) {
                return Response.builder().message("Applicant has not applied for this property.").success(false).status(HttpStatus.BAD_REQUEST.value()).data(null).build();
            }
            applicant.setStatus(Status.REJECTED);
            applicantRepository.save(applicant);
            return Response.builder().message("Application rejected successfully.").success(true).status(HttpStatus.OK.value()).data(null).build();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Bad request - " + e.getMessage(), e);
            return Response.builder().message(e.getMessage()).status(HttpStatus.BAD_REQUEST.value()).success(false).build();
        }
    }

    /**
     * Filters house details based on certain criteria such as property type, location, and expected rent.
     * Retrieves the user ID from the request, fetches the user details, and then calls the 'filterhouse' method in the 'housedetailsRepository' to get the filtered results.
     * The filtered results are converted into a list of maps and returned as the response.
     *
     * @param request The HTTP request object containing the user ID.
     * @param data The filter criteria for filtering the house details.
     * @return The response object containing the filtered house details.
     * @throws UserNotFoundException If the user is not found.
     */
    @Override
    public Response myhelpsfilter(HttpServletRequest request, FilterDto data, String pageName) {
        try {
            Pageable pageable = PageRequest.of(data.getPage() - 1, data.getLimit());
            Integer userId = (Integer) request.getAttribute(Declarations.USER_ID);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(ToastMessage.USER_NOT_FOUND));

            if (data.getMinexpectedrent() != null && data.getMaxexpectedrent() != null
                    && data.getMinexpectedrent() > data.getMaxexpectedrent()) {
                throw new IllegalArgumentException("Minimum expected rent cannot be greater than maximum expected rent");
            }

            Page<String> filteredResults = null;
            long count;

            switch (pageName) {
                case "OfferReceived":
                    filteredResults = housedetailsRepository.myhelpsfilterhouse(
                            user.getId(),
                            data.getLocation(),
                            data.getPropertytype(),
                            data.getConstructiontype(),
                            data.getMinexpectedrent(),
                            data.getMaxexpectedrent(), pageable);
                    count = housedetailsRepository.countmyhelpsfilterhouse(
                            user.getId(),
                            data.getLocation(),
                            data.getPropertytype(),
                            data.getConstructiontype(),
                            data.getMinexpectedrent(),
                            data.getMaxexpectedrent());

                    List<Map<String, Object>> filteredDataWithApplicantCount = new ArrayList<>();
                    filteredResults.forEach(result -> {
                        Map<String, Object> resultMap = new JSONObject(result).toMap();
                        Integer houseId = (Integer) resultMap.get("id");
                        long applicantCount = housedetailsRepository.applicantCount(houseId);
                        resultMap.put("applicantCount", applicantCount);
                        filteredDataWithApplicantCount.add(resultMap);
                    });

                    return Response.builder().data(filteredDataWithApplicantCount).count(count)
                            .message("Data filtered successfully").status(HttpStatus.OK.value()).success(true).build();
                case "OfferMade":
                    filteredResults = applicantRepository.findAppliedFilterData(
                            user.getId(),
                            data.getLocation(),
                            data.getPropertytype(),
                            data.getConstructiontype(),
                            data.getMinexpectedrent(),
                            data.getMaxexpectedrent(), pageable);
                    count = applicantRepository.countAppliedFilterData(
                            user.getId(),
                            data.getLocation(),
                            data.getPropertytype(),
                            data.getConstructiontype(),
                            data.getMinexpectedrent(),
                            data.getMaxexpectedrent());
                    break;
                case "Completed":
                    filteredResults = housedetailsRepository.findCompletedFilterData(
                            user.getId(),
                            data.getLocation(),
                            data.getPropertytype(),
                            data.getConstructiontype(),
                            data.getMinexpectedrent(),
                            data.getMaxexpectedrent(), pageable);
                    count = housedetailsRepository.countCompletedFilterData(
                            user.getId(),
                            data.getLocation(),
                            data.getPropertytype(),
                            data.getConstructiontype(),
                            data.getMinexpectedrent(),
                            data.getMaxexpectedrent());
                    break;
                default:
                    throw new IllegalArgumentException("Invalid PageName ");
            }

            List<Map<String, Object>> filteredData = new ArrayList<>();
            filteredResults.forEach(result -> filteredData.add(new JSONObject(result).toMap()));
            return Response.builder().data(filteredData).count(count).message("Data filtered successfully")
                    .status(HttpStatus.OK.value()).success(true).build();
        } catch (Exception e) {
            logger.error(ToastMessage.BAD_REQUEST + e.getMessage(), e);
            e.printStackTrace();
            return Response.builder().message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST.value()).success(false).build();
        }
    }


    /**
     * Filters the house details based on the provided filter criteria.
     *
     * @param request the HttpServletRequest object
     * @param data the FilterDto object containing the filter criteria
     * @return the Response object containing the filtered house details
     * @throws UserNotFoundException if the user is not found
     * @throws IllegalArgumentException if the minimum expected rent is greater than the maximum expected rent
     */
    @Override
    public Response myexploresfilter(HttpServletRequest request,FilterDto data) {
        try {
            Pageable pageable = PageRequest.of(data.getPage()-1, data.getLimit());
            Integer userId = (Integer) request.getAttribute(Declarations.USER_ID);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(ToastMessage.USER_NOT_FOUND));

            if (data.getMinexpectedrent() != null && data.getMaxexpectedrent() != null
                    && data.getMinexpectedrent() > data.getMaxexpectedrent()) {
                throw new IllegalArgumentException("Minimum expected rent cannot be greater than maximum expected rent");
            }
            Page<String> filteredResults = housedetailsRepository.exploresfilterhouse(
                    user.getId(),
                    data.getLocation(),
                    data.getPropertytype(),
                    data.getConstructiontype(),
                    data.getMinexpectedrent(),
                    data.getMaxexpectedrent(), pageable);
            logger.info("explore filter data:{}", filteredResults);

            List<Map<String, Object>> filteredData = new ArrayList<>();
            filteredResults.forEach(result -> filteredData.add(new JSONObject(result).toMap()));
            long count = housedetailsRepository.countExploreFilter(
                    user.getId(),
                    data.getLocation(),
                    data.getPropertytype(),
                    data.getConstructiontype(),
                    data.getMinexpectedrent(),
                    data.getMaxexpectedrent());
            return Response.builder().data(filteredData).count(count).message("Data filtered successfully")
                    .status(HttpStatus.OK.value()).success(true).build();
        } catch (Exception e) {
            logger.error(ToastMessage.BAD_REQUEST+ e.getMessage(), e);
            e.printStackTrace();
            return Response.builder().message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST.value()).success(false).build();
        }
    }
    /**
     * Retrieves the applied rental details for a specific user.
     *
     * @param request     the HttpServletRequest object containing the user_id attribute
     * @param pagination  the PaginationDto object containing the page number and limit for pagination
     * @return            a Response object containing the applied rental details, total count, and success status
     * @throws Exception  if there is an error retrieving the applied rental details
     */
    @Override
    public Response getappliedHousesByUserId(HttpServletRequest request, PaginationDto pagination) {
        try {
            Pageable pageable = PageRequest.of(pagination.getPage()-1, pagination.getLimit());
            Integer userId = (Integer) request.getAttribute(Declarations.USER_ID);
            Page<String> houses = applicantRepository.findAppliedHouseByUserId(userId, pageable);
            List<Map<String, Object>> getappliedList = new ArrayList<>();
            houses.forEach(list -> getappliedList.add(new JSONObject(list).toMap()));
            long totalCount = applicantRepository.countAppliedHouseByUserId(userId);
            return Response.builder().data(getappliedList).message("Applied rental details retrived sucessfully").count(totalCount)
                    .status(HttpStatus.OK.value()).success(true).build();
        } catch (Exception e) {
            logger.error(ToastMessage.BAD_REQUEST+ e.getMessage(), e);
            e.printStackTrace();
            return Response.builder().message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST.value()).success(false).build();
        }
    }

    /**
     * Deletes the application for a house by the user.
     *
     * @param request  the HttpServletRequest object containing the user's request
     * @param houseId  the ID of the house for which the application is to be deleted
     * @return         a Response object indicating the status of the operation
     * @throws UserNotFoundException  if the user is not found in the database
     */
    @Override
    public Response deleteHouseApplication(HttpServletRequest request, Integer houseId) {
        try {
            Integer userId = (Integer) request.getAttribute(Declarations.USER_ID);
            Optional<User> user = userRepository.findById(userId);
            if(!user.isPresent()){
                throw  new UserNotFoundException(ToastMessage.USER_NOT_FOUND);
            }
            Optional<Applicants> existingApplicant = applicantRepository.findByUserIdAndHouseId(userId, houseId);

            if (!existingApplicant.isPresent()) {
                return Response.builder().message("You haven't applied for the property")
                        .status(HttpStatus.BAD_REQUEST.value()).success(false).build();
            }

            if (existingApplicant.get().getStatus() == Status.ACCEPTED) {
                return Response.builder().message("You have already been accepted for the property.You cannot delete your application.")
                        .status(HttpStatus.BAD_REQUEST.value()).success(false).build();
            }

            applicantRepository.delete(existingApplicant.get());

            return Response.builder().message("Application deleted successfully.").success(true)
                    .status(HttpStatus.OK.value()).data(null).build();

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Bad request - " + e.getMessage(), e);
            return Response.builder().message(e.getMessage()).status(HttpStatus.BAD_REQUEST.value()).success(false).build();
        }
    }


    /**
     * Converts an Instant object to the start of the corresponding day in UTC time zone.
     *
     * @param date The input date to be parsed.
     * @return The parsed Instant object representing the start of the day in UTC time zone.
     */
    public Instant parseDate(Instant date){
        ZoneId zoneId = ZoneId.of("UTC");
        LocalDate localDate = date.atZone(zoneId).toLocalDate();
        return localDate.atStartOfDay(zoneId).toInstant();
    }

    /**
     * Updates the status of houses based on their availability.
     * This method is scheduled to run every 10 seconds.
     * It retrieves all the house details from the database and checks if their availability till date has passed.
     * If the availability till date has passed and the house status is not already set to COMPLETED,
     * the house status is updated to COMPLETED and the rental validity status is set to EXPIRED.
     * Additionally, a notification is sent to the user who owns the house, informing them that their rental property has expired.
     */
    @Scheduled(cron = "*/10 * * * * *")
    public void updateHouseStatusBasedOnAvailability() {
        List<HouseDetails> houseDetailsList = housedetailsRepository.findAll();
        Instant currentDate = Instant.now().truncatedTo(ChronoUnit.MINUTES);
        for (HouseDetails houseDetails : houseDetailsList) {
            Integer userId = houseDetails.getUserId().getId();
            if (houseDetails.getAvailabilitytill() != null &&
                    houseDetails.getAvailabilitytill().isBefore(currentDate) &&
                    houseDetails.getHousestatus() != HouseStatus.COMPLETED) {
                logger.info("Updating house with ID: {} to COMPLETED status as it is expired.", houseDetails.getId());
                houseDetails.setHousestatus(HouseStatus.COMPLETED);
                houseDetails.setRentalValidityStatus(RentalValidityStatus.EXPIRED);
                housedetailsRepository.save(houseDetails);
            }
        }
    }


}
