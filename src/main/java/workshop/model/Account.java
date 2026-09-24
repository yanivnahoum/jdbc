package workshop.model;

import java.math.BigDecimal;

public record Account(long id, long ownerId, BigDecimal balance) {
}
