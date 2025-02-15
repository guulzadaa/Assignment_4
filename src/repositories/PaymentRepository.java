package repositories;

import data.interfaces.IDB;
import models.Payment;
import repositories.interfaces.IPaymentRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepository implements IPaymentRepository {
    private final IDB db;

    public PaymentRepository(IDB db) {
        this.db = db;
    }

    @Override
    public boolean createPayment(int bookingId) {
        String bookingQuery = "SELECT b.check_in_date, b.check_out_date, r.price " +
                "FROM bookings b " +
                "JOIN rooms r ON b.room_id = r.id " +
                "WHERE b.id = ?";

        String insertPaymentQuery = "INSERT INTO payments (booking_id, amount, payment_method, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement bookingStmt = conn.prepareStatement(bookingQuery)) {

            bookingStmt.setInt(1, bookingId);
            try (ResultSet rs = bookingStmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Booking not found.");
                    return false;
                }

                Date checkIn = rs.getDate("check_in_date");
                Date checkOut = rs.getDate("check_out_date");
                double pricePerDay = rs.getDouble("price");

                // Исправлен расчет дней (если check-in и check-out совпадают, минимум 1 день)
                long diff = checkOut.getTime() - checkIn.getTime();
                int days = Math.max(1, (int) (diff / (1000 * 60 * 60 * 24)));
                double totalAmount = pricePerDay * days;

                try (PreparedStatement insertStmt = conn.prepareStatement(insertPaymentQuery)) {
                    insertStmt.setInt(1, bookingId);
                    insertStmt.setDouble(2, totalAmount);
                    insertStmt.setString(3, "not_specified"); // Можно уточнять метод позже
                    insertStmt.setString(4, "pending");
                    insertStmt.executeUpdate();
                }
            }

            return true;
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            return false;
        }
    }


    @Override
    public Payment getPaymentById(int id) {
        try (Connection conn = db.getConnection()) {
            String sql = "SELECT * FROM payments WHERE id = ?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                return new Payment(
                        rs.getInt("id"),
                        rs.getInt("booking_id"),
                        rs.getDouble("amount"),
                        rs.getString("payment_method"),
                        rs.getString("status"),
                        rs.getString("created_at")
                );
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement st = conn.prepareStatement("SELECT * FROM payments");
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                payments.add(new Payment(
                        rs.getInt("id"),
                        rs.getInt("booking_id"),
                        rs.getDouble("amount"),
                        rs.getString("payment_method"),
                        rs.getString("status"),
                        rs.getString("created_at")
                ));
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return payments;
    }

    @Override
    public boolean updatePaymentStatus(int id, String newStatus) {
        try (Connection conn = db.getConnection()) {
            String sql = "UPDATE payments SET status = ? WHERE id = ?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, newStatus);
            st.setInt(2, id);

            int rowsUpdated = st.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return false;
        }
    }
}