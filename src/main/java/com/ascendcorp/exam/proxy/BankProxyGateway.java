package com.ascendcorp.exam.proxy;

import com.ascendcorp.exam.model.TransferRequest;
import com.ascendcorp.exam.model.TransferResponse;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class BankProxyGateway {

    static final Logger log = Logger.getLogger(BankProxyGateway.class);

    public TransferResponse requestTransfer(
            String transactionId, Date tranDateTime, String channel,
            String bankCode, String bankNumber, double amount,
            String reference1, String reference2) {

        return new TransferResponse();
    }

    public TransferResponse requestTransfer(TransferRequest transferRequest) {

        return new TransferResponse();
    }
}

