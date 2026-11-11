package id.ac.polban.pbo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class BankAccountTest {
    private BankAccount sender;
    private BankAccount receiver;

    @BeforeEach
    void setUp() {
        sender = new BankAccount("001", "Andi", 100_000);
        receiver = new BankAccount("002", "Budi", 0);
    }

    @Test
    void deposit_menambahSaldo() {
        // Arrange
        BankAccount acc = new BankAccount("001", "Andi", 100_000);
        // Act
        acc.deposit(50_000);
        // Assert
        assertEquals(150_000, acc.getBalance(), 0.001);
    }

    @Test
    void constructor_menyimpanDataDenganBenar() {
        BankAccount acc = new BankAccount("001", "Andi", 100_000);
        assertAll("data rekening",
            () -> assertEquals("001", acc.getAccountNumber()),
            () -> assertEquals("Andi", acc.getOwner()),
            () -> assertEquals(100_000, acc.getBalance(), 0.001)
        );
    }

    @Test
    void withdraw_melebihiSaldo_ditolak() {
        BankAccount acc = new BankAccount("001", "Andi", 100_000);
        assertThrows(
            InsufficientBalanceException.class,
            () -> acc.withdraw(200_000)
        );
        assertEquals(100_000, acc.getBalance(), 0.001);
    }

    @Test
    void withdraw_valid_tidakMelempar() {
        BankAccount acc = new BankAccount("001", "Andi", 100_000);
        assertDoesNotThrow(() -> acc.withdraw(30_000));
        assertEquals(70_000, acc.getBalance(), 0.001);
    }

    @Test
    void constructor_saldoNegatif_ditolak() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> new BankAccount("001", "Andi", -1));
        assertEquals("Saldo awal tidak boleh negatif", ex.getMessage());
    }

    @ParameterizedTest(name = "setoran {0} ditolak")
    @ValueSource(doubles = {0, -1, -50_000, -0.01})
    void deposit_tidakValid(double amount) {
        assertThrows(
            IllegalArgumentException.class,
            () -> sender.deposit(amount)
        );
    }

    @ParameterizedTest(name = "saldo {0} - tarik {1} = {2}")
    @CsvSource({
        "100000, 10000,   90000",
        "100000, 100000,  0",
        "50000,  25000.5, 24999.5"
    })
    void withdraw_berbagaiNilai(double awal, double tarik, double sisa) throws InsufficientBalanceException {
        BankAccount acc = new BankAccount("009", "Dewi", awal);
        acc.withdraw(tarik);
        assertEquals(sisa, acc.getBalance(), 0.001);
    }

    @Test
    void transfer_targetNull_ditolak() {
        BankAccount sender = new BankAccount("001", "Andi", 100_000);
        assertThrows(
            IllegalArgumentException.class,
            () -> sender.transfer(null, 20_000)
        );
        assertEquals(100_000, sender.getBalance(), 0.001);
    }
}
