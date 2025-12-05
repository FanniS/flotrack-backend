package com.portfolio.flotrack.controller;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;

import com.portfolio.flotrack.FlotrackApplication;
import com.portfolio.flotrack.model.ExpenseCategory;
import com.portfolio.flotrack.model.IncomeCategory;
import com.portfolio.flotrack.model.Transaction;
import com.portfolio.flotrack.model.User;
import com.portfolio.flotrack.repository.ExpenseCategoryRepository;
import com.portfolio.flotrack.repository.IncomeCategoryRepository;
import com.portfolio.flotrack.repository.TransactionRepository;
import com.portfolio.flotrack.repository.UserRepository;
import com.portfolio.flotrack.service.CustomUserDetailsService;

import jakarta.transaction.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = FlotrackApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@Transactional
public class TransactionControllerIntegrationTest {
	@Autowired
	private MockMvc mvc;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private IncomeCategoryRepository incomeCategoryRepository;

	@Autowired
	private ExpenseCategoryRepository expenseCategoryRepository;

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	private IncomeCategory incomeCategory;
	private ExpenseCategory expenseCategory;
	private User user;

	private void createTransactions() {
		createExpenseAndIncomeCategories();
		createUser();
		createIncomeTransactions(user);
		createExpenseTransactions(user);
	}

	private User createUser() {
		user = new User();
		user.setUsername("testuser");
		user.setPassword("password");
		user.setEmail("testuser@example.com");
		userRepository.save(user);
		return user;
	}

	private void createExpenseAndIncomeCategories() {
		incomeCategory = incomeCategoryRepository.save(new IncomeCategory(null, "Test Income Category", null));
		incomeCategoryRepository.save(new IncomeCategory(null, "Test Income Category 2", null));
		expenseCategory = expenseCategoryRepository.save(new ExpenseCategory(null, "Test Expense Category", null));
		expenseCategoryRepository.save(new ExpenseCategory(null, "Test Expense Category 2", null));
	}

	private void createIncomeTransactions(User user) {
		transactionRepository.save(new Transaction(
				null,
				"Test Income Transaction 1",
				200.0,
				false,
				LocalDate.now(),
				user,
				incomeCategory,
				null));
		transactionRepository.save(new Transaction(
				null,
				"Test Income Transaction 2",
				150.0,
				false,
				LocalDate.now(),
				user,
				incomeCategory,
				null));
	}

	private void createExpenseTransactions(User user) {
		transactionRepository.save(new Transaction(
				null,
				"Test Expense Transaction 1",
				100.0,
				true,
				LocalDate.now(),
				user,
				null,
				expenseCategory));
		transactionRepository.save(new Transaction(
				null,
				"Test Expense Transaction 2",
				50.0,
				true,
				LocalDate.now(),
				user,
				null,
				expenseCategory));
	}

	private void setAuthenticatedUser() {
		UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser@example.com");
		SecurityContextHolder.getContext()
				.setAuthentication(
						new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities()));
	}

	@BeforeEach
	public void setUp() {
		createTransactions();
		setAuthenticatedUser();
	}

	@WithMockUser(username = "testuser")
	@Test
	public void whenGetTransactions_thenStatusOk() throws Exception {
		mvc.perform(get("/api/transactions/manage")
				.param("page", "0")
				.param("size", "10")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@WithMockUser(username = "testuser")
	@Test
	public void whenGetTransactions_thenStatusOk_andContentEquals() throws Exception {
		mvc.perform(get("/api/transactions/manage")
				.param("page", "0")
				.param("size", "10")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].description", is("Test Income Transaction 1")));
	}

	@WithMockUser(username = "testuser")
	@Test
	public void whenGetTransactionById_thenStatusOk() throws Exception {
		Transaction transaction = transactionRepository.findAll().get(0);
		mvc.perform(get("/api/transactions/" + transaction.getId())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(transaction.getId()));
	}

	@WithMockUser(username = "testuser")
	@Test
	public void whenCreateTransaction_thenStatusCreated() throws Exception {
		mvc.perform(post("/api/transactions/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"description\":\"New Transaction\",\"amount\":100.0,\"isExpense\":false,\"categoryId\":"
						+ incomeCategory.getId() + "}"))
				.andExpect(status().isCreated());
	}

	@WithMockUser(username = "testuser")
	@Test
	public void whenUpdateTransaction_thenStatusOk() throws Exception {
		Transaction transaction = transactionRepository.findAll().get(0);
		mvc.perform(put("/api/transactions/update/" + transaction.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"description\":\"Updated Transaction\",\"amount\":150.0,\"isExpense\":false,\"categoryId\":"
						+ incomeCategory.getId() + "}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.description", is("Updated Transaction")));
	}

	@WithMockUser(username = "testuser")
	@Test
	public void whenDeleteTransaction_thenStatusNoContent() throws Exception {
		Transaction transaction = transactionRepository.findAll().get(0);
		mvc.perform(delete("/api/transactions/delete/" + transaction.getId())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
	}

}
