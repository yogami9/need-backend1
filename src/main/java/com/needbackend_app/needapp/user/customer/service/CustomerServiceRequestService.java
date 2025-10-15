package com.needbackend_app.needapp.user.customer.service;

import com.needbackend_app.needapp.user.customer.dto.CustomerServiceRequestDTO;
import com.needbackend_app.needapp.user.customer.dto.CustomerServiceResponseDTO;
import com.needbackend_app.needapp.user.customer.model.CustomerServiceRequest;
import com.needbackend_app.needapp.user.util.ApiResponse;

import java.util.List;

public interface CustomerServiceRequestService {

    public ApiResponse createRequest(CustomerServiceRequestDTO requestDTO);

    List<CustomerServiceResponseDTO> getAllequest();
}
