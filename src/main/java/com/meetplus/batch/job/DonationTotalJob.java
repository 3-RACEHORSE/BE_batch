package com.meetplus.batch.job;

import com.meetplus.batch.application.dto.TotalDonationDto;
import com.meetplus.batch.domain.payment.TotalDonationSettlement;
import com.meetplus.batch.infrastructure.payment.BankRepository;
import com.meetplus.batch.infrastructure.payment.TotalSettlementRepository;
import jakarta.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
public class DonationTotalJob {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final BankRepository bankRepository;
	private final TotalSettlementRepository totalSettlementRepository;
	private final EntityManagerFactory entityManagerFactory;

	@Autowired
	public DonationTotalJob(JobRepository jobRepository,
		@Qualifier("paymentTransactionManager") PlatformTransactionManager transactionManager,
		BankRepository bankRepository,
		TotalSettlementRepository totalSettlementRepository,
		@Qualifier("paymentEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.bankRepository = bankRepository;
		this.totalSettlementRepository = totalSettlementRepository;
		this.entityManagerFactory = entityManagerFactory;
	}

	@Bean
	public TotalDonationItemReader totalDonationReader() {
		log.info("totalDonationReader");
		return new TotalDonationItemReader(bankRepository, totalSettlementRepository);
	}

	@Bean
	public ItemProcessor<TotalDonationDto, TotalDonationSettlement> TotalDonationProcessor(
	) {
		return totalDonationDto -> {
			try {
				BigDecimal totalDonation = totalDonationDto.getTotalDonation();
				List<TotalDonationSettlement> totalDonationSettlement = totalSettlementRepository.findAll();
				log.info(totalDonationDto.getLastSettlementDate().toString());
				if (totalDonationSettlement.isEmpty()) {
					log.info("totalDonationSettlement is empty");
					TotalDonationSettlement totalDonationSettlement1 = TotalDonationSettlement.builder()
						.totalDonation(totalDonation)
						.lastSettlementDate(LocalDateTime.now())
						.build();
					log.info("totalDonationSettlement1: {}", totalDonationSettlement1.toString());
					totalSettlementRepository.save(totalDonationSettlement1);
					return totalDonationSettlement1;
				}
				else {
					TotalDonationSettlement totalDonationSettlement1 = totalDonationSettlement.get(0).toBuilder()
						.id(totalDonationSettlement.get(0).getId())
						.totalDonation(totalDonationSettlement.get(0).getTotalDonation().add(totalDonation))
						.lastSettlementDate(LocalDateTime.now())
						.build();
					log.info("totalDonationSettlement1: {}", totalDonationSettlement1);
					totalSettlementRepository.save(totalDonationSettlement1);
					return null;
				}
			} catch (Exception e) {
				log.info("processor error: {}", e.getMessage());
				return null;
			}
		};
	}

	@Bean
	public JpaItemWriter<TotalDonationSettlement> totalSettlementWriter() {
		return new JpaItemWriterBuilder<TotalDonationSettlement>()
			.entityManagerFactory(entityManagerFactory)
			.build();
	}

	@Bean
	@Qualifier("donationTotalStep")
	public Step totalDonationStep() {
		return new StepBuilder("donationTotalStep", jobRepository)
			.<TotalDonationDto, TotalDonationSettlement>chunk(1, transactionManager)
			.reader(totalDonationReader())
			.processor(TotalDonationProcessor())
			.writer(totalSettlementWriter())
			.allowStartIfComplete(true)
			.build();
	}

	@Bean
	@Qualifier("donationTotalJob")
	public Job totalDonationSettlementJob(
		@Qualifier("donationTotalStep") Step totalDonationStep) {
		return new JobBuilder("donationTotalJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(totalDonationStep)
			.build();
	}
}
