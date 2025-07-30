package com.example.nepaltourismmanagement.utils;

import com.example.nepaltourismmanagement.models.Booking;
import com.example.nepaltourismmanagement.models.Guide;
import com.example.nepaltourismmanagement.models.Trek;
import com.example.nepaltourismmanagement.models.User;
import com.example.nepaltourismmanagement.models.UserRole;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DatabaseUtil {

    // File paths
    private static final String DATA_DIR = "data/";
    private static final String USERS_FILE = DATA_DIR + "users.txt";
    private static final String BOOKINGS_FILE = DATA_DIR + "bookings.txt";
    private static final String TREKS_FILE = DATA_DIR + "treks.txt";
    private static final String GUIDES_FILE = DATA_DIR + "guides.txt";

    // Admin credentials
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    // Current date & time constant
    private static final String CURRENT_DATETIME = "2025-07-30 07:40:12";

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Constructor
    public DatabaseUtil() {
        // Initialize data storage
        initializeDataStorage();

        // Create admin user if it doesn't exist
        createAdminUserIfNotExists();

        // Create sample tourist user if it doesn't exist
        createSampleTouristIfNotExists();

        // Debug message to verify paths
        System.out.println("Data directory path: " + new File(DATA_DIR).getAbsolutePath());
        System.out.println("Users file path: " + new File(USERS_FILE).getAbsolutePath());
        System.out.println("Bookings file path: " + new File(BOOKINGS_FILE).getAbsolutePath());
        System.out.println("Current system time: " + CURRENT_DATETIME);
    }

    // Initialize data storage by creating directories and files if they don't exist
    private void initializeDataStorage() {
        try {
            // Create data directory if it doesn't exist
            Files.createDirectories(Paths.get(DATA_DIR));

            // Create files if they don't exist
            createFileIfNotExists(USERS_FILE);
            createFileIfNotExists(BOOKINGS_FILE);
            createFileIfNotExists(TREKS_FILE);
            createFileIfNotExists(GUIDES_FILE);

            // If no treks exist, add some sample treks
            if (Files.size(Paths.get(TREKS_FILE)) == 0) {
                addSampleTreks();
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize data storage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Add some sample treks if none exist
    private void addSampleTreks() {
        try {
            List<String> sampleTreks = new ArrayList<>();

            // Format: id|name|region|maxAltitude|duration|difficulty|basePrice|description|seasonal|bestSeason
            sampleTreks.add(UUID.randomUUID().toString() + "|Everest Base Camp Trek|Khumbu|5364|14|Difficult|1500|Experience the world's highest peak up close|true|Spring,Autumn");
            sampleTreks.add(UUID.randomUUID().toString() + "|Annapurna Circuit|Annapurna|5416|18|Difficult|1800|Complete circuit around the Annapurna massif|true|Spring,Autumn");
            sampleTreks.add(UUID.randomUUID().toString() + "|Langtang Valley|Langtang|4600|7|Moderate|900|Beautiful valley trek with rich culture|true|Spring,Autumn,Winter");
            sampleTreks.add(UUID.randomUUID().toString() + "|Poon Hill Trek|Annapurna|3210|5|Easy|700|Short trek with amazing mountain views|true|All Year");
            sampleTreks.add(UUID.randomUUID().toString() + "|Manaslu Circuit|Manaslu|5115|16|Very Difficult|1600|Remote and challenging trek around Manaslu|true|Spring,Autumn");

            Files.write(Paths.get(TREKS_FILE), sampleTreks);

            System.out.println("Added sample treks to the database");
        } catch (IOException e) {
            System.err.println("Error adding sample treks: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createFileIfNotExists(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            Files.createFile(path);
            System.out.println("Created file: " + filePath);
        }
    }

    // Method to create admin user if it doesn't exist
    private void createAdminUserIfNotExists() {
        try {
            // First check if users file exists and create if it doesn't
            Path usersFilePath = Paths.get(USERS_FILE);
            if (!Files.exists(usersFilePath)) {
                Files.createFile(usersFilePath);
            }

            // Check if admin user exists
            if (!isUserExists(ADMIN_USERNAME)) {
                User adminUser = new User();
                adminUser.setId(UUID.randomUUID().toString());
                adminUser.setUsername(ADMIN_USERNAME);
                adminUser.setPassword(ADMIN_PASSWORD);
                adminUser.setFullName("System Administrator");
                adminUser.setEmail("admin@tripsewa.com");
                adminUser.setContactNumber("9800000000");
                adminUser.setNationality("Nepal");
                adminUser.setRole(UserRole.ADMIN);

                // Format: id|username|password|fullName|email|contactNumber|nationality|passportNumber|emergencyContact|role|registrationDate
                StringBuilder sb = new StringBuilder();
                sb.append(adminUser.getId()).append("|");
                sb.append(adminUser.getUsername()).append("|");
                sb.append(adminUser.getPassword()).append("|");
                sb.append(adminUser.getFullName()).append("|");
                sb.append(adminUser.getEmail()).append("|");
                sb.append(adminUser.getContactNumber()).append("|");
                sb.append(adminUser.getNationality()).append("|");
                sb.append("").append("|"); // passportNumber
                sb.append("").append("|"); // emergencyContact
                sb.append(adminUser.getRole().toString()).append("|");
                sb.append(CURRENT_DATETIME);

                Files.write(usersFilePath,
                        Collections.singletonList(sb.toString()),
                        StandardOpenOption.APPEND);

                System.out.println("Admin user created successfully with username: " + ADMIN_USERNAME);
            }
        } catch (IOException e) {
            System.err.println("Error creating admin user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to create sample tourist user if it doesn't exist
    private void createSampleTouristIfNotExists() {
        try {
            // Check if the sample tourist user exists
            if (!isUserExists("tourist")) {
                String touristId = UUID.randomUUID().toString();

                User touristUser = new User();
                touristUser.setId(touristId);
                touristUser.setUsername("tourist");
                touristUser.setPassword("tourist123");
                touristUser.setFullName("BibekDkl");
                touristUser.setEmail("tourist@example.com");
                touristUser.setContactNumber("9800000001");
                touristUser.setNationality("Nepal");
                touristUser.setRole(UserRole.TOURIST);

                // Format: id|username|password|fullName|email|contactNumber|nationality|passportNumber|emergencyContact|role|registrationDate
                StringBuilder sb = new StringBuilder();
                sb.append(touristId).append("|");
                sb.append(touristUser.getUsername()).append("|");
                sb.append(touristUser.getPassword()).append("|");
                sb.append(touristUser.getFullName()).append("|");
                sb.append(touristUser.getEmail()).append("|");
                sb.append(touristUser.getContactNumber()).append("|");
                sb.append(touristUser.getNationality()).append("|");
                sb.append("T123456").append("|"); // passportNumber
                sb.append("9800000002").append("|"); // emergencyContact
                sb.append(touristUser.getRole().toString()).append("|");
                sb.append(CURRENT_DATETIME);

                Files.write(Paths.get(USERS_FILE),
                        Collections.singletonList(sb.toString()),
                        StandardOpenOption.APPEND);

                System.out.println("Sample tourist user created successfully with username: tourist and password: tourist123");
                System.out.println("Tourist ID: " + touristId);
            }
        } catch (IOException e) {
            System.err.println("Error creating sample tourist user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to check if a user exists
    private boolean isUserExists(String username) {
        try {
            if (!Files.exists(Paths.get(USERS_FILE))) {
                return false;
            }

            List<String> lines = Files.readAllLines(Paths.get(USERS_FILE));

            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2 && parts[1].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error checking if user exists: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Method to authenticate a user
    public User authenticateUser(String username, String password) {
        try {
            if (!Files.exists(Paths.get(USERS_FILE))) {
                System.err.println("Users file does not exist");
                return null;
            }

            List<String> lines = Files.readAllLines(Paths.get(USERS_FILE));

            for (String line : lines) {
                String[] parts = line.split("\\|");

                if (parts.length >= 10 && parts[1].equals(username) && parts[2].equals(password)) {
                    User user = new User();
                    user.setId(parts[0]);
                    user.setUsername(parts[1]);
                    user.setPassword(parts[2]); // Usually wouldn't store this in memory
                    user.setFullName(parts[3]);
                    user.setEmail(parts[4]);
                    user.setContactNumber(parts[5]);
                    user.setNationality(parts[6]);
                    if (parts.length > 7) user.setPassportNumber(parts[7]);
                    if (parts.length > 8) user.setEmergencyContact(parts[8]);
                    user.setRole(UserRole.valueOf(parts[9]));

                    System.out.println("User authenticated: " + user.getUsername() + " (ID: " + user.getId() + ")");
                    return user;
                }
            }

            System.out.println("Authentication failed for username: " + username);
        } catch (IOException e) {
            System.err.println("Error reading users file: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Method to register a new user
    public boolean registerUser(User user) {
        String userId = UUID.randomUUID().toString();
        user.setId(userId);

        // Format: id|username|password|fullName|email|contactNumber|nationality|passportNumber|emergencyContact|role|registrationDate
        StringBuilder sb = new StringBuilder();
        sb.append(userId).append("|");
        sb.append(user.getUsername() != null ? user.getUsername() : "").append("|");
        sb.append(user.getPassword()).append("|");
        sb.append(user.getFullName()).append("|");
        sb.append(user.getEmail()).append("|");
        sb.append(user.getContactNumber()).append("|");
        sb.append(user.getNationality()).append("|");
        sb.append(user.getPassportNumber() != null ? user.getPassportNumber() : "").append("|");
        sb.append(user.getEmergencyContact() != null ? user.getEmergencyContact() : "").append("|");
        sb.append(user.getRole().toString()).append("|");
        sb.append(CURRENT_DATETIME);

        try {
            Files.write(Paths.get(USERS_FILE),
                    Collections.singletonList(sb.toString()),
                    StandardOpenOption.APPEND);

            // If user is a guide, create a guide profile
            if (user.getRole() == UserRole.GUIDE) {
                createGuideProfile(userId, user.getFullName(), user.getEmail(), user.getContactNumber());
            }

            return true;
        } catch (IOException e) {
            System.err.println("Error writing to users file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Create a guide profile when a guide user registers
    private void createGuideProfile(String userId, String name, String email, String phone) {
        String guideId = UUID.randomUUID().toString();

        // Format: id|userId|name|email|phoneNumber|languages|yearsOfExperience|certifications|specializations|available
        StringBuilder sb = new StringBuilder();
        sb.append(guideId).append("|");
        sb.append(userId).append("|");
        sb.append(name).append("|");
        sb.append(email).append("|");
        sb.append(phone).append("|");
        sb.append("English, Nepali").append("|"); // Default languages
        sb.append("0").append("|"); // yearsOfExperience
        sb.append("Basic Guide Training").append("|"); // Default certifications
        sb.append("General Trekking").append("|"); // Default specializations
        sb.append("true"); // available by default

        try {
            Files.write(Paths.get(GUIDES_FILE),
                    Collections.singletonList(sb.toString()),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);

            System.out.println("Guide profile created for user ID: " + userId);
        } catch (IOException e) {
            System.err.println("Error writing to guides file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to get all bookings
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();

        try {
            if (!Files.exists(Paths.get(BOOKINGS_FILE)) || Files.size(Paths.get(BOOKINGS_FILE)) == 0) {
                return bookings;
            }

            Map<String, Trek> treks = getAllTreksMap();
            Map<String, User> users = getAllUsersMap();
            Map<String, Guide> guides = getAllGuidesMap();

            List<String> lines = Files.readAllLines(Paths.get(BOOKINGS_FILE));

            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 9) {
                    Booking booking = parseBooking(parts);

                    // Add additional information
                    if (treks.containsKey(booking.getTrekId())) {
                        booking.setTrekName(treks.get(booking.getTrekId()).getName());
                    }

                    if (users.containsKey(booking.getTouristId())) {
                        booking.setTouristName(users.get(booking.getTouristId()).getFullName());
                    }

                    if (booking.getGuideId() != null && guides.containsKey(booking.getGuideId())) {
                        booking.setGuideName(guides.get(booking.getGuideId()).getName());
                    }

                    bookings.add(booking);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading bookings file: " + e.getMessage());
            e.printStackTrace();
        }

        return bookings;
    }

    // Helper method to parse a booking from file line
    private Booking parseBooking(String[] parts) {
        Booking booking = new Booking();
        booking.setId(parts[0]);
        booking.setTrekId(parts[1]);
        booking.setTouristId(parts[2]);
        booking.setGuideId(parts[3].isEmpty() ? null : parts[3]);
        booking.setBookingDate(parts[4]);
        booking.setStatus(parts[5]);
        booking.setPrice(Double.parseDouble(parts[6]));
        booking.setDuration(Integer.parseInt(parts[7]));
        booking.setHighRiskAcknowledged(Boolean.parseBoolean(parts[8]));
        return booking;
    }

    // Method to get all users as a map for quick lookup
    private Map<String, User> getAllUsersMap() {
        Map<String, User> usersMap = new HashMap<>();

        try {
            List<String> lines = Files.readAllLines(Paths.get(USERS_FILE));

            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    User user = new User();
                    user.setId(parts[0]);
                    user.setUsername(parts[1]);
                    user.setFullName(parts[3]);
                    user.setEmail(parts[4]);
                    if (parts.length > 5) user.setContactNumber(parts[5]);
                    if (parts.length > 6) user.setNationality(parts[6]);
                    if (parts.length > 9) user.setRole(UserRole.valueOf(parts[9]));

                    usersMap.put(user.getId(), user);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users file: " + e.getMessage());
            e.printStackTrace();
        }

        return usersMap;
    }

    // Method to filter bookings by status
    public List<Booking> getBookingsByStatus(String status) {
        return getAllBookings().stream()
                .filter(booking -> status.equals(booking.getStatus()))
                .collect(Collectors.toList());
    }

    // Method to get bookings for a specific tourist
    public List<Booking> getBookingsForTourist(String touristId) {
        if (touristId == null) {
            System.err.println("ERROR: getBookingsForTourist called with null touristId");
            return new ArrayList<>();
        }

        System.out.println("Getting bookings for tourist ID: " + touristId);

        List<Booking> bookings = getAllBookings().stream()
                .filter(booking -> touristId.equals(booking.getTouristId()))
                .collect(Collectors.toList());

        System.out.println("Found " + bookings.size() + " bookings for tourist ID: " + touristId);
        return bookings;
    }

    // Method to get assigned trips for a specific guide
    public List<Booking> getAssignedTripsForGuide(String guideId) {
        return getAllBookings().stream()
                .filter(booking -> guideId.equals(booking.getGuideId()))
                .collect(Collectors.toList());
    }

    // Method to update booking status
    public boolean updateBookingStatus(String bookingId, String status) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(BOOKINGS_FILE));
            List<String> updatedLines = new ArrayList<>();
            boolean found = false;

            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 6 && parts[0].equals(bookingId)) {
                    // Update status
                    parts[5] = status;
                    updatedLines.add(String.join("|", parts));
                    found = true;
                } else {
                    updatedLines.add(line);
                }
            }

            if (found) {
                Files.write(Paths.get(BOOKINGS_FILE), updatedLines);
                return true;
            }

        } catch (IOException e) {
            System.err.println("Error updating booking status: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Method to assign a guide to a booking
    public boolean assignGuideToBooking(String bookingId, String guideId) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(BOOKINGS_FILE));
            List<String> updatedLines = new ArrayList<>();
            boolean found = false;

            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 6 && parts[0].equals(bookingId)) {
                    // Update guide ID and status
                    parts[3] = guideId;
                    parts[5] = "Confirmed";
                    updatedLines.add(String.join("|", parts));
                    found = true;
                } else {
                    updatedLines.add(line);
                }
            }

            if (found) {
                Files.write(Paths.get(BOOKINGS_FILE), updatedLines);
                return true;
            }

        } catch (IOException e) {
            System.err.println("Error assigning guide to booking: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Method to create a new booking
    public boolean createBooking(Booking booking) {
        try {
            if (booking.getTouristId() == null) {
                System.err.println("Error: Tourist ID is null in booking");
                return false;
            }

            String bookingId = UUID.randomUUID().toString();
            booking.setId(bookingId);

            // Make sure the file and directories exist
            Path dataDir = Paths.get(DATA_DIR);
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
                System.out.println("Created data directory: " + dataDir);
            }

            Path bookingsFilePath = Paths.get(BOOKINGS_FILE);
            if (!Files.exists(bookingsFilePath)) {
                Files.createFile(bookingsFilePath);
                System.out.println("Created bookings file: " + bookingsFilePath);
            }

            // Format: id|trekId|touristId|guideId|bookingDate|status|price|duration|highRiskAcknowledged
            StringBuilder sb = new StringBuilder();
            sb.append(bookingId).append("|");
            sb.append(booking.getTrekId()).append("|");
            sb.append(booking.getTouristId()).append("|");
            sb.append(booking.getGuideId() != null ? booking.getGuideId() : "").append("|");
            sb.append(booking.getBookingDate()).append("|");
            sb.append(booking.getStatus()).append("|");
            sb.append(booking.getPrice()).append("|");
            sb.append(booking.getDuration()).append("|");
            sb.append(booking.isHighRiskAcknowledged());

            // Print booking details for debugging
            System.out.println("Creating booking with details:");
            System.out.println("  ID: " + bookingId);
            System.out.println("  Trek ID: " + booking.getTrekId());
            System.out.println("  Tourist ID: " + booking.getTouristId());
            System.out.println("  Date: " + booking.getBookingDate());
            System.out.println("  Status: " + booking.getStatus());

            Files.write(bookingsFilePath,
                    Collections.singletonList(sb.toString()),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);

            System.out.println("Booking created successfully with ID: " + bookingId);
            return true;
        } catch (IOException e) {
            System.err.println("Error creating booking: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error creating booking: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update a booking's details
     * @param booking The booking with updated information
     * @return true if update successful, false otherwise
     */
    public boolean updateBooking(Booking booking) {
        System.out.println("Updating booking with ID: " + booking.getId());

        try {
            List<String> lines = Files.readAllLines(Paths.get(BOOKINGS_FILE));
            List<String> updatedLines = new ArrayList<>();
            boolean found = false;

            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 6 && parts[0].equals(booking.getId())) {
                    // Format: id|trekId|touristId|guideId|bookingDate|status|price|duration|highRiskAcknowledged
                    StringBuilder sb = new StringBuilder();
                    sb.append(booking.getId()).append("|");
                    sb.append(booking.getTrekId()).append("|");
                    sb.append(booking.getTouristId()).append("|");
                    sb.append(booking.getGuideId() != null ? booking.getGuideId() : "").append("|");
                    sb.append(booking.getBookingDate()).append("|");
                    sb.append(booking.getStatus()).append("|");
                    sb.append(booking.getPrice()).append("|");
                    sb.append(booking.getDuration()).append("|");
                    sb.append(booking.isHighRiskAcknowledged());

                    updatedLines.add(sb.toString());
                    found = true;

                    System.out.println("Booking found and updated: " + sb.toString());
                } else {
                    updatedLines.add(line);
                }
            }

            if (found) {
                Files.write(Paths.get(BOOKINGS_FILE), updatedLines);
                System.out.println("Booking updated successfully");
                return true;
            } else {
                System.err.println("Booking with ID " + booking.getId() + " not found");
                return false;
            }
        } catch (IOException e) {
            System.err.println("Error updating booking: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Method to get all guides
    public List<Guide> getAllGuides() {
        List<Guide> guides = new ArrayList<>();

        try {
            if (!Files.exists(Paths.get(GUIDES_FILE)) || Files.size(Paths.get(GUIDES_FILE)) == 0) {
                return guides;
            }

            List<String> lines = Files.readAllLines(Paths.get(GUIDES_FILE));

            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 6) {
                    Guide guide = new Guide();
                    guide.setId(parts[0]);
                    guide.setUserId(parts[1]);
                    guide.setName(parts[2]);
                    guide.setEmail(parts[3]);
                    guide.setPhoneNumber(parts[4]);
                    if (parts.length > 5) guide.setLanguages(parts[5]);
                    if (parts.length > 6) guide.setYearsOfExperience(Integer.parseInt(parts[6]));
                    if (parts.length > 7) guide.setCertifications(parts[7]);
                    if (parts.length > 8) guide.setSpecializations(parts[8]);
                    if (parts.length > 9) guide.setAvailable(Boolean.parseBoolean(parts[9]));

                    guides.add(guide);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading guides file: " + e.getMessage());
            e.printStackTrace();
        }

        return guides;
    }

    // Method to get all guides as a map for quick lookup
    private Map<String, Guide> getAllGuidesMap() {
        Map<String, Guide> guidesMap = new HashMap<>();

        for (Guide guide : getAllGuides()) {
            guidesMap.put(guide.getId(), guide);
        }

        return guidesMap;
    }

    // Method to get a guide by user ID
    public Guide getGuideByUserId(String userId) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(GUIDES_FILE));

            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 6 && parts[1].equals(userId)) {
                    Guide guide = new Guide();
                    guide.setId(parts[0]);
                    guide.setUserId(parts[1]);
                    guide.setName(parts[2]);
                    guide.setEmail(parts[3]);
                    guide.setPhoneNumber(parts[4]);
                    if (parts.length > 5) guide.setLanguages(parts[5]);
                    if (parts.length > 6) guide.setYearsOfExperience(Integer.parseInt(parts[6]));
                    if (parts.length > 7) guide.setCertifications(parts[7]);
                    if (parts.length > 8) guide.setSpecializations(parts[8]);
                    if (parts.length > 9) guide.setAvailable(Boolean.parseBoolean(parts[9]));

                    return guide;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading guides file: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Method to add a new guide
    public boolean addGuide(Guide guide) {
        String guideId = UUID.randomUUID().toString();
        guide.setId(guideId);

        // Format: id|userId|name|email|phoneNumber|languages|yearsOfExperience|certifications|specializations|available
        StringBuilder sb = new StringBuilder();
        sb.append(guideId).append("|");
        sb.append(guide.getUserId() != null ? guide.getUserId() : "").append("|");
        sb.append(guide.getName()).append("|");
        sb.append(guide.getEmail()).append("|");
        sb.append(guide.getPhoneNumber()).append("|");
        sb.append(guide.getLanguages() != null ? guide.getLanguages() : "").append("|");
        sb.append(guide.getYearsOfExperience()).append("|");
        sb.append(guide.getCertifications() != null ? guide.getCertifications() : "").append("|");
        sb.append(guide.getSpecializations() != null ? guide.getSpecializations() : "").append("|");
        sb.append(guide.isAvailable());

        try {
            Files.write(Paths.get(GUIDES_FILE),
                    Collections.singletonList(sb.toString()),
                    StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            System.err.println("Error adding guide: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Method to update a guide
    public boolean updateGuide(Guide guide) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(GUIDES_FILE));
            List<String> updatedLines = new ArrayList<>();
            boolean found = false;

            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 6 && parts[0].equals(guide.getId())) {
                    // Format: id|userId|name|email|phoneNumber|languages|yearsOfExperience|certifications|specializations|available
                    StringBuilder sb = new StringBuilder();
                    sb.append(guide.getId()).append("|");
                    sb.append(guide.getUserId() != null ? guide.getUserId() : "").append("|");
                    sb.append(guide.getName()).append("|");
                    sb.append(guide.getEmail()).append("|");
                    sb.append(guide.getPhoneNumber()).append("|");
                    sb.append(guide.getLanguages() != null ? guide.getLanguages() : "").append("|");
                    sb.append(guide.getYearsOfExperience()).append("|");
                    sb.append(guide.getCertifications() != null ? guide.getCertifications() : "").append("|");
                    sb.append(guide.getSpecializations() != null ? guide.getSpecializations() : "").append("|");
                    sb.append(guide.isAvailable());

                    updatedLines.add(sb.toString());
                    found = true;
                } else {
                    updatedLines.add(line);
                }
            }

            if (found) {
                Files.write(Paths.get(GUIDES_FILE), updatedLines);
                return true;
            }

        } catch (IOException e) {
            System.err.println("Error updating guide: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Method to delete a guide
    public boolean deleteGuide(String guideId) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(GUIDES_FILE));
            List<String> updatedLines = new ArrayList<>();
            boolean found = false;

            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 1 && parts[0].equals(guideId)) {
                    found = true;
                } else {
                    updatedLines.add(line);
                }
            }

            if (found) {
                Files.write(Paths.get(GUIDES_FILE), updatedLines);
                return true;
            }

        } catch (IOException e) {
            System.err.println("Error deleting guide: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Method to get all treks
    public List<Trek> getAllTreks() {
        List<Trek> treks = new ArrayList<>();

        try {
            if (!Files.exists(Paths.get(TREKS_FILE)) || Files.size(Paths.get(TREKS_FILE)) == 0) {
                return treks;
            }

            List<String> lines = Files.readAllLines(Paths.get(TREKS_FILE));

            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 8) {
                    Trek trek = new Trek();
                    trek.setId(parts[0]);
                    trek.setName(parts[1]);
                    trek.setRegion(parts[2]);
                    trek.setMaxAltitude(Integer.parseInt(parts[3]));
                    trek.setDuration(Integer.parseInt(parts[4]));
                    trek.setDifficulty(parts[5]);
                    trek.setBasePrice(Double.parseDouble(parts[6]));
                    trek.setDescription(parts[7]);
                    if (parts.length > 8) trek.setSeasonal(Boolean.parseBoolean(parts[8]));
                    if (parts.length > 9) trek.setBestSeason(parts[9]);

                    treks.add(trek);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading treks file: " + e.getMessage());
            e.printStackTrace();
        }

        return treks;
    }

    // Method to get a specific trek by ID
    public Trek getTrekById(String trekId) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(TREKS_FILE));

            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 8 && parts[0].equals(trekId)) {
                    Trek trek = new Trek();
                    trek.setId(parts[0]);
                    trek.setName(parts[1]);
                    trek.setRegion(parts[2]);
                    trek.setMaxAltitude(Integer.parseInt(parts[3]));
                    trek.setDuration(Integer.parseInt(parts[4]));
                    trek.setDifficulty(parts[5]);
                    trek.setBasePrice(Double.parseDouble(parts[6]));
                    trek.setDescription(parts[7]);
                    if (parts.length > 8) trek.setSeasonal(Boolean.parseBoolean(parts[8]));
                    if (parts.length > 9) trek.setBestSeason(parts[9]);

                    return trek;
                }
            }
        } catch (IOException e) {
            System.err.println("Error finding trek by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Method to get all treks as a map for quick lookup
    private Map<String, Trek> getAllTreksMap() {
        Map<String, Trek> treksMap = new HashMap<>();

        for (Trek trek : getAllTreks()) {
            treksMap.put(trek.getId(), trek);
        }

        return treksMap;
    }

    // Method to add a new trek
    public boolean addTrek(Trek trek) {
        String trekId = UUID.randomUUID().toString();
        trek.setId(trekId);

        // Format: id|name|region|maxAltitude|duration|difficulty|basePrice|description|seasonal|bestSeason
        StringBuilder sb = new StringBuilder();
        sb.append(trekId).append("|");
        sb.append(trek.getName()).append("|");
        sb.append(trek.getRegion()).append("|");
        sb.append(trek.getMaxAltitude()).append("|");
        sb.append(trek.getDuration()).append("|");
        sb.append(trek.getDifficulty()).append("|");
        sb.append(trek.getBasePrice()).append("|");
        sb.append(trek.getDescription()).append("|");
        sb.append(trek.isSeasonal()).append("|");
        sb.append(trek.getBestSeason() != null ? trek.getBestSeason() : "");

        try {
            Files.write(Paths.get(TREKS_FILE),
                    Collections.singletonList(sb.toString()),
                    StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            System.err.println("Error adding trek: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Method to update a trek
    public boolean updateTrek(Trek trek) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(TREKS_FILE));
            List<String> updatedLines = new ArrayList<>();
            boolean found = false;

            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 8 && parts[0].equals(trek.getId())) {
                    // Format: id|name|region|maxAltitude|duration|difficulty|basePrice|description|seasonal|bestSeason
                    StringBuilder sb = new StringBuilder();
                    sb.append(trek.getId()).append("|");
                    sb.append(trek.getName()).append("|");
                    sb.append(trek.getRegion()).append("|");
                    sb.append(trek.getMaxAltitude()).append("|");
                    sb.append(trek.getDuration()).append("|");
                    sb.append(trek.getDifficulty()).append("|");
                    sb.append(trek.getBasePrice()).append("|");
                    sb.append(trek.getDescription()).append("|");
                    sb.append(trek.isSeasonal()).append("|");
                    sb.append(trek.getBestSeason() != null ? trek.getBestSeason() : "");

                    updatedLines.add(sb.toString());
                    found = true;
                } else {
                    updatedLines.add(line);
                }
            }

            if (found) {
                Files.write(Paths.get(TREKS_FILE), updatedLines);
                return true;
            }

        } catch (IOException e) {
            System.err.println("Error updating trek: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Method to delete a trek
    public boolean deleteTrek(String trekId) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(TREKS_FILE));
            List<String> updatedLines = new ArrayList<>();
            boolean found = false;

            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 1 && parts[0].equals(trekId)) {
                    found = true;
                } else {
                    updatedLines.add(line);
                }
            }

            if (found) {
                Files.write(Paths.get(TREKS_FILE), updatedLines);
                return true;
            }

        } catch (IOException e) {
            System.err.println("Error deleting trek: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Get all users
     * @return List of all users
     */
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();

        try {
            if (!Files.exists(Paths.get(USERS_FILE))) {
                return userList;
            }

            List<String> lines = Files.readAllLines(Paths.get(USERS_FILE));

            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 10) {
                    User user = new User();
                    user.setId(parts[0]);
                    user.setUsername(parts[1]);
                    // Don't set password for security
                    user.setFullName(parts[3]);
                    user.setEmail(parts[4]);
                    user.setContactNumber(parts[5]);
                    user.setNationality(parts[6]);
                    if (parts.length > 7) user.setPassportNumber(parts[7]);
                    if (parts.length > 8) user.setEmergencyContact(parts[8]);
                    user.setRole(UserRole.valueOf(parts[9]));

                    userList.add(user);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users file: " + e.getMessage());
            e.printStackTrace();
        }

        return userList;
    }

    /**
     * Delete a user by ID
     * @param userId The user ID to delete
     * @return true if successfully deleted, false otherwise
     */
    public boolean deleteUser(String userId) {
        try {
            // First find the user to get the username
            String username = null;
            User userToDelete = null;

            for (User user : getAllUsers()) {
                if (user.getId().equals(userId)) {
                    username = user.getUsername();
                    userToDelete = user;
                    break;
                }
            }

            if (username == null) {
                System.err.println("User with ID " + userId + " not found");
                return false;
            }

            // Don't delete the admin user
            if ("admin".equals(username)) {
                System.err.println("Cannot delete the admin user");
                return false;
            }

            // If user is a guide, delete guide profile too
            if (userToDelete.getRole() == UserRole.GUIDE) {
                Guide guide = getGuideByUserId(userId);
                if (guide != null) {
                    deleteGuide(guide.getId());
                }
            }

            // If user is a tourist, delete or reassign their bookings
            if (userToDelete.getRole() == UserRole.TOURIST) {
                List<Booking> userBookings = getBookingsForTourist(userId);
                for (Booking booking : userBookings) {
                    // For now, just cancel the bookings
                    updateBookingStatus(booking.getId(), "Cancelled");
                }
            }

            // Read all users and write back except the one to delete
            List<String> lines = Files.readAllLines(Paths.get(USERS_FILE));
            List<String> updatedLines = new ArrayList<>();

            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 1 && !parts[0].equals(userId)) {
                    updatedLines.add(line);
                }
            }

            Files.write(Paths.get(USERS_FILE), updatedLines);
            System.out.println("User deleted successfully: " + username + " (ID: " + userId + ")");
            return true;

        } catch (IOException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update a user's information
     * @param user User object with updated information
     * @return true if update successful, false otherwise
     */
    public boolean updateUser(User user) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(USERS_FILE));
            List<String> updatedLines = new ArrayList<>();
            boolean found = false;

            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 10 && parts[0].equals(user.getId())) {
                    // Keep the password and registration date from original record
                    String password = parts[2];
                    String registrationDate = parts.length > 10 ? parts[10] : CURRENT_DATETIME;

                    // Format: id|username|password|fullName|email|contactNumber|nationality|passportNumber|emergencyContact|role|registrationDate
                    StringBuilder sb = new StringBuilder();
                    sb.append(user.getId()).append("|");
                    sb.append(user.getUsername()).append("|");
                    sb.append(password).append("|");
                    sb.append(user.getFullName()).append("|");
                    sb.append(user.getEmail()).append("|");
                    sb.append(user.getContactNumber()).append("|");
                    sb.append(user.getNationality()).append("|");
                    sb.append(user.getPassportNumber() != null ? user.getPassportNumber() : "").append("|");
                    sb.append(user.getEmergencyContact() != null ? user.getEmergencyContact() : "").append("|");
                    sb.append(user.getRole().toString()).append("|");
                    sb.append(registrationDate);

                    updatedLines.add(sb.toString());
                    found = true;

                    System.out.println("User updated: " + user.getUsername() + " (ID: " + user.getId() + ")");
                } else {
                    updatedLines.add(line);
                }
            }

            if (found) {
                Files.write(Paths.get(USERS_FILE), updatedLines);
                return true;
            } else {
                System.err.println("User with ID " + user.getId() + " not found");
                return false;
            }

        } catch (IOException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get booking statistics
     * @return Map containing booking statistics
     */
    public Map<String, Object> getBookingStatistics() {
        Map<String, Object> stats = new HashMap<>();

        List<Booking> allBookings = getAllBookings();

        // Count by status
        Map<String, Integer> statusCounts = new HashMap<>();

        // Calculate total revenue
        double totalRevenue = 0;

        // Track trek popularity
        Map<String, Integer> trekPopularity = new HashMap<>();

        for (Booking booking : allBookings) {
            // Count by status
            String status = booking.getStatus();
            statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);

            // Add to revenue if confirmed or completed
            if ("Confirmed".equals(status) || "Completed".equals(status)) {
                totalRevenue += booking.getPrice();
            }

            // Track trek popularity
            String trekName = booking.getTrekName();
            if (trekName != null) {
                trekPopularity.put(trekName, trekPopularity.getOrDefault(trekName, 0) + 1);
            }
        }

        // Find most popular trek
        String mostPopularTrek = "";
        int maxBookings = 0;

        for (Map.Entry<String, Integer> entry : trekPopularity.entrySet()) {
            if (entry.getValue() > maxBookings) {
                maxBookings = entry.getValue();
                mostPopularTrek = entry.getKey();
            }
        }

        stats.put("totalBookings", allBookings.size());
        stats.put("statusCounts", statusCounts);
        stats.put("totalRevenue", totalRevenue);
        stats.put("mostPopularTrek", mostPopularTrek);
        stats.put("mostPopularTrekCount", maxBookings);

        return stats;
    }

    /**
     * Get current date/time
     * @return Current date/time string
     */
    public String getCurrentDateTime() {
        return CURRENT_DATETIME;
    }

    /**
     * Check if the current user is BibekDkl
     * @param username The username to check
     * @return True if the user is BibekDkl
     */
    public boolean isBibekDkl(String username) {
        return "BibekDkl".equals(username);
    }
}