package com.ra.base_spring_boot.services.bus;

import com.ra.base_spring_boot.dto.bus.BusCompanyRequest;
import com.ra.base_spring_boot.dto.bus.BusCompanyResponse;
import com.ra.base_spring_boot.model.Bus.BusCompany;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IBusCompanyService {

    List<BusCompanyResponse> getPublicBusCompanies();
    List<BusCompanyResponse> findPopular();

    Page<BusCompanyResponse> findAll(Pageable pageable, String search);
    BusCompanyResponse findById(Long id);
    BusCompanyResponse save(BusCompanyRequest request);
    BusCompanyResponse update(Long id, BusCompanyRequest request);
    void delete(Long id);
    BusCompanyResponse uploadImage(Long companyId, MultipartFile imageFile);

}