package com.needbackend_app.needapp.user.customer.dto;


import java.util.UUID;


public record SubCategoryDTO (

     UUID id,
     String name,
     String description
) {}
