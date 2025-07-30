package com.example.nepaltourismmanagement.utils;

import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;

public class LanguageManager {

    // Singleton instance
    private static LanguageManager instance;

    // Current language
    private String currentLanguage;

    // Translation maps
    private Map<String, String> englishToNepaliMap;

    // Current date/time for system
    private static final String CURRENT_DATETIME = "2025-07-30 06:21:09";
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
        initializeTranslations();
    }

    /**
     * Initialize translation maps with common phrases
     */
    private void initializeTranslations() {
        System.out.println("Initializing language translations");

        englishToNepaliMap = new HashMap<>();

        // Login screen
        addTranslation("Login", "लगइन");
        addTranslation("Register", "दर्ता गर्नुहोस्");
        addTranslation("Username", "प्रयोगकर्ता नाम");
        addTranslation("Password", "पासवर्ड");
        addTranslation("Remember me", "मलाई सम्झनुहोस्");
        addTranslation("Forgot password?", "पासवर्ड बिर्सनुभयो?");
        addTranslation("Don't have an account?", "खाता छैन?");
        addTranslation("Trip Sewa Nepal !", "ट्रिप सेवा नेपाल !");
        addTranslation("Your Gateway to Adventure", "साहसिक यात्राको प्रवेशद्वार");
        addTranslation("Welcome Back!", "फेरि स्वागत छ!");

        // Registration form
        addTranslation("Create Account", "खाता सिर्जना गर्नुहोस्");
        addTranslation("Join Trip Sewa Nepal!", "ट्रिप सेवा नेपाल मा सामेल हुनुहोस्!");
        addTranslation("Full Name *", "पूरा नाम *");
        addTranslation("Email *", "इमेल *");
        addTranslation("Phone *", "फोन *");
        addTranslation("Username *", "प्रयोगकर्ता नाम *");
        addTranslation("Password *", "पासवर्ड *");
        addTranslation("Confirm Password *", "पासवर्ड पुष्टि गर्नुहोस् *");
        addTranslation("Register as *", "यसको रूपमा दर्ता गर्नुहोस् *");
        addTranslation("Tourist", "पर्यटक");
        addTranslation("Guide", "गाइड");
        addTranslation("Already have an account?", "पहिले नै खाता छ?");
        addTranslation("Sign in", "साइन इन गर्नुहोस्");

        // Dashboard elements
        addTranslation("Admin Dashboard", "प्रशासक ड्यासबोर्ड");
        addTranslation("Tourist Dashboard", "पर्यटक ड्यासबोर्ड");
        addTranslation("Guide Dashboard", "गाइड ड्यासबोर्ड");
        addTranslation("Book New Trip", "नयाँ यात्रा बुक गर्नुहोस्");
        addTranslation("View Your Trips", "तपाईंको यात्राहरू हेर्नुहोस्");
        addTranslation("Manage Trips", "यात्राहरू व्यवस्थापन गर्नुहोस्");
        addTranslation("Logout", "लगआउट");
        addTranslation("Refresh", "रिफ्रेश");

        // Trek-related terms
        addTranslation("Trek Name", "यात्राको नाम");
        addTranslation("Duration", "अवधि");
        addTranslation("Difficulty", "कठिनाई");
        addTranslation("Price", "मूल्य");
        addTranslation("Book", "बुक गर्नुहोस्");
        addTranslation("Status", "स्थिति");
        addTranslation("Region", "क्षेत्र");
        addTranslation("Max Altitude", "अधिकतम उचाई");

        // Alert messages
        addTranslation("Registration Successful", "दर्ता सफल भयो");
        addTranslation("Your account has been created successfully! You can now log in.",
                "तपाईंको खाता सफलतापूर्वक सिर्जना गरिएको छ! तपाईं अब लगइन गर्न सक्नुहुन्छ।");
        addTranslation("Registration Failed", "दर्ता असफल भयो");
        addTranslation("Validation Error", "प्रमाणीकरण त्रुटि");
        addTranslation("All fields marked with * are required.", "* चिन्ह भएका सबै फिल्डहरू आवश्यक छन्।");

        System.out.println("Translations initialized with " + englishToNepaliMap.size() + " entries");
    }

    /**
     * Add a translation pair
     */
    private void addTranslation(String english, String nepali) {
        englishToNepaliMap.put(english, nepali);
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
     * Translate text based on current language
     * @param text - text to translate
     * @return translated text or original if no translation exists
     */
    public String translate(String text) {
        if (text == null) return "";

        if ("English".equals(currentLanguage)) {
            return text; // Already in English
        } else if ("Nepali".equals(currentLanguage)) {
            String translation = englishToNepaliMap.get(text);
            if (translation != null) {
                return translation;
            }

            // Try to find partial matches for labels with additional content
            for (Map.Entry<String, String> entry : englishToNepaliMap.entrySet()) {
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
                    }
                }
            } else if (node instanceof TextInputControl) {
                // TextField, TextArea, etc.
                TextInputControl textInput = (TextInputControl) node;
                if (textInput.getPromptText() != null && !textInput.getPromptText().isEmpty()) {
                    String originalPrompt = textInput.getPromptText();
                    String translatedPrompt = translate(originalPrompt);
                    textInput.setPromptText(translatedPrompt);
                }
            } else if (node instanceof Text) {
                // Text node
                Text text = (Text) node;
                if (text.getText() != null && !text.getText().isEmpty()) {
                    String originalText = text.getText();
                    String translatedText = translate(originalText);
                    text.setText(translatedText);
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

    public String getCurrentDateTime() {
        return CURRENT_DATETIME;
    }

    public String getCurrentUsername() {
        return CURRENT_USERNAME;
    }
}