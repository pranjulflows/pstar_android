package com.Red.PSTAR_app.localization;

import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import android.support.annotation.RawRes;
import android.util.Log;

import com.Red.PSTAR_app.R;
import com.Red.PSTAR_app.utils.AppConstants;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alexey Matrosov on 28.09.2018.
 */
public class Localizer {
    public enum Locale {
        English,
        French
    }

    private Locale currentLocale = Locale.French;
    private final Map<String, LocalizedData> localizedValues = new HashMap<>();
    private final Map<String, LocalizedImage> localizedImages = new HashMap<>();

    private static Localizer instance;

    public static Localizer getInstance() {
        if (instance == null)
            instance = new Localizer();

        return instance;
    }

    public static String getString(String key) {
        return getInstance().getLocalizedString(key);
    }

    public static int getImage(String key) {
        return getInstance().getLocalizedImage(key);
    }

    private Localizer() {
        init();
    }

    public void setLocale(Locale locale) {
        currentLocale = locale;
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    private String getLocalizedString(String key) {
        if (key == null || key.isEmpty()) {
            Log.w("PSTAR", "Wrong key");
            return "";
        }

        LocalizedData data = localizedValues.get(key);
        if (data == null) {
            Log.w("PSTAR", "LocalizedData is null");
            return "";
        }

        String localized = data.getLocalizedData(currentLocale);

        return localized == null ? "" : localized;
    }

    @DrawableRes
    private int getLocalizedImage(String key) {
        if (key == null || key.isEmpty()) {
            Log.w("PSTAR", "Wrong key");
            return 0;
        }

        LocalizedImage data = localizedImages.get(key);
        if (data == null) {
            Log.w("PSTAR", "LocalizedImage is null");
            return 0;
        }

        return data.getLocalizedData(currentLocale);
    }

    // Get localized Abbreviations list
    public List<String> getAbbreviations(Resources resources) {
        List<String> abbreviations = new ArrayList<>();
        int resId = getAbbreviationsFile();

        try {
            InputStream is = resources.openRawResource(resId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty())
                    abbreviations.add(line);
            }

            reader.close();
            is.close();
        } catch (Exception ex) {
            Log.e("PSTAR", ex.getMessage(), ex);
        }

        return abbreviations;
    }

//    public void readDb(Resources resources) {
//        try {
//            InputStream is = resources.openRawResource(R.raw.fr_db);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//
//            List<String> lines = new ArrayList<>();
//
//            String line;
//            while ((line = reader.readLine()) != null){
//                line = line.trim();
//                if (!line.isEmpty())
//                    lines.add(line);
//            }
//// En
////            StringBuilder sb = new StringBuilder();
////            int ind ;
////            while((ind =  reader.read())!=-1){
////                char ch = (char)ind;
////                sb.append(ch);
////            }
////
////            String s = sb.toString();
////            String[] array =  s.split("\r");
////            for (int i = 0; i < array.length; i++){
////                if (array[i].startsWith("\n"))
////                    array[i] = array[i].substring(1);
////
////            }
//
//// Fr
////            List<Question> questions = new ArrayList<>();
////
////            for (String val : lines) {
////                String[] data = val.split(",");
////
////
////                Question question = new Question();
////                question.Qno = data[0].replace("ы", ";").replace("в", ",");
////                question.Question = data[1].replace("ы", ";").replace("в", ",");
////                question.A = data[2].replace("ы", ";").replace("в", ",");
////                question.B = data[3].replace("ы", ";").replace("в", ",");
////                question.C = data[4].replace("ы", ";").replace("в", ",");
////                question.D = data[5].replace("ы", ";").replace("в", ",");
////                question.Correct = data[6].replace("ы", ";").replace("в", ",");
////
////                questions.add(question);
////            }
////
////            String json = new Gson().toJson(questions);
////
////
////            reader.close();
////            is.close();
//        } catch (Exception ex) {
//            int a = 10;
//        }
//    }


    @RawRes
    private int getAbbreviationsFile() {
        switch (currentLocale) {
            case English:
                return R.raw.abbreviations_en;
            case French:
                return R.raw.abbreviations_fr;
            default:
                throw new RuntimeException("Unknown locale");
        }
    }

    private void init() {
        // Strings
        localizedValues.put("abbreviations_title", new LocalizedData("Abbreviations", "Abréviations"));
        localizedValues.put("abbreviations_note", new LocalizedData("NOTE: The abbreviations and acronyms listed below may be used throughout the PSTAR exam.", "NOTE: Les abréviations et acronymes suivants peuvent être employés à travers ce guide."));
        localizedValues.put("rate_popup_title", new LocalizedData("Rate Our App", "Évaluez notre application"));
        localizedValues.put("rate_popup_message", new LocalizedData("If you love our app, please take a moment to rate it", "Si vous aimez notre application, prenez un moment pour l'évaluer"));
        localizedValues.put("rate_popup_close", new LocalizedData("Close", "Fermer"));
        localizedValues.put("rate_popup_rate", new LocalizedData("Rate", "Évaluer"));
        localizedValues.put("rate_popup_feedback", new LocalizedData("Send Feedback", "Envoyer des commentaires"));

        localizedValues.put(AppConstants.DIALOG_PURCHASE_TITLE, new LocalizedData("Get Premium Access Today", "Accédez au service Premium"));
        localizedValues.put(AppConstants.DIALOG_PURCHASE_MESSAGE, new LocalizedData("Get access to all PSTAR sections,  unlimited Sample Exams, the Aviation Language Proficiency Test Guide, the Radio Study Guide for the Restricted Operator Certificate (ROC-A) with sample questions, and future updates!",
                "Accès à toutes les sections du PSTAR, échantillons d’examens, guide pour le test d’évaluation des compétences linguistiques en aviation, guide pour l’examen d’opérateur radio avec échantillons de questions"));
        localizedValues.put(AppConstants.DIALOG_BUTTON_CONTINUE, new LocalizedData("Continue", "Continuez"));
        localizedValues.put(AppConstants.DIALOG_BUTTON_CANCEL, new LocalizedData("Cancel", "Annulez"));
        localizedValues.put(AppConstants.DIALOG_LIFETIME_PURCHASE, new LocalizedData("(If you have previously paid for the full version, you do not need to pay again. Just send us an email with prove of purchase to info@pstarexamapp.com for a free code.)", "(Si vous avez déjà payé la version complète, vous n’avez pas besoin de payer à nouveau. Il suffit de nous envoyer un email avec la preuve d'achat à info@pstarexamapp.com pour un code gratuit.)"));


        localizedValues.put(CategoryKey.COLLISION_AVOIDANCE, new LocalizedData("COLLISION AVOIDANCE", "ÉVITEMENT ABORDAGE"));
        localizedValues.put(CategoryKey.VISUAL_SIGNALS, new LocalizedData("VISUAL SIGNALS", "SIGNAUX VISUELS"));
        localizedValues.put(CategoryKey.COMMUNICATIONS, new LocalizedData("COMMUNICATIONS", "COMMUNICATIONS"));
        localizedValues.put(CategoryKey.AERODROMES, new LocalizedData("AERODROMES", "AÉRODROMES"));
        localizedValues.put(CategoryKey.EQUIPMENT, new LocalizedData("EQUIPMENT", "ÉQUIPEMENTS"));
        localizedValues.put(CategoryKey.PILOT_RESPONSIBILITIES, new LocalizedData("PILOT RESPONSIBILITIES", "RESPONSABILITÉ DU PILOTE"));
        localizedValues.put(CategoryKey.WAKE_TURBULENCE, new LocalizedData("WAKE TURBULENCE", "TURBULENCE DE SILLAGE"));
        localizedValues.put(CategoryKey.AEROMEDICAL, new LocalizedData("AEROMEDICAL", "AÉROMÉDICAL"));
        localizedValues.put(CategoryKey.FLIGHT_PLANS, new LocalizedData("FLIGHT PLANS", "PLANS DE VOL ET ITINÉRAIRES DE VOL"));
        localizedValues.put(CategoryKey.CLEARANCES_AND_INSTRUCTIONS, new LocalizedData("CLEARANCES AND INSTRUCTIONS", "AUTORISATIONS ET INSTRUCTIONS"));
        localizedValues.put(CategoryKey.AIRCRAFT_OPERATIONS, new LocalizedData("AIRCRAFT OPERATIONS", "EXPLOITATION D’AÉRONEF"));
        localizedValues.put(CategoryKey.GENERAL_AIRSPACE, new LocalizedData("GENERAL AIRSPACE", "ESPACE AÉRIEN − EN GÉNÉRAL"));
        localizedValues.put(CategoryKey.CONTROLLED_AIRSPACE, new LocalizedData("CONTROLLED AIRSPACE", "ESPACE AÉRIEN CONTRÔLÉ"));
        localizedValues.put(CategoryKey.AVIATION_OCCURRENCES, new LocalizedData("AVIATION OCCURRENCES", "FAITS AÉRONAUTIQUES"));
        localizedValues.put(CategoryKey.Exam, new LocalizedData("PRACTICE EXAM", "EXAMEN PRATIGUE"));

        // Images
        localizedImages.put("category_collision", new LocalizedImage(R.drawable.collision_avoidance_en, R.drawable.collision_avoidance_fr));
        localizedImages.put("category_visual_signals", new LocalizedImage(R.drawable.visual_signals_en, R.drawable.visual_signals_fr));
        localizedImages.put("category_communications", new LocalizedImage(R.drawable.comms_en, R.drawable.comms_fr));
        localizedImages.put("category_aerodromes", new LocalizedImage(R.drawable.aerodromes_en, R.drawable.aerodromes_fr));
        localizedImages.put("category_equipments", new LocalizedImage(R.drawable.equipments_en, R.drawable.equipments_fr));
        localizedImages.put("category_pilot_responsib", new LocalizedImage(R.drawable.pilot_responsib_en, R.drawable.pilot_responsib_fr));
        localizedImages.put("category_wake_turb", new LocalizedImage(R.drawable.wake_turb_en, R.drawable.wake_turb_fr));
        localizedImages.put("category_aeromedcial", new LocalizedImage(R.drawable.aeromedical_en, R.drawable.aeromedical_fr));
        localizedImages.put("category_flight_plans", new LocalizedImage(R.drawable.flight_plans_en, R.drawable.flight_plans_fr));
        localizedImages.put("category_clearence_instructions", new LocalizedImage(R.drawable.clearence_instructions_en, R.drawable.clearence_instructions_fr));
        localizedImages.put("category_aircraftops", new LocalizedImage(R.drawable.aircraftops_en, R.drawable.aircraftops_fr));
        localizedImages.put("category_general_airsp", new LocalizedImage(R.drawable.general_airsp_en, R.drawable.general_airsp_fr));
        localizedImages.put("category_airsp", new LocalizedImage(R.drawable.contr_airsp_en, R.drawable.contr_airsp_fr));
        localizedImages.put("category_aviation_occurrences", new LocalizedImage(R.drawable.aviation_occurrences_en, R.drawable.aviation_occurrences_fr));
        localizedImages.put("category_questions", new LocalizedImage(R.drawable.question_mark_en, R.drawable.question_mark_fr));

        localizedImages.put("category_collision_gold", new LocalizedImage(R.drawable.collision_avoidance_en_gold, R.drawable.collision_avoidance_fr_gold));
        localizedImages.put("category_visual_signals_gold", new LocalizedImage(R.drawable.visual_signals_en_gold, R.drawable.visual_signals_fr_gold));
        localizedImages.put("category_communications_gold", new LocalizedImage(R.drawable.comms_en_gold, R.drawable.comms_fr_gold));
        localizedImages.put("category_aerodromes_gold", new LocalizedImage(R.drawable.aerodromes_en_gold, R.drawable.aerodromes_fr_gold));
        localizedImages.put("category_equipments_gold", new LocalizedImage(R.drawable.equipments_en_gold, R.drawable.equipments_fr_gold));
        localizedImages.put("category_pilot_responsib_gold", new LocalizedImage(R.drawable.pilot_responsib_en_gold, R.drawable.pilot_responsib_fr_gold));
        localizedImages.put("category_wake_turb_gold", new LocalizedImage(R.drawable.wake_turb_en_gold, R.drawable.wake_turb_fr_gold));
        localizedImages.put("category_aeromedcial_gold", new LocalizedImage(R.drawable.aeromedical_en_gold, R.drawable.aeromedical_fr_gold));
        localizedImages.put("category_flight_plans_gold", new LocalizedImage(R.drawable.flight_plans_en_gold, R.drawable.flight_plans_fr_gold));
        localizedImages.put("category_clearence_instructions_gold", new LocalizedImage(R.drawable.clearence_instructions_en_gold, R.drawable.clearence_instructions_fr_gold));
        localizedImages.put("category_aircraftops_gold", new LocalizedImage(R.drawable.aircraftops_en_gold, R.drawable.aircraftops_fr_gold));
        localizedImages.put("category_general_airsp_gold", new LocalizedImage(R.drawable.general_airsp_en_gold, R.drawable.general_airsp_fr_gold));
        localizedImages.put("category_airsp_gold", new LocalizedImage(R.drawable.contr_airsp_en_gold, R.drawable.contr_airsp_fr_gold));
        localizedImages.put("category_aviation_occurrences_gold", new LocalizedImage(R.drawable.aviation_occurrences_en_gold, R.drawable.aviation_occurrences_fr_gold));
        localizedImages.put("category_questions_gold", new LocalizedImage(R.drawable.question_mark_en_gold, R.drawable.question_mark_fr_gold));
    }
}