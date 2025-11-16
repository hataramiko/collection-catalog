package com.mikohatara.collectioncatalog.util

import java.util.Locale

/*  getLocale() is intended to be used for formatting numeral values,
*   the formatting of which should conform to the user's region of choice,
*   as opposed to the app's current language.
* */
fun getLocale(countryCode: String): Locale {
    val appRegionLanguage = countryCodeToLanguageMap[countryCode] ?: "en"

    val currentLocale = Locale.Builder()
        .setLanguage(appRegionLanguage)
        .setRegion(countryCode)
        .build()

    return currentLocale
}

/*  As opposed to getLocale(), getCalendarLocale() is intended to provide a Locale for
*   formatting date values, which should be displayed in the app's current language,
*   supplemented by the user's region of choice.
* */
fun getCalendarLocale(countryCode: String): Locale {
    val appLocale = Locale.getDefault()
    val appLanguage = appLocale.language

    val calendarLocale = Locale.Builder()
        .setLanguage(appLanguage)
        .setRegion(countryCode)
        .build()

    return calendarLocale
}

private val countryCodeToLanguageMap = mapOf(
    "AD" to "ca", // Andorra -> Catalan
    "AE" to "ar", // UAE -> Arabic
    "AF" to "ps", // Afghanistan -> Pashto
    "AG" to "en", // Antigua and Barbuda -> English
    "AI" to "en", // Anguilla -> English
    "AL" to "sq", // Albania -> Albanian
    "AM" to "hy", // Armenia -> Armenian
    "AR" to "es", // Argentina -> Spanish
    "AT" to "de", // Austria -> German
    "AU" to "en", // Australia -> English
    "AZ" to "az", // Azerbaijan -> Azerbaijani
    "BA" to "bs", // Bosnia and Herzegovina -> Bosnian
    "BB" to "en", // Barbados -> English
    "BD" to "bn", // Bangladesh -> Bengali
    "BE" to "nl", // Belgium -> Dutch (Flemish)
    "BF" to "fr", // Burkina Faso -> French
    "BG" to "bg", // Bulgaria -> Bulgarian
    "BH" to "ar", // Bahrain -> Arabic
    "BI" to "fr", // Burundi -> French
    "BJ" to "fr", // Benin -> French
    "BN" to "ms", // Brunei -> Malay
    "BO" to "es", // Bolivia -> Spanish
    "BR" to "pt", // Brazil -> Portuguese
    "BS" to "en", // Bahamas -> English
    "BT" to "dz", // Bhutan -> Dzongkha
    "BW" to "en", // Botswana -> English
    "BY" to "be", // Belarus -> Belarusian
    "BZ" to "en", // Belize -> English
    "CA" to "en", // Canada -> English (French 'fr' is also common)
    "CH" to "de", // Switzerland -> German (de, fr, it are official)
    "CL" to "es", // Chile -> Spanish
    "CN" to "zh", // China -> Chinese
    "CO" to "es", // Colombia -> Spanish
    "CR" to "es", // Costa Rica -> Spanish
    "CU" to "es", // Cuba -> Spanish
    "CY" to "el", // Cyprus -> Greek
    "CZ" to "cs", // Czechia -> Czech
    "DE" to "de", // Germany -> German
    "DK" to "da", // Denmark -> Danish
    "DO" to "es", // Dominican Republic -> Spanish
    "DZ" to "ar", // Algeria -> Arabic
    "EC" to "es", // Ecuador -> Spanish
    "EE" to "et", // Estonia -> Estonian
    "EG" to "ar", // Egypt -> Arabic
    "ES" to "es", // Spain -> Spanish
    "ET" to "am", // Ethiopia -> Amharic
    "FI" to "fi", // Finland -> Finnish
    "FR" to "fr", // France -> French
    "GA" to "fr", // Gabon -> French
    "GB" to "en", // United Kingdom -> English
    "GE" to "ka", // Georgia -> Georgian
    "GR" to "el", // Greece -> Greek
    "GT" to "es", // Guatemala -> Spanish
    "HK" to "zh", // Hong Kong -> Chinese
    "HN" to "es", // Honduras -> Spanish
    "HR" to "hr", // Croatia -> Croatian
    "HU" to "hu", // Hungary -> Hungarian
    "ID" to "id", // Indonesia -> Indonesian
    "IE" to "en", // Ireland -> English
    "IL" to "he", // Israel -> Hebrew
    "IN" to "hi", // India -> Hindi (English 'en' is also common)
    "IQ" to "ar", // Iraq -> Arabic
    "IR" to "fa", // Iran -> Persian
    "IS" to "is", // Iceland -> Icelandic
    "IT" to "it", // Italy -> Italian
    "JM" to "en", // Jamaica -> English
    "JO" to "ar", // Jordan -> Arabic
    "JP" to "ja", // Japan -> Japanese
    "KE" to "sw", // Kenya -> Swahili
    "KG" to "ky", // Kyrgyzstan -> Kyrgyz
    "KH" to "km", // Cambodia -> Khmer
    "KR" to "ko", // South Korea -> Korean
    "KW" to "ar", // Kuwait -> Arabic
    "KZ" to "kk", // Kazakhstan -> Kazakh
    "LA" to "lo", // Laos -> Lao
    "LB" to "ar", // Lebanon -> Arabic
    "LI" to "de", // Liechtenstein -> German
    "LK" to "si", // Sri Lanka -> Sinhalese
    "LT" to "lt", // Lithuania -> Lithuanian
    "LU" to "lb", // Luxembourg -> Luxembourgish
    "LV" to "lv", // Latvia -> Latvian
    "MA" to "ar", // Morocco -> Arabic
    "MC" to "fr", // Monaco -> French
    "MD" to "ro", // Moldova -> Romanian
    "ME" to "sr", // Montenegro -> Serbian
    "MK" to "mk", // North Macedonia -> Macedonian
    "ML" to "fr", // Mali -> French
    "MM" to "my", // Myanmar -> Burmese
    "MN" to "mn", // Mongolia -> Mongolian
    "MO" to "zh", // Macao -> Chinese
    "MT" to "mt", // Malta -> Maltese
    "MX" to "es", // Mexico -> Spanish
    "MY" to "ms", // Malaysia -> Malay
    "NG" to "en", // Nigeria -> English
    "NI" to "es", // Nicaragua -> Spanish
    "NL" to "nl", // Netherlands -> Dutch
    "NO" to "no", // Norway -> Norwegian
    "NP" to "ne", // Nepal -> Nepali
    "NZ" to "en", // New Zealand -> English
    "OM" to "ar", // Oman -> Arabic
    "PA" to "es", // Panama -> Spanish
    "PE" to "es", // Peru -> Spanish
    "PH" to "en", // Philippines -> English
    "PK" to "ur", // Pakistan -> Urdu
    "PL" to "pl", // Poland -> Polish
    "PR" to "es", // Puerto Rico -> Spanish
    "PT" to "pt", // Portugal -> Portuguese
    "PY" to "es", // Paraguay -> Spanish
    "QA" to "ar", // Qatar -> Arabic
    "RO" to "ro", // Romania -> Romanian
    "RS" to "sr", // Serbia -> Serbian
    "RU" to "ru", // Russia -> Russian
    "SA" to "ar", // Saudi Arabia -> Arabic
    "SE" to "sv", // Sweden -> Swedish
    "SG" to "en", // Singapore -> English
    "SI" to "sl", // Slovenia -> Slovenian
    "SK" to "sk", // Slovakia -> Slovak
    "SN" to "fr", // Senegal -> French
    "SV" to "es", // El Salvador -> Spanish
    "TH" to "th", // Thailand -> Thai
    "TN" to "ar", // Tunisia -> Arabic
    "TR" to "tr", // Turkey -> Turkish
    "TW" to "zh", // Taiwan -> Chinese
    "UA" to "uk", // Ukraine -> Ukrainian
    "US" to "en", // United States -> English
    "UY" to "es", // Uruguay -> Spanish
    "UZ" to "uz", // Uzbekistan -> Uzbek
    "VE" to "es", // Venezuela -> Spanish
    "VN" to "vi", // Vietnam -> Vietnamese
    "ZA" to "en"  // South Africa -> English
)
