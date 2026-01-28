package com.demo.keda.vo;

import java.util.List;

public record CustomerAccounts(Long customerId, List<AccountDetails> accounts) {
}
