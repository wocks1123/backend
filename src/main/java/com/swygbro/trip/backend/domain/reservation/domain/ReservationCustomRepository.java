package com.swygbro.trip.backend.domain.reservation.domain;

import java.util.List;

public interface ReservationCustomRepository {
    public List<Reservation> findPastReservationsByClientId(Long clientId);

    public List<Reservation> findFutureReservationsByClientId(Long clientId);

    public List<Reservation> findPastReservationsByGuideId(Long guideId);

    public List<Reservation> findFutureReservationsByGuideId(Long guideId);

    public List<Reservation> findByClientId(Long clientId);

}
