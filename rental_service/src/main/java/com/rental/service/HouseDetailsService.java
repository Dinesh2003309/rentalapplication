package com.rental.service;

import com.rental.dtos.FilterDto;
import com.rental.dtos.PaginationDto;
import com.rental.dtos.EditHousedetailsDTO;
import com.rental.dtos.HouseDetailsDTO;
import com.rental.payload.Response;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

public interface HouseDetailsService {

	Response addHouseDetailsResponse(HttpServletRequest request, HouseDetailsDTO houseDetailsDTO);

	Response softDeleteHouseDetailsById(int id);

	Response getHouseDetailById(int id);

	Response getHouseDetailsByUserId(HttpServletRequest request, PaginationDto pagination);

	Response getAllCompletedHouseDetailsByOtherUser(HttpServletRequest request, PaginationDto pagination);

	Response getAllActiveHouseDetailsByOtherUser(HttpServletRequest request, PaginationDto pagination);

	Response editHouseDetails(HttpServletRequest request, int id, EditHousedetailsDTO updatedto);

	Response getCompletedHouseDetailsByUserId(HttpServletRequest request, PaginationDto pagination);

	Response addhouseimage(HttpServletRequest request, MultipartFile file);

	Response applyForHouse(HttpServletRequest request, Integer id);

	Response getApplicantsByHouseId(HttpServletRequest request, Integer houseId, PaginationDto pagination);


	Response acceptApplication(HttpServletRequest request, Integer id, Integer applicantUserId);


	Response rejectApplication(HttpServletRequest request, Integer houseId, Integer applicantUserId);

	Response myhelpsfilter(HttpServletRequest request, FilterDto data, String pageName);

	Response myexploresfilter(HttpServletRequest request, FilterDto data);

	Response getappliedHousesByUserId(HttpServletRequest request, PaginationDto pagination);

	Response deleteHouseApplication(HttpServletRequest request, Integer houseId);

}
