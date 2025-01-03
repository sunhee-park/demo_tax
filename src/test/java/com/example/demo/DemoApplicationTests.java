package com.example.demo;

import com.example.demo.dto.SignupRequest;
import com.example.demo.entity.IncomeDetails;
import com.example.demo.entity.Users;
import com.example.demo.exception.CustomException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.integration.ScrapClient;
import com.example.demo.integration.incomescraping.IncomeResponse;
import com.example.demo.repository.IncomeDetailsRepository;
import com.example.demo.repository.UsersRepository;
import com.example.demo.service.IncomeScrapingService;
import com.example.demo.service.IncomeTaxCalculationService;
import com.example.demo.service.UsersService;
import com.example.demo.util.taxcalculator.TaxCalculator;
import com.example.demo.util.taxcalculator.TaxCalculatorFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private ScrapClient scrapClient;

	@Autowired
	private IncomeScrapingService incomeScrapingService;

	@Autowired
	private IncomeTaxCalculationService incomeTaxCalculationService;

	@Autowired
	private UsersService usersService;

	@Autowired
	private IncomeDetailsRepository incomeDetailsRepository;

	@Autowired
	private UsersRepository usersRepository;

	@Test
	void contextLoads() {
	}

	@BeforeEach
	void setUp() throws Exception {
		String userId = "test5";
		String password = "test5";
		String name ="조조";
		String regNo = "810326-2715702";

		SignupRequest request = new SignupRequest();
		request.setUserId(userId);
		request.setPassword(password);
		request.setName(name);
		request.setRegNo(regNo);

		usersService.signup(request);
	}

	// scraping data의 json to object mapping test
	@Test
	void testScrapResponseMapping() throws Exception {
		// Given: Mock JSON 데이터
		String mockJson = "{\"status\":\"success\",\"data\":{\"종합소득금액\":20000000,\"이름\":\"동탁\",\"소득공제\":{\"국민연금\":[{\"월\":\"2023-01\",\"공제액\":\"300,000.25\"},{\"월\":\"2023-02\",\"공제액\":\"200,000\"},{\"월\":\"2023-03\",\"공제액\":\"400,000.75\"},{\"월\":\"2023-05\",\"공제액\":\"100,000.10\"},{\"월\":\"2023-06\",\"공제액\":\"300,000\"},{\"월\":\"2023-08\",\"공제액\":\"200,000.20\"},{\"월\":\"2023-09\",\"공제액\":\"300,000.40\"},{\"월\":\"2023-10\",\"공제액\":\"300,000.70\"},{\"월\":\"2023-11\",\"공제액\":\"0\"},{\"월\":\"2023-12\",\"공제액\":\"0\"}],\"신용카드소득공제\":{\"month\":[{\"01\":\"100,000.10\"},{\"03\":\"100,000.20\"},{\"05\":\"200,000.30\"},{\"10\":\"100,000\"},{\"12\":\"300,000.50\"}],\"year\":2023},\"세액공제\":\"300,000\"}},\"errors\":{\"code\":null,\"message\":null,\"validations\":null}}";

		// When: JSON을 ObjectMapper로 파싱
		ObjectMapper objectMapper = new ObjectMapper();
		IncomeResponse response = objectMapper.readValue(mockJson, IncomeResponse.class);

		// Then: 데이터 검증
		assertEquals(20000000, response.getData().getTotalIncome().intValue());
		assertEquals(300000, response.getData().getDeductions().getTaxCredit().intValue());
		assertEquals(new BigDecimal("2900003.50"), response.getData().getDeductions().calculateTotalDeductions());
	}

	// http client request and response test
	@Test
	void testFetchScrapData() {
		// Arrange
		String name = "동탁";
		String regNo = "921108-1582816";

		// Act
		IncomeResponse response = scrapClient.fetchScrapData(name, regNo);

		// Assert
		assertEquals(20000000, response.getData().getTotalIncome().intValue());
	}

	// 세금 계산 test
	@Test
	void testTaxCalculation() {
		// Arrange
		BigDecimal totalIncome = new BigDecimal("20000000");
		BigDecimal deductions = new BigDecimal("2900003.50");
		BigDecimal taxableIncome = totalIncome.subtract(deductions).setScale(0, RoundingMode.HALF_UP);
		// 세금 계산
		BigDecimal baseTax = new BigDecimal("840000"); // 기본 세금
		BigDecimal threshold = new BigDecimal("14000000"); // 기준 금액
		BigDecimal excessTaxRate = new BigDecimal("0.15"); // 초과 금액의 15%

		BigDecimal excessIncome = taxableIncome.subtract(threshold);
		BigDecimal excessTax = excessIncome.multiply(excessTaxRate).setScale(0, RoundingMode.HALF_UP);

		BigDecimal expectedTax = baseTax.add(excessTax).setScale(0, RoundingMode.HALF_UP);

		// Act
		TaxCalculator calculator = TaxCalculatorFactory.createCalculator("default");
		BigDecimal calculatedTax = calculator.calculateTax(totalIncome.subtract(deductions));

		// Assert
		assertEquals(expectedTax, calculatedTax);

	}

	// scraping data service test
	@Test
	void testGetIncomeDetailsFromScrap_NewIncomeDetails() throws Exception {

		String name = "조조";
		String regNo = "810326-2715702";

		IncomeDetails result = incomeScrapingService.getIncomeDetailsFromScrap(name, regNo);

		assertEquals(new BigDecimal("60000000"), result.getTotalIncome());
		assertEquals(new BigDecimal("2462100.17"), result.getDeductions());
		assertEquals(new BigDecimal("300000"), result.getTaxCredit());

	}

	//calculate refund test
	@Test
	void testCalculateRefund(){

		Long userId = 1L;

		Users user = usersRepository.findById(userId)
				.orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

		IncomeDetails incomeDetails = new IncomeDetails();
		incomeDetails.setUser(user);
		incomeDetails.setTotalIncome(new BigDecimal("60000000"));
		incomeDetails.setTaxCredit(new BigDecimal("300000"));
		incomeDetails.setDeductions(new BigDecimal("2462100.17"));

		incomeDetailsRepository.save(incomeDetails);

		String result = incomeTaxCalculationService.calculateRefund(userId);

		assertEquals("7,749,096", result);
	}



}
