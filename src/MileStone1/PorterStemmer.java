package MileStone1;

import java.util.regex.*;

public class PorterStemmer {

    // a single consonant
    private static final String c = "[^aeiou]";
    // a single vowel
    private static final String v = "[aeiouy]";

    // a sequence of consonants; the second/third/etc consonant cannot be 'y'
    private static final String C = c + "[^aeiouy]*";
    // a sequence of vowels; the second/third/etc cannot be 'y'
    private static final String V = v + "[aeiou]*";

    // this regex pattern tests if the token has measure > 0 [at least one VC].
    private static final Pattern mGr0 = Pattern.compile("^(" + C + ")?" + V + C);

    // add more Pattern variables for the following patterns:
    // m equals 1: token has measure == 1
    private static final Pattern mGr1 = Pattern.compile("^(" + C + ")?" + "(" + V + C + "){1}" + "(" + V + ")?$");

    // m greater than 1: token has measure > 1
    private static final Pattern mGr2 = Pattern.compile("^(" + C + ")?" + "(" + V + C + "){2,}" + "(" + V + ")?$");

    // vowel: token has a vowel after the first (optional) C
    private static final Pattern mGr3 = Pattern.compile("^(" + C + ")?" + V);

    // double consonant: token ends in two consonants that are the same, 
    //			unless they are L, S, or Z. (look up "backreferencing" to help 
    //			with this)
    private static final Pattern mGr4 = Pattern.compile("([^aeioulsz])\\1$");

    // m equals 1, cvc: token is in Cvc form, where the last c is not w, x,
    //			or y.
    private static final Pattern mGr5 = Pattern.compile("^(" + C + ")?" + v + "[^aeiouwxy]$");

    private static final Pattern mGr6 = Pattern.compile("([^aeiousz])\\1$");

    private static final Pattern mGr7 = Pattern.compile("([^aeiou])\\1$");

    private static final String[][] step2pairs = {new String[]{"ational", "ate"},
    new String[]{"tional", "tion"},
    new String[]{"enci", "ence"},
    new String[]{"anci", "ance"},
    new String[]{"izer", "ize"},
    new String[]{"abli", "able"},
    new String[]{"alli", "al"},
    new String[]{"entli", "ent"},
    new String[]{"eli", "e"},
    new String[]{"ousli", "ous"},
    new String[]{"ization", "ize"},
    new String[]{"ation", "ate"},
    new String[]{"ator", "ate"},
    new String[]{"alism", "al"},
    new String[]{"iveness", "ive"},
    new String[]{"fulness", "ful"},
    new String[]{"ousness", "ous"},
    new String[]{"aliti", "al"},
    new String[]{"iviti", "ive"},
    new String[]{"biliti", "ble"},};

    private static final String[][] step3pairs = {new String[]{"icate", "ic"},
    new String[]{"ative", ""},
    new String[]{"alize", "al"},
    new String[]{"iciti", "ic"},
    new String[]{"ical", "ic"},
    new String[]{"ful", ""},
    new String[]{"ness", ""}
    };

    private static final String[][] step4pairs = {new String[]{"al", ""},
    new String[]{"ance", ""},
    new String[]{"ence", ""},
    new String[]{"er", ""},
    new String[]{"ic", ""},
    new String[]{"able", ""},
    new String[]{"ible", ""},
    new String[]{"ant", ""},
    new String[]{"ement", ""},
    new String[]{"ment", ""},
    new String[]{"ent", ""},
    new String[]{"ion", ""},
    new String[]{"ou", ""},
    new String[]{"ism", ""},
    new String[]{"ate", ""},
    new String[]{"iti", ""},
    new String[]{"ous", ""},
    new String[]{"ive", ""},
    new String[]{"ize", ""}
    };

    public static String processToken(String token) {
        //  System.err.println(" token :- "+token);
        if (token.length() < 3) {
            return token; // token must be at least 3 chars
        }
        // step 1a
        // program the other steps in 1a. 
        // note that Step 1a.3 implies that there is only a single 's' as the 
        //	suffix; ss does not count. you may need a regex pattern here for 
        // "not s followed by s".
        if (token.endsWith("sses")) {
            token = token.substring(0, token.length() - 2);
        }
        if (token.endsWith("ies")) {
            token = token.substring(0, token.length() - 2);
        }
        if (token.endsWith("s") && !(token.endsWith("ss"))) {
            token = token.substring(0, token.length() - 1);
        }

        // step 1b
        boolean doStep1bb = false;
        //		step 1b
        if (token.endsWith("eed")) { // 1b.1
            // token.substring(0, token.length() - 3) is the stem prior to "eed".
            // if that has m>0, then remove the "d".
            String stem = token.substring(0, token.length() - 3);
            if (mGr0.matcher(stem).find()) { // if the pattern matches the stem
                token = stem + "ee";
            }
        } else // program the rest of 1b. set the boolean doStep1bb to true if Step 1b* 
        // should be performed.
        if (token.endsWith("ed")) { // 1b.2
            String stem = token.substring(0, token.length() - 2);
            if (mGr3.matcher(stem).find()) { // if the pattern matches the stem
                token = stem;
                doStep1bb = true;
            }
        } else if (token.endsWith("ing")) { // 1b.3
            String stem = token.substring(0, token.length() - 3);
            if (mGr3.matcher(stem).find()) { // if the pattern matches the stem
                token = stem;
                doStep1bb = true;
            }
        }

        // step 1b*, only if the 1b.2 or 1b.3 were performed.
        if (doStep1bb) {
            if (token.endsWith("at") || token.endsWith("bl")
                    || token.endsWith("iz")) {

                token = token + "e";
            }
            // use the regex patterns you wrote for 1b*.4 and 1b*.5
            if (mGr4.matcher(token).find()) {
                token = token.substring(0, token.length() - 1);
            } else if ((mGr1.matcher(token).find()) && (mGr5.matcher(token).find())) {
                token = token + "e";
            }
        }

        // step 1c
        // program this step. test the suffix of 'y' first, then test the 
        // condition *v* on the stem.
        if (token.endsWith("y")) {
            String stem = token.substring(0, token.length() - 1);
            if (mGr3.matcher(stem).find()) {
                token = stem + "i";
            }
        }

        // step 2
        // program this step. for each suffix, see if the token ends in the 
        // suffix. 
        //    * if it does, extract the stem, and do NOT test any other suffix.
        //    * take the stem and make sure it has m > 0.
        //        * if it does, complete the step and do not test any others.
        //          if it does not, attempt the next suffix.
        // you may want to write a helper method for this. a matrix of
        // "suffix"/"replacement" pairs might be helpful. It could look like
        // string[][] step2pairs = {  new string[] {"ational", "ate"}, 
        //										new string[] {"tional", "tion"}, ....
        String step2result = step2AND3(token, step2pairs);
        if (!step2result.equals("")) {
            token = step2result;
        }

        // step 3
        // program this step. the rules are identical to step 2 and you can use
        // the same helper method. you may also want a matrix here.
        String step3result = step2AND3(token, step3pairs);
        if (!step3result.equals("")) {
            token = step3result;
        }

        // step 4
        // program this step similar to step 2/3, except now the stem must have
        // measure > 1.
        // note that ION should only be removed if the suffix is SION or TION, 
        // which would leave the S or T.
        // as before, if one suffix matches, do not try any others even if the 
        // stem does not have measure > 1.
        String step4result = step4(token, step4pairs);
        if (!step4result.equals("")) {
            token = step4result;
        }

        // step 5
        // program this step. you have a regex for m=1 and for "Cvc", which
        // you can use to see if m=1 and NOT Cvc.
        // all your code should change the variable token, which represents
        // the stemmed term for the token.
        if (token.endsWith("e")) { // 5a 1
            String stem = token.substring(0, token.length() - 1);
            if (mGr2.matcher(stem).find()) { // if the pattern matches the stem
                token = stem;
            }
        }
        if (token.endsWith("e")) { // 5a 2
            String stem = token.substring(0, token.length() - 1);
            if (mGr1.matcher(stem).find() && !(mGr5.matcher(stem).find())) { // if the pattern matches the stem
                token = stem;
            }
        }

        if (mGr2.matcher(token).find() && mGr7.matcher(token).find() && mGr6.matcher(token).find()) { // 5b
            token = token.substring(0, token.length() - 1);

        }

        return token;
    }

    //This method is used to apply step 2 and 3 of porter stemmer
    private static String step2AND3(String token, String[][] step2pairs) {
        String result = "";
        for (int i = 0; i < step2pairs.length; i++) {
            if (token.endsWith(step2pairs[i][0])) {
                String stem = token.substring(0, token.length() - step2pairs[i][0].length());
                if (mGr0.matcher(stem).find()) {
                    result = stem + step2pairs[i][1];
                }
                break;
            }

        }
        return result;
    }

    //This method is used to apply step 4 of porter stemmer	
    private static String step4(String token, String[][] step2pairs) {
        String result = "";
        for (int i = 0; i < step2pairs.length; i++) {
            if (token.endsWith(step2pairs[i][0]) && !step2pairs[i][0].equalsIgnoreCase("ion")) {
                String stem = token.substring(0, token.length() - step2pairs[i][0].length());
                if (mGr2.matcher(stem).find()) {
                    result = stem + step2pairs[i][1];
                }
                break;
            } else if ((token.endsWith("sion") || token.endsWith("tion")) && step2pairs[i][0].equalsIgnoreCase("ion")) {
                String stem = token.substring(0, token.length() - step2pairs[i][0].length());
                if (mGr2.matcher(stem).find()) {
                    result = stem + step2pairs[i][1];
                }
                break;
            } else {
                result = token;

            }
        }
        return result;
    }
}
