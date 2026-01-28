package com.demo.keda.o11y;

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import org.springframework.http.server.observation.DefaultServerRequestObservationConvention;
import org.springframework.http.server.observation.ServerRequestObservationContext;
import org.springframework.stereotype.Component;

@Component
public class ExtendedServerRequestObservationConvention extends DefaultServerRequestObservationConvention {

    @Override
    public KeyValues getLowCardinalityKeyValues(ServerRequestObservationContext context) {
        // here, we just want to have an additional KeyValue to the observation, keeping the default values
        return super.getLowCardinalityKeyValues(context).and(custom(context));
    }

    private KeyValue custom(ServerRequestObservationContext context) {
        Integer numberAccounts = (Integer) context.getCarrier().getAttribute("numberAccounts");
        String numberAccountsStr = numberAccounts != null ? String.valueOf(numberAccounts) : "na";
        return KeyValue.of("number.accounts", numberAccountsStr);
    }
}
