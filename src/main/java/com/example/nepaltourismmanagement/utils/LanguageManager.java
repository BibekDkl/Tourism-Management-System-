package com.example.nepaltourismmanagement.utils;

import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;

public class LanguageManager {

    // Singleton instance
    private static LanguageManager instance;

    // Current language
    private String currentLanguage;

    // Nepali font
    private static Font nepaliFont;

    // Translation maps
    private static final Map<String, Map<String, String>> translations = new HashMap<>();

    // Current date/time for system
    private static final String CURRENT_DATETIME = "2025-07-30 08:34:16";
    private static final String CURRENT_USERNAME = "BibekDkl";

    /**
     * Get the singleton instance of LanguageManager
     */
    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    /**
     * Private constructor for singleton pattern
     */
    private LanguageManager() {
        currentLanguage = "English"; // Default language
        loadNepaliFont();
        initializeTranslations();
    }

    /**
     * Initialize translation maps with common phrases
     */
    private void initializeTranslations() {
        System.out.println("Initializing language translations");

        // Initialize English translations
        Map<String, String> englishTexts = new HashMap<>();

        // Basic/Common texts
        englishTexts.put("Welcome", "Welcome");
        englishTexts.put("Login", "Login");
        englishTexts.put("Register", "Register");
        englishTexts.put("Username", "Username");
        englishTexts.put("Password", "Password");
        englishTexts.put("RememberMe", "Remember Me");
        englishTexts.put("ForgotPassword", "Forgot Password?");
        englishTexts.put("WelcomeMessage", "Welcome to Trip Sewa Nepal!");
        englishTexts.put("Logout", "Logout");
        englishTexts.put("Refresh", "Refresh");
        englishTexts.put("DashboardInfo", "Explore the beautiful Himalayan region.");
        englishTexts.put("SelectLanguage", "Select Language");
        englishTexts.put("RefreshSuccess", "Dashboard refreshed successfully!");

        // Login screen
        englishTexts.put("Welcome Back!", "Welcome Back!");
        englishTexts.put("Don't have an account?", "Don't have an account?");
        englishTexts.put("Your Gateway to Adventure", "Your Gateway to Adventure");
        englishTexts.put("Trip Sewa Nepal !", "Trip Sewa Nepal !");

        // Registration form
        englishTexts.put("Create Account", "Create Account");
        englishTexts.put("Join Trip Sewa Nepal!", "Join Trip Sewa Nepal!");
        englishTexts.put("Full Name *", "Full Name *");
        englishTexts.put("Email *", "Email *");
        englishTexts.put("Phone *", "Phone *");
        englishTexts.put("Username *", "Username *");
        englishTexts.put("Password *", "Password *");
        englishTexts.put("Confirm Password *", "Confirm Password *");
        englishTexts.put("Register as *", "Register as *");
        englishTexts.put("Tourist", "Tourist");
        englishTexts.put("Guide", "Guide");
        englishTexts.put("Already have an account?", "Already have an account?");
        englishTexts.put("Sign in", "Sign in");

        // Navigation/Tab titles
        englishTexts.put("BookingManagement", "Booking Management");
        englishTexts.put("TrekGuideManagement", "Trek Guide Management");
        englishTexts.put("TrekLocations", "Trek Locations");
        englishTexts.put("AnalyticsDashboard", "Analytics Dashboard");
        englishTexts.put("GuideDashboard", "Guide Dashboard");
        englishTexts.put("TouristDashboard", "Tourist Dashboard");
        englishTexts.put("Book New Trip", "Book New Trip");
        englishTexts.put("View Your Trips", "View Your Trips");
        englishTexts.put("Manage Trips", "Manage Trips");

        // Booking Management
        englishTexts.put("FilterByStatus", "Filter By Status");
        englishTexts.put("FilterByDate", "Filter By Date");
        englishTexts.put("BookingID", "Booking ID");
        englishTexts.put("TouristName", "Tourist Name");
        englishTexts.put("Nationality", "Nationality");
        englishTexts.put("TrekGuide", "Trek Guide");
        englishTexts.put("TrekLocation", "Trek Location");
        englishTexts.put("TrekDate", "Trek Date");
        englishTexts.put("Status", "Status");
        englishTexts.put("TrekFee", "Trek Fee");
        englishTexts.put("AssignTrekGuide", "Assign Trek Guide");
        englishTexts.put("AssignGuide", "Assign Guide");
        englishTexts.put("UpdateStatus", "Update Status");
        englishTexts.put("CancelBooking", "Cancel Booking");
        englishTexts.put("BookingStatistics", "Booking Statistics");
        englishTexts.put("TouristsByNationality", "Tourists by Nationality");
        englishTexts.put("MonthlyBookings", "Monthly Bookings");

        // Guide Management
        englishTexts.put("GuideID", "Guide ID");
        englishTexts.put("GuideName", "Guide Name");
        englishTexts.put("Languages", "Languages");
        englishTexts.put("Experience", "Experience (Years)");
        englishTexts.put("Certifications", "Certifications");
        englishTexts.put("Email", "Email");
        englishTexts.put("Phone", "Phone");
        englishTexts.put("Specialization", "Specialization");
        englishTexts.put("AddGuide", "Add Guide");
        englishTexts.put("UpdateGuide", "Update Guide");
        englishTexts.put("DeleteGuide", "Delete Guide");
        englishTexts.put("Upcoming Trek", "Upcoming Trek");

        // Trek Management
        englishTexts.put("TrekLocationManagement", "Trek Location Management");
        englishTexts.put("TrekName", "Trek Name");
        englishTexts.put("Region", "Region");
        englishTexts.put("MaxAltitude", "Max Altitude");
        englishTexts.put("Duration", "Duration (Days)");
        englishTexts.put("Difficulty", "Difficulty");
        englishTexts.put("BasePrice", "Base Price");
        englishTexts.put("DifficultyLevel", "Difficulty Level");
        englishTexts.put("AddTrek", "Add Trek");
        englishTexts.put("UpdateTrek", "Update Trek");
        englishTexts.put("DeleteTrek", "Delete Trek");
        englishTexts.put("TrekStatistics", "Trek Statistics");
        englishTexts.put("MostPopularTreks", "Most Popular Treks");
        englishTexts.put("MonthlyTrekStatistics", "Monthly Trek Statistics");

        // Analytics
        englishTexts.put("BusinessAnalytics", "Business Analytics");
        englishTexts.put("TotalRevenue", "Total Revenue");
        englishTexts.put("MonthlyRevenue", "Monthly Revenue");
        englishTexts.put("TotalBookings", "Total Bookings");
        englishTexts.put("ActiveBookings", "Active Bookings");
        englishTexts.put("TotalTourists", "Total Tourists");
        englishTexts.put("MonthlyTourists", "Monthly Tourists");
        englishTexts.put("UniqueNationalities", "Unique Nationalities");
        englishTexts.put("AvgTrekDuration", "Avg Trek Duration");
        englishTexts.put("TouristNationalityDistribution", "Tourist Nationality Distribution");
        englishTexts.put("BookingTrends", "Booking Trends");
        englishTexts.put("PopularTreks", "Popular Treks");
        englishTexts.put("Revenue", "Revenue (NPR)");

        // Guide Dashboard specific
        englishTexts.put("TotalEarnings", "Total Earnings");
        englishTexts.put("MonthlyEarnings", "Monthly Earnings");
        englishTexts.put("CompletedTreks", "Completed Treks");
        englishTexts.put("SuccessRate", "Success Rate");
        englishTexts.put("AverageRating", "Average Rating");
        englishTexts.put("TotalTreks", "Total Treks");
        englishTexts.put("UpcomingTreks", "Upcoming Treks");
        englishTexts.put("TrekDistribution", "Trek Distribution");
        englishTexts.put("EarningsChart", "Earnings Chart");
        englishTexts.put("ImportantUpdates", "Important Updates");
        englishTexts.put("WeatherAlerts", "WEATHER ALERTS");
        englishTexts.put("SafetyNotices", "SAFETY NOTICES");
        englishTexts.put("ImportantAnnouncements", "IMPORTANT ANNOUNCEMENTS");
        englishTexts.put("TrekDetails", "Trek Details");

        // Tourist Dashboard specific
        englishTexts.put("BookTrek", "Book Trek");
        englishTexts.put("MyBookings", "My Bookings");
        englishTexts.put("BookingForm", "Booking Form");
        englishTexts.put("SelectTrek", "Select Trek");
        englishTexts.put("EmergencyContact", "Emergency Contact");
        englishTexts.put("EstimatedPrice", "Estimated Price");
        englishTexts.put("Book", "Book");
        englishTexts.put("Update", "Update");
        englishTexts.put("Cancel", "Cancel");
        englishTexts.put("Print", "Print");
        englishTexts.put("BookingDetails", "Booking Details");
        englishTexts.put("Reference", "Reference");
        englishTexts.put("SpecialRequests", "Special Requests");
        englishTexts.put("Payment", "Payment");
        englishTexts.put("UpdateBooking", "Update Booking");
        englishTexts.put("PrintBooking", "Print Booking");
        englishTexts.put("BookingConfirmed", "Booking Confirmed");
        englishTexts.put("BookingCancelled", "Booking Cancelled");
        englishTexts.put("BookingUpdated", "Booking Updated");
        englishTexts.put("HighAltitudeWarning", "High Altitude Warning");
        englishTexts.put("EmergencyReport", "Emergency Report");
        englishTexts.put("ExportBookings", "Export Bookings");
        englishTexts.put("ReportEmergency", "Report Emergency");

        // Common terms
        englishTexts.put("Trek", "Trek");
        englishTexts.put("Date", "Date");
        englishTexts.put("Price", "Price");
        englishTexts.put("Month", "Month");
        englishTexts.put("Days", "days");
        englishTexts.put("NumberOfBookings", "Number of Bookings");
        englishTexts.put("NumberOfTreks", "Number of Treks");
        englishTexts.put("None", "None");

        // Alert messages
        englishTexts.put("Registration Successful", "Registration Successful");
        englishTexts.put("Your account has been created successfully! You can now log in.",
                "Your account has been created successfully! You can now log in.");
        englishTexts.put("Registration Failed", "Registration Failed");
        englishTexts.put("Validation Error", "Validation Error");
        englishTexts.put("All fields marked with * are required.", "All fields marked with * are required.");

        // Status values
        englishTexts.put("All", "All");
        englishTexts.put("Pending", "Pending");
        englishTexts.put("Confirmed", "Confirmed");
        englishTexts.put("Cancelled", "Cancelled");
        englishTexts.put("Completed", "Completed");

        // Difficulty levels
        englishTexts.put("Easy", "Easy");
        englishTexts.put("Moderate", "Moderate");
        englishTexts.put("Difficult", "Difficult");
        englishTexts.put("Challenging", "Challenging");
        englishTexts.put("Very Difficult", "Very Difficult");
        englishTexts.put("Extreme", "Extreme");

        // Festival seasons
        englishTexts.put("Dashain", "Dashain");
        englishTexts.put("Tihar", "Tihar");
        englishTexts.put("Holi", "Holi");
        englishTexts.put("Festival Discount", "Festival Discount");
        englishTexts.put("Festival Discount Information", "Festival Discount Information");
        englishTexts.put("Book during festivals and get 20% discount!", "Book during festivals and get 20% discount!");
        englishTexts.put("Congratulations! You received a", "Congratulations! You received a");
        englishTexts.put("discount for booking during", "discount for booking during");

        translations.put("English", englishTexts);

        // Initialize Nepali translations
        Map<String, String> nepaliTexts = new HashMap<>();

        // Basic/Common texts
        nepaliTexts.put("Welcome", "स्वागत छ");
        nepaliTexts.put("Login", "लगइन");
        nepaliTexts.put("Register", "दर्ता गर्नुहोस्");
        nepaliTexts.put("Username", "प्रयोगकर्ता नाम");
        nepaliTexts.put("Password", "पासवर्ड");
        nepaliTexts.put("RememberMe", "मलाई सम्झनुहोस्");
        nepaliTexts.put("ForgotPassword", "पासवर्ड बिर्सनुभयो?");
        nepaliTexts.put("WelcomeMessage", "ट्रिप सेवा नेपालमा स्वागत छ!");
        nepaliTexts.put("Logout", "लगआउट");
        nepaliTexts.put("Refresh", "रिफ्रेश");
        nepaliTexts.put("DashboardInfo", "सुन्दर हिमालय क्षेत्र अन्वेषण गर्नुहोस्।");
        nepaliTexts.put("SelectLanguage", "भाषा छान्नुहोस्");
        nepaliTexts.put("RefreshSuccess", "ड्यासबोर्ड सफलतापूर्वक रिफ्रेस गरियो!");

        // Login screen
        nepaliTexts.put("Welcome Back!", "फेरि स्वागत छ!");
        nepaliTexts.put("Don't have an account?", "खाता छैन?");
        nepaliTexts.put("Your Gateway to Adventure", "साहसिक यात्राको प्रवेशद्वार");
        nepaliTexts.put("Trip Sewa Nepal !", "ट्रिप सेवा नेपाल !");

        // Registration form
        nepaliTexts.put("Create Account", "खाता सिर्जना गर्नुहोस्");
        nepaliTexts.put("Join Trip Sewa Nepal!", "ट्रिप सेवा नेपालमा सामेल हुनुहोस्!");
        nepaliTexts.put("Full Name *", "पूरा नाम *");
        nepaliTexts.put("Email *", "इमेल *");
        nepaliTexts.put("Phone *", "फोन *");
        nepaliTexts.put("Username *", "प्रयोगकर्ता नाम *");
        nepaliTexts.put("Password *", "पासवर्ड *");
        nepaliTexts.put("Confirm Password *", "पासवर्ड पुष्टि गर्नुहोस् *");
        nepaliTexts.put("Register as *", "यसको रूपमा दर्ता गर्नुहोस् *");
        nepaliTexts.put("Tourist", "पर्यटक");
        nepaliTexts.put("Guide", "गाइड");
        nepaliTexts.put("Already have an account?", "पहिले नै खाता छ?");
        nepaliTexts.put("Sign in", "साइन इन गर्नुहोस्");

        // Navigation/Tab titles
        nepaliTexts.put("BookingManagement", "बुकिङ व्यवस्थापन");
        nepaliTexts.put("TrekGuideManagement", "ट्रेक गाइड व्यवस्थापन");
        nepaliTexts.put("TrekLocations", "ट्रेक स्थानहरू");
        nepaliTexts.put("AnalyticsDashboard", "विश्लेषण ड्यासबोर्ड");
        nepaliTexts.put("GuideDashboard", "गाइड ड्यासबोर्ड");
        nepaliTexts.put("TouristDashboard", "पर्यटक ड्यासबोर्ड");
        nepaliTexts.put("Book New Trip", "नयाँ यात्रा बुक गर्नुहोस्");
        nepaliTexts.put("View Your Trips", "तपाईंको यात्राहरू हेर्नुहोस्");
        nepaliTexts.put("Manage Trips", "यात्राहरू व्यवस्थापन गर्नुहोस्");

        // Booking Management
        nepaliTexts.put("FilterByStatus", "स्थिति अनुसार फिल्टर");
        nepaliTexts.put("FilterByDate", "मिति अनुसार फिल्टर");
        nepaliTexts.put("BookingID", "बुकिङ ID");
        nepaliTexts.put("TouristName", "पर्यटकको नाम");
        nepaliTexts.put("Nationality", "राष्ट्रियता");
        nepaliTexts.put("TrekGuide", "ट्रेक गाइड");
        nepaliTexts.put("TrekLocation", "ट्रेक स्थान");
        nepaliTexts.put("TrekDate", "ट्रेक मिति");
        nepaliTexts.put("Status", "स्थिति");
        nepaliTexts.put("TrekFee", "ट्रेक शुल्क");
        nepaliTexts.put("AssignTrekGuide", "ट्रेक गाइड तोक्नुहोस्");
        nepaliTexts.put("AssignGuide", "गाइड तोक्नुहोस्");
        nepaliTexts.put("UpdateStatus", "स्थिति अपडेट गर्नुहोस्");
        nepaliTexts.put("CancelBooking", "बुकिङ रद्द गर्नुहोस्");
        nepaliTexts.put("BookingStatistics", "बुकिङ तथ्याङ्क");
        nepaliTexts.put("TouristsByNationality", "राष्ट्रियता अनुसार पर्यटक");
        nepaliTexts.put("MonthlyBookings", "मासिक बुकिङ");

        // Guide Management
        nepaliTexts.put("GuideID", "गाइड ID");
        nepaliTexts.put("Upcoming Trek", "आगामी ट्रेक");
        nepaliTexts.put("GuideName", "गाइडको नाम");
        nepaliTexts.put("Languages", "भाषाहरू");
        nepaliTexts.put("Experience", "अनुभव (वर्षहरू)");
        nepaliTexts.put("Certifications", "प्रमाणपत्रहरू");
        nepaliTexts.put("Email", "इमेल");
        nepaliTexts.put("Phone", "फोन");
        nepaliTexts.put("Specialization", "विशेषज्ञता");
        nepaliTexts.put("AddGuide", "गाइड थप्नुहोस्");
        nepaliTexts.put("UpdateGuide", "गाइड अपडेट गर्नुहोस्");
        nepaliTexts.put("DeleteGuide", "गाइड मेट्नुहोस्");

        // Trek Management
        nepaliTexts.put("TrekLocationManagement", "ट्रेक स्थान व्यवस्थापन");
        nepaliTexts.put("TrekName", "ट्रेकको नाम");
        nepaliTexts.put("Region", "क्षेत्र");
        nepaliTexts.put("MaxAltitude", "अधिकतम उचाई");
        nepaliTexts.put("Duration", "अवधि (दिनहरू)");
        nepaliTexts.put("Difficulty", "कठिनाई");
        nepaliTexts.put("BasePrice", "आधार मूल्य");
        nepaliTexts.put("DifficultyLevel", "कठिनाई स्तर");
        nepaliTexts.put("AddTrek", "ट्रेक थप्नुहोस्");
        nepaliTexts.put("UpdateTrek", "ट्रेक अपडेट गर्नुहोस्");
        nepaliTexts.put("DeleteTrek", "ट्रेक मेट्नुहोस्");
        nepaliTexts.put("TrekStatistics", "ट्रेक तथ्याङ्क");
        nepaliTexts.put("MostPopularTreks", "सबैभन्दा लोकप्रिय ट्रेकहरू");
        nepaliTexts.put("MonthlyTrekStatistics", "मासिक ट्रेक तथ्याङ्क");

        // Analytics
        nepaliTexts.put("BusinessAnalytics", "व्यापारिक विश्लेषण");
        nepaliTexts.put("TotalRevenue", "कुल आम्दानी");
        nepaliTexts.put("MonthlyRevenue", "मासिक आम्दानी");
        nepaliTexts.put("TotalBookings", "कुल बुकिङहरू");
        nepaliTexts.put("ActiveBookings", "सक्रिय बुकिङहरू");
        nepaliTexts.put("TotalTourists", "कुल पर्यटकहरू");
        nepaliTexts.put("MonthlyTourists", "मासिक पर्यटकहरू");
        nepaliTexts.put("UniqueNationalities", "अनुपम राष्ट्रियताहरू");
        nepaliTexts.put("AvgTrekDuration", "औसत ट्रेक अवधि");
        nepaliTexts.put("TouristNationalityDistribution", "पर्यटक राष्ट्रियता वितरण");
        nepaliTexts.put("BookingTrends", "बुकिङ प्रवृत्ति");
        nepaliTexts.put("PopularTreks", "लोकप्रिय ट्रेकहरू");
        nepaliTexts.put("Revenue", "आम्दानी (NPR)");

        // Guide Dashboard specific
        nepaliTexts.put("TotalEarnings", "कुल आम्दानी");
        nepaliTexts.put("MonthlyEarnings", "मासिक आम्दानी");
        nepaliTexts.put("CompletedTreks", "पूरा गरिएका ट्रेकहरू");
        nepaliTexts.put("SuccessRate", "सफलताको दर");
        nepaliTexts.put("AverageRating", "औसत रेटिङ");
        nepaliTexts.put("TotalTreks", "कुल ट्रेकहरू");
        nepaliTexts.put("UpcomingTreks", "आगामी ट्रेकहरू");
        nepaliTexts.put("TrekDistribution", "ट्रेक वितरण");
        nepaliTexts.put("EarningsChart", "आम्दानी चार्ट");
        nepaliTexts.put("ImportantUpdates", "महत्वपूर्ण अपडेटहरू");
        nepaliTexts.put("WeatherAlerts", "मौसमी चेतावनी");
        nepaliTexts.put("SafetyNotices", "सुरक्षा सूचनाहरू");
        nepaliTexts.put("ImportantAnnouncements", "महत्वपूर्ण घोषणाहरू");
        nepaliTexts.put("TrekDetails", "ट्रेक विवरणहरू");

        // Tourist Dashboard specific
        nepaliTexts.put("BookTrek", "ट्रेक बुक गर्नुहोस्");
        nepaliTexts.put("MyBookings", "मेरा बुकिङहरू");
        nepaliTexts.put("BookingForm", "बुकिङ फारम");
        nepaliTexts.put("SelectTrek", "ट्रेक छान्नुहोस्");
        nepaliTexts.put("EmergencyContact", "आपतकालीन सम्पर्क");
        nepaliTexts.put("EstimatedPrice", "अनुमानित मूल्य");
        nepaliTexts.put("Book", "बुक गर्नुहोस्");
        nepaliTexts.put("Update", "अपडेट गर्नुहोस्");
        nepaliTexts.put("Cancel", "रद्द गर्नुहोस्");
        nepaliTexts.put("Print", "प्रिन्ट गर्नुहोस्");
        nepaliTexts.put("BookingDetails", "बुकिङ विवरणहरू");
        nepaliTexts.put("Reference", "सन्दर्भ");
        nepaliTexts.put("SpecialRequests", "विशेष अनुरोधहरू");
        nepaliTexts.put("Payment", "भुक्तानी");
        nepaliTexts.put("UpdateBooking", "बुकिङ अपडेट गर्नुहोस्");
        nepaliTexts.put("PrintBooking", "बुकिङ प्रिन्ट गर्नुहोस्");
        nepaliTexts.put("BookingConfirmed", "बुकिङ पुष्टि भयो");
        nepaliTexts.put("BookingCancelled", "बुकिङ रद्द भयो");
        nepaliTexts.put("BookingUpdated", "बुकिङ अपडेट भयो");
        nepaliTexts.put("HighAltitudeWarning", "उच्च उचाई चेतावनी");
        nepaliTexts.put("EmergencyReport", "आपतकालीन रिपोर्ट");
        nepaliTexts.put("ExportBookings", "बुकिङहरू निर्यात गर्नुहोस्");
        nepaliTexts.put("ReportEmergency", "आपतकाल रिपोर्ट गर्नुहोस्");

        // Common terms
        nepaliTexts.put("Trek", "ट्रेक");
        nepaliTexts.put("Date", "मिति");
        nepaliTexts.put("Price", "मूल्य");
        nepaliTexts.put("Month", "महिना");
        nepaliTexts.put("Days", "दिनहरू");
        nepaliTexts.put("NumberOfBookings", "बुकिङको संख्या");
        nepaliTexts.put("NumberOfTreks", "ट्रेकको संख्या");
        nepaliTexts.put("None", "कुनै छैन");

        // Alert messages
        nepaliTexts.put("Registration Successful", "दर्ता सफल भयो");
        nepaliTexts.put("Your account has been created successfully! You can now log in.",
                "तपाईंको खाता सफलतापूर्वक सिर्जना गरिएको छ! तपाईं अब लगइन गर्न सक्नुहुन्छ।");
        nepaliTexts.put("Registration Failed", "दर्ता असफल भयो");
        nepaliTexts.put("Validation Error", "प्रमाणीकरण त्रुटि");
        nepaliTexts.put("All fields marked with * are required.", "* चिन्ह भएका सबै फिल्डहरू आवश्यक छन्।");

        // Status values
        nepaliTexts.put("All", "सबै");
        nepaliTexts.put("Pending", "पेन्डिंग");
        nepaliTexts.put("Confirmed", "पुष्टि भएको");
        nepaliTexts.put("Cancelled", "रद्द गरिएको");
        nepaliTexts.put("Completed", "पूरा भएको");

        // Difficulty levels
        nepaliTexts.put("Easy", "सजिलो");
        nepaliTexts.put("Moderate", "मध्यम");
        nepaliTexts.put("Difficult", "कठिन");
        nepaliTexts.put("Challenging", "चुनौतीपूर्ण");
        nepaliTexts.put("Very Difficult", "धेरै कठिन");
        nepaliTexts.put("Extreme", "अत्यन्त कठिन");

        // Festival seasons
        nepaliTexts.put("Dashain", "दशैं");
        nepaliTexts.put("Tihar", "तिहार");
        nepaliTexts.put("Holi", "होली");
        nepaliTexts.put("Festival Discount", "चाड पर्व छुट");
        nepaliTexts.put("Festival Discount Information", "चाड पर्व छुट जानकारी");
        nepaliTexts.put("Book during festivals and get 20% discount!", "चाडपर्वमा बुक गर्नुहोस् र २०% छुट पाउनुहोस्!");
        nepaliTexts.put("Congratulations! You received a", "बधाई छ! तपाईंले प्राप्त गर्नुभयो");
        nepaliTexts.put("discount for booking during", "छुट बुकिङ गरेको बेलामा");

        translations.put("Nepali", nepaliTexts);

        System.out.println("Translations initialized with English: " + englishTexts.size() +
                " and Nepali: " + nepaliTexts.size() + " entries");
    }

    /**
     * Load Nepali font for proper display of Devanagari script
     */
    private void loadNepaliFont() {
        try {
            InputStream fontStream = getClass().getResourceAsStream("/fonts/NotoSansDevanagari-VariableFont_wdth,wght.ttf");
            if (fontStream == null) {
                String[] possibleNames = {
                        "/fonts/NotoSansDevanagari.ttf",
                        "/fonts/NotoSansDevanagari-Bold.ttf",
                        "/NotoSansDevanagari-Regular.ttf",
                        "/NotoSansDevanagari.ttf"
                };
                for (String name : possibleNames) {
                    fontStream = getClass().getResourceAsStream(name);
                    if (fontStream != null) break;
                }
            }

            if (fontStream != null) {
                nepaliFont = Font.loadFont(fontStream, 14);
                System.out.println("Nepali font loaded successfully: " + nepaliFont.getName());
            } else {
                System.err.println("Could not find Nepali font file. Using system default.");
                nepaliFont = Font.font("Arial Unicode MS", 14);
            }
        } catch (Exception e) {
            System.err.println("Error loading Nepali font: " + e.getMessage());
            nepaliFont = Font.getDefault();
        }
    }

    /**
     * Set the current language
     * @param language - "English" or "Nepali"
     */
    public void setLanguage(String language) {
        if ("English".equals(language) || "Nepali".equals(language)) {
            System.out.println("Setting language to: " + language);
            this.currentLanguage = language;
        } else {
            System.err.println("Unsupported language: " + language);
        }
    }

    /**
     * Get the current language
     * @return current language
     */
    public String getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * Get the Nepali font
     * @return font for Nepali text
     */
    public Font getNepaliFont() {
        return nepaliFont;
    }

    /**
     * Translate text based on current language
     * @param text - text to translate
     * @return translated text or original if no translation exists
     */
    public String translate(String text) {
        if (text == null) return "";

        if ("English".equals(currentLanguage)) {
            return text; // Already in English
        } else if ("Nepali".equals(currentLanguage)) {
            Map<String, String> nepaliTexts = translations.get("Nepali");
            if (nepaliTexts.containsKey(text)) {
                return nepaliTexts.get(text);
            }

            // Try to find partial matches for labels with additional content
            for (Map.Entry<String, String> entry : nepaliTexts.entrySet()) {
                if (text.contains(entry.getKey())) {
                    return text.replace(entry.getKey(), entry.getValue());
                }
            }

            return text; // No translation found
        } else {
            return text; // Default to original text
        }
    }

    /**
     * Apply translations to a JavaFX scene or component
     * @param node - root node to translate
     */
    public void translateUI(javafx.scene.Node node) {
        System.out.println("Translating UI to: " + currentLanguage);
        try {
            processNode(node);
            System.out.println("UI translation completed");
        } catch (Exception e) {
            System.err.println("Error translating UI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Process a JavaFX node for translation
     * @param node - node to process
     */
    private void processNode(javafx.scene.Node node) {
        try {
            // Skip translation if node is null
            if (node == null) return;

            // Skip system labels (like date and user)
            if (node instanceof Label) {
                Label label = (Label) node;
                if (label.getText() != null &&
                        (label.getText().contains("2025-07-30") ||
                                label.getText().contains("BibekDkl"))) {
                    return;
                }
            }

            // Handle different types of UI components
            if (node instanceof Labeled) {
                // Button, Label, CheckBox, etc.
                Labeled labeled = (Labeled) node;
                if (labeled.getText() != null && !labeled.getText().isEmpty()) {
                    String originalText = labeled.getText();
                    String translatedText = translate(originalText);
                    if (!originalText.equals(translatedText)) {
                        System.out.println("Translated: '" + originalText + "' -> '" + translatedText + "'");
                        labeled.setText(translatedText);

                        // Apply Nepali font if needed
                        if ("Nepali".equals(currentLanguage) && nepaliFont != null) {
                            labeled.setFont(nepaliFont);
                        }
                    }
                }
            } else if (node instanceof TextInputControl) {
                // TextField, TextArea, etc.
                TextInputControl textInput = (TextInputControl) node;
                if (textInput.getPromptText() != null && !textInput.getPromptText().isEmpty()) {
                    String originalPrompt = textInput.getPromptText();
                    String translatedPrompt = translate(originalPrompt);
                    textInput.setPromptText(translatedPrompt);

                    // Apply Nepali font if needed
                    if ("Nepali".equals(currentLanguage) && nepaliFont != null) {
                        textInput.setFont(nepaliFont);
                    }
                }
            } else if (node instanceof Text) {
                // Text node
                Text text = (Text) node;
                if (text.getText() != null && !text.getText().isEmpty()) {
                    String originalText = text.getText();
                    String translatedText = translate(originalText);
                    text.setText(translatedText);

                    // Apply Nepali font if needed
                    if ("Nepali".equals(currentLanguage) && nepaliFont != null) {
                        text.setFont(nepaliFont);
                    }
                }
            }

            // Handle MenuBar - avoid casting errors
            if (node instanceof MenuBar) {
                MenuBar menuBar = (MenuBar) node;
                for (Menu menu : menuBar.getMenus()) {
                    menu.setText(translate(menu.getText()));
                    for (MenuItem item : menu.getItems()) {
                        item.setText(translate(item.getText()));
                    }
                }
            }

            // Handle TableView columns directly
            if (node instanceof TableView) {
                TableView<?> tableView = (TableView<?>) node;
                for (TableColumn<?,?> column : tableView.getColumns()) {
                    if (column.getText() != null) {
                        column.setText(translate(column.getText()));
                    }
                }
            }

            // Handle ComboBox items
            if (node instanceof ComboBox) {
                @SuppressWarnings("unchecked")
                ComboBox<String> comboBox = (ComboBox<String>) node;

                // Skip language selection combo box
                if (comboBox.getId() != null && comboBox.getId().equals("languageCombo")) {
                    return;
                }

                String currentValue = comboBox.getValue();

                for (int i = 0; i < comboBox.getItems().size(); i++) {
                    String item = comboBox.getItems().get(i);
                    String translatedItem = translate(item);
                    comboBox.getItems().set(i, translatedItem);
                }

                // Restore selected value if it was translated
                if (currentValue != null) {
                    String translatedCurrentValue = translate(currentValue);
                    comboBox.setValue(translatedCurrentValue);
                }

                // Apply Nepali font if needed
                if ("Nepali".equals(currentLanguage) && nepaliFont != null) {
                    comboBox.setStyle(comboBox.getStyle() + "; -fx-font-family: '" + nepaliFont.getFamily() + "';");
                }
            }

            // Process child nodes if this is a parent
            if (node instanceof javafx.scene.Parent) {
                javafx.scene.Parent parent = (javafx.scene.Parent) node;
                for (javafx.scene.Node child : parent.getChildrenUnmodifiable()) {
                    processNode(child);
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing node: " + e.getMessage() + ", Node type: " +
                    (node != null ? node.getClass().getName() : "null"));
        }
    }

    /**
     * Helper method to set the language and translate an entire scene
     * @param language - target language
     * @param root - root node of the scene
     */
    public void setLanguageAndTranslate(String language, javafx.scene.Parent root) {
        setLanguage(language);
        translateUI(root);
    }

    /**
     * Get text translation for a key (compatibility with the other implementation)
     * @param key - text key to translate
     * @return translated text or original key if no translation exists
     */
    public String getText(String key) {
        try {
            Map<String, String> texts = translations.get(currentLanguage);
            if (texts != null && texts.containsKey(key)) {
                return texts.get(key);
            }

            // Fallback to English
            Map<String, String> englishTexts = translations.get("English");
            if (englishTexts != null && englishTexts.containsKey(key)) {
                return englishTexts.get(key);
            }

            return key;
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * Get the current date/time
     */
    public String getCurrentDateTime() {
        return CURRENT_DATETIME;
    }

    /**
     * Get the current username
     */
    public String getCurrentUsername() {
        return CURRENT_USERNAME;
    }
}