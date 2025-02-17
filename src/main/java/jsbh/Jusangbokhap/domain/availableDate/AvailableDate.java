package jsbh.Jusangbokhap.domain.availableDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import jakarta.persistence.Version;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import jsbh.Jusangbokhap.api.availableDate.exception.AvailableDateCustomException;
import jsbh.Jusangbokhap.api.availableDate.exception.AvailableDateErrorCode;
import jsbh.Jusangbokhap.domain.accommodation.Accommodation;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AvailableDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    @Column(nullable = false)
    private LocalDate checkin;

    @Column(nullable = false)
    private LocalDate checkout;

    @Enumerated(EnumType.STRING)
    private AvailableDateStatus status;

    @Version
    private Long version;

    protected void setAccommodation(Accommodation accommodation) {
        this.accommodation = accommodation;
    }

    @Builder
    public AvailableDate(LocalDate checkin, LocalDate checkout, AvailableDateStatus status) {
        validateDateRange(checkin, checkout);
        this.checkin = checkin;
        this.checkout = checkout;
        this.status = status;
    }

    public AvailableDate matchDates(LocalDate checkin, LocalDate checkout) {
        if (this.checkin.equals(checkin) && this.checkout.equals(checkout)) {
            return this;
        }
        return null;
    }

    public boolean isAvailable() {
        return this.status == AvailableDateStatus.AVAILABLE;
    }

    public void updateStatus() {
        this.status = AvailableDateStatus.BOOKED;
    }

    public void updateDate(LocalDate startDate, LocalDate endDate, AvailableDateStatus status) {
        validateDateRange(startDate, endDate);
        this.checkin = startDate;
        this.checkout = endDate;
        this.status = status;
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {

        if (startDate == null || endDate == null) {
            throw new AvailableDateCustomException(AvailableDateErrorCode.MISSING_DATE);
        }

        if (startDate.isEqual(endDate)) {
            throw new AvailableDateCustomException(AvailableDateErrorCode.SAME_START_END);

        }

        if (startDate.isAfter(endDate)) {
            throw new AvailableDateCustomException(AvailableDateErrorCode.START_AFTER_END);
        }
    }

    public boolean isOverlappingWith(AvailableDate newDate) {
        return (newDate.getCheckin().isBefore(this.getCheckout()) &&
                newDate.getCheckout().isAfter(this.getCheckin())) ||

                (newDate.getCheckin().equals(this.getCheckin()) ||
                        newDate.getCheckout().equals(this.getCheckout())) ||

                (newDate.getCheckin().isBefore(this.getCheckin()) &&
                        newDate.getCheckout().isAfter(this.getCheckout()));
    }
}