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
import java.time.LocalDate;
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
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private AvailableDateStatus status;

    protected void setAccommodation(Accommodation accommodation) {
        this.accommodation = accommodation;
    }

    @Builder
    public AvailableDate(LocalDate startDate, LocalDate endDate, AvailableDateStatus status) {
        validateDateRange(startDate, endDate);
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public void updateDate(LocalDate startDate, LocalDate endDate, AvailableDateStatus status) {
        validateDateRange(startDate, endDate);
        this.startDate = startDate;
        this.endDate = endDate;
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
        return (newDate.getStartDate().isBefore(this.getEndDate()) &&
                newDate.getEndDate().isAfter(this.getStartDate())) ||

                (newDate.getStartDate().equals(this.getStartDate()) ||
                        newDate.getEndDate().equals(this.getEndDate())) ||

                (newDate.getStartDate().isBefore(this.getStartDate()) &&
                        newDate.getEndDate().isAfter(this.getEndDate()));
    }
}
