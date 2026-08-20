package dio.budgeting.application;

import dio.budgeting.domain.Category;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class GetTotalSpentByCategoryUseCase {

    private final TransactionRepository transactionRepository;

    public GetTotalSpentByCategoryUseCase(
            TransactionRepository transactionRepository
    ) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(
        name = "get-total-spent-by-category",
        description = "Calcula o valor total das transações de uma categoria"
    )
    public long execute(Category category) {
        return transactionRepository.findAllByCategory(category)
                .stream()
                .mapToLong(transaction -> transaction.getAmount())
                .sum();
    }
}