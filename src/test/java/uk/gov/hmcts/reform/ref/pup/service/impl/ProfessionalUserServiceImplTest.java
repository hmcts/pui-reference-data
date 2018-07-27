package uk.gov.hmcts.reform.ref.pup.service.impl;

import uk.gov.hmcts.reform.ref.pup.domain.PaymentAccount;
import uk.gov.hmcts.reform.ref.pup.domain.ProfessionalUser;
import uk.gov.hmcts.reform.ref.pup.dto.ProfessionalUserCreation;
import uk.gov.hmcts.reform.ref.pup.exception.ApplicationException;
import uk.gov.hmcts.reform.ref.pup.exception.ApplicationException.ApplicationErrorCode;
import uk.gov.hmcts.reform.ref.pup.repository.PaymentAccountRepository;
import uk.gov.hmcts.reform.ref.pup.repository.ProfessionalUserRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class ProfessionalUserServiceImplTest {

    @Mock
    private ProfessionalUserRepository professionalUserRepository;
    
    @Mock
    private PaymentAccountRepository paymentAccountRepository;
    
    @InjectMocks
    private ProfessionalUserServiceImpl professionalUserService;
    
    @Captor
    ArgumentCaptor<ProfessionalUser> professionalUserCaptor;

    private ProfessionalUserCreation testUserRequest;
    private ProfessionalUser testUser;
    private PaymentAccount testPaymentAccount;

    @Before
    public void setUp() {

        testUserRequest = createFakeProfessionalUserRequest();
        testUser = createFakeProfessionalUser();
        testPaymentAccount = createFakePaymentAccount();
    }
    
    private PaymentAccount createFakePaymentAccount() {
        PaymentAccount paymentAccount = new PaymentAccount();
        paymentAccount.setPbaNumber("DUMMY");
        paymentAccount.setUuid(UUID.randomUUID());
        return paymentAccount;
    }

    private ProfessionalUser createFakeProfessionalUser() {
        ProfessionalUser firstTestUser = new ProfessionalUser();
        firstTestUser.setEmail("DUMMY@DUMMY.com");
        firstTestUser.setFirstName("DUMMY");
        firstTestUser.setPhoneNumber("DUMMY");
        firstTestUser.setSurname("DUMMY");
        firstTestUser.setUserId("DUMMY");
        firstTestUser.setAccountAssignments(new HashSet<>());
        return firstTestUser;
    }
    
    private ProfessionalUserCreation createFakeProfessionalUserRequest() {
        ProfessionalUserCreation firstTestUser = new ProfessionalUserCreation();
        firstTestUser.setEmail("DUMMY@DUMMY.com");
        firstTestUser.setFirstName("DUMMY");
        firstTestUser.setPhoneNumber("DUMMY");
        firstTestUser.setSurname("DUMMY");
        firstTestUser.setUserId("DUMMY");
        return firstTestUser;
    }
    
    
    private ProfessionalUser createFakeProfessionalUserWithSameEmail() {
        ProfessionalUser foolTestUser = new ProfessionalUser();
        foolTestUser.setEmail("DUMMY@DUMMY.com");
        foolTestUser.setFirstName("FOOL");
        foolTestUser.setPhoneNumber("FOOL");
        foolTestUser.setSurname("FOOL");
        foolTestUser.setUserId("FOOL");
        return foolTestUser;
    }

    @Test
    public void create() throws ApplicationException {
        when(professionalUserRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ProfessionalUser created = professionalUserService.create(testUserRequest);
        
        assertThat(created.getEmail(), equalTo(testUserRequest.getEmail()));
        assertThat(created.getFirstName(), equalTo(testUserRequest.getFirstName()));
        assertThat(created.getPhoneNumber(), equalTo(testUserRequest.getPhoneNumber()));
        assertThat(created.getSurname(), equalTo(testUserRequest.getSurname()));
        assertThat(created.getUserId(), equalTo(testUserRequest.getUserId()));
        
    }
    
    @Test(expected = ApplicationException.class)
    public void createWithAnAlreadyUsedEmailShouldReturnAnException() throws ApplicationException {
        when(professionalUserRepository.findOneByEmail(testUser.getEmail())).thenReturn(Optional.of(createFakeProfessionalUserWithSameEmail()));
        when(professionalUserRepository.save(testUser)).thenReturn(testUser);
        
        professionalUserService.create(testUserRequest);
    }

    @Test
    public void retrieve() throws ApplicationException {
        when(professionalUserRepository.findOneByUserId(testUser.getUserId())).thenReturn(Optional.of(testUser));

        Optional<ProfessionalUser> retrieve = professionalUserService.retrieve(testUser.getUserId());

        assertThat(retrieve.get().getUserId(), equalTo(testUser.getUserId()));
    }
    
    @Test
    public void delete() throws ApplicationException {

        professionalUserService.delete(testUser.getUserId());

        verify(professionalUserRepository, only()).deleteByUserId(testUser.getUserId());
    }
    
    @Test
    public void assignPaymentAccount() throws ApplicationException {

        when(professionalUserRepository.findOneByUserId(testUser.getUserId())).thenReturn(Optional.of(testUser));
        when(paymentAccountRepository.findById(testPaymentAccount.getUuid())).thenReturn(Optional.of(testPaymentAccount));
        
        professionalUserService.assignPaymentAccount(testUser.getUserId(), testPaymentAccount.getUuid());
        
        verify(professionalUserRepository).save(professionalUserCaptor.capture());
                
        assertThat(professionalUserCaptor.getValue().getUserId(), equalTo(testUser.getUserId()));
        Set<PaymentAccount> accountAssignments = professionalUserCaptor.getValue().getAccountAssignments();
        assertThat(accountAssignments.size(), equalTo(1));
        assertThat(accountAssignments.iterator().next().getUuid(), equalTo(testPaymentAccount.getUuid()));
    }

    @Test
    public void assignPaymentAccount_withAnInexistantProfessionalUserShouldReturnAnException() throws ApplicationException {
        
        when(professionalUserRepository.findOneByUserId(testUser.getUserId())).thenReturn(Optional.empty());
        
        try {
            professionalUserService.assignPaymentAccount(testUser.getUserId(), testPaymentAccount.getUuid());
            fail();
        } catch (ApplicationException e) {
            assertThat(e.getApplicationErrorCode(), equalTo(ApplicationErrorCode.PROFESSIONAL_USER_ID_DOES_NOT_EXIST));
        }
    }

    @Test
    public void assignPaymentAccount_withAnInexistantPaymentAccountShouldReturnAnException() throws ApplicationException {

        when(professionalUserRepository.findOneByUserId(testUser.getUserId())).thenReturn(Optional.of(testUser));
        when(paymentAccountRepository.findById(testPaymentAccount.getUuid())).thenReturn(Optional.empty());
        
        try {
            professionalUserService.assignPaymentAccount(testUser.getUserId(), testPaymentAccount.getUuid());
            fail();
        } catch (ApplicationException e) {
            assertThat(e.getApplicationErrorCode(), equalTo(ApplicationErrorCode.PAYMENT_ACCOUNT_ID_DOES_NOT_EXIST));
        }
    }
    
    @Test
    public void unassignPaymentAccount() throws ApplicationException {

        when(professionalUserRepository.findOneByUserId(testUser.getUserId())).thenReturn(Optional.of(testUser));
        when(paymentAccountRepository.findById(testPaymentAccount.getUuid())).thenReturn(Optional.of(testPaymentAccount));
        
        // if user has an account 
        testUser.getAccountAssignments().add(testPaymentAccount);
        assertThat(testUser.getAccountAssignments().size(), equalTo(1));
        
        professionalUserService.unassignPaymentAccount(testUser.getUserId(), testPaymentAccount.getUuid());
        
        verify(professionalUserRepository).save(professionalUserCaptor.capture());
                
        assertThat(professionalUserCaptor.getValue().getUserId(), equalTo(testUser.getUserId()));
        Set<PaymentAccount> accountAssignments = professionalUserCaptor.getValue().getAccountAssignments();
        assertThat(accountAssignments.size(), equalTo(0));
    }

    @Test
    public void unassignPaymentAccount_withAnInexistantProfessionalUserShouldReturnAnException() throws ApplicationException {

        when(professionalUserRepository.findOneByUserId(testUser.getUserId())).thenReturn(Optional.empty());
        
        try {
            professionalUserService.unassignPaymentAccount(testUser.getUserId(), testPaymentAccount.getUuid());
            fail();
        } catch (ApplicationException e) {
            assertThat(e.getApplicationErrorCode(), equalTo(ApplicationErrorCode.PROFESSIONAL_USER_ID_DOES_NOT_EXIST));
        }
    }
    
    @Test
    public void unassignPaymentAccount_withAnInexistantPaymentAccountShouldReturnAnException() throws ApplicationException {

        when(professionalUserRepository.findOneByUserId(testUser.getUserId())).thenReturn(Optional.of(testUser));
        when(paymentAccountRepository.findById(testPaymentAccount.getUuid())).thenReturn(Optional.empty());
        
        try {
            professionalUserService.unassignPaymentAccount(testUser.getUserId(), testPaymentAccount.getUuid());
            fail();
        } catch (ApplicationException e) {
            assertThat(e.getApplicationErrorCode(), equalTo(ApplicationErrorCode.PAYMENT_ACCOUNT_ID_DOES_NOT_EXIST));
        }
    }
    
}