package io.simondev.demoinflearnrestapi.events;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Component
public class EventValidator {

    public void validate(EventDto eventDto, Errors errors) {
        // Base Price가 Max Price보다 큰데, 무제한 경매가 아닌 경우
        if (eventDto.getBasePrice() > eventDto.getMaxPrice()
            && eventDto.getMaxPrice() != 0) {
            errors.rejectValue("basePrice", "wrongValue", "BasePrice is wrong.");
            errors.rejectValue("maxPrice", "wrongValue", "MaxPrice is wrong.");
            errors.reject("wrongPrices", "Prices are wrong");
        }

        // 날짜 설정이 잘못된 경우
        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if (endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
            endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
            endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())) {
            errors.rejectValue("endEventDateTime", "wrongValue", "endEventDateTime is wrong");
        }

        //TODO beginEventDateTime
        //TODO closeEnrollmentDateTime
    }
}
