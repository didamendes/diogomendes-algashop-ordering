package com.diogomendes.algashop.ordering.domain.model.entity;

import com.diogomendes.algashop.ordering.domain.model.exception.CustomerArchivedException;
import com.diogomendes.algashop.ordering.domain.model.valueobject.*;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.CustomerId;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.diogomendes.algashop.ordering.domain.model.exception.ErrorMessages.*;
import static java.util.Objects.requireNonNull;

public class Customer implements AggregateRoot<CustomerId> {
    private CustomerId id;
    private FullName fullName;
    private BirthDate birthDate;
    private Email email;
    private Phone phone;
    private Document document;
    private Boolean promotionNotificationsAllowed;
    private Boolean archived;
    private OffsetDateTime registeredAt;
    private OffsetDateTime archivedAt;
    private LoyaltyPoints loyaltyPoints;
    private Address address;
    private Long version;

    @Builder(builderClassName = "BrandNewCustomerBuilder", builderMethodName = "brandNew")
    private static Customer createBrandNew( FullName fullName, BirthDate birthDate, Email email, Phone phone,
                                    Document document, Boolean promotionNotificationsAllowed, Address address) {
        return  new Customer(new CustomerId(),
                fullName,
                birthDate,
                email,
                phone,
                document,
                promotionNotificationsAllowed,
                false,
                OffsetDateTime.now(),
                null,
                new LoyaltyPoints(0),
                address,
                null
                );
    }

    @Builder(builderClassName = "ExistingCustomerBuilder", builderMethodName = "existing")
    private Customer(CustomerId id, FullName fullName, BirthDate birthDate, Email email,
                    Phone phone, Document document, Boolean promotionNotificationsAllowed,
                    Boolean archived, OffsetDateTime registeredAt, OffsetDateTime archivedAt,
                    LoyaltyPoints loyaltyPoints, Address address, Long version) {
        this.setId(id);
        this.setFullName(fullName);
        this.setBirthDate(birthDate);
        this.setEmail(email);
        this.setPhone(phone);
        this.setDocument(document);
        this.setPromotionNotificationsAllowed(promotionNotificationsAllowed);
        this.setArchived(archived);
        this.setRegisteredAt(registeredAt);
        this.setArchivedAt(archivedAt);
        this.setLoyaltyPoints(loyaltyPoints);
        this.setAddress(address);
        this.setVersion(version);
    }

    public void addLoyaltyPoints(LoyaltyPoints loayltyPointsAdded) {
        verifyIfChangeable();
        this.setLoyaltyPoints(this.loyaltyPoints().add(loayltyPointsAdded));
    }

    public void archive() {
        verifyIfChangeable();
        this.setArchived(true);
        this.setArchivedAt(OffsetDateTime.now());
        this.setFullName(new FullName("Anonymous", "Anonymous"));
        this.setPhone(new Phone("000-000-0000"));
        this.setDocument(new Document("000-00-0000"));
        this.setEmail(new Email(UUID.randomUUID() + "@anonymous.com"));
        this.setBirthDate(null);
        this.setPromotionNotificationsAllowed(false);
        this.setAddress(this.address.toBuilder().number("Anonymized").complement(null).build());
    }

    public void enablePromotionNotifications() {
        verifyIfChangeable();
        this.setPromotionNotificationsAllowed(true);
    }

    public void disablePromotionNotifications() {
        verifyIfChangeable();
        this.setPromotionNotificationsAllowed(false);
    }

    public void changeName(FullName fullName) {
        verifyIfChangeable();
        this.setFullName(fullName);
    }

    public void changeEmail(Email email) {
        verifyIfChangeable();
        this.setEmail(email);
    }

    public void changePhone(Phone phone) {
        verifyIfChangeable();
        this.setPhone(phone);
    }

    public void changeAddress(Address address) {
        verifyIfChangeable();
        this.setAddress(address);
    }

    public CustomerId id() {
        return id;
    }

    public FullName fullName() {
        return fullName;
    }

    public BirthDate birthDate() {
        return birthDate;
    }

    public Email email() {
        return email;
    }

    public Phone phone() {
        return phone;
    }

    public Document document() {
        return document;
    }

    public Boolean promotionNotificationsAllowed() {
        return promotionNotificationsAllowed;
    }

    public Boolean archived() {
        return archived;
    }

    public OffsetDateTime registeredAt() {
        return registeredAt;
    }

    public OffsetDateTime archivedAt() {
        return archivedAt;
    }

    public LoyaltyPoints loyaltyPoints() {
        return loyaltyPoints;
    }

    public Address address() {
        return address;
    }

    public Long version() {
        return version;
    }

    private void setId(CustomerId id) {
        requireNonNull(id);
        this.id = id;
    }

    private void setFullName(FullName fullName) {
        requireNonNull(fullName, VALIDATION_ERROR_FULLNAME_IS_NULL);
        this.fullName = fullName;
    }

    private void setBirthDate(BirthDate birthDate) {
        this.birthDate = birthDate;
    }

    private void setEmail(Email email) {
        this.email = email;
    }

    private void setPhone(Phone phone) {
        this.phone = phone;
    }

    private void setDocument(Document document) {
        this.document = document;
    }

    private void setPromotionNotificationsAllowed(Boolean promotionNotificationsAllowed) {
        requireNonNull(promotionNotificationsAllowed);
        this.promotionNotificationsAllowed = promotionNotificationsAllowed;
    }

    private void setArchived(Boolean archived) {
        requireNonNull(archived);
        this.archived = archived;
    }

    private void setRegisteredAt(OffsetDateTime registeredAt) {
        requireNonNull(registeredAt);
        this.registeredAt = registeredAt;
    }

    private void setArchivedAt(OffsetDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    private void setLoyaltyPoints(LoyaltyPoints loyaltyPoints) {
        requireNonNull(loyaltyPoints);
        this.loyaltyPoints = loyaltyPoints;
    }

    private void setAddress(Address address) {
        requireNonNull(address);
        this.address = address;
    }

    private void verifyIfChangeable() {
        if (this.archived()) {
            throw new CustomerArchivedException();
        }
    }

    private void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
