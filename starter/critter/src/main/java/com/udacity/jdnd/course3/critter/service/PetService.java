package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.dto.CustomerDTO;
import com.udacity.jdnd.course3.critter.dto.PetDTO;
import com.udacity.jdnd.course3.critter.model.PetModel;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class PetService {

    @Autowired
    PetRepository petRepository;

    public PetModel savePet(PetDTO petDTO) {
        PetModel petModel = new PetModel();
        BeanUtils.copyProperties(petDTO, petModel);
        petModel.setId(null);
        return petRepository.save(petModel);
    }

    public PetModel findPetById(long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new IllegalStateException("Pet not found!"));
    }

    public List<PetModel> findPetByOwnerId(long ownerId) {
        return petRepository.findAllByOwnerIdOrderByIdDesc(ownerId);
    }

    public List<Long> getCustomerPetIds(Long id) {
        List<Long> ids = new ArrayList<>();
        findPetByOwnerId(id).forEach(petModel -> ids.add(petModel.getId()));
        return ids;
    }

    public PetModel updatePet(PetModel petModel) {
        return petRepository.save(petModel);
    }

    public List<PetDTO> findAllPets() {
        List<PetDTO> petDTOS = new ArrayList<>();
        petRepository.findAll().forEach(petModel -> {
            PetDTO petDTO = new PetDTO();
            BeanUtils.copyProperties(petModel, petDTO);
            petDTOS.add(petDTO);
        });

        return petDTOS;
    }
}
