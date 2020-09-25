package com.udacity.jdnd.course3.critter.controller;

import com.udacity.jdnd.course3.critter.dto.PetDTO;
import com.udacity.jdnd.course3.critter.model.PetModel;
import com.udacity.jdnd.course3.critter.service.PetService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles web requests related to Pets.
 */
@RestController
@RequestMapping("/pet")
public class PetController {

    @Autowired
    PetService petService;

    private PetModel convertPetToEntity(PetDTO petDTO){
        PetModel petModel = new PetModel();
        BeanUtils.copyProperties(petDTO, petModel);
        return petModel;
    }

    private PetDTO convertPetToDTO(PetModel petModel){
        PetDTO petDTO = new PetDTO();
        BeanUtils.copyProperties(petModel, petDTO);
        return petDTO;
    }

    @PostMapping
    public PetDTO savePet(@RequestBody PetDTO petDTO) {
        PetModel petModel = convertPetToEntity(petDTO);
        petModel = petService.savePet(petModel);
        return convertPetToDTO(petModel);
    }

    @GetMapping("/{petId}")
    public PetDTO getPet(@PathVariable long petId) {
        PetModel petModel = petService.findPetById(petId);
        return convertPetToDTO(petModel);
    }

    @PostMapping("/{customerId}")
    public PetDTO setCustomerPet(@PathVariable long customerId, @RequestBody PetDTO petDTO) {
        PetModel petModel = convertPetToEntity(petDTO);
        petModel.setOwnerId(customerId);
        petModel = petService.updatePet(petModel);
        return convertPetToDTO(petModel);
    }

    @GetMapping
    public List<PetDTO> getPets(){
        List<PetDTO> petDTOS = new ArrayList<>();
        petService.findAllPets().forEach(petModel -> petDTOS.add(convertPetToDTO(petModel)));
        return petDTOS;
    }

    @GetMapping("/owner/{ownerId}")
    public List<PetDTO> getPetsByOwner(@PathVariable long ownerId) {
        List<PetModel> petModels = petService.findPetByOwnerId(ownerId);
        List<PetDTO> petDTOs = new ArrayList<>();
        petModels.forEach(petModel -> {
            PetDTO petDTO = new PetDTO();
            BeanUtils.copyProperties(petModel, petDTO);
            petDTOs.add(petDTO);
        });

        return petDTOs;
    }
}
