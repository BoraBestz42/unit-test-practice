package com.ascendcorp.exam.service;

import com.ascendcorp.exam.model.InquiryServiceResultDTO;
import com.ascendcorp.exam.model.TransferRequest;
import com.ascendcorp.exam.model.TransferResponse;
import com.ascendcorp.exam.proxy.BankProxyGateway;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.web.server.WebServerException;

import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InquiryServiceTest {

    @InjectMocks
    private InquiryService inquiryService;

    @Mock
    private BankProxyGateway bankProxyGateway;

    private TransferRequest createTransferRequest() {
        TransferRequest request = new TransferRequest();
        request.setTransactionId("1234");
        request.setTranDateTime(new Date());
        request.setChannel("Mobile");
        request.setLocationCode(null);
        request.setBankCode("BANK1");
        request.setBankNumber("4321000");
        request.setAmount(100d);
        request.setReference1("ref1");
        request.setReference2("ref2");
        request.setFirstName("test");
        request.setLastName("data");
       return request;
    }
    @Test
    public void should_return200_when_bankApproved() {
        TransferRequest request = createTransferRequest();
        TransferResponse response = new TransferResponse();
        response.setResponseCode("approved");
        response.setDescription("approved");

        when(bankProxyGateway.requestTransfer(any(TransferRequest.class))).thenReturn(response);

        InquiryServiceResultDTO inquiry = inquiryService.inquiry(request);

        assertNotNull(inquiry);
        assertEquals("200", inquiry.getReasonCode());
        assertEquals("approved", inquiry.getReasonDesc());
    }
    
    @Test
    public void should_return500_when_noRequireValue() {
        // transactionId
        TransferRequest request = createTransferRequest();
        request.setTransactionId(null);
        InquiryServiceResultDTO inquiry = inquiryService.inquiry(request);
        assertNotNull(inquiry);
        assertEquals("500", inquiry.getReasonCode());
        assertEquals("General Invalid Data", inquiry.getReasonDesc());
        
        // dateTime
        request = createTransferRequest();
        request.setTranDateTime(null);
        inquiry = inquiryService.inquiry(request);
        assertNotNull(inquiry);
        assertEquals("500", inquiry.getReasonCode());
        assertEquals("General Invalid Data", inquiry.getReasonDesc());
        
        // Channel
        request = createTransferRequest();
        request.setChannel(null);
        inquiry = inquiryService.inquiry(request);
        assertNotNull(inquiry);
        assertEquals("500", inquiry.getReasonCode());
        assertEquals("General Invalid Data", inquiry.getReasonDesc());

        // BankCode
        request = createTransferRequest();
        request.setBankCode(null);
        inquiry = inquiryService.inquiry(request);
        assertNotNull(inquiry);
        assertEquals("500", inquiry.getReasonCode());
        assertEquals("General Invalid Data", inquiry.getReasonDesc());

        // BankNumber
        request = createTransferRequest();
        request.setBankNumber(null);
        inquiry = inquiryService.inquiry(request);
        assertNotNull(inquiry);
        assertEquals("500", inquiry.getReasonCode());
        assertEquals("General Invalid Data", inquiry.getReasonDesc());

        // Amount
        request = createTransferRequest();
        request.setAmount(0d);
        inquiry = inquiryService.inquiry(request);
        assertNotNull(inquiry);
        assertEquals("500", inquiry.getReasonCode());
        assertEquals("General Invalid Data", inquiry.getReasonDesc());
    }

    @Test
    public void should_return400_when_invalidDataWithoutDesc() {
        TransferRequest request = createTransferRequest();
        TransferResponse response = new TransferResponse();
        response.setResponseCode("invalid_data");

        when(bankProxyGateway.requestTransfer(any(TransferRequest.class))).thenReturn(response);

        InquiryServiceResultDTO inquiry = inquiryService.inquiry(request);

        assertNotNull(inquiry);
        assertEquals("400", inquiry.getReasonCode());
        assertEquals("General Invalid Data", inquiry.getReasonDesc());
    }
    
    @Test
    public void should_return1091WithReasonDesc_when_invalidDataWithDescAndCode() {
        TransferRequest request = createTransferRequest();
        TransferResponse response = new TransferResponse();
        response.setResponseCode("invalid_data");
        response.setDescription("100:1091:Data type is invalid.");

        when(bankProxyGateway.requestTransfer(any(TransferRequest.class))).thenReturn(response);

        InquiryServiceResultDTO inquiry = inquiryService.inquiry(request);

        assertNotNull(inquiry);
        assertEquals("1091", inquiry.getReasonCode());
        assertEquals("Data type is invalid.", inquiry.getReasonDesc());
    }

    @Test
    public void should_return400_when_invalidDataWithDesc() {
        TransferRequest request = createTransferRequest();
        TransferResponse response = new TransferResponse();
        response.setResponseCode("invalid_data");
        response.setDescription("General error.");

        when(bankProxyGateway.requestTransfer(any(TransferRequest.class))).thenReturn(response);

        InquiryServiceResultDTO inquiry = inquiryService.inquiry(request);

        assertNotNull(inquiry);
        assertEquals("400", inquiry.getReasonCode());
        assertEquals("General Invalid Data", inquiry.getReasonDesc());
    }

    @Test
    public void should_return400_when_errorAndDescIsNull()  {
        TransferRequest request = createTransferRequest();
        TransferResponse response = new TransferResponse();
        response.setDescription(null);

        when(bankProxyGateway.requestTransfer(any(TransferRequest.class))).thenReturn(response);

        InquiryServiceResultDTO inquiry = inquiryService.inquiry(request);

        assertNotNull(inquiry);
        assertEquals("400", inquiry.getReasonCode());
        assertEquals("General Invalid Data", inquiry.getReasonDesc());
    }

    @Test
    public void should_return400_when_errorAndNoDescCode() {
        TransferRequest request = createTransferRequest();
        TransferResponse response = new TransferResponse();
        response.setResponseCode("transaction_error");
        response.setDescription("Transaction error.");

        when(bankProxyGateway.requestTransfer(any(TransferRequest.class))).thenReturn(response);

        InquiryServiceResultDTO inquiry = inquiryService.inquiry(request);

        assertEquals("400", inquiry.getReasonCode());
        assertEquals("General Transaction error.", inquiry.getReasonDesc());

    }
    @Test
    public void should_return1091_when_errorAndDesc3Code() {
        TransferRequest request = createTransferRequest();
        TransferResponse response = new TransferResponse();
        response.setResponseCode("transaction_error");
        response.setDescription("100:1091:Transaction is error with code 1091.");

        when(bankProxyGateway.requestTransfer(any(TransferRequest.class))).thenReturn(response);

        InquiryServiceResultDTO inquiry = inquiryService.inquiry(request);

        assertEquals("1091", inquiry.getReasonCode());
        assertEquals("Transaction is error with code 1091.", inquiry.getReasonDesc());
    }

    @Test
    public void should_return1092_when_errorAndDesc2Code()  {
        TransferRequest request = createTransferRequest();
        TransferResponse response = new TransferResponse();
        response.setResponseCode("transaction_error");
        response.setDescription("1092:Transaction is error with code 1092.");

        when(bankProxyGateway.requestTransfer(any(TransferRequest.class))).thenReturn(response);

        InquiryServiceResultDTO inquiry = inquiryService.inquiry(request);

        assertEquals("1092", inquiry.getReasonCode());
        assertEquals("Transaction is error with code 1092.", inquiry.getReasonDesc());
    }
    @Test
    public void should_return98_when_errorAndDescCode98() {
        TransferRequest request = createTransferRequest();
        TransferResponse response = new TransferResponse();
        response.setResponseCode("transaction_error");
        response.setDescription("98:Transaction is error with code 98.");

        when(bankProxyGateway.requestTransfer(any(TransferRequest.class))).thenReturn(response);

        InquiryServiceResultDTO inquiry = inquiryService.inquiry(request);

        assertEquals("98", inquiry.getReasonCode());
        assertEquals("Transaction is error with code 98.", inquiry.getReasonDesc());
    }

    @Test
    public void should_return501_when_unknownAndWithoutDesc() {
        TransferRequest request = createTransferRequest();
        TransferResponse response = new TransferResponse();
        response.setResponseCode("unknown");

        when(bankProxyGateway.requestTransfer(any(TransferRequest.class))).thenReturn(response);

        InquiryServiceResultDTO inquiry = inquiryService.inquiry(request);

        assertEquals("501", inquiry.getReasonCode());
        assertEquals("General Invalid Data", inquiry.getReasonDesc());
    }

    @Test
    public void should_return501_when_unknownAndDesc()  {
        TransferRequest request = createTransferRequest();
        TransferResponse response = new TransferResponse();
        response.setResponseCode("unknown");
        response.setDescription("5001:Unknown error code 5001");

        when(bankProxyGateway.requestTransfer(any(TransferRequest.class))).thenReturn(response);

        InquiryServiceResultDTO inquiry = inquiryService.inquiry(request);

        assertEquals("5001", inquiry.getReasonCode());
        assertEquals("Unknown error code 5001", inquiry.getReasonDesc());
    }

    @Test
    public void should_return501_when_unknownAndEmptyDesc() {
        TransferRequest request = createTransferRequest();
        TransferResponse response = new TransferResponse();
        response.setResponseCode("unknown");
        response.setDescription("5002: ");

        when(bankProxyGateway.requestTransfer(any(TransferRequest.class))).thenReturn(response);

        InquiryServiceResultDTO inquiry = inquiryService.inquiry(request);
        assertEquals("5002", inquiry.getReasonCode());
        assertEquals("General Invalid Data", inquiry.getReasonDesc());
    }

    @Test
    public void should_return501_when_unknownAndTextDesc() {
        TransferRequest request = createTransferRequest();
        TransferResponse response = new TransferResponse();
        response.setResponseCode("unknown");
        response.setDescription("General Invalid Data code 501");

        when(bankProxyGateway.requestTransfer(any(TransferRequest.class))).thenReturn(response);

        InquiryServiceResultDTO inquiry = inquiryService.inquiry(request);
        assertEquals("501", inquiry.getReasonCode());
        assertEquals("General Invalid Data", inquiry.getReasonDesc());
    }
    @Test
    public void should_return504_when_errorDescNotSupport() {
        TransferRequest request = createTransferRequest();
        TransferResponse response = new TransferResponse();
        response.setResponseCode("not_support");
        response.setDescription("Not support");

        when(bankProxyGateway.requestTransfer(any(TransferRequest.class))).thenReturn(response);

        InquiryServiceResultDTO inquiry = inquiryService.inquiry(request);

        assertEquals("504", inquiry.getReasonCode());
        assertEquals("Internal Application Error", inquiry.getReasonDesc());
    }

    @Test
    public void should_return504_when_responseNull() {
        TransferRequest request = createTransferRequest();
        when(bankProxyGateway.requestTransfer(any(TransferRequest.class))).thenReturn(null);

        InquiryServiceResultDTO inquiry = inquiryService.inquiry(request);

        assertEquals("504", inquiry.getReasonCode());
        assertEquals("Internal Application Error", inquiry.getReasonDesc());
    }

    @Test
    public void should_return504_when_throwWebServiceException() {
        TransferRequest request = createTransferRequest();

        when(bankProxyGateway.requestTransfer(any(TransferRequest.class))).thenThrow(WebServerException.class);

        InquiryServiceResultDTO inquiry = inquiryService.inquiry(request);

        assertNotNull(inquiry);
        assertEquals("504", inquiry.getReasonCode());
        assertEquals("Internal Application Error", inquiry.getReasonDesc());
    }


    @Test
    public void should_return503_when_socketTimeout() {

        TransferRequest request = createTransferRequest();

        WebServerException ex = new WebServerException("java.net.SocketTimeoutException error", null);
        when(bankProxyGateway.requestTransfer(any(TransferRequest.class))).thenThrow(ex);

        InquiryServiceResultDTO inquiry = inquiryService.inquiry(request);

        assertNotNull(inquiry);
        assertEquals("503", inquiry.getReasonCode());
        assertEquals("Error timeout", inquiry.getReasonDesc());
    }

    @Test
    public void should_return503_when_connectionTimeout() {

        TransferRequest request = createTransferRequest();

        WebServerException ex = new WebServerException("Server Connection timed out", null);
        when(bankProxyGateway.requestTransfer(any(TransferRequest.class))).thenThrow(ex);

        InquiryServiceResultDTO inquiry = inquiryService.inquiry(request);

        assertNotNull(inquiry);
        assertEquals("503", inquiry.getReasonCode());
        assertEquals("Error timeout", inquiry.getReasonDesc());
    }
}
