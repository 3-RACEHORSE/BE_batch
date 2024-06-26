package com.meetplus.batch.job;

import com.meetplus.batch.application.dto.TotalDonationDto;
import com.meetplus.batch.common.CustomJobParameter;
import com.meetplus.batch.domain.payment.TotalDonationSettlement;
import com.meetplus.batch.infrastructure.payment.BankRepository;
import com.meetplus.batch.infrastructure.payment.TotalSettlementRepository;
import jakarta.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
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
	private final CustomJobParameter customJobParameter;


	@Autowired
	public DonationTotalJob(JobRepository jobRepository,
		@Qualifier("paymentTransactionManager") PlatformTransactionManager transactionManager,
		BankRepository bankRepository,
		TotalSettlementRepository totalSettlementRepository,
		@Qualifier("paymentEntityManagerFactory") EntityManagerFactory entityManagerFactory,
		CustomJobParameter customJobParameter) {
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.bankRepository = bankRepository;
		this.totalSettlementRepository = totalSettlementRepository;
		this.entityManagerFactory = entityManagerFactory;
		this.customJobParameter = customJobParameter;
	}

	@Bean
	@JobScope
	public TotalDonationItemReader totalDonationReader() {
		return new TotalDonationItemReader(bankRepository, customJobParameter);
	}

	@Bean
	@JobScope
	public ItemProcessor<TotalDonationDto, TotalDonationSettlement> TotalDonationProcessor(
	) {
		return totalDonationDto -> {
			try {
				BigDecimal totalDonation = totalDonationDto.getTotalDonation();
				List<TotalDonationSettlement> totalDonationSettlement = totalSettlementRepository.findAll();

				if (totalDonationSettlement.isEmpty()) {
					log.info("totalDonationSettlement is empty");
					TotalDonationSettlement totalDonationSettlement1 = TotalDonationSettlement.builder()
						.totalDonation(totalDonation)
						.build();
					totalSettlementRepository.save(totalDonationSettlement1);
					return totalDonationSettlement1;
				}

				TotalDonationSettlement totalDonationSettlement1 = TotalDonationSettlement.builder()
					.id(totalDonationSettlement.get(0).getId())
					.totalDonation(totalDonation)
					.build();
				log.info("totalDonationSettlement1: {}", totalDonationSettlement1);
				totalSettlementRepository.save(totalDonationSettlement1);
				return totalDonationSettlement1;
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
	@JobScope
	@Qualifier("donationTotalStep")
	public Step totalDonationStep() {
		return new StepBuilder("donationTotalStep", jobRepository)
			.<TotalDonationDto, TotalDonationSettlement>chunk(10, transactionManager)
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
