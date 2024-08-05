package accounts.web;

import accounts.AccountManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.money.Percentage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import rewards.internal.account.Account;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)  // includes @ExtendWith(SpringExtension.class)
//@ExtendWith(SpringExtension.class)
public class AccountControllerBootTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountManager accountManager;

    @Test
    public void accountDetails() throws Exception {
        // Arrange
        given(accountManager.getAccount(0L))
            .willReturn(new Account("1234567890", "John Doe"));

        // Act and assert
        mockMvc.perform(get("/accounts/0"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("name").value("John Doe"))
            .andExpect(jsonPath("number").value("1234567890"));

        // Verify
        verify(accountManager).getAccount(0L);
    }

    @Test
    public void createAccount() throws Exception {
        Account testAccount = new Account("1234512345", "Mary Jones");
        testAccount.setEntityId(21L);

        given(accountManager.save(any(Account.class)))
            .willReturn(testAccount);

        mockMvc.perform(post("/accounts")
                .content(asJsonString(testAccount))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "http://localhost/accounts/21"));

        verify(accountManager).save(any(Account.class));
    }

    @Test
    public void getAllAccounts() throws Exception {
        // Arrange
        given(accountManager.getAllAccounts())
            .willReturn(
                List.of(
                    new Account("1234567890", "John Doe"),
                    new Account("1234567891", "Jane Doe")
                )
            );

        // Act and assert
        mockMvc.perform(get("/accounts"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].name").value("John Doe"))
            .andExpect(jsonPath("$[0].number").value("1234567890"))
            .andExpect(jsonPath("$[1].name").value("Jane Doe"))
            .andExpect(jsonPath("$[1].number").value("1234567891"))
            // Test the length of the JSON array is equal to 21
            .andExpect(jsonPath("$.length()").value(2));

        // Verify
        verify(accountManager).getAllAccounts();
    }

    // Get an existing beneficiary for an account
    @Test
    public void getValidBeneficiaryForAnAccount() throws Exception {
        Account account = new Account("1234567890", "John Doe");
        account.addBeneficiary("Corgan", new Percentage(0.1));

        given(accountManager.getAccount(anyLong()))
            .willReturn(account);

        mockMvc.perform(get("/accounts/{accountId}/beneficiaries/{beneficiaryName}", 0L, "Corgan"))
            .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("name").value("Corgan"))
            .andExpect(jsonPath("allocationPercentage").value("0.1"));

        verify(accountManager).getAccount(anyLong());
    }

    // Get a non-existing beneficiary for an account
    @Test
    public void getNonExistingBeneficiary() {
        Account account = new Account("1234567890", "John Doe");
        account.addBeneficiary("Corgan", new Percentage(0.1));

        given(accountManager.getAccount(anyLong()))
            .willReturn(account);

        // Act and assert
        try {
            mockMvc.perform(get("/accounts/{accountId}/beneficiaries/{beneficiaryName}", 0L, "Kate"))
                .andExpect(status().isNotFound());  // 404
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Verify
        verify(accountManager).getAccount(anyLong());
    }

    // Add a new beneficiary to an account.
    @Test
    public void addNewBeneficiaryToAccount() {
        // Arrange
        given(accountManager.getAccount(anyLong()))
            .willReturn(new Account("1234567890", "John Doe"));

        // Act and assert
        try {
            mockMvc.perform(post("/accounts/{entityId}/beneficiaries", 0L)
                .content("Kate"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/accounts/0/beneficiaries/Kate"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Remove a beneficiary from an account.
    @Test
    public void removeBeneficiaryFromAccount() {
        // Arrange
        Account account = new Account("1234567890", "John Doe");
        account.addBeneficiary("Corgan", new Percentage(0.1));
        given(accountManager.getAccount(anyLong()))
            .willReturn(account);

        // Act and assert
        try {
            mockMvc.perform(delete("/accounts/{entityId}/beneficiaries/{name}", 0L, "Corgan"))
                .andExpect(status().isNoContent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Remove a non-existent beneficiary from an account.
    @Test
    public void removeNonExistentBeneficiaryFromAccount() {
        // Arrange
        Account account = new Account("1234567890", "John Doe");
        given(accountManager.getAccount(anyLong()))
            .willReturn(account);

        // Act and assert
        try {
            mockMvc.perform(delete("/accounts/{entityId}/beneficiaries/{name}", 0L, "Noname"))
                .andExpect(status().isNotFound());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void accountDetailsFail() throws Exception {
        given(accountManager.getAccount(any(Long.class)))
            .willThrow(new IllegalArgumentException("No such account with id " + 0L));

        mockMvc.perform(get("/accounts/9999"))
            .andExpect(status().isNotFound());

        verify(accountManager).getAccount(any(Long.class));
    }

    // Utility class for converting an object into JSON string
    protected static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
