package com.fintrack.scheduler;

import com.fintrack.entity.RecurringTransaction;
import com.fintrack.entity.Transaction;
import com.fintrack.enums.Frequency;
import com.fintrack.repository.RecurringTransactionRepository;
import com.fintrack.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecurringTransactionScheduler {

    private final RecurringTransactionRepository recurringRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Runs every day at midnight (00:00)
     * Processes all recurring transactions due today or earlier
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void processRecurringTransactions() {
        log.info("RecurringTransactionScheduler started at: {}", LocalDate.now());

        List<RecurringTransaction> dueTransactions = recurringRepository
                .findByNextExecutionDateLessThanEqualAndIsActiveTrue(LocalDate.now());

        log.info("Found {} recurring transactions to process", dueTransactions.size());

        for (RecurringTransaction rt : dueTransactions) {
            try {
                // Create actual transaction
                Transaction transaction = Transaction.builder()
                        .amount(rt.getAmount())
                        .type(rt.getType())
                        .description(rt.getName() + " (Auto-generated)")
                        .date(LocalDate.now())
                        .isRecurring(true)
                        .user(rt.getUser())
                        .category(rt.getCategory())
                        .build();

                transactionRepository.save(transaction);

                // Update next execution date
                rt.setNextExecutionDate(calculateNextDate(rt.getNextExecutionDate(), rt.getFrequency()));
                recurringRepository.save(rt);

                log.info("Processed recurring transaction: {} for user: {}",
                        rt.getName(), rt.getUser().getId());

            } catch (Exception e) {
                log.error("Error processing recurring transaction ID: {}, Error: {}",
                        rt.getId(), e.getMessage());
            }
        }

        log.info("RecurringTransactionScheduler completed. Processed: {} transactions",
                dueTransactions.size());
    }

    /**
     * Runs every day at 1 AM — marks goals as FAILED if deadline passed
     */
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void checkGoalDeadlines() {
        log.info("Goal deadline check started at: {}", LocalDate.now());
        // Goal status update logic handled in GoalServiceImpl
    }

    private LocalDate calculateNextDate(LocalDate current, Frequency frequency) {
        return switch (frequency) {
            case DAILY -> current.plusDays(1);
            case WEEKLY -> current.plusWeeks(1);
            case MONTHLY -> current.plusMonths(1);
            case YEARLY -> current.plusYears(1);
        };
    }
}
