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

    public void setAccommodation(Accommodation accommodation) {
        this.accommodation = accommodation;
    }

    @Builder
    public AvailableDate(LocalDate startDate, LocalDate endDate, AvailableDateStatus status) {
        validateDate(startDate, endDate);
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    private void validateDate(LocalDate startDate, LocalDate endDate) {

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("날짜 값은 필수입니다.");
        }

        if (startDate.isEqual(endDate)) {
            throw new IllegalArgumentException("시작일과 종료일은 같을 수 없습니다.");

        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작 날짜는 종료 날짜보다 이후일 수 없습니다.");
        }
    }
}
