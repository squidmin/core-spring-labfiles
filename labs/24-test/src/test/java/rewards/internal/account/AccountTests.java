package rewards.internal.account;

import common.money.MonetaryAmount;
import common.money.Percentage;
import org.junit.jupiter.api.Test;
import rewards.AccountContribution;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Account class that verify Account behavior works in isolation.
 */
public class AccountTests {

	private Account account = new Account("1", "Keith and Keri Donald");

	@Test
	public void accountIsValid() {
		// setup account with a valid set of beneficiaries to prepare for testing
		account.addBeneficiary("Annabelle", Percentage.valueOf("50%"));
		account.addBeneficiary("Corgan", Percentage.valueOf("50%"));
		assertTrue(account.isValid());
	}

	@Test
	public void accountIsInvalidWithNoBeneficiaries() {
		assertFalse(account.isValid());
	}

	@Test
	public void accountIsInvalidWhenBeneficiaryAllocationsAreOver100() {
		account.addBeneficiary("Annabelle", Percentage.valueOf("50%"));
		account.addBeneficiary("Corgan", Percentage.valueOf("100%"));
		assertFalse(account.isValid());
	}

	@Test
	public void accountIsInvalidWhenBeneficiaryAllocationsAreUnder100() {
		account.addBeneficiary("Annabelle", Percentage.valueOf("50%"));
		account.addBeneficiary("Corgan", Percentage.valueOf("25%"));
		assertFalse(account.isValid());
	}

	@Test
	public void makeContribution() {
		account.addBeneficiary("Annabelle", Percentage.valueOf("50%"));
		account.addBeneficiary("Corgan", Percentage.valueOf("50%"));
		AccountContribution contribution = account.makeContribution(MonetaryAmount.valueOf("100.00"));
		assertEquals(contribution.getAmount(), MonetaryAmount.valueOf("100.00"));
		assertEquals(MonetaryAmount.valueOf("50.00"), contribution.getDistribution("Annabelle").getAmount());
		assertEquals(MonetaryAmount.valueOf("50.00"), contribution.getDistribution("Corgan").getAmount());
	}
	
	@Test
	public void throwIllegalStateExceptionWhenContributionIsInvalid() throws Exception {
		Throwable exception = assertThrows(IllegalStateException.class,
				() -> {
					account.addBeneficiary("Annabelle", Percentage.valueOf("50%"));
					account.addBeneficiary("Corgan", Percentage.valueOf("100%"));
					account.makeContribution(MonetaryAmount.valueOf("100.00"));
				});
	    assertEquals("Cannot make contributions to this account: it has invalid beneficiary allocations", exception.getMessage());
	}

}
