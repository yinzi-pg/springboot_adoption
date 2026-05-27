package com.ambow.springboot_adoption.service;

import com.ambow.springboot_adoption.model.PetModel;
import com.ambow.springboot_adoption.vo.Result;

public interface PetInformationService {
    Result getPetList(Integer pageNum, Integer pageSize);



    Result searchPetService(String keyword, Integer pageNum, Integer pageSize);

    Result addPet(PetModel petModel);

    Result updatePet(PetModel petModel);

    Result deletePet(Integer petId);
}



