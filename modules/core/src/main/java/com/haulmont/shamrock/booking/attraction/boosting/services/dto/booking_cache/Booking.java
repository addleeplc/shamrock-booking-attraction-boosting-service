/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.haulmont.bali.jackson.addapters.PrefixRemovingLongDeserializer;
import com.haulmont.bali.jackson.joda.DateTimeAdapter;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.Product;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Booking {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("pid")
    @JsonDeserialize(using = PrefixRemovingLongDeserializer.class)
    private Long pid;

    @JsonProperty("number")
    private String number;

    @JsonProperty("payment_type")
    private String paymentType;

    @JsonProperty("asap")
    private Boolean asap;

    @JsonProperty("date")
    @JsonDeserialize(using = DateTimeAdapter.Deserializer.class)
    @JsonSerialize(using = DateTimeAdapter.Serializer.class)
    private DateTime date;

    @JsonProperty("instructions")
    private List<Instruction> instructions;

    @JsonProperty("priority")
    private Integer priority;

    @JsonProperty("appearance_date")
    @JsonDeserialize(using = DateTimeAdapter.Deserializer.class)
    @JsonSerialize(using = DateTimeAdapter.Serializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private DateTime appearanceDate;

    @JsonProperty("adjustments")
    private List<Adjustment> adjustments = new ArrayList<>();

    @JsonProperty("zone_id")
    private UUID zoneId;

    @JsonProperty("product")
    private Product product;

    @JsonProperty("customer")
    private Customer customer;

    @JsonProperty("customer_reference")
    private CustomerReference customerReference;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public DateTime getAppearanceDate() {
        return appearanceDate;
    }

    public void setAppearanceDate(DateTime appearanceDate) {
        this.appearanceDate = appearanceDate;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public Boolean getAsap() {
        return asap;
    }

    public void setAsap(Boolean asap) {
        this.asap = asap;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public UUID getZoneId() {
        return zoneId;
    }

    public void setZoneId(UUID zoneId) {
        this.zoneId = zoneId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<Instruction> instructions) {
        this.instructions = instructions;
    }

    public List<Adjustment> getAdjustments() {
        return adjustments;
    }

    public void setAdjustments(List<Adjustment> adjustments) {
        this.adjustments = adjustments;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public CustomerReference getCustomerReference() {
        return customerReference;
    }

    public void setCustomerReference(CustomerReference customerReference) {
        this.customerReference = customerReference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Booking booking = (Booking) o;

        return id.equals(booking.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Booking(%s,%s)", number, id);
    }

}
