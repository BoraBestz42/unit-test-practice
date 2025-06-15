package com.ascendcorp.exam.service;

import com.ascendcorp.exam.exception.*;
import com.ascendcorp.exam.model.InquiryServiceResultDTO;
import com.ascendcorp.exam.model.TransferRequest;
import com.ascendcorp.exam.model.TransferResponse;
import com.ascendcorp.exam.proxy.BankProxyGateway;
import org.apache.log4j.Logger;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Supplier;

@Service
public class InquiryService {

    private final BankProxyGateway bankProxyGateway;
    static final Logger log = Logger.getLogger(InquiryService.class);

    public static final String GENERAL_INVALID_DATA = "General Invalid Data";

    public static final String GENERAL_TRANSACTION_ERROR = "General Transaction error.";

    public InquiryService(BankProxyGateway bankProxyGateway) {
        this.bankProxyGateway = bankProxyGateway;
    }

    public InquiryServiceResultDTO inquiry(TransferRequest transferRequest) {
        InquiryServiceResultDTO respDTO;
        try{
            validateInquiry(transferRequest);
            TransferResponse response = bankProxyGateway.requestTransfer(transferRequest);
            respDTO = mapBankTransfer(response);
        } catch (DataTypeInvalidException ex) {
            respDTO = new InquiryServiceResultDTO();
            respDTO.setReasonCode("1091");
            respDTO.setReasonDesc("Data type is invalid.");
        } catch (GeneralInvalidDataException ex) {
            respDTO = new InquiryServiceResultDTO();
            String code = ex.getCode();
            if (code.equalsIgnoreCase("400")){
                respDTO.setReasonCode("400");
                respDTO.setReasonDesc(GENERAL_INVALID_DATA);
            } else if (code.equalsIgnoreCase("500")){
                respDTO.setReasonCode("500");
                respDTO.setReasonDesc(GENERAL_INVALID_DATA);
            } else if (code.equalsIgnoreCase("501")){
                respDTO.setReasonCode("501");
                respDTO.setReasonDesc(GENERAL_INVALID_DATA);
            } else if (code.equalsIgnoreCase("5001")){
                respDTO.setReasonCode("5001");
                respDTO.setReasonDesc("Unknown error code 5001");
            } else if (code.equalsIgnoreCase("5002")){
                respDTO.setReasonCode("5002");
                respDTO.setReasonDesc(GENERAL_INVALID_DATA);
            }
        } catch (TransactionErrorException ex) {
            respDTO = new InquiryServiceResultDTO();
            String code = ex.getCode();
            if (code.equalsIgnoreCase("98") ||
                code.equalsIgnoreCase("1091") ||
                code.equalsIgnoreCase("1092")){
                respDTO.setReasonCode(ex.getCode());
                respDTO.setReasonDesc(ex.getMessage());
            } else {
                respDTO.setReasonCode("400");
                respDTO.setReasonDesc(GENERAL_TRANSACTION_ERROR);
            }
        } catch(WebServerException ex) {
            String faultString = ex.getMessage();
            respDTO = new InquiryServiceResultDTO();
            if(faultString != null && faultString.indexOf("java.net.SocketTimeoutException") > -1) {
                // bank socket timeout
                respDTO.setReasonCode(ReasonCode.SOCKET_TIMEOUT.code);
                respDTO.setReasonDesc(ReasonCode.SOCKET_TIMEOUT.getDescription());
            } else if (faultString != null && faultString.indexOf("Connection timed out") > -1) {
                // bank connection timeout
                respDTO.setReasonCode(ReasonCode.CONNECTION_TIMEOUT.getCode());
                respDTO.setReasonDesc(ReasonCode.CONNECTION_TIMEOUT.getDescription());
            } else {
                // bank general error
                respDTO.setReasonCode(ReasonCode.INTERNAL_ERROR.getCode());
                respDTO.setReasonDesc(ReasonCode.INTERNAL_ERROR.description);
            }
        } catch (InternalServerErrorException ex) {
            respDTO = new InquiryServiceResultDTO();
            respDTO.setReasonCode(ReasonCode.INTERNAL_ERROR.getCode());
            respDTO.setReasonDesc(ReasonCode.INTERNAL_ERROR.description);
        } catch (IllegalStateException ex){
            throw new IllegalStateException("Unsupported Error Reason Code");
        }

        return respDTO;
    }

    public void validateInquiry(TransferRequest transferRequest) throws GeneralInvalidDataException {
        Map<String, Supplier<Object>> validateField = Map.of(
                "transactionId", transferRequest::getTransactionId,
                "tranDateTime", transferRequest::getTranDateTime,
                "channel", transferRequest::getChannel,
                "bankCode", transferRequest::getBankCode,
                "bankNumber", transferRequest::getBankNumber,
                "amount", transferRequest::getAmount
        );
        log.info("Validate transferRequest");
        validateField.forEach((fieldName, getValue) -> {
            Object value = getValue.get();
            if (value == null) {
                log.info(fieldName + " is Required!");
                throw new GeneralInvalidDataException("500", fieldName + " cannot be null");
            } else if (value instanceof Double) {
                Double doubleValue = (Double) value;
                if (doubleValue < 1) {
                    log.info(fieldName + " must more than                                                                                                                                                       a zero!");
                    throw new GeneralInvalidDataException("500", fieldName + " must more than zero");
                }
            }
        });
        log.info("Validate transferRequest Success");
    }

    public InquiryServiceResultDTO mapBankTransfer(TransferResponse transferResponse) {
        if (transferResponse != null) {
            String responseCode = transferResponse.getResponseCode();
            if (responseCode == null){
                throw new GeneralInvalidDataException("400", GENERAL_INVALID_DATA);
            } else {
                if (responseCode.equalsIgnoreCase("approved")) {
                    return approved(transferResponse);
                } else if (responseCode.equalsIgnoreCase("invalid_data")) {
                    invalidData(transferResponse);
                } else if (responseCode.equalsIgnoreCase("transaction_error")) {
                    transactionError(transferResponse);
                } else if (responseCode.equalsIgnoreCase("unknown")) {
                    unknownCode(transferResponse);
                } else if (responseCode.equalsIgnoreCase("not_support")) {
                    throw new InternalServerErrorException("504", "Internal Application Error");
                } else {
                    throw new IllegalStateException("Unhandled response code: " + responseCode);
                }
            }
        } else {
            throw new InternalServerErrorException("504", "Internal Application Error");
        }
        throw new IllegalStateException("Unexpected error");
    }

    public InquiryServiceResultDTO approved (TransferResponse transferResponse){
        InquiryServiceResultDTO resultDTO = new InquiryServiceResultDTO();
        resultDTO.setRef_no1(transferResponse.getReferenceCode1());
        resultDTO.setRef_no2(transferResponse.getReferenceCode2());
        resultDTO.setAmount(transferResponse.getBalance());
        resultDTO.setTranID(transferResponse.getBankTransactionID());
        resultDTO.setReasonCode("200");
        resultDTO.setReasonDesc(transferResponse.getDescription());
        log.info("Mapping response success");
        return resultDTO;
    }

    public void invalidData (TransferResponse transferResponse){
        String replyDesc = transferResponse.getDescription();
        if (replyDesc != null) {
            String[] respDesc = replyDesc.split(":");
            if (respDesc != null && respDesc.length >= 3) {
                // bank description full format
                String reasonCode = respDesc[1];
                String description = respDesc[2];
                if (reasonCode.equalsIgnoreCase("1091")) {
                    throw new DataTypeInvalidException(reasonCode, description);
                }
            } else {
                // bank description short format
                throw new GeneralInvalidDataException("400", GENERAL_INVALID_DATA);
            }
        } else {
            throw new GeneralInvalidDataException("400", GENERAL_INVALID_DATA);
        }
    }

    public void transactionError (TransferResponse transferResponse){
        // bank response code = transaction_error
        String replyDesc = transferResponse.getDescription();
        if (replyDesc != null) {
            String[] respDesc = replyDesc.split(":");
            if (respDesc != null && respDesc.length == 2) {
                String reasonCode = respDesc[0];
                String description = respDesc[1];
                if (reasonCode.equalsIgnoreCase("98") || reasonCode.equalsIgnoreCase("1092")) {
                    // bank code 98 or 1092
                    throw new TransactionErrorException(reasonCode, description);
                }
            } else if (respDesc != null &&respDesc.length == 3) {
                String reasonCode = respDesc[1];
                String description = respDesc[2];
                if (reasonCode.equalsIgnoreCase("1091")) {
                    throw new TransactionErrorException(reasonCode, description);
                }
                // bank description full format
            } else {
                // bank description incorrect format
                throw new TransactionErrorException("400", GENERAL_TRANSACTION_ERROR);
            }
        } else {
            // bank no description
            throw new TransactionErrorException("400", GENERAL_TRANSACTION_ERROR);
        }
    }
    public void unknownCode(TransferResponse transferResponse) {
        String replyDesc = transferResponse.getDescription();
        if (replyDesc != null) {
            String[] respDesc = replyDesc.split(":");
            if (respDesc != null && respDesc.length >= 2) {
                // bank description full format
                String reasonCode = respDesc[0];
                String description = respDesc[1];
                if (description == null || !reasonCode.equalsIgnoreCase("5002") && description.trim().isEmpty()) {
                    throw new GeneralInvalidDataException("501", GENERAL_INVALID_DATA);
                } else if (reasonCode.equalsIgnoreCase("5001")){
                    throw new GeneralInvalidDataException(reasonCode, description);
                } else if (reasonCode.equalsIgnoreCase("5002") && description.trim().isEmpty()){
                    throw new GeneralInvalidDataException(reasonCode, GENERAL_INVALID_DATA);
                }
            } else {
                // bank description short format
                throw new GeneralInvalidDataException("501", GENERAL_INVALID_DATA);
            }
        } else {
            // bank no description
            throw new GeneralInvalidDataException("501", GENERAL_INVALID_DATA);
        }
    }

    public enum ReasonCode {
        CONNECTION_TIMEOUT("503", "Error timeout"),
        SOCKET_TIMEOUT("503", "Error timeout"),
        INTERNAL_ERROR("504", "Internal Application Error"),

        ;
        private final String code;
        private final String description;

        ReasonCode(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }
}

