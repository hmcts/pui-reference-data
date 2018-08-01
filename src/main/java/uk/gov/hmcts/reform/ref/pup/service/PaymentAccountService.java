package uk.gov.hmcts.reform.ref.pup.service;

import uk.gov.hmcts.reform.ref.pup.domain.PaymentAccount;
import uk.gov.hmcts.reform.ref.pup.dto.PaymentAccountAssignment;
import uk.gov.hmcts.reform.ref.pup.dto.PaymentAccountCreation;
import uk.gov.hmcts.reform.ref.pup.exception.ApplicationException;

import java.util.List;
import java.util.Optional;

public interface PaymentAccountService {

    PaymentAccount create(PaymentAccountCreation paymentAccount) throws ApplicationException;

    Optional<PaymentAccount> retrieve(String pbaNumber) throws ApplicationException;

    void delete(String pbaNumber) throws ApplicationException;

    List<PaymentAccount> retrieveForUser(String userId) throws ApplicationException;

    Optional<PaymentAccount> assign(String pbaNumber, PaymentAccountAssignment paymentAccountAssignment) throws ApplicationException;

    Optional<PaymentAccount> unassign(String pbaNumber, PaymentAccountAssignment paymentAccountAssignment) throws ApplicationException;
    
}