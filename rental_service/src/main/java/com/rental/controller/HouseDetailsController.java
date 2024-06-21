package com.rental.controller;


import com.rental.dtos.FilterDto;
import com.rental.dtos.PaginationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.rental.dtos.EditHousedetailsDTO;
import com.rental.dtos.HouseDetailsDTO;
import com.rental.payload.Response;
import com.rental.service.HouseDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")

public class HouseDetailsController {

    private final HouseDetailsService housedetailsService;

    /**
     * Adds house details by sending a POST request to the "/api/v1/user/rental/addhouse" endpoint.
     * 
     * @param request the HttpServletRequest object containing information about the HTTP request
     * @param houseDetailsDTO the DTO object containing the details of the house to be added
     * @return a ResponseEntity object containing a Response object
     */
    @PostMapping("/rental/addhouse")
    public ResponseEntity<Response> addHouseDetails(HttpServletRequest request, @Valid @RequestBody HouseDetailsDTO houseDetailsDTO) {
        Response response = housedetailsService.addHouseDetailsResponse(request, houseDetailsDTO);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }
  /**
     * Retrieves the details of a house by its ID.
     * 
     * @param id The ID of the house to retrieve details for.
     * @return ResponseEntity<Response> The response entity containing the details of the house and the HTTP status code.
     */
    @GetMapping("/rental/viewhouse/{id}")
    public ResponseEntity<Response> getHouseDetailById(@PathVariable int id) {
        Response response = housedetailsService.getHouseDetailById(id);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * Retrieves the house details for a specific user.
     *
     * @param request The HTTP request object containing the user information.
     * @return The response entity containing the house details for the user.
     */
    @PostMapping("/rental/myhelps/listhouses")
    public ResponseEntity<Response> getHouseDetailsByUserId(HttpServletRequest request,@Valid@RequestBody PaginationDto pagination) {
        Response response = housedetailsService.getHouseDetailsByUserId(request,pagination);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }

    /**
     * Retrieves a list of active house details created by other users.
     *
     * @param request the HTTP request object
     * @return a ResponseEntity object containing the response body and status code
     */
    @PostMapping("/rental/explores/requestedbyyou")
    public ResponseEntity<Response> getHousesCreatedByOtherUsers(HttpServletRequest request, @Valid@RequestBody PaginationDto pagination) {
        Response response = housedetailsService.getAllActiveHouseDetailsByOtherUser(request,pagination);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }

    /**
     * Soft deletes a house details record by its ID.
     *
     * @param id The ID of the house details record to be soft deleted.
     * @return The response entity containing the status and response body of the soft delete operation.
     */
    @DeleteMapping("/rental/softdeletehouse/{id}")
    public ResponseEntity<Response> softDeleteHouseDetails(@PathVariable int id) {
        Response response = housedetailsService.softDeleteHouseDetailsById(id);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }

  /**
         * Edits the details of a house.
         *
         * @param id         The ID of the house to be edited.
         * @param updatedto  An object containing the updated details of the house.
         * @return           A response entity containing the updated house details.
         */
    @PatchMapping("/rental/edithouse/{id}")
    public ResponseEntity<Response> editHouseDetails(HttpServletRequest request,@PathVariable int id, @Valid@RequestBody EditHousedetailsDTO updatedto) {
        Response response = housedetailsService.editHouseDetails(request,id, updatedto);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }


  /**
         * Retrieves the completed house details for a specific user.
         *
         * @param request The HTTP request object containing the user information.
         * @return The response entity containing the completed house details for the user.
         */
    @PostMapping("/rental/myhelps/completed")
    public ResponseEntity<Response> getCompletedHouseDetailsByUserId(HttpServletRequest request, @Valid@RequestBody PaginationDto pagination) {
        Response response = housedetailsService.getCompletedHouseDetailsByUserId(request, pagination);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }
 /**
     * Retrieves all completed house details created by other users.
     *
     * @param request The HTTP request object.
     * @return The response entity containing the response and the HTTP status code.
     */
    @PostMapping("/rental/explores/completed")
    public ResponseEntity<Response> getAllCompletedHouseDetailsByOtherUser(HttpServletRequest request, @Valid@RequestBody PaginationDto pagination) {
        Response response=housedetailsService.getAllCompletedHouseDetailsByOtherUser(request,pagination);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }
 /**
         * Uploads house images.
         * 
         * @param request The HTTP request object containing the image data.
         * @param file The file object representing the image to be uploaded.
         * @return The HTTP response containing the status and response body.
         */
    @PostMapping("/rental/uploadhouseimages")
    public ResponseEntity<Response> uploadHouseImages(HttpServletRequest request, @RequestParam("houseimage") MultipartFile file){
        Response response = housedetailsService.addhouseimage(request, file);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }

    /**
     * This method is used to apply for a house by sending a POST request to the "/rental/applyforhouse/{id}" endpoint.
     * It calls the applyForHouse method of the housedetailsService object, passing the request and id as parameters.
     * It then creates a ResponseEntity object with the response received from the service method and the corresponding HTTP status code.
     *
     * @param request The HTTP request object containing the details of the request.
     * @param id The ID of the house to apply for.
     * @return The response entity object containing the response received from the service method and the corresponding HTTP status code.
     */
    @GetMapping("/rental/applyforhouse/{id}")
    public ResponseEntity<Response> applyForHouse(HttpServletRequest request, @PathVariable int id) {
        Response response = housedetailsService.applyForHouse(request, id);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }
   /**
     * Retrieves a list of applicants for a specific house by its ID.
     * 
     * @param request The HTTP request object.
     * @param id The ID of the house for which to retrieve the applicants.
     * @return The response entity containing the list of applicants for the specified house.
     */
    @PostMapping("/rental/listapplicants/{id}")
    public ResponseEntity<Response> applicantslist(HttpServletRequest request, @PathVariable int id, @Valid@RequestBody PaginationDto pagination) {
        Response response = housedetailsService.getApplicantsByHouseId(request, id, pagination);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }

    /**
     * Accepts an application for a house rental and returns a response entity with the status and response body.
     *
     * @param request The HTTP servlet request object.
     * @param id The ID of the house for which the application is being accepted.
     * @param applicantUserId The ID of the user who is applying for the house.
     * @return The response entity containing the status and response body.
     */
    @PostMapping("/rental/acceptapplicant")
    public ResponseEntity<Response> acceptApplication(HttpServletRequest request,
                                                      @RequestParam("houseId") Integer id, @RequestParam("applicantUserId") Integer applicantUserId) {
        Response response = housedetailsService.acceptApplication(request, id, applicantUserId);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }

    /**
     * Rejects an application for a house.
     *
     * @param request           the HTTP servlet request
     * @param id                the ID of the house
     * @param applicantUserId   the ID of the applicant user
     * @return                  the response entity containing the response
     */
    @PostMapping("/rental/rejectapplicant")
    public ResponseEntity<Response> rejectApplication(HttpServletRequest request,
                                                      @RequestParam("houseId") Integer id, @RequestParam("applicantUserId") Integer applicantUserId) {
        Response response = housedetailsService.rejectApplication(request, id, applicantUserId);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }

    /**
     * Handles a POST request to filter rental properties based on the provided filter criteria.
     * 
     * @param request The HTTP request object containing the filter criteria.
     * @param data The data transfer object containing the filter criteria.
     * @return A ResponseEntity object containing the filtered rental properties.
     */
    @PostMapping("/rental/myhelps/filter")
    public ResponseEntity<Response> myhelpsrentalFiler(HttpServletRequest request, @Valid @RequestBody FilterDto data, @RequestParam("page") String pageName){
        Response response = housedetailsService.myhelpsfilter(request, data, pageName);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }

    /**
     * This method is used to filter rental properties for exploration.
     * It takes in a HttpServletRequest object and a FilterDto object as parameters.
     * The FilterDto object contains the filter criteria for the rental properties.
     * The method calls the 'myexploresfilter' method of the 'housedetailsService' object to filter the rental properties.
     * It returns a ResponseEntity object with the filtered rental properties as the response body.
     * The HTTP status of the response is determined by the status of the 'response' object.
     * 
     * @param request The HttpServletRequest object containing the request information.
     * @param data The FilterDto object containing the filter criteria for the rental properties.
     * @return A ResponseEntity object with the filtered rental properties as the response body.
     */
    @PostMapping("/rental/explores/filter")
    public ResponseEntity<Response> exploresrentalFiler(HttpServletRequest request, @Valid @RequestBody FilterDto data){
        Response response = housedetailsService.myexploresfilter(request, data);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }

    /**
     * Retrieves the houses applied by a user.
     *
     * This method takes in the user's request and pagination details as parameters and returns a ResponseEntity object
     * containing the response and the HTTP status code. It calls the 'getappliedHousesByUserId' method of the 'housedetailsService'
     * to retrieve the houses applied by the user. The response is then wrapped in a ResponseEntity object and returned.
     *
     * @param request     the user's request
     * @param pagination  the pagination details
     * @return            a ResponseEntity object containing the response and the HTTP status code
     */
    @PostMapping("rental/myhelps/applied")
    public ResponseEntity<Response> getHousesByUserId(HttpServletRequest request, @Valid@RequestBody PaginationDto pagination) {
        Response response= housedetailsService.getappliedHousesByUserId(request, pagination);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }

    /**
     * Deletes the application for a house with the given houseId.
     *
     * @param request    the HttpServletRequest object
     * @param houseId    the id of the house
     * @return           the ResponseEntity containing the response status and body
     */
    @DeleteMapping("/rental/myhelps/deleteapplication/{houseId}")
    public ResponseEntity<Response> deleteApplication(HttpServletRequest request, @PathVariable Integer houseId) {
        Response response = housedetailsService.deleteHouseApplication(request, houseId);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }

}
