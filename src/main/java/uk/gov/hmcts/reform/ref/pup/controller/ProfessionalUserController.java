package uk.gov.hmcts.reform.ref.pup.controller;

import uk.gov.hmcts.reform.ref.pup.domain.ProfessionalUser;
import uk.gov.hmcts.reform.ref.pup.dto.ProfessionalUserCreation;
import uk.gov.hmcts.reform.ref.pup.dto.ProfessionalUserDto;
import uk.gov.hmcts.reform.ref.pup.exception.ApplicationException;
import uk.gov.hmcts.reform.ref.pup.service.adaptor.ProfessionalUserServiceAdaptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.Optional;

import javax.validation.Valid;

@RestController
@RequestMapping("pup/professional-users")
public class ProfessionalUserController {

    private static final ResponseEntity<ProfessionalUserDto> NOT_FOUND_RESPONSE = ResponseEntity.notFound().build();
    private final ProfessionalUserServiceAdaptor professionalUserService;

    @Autowired
    public ProfessionalUserController(ProfessionalUserServiceAdaptor professionalUserService) {
        this.professionalUserService = professionalUserService;
    }

    @PostMapping
    @ApiOperation("Create Professional User.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = ProfessionalUserDto.class)
    })
    public ResponseEntity<ProfessionalUserDto> createProfessionalUser(@RequestBody @Valid ProfessionalUserCreation professionalUser) throws ApplicationException {
        return ResponseEntity.ok(professionalUserService.create(professionalUser));
    }

    @GetMapping(value = "{userId}")
    @ApiOperation("Retrieve Professional User.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success", response = ProfessionalUser.class)
    })
    public ResponseEntity<ProfessionalUserDto> getProfessionalUser(@PathVariable String userId) throws ApplicationException {
        Optional<ProfessionalUserDto> professionalUser = professionalUserService.retrieve(userId);
        if (!professionalUser.isPresent()) {
            return NOT_FOUND_RESPONSE;
        }

        return ResponseEntity.ok(professionalUser.get());
    }

    @DeleteMapping(value = "{userId}")
    @ApiOperation("Delete Professional User.")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "No Content")
    })
    public ResponseEntity<ProfessionalUserDto> deleteProfessionalUser(@PathVariable String userId) throws ApplicationException {
        professionalUserService.delete(userId);
        return ResponseEntity.noContent().build();
    }

}
