package controllers.interfaces;

import models.Booking;

import java.util.List;

public interface IBookingController {
    String createBooking(Booking booking);
    String getBookingById(int id);
    List<Booking> getAllBookings();

    String cancelBooking(int id);


    void notifyCustomer(int bookingId);
}