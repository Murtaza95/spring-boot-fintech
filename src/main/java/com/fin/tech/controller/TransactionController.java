package com.fin.tech.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fin.tech.command.JwtResponse;
import com.fin.tech.command.TransactionCommand;
import com.fin.tech.exception.ExceptionHandler;
import com.fin.tech.service.TransactionService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 
 * @author Murtaza Gillani
 *
 */
@RestController
@RequestMapping("/transactions")
@Api(tags = "Transacrions API")
public class TransactionController {
	@Autowired
	private TransactionService transactionService;

	/**
	 * This endpoint shall initiate the transaction process.t.
	 * 
	 * @param cmd
	 * @return
	 */
	@PostMapping("/transfer")
	@ApiOperation(value = "Transfer User Amount", notes = "This endpoint shall handle the user's transactrion process. In case of success, the amount is successfully transfered."
			+ "This method validates the user balance. If the belanace is insufficient then InsufficientBalanceException is thrown. "
			+ "This method validates the amount wheter the correct amount is provied by the user. In case of invalid a,ount (<0) then InvalidAmountException is thrown."
			+ "After validating the above two, if any other exception occurred like the credit user does not exist then the TransactionException"
			+ "is thrown and the transaction is reverted.   ")
	@ApiResponses(value = {

			@ApiResponse(code = 200, message = "Fund Successfully Transfered."),
			@ApiResponse(code = 406, message = "Invalid amount."),
			@ApiResponse(code = 406, message = "Insufficient balance"),
			@ApiResponse(code = 500, message = "There is an error while doing the transaction. Your transaction is reverted."), })
	public ResponseEntity<?> transferAmount(@RequestBody TransactionCommand cmd) {
		try {
			if (cmd.getFromEmail() == null || cmd.getFromEmail().isEmpty() || cmd.getToEmail() == null
					|| cmd.getToEmail().isEmpty() || cmd.getAmount() == null) {
				return ResponseEntity.badRequest().body("From Email, To Email and Transfer amount is mandatory.");
			}

			transactionService.initiateTransaction(cmd);
		} catch (Exception e) {
			Map<String, Object> errorResponse = ExceptionHandler.buildErrorResponse(e);
			return ResponseEntity.status((int) errorResponse.get("status")).body(errorResponse);

		}

		return ResponseEntity.ok("Fund Successfully Transfered.");

	}

}
