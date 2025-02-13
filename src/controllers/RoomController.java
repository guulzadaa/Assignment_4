package controllers;

import controllers.interfaces.IRoomController;
import models.Room;
import repositories.interfaces.IRoomRepository;

import java.util.List;

public class RoomController implements IRoomController {
    private final IRoomRepository repository;

    public RoomController(IRoomRepository repository) {
        this.repository = repository;
    }

    public String deleteRoomByNumber(String roomNumber) {
        return repository.deleteRoomByNumber(roomNumber);
    }

    @Override
    public String createRoom(Room room) {
        boolean created = repository.createRoom(room);
        return created ? "Room successfully added!" : "Failed to add room.";
    }

    @Override
    public String getRoomById(int id) {
        Room room = repository.getRoomById(id);
        return room != null ? room.toString() : "Room not found.";
    }

    @Override
    public String getAllRooms() {
        List<Room> rooms = repository.getAllRooms();
        return rooms.isEmpty() ? "No rooms available." : rooms.toString();
    }

    @Override
    public String updateRoomStatus(int id, String newStatus) {
        boolean updated = repository.updateRoomStatus(id, newStatus);
        return updated ? "Room status updated successfully!" : "Failed to update room status.";
    }


    public List<Room> getAllRoomsList() {
        return repository.getAllRooms();
    }
}