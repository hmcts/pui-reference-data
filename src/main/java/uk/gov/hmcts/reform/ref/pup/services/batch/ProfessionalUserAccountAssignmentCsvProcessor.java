package uk.gov.hmcts.reform.ref.pup.services.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.ref.pup.domain.*;
import uk.gov.hmcts.reform.ref.pup.services.domain.*;

public class ProfessionalUserAccountAssignmentCsvProcessor implements ItemProcessor<ProfessionalUserAccountAssignmentCsvDTO, ProfessionalUserAccountAssignment> {

    @Autowired
    private ProfessionalUserAccountAssignmentService professionalUserAccountAssignmentService;

    @Override
    public ProfessionalUserAccountAssignment process(ProfessionalUserAccountAssignmentCsvDTO professionalUserAccountAssignmentCsvDTO) throws Exception {
        return professionalUserAccountAssignmentService.createProfessionalUserAccountWithDTO(professionalUserAccountAssignmentCsvDTO);
    }
}
