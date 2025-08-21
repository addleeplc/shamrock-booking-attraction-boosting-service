/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.boost_applier;

import com.haulmont.monaco.sql.SqlRepository;
import com.haulmont.monaco.sql2o.Sql2oCommand;
import com.haulmont.shamrock.booking.attraction.boosting.config.ServiceConfiguration;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.Booking;
import com.haulmont.shamrock.booking.attraction.boosting.util.HostnameUtil;
import org.haulmont.sql2o.Connection;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

@Component
public class LiveBookingBoostApplierService implements BookingBoostApplier {

    @Inject
    private Logger log;

    @Inject
    private SqlRepository sqlRepository;

    @Inject
    private ServiceConfiguration configuration;

    /**
     * CALL DIRECTLY ONLY IF you are SURE you do want to APPLY BOOST DESPITE the working MODE,
     * IF NOT use {@link BookingBoostApplierService#apply(Double, Booking)}
     */
    public void apply(Double amount, Booking booking) {
        new UpdateBoostCommand(
                booking.getPid(),
                configuration.getBoostExtraCode(),
                configuration.getBoostExecutionLogCreatedBy(),
                amount,
                HostnameUtil.getHostname(), //todo check what we use in gps consumers for example
                configuration.getBoostInstructionCode()
        ).execute();
        //todo why different logs in Live and Dummy provider?
        log.info("Update boost for booking {}. Amount: {}", booking, amount);
    }

    private class UpdateBoostCommand extends Sql2oCommand<Void> {
        private final Long bookingId;
        private final String extraCode;
        private final Integer createdBy;
        private final Double amount;
        private final String host;
        private final String siCode;

        private UpdateBoostCommand(Long bookingId,
                                  String extraCode,
                                  Integer createdBy,
                                  Double amount,
                                  String host,
                                  String siCode) {
            super("shamrock-ds");
            this.bookingId = bookingId;
            this.extraCode = extraCode;
            this.createdBy = createdBy;
            this.amount = amount;
            this.host = host;
            this.siCode = siCode;
        }

        @Override
        protected Void  __execute(Connection connection) {
            //todo string replace is not best approach
            connection.createQuery(sqlRepository.loadSql(getName() + ".sql"))
                    .addParameter("siCode", siCode)
                    .addParameter("bookingId", bookingId)
                    .addParameter("extraCode", extraCode)
                    .addParameter("createdBy", createdBy)
                    .addParameter("amount", amount)
                    .addParameter("host", host)
                    .executeUpdate();

            return null;
        }

        @Override
        protected String getName() {
            return "batchAddDriverExtrasAndExecutionLog";
        }
    }
}
