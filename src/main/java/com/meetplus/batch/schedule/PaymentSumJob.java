package com.meetplus.batch.schedule;

import com.meetplus.batch.common.CustomJobParameter;
import com.meetplus.batch.domain.payment.Bank;
import com.meetplus.batch.infrastructure.payment.BankRepository;
import com.meetplus.batch.infrastructure.payment.PaymentRepository;
import com.meetplus.batch.kafka.KafkaProducerCluster;
import com.meetplus.batch.kafka.Topics;
import com.meetplus.batch.kafka.dto.SaveDonationDto;
import jakarta.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
public class PaymentSumJob {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final PaymentRepository paymentRepository;
	private final BankRepository bankRepository;
	private final EntityManagerFactory entityManagerFactory;
	private final CustomJobParameter customJobParameter;
	private final KafkaProducerCluster producer;

	@Autowired
	public PaymentSumJob(JobRepository jobRepository,
		@Qualifier("paymentTransactionManager") PlatformTransactionManager transactionManager,
		PaymentRepository paymentRepository,
		BankRepository bankRepository,
		@Qualifier("paymentEntityManagerFactory") EntityManagerFactory entityManagerFactory,
		CustomJobParameter customJobParameter, KafkaProducerCluster producer) {
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.paymentRepository = paymentRepository;
		this.bankRepository = bankRepository;
		this.entityManagerFactory = entityManagerFactory;
		this.customJobParameter = customJobParameter;
		this.producer = producer;
	}


	@Bean
	@StepScope
	public ItemReader<String> paymentSumReader() {
		return new ItemReader<String>() {
			private List<String> auctionUuids = paymentRepository.getAuctionUuidsByDateRange(
				customJobParameter.getStartTime(),
				customJobParameter.getEndTime());
			private int nextIndex = 0;

			@Override
			public String read() throws Exception {
				if (nextIndex < auctionUuids.size()) {
					return auctionUuids.get(nextIndex++);
				} else {
					return null; // 모든 auctionUuid를 처리한 경우 null 반환
				}
			}
		};
	}

	@Bean
	@StepScope
	public ItemProcessor<String, Bank> paymentSumProcessor(
	) {
		return auctionUuid -> {
			try {
				BigDecimal totalAmount = paymentRepository.getTotalAmountByAuctionUuid(
					auctionUuid,
					customJobParameter.getStartTime(),
					customJobParameter.getEndTime()
				);
				Optional<Bank> bankOpt = bankRepository.findByAuctionUuid(auctionUuid);
				if (bankOpt.isPresent()) {
					Bank bank = bankOpt.get();
					return Bank.builder()
						.id(bank.getId())
						.auctionUuid(bank.getAuctionUuid())
						.donation(bank.getDonation().add(totalAmount))
						.build();
				} else if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
					Bank bank = Bank.builder()
						.auctionUuid(auctionUuid)
						.donation(totalAmount)
						.build();
					bankRepository.save(bank);
					producer.sendMessage(Topics.AUCTION_POST_SERVICE.getTopic(),
						SaveDonationDto.builder()
							.auctionUuid(auctionUuid)
							.donation(totalAmount)
							.build());
					return bank;
				} else {
					return null;
				}
			} catch (Exception e) {
				log.info("Error processing auctionUuid: {}", e.getMessage());
				return null;
			}
		};
	}

	@Bean
	public JpaItemWriter<Bank> paymentSumWriter() {
		return new JpaItemWriterBuilder<Bank>()
			.entityManagerFactory(entityManagerFactory)
			.build();
	}

	@Bean
	@JobScope
	@Qualifier("sumPaymentAmountPaidStep")
	public Step sumPaymentAmountPaidStep() {
		return new StepBuilder("sumPaymentAmountPaidStep", jobRepository)
			.<String, Bank>chunk(10, transactionManager)
			.reader(paymentSumReader())
			.processor(paymentSumProcessor())
			.writer(paymentSumWriter())
			.build();
	}

	@Bean
	@Qualifier("sumPaymentAmountPaidJob")
	public Job sumPaymentAmountPaidJob(
		@Qualifier("sumPaymentAmountPaidStep") Step sumPaymentAmountPaidStep) {
		return new JobBuilder("sumPaymentAmountPaidJob", jobRepository)
			.start(sumPaymentAmountPaidStep)
			.build();
	}
}
