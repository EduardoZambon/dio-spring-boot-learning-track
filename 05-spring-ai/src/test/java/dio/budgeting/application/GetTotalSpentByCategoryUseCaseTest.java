package dio.budgeting.application;

import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetTotalSpentByCategoryUseCaseTest {

    private TransactionRepository transactionRepository;
    private GetTotalSpentByCategoryUseCase useCase;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        useCase = new GetTotalSpentByCategoryUseCase(transactionRepository);
    }

    @Test
    void shouldCalculateTotalSpentByCategory() {
        var transactions = List.of(
                new Transaction("Mercado", 150, Category.GROCERIES),
                new Transaction("Supermercado", 250, Category.GROCERIES),
                new Transaction("Padaria", 50, Category.GROCERIES)
        );

        when(transactionRepository.findAllByCategory(Category.GROCERIES))
                .thenReturn(transactions);

        long total = useCase.execute(Category.GROCERIES);

        assertEquals(450, total);
        verify(transactionRepository).findAllByCategory(Category.GROCERIES);
    }

    @Test
    void shouldReturnZeroWhenCategoryHasNoTransactions() {
        when(transactionRepository.findAllByCategory(Category.PHARMA))
                .thenReturn(List.of());

        long total = useCase.execute(Category.PHARMA);

        assertEquals(0, total);
        verify(transactionRepository).findAllByCategory(Category.PHARMA);
    }
}
